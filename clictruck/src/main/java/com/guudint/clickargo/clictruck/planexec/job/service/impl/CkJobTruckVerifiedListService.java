package com.guudint.clickargo.clictruck.planexec.job.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruckVerified;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.common.dto.CkRecordDate;
import com.guudint.clickargo.common.model.TCkRecordDate;
import com.guudint.clickargo.common.service.AbstractCkListingService;
import com.guudint.clickargo.job.dto.CkJob;
import com.guudint.clickargo.job.model.TCkJob;
import com.guudint.clickargo.master.dto.CkMstJobState;
import com.guudint.clickargo.master.dto.CkMstJobType;
import com.guudint.clickargo.master.dto.CkMstShipmentType;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.JobStates;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

public class CkJobTruckVerifiedListService extends AbstractCkListingService<TCkJobTruck, String, CkJobTruckVerified> {

	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(CkJobTruckVerifiedListService.class);
	private static String AUDIT_TAG = "CK JOB TRUCK";
	private static String TABLE_NAME = "T_CK_JOB_TRUCK";
	private static String HISTORY = "history";
	private static String DEFAULT = "default";

	@Autowired
	private CkJobTruckServiceUtil ckJobTruckUtilService;

	public CkJobTruckVerifiedListService() {
		super("ckJobTruckDao", AUDIT_TAG, TCkJobTruck.class.getName(), TABLE_NAME);

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkJobTruckVerified> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkJobTruckVerified dto = this.whereDto(filterRequest);
			if (null == dto)
				throw new ProcessingException("whereDto null");

			filterRequest.setTotalRecords(super.countByAnd(dto));
			String selectClause = "FROM TCkJobTruck o ";
			String orderByClauses = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkJobTruck> entities = this.findEntitiesByAnd(dto, selectClause, orderByClauses,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			List<CkJobTruckVerified> dtos = entities.stream().map(x -> {
				try {
					return dtoFromEntity(x);
				} catch (ParameterException | ProcessingException e) {
					LOG.error("filterBy", e);
				}
				return null;
			}).collect(Collectors.toList());

			return dtos;
		} catch (ParameterException | ProcessingException ex) {
			LOG.error("filterBy", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("filterBy", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected CkJobTruckVerified dtoFromEntity(TCkJobTruck entity) throws ParameterException, ProcessingException {
		try {
			if (null == entity)
				throw new ParameterException("param entity null");

			CkJobTruckVerified dto = new CkJobTruckVerified(entity);
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

				Optional<TCkRecordDate> opRecordDate = Optional.ofNullable(ckJobE.getTCkRecordDate());
				if (opRecordDate.isPresent()) {
					dto.setBillingDate(opRecordDate.get().getRcdDtBilled());
					dto.setVerifiedBy(opRecordDate.get().getRcdUidVerified());
					dto.setVerifiedDate(opRecordDate.get().getRcdDtVerified());
				}
					
			}

			
			dto.setCharges(dto.getJobTotalCharge());
			dto.setReimbursements(dto.getJobTotalReimbursements());

			Optional<TCoreAccn> opCoreAccnByJobPartyTo = Optional.ofNullable(entity.getTCoreAccnByJobPartyTo());
			dto.setInvoiceFromAccn(
					opCoreAccnByJobPartyTo.isPresent() ? new CoreAccn(opCoreAccnByJobPartyTo.get()) : null);

			// is Domestic
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

	@Override
	protected String getWhereClause(CkJobTruckVerified dto, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			StringBuffer searchStatement = new StringBuffer();
			if (null != dto.getJobStatus()) {
				searchStatement.append(getOperator(wherePrinted) + "o.jobStatus=:jobStatus");
				wherePrinted = true;
			}

			Principal principal = principalUtilService.getPrincipal();
			if (principal == null)
				throw new ProcessingException("principal is null");

			CoreAccn accn = principal.getCoreAccn();

			if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_FF.name())
					|| accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_CO.name())) {
				searchStatement.append(getOperator(wherePrinted)).append("o.TCoreAccnByJobPartyCoFf.accnId = :accnId");
				wherePrinted = true;
				searchStatement.append(getOperator(wherePrinted))
						.append("o.TCkJob.TCkMstJobState.jbstId IN :jobTruckStates");
				wherePrinted = true;
			} else if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_TO.name())) {
				// For Truck Operators
				searchStatement.append(getOperator(wherePrinted)).append("o.TCoreAccnByJobPartyTo.accnId = :accnId");
				wherePrinted = true;
				searchStatement.append(getOperator(wherePrinted))
						.append("o.TCkJob.TCkMstJobState.jbstId IN :jobTruckStates");
				wherePrinted = true;
			} else {
				searchStatement.append(getOperator(wherePrinted))
						.append("o.TCkJob.TCkMstJobState.jbstId IN :jobTruckStates");
				wherePrinted = true;
			}

			if (StringUtils.isNotBlank(dto.getJobId())) {
				searchStatement.append(getOperator(wherePrinted)).append("o.jobId LIKE :jobId");
				wherePrinted = true;
			}

			Optional<CkJob> opCkJob = Optional.of(dto.getTCkJob());
			if (opCkJob.isPresent()) {
				Optional<CkMstShipmentType> opShipmntType = opCkJob.map(CkJob::getTCkMstShipmentType);
				if (opShipmntType.isPresent()) {
					if (StringUtils.isNotBlank(opShipmntType.get().getShtId())) {
						searchStatement.append(getOperator(wherePrinted))
								.append("o.TCkJob.TCkMstShipmentType.shtId = :shtId");
						wherePrinted = true;
					}

					if (StringUtils.isNotBlank(opShipmntType.get().getShtName())) {
						searchStatement.append(getOperator(wherePrinted))
								.append("o.TCkJob.TCkMstShipmentType.shtName = :shtName");
						wherePrinted = true;

					}
				}

				Optional<CkRecordDate> opCkRecordDate = Optional.of(dto.getTCkJob()).map(CkJob::getTCkRecordDate);
				if (opCkRecordDate.isPresent()) {
					if (opCkRecordDate.get().getRcdDtVerified() != null) {
						searchStatement.append(getOperator(wherePrinted)).append(
								"DATE_FORMAT(o.TCkJob.TCkRecordDate.rcdDtVerified,'%d/%m/%Y') = :rcdDtVerified");
						wherePrinted = true;
					}

//					if (StringUtils.isNotBlank(opCkRecordDate.get().getRcdUidVerified())) {
//						searchStatement.append(getOperator(wherePrinted)).append(
//								"DATE_FORMAT(o.TCkJob.TCkRecordDate.rcdUidVerified,'%d/%m/%Y') = :rcdUidVerified");
//						wherePrinted = true;
//					}
				}

				Optional<CkMstJobState> opCkJobMstState = Optional.of(dto.getTCkJob()).map(CkJob::getTCkMstJobState);
				if (opCkJobMstState.isPresent()) {
					if (StringUtils.isNotBlank(opCkJobMstState.get().getJbstId())) {
						searchStatement.append(getOperator(wherePrinted))
								.append("o.TCkJob.TCkMstJobState.jbstId = :jobState");
						wherePrinted = true;
					}
				}
			}

			Optional<CoreAccn> opCoreAccnByJobPartyTo = Optional.ofNullable(dto.getInvoiceFromAccn());
			if (opCoreAccnByJobPartyTo.isPresent()) {
				if (StringUtils.isNotBlank(opCoreAccnByJobPartyTo.get().getAccnId())) {
					searchStatement.append(getOperator(wherePrinted))
							.append("o.TCoreAccnByJobPartyTo.accnId = :toAccnId");
					wherePrinted = true;
				}
				if (StringUtils.isNotBlank(opCoreAccnByJobPartyTo.get().getAccnName())) {
					searchStatement.append(getOperator(wherePrinted))
							.append("o.TCoreAccnByJobPartyTo.accnName LIKE :toAccnName");
					wherePrinted = true;
				}
			}

//			if (StringUtils.isNotBlank(dto.getVerifiedBy())) {
//				// verified by and date
//				searchStatement.append(getOperator(wherePrinted))
//						.append("o.TCkJob.TCkRecordDate.rcdUidVerified LIKE :verifiedBy");
//				wherePrinted = true;
//			}

			Optional<Date> opVerifiedDate = Optional.ofNullable(dto.getVerifiedDate());
			if (opVerifiedDate.isPresent() && null != opVerifiedDate.get()) {
				searchStatement.append(getOperator(wherePrinted)
						+ "DATE_FORMAT(o.TCkJob.TCkRecordDate.rcdDtVerified,'%d/%m/%Y') = :verifiedDate");
				wherePrinted = true;
			}

			Optional<Date> opBillingDate = Optional.ofNullable(dto.getBillingDate());
			if (opBillingDate.isPresent() && null != opBillingDate.get()) {
				searchStatement.append(getOperator(wherePrinted)
						+ "DATE_FORMAT(o.TCkJob.TCkRecordDate.rcdDtBilled,'%d/%m/%Y') = :billedDate");
				wherePrinted = true;
			}

			Optional<Date> opJobDtLupd = Optional.ofNullable(dto.getJobDtLupd());
			if (opJobDtLupd.isPresent() && null != opJobDtLupd.get()) {
				searchStatement.append(getOperator(wherePrinted) + "DATE_FORMAT(o.jobDtLupd,'%d/%m/%Y') = :jobDtLupd");
				wherePrinted = true;
			}
			
			//add jobnotrip filter by
			if (dto.getJobNoTrips() != null && dto.getJobNoTrips() > 0) {
				searchStatement.append(getOperator(wherePrinted))
					.append("o.jobNoTrips =  :jobNoTripsFilter");
				wherePrinted = true;
			}
			
			//add verifyBy filter
			if ( dto.getVerifiedBy() != null ) {
				searchStatement.append(getOperator(wherePrinted))
					.append("o.TCkJob.TCkRecordDate.rcdUidVerified LIKE :verifiedbyFilter");
				wherePrinted = true;
			}
			
			// add payment state filter
			if ( dto.getJobInPaymentState() != null ) {
				searchStatement.append(getOperator(wherePrinted))
					.append("o.jobInPaymentState = :paymentStateFilter");
				wherePrinted = true;
			}

			//add reimbursement filter
			if (dto.getJobTotalReimbursements() != null ) {
				searchStatement.append(getOperator(wherePrinted))
					.append("o.jobTotalReimbursements = :reimbursement");
				wherePrinted = true;
			}
			
			//add tripcharge filter
			if (dto.getJobTotalCharge() != null){
				searchStatement.append(getOperator(wherePrinted))
					.append("o.jobTotalCharge = :tripCharge");
				wherePrinted = true;
			}
		

			return searchStatement.toString();
		} catch (ParameterException ex) {
			LOG.error("getWhereClause", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("getWhereClause", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected HashMap<String, Object> getParameters(CkJobTruckVerified dto)
			throws ParameterException, ProcessingException {
		LOG.debug("getParameters");

		SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			HashMap<String, Object> parameters = new HashMap<String, Object>();

			Principal principal = principalUtilService.getPrincipal();

			if (principal == null)
				throw new ProcessingException("principal is null");
			CoreAccn accn = principal.getCoreAccn();

			parameters.put("accnId", accn.getAccnId());
			// Account Type Freight Forwarder
			if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_FF.name())
					|| accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_CO.name())
					|| accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_TO.name())) {
				if (dto.getHistory() != null && dto.getHistory().equalsIgnoreCase(HISTORY)) {

					parameters.put("jobTruckStates", Arrays.asList(JobStates.APP_BILL.name()));

				} else {
					parameters.put("jobTruckStates", Arrays.asList(JobStates.VER_BILL.name()));

				}
				// Account Type Cargo Owner
			}

