package com.bericotech.clavin.rest.core;

import java.util.ArrayList;
import java.util.List;

import com.bericotech.clavin.resolver.ResolvedLocation;

public class ResolvedLocations {
 
	private List<ResolvedLocation> resolvedLocations = new ArrayList<ResolvedLocation>();

	public ResolvedLocations(
			List<ResolvedLocation> resolvedLocations 
			) {
		
		this.resolvedLocations = resolvedLocations;
	
	}

	
	public List<ResolvedLocation> getResolvedLocations() {
		return resolvedLocations;
	}
}