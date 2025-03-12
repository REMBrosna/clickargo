package com.guudint.clickargo.clictruck.planexec.job.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripLocation;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.service.TripMobileService;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripLocation;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtTripLocationService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.guudint.clickargo.clictruck.constant.TruckTripAttachmentEnum;
import com.guudint.clickargo.clictruck.master.dao.CkCtMstTripAttachTypeDao;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstTripAttachType;
import com.guudint.clickargo.clictruck.planexec.job.validator.TripAttachValidator;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripAttach;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripAttach;
import com.guudint.clickargo.clictruck.util.FileUtil;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.common.service.ICkSession;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.config.model.TCoreSysparam;
import com.vcc.camelone.util.bean.COBeansUtil;

@Service
public class CkCtTripAttachServiceImpl {

	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(CkCtTripDoServiceImpl.class);
	
	public static final String PREFIX_TRIP_ATT = "CKTATT";

	@Autowired
	@Qualifier("coreSysparamDao")
	protected GenericDao<TCoreSysparam, String> coreSysparamDao;
	
	@Autowired
	@Qualifier("ckCtTripDao")
	protected GenericDao<TCkCtTrip, String> ckCtTripDao;
	
	@Autowired
	protected GenericDao<TCkCtTripAttach, String> ckCtTripAttachDao;
	
	@Autowired
	protected CkCtMstTripAttachTypeDao ckCtMstTripAttachTypeDao;
	
	@Autowired
	protected ICkSession ckSession;
	
	@Autowired
	protected TripAttachValidator validator;

	@Autowired
	protected CkCtTripLocationService ckCtTripLocationService;
	
