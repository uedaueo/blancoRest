package blanco.sample;

import blanco.rest.common.LogLevel;
import blanco.rest.common.Util;
import blanco.rest.valueobject.*;


/**
 * Created by Blanco on 2016/08/05.
 */
public class BlancoApiSample extends AbstractBlancoApiSample{

    @Override
    public String toString() {
        return "BlancoApiSample class";
    }

    @Override
    protected BlancoApiSampleGetResponse process(BlancoApiSampleGetRequest argBlancoApiSampleGetRequest) {
        Util.infoPrintln(LogLevel.LOG_DEBUG,"BlancoApiSample#process getResponse");

        BlancoApiSampleGetResponse response = new BlancoApiSampleGetResponse();

        response.setResultField1("BlancoApiSampleGetResponse result");

        return response;
    }

    @Override
    protected BlancoApiSamplePostResponse process(BlancoApiSamplePostRequest argBlancoApiSamplePostRequest) {
        Util.infoPrintln(LogLevel.LOG_DEBUG,"BlancoApiSample#process postResponse");

        BlancoApiSamplePostResponse response = new BlancoApiSamplePostResponse();

        response.setResultField1(argBlancoApiSamplePostRequest.getField1());

        response.setResultField2(argBlancoApiSamplePostRequest.getField2());

        response.setResultField3(argBlancoApiSamplePostRequest.getField3());

        response.setResultField4(argBlancoApiSamplePostRequest.getField4());

        response.setResultField5(argBlancoApiSamplePostRequest.getField5());

        response.setResultField6(argBlancoApiSamplePostRequest.getField6());

        response.setObjectSample(argBlancoApiSamplePostRequest.getObjectSample());

        return response;
    }

    @Override
    protected BlancoApiSamplePutResponse process(BlancoApiSamplePutRequest argBlancoApiSamplePutRequest) {
        Util.infoPrintln(LogLevel.LOG_DEBUG,"BlancoApiSample#process putResponse");

        BlancoApiSamplePutResponse response = new BlancoApiSamplePutResponse();

        response.setResultField1("BlancoApiSamplePutResponse result");

        return response;
    }

    @Override
    protected BlancoApiSampleDeleteResponse process(BlancoApiSampleDeleteRequest argBlancoApiSampleDeleteRequest) {
        Util.infoPrintln(LogLevel.LOG_DEBUG,"BlancoApiSample#process deleteGet");

        BlancoApiSampleDeleteResponse response = new BlancoApiSampleDeleteResponse();

        response.setResultField1("BlancoApiSampleDeleteResponse result");

        return response;
    }

    @Override
    protected ApiGetTelegram execute(ApiGetTelegram apiGetTelegram) {
        return null;
    }

    @Override
    protected ApiPostTelegram execute(ApiPostTelegram apiPostTelegram) {
        return null;
    }

    @Override
    protected ApiPutTelegram execute(ApiPutTelegram apiPutTelegram) {
        return null;
    }

    @Override
    protected ApiDeleteTelegram execute(ApiDeleteTelegram apiDeleteTelegram) {
        return null;
    }
}
