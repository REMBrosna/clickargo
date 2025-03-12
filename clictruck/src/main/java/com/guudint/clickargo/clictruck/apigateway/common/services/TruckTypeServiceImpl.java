package com.guudint.clickargo.clictruck.apigateway.common.services;

import com.guudint.clickargo.clictruck.apigateway.common.AbstractApiGatewayService;
import com.guudint.clickargo.clictruck.apigateway.dto.TruckType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstVehType;
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
public class TruckTypeServiceImpl extends AbstractApiGatewayService<TruckType> {
    private static Logger log = Logger.getLogger(TruckTypeServiceImpl.class);

    @Autowired
    @Qualifier("ckCtMstVehTypeDao")
    private GenericDao<TCkCtMstVehType, String> ckCtMstVehTypeDao;

    @Override
    public Optional<Object> getList(Map<String, String> params) throws Exception {
        log.info("getTruckTypeList");
        List<TruckType> truckTypes = new ArrayList<>();
        List<TCkCtMstVehType> vehTypes = getVehTypes();
        for (TCkCtMstVehType entity: vehTypes){
            TruckType truckType = new TruckType();
            truckType.setId(entity.getVhtyId());
            truckType.setName(entity.getVhtyName());
            truckTypes.add(truckType);
        }
        return Optional.of(truckTypes);
    }
    public List<TCkCtMstVehType> getVehTypes() throws Exception {
        String hql = "SELECT o FROM TCkCtMstVehType o WHERE o.vhtyStatus = 'A' ORDER BY o.vhtyId ASC";
        return ckCtMstVehTypeDao.getByQuery(hql);
    }

}
