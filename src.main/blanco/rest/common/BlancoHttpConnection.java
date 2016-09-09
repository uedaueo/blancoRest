package blanco.rest.common;

import blanco.rest.Exception.BlancoRestException;
import blanco.rest.resourcebundle.BlancoRestResourceBundle;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
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
    public String connect(String strJson) throws BlancoRestException {

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
