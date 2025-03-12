package com.guudint.clickargo.clictruck.finacing.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.finacing.dto.CkCtDebitNoteItem;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtDebitNoteItem;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtDebitNoteItemDao extends GenericDao<TCkCtDebitNoteItem, String> {

	public List<CkCtDebitNoteItem> getDebitNoteitems(String dnId) throws Exception;
}