	/**
	 * This method creates a trip attachment
	 * @param ckCtTripAttach
	 * @param principal
	 * @return
	 * @throws ParameterException
	 * @throws EntityNotFoundException
	 * @throws ValidationException
	 * @throws ProcessingException
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtTripAttach createAttachment(CkCtTripAttach ckCtTripAttach, Principal principal)
			throws ParameterException, ValidationException, Exception {
		LOG.debug("createAttachment");

		if (null == ckCtTripAttach)
			throw new ParameterException("ckAccnAtt jobAttach null");
		if (null == principal)
			throw new ParameterException("principal null");

        // CT-260 - File type validation when upload pickup, dropoff images and DO attachment
		List<ValidationError> errors = validator.validateCreate(ckCtTripAttach, principal);

		if (!errors.isEmpty()) {
			throw new ValidationException(this.validationErrorMap(errors));
		}

		Optional<String> opTrId = Optional.ofNullable(ckCtTripAttach.getTCkCtTrip().getTrId());
		boolean allowRequired = Objects.nonNull(ckCtTripAttach.getTCkCtMstTripAttachType()) &&
				Objects.nonNull(ckCtTripAttach.getTCkCtMstTripAttachType().getAtypId()) && (
						TripMobileService.TripAttachTypeEnum.PHOTO.name().equalsIgnoreCase(ckCtTripAttach.getTCkCtMstTripAttachType().getAtypId()) ||
						TripMobileService.TripAttachTypeEnum.DOCUMENT.name().equalsIgnoreCase(ckCtTripAttach.getTCkCtMstTripAttachType().getAtypId())
		);
		
		TCkCtTrip ckCtTrip = ckCtTripDao.find(opTrId.get());
		if (null == ckCtTrip)
			throw new ProcessingException("coreAccn not found: " + opTrId.get());
		
		Hibernate.initialize(ckCtTrip.getTCkJobTruck());

		TCkCtTripAttach tripAttE = new TCkCtTripAttach();
		BeanUtils.copyProperties(ckCtTripAttach, tripAttE, COBeansUtil.getNullPropertyNames(ckCtTripAttach));
		tripAttE.setAtId(CkUtil.generateId(PREFIX_TRIP_ATT));
		tripAttE.setTCkCtTrip(ckCtTrip);
		tripAttE.setAtStatus(RecordStatus.ACTIVE.getCode());
		tripAttE.setAtDtCreate(Calendar.getInstance().getTime());
		tripAttE.setAtUidCreate(principal.getUserId());
		tripAttE.setAtDtLupd(Calendar.getInstance().getTime());
		tripAttE.setAtUidLupd(principal.getUserId());
		
		TCkCtMstTripAttachType ckMstAccnAttType = ckCtMstTripAttachTypeDao.find(ckCtTripAttach.getTCkCtMstTripAttachType().getAtypId());
		tripAttE.setTCkCtMstTripAttachType(ckMstAccnAttType);
		tripAttE.setAtSource(TruckTripAttachmentEnum.WEB.name());

		// not required PICK_UP, DROP_OFF and SIGNATURE
		String fileLocation = null;
		if (allowRequired) {
			fileLocation = this.saveAttachment(ckCtTrip.getTCkJobTruck().getJobId(), ckCtTripAttach.getAtId(), ckCtTripAttach.getAtName(), ckCtTripAttach.getAtLocData());
		}
		if (StringUtils.isNotBlank(fileLocation))
			tripAttE.setAtLoc(fileLocation);
		ckCtTripAttachDao.saveOrUpdate(tripAttE);
		ckCtTripAttach.setAtLoc(fileLocation);
		if (Objects.nonNull(ckCtTripAttach.getTCkCtTrip()) && (
				Objects.nonNull(ckCtTripAttach.getTCkCtTrip().getTCkCtTripLocationByTrFrom()) ||
				Objects.nonNull(ckCtTripAttach.getTCkCtTrip().getTCkCtTripLocationByTrTo())
		)){
			this.savePickupAndDropOffComment(ckCtTripAttach.getTCkCtTrip(), ckMstAccnAttType.getAtypId(), ckCtTripAttach.getAtComment(), principal);
		}

		return ckCtTripAttach;
	}
	
	/**
	 * Saves the specified file to the base location configured in SysParam
	 * @param atId
	 * @param filename
	 * @param data
	 * @return
	 * @throws Exception
	 */
	protected String saveAttachment(String truckJobId, String atId, String filename, byte[] data) throws Exception {
		if (StringUtils.isBlank(filename))
			throw new ParameterException("param filename null or empty");

		if (data == null)
			throw new ParameterException("param data null or empty");

		String basePath = getSysParam(CtConstant.KEY_ATTCH_BASE_LOCATION);
		if (StringUtils.isBlank(basePath))
			throw new ProcessingException("basePath is not configured");

		return FileUtil.saveAttachment(atId, basePath.concat(truckJobId).concat(System.getProperty("file.separator")), filename, data);
	}

	protected void savePickupAndDropOffComment(CkCtTrip trip, String atypId, String atComment, Principal principal) throws Exception {
		if (trip == null || StringUtils.isBlank(atypId)) {
			throw new ParameterException("savePickupAndDropOffComment: params null or empty");
		}

		CkCtTripLocation tripLocation = null;
		TripMobileService.TripAttachTypeEnum attachType = TripMobileService.TripAttachTypeEnum.valueOf(atypId);

		switch (attachType) {
			case PHOTO:
			case DOCUMENT:
			case PHOTO_PICKUP:
				tripLocation = ckCtTripLocationService.findById(trip.getTCkCtTripLocationByTrFrom().getTlocId());
				break;
			case SIGNATURE:
			case PHOTO_DROPOFF:
				tripLocation = ckCtTripLocationService.findById(trip.getTCkCtTripLocationByTrTo().getTlocId());
				break;
			default:
				throw new IllegalArgumentException("Invalid attachment type: " + atypId);
		}
		tripLocation.setTlocComment(atComment);
		ckCtTripLocationService.update(tripLocation, principal);
	}
	
	protected String getSysParam(String key) throws Exception {
		if (StringUtils.isBlank(key))
			throw new ParameterException("param key null or empty");

		TCoreSysparam sysParam = coreSysparamDao.find(key);
		if (sysParam != null) {
			return sysParam.getSysVal();
		}

		return null;
	}

