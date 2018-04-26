package com.github.endruk.generatingcodesuggestions.interfaces;

import com.github.javaparser.ast.Node;

public interface NodeHandler {
	boolean handle(Node node);
}
