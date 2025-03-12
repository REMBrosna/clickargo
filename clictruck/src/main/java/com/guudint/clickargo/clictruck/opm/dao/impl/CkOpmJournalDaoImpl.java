package com.guudint.clickargo.clictruck.opm.dao.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.guudint.clickargo.clictruck.opm.dao.CkOpmJournalDao;
import com.guudint.clickargo.clictruck.opm.model.TCkOpmJournal;
import com.guudint.clickargo.master.enums.JournalTxnType;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

@Service
public class CkOpmJournalDaoImpl extends GenericDaoImpl<TCkOpmJournal, String> implements CkOpmJournalDao {

	@Override
	public List<TCkOpmJournal> findByJobTruckId(String jobTruckId) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkOpmJournal.class);
		criteria.add(Restrictions.eq("opmjTxnRef", jobTruckId));
		return super.getByCriteria(criteria);
	}

	@Override
	public List<TCkOpmJournal> findByJobTruckIdAndJournalType(String jobTruckId, JournalTxnType journalTxtType)
			throws Exception {

		List<TCkOpmJournal> journalList = this.findByJobTruckId(jobTruckId);

		if (null == journalList || journalList.size() == 0) {
			return null;
		}

		return journalList.stream()
				.filter(journal -> journalTxtType.name().equalsIgnoreCase(journal.getTCkMstJournalTxnType().getJttId()))
				.collect(Collectors.toList());
	}

}
