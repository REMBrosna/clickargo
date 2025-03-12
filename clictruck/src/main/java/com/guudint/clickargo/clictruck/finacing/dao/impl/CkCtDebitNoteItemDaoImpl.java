package com.guudint.clickargo.clictruck.finacing.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.finacing.dao.CkCtDebitNoteItemDao;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtDebitNote;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtDebitNoteItem;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtDebitNoteItem;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;
import com.vcc.camelone.master.dto.MstCurrency;

public class CkCtDebitNoteItemDaoImpl extends GenericDaoImpl<TCkCtDebitNoteItem, String>
		implements CkCtDebitNoteItemDao {

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtDebitNoteItem> getDebitNoteitems(String dnId) throws Exception {
		List<CkCtDebitNoteItem> dtoItemList = new ArrayList<>();
		String hql = "from TCkCtDebitNoteItem o where o.TCkCtDebitNote.dnId=:dnId and o.itmStatus=:status";
		Map<String, Object> params = new HashMap<>();
		params.put("dnId", dnId);
		params.put("status", RecordStatus.ACTIVE.getCode());
		List<TCkCtDebitNoteItem> itmList = this.getByQuery(hql, params);
		if (itmList != null && itmList.size() > 0) {
			for (TCkCtDebitNoteItem itm : itmList) {
				Hibernate.initialize(itm.getTCkCtDebitNote());
				Hibernate.initialize(itm.getTMstCurrency());

				CkCtDebitNoteItem dtoItm = new CkCtDebitNoteItem(itm);
				dtoItm.setTCkCtDebitNote(new CkCtDebitNote(itm.getTCkCtDebitNote()));
				dtoItm.setTMstCurrency(new MstCurrency(itm.getTMstCurrency()));
				dtoItemList.add(dtoItm);
			}
		}

		return dtoItemList;
	}
	
}
