//****************************************************************************
//
// Copyright CANON INC. 2004
// All Rights Reserved
//
// SampleAuthControl.java
//
// MEAP Login Application SDK
//
// Version 1.00
//
//****************************************************************************
package com.inepro.service.login;

import org.osgi.framework.BundleContext;

import com.canon.meap.cpca.data.Symbol;
import com.canon.meap.csee.service.login.base.AuthControl;
import com.canon.meap.csee.service.login.base.LoginContextExt;
import com.canon.meap.csee.service.login.base.cpca.CpcaAuthFailException;
import com.canon.meap.csee.service.login.base.util.LoginException;
import com.canon.meap.csee.service.login.base.util.LoginProperties;
import com.inepro.service.login.data.UserDataEntity;
import com.inepro.service.login.model.CustomerInfo;
import com.inepro.service.login.group.GroupAttributeContextExt;
import com.inepro.service.login.group.GroupInformation;




public class UserAuthControl extends AuthControl {

	//=========================================================================
	// instance fields - attributes
	//=========================================================================


	/*
	 * Instance of LoginProperties class
	 */
	private LoginProperties _resource = null;

	//=========================================================================
	// constructors
	//=========================================================================

	/**
	 * Constructs a new stance of UserAuthControl class.
	 * @param bundleContext
	 * the BundleContext instance of login application
	 */
	public UserAuthControl(BundleContext bundleContext, LoginProperties pro) {
		super(bundleContext.getBundle());
		_resource = pro;
	}

	//=========================================================================
	// SampleAuthControl Method
	//=========================================================================

	/**
	 * Performs user authentications and creates a new instance of SampleLoginContextImpl 
	 * when authentication is passed.
	 * @param username
	 * the name of login user
	 * @param password
	 * 
	 * the password
	 * @return
	 * Returns the instance of LoginContextImpl class when user authentication is passed.
	 * @throws LoginException
	 * if fails in login
	 * @see com.canon.meap.csee.service.login.base.AuthControl#getCpcaUserType(String, String)
	 */
	public synchronized LoginContextExt createLoginContext(CustomerInfo customerInfo, String uid)
		throws LoginException {

		UserDataEntity userdata = UserDataEntity.getInstance();
		try{
			if(customerInfo != null)
			{
				String userType = Long.toString(Symbol.id_val_user_type_administrator);
				if(! customerInfo.isAdmin)
				{
					userType =  Long.toString(Symbol.id_val_user_type_generic);
				}
				userdata.setDefaultUserData(uid,userType);
			}else{
				throw new LoginException(_resource.getStringProperty("LOGIN_ERR_INVALID_USER_NAME"));
			}
			
		}catch(Exception e)
		{
			throw new LoginException(e.getMessage());
		}
		/*
		 * Create  a new instance of LoginContextImpl class and return it.
		 */
		String cpcaUserType;
        try {
            cpcaUserType = Short.toString(this.getCpcaUserType(
                    userdata.getCpcaUid(), userdata.getCpcaPwd()));
        } catch (CpcaAuthFailException e) {
            throw new CpcaAuthFailException(
            		_resource.getStringProperty("LOGIN_ERR_CPCA_LOGIN_FAILED"));
        }
         LoginContextExt context = new LoginContextImplNiR(
             		userdata.getLoginUserId(), 
             		userdata.getUserType(),
             		uid,
     				userdata.getMailAddress(), 
     				userdata.getCpcaUid(), 
     				userdata.getCpcaPwd(), 
     				cpcaUserType,
     				userdata.getRole(),
     				userdata.getGroupName());;

     				return GroupInformation
     		                .getGroupData((GroupAttributeContextExt) context);	 
       
	}
	
	public synchronized LoginContextExt createLoginContextForAdmin(String username, String password) throws LoginException
	{
		UserDataEntity userdata = UserDataEntity.getInstance();
		userdata.setDefaultUserData("admin",Long.toString(Symbol.id_val_user_type_administrator));
		
		/*
		 * Create  a new instance of LoginContextImpl class and return it.
		 */
		String cpcaUserType;
        try {
            cpcaUserType = Short.toString(this.getCpcaUserType(
                    userdata.getCpcaUid(), userdata.getCpcaPwd()));
        } catch (CpcaAuthFailException e) {
            throw new CpcaAuthFailException(
            		_resource.getStringProperty("LOGIN_ERR_CPCA_LOGIN_FAILED"));
        }
      
    	   LoginContextExt context = new LoginContextImplNiR(
           		userdata.getLoginUserId(), 
           		userdata.getUserType(),
           		username,
   				userdata.getMailAddress(), 
   				userdata.getCpcaUid(), 
   				userdata.getCpcaPwd(), 
   				cpcaUserType,
   				userdata.getRole(),
   				userdata.getGroupName());

           return GroupInformation
                   .getGroupData((GroupAttributeContextExt) context);
    		   
       
	}
	/**
	 * Performs user authentications and creates a new instance of SampleLoginContextImpl 
	 * when authentication is passed.
	 * @param username
	 * the name of login user
	 * @param password
	 * the password
	 * @param domain
	 * @return
	 * Returns the instance of SampleLoginContextImpl class when user authentication is passed.
	 * @throws LoginException
	 * if fails in login
	 * @see com.canon.meap.csee.service.login.base.AuthControl#getCpcaUserType(String, String)
	 */
	public LoginContextExt createLoginContext(
		String username,
		String password,
		String domain)
		throws LoginException {
		return this.createLoginContext(username, password);
	}
	
	public LoginContextExt createLoginContext(
			String username,
			String password) throws LoginException
	{
		UserDataEntity userdata = UserDataEntity.getInstance();
		userdata.setDefaultUserData(username,Long.toString(Symbol.id_val_user_type_administrator));
		
		/*
		 * Create  a new instance of LoginContextImpl class and return it.
		 */
		String cpcaUserType;
        try {
            cpcaUserType = Short.toString(this.getCpcaUserType(
                    userdata.getCpcaUid(), userdata.getCpcaPwd()));
        } catch (CpcaAuthFailException e) {
            throw new CpcaAuthFailException(
            		_resource.getStringProperty("LOGIN_ERR_CPCA_LOGIN_FAILED"));
        }
		
        return new RemoteLoginContextImpl(
    			userdata.getLoginUserId(),
    			userdata.getPassword(),
    			userdata.getUserType(),
    			userdata.getUserName(),
    			userdata.getMailAddress(),
    			userdata.getCpcaUid(),
    			userdata.getCpcaPwd(),
    			cpcaUserType);
	}

}
