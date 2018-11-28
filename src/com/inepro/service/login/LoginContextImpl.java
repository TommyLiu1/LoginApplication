package com.inepro.service.login;

import com.canon.meap.cpca.data.Symbol;
import com.canon.meap.csee.service.login.base.LoginContextExt;
import com.canon.meap.service.bcs.BillingCode;
import com.canon.meap.service.login.event.UserEventExt;

/**
 * Implementation class for the Non AMS mode LoginContextExt interface.
 * 
 */
public class LoginContextImpl implements LoginContextExt {

    //=========================================================================
    // constants
    //=========================================================================

    /**
     * general user
     */
    protected static final String STR_GENERAL_USER = "general user";

    /**
     * administrator
     */
    protected static final String STR_ADMINISTRATOR = "administrator";

    /**
     * Constant indicating the user type of general user
     */
    protected static final String ID_GENERAL_USER = String
            .valueOf(Symbol.id_val_user_type_generic);

    /**
     * Constant indicating the user type of Administrator
     */
    protected static final String ID_ADMINISTRATOR = String
            .valueOf(Symbol.id_val_user_type_administrator);

    /**
     * Constant of a valid state of SampleLoginContextImpl instance
     */
    protected static final int VALID = 0;

    /**
     * Constant of an invalid state of SampleLoginContextImpl instance
     */
    protected static final int INVALIDATED = 1;

    //=========================================================================
    // instance fields - attributes
    //=========================================================================

    /**
     * State flag of LoginContextImpl instance
     */
    protected int state = VALID;

    /**
     * the login user name
     */
    protected String _userId = null;

    /**
     * the user type
     */
    protected String _userType = null;

    /**
     * the display name
     */
    protected String _userName = null;

    /**
     * the email address
     */
    protected String _mailAddress = null;

    /**
     * the department ID of CPCA
     */
    protected String _cpcaUid = null;

    /**
     * the password of CPCA
     */
    protected String _cpcaPwd = null;

    /**
     * the user type of CPCA
     */
    protected String _cpcaUserType = null;

    /**
     * the access code
     */
    protected String _accessCode = null;
    
    
    protected String _password = null;

    /**
     * LoginContextExt object that holds user information
     */
    protected LoginContextExt cashedloginContextExt = null;

    /**
     * flag indicating whether to enable CLS authentication
     */
    protected int _clsEnabled = UserEventExt.CONTEXTUAL_LOGIN_SERVICE_DISABLED;

    /**
     * flag of login category
     */
    protected int _loginCategory = UserEventExt.LOGIN_CATEGORY_MANUAL;

    /**
     * groupName
     */
    protected String _groupname = null;

    //=========================================================================
    // construtors
    //=========================================================================

    /**
     * Constructs a new instance of SampleLoginContextImpl instance.<P>
     * The information from arguments are saved as user information.<P>
     * 
     * @param userId
     * the user ID
     * @param userType
     * the user type
     * @param userName
     * the display name
     * @param mailAddress
     * the email address
     * @param cpcaUid
     * the department ID of CPCA
     * @param cpcaPwd
     * the password of CPCA
     * @param cpcaUserType
     * the user type of CPCA
     */
    public LoginContextImpl(String userId, String userType,
            String userName, String mailAddress, String cpcaUid,
            String cpcaPwd, String cpcaUserType, String groupName) {
        this._userId = userId;
        this._userType = userType;
        this._userName = userName;
        this._mailAddress = mailAddress;
        this._cpcaUid = cpcaUid;
        this._cpcaPwd = cpcaPwd;
        this._cpcaUserType = cpcaUserType;
        this._groupname = groupName;
    }
    
    
    public LoginContextImpl(
    		String userId,
    		String password,
    		String userType,
    		String userName,
    		String mailAddress,
    		String cpcaUid,
    		String cpcaPwd,
    		String cpcaUserType,
    		String accessCode) {
    		this._userId = userId;
    		this._password = password;
    		this._userType = userType;
    		this._userName = userName;
    		this._mailAddress = mailAddress;
    		this._cpcaUid = cpcaUid;
    		this._cpcaPwd = cpcaPwd;
    		this._cpcaUserType = cpcaUserType;
    		this._accessCode = accessCode;
    	}