			if (StringUtils.isNotBlank(dto.getJobId())) {
				parameters.put("jobId", "%" + dto.getJobId() + "%");
			}

			Optional<CkJob> opCkJob = Optional.of(dto.getTCkJob());
			if (opCkJob.isPresent()) {
				Optional<CkMstShipmentType> opShipmntType = opCkJob.map(CkJob::getTCkMstShipmentType);
				if (opShipmntType.isPresent()) {
					if (StringUtils.isNotBlank(opShipmntType.get().getShtId()))
						parameters.put("shtId", opShipmntType.get().getShtId());

					if (StringUtils.isNotBlank(opShipmntType.get().getShtName()))
						parameters.put("shtName", opShipmntType.get().getShtName());
				}

				Optional<CkRecordDate> opCkRecordDate = Optional.of(dto.getTCkJob()).map(CkJob::getTCkRecordDate);
				if (opCkRecordDate.isPresent()) {
//					if (opCkRecordDate.get().getRcdDtVerified() != null)
//						parameters.put("rcdDtVerified", sdfDate.format(opCkRecordDate.get().getRcdDtVerified()));
//					if (StringUtils.isNotBlank(opCkRecordDate.get().getRcdUidVerified()))
//						parameters.put("rcdUidVerified", sdfDate.format(opCkRecordDate.get().getRcdDtVerified()));
				}

				Optional<CkMstJobState> opCkJobMstState = Optional.of(dto.getTCkJob()).map(CkJob::getTCkMstJobState);
				if (opCkJobMstState.isPresent()) {
					if (StringUtils.isNotBlank(opCkJobMstState.get().getJbstId()))
						parameters.put("jobState", opCkJobMstState.get().getJbstId());

				}
			}

