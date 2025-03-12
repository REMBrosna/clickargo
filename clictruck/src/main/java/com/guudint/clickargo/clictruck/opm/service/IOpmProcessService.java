package com.guudint.clickargo.clictruck.opm.service;

import java.util.List;
import java.util.Map;

import com.guudint.clickargo.clictruck.opm.OpmException;
import com.guudint.clickargo.clictruck.opm.third.dto.OpmCreditReq;

public interface IOpmProcessService<T extends OpmCreditReq> {

	public List<T> parseFile(String file, Map<Integer, OpmException> errsMap) throws OpmException;

	public void validateReq(T req, String financer, Map<Integer, OpmException> errMap) throws OpmException;

	public void process(T req, String financer, Map<Integer, OpmException> errMap) throws OpmException;

	public String generateErrorFile(String originalFile, Map<Integer, OpmException> errsMap, String financer)
			throws Exception;

	public String getAccnIdList(List<T> reqList) throws Exception;

	public String getJobTruckIdList(List<T> reqList) throws Exception;

}
