package api;

import blanco.rest.BlancoRestConstants;
import blanco.rest.Exception.BlancoRestException;
import blanco.rest.common.*;
import blanco.rest.valueobject.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Random;
import javax.servlet.*;
import javax.servlet.http.*;

public class MainServlet extends HttpServlet{

	private Random random;

	@Override
	public void init(){
		System.out.println("MainServlet#init()");

		this.loadSettings();

		random = new SecureRandom();
		random.setSeed(Thread.currentThread().getId());

		/*
		* SessionManagerの設定は起動時一発目だけでいい
		*/
		this.createSessionManager();
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {

		this.actionDispatcher("GET", request, response);

	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {

		this.actionDispatcher("POST", request, response);

	}

	@Override
	public void doPut(HttpServletRequest request, HttpServletResponse response) {

		this.actionDispatcher("PUT", request, response);

	}

	@Override
	public void doDelete(HttpServletRequest request, HttpServletResponse response) {

		this.actionDispatcher("DELETE", request, response);

	}

	private void actionDispatcher(String httpMethod, HttpServletRequest request, HttpServletResponse response) {

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

		}
//		catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (InstantiationException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (BlancoRestException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		catch (Exception e) {
			/* 全ての Exception をここで受けて, クライアントに必ず dismiss を返す */
			Util.infoPrintln(LogLevel.LOG_EMERG,"[blancoRest] Critical Exception Occured!!!");

			e.printStackTrace();

			/*
			 エラーメッセージの準備
			 */
			ErrorItem errorItem = new ErrorItem();
			errorItem.setcode(e.getClass().getSimpleName());
			ArrayList<String> msgs = new ArrayList<>();
			msgs.add(e.getMessage());
			errorItem.setmessages(msgs);

			ArrayList<ErrorItem> errorItems = new ArrayList<>();
			errorItems.add(errorItem);

			/*
			 とりあえずダミーのレスポンス
			 */
//			ApiTelegram telegramResponse = new ApiTelegram();

			CommonResponse commonResponse = new CommonResponse();

//			commonResponse.settelegram(telegramResponse);
			commonResponse.setstatus(BlancoRestConstants.API_STATUS_DISMISS);
			commonResponse.seterrors(errorItems);

			long currentTimeMillis = System.currentTimeMillis();
			String seed = "" + currentTimeMillis + random.nextDouble();
			commonResponse.setinfo(new ResponseHeader());
			commonResponse.getinfo().settoken(ApiBase.getSessionManager().renew("", seed));
			commonResponse.getinfo().setlang("ja");

			// Java → JSON に変換
			try {
				ObjectMapper mapper = new ObjectMapper();
				String json = mapper.writeValueAsString(commonResponse);
				Util.infoPrintln(LogLevel.LOG_DEBUG,"Java → JSON json = " + json);
				response.setContentType("application/json;charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.println(json);
				out.close();
			} catch (IOException ex) {
				Util.infoPrintln(LogLevel.LOG_EMERG,"[blancoRest] Terrible Exception Occured!!! : " + ex.getMessage());
				ex.printStackTrace();
			}
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

	public void loadSettings() {

		/*
		 * web.xmlで定義されている初期パラメータを取得
		 * ここで読み込むのは ConfigDir と SystemId のみ．
		 */
		Enumeration params = getInitParameterNames();

		while (params.hasMoreElements()) {
			String param = (String) params.nextElement();
			Config.properties.put(param, getInitParameter(param));
		}

		Util.infoPrintln(LogLevel.LOG_DEBUG, "MainServlet#loadSettings : Web.xml is read");

		String ConfigDir = Config.properties.getProperty(Config.configDirKey);
		String SystemId = Config.properties.getProperty(Config.systemIdKey);
		String settings = null;


		try {
			if (ConfigDir == null || SystemId == null) {
				settings = BlancoRestConstants.CONFIG_FILE;
			} else {
				settings = ConfigDir + "/" + SystemId + ".xml";
				File file = new File(settings);
				if (!file.exists()){
					Util.infoPrintln(LogLevel.LOG_ERR, "MainServlet#loadSettings : NO SUCH FILE : " + settings);
					settings = BlancoRestConstants.CONFIG_FILE;
				}
			}
			Util.infoPrintln(LogLevel.LOG_DEBUG, "MainServlet#loadSettings : reading ... " + settings);
			new Config(settings);
		} catch (IOException e) {
			Util.infoPrintln(LogLevel.LOG_CRIT, "!!! CAN NOT READ CONFIGs !!!");
			e.printStackTrace();
		}
	}
}
