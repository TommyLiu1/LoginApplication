package com.inepro.service.login.lls.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import com.canon.meap.csee.service.login.base.lls.atk.ui.UIBasePanel;
import com.canon.meap.csee.service.login.base.util.LoginProperties;
import com.canon.meap.csee.service.login.base.util.LoginSyslog;
import com.canon.meap.csee.service.login.base.util.Syslog;
import com.canon.meap.ctk.atk.CImagePanel;
import com.inepro.service.login.model.ScreenSettingsResult;
import com.inepro.service.login.net.HttpRequestUtils;

public class LocalLoginStartPanel extends UIBasePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private MouseListener _action;
	private LoginProperties _resources;


	public LocalLoginStartPanel(LoginProperties pro, MouseListener actionListener) {
		super(pro);
		this._resources = pro;
		this._action = actionListener;
	}
	
	 public LocalLoginStartPanel(MouseListener actionListener) {
		 this._action = actionListener;
	    }
	 
	 public LocalLoginStartPanel(){}
	 public void init()
	 {
		 layoutPanel();
	 }

	@Override
	protected void layoutPanel() {
		setLayout(null);
		setBounds(0, 0, 640, 444);
		
		CImagePanel bgPanel = new CImagePanel();
		bgPanel.setLayout(null);
		bgPanel.setBounds(0, 0, 640, 444);
		bgPanel.setBackground(Color.white);
		add(bgPanel);
		
		try {
			bgPanel.addMouseListener(_action);
			ScreenSettingsResult screenSettingsResult =HttpRequestUtils.getScreenSettingsResult();
			String image_url = screenSettingsResult.getbackGroundIdleImageURL();
			//String image_text = screenSettingsResult.getWelComeText();
			Image image = getToolkit().getImage(new URL(image_url));
//		    BufferedImage image = ImageIO.read(new URL(image_url));
//		    image = process(image, image_text);
		    bgPanel.setImage(image);
		} catch (Exception e) {
			try {
				bgPanel.setImage(this.getImage(_resources.getStringProperty("LLS_IMAGE_START_BG_ICON")));
//				String image_text ="Welcome, touch the screen to start or present your card";
//				BufferedImage image = ImageIO.read(new File(_resources.getStringProperty("LLS_IMAGE_START_BG_ICON")));
//				image = process(image, image_text);
//				bgPanel.setImage((Image)image);
			} catch (Exception e1) {
				LoginSyslog.append(Syslog.LLS_ERROR, Syslog.WARNING, "LocalLoginPanel: layoutPanel : " + e1.toString());
			}
			LoginSyslog.append(Syslog.LLS_ERROR, Syslog.WARNING, "LocalLoginPanel: layoutPanel : " + e.toString());
		}
	}
	
	 public  BufferedImage process(BufferedImage old,String text) {
	        BufferedImage img = new BufferedImage(
	        		old.getWidth(), old.getHeight(), BufferedImage.TYPE_INT_RGB);
	        Graphics g2d = img.getGraphics();
	        g2d.drawImage(old, 0, 0, old.getWidth(), old.getHeight(), this);
	        g2d.setColor(Color.white);
	        g2d.setFont(new Font("Serif", Font.BOLD, 40));
	        FontMetrics fm = g2d.getFontMetrics();
	        int x = img.getWidth() - fm.stringWidth(text) - 5;
	        int y = img.getHeight() /2;
	        g2d.drawString(text, x, y);
	        g2d.dispose();
	        return img;
	    }
	 
	@Override
	protected void setMessages() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void draw(Graphics arg0) {
		// TODO Auto-generated method stub

	}

}
