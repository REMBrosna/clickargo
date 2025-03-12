package com.guudint.clickargo.clictruck.master.dao.impl;


import com.guudint.clickargo.clictruck.master.dao.CkCtMstUomSizeDao;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstUomSize;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstUomWeight;
import com.guudint.clickargo.clictruck.master.service.MasterService;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;
import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public class CkCtMstUomSizeDaoImpl extends GenericDaoImpl<TCkCtMstUomSize, String> implements MasterService<TCkCtMstUomSize>, CkCtMstUomSizeDao {
    private static Logger LOG = Logger.getLogger(CkCtMstUomSizeDaoImpl.class);
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public List<TCkCtMstUomSize> listAll() throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtMstUomSize.class);
        criteria.add(Restrictions.eq("sizStatus", RecordStatus.ACTIVE.getCode()));
        criteria.addOrder(Order.asc("sizName"));
        return getByCriteria(criteria);
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public TCkCtMstUomSize getSizeUomByDesc(String desc) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtMstUomSize.class);
        criteria.add(Restrictions.eq("sizStatus", RecordStatus.ACTIVE.getCode()));
        criteria.add(Restrictions.eq("sizDesc", desc));
        return getOne(criteria);
    }
    @Override
    public List<TCkCtMstUomSize> listByStatus(Character status) {
        return null;
    }
}
