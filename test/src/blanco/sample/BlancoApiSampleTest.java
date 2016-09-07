
package blanco.sample;


import blanco.rest.Exception.BlancoRestException;
import blanco.rest.common.ApiBase;
import blanco.rest.common.LogLevel;
import blanco.rest.common.Util;
import blanco.sample.valueobject.BlancoObjectSample;
import blanco.sample.valueobject.ObjectSample;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;


/**
 * Created by Blanco on 2016/08/22.
 */

public class BlancoApiSampleTest {

    @Before
    public void setUp() {
        // data の準備
        /*
        String token = "toke";
        String lang = "jp";

        BlancoApiSamplePostRequest request = new BlancoApiSamplePostRequest();
        request.setField1("field1");
        request.setField2(2L);
        request.setField3(true);
        request.setField4(4.5);
        ArrayList<BlancoObjectSample> aBlancoObjectSample = new ArrayList<>();
        request.setField5(aBlancoObjectSample);
        request.setField6("field6");
        BlancoObjectSample BlancoObjectSample = new BlancoObjectSample();
        request.setObjectSample(BlancoObjectSample);
        */




    }

    @Test
    public void doTest() {
        // test


        String token = "toke";
        String lang = "jp";

        BlancoApiSamplePostRequest request = new BlancoApiSamplePostRequest();

        request.setField1("field1");

        request.setField2(2L);

        request.setField3(true);

        request.setField4(4.5);

//        ArrayList<BlancoObjectSample> aBlancoObjectSample = request.getField5();
//        aBlancoObjectSample.add(BlancoObjectSample);
//        BlancoObjectSample sBlancoObjectSample = new BlancoObjectSample();
//        sBlancoObjectSample.setstringField1("a");
//        aBlancoObjectSample.add(sBlancoObjectSample);
//        request.setField5(aBlancoObjectSample);


        ObjectSample objectSample = new ObjectSample();
        objectSample.setstringField1("obj6");
        objectSample.setbooleanField1(true);
        objectSample.setintField1(61);
        ArrayList<String> arrayList6 = new ArrayList<>();
        arrayList6.add("arrayList61");
        arrayList6.add("arrayList62");
        objectSample.setarrayField(arrayList6);
        request.setField6(objectSample);

        BlancoObjectSample BlancoObjectSample = new BlancoObjectSample();
        BlancoObjectSample.setstringField1("BlancoObjectSample");
        //BlancoObjectSample.setbooleanField1(true);
        //BlancoObjectSample.setintField1(71);
        ArrayList<String> arrayList7 = new ArrayList<>();
        arrayList7.add("arrayList71");
        arrayList7.add("arrayList72");
        //BlancoObjectSample.setarrayField(arrayList7);
        request.setObjectSample(BlancoObjectSample);

        ArrayList<BlancoObjectSample> aBlancoObjectSample = new ArrayList<>();
        // 配列は幾つにしますか？

        for (int i = 0; i < 2; i++) {
            aBlancoObjectSample.add(new BlancoObjectSample());
        }

        // 配列の一つ目
        BlancoObjectSample obj = aBlancoObjectSample.get(0);
        obj.setstringField1("a");
        //obj.setbooleanField1(true);
        //obj.setintField1(51);
        //arrayField String ならaddするだけでいい
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("arrayList1");
        arrayList.add("arrayList2");
        //obj.setarrayField(arrayList);


        // 配列の二つ目
        obj = aBlancoObjectSample.get(1);
        obj.setstringField1("b");
        //obj.setbooleanField1(false);
        //obj.setintField1(52);
        //arrayField
        ArrayList<String> arr = new ArrayList<>();
        // 配列の要素数をいくつにしようか？
        arr.add("aaaa");
        arr.add("bbbb");
        arr.add("cccc");
        //obj.setarrayField(arr);

        request.setField5(aBlancoObjectSample);

        BlancoApiSample client = new BlancoApiSample();
        try {

            BlancoApiSamplePostResponse BlancoApiSamplePostResponse = (BlancoApiSamplePostResponse) client.send(request);
            if(BlancoApiSamplePostResponse == null){
                Util.infoPrintln(LogLevel.LOG_DEBUG,"BlancoApiSamplePostResponse null");
            }

            assertEquals("field1",BlancoApiSamplePostResponse.getResultField1());

            assertEquals(new Long(2),BlancoApiSamplePostResponse.getResultField2());


            assertEquals(new Boolean(true),BlancoApiSamplePostResponse.getResultField3());

            assertEquals(new Double(4.5),BlancoApiSamplePostResponse.getResultField4());

            //assertEquals(,BlancoApiSamplePostResponse.getResultField5());


            //assertEquals(request.setField6(),request.getField6());

            //assertEquals("7.0",request.getObjectSample());
        } catch (BlancoRestException e) {
            e.printStackTrace();
        }

        /**
        BlancoApiSamplePostResponse BlancoApiSamplePostResponse = new BlancoApiSamplePostResponse();

        assertEquals("toke",token);
        assertEquals("ja",lang);

        assertEquals("field1",BlancoApiSamplePostResponse.getResultField1());

        assertEquals("2L",BlancoApiSamplePostResponse.getResultField2());

        assertEquals("true",BlancoApiSamplePostResponse.getResultField3());

        assertEquals("4.5",BlancoApiSamplePostResponse.getResultField4());

        //assertEquals(,BlancoApiSamplePostResponse.getResultField5());


        //assertEquals(request.setField6(),request.getField6());

        assertEquals("7.0",request.getObjectSample());
         **/


    }

    @After
    public void tearDown() {
        // 後始末
        System.gc();
    }

}
