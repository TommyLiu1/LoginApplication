package com.inepro.service.login.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;

import com.canon.meap.service.conf.ConfigurationService;
import com.canon.meap.service.login.LoginSession;
import com.canon.meap.service.login.RemoteLoginService;
import com.canon.meap.csee.service.login.base.util.DeviceInformation;
import com.canon.meap.csee.service.login.base.util.LoginException;

public class ConfigServiceServlet extends HttpServlet {
	
	private String deviceName = "";
	private String deviceHostOrIP = "";
	private String devicePort = "";
	private String locale = "";
	private String debugModel = "";
	private String pullPrintUrl = "";
	private String pullPrintPort = "";


	/**
	 * version ID for serialized form.
	 */
	private static final long serialVersionUID = -8308474102709190041L;
	 private static final String SERVLET_URL = "/Configuration";
	private static ConfigServiceServlet configServiceServlet;
	private static final String CHARSET = "text/html; charset=UTF-8";

  /* PrintWriter */
  private PrintWriter out;

  private HttpServletRequest req;
  private HttpServletResponse res;

  private BundleContext _bundleContext = null;
  private RemoteLoginService _rls = null;
  private SharedConfigurationAccessor _sharedConfAccessor;

  private ConfigurationService _service;

  /**
   * Constructor
   */
  public ConfigServiceServlet(RemoteLoginService rls, BundleContext bundleContext, ConfigurationService service) {
	  super();
	  this._service = service;
      this._rls = rls;
      this._bundleContext = bundleContext;
  }

  /**
   * Perform the initialization
   */
  public void init() throws ServletException {
      super.init();

      configServiceServlet = this;
      _sharedConfAccessor = new SharedConfigurationAccessor(_service);

     
      Object deviceName = _sharedConfAccessor.getConfigurationValue(
              ConfigAppConstants.DEVICE_CONFIG_NAME);
      if (deviceName != null) {
    	  this.deviceName = deviceName.toString();
      }
    
     
      Object deviceHostOrIP = _sharedConfAccessor.getConfigurationValue(
              ConfigAppConstants.DEVICE_CONFIG_HOST_NAME);
      if (deviceHostOrIP != null) {
    	  this.deviceHostOrIP = deviceHostOrIP.toString();
      }
      
      
      Object devicePort = _sharedConfAccessor.getConfigurationValue(
              ConfigAppConstants.DEVICE_CONFIG_PORT_NAME);
      if (devicePort != null) {
    	  this.devicePort = devicePort.toString();
      }
      
      Object locale = _sharedConfAccessor.getConfigurationValue(
              ConfigAppConstants.DEVICE_CONFIG_LOCALE_NAME);
      if (locale != null) {
    	  this.locale = locale.toString();
      }
       
      
      Object debugMode = _sharedConfAccessor.getConfigurationValue(
              ConfigAppConstants.DEVICE_CONFIG_DEBUG_NAME);
      if (debugMode != null) {  
    	  this.debugModel = debugMode.toString();
      }
      return;
  }

  /**
   * Perform the End process
   */
  public void destroy() {
      super.destroy();

      return;
  }

  /**
   * Receive the HttpGet request
   *
   * @param   req request
   * @param   res response
   */
  public void doGet(HttpServletRequest req, HttpServletResponse res)
          throws ServletException, IOException {
	  this.init();
	  res.setContentType(CHARSET);
    
	  final LoginSession session = this._rls.getLoginSession(req);

      if (req.getParameter("logout") != null) {
          session.cancel();
          res.sendRedirect(SERVLET_URL);
          return;
      }
  	/* Perform the service */
      try {
		servletMain(req, res);
	} catch (LoginException e) {
		e.printStackTrace();
	}

      return;
  }

  /**
   * Receive the HttpPost request
   *
   * @param   req request
   * @param   res response
   */
  public void doPost(HttpServletRequest req, HttpServletResponse res)
          throws ServletException, IOException {
	  
	res.setContentType(CHARSET);
  	this.req = req;
  	this.res = res;
  	final LoginSession session = this._rls.getLoginSession(req);

    if (req.getParameter("logout") != null) {
        session.cancel();
        res.sendRedirect(SERVLET_URL);
        return;
    }

      /* Perform the service */
  	  operate();
  	  showPostResult(req,res);
      //servletMain(req, res);

      return;
  }

