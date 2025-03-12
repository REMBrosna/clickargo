package com.guudint.clickargo.clictruck.master.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.master.dao.CkCtMstTripAttachTypeDao;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstTripAttachType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstTripAttachType;
import com.guudint.clickargo.clictruck.master.service.MasterService;

@Transactional(readOnly = true)
public class CkCtMstTripAttachTypeServiceImpl implements MasterService<CkCtMstTripAttachType> {

	private static Logger LOG = Logger.getLogger(CkCtMstTripAttachTypeServiceImpl.class);

	@Autowired
	private CkCtMstTripAttachTypeDao ckCtMstTripAttachTypeDao;

	@Override
	public List<CkCtMstTripAttachType> listAll() {
		List<CkCtMstTripAttachType> ckCtMstTripAttachTypes = new ArrayList<>();
		try {
			for (TCkCtMstTripAttachType tckCtMstTripAttachType : ckCtMstTripAttachTypeDao.getAll()) {
				ckCtMstTripAttachTypes.add(new CkCtMstTripAttachType(tckCtMstTripAttachType));
			}
		} catch (Exception e) {
			LOG.error(e);
		}
		return ckCtMstTripAttachTypes;
	}

	@Override
	public List<CkCtMstTripAttachType> listByStatus(Character status) {
		List<CkCtMstTripAttachType> ckCtMstTripAttachTypes = new ArrayList<>();
		try {
			for (TCkCtMstTripAttachType tckCtMstTripAttachType : ckCtMstTripAttachTypeDao.findByAtStatus(status)) {
				ckCtMstTripAttachTypes.add(new CkCtMstTripAttachType(tckCtMstTripAttachType));
			}
		} catch (Exception e) {
			LOG.error(e);
		}
		return ckCtMstTripAttachTypes;
	}

}
