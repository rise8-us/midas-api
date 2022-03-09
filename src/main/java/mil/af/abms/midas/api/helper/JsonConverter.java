package mil.af.abms.midas.api.helper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

//Pulled from https://medium.com/@bcarunmail/using-json-datatype-in-relational-database-to-develop-flexible-configurable-software-6a169e9ce75b
@Converter
public class JsonConverter implements AttributeConverter<Map<String, Object>, String> {

    private static final ObjectMapper mapper;

    static {
        // To avoid instantiating ObjectMapper again and again.
        mapper = new ObjectMapper();
    }

    @Override
    public String convertToDatabaseColumn(Map<String, Object> data) {
        if (null == data) {
            // You may return null if you prefer that style
            return "{}";
        }

        try {
            return mapper.writeValueAsString(data);

        } catch (IOException e) {
            throw new IllegalArgumentException("Error converting map to JSON", e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String s) {
        if (null == s) {
            // You may return null if you prefer that style
            return new HashMap<>();
        }

        try {
            return mapper.readValue(s, new TypeReference<>() { });

        } catch (IOException e) {
            throw new IllegalArgumentException("Error converting JSON to map", e);
        }
    }
}
