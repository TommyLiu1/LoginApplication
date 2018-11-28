package com.inepro.service.login.lls.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import com.canon.meap.csee.service.login.base.lls.atk.ui.UIBasePanel;
import com.canon.meap.csee.service.login.base.util.LoginProperties;
import com.canon.meap.csee.service.login.base.util.LoginSyslog;
import com.canon.meap.csee.service.login.base.util.Syslog;
import com.canon.meap.ctk.atk.CAlignment;
import com.canon.meap.ctk.atk.CArrowStyle;
import com.canon.meap.ctk.atk.CLabel;
import com.canon.meap.ctk.atk.CLabelButton;
import com.inepro.service.login.net.HttpRequestUtils;



public class LLSCostCentresPanel extends UIBasePanel{

	private static final long serialVersionUID = 449452459050615438L;

	private Color colorBase = new Color(187, 187, 170);

    private Color colorWhite = new Color(255, 255, 255);

    private Color colorBlack = new Color(0, 0, 0);

    private Color colorGray = new Color(85, 85, 85);

    private ActionListener action;

    private CLabelButton _okButton = null;

    private CLabel _inforLabel = null;

    private Panel panelTop = null;

    private Panel panelBottom = null;
    
    private ButtonGroup group = new ButtonGroup();
    
    

	/**
     * Constructs a new instance of LLSCostCentrePanel.<P>
     * 
     * 
     * @param pro
     * the LoginProperties instance 
     * @param actionListener
     * the actionListener for receving the action event 
     */
    
    public LLSCostCentresPanel(LoginProperties pro,
            ActionListener actionListener) {
        super(pro);
        this.action = actionListener;
    }
    
    public LLSCostCentresPanel(ActionListener actionListener) {
        this.action = actionListener;
    }
    
    public void init() {
    	layoutPanel();
    }

