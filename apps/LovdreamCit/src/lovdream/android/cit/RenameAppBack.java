/**
 * 
 */
package lovdream.android.cit;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.os.SystemProperties;

public class RenameAppBack extends Activity {
	CheckBox mRenameCheckBox;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rename_app_back);
		mRenameCheckBox = (CheckBox) findViewById(R.id.rename_app_back);
		File file = new File("system/app_back");
		Log.d("CJ", "file.exists()="+file.exists());
		if (file.exists()) {
			mRenameCheckBox.setChecked(true);
			mRenameCheckBox.setText("system/app_back");
		}else {
			mRenameCheckBox.setChecked(false);
			mRenameCheckBox.setText("system/app_back_off");
		}
		mRenameCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Log.d("CJ", "isChecked="+isChecked);
				if (isChecked) {                       
					SystemProperties.set("ctl.start", "rn_appbackoff");
					mRenameCheckBox.setText("system/app_back");
				} else {
					SystemProperties.set("ctl.start", "rn_appback");
					mRenameCheckBox.setText("system/app_back_off");
				}
			}
		});
	}
}
