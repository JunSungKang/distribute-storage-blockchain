package com.jskang.storageclient.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jskang.storageclient.response.ResponseData;
import java.util.Map;

public class Converter {

    private static ObjectMapper mapper = new ObjectMapper();

    /**
     * Object to json string change.
     *
     * @param map json object.
     * @return json string.
     */
    public static String objToJson(Object map) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Object to Custom Object change.
     *
     * @param json          json object.
     * @param typeReference custom object, new TypeReference<?>(){}.
     * @return custom object.
     */
    public static Object objToObj(Object json, TypeReference typeReference) {
        return mapper.convertValue(json, typeReference);
    }

    public static Map jsonToMap(String json) {
        try {
            return mapper.readValue(json, new TypeReference<Map>() {
            });
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Json string to custom object change.
     *
     * @param json          json string.
     * @param typeReference custom object, new TypeReference<?>(){}.
     * @return custom object.
     */
    public static Object jsonToObj(String json, TypeReference typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Json string to ResponseData change.
     *
     * @param json json string.
     * @return custom object.
     */
    public static ResponseData jsonToResponseData(String json) {
        try {
            return mapper.readValue(json, new TypeReference<ResponseData>() {
            });
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
}
