package com.guudint.clickargo.clictruck.planexec.trip.mobile.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ObjectUtils;

import com.guudint.clickargo.clictruck.common.dto.CkCtDrv;
import com.guudint.clickargo.clictruck.common.dto.CkCtVeh;
import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehType;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckDao;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkJobTruckServiceUtil;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDoAttachDao;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripCargoMm;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripDoAttach;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.service.CkCtTripWorkflowServiceImpl.TripStatus;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripDoAttach;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtTripCargoMmService;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtTripService;
import com.guudint.clickargo.common.dto.CkRecordDate;
import com.guudint.clickargo.common.service.ICkSession;
import com.guudint.clickargo.job.dto.CkJob;
import com.guudint.clickargo.job.model.TCkJob;
import com.guudint.clickargo.master.dto.CkMstJobState;
import com.guudint.clickargo.master.dto.CkMstJobType;
import com.guudint.clickargo.master.dto.CkMstShipmentType;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;


public class CkJobTruckTripMobileService {
	
	private static Logger LOG = Logger.getLogger(CkJobTruckTripMobileService.class);
	
	@Autowired
	private CkCtTripService ckCtTripService;
	
	@Autowired
	private CkJobTruckDao ckJobTruckDao;
	
	@Autowired
	private CkJobTruckServiceUtil ckJobTruckUtilService;
	
	@Autowired
	protected ICkSession ckSession;
	
	@Autowired
	private CkCtTripDoAttachDao attachDao;
	
	@Autowired
	private CkCtTripCargoMmService cargoMmService;

	@Transactional
	public List<CkJobTruck> listCargoDetail(String tripId) throws Exception {
		
		LOG.debug("listCargoDetail");
		
		Principal principal = ckSession.getPrincipal();
		if (null == principal)
			throw new ProcessingException("principal is null");
		CkCtTrip tckCtTrips = ckCtTripService.findById(tripId);
		if (tckCtTrips == null) {
			throw new ProcessingException("Trip data is null");
		}
		
		List<TCkJobTruck> ckJobTruck = ckJobTruckDao.findByIds(Arrays.asList(tckCtTrips.getTCkJobTruck().getJobId()));
		
		List<CkJobTruck> dtos = ckJobTruck.stream().map(x -> {
			try {
				return dtoFromEntity(x);
			} catch (ParameterException | ProcessingException e) {
				LOG.error("listCargoDetail", e);
			}
			return null;
		}).collect(Collectors.toList());

		return dtos;

	}

	@Transactional
	public CkCtTrip getRemarks(String tripId) throws Exception {
		Principal principal = ckSession.getPrincipal();
		if (null == principal)
			throw new ProcessingException("principal is null");

		if (tripId == null)
			throw new ParameterException("param is null");
		
		CkCtTrip tckCtTrips = ckCtTripService.findById(tripId);
		if (tckCtTrips == null)
			throw new EntityNotFoundException("trip does not exist. please try again");
		
		return tckCtTrips ;
	}
	
	
	protected CkJobTruck dtoFromEntity(TCkJobTruck entity) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");

