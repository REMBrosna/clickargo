package com.acleda.company.student.master.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping(value = "/api/v1/")
@CrossOrigin
@RestController
public class EntityServiceController extends AbstractEntityController {

    // Static Attributes
    ////////////////////
    private static final Logger log = LoggerFactory.getLogger(EntityServiceController.class);

    @RequestMapping(value = "/{entity}", method = RequestMethod.POST)
    public ResponseEntity<Object> createEntity(@PathVariable String entity, @RequestBody String object) {
        log.debug("createEntity");
        return super.createEntity(entity, object);
    }

    @RequestMapping(value = "/{entity}", method = RequestMethod.GET)
    public ResponseEntity<Object> getEntities(@PathVariable String entity) {
        log.debug("getEntities");
        return super.getEntities(entity);
    }

    @RequestMapping(value = "{entity}/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getEntityById(@PathVariable String entity, @PathVariable String id) {
        log.debug("getEntityById");
        return super.getEntityById(entity, id);
    }

    @RequestMapping(value = "{entity}/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateEntity(@RequestBody String object, @PathVariable String entity,
                                               @PathVariable String id) {
        log.info("updateEntity");
        return super.updateEntity(object, entity, id);
    }

    @RequestMapping(value = {"{entity}/{id}/{action}"}, method = {RequestMethod.PUT})
    public ResponseEntity<Object> updateEntityStatus(@RequestBody String object, @PathVariable String entity,
                                                     @PathVariable String id, @PathVariable String action) {
        return super.updateEntityStatus(object, entity, id, action);
    }
    @RequestMapping(value = "/{entity}/list", method = RequestMethod.GET)
    public ResponseEntity<Object> getEntitiesBy(@PathVariable String entity, @RequestParam Map<String, String> params) {
        log.debug("getEntitiesBy");
        return super.getEntitiesBy(entity, params);
    }

    @RequestMapping(value = "{entity}/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteEntityById(@PathVariable String entity, @PathVariable String id) {
        log.debug("getEntityById");
        return super.deleteEntityById(entity, id);
    }
}
