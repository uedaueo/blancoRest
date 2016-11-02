package blanco.rest.common;

import blanco.rest.Exception.BlancoRestException;
import blanco.rest.resourcebundle.BlancoRestResourceBundle;
import blanco.rest.valueobject.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;


import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

//import haino.sample.HainoApiSampleGetResponse;

/**
 * Created by tueda on 15/10/05.
 */
abstract public class ApiBase {

    private BlancoRestResourceBundle fBundle = new BlancoRestResourceBundle();


    /*
         * 自動生成された API クラスで override されます
         */
    abstract protected String getGetRequestId();
    abstract protected String getGetResponseId();
    abstract protected String getPostRequestId();
    abstract protected String getPostResponseId();
    abstract protected String getPutRequestId();
    abstract protected String getPutResponseId();
    abstract protected String getDeleteRequestId();
    abstract protected String getDeleteResponseId();
    abstract protected ApiGetTelegram execute(ApiGetTelegram apiGetTelegram) throws BlancoRestException;
    abstract protected ApiPostTelegram execute(ApiPostTelegram apiPostTelegram) throws BlancoRestException;
    abstract protected ApiPutTelegram execute(ApiPutTelegram apiPutTelegram) throws BlancoRestException;
    abstract protected ApiDeleteTelegram execute(ApiDeleteTelegram apiDeleteTelegram) throws BlancoRestException;

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    private SessionManager sessionManager = new SessionManagerImpl();



    final private ApiTelegram sendTo(ApiTelegram request, String httpMethod) throws BlancoRestException {
        ApiTelegram response = null;
        CommonRequest commonRequest = new CommonRequest();
        commonRequest.settoken("dummy");
        commonRequest.setlang("ja");
        commonRequest.setrequest(request);

        CommonResponse commonResponse = null;

        ApiTelegram output = getOutputInstance(httpMethod);

        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(commonRequest);
            System.out.println("JSON: " + json);
            String url = Config.properties.getProperty(Config.apiUrlKey) + this.getClass().getSimpleName();
            BlancoHttpConnection conn = new BlancoHttpConnection(url);
            System.out.println("URL: " + url);
            String responseJson = conn.connect(json, httpMethod);

            System.out.println("Response: " + responseJson);

            ResponseDeserializer deserializer = new ResponseDeserializer();
            deserializer.setResponseClass(output);

            SimpleModule module =
                    new SimpleModule("PolymorphicAnimalDeserializerModule");
            module.addDeserializer(CommonResponse.class, deserializer);

            /*
             * 念のため作り直し
             */
            mapper = new ObjectMapper();
            mapper.registerModule(module);

            System.out.println("ApiBase#responseJson " + responseJson);
            commonResponse = (CommonResponse)mapper.readValue(responseJson, CommonResponse.class);
            System.out.println("ApiBase#commonResponse" + commonResponse);

            if(commonResponse != null){
                if ("SUCCESS".equalsIgnoreCase(commonResponse.getstatus())){
                    response = commonResponse.getresponse();
                }else {
                    Util.infoPrintln(LogLevel.LOG_DEBUG,"ApiBase#sendTo response status: " + commonResponse.getstatus());
                }
            }else{
                Util.infoPrintln(LogLevel.LOG_DEBUG,"ApiBase#sendTo readValue null");
            }



        } catch (JsonProcessingException e) {
            throw new BlancoRestException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /**
        HttpClient httpclient = new HttpClient();
        GetMethod httpget = new GetMethod("https://www.verisign.com/");
        try {
            httpclient.executeMethod(httpget);
            System.out.println(httpget.getStatusLine());
        } finally {
            httpget.releaseConnection();
        }

        response = getDummyResponse();
        **/
        return response;
    }

    final public ApiPostTelegram send(ApiPostTelegram request) throws BlancoRestException {
        if (request == null) {
            throw new BlancoRestException(
                    fBundle.getBlancorestErrorMsg01()
            );
        }

        if (!this.getPostRequestId().equalsIgnoreCase(request.getClass().getCanonicalName())) {
            throw new BlancoRestException(
                    fBundle.getBlancorestErrorMsg02(
                            this.getPostRequestId(), request.getClass().getCanonicalName())
            );
        }

        return (ApiPostTelegram) sendTo(request, "POST");
    }

