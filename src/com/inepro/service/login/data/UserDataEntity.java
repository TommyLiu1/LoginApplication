
package com.inepro.service.login.data;

import com.canon.meap.cpca.data.Symbol;

/**
 * This class holds record information of SimpleDataBase class.
 *
 * @version       $Revision: 1.2 $, $Date: 2018/11/15 05:13:54 $
 */
public final class UserDataEntity {
	
	private static UserDataEntity _instance = null;

	//=========================================================================
	// instance fields
	//=========================================================================
	private String _loginUserId = null;
	private String _password = null;
	private String _cpcaUid = null;
	private String _cpcaPwd = null;
	private String _userType = null;
	private String _userName = null;
	private String _mailAddress = null;
    private String _role = null;
    private String _groupName = null;

	//=========================================================================
	// construtors
	//=========================================================================
	/**
	 * Constructs a new instance of SimpleDataBaseEntity class.
	 */
	public UserDataEntity() {
	}

	/**
	 * Gets the UserDataEntity instance held in the class.<P>
	 *
	 * @return
	 * the instance of SimpleDataBase
	 *
	 */
	public static UserDataEntity getInstance() {
		if (_instance == null) {
			_instance = new UserDataEntity();
		}
		return _instance;
	}

	//=========================================================================
	// SimpleDataBaseEntity methods
	//=========================================================================
	/**
	 * Sets the login user name.
	 * @param loginUserId
	 * the login user name
	 */
	public void setLoginUserId(String loginUserId) {
		this._loginUserId = loginUserId;
	}

	/**
	 * Sets the password.
	 * @param password
	 * the password
	 */
	public void setPassword(String password) {
		this._password = password;
	}

	/**
	 * Sets the CPCA department ID.
	 * @param cpcaUid
	 * the CPCA department ID
	 */
	public void setCpcaUid(String cpcaUid) {
		this._cpcaUid = cpcaUid;
	}

	/**
	 * Sets the password of CPCA department.
	 * @param cpcaPwd
	 * the password of CPCA department
	 */
	public void setCpcaPwd(String cpcaPwd) {
		this._cpcaPwd = cpcaPwd;
	}

	/**
	 * Sets the user type.
	 * @param userType
	 * the user type
	 */
	public void setUserType(String userType) {
		this._userType = userType;
	}

	/**
	 * Sets the display name.
	 * @param userName
	 * the display name
	 */
	public void setUserName(String userName) {
		this._userName = userName;
	}

	/**
	 * Sets the email address.
	 * @param mailAddress
	 * the email address
	 */
	public void setMailAddress(String mailAddress) {
		this._mailAddress = mailAddress;
	}

	/**
	 * Gets the login user name.
	 * @return
	 * the login user name
	 */
	public String getLoginUserId() {
		return _loginUserId;
	}

	/**
	 * Gets the password.
	 * @return
	 * the password
	 */
	public String getPassword() {
		if (_password == null) {
			return "";
		}
		return _password;
	}

	/**
	 * Gets the CPCA department ID.<P>
	 *
	 * This method returns 0 when the CPCA department ID is null.
	 * @return
	 * the CPCA department ID
	 */
	public String getCpcaUid() {
		if (_cpcaUid == null) {
			return "0";
		}
		return _cpcaUid;
	}

	/**
	 * Gets the password of CPCA department.<P>
	 *
	 * This method returns 0 when the password of CPCA department is null.
	 * @return
	 * the password of CPCA department
	 */
	public String getCpcaPwd() {
		if (_cpcaPwd == null) {
			return "0";
		}
		return _cpcaPwd;
	}

	/**
	 * Gets the user type.
	 *
	 * This method returns 1(general user) when the user type is null.
	 * @return
	 * the user type
	 */
	public String getUserType() {
		if (_userType == null) {
			return "1";
		}
		return _userType;
	}

	/**
	 * Gets the display name.
	 * @return
	 * the display name
	 */
	public String getUserName() {
		if (_userName == null) {
			return "";
		}
		return _userName;
	}

	/**
	 * Gets the email address.
	 * @return
	 * the email address
	 */
	public String getMailAddress() {
		if (_mailAddress == null) {
			return "";
		}
		return _mailAddress;
	}
	
	/**
     * Gets the role.
     * @return
     * the role
     */
    public String getRole() {
        if (this._role == null) {
            return "";
        }
        return this._role;
    }

    /**
     * Sets the role.
     * @param role
     * the role
     */
    public void setRole(String role) {
        this._role = role;
    }
    
    /**
     * Gets the groupName.
     * @return
     * the groupName
     */
    public String getGroupName() {
        if (this._groupName == null) {
            return "";
        }
        return this._groupName;
    }

    /**
     * Sets the groupName.
     * @param role
     * the groupName
     */
    public void setGroupName(String groupName) {
        this._groupName = groupName;
    }
	
	public void setDefaultUserData(String id, String userType)
	{
		if(Long.toString(Symbol.id_val_user_type_administrator).equals(userType))
		{
			setLoginUserId(id);
			setPassword("password");
			setUserType(userType);
			setUserName("administrator");
			setCpcaUid("1");
			setCpcaPwd("1");
			setMailAddress("admin@domainname");
			setRole("Administrator");
		    setGroupName("Administrator");
			
		}
		if(Long.toString(Symbol.id_val_user_type_generic).equals(userType))
		{
			setLoginUserId(id);
			setPassword("password");
			setUserType(userType);
			setUserName("generaluser");
			setCpcaUid("2");
			setCpcaPwd("2");
			setMailAddress("general@domainname");
			setRole("GeneralUser");
		    setGroupName("GeneralUser");
		}
	}
}
