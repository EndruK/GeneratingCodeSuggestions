package com.github.endruk.generatingcodesuggestions.astprinter;

import java.util.List;

import com.github.endruk.generatingcodesuggestions.featuremechanics.Feature;
import com.github.endruk.generatingcodesuggestions.utils.FileWriter;
import com.github.javaparser.ast.Node;

public abstract class ASTPrinter {
	public abstract void print(Node node, String targetFilepath);
	public void printFeatures(List<Feature> features, String targetFilePath) {
		String outputString = "";
		for(Feature f : features) {
			outputString += f.toString() + "\n";
		}
		FileWriter w = new FileWriter();
		w.writeToFile(targetFilePath, outputString);
	}
}
