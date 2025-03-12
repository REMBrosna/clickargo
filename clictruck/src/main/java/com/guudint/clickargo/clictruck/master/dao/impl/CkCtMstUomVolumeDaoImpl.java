package com.guudint.clickargo.clictruck.master.dao.impl;


import com.guudint.clickargo.clictruck.master.dao.CkCtMstUomVolumeDao;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstUomSize;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstUomVolume;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstUomWeight;
import com.guudint.clickargo.clictruck.master.service.MasterService;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public class CkCtMstUomVolumeDaoImpl extends GenericDaoImpl<TCkCtMstUomVolume, String> implements MasterService<TCkCtMstUomVolume>, CkCtMstUomVolumeDao {
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public List<TCkCtMstUomVolume> listAll() throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtMstUomVolume.class);
        criteria.add(Restrictions.eq("volStatus", RecordStatus.ACTIVE.getCode()));
        criteria.addOrder(Order.asc("volName"));
        return getByCriteria(criteria);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public TCkCtMstUomVolume getVolumeUomByDesc(String desc) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtMstUomVolume.class);
        criteria.add(Restrictions.eq("volStatus", RecordStatus.ACTIVE.getCode()));
        criteria.add(Restrictions.eq("volDesc", desc));
        return getOne(criteria);
    }

    @Override
    public List<TCkCtMstUomVolume> listByStatus(Character status) {
        return null;
    }
}
