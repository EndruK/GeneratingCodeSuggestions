package com.github.endruk.generatingcodesuggestions.scanner;

import java.io.File;
import java.util.List;

import com.github.endruk.generatingcodesuggestions.astprinter.ASTPrinter;
import com.github.endruk.generatingcodesuggestions.interfaces.FileNodeHandler;
import com.github.endruk.generatingcodesuggestions.utils.Feature;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

public class CompilationUnitScanner extends ASTNodeScanner {

	public CompilationUnitScanner(ASTPrinter printer) {
		super(printer);
		this.fileNodeHandler = new FileNodeHandler() {
			
			@Override
			public boolean handle(Node node, List<Node> methods, List<Node> variables, File file, File targetDir) {
				if(node instanceof CompilationUnit) {
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
		// TODO Auto-generated method stub
		return null;
	}

}