		try {
			if (null == entity)
				throw new ParameterException("param entity null");

			CkJobTruck dto = new CkJobTruck(entity);
			// no deep copy from BeanUtils
			TCkJob ckJobE = entity.getTCkJob();
			if (null != ckJobE) {
				CkJob ckJob = new CkJob(ckJobE);
				Optional.ofNullable(entity.getTCkJob().getTCkMstJobState())
						.ifPresent(e -> ckJob.setTCkMstJobState(new CkMstJobState(e)));
				Optional.ofNullable(ckJobE.getTCkMstShipmentType())
						.ifPresent(e -> ckJob.setTCkMstShipmentType(new CkMstShipmentType(e)));
				Optional.ofNullable(ckJobE.getTCkMstJobType())
						.ifPresent(e -> ckJob.setTCkMstJobType(new CkMstJobType(e)));
				Optional.ofNullable(ckJobE.getTCkRecordDate())
						.ifPresent(e -> ckJob.setTCkRecordDate(new CkRecordDate(e)));
				Optional.ofNullable(ckJobE.getTCoreAccnByJobCoAccn())
						.ifPresent(e -> ckJob.setTCoreAccnByJobCoAccn(new CoreAccn(e)));
				Optional.ofNullable(ckJobE.getTCoreAccnByJobFfAccn())
						.ifPresent(e -> ckJob.setTCoreAccnByJobFfAccn(new CoreAccn(e)));
				Optional.ofNullable(ckJobE.getTCoreAccnByJobOwnerAccn())
						.ifPresent(e -> ckJob.setTCoreAccnByJobOwnerAccn(new CoreAccn(e)));
				Optional.ofNullable(ckJobE.getTCoreAccnByJobToAccn())
						.ifPresent(e -> ckJob.setTCoreAccnByJobToAccn(new CoreAccn(e)));

				dto.setTCkJob(ckJob);
				dto.setHasRemarks(ckJobTruckUtilService.isJobRemarked(ckJob.getJobId()));

			}

			Optional<TCkCtDrv> opCkCtDrv = Optional.ofNullable(entity.getTCkCtDrv());
			dto.setTCkCtDrv(opCkCtDrv.isPresent() ? new CkCtDrv(opCkCtDrv.get()) : null);

			Optional<TCkCtVeh> opCkCtVeh = Optional.ofNullable(entity.getTCkCtVeh());
			dto.setTCkCtVeh(opCkCtVeh.isPresent() ? new CkCtVeh(opCkCtVeh.get()) : null);
			if (opCkCtVeh.isPresent()) {
				dto.getTCkCtVeh().setTCkCtMstVehType(new CkCtMstVehType(opCkCtVeh.get().getTCkCtMstVehType()));
			}

			// Load job trips if have
			List<CkCtTrip> tckCtTrips = ckCtTripService.findTripsByTruckJobAndStatus(dto.getJobId(),
					Arrays.asList(TripStatus.M_ACTIVE.getStatusCode(), TripStatus.M_PICKED_UP.getStatusCode(), TripStatus.M_DELIVERED.getStatusCode(), TripStatus.DLV.getStatusCode()));
			dto.setTckCtTripList(tckCtTrips);
			
			List<CkCtTripCargoMm> tckCtTripCargoMms = cargoMmService.findTripCargoFmmsByTripId(tckCtTrips.get(0).getTrId());
					dto.getTckCtTripList().get(0).setTripCargoMmList(
							!ObjectUtils.isEmpty(tckCtTripCargoMms) ? tckCtTripCargoMms : new ArrayList<CkCtTripCargoMm>());
			
			dto.setDomestic(!ckJobTruckUtilService.isFirstMile(dto.getTCkJob().getTCkMstShipmentType().getShtId()));

			return dto;
		} catch (ParameterException ex) {
			LOG.error("dtoFromEntity", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("dtoFromEntity", ex);
			throw new ProcessingException(ex);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtTripDoAttach> downloadDsv(String tripId) throws Exception {
	    Principal principal = ckSession.getPrincipal();

	    if (principal == null) {
	        throw new ProcessingException("principal is null");
	    }

	    List<TCkCtTripDoAttach> tckCtTripDoAttachList = attachDao.findByTripId(tripId);

	    if (tckCtTripDoAttachList.isEmpty()) {
	    	throw new ParameterException("dto is null");
	    }
	    
	    List<CkCtTripDoAttach> tripDoAttachList = new ArrayList<CkCtTripDoAttach>();
	    
	    tckCtTripDoAttachList.forEach((item) -> {
	    	CkCtTripDoAttach doAttachItem = new CkCtTripDoAttach(item);
	    	
	    try {
	    		File filepath = new File(doAttachItem.getDoaLoc());
	    		doAttachItem.setDoaData(FileCopyUtils.copyToByteArray(filepath));
	    		doAttachItem.setDoaLoc(null);
	    		
	    		tripDoAttachList.add(doAttachItem);
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
	    });
	    
	    
	    return tripDoAttachList;

	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtTripDoAttach> downloadDOAttachByJob(String jobId) throws Exception {
	    Principal principal = ckSession.getPrincipal();

	    if (principal == null) {
	        throw new ProcessingException("principal is null");
	    }

	    List<TCkCtTripDoAttach> tckCtTripDoAttachList = attachDao.findByJobId(jobId);

	    if (tckCtTripDoAttachList.isEmpty()) {
	    	throw new ParameterException("dto is null");
	    }
	    
	    List<CkCtTripDoAttach> tripDoAttachList = new ArrayList<CkCtTripDoAttach>();
	    
	    tckCtTripDoAttachList.forEach((item) -> {
	    	CkCtTripDoAttach doAttachItem = new CkCtTripDoAttach(item);
	    	
	    try {
	    		File filepath = new File(doAttachItem.getDoaLoc());
	    		doAttachItem.setDoaData(FileCopyUtils.copyToByteArray(filepath));
	    		doAttachItem.setDoaLoc(null);
	    		
	    		tripDoAttachList.add(doAttachItem);
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
	    });
	    
	    
	    return tripDoAttachList;

	}
	
}
