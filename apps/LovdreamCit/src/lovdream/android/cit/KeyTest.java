package lovdream.android.cit;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.util.AttributeSet;
import android.content.res.TypedArray;
import android.util.Xml;
import android.graphics.Color;

import org.xmlpull.v1.XmlPullParser;

public class KeyTest extends Activity implements View.OnClickListener{

	private ArrayList<Key> testKeys;
	
	@Override
	protected void onPause() {
		super.onPause();
		SystemProperties.set("sys.cit_keytest","false");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SystemProperties.set("sys.cit_keytest","true");
	}
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		this.setContentView(R.layout.keytest);
		
		loadTestKeys();
		((Button)findViewById(R.id.btn_success)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_fail)).setOnClickListener(this);

		LinearLayout parent = (LinearLayout) findViewById(R.id.test_key_listLinearLayout);
		if((testKeys != null) && (!testKeys.isEmpty())){
			for(Key key : testKeys){
				parent.addView(key.textView);
			}
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		Bundle b = new Bundle();
		Intent intent = new Intent();

		if(id == R.id.btn_success){
			b.putInt("test_result", 1);
		}else{
			b.putInt("test_result", 0);
		}
		
		intent.putExtras(b);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	@Override
	public boolean onKeyDown(int i, KeyEvent keyevent) {
		return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent keyevent) {
		if((testKeys == null) || (testKeys.isEmpty())){
			return true;
		}

		for(Key key : testKeys){
			if(key.keyCode == keyCode){
				//key.textView.setTextColor(Color.GRAY);
				key.textView.setVisibility(View.INVISIBLE);
				testKeys.remove(key);
				break;
			}
		}
		if(testKeys.isEmpty()){
			Button success = (Button)findViewById(R.id.btn_success);
			success.setEnabled(true);
			if(CITMain.CITtype == CITMain.PCBA_AUTO_TEST 
					|| CITMain.CITtype == CITMain.MACHINE_AUTO_TEST){
				success.performClick();
			}
		}
		return true;
	}

	@Override
	public void onAttachedToWindow() {
		getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();
	}

	private void loadTestKeys(){
		XmlPullParser parser;

		String product = SystemProperties.get("ro.build.product","");

		parser = getResources().getXml(R.xml.test_keys);

		AttributeSet attrs = Xml.asAttributeSet(parser);

		testKeys = new ArrayList<Key>();
		int type;

		try{
			while((type = parser.next()) != XmlPullParser.START_TAG &&
					type != XmlPullParser.END_DOCUMENT){
				// do nothing
				// just look for the root node
			}

			final int depth = parser.getDepth();
			while(((type = parser.next()) != XmlPullParser.END_TAG ||
						parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT){
				if(type != XmlPullParser.START_TAG){
					continue;
				}
				final String name = parser.getName();
				if(Key.class.getCanonicalName().equals(name)){
					Key key = new Key(this,attrs);
					if(key.isSupported){
						testKeys.add(key);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public class Key{

		int keyCode;
		String keyName;
		TextView textView;
		boolean isSupported;

		public Key(Context context,AttributeSet attr){
			TypedArray array = context.obtainStyledAttributes(attr,R.styleable.Key);

			keyName = array.getString(R.styleable.Key_keyName);
			keyCode = KeyEvent.keyCodeFromString(array.getString(R.styleable.Key_keyCode));
			isSupported = keyCode > 0;

			LayoutInflater inflater = LayoutInflater.from(context);
			textView = (TextView)inflater.inflate(R.layout.key_view,null);
			textView.setText(keyName);

			/*let these configs can be re-used*/
			array.recycle();
		}
	}
}
