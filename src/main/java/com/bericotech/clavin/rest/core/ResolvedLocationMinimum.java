package com.bericotech.clavin.rest.core;

import com.bericotech.clavin.resolver.ResolvedLocation;

public class ResolvedLocationMinimum {
	
	public final int geonameID;
	public final String name;
	public final String countryCode;
	public final double latitude;
	public final double longitude;
	
	ResolvedLocationMinimum(ResolvedLocation rl) {
		this.geonameID = rl.getGeoname().getGeonameID();
		this.name = rl.getGeoname().getName();
		this.countryCode = rl.getGeoname().getPrimaryCountryCode().toString();
		this.latitude = rl.getGeoname().getLatitude();
		this.longitude= rl.getGeoname().getLongitude();
	}

}
