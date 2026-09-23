package uz.mirix.queryguard;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class Fingerprints {
    private Fingerprints() {}
    public static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes, 0, 12);
        } catch (NoSuchAlgorithmException e) { throw new IllegalStateException("SHA-256 is unavailable", e); }
    }
}
