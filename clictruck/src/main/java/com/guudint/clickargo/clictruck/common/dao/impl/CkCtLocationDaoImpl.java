package com.guudint.clickargo.clictruck.common.dao.impl;

import java.util.List;
import java.util.Optional;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.guudint.clickargo.clictruck.common.constant.CkCtLocationConstant;
import com.guudint.clickargo.clictruck.common.constant.CkCtLocationConstant.LocationId;
import com.guudint.clickargo.clictruck.common.dao.CkCtLocationDao;
import com.guudint.clickargo.clictruck.common.model.TCkCtLocation;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstLocationType;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtLocationDaoImpl extends GenericDaoImpl<TCkCtLocation, String> implements CkCtLocationDao {

    @Override
    public Optional<TCkCtLocation> findByName(String name) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtLocation.class);
        criteria.add(Restrictions.eq(CkCtLocationConstant.PropertyName.LOC_NAME, name));
        return Optional.ofNullable(getOne(criteria));
    }

    @Override
    public Optional<TCkCtLocation> findById(String locId) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtLocation.class);
        criteria.add(Restrictions.eq(CkCtLocationConstant.PropertyName.LOC_ID, locId));
        return Optional.ofNullable(getOne(criteria));
    }

    @Override
    public Optional<TCkCtLocation> findByNameAndCompany(String name, String companyId) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtLocation.class);
        criteria.add(Restrictions.eq(CkCtLocationConstant.PropertyName.LOC_NAME, name));
        TCoreAccn tCoreAccn = new TCoreAccn();
        tCoreAccn.setAccnId(companyId);
        criteria.add(Restrictions.eq(CkCtLocationConstant.PropertyName.LOC_COMPANY, tCoreAccn));
        return Optional.ofNullable(getOne(criteria));
    }

    @Override
    public Optional<TCkCtLocation> findByAddressAndCompany(String address, String companyId) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtLocation.class);
        criteria.add(Restrictions.eq(CkCtLocationConstant.PropertyName.LOC_ADDRESS, address));
        TCoreAccn tCoreAccn = new TCoreAccn();
        tCoreAccn.setAccnId(companyId);
        criteria.add(Restrictions.eq(CkCtLocationConstant.PropertyName.LOC_COMPANY, tCoreAccn));
        return Optional.ofNullable(getOne(criteria));
    }

    @Override
    public Optional<TCkCtLocation> findByNameAndCompanyAndAddress(String companyId, String locType, String name, String addressDetail) throws Exception {
    	
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtLocation.class);
        criteria.add(Restrictions.eq(CkCtLocationConstant.PropertyName.LOC_COMPANY, new TCoreAccn(companyId, null, ' ', null)));
        criteria.add(Restrictions.eq(CkCtLocationConstant.PropertyName.LOC_TYPE, new TCkCtMstLocationType(LocationId.ADDRESS, null)));
        criteria.add(Restrictions.eq(CkCtLocationConstant.PropertyName.LOC_NAME, name));
        criteria.add(Restrictions.eq(CkCtLocationConstant.PropertyName.LOC_ADDRESS, addressDetail));
        
        return Optional.ofNullable(getOne(criteria));
    }
    
    @Override
    public List<TCkCtLocation> findByGPSisNull() throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtLocation.class);
        criteria.add(Restrictions.isNull(CkCtLocationConstant.PropertyName.LOC_GPS));
        return super.getByCriteria(criteria);
    }

    @Override
    public Optional<TCkCtLocation> findByDefaultRegion(String region, String locName, String companyId) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtLocation.class);
        criteria.add(Restrictions.eq(CkCtLocationConstant.PropertyName.LOC_NAME, locName));
        TCkCtMstLocationType tCkCtMstLocationType = new TCkCtMstLocationType();
        tCkCtMstLocationType.setLctyId(region);
        criteria.add(Restrictions.eq(CkCtLocationConstant.PropertyName.LOC_TYPE, tCkCtMstLocationType));
        TCoreAccn tCoreAccn = new TCoreAccn();
        tCoreAccn.setAccnId(companyId);
        criteria.add(Restrictions.eq(CkCtLocationConstant.PropertyName.LOC_COMPANY, tCoreAccn));
        return Optional.ofNullable(getOne(criteria));
    }
    @Override
    public Optional<TCkCtLocation> findByLocationName(String locName,String companyId) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtLocation.class);
        criteria.add(Restrictions.eq(CkCtLocationConstant.PropertyName.LOC_NAME, locName));
        TCoreAccn tCoreAccn = new TCoreAccn();
        tCoreAccn.setAccnId(companyId);
        criteria.add(Restrictions.eq(CkCtLocationConstant.PropertyName.LOC_COMPANY, tCoreAccn));
        return Optional.ofNullable(getOne(criteria));
    }
}
