package com.inepro.service.login.lls;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map.Entry;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.canon.meap.csee.service.login.base.AuthControl;
import com.canon.meap.csee.service.login.base.lls.LocalLoginBaseApplet;
import com.canon.meap.csee.service.login.base.lls.ui.HardKey;
import com.canon.meap.csee.service.login.base.lls.ui.WindowSize;
import com.canon.meap.csee.service.login.base.util.DeviceInformation;
import com.canon.meap.csee.service.login.base.util.LoginException;
import com.canon.meap.csee.service.login.base.util.LoginProperties;
import com.canon.meap.csee.service.login.base.util.LoginSyslog;
import com.canon.meap.csee.service.login.base.util.Syslog;
import com.inepro.service.login.lls.ui.LLSCostCentresPanel;
import com.inepro.service.login.lls.ui.LocalLoginPanel;
import com.inepro.service.login.lls.ui.LocalLoginStartPanel;
import com.inepro.service.login.model.CustomerInfo;
import com.inepro.service.login.model.LoginResult;
import com.inepro.service.login.Activator;
import com.inepro.service.login.auth.ICCardAuthInfo;
import com.inepro.service.login.net.HttpRequestUtils;
import com.canon.meap.ctk.awt.CColor;
import com.canon.meap.service.avs.CAppletAdapter;
import com.canon.meap.service.avs.CAppletException;
import com.canon.meap.service.avs.login.LoginService;
import com.canon.meap.service.login.LocalLoginService;
import com.canon.meap.service.login.customize.AuthInfo;
import com.canon.meap.service.login.customize.AuthenticatedUserData;
import com.canon.meap.service.login.customize.CustomizeException;
import com.canon.meap.service.login.customize.LoginHandler;
import com.canon.meap.service.login.event.UserEvent;
import com.canon.meap.service.login.event.UserEventListener;
import com.canon.meap.service.login.iccard.CardReadException;
import com.canon.meap.service.login.iccard.receiver.CardDriverManager;
import com.canon.meap.service.login.iccard.receiver.CardEvent;
import com.canon.meap.service.login.iccard.receiver.CardEventListener;
import com.canon.meap.service.sis.CLoginData;
import com.canon.meap.service.sis.event.CHardKeyEvent;
import com.canon.meap.service.sis.event.CHardKeyListener;

/**
 * This class is an Applet class that implements LoginService.<P>
 * 
 * When the user authentication is successful, a new login context is created and stored at AVS login event.
 * The login authentication will notify the AVS and local login service.
 *
 * @see com.canon.meap.csee.service.login.LocalLoginHandler.lls.LoginHandler
 * @version       $Revision: 1.8 $, $Date: 2018/11/13 17:29:07 $
 */
