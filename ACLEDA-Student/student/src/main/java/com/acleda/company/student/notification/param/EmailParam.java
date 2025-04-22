package com.acleda.company.student.notification.param;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Setter
@Getter
public class EmailParam {

    private String[] to;
    private String[] cc;
    private HashMap<String, String> contentFields;
    private Long templateId;

    public String toJson() throws Exception {
        ObjectMapper objMapper = new ObjectMapper();
        objMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAll();
        FilterProvider filters = new SimpleFilterProvider().addFilter("EmailParamFilter", filter).setFailOnUnknownId(false);
        objMapper.setFilterProvider(filters);
        return objMapper.writeValueAsString(this);
    }
}
