package com.guudint.clickargo.clictruck.master.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.guudint.clickargo.common.RecordStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.master.dao.CkCtMstVehTypeDao;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstVehType;
import com.guudint.clickargo.clictruck.master.service.MasterService;

@Transactional(readOnly = true)
public class CkCtMstVehTypeServiceImpl implements MasterService<CkCtMstVehType> {

    private static Logger LOG = Logger.getLogger(CkCtMstVehTypeServiceImpl.class);

    @Autowired
    private CkCtMstVehTypeDao ckCtMstVehTypeDao;

    @Override
    public List<CkCtMstVehType> listAll() {
        List<CkCtMstVehType> ckCtMstVehTypes = new ArrayList<>();
        try {
            for (TCkCtMstVehType tCkCtMstVehType : ckCtMstVehTypeDao.findByVhtyStatus(RecordStatus.ACTIVE.getCode())) {
                ckCtMstVehTypes.add(new CkCtMstVehType(tCkCtMstVehType));
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return ckCtMstVehTypes;
    }

    @Override
    public List<CkCtMstVehType> listByStatus(Character status) {
        List<CkCtMstVehType> ckCtMstVehTypes = new ArrayList<>();
        try {
            for (TCkCtMstVehType tCkCtMstVehType : ckCtMstVehTypeDao.findByVhtyStatus(status)) {
                ckCtMstVehTypes.add(new CkCtMstVehType(tCkCtMstVehType));
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return ckCtMstVehTypes;
    }

}
