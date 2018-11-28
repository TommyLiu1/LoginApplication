package com.inepro.service.login.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LogoutUserResult {
	@JsonProperty("LogoutUserResult")
	public detailInfor logoutUserResult;
	public class detailInfor{
		@JsonProperty("LogoutSuccess")
		public boolean  logoutSuccess;
		@JsonProperty("Message")
	    public String message;
		@JsonProperty("RequestSuccess")
	    public boolean requestSuccess;
	}
	
	public LogoutUserResult()
	{
		this.logoutUserResult = new detailInfor();
	}
	
}