			Optional<CoreAccn> opCoreAccnByJobPartyTo = Optional.of(dto.getInvoiceFromAccn());
			if (opCoreAccnByJobPartyTo.isPresent()) {
				if (StringUtils.isNotBlank(opCoreAccnByJobPartyTo.get().getAccnId())) {
					parameters.put("toAccnId", opCoreAccnByJobPartyTo.get().getAccnId());
				}
				if (StringUtils.isNotBlank(opCoreAccnByJobPartyTo.get().getAccnName())) {
					parameters.put("toAccnName", "%" + opCoreAccnByJobPartyTo.get().getAccnName() + "%");
				}
			}

//			if (StringUtils.isNotBlank(dto.getVerifiedBy())) {
//				// verified by and date
//				parameters.put("verifiedByFilter", StringUtils.isNotBlank(dto.getVerifiedBy()));
//			}

			Optional<Date> opVerifiedDate = Optional.ofNullable(dto.getVerifiedDate());
			if (opVerifiedDate.isPresent() && null != opVerifiedDate.get()) {
				parameters.put("verifiedDate", sdfDate.format(opVerifiedDate.get()));
			}

			Optional<Date> opBillingDate = Optional.ofNullable(dto.getBillingDate());
			if (opBillingDate.isPresent() && null != opBillingDate.get()) {
				parameters.put("billedDate", sdfDate.format(opBillingDate.get()));
			}

