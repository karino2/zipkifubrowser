package com.googlecode.zipkifubrowser;

import java.io.BufferedReader;
import java.io.IOException;

public interface StreamHandlable {
	void action(String entryName, BufferedReader is)  throws IOException;

}
