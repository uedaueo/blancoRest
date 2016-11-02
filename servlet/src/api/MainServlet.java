package api;
import blanco.rest.BlancoRestConstants;
import blanco.rest.Exception.BlancoRestException;
import blanco.rest.common.*;
import blanco.rest.valueobject.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.*;
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


	}

	
	public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException,IOException	{
		String apiPackage = Config.properties.getProperty(Config.defaultPackageKey);
		if(apiPackage == null){
			apiPackage = BlancoRestConstants.DEFAULT_PACKAGE;
		}
		String api = apiPackage + "." + request.getParameter("api");
		Util.infoPrintln(LogLevel.LOG_DEBUG, "doGet " + api);
		try {
			//クラスの名前文字列からクラスのインスタンスを生成
			Class apiClass = Class.forName(api);
			ApiBase apiClassInstance = (ApiBase) apiClass.newInstance();
			Util.infoPrintln(LogLevel.LOG_DEBUG, apiClassInstance.toString());

			createSessionManager(apiClassInstance);

			StringBuilder sb = new StringBuilder();
			//ボディ部分の情報を取得
			BufferedReader reader = request.getReader();
			try {
				String line;
				while ((line = reader.readLine()) != null) {//1行ずつ読んで、空行がくるまで
					sb.append(line).append('\n');//空行があった部分に空行を入れる
					Util.infoPrintln(LogLevel.LOG_DEBUG, "sb");
				}
			} finally {
				reader.close();
			}
			String jsonString = sb.toString();//sbをString型に変換
			Util.infoPrintln(LogLevel.LOG_DEBUG, "jsonString = " + jsonString);

			/* HTTP body の JSON を request 電文に詰め替える
			 */
			String strRequestClass = api + "GetRequest";
			Class<?> requestClass = Class.forName(strRequestClass);
			ApiTelegram requestClassInstance = (ApiTelegram) requestClass.newInstance();

			String strResponseClass = api + "GetResponse";
			Class<?> responseClass = Class.forName(strResponseClass);
			ApiTelegram responseClassInstance = (ApiTelegram) responseClass.newInstance();

			ObjectMapper mapper = new ObjectMapper();
			SimpleModule module = new SimpleModule();

			RequestDeserializer apiDeserializer = new RequestDeserializer();
			apiDeserializer.setRequestClass(requestClassInstance);

			module.addDeserializer(CommonRequest.class, apiDeserializer);
			mapper.registerModule(module);
			CommonRequest readValue = (CommonRequest) mapper.readValue(jsonString, CommonRequest.class);
			Util.infoPrintln(LogLevel.LOG_DEBUG, "JSON → Java ms = " + readValue);


			// JSON → Java に変換
			//ApiPostTelegram telegramRequest = (ApiPostTelegram) mapper.readValue(jsonString,requestClass);
			//Util.infoPrintln(LogLevel.LOG_DEBUG, "JSON → Java ms = " + telegramRequest);

			SessionManager sessionManager = apiClassInstance.getSessionManager();
			if(sessionManager.validate(readValue.gettoken()) == false){
				Util.infoPrintln(LogLevel.LOG_EMERG,"sessionManager.validate = false");
				throw new BlancoRestException("sessionManager.validate = false");
			}

			/*
			API の呼び出し
			 */
			ApiGetTelegram telegramResponse = apiClassInstance.action((ApiGetTelegram) readValue.getrequest());
			// Java → JSON に変換

			CommonResponse commonResponse =  new CommonResponse();
			Util.infoPrintln(LogLevel.LOG_DEBUG,"telegramResponse = " + telegramResponse);
			commonResponse.setresponse(telegramResponse);
			commonResponse.setstatus("SUCCESS");
			long currentTimeMillis = System.currentTimeMillis();
			String seed = "" + currentTimeMillis + random.nextDouble();
			commonResponse.settoken(sessionManager.renew(readValue.gettoken(), seed));
			commonResponse.setlang(readValue.getlang());
			String json = mapper.writeValueAsString(commonResponse);
			Util.infoPrintln(LogLevel.LOG_DEBUG,"Java → JSON json = " + json);
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

	public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException,IOException {
		String apiPackage = Config.properties.getProperty(Config.defaultPackageKey);
		if(apiPackage == null){
			apiPackage = BlancoRestConstants.DEFAULT_PACKAGE;
		}
		String api = apiPackage + "." + request.getParameter("api");
		Util.infoPrintln(LogLevel.LOG_DEBUG,"doPost " + api);
		try {
			//クラスの名前文字列からクラスのインスタンスを生成
			Class apiClass = Class.forName(api);
			ApiBase apiClassInstance = (ApiBase) apiClass.newInstance();
			Util.infoPrintln(LogLevel.LOG_DEBUG,apiClassInstance.toString());

			createSessionManager(apiClassInstance);

			StringBuilder sb = new StringBuilder();
			//ボディ部分の情報を取得
			BufferedReader reader = request.getReader();
			try {
				String line;
				while ((line = reader.readLine()) != null) {//1行ずつ読んで、空行がくるまで
					sb.append(line).append('\n');//空行があった部分に空行を入れる
					Util.infoPrintln(LogLevel.LOG_DEBUG,"sb");
				}
			} finally {
				reader.close();
			}
			String jsonString = sb.toString();//sbをString型に変換
			Util.infoPrintln(LogLevel.LOG_DEBUG,"Main jsonString = " + jsonString);


			/* HTTP body の JSON を request 電文に詰め替える
			 */
			String strRequestClass = api + "PostRequest";
			Class<?> requestClass = Class.forName(strRequestClass);
			ApiTelegram requestClassInstance = (ApiTelegram) requestClass.newInstance();

			String strResponseClass = api + "PostResponse";
			Class<?> responseClass = Class.forName(strResponseClass);
			ApiTelegram responseClassInstance = (ApiTelegram) responseClass.newInstance();

			ObjectMapper mapper = new ObjectMapper();
			SimpleModule module = new SimpleModule();

			RequestDeserializer apiDeserializer = new RequestDeserializer();
			apiDeserializer.setRequestClass(requestClassInstance);

			module.addDeserializer(CommonRequest.class, apiDeserializer);
			mapper.registerModule(module);
			CommonRequest readValue = (CommonRequest) mapper.readValue(jsonString, CommonRequest.class);
			Util.infoPrintln(LogLevel.LOG_DEBUG, "JSON → Java ms = " + readValue);


			// JSON → Java に変換
			//ApiPostTelegram telegramRequest = (ApiPostTelegram) mapper.readValue(jsonString,requestClass);
			//Util.infoPrintln(LogLevel.LOG_DEBUG, "JSON → Java ms = " + telegramRequest);

			SessionManager sessionManager = apiClassInstance.getSessionManager();
			if(sessionManager.validate(readValue.gettoken()) == false){
				Util.infoPrintln(LogLevel.LOG_EMERG,"sessionManager.validate = false");
				throw new BlancoRestException("sessionManager.validate = false");
			}

			/*
			API の呼び出し
			 */
			ApiPostTelegram telegramResponse = apiClassInstance.action((ApiPostTelegram) readValue.getrequest());
			// Java → JSON に変換

			CommonResponse commonResponse =  new CommonResponse();
			Util.infoPrintln(LogLevel.LOG_DEBUG,"telegramResponse = " + telegramResponse);
			commonResponse.setresponse(telegramResponse);
			commonResponse.setstatus("SUCCESS");
			long currentTimeMillis = System.currentTimeMillis();
			String seed = "" + currentTimeMillis + random.nextDouble();
			commonResponse.settoken(sessionManager.renew(readValue.gettoken(), seed));
			commonResponse.setlang(readValue.getlang());
			String json = mapper.writeValueAsString(commonResponse);
			Util.infoPrintln(LogLevel.LOG_DEBUG,"Java → JSON json = " + json);
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

	public void doPut(HttpServletRequest request, HttpServletResponse response)throws ServletException,IOException	{
		String apiPackage = Config.properties.getProperty(Config.defaultPackageKey);
		if(apiPackage == null){
			apiPackage = BlancoRestConstants.DEFAULT_PACKAGE;
		}
		String api = apiPackage + "." + request.getParameter("api");
		Util.infoPrintln(LogLevel.LOG_DEBUG,"doPut " + api);
		try {
			//クラスの名前文字列からクラスのインスタンスを生成
			Class apiClass = Class.forName(api);
			ApiBase apiClassInstance = (ApiBase) apiClass.newInstance();
			Util.infoPrintln(LogLevel.LOG_DEBUG,apiClassInstance.toString());

			createSessionManager(apiClassInstance);

			StringBuilder sb = new StringBuilder();
			//ボディ部分の情報を取得
			BufferedReader reader = request.getReader();
			try {
				String line;
				while ((line = reader.readLine()) != null) {//1行ずつ読んで、空行がくるまで
					sb.append(line).append('\n');//空行があった部分に空行を入れる
					Util.infoPrintln(LogLevel.LOG_DEBUG,"sb");
				}
			} finally {
				reader.close();
			}
			String jsonString = sb.toString();//sbをString型に変換
			Util.infoPrintln(LogLevel.LOG_DEBUG,"Main jsonString = " + jsonString);


			/* HTTP body の JSON を request 電文に詰め替える
			 */
			String strRequestClass = api + "PutRequest";
			Class<?> requestClass = Class.forName(strRequestClass);
			ApiTelegram requestClassInstance = (ApiTelegram) requestClass.newInstance();

			String strResponseClass = api + "PutResponse";
			Class<?> responseClass = Class.forName(strResponseClass);
			ApiTelegram responseClassInstance = (ApiTelegram) responseClass.newInstance();

			ObjectMapper mapper = new ObjectMapper();
			SimpleModule module = new SimpleModule();

			RequestDeserializer apiDeserializer = new RequestDeserializer();
			apiDeserializer.setRequestClass(requestClassInstance);

			module.addDeserializer(CommonRequest.class, apiDeserializer);
			mapper.registerModule(module);
			CommonRequest readValue = (CommonRequest) mapper.readValue(jsonString, CommonRequest.class);
			Util.infoPrintln(LogLevel.LOG_DEBUG, "JSON → Java ms = " + readValue);


			// JSON → Java に変換
			//ApiPostTelegram telegramRequest = (ApiPostTelegram) mapper.readValue(jsonString,requestClass);
			//Util.infoPrintln(LogLevel.LOG_DEBUG, "JSON → Java ms = " + telegramRequest);

			SessionManager sessionManager = apiClassInstance.getSessionManager();
			if(sessionManager.validate(readValue.gettoken()) == false){
				Util.infoPrintln(LogLevel.LOG_EMERG,"sessionManager.validate = false");
				throw new BlancoRestException("sessionManager.validate = false");
			}

			/*
			API の呼び出し
			 */
			ApiPutTelegram telegramResponse = apiClassInstance.action((ApiPutTelegram) readValue.getrequest());
			// Java → JSON に変換

			CommonResponse commonResponse =  new CommonResponse();
			Util.infoPrintln(LogLevel.LOG_DEBUG,"telegramResponse = " + telegramResponse);
			commonResponse.setresponse(telegramResponse);
			commonResponse.setstatus("SUCCESS");
			long currentTimeMillis = System.currentTimeMillis();
			String seed = "" + currentTimeMillis + random.nextDouble();
			commonResponse.settoken(sessionManager.renew(readValue.gettoken(), seed));
			commonResponse.setlang(readValue.getlang());
			String json = mapper.writeValueAsString(commonResponse);
			Util.infoPrintln(LogLevel.LOG_DEBUG,"Java → JSON json = " + json);
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

	public void doDelete(HttpServletRequest request, HttpServletResponse response)throws ServletException,IOException	{
		String apiPackage = Config.properties.getProperty(Config.defaultPackageKey);
		if(apiPackage == null){
			apiPackage = BlancoRestConstants.DEFAULT_PACKAGE;
		}
		String api = apiPackage + "." + request.getParameter("api");
		Util.infoPrintln(LogLevel.LOG_DEBUG,"doDelete " + api);
		try {
			//クラスの名前文字列からクラスのインスタンスを生成
			Class apiClass = Class.forName(api);
			ApiBase apiClassInstance = (ApiBase) apiClass.newInstance();
			Util.infoPrintln(LogLevel.LOG_DEBUG,apiClassInstance.toString());

			createSessionManager(apiClassInstance);

			StringBuilder sb = new StringBuilder();
			//ボディ部分の情報を取得
			BufferedReader reader = request.getReader();
			try {
				String line;
				while ((line = reader.readLine()) != null) {//1行ずつ読んで、空行がくるまで
					sb.append(line).append('\n');//空行があった部分に空行を入れる
					Util.infoPrintln(LogLevel.LOG_DEBUG,"sb");
				}
			} finally {
				reader.close();
			}
			String jsonString = sb.toString();//sbをString型に変換
			Util.infoPrintln(LogLevel.LOG_DEBUG,"Main jsonString = " + jsonString);


			/* HTTP body の JSON を request 電文に詰め替える
			 */
			String strRequestClass = api + "DeleteRequest";
			Class<?> requestClass = Class.forName(strRequestClass);
			ApiTelegram requestClassInstance = (ApiTelegram) requestClass.newInstance();

			String strResponseClass = api + "DeleteResponse";
			Class<?> responseClass = Class.forName(strResponseClass);
			ApiTelegram responseClassInstance = (ApiTelegram) responseClass.newInstance();

			ObjectMapper mapper = new ObjectMapper();
			SimpleModule module = new SimpleModule();

			RequestDeserializer apiDeserializer = new RequestDeserializer();
			apiDeserializer.setRequestClass(requestClassInstance);

			module.addDeserializer(CommonRequest.class, apiDeserializer);
			mapper.registerModule(module);
			CommonRequest readValue = (CommonRequest) mapper.readValue(jsonString, CommonRequest.class);
			Util.infoPrintln(LogLevel.LOG_DEBUG, "JSON → Java ms = " + readValue);


			// JSON → Java に変換
			//ApiPostTelegram telegramRequest = (ApiPostTelegram) mapper.readValue(jsonString,requestClass);
			//Util.infoPrintln(LogLevel.LOG_DEBUG, "JSON → Java ms = " + telegramRequest);

			SessionManager sessionManager = apiClassInstance.getSessionManager();
			if(sessionManager.validate(readValue.gettoken()) == false){
				Util.infoPrintln(LogLevel.LOG_EMERG,"sessionManager.validate = false");
				throw new BlancoRestException("sessionManager.validate = false");
			}

			/*
			API の呼び出し
			 */
			ApiDeleteTelegram telegramResponse = apiClassInstance.action((ApiDeleteTelegram) readValue.getrequest());
			// Java → JSON に変換

			CommonResponse commonResponse =  new CommonResponse();
			Util.infoPrintln(LogLevel.LOG_DEBUG,"telegramResponse = " + telegramResponse);
			commonResponse.setresponse(telegramResponse);
			commonResponse.setstatus("SUCCESS");
			long currentTimeMillis = System.currentTimeMillis();
			String seed = "" + currentTimeMillis + random.nextDouble();
			commonResponse.settoken(sessionManager.renew(readValue.gettoken(), seed));
			commonResponse.setlang(readValue.getlang());
			String json = mapper.writeValueAsString(commonResponse);
			Util.infoPrintln(LogLevel.LOG_DEBUG,"Java → JSON json = " + json);
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

	private void createSessionManager(ApiBase apiClassInstance) {
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
		apiClassInstance.setSessionManager(sessionManagerImpl);
	}

}
