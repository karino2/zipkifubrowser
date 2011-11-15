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

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ListSummaryActivity extends ListActivity {
	KifuSummaryDatabase database;
	FilterCondition filterCondition;
    static final int FROM_DATE_DIALOG_ID = 1;
    static final int TO_DATE_DIALOG_ID = 2;    
    
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	switch(id) {
    	case FROM_DATE_DIALOG_ID:
    		Date from = filterCondition.getFromOrDefault();
    		return new DatePickerDialog(this, new OnDateSetListener() {
				
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear,
						int dayOfMonth) {
					Date from = new Date(year-1900, monthOfYear, dayOfMonth);
					filterCondition.setFrom(from);
					applyNewFilterCondition();
				}
			}, from.getYear()+1900, from.getMonth(), from.getDay());
    	case TO_DATE_DIALOG_ID:
    		Date to = filterCondition.getToOrDefault();
    		return new DatePickerDialog(this, new OnDateSetListener() {
				
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear,
						int dayOfMonth) {
					Date to = new Date(year-1900, monthOfYear, dayOfMonth);
					filterCondition.setTo(to);
					applyNewFilterCondition();
				}
			},  to.getYear()+1900, to.getMonth(), to.getDay());
    		
    	}
    	return null;
    }
    
    public void applyNewFilterCondition()
    {
    	ExpandableListView elv = findExpandableFilter();
    	if(elv.isGroupExpanded(0))
    	{
    		updateFromDisplay(elv.findViewById(R.id.fromControl));
    		updateToDisplay(elv.findViewById(R.id.toControl));
    	}
    	refreshSummaryList();
    }
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		filterCondition = new FilterCondition();
		setContentView(R.layout.list_summary);
		this.getListView().setDividerHeight(2);
		
		ExpandableListView elv = findExpandableFilter();
		elv.setAdapter(new ExpandableFilterAdapter(getLayoutInflater()));
				
		database = new KifuSummaryDatabase();
		database.open(this);
		fillData();
        getListView().setOnCreateContextMenuListener(this);		
	}

	ExpandableListView findExpandableFilter() {
		ExpandableListView elv = (ExpandableListView)findViewById(R.id.expandableFilter);
		return elv;
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
	
	private void refreshSummaryList() {
		newCursor();
		summaryAdapter.changeCursor(cursor);
	}
	SimpleCursorAdapter summaryAdapter;
	private void fillData() {
		newCursor();
		
		String[] from = new String[] { "BEGIN", "KISENSYOUSAI", "SENKEI", "SENTE", "GOTE" };
		int[] to = new int[] { R.id.beginDateTextView,
				R.id.syousaiTextView, R.id.senkeiTextView, R.id.senteTextView, R.id.goteTextView };

		summaryAdapter = new SimpleCursorAdapter(this,
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

	void newCursor() {
		if(cursor != null)
			stopManagingCursor(cursor);
		cursor = database.fetchAllKifuSummary(filterCondition.generateQuery(), filterCondition.generateQueryArg());
		startManagingCursor(cursor);
	}

	
	void updateFromDisplay(final View fromControl) {
		setDate(fromControl, R.id.fromDateEdit, filterCondition.getFromOrDefault());
		findEditText(fromControl, R.id.fromDateEdit).setEnabled(filterCondition.isFromEnable());
	}


	void updateToDisplay(final View toControl) {
		setDate(toControl, R.id.toDateEdit, filterCondition.getToOrDefault());
		findEditText(toControl, R.id.toDateEdit).setEnabled(filterCondition.isToEnable());
	}

	private void setDate(View holder, int editId, Date dt) {
		EditText et = (EditText)holder.findViewById(editId);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		et.setText(sdf.format(dt));
	}
	EditText findEditText(View holder, int id)
	{
		return (EditText)holder.findViewById(id);
	}

	Spinner findSpinner(View view, int id) {
		return (Spinner)view.findViewById(id);
	}

	class ExpandableFilterAdapter extends BaseExpandableListAdapter {
		
		View filterView;
		LayoutInflater factory;
		
		
		
		View getFilterView(ViewGroup parent) {
			if(filterView == null)
				filterView = factory.inflate(R.layout.filter_view, parent, false);
			return filterView;
		}
		
		ExpandableFilterAdapter(LayoutInflater factory)
		{
			this.factory = factory;
		}

		@Override
		public Object getChild(int groupPos, int childPosition) {
			// dummy now.
			switch(childPosition) {
			case 0:
				return R.id.fromControl;
			case 1:
				return R.id.toControl;
			case 2:
				return R.id.senkeiControl;
			case 3:
				return R.id.kisiControl;
			}
			throw new RuntimeException("never reached here");
		}

		@Override
		public long getChildId(int groupPos, int childPos) {
			return childPos;
		}

		@Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {
			if(convertView != null)
				return convertView;
			View child = findChildViewFirstTime(childPosition, parent);
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            child.setLayoutParams(lp);
			
			return child;
		}
		
		CheckBox findCheckBox(View holder, int id)
		{
			return (CheckBox)holder.findViewById(id);
		}
		
		void bindFromControl(final View fromControl)
		{
			updateFromDisplay(fromControl);
			EditText fromEdit = findEditText(fromControl, R.id.fromDateEdit);
			fromEdit.setEnabled(filterCondition.isFromEnable());
			fromEdit.setOnTouchListener(new OnTouchListener() {				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					showDialog(FROM_DATE_DIALOG_ID);
					return true;
				}
			});
			
			findCheckBox(fromControl, R.id.fromCheck).setOnCheckedChangeListener(new OnCheckedChangeListener() {				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					filterCondition.setFromEnable(isChecked);
					findEditText(fromControl, R.id.fromDateEdit).setEnabled(isChecked);					
					applyNewFilterCondition();
				}
			});
		}

		void bindToControl(final View toControl)
		{
			updateToDisplay(toControl);
			EditText toEdit = findEditText(toControl, R.id.toDateEdit);
			toEdit.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					showDialog(TO_DATE_DIALOG_ID);
					return true;
				}
			});
			findCheckBox(toControl, R.id.toCheck).setOnCheckedChangeListener(new OnCheckedChangeListener() {				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					filterCondition.setToEnable(isChecked);
					findEditText(toControl, R.id.toDateEdit).setEnabled(isChecked);					
					applyNewFilterCondition();
				}
			});
		}

		String[] senkeiArray;
		
		private void bindSenkeiControl(final View senkeiControl) {
			findSpinner(senkeiControl, R.id.senkeiSpinner).setEnabled(filterCondition.isSenkeiEnabled());
			findCheckBox(senkeiControl, R.id.senkeiCheck).setOnCheckedChangeListener(new OnCheckedChangeListener() {				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(senkeiArray == null)
					{
						Spinner spinner = (Spinner)findSpinner(senkeiControl, R.id.senkeiSpinner);
						senkeiArray = database.fetchSenkei();
						
						ArrayAdapter<String> adapter =
							new ArrayAdapter<String>(ListSummaryActivity.this,
									android.R.layout.simple_spinner_item,
									senkeiArray);
				        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spinner.setAdapter(adapter);
					}
					filterCondition.setSenkeiEnabled(isChecked);
					findSpinner(senkeiControl, R.id.senkeiSpinner).setEnabled(isChecked);
					applyNewFilterCondition();
				}
			});
			
			Spinner spinner = findSpinner(senkeiControl, R.id.senkeiSpinner);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					filterCondition.setSenkei(senkeiArray[position]);
					applyNewFilterCondition();
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
		}

		String[] kisiArray;
		private void bindKisi(final View kisiControl) {
			findSpinner(kisiControl, R.id.kisiSpinner).setEnabled(filterCondition.isKisiEnabled());	
			findCheckBox(kisiControl, R.id.kisiCheck).setOnCheckedChangeListener(new OnCheckedChangeListener() {				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(kisiArray == null)
					{
						Spinner spinner = (Spinner)findSpinner(kisiControl, R.id.kisiSpinner);
						kisiArray = database.fetchKisi();
						
						ArrayAdapter<String> adapter =
							new ArrayAdapter<String>(ListSummaryActivity.this,
									android.R.layout.simple_spinner_item,
									kisiArray);
				        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spinner.setAdapter(adapter);
					}
					filterCondition.setKisiEnabled(isChecked);
					findSpinner(kisiControl, R.id.kisiSpinner).setEnabled(isChecked);
					applyNewFilterCondition();
				}
			});
			
			Spinner spinner = findSpinner(kisiControl, R.id.kisiSpinner);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					filterCondition.setKisi(kisiArray[position]);
					applyNewFilterCondition();
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
			
		}

		
		View findChildViewFirstTime(int childPosition, ViewGroup parent) {
			View view;
			switch(childPosition) {
			case 0:
				view = getFilterView(parent).findViewById(R.id.fromControl);
				bindFromControl(view);
				return view;
			case 1:
				view = getFilterView(parent).findViewById(R.id.toControl);
				bindToControl(view);
				return view;
			case 2:
				view = getFilterView(parent).findViewById(R.id.senkeiControl);
				bindSenkeiControl(view);
				return view;
			case 3:
				view = getFilterView(parent).findViewById(R.id.kisiControl);
				bindKisi(view);
				return view;
			}
			throw new RuntimeException("never reached here");
		}


		@Override
		public int getChildrenCount(int arg0) {
			return 4;
		}

		@Override
		public Object getGroup(int arg0) {
			// dummy now.
			return R.string.filter_label_expand;
		}

		@Override
		public int getGroupCount() {
			return 1;
		}

		@Override
		public long getGroupId(int arg0) {
			return 0;
		}
		
        public TextView getGenericView() {
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            TextView textView = new TextView(ListSummaryActivity.this);
            textView.setLayoutParams(lp);
            
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            textView.setPadding(64, 2, 0, 8);
            return textView;
        }
		

		@Override
		public View getGroupView(int arg0, boolean isExpanded, View convertView,
				ViewGroup parent) {
			TextView ret;
			if(convertView != null)
				ret = (TextView)convertView;
			else
				ret = getGenericView();
			
			if(isExpanded)
				ret.setText(R.string.filter_label_collapse);
			else
				ret.setText(R.string.filter_label_expand);
			return ret;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			return true;
		}
		
	}
	
}
