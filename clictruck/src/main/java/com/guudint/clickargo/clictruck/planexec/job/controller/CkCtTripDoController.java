package com.guudint.clickargo.clictruck.planexec.job.controller;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkCtTripAttachServiceImpl;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkCtTripDoServiceImpl;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripAttach;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripDo;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripDoAttach;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripDo;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.service.ICkSession;
import com.guudint.clickargo.controller.AbstractCkController;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;
import com.vcc.camelone.common.service.entity.IEntityService;

@RequestMapping(value = "/api/v1/clickargo/clictruck/tripdo")
@CrossOrigin
public class CkCtTripDoController extends AbstractCkController {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(CkCtTripDoController.class);

	@Autowired
	protected ICkSession ckSession;
	
	@Autowired
	protected IEntityService<TCkCtTrip, String, CkCtTrip> ckCtTripService;
	
	@Autowired
	protected IEntityService<TCkCtTripDo, String, CkCtTripDo> ckCtTripDoService;
	
	@Autowired
	private CkCtTripDoServiceImpl ckCtTripDoServiceImpl;
	
	@Autowired
	private CkCtTripAttachServiceImpl ckCtTripAttachServiceImpl;
	
	// Interface Methods
	/////////////////////
	@GetMapping("{entity}")
	public ResponseEntity<Object> newEntity(@PathVariable String entity){
		return super.newEntity(entity);
	}
	
	/**
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.controller.entity.IEntityServiceController#createEntity(java.lang.String,
	 *      java.lang.String)
	 */
	@RequestMapping(value = "/{entity}", method = RequestMethod.POST)
	public ResponseEntity<Object> createEntity(@PathVariable String entity, @RequestBody String object) {
		log.debug("createEntity");
		return super.createEntity(entity, object);
		
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.controller.entity.IEntityServiceController#getEntityById(java.lang.String,
	 *      java.lang.String)
	 */
	@RequestMapping(value = "{entity}/{id}", method = RequestMethod.GET)
	public ResponseEntity<Object> getEntityById(@PathVariable String entity, @PathVariable String id) {
		log.debug("getEntityById");
		return super.getEntityById(entity, id);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.controller.entity.IEntityServiceController#updateEntity(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@RequestMapping(value = "{entity}/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Object> updateEntity(@RequestBody String object, @PathVariable String entity,
			@PathVariable String id) {
		log.debug("updateEntity");
		return super.updateEntity(object, entity, id);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.controller.entity.IEntityServiceController#deleteEntityById(java.lang.String,
	 *      java.lang.String)
	 */
	@RequestMapping(value = "{entity}/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Object> deleteEntityById(@PathVariable String entity, @PathVariable String id) {
		log.debug("getEntityById");
		return super.deleteEntityById(entity, id);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.controller.entity.IEntityServiceController#getEntitiesBy(java.lang.String,
	 *      java.util.Map)
	 */
	@RequestMapping(value = "/{entity}/list", method = RequestMethod.GET)
	public ResponseEntity<Object> getEntitiesBy(@PathVariable String entity, @RequestParam Map<String, String> params) {
		log.debug("getEntitiesBy");
		return super.getEntitiesBy(entity, params);
	}
	
