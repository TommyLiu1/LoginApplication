//****************************************************************************
//
// Copyright CANON INC. 2004
// All Rights Reserved
//
// Activator.java
//
// MEAP Login Application SDK
//
// Version 1.00
//
//****************************************************************************
package com.inepro.service.login;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import com.canon.meap.csee.service.login.base.LoginActivator;
import com.canon.meap.csee.service.login.base.LoginStartup;
import com.canon.meap.csee.service.login.base.ils.ILSActivator;
import com.canon.meap.csee.service.login.base.ils.InternalLoginServiceImpl;
import com.canon.meap.csee.service.login.base.util.LoginException;
import com.canon.meap.csee.service.login.base.util.LoginProperties;
import com.canon.meap.csee.service.login.base.util.LoginSyslog;
import com.canon.meap.csee.service.login.base.util.Syslog;
import com.inepro.service.login.auth.AuthModuleActivator;
import com.inepro.service.login.servlet.ServletActivator;

/**
 * The Activator class implements the BundleActivator interface for sample class
 * of login application.
 * <P>
 *
 * This class provides the start and terminate process for LLS,ILS,RLS and
 * servlet of user information edit.
 * <P>
 *
 * This class activate Activator classes for RLS and LLS by reading property
 * file.
 * <P>
 * 
 * Since these Activator classes implement the LoginActivator interface,it can
 * customized without changing other components.
 * 
 * Activator class for RLS is indicated by the property file as
 * "LLS_ACTIVATOR_CLASS". <BR>
 * Activator class for RLS is indicated by the property file as
 * "RLS_ACTIVATOR_CLASS".
 * <P>
 * 
 * The property file is specified with the following paths.
 * <P>
 * 
 * res/LoginSetting.LoginSetting.properties
 * <P>
 * 
 * @version $Revision: 1.7 $, $Date: 2006/09/15 07:58:12 $
 */
public final class Activator implements BundleActivator {

	// =========================================================================
	// Instance Fields
	// =========================================================================

	private UserAuthControl _authControl = null;
	private ServiceRegistration _saReg = null;
	private ServiceRegistration _ilsReg = null;
	private LoginActivator _saActivator = null;
	private LoginActivator _servletActivator = null;
	private LoginActivator _rlsActivator = null;
	private LoginActivator _llsActivator = null;
	private LoginActivator _ilsActivator = null;
	private LoginProperties _pro = new LoginProperties("res/LoginSetting");
	private static BundleContext _bundleContext = null;

	// =========================================================================
	// constructors
	// =========================================================================

	/**
	 * Constructs a new instance of Activator class.
	 * <P>
	 */
	public Activator() {
		/*
		 * Define SysLog class and register it on LoginSyslog instance.
		 */
		LoginSyslog.setSyslog(new Syslog() {
			public void append(int groupID, int priority, String mesg) {
				System.out.println("LOGIN APPLICATION :" + mesg);

			}
		});
	}

	// =========================================================================
	// Activator methods
	// =========================================================================

