package api;
import blanco.rest.BlancoRestConstants;
import blanco.rest.common.*;
import blanco.rest.valueobject.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class MainServlet extends HttpServlet{

	@Override
	public void init(){
		System.out.println("MainServlet#init()");
		new Config();
	}

	
	public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException,IOException	{
		String apiPackage = Config.properties.getProperty(Config.defaultPackageKey);
		if(apiPackage == null){
			apiPackage = BlancoRestConstants.DEFAULT_PACKAGE;
		}
		String api = apiPackage + "." + request.getParameter("api");
		Util.infoPrintln(LogLevel.LOG_DEBUG,api);
		System.out.println("api = " + api);
		try {
			//クラスの名前文字列からクラスのインスタンスを生成
			Class apiClass = Class.forName(api);
			ApiBase apiClassInstance = (ApiBase) apiClass.newInstance();
			Util.infoPrintln(LogLevel.LOG_DEBUG,apiClassInstance.toString());
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
			Util.infoPrintln(LogLevel.LOG_DEBUG,"jsonString = " + jsonString);

			/* HTTP body の JSON を request 電文に詰め替える
			 */
			String strRequestClass = api + "GetRequest";
			Class requestClass = Class.forName(strRequestClass);
			ApiGetTelegram telegramRequest = (ApiGetTelegram) requestClass.newInstance();


			/*
			API の呼び出し
			 */
			ApiGetTelegram telegramResponse = apiClassInstance.action(telegramRequest);
			PrintWriter out = response.getWriter();
			out.println(jsonString);
			out.close();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
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




			/*
			API の呼び出し
			 */
			ApiPostTelegram telegramResponse = apiClassInstance.action((ApiPostTelegram) readValue.getrequest());
			// Java → JSON に変換
			CommonResponse commonResponse =  new CommonResponse();
			commonResponse.setresponse(telegramResponse);
			commonResponse.setstatus("SUCCESS");
			commonResponse.settoken(readValue.gettoken());
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
		}


	}

	public void doPut(HttpServletRequest request, HttpServletResponse response)throws ServletException,IOException	{
		String apiPackage = Config.properties.getProperty(Config.defaultPackageKey);
		if(apiPackage == null){
			apiPackage = BlancoRestConstants.DEFAULT_PACKAGE;
		}
               	
	}

	public void doDelete(HttpServletRequest request, HttpServletResponse response)throws ServletException,IOException	{
		String apiPackage = Config.properties.getProperty(Config.defaultPackageKey);
		if(apiPackage == null){
			apiPackage = BlancoRestConstants.DEFAULT_PACKAGE;
		}
                
	}

}
