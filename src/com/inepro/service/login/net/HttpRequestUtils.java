package com.inepro.service.login.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import com.canon.meap.csee.service.login.base.util.DeviceInformation;
import com.canon.meap.csee.service.login.base.util.LoginException;
import com.canon.meap.csee.service.login.base.util.LoginProperties;
import com.canon.meap.csee.service.login.base.util.LoginSyslog;
import com.canon.meap.service.conf.ConfigurationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inepro.service.login.Activator;
import com.inepro.service.login.model.LogRequestModel;
import com.inepro.service.login.model.LoginRequestModel;
import com.inepro.service.login.model.LoginResult;
import com.inepro.service.login.model.LogoutRequestModel;
import com.inepro.service.login.model.LogoutUserResult;
import com.inepro.service.login.model.ScreenSettingsRequestModel;
import com.inepro.service.login.model.ScreenSettingsResult;
import com.inepro.service.login.model.TCostCentres;
import com.inepro.service.login.model.UpdateCostCentreRequestModel;
import com.inepro.service.login.model.UpdateCostCentreResult;
import com.inepro.service.login.servlet.ConfigAppConstants;
import com.inepro.service.login.servlet.ServletActivator;
import com.inepro.service.login.servlet.SharedConfigurationAccessor;

public class HttpRequestUtils {

