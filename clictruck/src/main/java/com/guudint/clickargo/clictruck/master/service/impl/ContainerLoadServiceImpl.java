package com.guudint.clickargo.clictruck.master.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.guudint.clickargo.clictruck.master.constant.ContainerLoad;
import com.guudint.clickargo.clictruck.master.dto.GeneralReference;
import com.guudint.clickargo.clictruck.master.service.MasterService;

public class ContainerLoadServiceImpl implements MasterService<GeneralReference> {

    private static Logger LOG = Logger.getLogger(CkCtMstVehTypeServiceImpl.class);

    @Override
    public List<GeneralReference> listAll() {
        LOG.info("get all container load");
        List<ContainerLoad> containerLoads = Arrays.asList(ContainerLoad.class.getEnumConstants());
        List<GeneralReference> generalReferences = new ArrayList<>();
        for(ContainerLoad containerLoad : containerLoads){
            generalReferences.add(new GeneralReference(containerLoad.name(), containerLoad.getLabel()));
        }
        return generalReferences;
    }

    @Override
    public List<GeneralReference> listByStatus(Character status) {
        return new ArrayList<>();
    }

}
