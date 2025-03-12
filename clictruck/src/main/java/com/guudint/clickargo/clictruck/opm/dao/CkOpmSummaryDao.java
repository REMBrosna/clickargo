package com.guudint.clickargo.clictruck.opm.dao;

import com.guudint.clickargo.clictruck.opm.model.TCkOpmSummary;
import com.guudint.clickargo.master.dto.CkMstServiceType;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.master.dto.MstCurrency;

public interface CkOpmSummaryDao extends GenericDao<TCkOpmSummary, String> {

	TCkOpmSummary findByAccnId(String accnId) throws Exception;
	
	TCkOpmSummary getByServiceTypeAndAccnAndCcy(CkMstServiceType serviceType, CoreAccn accn, MstCurrency ccy);

}
