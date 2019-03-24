package com.android.lovdream;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
//import com.qualcomm.qcnvitems.QcNvItems;

public class ChangeMeid  extends Activity implements OnClickListener
{
	 private Button mSubmit;
	private Button mCancleButton;
	//private QcNvItems mNv;
	private EditText mMEIDEdit;
	private Handler handle = new Handler() {

		private String oldMEID;

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 0x11) {
				try {
					//oldMEID = mNv.getMeid();
	          if(oldMEID !=null ){
	  			mMEIDEdit.setText(oldMEID);
				mMEIDEdit.setSelection(oldMEID.length());
	            }
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		android.util.Log.i("....zzj","---------oncreate------");
		setContentView(R.layout.change_meid);
		mSubmit = (Button) findViewById(R.id.bt_change);
		mCancleButton=(Button)findViewById(R.id.bt_cancle);
		mMEIDEdit = (EditText) findViewById(R.id.et_meid_card1);
		mSubmit.setOnClickListener(this);
		mCancleButton.setOnClickListener(this);
		/*
		mNv = new QcNvItems(this);
		if(mNv !=null){
			handle.sendEmptyMessageDelayed(0x11, 1000);
		}
		*/
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.bt_change:
			String newMEID = mMEIDEdit.getEditableText().toString();
			boolean b = CheckMeid(newMEID);
			if (newMEID != null && b) {
				try {
					//mNv.setMEID(newMEID);
					Toast.makeText(this, R.string.set_successful,
							Toast.LENGTH_SHORT).show();
					this.finish();
				} catch (Exception e) {
					// TODO: handle exception
				}
			} else {
				Toast.makeText(this, R.string.meid_erro_tip, Toast.LENGTH_SHORT)
						.show();
				return;
			}
			break;
		 case R.id.bt_cancle:
			 ChangeMeid.this.finish();
			break;
		}
	}

	private boolean CheckMeid(String meid){
	    return meid.length() == 14 && meid.matches("^[A-Fa-f0-9]+");
	}    
}
