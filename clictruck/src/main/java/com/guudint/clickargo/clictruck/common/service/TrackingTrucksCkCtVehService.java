package com.guudint.clickargo.clictruck.common.service;

import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehType;
import java.util.List;

public interface TrackingTrucksCkCtVehService {
	List<CkCtMstVehType> getVehTypeByCompany(String companyId) throws Exception;

}
