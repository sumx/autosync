package util;

import java.io.File;
import java.io.FilenameFilter;

public class TxtFilenameFilter implements FilenameFilter {
	public boolean accept(File dir, String name) {
		if (name != null && !name.toLowerCase().contains(".bin") && !name.toLowerCase().contains(".Ds")&& !name.toLowerCase().contains("source")&& !name.toLowerCase().contains(".swp")) {
			return true;
		} else {
			return false;
		}
	}
}
