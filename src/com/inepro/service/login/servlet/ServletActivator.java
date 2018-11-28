//****************************************************************************
//
// Copyright CANON INC. 2012
//
// ServletActivator.java
//
// MEAP Login Application SDK
//
// Version 4.00
//
//****************************************************************************


package com.inepro.service.login.servlet;

import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

import com.canon.meap.csee.service.login.base.AuthControl;
import com.canon.meap.csee.service.login.base.LoginActivator;
import com.canon.meap.csee.service.login.base.util.LoginProperties;
import com.canon.meap.service.conf.ConfigurationService;
import com.canon.meap.service.login.RemoteLoginService;
import com.canon.meap.service.portalservice.PortalProperties;
import com.canon.meap.service.portalservice.PortalRegistration;
import com.canon.meap.service.portalservice.PortalService;
import com.inepro.service.login.UserAuthControl;

/**
 * This class is an Activator class that implements the BundleActivator and displays the device information servlet. <P>
 * 
 * 
 */
public final class ServletActivator implements LoginActivator, ServiceListener {

    private static final String SERVLET_PATH = "/Configuration";
    /**
     * Servlet resource path
     */
    private static final String SERVLET_RESOURCE = "/ConfigurationImage";

    private BundleContext _bundleContext = null;

    private HttpContext _httpContext = null;

    private ServiceReference _httpServiceReference = null;

    private ServiceReference _rlsServiceReference = null;
    
    private ServiceReference _confServiceReference = null;

    private HttpService _httpService = null;

    private ConfigServiceServlet _configSevlet = null;

    private RemoteLoginService _rls = null;
    
    private static ConfigurationService _confservice = null;
    
    /**
     * PortalPropertiesImpl class
     */
    private PortalPropertiesImpl portalPropertiesImpl;

    /**
     * PortalRegistration class
     */
    private PortalRegistration portalRegistration;

    /**
     * PortalService registration flag
     */
    private boolean portalRegistrationState;
    
    private ServiceReference _portalServiceReference;
    
    private PortalService _portalServiceInstance;

    /**
     * Constructs a new instance of ServletActivator class.
     */
    public ServletActivator() {
        super();
        portalRegistrationState = false;
    }

    /**
     * Performs the start process of device information edit servlet, and registers the servlet to HttpService.<P>
     * 
     * "res/LoginSetting" will be the default loginProperties instance.<P>
     * 
     * 
     * @param bundleContext
     * the BundleContext instance
     * @param authControl
     * the AuthControl instance
     * @throws Exception
     * If an exception occurs, the framework will remove the listener of this bundle and unregister all services registered via this bundle.
     */
//    public void start(BundleContext bundleContext, AuthControl authControl)
//            throws Exception {
//        this.start(bundleContext, authControl, new LoginProperties(
//                "res.LoginSetting"));
//    }

    /**
     * Performs the start process of device information edit servlet, and registers the servlet to HttpService. <P>
     * 
     * 
     * @param bundleContext
     * the BundleContext instance
     * @param authControl
     * the AuthControl instance
     * @param loginProperties
     * the LoginProperties instance
     * @throws Exception
     * If an exception occurs, the framework will remove the listener of this bundle and unregister all services registered via this bundle.
     */
    public void start(BundleContext bundleContext, AuthControl authControl,
            LoginProperties loginProperties) throws Exception {

        this._bundleContext = bundleContext;

        try {
        	 String PORTAL_SERVICE_FILTER
             = "(objectClass=com.canon.meap.service.portalservice.PortalService)";
        	final String CONFIG_NAME = ConfigurationService.class.getName();
            this._rlsServiceReference = bundleContext
                    .getServiceReference("com.canon.meap.service.login.RemoteLoginService");
            this._rls = (RemoteLoginService) bundleContext
                    .getService(this._rlsServiceReference);
            this._confServiceReference = bundleContext.getServiceReference(CONFIG_NAME);
            _confservice = (ConfigurationService)bundleContext.getService(_confServiceReference);
     
            /* Define the ServiceListener */
            this._bundleContext.addServiceListener(
                  this, PORTAL_SERVICE_FILTER);
            
            this._bundleContext.addServiceListener(this, "(objectClass="+ ConfigurationService.class+")");
            this._configSevlet = new ConfigServiceServlet(this._rls,
                    bundleContext,  _confservice);

            try {
                this.fetchHttpService();
            } catch (Exception e) {
                this._bundleContext.addServiceListener(this,
                        "(objectClass=org.osgi.service.http.HttpService)");
                return;
            }
            this.registerServlet();

        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        }
    }

