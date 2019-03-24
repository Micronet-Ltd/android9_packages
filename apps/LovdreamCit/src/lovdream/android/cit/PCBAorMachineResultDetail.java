/**
 * 
 */
package lovdream.android.cit;

import java.util.ArrayList;

import android.R.string;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Contacts.Intents;
import android.util.Log;
import android.widget.ArrayAdapter;

public class PCBAorMachineResultDetail extends ListActivity {
	
	private String[] mTestResultString;
	
	private String mTestTime;
	private String mTestResultCode;
	private String mTableName;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		if (TestResult.CITtype == TestResult.MACHINE_AUTO_TEST) {
			mTableName = "citMachineTestResult";
			String[] s1 = getResources().getStringArray(
					R.array.MachineAutoTestStrings);
			ArrayList<String> d = new ArrayList<String>();
			int len = s1.length;
			for(int i=0;i<len;i++)
			{
				if (!((!CITOneByOne.HasBackMic && i == 4) ||   (CITOneByOne.NoFm && i==7) || (CITOneByOne.NoGsensor && i==9) 
						|| (!CITOneByOne.HasButtonLight && i==10) || (CITOneByOne.NoFlashLight && i==11) || (!CITOneByOne.HasBreathingLight && i == 15) 
						|| (!CITOneByOne.HasGyroSensor && i == 16) || ((CITMain.mIsA500 || !CITOneByOne.HasNFC) && i==17) || (!CITOneByOne.HasHall && i==18)
						|| (!CITOneByOne.HasCompass && i == 19) || (!CITOneByOne.HasOTG && i == 20) || (!CITOneByOne.Has8Pin && i==21) || (!CITOneByOne.Has8PinOtg && i==22) 
						|| (CITOneByOne.NoWifi && i == 24) || (CITOneByOne.NoBt && i==25) || (CITOneByOne.NoLightsensor && i==27) 
						|| (CITOneByOne.NoDistancesensor && i==28) || (CITOneByOne.NoGps && i==29) || (!CITOneByOne.HasTwoColorLights && i==30)
						|| (!CITOneByOne.mIsDoubleScreen && i == 31) || (!CITOneByOne.HasBackCharging && i == 32))){
					d.add(s1[i]);
				}
			}
			len = d.size();
			mTestResultString = new String[len];
			mTestResultString = d.toArray(mTestResultString);
			d.clear();
			d = null;
		}else if (TestResult.CITtype == TestResult.PCBA_AUTO_TEST) {
			mTableName = "citPCBATestResult";
			String[] s1 = getResources().getStringArray(
					R.array.PCBAAutoTestStrings);
			ArrayList<String> d = new ArrayList<String>();
			int len = s1.length;
			for(int i=0;i<len;i++)
			{
				if (!((!CITOneByOne.HasBackMic && i == 4) || (CITOneByOne.NoFm && i==7) || (CITOneByOne.NoGsensor && i==9) 
						|| (!CITOneByOne.HasButtonLight && i==10) || (CITOneByOne.NoFlashLight && i==11) || (!CITOneByOne.HasBreathingLight && i == 13) 
						|| (!CITOneByOne.HasGyroSensor && i == 14) || ((CITMain.mIsA500 || !CITOneByOne.HasNFC) && i==15) || (!CITOneByOne.HasHall && i==16) 
						|| (!CITOneByOne.HasCompass && i == 17) || (!CITOneByOne.HasOTG && i == 18) || (!CITOneByOne.Has8Pin && i == 19) || (!CITOneByOne.Has8PinOtg && i == 20) 
						|| (CITOneByOne.NoWifi && i==22) || (CITOneByOne.NoBt && i==23) || (CITOneByOne.NoLightsensor && i==25) 
						|| (CITOneByOne.NoDistancesensor && i==26) || (CITOneByOne.NoGps && i==27) || (!CITOneByOne.HasTwoColorLights && i==28)
						|| (!CITOneByOne.mIsDoubleScreen && i == 29) || (!CITOneByOne.HasBackCharging && i == 30))){
					d.add(s1[i]);
				}
			}
			len = d.size();
			mTestResultString = new String[len];
			mTestResultString = d.toArray(mTestResultString);
			d.clear();
			d = null;
		}
		
		Intent intent = getIntent();
		mTestTime = intent.getStringExtra("testTime");
		Cursor cursor = getCursorOfDb(mTableName);
		cursor.moveToFirst();
		mTestResultCode = cursor.getString(cursor.getColumnIndex("result"));
		cursor.close();
		Log.d("CJ", "testResultString.length="+mTestResultString.length);
		for (int i = 0; i < mTestResultString.length; i++) {
			if (mTestResultCode.substring(i, i+1).equals("1")) {
				mTestResultString[i] = mTestResultString[i] + "    " +getString(R.string.success);
			}else if (mTestResultCode.substring(i, i+1).equals("0")) {
				mTestResultString[i] = mTestResultString[i] + "    " +getString(R.string.fail);
			}else {
				mTestResultString[i] = mTestResultString[i] + "    " +getString(R.string.not_complete);
			}
			
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mTestResultString));
	}
	
	private Cursor getCursorOfDb (String table){
		DatabaseHelper dbHelper = new DatabaseHelper(this,  
                "cit_test_result_db", 2);
        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.query(table, new String[] { "time"
        , "result"}, "time=?", new String[] { mTestTime }, null, null, null);
        return cursor;
	}
	
}
