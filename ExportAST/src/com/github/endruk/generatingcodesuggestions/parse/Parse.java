package com.github.endruk.generatingcodesuggestions.parse;

import java.io.File;

import com.github.endruk.generatingcodesuggestions.interfaces.FileHandler;
import com.github.endruk.generatingcodesuggestions.interfaces.Filter;
import com.github.endruk.generatingcodesuggestions.scanner.ASTNodeScanner;
import com.github.javaparser.JavaParser;


public class Parse{
	
	Filter filter;
	FileHandler fileHandler;
	File targetDir;
	ASTNodeScanner scanner;
	boolean verbose;
	
	public Parse(ASTNodeScanner nodeScanner, boolean verbose) {
		// filter -> .java files
		filter = new Filter() {
			
			@Override
			public boolean interested(int level, String path, File file) {
				return path.endsWith(".java");
			}
		};
		// fileHandler -> what to do with a file
		fileHandler = new FileHandler() {
			
			@Override
			public void handle(int level, String path, File file) {
				handleJavaFile(path, file);
			}
		};
		this.scanner = nodeScanner;
		this.verbose = verbose;
	}
	
	public void walkTree(File dir, File outputRootDir) {
		this.targetDir = outputRootDir;
		new ParseFiles(filter, fileHandler).explore(dir);
		System.out.println("done walking the directory");
	}
	private void handleJavaFile(String path, File file) {
		if(verbose) {
			System.out.println("handling file " + file);
		}
		this.scanner.resetFileCount();
		try {
			new NodeIterator(this.scanner.getNodeHandler(), file, targetDir).explore(JavaParser.parse(file));
		} catch(Exception e) {
			new RuntimeException(e);
			e.printStackTrace();
		}
	}
}
