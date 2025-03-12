package com.guudint.clickargo.clictruck.common.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.common.model.TCkCtAccnInqReqDocs;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtAccnInqReqDocsDao extends GenericDao<TCkCtAccnInqReqDocs, String> {
	
	List<TCkCtAccnInqReqDocs> getDocsByAccnReq(String accnInqReqId) throws Exception;
}
