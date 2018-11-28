package com.inepro.service.login.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ScreenSettingsRequestModel {
	
	@JsonProperty("ScreenSettingsRequestModel")
	public ScreenInfor screenSettingsRequestModel;
	
	public class ScreenInfor
	{
		@JsonProperty("SerialNumber")
		public String serialNumber;
		@JsonProperty("DeviceType")
		public int deviceType;
		public ScreenInfor(String serialNumber)
		{
			this.serialNumber = serialNumber;
			this.deviceType = 20;
		}
	}
	
	public ScreenSettingsRequestModel(String serialNumber){
		this.screenSettingsRequestModel = new ScreenInfor(serialNumber);
		
	}

}
