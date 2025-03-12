package com.guudint.clickargo.clictruck.master.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.common.dto.VehExtParamEnum;
import com.guudint.clickargo.clictruck.master.dto.MonitoringType;
import com.guudint.clickargo.clictruck.master.service.MasterService;

@Transactional(readOnly = true)
public class MonitoringExpiryTypeServiceImpl implements MasterService<MonitoringType> {

    private static Logger LOG = Logger.getLogger(MonitoringExpiryTypeServiceImpl.class);

	@Override
	public List<MonitoringType> listAll() {
		LOG.debug("listAll");
		return Arrays.stream(VehExtParamEnum.values())
				.filter(paramEnum -> !paramEnum.name().equalsIgnoreCase(VehExtParamEnum.EXP_DRIVER_LICENSE.name())
						&& !paramEnum.name().equalsIgnoreCase(VehExtParamEnum.VEHICLE_MAINTENANCE.name()))
				.map(paramEnum -> {
					MonitoringType ckCtMstMonitoringType = new MonitoringType();
					ckCtMstMonitoringType.setCode(paramEnum.name());
					ckCtMstMonitoringType.setDesc(paramEnum.getDesc());
					return ckCtMstMonitoringType;
				}).collect(Collectors.toList());
	}

	@Override
	public List<MonitoringType> listByStatus(Character status) {
		// TODO Auto-generated method stub
		return null;
	}
}
