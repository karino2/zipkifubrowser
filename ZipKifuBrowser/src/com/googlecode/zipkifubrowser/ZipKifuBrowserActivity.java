package com.googlecode.zipkifubrowser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ZipKifuBrowserActivity extends Activity {
	private final int REQUEST_PICK_DIRECTORY = 1;
	
	
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
				EditText et = getFolderPathEditText();
				String path = et.getText().toString();
				showMessage("parse start with " + path);
			}
        
        });
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
    
}