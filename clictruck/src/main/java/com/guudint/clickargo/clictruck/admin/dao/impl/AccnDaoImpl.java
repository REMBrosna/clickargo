package com.guudint.clickargo.clictruck.admin.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.guudint.clickargo.clictruck.admin.constant.CoreAccnConstant;
import com.guudint.clickargo.clictruck.admin.dao.AccnDao;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;
import com.vcc.camelone.master.model.TMstAccnType;

public class AccnDaoImpl extends GenericDaoImpl<TCoreAccn, String> implements AccnDao {

    @Override
    public List<TCoreAccn> findByField(TCoreAccn tCoreAccn, int offset, int limit) throws Exception {
        Criteria criteria = currentSession().createCriteria(TCoreAccn.class);
        TMstAccnType tMstAccnType = Optional.ofNullable(tCoreAccn.getTMstAccnType()).orElse(new TMstAccnType());
        if (StringUtils.isNotBlank(tMstAccnType.getAtypId())) {
            criteria.add(Restrictions.eq(CoreAccnConstant.PropertyName.ACCN_TYPE, tCoreAccn.getTMstAccnType()));
        }
        if (StringUtils.isNotBlank(tCoreAccn.getAccnName())) {
            criteria.add(Restrictions.ilike(CoreAccnConstant.PropertyName.ACCN_NAME, tCoreAccn.getAccnName(),
                    MatchMode.ANYWHERE));
        }
        criteria.addOrder(Order.asc(CoreAccnConstant.PropertyName.ACCN_NAME));
        List<TCoreAccn> tCoreAccns = new ArrayList<>();
        for (Object obj : criteria.list()) {
            tCoreAccns.add(TCoreAccn.class.cast(obj));
        }
        return tCoreAccns;
    }

}