final class LoginApplet extends LocalLoginBaseApplet implements LoginService, UserEventListener,CardEventListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private LocalLoginPanel _userLoginPanel = null;
	
	private LocalLoginStartPanel _startupPanel = null;

    private ActionEventListener _action = null;
    
    private MouseListener _mouseListenser = null;
    
    private CardDriverManager _cdm = null;
    
    private boolean _loginNow = false;

    private LocalLoginHandler _loginHandler = null;

    private HardKey _hardKey = null;

    private CHardKeyListener _hardKeyEventListener = null;
    
    private LocalLoginService _localLoginService = null;
    
    private ServiceReference _llsServiceReference = null;
    
    private ServiceReference _cardDriverManagerReference = null;

    private CAppletAdapter _cAppletAdapter = null;

    private LoginProperties _resource = null;
    
    private WindowSize _windowSize = null;
    
    private static Object __lock = new Object();

    /**
     * Constructs a new instance of LoginApplet with the specified LoginHandler.
     * @param handler
     * the LoginHandler instance
     * @param pro
     * the LoginProperties instance
     */
    LoginApplet(LocalLoginHandler handler, LoginProperties pro, AuthControl authControl, BundleContext bundleContext) {
    	super(authControl, bundleContext);
        _loginHandler = handler;
        _resource = pro;
        _llsServiceReference = bundleContext.getServiceReference("com.canon.meap.service.login.LocalLoginService");
        _cardDriverManagerReference = bundleContext.getServiceReference("com.canon.meap.service.login.iccard.receiver.CardDriverManager");
        if (_llsServiceReference != null) {
          _localLoginService = (LocalLoginService)bundleContext.getService(this._llsServiceReference);
        }
        if(_cardDriverManagerReference != null)
        {
        	_cdm = (CardDriverManager) bundleContext.getService(_cardDriverManagerReference);
        }
    }

    /**
     * Initializes the login panel to be displayed on the panel of real device.<P>
     * 
     * The initialization process creates and registers an action listener on login button, 
     * a Hardkey event listener, a locale change event listener, and displays the panel.
     */
    public void init() {
        super.init();
        _windowSize = new WindowSize(this);
        _action = new ActionEventListener(this);
        _mouseListenser = new MouseEventListener(this);
        setLayout(null);
        setBounds(0, 0, this._windowSize.getLSizeWidth(),
                _windowSize.getLSizeHeight());
//        setBounds(0, 0, 1024,520);
        setBackground(CColor.silver);
        requestFocus();
        constructPanels();

        /* 
         * Create HardKey
         */
        createHardKey();

        /*
         * Register action listeners 
         */
        setLocaleListener();
        addLogoutListener();
        addCardReaderListener();

        _userLoginPanel.setMessages();
    }

    /**
     * Creats and displays the login panel.
     */
    private void constructPanels() {
        if (_resource == null) {
            _userLoginPanel = new LocalLoginPanel(_action);
            _startupPanel = new LocalLoginStartPanel(_mouseListenser);
        } else {
            _userLoginPanel = new LocalLoginPanel(_action, _resource);
            _startupPanel = new LocalLoginStartPanel(_resource, _mouseListenser);
            
        }
       
        add(_userLoginPanel);;
        add(_startupPanel);
        _userLoginPanel.setVisible(false);
        _startupPanel.setVisible(true);
        _startupPanel.init();
        _userLoginPanel.init();
    }
    
    private boolean componentExists(Component panel)
    {
    	Component components[] = getComponents();
    	for (Component component: components) {
    	    if (component.equals(panel)) {
    	        return true;
    	    }
    	}
    	return false;
    }

    /**
     * Creats and activates an event listener on hardkey.
     */
    private void createHardKey() {

        /* 
         * Create a new instance of CAppletAdapter class
         */
        try {
            _cAppletAdapter = new CAppletAdapter(this);
        } catch (CAppletException cae) {
            LoginSyslog.append(Syslog.LLS_ERROR, Syslog.WARNING,
                    "Applet: createHardKey : " + cae.toString());
        }

        /* 
         * Create and activate an event listener on hardkey.
         */
        _hardKey = new HardKey(this._cAppletAdapter);
        _hardKeyEventListener = new HardKeyEventListener();
        _hardKey.activate(_hardKeyEventListener);

        return;
    }

    /**
     * Registers locale change event listener on panel.
     */
    private void setLocaleListener() {
        CAppletAdapter appletAdapter = null;
        try {
            appletAdapter = new CAppletAdapter(this);
        } catch (CAppletException exception) {
            LoginSyslog.append(Syslog.LLS_ERROR, Syslog.WARNING,
                    "Applet: setLocaleListener : " + exception.toString());
        }
        if (appletAdapter != null) {
            appletAdapter.addLocaleListener(_userLoginPanel);
        }
    }
    
    private void addLogoutListener()
    {
    	if (this._localLoginService != null)
    	{
    		_localLoginService.addUserEventListener(this);
    	}
    }
    
    private void addCardReaderListener()
    {
    	if(this._cdm != null)
    	{
    		_cdm.addCardEventListener(this);
    	}
    }

    /**k
     * Performs the termination process.<P>
     * 
     * This method calls the finish() method of HardKey class and removes the hardkey event listener.
     */
    public void destroy() {
        super.destroy();

        if (_hardKey != null) {
            try {
                _hardKey.finish();
            } catch (Throwable e) {
                LoginSyslog.appendError(Syslog.LLS_ERROR, e.toString());
            }
            _hardKey = null;
        }

        return;
    }

    /**
     * Invoked when login button on login panel is pressed.<P>
     * 
     * This method performs login authentication via SampleLoginHandler.
     * If the login authentication is successful, the input field of panel is cleared.
     * If the login authentication is failed, an error message is displayed.
     * 
     * This method is ignored when the user has logined.
     * 
     * @see com.canon.meap.csee.service.login.sample.lls.SampleLoginHandler#login
     * @see com.canon.meap.csee.service.login.sample.lls.ui.SampleLocalLoginPanel
     */
    protected void goHomePage(CustomerInfo cutomerInfo) {
        synchronized (getLockObject()) {

            if (_loginNow) {
                return;
            }

            _loginNow = false;
            /*
             * Perform login authentication via LoginHandler.
             */
            try {
            	String uid = _userLoginPanel.getIdTextField();
            	String password =  _userLoginPanel.getPassTextField();
            	if (cutomerInfo == null)
            	{
            		_loginHandler.loginForAdmin(uid, password);
            	}else{
            		_loginHandler.login(cutomerInfo, uid);
            	}
            	_userLoginPanel.notifyLoginSuccess();
                _loginNow = true;
            } catch (LoginException exception) {
                _userLoginPanel.setErrorMessage(exception.getMessage(), true);
            }
        }
    }
    
    protected void sendUpdateCostCentreRequest(String  selectedBtn) throws LoginException
    {
    	try{
    		String username = _userLoginPanel.getIdTextField();
			String seriesNum = DeviceInformation.getDeviceSerialnumber(Activator.getBundleContext().getBundle());
			HashMap<Integer, String> costCentres = HttpRequestUtils.getCostCentres();
			int costcentresID = 0;
			for (Entry<Integer, String> entry : costCentres.entrySet()) {
		        if (entry.getValue().equals(selectedBtn)) {
		        	costcentresID =  entry.getKey();
		        }
		    }
            String api_url = HttpRequestUtils.getUpdateCostCentresApiUrl();	
			if (api_url != "")
			{
				HttpRequestUtils.getUpdateCostCentresResult(api_url, seriesNum, username, costcentresID);
			}
		} catch (Exception  e) {
			throw new LoginException(e.getMessage());
			
		}
    }
    
    protected void sendLogoutRequest(String userName )
    {
    	try{
            String api_url = HttpRequestUtils.getLogoutApiUrl();
			if (api_url != "")
			{
				HttpRequestUtils.getUserLogoutResult(api_url, userName);
			}
		} catch (Exception  e) {
			_userLoginPanel.setErrorMessage(e.getMessage(), true);
			
		}
    }
    /**
     * Invoked when AVS performs the logout process.<P>
     * 
     * 
     * This method performs logout process via SampleLoginHandler,un-displays the error panel,
     * and clears the input field of panel.<P>
     * 
     * This method is invoked at the followings:(the behavior may be different at different device model) 
     * Auto-cleared (the time out is set at user mode)<BR>
     * Restores from sleep mode(Sets the sleep interval at user mode can transit the device to sleep mode. Also,pressing ON/OFF key on the panel can do this.)<BR>
     * Transits to low power mode(the time out is set at user mode).<BR>
     * Presses the power save button.<BR>
     * Sets, modifies and removes the system administrator at user mode.<BR>
     * Changes the setting of department ID from ON to OFF at user mode by general user.<BR>
     * Changes the setting of display language at user mode.<P>
     * @see com.inepro.service.login.lls.LocalLoginHandler#loggedout
     */
    public void loggedout() {
        synchronized (getLockObject()) {

            _loginHandler.loggedout();
            _loginNow = false;

            /*
             * Set ErrorPanel invisible
             */
            _userLoginPanel.setVisible(true);
            _userLoginPanel.setErrorPanelVisible(false);
            requestFocus();
            _userLoginPanel.clearTextField();
        }
    }

    /**
     * Notified by AVS after user pressed the ID key during login.<P>
     * 
     * This method performs the process related to ID key press via SampleLoginHandler frist
     * and then calls the loggedout method of this class.<P>
     * 
     * 
     * @see com.canon.meap.csee.service.login.sample.lls.SampleLoginHandler#loggedoutWithIDKey
     */
    public void loggedoutWithIDKey() {
        _loginHandler.loggedoutWithIDKey();
        loggedout();
    }

    /**
     * Invoked when changing the permission of department management from general user to system administrator at user mode.<P>
     * 
     * This method performs the related process via LoginHandler.<P>
     * 
     * @param cLoginData
     * the instance of CLoginData
     * @see com.canon.meap.csee.service.login.sample.lls.SampleLoginHandler#loggedin
     */
    public void loggedin(CLoginData cLoginData) {
        _loginHandler.loggedin(cLoginData);
    }

    /**
     * Not used.<P>
     * 
     * @param i
     */
    public void authenticationTypeChanged(int i) {
    }

    /**
     * This class implements the CHardKeyListener interface and is used for receiving the hardkey event.<P>
     * 
     * @see com.canon.meap.service.sis.event.CHardKeyListener
     */
    private class HardKeyEventListener implements CHardKeyListener {
        /**
         * Process when hardKeyTyped occurred should be perfomed here.<P>
         * 
         * No process is perfomed in this method.
         * @param e
         * the hardkey event
         */
        public void hardKeyTyped(CHardKeyEvent e) {
        }

        /**
         * Process when headKeyReleased occurred should be perfomed here.<P>
         * 
         * No process is perfomed in this method.
         * @param e
         * the hardkey event
         */
        public void hardKeyReleased(CHardKeyEvent e) {
        }

        /**
         * Performs the process when hardKeyPressed occurred.<P>
         * 
         * If clear key is pressed, then clear the input field on login panel.<P>
         * If reset key is pressed, then clear the input field on login panel.<P>
         * If ID key is pressed, loginButtonPressed method will be called.<P>
         * @param e
         * the hardkey event
         */
        public void hardKeyPressed(CHardKeyEvent e) {
            int keyCode = 0;

            keyCode = e.getKeyCode();

            /* 
             * Notify HardKeyEvent to Applet
             */
            if (keyCode == HardKey.CLEAR) {
                _userLoginPanel.clearTextField();
            } else if (keyCode == HardKey.RESET) {
                _userLoginPanel.clearTextField();
            } else if (keyCode == HardKey.ID_KEY) {
            	String username = _userLoginPanel.getIdTextField();
            	String password = _userLoginPanel.getPassTextField();
            	if("admin".equals(username) && "password".equals(password))
            	{
            		goHomePage(null);
            	}else{
            		LoginResult result;
					try {
						result = HttpRequestUtils.login(username, password);
						goHomePage(result.loginResult.customerInfo);
					} catch (LoginException e1) {
						_userLoginPanel.setErrorMessage(e1.getMessage(), true);
					}
            		
            	}
            	
            }

            return;
        }

    } /* end class HardKeyEventListener */
    private class MouseEventListener implements MouseListener
    {
    	private LoginApplet _applet = null;
    	
    	public MouseEventListener(LoginApplet applet)
    	{
    		this._applet = applet;
    	}
		@Override
		public void mouseClicked(MouseEvent paramMouseEvent) {
			_applet._startupPanel.setVisible(false);
			_applet._userLoginPanel.setVisible(true);
		}

		@Override
		public void mousePressed(MouseEvent paramMouseEvent) {
			_applet._startupPanel.setVisible(false);
			_applet._userLoginPanel.setVisible(true);
			
		}

		@Override
		public void mouseReleased(MouseEvent paramMouseEvent) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent paramMouseEvent) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent paramMouseEvent) {
			// TODO Auto-generated method stub
			
		}
    	
    }
    /**
     * This class is used for receiving the action event from login panel.<P>
     */
    private class ActionEventListener implements ActionListener {

        /**
         * Performs the key-related event.<P>
         * 
         * If login button at login panel is pressed, this method will call loginButtonPressed.<P>
         * If OK button at error panel is pressed, this method hides the error panel and clears the 
         * input field at login panel.<P>
         * 
         * @param actionEvent
         * the ActionEvent of pressed key
         */
        public void actionPerformed(ActionEvent actionEvent) {
            String ac = actionEvent.getActionCommand();
            
            if ("loginButton".equals(ac)) {
            	String username = _applet._userLoginPanel.getIdTextField();
            	String password = _applet._userLoginPanel.getPassTextField();
            	if("admin".equals(username) && "password".equals(password))
            	{
            		_applet.goHomePage(null);
            		return;
            	}
            	try {
					result = HttpRequestUtils.login(username, password);
            		//result = HttpRequestUtils.LoginWithoutRequest();
					if(result.loginResult.loginSuccess)
					{
						boolean askCostCentres = result.loginResult.customerInfo.askCostCentres;
						if (askCostCentres && ! result.loginResult.tCostCentres.isEmpty())
						{
							 _costCentresPanel = new LLSCostCentresPanel(_applet._resource, _applet._action);
							_applet._userLoginPanel.setVisible(false);
							if (! _applet.componentExists(_costCentresPanel))
							{
								_applet.add(_costCentresPanel);
							}
							_costCentresPanel.init();
							_costCentresPanel.setVisible(true);
							return;
						}
						_applet.goHomePage(result.loginResult.customerInfo);
						
					}else {
						_applet._userLoginPanel.setErrorMessage(_loginProperties.getStringProperty("LOGIN_ERR_INVALID_USER_NAME"), true);
						
					}
					
				} catch (LoginException e) {
					_applet._userLoginPanel.setErrorMessage("Login Exception:"+e.getMessage(), true);
				}
                return;
            }
            if ("clearButton".equals(ac)) {
                return;
            }
            if ("errOkButton".equals(ac)) {
                _applet._userLoginPanel.setErrorPanelVisible(false);
                _applet._userLoginPanel.clearPassTextField();
                _applet.requestFocus();
                return;
            } 
            if ("_OK".equals(ac)) {
            	String username = _applet._userLoginPanel.getIdTextField();
            	if("admin".equals(username))
            	{
            		return;
            	}
            	try {
					LoginResult result = HttpRequestUtils.login(username, "");
					//result = HttpRequestUtils.LoginWithoutRequest2();
					if(result.loginResult.loginSuccess)
					{
						boolean askCostCentres = result.loginResult.customerInfo.askCostCentres;
						boolean isEmptyForCostCentre = result.loginResult.tCostCentres.isEmpty();
						if (askCostCentres && ! isEmptyForCostCentre)
						{
							 _costCentresPanel = new LLSCostCentresPanel(_applet._resource, _applet._action);
							_applet._userLoginPanel.setVisible(false);
							if (! _applet.componentExists(_costCentresPanel))
							{
								_applet.add(_costCentresPanel);
							}
							_costCentresPanel.init();
							_costCentresPanel.setVisible(true);
							return;
						}
						_applet.goHomePage(result.loginResult.customerInfo);
						
					}else{
						return;
					}
            	}catch(Exception e)
            	{
            		return;
            	}
            }
            if ("costCentresBtn".equals(ac)) {
            	String selectedBtn = _costCentresPanel.getSelectedRadioBtnName();
            	try{
            		_applet.sendUpdateCostCentreRequest(selectedBtn);
            		 if (_applet.componentExists(_costCentresPanel))
            			 _applet.remove(_costCentresPanel);
            
                	if(result != null)
                	{
                		_applet.goHomePage(result.loginResult.customerInfo);
                	}else{
                		_applet._userLoginPanel.setVisible(true);
                		_applet._userLoginPanel.setErrorMessage("error occurred when go to home", true);
                	}
                	return;
            	}catch(Exception e)
            	{
            		_applet._userLoginPanel.setErrorMessage("send updateCostCentre request failed", true);
            	}
            }
            
            if("backButton".equals(ac))
            {
            	_applet._startupPanel.setVisible(true);
            	_applet._userLoginPanel.setVisible(false);
            }
        }

        /**
         * The instance of this class.<P>
         */
        private LoginApplet _applet = null;
        private LoginProperties _loginProperties = null;
        private LoginResult result = null;
        private LLSCostCentresPanel _costCentresPanel = null;

        /**
         * Constructs a new instance of ActionEventListener with the specified SampleLoginApplet.
         * @param applet
         * the instance of SampleLoginApplet
         */
        private ActionEventListener(LoginApplet applet) {
            this._applet = applet;
            this._loginProperties = applet._resource;
        }
    }

    /**
     * @return 
     */
    private static Object getLockObject() {
        return __lock;
    }

	@Override
	public void login(UserEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void logout(UserEvent event) {
		String userName = event.getLoginContext().getUserAttribute("uid");
		sendLogoutRequest(userName);
	}

	@Override
	public void actionCardEvent(CardEvent e) {
		
		if(! _startupPanel.isVisible())
		{
			return;
		}
		
		int evtId = e.getEventType();
        if ((evtId == CardEvent.CARD_PLACE_PULLOUT)
                || (evtId == CardEvent.CARD_PULLOUT)
                || (evtId == CardEvent.CARD_REMOVE)) {
            return;
        }
        
        try {

			AuthInfo info = new ICCardAuthInfo();
			info.setAuthValue(AuthInfo.KEY_CARDID, e.getCardId());

			LoginHandler handler = LoginHandler.getInstance();
			AuthenticatedUserData aud = handler.executeAuthentication(info);
			handler.executeUnlock(aud);

		} catch (CustomizeException ex) {
			String strPro = String.valueOf(ex.getErrCode());
			_userLoginPanel.setErrorMessage(_resource.getStringProperty(strPro),true);

		} catch (CardReadException ex) {
			ex.printStackTrace();
			String strPro = String.valueOf(ex.getErrCode());
			_userLoginPanel.setErrorMessage(_resource.getStringProperty(strPro),true);

		} catch (Exception ex) {
			ex.printStackTrace();
			String strPro = String.valueOf(CustomizeException.LOGIN_ERR_OTHER);
			_userLoginPanel.setErrorMessage(_resource.getStringProperty(strPro),true);
		}
		
	}

}
