package com.guudint.clickargo.clictruck.planexec.job.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.clictruck.admin.ratetable.model.TCkCtRateTable;
import com.guudint.clickargo.clictruck.common.dto.CkCtLocation;
import com.guudint.clickargo.clictruck.common.model.TCkCtLocation;
import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.guudint.clickargo.clictruck.constant.TruckTripAttachmentEnum;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripDo;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripDoAttach;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripDo;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripDoAttach;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtTripDoService;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.common.service.ICkSession;
import com.guudint.clickargo.job.dto.CkJob;
import com.guudint.clickargo.job.dto.CkJobAttach;
import com.guudint.clickargo.job.model.TCkJob;
import com.guudint.clickargo.job.model.TCkJobAttach;
import com.guudint.clickargo.job.service.impl.CkJobAttachService;
import com.guudint.clickargo.master.enums.AttachmentTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.entity.IEntityService;
import com.vcc.camelone.config.model.TCoreSysparam;
import com.vcc.camelone.util.bean.COBeansUtil;

@Service
public class CkCtTripDoServiceImpl {

	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(CkCtTripDoServiceImpl.class);
	public static final String TRIP_DO_PREFIX = "CKTDO";
	Locale locale = LocaleContextHolder.getLocale();
	
	@Autowired
	private CkJobAttachService jobAttachService;

	@Autowired
	MessageSource messageSource;
	
	@Autowired
	protected IEntityService<TCkJob, String, CkJob> ckJobService;

	@Autowired
	protected GenericDao<TCkCtRateTable, String> ckCtRateTable;
	
	@Autowired
	protected GenericDao<TCkCtLocation, String> ckCtLocation;

	@Autowired
	@Qualifier("coreSysparamDao")
	protected GenericDao<TCoreSysparam, String> coreSysparamDao;
	
	@Autowired
	@Qualifier("ckCtTripDao")
	protected GenericDao<TCkCtTrip, String> ckCtTripDao;
	
	@Autowired
	protected GenericDao<TCkCtTripDoAttach, String> ckCtTripDoAttachDao;
	
	@Autowired
	protected GenericDao<TCkCtTripDo, String> ckCtTripDoDao;
	
	@Autowired
	private CkCtTripDoService ckCtTripDoService;
	
	@Autowired
	protected ICkSession ckSession;
	
