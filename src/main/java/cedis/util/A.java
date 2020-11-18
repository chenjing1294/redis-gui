package cedis.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class A {
    private static ObjectMapper mapper = new ObjectMapper();

    public static String prettyJSON(String s) throws JsonProcessingException {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readValue(s, Object.class));
    }
}
