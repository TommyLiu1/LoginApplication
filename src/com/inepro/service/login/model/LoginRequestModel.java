package com.inepro.service.login.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequestModel {
	@JsonProperty("LoginModel")
	public LoginInfo loginInfo;
	
	public class LoginInfo {
		@JsonProperty("SerialNumber")
		public String SerialNumber;

		@JsonProperty("Username")
		public String Username;

		@JsonProperty("Password")
		public String Password;

		@JsonProperty("LoginMethod")
		public String LoginMethod;

		@JsonProperty("RFIDCardNumber")
		public String RFIDCardNumber;

		@JsonProperty("NoLock")
		public boolean NoLock;

		public LoginInfo() {
			this.LoginMethod = "5";
			this.RFIDCardNumber = "";
			this.NoLock = false;
		}

		public LoginInfo(String username, String password, String SerialNumber) {
			this();
			this.Username = username;
			this.Password = password;
			this.SerialNumber = SerialNumber;
		}
	}
	public LoginRequestModel(String username, String password, String SerialNumbe){
		this.loginInfo = new LoginInfo(username, password, SerialNumbe);
	}
			
}
