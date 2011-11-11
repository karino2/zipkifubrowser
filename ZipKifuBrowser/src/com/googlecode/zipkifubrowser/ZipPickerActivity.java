package com.googlecode.zipkifubrowser;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ZipPickerActivity extends Activity {
	File currentDir = null;
	
	ListView fileList = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zip_picker_view);
        fileList = (ListView)findViewById(R.id.directoryListView);
        fileList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
        String initPath = "/sdcard/";
        showContents(fileList, initPath);
        
        
        fileList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				String path = listedFiles[position];
				if(path.equals(".."))
				{
					showContents(fileList, currentDir.getParent());
					return;
				}
				File selectedFile = new File(currentDir, path);
				if(selectedFile.isDirectory())
				{
					showContents(fileList, selectedFile.getAbsolutePath());
					return;
				}
				
				Intent result = new Intent();
				result.setData(Uri.fromFile(selectedFile));
	            setResult(RESULT_OK, result);
	            finish();
				
			}
        	
        });
        
        
     }

	void showContents(ListView list, String path) {
		ArrayAdapter<String> adap = new ArrayAdapter<String>(this, R.layout.list_item, listFiles(path));
        list.setAdapter(adap);
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
    	if(currentDir.getParent() != null)
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
