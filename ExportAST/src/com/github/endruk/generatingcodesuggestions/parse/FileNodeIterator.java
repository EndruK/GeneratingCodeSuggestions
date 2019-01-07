package com.github.endruk.generatingcodesuggestions.parse;
import java.io.File;

import com.github.endruk.generatingcodesuggestions.interfaces.FileNodeHandler;
import com.github.javaparser.ast.Node;

/**
 * 
 * @author andre
 * Class to handle a node
 * <p>
 * NodeIterator is a class to handle what should happen at a node.
 * It has an interface to handle an incoming node
 * The explore function visits each child node of an incoming node recursively.
 * <p>
 */
public class FileNodeIterator {
	private FileNodeHandler fileNodeHandler;
	private File file;
	private File targetDir;
	
	/**
	 * constructor of NodeIterator
	 * @param nodeHandler
	 * only sets the nodeHandler interface
	 */
	public FileNodeIterator(FileNodeHandler nodeHandler, File file, File targetDir) {
		this.fileNodeHandler = nodeHandler;
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
		if(fileNodeHandler.handle(node, this.file, this.targetDir)) {
			for(Node child : node.getChildNodes()) {
				explore(child);
			}
		}
	}
}
