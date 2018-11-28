
package com.inepro.service.login.lls.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Panel;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import com.canon.meap.csee.service.login.base.lls.ui.UIBasePanel;
import com.canon.meap.csee.service.login.base.util.LoginProperties;
import com.canon.meap.csee.service.login.base.util.LoginSyslog;
import com.canon.meap.csee.service.login.base.util.Syslog;
import com.canon.meap.ctk.atk.CAlignment;
import com.canon.meap.ctk.atk.CImageButton;
import com.canon.meap.ctk.atk.CImagePanel;
import com.canon.meap.ctk.atk.CTextField;
import com.canon.meap.ctk.awt.CImageComponent;
import com.canon.meap.ctk.awt.CLabel;
import com.canon.meap.ctk.awt.CLabelButton;
import com.canon.meap.service.sis.event.CLocaleEvent;

/**
 * This class displays the local login panel and records the action on the panel.<P>
 * 
 * The input window for login user name and password is created in this panel.<P>
 * @version       $Revision: 1.6 $, $Date: 2006/03/10 07:36:08 $
 */
public final class LocalLoginPanel extends UIBasePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The based color of login panel.
	 */
	protected Color colorBase = new Color(187, 187, 170);
	/**
	 * The blue color used by dialog.
	 */
	protected Color colorDialogBlue = new Color(119, 119, 170);
	
	protected Color colorBlue = new Color(157, 157, 255);
	/**
	 * The white color used by login panel.
	 */
	protected Color colorWhite = new Color(255, 255, 255);
	/**
	 * The black color used by login panel.
	 */
	protected Color colorBlack = new Color(0, 0, 0);
	/**
	 * The gray color used by login panel.
	 */
	protected Color colorGray = new Color(85, 85, 85);

	/* 
	 * Instance of ActionListener class
	 *  
	 */
	private ActionListener _action = null;

	/* 
	 * Instance of LoginErrorPanel class
	 *  
	 */
	 private LoginErrorPanel _errorPanel = null;
	 
	 private CImageComponent _usrIconImage = null;
	 private CImageComponent _passIconImage = null;
	
	 private int _width = 0;

	 private int _height = 0;


	/*
	 * Resource file name
	 */
	private static final String NULL_STRING = "";
	
	private CTextField _idTextField = null;
	private CTextField _passTextField = null;

	private CLabelButton _loginButton = null;
	private CImageButton _loginImageButton = null;
	private CImageButton _backImageButton = null;
	private CLabel _userLabel1 = null;
	private CLabel _userLabel2 = null;
	private CLabel _titleLabel = null;
	private CLabel _notesLabel = null;
	/**
	 * Constructs a new LocalLoginPanel instance with the specified ActionListener.<P>
	 * 
	 * Via the ActionListener argument, action on panel can be delivered and processed at LLS.<P>
	 * 
	 * @param actionListener
	 *
	 */
	public LocalLoginPanel(ActionListener actionListener) {
		_action = actionListener;
	}

	/**
	 * Constructs a new LocalLoginPanel instance with the specified ActionListener.<P>
	 * 
	 * Via the ActionListener argument, action on panel can be delivered and processed at LLS.<P>
	 * 
	 * @param actionListener
	 * @param pro
	 * the LoginProperties instance
	 */
	public LocalLoginPanel(ActionListener actionListener, LoginProperties pro) {
		super(pro);
		_action = actionListener;
	}
	
	public LocalLoginPanel(ActionListener actionListener, LoginProperties pro, int width, int height) {
		super(pro);
		_action = actionListener;
		_width = width;
	    _height = height;
	}
	/**
	 * Creates the panel via layoutPanel() and setMessages() method.<P>
	 * 
	 * When applet of login application is loaded to system, this method should be invoked 
	 * explicitly by login application.<P>
	 * 
	 * Same process is done for error panel.<P>
	 *  
	 */
	public void init() {
		super.init();
		if (this._errorPanel != null) {
			_errorPanel.init();
		}
	}

	/**
	 * Invoked by SIS at CLocalListener interface when changing the setting of display language.<P>
	 * 
	 * This method obtains the specified locale from CLocaleEvent and displays the panel
	 * using the acquired locale. <P>
	 * 
	 * Same process is done for error panel.<P>
	 * 
	 * 
	 * @param e CLocaleEvent
	 * the event occurred when changing the setting of display language
	 */
	public void localeChanged(CLocaleEvent event) {
		super.localeChanged(event);
		_errorPanel.localeChanged(event);
	}

	/**
	 * Enables or disables the display of error panel with the specified error message according to the 
	 * value of visible flag.<P>
	 * 
	 * If the visible flag is true, enables the display of error panel.
	 * If the visible flag is false,disables the display of error panel.
	 * 
	 * The error process is done at error panel class.
	 * 
	 * @param errMsg
	 * the message to be displayed
	 * @param visible
	 * the flag indicates if display the error panel or not
	 */
	public void setErrorMessage(String errMsg, boolean visible) {
		_errorPanel.setErrorMessage(errMsg);
		setErrorPanelVisible(visible);
	}

	/**
	 * Changes the display of error panel according to the visible flag.<P>
	 * 
	 * If the visible flag is true, displays the error panel and disables the login button simultaneously.
	 * If the visible flag is false, disable the display of error panel and enables the login button simultaneously.<P>
	 * @param visible
	 * the flag indicates if display the error panel or not
	 */
	public void setErrorPanelVisible(boolean visible) {
		_errorPanel.setVisible(visible);
		_loginButton.setEnabled(!visible);
	}

	/**
	 * Configures the necessary components to login panel.<P>
	 * 
	 * This method creates the panel of login user name and password.
	 * In additional, the error panel displayed on login panel is also created. 
	 * The layout of components on error panel is done by error panel.
	 */
	protected void layoutPanel() {
	
		setLayout(null);
		if(_width !=0 && _height !=0)
		{
			setBounds(0, 0, _width, _height);
		}else{
			setBounds(0, 0, 640, 480);
		}
		setBackground(colorWhite);

		Panel leftPanel = new Panel();
		leftPanel.setLayout(null);
		leftPanel.setBounds(100,0,40,40);
		leftPanel.setBackground(Color.white);
		
		/* Title Panel */
        Panel titlePanel = new Panel();
        titlePanel.setLayout(null);
        titlePanel.setBounds(145,0,335,40);
        titlePanel.setBackground(Color.RED);
        add(titlePanel);
        add(leftPanel);
        
        _backImageButton = new CImageButton();
        _backImageButton.setBounds(0, 0, 40, 41);
        _backImageButton.addActionListener(_action);
		try{
			_backImageButton.setActionCommand("backButton");
			_backImageButton.setImage(getImage(_resources.getStringProperty("LLS_IMAGE_BACK_ICON")));
		}catch (PropertyVetoException propertyvetoexception) {
			LoginSyslog.append(
					Syslog.LLS_ERROR,
					Syslog.WARNING,
					"_backImageButton: layoutPanel : " + propertyvetoexception.toString());
		}
		leftPanel.add(_backImageButton);
//        titlePanel.add(makeCHLine(0, 0, 613, 1, colorWhite));
//        titlePanel.add(makeCVLine(0, 0, 1, 30,colorWhite));
//        titlePanel.add(makeCHLine(0, 29, 613, 1, colorBlack));
//        titlePanel.add(makeCVLine(612, 0, 1, 30, colorBlack));
        
        CLabel titlelbl = new CLabel();
        titlelbl.setBounds(48, 10, 60, 20);
        titlelbl.setFont(new Font("Dialog", 1, 18));
        titlelbl.setForeground(Color.WHITE);
        titlelbl.setText("Login");
        titlePanel.add(titlelbl);
        
		_errorPanel = new LoginErrorPanel(this._resources, _action);
		add(_errorPanel);
		_errorPanel.setVisible(false);
		
		/* Top Panel */
		CImagePanel panelTop = new CImagePanel();
		panelTop.setLayout(null);
		//12, 32, 613, 348
		panelTop.setBounds(12, 32, 613, 318);
		panelTop.setBackground(colorWhite);
		add(panelTop);

//		panelTop.add(makeCHLine(0, 0, 613, 1, colorWhite));
//		panelTop.add(makeCVLine(0, 0, 1, 348, colorWhite));
//		panelTop.add(makeCHLine(0, 367, 613, 1, colorBlack));
//		panelTop.add(makeCVLine(612, 0, 1, 348, colorBlack));

		/* Bottom Panel */
		Panel panelBottom = new Panel();
		panelBottom.setLayout(null);
		//12, 382, 613, 51
		panelBottom.setBounds(12, 352, 613, 51);
		panelBottom.setBackground(colorWhite);
		add(panelBottom);

//		panelBottom.add(makeCHLine(0, 0, 613, 1, colorWhite));
//		panelBottom.add(makeCVLine(0, 0, 1, 51, colorWhite));
//		panelBottom.add(makeCHLine(0, 50, 613, 1, colorBlack));
//		panelBottom.add(makeCVLine(612, 0, 1, 51, colorBlack));
		
		_titleLabel = new CLabel();
        _notesLabel = new CLabel();
        _titleLabel.setBounds((panelTop.getWidth() - 500) / 2, 40, 500, 40);
        _titleLabel.setFont(new Font("Dialog", 1, 18));
        _titleLabel.setBackground(colorWhite);
        _notesLabel.setBounds((panelTop.getWidth() - 500) / 2, 70, 500, 40);
        _notesLabel.setFont(new Font("Dialog", 1, 16));
        _notesLabel.setBackground(colorWhite);
        try {
        	_titleLabel.setHorizontalAlignment(CAlignment.CENTER);
        	_titleLabel.setVerticalAlignment(CAlignment.TOP);
            _notesLabel.setHorizontalAlignment(CAlignment.CENTER);
            _notesLabel.setVerticalAlignment(CAlignment.TOP);
        } catch (PropertyVetoException propertyvetoexception) {
            LoginSyslog
                    .append(Syslog.LLS_ERROR, Syslog.WARNING,
                            "Applet: layoutPanel : "
                                    + propertyvetoexception.toString());

        }
       panelTop.add(this._notesLabel);
       panelTop.add(this._titleLabel);
		
		_userLabel1 = new CLabel();
		_userLabel2 = new CLabel();
		_userLabel1.setBounds(((panelTop.getWidth() - 500) / 2) + 100, 109, 256, 16);
		_userLabel2.setBounds(((panelTop.getWidth() - 500) / 2) + 100, 200, 256, 16);
		_userLabel1.setFont(new Font("Dialog", 1, 16));
		_userLabel2.setFont(new Font("Dialog", 1, 16));
		_userLabel1.setForeground(Color.RED);
		_userLabel2.setForeground(Color.RED);
		_userLabel1.setBackground(colorWhite);
		_userLabel2.setBackground(colorWhite);
		
		try {
			_userLabel1.setHorizontalAlignment(CLabel.LEFT);
			_userLabel1.setVerticalAlignment(CLabel.CENTER);
			_userLabel2.setHorizontalAlignment(CLabel.LEFT);
			_userLabel2.setVerticalAlignment(CLabel.CENTER);
		} catch (PropertyVetoException propertyvetoexception4) {
			LoginSyslog.append(
				Syslog.LLS_ERROR,
				Syslog.WARNING,
				"Applet: layoutPanel : " + propertyvetoexception4.toString());
		}
		panelTop.add(_userLabel1);
		panelTop.add(_userLabel2);

		_idTextField = new CTextField();
		_usrIconImage = new CImageComponent();
		_usrIconImage.setBounds(((panelTop.getWidth() - 500) / 2) + 50, 128, 48, 40);
		try{
			_usrIconImage.setImage(getImage(_resources.getStringProperty("LLS_IMAGE_USER_ICON")));
		}catch (PropertyVetoException propertyvetoexception) {
			LoginSyslog.append(
					Syslog.LLS_ERROR,
					Syslog.WARNING,
					"LocalLoinPanel: layoutPanel : " + propertyvetoexception.toString());
		}
		_idTextField.setBounds(((panelTop.getWidth() - 500) / 2) + 100, 128, 296, 40);
		_idTextField.setFont(new Font("Dialog", 1, 16));
		_idTextField.addActionListener(_action);
		_idTextField.setName("user");
		_idTextField.setVKBTitle("Inepro Login Application");
		_idTextField.setVisible(true);
		//_idTextField.add(makeCHLine(0, 39, 296, 1, Color.red));
		panelTop.add(_idTextField);
		panelTop.add(_usrIconImage);

		_passTextField = new CTextField();
		_passIconImage = new CImageComponent();
		_passIconImage.setBounds(((panelTop.getWidth() - 500) / 2 + 58) , 219, 40, 40);
		try{
			_passIconImage.setImage(getImage(_resources.getStringProperty("LLS_IMAGE_PASS_ICON")));
		}catch (PropertyVetoException propertyvetoexception) {
			LoginSyslog.append(
					Syslog.LLS_ERROR,
					Syslog.WARNING,
					"LocalLoinPanel: layoutPanel : " + propertyvetoexception.toString());
		}
		_passTextField.setBounds(((panelTop.getWidth() - 500) / 2) + 100, 219, 296, 40);
		_passTextField.setFont(new Font("Dialog", 1, 16));
		_passTextField.setName("password");
		_passTextField.setVKBTitle("Inepro Login Application");
		_passTextField.setEchoChar('*');
		//_passTextField.add(makeCHLine(0, 39, 296, 1, Color.red));
		panelTop.add(_passTextField);
		panelTop.add(_passIconImage);

//		_loginButton = new CLabelButton();
		_loginImageButton = new CImageButton();
		_loginImageButton.setBounds(417, 0, 171, 60);
		_loginImageButton.addActionListener(_action);
		try{
			_loginImageButton.setActionCommand("loginButton");
			_loginImageButton.setImage(getImage(_resources.getStringProperty("LLS_IMAGE_LOGIN_ICON")));
		}catch (PropertyVetoException propertyvetoexception) {
			LoginSyslog.append(
					Syslog.LLS_ERROR,
					Syslog.WARNING,
					"LocalLoinPanel: layoutPanel : " + propertyvetoexception.toString());
		}

//		try {
//			_loginButton.setHorizontalAlignment(CLabelButton.CENTER);
//			_loginButton.setArrowStyle(CLabelButton.ARROW_RETURN_STYLE);
//		} catch (PropertyVetoException e1) {
//			LoginSyslog.append(
//				Syslog.LLS_ERROR,
//				Syslog.WARNING,
//				"LocalLoinPanel: layoutPanel : " + e1.toString());
//		}
//		_loginButton.setBounds(431, 8, 171, 34);
//		_loginButton.setFont(new Font("Dialog", 1, 16));
//		_loginButton.setBackground(Color.lightGray);
//		_loginButton.addActionListener(_action);
//		try {
//			_loginButton.setActionCommand("loginButton");
//		} catch (PropertyVetoException propertyvetoexception6) {
//			LoginSyslog.append(
//				Syslog.LLS_ERROR,
//				Syslog.WARNING,
//				"LocalLoinPanel: layoutPanel : " + propertyvetoexception6.toString());
//		}
		panelBottom.add(_loginImageButton);
		//panelBottom.add(_loginButton);

	}

	/**
	 * 
	 * Sets the message for displayed on each components.<P>
	 * 
	 * The messages for login user name and password is set at here.<P>
	 */
	public void setMessages() {
		_titleLabel.setText(_resources.getStringProperty("LLS_LABEL_TITLE"));
        _notesLabel.setText(_resources.getStringProperty("LLS_LABEL_NOTE"));
		_userLabel1.setText(_resources.getStringProperty("LLS_LABEL_USERNAME"));
		_userLabel2.setText(_resources.getStringProperty("LLS_LABEL_PASSWORD"));
		//_loginButton.setLabel(_resources.getStringProperty("LLS_BUTTON_LOGIN"));
	}

	/**
	 * Clears the input field of login user name and password.<P>
	 */
	public void clearTextField() {
		_idTextField.setText(NULL_STRING);
		_passTextField.setText(NULL_STRING);
	}

	/**
	 * Clears the input field of passowrd.<P>
	 */
	public void clearPassTextField() {
		_passTextField.setText(NULL_STRING);
	}

	/**
	 * Gets the inputted login user name.<P>
	 * @return
	 * the login user name
	 */
	public String getIdTextField() {
		return _idTextField.getText();
	}

	/**
	 * Gets the input of password.<P>
	 * @return
	 * the password
	 */
	public String getPassTextField() {
		return _passTextField.getText();
	}

	
	public void notifyLoginSuccess() {
		clearTextField();
	}

}
