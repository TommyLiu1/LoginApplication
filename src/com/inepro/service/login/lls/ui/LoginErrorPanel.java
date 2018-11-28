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
import com.canon.meap.ctk.awt.CImageComponent;
import com.canon.meap.ctk.awt.CLabel;
import com.canon.meap.ctk.awt.CLabelButton;

/**
 * This class controls the display of error panel of local login panel.<P>
 * 
 * Error panel for displaying the error message at login panel is created in this class.<P> 
 * @version       $Revision: 1.5 $, $Date: 2006/03/10 07:36:08 $
 */
public final class LoginErrorPanel extends UIBasePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 
	 * Constructs a new instance of SampleLoginErrorPanel with the specified ActionListener.<P>
	 * 
	 * The resource file specified as argument is used for locale process at super class.<P>
	 * 
	 * Via the ActionListener argument, action on panel can be delivered and processed at LLS.<P>
	 * 
	 * @param pro
	 * the LoginProperties instance
	 * @param actionListener
	 */
	public LoginErrorPanel(LoginProperties pro, ActionListener actionListener) {
		super(pro);
		action = actionListener;
	}

	/** 
	 * Configures the necessary components to error panel.
	 */
	protected void layoutPanel() {
		setLayout(null);
		setBounds(116, 112, 408, 216);
		setBackground(colorWhite);

		add(makeCHLine(1, 212, 404, 1, colorBlack));
		add(makeCVLine(404, 1, 1, 211, colorBlack));

		add(makeCHLine(0, 0, 404, 1, colorBlack));
		add(makeCVLine(0, 0, 1, 212, colorBlack));
		add(makeCHLine(0, 211, 405, 2, colorGray));
		add(makeCVLine(403, 0, 2, 213, colorGray));

		add(makeCHLine(3, 213, 404, 2, colorGray));
		add(makeCVLine(405, 3, 2, 212, colorGray));

		add(makeCVLine(0, 213, 2, 3, colorBase));
		add(makeCHLine(405, 0, 3, 2, colorBase));

		_errIconImage = new CImageComponent();
		_errIconImage.setBounds(30, 39, 24, 21);
		add(_errIconImage);

		_okButton = new CLabelButton();
		try {
			_okButton.setHorizontalAlignment(CLabelButton.CENTER);
			_okButton.setArrowStyle(CLabelButton.ARROW_RETURN_STYLE);
		} catch (PropertyVetoException e1) {
			LoginSyslog.append(
				Syslog.LLS_ERROR,
				Syslog.WARNING,
				"Applet: layoutPanel : " + e1.toString());
		}
		_okButton.setBounds(212, 156, 171, 34);
		_okButton.setFont(new Font("Dialog", 1, 16));
		_okButton.setBackground(Color.lightGray);
		_okButton.addActionListener(action);
		try {
			_okButton.setActionCommand("errOkButton");
		} catch (PropertyVetoException propertyvetoexception1) {
			LoginSyslog.append(
				Syslog.LLS_ERROR,
				Syslog.WARNING,
				"Applet: layoutPanel : " + propertyvetoexception1.toString());
		}
		add(_okButton);

		_errorLabel = new CLabel();
		_errorLabel.setBounds(62, 37, 304, 80);
		_errorLabel.setFont(new Font("Dialog", 1, 16));
		_errorLabel.setBackground(colorWhite);
		try {
			_errorLabel.setHorizontalAlignment(CLabel.LEFT);
			_errorLabel.setVerticalAlignment(CLabel.TOP);
		} catch (PropertyVetoException propertyvetoexception2) {
			LoginSyslog.append(
				Syslog.LLS_ERROR,
				Syslog.WARNING,
				"Applet: layoutPanel : " + propertyvetoexception2.toString());
		}
		add(_errorLabel);

		Panel panelTop = new Panel();
		panelTop.setLayout(null);
		panelTop.setBounds(7, 8, 391, 129);
		panelTop.setBackground(colorWhite);
		add(panelTop);

		panelTop.add(makeCHLine(0, 0, 390, 1, colorBlack));
		panelTop.add(makeCVLine(0, 0, 1, 128, colorBlack));
		panelTop.add(makeCHLine(1, 128, 390, 1, colorGray));
		panelTop.add(makeCVLine(390, 1, 1, 128, colorGray));

		Panel panelBottom = new Panel();
		panelBottom.setLayout(null);
		panelBottom.setBounds(7, 139, 391, 67);
		panelBottom.setBackground(colorWhite);
		add(panelBottom);

		panelBottom.add(makeCHLine(0, 0, 390, 1, colorBlack));
		panelBottom.add(makeCVLine(0, 0, 1, 66, colorBlack));
		panelBottom.add(makeCHLine(1, 66, 390, 1, colorGray));
		panelBottom.add(makeCVLine(390, 1, 1, 67, colorGray));
	}

	/** 
	 * 
	 * Sets the message for displayed on each component.<P>
	 * 
	 * The displayed message is set in this method.<P>
	 */
	public void setMessages() {
		_okButton.setLabel(_resources.getStringProperty("LLS_BUTTON_OK"));
		_errorLabel.setText(errMsg);

		try {
			_errIconImage.setImage(
				getImage(_resources.getStringProperty("LLS_IMAGE_ERROR_ICON")));
		} catch (PropertyVetoException propertyvetoexception) {
			propertyvetoexception.printStackTrace();
		}
	}

	/** 
	 * Sets the error message with the specified string.
	 * @param name
	 * the message to be displayed
	 */
	public void setErrorMessage(String name) {
		errMsg = name;
		_errorLabel.setText(errMsg);
	}

	/** 
	 * The based color of login panel.
	 */
	protected Color colorBase = new Color(187, 187, 170);
	/** 
	 */
	protected Color colorDialogBlue = new Color(119, 119, 170);
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
	private ActionListener action;

	private String errMsg = null;
	private CImageComponent _errIconImage = null;
	private CLabelButton _okButton = null;
	private CLabel _errorLabel = null;
}
