package com.inepro.service.login.lls;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import com.canon.meap.csee.service.login.base.AuthControl;
import com.canon.meap.csee.service.login.base.LoginActivator;
import com.canon.meap.csee.service.login.base.lls.LocalLoginServiceImpl;
import com.canon.meap.csee.service.login.base.util.LoginProperties;
import com.canon.meap.service.avs.login.LoginService;
import com.canon.meap.service.avs.login.LoginServiceRegister;
import com.inepro.service.login.UserAuthControl;

/**
 * The LLSActivator class is an Activator class of LLS implemented LoginActivator interface.<P>
 *
 * A new instance of LLSActivator class is created at com.canon.meap.csee.service.login.sample.Activator class.<P>
 *
 * The start() method of LLSActivator class starts LocalLoginService,obtains LoginServiceRegister,and then
 * registers the LoginApplet to AppletViewerService.<P>
 *
 * The stop() method of LLSActivator class deletes the resource from AppletViewerService and releases the AppletViewerService.<P>
 *
 * @version       $Revision: 1.4 $, $Date: 2004/11/30 07:16:08 $
 */
public final class LLSActivator implements LoginActivator {

	//=========================================================================
	// Instance Fields
	//=========================================================================

	private ServiceRegistration _llsRegistration = null;
	private ServiceReference _serviceReference = null;
	private LoginService _loginApplet = null;
	private LoginServiceRegister _loginServiceRegister = null;
	private LoginProperties _resource = null;

	//=========================================================================
	// constructors
	//=========================================================================

	/**
	* Constructs a new instance of LLSActivator.
	*/
	public LLSActivator() {
	}

	//=========================================================================
	// LLSActivator methods
	//=========================================================================

	/**
	 * Starts the LLS.<P>
	 *
	 * This method creates a new LocalLoginServiceImpl instance and registers
	 * it to framework as a service. In addition, this method will register LoginApplet
	 * to AppletViewerService.
	 * @param bundleContext
	 * the BundleContext instance of login application
	 * @param authControl
	 * a AuthControl instance
	 * @throws Exception
	 * When an exception is thrown, the framework will removes the listener of this bundle and unregister the service registered
	 * via this bundle.
	 */
	public void start(
		BundleContext bundleContext,
		AuthControl authControl)
		throws Exception {
		start(bundleContext, authControl,new LoginProperties("res/LoginSetting"));
	}

	/**
	 * Starts the LLS.<P>
	 *
	 * This method creates a new LocalLoginServiceImpl instance and registers
	 * it to framework as a service. In addition, this method will register LoginApplet
	 * to AppletViewerService.
	 * @param bundleContext
	 * the BundleContext instance of login application
	 * @param authControl
	 * a AuthControl instance
	 * @param pro
	 * a LoginProperties instance
	 * @throws Exception
	 * When an exception is thrown, the framework will removes the listener of this bundle and unregister the service registered
	 * via this bundle.
	 */
	public void start(
		BundleContext bundleContext,
		AuthControl authControl,
		LoginProperties pro)
		throws Exception {

		_resource = pro;

		/*
		 * Acquire LoginServiceRegister from OSGI framework.
		 */
		try {
			_serviceReference =
				bundleContext.getServiceReference(
					LocalLoginServiceImpl.AVS_REGISTER_INTERFACE);

			_loginServiceRegister =
				(LoginServiceRegister) bundleContext.getService(_serviceReference);

		} catch (Exception exception) {
			throw exception;
		}

		if (_loginServiceRegister == null) {
			unregister(bundleContext);
			throw new BundleException("failed to import LoginServiceRegister");
		}

		/*
		 * Create a new instance of LocalLoginServiceImpl.
		 */
		LocalLoginServiceImpl localLoginServiceImpl = new LocalLoginServiceImpl();
		Dictionary<String,String> llsProps = new Hashtable<String,String>(1);
		llsProps.put(Constants.SERVICE_DESCRIPTION, LocalLoginServiceImpl.ALIAS_LLS);

		/*
		 * Register LocalLoginService to OSGI framework.
		 */
		_llsRegistration =
			bundleContext.registerService(
				LocalLoginServiceImpl.PATH_LLS,
				localLoginServiceImpl,
				llsProps);

		/*
		 * Register LoginApplet to AVS.
		 */
		_loginApplet = new LoginApplet(new LocalLoginHandler(localLoginServiceImpl, (UserAuthControl) authControl), _resource,(UserAuthControl) authControl, bundleContext);

		_loginServiceRegister.registerLoginService(_loginApplet);
	}

	/**
	 * Stops the LLS.
	 * @param bundleContext
	 * the BundleContext instance of login application
	 * @throws Exception
	 * when an exception is thrown, the framework will remove the listener of this bundle and unregister the service registered
	 * via this bundle.
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		unregister(bundleContext);
	}

	/**
	 * Unregisters the LoginApplet from AppletViewerService.
	 * @param bundleContext
	 * the BundleContext instance of login application
	 */
	private void unregister(BundleContext bundleContext) {

		/*
		 * Delete LocalLoginService from OSGI framework.
		 */
		if (_llsRegistration != null) {
			_llsRegistration.unregister();
			_llsRegistration = null;
		}

		/*
		 * Delete this applet from AVS
		 */
		if (_loginServiceRegister != null) {
			_loginServiceRegister.unregisterLoginService();
			_loginServiceRegister = null;
		}

		/*
		 * Release the AVS
		 */
		if (_serviceReference != null) {
			bundleContext.ungetService(_serviceReference);
			_serviceReference = null;
		}
		_loginApplet = null;
	}
}
