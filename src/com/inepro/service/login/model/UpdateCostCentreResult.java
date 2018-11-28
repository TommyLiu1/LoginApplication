package com.inepro.service.login.model;

import com.fasterxml.jackson.annotation.JsonProperty;
public class UpdateCostCentreResult {
	
	@JsonProperty("UpdateCostCentreResult")
	public detailInfor updateCostCentreResult;
	
	public class detailInfor{
		@JsonProperty("UpdateSuccess")
		public boolean  updateSuccess;
		@JsonProperty("Message")
	    public String message;
		@JsonProperty("RequestSuccess")
	    public boolean requestSuccess;
	}
	
	public UpdateCostCentreResult()
	{
		this.updateCostCentreResult = new detailInfor();
	}
	
}
