package com.bericotech.clavin.rest.core;

import java.util.ArrayList;
import java.util.List;

import com.bericotech.clavin.resolver.ResolvedLocation;

public class ResolvedLocationsMinimum {
 
	private List<ResolvedLocationMinimum> resolvedLocationsMinimum = new ArrayList<ResolvedLocationMinimum>();

	public ResolvedLocationsMinimum(
			List<ResolvedLocation> resolvedLocations 
			) {
		
		for (ResolvedLocation rl : resolvedLocations ) {
			resolvedLocationsMinimum.add(new ResolvedLocationMinimum(rl));
		}
	}

	
	public List<ResolvedLocationMinimum> getResolvedLocationsMinimum() {
		return resolvedLocationsMinimum;
	}
}