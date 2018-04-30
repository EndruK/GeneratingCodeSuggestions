package com.github.endruk.generatingcodesuggestions.scanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.endruk.generatingcodesuggestions.astprinter.ASTPrinter;
import com.github.endruk.generatingcodesuggestions.exceptions.NodeNotFoundException;
import com.github.endruk.generatingcodesuggestions.featuremechanics.Feature;
import com.github.endruk.generatingcodesuggestions.featuremechanics.ImportFileHandler;
import com.github.endruk.generatingcodesuggestions.featuremechanics.ImportWildcardHandler;
import com.github.endruk.generatingcodesuggestions.interfaces.FileNodeHandler;
import com.github.endruk.generatingcodesuggestions.interfaces.JavaparserTypeInterface;
import com.github.endruk.generatingcodesuggestions.interfaces.NodeHandler;
import com.github.endruk.generatingcodesuggestions.interfaces.TargetHandler;
import com.github.endruk.generatingcodesuggestions.parse.NodeIterator;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;

public class VariableDeclarationScanner extends ASTNodeScanner {
	
	public VariableDeclarationScanner(ASTPrinter printer,
			String targetScanPackage,
			String packagePosition) {
		super(printer, targetScanPackage, packagePosition);
		this.fileNodeHandler = new FileNodeHandler() {
			
			@Override
			public boolean handle(Node node, List<Node> methods, List<Node> variables, File file, File targetDir) {
				if(node instanceof VariableDeclarationExpr || node instanceof FieldDeclaration) {
					exportNode(node, methods, variables, file, targetDir);
					return false;
				}
				else {
					if(node instanceof IfStmt) return false;
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
//			return findFieldDeclarationFeatures(node);
			return null;
		}
		else {
			throw new NodeNotFoundException("Node was neither VariableDeclarationExpr nor FieldDeclaration!");
		}
	}
	
	private List<Feature> findVariableDeclarationFeatures(VariableDeclarationExpr node) {
		//get parent method and parent class
		Optional<MethodDeclaration> parentMethod = node.getAncestorOfType(MethodDeclaration.class);
		Optional<ClassOrInterfaceDeclaration> parentClassOp = node.getAncestorOfType(ClassOrInterfaceDeclaration.class);
		Optional<CompilationUnit> parentCompUnitOp = node.getAncestorOfType(CompilationUnit.class);
		//lists for different features
		List<Feature> prevVars           = new ArrayList<Feature>();
		List<Feature> methodParams       = new ArrayList<Feature>();
		List<Feature> visibleMethods     = new ArrayList<Feature>();
		List<Feature> visibleFieldDecls  = new ArrayList<Feature>();
		List<Feature> swingImportMethods = new ArrayList<Feature>();
		List<Feature> swingImportFields  = new ArrayList<Feature>();
		
		//###################################################################
		//all variables before the current variable
		if(parentMethod.isPresent()) {
			//do stuff with the parent method
			MethodDeclaration method = parentMethod.get();
			prevVars = getVariablesBeforeInMethod(method, node);
			methodParams = getMethodParameter(method);
		}
		//###################################################################
		// all visible methods and fields
		
		JavaparserTypeInterface methodTypeInterface = new JavaparserTypeInterface() {
			
			@Override
			public boolean isClass(Node node) {
				if(node instanceof MethodDeclaration) return true;
				return false;
			}
			
			@Override
			public Feature getFeature(Node node) {
				Feature f = new Feature();
				f.type = getMethodType((MethodDeclaration) node);
				f.content = getMethodName((MethodDeclaration) node);
				return f;
			}
		};
		JavaparserTypeInterface fieldTypeInterface = new JavaparserTypeInterface() {
			
			@Override
			public boolean isClass(Node node) {
				if(node instanceof FieldDeclaration) return true;
				return false;
			}
			
			@Override
			public Feature getFeature(Node node) {
				Feature f = new Feature();
				f.type = getFieldType((FieldDeclaration) node);
				f.content = getFieldName((FieldDeclaration) node);
				return f;
			}
		};
		if(parentClassOp.isPresent()) {
			//do stuff with the parent class
			ClassOrInterfaceDeclaration parentClass = (ClassOrInterfaceDeclaration) parentClassOp.get();
			visibleMethods = getClassMembers(parentClass, methodTypeInterface);
			visibleFieldDecls = getClassMembers(parentClass, fieldTypeInterface);
		}
		//###################################################################
		//get the imports
		if(parentCompUnitOp.isPresent()) {
			//do stuff with the parent compilation unit
			CompilationUnit parentCompUnit = (CompilationUnit)parentCompUnitOp.get();
			List<Feature> imports = getImportFeatures(parentCompUnit);
		}
		
		
		//###################################################################
		//print the stuff
		if(false) {
			System.out.println("\n###############previous Variables:");
			printFeatureList(prevVars);
			System.out.println("\n###############current Method parameter:");
			printFeatureList(methodParams);
			System.out.println("\n###############visible Methods current Class:");
			printFeatureList(visibleMethods);
			System.out.println("\n###############visible Fields current Class:");
			printFeatureList(visibleFieldDecls);
		}
		//###################################################################
		
		return null;
	}
	
	private List<Feature> getImportFeatures(CompilationUnit unit) {
		List<Feature> result = new ArrayList<Feature>();
		List<ImportDeclaration> packageImports = getPackageImports(unit);
		for(ImportDeclaration imp : packageImports) {
//			System.out.println(imp);
			//dissolve import name
			boolean isASterisk = imp.isAsterisk();
			String importString = imp.getName().asString();
			importString = importString.replace(this.targetScanPackage + ".", "");
			importString = importString.replace(".", "/");
			//if(isASterisk) importString += "/*";
			
			String sourcePath = this.packagePosition + "/" + importString;
			sourcePath += ".java";

			System.out.println(sourcePath);
			
			if(isASterisk) {
				// TODO: handle wildcard
				//get all files in folder recursively
				//build a file crawler
				//iterate over all files
				//build a node crawler
				//extract all methods with tag public
				//extract all fieldDecls with tag public
				ImportWildcardHandler importWildcardHandler = new ImportWildcardHandler(sourcePath);
				importWildcardHandler.extract();
				result = importWildcardHandler.getAllFeatures();
			}
			else {
				NodeHandler methodHandler = new NodeHandler() {
					
					@Override
					public boolean handle(Node node) {
						if(node instanceof MethodDeclaration) { //check if current node is a method
							MethodDeclaration md = (MethodDeclaration) node;
							if(md.isPublic()) { //check if method is public
								return false; // return false to not explore the tree further
							}
						}
						return true; //walk the tree further
					}
				};
				NodeHandler fieldHandler = new NodeHandler() {
					
					@Override
					public boolean handle(Node node) {
						if(node instanceof FieldDeclaration) { //check if current node is field declaration
							FieldDeclaration fd = (FieldDeclaration) node;
							if(fd.isPublic()) { //check if field declaration is public
								return false; // return false to not explore the tree further
							}
						}
						return true; //walk the tree further
					}
				};
				TargetHandler methodTargetHandler = new TargetHandler() { // what to do with the target nodes
					@Override
					public List<Feature> handleTargets(List<Node> targets) {
						List<Feature> result = new ArrayList<Feature>();
						for(Node n : targets) {
							Feature f = new Feature();
							MethodDeclaration m = (MethodDeclaration) n; //target nodes are method declarations
							f.type = m.getType().asString(); // get type
							f.content = m.getName().asString(); // get name
							result.add(f);
						}
						return result;
					}
				};
				TargetHandler fieldTargetHandler = new TargetHandler() { // what to do with the target nodes
					@Override
					public List<Feature> handleTargets(List<Node> targets) {
						List<Feature> result = new ArrayList<Feature>();
						for(Node n : targets) {
							Feature f = new Feature();
							FieldDeclaration fd = (FieldDeclaration) n; //target nodes are field declarations
							//TODO: change this for more than one field declaration in one line
							f.type = fd.getVariable(0).getType().asString(); //get the type of the first variable of the field
							f.content = fd.getVariable(0).getName().asString(); //get the name of the first variable of the field
							result.add(f);
						}
						return result;
					}
				};
				//create a fileHandler for the imported library to scan for public methods and public fields
				ImportFileHandler importFileHandlerMethod = new ImportFileHandler(sourcePath);
				ImportFileHandler importFileHandlerField = new ImportFileHandler(sourcePath);
				importFileHandlerMethod.extract(methodHandler, methodTargetHandler);// extract public methods
				importFileHandlerField.extract(fieldHandler, fieldTargetHandler);// extract public fields
				List<Feature> publicMethods = importFileHandlerMethod.getAllFeatures();//get the result methods
				List<Feature> publicFields = importFileHandlerField.getAllFeatures();//get the result fields
				System.out.println("Methods: ");
				printFeatureList(publicMethods);
				System.out.println("Fields: ");
				printFeatureList(publicFields);
				
			}
		}
		return result;
	}
	
	
	/**
	 * Get all imports for the targeted package for a given compilation unit (uses this.targetScanPackage)
	 * @param unit - the given java file as compilation unit
	 * @return - list of import declarations
	 */
	private List<ImportDeclaration> getPackageImports(CompilationUnit unit) {
		NodeList<ImportDeclaration> allImports = unit.getImports(); //get all imports
		NodeList<ImportDeclaration> packageImports = new NodeList<ImportDeclaration>();
		//iterate over all imports
		for(ImportDeclaration imp : allImports) {
			//if the current import contains the targeted import package -> add it
			if(imp.getName().toString().contains(this.targetScanPackage)) {
				packageImports.add(imp);
			}
		}
		return packageImports;
	}
	
	/**
	 * Get all method parameter for a given method
	 * @param method - the method
	 * @return - list of features
	 */
	private List<Feature> getMethodParameter(MethodDeclaration method) {
		List<Feature> result = new ArrayList<Feature>();
		//get all parameter for the method
		NodeList<Parameter> parameters = method.getParameters();
		for(Parameter parameter : parameters) {
			//create a feature for the parameter
			Feature f = new Feature();
			f.type = parameter.getType().asString();
			f.content = parameter.getName().asString();
			result.add(f);
		}
		return result;
	}
	
	/**
	 * Get all members of a class
	 * it is able to cope with methods and fields
	 * the functionality has to be defined in the JavaparserTypeInterface
	 * this function iterates over all members of a class and adds a feature if JavaparserTypeInterface.isClass returns true
	 * The handling of features are defined in JavaparserTypeInterface.getFeature
	 * @param cl - the class
	 * @param typeInterface - the JavaparserTypeInterface
	 * @return list of features
	 */
	private List<Feature> getClassMembers(ClassOrInterfaceDeclaration cl, 
			JavaparserTypeInterface typeInterface) {
		List<Feature> result = new ArrayList<Feature>(); 
		// get all members of the class
		NodeList<BodyDeclaration<?>> members = cl.getMembers();
		//iterate over all members of the class
		for(BodyDeclaration<?> decl : members) {
			//check if the current node is an instance of the targeted type
			if(typeInterface.isClass((Node)decl)) {
				//get the feature out of the targeted node
				Feature f = typeInterface.getFeature((Node)decl);
				result.add(f);
			}
		}
		return result;
	}
	
	/**
	 * Gets all variables which are defined before a given variable in the same scope
	 * @param method - the method in which the variable is declared
	 * @param origNode - the given target variable
	 * @return - list of features
	 */
	private List<Feature> getVariablesBeforeInMethod(MethodDeclaration method, VariableDeclarationExpr origNode) {
		List<Feature> result = new ArrayList<Feature>();
		// check if the given method has a body
		Optional<BlockStmt> bodyOp = method.getBody();
		if (bodyOp.isPresent()) {
			//cast to block statement
			BlockStmt body = (BlockStmt) bodyOp.get();
			
			//create a node iterator for the block statement
			NodeIterator iterator = new NodeIterator(this.nodeHandler);
			//explore the method until we reach our target variable declaration
			iterator.explore(body, origNode);
			//get all variables declared in front of the target declaration
			List<Node> preVars = iterator.getTargets();
			//get features for the list of variables
			result = getFeatureList(preVars);
		}
		return result;
	}
	
	/**
	 * transform a list of variable declaration expression nodes to a list of features
	 * @param nodes - list of variable declaration nodes
	 * @return - list of features
	 */
	private List<Feature> getFeatureList(List<Node> nodes) {
		List<Feature> result = new ArrayList<Feature>();
		//iterate over all nodes in the given list
		for(Node node : nodes) {
			//cast to variable declaration expression
			VariableDeclarationExpr v = (VariableDeclarationExpr) node;
			//create feature out of node
			Feature f = new Feature();
			f.type = getVariableType(v);
			f.content = getVariableName(v);
			result.add(f);
		}
		return result;
	}
	
	/**
	 * get the type of a variable declaration expression
	 * @param expr - the variable declaration expression
	 * @return - type of the variable declaration expression as string
	 */
	private String getVariableType(VariableDeclarationExpr expr) {
		//TODO: improve this for more than one type
		return expr.getVariable(0).getType().asString();
	}
	
	/**
	 * get the identifier of a variable declaration expression
	 * @param expr - the variable declaration expression
	 * @return - identifier of the variable declaration expression as string
	 */
	private String getVariableName(VariableDeclarationExpr expr) {
		//TODO: improve this for more than one name
		return expr.getVariable(0).getName().asString();
	}
	
	/**
	 * get the type of a method declaration
	 * @param decl - the method declaration
	 * @return - return type of the method declaration as string
	 */
	private String getMethodType(MethodDeclaration decl) {
		return decl.getType().asString();
	}
	
	/**
	 * get the identifier of a method declaration
	 * @param decl - the method declaration
	 * @return - identifier of the method declaration as string
	 */
	private String getMethodName(MethodDeclaration decl) {
		return decl.getNameAsString();
	}
	
	/**
	 * get the type of a field declaration
	 * @param decl - the field declaration
	 * @return - type of the field declaration as string
	 */
	private String getFieldType(FieldDeclaration decl) {
		//TODO: improve this for more than one type
		return decl.getVariable(0).getType().asString();
	}
	
	/**
	 * get the identifier of a field declaration
	 * @param decl - the field declaration
	 * @return - identifier of the field declaration as string
	 */
	private String getFieldName(FieldDeclaration decl) {
		//TODO: improve this for more than one name
		return decl.getVariable(0).getName().asString();
	}
	
	/**
	 * print a given feature list
	 * @param list - the list of features
	 */
	private void printFeatureList(List<Feature> list) {
		for(Feature f : list) {
			System.out.println(f.toString());
		}
	}
}
