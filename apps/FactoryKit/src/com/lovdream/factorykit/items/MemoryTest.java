
package com.lovdream.factorykit.items;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.lovdream.factorykit.R;
import com.lovdream.factorykit.TestItemBase;
import com.swfp.utils.Utils;

public class MemoryTest extends TestItemBase{

	
	TextView tv_ram;
	TextView tv_rom;

	public String getKey(){
		return "memory_test";
	}

	@Override
	public String getTestMessage(){
		return getActivity().getString(R.string.memory_test_mesg);
	}

	@Override
	public void onStartTest() {
		
	}

	@Override
	public void onStopTest() {
		
	}
	
	public View getTestView(LayoutInflater inflater){
		View v = inflater.inflate(R.layout.memory_test,null);
		tv_ram = (TextView)v.findViewById(R.id.ram_info);
		tv_rom = (TextView)v.findViewById(R.id.rom_info);
		
		enableSuccess(true);
		
		
		String ramInfo = getResources().getString(R.string.memory_test_ram)+Utils.getRamTotalSize(getActivity());
		String romInfo = getResources().getString(R.string.memory_test_rom)+Utils.getRomTotalSize(getActivity());
		tv_ram.setText(ramInfo);
		tv_rom.setText(romInfo);
		
		return v;
	}

}