	/**
	 * Starts the login application.
	 * <P>
	 *
	 * The followings are done in this method.
	 * <P>
	 * 1.Creates a new instance of AuthControl.<BR>
	 * 2.Creates a new instance of SecurityAgentImpl class and registers it on
	 * framework.<BR>
	 * 3.Creats and starts a new instance of Activator class for user
	 * information edit servlet.<BR>
	 * 4.Creats and starts a new instance of Activator class for
	 * LocalLoginService.<BR>
	 * 5.Creats and starts a new instance of Activator class for
	 * RemoteLoginService.<BR>
	 * 6.Creats a new instance of InternalLoginServiceImpl class and registers
	 * it on framework.
	 * <P>
	 *
	 * @param bundleContext
	 *            the BundleContext instance of login application
	 * @throws Exception
	 *             When an exception is thrown, framework will remove the
	 *             listener of this bundle and unregister all the registered
	 *             services of this bundle.
	 */
	public void start(BundleContext bundleContext) throws Exception {

		_bundleContext = bundleContext;

		/*
		 * Create a new instance of AuthControl.
		 */
		_authControl = new UserAuthControl(bundleContext, _pro);
		LoginStartup.startAll(bundleContext);
		
		/*
         * Non AMS authentication module will start when the device is in Non AMS mode.
         *  
         */
		try{
			_saActivator = new AuthModuleActivator();
			_saActivator.start(bundleContext, _authControl, _pro);
		}catch(Exception e)
		{
			LoginSyslog.appendCritical(Syslog.LOGIN_SYS_ERROR, "_saActivator start failed!");
		}
		
		/*
		 * Create and start a new instance of Activator class for RLS. Activator
		 * class for RLS is indicated by the property file as
		 * "RLS_ACTIVATOR_CLASS".
		 */
		try {

			Class<?> c = Class.forName(_pro.getStringProperty("RLS_ACTIVATOR_CLASS"));
			Object obj = c.newInstance();

			if (obj instanceof LoginActivator) {
				this._rlsActivator = (LoginActivator) obj;
			} else {
				LoginSyslog.appendCritical(Syslog.LOGIN_SYS_ERROR, "RLSActivator does not exist!!");
				throw new LoginException("RLSActivator does not exist!!");
			}

			this._rlsActivator.start(bundleContext, _authControl, _pro);

		} catch (Exception exception) {
			throw exception;
		}

		/*
		 * Create and start a new instance of Activator class for LLS. Activator
		 * class for LLS is indicated by the property file as
		 * "LLS_ACTIVATOR_CLASS".
		 */
		try {
			Class<?> c = Class.forName(_pro.getStringProperty("LLS_ACTIVATOR_CLASS"));
			Object obj = c.newInstance();

			if (obj instanceof LoginActivator) {
				this._llsActivator = (LoginActivator) obj;
			} else {
				LoginSyslog.appendCritical(Syslog.LOGIN_SYS_ERROR, "LLSActivator does not exist!!");
				throw new LoginException("LLSActivator does not exist!!");
			}

			_llsActivator.start(bundleContext, _authControl, _pro);

		} catch (Exception exception) {
			throw exception;
		}

		/*
		 * Create and start a new instance of Activator class for user
		 * information edit servlet.
		 */
		try {
			_servletActivator = new ServletActivator();
			_servletActivator.start(bundleContext, _authControl, _pro);
		} catch (Exception exception) {
			throw exception;
		}

		/*
		 * Starts ILS.
		 */
		this._ilsActivator = new ILSActivator();
		this._ilsActivator.start(bundleContext, this._authControl, _pro);

		/*
		 * Create a new instance of InternalLoginServiceImpl class and resist it
		 * to framework.
		 */
		startILS(bundleContext);
	}

	/**
	 * Gets the the BundleContext instance of login application
	 * <P>
	 * 
	 * @return the BundleContext instance of login application
	 */
	public static BundleContext getBundleContext() {
		return _bundleContext;
	}

	/**
	 * Stops the login application.
	 * <P>
	 *
	 * This method releases all the resources(e.g. thread and instance) used by
	 * login application.
	 *
	 * @param bundleContext
	 *            the BundleContext instance of login application When an
	 *            exception is thrown, framework will remove the listener of
	 *            this bundle and unregister all the registered services of this
	 *            bundle.
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		unregister(bundleContext);
		if (_saActivator != null) {
            _saActivator.stop(bundleContext);
            _saActivator = null;

        }
		LoginStartup.stop(bundleContext);
	}

	// =========================================================================
	// Private Methods
	// =========================================================================

	/**
	 * Starts the InternalLoginService.
	 * 
	 * @param bundleContext
	 *            the BundleContext instance of login application
	 */
	private void startILS(BundleContext bundleContext) throws IOException {

		/*
		 * Create a new instance of InternalLoginServiceImpl class and resist it
		 * to framework.
		 */
		Dictionary<String, String> ilsProps = new Hashtable<String, String>(1);
		InternalLoginServiceImpl ils = new InternalLoginServiceImpl(_authControl, bundleContext);
		ilsProps.put(Constants.SERVICE_DESCRIPTION, InternalLoginServiceImpl.ALIAS_ILS);
		_ilsReg = bundleContext.registerService(InternalLoginServiceImpl.PATH_ILS, ils, ilsProps);
	}

	/**
	 * Stops the login application.
	 * <P>
	 * 
	 * @param bundleContext
	 *            the BundleContext instance of login application
	 * @throws Exception
	 *             throws the exception to the method who calls it when an
	 *             exception occurs
	 */
	private void unregister(BundleContext bundleContext) throws Exception {

		if (_saReg != null) {
			_saReg.unregister();
			_saReg = null;
		}

		if (_ilsReg != null) {
			_ilsReg.unregister();
			_ilsReg = null;
		}

		if (_servletActivator != null) {
			_servletActivator.stop(bundleContext);
			_servletActivator = null;

		}
		if (_llsActivator != null) {
			_llsActivator.stop(bundleContext);
			_llsActivator = null;

		}
		if (_rlsActivator != null) {
			_rlsActivator.stop(bundleContext);
			_rlsActivator = null;

		}
		_authControl = null;
	}
}
