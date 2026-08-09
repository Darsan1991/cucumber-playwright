package utilities;

import attributes.Function;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static SecretKey key = new SecretKeySpec("12345678901234567890123456789012".getBytes(StandardCharsets.UTF_8), ALGORITHM);

    public static String encrypt(String text, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encrypted = cipher.doFinal(text.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String encryptedText, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decrypted = cipher.doFinal(
                Base64.getDecoder().decode(encryptedText)
        );

        return new String(decrypted);
    }

    @Function
    public static String encrypt(String text) throws Exception {
        return encrypt(text, key);
    }

    @Function
    public static String decrypt(String text) throws Exception {
        return decrypt(text, key);
    }

    public static void main(String[] args) throws Exception {
        var key = "Test1234";
        System.out.printf(encrypt(key));
        System.out.printf(decrypt(encrypt(key)));
    }

}
