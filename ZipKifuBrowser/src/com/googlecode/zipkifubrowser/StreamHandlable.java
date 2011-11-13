package com.googlecode.zipkifubrowser;

import java.io.IOException;
import java.io.InputStream;

public interface StreamHandlable {
	void action(InputStream is)  throws IOException;

}