    /**
     * Configures the necessary components to error panel.<P>
     *  
     */
    protected void layoutPanel() {
      
    	this.setLayout(null);
    	this.setBounds(116, 112, 408, 216);
        //this.setBounds((this._width - 408) / 2, (this._height - 216) / 2, 408, 216);
        this.setBackground(colorWhite);

        this.add(this.makeCHLine(1, 212, 404, 1, this.colorBlack));
        this.add(this.makeCVLine(404, 1, 1, 211, this.colorBlack));

        this.add(this.makeCHLine(0, 0, 404, 1, this.colorBlack));
        this.add(this.makeCVLine(0, 0, 1, 212, this.colorBlack));
        this.add(this.makeCHLine(0, 211, 405, 2, this.colorGray));
        this.add(this.makeCVLine(403, 0, 2, 213, this.colorGray));

        this.add(this.makeCHLine(3, 213, 404, 2, this.colorGray));
        this.add(this.makeCVLine(405, 3, 2, 212, this.colorGray));

        this.add(this.makeCVLine(0, 213, 2, 3, this.colorBase));
        this.add(this.makeCHLine(405, 0, 3, 2, this.colorBase));

        this.panelTop = new Panel();
        this.panelTop.setLayout(null);
        this.panelTop.setBounds(7, 8, 391, 129);
        this.panelTop.setBackground(colorWhite);
        this.add(this.panelTop);

        this.panelTop.add(this.makeCHLine(0, 0, 390, 1, this.colorBlack));
        this.panelTop.add(this.makeCVLine(0, 0, 1, 128, this.colorBlack));
        this.panelTop.add(this.makeCHLine(1, 128, 390, 1, this.colorGray));
        this.panelTop.add(this.makeCVLine(390, 1, 1, 128, this.colorGray));

        this.panelBottom = new Panel();
        this.panelBottom.setLayout(null);
        this.panelBottom.setBounds(7, 138, 391, 67);
        this.panelBottom.setBackground(colorWhite);
        this.add(this.panelBottom);

        this.panelBottom.add(this.makeCHLine(0, 0, 390, 1, this.colorBlack));
        this.panelBottom.add(this.makeCVLine(0, 0, 1, 66, this.colorBlack));
        this.panelBottom.add(this.makeCHLine(1, 66, 390, 1, this.colorGray));
        this.panelBottom.add(this.makeCVLine(390, 1, 1, 67, this.colorGray));

        this._okButton = new CLabelButton();
        try {
            this._okButton.setHorizontalAlignment(CAlignment.CENTER);
            this._okButton.setArrowStyle(CArrowStyle.ARROW_RETURN_STYLE);
        } catch (PropertyVetoException e1) {
            LoginSyslog.append(Syslog.LLS_ERROR, Syslog.WARNING,
                    "Applet: layoutPanel : " + e1.toString());
        }
        this._okButton.setBounds(204, 17, 171, 34);
        this._okButton.setFont(new Font("Dialog", 1, 16));
        this._okButton.setBackground(Color.lightGray);
        this._okButton.addActionListener(this.action);
        this._okButton.setLabel("OK");
        try {
            this._okButton.setActionCommand("costCentresBtn");
        } catch (PropertyVetoException propertyvetoexception1) {
            LoginSyslog.append(Syslog.LLS_ERROR, Syslog.WARNING,
                    "Applet: layoutPanel : "
                            + propertyvetoexception1.toString());
        }
        this.panelBottom.add(this._okButton);
      //Create the radio buttons.
        HashMap<Integer,String> costCentresMap = HttpRequestUtils.getCostCentres();
        ArrayList<String> costCentres =new ArrayList<String>();
        for (int key : costCentresMap.keySet()) {
        	costCentres.add(costCentresMap.get(key));
        }
       
    	this._inforLabel = new CLabel();
        this._inforLabel.setBounds(10, 20, 200, 15);
        this._inforLabel.setFont(new Font("Dialog", 1, 16));
        this._inforLabel.setText("Plase choose cost centre:");
        this._inforLabel.setBackground(colorWhite);
        this.panelTop.add(_inforLabel);
        
       JRadioButton [] costButton= new JRadioButton[costCentres.size()];
        int index = 1;
        int o_index = 1;
        int j_index = 1;
        for(String costCentresName:costCentres)
        {
        	
        	costButton[index -1] = new JRadioButton(costCentresName);
        	costButton[index -1].setActionCommand(costCentresName);
        	if(1 == index)
        	{
        		costButton[index -1].setSelected(true);
        	}
        	if(index % 2 == 0)
        	{
        		costButton[index -1].setBounds(190, 60 + (o_index -1) * 30, 150, 30);
        		o_index++;
        	}else{
        		costButton[index -1].setBounds(10, 60 + (j_index -1) * 30, 150, 30);
        		j_index++;
        	} 
        	costButton[index -1].setFont(new Font("Dialog", 1, 16));
        	costButton[index -1].setBackground(colorWhite);
        	costButton[index -1].setHorizontalAlignment(CAlignment.LEFT);
        	costButton[index -1].addActionListener(this.action);
        	group.add(costButton[index -1]);
        	this.panelTop.add(costButton[index -1]);
        	index ++;
        }
        
    }

    /**
     * Sets the message for displayed on each components.<P>
     * 
     * The displayed message is set in this method.<P>
     */
    public void setMessages() {
     
    }

    /**
     * Sets the error message with the specified string.
     * 
     * @param name
     * the message to be displayed
     */
    public void setErrorMessage(String name) {
    }

    /**
     * Draws a panel.
     * This method isn't used in this Sample application.
     * 
     * @param graphics
     * the Graphics instance
     */
    protected void draw(Graphics graphics) {

    }
    
    public String getSelectedRadioBtnName()
    {
    	 Enumeration<AbstractButton> allRadioButton=group.getElements();  
         while(allRadioButton.hasMoreElements())  
         {  
            JRadioButton temp=(JRadioButton)allRadioButton.nextElement();  
            if(temp.isSelected())  
            {  
               return temp.getText();
            }  
         }
         return "";
    }
    

}
