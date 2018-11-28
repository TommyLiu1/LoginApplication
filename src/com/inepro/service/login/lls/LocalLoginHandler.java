
package com.inepro.service.login.lls;

import com.canon.meap.csee.service.login.base.LoginContextExt;
import com.canon.meap.csee.service.login.base.cpca.CpcaAuthFailException;
import com.canon.meap.csee.service.login.base.lls.LocalLoginServiceImpl;
import com.canon.meap.csee.service.login.base.util.LoginException;

import com.canon.meap.service.sis.CCPCALoginData;
import com.canon.meap.service.sis.CLoginData;
import com.inepro.service.login.UserAuthControl;
import com.inepro.service.login.model.CustomerInfo;

/** 
 * This class performs the Login process of LLS.<P>
 * @see com.canon.meap.csee.service.login.LoginApplet.lls.SampleLoginApplet
 * @version       $Revision: 1.4 $, $Date: 2018/11/14 07:58:12 $
 */
public final class LocalLoginHandler {

	/** 
	 * The class for login check.
	 */
	private UserAuthControl _authControl = null;
	/** 
	 * Instance of LocalLoginServiceImpl class
	 */
	private LocalLoginServiceImpl _localLoginService = null;

	/**
	 * Constructs a new instance of LoginHandler class with the specified 
	 * LocalLoginServiceImpl and AuthControl.<P>
	 * @param lls
	 * the LocalLoginServiceImpl instance
	 * @param authControl
	 * the AuthControl instance
	 */
	public LocalLoginHandler(LocalLoginServiceImpl lls, UserAuthControl authControl) {
		_localLoginService = lls;
		this._authControl =  authControl;
	}

	/** 
	 * Invoked when changed the user from general user to system administrator at user mode.<P>
	 * 
	 * This method notifies the department ID, department password, and department user type .<P>
	 * 
	 * Notification to each application is performed via LocalLoginService.<P>
	 * 
	 * When system administrator has not been set, the user type is always administrator even the user type in CLoginData is 
	 * general user. <P>
	 *  
	 * @param cLoginData
	 * the CLoginData instance
	 * @see com.canon.meap.csee.service.login.base.lls.LocalLoginServiceImpl#login
	 */
	public synchronized void loggedin(CLoginData cLoginData) {

		CCPCALoginData logindata = (CCPCALoginData) cLoginData;
		int userType = logindata.getUserType();
		if (_authControl.isSystemManagerAuth() == false) {
			userType = (int) CCPCALoginData.USER_TYPE_ADMIN;
		}
		_localLoginService.setLoginContext(
			logindata.getUserId(),
			logindata.getPassword(),
			userType);
	}

	/** 
	 * Invoked by AVS when a logout event occur.<P>
	 * 
	 * This method notifies the logout to each application via LocalLoginService.
	 * @see com.canon.meap.csee.service.login.base.lls.LocalLoginServiceImpl#logout
	 */
	public void loggedout() {
		if (_localLoginService.getLoginContext() != null) {
			_localLoginService.logout();
		}
	}

	/** 
	 * Invoked by AVS if the ID key is pressed when user is in the state of login.<P>
	 * 
	 * No process is done in this method.<P>
	 * 
	 */
	public void loggedoutWithIDKey() {
	}

	/** 
	 * Performs the authentication with the specified login user name and password.<P>
	 * 
	 * This method performs logout process first when user is login, and then performs the authentication.
	 * When authentication is success, this method notifies login event to each application.
	 * 
	 * The authentication is performed via authControl.<P>
	 * The notification to each application is performed via LocalLoginService.<P>
	 * 
	 * @param id
	 * the login user name
	 * @param pass
	 * the password
	 * @throws LoginException
	 * if user ID and password does not exist in account database
	 * @throws CpcaAuthFailException
	 * if failed the department authentication
	 * @see com.canon.meap.csee.service.login.base.lls.LocalLoginServiceImpl#login
	 * @see com.inepro.service.login.UserAuthControl#createLoginContext(String, String)
	 */
	public synchronized void loginForAdmin(String uid, String pass)
		throws LoginException {

		if (_localLoginService.getLoginContext() != null) {
			_localLoginService.logout();
		}

		/*
		 * Perform authentications to AuthControl class and get the LoginContextExt.
		 */
		LoginContextExt logincontext = _authControl.createLoginContextForAdmin(uid, pass);
		if (logincontext != null) {
			_localLoginService.login(logincontext);
		} else {
			throw new LoginException("LoginContext == null");
		}
		return;
	}
	
	
	public synchronized void login(CustomerInfo customerInfo, String uid)
			throws LoginException {

			if (_localLoginService.getLoginContext() != null) {
				_localLoginService.logout();
			}

			/*
			 * Perform authentications to AuthControl class and get the LoginContextExt.
			 */
			LoginContextExt logincontext = _authControl.createLoginContext(customerInfo, uid);

			if (logincontext != null) {
				_localLoginService.login(logincontext);
			} else {
				throw new LoginException("LoginContext == null");
			}

			return;
		}
}
