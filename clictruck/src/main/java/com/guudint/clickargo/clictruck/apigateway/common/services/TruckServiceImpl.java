package com.guudint.clickargo.clictruck.apigateway.common.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.guudint.clickargo.clictruck.apigateway.dto.Chasis;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.apigateway.common.AbstractApiGatewayService;
import com.guudint.clickargo.clictruck.apigateway.dto.Truck;
import com.guudint.clickargo.clictruck.common.service.impl.CkCtVehServiceImpl;
import com.guudint.clickargo.common.service.ICkSession;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.ProcessingException;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
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
public class TruckServiceImpl extends AbstractApiGatewayService<Truck> {
    private static Logger log = Logger.getLogger(TruckServiceImpl.class);

    @Autowired
    @Qualifier("ckCtVehDao")
    private GenericDao<TCkCtVeh, String> ckCtVehDao;

    @Autowired
    private CkCtVehServiceImpl ckCtVehService;

    @Override
    public Optional<Object> getListByAccnId(String accnId, Map<String, String> params) throws Exception {
        log.info("getVehicleList");
        List<Truck> vehList = new ArrayList<>();
        List<TCkCtVeh> vehLists = getVehLists(accnId);
        for (TCkCtVeh entity : vehLists) {

            Truck veh = new Truck();
            veh.setId(entity.getVhId());
            veh.setCompanyId(accnId);
            veh.setType(entity.getTCkCtMstVehType() != null ? entity.getTCkCtMstVehType().getVhtyId() : null);
            veh.setLicenseNo(entity.getVhPlateNo());
            veh.setClazz(entity.getVhClass() != null ? entity.getVhClass() : 0);
            veh.setLength(entity.getVhLength() != null ? String.valueOf(entity.getVhLength()) : null);
            veh.setWidth(entity.getVhWidth() != null ? String.valueOf(entity.getVhWidth()) : null);
            veh.setHeight(entity.getVhHeight() != null ? String.valueOf(entity.getVhHeight()) : null);
            veh.setMaxWeight(entity.getVhWeight() != null ? String.valueOf(entity.getVhWeight()) : null);
            veh.setVolume(entity.getVhVolume() != null ? String.valueOf(entity.getVhVolume()) : null);
            veh.setRemark(entity.getVhRemarks());
            veh.setWeightUOM("KG");
            veh.setSizeUOM("M");
            if (entity.getTCkCtMstChassisType() != null){
                Chasis chasis = new Chasis();
                chasis.setSize(entity.getTCkCtMstChassisType().getChtyId());
                if (entity.getVhChassisNo() != null){
                    JsonObject json = ckCtVehService.getAsJson(entity.getVhChassisNo());
                    if (json == null){
                        chasis.setNumber(entity.getVhChassisNo());
                    } else {
                        String vhChassisNoOth = String.valueOf(json.get("vhChassisNoOth")).replace("\"", "");
                        chasis.setNumber(vhChassisNoOth);
                    }
                }
                veh.setChasis(chasis);
            }
            vehList.add(veh);
        }
        return Optional.of(vehList);
    }

    private List<TCkCtVeh> getVehLists(String accnId) throws Exception {
        log.info("getVehLists");
        Map<String, Object> params = new HashMap<>();
        params.put("accnId", accnId);
        String hql = "SELECT o FROM TCkCtVeh o WHERE o.vhStatus = 'A' AND o.TCoreAccn.accnId= :accnId ORDER BY o.vhId ASC";
        return ckCtVehDao.getByQuery(hql, params);
    }

}
