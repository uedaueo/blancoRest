package blanco.rest.test;

import blanco.rest.Exception.BlancoRestException;
import blanco.rest.api.TelegramSample;
import blanco.sample.restphp.RestSampleRequest;
import blanco.sample.restphp.RestSampleResponse;
import blanco.sample.valueobject.ObjectSample;

import java.util.ArrayList;

/**
 * Created by tueda on 15/10/08.
 */
public class TestClient {

    public static void main(String [] argv) {

        new TestClient().doTest();

    }

    public void doTest() {

        RestSampleRequest request = new RestSampleRequest();
        request.setField1("Field1 Test");
        request.setField2(999L);
        request.setField3(false);
        request.setField4(9.9999);
        ObjectSample obj00 = new ObjectSample();
        obj00.setbooleanField1(true);
        obj00.setstringField1("hoge");
        ObjectSample obj01 = new ObjectSample();
        obj01.setbooleanField1(true);
        obj01.setstringField1("hoge");
        ArrayList<ObjectSample> arrayObj = new ArrayList<>();
        arrayObj.add(obj00);
        arrayObj.add(obj01);
        request.setField5(arrayObj);
        request.setObjectSample(obj00);

        TelegramSample sample = new TelegramSample();
        RestSampleResponse response = null;
        try {
            response = (RestSampleResponse)sample.send(request);
        } catch (BlancoRestException e) {
            e.printStackTrace();
        }

        System.out.println(response);

    }

}
