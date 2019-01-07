package com.github.endruk.generatingcodesuggestions.interfaces;

import java.io.File;

import com.github.javaparser.ast.Node;

public interface FileNodeHandler {
	boolean handle(Node node, File file, File targetDir);
}
