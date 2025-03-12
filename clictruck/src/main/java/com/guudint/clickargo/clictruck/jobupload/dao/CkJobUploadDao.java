package com.guudint.clickargo.clictruck.jobupload.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.jobupload.model.TCkJobUpload;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkJobUploadDao extends GenericDao<TCkJobUpload, String> {

	List<TCkJobUpload> findByAccn(String accnId) throws Exception;
}
