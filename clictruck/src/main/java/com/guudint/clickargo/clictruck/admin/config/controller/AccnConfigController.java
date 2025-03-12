package com.guudint.clickargo.clictruck.admin.config.controller;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.guudint.clickargo.clictruck.va.service.impl.VirtualAccountServiceImpl;
import com.vcc.camelone.common.controller.entity.AbstractEntityController;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;

@CrossOrigin
@RequestMapping("/api/v1/clickargo/clictruck/accnConfig")
public class AccnConfigController extends AbstractEntityController {

    private static Logger LOG = Logger.getLogger(AccnConfigController.class);
    
    @Autowired
	private VirtualAccountServiceImpl virtualAccountServiceImpl;
    
    @PostMapping("/{entity}")
	public ResponseEntity<Object> createEntity(@PathVariable String entity, @RequestBody String object) {
		LOG.debug("createEntity");
		return super.createEntity(entity, object);
	} 
   
    @GetMapping("/{entity}")
    public ResponseEntity<Object> getEntities(@PathVariable String entity) {
        LOG.debug("getEntities");
        try {
            getPrincipal();
        } catch (ProcessingException e) {
            LOG.error(e.getMessage(), e);
            ServiceStatus serviceStatus = new ServiceStatus();
            serviceStatus.setErr(new ServiceError(-100, e));
            serviceStatus.setStatus(STATUS.EXCEPTION);
            return ResponseEntity.badRequest().body(serviceStatus);
        }
        return super.getEntities(entity);
    }

    @GetMapping("/{entity}/{id}")
    public ResponseEntity<Object> getEntityById(@PathVariable String entity, @PathVariable String id) {
        LOG.debug("getEntityById");
        try {
            getPrincipal();
        } catch (ProcessingException e) {
            LOG.error(e.getMessage(), e);
            ServiceStatus serviceStatus = new ServiceStatus();
            serviceStatus.setErr(new ServiceError(-100, e));
            serviceStatus.setStatus(STATUS.EXCEPTION);
            return ResponseEntity.badRequest().body(serviceStatus);
        }
        return super.getEntityById(entity, id);
    }

    @PutMapping("/{entity}/{id}")
    public ResponseEntity<Object> updateEntity(@PathVariable String entity, @RequestBody String object, @PathVariable String id) {
        LOG.debug("updateEntity");
        try {
            getPrincipal();
        } catch (ProcessingException e) {
            LOG.error(e.getMessage(), e);
            ServiceStatus serviceStatus = new ServiceStatus();
            serviceStatus.setErr(new ServiceError(-100, e));
            serviceStatus.setStatus(STATUS.EXCEPTION);
            return ResponseEntity.badRequest().body(serviceStatus);
        }
        return super.updateEntity(object, entity, id);
    }

    @DeleteMapping("/{entity}/{id}")
    public ResponseEntity<Object> deleteEntityById(@PathVariable String entity, @PathVariable String id) {
        LOG.debug("getEntityById");
        try {
            getPrincipal();
        } catch (ProcessingException e) {
            LOG.error(e.getMessage(), e);
            ServiceStatus serviceStatus = new ServiceStatus();
            serviceStatus.setErr(new ServiceError(-100, e));
            serviceStatus.setStatus(STATUS.EXCEPTION);
            return ResponseEntity.badRequest().body(serviceStatus);
        }
        return super.deleteEntityById(entity, id);
    }
    
    @GetMapping("/{entity}/list")
   	public ResponseEntity<Object> getEntitiesBy(@PathVariable String entity, @RequestParam Map<String, String> params) {
   		LOG.debug("getEntitiesBy Controller");
   		return super.getEntitiesBy(entity, params);
   	}

    @GetMapping("/{entity}/accnVAEnable")
	public ResponseEntity<Object> getAccnVAEnable(@PathVariable String entity) throws Exception {
		LOG.debug("accnVAEnable Controller");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			serviceStatus.setData(virtualAccountServiceImpl.accnVAEnable());
			serviceStatus.setStatus(STATUS.SUCCESS);
			return ResponseEntity.ok().body(serviceStatus);
		} catch (ParameterException | ProcessingException e) {
			LOG.error(e);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			serviceStatus.setStatus(STATUS.EXCEPTION);
			return ResponseEntity.badRequest().body(serviceStatus);
		}
	}

	@GetMapping("/{entity}/generateVA/{id}")
	public ResponseEntity<Object> generateVA(@PathVariable String entity, @PathVariable String id) throws Exception {
		LOG.debug("generateVA Controller");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			serviceStatus.setData(virtualAccountServiceImpl.generateVANumber(id));
			serviceStatus.setStatus(STATUS.SUCCESS);
			return ResponseEntity.ok().body(serviceStatus);
		} catch (ParameterException | ProcessingException e) {
			LOG.error(e);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			serviceStatus.setStatus(STATUS.EXCEPTION);
			return ResponseEntity.badRequest().body(serviceStatus);
		}
	}
    
}
