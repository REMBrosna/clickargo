package com.guudint.clickargo.clictruck.admin.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.admin.dao.AccnDao;
import com.guudint.clickargo.clictruck.admin.dto.Parties;
import com.guudint.clickargo.clictruck.admin.service.ClicTruckAccnService;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.master.model.TMstAccnType;

public class AccnServiceImpl implements ClicTruckAccnService {

	@Autowired
	private AccnDao accnDao;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<Parties> getParties(Parties parties) throws Exception {
		TMstAccnType tMstAccnType = new TMstAccnType();
		tMstAccnType.setAtypId(parties.getPartyId());
		TCoreAccn tCoreAccn = new TCoreAccn();
		tCoreAccn.setAccnName(parties.getPartyName());
		tCoreAccn.setTMstAccnType(tMstAccnType);
		List<TCoreAccn> tCoreAccns = accnDao.findByField(tCoreAccn, parties.getOffset(), parties.getLimit());
		List<Parties> partiesList = new ArrayList<>();
		for (TCoreAccn coreAccn : tCoreAccns) {
			Parties party = new Parties();
			party.setPartyId(coreAccn.getAccnId());
			party.setPartyName(coreAccn.getAccnName());
			party.setPartyType(coreAccn.getTMstAccnType().getAtypDescription());
			partiesList.add(party);
		}
		return partiesList;
	}

}
