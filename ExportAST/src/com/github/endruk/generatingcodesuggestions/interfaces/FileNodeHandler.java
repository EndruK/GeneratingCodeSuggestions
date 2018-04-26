package com.github.endruk.generatingcodesuggestions.interfaces;

import java.io.File;
import java.util.List;

import com.github.javaparser.ast.Node;

public interface FileNodeHandler {
	boolean handle(Node node, List<Node> methods, List<Node> variables, File file, File targetDir);
}
