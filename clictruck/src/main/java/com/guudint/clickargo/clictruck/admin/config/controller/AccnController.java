package com.guudint.clickargo.clictruck.admin.config.controller;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vcc.camelone.common.controller.entity.AbstractEntityController;


@CrossOrigin
@RestController
@RequestMapping("/api/v1/clickargo/clictruck/accn")
@Deprecated
public class AccnController extends AbstractEntityController {

    private static final String entity = "coreAccn";

    private static Logger LOG = Logger.getLogger(AccnController.class);

    @GetMapping("/list")
    public ResponseEntity<Object> getEntitiesBy(@RequestParam Map<String, String> params) {
        LOG.debug("getEntitiesBy");
        return super.getEntitiesBy(entity, params);
    }
}
