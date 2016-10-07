package nl.desertspring.traffic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class Util {
	static InputStream openFile(File file) throws IOException {
		FileInputStream input = new FileInputStream(file);
				
		if (file.getName().endsWith(".gz")) {
			return new GZIPInputStream(input);
		} else {
			return input;
		}
	}
}
