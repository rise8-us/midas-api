package mil.af.abms.midas.api.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        } catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException("No keycloak sub: value found");
        }
    }

    public static Map<String, String> getConditions(InputStream sonarqubeQualityStream) {
        Map<String, String> conditionMap = new HashMap<>();
        try {
            JsonNode conditions = dateMapper().readTree(sonarqubeQualityStream).get("projectStatus").get("conditions");
            conditions.iterator().forEachRemaining(c ->
                    conditionMap.put(c.get("metricKey").asText(), c.get("actualValue").asText()));
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }
        return conditionMap;
    }

    public static JsonNode convertToJsonNode(String input) {
        try {
            return dateMapper().readTree(dateMapper().getFactory().createParser(input));
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }

        return null;
    }

}
