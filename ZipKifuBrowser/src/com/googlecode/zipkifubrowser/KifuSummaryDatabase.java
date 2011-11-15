package com.googlecode.zipkifubrowser;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class KifuSummaryDatabase implements KifuSummaryStorable {
    private static final String DATABASE_NAME = "zip_kifu_summary.db";
    private static final int DATABASE_VERSION = 1;
    private static final String SUMMARY_TABLE_NAME = "summary";
    private static final String TAG = "KifuSummaryDatabase";
	
	
    static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + SUMMARY_TABLE_NAME + " ("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "PATH TEXT,"
                    + "BEGIN INTEGER,"
                    + "END INTEGER,"
                    + "KISEN TEXT,"
                    + "SENKEI TEXT,"
                    + "SENTE TEXT,"
                    + "GOTE TEXT,"
                    + "KISENSYOUSAI TEXT"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            recreate(db);
        }

		public void recreate(SQLiteDatabase db) {
			db.execSQL("DROP TABLE IF EXISTS " + SUMMARY_TABLE_NAME);
            onCreate(db);
		}		
    }

    DatabaseHelper dbHelper;
    SQLiteDatabase database;
	public void open(Context context) {
    	dbHelper = new DatabaseHelper(context);
    	database = dbHelper.getWritableDatabase();
    }
	
	public void close() {
		dbHelper.close();
	}
	
	public void recreate() {
		dbHelper.recreate(database);
	}
    
	@Override
	public void save(KifuSummary summary) {
		ContentValues values = new ContentValues();
		values.put("PATH", summary.getPath());
		values.put("BEGIN", summary.getBegin().getTime());
		values.put("END", summary.getEnd().getTime());
		values.put("KISEN", summary.getKisen());
		values.put("SENKEI", summary.getSenkei());
		values.put("SENTE", summary.getSente());
		values.put("GOTE", summary.getGote());
		values.put("KISENSYOUSAI", summary.getKisenSyousai());
		
		database.insert(SUMMARY_TABLE_NAME, null, values);		
	}
	
	public Cursor fetchAllKifuSummary(String selection, String[] selectionArgs) {
		return database.query(SUMMARY_TABLE_NAME,
				new String[] { "_id",
				"PATH",
				"BEGIN", "END", "KISEN", "SENKEI", "SENTE", "GOTE", "KISENSYOUSAI" },
				selection, selectionArgs,
				null, null, "BEGIN DESC");	
	}
	
	public String[] fetchSenkei() {
		Cursor cursor = database.query(true, SUMMARY_TABLE_NAME,
				new String[]{"SENKEI"}, null, null, null, null, null, null);
		ArrayList<String> list = new ArrayList<String>();
		while(!cursor.isLast())
		{
			cursor.moveToNext();
			list.add(cursor.getString(0));
		}
		cursor.close();
		return list.toArray(new String[0]);
	}
	
	public Cursor fetchKisi(String prefix) {
		Cursor cursor1 = database.query(true, SUMMARY_TABLE_NAME,
				new String[]{"SENTE"}, "SENTE LIKE ?", new String[] { prefix+"%" }, null, null, null, null);
		Cursor cursor2 = database.query(true, SUMMARY_TABLE_NAME,
				new String[]{"GOTE"}, "GOTE LIKE ?", new String[] { prefix+"%" }, null, null, null, null);
		return new MergedCursor(cursor1, cursor2);
		
	}
	
	public String[] fetchKisiAsArray() {
		Set<String> set = new TreeSet<String>();
		Cursor cursor = database.query(true, SUMMARY_TABLE_NAME,
				new String[]{"SENTE"}, null, null, null, null, null, null);
		copyTo(cursor, set);
		cursor.close();
		cursor = database.query(true, SUMMARY_TABLE_NAME,
				new String[]{"GOTE"}, null, null, null, null, null, null);
		copyTo(cursor, set);
		cursor.close();
		return set.toArray(new String[0]);
	}

	void copyTo(Cursor cursor, Set<String> set) {
		while(!cursor.isLast())
		{
			cursor.moveToNext();
			if(cursor.getString(0) != null)
				set.add(cursor.getString(0));
		}
	}

	public KifuSummary fetchKifuSummary(long id) {
		Cursor cursor = database.query(true, SUMMARY_TABLE_NAME, new String[] { "_id",
				"PATH",
				"BEGIN", "END", "KISEN", "SENKEI", "SENTE", "GOTE", "KISENSYOUSAI" },
				"_id =" + id, null, null, null, null, null);
		if(cursor == null)
			throw new RuntimeException("no kifu data in specified id " + id + ", what's happen?");
		cursor.moveToFirst();
		KifuSummary summary = new KifuSummary(cursor.getString(1));
		summary.setBegin(new Date(cursor.getInt(2)));
		summary.setEnd(new Date(cursor.getInt(3)));
		summary.setKisen(cursor.getString(4));
		summary.setSenkei(cursor.getString(5));
		summary.setSente(cursor.getString(6));
		summary.setGote(cursor.getString(7));
		summary.setKisenSyousai(cursor.getString(8));
		cursor.close();
		return summary;
	}
	
	/*
	public static KifuSummary toKifuSummary()
	{
		return null;
	}
	*/

}
