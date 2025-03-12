package com.guudint.clickargo.clictruck.planexec.job.mobile.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.guudint.clickargo.clictruck.admin.dto.ChangeMobileDriverLang;
import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkJobTruckService;
import com.guudint.clickargo.common.service.ICkSession;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

public class CkJobTruckMobileLangService {
	
	private static Logger LOG = Logger.getLogger(CkJobTruckService.class);
	
	@Autowired
	protected ICkSession ckSession;
	
	@Autowired
	@Qualifier("ckCtDrvDao")
	private GenericDao<TCkCtDrv, String> ckCtDrvDao;

	public void driverLang(ChangeMobileDriverLang dto) throws Exception {
		Principal principal = ckSession.getPrincipal();
		if (null == principal)
			throw new ProcessingException("principal is null");

		if (dto == null)
			throw new ParameterException("param dto null");
		
		TCkCtDrv ckCtDrv = getDrvMobileById(dto.getDrvMobileId());
		if (ckCtDrv == null)
			throw new EntityNotFoundException("user with driver id does not exist. please try again");

		ckCtDrv.setDrvMobileLang(dto.getLang());
		ckCtDrvDao.saveOrUpdate(ckCtDrv);
		
	
		
	}
	
	private TCkCtDrv getDrvMobileById(String mobileId) throws EntityNotFoundException {
		try {
			Map<String, Object> param = new HashMap<>();
			param.put("drvMobileId", mobileId);
			String sql = "SELECT o FROM TCkCtDrv o WHERE o.drvMobileId = :drvMobileId";
			List<TCkCtDrv> tCkCtDrvs = ckCtDrvDao.getByQuery(sql, param);
			if (tCkCtDrvs == null || tCkCtDrvs.size() == 0) {
				throw new Exception("Fail to find driverId " + mobileId);
			} else {
				TCkCtDrv ckCtDrv = tCkCtDrvs.get(0);
				if (ckCtDrv.getDrvStatus() != Constant.ACTIVE_STATUS) {
					throw new Exception(mobileId + " is locked. Please contact administrator.");
				}
				return tCkCtDrvs.get(0);
			}
		} catch (Exception ex) {
			LOG.error("getDrvMobileById", ex);
			throw new EntityNotFoundException(ex.getMessage());
		}
	}

	public ChangeMobileDriverLang getLanguageDriv(String drivMobileId) throws Exception {
		
		ChangeMobileDriverLang changeMobileDriverLang = new ChangeMobileDriverLang();
		Principal principal = ckSession.getPrincipal();
		if (null == principal)
			throw new ProcessingException("principal is null");
		
		TCkCtDrv ckCtDrv = getDrvMobileById(drivMobileId);
		if (ckCtDrv == null)
			throw new EntityNotFoundException("user with driver id does not exist. please try again");
		changeMobileDriverLang.setDrvMobileId(ckCtDrv.getDrvMobileId());
		changeMobileDriverLang.setLang(ckCtDrv.getDrvMobileLang());
		
		return changeMobileDriverLang;
		
	}

}
