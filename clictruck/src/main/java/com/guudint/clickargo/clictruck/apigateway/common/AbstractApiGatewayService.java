package com.guudint.clickargo.clictruck.apigateway.common;

import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ProcessingException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
public abstract class AbstractApiGatewayService<T> implements ApiGatewayService<T> {
	@Override
	public Object create(String accnId, Object o) throws Exception {
		throw new Exception("Not support!");
	}

	@Override
	public Optional<Object> getList(Map<String, String> params) throws Exception {
		return Optional.empty();
	}
	@Override
	public Optional<Object> getListByAccnId(String accnId, Map<String, String> params) throws Exception {
		if (StringUtils.isBlank(accnId)) {
			throw new Exception("Company account ID is required!");
		}
		return Optional.empty();
	}
}