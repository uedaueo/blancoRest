package blanco.rest.common;

/**
 * token 文字列を利用したセッション管理を提供します
 *
 * Created by haino on 2016/09/10.
 */
public interface SessionManager {

    /**
     * token のバリデート
     * @param token String token 文字列
     * @return boolean
     */
    public boolean validate(String token);

    /**
     * token の文字列を返す
     * @param token String token 文字列
     * @return String
     */
    public String renew(String token, String seed);

}
