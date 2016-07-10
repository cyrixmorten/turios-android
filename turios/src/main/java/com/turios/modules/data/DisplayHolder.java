package com.turios.modules.data;

import java.util.List;


public class DisplayHolder implements Comparable<DisplayHolder> {

	private long id;
	private long time_received;
	private long time_delete;
	private List<HydrantHolder> nearbyHydrants; 
	private AddressHolder addressHolder;
	private String picklist;
	private String accessplan;
	
	
	
	public DisplayHolder(long id, long time_received, long time_delete,
			List<HydrantHolder> nearbyHydrants, AddressHolder addressHolder,
			String picklist, String accessplan) {
		super();
		this.id = id;
		this.time_received = time_received;
		this.time_delete = time_delete;
		this.nearbyHydrants = nearbyHydrants;
		this.addressHolder = addressHolder;
		this.picklist = picklist;
		this.accessplan = accessplan;
	}

	


	public long getId() {
		return id;
	}



	public long getTime_received() {
		return time_received;
	}




	public long getTime_delete() {
		return time_delete;
	}




	public List<HydrantHolder> getNearbyHydrants() {
		return nearbyHydrants;
	}




	public AddressHolder getAddressHolder() {
		return addressHolder;
	}




	public String getPicklist() {
		return picklist;
	}




	public String getAccessplan() {
		return accessplan;
	}




	@Override
	public int compareTo(DisplayHolder another) {
		if (time_received > another.time_received) {
			return 1;
		} else {
			return -1;
		}
	}
	

}