    /**
     * Performs the terminate process of device information edit servlet and unregisters the servlet from HttpService.<P>
     * 
     * This method also releases HttpService.<P>
     * 
     * 
     * @param bundleContext
     * the BundleContext instance
     * @throws Exception
     * If an exception occurs, the framework will remove the listener of this bundle and unregister all services registered via this bundle.
     */
    public void stop(BundleContext bundleContext) throws Exception {
    	boolean ungetStatus = false;
    	 /* Delete this Servlet from Portal service */
        if (portalRegistrationState == true) {
            try {
                _portalServiceInstance.unregisterLinkInfo(
                        PortalProperties.ALL_USERS, portalRegistration);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        /* Release Portal service */
        if (_portalServiceInstance != null) {
            ungetStatus = _bundleContext.ungetService(_portalServiceReference);
            if (ungetStatus == false) {
                throw new Exception("Portal ungetService failed.");
            }
        }

        try {
            this._httpService.unregister(SERVLET_RESOURCE);
            this._httpService.unregister(SERVLET_PATH);
        } catch (Exception exception) {
            throw exception;
        }

        if (this._httpServiceReference != null) {
            bundleContext.ungetService(this._httpServiceReference);
        }

        if (this._rlsServiceReference != null) {
            bundleContext.ungetService(this._rlsServiceReference);
            this._rlsServiceReference = null;
        }
        
     
        if (_confServiceReference != null) {
        	ungetStatus = _bundleContext.ungetService(_confServiceReference);
        	this._confServiceReference = null;
           
        }
    }

    /**
     * Gets HttpService from OSGI framework.<P>
     * 
     * 
     * @throws Exception
     * If an exception occurs, the framework will remove the listener of this bundle and unregister all services registered via this bundle.
     */
    private void fetchHttpService() throws Exception {

        String HTTP_SERVICE = "org.osgi.service.http.HttpService";

        this._httpServiceReference = this._bundleContext
                .getServiceReference(HTTP_SERVICE);
        if (this._httpServiceReference == null) {
            throw new Exception("");
        }

        this._httpService = (HttpService) this._bundleContext
                .getService(this._httpServiceReference);
        if (this._httpService == null) {
            throw new Exception("");
        }

        return;
    }

    /**
     * Registers Servret to HttpService.
     * 
     * @throws Exception
     * If failed in register, Exception is thrown.
     */
    private void registerServlet() throws Exception {

        Properties properties = null;
        this._httpContext = this._httpService.createDefaultHttpContext();
        properties = new Properties();
        this._httpService.registerServlet(SERVLET_PATH, this._configSevlet,
                properties, this._httpContext);
        this._httpService.registerResources(
        		SERVLET_RESOURCE, "/res/images", this._httpContext);
        /* Regist this Servlet to Portal service */
        registerPortalService();

    }

    /**
     * This method is called by OSGi when service information change. 
     * @param event 
     * the event information
     */
    public void serviceChanged(ServiceEvent event) {

        if (event.getType() == ServiceEvent.REGISTERED) {
            try {
//                this.fetchHttpService();
//                this.registerServlet();
                this.registerPortalService();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(event.getType() == ServiceEvent.UNREGISTERING)
        {
        	  if (portalRegistrationState == true) {
  	            try {
  	                _portalServiceInstance.unregisterLinkInfo(
  	                        PortalProperties.ALL_USERS, portalRegistration);
  	            }
  	            catch (Exception exception) {
  	                exception.printStackTrace();
  	            }
  	        }
        }
    }
    
    /**
     * Regist this Servlet to Portal service
     */
    private synchronized void registerPortalService() {
    	if (this._bundleContext == null) {
            throw new IllegalArgumentException();
        }
        String PORTAL_SERVICE
                = "com.canon.meap.service.portalservice.PortalService";

        if (portalRegistrationState == true) {
            return;
        }

        _portalServiceReference
                = this._bundleContext.getServiceReference(PORTAL_SERVICE);
        if (_portalServiceReference == null) {
            return;
        }

        /* Acquire Portal service from OSGi framework */
        _portalServiceInstance = (PortalService)
                this._bundleContext.getService(_portalServiceReference);
        if (_portalServiceInstance == null) {
            return;
        }
        
        /* Regist this Servlet to Portal service */
        portalPropertiesImpl = new PortalPropertiesImpl(
                "res.ConfigurationPortalResource");
        portalRegistration = _portalServiceInstance.registerLinkInfo(
                PortalProperties.ALL_USERS, portalPropertiesImpl);
       
        portalRegistrationState = true;

        return;
    }

	public static ConfigurationService getConfigInstance()
	{
		return _confservice;
	}

	@Override
	public void start(BundleContext bundleContext, AuthControl authControl) throws Exception {
		LoginProperties loginProperties = new LoginProperties(
	            "res/LoginSetting");

		this.start(bundleContext, authControl, loginProperties);
	}

}
