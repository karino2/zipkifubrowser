package com.googlecode.zipkifubrowser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class ZipKifuBrowserActivity extends Activity {
	public static final String LAST_ZIP_PATH_KEY = "last_zip_path";	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		SharedPreferences prefs = getSharedPreferences("History", MODE_PRIVATE);
		String zipPath = prefs.getString(ZipKifuBrowserActivity.LAST_ZIP_PATH_KEY, "");
		
		if(!"".equals(zipPath))
		{
			Intent intent = new Intent(this, ListSummaryActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		Intent intent = new Intent(this, ZipParseActivity.class);
		startActivity(intent);
		finish();
		return;
    }
    
    
}