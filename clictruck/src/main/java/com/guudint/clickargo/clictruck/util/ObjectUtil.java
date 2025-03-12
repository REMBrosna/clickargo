package com.guudint.clickargo.clictruck.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectUtil {

    private static Logger LOG = Logger.getLogger(ObjectUtil.class);

    public static <T> T mapToObject(Map<String, Object> map, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(map, clazz);
    }

    public static Map<String, Object> objectToMap(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(obj, new TypeReference<HashMap<String, Object>>() {
        });
    }

    public static String jsonToString(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException ex) {
            LOG.error(ex);
        }
        return "";
    }

    public static <T> T stringToObject(String value, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(value, clazz);
        } catch (JsonParseException e) {
            LOG.error(e);
        } catch (JsonMappingException e) {
            LOG.error(e);
        } catch (IOException e) {
            LOG.error(e);
        }
        return null;
    }
    
    public static String objToStr(Object obj) {
    	return (obj != null) ? String.valueOf(obj) : null;
    }
}
