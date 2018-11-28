package com.inepro.service.login.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateCostCentreRequestModel {
	@JsonProperty("UpdateCostCentreRequestModel")
	public detailInfor updateCostCentreRequestModel;
	
	public class detailInfor{
		@JsonProperty("SerialNumber")
		public String serialNumber;

		@JsonProperty("Username")
		public String username;
		
		@JsonProperty("CostcentreID")
		public int costCentreID;
		
		public detailInfor(String serialNumber,String username,int costCentreID)
		{
			this.serialNumber = serialNumber;
			this.username = username;
			this.costCentreID = costCentreID;
		}
	}
	
	
	public UpdateCostCentreRequestModel(){}
	
	public UpdateCostCentreRequestModel(String serialNumber,String username, int costCentreID){
		this.updateCostCentreRequestModel = new detailInfor(serialNumber, username, costCentreID);
	}

}
