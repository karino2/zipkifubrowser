package com.googlecode.zipkifubrowser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipReader {
	
	StreamHandlable streamHandler;
	ZipFile zipFile;
	
	public static ZipReader create(String path) throws IOException
	{
		return new ZipReader(new ZipFile(path), new KifuStreamHandler());
	}
	
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
		if(ent.isDirectory())
			return; // skip.
		
		InputStream is = zipFile.getInputStream(ent);		
		streamHandler.action(is);
	}
}
