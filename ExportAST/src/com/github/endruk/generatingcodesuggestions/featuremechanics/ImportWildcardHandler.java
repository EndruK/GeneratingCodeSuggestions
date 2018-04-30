package com.github.endruk.generatingcodesuggestions.featuremechanics;

import java.util.ArrayList;
import java.util.List;

public class ImportWildcardHandler {
	private List<Feature> featureList;
	
	public ImportWildcardHandler(String path) {
		this.featureList = new ArrayList<Feature>();
	}
	
	public void extract() {
		
	}
	
	public List<Feature> getAllFeatures() {
		return this.featureList;
	}
}
