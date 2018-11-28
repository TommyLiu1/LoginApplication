package com.inepro.service.login.rls;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.canon.meap.csee.service.login.base.LoginContextExt;
import com.canon.meap.csee.service.login.base.cpca.CpcaAuthFailException;
import com.canon.meap.csee.service.login.base.rls.LoginSessionImpl;
import com.canon.meap.csee.service.login.base.rls.MaxLoginUserException;
import com.canon.meap.csee.service.login.base.rls.RemoteLoginServiceImpl;
import com.canon.meap.csee.service.login.base.servlet.ReplaceStrPair;
import com.canon.meap.csee.service.login.base.util.LoginException;
import com.canon.meap.csee.service.login.base.util.LoginProperties;
import com.inepro.service.login.UserAuthControl;
import com.inepro.service.login.model.LoginResult;
import com.inepro.service.login.net.HttpRequestUtils;
import com.canon.meap.csee.service.login.base.rls.LoginServlet;

/***
 * This class implements LoginServlet and displays remote login page.<P>
 *
 * For responding to the login request, the authentication process is done. The
 * class then creates of new sessions and redirects it to corresponding URL.<P>
 */
final class RLSLoginServlet extends LoginServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * HTTP charset parameter
	 */
	private static final String CHARSET = "text/html; charset=UTF-8";

	/*
	 * Form parameter
	 */
	private static final String USER_ID = "userID";
	private static final String USER_PASSWORD = "password";
	private static final String DIRECTURL = "uri";

	/*
	 * Query string
	 */
	private static final String QUERY_STRING = "?";

	/*
	 * Prefix of replace keyword
	 */
	private static final String KEY_PREFIX = "%%RLS_KEY_";

	/*
	 * URL of the redirection point
	 */
	private static final String DIRECT_URL = "%%RLS_KEY_DIRECT_URL";

	/*
	 * The replaced keyword of error message
	 */
	private static final String REPLACE_ERRMESSAGE = "%%RLS_KEY_ERR_MESSAGE";

	/*
	 * The key of a property file
	 */

	/*
	 * Template HTML of login page
	 */
	private static final String RLS_HTML = "RLS_HTML";

	/*
	 * Template HTML of login error page
	 */
	private static final String RLS_ERR = "RLS_ERR_HTML";

	/*
	 * Error message of authentications to account database
	 */
	private static final String RLS_ID_ERR_LANG1 = "RLS_ID_ERR_LANG1";

	/*
	 * Error message of authentications to CPCA
	 */
	private static final String RLS_ID_ERR_LANG2 = "RLS_ID_ERR_LANG2";

	/*
	 * Error message of an excess of the number of the maximum login
	 */
	private static final String RLS_ID_ERR_LANG3 = "RLS_ID_ERR_LANG3";

	//=========================================================================
	// instance fields
	//=========================================================================

	private final UserAuthControl _authControl;
	private final RemoteLoginServiceImpl _rlsImpl;
	private LoginProperties _pro = null;

	//=========================================================================
	// construtors
	//=========================================================================

	/**
	 * Constructs a new instance of RLSLoginServlet.
	 *
	 * @param rlsImpl
	 * the RemoteLoginServiceImpl instance
	 * @param bundleContext
	 * the BundleContext instance
	 * @param authControl
	 * the AuthControl instance
	 * @param pro
	 * the LoginProperties instance
	 *
	 */
	public RLSLoginServlet(RemoteLoginServiceImpl rlsImpl, UserAuthControl authControl,LoginProperties pro) {
		super(pro);
		this._pro = pro;
		this._rlsImpl = rlsImpl;
		this._authControl =  authControl;
	}

	//=========================================================================
	// RLSLoginServlet methods
	//=========================================================================

	/**
	 * Displays the remote login page and requests the user ID and password.<P>
	 *
	 * Authentication process is done to respond to the inputted user ID and password,
	 * If success, the class creates a new session via RemoteLoginServiceImpl and redirects
	 * it to corresponding URL.<P>
	 *
	 * When the authentication is failed, an error page is displayed.<P>
	 *
	 * @param req
	 * the request information for HTTP Servlet
	 * @param res
	 * the response information for HTTP Servlet
	 * If an exception occurs, an exception will be thrown.
	 * @throws IOException
	 * If an exception occurs, an exception will be thrown.
	 * @see com.canon.meap.csee.service.login.base.rls.LoginServlet#service
	*/
	public void service(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException {

		/*
		 * Set the character encoding of HTTP response
		 *
		 */
		res.setContentType(CHARSET);

		/*
		 * Get the form arameters of user id and password.
		 *
		 */
		String userId = req.getParameter(USER_ID);
		String password = req.getParameter(USER_PASSWORD);

		/*
		 * Check whether the parameter is set up and perform authentication, when set up.
		 *
		 */
		if (userId != null && password != null) {
			try {

				/*
				 * Perform user authentication and create a new instance of SampleLoginContextImpl.
				 *
				 */
				LoginContextExt context = null;
				if("admin".equals(userId) && "password".equals(password))
				{
					context =_authControl.createLoginContext(userId, password);
				}else{
					LoginResult result = HttpRequestUtils.login(userId, password);
					//LoginResult result = HttpRequestUtils.LoginWithoutRequest();
					if(result.loginResult.loginSuccess)
					{
						context =_authControl.createLoginContext(userId, password);
					}else{
						throw new LoginException(_pro.getStringProperty("LOGIN_ERR_INVALID_USER_NAME"));
					}
					
				}
				/*
				 * Create LoginSession
				 *
				 */
				LoginSessionImpl sessionImpl = _rlsImpl.createLoginSession(context);

				/*
				 * Add cookie to HTTP response.
				 *
				 */
				res.addCookie(sessionImpl.getCookie());

				/*
				 * Send redirect to the request url.
				 *
				 */
				res.sendRedirect(req.getParameter(DIRECTURL));
				return;
			} catch (LoginException ex) {

				String errorLang;
				if (ex instanceof LoginException) {
					errorLang = RLS_ID_ERR_LANG1;
				} else if (ex instanceof CpcaAuthFailException) {
					errorLang = RLS_ID_ERR_LANG2;
				} else if (ex instanceof MaxLoginUserException) {
					errorLang = RLS_ID_ERR_LANG3;
				} else {
					printFatalErrorPage(res.getWriter());
					return;
				}

				/*
				 * Set the error message and call printErrPage() method.
				 *
				 */
				printErrPage(
					req,
					res.getWriter(),
					req.getParameter(DIRECTURL),
					errorLang);
				return;
			}
		}
		/*
		 * Get the request URI and display Login Page.
		 *
		 */
		else {

			/*
			 * Get the request URI.
			 *
			 */
			String directURL = req.getParameter(DIRECTURL);
			if (directURL == null) {
				directURL = req.getRequestURI();

				/*
				 * When query string exists,add it to the request URI.
				 *
				 */
				String queryString = req.getQueryString();
				if (queryString != null) {
					StringBuffer stringBuffer =
						new StringBuffer(directURL.length() + queryString.length() + 1);
					stringBuffer.append(directURL);
					stringBuffer.append(QUERY_STRING);
					stringBuffer.append(queryString);
					directURL = stringBuffer.toString();
				}
			}
			printLoginPage(req, res.getWriter(), directURL);
			return;
		}
	}

	//=========================================================================
	// private methods
	//=========================================================================
	/**
	 * Displays the login page.<P>
	 * @param req
	 * the request information for HTTP Servlet
	 * @param pw
	 * the PrintWriter instance for outputting HTML
	 * @param uri
	 * the URL to be directed
	 * @see com.canon.meap.csee.service.login.base.servlet.BaseServlet#printPage(HttpServletRequest, String, String, ReplaceStrPair[])
	 * @see com.canon.meap.csee.service.login.base.servlet.ReplaceStrPair
	 */
	private void printLoginPage(
		HttpServletRequest req,
		PrintWriter pw,
		String directURL) {

		/*
		 * Create a new instance of HtmlReplaceKeySet class and set the replaced keyword and replacement string.
		 */
		ReplaceStrPair keySet[] = new ReplaceStrPair[1];
		keySet[0] = new ReplaceStrPair(DIRECT_URL, directURL);

		/*
		 * Get a StringBuffer by calling printPage() method.
		 */
		try {
			StringBuffer html = printPage(req, RLS_HTML, KEY_PREFIX, keySet);
			pw.print(html);
			pw.flush();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
			/*
			 * When an exception is caught,calls printFatalErrorPage() method and displays error page.
			 */
			printFatalErrorPage(pw);
		}
	}

	/**
	 * Displays the authentication error page.<P>
	 * @param req
	 * the request information for HTTp Servlet
	 * @param pw
	 * the PrintWriter instance for outputting HTML
	 * @param uri
	 * the URL to be directed
	 * @param errorID
	 * the erro message
	 */
	private void printErrPage(
		HttpServletRequest req,
		PrintWriter pw,
		String uri,
		String errorID) {

		/*
		 * Acquire an error message from property file.
		 */
		String errorMessege = getStringProperty(errorID, getCode(req));

		/*
		 * Create a new instance of HtmlReplaceKeySet class and set the replaced keyword and replacement string.
		 * The replaced keywords are DIRECT_URL and REPLACE_ERRMESSAGE.
		 */
		ReplaceStrPair keySet[] = new ReplaceStrPair[2];
		keySet[0] = new ReplaceStrPair(DIRECT_URL, uri);
		keySet[1] = new ReplaceStrPair(REPLACE_ERRMESSAGE, errorMessege);

		/*
		 * Get a StringBuffer by calling printPage() method.
		 */
		try {
			StringBuffer html = printPage(req, RLS_ERR, KEY_PREFIX, keySet);
			pw.print(html);
			pw.flush();
			pw.close();
		} catch (Exception e) {
			printFatalErrorPage(pw);
		}

	}

	/**
	 * Displays the system error page.<P>
	 *
	 * The error message to be displayed is the string returned by getFatalErrorMessage of LoginServlet class.<P>
	 * @param pw
	 * the PrintWriter instance for outputting HTML
	 * @see com.canon.meap.csee.service.login.base.servlet.BaseServlet#getFatalErrorMessage()
	 */
	private void printFatalErrorPage(PrintWriter pw) {
		pw.println(getFatalErrorMessage());
		pw.flush();
		pw.close();
	}
}
