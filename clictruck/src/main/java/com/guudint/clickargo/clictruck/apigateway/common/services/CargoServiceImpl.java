package com.guudint.clickargo.clictruck.apigateway.common.services;

import com.guudint.clickargo.clictruck.apigateway.common.AbstractApiGatewayService;
import com.guudint.clickargo.clictruck.apigateway.dto.CargoType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstCargoType;
import com.vcc.camelone.common.dao.GenericDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
@Service
public class CargoServiceImpl extends AbstractApiGatewayService<CargoType> {
    private static Logger log = Logger.getLogger(CargoServiceImpl.class);

    @Autowired
    @Qualifier("ckCtMstCargoTypeDao")
    private GenericDao<TCkCtMstCargoType, String> ckCtMstCargoTypeDao;

    @Override
    public Optional<Object> getList(Map<String, String> params) throws Exception {
        log.info("getCargoTypeList");
        List<CargoType> cargoTypes = cargoTypeList().stream()
                .map(entity -> {
                    CargoType cargoType = new CargoType();
                    cargoType.setId(entity.getCrtypId());
                    cargoType.setName(entity.getCrtypName());
                    return cargoType;
                })
                .collect(Collectors.toList());
        return Optional.of(cargoTypes);
    }
    public List<TCkCtMstCargoType> cargoTypeList() throws Exception {
        String hql = "SELECT o FROM TCkCtMstCargoType o WHERE o.crtypStatus = 'A' ORDER BY o.crtypId ASC";
        return ckCtMstCargoTypeDao.getByQuery(hql);
    }
}
