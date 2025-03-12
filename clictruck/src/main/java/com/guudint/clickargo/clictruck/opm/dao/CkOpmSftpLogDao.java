package com.guudint.clickargo.clictruck.opm.dao;

import com.guudint.clickargo.clictruck.opm.model.TCkOpmSftpLog;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkOpmSftpLogDao extends GenericDao<TCkOpmSftpLog, String> {

	TCkOpmSftpLog findByFileName(String fileName) throws Exception;
	
}
