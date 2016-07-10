package com.turios.modules.data;

import com.google.android.gms.maps.model.LatLng;

public class AddressHolder {
	public final String address;
	public final String city;
	public final LatLng position;
	
	private boolean hydrantsNearby;


	public AddressHolder(String address, String city, LatLng position) {
		super();
		this.address = address;
		this.city = city;
		this.position = position;
	}

	public boolean isHydrantsNearby() {
		return hydrantsNearby;
	}

	public void setHydrantsNearby(boolean hydrantsNearby) {
		this.hydrantsNearby = hydrantsNearby;
	}
	
	@Override
	public String toString() {
		return "AddressHolder [address=" + address + ", city=" + city
				+ ", position=" + position + "]";
	}
}
