package com.guudint.clickargo.clictruck.apigateway.common;

import com.vcc.camelone.cac.model.Principal;

import java.util.List;
import java.util.Map;
import java.util.Optional;
/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
public interface ApiGatewayService<T> {
    Object create(String accnId, Object o) throws Exception;
    Optional<Object> getList(Map<String, String> params) throws Exception;
    Optional<Object> getListByAccnId(String accnId, Map<String, String> params) throws Exception;

}