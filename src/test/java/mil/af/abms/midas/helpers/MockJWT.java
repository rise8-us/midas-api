package mil.af.abms.midas.helpers;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MockJWT {

    private static final String JWT_HEADER = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    private static final String SECRET = "Secret for mock JWT";


    public static String get(Boolean withCert) {
        String header = encode(JWT_HEADER.getBytes(StandardCharsets.UTF_8));
        String jwtPayload = createJWTPayload(withCert);
        String headerAndPayload = header + "." + jwtPayload;
        String signature = hmacSha256(headerAndPayload, SECRET);
        return headerAndPayload + "." + signature;

    }

    public static String encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String createJWTPayload(Boolean withCert) {
        var jwtPayload = new JSONObject();
        var groups = new JSONArray();
        groups.put("mixer-IL2-admin");
        var zoneOffset = ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now());
        var cert = withCert ? "Grogu.Yoda.1234567890" : "";

        try {
            jwtPayload.put("usercertificate", cert);
            jwtPayload.put("sub", "abc-123");
            jwtPayload.put("name", "Grogu Yoda");
            jwtPayload.put("email", "grogu.yoda@af.mil");
            jwtPayload.put("exp", LocalDateTime.now().toEpochSecond(zoneOffset) + 6000L);
            jwtPayload.put("group-simple", groups);

        } catch (JSONException e) {
            Logger.getLogger(MockJWT.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }

        return encode(jwtPayload.toString().getBytes());

    }

    private static String hmacSha256(String data, String secret) {
        try {

            byte[] hash = secret.getBytes(StandardCharsets.UTF_8);

            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(hash, "HmacSHA256");
            sha256Hmac.init(secretKey);

            byte[] signedBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            return encode(signedBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            Logger.getLogger(MockJWT.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
    }

}
