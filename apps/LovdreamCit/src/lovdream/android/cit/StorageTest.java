package lovdream.android.cit;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import java.io.*;

public class StorageTest extends Activity {

	public StorageTest() {
		char ac[] = new char[4];
		buffer = ac;
		char ac1[] = new char[4];
		bufferReader = ac1;
	}

	private boolean compareTempFile() {
		boolean flag;
		if (bufferReader.toString() == "" || buffer.toString() == "") {
			m_nTestInfo = 3;
			deleteFile("\\sdcard\\test.txt");
			flag = false;
		} else {
			char ac[] = bufferReader;
			String s = new String(ac);
			char ac1[] = buffer;
			String s1 = new String(ac1);
			boolean flag1;
			if (s.equals(s1)) {
				flag1 = true;
				m_nTestInfo = 3;
			} else {
				m_nTestInfo = 4;
				flag1 = false;
			}
			flag = flag1;
		}
		return flag;
	}

	private boolean readTempFile() {
		boolean flag = true;
		File file = new File("\\sdcard\\test.txt");
		try {
			FileReader filereader = new FileReader(file);
			char ac[] = bufferReader;
			filereader.read(ac);
			filereader.close();
			file.delete();
			return flag;
		} catch (IOException ioexception) {
			flag = false;
			Log.e("LOG_TAG", ioexception.getLocalizedMessage());
			return flag;
		}
	}

	private boolean searchCard() {
		boolean flag;
		if (Environment.getExternalStorageState().equals("removed")) {
			m_nTestInfo = 0;
			flag = false;
		} else {
			flag = true;
		}
		return flag;
	}

	private void showErrorMsg() {
		String s = "";

		mTextView = (TextView) findViewById(R.id.storage_text);
		switch (m_nTestInfo) {
		case 0:
			s = getString(R.string.storage_message_0);
			break;
		case 1:
			s = getString(R.string.storage_message_1);
			break;
		case 2:
			s = getString(R.string.storage_message_2);
			break;
		case 3:
			s = getString(R.string.storage_message_3);
			break;
		case 4:
			s = getString(R.string.storage_message_4);
		}
		mTextView.setText(s);
		return;
	}

	private void startTestStorage() {
		if (!searchCard())
			showErrorMsg();
		else if (!writeTempFile())
			showErrorMsg();
		else if (!readTempFile())
			showErrorMsg();
		else if (!compareTempFile())
			showErrorMsg();
		else
			showErrorMsg();
	}

	private boolean writeTempFile() {
		boolean flag = true;
		File file = new File("\\sdcard\\test.txt");

		try {
			FileWriter filewriter = new FileWriter(file);
			for (int i = 0; i < 4; i++)
				buffer[i] = 'a';

			if (file.canWrite()) {
				char ac[] = buffer;
				filewriter.write(ac);
				filewriter.flush();
			}
			filewriter.close();
		} catch (IOException ioexception) {
			flag = false;
			m_nTestInfo = 1;
			Log.e("LOG_TAG", ioexception.getLocalizedMessage());
		}
		return flag;
	}

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.test_storage);
		startTestStorage();
	}

	private static final int BUFF_SIZE = 4;
	private static final String LOG_TAG = "StorageCard";
	private static final String strFilePaht = "\\sdcard\\test.txt";
	char buffer[];
	char bufferReader[];
	private TextView mTextView;
	private int m_nTestInfo;
}
