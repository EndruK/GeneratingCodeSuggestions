package com.github.endruk.generatingcodesuggestions.main;

import com.github.endruk.generatingcodesuggestions.astprinter.YAMLPrinter;
import com.github.endruk.generatingcodesuggestions.scanner.CompilationUnitScanner;
import com.github.endruk.generatingcodesuggestions.scanner.MethodDeclarationScanner;
import com.github.endruk.generatingcodesuggestions.scanner.VariableDeclarationScanner;

public class ExportAST {
	private static YAMLPrinter yamlPrinterWithEscape = new YAMLPrinter(true);
	private static YAMLPrinter yamlPrinterWithoutEscape = new YAMLPrinter(false);

	public static void main(String[] args) {
		String corpusPath = "/media/andre/E896A5A496A573AA/Corpora/Java/Swing/Anne_Peter/Swing_Classes_Without_Comments";
//		Setup setupVarEnc = new Setup(
//				corpusPath, 
//				"/media/andre/E896A5A496A573AA/Corpora/AST/Swing/VariableDeclarationsFieldDeclarations_YAML_whitespaceEncoded",
//				new VariableDeclarationScanner(yamlPrinterWithEscape));
//		setupVarEnc.execute(false);
//		Setup setupVarNoEnc = new Setup(
//				corpusPath, 
//				"/media/andre/E896A5A496A573AA/Corpora/AST/Swing/VariableDeclarationsFieldDeclarations_YAML",
//				new VariableDeclarationScanner(yamlPrinterWithoutEscape));
//		setupVarNoEnc.execute(false);
//		Setup setupMEnc = new Setup(
//				corpusPath, 
//				"/media/andre/E896A5A496A573AA/Corpora/AST/Swing/MethodDeclarations_YAML_whitespaceEncoded",
//				new MethodDeclarationScanner(yamlPrinterWithEscape));
//		setupMEnc.execute(false);
//		Setup setupMNoEnc = new Setup(
//				corpusPath, 
//				"/media/andre/E896A5A496A573AA/Corpora/AST/Swing/MethodDeclarations_YAML",
//				new MethodDeclarationScanner(yamlPrinterWithoutEscape));
//		setupMNoEnc.execute(false);
		Setup setupCompUEnc = new Setup(
				corpusPath,
				"/media/andre/E896A5A496A573AA/Corpora/AST/Swing/CompilationUnit_YAML_whitespaceEncoded",
				new CompilationUnitScanner(yamlPrinterWithEscape));
		setupCompUEnc.execute(true);
		
		Setup setupCompUNoEnc = new Setup(
				corpusPath,
				"/media/andre/E896A5A496A573AA/Corpora/AST/Swing/CompilationUnit_YAML",
				new CompilationUnitScanner(yamlPrinterWithoutEscape));
		setupCompUNoEnc.execute(true);
		
		
//		Setup test = new Setup(
//				"/home/andre/Documents/exporterTest/TestClass",
//				"/home/andre/Documents/exporterTest/TestOutput",
//				new VariableDeclarationScanner(yamlPrinterWithEscape));
//		test.execute(false);
	}
}
