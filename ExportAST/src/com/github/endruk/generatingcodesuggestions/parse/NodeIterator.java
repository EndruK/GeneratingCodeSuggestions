package com.github.endruk.generatingcodesuggestions.parse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.endruk.generatingcodesuggestions.interfaces.NodeHandler;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

/**
 * 
 * @author andre
 * Class to handle a node
 * <p>
 * NodeIterator is a class to handle what should happen at a node.
 * It has an interface to handle an incoming node
 * Members are:
 * - methods - a list of nodes indicating a method
 * - variables - a list of nodes indicating a variable
 * The explore function visits each child node of an incoming node recursively.
 * <p>
 */
public class NodeIterator {
	private List<Node> methods = new ArrayList<Node>();
	private List<Node> variables = new ArrayList<Node>();
	private NodeHandler nodeHandler;
	private File file;
	private File targetDir;
	
	/**
	 * constructor of NodeIterator
	 * @param nodeHandler
	 * only sets the nodeHandler interface
	 */
	public NodeIterator(NodeHandler nodeHandler, File file, File targetDir) {
		this.nodeHandler = nodeHandler;
		this.file = file;
		this.targetDir = targetDir;
	}
	
	/**
	 * visit all child nodes of a node recursively until false is returned
	 * adds the node to list of methods if it is a method
	 * adds the node to list of variables if it is a variable
	 * @param node
	 */
	public void explore(Node node) {
		if(nodeHandler.handle(node, methods, variables, this.file, this.targetDir)) {
			for(Node child : node.getChildNodes()) {
				explore(child);
			}
		}
		if(node instanceof MethodDeclaration) {
			methods.add(node);
		}
		if(node instanceof VariableDeclarationExpr || node instanceof FieldDeclaration) {
			// check if variable type is part of swing
			// add variable to list
			variables.add(node);
		}
	}
	
	/**
	 * getter for methods
	 * @return List of method nodes
	 */
	public List<Node> getMethods() {
		return methods;
	}
	
	/**
	 * getter for variables
	 * @return List of variable nodes
	 */
	public List<Node> getVariables() {
		return variables;
	}
}
