package com.guudint.clickargo.clictruck.opm.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.opm.model.TCkOpm;
import com.guudint.clickargo.master.dto.CkMstServiceType;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.master.dto.MstCurrency;

public interface CkOpmDao extends GenericDao<TCkOpm, String> {

	TCkOpm findByAccnId(String accnId) throws Exception;

	TCkOpm findByAccnIdStatus(String accnId, List<Character> status) throws Exception;

	TCkOpm getByServiceTypeAndAccnAndCcy(CkMstServiceType serviceType, CoreAccn accn, MstCurrency ccy);

}
