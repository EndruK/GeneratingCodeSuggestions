package com.github.endruk.generatingcodesuggestions.featuremechanics;

import com.github.javaparser.ast.Node;

import java.util.List;

public interface FeatureHandler {
	public List<Feature> getFeatures(Node node);
}
