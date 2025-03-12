package com.guudint.clickargo.clictruck.master.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.guudint.clickargo.clictruck.master.constant.CkCtMstVehTypeConstant;
import com.guudint.clickargo.clictruck.master.dao.CkCtMstVehTypeDao;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstVehType;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtMstVehTypeDaoImpl extends GenericDaoImpl<TCkCtMstVehType, String> implements CkCtMstVehTypeDao {

    @Override
    public List<TCkCtMstVehType> findByVhtyStatus(Character status) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtMstVehType.class);
        criteria.add(Restrictions.eq(CkCtMstVehTypeConstant.PropertyName.VHTY_STATUS, status));
        criteria.addOrder(Order.asc(CkCtMstVehTypeConstant.PropertyName.VHTY_NAME)); // Replace SOME_FIELD with the field to order by
        return getByCriteria(criteria);
    }

    @Override
    public List<TCkCtMstVehType> findByVhtyName(String vhtyName) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtMstVehType.class);
        criteria.add(Restrictions.eq(CkCtMstVehTypeConstant.PropertyName.VHTY_NAME, vhtyName));
        return getByCriteria(criteria);
    }

}
