package com.googlecode.zipkifubrowser;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
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
    }
    
    
    
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

    class ZipReadTask extends AsyncTask<String, String, String> {
    	
    	ProgressDialog progress;
    	ZipReadTask(ProgressDialog prog)
    	{
    		progress = prog;
    	}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			// do long task here.
			// publishProgress("");
			
			// dummy wait.
			int sleepCount = 0;
			while(!isCancelled())
			{
				try {
					publishProgress(arg0[0] + " sleep:" + sleepCount++);
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// do nothing
				}
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
		
		zipReadTask = new ZipReadTask(progress);
				
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
				
				showMessage(data.getData().getPath() + " selected");
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