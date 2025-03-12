package com.guudint.clickargo.clictruck.master.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.master.service.MasterService;
import com.guudint.clickargo.master.dao.CkMstCntTypeDao;
import com.guudint.clickargo.master.dto.CkMstCntType;
import com.guudint.clickargo.master.model.TCkMstCntType;

@Transactional(readOnly = true)
public class CkMstCntTypeServiceImpl implements MasterService<CkMstCntType> {

    private static Logger LOG = Logger.getLogger(CkMstCntTypeServiceImpl.class);

    @Autowired
    private CkMstCntTypeDao ckMstCntTypeDao;

    @Override
    public List<CkMstCntType> listAll() {
        List<CkMstCntType> ckMstCntTypes = new ArrayList<>();
        try {
            for (TCkMstCntType tCkMstCntType : ckMstCntTypeDao.getAll()) {
                ckMstCntTypes.add(new CkMstCntType(tCkMstCntType));
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return ckMstCntTypes;
    }

    @Override
    public List<CkMstCntType> listByStatus(Character status) {
        List<CkMstCntType> ckMstCntTypes = new ArrayList<>();
        try {
            for (TCkMstCntType tCkMstCntType : ckMstCntTypeDao.findByCnttStatus(status)) {
                ckMstCntTypes.add(new CkMstCntType(tCkMstCntType));
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return ckMstCntTypes;
    }

}
