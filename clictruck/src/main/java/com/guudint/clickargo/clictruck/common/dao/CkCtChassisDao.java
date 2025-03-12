package com.guudint.clickargo.clictruck.common.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.common.model.TCkCtChassis;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtChassisDao  extends GenericDao<TCkCtChassis, String> {

	/**
	 * This method validates if there are existing chsNo no based on chassisType (size) and accnId
	 * @param chsNo
	 * @param chassisType
	 * @param accnId
	 * @return
	 * @throws Exception
	 */
	List<TCkCtChassis> findExistingChassis(String chsNo, String chassisType, String accnId) throws Exception;
	List<TCkCtChassis> findChassisByCompany(String accnId) throws Exception;
	TCkCtChassis findChassisByChsNo(String chsNo, String accnId) throws Exception;
}
