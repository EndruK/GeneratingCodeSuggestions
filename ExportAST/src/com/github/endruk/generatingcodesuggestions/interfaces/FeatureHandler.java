package com.github.endruk.generatingcodesuggestions.interfaces;

import com.github.javaparser.ast.Node;

import java.util.List;

import com.github.endruk.generatingcodesuggestions.utils.Feature;

public interface FeatureHandler {
	public List<Feature> getFeatures(Node node);
}
