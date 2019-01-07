package com.github.endruk.generatingcodesuggestions.main;

import com.github.endruk.generatingcodesuggestions.astprinter.YAMLPrinter;
import com.github.endruk.generatingcodesuggestions.scanner.CompilationUnitScanner;
import com.github.endruk.generatingcodesuggestions.scanner.MethodDeclarationScanner;
import com.github.endruk.generatingcodesuggestions.scanner.VariableDeclarationScanner;

public class ExportAST {
	private static YAMLPrinter yamlPrinterWithEscape = new YAMLPrinter(true);
	private static YAMLPrinter yamlPrinterWithoutEscape = new YAMLPrinter(false);
	
	private static final String targetScanPackage = "javax.swing";
	private static final String packagePosition = "/home/andre/Documents/SWING_SRC";

	public static void main(String[] args) {
		String corpusPath = "/media/andre/E896A5A496A573AA/Corpora/AndreKarge_2018-04-25_Java_Swing_Code+AST/Java/Swing/Anne_Peter/Swing_Classes_Without_Comments";
//		Setup setupVarEnc = new Setup(
//				corpusPath, 
//				"/media/andre/E896A5A496A573AA/Corpora/AST/Swing/VariableDeclarations_YAML_whitespaceEncoded",
//				new VariableDeclarationScanner(yamlPrinterWithEscape, targetScanPackage, packagePosition));
//		setupVarEnc.execute(true);
//		Setup setupVarNoEnc = new Setup(
//				corpusPath, 
//				"/media/andre/E896A5A496A573AA/Corpora/AST/Swing/VariableDeclarations_YAML",
//				new VariableDeclarationScanner(yamlPrinterWithoutEscape, targetScanPackage, packagePosition));
//		setupVarNoEnc.execute(true);
		Setup setupMEnc = new Setup(
				corpusPath, 
				"/media/andre/E896A5A496A573AA/Corpora/AndreKarge_2018-04-25_Java_Swing_Code+AST/AST/Swing/MethodDeclarations_YAML_whitespaceEncoded",
				new MethodDeclarationScanner(yamlPrinterWithEscape, targetScanPackage, packagePosition));
		setupMEnc.execute(true);
		Setup setupMNoEnc = new Setup(
				corpusPath, 
				"/media/andre/E896A5A496A573AA/Corpora/AndreKarge_2018-04-25_Java_Swing_Code+AST/AST/Swing/MethodDeclarations_YAML",
				new MethodDeclarationScanner(yamlPrinterWithoutEscape, targetScanPackage, packagePosition));
		setupMNoEnc.execute(true);
//		Setup setupCompUEnc = new Setup(
//				corpusPath,
//				"/media/andre/E896A5A496A573AA/Corpora/AST/Swing/CompilationUnit_YAML_whitespaceEncoded",
//				new CompilationUnitScanner(yamlPrinterWithEscape, targetScanPackage, packagePosition));
//		setupCompUEnc.execute(true);
//		
//		Setup setupCompUNoEnc = new Setup(
//				corpusPath,
//				"/media/andre/E896A5A496A573AA/Corpora/AST/Swing/CompilationUnit_YAML",
//				new CompilationUnitScanner(yamlPrinterWithoutEscape, targetScanPackage, packagePosition));
//		setupCompUNoEnc.execute(true);
		
		
//		Setup test = new Setup(
//				"/home/andre/Documents/exporterTest/TestClass",
//				"/home/andre/Documents/exporterTest/TestOutput",
//				new VariableDeclarationScanner(yamlPrinterWithEscape, targetScanPackage, packagePosition));
//		test.execute(false);
	}
}
