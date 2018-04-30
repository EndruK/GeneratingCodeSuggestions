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
			visibleMethods = getClassMembers(parentClass, node, methodTypeInterface);
			visibleFieldDecls = getClassMembers(parentClass, node, fieldTypeInterface);
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
	
	private List<ImportDeclaration> getPackageImports(CompilationUnit unit) {
		NodeList<ImportDeclaration> allImports = unit.getImports();
		NodeList<ImportDeclaration> packageImports = new NodeList<ImportDeclaration>();
		for(ImportDeclaration imp : allImports) {
			if(imp.getName().toString().contains(this.targetScanPackage)) {
				packageImports.add(imp);
//				if(imp.isAsterisk()) System.out.println(imp.getName() + ".*");
//				else System.out.println(imp.getName());
			}
		}
		return packageImports;
	}
	
	private List<Feature> getMethodParameter(MethodDeclaration method) {
		List<Feature> result = new ArrayList<Feature>();
		NodeList<Parameter> parameters = method.getParameters();
		for(Parameter parameter : parameters) {
			Feature f = new Feature();
			f.type = parameter.getType().asString();
			f.content = parameter.getName().asString();
			result.add(f);
		}
		return result;
	}
	
	private List<Feature> getClassMembers(ClassOrInterfaceDeclaration cl, 
			VariableDeclarationExpr origNode, 
			JavaparserTypeInterface typeInterface) {
		List<Feature> result = new ArrayList<Feature>(); 
		NodeList<BodyDeclaration<?>> members = cl.getMembers();
		for(BodyDeclaration<?> decl : members) {
			if(typeInterface.isClass((Node)decl)) {
				Feature f = typeInterface.getFeature((Node)decl);
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
