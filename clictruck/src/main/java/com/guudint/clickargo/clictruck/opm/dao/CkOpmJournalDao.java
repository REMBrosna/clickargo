package com.guudint.clickargo.clictruck.opm.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.opm.model.TCkOpmJournal;
import com.guudint.clickargo.master.enums.JournalTxnType;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkOpmJournalDao  extends GenericDao<TCkOpmJournal, String> {

	public List<TCkOpmJournal> findByJobTruckId(String jobTruckId) throws Exception;
	
	public List<TCkOpmJournal> findByJobTruckIdAndJournalType(String jobTruckId, JournalTxnType journalTxtType) throws Exception;

}
