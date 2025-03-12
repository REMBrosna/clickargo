package com.guudint.clickargo.clictruck.opm.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.guudint.clickargo.clictruck.opm.dao.CkOpmDao;
import com.guudint.clickargo.clictruck.opm.model.TCkOpm;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.master.dto.CkMstServiceType;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;
import com.vcc.camelone.master.dto.MstCurrency;

@Service
public class CkOpmDaoImpl extends GenericDaoImpl<TCkOpm, String> implements CkOpmDao {

	@Override
	public TCkOpm findByAccnId(String accnId) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkOpm.class);
		criteria.add(Restrictions.eq("TCoreAccn.accnId", accnId));
		criteria.add(Restrictions.eq("opmStatus", RecordStatus.ACTIVE.getCode()));
		return getOne(criteria);
	}

	@Override
	public TCkOpm getByServiceTypeAndAccnAndCcy(CkMstServiceType serviceType, CoreAccn accn, MstCurrency ccy) {
		try {
			DetachedCriteria criteria = DetachedCriteria.forClass(TCkOpm.class);
			criteria.add(Restrictions.eq("TCkMstServiceType.svctId", serviceType.getSvctId()));
			criteria.add(Restrictions.eq("TCoreAccn.accnId", accn.getAccnId()));
			criteria.add(Restrictions.eq("TMstCurrency.ccyCode", ccy.getCcyCode()));
			criteria.add(Restrictions.eq("opmStatus", RecordStatus.ACTIVE.getCode()));
			return getOne(criteria);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public TCkOpm findByAccnIdStatus(String accnId, List<Character> status) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkOpm.class);
		criteria.add(Restrictions.eq("TCoreAccn.accnId", accnId));
		criteria.add(Restrictions.in("opmStatus", status));
		return getOne(criteria);
	}

}