	/**
	 * Do GET request
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	private final static String login_base_api_url = "IneproPrinterService.svc/Login";
	private final static String update_costcentres_base_api_url = "IneproPrinterService.svc/UpdateCostCentre";
	private final static String logout_base_api_url = "IneproPrinterService.svc/LogoutUser";
	private final static String log_base_api_url = "IneproPrinterService.svc/Log";
	private final static String background_image_base_api_url = "IneproPrinterService.svc/GetScreenSettings";
	
	
	private final static LoginProperties loginProperties = new LoginProperties(
	            "res/LoginSetting");
	
	private static ArrayList<TCostCentres> tCostCentres =new ArrayList<TCostCentres> ();
	
	
	private static void disableSSLCertificateChecking() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        } };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        	// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
	
	public static String doGet(String url) {

		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		StringBuffer resultBuffer = new StringBuffer();
		String tempLine = null;

		try {
			disableSSLCertificateChecking();
			URL localURL = new URL(url);
			HttpURLConnection httpURLConnection = (HttpURLConnection) localURL.openConnection();
			httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			if (httpURLConnection.getResponseCode() == 200) {
				inputStream = httpURLConnection.getInputStream();
				inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
				reader = new BufferedReader(inputStreamReader);
				while ((tempLine = reader.readLine()) != null) {
					resultBuffer.append(tempLine);
				}
			}

		} catch (Exception e) {
			LoginSyslog.appendError(2, "http get error:" + e.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					LoginSyslog.appendError(2, "http get error:" + e.getMessage());
				}
			}

			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException e) {
					LoginSyslog.appendError(2, "http get error:" + e.getMessage());
				}
			}

			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					LoginSyslog.appendError(2, "http get error:" + e.getMessage());
				}
			}

		}

		return resultBuffer.toString();
	}
	
	public static HashMap<Integer,String> getCostCentres()
	{
		HashMap<Integer,String> result = new HashMap<Integer,String>();
		for(TCostCentres tCostCentres:HttpRequestUtils.tCostCentres)
		{
			result.put(tCostCentres.id, tCostCentres.shortName);
		}
		return result;
		
	}

	public static String getLoginApiUrl() {
		ConfigurationService config = ServletActivator.getConfigInstance();
		if (config == null) {
			return "";
		}
		try {
			SharedConfigurationAccessor sharedConfAccessor = new SharedConfigurationAccessor(config);
			String hostName = sharedConfAccessor.getConfigurationValue(ConfigAppConstants.DEVICE_CONFIG_HOST_NAME)
					.toString();
			if (hostName == null || hostName.equals("")) {
				return "";
			}
			String port = sharedConfAccessor.getConfigurationValue(ConfigAppConstants.DEVICE_CONFIG_PORT_NAME)
					.toString();
			if (port == null || port.equals("") || port.equals("80")) {
				return String.format("%s/%s", hostName, login_base_api_url);
			}
			return String.format("%s:%s/%s", hostName, port, login_base_api_url);

		} catch (Exception e) {
			return "";
		}
	}
	public static String getLogoutApiUrl()
	{
		ConfigurationService config = ServletActivator.getConfigInstance();
		if (config == null) {
			return "";
		}
		try {
			SharedConfigurationAccessor sharedConfAccessor = new SharedConfigurationAccessor(config);
			String hostName = sharedConfAccessor.getConfigurationValue(ConfigAppConstants.DEVICE_CONFIG_HOST_NAME)
					.toString();
			if (hostName == null || hostName.equals("")) {
				return "";
			}
			String port = sharedConfAccessor.getConfigurationValue(ConfigAppConstants.DEVICE_CONFIG_PORT_NAME)
					.toString();
			if (port == null || port.equals("") || port.equals("80")) {
				return String.format("%s/%s", hostName, logout_base_api_url);
			}
			return String.format("%s:%s/%s", hostName, port, logout_base_api_url);

		} catch (Exception e) {
			return "";
		}
	}
	
	public static String getUpdateCostCentresApiUrl()
	{
		ConfigurationService config = ServletActivator.getConfigInstance();
		if (config == null) {
			return "";
		}
		try {
			SharedConfigurationAccessor sharedConfAccessor = new SharedConfigurationAccessor(config);
			String hostName = sharedConfAccessor.getConfigurationValue(ConfigAppConstants.DEVICE_CONFIG_HOST_NAME)
					.toString();
			if (hostName == null || hostName.equals("")) {
				return "";
			}
			String port = sharedConfAccessor.getConfigurationValue(ConfigAppConstants.DEVICE_CONFIG_PORT_NAME)
					.toString();
			if (port == null || port.equals("") || port.equals("80")) {
				return String.format("%s/%s", hostName, update_costcentres_base_api_url);
			}
			return String.format("%s:%s/%s", hostName, port, update_costcentres_base_api_url);

		} catch (Exception e) {
			return "";
		}
	}
	
	public static String getLogApiUrl()
	{
		ConfigurationService config = ServletActivator.getConfigInstance();
		if (config == null) {
			return "";
		}
		try {
			SharedConfigurationAccessor sharedConfAccessor = new SharedConfigurationAccessor(config);
			String hostName = sharedConfAccessor.getConfigurationValue(ConfigAppConstants.DEVICE_CONFIG_HOST_NAME)
					.toString();
			if (hostName == null || hostName.equals("")) {
				return "";
			}
			String port = sharedConfAccessor.getConfigurationValue(ConfigAppConstants.DEVICE_CONFIG_PORT_NAME)
					.toString();
			if (port == null || port.equals("") || port.equals("80")) {
				return String.format("%s/%s", hostName, log_base_api_url);
			}
			return String.format("%s:%s/%s", hostName, port, log_base_api_url);

		} catch (Exception e) {
			return "";
		}
	}
	
	public static String getBackGroundImnageApiUrl()
	{
		ConfigurationService config = ServletActivator.getConfigInstance();
		if (config == null) {
			return "";
		}
		try {
			SharedConfigurationAccessor sharedConfAccessor = new SharedConfigurationAccessor(config);
			String hostName = sharedConfAccessor.getConfigurationValue(ConfigAppConstants.DEVICE_CONFIG_HOST_NAME)
					.toString();
			if (hostName == null || hostName.equals("")) {
				return "";
			}
			String port = sharedConfAccessor.getConfigurationValue(ConfigAppConstants.DEVICE_CONFIG_PORT_NAME)
					.toString();
			if (port == null || port.equals("") || port.equals("80")) {
				return String.format("%s/%s", hostName, background_image_base_api_url);
			}
			return String.format("%s:%s/%s", hostName, port, background_image_base_api_url);

		} catch (Exception e) {
			return "";
		}
	}
	public static String doJsonPost(String urlPath, String params) throws LoginException{
		StringBuffer result = new StringBuffer();
		HttpURLConnection conn = null;
		BufferedReader reader = null;
		try{
			disableSSLCertificateChecking();
		}catch(Exception e)
		{
			LoginSyslog.appendError(2, "Disable ssl error:" + e.getMessage());
			//throw new DBAuthFailException("disablessl error:"+e.getMessage());
		}
		try {
			URL url = new URL(urlPath);
			if (urlPath.contains("https"))
			{
				conn = (HttpsURLConnection) url.openConnection();
			}else{
				conn = (HttpURLConnection) url.openConnection();
			}
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			//conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			conn.setRequestProperty("accept", "application/json");
			conn.setConnectTimeout(30*1000);
			// add request parameters
			if (params != null && params != "") {
				byte[] writebytes = params.getBytes();
				conn.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
				DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
				dos.writeBytes(params);
				dos.flush();
				dos.close();
			}
			if (conn.getResponseCode() == 200) {
				 String readLine=new String();
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
				 while((readLine = reader.readLine())!=null){
		                result.append(readLine).append("\n");
		            };
			}else{
				throw new LoginException("http code:"+conn.getResponseCode()+conn.getResponseMessage());
			}
		} catch (Exception e) {
			throw new LoginException("https post:"+e.getLocalizedMessage());
		
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					LoginSyslog.appendError(2, "http post error:" + e.getMessage());
				}
			}
			if(conn != null)
			{
				conn.disconnect();
			}
		}
		return result.toString();
	}
	
	public static LoginResult login(String username, String password) throws LoginException
	{
		if("".equals(username) &&  "".equals(password))
		{
			throw new LoginException(loginProperties.getStringProperty("LOGIN_ERR_INVALID_USER_NAME"));
		}
		    String json = "";
	        ObjectMapper mapper = new ObjectMapper();
	        mapper.disable(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS);
	        String seriesNum;
			try {
				seriesNum = DeviceInformation.getDeviceSerialnumber(Activator.getBundleContext().getBundle());
			} catch (Exception e) {
				seriesNum ="";
			}
			LoginRequestModel loginModel = new LoginRequestModel(username,password,seriesNum);
			try {
				 json = mapper.writeValueAsString(loginModel);
			} catch (JsonProcessingException e) {
				LoginSyslog.appendError(2, loginProperties
		                            .getStringProperty("LONIN_ERR_WRITE_JSON") + "\n" +e.getMessage());
				 throw new LoginException(
		                    loginProperties
		                            .getStringProperty("LONIN_ERR_WRITE_JSON") + "\n" +e.getMessage());
			}
			String api_url = getLoginApiUrl();
			//String api_url = "https://server1.bgms.co.uk/IneproPrinterService.svc/Login";
			String result = HttpRequestUtils.doJsonPost(api_url, json);
			if (result == null || result =="")
			{
				throw new LoginException("post json:"+json + api_url);
			}
			LoginResult loginResult = null;
			try {
				loginResult = mapper.readValue(result, LoginResult.class);
				if (loginResult.loginResult.loginSuccess)
				{
					tCostCentres = loginResult.loginResult.tCostCentres;
				}
			} catch (Exception e) {
				LoginSyslog.appendError(2, loginProperties
                        .getStringProperty("LONIN_ERR_READ_JSON") + "\n" +e.getMessage());
				throw new LoginException(
	                    loginProperties
	                            .getStringProperty("LONIN_ERR_READ_JSON") + "error detail: " +e.getMessage()+"post json: "+json +"result:"+result);
				
			}
			
			return loginResult;
	}
	
	public static LogoutUserResult getUserLogoutResult(String api_url,String userName) throws LoginException
	{
		String json = "";
		String seriaNumber = "";
		try {
			seriaNumber = DeviceInformation.getDeviceSerialnumber(Activator.getBundleContext().getBundle());
		} catch (LoginException e1) {
			throw new LoginException("got seriaNumber errror:"+e1.getMessage());
		}
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS);
        LogoutRequestModel logoutRequest = new LogoutRequestModel(seriaNumber, userName);
        try {
			json = mapper.writeValueAsString(logoutRequest);
			String result = HttpRequestUtils.doJsonPost(api_url, json);
			return  mapper.readValue(result, LogoutUserResult.class);
			
		} catch (Exception e) {
			LoginSyslog.appendError(2, loginProperties
                    .getStringProperty("LONIN_ERR_WRITE_JSON") + "\n" +e.getMessage());
			throw new LoginException(
                    loginProperties
                            .getStringProperty("LONIN_ERR_READ_JSON") + "error detail: " +e.getMessage()+"post json: "+json);
		}
	}
	
	public static UpdateCostCentreResult getUpdateCostCentresResult(String api_url, String serialNumber,String userName, int costCentresID) throws LoginException
	{
		String json = "";
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS);
        UpdateCostCentreRequestModel updateRequest = new UpdateCostCentreRequestModel(serialNumber, userName, costCentresID);
        try {
			json = mapper.writeValueAsString(updateRequest);
			String result = HttpRequestUtils.doJsonPost(api_url, json);
			return  mapper.readValue(result, UpdateCostCentreResult.class);
			
		} catch (Exception e) {
			LoginSyslog.appendError(2, loginProperties
                    .getStringProperty("LONIN_ERR_WRITE_JSON") + "\n" +e.getMessage());
			throw new LoginException(
                    loginProperties
                            .getStringProperty("LONIN_ERR_READ_JSON") + "error detail: " +e.getMessage()+"post json: "+json);
		}
	}
	
	public static String getUpdateCostCentreJson(String serialNumber,String userName, int costCentresID)
	{
		String json = "";
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS);
        UpdateCostCentreRequestModel updateRequest = new UpdateCostCentreRequestModel(serialNumber, userName, costCentresID);
      
        try {
			json = mapper.writeValueAsString(updateRequest);
        }catch (IOException e) {
			LoginSyslog.appendError(2, loginProperties
                    .getStringProperty("LONIN_ERR_WRITE_JSON") + "\n" +e.getMessage());
            return "";
		}
        return json;
	}
	
	public static ScreenSettingsResult getScreenSettingsResult() throws LoginException
	{
		String json = "";
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS);
        String seriesNum;
		try {
			seriesNum = DeviceInformation.getDeviceSerialnumber(Activator.getBundleContext().getBundle());
		} catch (Exception e) {
			seriesNum ="";
		}
        ScreenSettingsRequestModel screenRequest = new ScreenSettingsRequestModel(seriesNum);
        try {
        	String api_url = "https://server1.bgms.co.uk/IneproPrinterService.svc/GetScreenSettings";//getLoginApiUrl();
			json = mapper.writeValueAsString(screenRequest);
			String result = HttpRequestUtils.doJsonPost(api_url, json);
			return  mapper.readValue(result, ScreenSettingsResult.class);
			
		} catch (Exception e) {
			LoginSyslog.appendError(2, loginProperties
                    .getStringProperty("LONIN_ERR_WRITE_JSON") + "\n" +e.getMessage());
			throw new LoginException(
                    loginProperties
                            .getStringProperty("LONIN_ERR_READ_JSON") + "error detail: " +e.getMessage()+"post json: "+json);
		}
        
	}
	public static String getLogoutRequestJson(String serialNumber,String userName)
	{
		String json = "";
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS);
        LogoutRequestModel logoutRequest = new LogoutRequestModel(serialNumber, userName);
        try {
			json = mapper.writeValueAsString(logoutRequest);
        }catch (IOException e) {
			LoginSyslog.appendError(2, loginProperties
                    .getStringProperty("LONIN_ERR_WRITE_JSON") + "\n" +e.getMessage());
            return "";
		}
        return json;
	}
	
	public static void sendLog2Server(String Content)
	{
		 String json = "";
		 ObjectMapper mapper = new ObjectMapper();
	        mapper.disable(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS);
	     LogRequestModel logRequest = new LogRequestModel(Content);
	     try {
				json = mapper.writeValueAsString(logRequest);
				HttpRequestUtils.doJsonPost(getLogApiUrl(), json);
	        }catch (Exception e) {
	        	System.out.println(e.getMessage());
	        } 
	}
	
	public static LoginResult LoginWithoutRequest() throws LoginException
	{
		
		String result = "{" +
			   "\"LoginResult\":{" +
			        "\"Customer\":{" +
			            "\"AskCostCentres\":true," +
			            "\"BudgetBalance\":9999999.99," +
			            "\"BudgetBalancePurseID\":2086418837," +
			            "\"CustomerCode\":\"12345\"," +
			            "\"DepartmentID\":1," +
			            "\"FirstName\":\"John\"," +
			            "\"ID\":-1952765949," +
			            "\"IsAdmin\":true," +
			            "\"LastName\":\"Smith\"," +
			            "\"PersonalBalance\":0," +
			            "\"PersonalBalancePurseID\":0" +
			        "}," +
			        "\"LoginSuccess\":true," +
			        "\"Message\":\"Login success.\"," +
			        "\"PrintJobCount\":2," +
			        "\"TCostCentres\":[" +
			            "{" + 
				            "\"AmountSpent\":20," +
			                "\"Code\":\"costcenter2\","  +
			                "\"GroupID\":0," +
			                "\"ID\":1," +
			                "\"LongName\":\"costcenter1\","  +
			                "\"MaxSpendingAmount\":0," +
			                "\"ShortName\":\"costcenter1\"," +
			                "\"ValidFrom\":\"0100-01-01T00:00:00.000Z\"," +
			                "\"ValidTo\":\"9999-12-31T23:59:59.136Z\"" +
			            "}," +
			            "{" +
			                "\"AmountSpent\":20," +
			                "\"Code\":\"costcenter2\","  +
			                "\"GroupID\":0," +
			                "\"ID\":2," +
			                "\"LongName\":\"costcenter2\","  +
			                "\"MaxSpendingAmount\":0," +
			                "\"ShortName\":\"costcenter2\"," +
			                "\"ValidFrom\":\"0100-01-01T00:00:00.000Z\"," +
			                "\"ValidTo\":\"9999-12-31T23:59:59.136Z\"" +
			            "}," +		                
						 "{" +
						 "\"AmountSpent\":20," +
						 "\"Code\":\"costcenter2\","  +
						 "\"GroupID\":0," +
						 "\"ID\":3," +
						 "\"LongName\":\"costcenter3\","  +
						 "\"MaxSpendingAmount\":0," +
						 "\"ShortName\":\"costcenter3\"," +
						 "\"ValidFrom\":\"0100-01-01T00:00:00.000Z\"," +
						 "\"ValidTo\":\"9999-12-31T23:59:59.136Z\"" +
						"}," +
						"{" +
						 "\"AmountSpent\":20," +
						 "\"Code\":\"costcenter2\","  +
						 "\"GroupID\":0," +
						 "\"ID\":4," +
						 "\"LongName\":\"costcenter4\","  +
						 "\"MaxSpendingAmount\":0," +
						 "\"ShortName\":\"costcenter4\"," +
						 "\"ValidFrom\":\"0100-01-01T00:00:00.000Z\"," +
						 "\"ValidTo\":\"9999-12-31T23:59:59.136Z\"" +
						"}" +
			        "]" +
			    "}" +
			"}";
		
		LoginResult loginResult = null;
		try {
			 ObjectMapper mapper = new ObjectMapper();
		     mapper.disable(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS);
		     loginResult = mapper.readValue(result, LoginResult.class);
		     if (loginResult.loginResult.loginSuccess)
			 {
					tCostCentres = loginResult.loginResult.tCostCentres;
			 }
			 
		} catch (Exception e) {
			throw new LoginException(e.getMessage());
		}
		
	   return loginResult;
	}
	
	
	public static LoginResult LoginWithoutRequest2() throws LoginException
	{
		
		String result = "{" +
			   "\"LoginResult\":{" +
			        "\"Customer\":{" +
			            "\"AskCostCentres\":true," +
			            "\"BudgetBalance\":9999999.99," +
			            "\"BudgetBalancePurseID\":2086418837," +
			            "\"CustomerCode\":\"12345\"," +
			            "\"DepartmentID\":1," +
			            "\"FirstName\":\"John\"," +
			            "\"ID\":-1952765949," +
			            "\"IsAdmin\":true," +
			            "\"LastName\":\"Smith\"," +
			            "\"PersonalBalance\":0," +
			            "\"PersonalBalancePurseID\":0" +
			        "}," +
			        "\"LoginSuccess\":true," +
			        "\"Message\":\"Login success.\"," +
			        "\"PrintJobCount\":2," +
			        "\"TCostCentres\":[" +
			            "{" + 
				            "\"AmountSpent\":20," +
			                "\"Code\":\"costcenter2\","  +
			                "\"GroupID\":0," +
			                "\"ID\":1," +
			                "\"LongName\":\"test1\","  +
			                "\"MaxSpendingAmount\":0," +
			                "\"ShortName\":\"test1\"," +
			                "\"ValidFrom\":\"0100-01-01T00:00:00.000Z\"," +
			                "\"ValidTo\":\"9999-12-31T23:59:59.136Z\"" +
			            "}," +
			            "{" +
			                "\"AmountSpent\":20," +
			                "\"Code\":\"costcenter2\","  +
			                "\"GroupID\":0," +
			                "\"ID\":2," +
			                "\"LongName\":\"costcenter2\","  +
			                "\"MaxSpendingAmount\":0," +
			                "\"ShortName\":\"test2\"," +
			                "\"ValidFrom\":\"0100-01-01T00:00:00.000Z\"," +
			                "\"ValidTo\":\"9999-12-31T23:59:59.136Z\"" +
			            "}," +		                
						 "{" +
						 "\"AmountSpent\":20," +
						 "\"Code\":\"costcenter2\","  +
						 "\"GroupID\":0," +
						 "\"ID\":3," +
						 "\"LongName\":\"test3\","  +
						 "\"MaxSpendingAmount\":0," +
						 "\"ShortName\":\"test3\"," +
						 "\"ValidFrom\":\"0100-01-01T00:00:00.000Z\"," +
						 "\"ValidTo\":\"9999-12-31T23:59:59.136Z\"" +
						"}" +
			        "]" +
			    "}" +
			"}";
		
		LoginResult loginResult = null;
		try {
			 ObjectMapper mapper = new ObjectMapper();
		     mapper.disable(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS);
		     loginResult = mapper.readValue(result, LoginResult.class);
		     if (loginResult.loginResult.loginSuccess)
			 {
					tCostCentres = loginResult.loginResult.tCostCentres;
			 }
			 
		} catch (Exception e) {
			throw new LoginException(e.getMessage());
		}
		
	   return loginResult;
	}
	public static void main(String[] args)
	{
		try {
			System.out.println(HttpRequestUtils.getScreenSettingsResult().getbackGroundIdleImageURL());
			System.out.println(HttpRequestUtils.getScreenSettingsResult().getWelComeText());
//			LoginResult result = HttpRequestUtils.LoginWithoutRequest();
//			System.out.println(result.loginResult.loginSuccess);
//			System.out.println(result.loginResult.customerInfo.askCostCentres);
//			for(int key:HttpRequestUtils.getCostCentres().keySet())
//			{
//				System.out.println(HttpRequestUtils.getCostCentres().get(key));
//			}
//			result = HttpRequestUtils.LoginWithoutRequest2();
//			for(int key:HttpRequestUtils.getCostCentres().keySet())
//			{
//				System.out.println(HttpRequestUtils.getCostCentres().get(key));
//			}
//			sendLog2Server("this is a test");
			//System.out.println(HttpRequestUtils.login("1122", "3344"));
		//System.out.println(HttpRequestUtils.getUserLogoutResult("https://server1.bgms.co.uk/IneproPrinterService.svc/Login","admin").logoutUserResult.message);
//			System.out.println(HttpRequestUtils.getUpdateCostCentresResult("G435P800567","1122", 12).updateCostCentreResult.message);
//		} catch (DBAuthFailException e) {
//			e.printStackTrace();
//		}
		
//		try {
//			 System.out.println(HttpRequestUtils.testLoginSucess());
//			 System.out.println(HttpRequestUtils.printJobCount);
//			 System.out.println(HttpRequestUtils.getCustomerInfo().askCostCentres);
//			 System.out.println(HttpRequestUtils.getCustomerInfo().id);
//			 System.out.println(HttpRequestUtils.getCustomerInfo().personalBalancePurseID);
//			 HashMap<Integer,String> tt = HttpRequestUtils.getCostCentres();
//			 for (int key : tt.keySet()) {
//				 System.out.println(key);
//				 System.out.println(tt.get(key));
//				}
//			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

}
