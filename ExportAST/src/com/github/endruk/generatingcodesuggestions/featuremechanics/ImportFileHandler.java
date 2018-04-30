package com.github.endruk.generatingcodesuggestions.featuremechanics;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.github.endruk.generatingcodesuggestions.interfaces.NodeHandler;
import com.github.endruk.generatingcodesuggestions.interfaces.TargetHandler;
import com.github.endruk.generatingcodesuggestions.parse.NodeIterator;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;

public class ImportFileHandler {
	private List<Feature> featureList;
	
	private File targetFile;
	
	public ImportFileHandler(String path) {
		this.featureList = new ArrayList<Feature>();
		this.targetFile = new File(path);
	}
	
	public void extract(NodeHandler nodeHandler, TargetHandler targetHandler) {
		try {
			Node n = JavaParser.parse(this.targetFile);
			NodeIterator iterator = new NodeIterator(nodeHandler);
			iterator.explore(n, null);
			List<Node> targets = iterator.getTargets();
			this.featureList = targetHandler.handleTargets(targets);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public List<Feature> getAllFeatures() {
		return this.featureList;
	}
}
