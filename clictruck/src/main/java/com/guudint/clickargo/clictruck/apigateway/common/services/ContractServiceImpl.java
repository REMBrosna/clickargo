package com.guudint.clickargo.clictruck.apigateway.common.services;

import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContract;
import com.guudint.clickargo.clictruck.apigateway.common.AbstractApiGatewayService;
import com.guudint.clickargo.clictruck.apigateway.dto.AccountTypeEnum;
import com.guudint.clickargo.clictruck.apigateway.dto.Company;
import com.guudint.clickargo.clictruck.apigateway.dto.Contract;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.manageaccn.dao.impl.CkCtFfCoDaoImpl;
import com.guudint.clickargo.manageaccn.model.TCkCtFfCo;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.dao.GenericDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
@Service
public class ContractServiceImpl extends AbstractApiGatewayService<Contract> {
    private static Logger log = Logger.getLogger(ContractServiceImpl.class);

    @Autowired
    @Qualifier("ckCtContractDao")
    private GenericDao<TCkCtContract, String> ckCtContractDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public Optional<Object> getListByAccnId(String accnId, Map<String, String> params) throws Exception {
        List<Contract> contracts = new ArrayList<>();
        try {
            List<TCkCtContract> contractList = this.getContractList(accnId);
            for (TCkCtContract entity : contractList) {
                Contract contract = new Contract();
                contract.setContractId(entity.getConId());
                contract.setContractName(entity.getConName());
                // TO
                TCoreAccn toAccn = entity.getTCoreAccnByConTo();
                if (toAccn != null && toAccn.getTMstAccnType() != null) {
                    if (toAccn.getTMstAccnType().getAtypId().equalsIgnoreCase(AccountTypeEnum.ACC_TYPE_TO.name())) {
                        Company to = new Company();
                        to.setId(toAccn.getAccnId());
                        to.setName(toAccn.getAccnName());
                        to.setType(AccountTypeEnum.ACC_TYPE_TO.getDesc());
                        contract.setTo(to);
                    }
                }
                // CO
                TCoreAccn coFFAccn = entity.getTCoreAccnByConCoFf();
                if (coFFAccn != null && coFFAccn.getTMstAccnType() != null) {
                    if (coFFAccn.getTMstAccnType().getAtypId().equalsIgnoreCase(AccountTypeEnum.ACC_TYPE_CO.name())){
                        Company co = new Company();
                        co.setId(coFFAccn.getAccnId());
                        co.setName(coFFAccn.getAccnName());
                        co.setType(AccountTypeEnum.ACC_TYPE_CO.getDesc());
                        contract.setCo(co);
                    }else {
                        Company ff = new Company();
                        ff.setId(coFFAccn.getAccnId());
                        ff.setName(coFFAccn.getAccnName());
                        ff.setType(AccountTypeEnum.ACC_TYPE_FF.getDesc());
                        contract.setFf(ff);
                    }
                }
                contracts.add(contract);
            }
        } catch (Exception ex){
            log.error("getContractList " + ex.getMessage());
            throw ex;
        }
        return Optional.of(contracts);
    }
    private List<TCkCtContract> getContractList(String accnId) throws Exception {
        HashMap<String, Object> params = new HashMap<>();
        params.put("accnId", accnId);
        params.put("status", RecordStatus.ACTIVE.getCode());
        String hql = "SELECT o FROM TCkCtContract o WHERE o.conStatus = :status AND " +
                "(o.TCoreAccnByConCoFf.accnId = :accnId OR o.TCoreAccnByConTo.accnId = :accnId) " +
                "ORDER BY o.conId ASC";
        return ckCtContractDao.getByQuery(hql, params);
    }
}
