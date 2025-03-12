package com.guudint.clickargo.clictruck.admin.ratetable.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.guudint.clickargo.clictruck.admin.ratetable.dao.CkCtTripRateDao;
import com.guudint.clickargo.clictruck.admin.ratetable.model.TCkCtTripRate;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;
import com.vcc.camelone.common.exception.ParameterException;

public class CkCtTripRateDaoImpl extends GenericDaoImpl<TCkCtTripRate, String> implements CkCtTripRateDao {

    @Override
    public List<TCkCtTripRate> findByCoFfAndLocFromTo(String coFf, String locFrom, String locTo) throws Exception {
        String hql = "from TCkCtTripRate o "
            +"where o.TCkCtLocationByTrLocFrom.locId = :locFrom and o.TCkCtLocationByTrLocTo.locId = :locTo and o.TCkCtRateTable.TCoreAccnByRtCoFf.accnId = :coFf";
        Map<String, Object> params = new HashMap<>();
        params.put("locFrom", locFrom);
        params.put("locTo", locTo);
        params.put("coFf", coFf);
        return getByQuery(hql, params);
    }
    
    @Override
    public List<TCkCtTripRate> findByRateTableIdLocFromToAndTruckType(String rateTableId, String locFrom, String locTo, String vehType)
    		throws Exception {
        String hql = "from TCkCtTripRate o "
            +"where o.TCkCtRateTable.rtId = :rateTableId "
            + "and o.TCkCtLocationByTrLocFrom.locId = :locFrom "
            + "and o.TCkCtLocationByTrLocTo.locId = :locTo "
            + "and o.TCkCtMstVehType.vhtyId = :vehType "
            + "and trStatus = :status ";
        Map<String, Object> params = new HashMap<>();
        params.put("rateTableId", rateTableId);
        params.put("locFrom", locFrom);
        params.put("locTo", locTo);
        params.put("vehType", vehType);
        params.put("status", RecordStatus.ACTIVE.getCode());
        return getByQuery(hql, params);
    }

	@Override
	public TCkCtTripRate findByTripRateTableAndFromToVehType(String toAccn, String coFfAccn, String currency,
			String locFrom, String locTo, String vehType) throws Exception {
		
		if (StringUtils.isAnyBlank(toAccn, coFfAccn, currency, locFrom, locTo, vehType))
			throw new ParameterException("all params must be supplied - toAccn, coFfAccn, currency, locFrom, locTo, vehType");
		
		String hql = "from TCkCtTripRate o where "
				+ " o.TCkCtRateTable.TCoreAccnByRtCompany.accnId=:toAccn "
				+ " and o.TCkCtRateTable.TCoreAccnByRtCoFf.accnId=:coFfAccn"
				+ " and o.TCkCtRateTable.TMstCurrency.ccyCode=:currency"
				+ " and o.TCkCtLocationByTrLocTo.locId=:locTo"
				+ " and o.TCkCtLocationByTrLocFrom.locId=:locFrom"
				+ " and o.TCkCtMstVehType.vhtyId=:vehType"
				+ " and o.trStatus=:status";
		Map<String, Object> params = new HashMap<>();
		params.put("toAccn", toAccn);
		params.put("coFfAccn", coFfAccn);
		params.put("currency", currency);
		params.put("locTo", locTo);
		params.put("locFrom", locFrom);
		params.put("vehType", vehType);
		params.put("status", RecordStatus.ACTIVE.getCode());
		
		List<TCkCtTripRate> list = getByQuery(hql, params);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	@Override
	public List<TCkCtTripRate> findByTripRateTableAndFromToVehType(String toAccn, String coFfAccn, String currency,
			String locFrom, String locTo, String vehType, List<Character> inStatus, String trType) throws Exception {
		
		if (StringUtils.isAnyBlank(toAccn, coFfAccn, currency, locFrom, locTo, vehType))
			throw new ParameterException("all params must be supplied - toAccn, coFfAccn, currency, locFrom, locTo, vehType");
		
		String hql = "from TCkCtTripRate o where "
				+ " o.TCkCtRateTable.TCoreAccnByRtCompany.accnId=:toAccn "
				+ " and o.TCkCtRateTable.TCoreAccnByRtCoFf.accnId=:coFfAccn"
				+ " and o.TCkCtRateTable.TMstCurrency.ccyCode=:currency"
				+ " and o.TCkCtLocationByTrLocTo.locId=:locTo"
				+ " and o.TCkCtLocationByTrLocFrom.locId=:locFrom"
				+ " and o.TCkCtMstVehType.vhtyId=:vehType"
				+ " and o.trStatus in (:status)"
				+ " and o.trType = :trType";
		Map<String, Object> params = new HashMap<>();
		params.put("toAccn", toAccn);
		params.put("coFfAccn", coFfAccn);
		params.put("currency", currency);
		params.put("locTo", locTo);
		params.put("locFrom", locFrom);
		params.put("vehType", vehType);
		params.put("status", inStatus);
		params.put("trType", trType);
		
		return getByQuery(hql, params);
	}
	
	@Override
	public TCkCtTripRate findByTripRateTableAndFromToVehType(String toAccn, String coFfAccn, String currency,
			String locFrom, String locTo, String vehType, List<Character> inStatus, Integer seqNo, String tripRateParentId) throws Exception {
		
		if (StringUtils.isAnyBlank(toAccn, coFfAccn, currency, locFrom, locTo, vehType))
			throw new ParameterException("all params must be supplied - toAccn, coFfAccn, currency, locFrom, locTo, vehType");
		
		String hql = "from TCkCtTripRate o where "
				+ " o.TCkCtRateTable.TCoreAccnByRtCompany.accnId=:toAccn "
				+ " and o.TCkCtRateTable.TCoreAccnByRtCoFf.accnId=:coFfAccn"
				+ " and o.TCkCtRateTable.TMstCurrency.ccyCode=:currency"
				+ " and o.TCkCtLocationByTrLocTo.locId=:locTo"
				+ " and o.TCkCtLocationByTrLocFrom.locId=:locFrom"
				+ " and o.TCkCtMstVehType.vhtyId=:vehType"
				+ " and o.trStatus in (:status)"
				+ " and o.trSeq = :seqNo";
		Map<String, Object> params = new HashMap<>();
		params.put("toAccn", toAccn);
		params.put("coFfAccn", coFfAccn);
		params.put("currency", currency);
		params.put("locTo", locTo);
		params.put("locFrom", locFrom);
		params.put("vehType", vehType);
		params.put("status", inStatus);
		params.put("seqNo", seqNo);
		//params.put("TCkCtTripRate.trId", tripRateParentId);
		
		List<TCkCtTripRate> list = getByQuery(hql, params);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

}
