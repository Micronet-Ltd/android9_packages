/**
 * 
 */
package lovdream.android.cit;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * An example of how to use the NFC foreground dispatch APIs. This will intercept any MIME data
 * based NDEF dispatch as well as all dispatched for NfcF tags.
 */
public class NFCTest extends Activity implements OnClickListener{
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private TextView mText;
    private int mCount = 0;
    private Button successButton, failButton; 

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.test_nfc);
        mText = (TextView) findViewById(R.id.text);
        mText.setText(R.string.nfc_scan_tag);
		successButton = (Button) findViewById(R.id.success_test);
                successButton.setEnabled(false);
		failButton = (Button) findViewById(R.id.fail_test);
		successButton.setOnClickListener(this);
		failButton.setOnClickListener(this);

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        
        if (mAdapter != null && !mAdapter.isEnabled()) {
        	mAdapter.enable();
		}

        // Create a generic PendingIntent that will be deliver to this activity. The NFC stack
        // will fill in the intent with the details of the discovered tag before delivering to
        // this activity.
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Setup an intent filter for all MIME based dispatches
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        mFilters = new IntentFilter[] {
                ndef,
        };

        // Setup a tech list for all NfcF tags
        mTechLists = new String[][] { new String[] { NfcF.class.getName() } };
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mAdapter != null && !mAdapter.isEnabled()) {
        	mAdapter.enable();
		}
        if (mAdapter != null) mAdapter.enableForegroundDispatch(this, mPendingIntent, null,
                null);
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
        mText.setText(getString(R.string.nfc_success_scan) + ++mCount + getString(R.string.nfc_times));
        successButton.setEnabled(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) mAdapter.disableForegroundDispatch(this);
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		Bundle b = new Bundle();
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.success_test:
			b.putInt("test_result", 1);
			intent.putExtras(b);
			setResult(RESULT_OK, intent);
			finish();
			break;
			
		case R.id.fail_test:
			b.putInt("test_result", 0);
			intent.putExtras(b);
			setResult(RESULT_OK, intent);
			finish();
			break;

		default:
			break;
		}
	}
}

