package com.github.endruk.generatingcodesuggestions.scanner;

import java.io.File;
import java.util.List;
import java.util.Optional;

import com.github.endruk.generatingcodesuggestions.astprinter.ASTPrinter;
import com.github.endruk.generatingcodesuggestions.interfaces.NodeHandler;
import com.github.endruk.generatingcodesuggestions.utils.Feature;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

public class VariableDeclarationScanner extends ASTNodeScanner {
	
	public VariableDeclarationScanner(ASTPrinter printer) {
		super(printer);
		this.nodeHandler = new NodeHandler() {
			
			@Override
			public boolean handle(Node node, List<Node> methods, List<Node> variables, File file, File targetDir) {
				if(node instanceof VariableDeclarationExpr || node instanceof FieldDeclaration) {
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
		//System.out.println(node.getParentNode() + "\n\n");
		Optional<Node> parentNodeOption = node.getParentNode();
		System.out.println("node type: " + node.getMetaModel().getTypeName());
		if(parentNodeOption.isPresent()) {
			Node parentNode = parentNodeOption.get();
			System.out.println("parent type: " + parentNode.getMetaModel().getTypeName());
		}
		return null;
	}
}
