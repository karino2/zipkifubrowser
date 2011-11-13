package com.googlecode.zipkifubrowser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class KifuStreamHandler implements StreamHandlable{

	String message = "";
	public void action(InputStream is) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "Shift_JIS"));
		message = reader.readLine();
	}

	public String getMessage() {
		return message;
	}
}
