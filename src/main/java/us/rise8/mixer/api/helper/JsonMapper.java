package us.rise8.mixer.api.helper;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonMapper {

    private JsonMapper() {
        throw new IllegalStateException("Utility Class");
    }

    private static final String KEYCLOAK_UID = "keycloakUid";

    public static ObjectMapper dateMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    public static String getKeycloakUidFromAuth(Authentication authentication) {
        try {
            JsonNode tokenInfo = dateMapper().readTree(authentication.getName());
            return tokenInfo.get(KEYCLOAK_UID).asText();
        } catch (JsonProcessingException e) {
            throw new AuthenticationCredentialsNotFoundException("No keycloak sub: value found");
        }
    }
}
