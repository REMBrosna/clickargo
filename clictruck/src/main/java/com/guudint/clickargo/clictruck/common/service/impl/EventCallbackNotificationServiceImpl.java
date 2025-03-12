package com.guudint.clickargo.clictruck.common.service.impl;

import java.util.Date;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.common.dao.CkCtVehDao;
import com.guudint.clickargo.clictruck.common.dao.CkCtVehExtDao;
import com.guudint.clickargo.clictruck.common.dto.EventCallbackNotification;
import com.guudint.clickargo.clictruck.common.dto.VehExtParamEnum;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.common.model.TCkCtVehExt;
import com.vcc.camelone.common.exception.ProcessingException;

public class EventCallbackNotificationServiceImpl {

	private static Logger LOG = Logger.getLogger(EventCallbackNotificationServiceImpl.class);
	
	@Autowired
	private CkCtVehDao ckCtVehDao;
	
	@Autowired
	private CkCtVehExtDao ckCtVehExtDao;

	@Transactional
	public void updateNotify(EventCallbackNotification dto) throws Exception {
		LOG.debug("updateNotify");
		
		Optional<TCkCtVeh> opTCkCtVeh = ckCtVehDao.findByImei(dto.getImei());
		if(opTCkCtVeh.isPresent()) {
			Optional<TCkCtVehExt> opTCkCtVehExt = ckCtVehExtDao.findByIdAndMonitoring(opTCkCtVeh.get().getVhId(), VehExtParamEnum.VEHICLE_MAINTENANCE.name(), 'O');
			if(opTCkCtVehExt.isPresent()) {
				if(dto.getDistance() > Integer.valueOf(opTCkCtVehExt.get().getVextValue())) {
					opTCkCtVehExt.get().setVextNotify('Y');
					opTCkCtVehExt.get().setVextDtLupd(new Date());
					opTCkCtVehExt.get().setVextUidLupd("SYS");
					ckCtVehExtDao.saveOrUpdate(opTCkCtVehExt.get());
				}
			} else {
				throw new ProcessingException("Vehicle Ext is not found");
			}
		} else {
			throw new ProcessingException("Vehicle is not found");
		}
	}
}
