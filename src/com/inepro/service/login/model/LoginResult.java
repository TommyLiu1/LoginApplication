package com.inepro.service.login.model;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResult {
	@JsonProperty("LoginResult")
	public detailInfor loginResult;
	
	public class detailInfor
	{
		@JsonProperty("Customer")
		public CustomerInfo customerInfo;
		@JsonProperty("LoginSuccess")
		public boolean loginSuccess;
		@JsonProperty("Message")
		public String message;
		@JsonProperty("PrintJobCount")
		public int printJobCount;
		@JsonProperty("TCostCentres")
		public ArrayList<TCostCentres> tCostCentres;
	}

	public LoginResult(){
		this.loginResult = new detailInfor();
	};

}
