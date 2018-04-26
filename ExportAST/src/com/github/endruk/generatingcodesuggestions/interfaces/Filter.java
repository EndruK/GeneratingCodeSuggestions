package com.github.endruk.generatingcodesuggestions.interfaces;

import java.io.File;

public interface Filter {
	boolean interested(int level, String path, File file);
}
