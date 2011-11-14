package com.googlecode.zipkifubrowser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class ListSummaryActivity extends ListActivity {
	KifuSummaryDatabase database;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_summary);
		this.getListView().setDividerHeight(2);
		database = new KifuSummaryDatabase();
		database.open(this);
		fillData();
        getListView().setOnCreateContextMenuListener(this);		
	}
	
    public static final int MENU_ITEM_PARSE_ZIP = Menu.FIRST;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        menu.add(0, MENU_ITEM_PARSE_ZIP, 0, R.string.menu_parse_zip)
        .setShortcut('3', 'p');
        
        return true;

    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_ITEM_PARSE_ZIP:
            startActivity(new Intent(this, ZipParseActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	Cursor cursor;
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// decode and 
		
		SharedPreferences prefs = getSharedPreferences("History", MODE_PRIVATE);
		String zipPath = prefs.getString(ZipKifuBrowserActivity.LAST_ZIP_PATH_KEY, "");
		
		if(zipPath.equals(""))
		{
			showMessage("empty zip path when try to decode to tmp file. what's happen?");
			return ;
		}
		File tmpPath;
		try {
			tmpPath = decodeToTempPath(zipPath, id);
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setDataAndType(Uri.fromFile(tmpPath), "text/ki2");
			startActivity(i);
		} catch (IOException e) {
			showMessage("IOException when create temp ki2 file");
		}
	}
	
    void showMessage(String message)
    {
		Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
		toast.show();    	
    }
	
	private static final String ZIP_BROWSER_TMP = "zip_kifu_browser_tmp";
	private static final int TMP_FILE_MAX = 5;
	
	private File getTempDir() throws IOException
	{
		File tmp_dir = new File(Environment.getExternalStorageDirectory(), ZIP_BROWSER_TMP);
		if(!tmp_dir.exists())
		{
			if(!tmp_dir.mkdir())
				throw new IOException("can't create directory at: " + tmp_dir.getAbsolutePath());
		}
		return tmp_dir;
	}
	
	private void RemoveFileIfMoreThanLimit() throws IOException
	{
		File tmp_dir = getTempDir();
		File[] files = tmp_dir.listFiles();
		if(files.length > TMP_FILE_MAX)
			files[0].delete();
	}
	
	private File decodeToTempPath(String zipPath, long id) throws IOException {
		KifuSummary summary = database.fetchKifuSummary(id);
		File tmpFile = getTempFile();
		
		ZipFile zipFile = new ZipFile(zipPath);
		ZipEntry entry = zipFile.getEntry(summary.getPath());
		InputStream is = zipFile.getInputStream(entry);
		BufferedInputStream bis = new BufferedInputStream(is);

		final int size = 1024*10;
		byte[] tmpBuf = new byte[size];
		
		FileOutputStream os  = new FileOutputStream(tmpFile);
		int readLen = bis.read(tmpBuf);
		while(readLen != -1)
		{
			os.write(tmpBuf, 0, readLen);
			readLen = bis.read(tmpBuf);
		}
		os.close();
		
		return tmpFile;
	}

	File getTempFile() throws IOException {
		RemoveFileIfMoreThanLimit();
		File tmpFile = new File(getTempDir(), System.currentTimeMillis() + ".ki2");
		tmpFile.createNewFile();
		return tmpFile;
	}

	private void fillData() {
		cursor = database.fetchAllKifuSummary();
		startManagingCursor(cursor);
		
		String[] from = new String[] { "BEGIN", "KISENSYOUSAI", "SENKEI", "SENTE", "GOTE" };
		int[] to = new int[] { R.id.beginDateTextView,
				R.id.syousaiTextView, R.id.senkeiTextView, R.id.senteTextView, R.id.goteTextView };

		SimpleCursorAdapter summaryAdapter = new SimpleCursorAdapter(this,
				R.layout.list_summary_item, cursor, from, to);
		summaryAdapter.setViewBinder(new ViewBinder(){

			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				if(columnIndex == 2)
				{
					TextView tv = (TextView)view;
					SimpleDateFormat  sdf = new SimpleDateFormat("yyyy/MM/dd");
					tv.setText(sdf.format(new Date(cursor.getLong(columnIndex))));
					return true;
				}
				return false;
			}
			
		});
		setListAdapter(summaryAdapter);
	}
}