	@PostMapping(value = "/doattach")
	public ResponseEntity<Object> createDoAttach(@RequestBody CkCtTripDoAttach ckCtTripDoAttach,
			@RequestParam String type) throws ParameterException, EntityNotFoundException, ProcessingException {

		CkCtTrip tCkCtTrip = ckCtTripService.findById(ckCtTripDoAttach.getTCkCtTrip().getTrId());
		ckCtTripDoAttach.setDoaId(CkUtil.generateId(ICkConstant.PREFIX_JOB_ATT));
		ckCtTripDoAttach.setTCkCtTrip(tCkCtTrip);
		
		ckCtTripDoAttach.setDoaStatus(RecordStatus.ACTIVE.getCode());
		ckCtTripDoAttach.setDoaDtCreate(Calendar.getInstance().getTime());
		ckCtTripDoAttach.setDoaUidCreate(ckSession.getPrincipal().getUserId());
		ckCtTripDoAttach.setDoaDtLupd(Calendar.getInstance().getTime());
		ckCtTripDoAttach.setDoaUidLupd(ckSession.getPrincipal().getUserId());
		
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			ckCtTripDoAttach = ckCtTripDoServiceImpl.createAttachment(ckCtTripDoAttach, type, ckSession.getPrincipal());
			return ResponseEntity.ok(ckCtTripDoAttach);
		} catch (ValidationException ex) {
			log.error("createTripAtt ", ex);
			serviceStatus.setData(ckCtTripDoAttach);
			serviceStatus.setStatus(STATUS.VALIDATION_FAILED);
			serviceStatus.setErr(new ServiceError(-500, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception ex) {
			log.error("createDoAttach", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}
	
	/**
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	@GetMapping("{entity}/fileData")
	public ResponseEntity<Object> getFileData(@RequestParam String id, @RequestParam String type) {
		log.debug("getFileData");
		try {
			switch (type) {
				case "doAttach":
					return ResponseEntity.ok(ckCtTripDoServiceImpl.getFileData(id, null));
				default:
					return null;
			}
		} catch (Exception e) {
            log.error("getFileData", e);
            ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
        }
	}
	
	/**
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	@DeleteMapping("{entity}/deleteDoAttach")
	public ResponseEntity<Object> deleteDoAttach(@RequestParam String id, @RequestParam String type) {
		log.debug("deleteDoAttach");
		try {
			switch (type) {
				case "unsigned":
					return ResponseEntity.ok(ckCtTripDoServiceImpl.deleteDoAttach(id, false, getPrincipal()));
				case "signed":
					return ResponseEntity.ok(ckCtTripDoServiceImpl.deleteDoAttach(id, true, getPrincipal()));
				default:
					return null;
			}
		} catch (Exception e) {
            log.error("deleteDoAttach", e);
            ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
        }
	}
	
	@PostMapping(value = "/doCreate")
	public ResponseEntity<Object> createDo(@RequestBody CkCtTripDo ckCtTripDo) throws ParameterException, EntityNotFoundException, ProcessingException {

		CkCtTrip tCkCtTrip = ckCtTripService.findById(ckCtTripDo.getTCkCtTrip().getTrId());
		ckCtTripDo.setDoId(CkUtil.generateId(ICkConstant.PREFIX_JOB_ATT));
		ckCtTripDo.setTCkCtTrip(tCkCtTrip);
		
		ckCtTripDo.setDoStatus(RecordStatus.ACTIVE.getCode());
		ckCtTripDo.setDoDtCreate(Calendar.getInstance().getTime());
		ckCtTripDo.setDoUidCreate(ckSession.getPrincipal().getUserId());
		ckCtTripDo.setDoDtLupd(Calendar.getInstance().getTime());
		ckCtTripDo.setDoUidLupd(ckSession.getPrincipal().getUserId());
		
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			ckCtTripDo = ckCtTripDoServiceImpl.createDo(ckCtTripDo, ckSession.getPrincipal());
			return ResponseEntity.ok(ckCtTripDo);
		} catch (Exception ex) {
			log.error("createDo", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}
	
	@Deprecated
	@PutMapping(value = "/doUpdate")
	public ResponseEntity<Object> updateDo(@RequestBody CkCtTripDo ckCtTripDo) throws ParameterException, EntityNotFoundException, ProcessingException {
	
		CkCtTrip tCkCtTrip = ckCtTripService.findById(ckCtTripDo.getTCkCtTrip().getTrId());
		ckCtTripDo.setTCkCtTrip(tCkCtTrip);
		
		CkCtTripDo tCkCtTripDo = ckCtTripDoService.findById(ckCtTripDo.getDoId());
		ckCtTripDo.setDoStatus(tCkCtTripDo.getDoStatus());
		ckCtTripDo.setDoDtCreate(tCkCtTripDo.getDoDtCreate());
		ckCtTripDo.setDoUidCreate(tCkCtTripDo.getDoUidCreate());
		ckCtTripDo.setDoDtLupd(Calendar.getInstance().getTime());
		ckCtTripDo.setDoUidLupd(ckSession.getPrincipal().getUserId());
		ckCtTripDo.setDoUnsigned(tCkCtTripDo.getDoUnsigned());
		if (null != ckCtTripDo.getDoSigned())
			ckCtTripDo.setDoSigned(tCkCtTripDo.getDoSigned());
		if (null != ckCtTripDo.getDoUnsigned())
			ckCtTripDo.setDoNo(tCkCtTripDo.getDoNo());
		
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			ckCtTripDo = ckCtTripDoServiceImpl.updateDo(ckCtTripDo, ckSession.getPrincipal());
			return ResponseEntity.ok(ckCtTripDo);
		} catch (Exception ex) {
			log.error("updateDo", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}
	
	@DeleteMapping("{entity}/doDelete")
	public ResponseEntity<Object> deleteDo(@RequestParam String doaId, @RequestParam String doNo) {
		log.debug("deleteDoAttach");
		try {
			return ResponseEntity.ok(ckCtTripDoServiceImpl.deleteDo(doaId, doNo, getPrincipal()));
		} catch (Exception e) {
            log.error("deleteDoAttach", e);
            ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
        }
	}
	
	/**
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	@Deprecated
	@GetMapping("{entity}/doFileData")
	public ResponseEntity<Object> getDoFileData(@RequestParam String id, @RequestParam String type) {
		log.debug("getFileData");
		try {
			switch (type) {
				case "signed":
					return ResponseEntity.ok(ckCtTripDoServiceImpl.getSignedDoFileData(id, getPrincipal()));
				case "unsigned":
					return ResponseEntity.ok(ckCtTripDoServiceImpl.getUnsignedDoFileData(id, getPrincipal()));
				default:
					return null;
			}
		} catch (Exception e) {
            log.error("getFileData", e);
            ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
        }
	}
	
	@PostMapping(value = "/createTripDo")
	public ResponseEntity<Object> createTripDo(@RequestParam List<TCkCtTripDo> tripDos) {
		log.debug("createTripDo");
		try {
			return ResponseEntity.ok(ckCtTripDoServiceImpl.createTripDo(tripDos));
		} catch (Exception e) {
            log.error("getFileData", e);
            ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
        }
	}
	
	/**
	 * This method creates a trip attachment
	 * @param ckCtTripAttach
	 * @return
	 * @throws ParameterException
	 * @throws EntityNotFoundException
	 * @throws ProcessingException
	 */
	@PostMapping(value = "/{entity}/createTripAtt")
	public ResponseEntity<Object> createTripAtt(@RequestBody CkCtTripAttach ckCtTripAttach) {

		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			ckCtTripAttach = ckCtTripAttachServiceImpl.createAttachment(ckCtTripAttach, ckSession.getPrincipal());
			return ResponseEntity.ok(ckCtTripAttach);
		} catch (ValidationException ex) {
			log.error("createTripAtt ", ex);
			serviceStatus.setData(ckCtTripAttach);
			serviceStatus.setStatus(STATUS.VALIDATION_FAILED);
			serviceStatus.setErr(new ServiceError(-500, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception ex) {
			log.error("createTripAtt ", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}
	
	/**
	 * This method retrieves a trip attachment
	 * @param id
	 * @return
	 */
	@GetMapping("/{entity}/getTripAtt")
	public ResponseEntity<Object> getTripAtt(@RequestParam String id) {
		log.debug("getTripAtt");
		try {
			return ResponseEntity.ok(ckCtTripAttachServiceImpl.getAttachment(id, null));
		} catch (Exception e) {
            log.error("getTripAtt ", e);
            ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
        }
	}
	
	/**
	 * This method deletes a trip attachment
	 * @param id
	 * @return
	 */
	@DeleteMapping("/{entity}/deleteTripAtt")
	public ResponseEntity<Object> deleteTripAtt(@RequestParam String id) {
		log.debug("deleteTripAtt");
		try {
			return ResponseEntity.ok(ckCtTripAttachServiceImpl.deleteAttachment(id, getPrincipal()));
		} catch (Exception e) {
            log.error("deleteTripAtt ", e);
            ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}
}
