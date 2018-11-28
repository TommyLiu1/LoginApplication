package com.inepro.service.login.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LogoutRequestModel {
	@JsonProperty("LogoutRequestModel")
	public detailInfor logoutRequestModel;
	public class detailInfor{
		@JsonProperty("SerialNumber")
		public String serialNumber;

		@JsonProperty("Username")
		public String username;
		
		public detailInfor(String serialNumber,String username)
		{
			this.serialNumber = serialNumber;
			this.username = username;
		}
	}
	public LogoutRequestModel(){}
	
	public LogoutRequestModel(String serialNumber,String username){
		this.logoutRequestModel = new detailInfor(serialNumber,username);
	}

}
