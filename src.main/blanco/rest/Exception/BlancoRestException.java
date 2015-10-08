package blanco.rest.Exception;

/**
 * Created by tueda on 15/10/09.
 */
public class BlancoRestException extends Exception {
    public BlancoRestException(String msg) {
        super(msg);
    }

    public BlancoRestException(Exception e) {
        super(e);
    }
}
