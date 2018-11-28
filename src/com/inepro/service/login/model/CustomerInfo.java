package com.inepro.service.login.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomerInfo {
	@JsonProperty("AskCostCentres")
	public boolean askCostCentres;
	@JsonProperty("ID")
	public long id;
	@JsonProperty("BudgetBalance")
	public double budgetBalance;
	@JsonProperty("BudgetBalancePurseID")
	public long budgetBalancePurseID;
	@JsonProperty("CustomerCode")
	public String CustomerCode;
	@JsonProperty("CloseCustomerXml")
	public String CloseCustomerXml;
	@JsonProperty("DepartmentID")
	public int departmentID;
	@JsonProperty("FirstName")
	public String firstName;
	@JsonProperty("IsAdmin")
	public boolean isAdmin;
	@JsonProperty("LastName")
	public String lastName;
	@JsonProperty("PersonalBalance")
	public int personalBalance;
	@JsonProperty("PersonalBalancePurseID")
	public int personalBalancePurseID;
	public CustomerInfo(){}

}
