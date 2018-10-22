package blanco.rest.common;

import java.security.MessageDigest;

/**
 * Created by haino on 2016/09/10.
 */
public class SessionManagerImpl implements SessionManager{
    @Override
    public boolean validate(String token) {
        return true;
    }

    @Override
    public String renew(String token ,String seed) {
        byte[] cipher_byte;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(seed.getBytes());
            cipher_byte = md.digest();
            StringBuilder sb = new StringBuilder(2 * cipher_byte.length);
            for (byte b : cipher_byte) {
                sb.append(String.format("%02x", b & 0xff));
            }
//            System.out.println(sb);
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