    /**
     * Gets the CPCA department ID held in the instance.
     * 
     * @return
     * the CPCA department ID
     * @see com.canon.meap.csee.service.login.base.LoginContextExt#getCpcaUserId
     */
    public String getCpcaUserId() {

        if (this.isValid() == false) {
            return null;
        }

        if (this.cashedloginContextExt != null) {
            return this.cashedloginContextExt.getCpcaUserId();
        }

        return this._cpcaUid;
    }

    /**
     * Gets the CPCA password held in the instance.
     * 
     * @return
     * the CPCA password
     * @see com.canon.meap.csee.service.login.base.LoginContextExt#getCpcaPassword
     */
    public String getCpcaPassword() {

        if (this.isValid() == false) {
            return null;
        }
        if (this.cashedloginContextExt != null) {
            return this.cashedloginContextExt.getCpcaPassword();
        }

        return this._cpcaPwd;
    }

    /**
     * Gets the display name held in the instance.
     * 
     * @return
     * the display name
     * @see com.canon.meap.csee.service.login.base.LoginContextExt#getUserName
     */
    public String getUserName() {

        if (this.isValid() == false) {
            return null;
        }
        if (this.cashedloginContextExt != null) {
            return this.cashedloginContextExt.getUserName();
        }

        return this._userName;
    }

    /**
     * Gets the email address held in the instance.
     * 
     * @return
     * the email address
     * @see com.canon.meap.csee.service.login.base.LoginContextExt#getMailAddress
     */
    public String getMailAddress() {

        if (this.isValid() == false) {
            return null;
        }
        if (this.cashedloginContextExt != null) {
            return this.cashedloginContextExt.getMailAddress();
        }

        return this._mailAddress;
    }

    /**
     * Gets the CPCA user type held in the instance.
     * 
     * @return
     * the CPCA user type
     * @see com.canon.meap.csee.service.login.base.LoginContextExt#getCpcaUserType
     */
    public String getCpcaUserType() {

        if (this.isValid() == false) {
            return null;
        }
        if (this.cashedloginContextExt != null) {
            return this.cashedloginContextExt.getCpcaUserType();
        }

        return this._cpcaUserType;
    }

    /**
     * Checks the user type held in the instance.
     * 
     * @param role
     * a string represents the specified user type.<P>
     *
     * "administrator" represents  administrator,"general user" represents general user.
     * @return
     * returns true when the user type in user information matches the specified user type.
     * @see com.canon.meap.csee.service.login.base.LoginContextExt#isUserInRole
     */
    public boolean isUserInRole(String role) {

        if (this.isValid() == false) {
            return false;
        }
        if (this.cashedloginContextExt != null) {
            return this.cashedloginContextExt.isUserInRole(role);
        }

        if (STR_GENERAL_USER.equals(role)
                && ID_GENERAL_USER.equals(this._userType)) {
            return true;
        }

        if (STR_ADMINISTRATOR.equals(role)
                && ID_ADMINISTRATOR.equals(this._userType)) {
            return true;
        }
        return false;
    }

    /**
     * Checks the validation of user information held in the instance.
     * 
     * @return
     * returns true when the user inoformation in the instance is valid.
     * @see com.canon.meap.csee.service.login.base.LoginContextExt#isValid
     */
    public boolean isValid() {

        if (this.cashedloginContextExt != null) {
            return this.cashedloginContextExt.isValid();
        }

        return (this.state == VALID);
    }

    /**
     * Invalidates the implementation class of SampleLoginContextImpl.<P>
     * 
     * @see com.canon.meap.csee.service.login.base.LoginContextExt#invalidate
     */
    public void invalidate() {

        if (this.cashedloginContextExt != null) {
            this.cashedloginContextExt.invalidate();
            this.state = INVALIDATED;
            return;
        }

        this.state = INVALIDATED;
    }

