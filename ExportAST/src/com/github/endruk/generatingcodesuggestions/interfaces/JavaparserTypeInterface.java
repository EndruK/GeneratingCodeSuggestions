package com.github.endruk.generatingcodesuggestions.interfaces;

import com.github.endruk.generatingcodesuggestions.featuremechanics.Feature;
import com.github.javaparser.ast.Node;

public interface JavaparserTypeInterface {
	boolean isClass(Node node);
	Feature getFeature(Node node);
}
