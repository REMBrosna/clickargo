package com.guudint.clickargo.clictruck.common.dao;

import java.util.Optional;

import com.guudint.clickargo.clictruck.common.model.TCkCtVehMlog;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtVehMlogDao extends GenericDao<TCkCtVehMlog, String> {
	Optional<TCkCtVehMlog> findByVehId(String vhId);
}