    /**
     * Gets the user attribute of the user being in login.<P>
     *
     * Attributes supported in this class are listed below:<BR>
     * <table border>
     * <tr><th>Attribute </th><th>string specified in attributeName</th></tr>
     * <tr><td>Distinguished Name<br>string for distinguishing the user<br>E.g. "uid=loginusername"<td>dn</td></tr>
     * <tr><td>Login user Name</td><td>uid</td></tr>
     * <tr><td>Department ID</td><td>canonUid</td></tr>
     * <tr><td>Display Name</td><td>cn</td></tr>
     * <tr><td>Email Address</td><td>mail</td></tr>
     * <tr><td>flag of clsEnabled</td><td>clsEnabled</td></tr>
     * <tr><td>flag of login category</td><td>loginCategory</td></tr>
     * </table><P>
     * 
     * @param name
     * the attribute name
     * @return
     * returns attribute value. If the specified attributes information does not exist, returns null.
     * @see com.canon.meap.csee.service.login.base.LoginContextExt#getUserAttribute
     */
    public String getUserAttribute(String name) {

        if (this.isValid() == false) {
            return null;
        }
        if (this.cashedloginContextExt != null) {
            return this.cashedloginContextExt.getUserAttribute(name);
        }

        String result = null;
        try {
            if ("dn".equals(name)) {
                result = "uid=" + this._userId;
            } else if ("uid".equals(name)) {
                result = this._userId;
            } else if ("dc".equals(name)) {
                return "";
            } else if ("canonUid".equals(name)) {
                result = this.convert7Length();
            } else if ("cn".equals(name)) {
                result = this._userName;
            } else if ("mail".equals(name)) {
                result = this._mailAddress;
            } else if ("clsEnabled".equals(name)) {
                result = Integer.toString(this._clsEnabled);
            } else if ("loginCategory".equals(name)) {
                result = Integer.toString(this._loginCategory);
            } else if ("sdlgroup".equals(name)) {
                result = this._groupname;
            } else {
                return null;

            }
        } catch (Exception exception) {
            return null;
        }
        if (result == null) {
            return "";
        }
        return result;
    }

    /**
     * Changes the department ID, department password and user type information of CPCA held in this instance.<P>
     *
     * At LoginApplet class which implements the LoginService interface in LLS, when loggedin() method is called,
     * this method provides the ways to update the LoginContext held in LLS.
     * 
     * @param uid
     * the department ID of CPCA
     * @param pwd
     * the department password of CPCA
     * @param userType
     * the user type of CPCA
     *
     * @see com.canon.meap.csee.service.login.base.LoginContextExt#change(long, long, int)
     */
    public void change(long uid, long pwd, int userType) {
        this._cpcaUid = Long.toString(uid);
        this._cpcaPwd = Long.toString(pwd);
        this._cpcaUserType = Integer.toString(userType);
    }

    /**
     * Returns a 7 digits string represents CPCA department ID, fills the string with
     * 0 in the header when the length is less than 7.
     * 
     * @return String
     * a 7 digits string represents CPCA department ID
     */
    private String convert7Length() {
        if (this._cpcaUid == null) {
            return null;
        }
        StringBuffer result = new StringBuffer(7 + this._cpcaUid.length());
        result.append("0000000");
        result.append(this._cpcaUid);
        return result.substring(result.length() - 7);
    }

    /**
     * Sample login application does not support this method.
     * 
     * @param billingCode
     * Not used.
     */
    public void setBillingCode(BillingCode billingCode) {
        //Not used.
    }

    /**
     * Gets the access code which has been held in this class.<P>
     * 
     * @return
     * the access code
     */
    public String getAccessCode() {
        if (this.isValid() == false) {
            return null;
        }
        if (this.cashedloginContextExt != null) {
            return this.cashedloginContextExt.getAccessCode();
        }

        return this._accessCode;
    }

    /**
     * Stores the access code passed into this class.<P>
     * 
     * @param accessCode
     * the access code
     */
    public void setAccessCode(String accessCode) {

        if (this.cashedloginContextExt != null) {
            this.cashedloginContextExt.setAccessCode(accessCode);
            return;
        }

        this._accessCode = accessCode;
    }

    /**
     * This method updates the LoginContextExt.
     * 
     * @param loginContextExt LoginContextExt
     */
    protected void updateLoginContextExt(LoginContextExt loginContextExt) {
        this.cashedloginContextExt = loginContextExt;
    }

}

