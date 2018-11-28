//****************************************************************************
//
// Copyright CANON INC. 2004
// All Rights Reserved
//
// RLSActivator.java
//
// MEAP Login Application SDK
//
// Version 1.00
//
//****************************************************************************
package com.inepro.service.login.rls;

import java.io.IOException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpContext;

import com.canon.meap.csee.service.login.base.AuthControl;
import com.canon.meap.csee.service.login.base.LoginActivator;
import com.canon.meap.csee.service.login.base.rls.LogoutServlet;
import com.canon.meap.csee.service.login.base.rls.RemoteLoginServiceImpl;
import com.canon.meap.csee.service.login.base.util.LoginProperties;
import com.canon.meap.service.http.HttpService;
import com.inepro.service.login.UserAuthControl;
import com.inepro.service.login.rls.RLSLoginServlet;


/**
 * This class is the Activator class that implements the LoginActivator.<P>
 * 
 * A new RLSActivator is created at com.canon.meap.csee.service.login.sample.Activator class.<P>
 * 
 * The start() method in this class starts RemoteLoginService,obtains HttpService, and then registers the LoginServlet
 * to HttpService.<P>
 * 
 * The stop() method in this class deletes the resources from HttpService and then releases the HttpService.<P>
 * 
 * @version       $Revision: 1.3 $, $Date: 2018/11/14 07:16:08 $
 */
public final class RLSActivator implements LoginActivator {

	//=========================================================================
	// Instance Fields
	//=========================================================================

	private ServiceRegistration _rlsReg;
	private ServiceReference _httpServiceReference;
	private HttpService _httpService;
	private RemoteLoginServiceImpl _rlsImpl;

	//=========================================================================
	// Constructors
	//=========================================================================

	/**
	 * Constructs a new instance of RLSActivator.
	 */
	public RLSActivator() {
	}

	//=========================================================================
	// RLSActivator Methods
	//=========================================================================

	/**
	 * Starts the RLS.<P>
	 * 
	 * This method obtains HTTPService from framework,registers RemoteLoginService to framework, and registers 
	 * LoginServlet and LogoutServlet to HttpService.
	 * 
	 * @param bundleContext
	 * the BundleContext instance of login application
	 * @param authControl
	 * the AuthControl instance
	 * @throws Exception
	 * If an exception occurs, the framework will remove the listener of this bundle and unregister all services registered via this bundle.
	 */
	public void start(BundleContext bundleContext,AuthControl authControl) throws Exception {
		start(bundleContext,authControl,new LoginProperties("res/LoginSetting"));
	}


	/**
	 * Starts the RLS.<P>
	 * 
	 * This method obtains HTTPService from framework,registers RemoteLoginService to framework, and registers 
	 * LoginServlet and LogoutServlet to HttpService.
	 * 
	 * @param bundleContext
	 * the BundleContext instance of login application
	 * @param authControl
	 * the AuthControl instance
	 * @param pro
	 * the LoginProperties instance
	 * @throws Exception
	 * If an exception occurs, the framework will remove the listener of this bundle and unregister all services registered via this bundle.
	 */
	public void start(BundleContext bundleContext,AuthControl authControl,LoginProperties pro) throws Exception {

		/*
		 * Acquire HttpService from OSGI framework.
		 */
		try {
			_httpServiceReference =
				bundleContext.getServiceReference(
					RemoteLoginServiceImpl.PATH_HTTPSERVICE);
			_httpService = (HttpService) bundleContext.getService(_httpServiceReference);
		} catch (Exception e) {
			unregister(bundleContext);
			throw e;
		}

		if (_httpService == null) {
			unregister(bundleContext);
			throw new BundleException("failed to import HttpService");
		}

		/*
		 * Register RemoteLoginServiceImpl to OSGI framework.
		 */
		_rlsImpl = new RemoteLoginServiceImpl(pro);
		Dictionary<String,String> rlsProps = new Hashtable<String,String>();
		rlsProps.put(Constants.SERVICE_DESCRIPTION, RemoteLoginServiceImpl.ALIAS_RLS);
		_rlsReg =
			bundleContext.registerService(
				RemoteLoginServiceImpl.PATH_RLS,
				_rlsImpl,
				rlsProps);

		HttpContext httpContext = new HttpContext() {

			public boolean handleSecurity(
				HttpServletRequest httpservletrequest,
				HttpServletResponse httpservletresponse)
				throws IOException {
				return true;
			}

			public URL getResource(String s) {
				try {
					return getClass().getClassLoader().getResource(s);
				} catch (Exception exception2) {
					exception2.printStackTrace();
				}
				return null;
			}

			public String getMimeType(String s) {
				return null;
			}

		};

		/*
		 * Register LoginServlet and LogoutServlet to HttpService. 
		 */
		RLSLoginServlet loginServlet = null;
		LogoutServlet logoutServlet = null;
		try {
			loginServlet = new RLSLoginServlet(_rlsImpl,(UserAuthControl)authControl, pro);
			_httpService.registerAdminServlet(
				com.canon.meap.service.http.HttpService.LOGIN,
				(HttpServlet) loginServlet,
				new Properties(),
				httpContext);

			logoutServlet = new LogoutServlet(_rlsImpl);
			_httpService.registerAdminServlet(
				com.canon.meap.service.http.HttpService.LOGOUT,
				(HttpServlet) logoutServlet,
				new Properties(),
				httpContext);
		} catch (Exception e) {
			unregister(bundleContext);
			throw e;
		}
	}

	/**
	 * Unregisters LoginServlet and LogoutServlet from HttpService.<P>
	 * 
	 * In addition, this method unregisters the RemoteLoginService from framework and releases
	 * the HttpService.
	 * 
	 *
	 * @param bundleContext
	 * the BundleContext instance of login application
	 * @throws Exception
	 * If an exception occurs, the framework will removes the listener of bundle and unrigister all the services registered via this bundle.
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		unregister(bundleContext);
		_rlsImpl.finish();
		_rlsReg=null;
		_httpServiceReference=null;
		_httpService=null;
		_rlsImpl=null;
	}

	//=========================================================================
	// Private Methods
	//=========================================================================

	/**
	 * Unregisters LoginServlet and LogoutServlet from HttpService.<P>
	 * 
	 * In addititon, this method unregisters the RemoteLoginService from framework and releases
	 * the HttpService.
	 * 
	 * @param bundleContext
	 * the BundleContext instance of login application
	 */
	private void unregister(BundleContext bundleContext) throws Exception {
		if (_rlsReg != null) {
			_rlsReg.unregister();
		}

		/* 
		 * Delete LoginServlet and LogoutServlet from HttpService.
		 */
		if (_httpService != null) {
			try {
				_httpService.adminUnregister(
					com.canon.meap.service.http.HttpService.LOGIN);
			} catch (Exception e) {
				throw e;
			}

			try {
				_httpService.adminUnregister(
					com.canon.meap.service.http.HttpService.LOGOUT);
			} catch (Exception e) {
				throw e;
			}
		}

		/*
		 * Release the HttpService.
		 */
		if (_httpServiceReference != null) {
			bundleContext.ungetService(_httpServiceReference);
		}

	}
}
