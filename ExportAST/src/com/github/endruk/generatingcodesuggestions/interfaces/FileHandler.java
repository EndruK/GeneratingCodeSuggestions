package com.github.endruk.generatingcodesuggestions.interfaces;

import java.io.File;

public interface FileHandler {
	void handle(int level, String path, File file);
}
