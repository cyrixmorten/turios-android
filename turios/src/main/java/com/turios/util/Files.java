package com.turios.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

public class Files {

	public static int lineCount(File file) {
		int lineCount = 0;
		LineNumberReader reader = null;
		try {
			reader = new LineNumberReader(new FileReader(file));
			while ((reader.readLine()) != null)
				;
			lineCount = reader.getLineNumber();
		} catch (Exception ex) {
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return lineCount;
	}
}
