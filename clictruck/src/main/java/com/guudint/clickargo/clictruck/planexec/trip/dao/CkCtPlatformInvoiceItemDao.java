package com.guudint.clickargo.clictruck.planexec.trip.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtPlatformInvoiceItem;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoiceItem;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtPlatformInvoiceItemDao extends GenericDao<TCkCtPlatformInvoiceItem, String>{
	
	public List<CkCtPlatformInvoiceItem> getPlatformFeeItems(String pfInvId) throws Exception;

}
