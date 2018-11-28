package com.inepro.service.login.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TCostCentres {
	@JsonProperty("AmountSpent")
	public int amountSpent;
	@JsonProperty("Code")
	public String code;
	@JsonProperty("GroupID")
	public int groupID;
	@JsonProperty("ID")
	public int id;
	@JsonProperty("LongName")
	public String longName;
	@JsonProperty("MaxSpendingAmount")
	public int maxSpendingAmount;
	@JsonProperty("ShortName")
	public String shortName;
	@JsonProperty("ValidFrom")
	public String validFrom;
	@JsonProperty("ValidTo")
	public String validTo;

}
