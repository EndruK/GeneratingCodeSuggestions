package com.github.endruk.generatingcodesuggestions.scanner;

import java.io.File;
import java.util.List;

import com.github.endruk.generatingcodesuggestions.astprinter.ASTPrinter;
import com.github.endruk.generatingcodesuggestions.interfaces.FileNodeHandler;
import com.github.endruk.generatingcodesuggestions.utils.Feature;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;

public class MethodDeclarationScanner extends ASTNodeScanner{
	public MethodDeclarationScanner(ASTPrinter printer) {
		super(printer);
		this.fileNodeHandler = new FileNodeHandler() {
			
			@Override
			public boolean handle(Node node, List<Node> methods, List<Node> variables, File file, File targetDir) {
				if(node instanceof MethodDeclaration) {
					exportNode(node, methods, variables, file, targetDir);
					return false;
				}
				else {
					return true;
				}
			}
		};
	}
	
	@Override
	protected List<Feature> handleFeatures(Node node) {
		//TODO: implement this
		return null;
	}
}
