package com.guudint.clickargo.clictruck.planexec.trip.dao;

import java.util.Date;
import java.util.List;

import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoice;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtPlatformInvoiceDao extends GenericDao<TCkCtPlatformInvoice, String> {

	public List<TCkCtPlatformInvoice> findPlatformInvoices(Date beginDate, Date endDate) throws Exception;

	//List<TCkCtPlatformInvoice> findByJobId(String jobId, String accnType) throws Exception;
	
	List<TCkCtPlatformInvoice> findByJobIdAndInvTo(String jobId, String accnType) throws Exception;

	List<TCkCtPlatformInvoice> findByJobId(String jobId) throws Exception;

	List<TCkCtPlatformInvoice> findByInvDtIssue(String start) throws Exception;
	
	List<TCkCtPlatformInvoice> findByInvoiceNumber(String invNo) throws Exception;
	
	List<TCkCtPlatformInvoice> findByAccnIdAndStatus(String accnId, List<String> invStatus) throws Exception;
	
	List<TCkCtPlatformInvoice> findByPaidDateAndAccnType( Date beginDate, Date endDate, String accnType) throws Exception;
}
