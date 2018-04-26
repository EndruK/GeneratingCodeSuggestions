package com.github.endruk.generatingcodesuggestions.astprinter;

import com.github.javaparser.ast.Node;

public abstract class ASTPrinter {
	public abstract void print(Node node, String targetFilepath);
}
