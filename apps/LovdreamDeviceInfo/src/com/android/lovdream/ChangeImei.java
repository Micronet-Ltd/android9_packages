package com.android.lovdream;

import java.io.IOException;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.qualcomm.qcnvitems.QcNvItems;

public class ChangeImei extends Activity implements OnClickListener {
	//private QcNvItems mNv;
	String oldIMEI;
	String oldIMEI1;
	private Handler handle = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 0x11) {
				try {
					//oldIMEI = mNv.getIMEI();
					if(mIMEIEdit !=null ){
						mIMEIEdit.setText(oldIMEI);
						mIMEIEdit.setSelection(oldIMEI.length());
					}
					//oldIMEI1=mNv.getIMEI2();
					if(oldIMEI1 !=null){
							mIMEIEdit1.setText(oldIMEI1);
							mIMEIEdit1.setSelection(oldIMEI1.length());
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}

	};
	private Button mSubmit;
	private EditText mIMEIEdit;
	private EditText mIMEIEdit1;
	private Button mCancleButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_imei);
		mSubmit = (Button) findViewById(R.id.bt_change);
		
		mCancleButton=(Button)findViewById(R.id.bt_cancle);
		mIMEIEdit = (EditText) findViewById(R.id.et_imei_card1);
		mIMEIEdit1= (EditText) findViewById(R.id.et_imei_card2);
		mSubmit.setOnClickListener(this);
		findViewById(R.id.bt_change1).setOnClickListener(this);
		mCancleButton.setOnClickListener(this);
		//mNv = new QcNvItems(this);
		handle.sendEmptyMessageDelayed(0x11, 1000);
		// updatesubMitState();
	}

	private boolean CheckIMEI(String meid) {
		return meid.length() == 15 && meid.matches("^[0-9]+");
	}

	private void updatesubMitState() {
		String newIMEI = mIMEIEdit.getEditableText().toString();
		boolean b = CheckIMEI(newIMEI);
		
		if (!newIMEI.equals(oldIMEI) && newIMEI != null && b) {
			mSubmit.setEnabled(true);
		} else {
			mSubmit.setEnabled(false);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.bt_change:
		String newIMEI = mIMEIEdit.getEditableText().toString();
			boolean b = CheckIMEI(newIMEI);
			if (newIMEI != null && b) {
				try {
					//mNv.setIMEI(newIMEI+"0");
					mIMEIEdit.setTextColor(Color.WHITE);
					Toast.makeText(this, R.string.set_successful,
							Toast.LENGTH_SHORT).show();
					mIMEIEdit.setTextColor(Color.WHITE);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}else {
				mIMEIEdit.setTextColor(Color.RED);
				Toast.makeText(this, R.string.imei_erro_tip, Toast.LENGTH_SHORT)
						.show();
				return;
			}
			break;
		 case R.id.bt_change1:
			String newIMEI1 = mIMEIEdit1.getEditableText().toString();
			boolean b1 = CheckIMEI(newIMEI1);
			if (newIMEI1 != null && b1) {
				try {
					//mNv.setIMEI(newIMEI1+"1");
					mIMEIEdit1.setTextColor(Color.WHITE);
					Toast.makeText(this, R.string.set_successful1,
							Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}else {
				mIMEIEdit1.setTextColor(Color.RED);
				Toast.makeText(this, R.string.imei_erro_tip1, Toast.LENGTH_SHORT)
						.show();
				return;
			}
				break;
		 case R.id.bt_cancle:
			 android.util.Log.i("....zzj","----------------222-------");
			 ChangeImei.this.finish();
			break;
		}
	}
}