	/**
	 * This method retrieves trip attachment
	 * @param id
	 * @param principal
	 * @return
	 * @throws ParameterException
	 * @throws ProcessingException
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public CkCtTripAttach getAttachment(String id, Principal principal) throws ParameterException, ProcessingException {
        LOG.debug("getAttachment");
        if (null == id) throw new ParameterException("param id is null or empty");
        try {
            TCkCtTripAttach tCkCtTripAttach = ckCtTripAttachDao.find(id);
            if (null == tCkCtTripAttach) throw new ProcessingException("tCkCtTripAttach not found with id : " + id);
            
            String fileName = FilenameUtils.getName(tCkCtTripAttach.getAtLoc());
            File file = new File(tCkCtTripAttach.getAtLoc());

            CkCtTripAttach dto = new CkCtTripAttach();
            BeanUtils.copyProperties(tCkCtTripAttach, dto, COBeansUtil.getNullPropertyNames(tCkCtTripAttach));
            dto.setAtLocData(Files.readAllBytes(file.toPath()));
			dto.setAtName(fileName);
            return dto;
        } catch (Exception ex) {
            LOG.error("getAttachment", ex);
            throw new ProcessingException(ex);
        }
    }
	
	/**
	 * Deletes a trip attachment and physical location in the server
	 * @param id
	 * @param principal
	 * @return
	 * @throws ParameterException
	 * @throws ProcessingException
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public int deleteAttachment(String id, Principal principal) throws ParameterException, ProcessingException {
        LOG.debug("deleteTripAttach");
        if (null == id) throw new ParameterException("param id is null or empty");
        try {
            TCkCtTripAttach tCkCtTripAttach = ckCtTripAttachDao.find(id);
            if (null == tCkCtTripAttach) throw new ProcessingException("tCkCtTripAttach not found with id : " + id);
            
    		Map<String, Object> params = new HashMap<>();
    		params.put("atId", id);
    		params.put("atStatus", RecordStatus.ACTIVE.getCode());
    		   		
    		// Delete file from the server [START]
      		String serverFile = tCkCtTripAttach.getAtLoc();
      		if (null != serverFile && !StringUtils.isEmpty(serverFile)) {
      	      File file = new File(serverFile);
      	      File directory = new File(file.getParent());
      	      this.deleteFolder(directory);
      	      LOG.debug(serverFile + " successfully deleted from the server!");
      		}
      		// Delete file from the server [END]
    		
    		int result = ckCtTripAttachDao.executeUpdate("DELETE FROM TCkCtTripAttach o WHERE o.atId = :atId AND o.atStatus = :atStatus", params);
    		
    		return result;
        } catch (Exception ex) {
            LOG.error("deleteTripAttach ", ex);
            throw new ProcessingException(ex);
        }
    }
	
	/**
	 * Method to delete document folder from server
	 * @param directory
	 */
	@Transactional
	public void deleteFolder(File directory) {
		for (File subFile : directory.listFiles()) {
			if (subFile.isDirectory()) {
				deleteFolder(subFile);
			} else {
				subFile.delete();
			}
		}
		directory.delete();
	}
	
	/**
     * 
     * @param validationErrors
     * @return
     * @throws ParameterException
     * @throws ProcessingException
     */
    protected String validationErrorMap(List<ValidationError> validationErrors)
			throws ParameterException, ProcessingException {
		LOG.debug("validationErrorMap");

		if (null == validationErrors)
			throw new ParameterException("param errros null");
		if (validationErrors.isEmpty())
			throw new ProcessingException("param errros empty");

		String json;
		try {
			Map<String, String> validationMap = new HashMap<>();
			validationErrors.stream().forEach(ve -> {
				LOG.error(ve.getErrorType() + " " + ve.getErrorDescription() + "  " + ve);
				validationMap.put(ve.getErrorType().toString(), ve.getErrorDescription());
			});

			json = (new ObjectMapper()).writeValueAsString(validationMap);

		} catch (Exception ex) {
			LOG.error("validationErrorMap", ex);
			throw new ProcessingException(ex.getMessage());
		}
		return json;
	}
	
}
