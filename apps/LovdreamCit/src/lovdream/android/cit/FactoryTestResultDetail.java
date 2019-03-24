/**
 * 
 */
package lovdream.android.cit;

import java.io.IOException;
import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
//import com.qualcomm.qcnvitems.QcNvItems;
import com.lovdream.util.SystemUtil;

public class FactoryTestResultDetail extends ListActivity {
	private String[] mTestResultString;
	//private QcNvItems mNv;
	private final static String tab = "    ";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
//		mNv = new QcNvItems(this);
		
		if (FactoryTestResult.resultType == CITMain.MACHINE_AUTO_TEST) {
			String[] s1 = getResources().getStringArray(
					R.array.MachineAutoTestResultStrings);
			ArrayList<String> d = new ArrayList<String>();
			int len = s1.length;
			for(int i=0;i<len;i++)
			{
				if (!((!CITOneByOne.HasBackMic && i == 4) ||   (CITOneByOne.NoFm && i==7) || (CITOneByOne.NoGsensor && i==9) 
						|| (!CITOneByOne.HasButtonLight && i==10) || (CITOneByOne.NoFlashLight && i==11) || (!CITOneByOne.HasBreathingLight && i == 15) 
						|| (!CITOneByOne.HasGyroSensor && i == 16) || ((CITMain.mIsA500 || !CITOneByOne.HasNFC) && i==17) || (!CITOneByOne.HasHall && i==18)
						|| (!CITOneByOne.HasCompass && i == 19) || (!CITOneByOne.HasOTG && i == 20) || (!CITOneByOne.Has8Pin && i==21) || (!CITOneByOne.Has8PinOtg && i==22) 
						|| (CITOneByOne.NoWifi && i == 24) || (CITOneByOne.NoBt && i==25) || (CITOneByOne.NoLightsensor && i==27) 
						|| (CITOneByOne.NoDistancesensor && i==28) || (CITOneByOne.NoGps && i==29) || (!CITOneByOne.HasTwoColorLights && i==30) )){
					d.add(s1[i]);
				}
			}
			len = d.size();
			mTestResultString = new String[len];
			mTestResultString = d.toArray(mTestResultString);
			d.clear();
			d = null;
		} else if(FactoryTestResult.resultType == CITMain.PCBA_AUTO_TEST) {
			String[] s1 = getResources().getStringArray(
					R.array.PCBAAutoTestResultStrings);
			ArrayList<String> d = new ArrayList<String>();
			int len = s1.length;
			for(int i=0;i<len;i++)
			{
				if (!((!CITOneByOne.HasBackMic && i == 4) || (CITOneByOne.NoFm && i==7) || (CITOneByOne.NoGsensor && i==9) 
						|| (!CITOneByOne.HasButtonLight && i==10) || (CITOneByOne.NoFlashLight && i==11) || (!CITOneByOne.HasBreathingLight && i == 13) 
						|| (!CITOneByOne.HasGyroSensor && i == 14) || ((CITMain.mIsA500 || !CITOneByOne.HasNFC) && i==15) || (!CITOneByOne.HasHall && i==16) 
						|| (!CITOneByOne.HasCompass && i == 17) || (!CITOneByOne.HasOTG && i == 18) || (!CITOneByOne.Has8Pin && i == 19) || (!CITOneByOne.Has8PinOtg && i == 20) 
						|| (CITOneByOne.NoWifi && i==22) || (CITOneByOne.NoBt && i==23) || (CITOneByOne.NoLightsensor && i==25) 
						|| (CITOneByOne.NoDistancesensor && i==26) || (CITOneByOne.NoGps && i==27) || (!CITOneByOne.HasTwoColorLights && i==28))){
					d.add(s1[i]);
				}
			}
			len = d.size();
			mTestResultString = new String[len];
			mTestResultString = d.toArray(mTestResultString);
			d.clear();
			d = null;
		} else if (FactoryTestResult.resultType == CITMain.FASTMMI_TEST) {
			String[] s1 = getResources().getStringArray(
					R.array.FASTMMITestStrings);
			ArrayList<String> d = new ArrayList<String>();
			int len = s1.length;
			for (int i = 0; i < len; i++) {

				d.add(s1[i]);
			}
			len = d.size();
			mTestResultString = new String[len];
			mTestResultString = d.toArray(mTestResultString);
			d.clear();
			d = null;
		}

		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResultDetails()));
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	private String[] getResultDetails() {
		String NV2499 = "";
		String pcbaString = "";
		String machineString = "";
		String machineString2 = "";
		String fastmmiString = "";
		NV2499 = SystemUtil.getNvFactoryData3I();
		pcbaString = NV2499.substring(64, 84).replace("B", "");
		machineString = NV2499.substring(32, 54).replace("B", "");
		machineString2 = NV2499.substring(96, 104).replace("B", "");
		fastmmiString = NV2499.substring(99, 124);
		
		Log.d("CJ", "mTestResultString.length"+mTestResultString.length);
		Log.d("CJ", "pcbaString.length()"+pcbaString.length());
		Log.d("CJ", "machineString.length()"+machineString.length());
		Log.d("CJ", "machineString2.length()"+machineString2.length());
		Log.d("CJ", "fastmmiString.length()"+fastmmiString.length());
		
		if (FactoryTestResult.resultType == CITMain.MACHINE_AUTO_TEST) {
			for (int i = 0; i < mTestResultString.length; i++) {
				if (machineString.substring(i, i+1).equals("P")) {
					mTestResultString[i] = mTestResultString[i] + tab + getString(R.string.result_pass);
				} else if (machineString.substring(i, i+1).equals("F")) {
					mTestResultString[i] = mTestResultString[i] + tab + getString(R.string.result_fail);
				} else if (machineString.substring(i, i+1).equals("U")) {
					mTestResultString[i] = mTestResultString[i] + tab + getString(R.string.result_not_tested);
				}
			}
		} else if(FactoryTestResult.resultType == CITMain.PCBA_AUTO_TEST) {
			
			for (int i = 0; i < mTestResultString.length; i++) {
				if (pcbaString.substring(i, i+1).equals("P")) {
					mTestResultString[i] = mTestResultString[i] + tab + getString(R.string.result_pass);
				} else if (pcbaString.substring(i, i+1).equals("F")) {
					mTestResultString[i] = mTestResultString[i] + tab + getString(R.string.result_fail);
				} else if (pcbaString.substring(i, i+1).equals("U")) {
					mTestResultString[i] = mTestResultString[i] + tab + getString(R.string.result_not_tested);
				}
			}
		} else if (FactoryTestResult.resultType == CITMain.FASTMMI_TEST) {
			for (int i = 0; i < mTestResultString.length; i++) {
				if (fastmmiString.substring(i, i+1).equals("P")) {
					mTestResultString[i] = mTestResultString[i] + tab + getString(R.string.result_pass);
				} else if (fastmmiString.substring(i, i+1).equals("F")) {
					mTestResultString[i] = mTestResultString[i] + tab + getString(R.string.result_fail);
				} else if (fastmmiString.substring(i, i+1).equals("U")) {
					mTestResultString[i] = mTestResultString[i] + tab + getString(R.string.result_not_tested);
				}
			}
		}
		
		return mTestResultString;
	}
}