			Optional<Date> opJobDtLupd = Optional.ofNullable(dto.getJobDtLupd());
			if (opJobDtLupd.isPresent() && null != opJobDtLupd.get())
				parameters.put("jobDtLupd", sdfDate.format(opJobDtLupd.get()));
			
			//add jobnotripsfilter
			if (dto.getJobNoTrips() != null && dto.getJobNoTrips() > 0) {
				parameters.put("jobNoTripsFilter", dto.getJobNoTrips());
			}
			
			//add verifyBy filter
			if ( dto.getVerifiedBy() != null ) {
				parameters.put("verifiedbyFilter", "%" + dto.getVerifiedBy() + "%");
			}
			
			//add payment state filter
			if (dto.getJobInPaymentState() != null ) {
				parameters.put("paymentStateFilter", dto.getJobInPaymentState());
			}
			
			//add reimbursement filter
			if (dto.getJobTotalReimbursements() != null )
				parameters.put("reimbursement", dto.getJobTotalReimbursements());
			
			//add tripcharge filter
			if (dto.getJobTotalCharge() != null)
				parameters.put("tripCharge", dto.getJobTotalCharge());
			
			return parameters;
		} catch (ParameterException ex) {
			LOG.error("getParameters", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("getParameters", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected CkJobTruckVerified whereDto(EntityFilterRequest filterRequest)
			throws ParameterException, ProcessingException {
		LOG.debug("whereDto");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");

			CkJobTruckVerified dto = new CkJobTruckVerified();
			CkJob ckJob = new CkJob();
			CkRecordDate recordDate = new CkRecordDate();
			CkMstJobState ckMstState = new CkMstJobState();
			CkMstShipmentType ckMstShpType = new CkMstShipmentType();

			CoreAccn invFromAccn = new CoreAccn();

			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;

				if (entityWhere.getAttribute().equalsIgnoreCase("jobId"))
					dto.setJobId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCkJob.TCkMstShipmentType.shtId"))
					ckMstShpType.setShtId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCkJob.TCkMstShipmentType.shtName"))
					ckMstShpType.setShtName(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCkJob.TCkRecordDate.rcdDtSubmit"))
					recordDate.setRcdDtSubmit(sdfDate.parse(opValue.get()));
				if (entityWhere.getAttribute().equalsIgnoreCase("TCkJob.TCkMstJobState.jbstId"))
					ckMstState.setJbstId(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("invoiceFromAccn.accnId"))
					invFromAccn.setAccnId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("invoiceFromAccn.accnName"))
					invFromAccn.setAccnName(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("jobDtLupd"))
					dto.setJobDtLupd(sdfDate.parse(opValue.get()));

				if (entityWhere.getAttribute().equalsIgnoreCase("verifiedDate")) {
					dto.setVerifiedDate(sdfDate.parse(opValue.get()));
					recordDate.setRcdDtVerified(sdfDate.parse(opValue.get()));
				}

				if (entityWhere.getAttribute().equalsIgnoreCase("verifiedBy")) {
					recordDate.setRcdUidVerified(opValue.get());
					dto.setVerifiedBy(opValue.get());
				}

				if (entityWhere.getAttribute().equalsIgnoreCase("billingDate"))
					dto.setBillingDate(sdfDate.parse(opValue.get()));

				// history toggle
				if (entityWhere.getAttribute().equalsIgnoreCase(HISTORY)) {
					dto.setHistory(opValue.get());
				} else if (entityWhere.getAttribute().equalsIgnoreCase(DEFAULT)) {
					dto.setHistory(opValue.get());
				}
				
				// filterby jobNoTrips
				if (entityWhere.getAttribute().equalsIgnoreCase("jobNoTrips"))
					dto.setJobNoTrips(Short.valueOf(opValue.get()));
				
				//filterby payment status
				if (entityWhere.getAttribute().equalsIgnoreCase("jobInPaymentState"))
					dto.setJobInPaymentState(opValue.get());

				//filterby reimbursement
				if (entityWhere.getAttribute().equalsIgnoreCase("jobTotalReimbursements"))
				{
					BigDecimal reimbursement = new BigDecimal(opValue.get());
					dto.setJobTotalReimbursements(reimbursement);
				}
				
				//filterby tripcharge
				if (entityWhere.getAttribute().equalsIgnoreCase("jobTotalCharge"))
				{
					BigDecimal tripcharge = new BigDecimal(opValue.get());
					dto.setJobTotalCharge(tripcharge);
				}
				
				
			}

			ckJob.setTCkMstShipmentType(ckMstShpType);
			ckJob.setTCkRecordDate(recordDate);
			ckJob.setTCkMstJobState(ckMstState);
			dto.setTCkJob(ckJob);

			dto.setInvoiceFromAccn(invFromAccn);
			return dto;
		} catch (ParameterException ex) {
			LOG.error("whereDto", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("whereDto", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected TCkJobTruck initEnity(TCkJobTruck entity) throws ParameterException, ProcessingException {
		if (null != entity) {
			Hibernate.initialize(entity.getTCkJob());
			Optional.ofNullable(entity.getTCkJob().getTCkMstJobState()).ifPresent(e -> Hibernate.initialize(e));
			Optional.ofNullable(entity.getTCkJob().getTCoreAccnByJobCoAccn()).ifPresent(e -> Hibernate.initialize(e));
			Optional.ofNullable(entity.getTCkJob().getTCoreAccnByJobFfAccn()).ifPresent(e -> Hibernate.initialize(e));
			Optional.ofNullable(entity.getTCkJob().getTCoreAccnByJobOwnerAccn())
					.ifPresent(e -> Hibernate.initialize(e));
			Optional.ofNullable(entity.getTCkJob().getTCoreAccnByJobToAccn()).ifPresent(e -> Hibernate.initialize(e));
			Hibernate.initialize(entity.getTCkCtContactDetailByJobContactTo());
			Hibernate.initialize(entity.getTCkCtContactDetailByJobContactCoFf());
			Hibernate.initialize(entity.getTCkCtDebitNoteByJobInvoiceeDebitNote());
			Hibernate.initialize(entity.getTCkCtDebitNoteByJobInvoicerDebitNote());
			Hibernate.initialize(entity.getTCkCtDrv());
			Hibernate.initialize(entity.getTCkCtMstVehType());
			Hibernate.initialize(entity.getTCkCtToInvoice());
			Hibernate.initialize(entity.getTCkCtVeh());
			Hibernate.initialize(entity.getTCoreAccnByJobPartyTo());
			Hibernate.initialize(entity.getTCoreAccnByJobPartyCoFf());
		}
		return entity;
	}

	@Override
	protected String formatOrderBy(String attribute) throws Exception {
		String newAttr = attribute;
		if (StringUtils.contains(newAttr, "tcoreAccn"))
			newAttr = newAttr.replace("tcoreAccn", "TCoreAccn");

		if (StringUtils.contains(newAttr, "tcoreAccnByJobPartyTo"))
			newAttr = newAttr.replace("tcoreAccnByJobPartyTo", "TCoreAccnByJobPartyTo");

		if (StringUtils.contains(newAttr, "tckJob"))
			newAttr = newAttr.replace("tckJob", "TCkJob");

		if (StringUtils.contains(newAttr, "tckMstShipmentType"))
			newAttr = newAttr.replace("tckMstShipmentType", "TCkMstShipmentType");

		if (StringUtils.contains(newAttr, "tckRecordDate"))
			newAttr = newAttr.replace("tckRecordDate", "TCkRecordDate");

		if (StringUtils.contains(newAttr, "tckMstJobState"))
			newAttr = newAttr.replace("tckMstJobState", "TCkMstJobState");

		if (StringUtils.contains(newAttr, "verifiedDate"))
			newAttr = "TCkJob.TCkRecordDate.rcdDtVerified";

		if (StringUtils.contains(newAttr, "verifiedBy"))
			newAttr = "TCkJob.TCkRecordDate.rcdUidVerified";

		return newAttr;
	}

}
