package tech.xtack.api.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class AuthUtils {

    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest((System.getenv("SALT") + password)
                .getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encodedhash);
    }

    public static String generateAuthToken() throws NoSuchAlgorithmException {
        Random random = new Random(System.currentTimeMillis() - Long.parseLong(System.getenv("SEED_OFFSET")));
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(bytes);
        return bytesToHex(encodedhash);
    }

    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
