package com.guudint.clickargo.clictruck.apigateway.services;

import com.guudint.clickargo.clictruck.apigateway.dao.CkCtVendorCompanyDao;
import com.guudint.clickargo.clictruck.apigateway.model.TCkCtVendorCompany;
import com.guudint.clickargo.common.service.ICkSession;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.PermissionException;
import com.vcc.camelone.common.exception.ProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AuthenticationHelper {
    @Autowired
    private ICkSession ckSession;

    @Autowired
    private CkCtVendorCompanyDao ckctVendorCompanyDao;

    /**
     * Helper method to validate Principal and accnId.
     */
    public void validatePrincipalAndAccnId(String accnId) throws Exception {
        Principal principal = ckSession.getPrincipal();
        if (principal == null) {
            throw new ProcessingException("Invalid token");
        }
        TCkCtVendorCompany tCkCtVendorCompany = ckctVendorCompanyDao.hasVendorAuthorization(accnId, principal.getUserAccnId());
        if (Objects.isNull(tCkCtVendorCompany)) {
            if (!accnId.equals(principal.getUserAccnId())) {
                throw new PermissionException("You are not authorized to act on this account");
            }
        }
    }
}
