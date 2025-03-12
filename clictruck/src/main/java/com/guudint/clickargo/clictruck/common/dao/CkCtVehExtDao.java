package com.guudint.clickargo.clictruck.common.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.guudint.clickargo.clictruck.common.model.TCkCtVehExt;
import com.guudint.clickargo.clictruck.common.model.TCkCtVehExtId;
import com.guudint.clickargo.clictruck.notification.model.TCkCtAlert;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtVehExtDao extends GenericDao<TCkCtVehExt, TCkCtVehExtId> {
	List<TCkCtVehExt> findByMonitoring(String param, Character monitor, String dueDate) throws Exception;

	Optional<TCkCtVehExt> findByIdAndMonitoring(String vhId, String param, Character monitor) throws Exception;

	TCkCtVehExt findByVehIdAndKey(String vehId, String key) throws Exception;
	List<TCkCtAlert> findByAlertCondition(Date dueDate, List<String> listState) throws Exception;
}
