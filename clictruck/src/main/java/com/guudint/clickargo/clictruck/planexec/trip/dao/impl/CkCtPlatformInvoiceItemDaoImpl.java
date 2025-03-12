package com.guudint.clickargo.clictruck.planexec.trip.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtPlatformInvoiceItemDao;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtPlatformInvoice;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtPlatformInvoiceItem;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoiceItem;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;
import com.vcc.camelone.master.dto.MstCurrency;

public class CkCtPlatformInvoiceItemDaoImpl extends GenericDaoImpl<TCkCtPlatformInvoiceItem, String>
		implements CkCtPlatformInvoiceItemDao {
	
	
	// copy from 
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtPlatformInvoiceItem> getPlatformFeeItems(String pfInvId) throws Exception {
		List<CkCtPlatformInvoiceItem> dtoItemList = new ArrayList<>();
		String hql = "from TCkCtPlatformInvoiceItem o where o.TCkCtPlatformInvoice.invId=:pfInvId and o.itmStatus=:status";
		Map<String, Object> params = new HashMap<>();
		params.put("pfInvId", pfInvId);
		params.put("status", RecordStatus.ACTIVE.getCode());
		List<TCkCtPlatformInvoiceItem> itemList = this.getByQuery(hql, params);
		if (itemList != null && itemList.size() > 0) {
			for (TCkCtPlatformInvoiceItem itm : itemList) {
				Hibernate.initialize(itm.getTCkCtPlatformInvoice());
				Hibernate.initialize(itm.getTMstCurrency());

				CkCtPlatformInvoiceItem dtoItm = new CkCtPlatformInvoiceItem(itm);
				dtoItm.setTCkCtPlatformInvoice(new CkCtPlatformInvoice(itm.getTCkCtPlatformInvoice()));
				dtoItm.setTMstCurrency(new MstCurrency(itm.getTMstCurrency()));
				dtoItemList.add(dtoItm);
			}
		}

		return dtoItemList;
	}
}
