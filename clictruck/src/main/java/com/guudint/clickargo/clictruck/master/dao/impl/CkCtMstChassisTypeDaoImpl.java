package com.guudint.clickargo.clictruck.master.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.guudint.clickargo.clictruck.master.constant.CkCtMstChassisTypeConstant;
import com.guudint.clickargo.clictruck.master.dao.CkCtMstChassisTypeDao;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstChassisType;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtMstChassisTypeDaoImpl extends GenericDaoImpl<TCkCtMstChassisType, String> implements CkCtMstChassisTypeDao {

    @Override
    public List<TCkCtMstChassisType> findByChtyStatus(Character status) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtMstChassisType.class);
        criteria.add(Restrictions.eq(CkCtMstChassisTypeConstant.PropertyName.CHTY_STATUS, status));
        return getByCriteria(criteria);
    }

}
