package com.guudint.clickargo.clictruck.master.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.guudint.clickargo.clictruck.dto.Response;
import com.guudint.clickargo.clictruck.master.service.MasterService;
import com.guudint.clickargo.common.RecordStatus;

@RequestMapping("/api/v1/clickargo/clictruck/master")
@CrossOrigin
public class MasterController {

    @Autowired
    private ApplicationContext applicationContext;

    private HashMap<String, String> mapServices;

    @GetMapping("/{entity}")
    public ResponseEntity<?> listAll(@PathVariable String entity) throws Exception {
        Object bean = applicationContext.getBean(mapServices.get(entity));
        MasterService<?> masterService = (MasterService<?>) bean;
        List<?> list = masterService.listAll();
        String message = "Data Master " + bean.getClass();
        Response response = new Response();
        if(list.size() == 0){
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setError(message);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } 
        response.setStatus(HttpStatus.OK.value());
        response.setSuccess(message);
        response.getData().addAll(list);
        return ResponseEntity.ok().body(response);
    }
    
    @GetMapping("/{entity}/{status}")
    public ResponseEntity<?> listByStatus(@PathVariable String entity, @PathVariable String status){
        Object bean = applicationContext.getBean(mapServices.get(entity));
        MasterService<?> masterService = (MasterService<?>) bean;
        char recordStatus = "active".equals(status) ? RecordStatus.ACTIVE.getCode() : RecordStatus.INACTIVE.getCode();
        List<?> list = masterService.listByStatus(recordStatus);
        String message = "Data Master " + bean.getClass();
        Response response = new Response();
        if(list.size() == 0){
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setError(message);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } 
        response.setStatus(HttpStatus.OK.value());
        response.setSuccess(message);
        response.getData().addAll(list);
        return ResponseEntity.ok().body(response);
    }

    public HashMap<String, String> getMapServices() {
        return mapServices;
    }

    public void setMapServices(HashMap<String, String> mapServices) {
        this.mapServices = mapServices;
    }
}