  /**
   * Perform the service
   *
   * @param   req request
   * @param   res response
   */
  private void servletMain(HttpServletRequest req, HttpServletResponse res)
          throws ServletException, IOException,LoginException {

		/* Generate the HTMLinformation */
		res.setContentType("text/html");
		out = res.getWriter();
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Configuration</title>");
		out.println("<style TYPE=\"text/css\">");
		out.println("<!--");
		out.println(".form-style-3 {");
		out.println("display:block;");
		out.println("margin-bottom: 10px;");
		out.println("width: 40%; height: 20%; position: absolute; left:50%; top:20%; transform:translate(-50%,-50%);");
		out.println("}");
		out.println(".form-style-3 label{");
		out.println("display:flex;");
		out.println("margin-bottom: 10px;");
		out.println("}");
		out.println(".form-style-3 label > span{");
		out.println("float: left;");
		out.println("width: 180px;");
		out.println("color: #000;");
		out.println("font-weight: bold;");
		out.println("font-size: 13px;");
		out.println("text-shadow: 1px 1px 1px #fff;");
		out.println("}");
		out.println(".form-style-3 fieldset{");
		out.println("border-radius: 10px;");
		out.println("-webkit-border-radius: 10px;");
		out.println("-moz-border-radius: 10px;");
		out.println("margin: 0px 0px 10px 0px;");
		out.println("border: 1px solid #FFD2D2;");
		out.println("padding: 20px;");
		out.println("background: #FFF;");
		out.println("box-shadow: inset 0px 0px 15px #FFE5E5;");
		out.println("-moz-box-shadow: inset 0px 0px 15px #FFE5E5;");
		out.println("-webkit-box-shadow: inset 0px 0px 15px #FFE5E5;");
		out.println("}");
		out.println(".form-style-3 fieldset legend{");
		out.println("color: #000;");
		out.println("border-top: 1px solid #000;");
		out.println("border-left: 1px solid #000;");
		out.println("border-right: 1px solid #000;");
		out.println("border-radius: 5px 5px 0px 0px;");
		out.println("-webkit-border-radius: 5px 5px 0px 0px;");
		out.println("-moz-border-radius: 5px 5px 0px 0px;");
		out.println("background: #FFF;");
		out.println("padding: 0px 8px 3px 8px;");
		out.println("box-shadow: -0px -1px 2px #F1F1F1;");
		out.println("-moz-box-shadow:-0px -1px 2px #F1F1F1;");
		out.println("-webkit-box-shadow:-0px -1px 2px #F1F1F1;");
		out.println("font-weight: normal;");
		out.println("font-size: 14px;");
		out.println("}");
		out.println(".form-style-3 input[type=text]{");
		out.println("border-radius: 3px;");
		out.println("-webkit-border-radius: 3px;");
		out.println("-moz-border-radius: 3px;");
		out.println("border: 1px solid #000;");
		out.println("outline: none;");
		out.println("color: #000;");
		out.println("padding: 5px 8px 5px 8px;");
		out.println("box-shadow: inset 1px 1px 4px #FFD5E7;");
		out.println("-moz-box-shadow: inset 1px 1px 4px #FFD5E7;");
		out.println("-webkit-box-shadow: inset 1px 1px 4px #FFD5E7;");
		out.println("background: #ffffff;");
		out.println("width:50%;");
		out.println("}");
		out.println(".form-style-3  input[type=submit]{");
		out.println("background: #007bff;");
		out.println("border: 1px solid #007bff;");
		out.println("padding: 5px 15px 5px 15px;");
		out.println("color: #fff;");
		out.println("box-shadow: inset -1px -1px 3px #FF62A7;");
		out.println("-moz-box-shadow: inset -1px -1px 3px #FF62A7;");
		out.println("-webkit-box-shadow: inset -1px -1px 3px #FF62A7;");
		out.println("border-radius: 3px;");
		out.println("border-radius: 3px;");
		out.println("-webkit-border-radius: 3px;");
		out.println("-moz-border-radius: 3px;");
		out.println("font-weight: bold;");
		out.println("}");
		out.println("-->");
		out.println("</style>");
		out.println("</head>");
		out.println("<body id=\"backGround\">");
		out.println("<div class=\"form-style-3\">");
		out.println("<h2><p id=\"font\" align=\"center\">Configuration</p></h2>");
		out.println("<form name=\"config\" action=\"#\" method=\"post\">");
		out.println("<fieldset><legend>Basic Settings</legend>");
		out.println(
				"<label for=\"deviceName\"><span>Device Name </span><input type=\"text\" class=\"input-field\" name=\"deviceName\" value=" +"\"" + this.deviceName + "\""+"/></label>");
		out.println(
				"<label for=\"serverIP\"><span>Server IP/Host </span><input type=\"text\" class=\"input-field\" name=\"serverIP\" value=" +"\"" + this.deviceHostOrIP + "\""+"/></label>");
		out.println(
				"<label for=\"serverPort\"><span>Server Port</span><input type=\"text\" class=\"input-field\" name=\"serverPort\" value=" +"\"" + this.devicePort + "\""+"/><span>(default:9191)</span></label>");
		out.println("</fieldset>");
		out.println("<fieldset><legend>Advanced Settings</legend>");
		out.println(
				"<label for=\"locate\"><span>Locate Override(Optional)</span><input type=\"text\" name=\"locate\" class=\"input-field\" value=" +"\"" + this.locale + "\""+"/></label>");
		if ("on".equals(this.debugModel))
		{
			out.println(
					"<label for=\"debugModel\"><span>Debug Model</span><input type=\"checkbox\" name=\"debugModel\" checked class=\"input-field\"></input></label>");
		}else{
			out.println(
					"<label for=\"debugModel\"><span>Debug Model</span><input type=\"checkbox\" name=\"debugModel\" class=\"input-field\"></input></label>");
		}
		out.println("<label><span>&nbsp;</span><input type=\"submit\" value=\"Save\" /></label>");
		out.println("</fieldset>");
		out.println("</form>");
		out.println("</div>");
		out.println("</body>");
		out.println("</html>");
		out.flush();
		out.close();

		return;
  }
  
