package lovdream.android.cit;


import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;


public class CITBroadcastReceiver extends BroadcastReceiver {
  
    public CITBroadcastReceiver() {
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
     
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setClass(context, CITMain.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
    }
}
