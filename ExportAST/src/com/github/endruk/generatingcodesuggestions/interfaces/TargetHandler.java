package com.github.endruk.generatingcodesuggestions.interfaces;

import java.util.List;

import com.github.endruk.generatingcodesuggestions.featuremechanics.Feature;
import com.github.javaparser.ast.Node;

public interface TargetHandler {
	public List<Feature> handleTargets(List<Node> targets);
}
