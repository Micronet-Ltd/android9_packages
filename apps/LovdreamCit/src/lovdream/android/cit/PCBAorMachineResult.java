/**
 * 
 */
package lovdream.android.cit;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PCBAorMachineResult extends ListActivity {

	private String mTableName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		if (TestResult.CITtype == TestResult.PCBA_AUTO_TEST) {
			mTableName = "citPCBATestResult";
		}else if (TestResult.CITtype == TestResult.MACHINE_AUTO_TEST) {
			mTableName = "citMachineTestResult";
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Cursor cursor = getCursorOfDb(mTableName);
		int i = cursor.getCount();
		String[] timeResult = new String[i];
		while (cursor.moveToNext()) {
			timeResult[i-1] = cursor.getString(cursor.getColumnIndex("time"));
			i--;
		}
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, timeResult));
		if (cursor != null) {
			cursor.close();
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent();
		intent.putExtra("testTime", l.getAdapter().getItem(position).toString());
		intent.setClass(this, PCBAorMachineResultDetail.class);
		startActivity(intent);
	}
	
	private Cursor getCursorOfDb (String table){
		DatabaseHelper dbHelper = new DatabaseHelper(this,  
                "cit_test_result_db", 2);
        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.query(table, null, null, null, null, null, null);
        return cursor;
	}
}