    final public ApiGetTelegram send(ApiGetTelegram request) throws BlancoRestException {
        if (request == null) {
            throw new BlancoRestException(
                    fBundle.getBlancorestErrorMsg01()
            );
        }

        if (!this.getGetRequestId().equalsIgnoreCase(request.getClass().getCanonicalName())) {
            throw new BlancoRestException(
                    fBundle.getBlancorestErrorMsg02(
                            this.getGetRequestId(), request.getClass().getCanonicalName())
            );
        }
        return (ApiGetTelegram) sendTo(request, "GET");
    }

    final public ApiPutTelegram send(ApiPutTelegram request) throws BlancoRestException {
        if (request == null) {
            throw new BlancoRestException(
                    fBundle.getBlancorestErrorMsg01()
            );
        }

        if (!this.getPutRequestId().equalsIgnoreCase(request.getClass().getCanonicalName())) {
            throw new BlancoRestException(
                    fBundle.getBlancorestErrorMsg02(
                            this.getPutRequestId(), request.getClass().getCanonicalName())
            );
        }
        return (ApiPutTelegram) sendTo(request, "PUT");
    }

    final public ApiDeleteTelegram send(ApiDeleteTelegram request) throws BlancoRestException {
        if (request == null) {
            throw new BlancoRestException(
                    fBundle.getBlancorestErrorMsg01()
            );
        }

        if (!this.getDeleteRequestId().equalsIgnoreCase(request.getClass().getCanonicalName())) {
            throw new BlancoRestException(
                    fBundle.getBlancorestErrorMsg02(
                            this.getDeleteRequestId(), request.getClass().getCanonicalName())
            );
        }
        return (ApiDeleteTelegram) sendTo(request, "DELETE");
    }


    public ApiGetTelegram action(ApiGetTelegram apitelegram) throws BlancoRestException{
        Util.infoPrintln(LogLevel.LOG_DEBUG,"ApiBase#action ApiGetTelegram");
        return this.execute(apitelegram);

    }

    public ApiPutTelegram action(ApiPutTelegram apitelegram) throws BlancoRestException{
        Util.infoPrintln(LogLevel.LOG_DEBUG,"ApiBase#action ApiPutTelegram");
        return this.execute(apitelegram);

    }

    public ApiPostTelegram action(ApiPostTelegram apitelegram) throws BlancoRestException{
        Util.infoPrintln(LogLevel.LOG_DEBUG,"ApiBase#action ApiPostTelegram");
        return this.execute(apitelegram);

    }

    public ApiDeleteTelegram action(ApiDeleteTelegram apitelegram) throws BlancoRestException{
        Util.infoPrintln(LogLevel.LOG_DEBUG,"ApiBase#action ApiDeleteTelegram");
        return this.execute(apitelegram);

    }

    final public ApiTelegram getOutputInstance(String httpMethod) throws BlancoRestException {
        ApiTelegram response = null;


        String responseId = null;
        if ("GET".equalsIgnoreCase(httpMethod)){
            responseId = this.getGetResponseId();
        }else if("POST".equalsIgnoreCase(httpMethod)){
            responseId = this.getPostResponseId();
        } else if("PUT".equalsIgnoreCase(httpMethod)){
            responseId = this.getPutResponseId();
        }else if("DELETE".equalsIgnoreCase(httpMethod)){
            responseId = this.getDeleteResponseId();
        }else {
            Util.infoPrintln(LogLevel.LOG_CRIT,"No method specified");
            throw  new BlancoRestException("No method specified");
        }

        Class<?> clazz;
        try {
            clazz = Class.forName(responseId);

            Constructor<?> constructor =
                    clazz.getConstructor(new Class<?>[0]);

            // インスタンス生成
            response = (ApiTelegram) constructor.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return response;
    }

}
