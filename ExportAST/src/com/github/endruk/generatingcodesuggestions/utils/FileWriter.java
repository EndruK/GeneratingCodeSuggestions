package com.github.endruk.generatingcodesuggestions.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

public class FileWriter {
	public void writeToFile(String targetFilepath, String outputString) {
		File targetFile = new File(targetFilepath);
		// create the file if it doesn't exist
		if(!targetFile.exists()) {
			try {
				targetFile.createNewFile();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		removeFileIfExist(targetFile);
		// write the String to the target file
		writeStringToFile(outputString, targetFile);
	}
	
	private void createFolderIfNotExist(String dir) {
		File folder = new File(dir);
		if(!folder.exists()) {
			folder.mkdir();
		}
	}
	
	private void removeFileIfExist(File targetFile) {
		if(targetFile.exists()) {
			targetFile.delete();
		}
	}
	
	private void writeStringToFile(String outputString, File outputFile) {
		System.out.println(outputFile.getPath());
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new java.io.FileWriter(outputFile, true));
			bw.write(outputString);
			bw.newLine();
			bw.flush();
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null) try {
				bw.close();
			} catch (IOException ioe2) {}
		}
	}
}
