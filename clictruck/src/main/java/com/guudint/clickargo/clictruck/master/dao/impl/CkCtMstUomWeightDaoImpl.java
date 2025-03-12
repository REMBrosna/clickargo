package com.guudint.clickargo.clictruck.master.dao.impl;

import com.guudint.clickargo.clictruck.master.dao.CkCtMstUomWeightDao;
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

public class CkCtMstUomWeightDaoImpl extends GenericDaoImpl<TCkCtMstUomWeight, String> implements MasterService<TCkCtMstUomWeight>, CkCtMstUomWeightDao {
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public List<TCkCtMstUomWeight> listAll() throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtMstUomWeight.class);
        criteria.add(Restrictions.eq("weiStatus", RecordStatus.ACTIVE.getCode()));
        criteria.addOrder(Order.asc("weiName"));
        return getByCriteria(criteria);
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public TCkCtMstUomWeight getWeightUomByDesc(String desc) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtMstUomWeight.class);
        criteria.add(Restrictions.eq("weiStatus", RecordStatus.ACTIVE.getCode()));
        criteria.add(Restrictions.eq("weiDesc", desc));
        return getOne(criteria);
    }
    @Override
    public List<TCkCtMstUomWeight> listByStatus(Character status) {
        return null;
    }


}
