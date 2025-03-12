package com.guudint.clickargo.clictruck.admin.contract.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.guudint.clickargo.clictruck.admin.contract.dao.CkCtConAddAttrDao;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtConAddAttr;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;
import com.vcc.camelone.common.exception.ParameterException;

public class CkCtConAddAttrDaoImpl extends GenericDaoImpl<TCkCtConAddAttr, String> implements CkCtConAddAttrDao {

	@Override
	public List<TCkCtConAddAttr> getAdditionalAttributesByContract(String contractId) throws Exception {
		if (StringUtils.isBlank(contractId))
			throw new ParameterException("param contractId null or empty");
		String hql = "from TCkCtConAddAttr o where o.TCkCtContract.conId=:contractId and o.caaStatus=:status";
		Map<String, Object> params = new HashMap<>();
		params.put("contractId", contractId);
		params.put("status", RecordStatus.ACTIVE.getCode());
		return getByQuery(hql, params);
	}

}
