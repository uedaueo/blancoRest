package blanco.rest.common;

import blanco.rest.Exception.BlancoRestException;
import blanco.rest.resourcebundle.BlancoRestResourceBundle;
import blanco.sample.valueobject.ApiTelegram;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by tueda on 15/10/05.
 */
abstract public class ApiBase {

    private BlancoRestResourceBundle fBundle = new BlancoRestResourceBundle();

    /*
     * 自動生成された API クラスで override されます
     */
    abstract protected String getRequestId();
    abstract protected String getResponseId();

    final public ApiTelegram send(ApiTelegram request) throws BlancoRestException {
        ApiTelegram response = null;

        if (request == null) {
            throw new BlancoRestException(
                fBundle.getBlancorestErrorMsg01()
            );
        }

        if (!this.getRequestId().equalsIgnoreCase(request.getClass().getCanonicalName())) {
            throw new BlancoRestException(
                    fBundle.getBlancorestErrorMsg02(
                            this.getRequestId(), request.getClass().getCanonicalName())
            );
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(request);
            System.out.println("JSON: " + json);
        } catch (JsonProcessingException e) {
            throw new BlancoRestException(e);
        }

        response = getDummyResponse();

        return response;
    }

    final public ApiTelegram getDummyResponse() {
        ApiTelegram response = null;

        String responseId = this.getResponseId();

        Class<?> clazz;
        try {
            clazz = Class.forName(responseId);

            Constructor<?> constructor =
                    clazz.getConstructor(new Class<?>[0]);

            // インスタンス生成
            response = (ApiTelegram)constructor.newInstance();
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
