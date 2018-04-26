package com.github.endruk.generatingcodesuggestions.parse;

import java.util.ArrayList;
import java.util.List;

import com.github.endruk.generatingcodesuggestions.interfaces.NodeHandler;
import com.github.javaparser.ast.Node;

public class NodeIterator {
	private NodeHandler nodeHandler;
	private List<Node> targets;
	private boolean stopCriteria = false;
	/**
	 * constructor of NodeIterator
	 * @param nodeHandler
	 * only sets the nodeHandler interface
	 */
	public NodeIterator(NodeHandler nodeHandler) {
		this.nodeHandler = nodeHandler;
		this.targets = new ArrayList<Node>();
	}
	
	public List<Node> getTargets() {
		return this.targets;
	}
	
	/**
	 * visit all child nodes of a node recursively until false is returned
	 * @param node
	 */
	public void explore(Node node, Node stopNode) {
		//TODO: handle local scopes  --> remember the path to method and don't unpack blocks
		if(nodeHandler.handle(node)) {
			for(Node child : node.getChildNodes()) {
				if(child != stopNode && !stopCriteria) {
					explore(child, stopNode);
				}
				else {
					stopCriteria = true;
					break;
				}
			}
		}
		else {
			this.targets.add(node);
		}
	}
}
