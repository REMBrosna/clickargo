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

import com.guudint.clickargo.clictruck.finacing.dto.JobPaymentStates;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruckAcknowledged;
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
import com.vcc.camelone.master.dto.MstAccnType;

public class CkJobTruckAcknowledgedListService
		extends AbstractCkListingService<TCkJobTruck, String, CkJobTruckAcknowledged> {

	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(CkJobTruckAcknowledgedListService.class);
	private static String AUDIT_TAG = "CK JOB TRUCK";
	private static String TABLE_NAME = "T_CK_JOB_TRUCK";
	private static String HISTORY = "history";
	private static String DEFAULT = "default";

	@Autowired
	private CkJobTruckServiceUtil ckJobTruckUtilService;

	public CkJobTruckAcknowledgedListService() {
		super("ckJobTruckDao", AUDIT_TAG, TCkJobTruck.class.getName(), TABLE_NAME);

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkJobTruckAcknowledged> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkJobTruckAcknowledged dto = this.whereDto(filterRequest);
			if (null == dto)
				throw new ProcessingException("whereDto null");

			filterRequest.setTotalRecords(super.countByAnd(dto));
			String selectClause = "FROM TCkJobTruck o ";
			String orderByClauses = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkJobTruck> entities = this.findEntitiesByAnd(dto, selectClause, orderByClauses,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			List<CkJobTruckAcknowledged> dtos = entities.stream().map(x -> {
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
	protected CkJobTruckAcknowledged dtoFromEntity(TCkJobTruck entity) throws ParameterException, ProcessingException {
		try {
			if (null == entity)
				throw new ParameterException("param entity null");

			CkJobTruckAcknowledged dto = new CkJobTruckAcknowledged(entity);
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
					dto.setAcknowledgedBy(opRecordDate.get().getRcdUidBillAcknowledged());
					dto.setAcknowledgedDate(opRecordDate.get().getRcdDtBillAcknowledged());
				}

				Optional<MstAccnType> opAccnType = Optional
						.ofNullable(principalUtilService.getPrincipal().getCoreAccn().getTMstAccnType());
				if (opAccnType.isPresent()
						&& opAccnType.get().getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_SP.name())) {
					dto.setPaymentDueDate(dto.getJobOutPaymentDtDue());
				} else {
					dto.setPaymentDueDate(dto.getJobInPaymentDtDue());
				}
			}

			dto.setCharges(dto.getJobTotalCharge());
			dto.setPlatformFees(dto.getPlatformFees());

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
	protected String getWhereClause(CkJobTruckAcknowledged dto, boolean wherePrinted)
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
			boolean isServiceProvider = false;

			if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_FF.name())
					|| accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_CO.name())) {
				searchStatement.append(getOperator(wherePrinted)).append("o.TCoreAccnByJobPartyCoFf.accnId = :accnId");
				wherePrinted = true;
				searchStatement.append(getOperator(wherePrinted))
						.append("o.TCkJob.TCkMstJobState.jbstId IN :jobTruckStates");
				wherePrinted = true;

				if (dto.getHistory() != null && dto.getHistory().equalsIgnoreCase(HISTORY)) {
					// PAYMENT STATE
					searchStatement.append(getOperator(wherePrinted))
							.append("o.jobInPaymentState IN (:inPaymentState)");
					wherePrinted = true;
				}

			} else if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_TO.name())) {
				// For Truck Operators
				searchStatement.append(getOperator(wherePrinted)).append("o.TCoreAccnByJobPartyTo.accnId = :accnId");
				wherePrinted = true;
				searchStatement.append(getOperator(wherePrinted))
						.append("o.TCkJob.TCkMstJobState.jbstId IN :jobTruckStates");
				wherePrinted = true;

			} else if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_SP.name())) {
				isServiceProvider = true;
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

				Optional<CoreAccn> opInvoiceToAccn = opCkJob.map(CkJob::getTCoreAccnByJobOwnerAccn);
				if (opInvoiceToAccn.isPresent()) {
					if (StringUtils.isNotBlank(opInvoiceToAccn.get().getAccnId())) {
						searchStatement.append(getOperator(wherePrinted))
								.append("o.TCkJob.TCoreAccnByJobOwnerAccn.accnId = :coFFAccnId");
						wherePrinted = true;
					}

					if (StringUtils.isNotBlank(opInvoiceToAccn.get().getAccnName())) {
						searchStatement.append(getOperator(wherePrinted))
								.append("o.TCkJob.TCoreAccnByJobOwnerAccn.accnName LIKE :coFFAccnName");
						wherePrinted = true;
					}
				}
				
				Optional<CoreAccn> opCoreAccnByJobCoAccn = opCkJob.map(CkJob::getTCoreAccnByJobCoAccn);
				if (opCoreAccnByJobCoAccn.isPresent()) {
					if (StringUtils.isNotBlank(opCoreAccnByJobCoAccn.get().getAccnId())) {
						searchStatement.append(getOperator(wherePrinted))
								.append("o.TCkJob.TCoreAccnByJobCoAccn.accnId = :jobCoAccnId");
						wherePrinted = true;
					}

					if (StringUtils.isNotBlank(opCoreAccnByJobCoAccn.get().getAccnName())) {
						searchStatement.append(getOperator(wherePrinted))
								.append("o.TCkJob.TCoreAccnByJobCoAccn.accnName LIKE :jobCoAccnName");
						wherePrinted = true;
					}
				}
				
				Optional<CoreAccn> opCoreAccnByJobFfAccn = opCkJob.map(CkJob::getTCoreAccnByJobFfAccn);
				if (opCoreAccnByJobFfAccn.isPresent()) {
					if (StringUtils.isNotBlank(opCoreAccnByJobFfAccn.get().getAccnId())) {
						searchStatement.append(getOperator(wherePrinted))
								.append("o.TCkJob.TCoreAccnByJobFfAccn.accnId = :jobFfAccnId");
						wherePrinted = true;
					}

					if (StringUtils.isNotBlank(opCoreAccnByJobFfAccn.get().getAccnName())) {
						searchStatement.append(getOperator(wherePrinted))
								.append("o.TCkJob.TCoreAccnByJobFfAccn.accnName LIKE :jobFfAccnName");
						wherePrinted = true;
					}
				}
				
				Optional<CoreAccn> opCoreAccnByJobToAccn = opCkJob.map(CkJob::getTCoreAccnByJobToAccn);
				if (opCoreAccnByJobToAccn.isPresent()) {
					if (StringUtils.isNotBlank(opCoreAccnByJobToAccn.get().getAccnId())) {
						searchStatement.append(getOperator(wherePrinted))
								.append("o.TCkJob.TCoreAccnByJobToAccn.accnId = :jobToAccnId");
						wherePrinted = true;
					}

					if (StringUtils.isNotBlank(opCoreAccnByJobToAccn.get().getAccnName())) {
						searchStatement.append(getOperator(wherePrinted))
								.append("o.TCkJob.TCoreAccnByJobToAccn.accnName LIKE :jobToAccnName");
						wherePrinted = true;
					}
				}

				Optional<CkRecordDate> opCkRecordDate = Optional.of(dto.getTCkJob()).map(CkJob::getTCkRecordDate);
				if (opCkRecordDate.isPresent()) {
					if (opCkRecordDate.get().getRcdDtSubmit() != null) {
						searchStatement.append(getOperator(wherePrinted))
								.append("DATE_FORMAT(o.TCkJob.TCkRecordDate.rcdDtSubmit,'%d/%m/%Y') = :rcdDtSubmit");
						wherePrinted = true;
					}
				}

				Optional<CkMstJobState> opCkJobMstState = Optional.of(dto.getTCkJob()).map(CkJob::getTCkMstJobState);
				if (opCkJobMstState.isPresent()) {
					if (StringUtils.isNotBlank(opCkJobMstState.get().getJbstId())) {
						searchStatement.append(getOperator(wherePrinted))
								.append("o.TCkJob.TCkMstJobState.jbstId = :jobState");
						wherePrinted = true;
					}
				}
				
				// Added filter for CkMstJobType
				Optional<CkMstJobType> opJobType = opCkJob.map(CkJob::getTCkMstJobType);
				if (opJobType.isPresent()) {
					if (StringUtils.isNotBlank(opJobType.get().getJbtId())) {
						searchStatement.append(getOperator(wherePrinted))
								.append("o.TCkJob.TCkMstJobType.jbtId = :jbtId");
						wherePrinted = true;
					}
					if (StringUtils.isNotBlank(opJobType.get().getJbtName())) {
						searchStatement.append(getOperator(wherePrinted))
								.append("o.TCkJob.TCkMstJobType.jbtName LIKE :jbtName");
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

			if (StringUtils.isNotBlank(dto.getAcknowledgedBy())) {
				// verified by and date
				searchStatement.append(getOperator(wherePrinted))
						.append("o.TCkJob.TCkRecordDate.rcdUidBillAcknowledged LIKE :acknowledgedBy");
				wherePrinted = true;
			}

			Optional<Date> opVerifiedDate = Optional.ofNullable(dto.getAcknowledgedDate());
			if (opVerifiedDate.isPresent() && null != opVerifiedDate.get()) {
				searchStatement.append(getOperator(wherePrinted)
						+ "DATE_FORMAT(o.TCkJob.TCkRecordDate.rcdDtBillAcknowledged,'%d/%m/%Y') = :acknowledgedDate");
				wherePrinted = true;
			}

			if (StringUtils.isNotBlank(dto.getPaymentState())) {
				searchStatement.append(getOperator(wherePrinted)).append("o.jobInPaymentState IN (:paymentState)");
				wherePrinted = true;
			}

			Optional<Date> opJobDtLupd = Optional.ofNullable(dto.getJobDtLupd());
			if (opJobDtLupd.isPresent() && null != opJobDtLupd.get()) {
				searchStatement.append(getOperator(wherePrinted) + "DATE_FORMAT(o.jobDtLupd,'%d/%m/%Y') = :jobDtLupd");
				wherePrinted = true;
			}

			// add jobnotrip filter by
			if (dto.getJobNoTrips() != null && dto.getJobNoTrips() > 0) {
				searchStatement.append(getOperator(wherePrinted)).append("o.jobNoTrips =  :jobNoTripsFilter");
				wherePrinted = true;
			}

			// add charges filter by
			if (dto.getJobTotalCharge() != null && dto.getJobTotalCharge().compareTo(BigDecimal.ZERO) > 0) {
				searchStatement.append(getOperator(wherePrinted)).append("o.jobTotalCharge =  :jobTotalChargeFilter");
				wherePrinted = true;
			}

			// add reimbursement filter by
			if (dto.getJobTotalReimbursements() != null) {
				searchStatement.append(getOperator(wherePrinted))
						.append("o.jobTotalReimbursements = :jobTotalReimbursementFilter");
				wherePrinted = true;
			}

			// add billing date filter by
			if (dto.getBillingDate() != null) {
				searchStatement.append(getOperator(wherePrinted)
						+ "DATE_FORMAT(o.TCkJob.TCkRecordDate.rcdDtBilled,'%d/%m/%Y') = :opBillingDate");
				wherePrinted = true;
			}

			// add payment due date
			if (dto.getPaymentDueDate() != null) {
				if (isServiceProvider) {
					searchStatement.append(getOperator(wherePrinted)
							+ "DATE_FORMAT(o.jobOutPaymentDtDue,'%d/%m/%Y') = :opPaymentDueDate");
				} else {
					searchStatement.append(getOperator(wherePrinted)
							+ "DATE_FORMAT(o.jobInPaymentDtDue,'%d/%m/%Y') = :opPaymentDueDate");
				}

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
	protected HashMap<String, Object> getParameters(CkJobTruckAcknowledged dto)
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

			// Account Type Freight Forwarder
			if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_FF.name())
					|| accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_CO.name())
					|| accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_TO.name())) {

				parameters.put("accnId", accn.getAccnId());

				if (dto.getHistory() != null && dto.getHistory().equalsIgnoreCase(HISTORY)) {

					parameters.put("jobTruckStates", Arrays.asList(JobStates.APP_BILL.name()));
					parameters.put("inPaymentState", Arrays.asList(JobPaymentStates.NEW.name()));

				} else {
					parameters.put("jobTruckStates", Arrays.asList(JobStates.ACK_BILL.name()));
				}
			}
			// Account Type SP
			else if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_SP.name())) {
				if (dto.getHistory() != null && dto.getHistory().equalsIgnoreCase(HISTORY)) {
					parameters.put("jobTruckStates", Arrays.asList(JobStates.APP_BILL.name()));
				} else {
					parameters.put("jobTruckStates", Arrays.asList(JobStates.ACK_BILL.name()));
				}
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
					if (opCkRecordDate.get().getRcdDtSubmit() != null)
						parameters.put("rcdDtSubmit", sdfDate.format(opCkRecordDate.get().getRcdDtSubmit()));
				}

				Optional<CkMstJobState> opCkJobMstState = Optional.of(dto.getTCkJob()).map(CkJob::getTCkMstJobState);
				if (opCkJobMstState.isPresent()) {
					if (StringUtils.isNotBlank(opCkJobMstState.get().getJbstId()))
						parameters.put("jobState", opCkJobMstState.get().getJbstId());

				}

				Optional<CoreAccn> opInvoiceToAccn = Optional.of(dto.getTCkJob())
						.map(CkJob::getTCoreAccnByJobOwnerAccn);
				if (opInvoiceToAccn.isPresent()) {
					if (StringUtils.isNotBlank(opInvoiceToAccn.get().getAccnId())) {
						parameters.put("coFFAccnId", opInvoiceToAccn.get().getAccnId());
					}
					if (StringUtils.isNotBlank(opInvoiceToAccn.get().getAccnName())) {
						parameters.put("coFFAccnName", "%" + opInvoiceToAccn.get().getAccnName() + "%");
					}
				}
				
				Optional<CoreAccn> opJobCoAccn = Optional.of(dto.getTCkJob()).map(CkJob::getTCoreAccnByJobCoAccn);
				if (opJobCoAccn.isPresent()) {
					if (StringUtils.isNotBlank(opJobCoAccn.get().getAccnId())) {
						parameters.put("jobCoAccnId", opJobCoAccn.get().getAccnId());
					}
					if (StringUtils.isNotBlank(opJobCoAccn.get().getAccnName())) {
						parameters.put("jobCoAccnName", "%" + opJobCoAccn.get().getAccnName() + "%");
					}
				}
				
				Optional<CoreAccn> opJobFfAccn = Optional.of(dto.getTCkJob()).map(CkJob::getTCoreAccnByJobFfAccn);
				if (opJobFfAccn.isPresent()) {
					if (StringUtils.isNotBlank(opJobFfAccn.get().getAccnId())) {
						parameters.put("jobFfAccnId", opJobFfAccn.get().getAccnId());
					}
					if (StringUtils.isNotBlank(opInvoiceToAccn.get().getAccnName())) {
						parameters.put("jobFFAccnName", "%" + opJobFfAccn.get().getAccnName() + "%");
					}
				}
				
				Optional<CoreAccn> opJobToAccn = Optional.of(dto.getTCkJob()).map(CkJob::getTCoreAccnByJobToAccn);
				if (opJobToAccn.isPresent()) {
					if (StringUtils.isNotBlank(opJobToAccn.get().getAccnId())) {
						parameters.put("jobToAccnId", opJobToAccn.get().getAccnId());
					}
					if (StringUtils.isNotBlank(opJobToAccn.get().getAccnName())) {
						parameters.put("jobToAccnName", "%" + opJobToAccn.get().getAccnName() + "%");
					}
				}
				
				// Added filter for CkMstJobType
				Optional<CkMstJobType> opJobTypeType = opCkJob.map(CkJob::getTCkMstJobType);
				if (opJobTypeType.isPresent()) {
					if (StringUtils.isNotBlank(opJobTypeType.get().getJbtId()))
						parameters.put("jbtId", opJobTypeType.get().getJbtId());
					if (StringUtils.isNotBlank(opJobTypeType.get().getJbtName()))
						parameters.put("jbtName", "%" + opJobTypeType.get().getJbtName() + "%");
				}
			}

			Optional<CoreAccn> opInvoiceFromAccn = Optional.of(dto.getInvoiceFromAccn());
			if (opInvoiceFromAccn.isPresent()) {
				if (StringUtils.isNotBlank(opInvoiceFromAccn.get().getAccnId())) {
					parameters.put("toAccnId", opInvoiceFromAccn.get().getAccnId());
				}
				if (StringUtils.isNotBlank(opInvoiceFromAccn.get().getAccnName())) {
					parameters.put("toAccnName", "%" + opInvoiceFromAccn.get().getAccnName() + "%");
				}
			}

			Optional<Date> opBillingDate = Optional.ofNullable(dto.getBillingDate());
			if (opBillingDate.isPresent() && null != opBillingDate.get()) {
				parameters.put("opBillingDate", sdfDate.format(opBillingDate.get()));

			}

			if (StringUtils.isNotBlank(dto.getAcknowledgedBy())) {
				parameters.put("acknowledgedBy", dto.getAcknowledgedBy());
			}

			Optional<Date> opAcknowledgedDate = Optional.ofNullable(dto.getAcknowledgedDate());
			if (opAcknowledgedDate.isPresent() && null != opAcknowledgedDate.get()) {
				parameters.put("acknowledgedDate", sdfDate.format(opAcknowledgedDate.get()));
			}

			if (StringUtils.isNotBlank(dto.getPaymentState())) {
				parameters.put("paymentState", dto.getPaymentState());
			}

			Optional<Date> opJobDtLupd = Optional.ofNullable(dto.getJobDtLupd());
			if (opJobDtLupd.isPresent() && null != opJobDtLupd.get())
				parameters.put("jobDtLupd", sdfDate.format(opJobDtLupd.get()));

			// add jobnotripsfilter
			if (dto.getJobNoTrips() != null && dto.getJobNoTrips() > 0) {
				parameters.put("jobNoTripsFilter", dto.getJobNoTrips());
			}

			// add jobtotalcharge filter
			if (dto.getJobTotalCharge() != null && dto.getJobTotalCharge().compareTo(BigDecimal.ZERO) > 0) {
				parameters.put("jobTotalChargeFilter", dto.getJobTotalCharge());
			}

			// add jobReimbursement filter
			if (dto.getJobTotalReimbursements() != null) {
				parameters.put("jobTotalReimbursementFilter", dto.getJobTotalReimbursements());
			}

			// add billing date filter by
			if (dto.getBillingDate() != null)
				parameters.put("opBillingDate", sdfDate.format(dto.getBillingDate()));

			// add payment due date
			if (dto.getPaymentDueDate() != null)
				parameters.put("opPaymentDueDate", sdfDate.format(dto.getPaymentDueDate()));

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
	protected CkJobTruckAcknowledged whereDto(EntityFilterRequest filterRequest)
			throws ParameterException, ProcessingException {
		LOG.debug("whereDto");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");

			CkJobTruckAcknowledged dto = new CkJobTruckAcknowledged();
			CkJob ckJob = new CkJob();
			CkRecordDate recordDate = new CkRecordDate();
			CkMstJobState ckMstState = new CkMstJobState();
			CkMstShipmentType ckMstShpType = new CkMstShipmentType();
			CkMstJobType ckMstJobType = new CkMstJobType();

			CoreAccn coreAccnByJobCoAccn = new CoreAccn();
			CoreAccn coreAccnByJobFfAccn = new CoreAccn();
			CoreAccn coreAccnByJobToAccn = new CoreAccn();
			
			CoreAccn invFromAccn = new CoreAccn();
			CoreAccn invToAccn = new CoreAccn();

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

				if (entityWhere.getAttribute().equalsIgnoreCase("TCkJob.TCoreAccnByJobOwnerAccn.accnId"))
					invToAccn.setAccnId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCkJob.TCoreAccnByJobOwnerAccn.accnName"))
					invToAccn.setAccnName(opValue.get());
				
				if (entityWhere.getAttribute().equalsIgnoreCase("TCkJob.TCoreAccnByJobCoAccn.accnId"))
					coreAccnByJobCoAccn.setAccnId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCkJob.TCoreAccnByJobCoAccn.accnName"))
					coreAccnByJobCoAccn.setAccnName(opValue.get());
				
				if (entityWhere.getAttribute().equalsIgnoreCase("TCkJob.TCoreAccnByJobFfAccn.accnId"))
					coreAccnByJobFfAccn.setAccnId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCkJob.TCoreAccnByJobFfAccn.accnName"))
					coreAccnByJobFfAccn.setAccnName(opValue.get());
				
				if (entityWhere.getAttribute().equalsIgnoreCase("TCkJob.TCoreAccnByJobToAccn.accnId"))
					coreAccnByJobToAccn.setAccnId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCkJob.TCoreAccnByJobToAccn.accnName"))
					coreAccnByJobToAccn.setAccnName(opValue.get());
				
				if (entityWhere.getAttribute().equalsIgnoreCase("jobDtLupd"))
					dto.setJobDtLupd(sdfDate.parse(opValue.get()));

				if (entityWhere.getAttribute().equalsIgnoreCase("acknowledgedDate"))
					dto.setAcknowledgedDate(sdfDate.parse(opValue.get()));

				if (entityWhere.getAttribute().equalsIgnoreCase("acknowledgedBy"))
					dto.setAcknowledgedBy(opValue.get());

				// filterby jobNoTrips
				if (entityWhere.getAttribute().equalsIgnoreCase("jobNoTrips"))
					dto.setJobNoTrips(Short.valueOf(opValue.get()));

				// filterby Job total charge
				if (entityWhere.getAttribute().equalsIgnoreCase("jobTotalCharge")) {
					BigDecimal charge = new BigDecimal(opValue.get());
					dto.setJobTotalCharge(charge);
				}

				// filterby job reimbursement
				if (entityWhere.getAttribute().equalsIgnoreCase("jobTotalReimbursements")) {
					BigDecimal reimbursement = new BigDecimal(opValue.get());
					dto.setJobTotalReimbursements(reimbursement);
				}

				// To filter Payment State (jobOutPaymentState)
				if (entityWhere.getAttribute().equalsIgnoreCase("jobOutPaymentState")) {
					dto.setJobOutPaymentState(opValue.get());
					dto.setPaymentState(opValue.get());
				}
				// To filter Payment State (jobInPaymentState)
				if (entityWhere.getAttribute().equalsIgnoreCase("jobInPaymentState")) {
					dto.setJobInPaymentState(opValue.get());
					dto.setPaymentState(opValue.get());
				}

				if (entityWhere.getAttribute().equalsIgnoreCase("billingDate")) {
					dto.setBillingDate(sdfDate.parse(opValue.get()));
				}

				if (entityWhere.getAttribute().equalsIgnoreCase("paymentDueDate")) {
					dto.setPaymentDueDate(sdfDate.parse(opValue.get()));

				}

				// Added filter for CkMstJobType
				if (entityWhere.getAttribute().equalsIgnoreCase("TCkJob.TCkMstJobType.jbtId"))
					ckMstJobType.setJbtId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCkJob.TCkMstJobType.jbtName"))
					ckMstJobType.setJbtName(opValue.get());
				
				// history toggle
				if (entityWhere.getAttribute().equalsIgnoreCase(HISTORY)) {
					dto.setHistory(opValue.get());
				} else if (entityWhere.getAttribute().equalsIgnoreCase(DEFAULT)) {
					dto.setHistory(opValue.get());
				}
			}

			ckJob.setTCkMstShipmentType(ckMstShpType);
			ckJob.setTCoreAccnByJobOwnerAccn(invToAccn);
			ckJob.setTCkRecordDate(recordDate);
			ckJob.setTCkMstJobState(ckMstState);
			ckJob.setTCkMstJobType(ckMstJobType);
			ckJob.setTCoreAccnByJobCoAccn(coreAccnByJobCoAccn);
			ckJob.setTCoreAccnByJobFfAccn(coreAccnByJobFfAccn);
			ckJob.setTCoreAccnByJobToAccn(coreAccnByJobToAccn);
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

		if (StringUtils.contains(newAttr, "tCoreAccnByJobOwnerAccn"))
			newAttr = newAttr.replace("tCoreAccnByJobOwnerAccn", "TCkJob.TCoreAccnByJobOwnerAccn.accnName");

		if (StringUtils.contains(newAttr, "billingDate")) {
			newAttr = newAttr.replace("billingDate", "TCkJob.TCkRecordDate.rcdDtBilled");
		}

		if (StringUtils.contains(newAttr, "invoiceFromAccn"))
			newAttr = newAttr.replace("invoiceFromAccn", "TCoreAccnByJobPartyTo");

		if (StringUtils.contains(newAttr, "tckJob"))
			newAttr = newAttr.replace("tckJob", "TCkJob");

		if (StringUtils.contains(newAttr, "tckMstShipmentType"))
			newAttr = newAttr.replace("tckMstShipmentType", "TCkMstShipmentType");

		if (StringUtils.contains(newAttr, "tckMstJobType"))
			newAttr = newAttr.replace("tckMstJobType", "TCkMstJobType");
		
		if (StringUtils.contains(newAttr, "tcoreAccnByJobCoAccn"))
			newAttr = newAttr.replace("tcoreAccnByJobCoAccn", "TCoreAccnByJobCoAccn");
		
		if (StringUtils.contains(newAttr, "tcoreAccnByJobFfAccn"))
			newAttr = newAttr.replace("tcoreAccnByJobFfAccn", "TCoreAccnByJobFfAccn");
		
		if (StringUtils.contains(newAttr, "tcoreAccnByJobToAccn"))
			newAttr = newAttr.replace("tcoreAccnByJobToAccn", "TCoreAccnByJobToAccn");
		
		if (StringUtils.contains(newAttr, "tckRecordDate"))
			newAttr = newAttr.replace("tckRecordDate", "TCkRecordDate");

		if (StringUtils.contains(newAttr, "tckMstJobState"))
			newAttr = newAttr.replace("tckMstJobState", "TCkMstJobState");

		Optional<MstAccnType> opAccnType = Optional
				.ofNullable(principalUtilService.getPrincipal().getCoreAccn().getTMstAccnType());
		if (StringUtils.contains(newAttr, "paymentDueDate")) {
			if (opAccnType.isPresent()
					&& opAccnType.get().getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_SP.name())) {
				newAttr = newAttr.replace("paymentDueDate", "jobOutPaymentDtDue");
			} else {
				newAttr = newAttr.replace("paymentDueDate", "jobInPaymentDtDue");
			}
		}

		if (StringUtils.contains(newAttr, "billingDate")) {
			newAttr = newAttr.replace("billingDate", "TCkJob.TCkRecordDate.rcdDtBilled");
		}

		if (StringUtils.contains(newAttr, "acknowledgedDate"))
			newAttr = newAttr.replace("acknowledgedDate", "TCkJob.TCkRecordDate.rcdDtBillAcknowledged");

		if (StringUtils.contains(newAttr, "acknowledgedBy"))
			newAttr = newAttr.replace("acknowledgedBy", "TCkJob.TCkRecordDate.rcdUidBillAcknowledged");
		return newAttr;
	}

}