  private void showPostResult(HttpServletRequest request, HttpServletResponse res) throws ServletException, IOException
  {
	  ArrayList<String> parameterNames = new ArrayList<String>();
	  Enumeration enumeration = request.getParameterNames();
	     while (enumeration.hasMoreElements()) {
	         String parameterName = (String) enumeration.nextElement();
	         parameterNames.add(parameterName);
	 }
	  String debugModel = "off";
	  if (parameterNames.contains(ConfigAppConstants.DEVICE_CONFIG_DEBUG_NAME))
	  {
	   		 debugModel = request.getParameter(ConfigAppConstants.DEVICE_CONFIG_DEBUG_NAME);
	  }
	  String content = "Save configuration successfully";
	  String title = "Configuration Result";
      res.setContentType("text/html");
      PrintWriter  writer = res.getWriter();
      writer.println(
         "<html>\n" +
            "<head><title>" + title + "</title></head>\n" +
            "<body bgcolor = \"#f0f0f0\">\n" +
               "<h1 align = \"center\">" + content + "</h1>\n" +
               "<ul>\n" +
                  "  <li><b>Device Name </b>: "
                  + request.getParameter(ConfigAppConstants.DEVICE_CONFIG_NAME) + "\n" +
                  "  <li><b>Server IP/Host </b>: "
                  + request.getParameter(ConfigAppConstants.DEVICE_CONFIG_HOST_NAME) + "\n" +
                  "  <li><b>Server Port </b>: "
                  + request.getParameter(ConfigAppConstants.DEVICE_CONFIG_PORT_NAME) + "\n" +
                  "  <li><b>Locate Override </b>: "
                  + request.getParameter(ConfigAppConstants.DEVICE_CONFIG_LOCALE_NAME) + "\n" +
                  "  <li><b>Debug Model </b>: "
                  + debugModel + "\n" +
               "</ul>\n" +
            "</body>\n" +
         "</html>"
      );
      writer.flush();
      writer.close();
  }

  private void operate() {
	  ArrayList<String> parameterNames = new ArrayList<String>();
	  Enumeration enumeration = req.getParameterNames();
	     while (enumeration.hasMoreElements()) {
	         String parameterName = (String) enumeration.nextElement();
	         parameterNames.add(parameterName);
	 }
	   
  	String deviceName = req.getParameter(ConfigAppConstants.DEVICE_CONFIG_NAME);
  	String devicePort = req.getParameter(ConfigAppConstants.DEVICE_CONFIG_PORT_NAME);
  	String devicePortOrIP = req.getParameter(ConfigAppConstants.DEVICE_CONFIG_HOST_NAME);
  	String locale = req.getParameter(ConfigAppConstants.DEVICE_CONFIG_LOCALE_NAME);
//	String pullPrintUrl = req.getParameter(ConfigAppConstants.PULL_PRINT_WEB_URL);
//	String pullPrintPort = req.getParameter(ConfigAppConstants.PULL_PRINT_WEB_PORT);
  	String debugModel ="off";
  	if (parameterNames.contains(ConfigAppConstants.DEVICE_CONFIG_DEBUG_NAME))
  	{
  		 debugModel = req.getParameter(ConfigAppConstants.DEVICE_CONFIG_DEBUG_NAME);
  	}
  	_sharedConfAccessor.setConfigurationValue(ConfigAppConstants.DEVICE_CONFIG_NAME, deviceName);
  	_sharedConfAccessor.setConfigurationValue(ConfigAppConstants.DEVICE_CONFIG_PORT_NAME, devicePort);
  	_sharedConfAccessor.setConfigurationValue(ConfigAppConstants.DEVICE_CONFIG_HOST_NAME, devicePortOrIP);
  	_sharedConfAccessor.setConfigurationValue(ConfigAppConstants.DEVICE_CONFIG_LOCALE_NAME, locale);
  	_sharedConfAccessor.setConfigurationValue(ConfigAppConstants.DEVICE_CONFIG_DEBUG_NAME, debugModel);
  }

  /**
   * Return the servlet
   *
   * @return  LogServiceServlet
   */
  public static ConfigServiceServlet getLogServiceServlet() {
      return configServiceServlet;
  }

}

