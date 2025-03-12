package com.guudint.clickargo.clictruck.common.dao;

import java.util.List;
import java.util.Optional;

import com.guudint.clickargo.clictruck.common.model.TCkCtLocation;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtLocationDao extends GenericDao<TCkCtLocation, String> {

    Optional<TCkCtLocation> findByName(String name) throws Exception;

    Optional<TCkCtLocation> findById(String id) throws Exception;

    Optional<TCkCtLocation> findByNameAndCompany(String name, String companyId) throws Exception;
    
    Optional<TCkCtLocation> findByAddressAndCompany(String address, String companyId) throws Exception;
    
    Optional<TCkCtLocation> findByNameAndCompanyAndAddress(String companyId, String locType, String name, String addressDetail) throws Exception;
    
    List<TCkCtLocation> findByGPSisNull() throws Exception;

    Optional<TCkCtLocation> findByDefaultRegion(String region,String locName, String companyId) throws Exception;
    Optional<TCkCtLocation> findByLocationName(String address, String companyId) throws Exception;
}
