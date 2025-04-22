package com.acleda.company.student.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class PediUtility {
    private static final int IDLEN = 35;

    /**
     * Generate id. id= appType + System.nanoTime()
     *
     * @param appType
     * @return
     */
    public static String generateId(String appType) {
        String genId = appType + generateId();
        if (genId.length() > IDLEN) {
            genId = genId.substring(0, IDLEN);
        }
        return genId;
    }

    synchronized public static String generateIdForm(String appType) {
        String genId = appType + generateIdForm();
        if (genId.length() > IDLEN) {
            genId = genId.substring(0, IDLEN);
        }
        return genId;
    }

    public static String generateIdForm() {
        return StringUtils.EMPTY + System.nanoTime();
    }

    public static String generateId() {
        int ranNo = ThreadLocalRandom.current().nextInt(0, 99999 + 1);
        SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd");
        String genId = sdf.format(Calendar.getInstance().getTime()) + String.format("%05d", ranNo);
        return genId;
    }

    // PORTEDI-563
    public static String generateId(String initialString, String port) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String yyyyMMdd = sdf.format(new Date());
        int ranNo = new Random().nextInt(99999);
        return initialString + port + yyyyMMdd + ranNo;
    }

    public static String generateToken() {
        Date dateNow = new Date();
        int random = (int) (Math.random() * 1000);
        if (random < 100) {
            random += 100;
        }
        String ret = new SimpleDateFormat("yyMMdd-HHmmss-SSS").format(dateNow) + random;
        return ret;
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> jsonToMap(String jsonStr) throws JsonParseException, JsonMappingException, IOException {

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(jsonStr, Map.class);
        return map;
    }

}

