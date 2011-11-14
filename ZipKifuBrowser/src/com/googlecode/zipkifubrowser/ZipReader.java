package com.googlecode.zipkifubrowser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.util.Log;

public class ZipReader {
	
	StreamHandlable streamHandler;
	ZipFile zipFile;
	
	public ZipReader(ZipFile zf, KifuStreamHandler kh)
	{
		zipFile = zf;
		streamHandler = kh;
	}
	
	Enumeration<? extends ZipEntry> entries;
	public void start()
	{
		entries = zipFile.entries();
	}
	
	public boolean isRunning()
	{
		return entries.hasMoreElements();
	}
	
	public void doOne() throws IOException
	{
		ZipEntry ent = entries.nextElement();
		if(ent.isDirectory() ||
				!ent.getName().endsWith(".KI2"))
			return; // skip.
		
		InputStream is = zipFile.getInputStream(ent);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "Shift_JIS"), 8*1024);
		try
		{
			streamHandler.action(ent.getName(), reader);			
		}catch (IllegalArgumentException e)
		{
			Log.w("ZKB", "date illegal. name = "+ ent.getName());
		}
	}
}
