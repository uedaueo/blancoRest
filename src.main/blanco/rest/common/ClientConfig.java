package blanco.rest.common;

/**
 * Created by tueda on 15/10/11.
 */
public class ClientConfig {

    /** API サーバのURL */
    public static String apiUrl = "http://10.211.55.4/dapanda/";
    /** BASIC 認証id */
    public static String authId = "";
    /** BASIC 認証パスワード */
    public static String authPasswd = "";

    public static String userAgent = "CarecoAPI test client";

    /** HttpClientにおける socket の timeout 時間（秒） */
    public static int socketTimeout = 3;
    /** HttpClientにおける接続の timeout 時間（秒） */
    public static int connectionTimeout = 3;

    /** 簡易なログレベル */
    public static String logLevel = "DEBUG";

    public static String token = "dummy";

    public static String lang = "ja";
}
