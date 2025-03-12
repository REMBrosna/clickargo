package com.guudint.clickargo.clictruck.common.dao;

import java.util.List;
import java.util.Optional;

import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtDrvDao extends GenericDao<TCkCtDrv, String> {

    Optional<TCkCtDrv> findByLicenseNo(String licenseNo) throws Exception;

    Optional<TCkCtDrv> findByEmail(String email) throws Exception;
    
    TCkCtDrv findByMobileUserId(String drvMobileId) throws Exception;
    
    TCkCtDrv findByMobileUserId(String drvMobileId, List<Character> status) throws Exception;
    
    boolean isPasswordChanged(String mobilePwd) throws Exception;
    
    TCkCtDrv findByDriverNameAccnId(String accnId, String drvName) throws Exception;
}
