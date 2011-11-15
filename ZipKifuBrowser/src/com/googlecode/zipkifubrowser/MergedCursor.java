package com.googlecode.zipkifubrowser;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

// only for Kisi name, so I assume row is only one.
public class MergedCursor implements Cursor {
	Cursor first;
	Cursor second;
	int position;
	
	public MergedCursor(Cursor first, Cursor second) {
		this.first = first;
		this.second = second;
		position = -1;
	}

	@Override
	public int getCount() {
		return first.getCount()+second.getCount();
	}

	@Override
	public int getPosition() {
		return position;
	}
	
	public boolean atFirstCursor() {
		return atFirstCursor(position);
	}
	
	public boolean atFirstCursor(int pos) {
		return first.getCount() > pos;		
	}
	
	public int secondOffset() {
		if(atFirstCursor())
			throw new RuntimeException("call secondOffset when at first cursor");
		return position - first.getCount();
	}	
	
	boolean firstMove(int offset) {
		if(position < -1)
			position = -1;
		return first.move(offset);
	}
	
	boolean firstMoveToPosition(int pos) {
		if(position < -1)
			position = -1;
		return first.moveToPosition(pos);
	}
	
	boolean secondMove(int offset) {
		if(position > getCount())
			position = -1; // is this OK?
		return second.move(offset);
	}

	boolean secondMoveToPosition(int secondPos) {
		if(position > getCount())
			position = -1; // is this OK?
		return second.moveToPosition(secondPos);
	}
	
	@Override
	public boolean move(int offset) {
		if(atFirstCursor() && atFirstCursor(position+offset))
		{
			position += offset;
			return firstMove(offset);
		}
		if(atFirstCursor() && !atFirstCursor(position+offset)) {
			position += offset;
			return secondMoveToPosition(secondOffset());			
		}
		if(!atFirstCursor() && atFirstCursor(position+offset)) {
			position += offset;
			return firstMoveToPosition(position);
		}
		// if(!atFirstCursor() && !atFirstCursor(position+offset)) {
		position += offset;
		return secondMove(offset);
	}

	@Override
	public boolean moveToPosition(int pos) {
		position = pos;
		if(atFirstCursor(pos))
			return first.moveToPosition(pos);
		return second.moveToPosition(secondOffset());
	}

	@Override
	public boolean moveToFirst() {
		position = 0;
		return first.moveToFirst();
	}

	@Override
	public boolean moveToLast() {
		position = getCount()-1;
		return second.moveToLast();
	}

	@Override
	public boolean moveToNext() {
		return move(1);
	}

	@Override
	public boolean moveToPrevious() {
		return move(-1);
	}

	@Override
	public boolean isFirst() {
		return (position == 0);
	}

	@Override
	public boolean isLast() {
		return position == getCount()-1;
	}

	@Override
	public boolean isBeforeFirst() {
		return position == -1;
	}

	// never happen in current implementation...
	@Override
	public boolean isAfterLast() {
		return position == getCount();
	}

	// first and second should be the same column set for my purpose.
	@Override
	public int getColumnIndex(String columnName) {
		return first.getColumnIndex(columnName);
	}

	@Override
	public int getColumnIndexOrThrow(String columnName)
			throws IllegalArgumentException {
		// TODO: temp implementation.
		if("_id".equals(columnName))
			return 1;
		return first.getColumnIndexOrThrow(columnName);
	}

	@Override
	public String getColumnName(int columnIndex) {
		return first.getColumnName(columnIndex);
	}

	@Override
	public String[] getColumnNames() {
		return first.getColumnNames();
	}

	@Override
	public int getColumnCount() {
		return first.getColumnCount();
	}

	@Override
	public byte[] getBlob(int columnIndex) {
		if(atFirstCursor())
			return first.getBlob(columnIndex);
		return second.getBlob(columnIndex);
	}

	@Override
	public String getString(int columnIndex) {
		// !! always return first column for my purpose.
		// TODO: might be 1.
		columnIndex = 0;
		if(atFirstCursor())
			return first.getString(columnIndex);
		return second.getString(columnIndex);
	}

	@Override
	public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {
		if(atFirstCursor()) {
			first.copyStringToBuffer(columnIndex, buffer);
			return;
		}
		second.copyStringToBuffer(columnIndex, buffer);
	}

	@Override
	public short getShort(int columnIndex) {
		if(atFirstCursor())
			return first.getShort(columnIndex);
		return second.getShort(columnIndex);
	}

	@Override
	public int getInt(int columnIndex) {
		if(atFirstCursor())
			return first.getInt(columnIndex);
		return second.getInt(columnIndex);
	}

	@Override
	public long getLong(int columnIndex) {
		// dummy _id handling
		if(columnIndex == 1)
			return position;
		
		if(atFirstCursor())
			return first.getLong(columnIndex);
		return second.getLong(columnIndex);
	}

	@Override
	public float getFloat(int columnIndex) {
		if(atFirstCursor())
			return first.getFloat(columnIndex);
		return second.getFloat(columnIndex);
	}

	@Override
	public double getDouble(int columnIndex) {
		if(atFirstCursor())
			return first.getDouble(columnIndex);
		return second.getDouble(columnIndex);
	}

	@Override
	public boolean isNull(int columnIndex) {
		if(atFirstCursor())
			return first.isNull(columnIndex);
		return second.isNull(columnIndex);
	}

	@Override
	public void deactivate() {
		first.deactivate();
		second.deactivate();
	}

	@Override
	public boolean requery() {
		boolean both = first.requery();
		both = both && second.requery();
		return both;
	}

	@Override
	public void close() {
		first.close();
		second.close();
	}

	@Override
	public boolean isClosed() {
		return first.isClosed() && second.isClosed();
	}

	@Override
	public void registerContentObserver(ContentObserver observer) {
		first.registerContentObserver(observer);
		second.registerContentObserver(observer);
	}

	@Override
	public void unregisterContentObserver(ContentObserver observer) {
		first.unregisterContentObserver(observer);
		second.unregisterContentObserver(observer);
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		first.registerDataSetObserver(observer);
		second.registerDataSetObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		first.unregisterDataSetObserver(observer);
		second.unregisterDataSetObserver(observer);
	}

	@Override
	public void setNotificationUri(ContentResolver cr, Uri uri) {
		first.setNotificationUri(cr, uri);
		second.setNotificationUri(cr, uri);
	}

	@Override
	public boolean getWantsAllOnMoveCalls() {
		return first.getWantsAllOnMoveCalls() && second.getWantsAllOnMoveCalls();
	}

	@Override
	public Bundle getExtras() {
		Bundle ret = first.getExtras();
		if(ret == null)
			return second.getExtras();
		ret.putAll(second.getExtras());
		return ret;
	}

	@Override
	public Bundle respond(Bundle extras) {
		throw new RuntimeException("respond, NYI");
	}

}
