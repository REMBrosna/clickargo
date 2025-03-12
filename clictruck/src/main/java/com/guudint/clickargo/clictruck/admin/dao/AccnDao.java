package com.guudint.clickargo.clictruck.admin.dao;

import java.util.List;

import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.dao.GenericDao;

public interface AccnDao extends GenericDao<TCoreAccn, String> {

    List<TCoreAccn> findByField(TCoreAccn tCoreAccn, int offset, int limit) throws Exception;
}