	/**
	 * Load the truck operators associated with this principal cargo owner or freight forwarder.
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CoreAccn> getTruckOperatorsByCoFf(Principal principal) throws ParameterException, Exception {
		LOG.debug("getTruckOperatorsByCoFf");
		try {

			if (principal == null)
				throw new ParameterException("param principal empty or null");
			
			SimpleDateFormat tfDate = new SimpleDateFormat("yyyy-MM-dd");
			List<CoreAccn> truckOpsList = new ArrayList<>();
			String hql = "FROM TCkCtRateTable o WHERE o.TCoreAccnByRtCoFf.accnId = :accnId AND o.rtStatus = :rtStatus"
					+ " AND :now BETWEEN DATE_FORMAT(o.rtDtStart, '%Y-%m-%d') AND DATE_FORMAT(o.rtDtEnd, '%Y-%m-%d')";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("accnId", principal.getCoreAccn().getAccnId());
			params.put("rtStatus", RecordStatus.ACTIVE.getCode());
			params.put("now", tfDate.format(Calendar.getInstance().getTime()));

			List<TCkCtRateTable> listRateTable = ckCtRateTable.getByQuery(hql, params);
			if (listRateTable != null && listRateTable.size() > 0) {
				for (TCkCtRateTable c : listRateTable) {
					Hibernate.initialize(c.getTCoreAccnByRtCompany());
					TCoreAccn accn = c.getTCoreAccnByRtCompany();
					truckOpsList.add(new CoreAccn(accn));
				}
			}

			return truckOpsList.stream()
					.collect(Collectors.toConcurrentMap(CoreAccn::getAccnId, Function.identity(), (p, q) -> p)).values()
					.stream().sorted(Comparator.comparing(CoreAccn::getAccnName)).collect(Collectors.toList());

		} catch (Exception ex) {
			LOG.error("getTruckOperatorsByCoFf", ex);
			throw ex;
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtLocation> getLocationsByTruckOperator(Principal principal, String accnId) throws ParameterException, Exception {
		LOG.debug("getLocationsByTruckOperator");
		try {

			if (principal == null)
				throw new ParameterException("param principal empty or null");
			
			SimpleDateFormat tfDate = new SimpleDateFormat("yyyy-MM-dd");
			List<CkCtLocation> locsList = new ArrayList<>();
			String hql = "FROM TCkCtLocation o WHERE o.TCoreAccn.accnId = :accnId AND o.locStatus = :locStatus"
					+ " AND :now BETWEEN DATE_FORMAT(o.locDtStart, '%Y-%m-%d') AND DATE_FORMAT(o.locDtEnd, '%Y-%m-%d')";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("accnId", accnId);
			params.put("locStatus", RecordStatus.ACTIVE.getCode());
			params.put("now", tfDate.format(Calendar.getInstance().getTime()));

			List<TCkCtLocation> listLocations = ckCtLocation.getByQuery(hql, params);
			if (listLocations != null && listLocations.size() > 0) {
				for (TCkCtLocation c : listLocations) {
					Hibernate.initialize(c.getTCoreAccn());
					Hibernate.initialize(c.getTCkCtMstLocationType());
					locsList.add(new CkCtLocation(c));
				}
			}
			return locsList.stream().sorted(Comparator.comparing(CkCtLocation::getLocName)).collect(Collectors.toList());

		} catch (Exception ex) {
			LOG.error("getLocationsByTruckOperator", ex);
			throw ex;
		}

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkJobAttach getAttachment(AttachmentTypes attachmentType, String ckJobId)
			throws ParameterException, EntityNotFoundException, Exception {
		LOG.debug("getAttachment");
		try {

			if (StringUtils.isBlank(ckJobId))
				throw new ParameterException("param ckJobId null or empty");

			if (attachmentType == null)
				throw new ParameterException("param attachmentType is null");

			// Retrieve the t_ck_job just to make sure that the job is really existing
			CkJob ckJob = ckJobService.findById(ckJobId);
			if (ckJob == null)
				throw new EntityNotFoundException("ckJob " + ckJobId + " not foundd");

			// Query from the jobAttach
			StringBuilder hql = new StringBuilder("FROM TCkJobAttach o WHERE o.attStatus=:attStatus");
			hql.append(" AND o.TMstAttType.mattId=:attType");
			hql.append(" AND o.TCkJob.jobId=:jobId");
			Map<String, Object> params = new HashMap<>();
			params.put("attStatus", RecordStatus.ACTIVE.getCode());
			params.put("attType", attachmentType.getId());
			params.put("jobId", ckJob.getJobId());

			// is it only expecting one record if we based on attachment type?
			List<TCkJobAttach> listJobAtt = jobAttachService.getDao().getByQuery(hql.toString(), params);
			if (listJobAtt != null && listJobAtt.size() > 0) {
				TCkJobAttach att = listJobAtt.get(0);
				return jobAttachService.dtoFromEntity(att, true);
			}
		} catch (Exception ex) {
			LOG.error("getAttachment", ex);
			throw ex;
		}

		return null;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtTripDoAttach createAttachment(CkCtTripDoAttach tripDoAttach, String type, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		LOG.debug("createAttachment");

		if (null == tripDoAttach)
			throw new ParameterException("param tripDoAttach null");
		if (null == principal)
			throw new ParameterException("principal null");

		Optional<String> opTripId = Optional.ofNullable(tripDoAttach.getTCkCtTrip().getTrId());
		if (!opTripId.isPresent())
			throw new ProcessingException("opTripId null or empty");

		TCkCtTrip ckCtrtrip = ckCtTripDao.find(opTripId.get());
		if (null == ckCtrtrip)
			throw new ProcessingException("ckCtrtrip job not found: " + opTripId.get());

		// Create and check duplicate doNo only for unsigned. no need to check for signed
		CkCtTripDo tripDo = tripDoAttach.getCkCtTripDo();
//		if (null != tripDo && null != ckCtTripDoService.findByDoNo(tripDo.getDoNo()) && type.equalsIgnoreCase("unsigned")) {
//			LOG.debug("Cannot create tripDo - duplicate doNo");
//			tripDoAttach.setDuplicateDoNo(true);
//			tripDoAttach.setDoaName(null);
//			return tripDoAttach;
//		}
		
		TCkCtTripDoAttach _tripDoAttach = new TCkCtTripDoAttach();
		BeanUtils.copyProperties(tripDoAttach, _tripDoAttach, COBeansUtil.getNullPropertyNames(tripDoAttach));
		_tripDoAttach.setTCkCtTrip(ckCtrtrip);
		_tripDoAttach.setDoaSource(TruckTripAttachmentEnum.WEB.name());

		// CT-260 - File type validation when upload pickup, dropoff images and DO attachment
		// Validate doaData file format
		if (!ObjectUtils.isEmpty(tripDoAttach.getDoaData())) {
			List<ValidationError> errors = this.validateCreate(tripDoAttach, type, principal);
			if (!errors.isEmpty()) {
				throw new ValidationException(this.validationErrorMap(errors));
			}
		}
		
		String fileLocation = this.saveAttachment(tripDoAttach.getDoaId(), tripDoAttach.getDoaName(),
				tripDoAttach.getDoaData());
		if (StringUtils.isNotBlank(fileLocation))
			_tripDoAttach.setDoaLoc(fileLocation);

		ckCtTripDoAttachDao.saveOrUpdate(_tripDoAttach);

		CkCtTripDo ckCtTripDoDto = null;
		TCkCtTripDo ckCtTripDoE = null;
		if (null != tripDo && !StringUtils.isEmpty(tripDo.getDoNo())) {
			//ckCtTripDoDto = ckCtTripDoService.findByDoNo(tripDo.getDoNo());
			ckCtTripDoDto = ckCtTripDoService.findByTripId(opTripId.get());
		}
		if (null != ckCtTripDoDto) {
			ckCtTripDoE = ckCtTripDoDao.find(ckCtTripDoDto.getDoId());
		}
		if (null != ckCtTripDoDto && type.equalsIgnoreCase("unsigned")) {
			ckCtTripDoE.setDoUnsigned(_tripDoAttach.getDoaId());
			ckCtTripDoDao.update(ckCtTripDoE);
		} else if (null != ckCtTripDoDto && type.equalsIgnoreCase("signed")) {
			ckCtTripDoE.setDoSigned(_tripDoAttach.getDoaId());
			ckCtTripDoDao.update(ckCtTripDoE);
		} else {
			// NO actions at this time
		}
			
		return tripDoAttach;

	};

	/**
	 * Saves the specified file to the base location configured in SysParam
	 * 
	 * @param filename
	 * @param data     - byte
	 */
	protected String saveAttachment(String doaId, String filename, byte[] data) throws Exception {
		if (StringUtils.isBlank(filename))
			throw new ParameterException("param filename null or empty");

		if (data == null)
			throw new ParameterException("param data null or empty");

		String basePath = getSysParam(CtConstant.KEY_ATTCH_BASE_LOCATION);
		if (StringUtils.isBlank(basePath))
			throw new ProcessingException("basePath is not configured");
		
		Path dir = Paths.get(basePath.concat(doaId));
		if (!Files.exists(dir)) {
			Files.createDirectories(dir);
		}

		File jobDir = new File(basePath.concat(doaId));
		File file = new File(jobDir.getAbsolutePath(), filename);
		FileOutputStream output = new FileOutputStream(file);
		output.write(data);
		output.close();
		return file.getAbsolutePath();
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

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtTripDo createDo(CkCtTripDo tripDo, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		LOG.debug("createDo");

		if (null == tripDo)
			throw new ParameterException("param tripDoAttach null");
		if (null == principal)
			throw new ParameterException("principal null");

		if (null != ckCtTripDoService.findByDoNo(tripDo.getDoNo())) {
			tripDo.setDuplicateDoNo(true);
			return tripDo;
//			throw new ProcessingException("Order number already exists!");
		}
			
		if (StringUtils.isEmpty(tripDo.getDoNo())) 
			throw new ProcessingException("Order number cannot be empty!");
		
		Optional<String> opTripId = Optional.ofNullable(tripDo.getTCkCtTrip().getTrId());
		if (!opTripId.isPresent())
			throw new ProcessingException("opTripId null or empty");

		TCkCtTrip ckCtrtrip = ckCtTripDao.find(opTripId.get());
		if (null == ckCtrtrip)
			throw new ProcessingException("ckCtrtrip job not found: " + opTripId.get());

		TCkCtTripDo _tripDo = new TCkCtTripDo();
		BeanUtils.copyProperties(tripDo, _tripDo, COBeansUtil.getNullPropertyNames(tripDo));
		_tripDo.setTCkCtTrip(ckCtrtrip);

//		String fileLocation = this.saveAttachment(tripDo.getDoId(), tripDo.getDoUnsignedName(),
//				tripDo.getDoUnsignedData());
//		if (StringUtils.isNotBlank(fileLocation))
//			_tripDo.setDoUnsigned(fileLocation);

		ckCtTripDoDao.saveOrUpdate(_tripDo);
		return tripDo;

	};
	
	@Deprecated
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtTripDo updateDo(CkCtTripDo tripDo, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		LOG.debug("createDo");

		if (null == tripDo)
			throw new ParameterException("param tripDoAttach null");
		if (null == principal)
			throw new ParameterException("principal null");

		Optional<String> opTripId = Optional.ofNullable(tripDo.getTCkCtTrip().getTrId());
		if (!opTripId.isPresent())
			throw new ProcessingException("opTripId null or empty");

		TCkCtTrip ckCtrtrip = ckCtTripDao.find(opTripId.get());
		if (null == ckCtrtrip)
			throw new ProcessingException("ckCtrtrip job not found: " + opTripId.get());

		TCkCtTripDo _tripDo = new TCkCtTripDo();
		BeanUtils.copyProperties(tripDo, _tripDo, COBeansUtil.getNullPropertyNames(tripDo));
		_tripDo.setTCkCtTrip(ckCtrtrip);

		if (null != tripDo.getDoUnsignedName()) {
			String ungsignedFileLoc = this.saveAttachment(tripDo.getDoId(), tripDo.getDoUnsignedName(),
					tripDo.getDoUnsignedData());
			if (StringUtils.isNotBlank(ungsignedFileLoc))
				_tripDo.setDoUnsigned(ungsignedFileLoc);
		}
		
		if (null != tripDo.getDoSignedName()) {
			String signedFileLoc = this.saveAttachment(tripDo.getDoId(), tripDo.getDoSignedName(),
					tripDo.getDoSignedData());
			if (StringUtils.isNotBlank(signedFileLoc))
				_tripDo.setDoSigned(signedFileLoc);
		}
		
		ckCtTripDoDao.saveOrUpdate(_tripDo);

		return tripDo;

	};

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public CkCtTripDoAttach getFileData(String id, Principal principal) throws ParameterException, ProcessingException {
        LOG.debug("getFileData");
        if (null == id) throw new ParameterException("param id is null or empty");
        try {
            TCkCtTripDoAttach tCkCtTripDoAttach = ckCtTripDoAttachDao.find(id);
            if (null == tCkCtTripDoAttach) throw new ProcessingException("Signed DO not found with id");

            String fileName = FilenameUtils.getName(tCkCtTripDoAttach.getDoaLoc());
            File file = new File(tCkCtTripDoAttach.getDoaLoc());
			if (!file.exists() || !file.isFile()) {
				throw new ParameterException("File not found or is not a valid file");
			}
            CkCtTripDoAttach dto = new CkCtTripDoAttach();
            BeanUtils.copyProperties(tCkCtTripDoAttach, dto, COBeansUtil.getNullPropertyNames(tCkCtTripDoAttach));
            dto.setDoaData(Files.readAllBytes(file.toPath()));
			dto.setDoaName(fileName);
            return dto;
        }catch (ParameterException ex) {
			LOG.error("Invalid parameter provided", ex);
			throw ex;
		} catch (ProcessingException ex) {
			LOG.error("Processing error", ex);
			throw ex;  // Rethrow the same exception
		} catch (Exception ex) {
			LOG.error("Unexpected error in getFileData", ex);
			throw new ProcessingException("An unexpected error occurred while processing the request");
		}
    }
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public int deleteDoAttach(String id, boolean isSigned, Principal principal) throws ParameterException, ProcessingException {
        LOG.debug("deleteDoAttach");
        if (null == id) throw new ParameterException("param id is null or empty");
        try {
            TCkCtTripDoAttach tCkCtTripDoAttach = ckCtTripDoAttachDao.find(id);
            if (null == tCkCtTripDoAttach) throw new ProcessingException("tCkCtTripDoAttach not found with id : " + id);
            
    		Map<String, Object> params = new HashMap<>();
    		params.put("doaId", id);
    		params.put("doaStatus", RecordStatus.ACTIVE.getCode());
    		    		
    		// Delete file from the server [START]
      		String serverFile = tCkCtTripDoAttach.getDoaLoc();
      		if (null != serverFile && !StringUtils.isEmpty(serverFile)) {
      	      File file = new File(serverFile);
      	      File directory = new File(file.getParent());
      	      this.deleteFolder(directory);
      	      LOG.debug(serverFile + " successfully deleted from the server!");
      		}
      		// Delete file from the server [END]
    		
    		int result = ckCtTripDoAttachDao.executeUpdate("DELETE FROM TCkCtTripDoAttach o WHERE o.doaId = :doaId AND o.doaStatus = :doaStatus", params);

    		// If upload is for unsigned, delete the current attachment
    		if (null != tCkCtTripDoAttach.getTCkCtTrip()) {
    			params.clear();
    			params.put("trId", tCkCtTripDoAttach.getTCkCtTrip().getTrId());
        		if (!isSigned) {
        			ckCtTripDoDao.executeUpdate("UPDATE TCkCtTripDo o SET o.doUnsigned = NULL WHERE o.TCkCtTrip.trId = :trId", params);
        		} else {
        			ckCtTripDoDao.executeUpdate("UPDATE TCkCtTripDo o SET o.doSigned = NULL WHERE o.TCkCtTrip.trId = :trId", params);
        		}
    		}
    		
    		return result;
        } catch (Exception ex) {
            LOG.error("deleteDoAttach", ex);
            throw new ProcessingException(ex);
        }
    }
	
	/**
	 * 
	 * @param directory
	 */
	@Transactional
	private void deleteFolder(File directory) {
		for (File subFile : directory.listFiles()) {
			if (subFile.isDirectory()) {
				deleteFolder(subFile);
			} else {
				subFile.delete();
			}
		}
		directory.delete();
	}
	
	@Deprecated
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public CkCtTripDo getSignedDoFileData(String id, Principal principal) throws ParameterException, ProcessingException {
        LOG.debug("getSignedDoFileData");
        if (null == id) throw new ParameterException("param id is null or empty");
        try {
            TCkCtTripDo tCkCtTripDo = ckCtTripDoDao.find(id);
            if (null == tCkCtTripDo) throw new ProcessingException("tCkCtTripDo not found with id : " + id);
            
            String fileName = FilenameUtils.getName(tCkCtTripDo.getDoSigned());
            File file = new File(tCkCtTripDo.getDoSigned());

            CkCtTripDo dto = new CkCtTripDo();
            BeanUtils.copyProperties(tCkCtTripDo, dto, COBeansUtil.getNullPropertyNames(tCkCtTripDo));
            dto.setDoSignedData(Files.readAllBytes(file.toPath()));
			dto.setDoSignedName(fileName);
            return dto;
        } catch (Exception ex) {
            LOG.error("getSignedDoFileData", ex);
            throw new ProcessingException(ex);
        }
    }
	
	@Deprecated
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public CkCtTripDo getUnsignedDoFileData(String id, Principal principal) throws ParameterException, ProcessingException {
        LOG.debug("getUnsignedDoFileData");
        if (null == id) throw new ParameterException("param id is null or empty");
        try {
            TCkCtTripDo tCkCtTripDo = ckCtTripDoDao.find(id);
            if (null == tCkCtTripDo) throw new ProcessingException("tCkCtTripDo not found with id : " + id);
            
            String fileName = FilenameUtils.getName(tCkCtTripDo.getDoUnsigned());
            File file = new File(tCkCtTripDo.getDoUnsigned());

            CkCtTripDo dto = new CkCtTripDo();
            BeanUtils.copyProperties(tCkCtTripDo, dto, COBeansUtil.getNullPropertyNames(tCkCtTripDo));
            dto.setDoUnsignedData(Files.readAllBytes(file.toPath()));
			dto.setDoUnsignedName(fileName);
            return dto;
        } catch (Exception ex) {
            LOG.error("getUnsignedDoFileData", ex);
            throw new ProcessingException(ex);
        }
    }
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public CkCtTripDo createTripDo(List<TCkCtTripDo> tripDos) throws ParameterException, ProcessingException {
        LOG.debug("createTripDo");

        try {
        	for (TCkCtTripDo tripDo : tripDos) {
        		// Validate if DoNo already exists
        		if (null != ckCtTripDoService.findByDoNo(tripDo.getDoNo())) 
        			throw new ProcessingException("Order number already exists!");
        		
        		// Add or update tripDo
        		CkCtTripDo ckCtTripDoDto = null;
        		TCkCtTripDo ckCtTripDoE = null;
        		if (null != tripDo && !StringUtils.isEmpty(tripDo.getDoNo())) {
        			ckCtTripDoDto = ckCtTripDoService.findByDoNo(tripDo.getDoNo());
        		}
        		if (null != ckCtTripDoDto) {
        			ckCtTripDoE = ckCtTripDoDao.find(ckCtTripDoDto.getDoId());
        		}
        		if (null == ckCtTripDoDto) {
        			tripDo.setDoId(CkUtil.generateIdSynch(TRIP_DO_PREFIX));
        			tripDo.setDoStatus(RecordStatus.ACTIVE.getCode());
        			tripDo.setDoDtCreate(Calendar.getInstance().getTime());
        			tripDo.setDoUidCreate(ckSession.getPrincipal().getUserId());
        			tripDo.setDoDtLupd(Calendar.getInstance().getTime());
        			tripDo.setDoUidLupd(ckSession.getPrincipal().getUserId());
        
        			TCkCtTripDo _tripDo = tripDo;
        			_tripDo.setDoNo(tripDo.getDoNo());
        			_tripDo.setTCkCtTrip(tripDo.getTCkCtTrip());
        			ckCtTripDoDao.saveOrUpdate(_tripDo);
        		} else if (null != ckCtTripDoDto) {
        			// unused 
//        			ckCtTripDoE.setDoSigned(_tripDoAttach.getDoaId());
        			ckCtTripDoDao.update(ckCtTripDoE);
        		} 
        	}

        } catch (Exception ex) {
            LOG.error("createTripDo", ex);
            throw new ProcessingException(ex);
        }
		return null;
    }
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public int deleteDo(String doaId, String doNo, Principal principal) throws ParameterException, ProcessingException {
		int result = 0;
		try {
    		Map<String, Object> params = new HashMap<>();
    		params.put("status", RecordStatus.ACTIVE.getCode());
    		
    		if (null != doaId) {
    			params.put("doaId", doaId);
    			ckCtTripDoAttachDao.executeUpdate("DELETE FROM TCkCtTripDoAttach o WHERE o.doaId = :doaId AND o.doaStatus = :status", params);
    			params.remove("doaId");
        		params.put("doNo", doNo);
    			result = ckCtTripDoDao.executeUpdate("DELETE FROM TCkCtTripDo o WHERE o.doNo = :doNo AND o.doStatus = :status", params);
    			// TODO: improvement for update 
    		} else {
        		params.put("doNo", doNo);
    			result = ckCtTripDoDao.executeUpdate("DELETE FROM TCkCtTripDo o WHERE o.doNo = :doNo AND o.doStatus = :status", params);
    			// TODO: improvement for update 
    		}
    			
    		return result;
		} catch (Exception ex) {
			LOG.error("deleteDo", ex);
			throw new ProcessingException(ex);
		}
	}
	
	private List<ValidationError> validateCreate(CkCtTripDoAttach dto, String type, Principal principal)
			throws ParameterException, ProcessingException {

		List<ValidationError> errorList = new ArrayList<>();
		
		if (principal == null)
			throw new ParameterException("param principal null");
		if (dto == null)
			throw new ParameterException("param dto null");
		
		try {
			
			Optional<byte[]> opDoaData = Optional.ofNullable(dto.getDoaData());

			// Validate File Format for DO and POD
			if (opDoaData.isPresent() && !ckCtTripDoService.isMimeTypeAllowed(opDoaData.get(), false, true)) {
				if (type.equalsIgnoreCase("unsigned")) {
					errorList.add(new ValidationError("", "invalid-unsigned-file-format", getMessage("valid.doattach.unsigned.format")));
					errorList.add(new ValidationError("", "invalid-do-file-format", getMessage("valid.doattach.unsigned.format")));
				} else if (type.equalsIgnoreCase("signed")) {
					errorList.add(new ValidationError("", "invalid-signed-file-format", getMessage("valid.doattach.signed.format")));
					errorList.add(new ValidationError("", "invalid-pod-file-format", getMessage("valid.doattach.signed.format")));
				}
			}
			
			return errorList;
		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}
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
    
	/**
	 * 
	 * @param message
	 * @return
	 */
	private String getMessage(String message) {
		return messageSource.getMessage(message, null, locale);
	}
}
