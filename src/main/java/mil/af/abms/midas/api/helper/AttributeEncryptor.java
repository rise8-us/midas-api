package mil.af.abms.midas.api.helper;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Optional;

import mil.af.abms.midas.config.CustomProperty;

@Converter
public class AttributeEncryptor implements AttributeConverter<String, String> {

    private final CustomProperty property;
    private final Cipher cipher;
    private final SecretKey secretKey;
    private final IvParameterSpec ivSpec;

    private static final String AES = "AES/CBC/PKCS5Padding";

    public AttributeEncryptor(CustomProperty property) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException {
        this.property = property;
        String ketStr = Optional.ofNullable(property.getKey()).orElseThrow(IllegalStateException::new);
        String salt = Optional.ofNullable(property.getSalt()).orElseThrow(IllegalStateException::new);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec keySpec = new PBEKeySpec(ketStr.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey tmp = factory.generateSecret(keySpec);
        secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
        cipher = Cipher.getInstance(AES);
        ivSpec = new IvParameterSpec(new byte[16]);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}

