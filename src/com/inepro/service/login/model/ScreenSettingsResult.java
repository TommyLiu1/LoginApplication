package com.inepro.service.login.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ScreenSettingsResult {
	@JsonProperty("GetScreenSettingsResult")
	public ResultDetail screenSettingsResult;
	
	public class ResultDetail{
		@JsonProperty("BackgroundIdleImageURL")
		public String backGroundIdleImageURL;
		@JsonProperty("BackgroundHomeImageURL")
		public String backgroundHomeImageURL;
		@JsonProperty("Message")
		public String message;
		@JsonProperty("RequestSuccess")
		public boolean requestSuccess;
		@JsonProperty("WelcomeText")
		public String welcomeText;
	}
	
	public ScreenSettingsResult()
	{
		this.screenSettingsResult = new ResultDetail();
	}
	
	public String getbackGroundIdleImageURL()
	{
		return this.screenSettingsResult.backGroundIdleImageURL;
	}
	
	public String getWelComeText()
	{
		return this.screenSettingsResult.welcomeText;
	}
	
	public String getBackgroundHomeImageURL()
	{
		return this.screenSettingsResult.backgroundHomeImageURL;
	}

}
