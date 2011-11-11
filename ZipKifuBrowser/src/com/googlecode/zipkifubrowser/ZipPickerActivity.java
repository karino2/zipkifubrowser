package com.googlecode.zipkifubrowser;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ZipPickerActivity extends Activity {
	File currentDir = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zip_picker_view);
        ListView list = (ListView)findViewById(R.id.directoryListView);
        list.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, listFiles("/sdcard/")));
        // list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, listDirectory("/sdcard/")));
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setItemsCanFocus(false);
        
        list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				String path = listedFiles[position];
				File selectedFile = new File(currentDir, path);
				if(selectedFile.isDirectory())
				{
					String message = "handle dril down here";
					showMessage("handle dril down here");
					return;
				}
				
				Intent result = new Intent();
				// TODO: should handle back
				result.setData(Uri.fromFile(selectedFile));
	            setResult(RESULT_OK, result);
	            /*
				if(selectedFile != null)
				{
					result.setData(Uri.fromFile(selectedFile));
		            setResult(RESULT_OK, result);
				}
				else
				{
		            setResult(RESULT_CANCELED, result);
				}
				*/
	            finish();
				
			}
        	
        });
        
        
     }
    
    String[] listedFiles = null;
    String[] listFiles(String path)
    {
    	listedFiles = listFilesCore(path);
    	return listedFiles;
    }
    
    String[] listFilesCore(String path)
    {
    	currentDir = new File(path);
    	ArrayList<String> ret = new ArrayList<String>();
    	ret.add("..");
    	String[] files = listDirOrZip();
    	for(String fname : files)
    		ret.add(fname);
    	return ret.toArray(files);
    }

	String[] listDirOrZip() {
		String[] files = currentDir.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String filename) {
				File f = new File(dir, filename);
				if(f.isDirectory())
					return true;
				if(f.getName().endsWith(".zip"))
					return true;
				return false;
			}
		});
		return files;
	}

	void showMessage(String message) {
		Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
		toast.show();
	}
 }
