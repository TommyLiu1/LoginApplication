package com.inepro.service.login;

import java.util.Vector;

import com.canon.meap.csee.service.login.base.NiRLoginContext;
import com.canon.meap.csee.service.login.base.acq.ACQAccessControlValue;
import com.canon.meap.csee.service.login.base.acq.ACQAdministratorACV;
import com.canon.meap.csee.service.login.base.acq.ACQDeviceAdministratorACV;
import com.canon.meap.csee.service.login.base.acq.ACQGeneralUserACV;
import com.canon.meap.csee.service.login.base.acq.ACQNetWorkAdministratorACV;
import com.inepro.service.login.group.GroupAttributeContextExt;
import com.inepro.service.login.group.GroupDataImpl;
import com.canon.meap.security.acq.ACQException;
import com.canon.meap.security.acq.ACQLoginContext;
import com.canon.meap.security.acq.act.ACTManager;
import com.canon.meap.security.acq.login.ACQLoginContextExt;

/**
 * Implementation class for the Non AMS mode LoginContextExt interface.<P>
 * 
 * Return LoginContext in this format for MEAPSpecVer 33 or later devices.<P>
 * 
 */
public class LoginContextImplNiR extends LoginContextImpl implements
        NiRLoginContext, GroupAttributeContextExt {

    //=========================================================================
    // construtors
    //=========================================================================

    /**
     * the role of user
     */
    private String _role = null;

    /**
     * NetworkAdmin
     */
    public static final String STR_NETWORK_ADMIN_ROLE = "NetWorkAdmin";

    /**
     * DeviceAdmin
     */
    public static final String STR_DEVICE_ADMIN_ROLE = "DeviceAdmin";

    /**
     * Administrator
     */
    public static final String STR_ADMIN_ROLE = "Administrator";

    /**
     * GeneralUser
     */
    public static final String STR_GENERAL_ROLE = "GeneralUser";

    /**
     * ACQLoginContextExt
     */
    private ACQLoginContextExt cashedACQLoginContext = null;

    /**
     * ACQAccessControlValue
     */
    private ACQAccessControlValue accessControlValue = null;

    private Vector groupList = new Vector();

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
     * @param role
     * the role  of user
     */
    public LoginContextImplNiR(String userId, String userType,
            String userName, String mailAddress, String cpcaUid,
            String cpcaPwd, String cpcaUserType, String role, String groupname) {

        super(userId, userType, userName, mailAddress, cpcaUid, cpcaPwd,
                cpcaUserType, groupname);

        if (STR_ADMIN_ROLE.equalsIgnoreCase(role)) {
            this.accessControlValue = new ACQAdministratorACV();
        } else if (STR_GENERAL_ROLE.equalsIgnoreCase(role)) {
            this.accessControlValue = new ACQGeneralUserACV();
        } else if (STR_NETWORK_ADMIN_ROLE.equalsIgnoreCase(role)) {
            this.accessControlValue = new ACQNetWorkAdministratorACV();
        } else if (STR_DEVICE_ADMIN_ROLE.equalsIgnoreCase(role)) {
            this.accessControlValue = new ACQDeviceAdministratorACV();
        } else {
            throw new IllegalArgumentException("role");
        }
        this._role = role;

    }

    /**
     * Not implemented.<P>
     * 
     * @throws ACQException
     * Not supported by the sample application.
     */
    public void updateDynamicACV() throws ACQException {
    }

    /**
     * Not implemented. <P>
     * 
     * @return NULL
     * @throws ACQException
     * Not supported by the sample application.
     */
    public ACTManager getACTManager() throws ACQException {
        return null;
    }

    /**
     * Not implemented.<P>
     * Be sure to implement to return null value. <P>
     * 
     * @param attribute
     * Path expression for ACV punctuated with slash "/" <BR>
     * @return NULL
     * @throws ACQException
     * Not supported by the sample application.
     */
    public Vector getDynamicAccessControlValue(String attribute)
            throws ACQException {
        return null;
    }

    /**
     * Gets the Group List
     * 
     * @return 
     * Group List
     */
    public Vector getGroupList() {
        return this.groupList;
    }

    /**
     * Returns ACV filled in with attribute values corresponding to ACT schema specified as parameter. 
     * 
     * @param str
     * ACT schema
     * @return
     * attribute value
     */
	public Vector getStaticAccessControlValue(String str) {

        if (this.isValid() == false) {
            return null;
        }

        if (this.cashedACQLoginContext != null) {
            return this.cashedACQLoginContext.getStaticAccessControlValue(str);
        }

        return this.accessControlValue.getAccessControlValue(str);
    }

    /**
     * Returns list vector containing user roles. <p>
     * 
     * @return
     * List vector containing user roles
     */
    public Vector getRoleList() {

        if (this.isValid() == false) {
            return null;
        }

        if (this.cashedACQLoginContext != null) {
            return this.cashedACQLoginContext.getRoleList();
        }

        /*
         * Returns vetor containing the specified roles.
         */
        Vector<String> v = new Vector<String>();
        v.add(this._role);
        return v;
    }

    /**
     * Returns user base role. <p>
     * 
     * @return
     * User base role
     */
    public String getBaseRole() {

        if (this.isValid() == false) {
            return null;
        }

        if (this.cashedACQLoginContext != null) {
            return this.cashedACQLoginContext.getBaseRole();
        }

        if (this.isUserInRole(STR_ADMINISTRATOR)) {
            return ACQLoginContext.USER_ROLE_ADMIN;
        } else {
            return ACQLoginContext.USER_ROLE_GENERAL;
        }
    }

    /**
     * This method updates the ACQLoginContextExt.
     * 
     * @param contextExt ACQLoginContextExt
     */
    public void update(ACQLoginContextExt contextExt) {
        this.cashedACQLoginContext = contextExt;

    }

    /**
     * This method updates the CLS flag.
     * 
     * @param clsEnabled
     * flag indicating whether to enable CLS authentication
     * @param loginCategory
     * flag of login category.
     */
    public void updateCLSFlg(int clsEnabled, int loginCategory) {
        this._clsEnabled = clsEnabled;
        this._loginCategory = loginCategory;
    }
    
    /**
     * return the attributename of Group.<P>
     *
     * return "sdlgroup".
     * @return sdlgroup
     * 
     */
    public String[] getGroupAttributeNames() {
        return new String[] { "sdlgroup" };
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
     * <tr><td>GroupName</td><td>sdlgroup</td></tr>
     * </table><P>
     * 
     * @param name
     * the attribute name
     * @return
     * returns attribute value. If the specified attributes information does not exist, returns null.
     * @see com.canon.meap.csee.service.login.base.LoginContextExt#getUserAttribute
     */
    public String[] getUserAttributeExt(String key) {

        String attribute = this.getUserAttribute(key);

        if (attribute == null) {
            return null;
        }
        return new String[] { attribute };

    }

    /**
     * Sets the GroupDataImpl.
     * 
     * @param groupDataImpl
     * the GroupDataImpl.
     */
    public void addGroup(GroupDataImpl groupDataImpl) {
        this.groupList.add(groupDataImpl);
    }
}
