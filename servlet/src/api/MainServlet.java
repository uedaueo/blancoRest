package api;

import blanco.rest.BlancoRestConstants;
import blanco.rest.Exception.BlancoRestException;
import blanco.rest.common.*;
import blanco.rest.valueobject.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Random;
import javax.servlet.*;
import javax.servlet.http.*;

public class MainServlet extends HttpServlet{

	private Random random;

	@Override
	public void init(){
		System.out.println("MainServlet#init()");
		new Config();
		random = new SecureRandom();
		random.setSeed(Thread.currentThread().getId());

		/*
		* SessionManagerの設定は起動時一発目だけでいい
		*/
		this.createSessionManager();
	}

	
	public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException,IOException	{

		this.actionDispatcher("GET", request, response);

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException,IOException {

		this.actionDispatcher("POST", request, response);

	}

	public void doPut(HttpServletRequest request, HttpServletResponse response)throws ServletException,IOException	{

		this.actionDispatcher("PUT", request, response);

	}

	public void doDelete(HttpServletRequest request, HttpServletResponse response)throws ServletException,IOException	{

		this.actionDispatcher("DELETE", request, response);

	}

	private void actionDispatcher(String httpMethod, HttpServletRequest request, HttpServletResponse response) throws IOException {

		try {
			// API クラスのインスタンスを生成
			ApiBase apiClassInstance = this.newApiClassInstance(request);
			Util.infoPrintln(LogLevel.LOG_DEBUG,apiClassInstance.toString());

			String jsonString = null;
			if ("POST".equalsIgnoreCase(httpMethod) || "PUT".equalsIgnoreCase(httpMethod)) {
				// Body から JSON 文字列を取得
				jsonString = this.getJsonFromBody(request);
			} else {
				// URL パラメータから JSON 文字列を取得
				jsonString = request.getParameter("data");
			}
			Util.infoPrintln(LogLevel.LOG_DEBUG,"Main jsonString = " + jsonString);

			/* JSON 文字列を request 電文に詰め替える
			 */
			ApiTelegram requestClassInstance = apiClassInstance.newRequestInstance(httpMethod);
			// ApiTelegram responseClassInstance = apiClassInstance.newResponseInstance("POST");

			CommonRequest commonRequest = createCommonRequest(jsonString, requestClassInstance);
			Util.infoPrintln(LogLevel.LOG_DEBUG, "JSON → Java ms = " + commonRequest);

			String token = commonRequest.getinfo().gettoken();
			String lang = commonRequest.getinfo().getlang();

			apiClassInstance.setRequest(commonRequest);
			apiClassInstance.setResponse(new CommonResponse());

			// セッション情報の確認
			SessionManager sessionManager = apiClassInstance.getSessionManager();
			if (apiClassInstance.isAuthenticationRequired() && sessionManager.validate(token) == false){
				Util.infoPrintln(LogLevel.LOG_EMERG,"sessionManager.validate = false");
				throw new BlancoRestException("sessionManager.validate = false");
			}

			/*
			API の呼び出し
			 */
			ApiTelegram telegramResponse = null;
			if ("GET".equalsIgnoreCase(httpMethod)) {
				telegramResponse = apiClassInstance.action((ApiGetTelegram) commonRequest.gettelegram());
			} else if ("POST".equalsIgnoreCase(httpMethod)) {
				telegramResponse = apiClassInstance.action((ApiPostTelegram) commonRequest.gettelegram());
			} else if ("PUT".equalsIgnoreCase(httpMethod)) {
				telegramResponse = apiClassInstance.action((ApiPutTelegram) commonRequest.gettelegram());
			} else if ("DELETE".equalsIgnoreCase(httpMethod)) {
				telegramResponse = apiClassInstance.action((ApiDeleteTelegram) commonRequest.gettelegram());
			}
			Util.infoPrintln(LogLevel.LOG_DEBUG,"telegramResponse = " + telegramResponse);

			CommonResponse commonResponse = apiClassInstance.getResponse();
			commonResponse.settelegram(telegramResponse);
			commonResponse.setstatus(BlancoRestConstants.API_STATUS_ACCEPTED);
			long currentTimeMillis = System.currentTimeMillis();
			String seed = "" + currentTimeMillis + random.nextDouble();
			commonResponse.setinfo(new ResponseHeader());
			commonResponse.getinfo().settoken(sessionManager.renew(token, seed));
			commonResponse.getinfo().setlang(lang);

			// Java → JSON に変換
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(commonResponse);
			Util.infoPrintln(LogLevel.LOG_DEBUG,"Java → JSON json = " + json);
			response.setContentType("application/json;charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println(json);
			out.close();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (BlancoRestException e) {
			e.printStackTrace();
		}
	}

	private String getJsonFromBody(HttpServletRequest request) throws IOException {
		String jsonstr = null;

		StringBuilder sb = new StringBuilder();
		//ボディ部分の情報を取得
		BufferedReader reader = request.getReader();
		try {
			String line;
			while ((line = reader.readLine()) != null) {//1行ずつ読んで、空行がくるまで
				sb.append(line).append('\n');//空行があった部分に空行を入れる
				// Util.infoPrintln(LogLevel.LOG_DEBUG,"sb");
			}
		} finally {
			reader.close();
		}

		jsonstr = sb.toString();

		return jsonstr;
	}

	private CommonRequest createCommonRequest(String jsonString, ApiTelegram requestClassInstance) throws IOException {

		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();

		RequestDeserializer apiDeserializer = new RequestDeserializer();
		apiDeserializer.setRequestClass(requestClassInstance);

		module.addDeserializer(CommonRequest.class, apiDeserializer);
		mapper.registerModule(module);
		CommonRequest readValue = (CommonRequest) mapper.readValue(jsonString, CommonRequest.class);

		if (readValue == null) {
			Util.infoPrintln(LogLevel.LOG_ERR, "MainServlet#createCommonRequest: Fail to read CommonRequest. readValue returns null.");
			readValue = new CommonRequest();
		}

		return readValue;
	}

	private ApiBase newApiClassInstance(HttpServletRequest request) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		String apiPackage = Config.properties.getProperty(Config.defaultPackageKey);
		if(apiPackage == null){
			apiPackage = BlancoRestConstants.DEFAULT_PACKAGE;
		}
		String api = apiPackage + "." + request.getParameter("api");
		Util.infoPrintln(LogLevel.LOG_DEBUG,"api = " + api);

		//クラスの名前文字列からクラスのインスタンスを生成
		Class apiClass = Class.forName(api);
		ApiBase apiClassInstance = (ApiBase) apiClass.newInstance();
		Util.infoPrintln(LogLevel.LOG_DEBUG,apiClassInstance.toString());

		return apiClassInstance;
	}

	private void createSessionManager() {
		// SessionManagerImpl の 設定
		String strSessionManagerImpl = Config.properties.getProperty(Config.sessionManagerKey);
		SessionManager sessionManagerImpl = null;
		try {
			if (strSessionManagerImpl != null) {
				Class sessionManagerImplClass = Class.forName(strSessionManagerImpl);
				sessionManagerImpl = (SessionManager) sessionManagerImplClass.newInstance();

			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		if (sessionManagerImpl == null) {
			sessionManagerImpl = new SessionManagerImpl();
		}
		ApiBase.setSessionManager(sessionManagerImpl);
	}
}
