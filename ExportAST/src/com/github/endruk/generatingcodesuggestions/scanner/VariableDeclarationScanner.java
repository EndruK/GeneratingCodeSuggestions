package com.github.endruk.generatingcodesuggestions.scanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.endruk.generatingcodesuggestions.astprinter.ASTPrinter;
import com.github.endruk.generatingcodesuggestions.exceptions.NodeNotFoundException;
import com.github.endruk.generatingcodesuggestions.interfaces.FileNodeHandler;
import com.github.endruk.generatingcodesuggestions.interfaces.NodeHandler;
import com.github.endruk.generatingcodesuggestions.parse.NodeIterator;
import com.github.endruk.generatingcodesuggestions.utils.Feature;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;

public class VariableDeclarationScanner extends ASTNodeScanner {
	
	public VariableDeclarationScanner(ASTPrinter printer) {
		super(printer);
		this.fileNodeHandler = new FileNodeHandler() {
			
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
		this.nodeHandler = new NodeHandler() {
			
			@Override
			public boolean handle(Node node) {
				if(node instanceof VariableDeclarationExpr) {
					return false;
				}
				else {
					return true;
				}
			}
		};
	}
	
	@Override
	protected List<Feature> handleFeatures(Node node) throws NodeNotFoundException {
		
		if(node instanceof VariableDeclarationExpr) {
			// features are prevVars & methods & method params & fields & imports 
			return findVariableDeclarationFeatures((VariableDeclarationExpr)node);
		}
		else if(node instanceof FieldDeclaration) {
			//TODO: implement this
			return findFieldDeclarationFeatures(node);
		}
		else {
			throw new NodeNotFoundException("Node was neither VariableDeclarationExpr nor FieldDeclaration!");
		}
	}
	
	private List<Feature> findVariableDeclarationFeatures(VariableDeclarationExpr node) {
		//prevVars
		Optional<MethodDeclaration> parentMethod = node.getAncestorOfType(MethodDeclaration.class);
		List<Feature> prevVars = new ArrayList<Feature>();
		if(parentMethod.isPresent()) {
			MethodDeclaration method = parentMethod.get();
			System.out.println("parent method: " + method.getName());
			prevVars = getVariablesBeforeInMethod(method, node);
		}
		//printFeatureList(prevVars);
		
		// all visible methods
		Optional<ClassOrInterfaceDeclaration> parentClassOp = node.getAncestorOfType(ClassOrInterfaceDeclaration.class);
		List<Feature> visibleMethods = new ArrayList<Feature>();
		if(parentClassOp.isPresent()) {
			ClassOrInterfaceDeclaration parentClass = (ClassOrInterfaceDeclaration) parentClassOp.get();
			System.out.println("parent class: " + parentClass.getNameAsString());
			visibleMethods = getClassMembers(parentClass, node);
		}
		printFeatureList(visibleMethods);
		// current method parameter
		
		// fields of class
		
		// imports
		
		return null;
	}
	private List<Feature> findFieldDeclarationFeatures(Node node) {
		return null;
	}
	
	private List<Feature> getClassMembers(ClassOrInterfaceDeclaration cl, VariableDeclarationExpr origNode) {
		List<Feature> result = new ArrayList<Feature>(); 
		NodeList<BodyDeclaration<?>> members = cl.getMembers();
		for(BodyDeclaration<?> decl : members) {
			if(decl instanceof MethodDeclaration) {
				//System.out.println(decl);
				Feature f = new Feature();
				f.type = getMethodType((MethodDeclaration) decl);
				f.content = getMethodName((MethodDeclaration) decl);
				result.add(f);
			}
			else if(decl instanceof FieldDeclaration) {
				//System.out.println(decl);
				Feature f = new Feature();
				f.type = getFieldType((FieldDeclaration) decl);
				f.content = getFieldName((FieldDeclaration) decl);
				result.add(f);
			}
		}
		return result;
	}
	
	private List<Feature> getVariablesBeforeInMethod(MethodDeclaration method, VariableDeclarationExpr origNode) {
		List<Feature> result = new ArrayList<Feature>();
		Optional<BlockStmt> bodyOp = method.getBody();
		if (bodyOp.isPresent()) {
			BlockStmt body = (BlockStmt) bodyOp.get();
			
			NodeIterator iterator = new NodeIterator(this.nodeHandler);
			iterator.explore(body, origNode);
			List<Node> preVars = iterator.getTargets();
			result = getFeatureList(preVars);
		}
		return result;
	}
	
	private List<Feature> getFeatureList(List<Node> nodes) {
		List<Feature> result = new ArrayList<Feature>();
		for(Node node : nodes) {
			VariableDeclarationExpr v = (VariableDeclarationExpr) node;
			Feature f = new Feature();
			f.type = getVariableType(v);
			f.content = getVariableName(v);
			result.add(f);
		}
		return result;
	}
	
	private String getVariableType(VariableDeclarationExpr expr) {
		//TODO: improve this for more than one type
		return expr.getVariable(0).getType().asString();
	}
	private String getVariableName(VariableDeclarationExpr expr) {
		//TODO: improve this for more than one name
		return expr.getVariable(0).getName().asString();
	}
	private String getMethodType(MethodDeclaration decl) {
		return decl.getType().asString();
	}
	private String getMethodName(MethodDeclaration decl) {
		return decl.getNameAsString();
	}
	private String getFieldType(FieldDeclaration decl) {
		//TODO: improve this for more than one type
		return decl.getVariable(0).getType().asString();
	}
	private String getFieldName(FieldDeclaration decl) {
		//TODO: improve this for more than one name
		return decl.getVariable(0).getName().asString();
	}
	private void printFeatureList(List<Feature> list) {
		for(Feature f : list) {
			System.out.println(f.toString());
		}
	}
}
