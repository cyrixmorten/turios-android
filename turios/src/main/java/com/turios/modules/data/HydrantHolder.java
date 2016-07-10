package com.turios.modules.data;
import java.io.Serializable;


public class HydrantHolder implements Serializable {

	private static final long serialVersionUID = 2756516800590696046L;
	
	private String iconPath;
	private String hydrantType;
	private double latitude;
	private double longitude;
	private String address;
	private String addressNumber;
	private String addressRemark;
	private String remark;
	
	public HydrantHolder(String iconPah, String hydrantType, double latitude, double longitude, String address, String addressNumber,
			String addressRemark, String remark) {
		super();
		this.iconPath = iconPah;
		this.hydrantType = hydrantType;
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
		this.addressNumber = addressNumber;
		this.addressRemark = addressRemark;
		this.remark = remark;
	}	

	@Override
	public String toString() {
		return "Hydrant [iconPath=" + iconPath + ", hydrantType=" + hydrantType
				+ ", latitude=" + latitude + ", longitude=" + longitude
				+ ", address=" + address + ", addressNumber=" + addressNumber
				+ ", addressRemark=" + addressRemark + ", remark=" + remark
				+ "]";
	}

	public String getIconPath() {
		return iconPath;
	}

	public String getAddress() {
		return address;
	}


	public String getAddressNumber() {
		return addressNumber;
	}


	public String getHydrantType() {
		return hydrantType;
	}
 
	public double getLatitude() {
		return latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public String getAddressRemark() {
		return addressRemark;
	}
	public String getRemark() {
		return remark;
	}
	
	public static class Builder {

		private String errors;

		private String iconPath;
		private String hydrantType;
		private double latitude;
		private double longitude;
		private String addressRemark;
		private String address;
		private String addressNumber;
		private String remark;

		public Builder() {
			iconPath = "";
			hydrantType = "";
			latitude = 0;
			longitude = 0;
			address = "";
			addressNumber = "";
			addressRemark = "";
			remark = "";
			errors = "";
		}

		public HydrantHolder build() {
			return new HydrantHolder(iconPath, hydrantType, latitude, longitude, address,
					addressNumber, addressRemark, remark);
		}

		public Builder setIconPath(String iconPath) {
			this.iconPath = iconPath;
			return this;
		}

		public Builder setLatitude(double latitude) {
			this.latitude = latitude;
			return this;
		}

		public Builder setLongitude(double longitude) {
			this.longitude = longitude;
			return this;
		}

		public Builder setAddress(String address) {
			this.address = address;
			return this;
		}

		public Builder setAddressNumber(String addressNumber) {
			this.addressNumber = addressNumber;
			return this;
		}

		public Builder setHydrantType(String hydrantType) {
			this.hydrantType = hydrantType;
			return this;
		}

		public Builder setLatitude(String latitude, int decimalpos) {
			try {
				if (latitude.length() > 4) {
					if (decimalpos > 0) {
						latitude = latitude.replace(".", "");
						latitude = latitude.substring(0, decimalpos) + "."
								+ latitude.substring(decimalpos);
					}
					this.latitude = Double.parseDouble(latitude);
				} else {
					errors = errors + " latitude too short";
				}
			} catch (NumberFormatException e) {
				errors = errors + " latitude not valid double " + latitude;
			}
			return this;
		}

		public Builder setLongitude(String longitude, int decimalpos) {
			try {
				if (longitude.length() > 4) {
					if (decimalpos > 0) {
						longitude = longitude.replace(".", "");
						longitude = longitude.substring(0, decimalpos) + "."
								+ longitude.substring(decimalpos);
					}
					this.longitude = Double.parseDouble(longitude);
				} else {
					errors = errors + " longitude too short ";
				}
			} catch (NumberFormatException e) {
				errors = errors + " longitude not valid double " + longitude;
			}
			return this;
		}

		public String getErrors() {
			return errors;
		}

		public Builder setAddressRemark(String addressRemark) {
			this.addressRemark = addressRemark;
			return this;
		}

		public Builder setRemark(String remark) {
			this.remark = remark;
			return this;
		}

	}
	
}
