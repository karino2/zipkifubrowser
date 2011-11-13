package com.googlecode.zipkifubrowser;

import java.io.IOException;
import java.util.zip.ZipFile;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ZipKifuBrowserActivity extends Activity {
	private final int REQUEST_PICK_DIRECTORY = 1;
	private final int DIALOG_ZIP_READ_PROGRESS_ID = 2;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button browseButton = (Button)findViewById(R.id.pickDirectoryButton);
        browseButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
		        Intent intent = new Intent(ZipKifuBrowserActivity.this, ZipPickerActivity.class);
		        startActivityForResult(intent,REQUEST_PICK_DIRECTORY);
			}
        	
        });
        
        Button startButton = (Button)findViewById(R.id.parseStartButton);
        startButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String path = getTargetZipPath();
//				showMessage("parse start with " + path);
				if(path == null || path.equals(""))
				{
					// should disable button this case, but anyway handle this case.
					showMessage("no zip selected.");
					return;
				}
				showDialog(DIALOG_ZIP_READ_PROGRESS_ID);
			}
        
        });
        
        Button listButton = (Button)findViewById(R.id.listSummaryButton);
        listButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
		        Intent intent = new Intent(ZipKifuBrowserActivity.this, ListSummaryActivity.class);
				startActivity(intent);
			}
        	
        });
        
        findButton(R.id.recreateTableButton).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				KifuSummaryDatabase db = new KifuSummaryDatabase();
				db.open(getApplicationContext());
				db.recreate();
				db.close();
				showMessage("recreate done");
			}
        	
        });
        
    }
    
    Button findButton(int id) { return (Button)findViewById(id); }
    
    
    protected Dialog onCreateDialog(int id){
    	Dialog dialog;
    	switch(id) {
    	case DIALOG_ZIP_READ_PROGRESS_ID:
    		dialog = startReadingZip();
    		break;
		default:
			dialog = null;
    	}
    	return dialog;
    }

	public static final String LAST_ZIP_PATH_KEY = "last_zip_path";

    class ZipReadTask extends AsyncTask<String, String, String> {

    	Context context;
    	ProgressDialog progress;
    	ZipReadTask(ProgressDialog prog, Context ctx)
    	{
    		progress = prog;
    		context = ctx;
    	}
    	
		@Override
		protected String doInBackground(String... arg0) {
			String zipPath = arg0[0];
			
			SharedPreferences prefs = getSharedPreferences("History", MODE_PRIVATE);
	    	SharedPreferences.Editor ed = prefs.edit();
	        ed.putString(LAST_ZIP_PATH_KEY, zipPath);
	        ed.commit();

			
			KifuSummaryDatabase db = new KifuSummaryDatabase();
			db.open(context);
			try			
			{
				publishProgress("start background reading");
				KifuStreamHandler ksh = new KifuStreamHandler(db);
				ZipReader zr = new ZipReader(new ZipFile(zipPath), ksh);
				
				zr.start();
				publishProgress("setup done");
				
				int processedNum = 0;
				while(zr.isRunning() && !isCancelled())
				{
					zr.doOne();
					publishProgress("parse [" + processedNum++ + "] file. " + ksh.getKisenSyousai());
					
					// for test code
					if(processedNum >= 100)
						break;
				}
			}catch(IOException ioe)
			{
				this.publishProgress("IOException! " + ioe.getMessage());
			}
			finally
			{
				db.close();
			}
			
			return null;
		}
		
		@Override
		protected void onProgressUpdate(String... arg)
		{
			progress.setMessage(arg[0]);
		}
		
		@Override
		protected void onPostExecute(String arg)
		{
			progress.dismiss();
		}
    }


    ZipReadTask zipReadTask;
	Dialog startReadingZip() {
		ProgressDialog progress = new ProgressDialog(this);
		progress.setTitle("ReadZip...");
		progress.setCancelable(true);
		
		zipReadTask = new ZipReadTask(progress, this);
				
		zipReadTask.execute(getTargetZipPath());
		progress.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				zipReadTask.cancel(false);				
			}
		});
		return progress;
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	switch(requestCode)
    	{
    	case REQUEST_PICK_DIRECTORY:
    		if(resultCode == Activity.RESULT_OK)
    		{
				EditText et = getFolderPathEditText();
				et.setText(data.getData().getPath());
				
				showDialog(DIALOG_ZIP_READ_PROGRESS_ID);				
//				showMessage(data.getData().getPath() + " selected");
    		}
    		break;
    	}
    }
    
    void showMessage(String message)
    {
		Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
		toast.show();    	
    }
	EditText getFolderPathEditText() {
		EditText et = (EditText)findViewById(R.id.folderPathEditText);
		return et;
	}



	String getTargetZipPath() {
		EditText et = getFolderPathEditText();
		String path = et.getText().toString();
		return path;
	}
    
}