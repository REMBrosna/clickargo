package com.guudint.clickargo.clictruck.report.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

//import com.guudint.pedi.app.workflow.model.TPediAppAccnAssn;
//import com.guudint.pedi.common.util.PediConstant.RecordStatus;
//import com.guudint.pedi.master.model.TPediMstAppType;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;

@Service
public class ClicTruckReportMiscService {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(ClicTruckReportMiscService.class);

//	@Autowired
//	@Qualifier("pediAppAccnAssnDao")
//	private GenericDao<TPediAppAccnAssn, String> pediAppAccnAssnDao;

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public ResponseEntity<Object> getAccnAssocAppType(String accnType) {
		log.debug("getAccnAssocAppType");

		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			if (StringUtils.isBlank(accnType))
				throw new ParameterException("param accnType is null or empty");

//			List<TPediMstAppType> appTypes = new ArrayList<TPediMstAppType>();
//			String hql = "FROM TPediAppAccnAssn o WHERE o.TMstAccnType.atypId = :atypId AND o.dstlRecStatus=:dstlRecStatus";
//			Map<String, Object> params = new HashMap<>();
//			params.put("atypId", accnType);
//			params.put("dstlRecStatus", String.valueOf(RecordStatus.ACTIVE.getCode()));
//			List<TPediAppAccnAssn> appTypeAssocList = pediAppAccnAssnDao.getByQuery(hql, params);
//			if (appTypeAssocList != null && appTypeAssocList.size() > 0) {
//				for (TPediAppAccnAssn accnAssn : appTypeAssocList) {
//					Hibernate.initialize(accnAssn.getTPediMstAppType());
//					appTypes.add(accnAssn.getTPediMstAppType());
//				}
//			}
//			return ResponseEntity.ok(appTypes);
			return null;

		} catch (Exception ex) {
			log.error("getAccnAssocAppType", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		}
	}
}
