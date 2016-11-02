package blanco.rest.common;

import blanco.rest.Exception.BlancoRestException;
import blanco.rest.resourcebundle.BlancoRestResourceBundle;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by tueda on 15/10/10.
 */
public class BlancoHttpConnection {

    static private BlancoRestResourceBundle fBundle = new BlancoRestResourceBundle();

    /** BASIC 認証 id */
    String userId = null;
    /** BASIC 認証パスワード */
    String passwd = null;
    /** API URL */
    String strUrl = null;

    /** API への接続, 普通にcloseすれば大丈夫らしい */
    CloseableHttpClient client = null;
    /** API からの response, 普通にcloseすれば大丈夫らしい */
    CloseableHttpResponse response = null;

    private String resJson = null;

    /**
     * 引数無しのコンストラクタは認めない
     */
    private BlancoHttpConnection() {

    }

    /**
     * Constructor
     * @param argUrl
     */
    public BlancoHttpConnection(String argUrl) {
        this.strUrl = argUrl;
    }

    /**
     * Constructor
     * @param argUrl
     * @param argUserId
     * @param argPasswd
     */
    public BlancoHttpConnection(String argUrl, String argUserId, String argPasswd) {
        this.strUrl = argUrl;
        this.userId = argUserId;
        this.passwd = argPasswd;
    }

    /**
     * 終了時にConnectionを確実にクローズ
     */
    @Override
    public void finalize() {
        close();
    }

    /**
     * @param strJson APIへの送信電文
     * @return
     */

    /**
     * APIへ接続
     *
     * @param strJson APIへの送信電文
     * @return Response body. JSON 文字列を期待しています．
     * @throws BlancoRestException
     */
    public String connectGet(String strJson) throws BlancoRestException {

        String ret = null;

        if (this.strUrl == null) {
            return ret;
        }

        HttpGet get = null;
        try {
            get = new HttpGet(strUrl);
            get.setHeader("User-Agent", Config.properties.getProperty(Config.userAgentKey));

        /* data の設定 */
//            HttpEntity requestEntity = new ByteArrayEntity(strJson.getBytes("UTF-8"));
//            post.setEntity(requestEntity);

            client = HttpClients.createDefault();
            response = client.execute(get);

            if (response == null) {
                throw new BlancoRestException(fBundle.getBlancorestErrorMsg03());
            }

            if (response.getStatusLine().getStatusCode() > 300) {
                throw new BlancoRestException(fBundle.getBlancorestErrorMsg04(
                        "" + response.getStatusLine().getStatusCode()));
            }

            HttpEntity responseEntity = response.getEntity();
            ret = EntityUtils.toString(responseEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return ret;
    }

    /**
     * APIへ接続
     *
     * @param strJson APIへの送信電文
     * @return Response body. JSON 文字列を期待しています．
     * @throws BlancoRestException
     */
    public String connectPost(String strJson) throws BlancoRestException {

        String ret = null;

        if (this.strUrl == null) {
            return ret;
        }

        HttpPost post = null;
        try {
            post = new HttpPost(strUrl);
            post.setHeader("User-Agent", Config.properties.getProperty(Config.userAgentKey));

        /* data の設定 */
            HttpEntity requestEntity = new ByteArrayEntity(strJson.getBytes("UTF-8"));
            post.setEntity(requestEntity);

            client = HttpClients.createDefault();
            response = client.execute(post);

            if (response == null) {
                throw new BlancoRestException(fBundle.getBlancorestErrorMsg03());
            }

            if (response.getStatusLine().getStatusCode() > 300) {
                throw new BlancoRestException(fBundle.getBlancorestErrorMsg04(
                        "" + response.getStatusLine().getStatusCode()));
            }

            HttpEntity responseEntity = response.getEntity();
            ret = EntityUtils.toString(responseEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return ret;
    }
    /**
     * APIへ接続
     *
     * @param strJson APIへの送信電文
     * @return Response body. JSON 文字列を期待しています．
     * @throws BlancoRestException
     */
    public String connectPut(String strJson) throws BlancoRestException {

        String ret = null;

        if (this.strUrl == null) {
            return ret;
        }

        HttpPut put = null;
        try {
            put = new HttpPut(strUrl);
            put.setHeader("User-Agent", Config.properties.getProperty(Config.userAgentKey));

        /* data の設定 */
            HttpEntity requestEntity = new ByteArrayEntity(strJson.getBytes("UTF-8"));
            put.setEntity(requestEntity);

            client = HttpClients.createDefault();
            response = client.execute(put);

            if (response == null) {
                throw new BlancoRestException(fBundle.getBlancorestErrorMsg03());
            }

            if (response.getStatusLine().getStatusCode() > 300) {
                throw new BlancoRestException(fBundle.getBlancorestErrorMsg04(
                        "" + response.getStatusLine().getStatusCode()));
            }

            HttpEntity responseEntity = response.getEntity();
            ret = EntityUtils.toString(responseEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return ret;
    }
    /**
     * APIへ接続
     *
     * @param strJson APIへの送信電文
     * @return Response body. JSON 文字列を期待しています．
     * @throws BlancoRestException
     */
    public String connectDelete(String strJson) throws BlancoRestException {

        String ret = null;

        if (this.strUrl == null) {
            return ret;
        }

        HttpDelete delete = null;
        try {
            delete = new HttpDelete(strUrl);
            delete.setHeader("User-Agent", Config.properties.getProperty(Config.userAgentKey));

            client = HttpClients.createDefault();
            response = client.execute(delete);

            if (response == null) {
                throw new BlancoRestException(fBundle.getBlancorestErrorMsg03());
            }

            if (response.getStatusLine().getStatusCode() > 300) {
                throw new BlancoRestException(fBundle.getBlancorestErrorMsg04(
                        "" + response.getStatusLine().getStatusCode()));
            }

            HttpEntity responseEntity = response.getEntity();
            ret = EntityUtils.toString(responseEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return ret;
    }

    /**
     * APIへ接続
     *
     * @param strJson APIへの送信電文
     * @return Response body. JSON 文字列を期待しています．
     * @throws BlancoRestException
     */
    public String connect(String strJson, String httpMethod) throws BlancoRestException {

        String ret = null;

        if ("GET".equalsIgnoreCase(httpMethod)){
            ret = connectGet(strJson);
        }else if("POST".equalsIgnoreCase(httpMethod)){
            ret = connectPost(strJson);
        } else if("PUT".equalsIgnoreCase(httpMethod)){
            ret = connectPut(strJson);
        }else if("DELETE".equalsIgnoreCase(httpMethod)){
            ret = connectDelete(strJson);
        }else {
            Util.infoPrintln(LogLevel.LOG_CRIT,"No method specified");
            throw  new BlancoRestException("No method specified");
        }
        return ret;
    }

    /**
     * 接続を閉じます
     * @return
     */
    public boolean close() {
        boolean ret = false;
        try {
            if (response != null) {
                response.close();
                response = null;
            }
            if (client != null) {
                client.close();
                client = null;
            }
            ret = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
