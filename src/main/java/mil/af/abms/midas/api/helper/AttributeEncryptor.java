package mil.af.abms.midas.api.helper;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;

import mil.af.abms.midas.config.CustomProperty;

@Converter
public class AttributeEncryptor implements AttributeConverter<String, String> {

    private static final int AES_KEY_SIZE = 256;
    private static final int ITERATION_COUNT = 65536;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    private final Random random = new SecureRandom();
    private final byte[] iv = new byte[GCM_IV_LENGTH];
    private final Cipher cipher;
    private final SecretKey secretKey;

    public AttributeEncryptor(CustomProperty property) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException {
        String keyStr = Optional.ofNullable(property.getKey()).orElseThrow(IllegalStateException::new);
        String salt = Optional.ofNullable(property.getSalt()).orElseThrow(IllegalStateException::new);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec keySpec = new PBEKeySpec(keyStr.toCharArray(), salt.getBytes(), ITERATION_COUNT, AES_KEY_SIZE);
        SecretKey tmp = factory.generateSecret(keySpec);

        secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
        cipher = Cipher.getInstance("AES/GCM/NoPadding");
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if(attribute == null || attribute.isEmpty()) { return null; }
        try {
            random.nextBytes(iv);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
            String eString = Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
            String ivStr = Base64.getEncoder().encodeToString(iv);
            return  ivStr + eString;

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) { return null; }
        try {
            byte[] ivFromStr = Base64.getDecoder().decode(dbData.substring(0, 16));
            String cipherStr = dbData.substring(16, dbData.length());
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, ivFromStr);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(cipherStr)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}

