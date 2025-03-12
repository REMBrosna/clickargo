package com.guudint.clickargo.clictruck.finacing.dao;

import java.util.Date;
import java.util.List;

import com.guudint.clickargo.clictruck.finacing.model.TCkCtDebitNote;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtDebitNoteDao extends GenericDao<TCkCtDebitNote, String> {
	

	public List<TCkCtDebitNote> findDebitNotes(Date beginDate, Date endDate, String fromAccnId) throws Exception;

	List<TCkCtDebitNote> findByJobId(String jobId) throws Exception;

}
