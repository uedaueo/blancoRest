package blanco.rest.common;

import blanco.rest.BlancoRestConstants;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by tueda on 15/10/11.
 */
public class Config {

    public static Properties properties = new Properties();

    public Config(){
        System.out.println("Config");
        read(BlancoRestConstants.CONFIG_FILE);
    }

    public Config(String filename){
        read(filename);
    }


    /** API サーバのURL */
    public static final String apiUrlKey = "ApiUrl"; //"http://10.211.55.4/dapanda/";
    /** BASIC 認証id */
    public static final String authIdKey = "AuthId"; //"";
    /** BASIC 認証パスワード */
    public static final String authPasswdKey = "AuthPass"; //"";

    public static final String userAgentKey = "UserAgent"; //"CarecoAPI test client";

    /** HttpClientにおける socket の timeout 時間（秒） */
    public static final String socketTimeoutKey = "SocketTimeout";
    //public static int socketTimeout = 3;
    /** HttpClientにおける接続の timeout 時間（秒） */
    public static final String connectionTimeoutKey = "ConnectionTimeout";
    //public static int connectionTimeout = 3;

    /** 簡易なログレベル */
    public static final String logLevelKey = "Loglevel"; //"DEBUG";

    public static final String defaultPackageKey = "DefaultPackage";

    public static final String sessionManagerKey = "SessionManagerImplClass";

    //public static final String tokenKey = "Token"; //"dummy";

    //public static final String langKey = "Lang";//"ja";

    private void read(String filename) {
        try {
            System.out.println("read filename = " + filename);
            InputStream stream = new FileInputStream(filename);
            properties.loadFromXML(stream);
            stream.close();
            properties.list(System.out);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
