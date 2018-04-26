package com.github.endruk.generatingcodesuggestions.scanner;

import java.io.File;
import java.util.List;

import com.github.endruk.generatingcodesuggestions.astprinter.ASTPrinter;
import com.github.endruk.generatingcodesuggestions.interfaces.FeatureHandler;
import com.github.endruk.generatingcodesuggestions.interfaces.NodeHandler;
import com.github.endruk.generatingcodesuggestions.utils.Feature;
import com.github.javaparser.ast.Node;

public abstract class ASTNodeScanner {
	protected NodeHandler nodeHandler;
	private ASTPrinter printer;
	protected FeatureHandler featureHandler;
	int fileNodeCount = 0;
	
	public ASTNodeScanner(ASTPrinter printer) {
		this.printer = printer;
		this.featureHandler = new FeatureHandler() {
			
			@Override
			public List<Feature> getFeatures(Node node) {
				return handleFeatures(node);
			}
		};
	}
	
	public NodeHandler getNodeHandler() {
		return nodeHandler;
	}
	public void resetFileCount() {
		this.fileNodeCount = 0;
	}
	
	protected void exportNode(Node node, List<Node> methods, List<Node> variables, File file, File targetDir) {
		this.fileNodeCount += 1;
		String className = file.getName();
		String targetFilepath = targetDir + "/" + className + "." + String.valueOf(this.fileNodeCount);
		printNodeToFile(node, targetFilepath);
		
		// get features for node
		List<Feature> features = featureHandler.getFeatures(node);
	}
	
	private void printNodeToFile(Node node, String targetFilepath) {
		this.printer.print(node, targetFilepath);
	}
	
	protected abstract List<Feature> handleFeatures(Node node);

}
