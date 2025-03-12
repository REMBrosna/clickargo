package com.guudint.clickargo.clictruck.master.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.master.dao.CkCtMstLocationTypeDao;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstLocationType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstLocationType;
import com.guudint.clickargo.clictruck.master.service.MasterService;

@Transactional(readOnly = true)
public class CkCtMstLocationTypeServiceImpl implements MasterService<CkCtMstLocationType> {

    private static Logger LOG = Logger.getLogger(CkCtMstLocationTypeServiceImpl.class);

    @Autowired
    private CkCtMstLocationTypeDao ckCtMstLocationTypeDao;

    @Override
    public List<CkCtMstLocationType> listAll() {
        List<CkCtMstLocationType> ckCtMstLocationTypes = new ArrayList<>();
        try {
            for (TCkCtMstLocationType tCkCtMstLocationType : ckCtMstLocationTypeDao.getAll()) {
                ckCtMstLocationTypes.add(new CkCtMstLocationType(tCkCtMstLocationType));
            }
        } catch (Exception e) {
            LOG.info(e);
        }
        return ckCtMstLocationTypes;
    }

    @Override
    public List<CkCtMstLocationType> listByStatus(Character status) {
        List<CkCtMstLocationType> ckCtMstLocationTypes = new ArrayList<>();
        try {
            for (TCkCtMstLocationType tCkCtMstLocationType : ckCtMstLocationTypeDao.findByLctyStatus(status)) {
                ckCtMstLocationTypes.add(new CkCtMstLocationType(tCkCtMstLocationType));
            }
        } catch (Exception e) {
            LOG.info(e);
        }
        return ckCtMstLocationTypes;
    }

}
