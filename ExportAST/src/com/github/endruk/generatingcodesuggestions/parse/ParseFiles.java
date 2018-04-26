package com.github.endruk.generatingcodesuggestions.parse;

import java.io.File;

import com.github.endruk.generatingcodesuggestions.interfaces.FileHandler;
import com.github.endruk.generatingcodesuggestions.interfaces.Filter;

/**
 * ParseFiles is a class to handle folders and files
 * @author andre
 * 
 */
public class ParseFiles {
	private FileHandler fileHandler;
	private Filter filter;
	
	/**
	 * Constructor of ParseFiles
	 * @param filter
	 * @param fileHandler
	 */
	public ParseFiles(Filter filter, FileHandler fileHandler) {
		this.fileHandler = fileHandler;
		this.filter = filter;
	}
	
	/**
	 * Access function to explore files in a directory
	 * @param root
	 */
	public void explore(File root) {
		explore(0, "", root);
	}
	
	/**
	 * move over all files in a directory and do fileHandler if filter is interested
	 * @param level
	 * @param path
	 * @param file
	 */
	private void explore(int level, String path, File file) {
		if(file.isDirectory()) {
			for(File child : file.listFiles()) {
				explore(level+1, path + "/" + child.getName(), child);
			}
		}
		else {
			//if the interface returns true -> do something with the file
			if(filter.interested(level, path, file)) {
				//call the interface method
				fileHandler.handle(level, path, file);
			}
		}
	}
}
