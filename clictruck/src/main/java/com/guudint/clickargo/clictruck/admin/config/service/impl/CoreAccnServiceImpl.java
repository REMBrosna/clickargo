package com.guudint.clickargo.clictruck.admin.config.service.impl;

import java.util.HashMap;

import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.service.impl.AccnService;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

public class CoreAccnServiceImpl extends AccnService {

    @Override
    protected String getWhereClause(CoreAccn coreAccn, boolean wherePrinted)
            throws ParameterException, ProcessingException {
        StringBuffer searchStatement = new StringBuffer(super.getWhereClause(coreAccn, wherePrinted));
        wherePrinted = searchStatement.toString().contains("WHERE");
        searchStatement.append(getOperator(wherePrinted) + "not exists (select distinct tcac.TCoreAccn.accnId "
                + "from TCoreAccnConfig tcac "
                + "where tcac.TCoreAccn.accnId = o.accnId and tcac.id.acfgKey = :acfgKey)");
        return searchStatement.toString();
    }

    @Override
    protected HashMap<String, Object> getParameters(CoreAccn coreAccn) throws ParameterException, ProcessingException {
        HashMap<String, Object> params = super.getParameters(coreAccn);
        params.put("acfgKey", "BANK_DETAIL");
        return params;
    }

}
