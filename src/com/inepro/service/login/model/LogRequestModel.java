package com.inepro.service.login.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LogRequestModel {
	@JsonProperty("LogRequestModel")
	public LogContent logContent;
	
	
	public class LogContent
	{
		@JsonProperty("Content")
		public String content;
		
		public LogContent(String content)
		{
			this.content = content;
		}
	}
	
	public LogRequestModel(String content)
	{
		logContent = new LogContent(content);
	}

}
