package lovdream.android.cit;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.lovdream.util.SystemUtil;

public class TestFlag extends Activity {
	
	private TextView txtStatus,G2_test,G4_test,jiao,PCBA_test,Auto_test ;
	private Button btnSucc ,btnFail;
	private static int LENGHT = 30;
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.test_flag);
		
		TextView tv = (TextView) findViewById(R.id.test_name);
		tv.setText(getString(R.string.test_flag_result));
		jiao = (TextView) findViewById(R.id.jiao);
		G2_test = (TextView) findViewById(R.id.G2_test);
		G4_test = (TextView) findViewById(R.id.G4_test);
		PCBA_test = (TextView) findViewById(R.id.PCBA_test);
		Auto_test = (TextView) findViewById(R.id.Auto_test);
		//txtStatus = (TextView) findViewById(R.id.txtStatus);
		btnSucc = (Button) findViewById(R.id.btnSuc); 
		btnFail = (Button) findViewById(R.id.btnFail);
		btnSucc.setVisibility(View.GONE);
		btnFail.setVisibility(View.GONE);
	}
	
	protected void onResume() {
		super.onResume();
		updateUI();
	};
	
	private void updateUI(){
		byte[] bresult = null;
		bresult = SystemUtil.getNvFactoryData3IByte();
		if (bresult == null) {
			Toast.makeText(this, getString(R.string.no_init_prompt), Toast.LENGTH_SHORT).show();
			//txtStatus.setText(getString(R.string.no_init_prompt));
			return;
		}
		char[] cChar = getChars(bresult);
		StringBuffer s = new StringBuffer();
		for(int i = 0; i < LENGHT; i++){
			s.append("  "+cChar[i]+"  ");
			if(i%5 == 4){
				s.append("\n");
			}
		}
		//txtStatus.setText(s.toString());
		bresult = SystemUtil.getNvFactoryData3IByte();
		if (bresult == null) {
			Toast.makeText(this, getString(R.string.no_init_prompt), Toast.LENGTH_SHORT).show();
			jiao.setText(getString(R.string.no_init_prompt));
			G2_test.setVisibility(View.GONE);
			G4_test.setVisibility(View.GONE);
			PCBA_test.setVisibility(View.GONE);
			Auto_test.setVisibility(View.GONE);
			return;
		}
		cChar = getChars(bresult);
	/*	for(int i=0;i<cChar.length;i++){
			Log.d("XJ","----"+cChar[i]+"------");
		}*/
		String strjiao=getString(R.string.jiao);
		String strreset=getString(R.string.frist);
		if('P'==cChar[0]){
			jiao.setText(strjiao+"PASS");
		}else if('F'==cChar[0]){
			jiao.setText(strjiao+"FAIL");
		}else{
			jiao.setText(strjiao+strreset);
		}
		String str2g=getString(R.string.G2_test);
		if('P'==cChar[1]){
			G2_test.setText(str2g+"PASS");
		}else if('F'==cChar[1]){
			G2_test.setText(str2g+"FAIL");
		}else{
			G2_test.setText(str2g+strreset);
		}
		String str4g=getString(R.string.G4_test);
		if('P'==cChar[2]){
			G4_test.setText(str4g+"PASS");
		}else if('F'==cChar[2]){
			G4_test.setText(str4g+"FAIL");
		}else{
			G4_test.setText(str4g+strreset);
		}
		if("msm8953_64_c551".equals(SystemProperties.get("ro.build.product"))){
			G4_test.setVisibility(View.GONE);
		}
		String strpcba=getString(R.string.PCBA_test);
		if('P'==cChar[4]){
			PCBA_test.setText(strpcba+"PASS");
		}else if('F'==cChar[4]){
			PCBA_test.setText(strpcba+"FAIL");
		}else{
			PCBA_test.setText(strpcba+strreset);
		}
		String strauto=getString(R.string.Auto_test);
		if('P'==cChar[3]){
			Auto_test.setText(strauto+"PASS");
		}else if('F'==cChar[3]){
			Auto_test.setText(strauto+"FAIL");
		}else{
			Auto_test.setText(strauto+strreset);
		}
	}
	
	private char[] getChars(byte[] bytes) {
		Charset cs = Charset.forName("UTF-8");
		ByteBuffer bb = ByteBuffer.allocate(bytes.length);
		bb.put(bytes);
		bb.flip();
		CharBuffer cb = cs.decode(bb);
		return cb.array();
	}
}
