package com.guudint.clickargo.clictruck.master.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.master.dao.CkCtMstChassisTypeDao;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstChassisType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstChassisType;
import com.guudint.clickargo.clictruck.master.service.MasterService;

@Transactional(readOnly = true)
public class CkCtMstChassisTypeServiceImpl implements MasterService<CkCtMstChassisType> {

    private static Logger LOG = Logger.getLogger(CkCtMstChassisTypeServiceImpl.class);

    @Autowired
    private CkCtMstChassisTypeDao ckCtMstChassisTypeDao;

    @Override
    public List<CkCtMstChassisType> listAll() {
        List<CkCtMstChassisType> ckCtMstChassisTypes = new ArrayList<>();
        try {
            for (TCkCtMstChassisType tCkCtMstChassisType : ckCtMstChassisTypeDao.getAll()) {
                ckCtMstChassisTypes.add(new CkCtMstChassisType(tCkCtMstChassisType));
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return ckCtMstChassisTypes;
    }

    @Override
    public List<CkCtMstChassisType> listByStatus(Character status) {
        List<CkCtMstChassisType> ckCtMstChassisTypes = new ArrayList<>();
        try {
            for (TCkCtMstChassisType tCkCtMstChassisType : ckCtMstChassisTypeDao.findByChtyStatus(status)) {
                ckCtMstChassisTypes.add(new CkCtMstChassisType(tCkCtMstChassisType));
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return ckCtMstChassisTypes;
    }

}
