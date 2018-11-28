package com.inepro.service.login.auth;

import java.util.Hashtable;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import com.canon.meap.csee.service.login.base.AuthControl;
import com.canon.meap.csee.service.login.base.LoginActivator;
import com.canon.meap.csee.service.login.base.SAImplFactory;
import com.canon.meap.csee.service.login.base.SecurityAgentImpl;
import com.canon.meap.csee.service.login.base.acq.ACQAppCtrFactory;
import com.canon.meap.csee.service.login.base.util.LoginProperties;
import com.canon.meap.csee.service.login.base.util.LoginSyslog;
import com.canon.meap.csee.service.login.base.util.Syslog;
import com.canon.meap.service.sis.CSSOLoginData;

public final class AuthModuleActivator implements LoginActivator {

	//=========================================================================
    // Instance Fields
    //=========================================================================

    private static SecurityAgentImpl _securityAgentImpl = null;

    private ServiceRegistration _regSecurityAgent = null;

    private ServiceRegistration _regAcqAPPControl = null;

    //=========================================================================
    // constructors
    //=========================================================================

    /**
     * Constructs a new instance of AuthModuleActivator
     */
    public AuthModuleActivator() {
        LoginSyslog.appendInformation(Syslog.LOGIN_SYS_EVENT_LOG,
                "AuthModuleActivator constructor called");

    }
    /**
     * Starts the Non AMS authentication module.<P>
     * 
     * @param bundleContext
     * the BundleContext instance
     * @param authControl
     * the AuthControl instance
     * @param loginProperties
     * the LoginProperties instance
     * @throws Exception
     * Exception will be passed to the caller.
     */
	@Override
	public void start(BundleContext bundlecontext, AuthControl authControl) throws Exception {
		 start(bundlecontext, authControl, new LoginProperties(
	                "res/LoginSetting"));
		
	}

	@Override
	public void start(BundleContext bundleContext, AuthControl authControl,
			LoginProperties paramLoginProperties) throws Exception {
		SAImplFactory implFactory = new SAImplFactory();
        _securityAgentImpl = implFactory.createSecurityAgentImpl(authControl,
                bundleContext, "LocalLogin", CSSOLoginData.AUT_ESA_LITE);

        Hashtable<String, String> saProps = new Hashtable<String, String>(1);
        saProps.put(Constants.SERVICE_DESCRIPTION, SecurityAgentImpl.ALIAS_SA);
        _regSecurityAgent = bundleContext.registerService(
                SecurityAgentImpl.PATH_SA, _securityAgentImpl, saProps);

        /**
         * ACQApplicationControlImpl instance is created by using ACQAppCtrFactory.
         */
        ACQAppCtrFactory appCtrFactory = new ACQAppCtrFactory();

        Object _acqAPPControl = appCtrFactory
                .createACQApplicationControlImpl(bundleContext);
        Properties acqAPPControlProp = new Properties();
        acqAPPControlProp.put(Constants.SERVICE_DESCRIPTION,
                "ACQApplicationControl");

        _regAcqAPPControl = bundleContext.registerService(
                "com.canon.meap.security.acq.login.ACQApplicationControl",
                _acqAPPControl, acqAPPControlProp);
		
	}

	/**
     * Stops the Non AMS authentication module.
     * <P> 
     * @param bundleContext
     * the BundleContext instance
     * @throws Exception
     * If an exception occurs, framework will remove the listeners of this bundle and unregister all the services, which are registered to this bundle.
     */
	@Override
	public void stop(BundleContext paramBundleContext) throws Exception {
		 if (_regAcqAPPControl != null) {
	            _regAcqAPPControl.unregister();
	        }

	        if (_regSecurityAgent != null) {
	            _regSecurityAgent.unregister();
	            _regSecurityAgent = null;
	        }

	        if (_securityAgentImpl != null) {
	            _securityAgentImpl.finish();
	            _securityAgentImpl = null;
	        }
		
	}
	
	/**
     * Gets the SecurityAgentImpl instance.
     * @return
     * the SecurityAgentImpl instance
     */
    public static SecurityAgentImpl getSecurityAgentImpl() {
    	return _securityAgentImpl;
    }

}
