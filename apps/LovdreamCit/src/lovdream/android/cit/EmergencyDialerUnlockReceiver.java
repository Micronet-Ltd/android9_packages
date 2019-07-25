/**
 * 
 */
package lovdream.android.cit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class EmergencyDialerUnlockReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		try {
			Log.i("CJ", "rm password");
    		Runtime.getRuntime().exec("rm /data/system/gesture.key /data/system/password.key");
		} catch (Exception e) {
			// TODO: handle exception
			Log.i("CJ", "rm fail");
		}
	}
}
