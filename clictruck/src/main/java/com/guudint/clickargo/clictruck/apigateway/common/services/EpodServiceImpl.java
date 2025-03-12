package com.guudint.clickargo.clictruck.apigateway.common.services;

import com.guudint.clickargo.clictruck.apigateway.dto.AccountTypeEnum;
import com.guudint.clickargo.clictruck.apigateway.dto.Company;
import com.guudint.clickargo.clictruck.common.dao.CkCtEpodTemplateDao;
import com.guudint.clickargo.clictruck.common.model.TCkCtEpodTemplate;
import com.guudint.clickargo.clictruck.apigateway.common.AbstractApiGatewayService;
import com.guudint.clickargo.clictruck.apigateway.dto.Epod;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripDoAttach;
import com.guudint.clickargo.manageaccn.dao.CkCoreAccnDao;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.dao.GenericDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
@Service
public class EpodServiceImpl extends AbstractApiGatewayService<Epod> {
    private static final Logger log = Logger.getLogger(EpodServiceImpl.class);

    @Autowired
    private CkCtEpodTemplateDao ckCtEpodTemplateDao;
    @Autowired
    private CkCoreAccnDao ckCoreAccnDao;

    @Autowired
    @Qualifier("ckCtTripDoAttachDao")
    protected GenericDao<TCkCtTripDoAttach, String> ckCtTripDoAttachDao;

    @Override
    public Optional<Object> getListByAccnId(String accnId, Map<String, String> params) throws Exception {
        log.info("Fetching EPOD list");
        List<Epod> epods = new ArrayList<>();
        List<TCkCtEpodTemplate> epodTemplateList = getListEpodTemplate(accnId);
        for (TCkCtEpodTemplate entity : epodTemplateList) {
            Epod epod = new Epod();
            epod.setId(entity.getEpodId());
            TCoreAccn coreAccnTo = getCompanyInfo(entity.getEpodAccnTo());
            if (coreAccnTo != null) {
                Company to = new Company();
                to.setId(coreAccnTo.getAccnId());
                to.setName(coreAccnTo.getAccnName());
                to.setType(AccountTypeEnum.ACC_TYPE_TO.getDesc());
                epod.setTo(to);
            }
            TCoreAccn coreAccnCo = getCompanyInfo(entity.getEpodAccnCo());
            if (coreAccnCo != null) {
                Company co = new Company();
                co.setId(coreAccnCo.getAccnId());
                co.setName(coreAccnCo.getAccnName());
                co.setType(AccountTypeEnum.ACC_TYPE_CO.getDesc());
                epod.setCo(co);
            }
            TCoreAccn coreAccnFf = getCompanyInfo(entity.getEpodAccnFf());
            if (coreAccnFf != null) {
                Company ff = new Company();
                ff.setId(coreAccnFf.getAccnId());
                ff.setName(coreAccnFf.getAccnName());
                ff.setType(AccountTypeEnum.ACC_TYPE_FF.getDesc());
                epod.setFf(ff);
            }
            epods.add(epod);
        }
        return Optional.of(epods);
    }
    public List<TCkCtEpodTemplate> getListEpodTemplate(String accnId) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("accnId", accnId);
        String hql = "SELECT o FROM TCkCtEpodTemplate o " +
                "WHERE (o.epodAccnFf = :accnId OR o.epodAccnTo = :accnId OR o.epodAccnCo = :accnId) " +
                "AND o.epodStatus = 'A' ORDER BY o.epodId ASC";
        return ckCtEpodTemplateDao.getByQuery(hql, param);
    }
    private TCoreAccn getCompanyInfo(String accnId) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("accnId", accnId);
        String hql = "SELECT o FROM TCoreAccn o WHERE o.accnId = :accnId AND o.accnStatus = 'A'";
        List<TCoreAccn> accnList = ckCoreAccnDao.getByQuery(hql, param);
        return accnList.isEmpty() ? null : accnList.get(0);
    }

}
