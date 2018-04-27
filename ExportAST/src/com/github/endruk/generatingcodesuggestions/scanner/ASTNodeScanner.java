package com.github.endruk.generatingcodesuggestions.scanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.endruk.generatingcodesuggestions.astprinter.ASTPrinter;
import com.github.endruk.generatingcodesuggestions.exceptions.NodeNotFoundException;
import com.github.endruk.generatingcodesuggestions.featuremechanics.Feature;
import com.github.endruk.generatingcodesuggestions.featuremechanics.FeatureHandler;
import com.github.endruk.generatingcodesuggestions.interfaces.FileNodeHandler;
import com.github.endruk.generatingcodesuggestions.interfaces.NodeHandler;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

public abstract class ASTNodeScanner {
	protected FileNodeHandler fileNodeHandler;
	protected NodeHandler nodeHandler;
	protected String targetScanPackage;
	protected String packagePosition;
	private ASTPrinter printer;
	protected FeatureHandler featureHandler;
	int fileNodeCount = 0;
	
	public ASTNodeScanner(ASTPrinter printer,
			String targetScanPackage,
			String packagePosition) {
		this.printer = printer;
		this.targetScanPackage = targetScanPackage;
		this.packagePosition = packagePosition;
		this.featureHandler = new FeatureHandler() {
			
			@Override
			public List<Feature> getFeatures(Node node) {
				try {
					return handleFeatures(node);
				} catch(NodeNotFoundException e) {
					e.printStackTrace();
				}
				return new ArrayList<Feature>();
			}
		};
	}
	
	public FileNodeHandler getNodeHandler() {
		return fileNodeHandler;
	}
	public void resetFileCount() {
		this.fileNodeCount = 0;
	}
	
	protected void exportNode(Node node, List<Node> methods, List<Node> variables, File file, File targetDir) {
		this.fileNodeCount += 1;
		String className = file.getName();
		String targetFilepath = targetDir + "/" + className + "." + String.valueOf(this.fileNodeCount);
		printNodeToFile(node, targetFilepath);
		if (node instanceof VariableDeclarationExpr) {
			VariableDeclarationExpr e = (VariableDeclarationExpr) node;
			System.out.println("current node: " + e.getVariable(0).getType() + " " + e.getVariable(0).getNameAsString());
		}
		// get features for node
		List<Feature> features = featureHandler.getFeatures(node);
		System.out.println();
	}
	
	private void printNodeToFile(Node node, String targetFilepath) {
		this.printer.print(node, targetFilepath);
	}
	
	protected abstract List<Feature> handleFeatures(Node node) throws NodeNotFoundException;

}
