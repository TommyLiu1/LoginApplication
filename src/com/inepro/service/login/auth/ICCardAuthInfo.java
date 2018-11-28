package com.inepro.service.login.auth;

import com.canon.meap.service.login.customize.AuthInfo;

public class ICCardAuthInfo extends AuthInfo{

	@Override
	public String[] getDisplayName() {
		 String[] displayNames = { "IC CARD AUTH" };
	        return displayNames;
	}

	@Override
	public String getAuthControlClassName() {
		 return "com.inepro.service.login.auth.ICCardAuthInfo";
	}

	@Override
	public String[] getSupportedLoginServiceTypes() {
		 String[] supportService = { AuthInfo.LOGIN_SERVICE_TYPE_LLS_ICCARD };
	        return supportService;
	}

	@Override
	public String[] getKeys() {
		String[] supportedKeys = { AuthInfo.KEY_CARDID, AuthInfo.KEY_DOMAIN };
        return supportedKeys;
	}

}
