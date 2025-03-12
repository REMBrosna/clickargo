package com.guudint.clickargo.clictruck.planexec.job.validator;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.guudint.clickargo.clictruck.common.model.TCkCtLocation;
import com.vcc.camelone.common.dao.GenericDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.clictruck.finacing.dao.CkCtToInvoiceDao;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtToInvoice;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtToInvoice;
import com.guudint.clickargo.clictruck.finacing.service.impl.CkCtToInvoiceServiceImpl;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstCargoType;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripAttachDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDoDao;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripCargoFm;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripLocation;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.service.TripMobileService.TripAttachTypeEnum;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripAttach;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripDo;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.enums.JobActions;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.job.service.IJobEvent.JobEvent;
import com.guudint.clickargo.job.service.IJobValidate;
import com.guudint.clickargo.master.dto.CkMstCntType;
import com.guudint.clickargo.master.enums.ShipmentTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class TruckJobValidator implements IJobValidate<CkJobTruck> {

	// Assuming dash is allowed, space not allowed
	private static final String PHONE_PATTERN = ICkConstant.PHONE_PATTERN;
	// Assuming 7 is the minimum length
	private static final int PHONE_MIN_LENGTH = 8;
	private static Logger log = Logger.getLogger(TruckJobValidator.class);
	
	// Tab names
	private static final String DRIVER_TRUCK_TAB = "driver";
	private static final String JOB_DETAILS_TAB = "jobDetails";
	private static final String DOCUMENTS_TAB = "documents";
	private static final String FIRST_MILE_TAB = "fmTrip";
	private static final String MID_MILE_TAB = "mmTrip";
	private static final String FIRST_MILE_INV_TAB = "invoice";
	private static final String MID_MILE_INV_TAB = "midMileInvoice";
	private static final String DELIVERY_ORDERS_TAB = "deliveryOrders";
	
	@Autowired
	MessageSource messageSource;

	@Autowired
	private CkCtTripAttachDao attachDao;
	
	@Autowired
	private CkCtTripDoDao ckCtTripDoDao;
	
	@Autowired
	private CkCtToInvoiceDao toInvoiceDao;

	@Autowired
	CkCtToInvoiceServiceImpl invoiceService;

	@Autowired
	private GenericDao<TCkCtLocation, String> ckCtLocationDao;
	
	// Autowired
	////////////
	Locale locale = LocaleContextHolder.getLocale();

	private String getMessage(String message) {
		return messageSource.getMessage(message, null, locale);
	}

	private ValidationError newValidationError(String field, String message) {
		return new ValidationError("", field, message);
	}

	// Override Methods
	///////////////////
	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.IJobValidate#validateCreate(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	public List<ValidationError> validateCreate(CkJobTruck dto, Principal principal)
			throws ParameterException, ProcessingException {
		if (null == dto)
			throw new ParameterException("param dto null");
		if (null == principal)
			throw new ParameterException("param principal null");

		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.IJobValidate#validateUpdate(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	public List<ValidationError> validateUpdate(CkJobTruck dto, Principal principal)
			throws ParameterException, ProcessingException {
		if (null == dto)
			throw new ParameterException("param dto null");
		if (null == principal)
			throw new ParameterException("param principal null");
		
		List<ValidationError> invalidList = new ArrayList<>();

		// CT-51 - [CO Operations-Import Job] System goes in processing after Click of
		// Delete button
		if (null != dto.getAction() && dto.getAction().getDesc().equalsIgnoreCase(JobActions.DELETE.name())) {
			return validateDelete(dto, principal);
		}
		
		if (null != dto.getAction() && dto.getAction().getDesc().equalsIgnoreCase(JobActions.START.name())) {
			return this.validateStart(dto, principal);
		}
		
		if (null != dto.getAction() && dto.getAction().getDesc().equalsIgnoreCase(JobActions.STOP.name())) {
			return this.validateStop(dto, principal);
		}
		
		invalidList.addAll(validateUpdateFields(dto, principal, JobEvent.UPDATE.getDesc()));

		return invalidList;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.IJobValidate#validateSubmit(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	public List<ValidationError> validateSubmit(CkJobTruck dto, Principal principal)
			throws ParameterException, ProcessingException {
		if (null == dto)
			throw new ParameterException("param dto null");
		if (null == principal)
			throw new ParameterException("param principal null");

		return validateFields(dto, principal, JobEvent.SUBMIT.getDesc());
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.IJobValidate#validateReject(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	public List<ValidationError> validateReject(CkJobTruck dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.IJobValidate#validateCancel(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	public List<ValidationError> validateCancel(CkJobTruck dto, Principal principal)
			throws ParameterException, ProcessingException {
		if (null == dto)
			throw new ParameterException("param dto null");
		if (null == principal)
			throw new ParameterException("param principal null");

		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.IJobValidate#validateDelete(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	public List<ValidationError> validateDelete(CkJobTruck dto, Principal principal)
			throws ParameterException, ProcessingException {
		if (null == dto)
			throw new ParameterException("param dto null");
		if (null == principal)
			throw new ParameterException("param principal null");
		return null;
	}

	/**
	 * 
	 * @param dto
	 * @param principal
	 * @return
	 * @throws ParameterException
	 * @throws ProcessingException
	 */
	public List<ValidationError> validateStart(CkJobTruck dto, Principal principal)
			throws ParameterException, ProcessingException {
		if (null == dto)
			throw new ParameterException("param dto null");
		if (null == principal)
			throw new ParameterException("param principal null");
		
		List<ValidationError> errorList = new ArrayList<ValidationError>();
		Set<String> invalidTabs = new HashSet<>();
		
		List<CkCtTrip> tripList = dto.getTckCtTripList();
		if (null != tripList && !ObjectUtils.isEmpty(tripList)) {
			for (CkCtTrip trip : tripList) {
				try {
					List<TCkCtTripDo> tripDoList = ckCtTripDoDao.findByTripId(trip.getTrId());
					if (ObjectUtils.isEmpty(tripDoList)) {
						invalidTabs.add(DELIVERY_ORDERS_TAB);
						errorList.add(newValidationError("or-not-found", getMessage("valid.jobTruck.delivery.dono.notFound")));
					}
				} catch (Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}				
			}
		}
		
		if (!invalidTabs.isEmpty() && invalidTabs.contains(DELIVERY_ORDERS_TAB)) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				errorList.add(newValidationError("invalidTabs.deliveryOrders", mapper.writeValueAsString(invalidTabs)));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return errorList;
	}
	
	/**
	 * 
	 * @param dto
	 * @param principal
	 * @return
	 * @throws ParameterException
	 * @throws ProcessingException
	 */
	public List<ValidationError> validateStop(CkJobTruck dto, Principal principal)
			throws ParameterException, ProcessingException {
		if (null == dto)
			throw new ParameterException("param dto null");
		if (null == principal)
			throw new ParameterException("param principal null");
		
		List<ValidationError> errorList = new ArrayList<ValidationError>();
		Set<String> invalidTabs = new HashSet<>();

		if(dto.isIgnorePickupDropOfAtt()){ // ignorePickupDropOfAtt: Makes PICKUP & DROP-OFF attachments non-mandatory when marking the job as Delivered.
			dto.setMissingDropOff(false);
			dto.setMissingPickup(false);
		}else {
			dto = this.checkAttachJob(dto);
		}

		List<CkCtTrip> tripList = dto.getTckCtTripList();
		
		// TRIP ATTACHMENTS - Validate trip attachments when job is mobile enabled
		if (null != tripList && !ObjectUtils.isEmpty(tripList) && dto.getJobMobileEnabled() == 'Y') {
			// if both dropOff and pickUp images are not uploaded, display errors for both
			if (dto.isMissingDropOff() && dto.isMissingPickup()) {
				invalidTabs.add(DOCUMENTS_TAB);
				if (dto.getTckCtTripList().size() == 1)
					errorList.add(newValidationError("do-pu-doc-missing", getMessage("valid.jobTruck.attachments.do.pu.null")));
				else
					errorList.add(newValidationError("do-pu-doc-missing", getMessage("valid.jobTruck.attachments.do.pu.multiple.null")));
			// if only missing dropOff, show message for dropOff
			} else if (dto.isMissingDropOff()) {
				invalidTabs.add(DOCUMENTS_TAB);
				if (dto.getTckCtTripList().size() == 1)
					errorList.add(newValidationError("dropoff-doc-missing", getMessage("valid.jobTruck.attachments.dropoff.null")));
				else 
					errorList.add(newValidationError("dropoff-doc-missing", getMessage("valid.jobTruck.attachments.dropoff.multiple.null")));
			// if only missing pickUp, show message for pickUp
			} else if (dto.isMissingPickup()) {
				invalidTabs.add(DOCUMENTS_TAB);
				if (dto.getTckCtTripList().size() == 1)
					errorList.add(newValidationError("pickup-doc-missing", getMessage("valid.jobTruck.attachments.pickup.null")));
				else 
					errorList.add(newValidationError("pickup-doc-missing", getMessage("valid.jobTruck.attachments.pickup.multiple.null")));
			}
		}
		
		// SIGNED DO - Validate signed DO if jobMobileEnabled is Y and jobSource is XML
		if (null != tripList && !ObjectUtils.isEmpty(tripList) 
				&& dto.getJobMobileEnabled() == 'Y'
				&& null != dto.getJobSource() && dto.getJobSource().contains("XML")) {
			for (CkCtTrip trip : tripList) {
				try {
					List<TCkCtTripDo> tripDoList = ckCtTripDoDao.findByTripId(trip.getTrId());
					for (TCkCtTripDo tripDo : tripDoList) {
						if (StringUtils.isBlank(tripDo.getDoSigned())) {
							invalidTabs.add(DELIVERY_ORDERS_TAB);
							errorList.add(newValidationError("pod-not-found", getMessage("valid.jobTruck.delivery.pod.notFound")));
						}	
					}
				} catch (Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}				
			}
		}
		
		if (!invalidTabs.isEmpty() && invalidTabs.contains(DOCUMENTS_TAB)) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				errorList.add(newValidationError("invalidTabs.documents", mapper.writeValueAsString(invalidTabs)));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (!invalidTabs.isEmpty() && invalidTabs.contains(DELIVERY_ORDERS_TAB)) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				errorList.add(newValidationError("invalidTabs.deliveryOrders", mapper.writeValueAsString(invalidTabs)));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return errorList;
	}
	
	/**
	 * 
	 * @param dto
	 * @return
	 */
	private CkJobTruck checkAttachJob(CkJobTruck dto) {
		List<CkCtTrip> tripList = dto.getTckCtTripList();
		List<String> tripIds = new ArrayList<String>();
		
		if (!ObjectUtils.isEmpty(tripList)) {
			for (CkCtTrip trip : tripList) {
				tripIds.add(trip.getTrId());
			}
		}
		List<TCkCtTripAttach> dropOff = null;
		List<TCkCtTripAttach> pickUp = null;
		try {
			dropOff = attachDao.findByAtypIdAndTrIds(TripAttachTypeEnum.PHOTO_DROPOFF.name(), tripIds);
			pickUp= attachDao.findByAtypIdAndTrIds(TripAttachTypeEnum.PHOTO_PICKUP.name(), tripIds);
		} catch (Exception ex) {
			log.error("checkAttachJob ", ex);
		}
		 
		if (dropOff == null || dropOff.size() != tripList.size())
			dto.setMissingDropOff(true);
		if (pickUp == null || pickUp.size() != tripList.size()) 
			dto.setMissingPickup(true);
		
		return dto;
	}
	
	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.IJobValidate#validateConfirm(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	public List<ValidationError> validateConfirm(CkJobTruck dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.IJobValidate#validatePay(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	public List<ValidationError> validatePay(CkJobTruck dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.IJobValidate#validatePaid(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	public List<ValidationError> validatePaid(CkJobTruck dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.IJobValidate#validateComplete(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	public List<ValidationError> validateComplete(CkJobTruck dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param dto
	 * @param principal
	 * @param action
	 * @return
	 * @throws ParameterException
	 * @throws ProcessingException
	 */
	private List<ValidationError> validateFields(CkJobTruck dto, Principal principal, String action)
			throws ParameterException, ProcessingException {
		List<ValidationError> errorList = new ArrayList<ValidationError>();

		// String added corresponds to the tab name in FE
		Set<String> invalidTabs = new HashSet<>();

		Date now = Calendar.getInstance().getTime();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Optional<Date> opJobDtPlan = Optional.ofNullable(dto.getJobDtPlan());

		try {
			if (null != dto.getAction() && dto.getAction().getDesc().equalsIgnoreCase(JobActions.CANCEL.name())) {
				return validateCancel(dto, principal);
			}

			if (null != dto.getAction() && dto.getAction().getDesc().equalsIgnoreCase(JobActions.SUBMIT.name())) {
				if (null == dto.getJobDtPlan()) {
					invalidTabs.add(JOB_DETAILS_TAB);
					errorList.add(newValidationError("jobDtPlan", getMessage("valid.jobTruck.jobDtPlan.notNull")));
				} else if (df.format(opJobDtPlan.get()).compareTo(df.format(now)) < 0) {
					invalidTabs.add(JOB_DETAILS_TAB);
					errorList.add(newValidationError("jobDtPlan", getMessage("valid.jobTruck.jobDtPlan.before")));
				}
				if (dto.isDomestic()) {
					// domestic, mid-mile;
					this.validateCkCtTripsDomestic(dto, errorList);
				} else {
					// first-mile
					this.validateCkCtTrips(dto, errorList);
				}

				if (null != dto.getTckCtTripList()) {
					for (CkCtTrip ckCtTrip : dto.getTckCtTripList()) {

						Optional<CkCtTripLocation> tripLocationByTrFrom = Optional.ofNullable(ckCtTrip.getTCkCtTripLocationByTrFrom());
						Optional<CkCtTripLocation> tripLocationByTrTo = Optional.ofNullable(ckCtTrip.getTCkCtTripLocationByTrTo());
						Optional<CkCtTripLocation> tripLocationByTrDepot = Optional.ofNullable(ckCtTrip.getTCkCtTripLocationByTrDepot());
						
						if (!dto.isDomestic()) {
							// Validate Location From - if present
							if (tripLocationByTrFrom.isPresent() && tripLocationByTrFrom.get().getTCkCtLocation() == null) {
								invalidTabs.add(FIRST_MILE_TAB);
								errorList.add(newValidationError("tckCtTripLocationByTrFrom.tckCtLocation.locId",
										getMessage("valid.jobTruck.ckCtTripLocationFrom.required")));
							}
							// Validate Location From - if equal or greater than current date
							if (tripLocationByTrFrom.isPresent() && tripLocationByTrFrom.get().getTlocDtLoc() != null
									&& df.format(tripLocationByTrFrom.get().getTlocDtLoc()).compareTo(df.format(now)) < 0) {
								invalidTabs.add(FIRST_MILE_TAB);
								errorList.add(newValidationError("tckCtTripLocationByTrFrom.tlocDtLoc",
										getMessage("valid.jobTruck.tckCtTripLocationByTrFrom.beforeToday")));
							}

							// Validate Location To - if present
							if (tripLocationByTrTo.isPresent() && tripLocationByTrTo.get().getTCkCtLocation() == null) {
								invalidTabs.add(FIRST_MILE_TAB);
								errorList.add(newValidationError("tckCtTripLocationByTrTo.tckCtLocation.locId",
										getMessage("valid.jobTruck.ckCtTripLocationTo.required")));
							}
							// Validate Location To - if equal or greater than current date
							if (tripLocationByTrTo.isPresent() && tripLocationByTrTo.get().getTlocDtLoc() != null
									&& df.format(tripLocationByTrTo.get().getTlocDtLoc()).compareTo(df.format(now)) < 0) {
								invalidTabs.add(FIRST_MILE_TAB);
								errorList.add(newValidationError("tckCtTripLocationByTrTo.tlocDtLoc",
										getMessage("valid.jobTruck.tckCtTripLocationByTrTo.beforeToday")));
							}
							// Validate Location To cannot be earlier than Location From
							if (tripLocationByTrFrom.isPresent() && tripLocationByTrFrom.get().getTlocDtLoc() != null
									&& tripLocationByTrTo.isPresent() && tripLocationByTrTo.get().getTlocDtLoc() != null) {
								if (df.format(tripLocationByTrTo.get().getTlocDtLoc())
										.compareTo(df.format(tripLocationByTrFrom.get().getTlocDtLoc())) < 0) {
									invalidTabs.add(FIRST_MILE_TAB);
									errorList.add(newValidationError("tckCtTripLocationByTrTo.tlocDtLoc",
											getMessage("valid.jobTruck.tckCtTripLocationByTrTo.beforeFrom")));
								}
							}
							
							// Added validation for mobile number, only applicable to TO
							if (dto.getJobMobileEnabled() == 'Y') {
								if (tripLocationByTrTo.isPresent() && StringUtils.isBlank(tripLocationByTrTo.get().getTlocMobileNo())) {
									invalidTabs.add(FIRST_MILE_TAB);
									errorList.add(newValidationError("tckCtTripLocationByTrTo.tlocMobileNo",
											getMessage("valid.jobTruck.tckCtTripLocationByTrTo.tlocMobileNo")));
								}
							}
							
							// Validate mobile number format if present [START]
							if (tripLocationByTrFrom.isPresent() && StringUtils.isNotEmpty(tripLocationByTrFrom.get().getTlocMobileNo())) {
								Pattern pattern = Pattern.compile(PHONE_PATTERN);
								Matcher matcher = pattern.matcher(tripLocationByTrFrom.get().getTlocMobileNo());
								if (tripLocationByTrFrom.get().getTlocMobileNo().length() < PHONE_MIN_LENGTH) {
									invalidTabs.add(FIRST_MILE_TAB);
									errorList.add(newValidationError("tckCtTripLocationByTrFrom.tlocMobileNo",
											getMessage("valid.jobTruck.tckCtTripLocation.tlocMobileNo.length")));
								} else if (!matcher.matches()) {
									invalidTabs.add(FIRST_MILE_TAB);
									errorList.add(newValidationError("tckCtTripLocationByTrFrom.tlocMobileNo",
											getMessage("valid.jobTruck.tckCtTripLocation.tlocMobileNo.format")));
								}
							}
							if (tripLocationByTrTo.isPresent() && StringUtils.isNotEmpty(tripLocationByTrTo.get().getTlocMobileNo())) {
								Pattern pattern = Pattern.compile(PHONE_PATTERN);
								Matcher matcher = pattern.matcher(tripLocationByTrTo.get().getTlocMobileNo());
								if (tripLocationByTrTo.get().getTlocMobileNo().length() < PHONE_MIN_LENGTH) {
									invalidTabs.add(FIRST_MILE_TAB);
									errorList.add(newValidationError("tckCtTripLocationByTrTo.tlocMobileNo",
											getMessage("valid.jobTruck.tckCtTripLocation.tlocMobileNo.length")));
								} else if (!matcher.matches()) {
									invalidTabs.add(FIRST_MILE_TAB);
									errorList.add(newValidationError("tckCtTripLocationByTrTo.tlocMobileNo",
											getMessage("valid.jobTruck.tckCtTripLocation.tlocMobileNo.format")));
								}
							}
							if (tripLocationByTrDepot.isPresent() && StringUtils.isNotEmpty(tripLocationByTrDepot.get().getTlocMobileNo())) {
								Pattern pattern = Pattern.compile(PHONE_PATTERN);
								Matcher matcher = pattern.matcher(tripLocationByTrDepot.get().getTlocMobileNo());
								if (tripLocationByTrDepot.get().getTlocMobileNo().length() < PHONE_MIN_LENGTH) {
									invalidTabs.add(FIRST_MILE_TAB);
									errorList.add(newValidationError("tckCtTripLocationByTrDepot.tlocMobileNo",
											getMessage("valid.jobTruck.tckCtTripLocation.tlocMobileNo.length")));
								} else if (!matcher.matches()) {
									invalidTabs.add(FIRST_MILE_TAB);
									errorList.add(newValidationError("tckCtTripLocationByTrDepot.tlocMobileNo",
											getMessage("valid.jobTruck.tckCtTripLocation.tlocMobileNo.format")));
								}
							}
							// Validate mobile number format if present [END]
							
							// Validate vehicle type
							if (null == dto.getTCkCtMstVehType() || StringUtils.isBlank(dto.getTCkCtMstVehType().getVhtyId())) {
								invalidTabs.add(FIRST_MILE_TAB);
								errorList.add(
										newValidationError("tckCtMstVehType.vhtyId", getMessage("valid.jobTruck.ckCtMstVehType.notNull")));
							}
							// Validate Location Depot - if present
							if (tripLocationByTrDepot.isPresent() && tripLocationByTrDepot.get().getTCkCtLocation() == null) {
								invalidTabs.add(FIRST_MILE_TAB);
								errorList.add(newValidationError("tckCtTripLocationByTrDepot.tckCtLocation.locId",
										getMessage("valid.jobTruck.ckCtTripLocationDepot.required")));
							}
							// Validate Location Depot - if equal or greater than current date
							if (tripLocationByTrDepot.isPresent() && tripLocationByTrDepot.get().getTlocDtLoc() != null
									&& df.format(tripLocationByTrDepot.get().getTlocDtLoc())
											.compareTo(df.format(now)) < 0) {
								invalidTabs.add(FIRST_MILE_TAB);
								errorList.add(newValidationError("tckCtTripLocationByTrDepot.tlocDtLoc",
										getMessage("valid.jobTruck.tckCtTripLocationByTrDepot.beforeToday")));
							}

							// Validate Depot Schedule Details cannot be earlier than To Schedule Details - IMPORT
							if (dto.getTCkJob().getTCkMstShipmentType().getShtId()
									.equalsIgnoreCase(ShipmentTypes.IMPORT.getId())) {
								if (tripLocationByTrTo.isPresent() && tripLocationByTrTo.get().getTlocDtLoc() != null
										&& tripLocationByTrDepot.isPresent() && tripLocationByTrDepot.get().getTlocDtLoc() != null) {
									if (tripLocationByTrDepot.get().getTlocDtLoc().before(tripLocationByTrTo.get().getTlocDtLoc())) {
										invalidTabs.add(FIRST_MILE_TAB);
										errorList.add(newValidationError("tckCtTripLocationByTrDepot.tlocDtLoc",
												getMessage("valid.jobTruck.tckCtTripLocationByTrDepot.beforeTo")));
									}
								}
							}

							// Validate From Schedule Details cannot be earlier than Depot Schedule Details - EXPORT
							if (dto.getTCkJob().getTCkMstShipmentType().getShtId()
									.equalsIgnoreCase(ShipmentTypes.EXPORT.getId())) {
								if (tripLocationByTrDepot.isPresent() && tripLocationByTrDepot.get().getTlocDtLoc() != null
										&& tripLocationByTrFrom.isPresent() && tripLocationByTrFrom.get().getTlocDtLoc() != null) {
									if (tripLocationByTrFrom.get().getTlocDtLoc().before(tripLocationByTrDepot.get().getTlocDtLoc())) {
										invalidTabs.add(FIRST_MILE_TAB);
										errorList.add(newValidationError("tckCtTripLocationByTrFrom.tlocDtLoc",
												getMessage("valid.jobTruck.tckCtTripLocationByTrFrom.beforeDepot")));
									}
								}
							}

						}
						
						// Validate Schedule Details if mobile enabled
						if (tripLocationByTrFrom.isPresent() && tripLocationByTrTo.isPresent()
								&& null != tripLocationByTrFrom.get().getTCkCtLocation() && null != tripLocationByTrTo.get().getTCkCtLocation()) {
							Date scheduleFrom = tripLocationByTrFrom.isPresent() ? tripLocationByTrFrom.get().getTlocDtLoc() : null;
							Date scheduleTo = tripLocationByTrTo.isPresent() ? tripLocationByTrTo.get().getTlocDtLoc() : null;
							if (dto.getJobMobileEnabled() == 'Y') {
								if (null == scheduleFrom) {
									invalidTabs.add(FIRST_MILE_TAB);
									errorList.add(newValidationError("tckCtTripLocationByTrFrom.tlocDtLoc",
											getMessage("valid.jobTruck.tlocDtLoc.required")));
									
								}
								if (null == scheduleTo) {
									invalidTabs.add(FIRST_MILE_TAB);
									errorList.add(newValidationError("tckCtTripLocationByTrTo.tlocDtLoc",
											getMessage("valid.jobTruck.tlocDtLoc.required")));
								}
								if (null != scheduleFrom && null != scheduleTo) {
									if (scheduleTo.before(scheduleFrom)) {
										invalidTabs.add(FIRST_MILE_TAB);
										errorList.add(newValidationError("tckCtTripLocationByTrTo.tlocDtLoc",
												getMessage("valid.jobTruck.tckCtTripLocationByTrTo.beforeFrom")));
									}
								}
							}
						}
					}
				}
			}

			if (null == dto.getTCoreAccnByJobPartyTo()
					|| StringUtils.isBlank(dto.getTCoreAccnByJobPartyTo().getAccnId())) {
				invalidTabs.add(JOB_DETAILS_TAB);
				errorList.add(newValidationError("tcoreAccnByJobPartyTo.accnId",
						getMessage("valid.jobTruck.coreAccnByJobPartyTo.notNull")));
			}

			if (null == dto.getJobShipmentRef() || StringUtils.isBlank(dto.getJobShipmentRef())) {
				invalidTabs.add(JOB_DETAILS_TAB);
				errorList.add(newValidationError("jobShipmentRef", getMessage("valid.jobTruck.shipmentRef.notNull")));
			} else if (null != dto.getJobShipmentRef() && !StringUtils.isBlank(dto.getJobShipmentRef())
					&& dto.getJobShipmentRef().length() > 255) {
				invalidTabs.add(JOB_DETAILS_TAB);
				errorList.add(newValidationError("jobShipmentRef", getMessage("valid.jobTruck.shipmentRef.maxLength")));
			}

			if (null != dto.getJobCustomerRef() && !StringUtils.isBlank(dto.getJobCustomerRef())
					&& dto.getJobCustomerRef().length() > 255) {
				invalidTabs.add(JOB_DETAILS_TAB);
				errorList.add(newValidationError("jobCustomerRef", getMessage("valid.jobTruck.customerRef.maxLength")));
			}

			if (!invalidTabs.isEmpty() && invalidTabs.contains(FIRST_MILE_TAB)) {
				ObjectMapper mapper = new ObjectMapper();
				errorList.add(newValidationError("invalidTabs.fmTrip", mapper.writeValueAsString(invalidTabs)));
			}
			
			if (!invalidTabs.isEmpty() && invalidTabs.contains(JOB_DETAILS_TAB)) {
				ObjectMapper mapper = new ObjectMapper();
				errorList.add(newValidationError("invalidTabs.jobDetails", mapper.writeValueAsString(invalidTabs)));
			}

			return errorList;
		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}
	}

	/**
	 * 
	 * @param dto
	 * @param principal
	 * @param action
	 * @return
	 * @throws ParameterException
	 * @throws ProcessingException
	 */
	private List<ValidationError> validateUpdateFields(CkJobTruck dto, Principal principal, String action)
			throws ParameterException, ProcessingException {
		List<ValidationError> errorList = new ArrayList<ValidationError>();

		// String added corresponds to the tab name in FE
		Set<String> invalidTabs = new HashSet<>();

		Date now = Calendar.getInstance().getTime();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Optional<Date> opJobDtPlan = Optional.ofNullable(dto.getJobDtPlan());

		try {
			
			if (null != dto.getAction() && dto.getAction().getDesc().equalsIgnoreCase(JobActions.CANCEL.name())) {
				return validateCancel(dto, principal);
			}

			// CT-51 - [CO Operations-Import Job] System goes in processing after Click of
			// Delete button
			if (null != dto.getAction() && dto.getAction().getDesc().equalsIgnoreCase(JobActions.DELETE.name())) {
				return validateDelete(dto, principal);
			}

			if (null != dto.getAction() && dto.getAction().getDesc().equalsIgnoreCase(JobActions.SUBMIT.name())) {
				if (!opJobDtPlan.isPresent()) {
					invalidTabs.add(JOB_DETAILS_TAB);
					errorList.add(newValidationError("jobDtPlan", getMessage("valid.jobTruck.jobDtPlan.notNull")));
				} else if (df.format(opJobDtPlan.get()).compareTo(df.format(now)) < 0) {
					invalidTabs.add(JOB_DETAILS_TAB);
					errorList.add(newValidationError("jobDtPlan", getMessage("valid.jobTruck.jobDtPlan.before")));
				}
				if (dto.isDomestic()) {
					// domestic, mid-mile;
					this.validateCkCtTripsDomestic(dto, errorList);
				} else {
					// first-mile
					this.validateCkCtTrips(dto, errorList);
				}
			}

			if (null == dto.getTCoreAccnByJobPartyTo()
					|| StringUtils.isBlank(dto.getTCoreAccnByJobPartyTo().getAccnId())) {
				invalidTabs.add(JOB_DETAILS_TAB);
				errorList.add(newValidationError("tcoreAccnByJobPartyTo.accnId",
						getMessage("valid.jobTruck.coreAccnByJobPartyTo.notNull")));
			}

			if (null == dto.getJobShipmentRef() || StringUtils.isBlank(dto.getJobShipmentRef())) {
				invalidTabs.add(JOB_DETAILS_TAB);
				errorList.add(newValidationError("jobShipmentRef", getMessage("valid.jobTruck.shipmentRef.notNull")));
			} else if (null != dto.getJobShipmentRef() && !StringUtils.isBlank(dto.getJobShipmentRef())
					&& dto.getJobShipmentRef().length() > 255) {
				invalidTabs.add(JOB_DETAILS_TAB);
				errorList.add(newValidationError("jobShipmentRef", getMessage("valid.jobTruck.shipmentRef.maxLength")));
			}
			if (null != dto.getTCkJob().getJobLoading() && StringUtils.isBlank(dto.getTCkJob().getJobLoading())){
				invalidTabs.add(JOB_DETAILS_TAB);
				errorList.add(newValidationError("jobLoading", getMessage("valid.jobTruck.jobLoading")));
			}
			if (null != dto.getTCkJob().getJobSubType() && StringUtils.isBlank(dto.getTCkJob().getJobSubType())){
				invalidTabs.add(JOB_DETAILS_TAB);
				errorList.add(newValidationError("jobSubType", getMessage("valid.jobTruck.jobSubType")));
			}

			if (null != dto.getJobCustomerRef() && !StringUtils.isBlank(dto.getJobCustomerRef())
					&& dto.getJobCustomerRef().length() > 255) {
				invalidTabs.add(JOB_DETAILS_TAB);
				errorList.add(newValidationError("jobCustomerRef", getMessage("valid.jobTruck.customerRef.maxLength")));
			}

			if (!invalidTabs.isEmpty() && invalidTabs.contains(JOB_DETAILS_TAB)) {
				ObjectMapper mapper = new ObjectMapper();
				errorList.add(newValidationError("invalidTabs.jobDetails", mapper.writeValueAsString(invalidTabs)));
			}

			if (null != dto.getAction() && dto.getAction().getDesc().equalsIgnoreCase(JobActions.ASSIGN.name())) {

				// Driver Validation
				// Driver - JobDrvOth
				if (null != dto.getJobDrvOth()) {
					if (dto.getJobDrvOth().containsKey("drvName")) {
						String name = dto.getJobDrvOth().get("drvName") == null ? null
								: dto.getJobDrvOth().get("drvName").toString();
						if (StringUtils.isBlank(name)) {
							invalidTabs.add(DRIVER_TRUCK_TAB);
							errorList.add(newValidationError("drvName", getMessage("valid.driver.drvName.notNull")));
						}
					}
					if (dto.getJobDrvOth().containsKey("drvPhone")) {
						String phone = dto.getJobDrvOth().get("drvPhone") == null ? null
								: dto.getJobDrvOth().get("drvPhone").toString();
						if (StringUtils.isBlank(phone)) {
							invalidTabs.add(DRIVER_TRUCK_TAB);
							errorList.add(newValidationError("drvPhone", getMessage("valid.driver.drvPhone.notNull")));
						}
					}
				}

				// Driver - TCkCtDrv
				else if (null != dto.getTCkCtDrv()) {
					if (null == dto.getTCkCtDrv().getDrvName() || StringUtils.isBlank(dto.getTCkCtDrv().getDrvName())) {
						invalidTabs.add(DRIVER_TRUCK_TAB);
						errorList.add(newValidationError("drvName", getMessage("valid.driver.drvName.notNull")));
					}

					if (null == dto.getTCkCtDrv().getDrvPhone()
							|| StringUtils.isBlank(dto.getTCkCtDrv().getDrvPhone())) {
						invalidTabs.add(DRIVER_TRUCK_TAB);
						errorList.add(newValidationError("drvPhone", getMessage("valid.driver.drvPhone.notNull")));
					}

				}

				else if (null == dto.getJobDrvOth() && null == dto.getTCkCtDrv()) {
					invalidTabs.add(DRIVER_TRUCK_TAB);
					errorList.add(newValidationError("drvName", getMessage("valid.driver.drvName.notNull")));
					errorList.add(newValidationError("drvPhone", getMessage("valid.driver.drvPhone.notNull")));
				}

				// Vehicle Validation
				// Vehicle - JobVehOth
				if (null != dto.getJobVehOth()) {
					if (dto.getJobVehOth().containsKey("vhPlateNo")) {
						String plateNo = dto.getJobVehOth().get("vhPlateNo") == null ? null
								: dto.getJobVehOth().get("vhPlateNo").toString();
						if (StringUtils.isBlank(plateNo)) {
							invalidTabs.add(DRIVER_TRUCK_TAB);
							errorList.add(
									newValidationError("vhPlateNo", getMessage("valid.vehicle.vhPlateNo.notNull")));
						}
					}
				}

				// Vehicle - TCkCtVeh
				else if (null != dto.getTCkCtVeh()) {
					if (null == dto.getTCkCtVeh().getVhPlateNo()
							|| StringUtils.isBlank(dto.getTCkCtVeh().getVhPlateNo())) {
						invalidTabs.add(DRIVER_TRUCK_TAB);
						errorList.add(newValidationError("vehicle", getMessage("valid.vehicle.notNull")));
					}

					if (null == dto.getTCkCtVeh().getTCkCtMstVehType() 
							|| StringUtils.isBlank(dto.getTCkCtVeh().getTCkCtMstVehType().getVhtyName())) {
						invalidTabs.add(DRIVER_TRUCK_TAB);
						errorList.add(newValidationError("vhType", getMessage("valid.vehicle.vhType.notNull")));
					}
//
//					if (null == dto.getTCkCtVeh().getVhClass()
//							|| StringUtils.isBlank(dto.getTCkCtVeh().getVhClass().toString())) {
//						invalidTabs.add(DRIVER_TRUCK_TAB);
//						errorList.add(newValidationError("vhClass", getMessage("valid.vehicle.vhClass.notNull")));
//					}

				}

				else if (null == dto.getJobVehOth() && null == dto.getTCkCtVeh()) {
					invalidTabs.add(DRIVER_TRUCK_TAB);
					errorList.add(newValidationError("vehicle", getMessage("valid.vehicle.notNull")));
					errorList.add(newValidationError("vhPlateNo", getMessage("valid.vehicle.vhPlateNo.notNull")));
					errorList.add(newValidationError("vhType", getMessage("valid.vehicle.vhType.notNull")));
//					errorList.add(newValidationError("vhClass", getMessage("valid.vehicle.vhClass.notNull")));
				}

				if (!invalidTabs.isEmpty()) {
					ObjectMapper mapper = new ObjectMapper();
					errorList.add(newValidationError("invalidTabs.driver", mapper.writeValueAsString(invalidTabs)));
				}
			}

			if (null != dto.getAction() && dto.getAction().getDesc().equalsIgnoreCase(JobActions.BILLJOB.name())) {

				// fetch invoice from DB.
				List<TCkCtToInvoice> toInvoiceList = toInvoiceDao.findByJobId(dto.getJobId());
				List<CkCtToInvoice> invoiceDtoList = new ArrayList<>();
				
				for(TCkCtToInvoice invEntity: toInvoiceList) {
					invoiceDtoList.add(invoiceService.dtoFromEntity(invEntity));
				}
				dto.setToInvoiceList(invoiceDtoList);
				
				// validate for TO invoice
				if ( dto.getToInvoiceList() == null || dto.getToInvoiceList().size() == 0) {
					invalidTabs.add(!dto.isDomestic() ? FIRST_MILE_INV_TAB: MID_MILE_INV_TAB);
				} else {
//					int i = 0;
					// only check and save 1 invoice for multi job
					//for (CkCtToInvoice toInv : dto.getToInvoiceList()) {
					if(dto.getToInvoiceList() != null && dto.getToInvoiceList().size() > 0) {
//						String prefix = "[" + i + "]";
						CkCtToInvoice toInv = dto.getToInvoiceList().get(0);
						
						if (StringUtils.isBlank(toInv.getInvNo())) {
							invalidTabs.add(!dto.isDomestic() ? FIRST_MILE_INV_TAB: MID_MILE_INV_TAB);
							errorList.add(newValidationError("invNo", getMessage("ckCtToInvoice.required")));
						}
						if (toInv.getInvDtIssue() == null) {
							invalidTabs.add(!dto.isDomestic() ? FIRST_MILE_INV_TAB: MID_MILE_INV_TAB);
							errorList.add(
									newValidationError("invDtIssue", getMessage("ckCtToInvoice.required")));
						}
						if (StringUtils.isBlank(toInv.getInvName())) {
							invalidTabs.add(!dto.isDomestic() ? FIRST_MILE_INV_TAB: MID_MILE_INV_TAB);
							errorList.add(newValidationError("invName", getMessage("ckCtToInvoice.required")));
						}
						if (null != toInv.getTripDoDetail() && null == toInv.getTripDoDetail().getPod()) {
							invalidTabs.add(!dto.isDomestic() ? FIRST_MILE_INV_TAB: MID_MILE_INV_TAB);
							errorList.add(newValidationError("pod", getMessage("ckCtToInvoice.required")));
						}
					}
					
					if(dto.getToInvoiceList()!=null &&
							dto.getToInvoiceList().size() < 1) {
						ObjectMapper mapper = new ObjectMapper();
						errorList.add(newValidationError("invalidTabs.midMileInvoice", mapper.writeValueAsString(invalidTabs)));
					}
				}

				if (!invalidTabs.isEmpty() && invalidTabs.contains(FIRST_MILE_INV_TAB)) {
					ObjectMapper mapper = new ObjectMapper();
					errorList.add(newValidationError("invalidTabs.invoice", mapper.writeValueAsString(invalidTabs)));
				}
				if (!invalidTabs.isEmpty() && invalidTabs.contains(MID_MILE_INV_TAB)) {
					ObjectMapper mapper = new ObjectMapper();
					errorList.add(newValidationError("invalidTabs.midMileInvoice", mapper.writeValueAsString(invalidTabs)));
				}
			}

			return errorList;
		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}
	}

	/**
	 * @param dto
	 * @param errorList
	 */
	private void validateCkCtTrips(CkJobTruck dto, List<ValidationError> errorList) {

		// String added corresponds to the tab name in FE
		Set<String> invalidTabs = new HashSet<>();

		List<CkCtTrip> ckCtTrips = dto.getTckCtTripList();
		if (ObjectUtils.isEmpty(ckCtTrips)) {
			invalidTabs.add(FIRST_MILE_TAB);
			errorList.add(newValidationError("tckCtTripLocationByTrTo.tckCtLocation.locId",
					getMessage("valid.jobTruck.ckCtTripLocationTo.required")));
			errorList.add(newValidationError("tckCtTripLocationByTrFrom.tckCtLocation.locId",
					getMessage("valid.jobTruck.ckCtTripLocationFrom.required")));
			errorList.add(newValidationError("tckCtTripLocationByTrDepot.tckCtLocation.locId",
					getMessage("valid.jobTruck.ckCtTripLocationDepot.required")));
		} else {
			for (CkCtTrip trip : ckCtTrips) {
				CkCtTripLocation ckCtTripLocationTo = trip.getTCkCtTripLocationByTrTo();
				CkCtTripLocation ckCtTripLocationFrom = trip.getTCkCtTripLocationByTrFrom();
				CkCtTripLocation ckCtTripLocationDepot = trip.getTCkCtTripLocationByTrDepot();

				// Validate TripLocationTo - if present
				if (null == ckCtTripLocationTo) {
					invalidTabs.add(FIRST_MILE_TAB);
					errorList.add(newValidationError("tckCtTripLocationByTrTo.tckCtLocation.locId",
							getMessage("valid.jobTruck.ckCtTripLocationTo.required")));
				}
				// Validate TripLocationFrom - if present
				if (null == ckCtTripLocationFrom) {
					invalidTabs.add(FIRST_MILE_TAB);
					errorList.add(newValidationError("tckCtTripLocationByTrFrom.tckCtLocation.locId",
							getMessage("valid.jobTruck.ckCtTripLocationFrom.required")));
				}
				// Validate TripLocationDepot - if present
				if ((!dto.isDomestic()) && (null == ckCtTripLocationDepot)) {
					invalidTabs.add(FIRST_MILE_TAB);
					errorList.add(newValidationError("tckCtTripLocationByTrDepot.tckCtLocation.locId",
							getMessage("valid.jobTruck.ckCtTripLocationDepot.required")));
				}

				// Origin and Destination cannot be the same
//				if (null != ckCtTripLocationFrom && null != ckCtTripLocationTo
//						&& null != ckCtTripLocationFrom.getTCkCtLocation()
//						&& null != ckCtTripLocationTo.getTCkCtLocation() && ckCtTripLocationFrom.getTCkCtLocation()
//								.getLocId().equals(ckCtTripLocationTo.getTCkCtLocation().getLocId())) {
//					invalidTabs.add(FIRST_MILE_TAB);
//					errorList.add(newValidationError("tckCtTripLocationByTrTo.tckCtLocation.locId",
//							getMessage("valid.jobTruck.ckCtTripLocationTo.equal")));
//				}
				
				// Validate ckCtTripCargoFmList
				List<CkCtTripCargoFm> ckCtTripCargoFmList = trip.getTckCtTripCargoFmList();
				if (ObjectUtils.isEmpty(ckCtTripCargoFmList)) {

				} else {
					for (CkCtTripCargoFm cargoFm : ckCtTripCargoFmList) {
						CkMstCntType ckMstCntType = cargoFm.getTCkMstCntType();
						CkCtMstCargoType ckCtMstCargoType = cargoFm.getTCkCtMstCargoType();
						String specialInstructions = cargoFm.getCgCargoSpecialInstn();

						// Container Type
						if (null == ckMstCntType || StringUtils.isBlank(ckMstCntType.getCnttId())) {

						}
						// Cargo Type
						if (null == ckCtMstCargoType || StringUtils.isBlank(ckCtMstCargoType.getCrtypId())) {

						}
						// Container No
						if (StringUtils.isBlank(cargoFm.getCgCntNo())) {

						} else if (cargoFm.getCgCntNo().length() > 255) {
							invalidTabs.add(FIRST_MILE_TAB);
							errorList.add(
									newValidationError("cgCntNo", getMessage("valid.jobTruck.containerNo.maxLength")));
						}
						// Goods Description
						if (StringUtils.isBlank(cargoFm.getCgCargoDesc())) {

						} else if (cargoFm.getCgCargoDesc().length() > 2048) { // max from db is 16777215
							invalidTabs.add(FIRST_MILE_TAB);
							errorList.add(newValidationError("cgCargoDesc",
									getMessage("valid.jobTruck.goodsDesc.maxLength")));
						}
						// Container Load
						if (null == cargoFm.getCgCntFullLoad() || !Character.isAlphabetic(cargoFm.getCgCntFullLoad())) {

						}
						// Special instruction
						if (null != specialInstructions && !StringUtils.isEmpty(specialInstructions)
								&& specialInstructions.length() > 2048) {
							invalidTabs.add(FIRST_MILE_TAB);
							errorList.add(newValidationError("cgCargoSpecialInstn",
									getMessage("valid.jobTruck.specialInstructions.maxLength")));
						}
						// Trip Charge, onlu validate if  tripCharges is not hidden
						if(!dto.getHiddenFields().contains("tripcharges")) {
							if (null == trip.getTCkCtTripCharge()) {
								invalidTabs.add(FIRST_MILE_TAB);
								errorList.add(newValidationError("tckCtTripCharge",
										getMessage("valid.jobTruck.tckCtTripCharge.notNull")));
							}
						}
						

					}
				}
			}
		}

		if (!invalidTabs.isEmpty()) {

			ObjectMapper mapper = new ObjectMapper();
			try {
				errorList.add(newValidationError("invalidTabs.fmTrip", mapper.writeValueAsString(invalidTabs)));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param dto
	 * @param errorList
	 */
	private void validateCkCtTripsDomestic(CkJobTruck dto, List<ValidationError> errorList) throws Exception {

		List<CkCtTrip> ckCtTrips = dto.getTckCtTripList();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date now = Calendar.getInstance().getTime();
		
		// string added correspond to the tab name in FE.
		Set<String> invalidTabs = new HashSet<>();

		// Validate vehicle type
		if (null == dto.getTCkCtMstVehType() || StringUtils.isBlank(dto.getTCkCtMstVehType().getVhtyId())) {
			invalidTabs.add(MID_MILE_TAB);
			errorList.add(
					newValidationError("tckCtMstVehType.vhtyId", getMessage("valid.jobTruck.ckCtMstVehType.notNull")));
		}
		
		if (ObjectUtils.isEmpty(ckCtTrips)) {
			invalidTabs.add(MID_MILE_TAB);
			errorList.add(newValidationError("Submit.API.call", getMessage("valid.jobTruck.domestic.trip.notNull")));
		} else {

			for (int i = 0; i < ckCtTrips.size(); i++) {
				CkCtTrip trip = ckCtTrips.get(i);

				String prefix = "[" + i + "]";

				CkCtTripLocation ckCtTripLocationTo = trip.getTCkCtTripLocationByTrTo();
				CkCtTripLocation ckCtTripLocationFrom = trip.getTCkCtTripLocationByTrFrom();

				// Validate TripLocationFrom - if present
				if (null == ckCtTripLocationFrom || null == ckCtTripLocationFrom.getTCkCtLocation()) {
					invalidTabs.add(MID_MILE_TAB);
					errorList.add(newValidationError(prefix + "tckCtTripLocationByTrFrom.tckCtLocation.locId",
							getMessage("valid.jobTruck.ckCtTripLocationFrom.required")));
				} else if (null != ckCtTripLocationFrom.getTCkCtLocation() && this.isRegion(trip.getTCkCtTripLocationByTrFrom().getTCkCtLocation().getLocId())){
					if (trip.getTCkCtTripLocationByTrFrom().getTlocLocAddress() == null || trip.getTCkCtTripLocationByTrFrom().getTlocLocAddress().equals("")){
						invalidTabs.add(MID_MILE_TAB);
						errorList.add(newValidationError(prefix + "tckCtTripLocationByTrFrom.tlocLocAddress",
								getMessage("valid.tckCtTripLocationByTrFrom.tckCtLocation.locAddress.required")));
					}
				}

				// Validate TripLocationTo 0 - if present
				if (null == ckCtTripLocationTo || null == ckCtTripLocationTo.getTCkCtLocation()) {
					invalidTabs.add(MID_MILE_TAB);
					errorList.add(newValidationError(prefix + "tckCtTripLocationByTrTo.tckCtLocation.locId",
							getMessage("valid.jobTruck.ckCtTripLocationTo.required")));
				} else if (null != ckCtTripLocationTo.getTCkCtLocation() && this.isRegion(trip.getTCkCtTripLocationByTrTo().getTCkCtLocation().getLocId())){
					if (trip.getTCkCtTripLocationByTrTo().getTlocLocAddress() == null || trip.getTCkCtTripLocationByTrTo().getTlocLocAddress().equals("")){
						invalidTabs.add(MID_MILE_TAB);
						errorList.add(newValidationError(prefix + "tckCtTripLocationByTrTo.tlocLocAddress",
								getMessage("valid.tckCtTripLocationByTrTo.tckCtLocation.locAddress.required")));
					}
				}

				// Validate Schedule Details From - if equal or greater than current date
				if (null != ckCtTripLocationFrom && ckCtTripLocationFrom.getTlocDtLoc() != null
						&& df.format(ckCtTripLocationFrom.getTlocDtLoc()).compareTo(df.format(now)) < 0) {
					invalidTabs.add(MID_MILE_TAB);
					errorList.add(newValidationError(prefix + "tckCtTripLocationByTrFrom.tlocDtLoc",
							getMessage("valid.jobTruck.tckCtTripLocationByTrFrom.beforeToday")));
				}
				
				// Validate Schedule Details To - if equal or greater than current date
				if (null != ckCtTripLocationTo && ckCtTripLocationTo.getTlocDtLoc() != null
						&& df.format(ckCtTripLocationTo.getTlocDtLoc()).compareTo(df.format(now)) < 0) {
					invalidTabs.add(MID_MILE_TAB);
					errorList.add(newValidationError(prefix + "tckCtTripLocationByTrTo.tlocDtLoc",
							getMessage("valid.jobTruck.tckCtTripLocationByTrTo.beforeToday")));
				}
				
				// Validate Schedule Details From is earlier than Schedule Details To
				if (null != ckCtTripLocationFrom && ckCtTripLocationFrom.getTlocDtLoc() != null
						&& null != ckCtTripLocationTo && ckCtTripLocationTo.getTlocDtLoc() != null) {
					if (df.format(ckCtTripLocationTo.getTlocDtLoc())
							.compareTo(df.format(ckCtTripLocationFrom.getTlocDtLoc())) < 0) {
						invalidTabs.add(MID_MILE_TAB);
						errorList.add(newValidationError(prefix + "tckCtTripLocationByTrTo.tlocDtLoc",
								getMessage("valid.jobTruck.tckCtTripLocationByTrTo.beforeFrom")));
					}
				}
				
				// Origin and Destination cannot be the same
//				if (null != ckCtTripLocationFrom && null != ckCtTripLocationTo
//						&& null != ckCtTripLocationFrom.getTCkCtLocation()
//						&& null != ckCtTripLocationTo.getTCkCtLocation() && ckCtTripLocationFrom.getTCkCtLocation()
//								.getLocId().equals(ckCtTripLocationTo.getTCkCtLocation().getLocId())) {
//					invalidTabs.add(MID_MILE_TAB);
//					errorList.add(newValidationError(prefix + "tckCtTripLocationByTrTo.tckCtLocation.locId",
//							getMessage("valid.jobTruck.ckCtTripLocationTo.equal")));
//				}

				// Added validation for mobile number, only applicable to TO
				if (dto.getJobMobileEnabled() == 'Y') {
					if (ckCtTripLocationTo != null && StringUtils.isBlank(ckCtTripLocationTo.getTlocMobileNo())) {
						invalidTabs.add(MID_MILE_TAB);
						errorList.add(newValidationError(prefix + "tckCtTripLocationByTrTo.tlocMobileNo",
								getMessage("valid.jobTruck.tckCtTripLocationByTrTo.tlocMobileNo")));
					}
				}
				
				// Validate Schedule Details if mobile enabled
				if (null != ckCtTripLocationFrom && null != ckCtTripLocationTo
						&& null != ckCtTripLocationFrom.getTCkCtLocation()
						&& null != ckCtTripLocationTo.getTCkCtLocation()) {
					Date scheduleFrom = null != ckCtTripLocationFrom ? ckCtTripLocationFrom.getTlocDtLoc() : null;
					Date scheduleTo = null != ckCtTripLocationTo ? ckCtTripLocationTo.getTlocDtLoc() : null;
					if (dto.getJobMobileEnabled() == 'Y') {
						if (null == scheduleFrom) {
							invalidTabs.add(MID_MILE_TAB);
							errorList.add(newValidationError(prefix + "tckCtTripLocationByTrFrom.tlocDtLoc",
									getMessage("valid.jobTruck.tlocDtLoc.required")));
							
						}
						if (null == scheduleTo) {
							invalidTabs.add(MID_MILE_TAB);
							errorList.add(newValidationError(prefix + "tckCtTripLocationByTrTo.tlocDtLoc",
									getMessage("valid.jobTruck.tlocDtLoc.required")));
						}
						if (null != scheduleFrom && null != scheduleTo) {
							if (scheduleTo.before(scheduleFrom)) {
								invalidTabs.add(MID_MILE_TAB);
								errorList.add(newValidationError(prefix + "tckCtTripLocationByTrTo.tlocDtLoc",
										getMessage("valid.jobTruck.tckCtTripLocationByTrTo.beforeFrom")));
							}
						}
					}
				}
				
				// Validate mobile number format if present [START]
				if (null != ckCtTripLocationFrom && StringUtils.isNotEmpty(ckCtTripLocationFrom.getTlocMobileNo())) {
					Pattern pattern = Pattern.compile(PHONE_PATTERN);
					Matcher matcher = pattern.matcher(ckCtTripLocationFrom.getTlocMobileNo());
					if (ckCtTripLocationFrom.getTlocMobileNo().length() < PHONE_MIN_LENGTH) {
						invalidTabs.add(MID_MILE_TAB);
						errorList.add(newValidationError(prefix + "tckCtTripLocationByTrFrom.tlocMobileNo",
								getMessage("valid.jobTruck.tckCtTripLocation.tlocMobileNo.length")));
					} else if (!matcher.matches()) {
						invalidTabs.add(MID_MILE_TAB);
						errorList.add(newValidationError(prefix + "tckCtTripLocationByTrFrom.tlocMobileNo",
								getMessage("valid.jobTruck.tckCtTripLocation.tlocMobileNo.format")));
					}
				}
				if (null != ckCtTripLocationTo && StringUtils.isNotEmpty(ckCtTripLocationTo.getTlocMobileNo())) {
					Pattern pattern = Pattern.compile(PHONE_PATTERN);
					Matcher matcher = pattern.matcher(ckCtTripLocationTo.getTlocMobileNo());
					if (ckCtTripLocationTo.getTlocMobileNo().length() < PHONE_MIN_LENGTH) {
						invalidTabs.add(MID_MILE_TAB);
						errorList.add(newValidationError(prefix + "tckCtTripLocationByTrTo.tlocMobileNo",
								getMessage("valid.jobTruck.tckCtTripLocation.tlocMobileNo.length")));
					} else if (!matcher.matches()) {
						invalidTabs.add(MID_MILE_TAB);
						errorList.add(newValidationError(prefix + "tckCtTripLocationByTrTo.tlocMobileNo",
								getMessage("valid.jobTruck.tckCtTripLocation.tlocMobileNo.format")));
					}
				}
				// Validate mobile number format if present [END]
			}
		}

		if (!invalidTabs.isEmpty()) {

			ObjectMapper mapper = new ObjectMapper();
			try {
				errorList.add(newValidationError("invalidTabs.mmTrip", mapper.writeValueAsString(invalidTabs)));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private boolean isRegion(String locId) throws Exception {
		TCkCtLocation location = ckCtLocationDao.find(locId);
		if (location != null){
			return location.isRegion();
		}
		return false;
	}
	
}