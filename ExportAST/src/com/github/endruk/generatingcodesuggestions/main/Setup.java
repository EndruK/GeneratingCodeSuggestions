package com.github.endruk.generatingcodesuggestions.main;

import java.io.File;

import com.github.endruk.generatingcodesuggestions.parse.Parse;
import com.github.endruk.generatingcodesuggestions.scanner.ASTNodeScanner;

public class Setup {
	String corpusPath;
	String targetPath;
	ASTNodeScanner scanner;
	
	public Setup(String corpusPath, String targetPath, ASTNodeScanner scanner) {
		this.corpusPath = corpusPath;
		this.targetPath = targetPath;
		this.scanner = scanner;
		checkTargetDir();
	}
	public void execute(boolean verbose) {
		Parse parse = new Parse(this.scanner, verbose);
		parse.walkTree(new File(this.corpusPath), new File(this.targetPath));
		System.out.println("done executing setup");
	}
	private void checkTargetDir() {
		File f = new File(this.targetPath);
		if(!f.isDirectory()) {
			f.mkdirs();
		}
	}
}
