//****************************************************************************
//
// Copyright CANON INC. 2004
// All Rights Reserved
//
// PortalPropertiesImpl.java
//
// MEAP Login Application SDK
//
// Version 1.00
//
//****************************************************************************

package com.inepro.service.login.servlet;

import java.util.Locale;
import java.util.ResourceBundle;

import com.canon.meap.csee.service.login.base.util.LoginProperties;
import com.canon.meap.csee.service.login.base.util.LoginSyslog;
import com.canon.meap.csee.service.login.base.util.Syslog;
import com.canon.meap.service.portalservice.PortalProperties;

/**
 * This class implements PortalProperties class.<P>
 * 
 * The information(title,URL,URL of image,tooltip) displayed in PortalService is obtained from specified property file:
 * 
 * @see com.canon.meap.service.portalservice.PortalProperties
 */
public final class PortalPropertiesImpl implements PortalProperties {

	private String _resourceName;
	private LoginProperties _pro;

	/**
	* Constructs a new instance of PortalPropertiesImpl.
	* @param  pro
	* the LoginProperties instance
	*/
	public PortalPropertiesImpl(LoginProperties pro) {
		this._pro = pro;
	}

	/**
	* Constructs a new instance of PortalPropertiesImpl.
	* @param  value
	* the resource name
	*/
	public PortalPropertiesImpl(String value) {
		this._resourceName = value;
	}

	/**
	 * Gets corresponding a string from property file according to the locale. 
	 * @param  key
	 * the key of the string
	 * @param  locale
	 * the locale of language
	 * @see com.canon.meap.service.portalservice.PortalProperties#getStringProperty
	 */
	public String getStringProperty(String key, Locale locale) {

		if(this._pro!=null){
			return _pro.getStringProperty(key,locale);
		}

		ResourceBundle resource = null;
		try {
			resource = ResourceBundle.getBundle(_resourceName, locale);
		} catch (Exception e) {
			LoginSyslog.append(
				Syslog.LOGIN_SYS_ERROR,
				Syslog.WARNING,
				"[getStringProperty:] " + e.toString());
			return null;
		}
		return resource.getString(key);
	}
}
