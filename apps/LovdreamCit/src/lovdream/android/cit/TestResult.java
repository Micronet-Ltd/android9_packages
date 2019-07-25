/**
 * 
 */
package lovdream.android.cit;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TestResult extends ListActivity {
	
	private String as[];
	private ArrayList<String> Items = new ArrayList<String>();
	public static int CITtype = -1;
	public static final int MACHINE_AUTO_TEST = 1;
	public static final int PCBA_AUTO_TEST = 2;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		getListItems();
	}
	
	private void getListItems() {
		as = getResources().getStringArray(R.array.TestResult);
		int j = 0;
		do {
			if (j < as.length) {
				Items.add(as[j]);
				j++;
			} else {
				setListAdapter(new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1, Items));
				getListView().setTextFilterEnabled(true);
				return;
			}
		} while (true);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent();
		switch (position) {
		case 0:
			CITtype = PCBA_AUTO_TEST;
			intent.setClass(this, PCBAorMachineResult.class);
			startActivity(intent);
			break;
		case 1:
			CITtype = MACHINE_AUTO_TEST;
			intent.setClass(this, PCBAorMachineResult.class);
			startActivity(intent);
			break;
		}
	}
}
