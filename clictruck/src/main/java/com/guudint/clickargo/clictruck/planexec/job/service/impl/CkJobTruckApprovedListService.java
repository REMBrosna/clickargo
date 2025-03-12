package com.guudint.clickargo.clictruck.planexec.job.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.guudint.clickargo.admin.service.util.ClickargoAccnService;
import com.guudint.clickargo.clictruck.finacing.constant.FinancingConstants.FinancingTypes;
import com.guudint.clickargo.clictruck.finacing.dto.JobPaymentStates;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruckApproved;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.common.dto.CkRecordDate;
import com.guudint.clickargo.common.model.TCkRecordDate;
import com.guudint.clickargo.common.service.AbstractCkListingService;
import com.guudint.clickargo.common.service.ICkCsAccnService;
import com.guudint.clickargo.job.dto.CkJob;
import com.guudint.clickargo.job.model.TCkJob;
import com.guudint.clickargo.master.dto.CkMstJobState;
import com.guudint.clickargo.master.dto.CkMstJobType;
import com.guudint.clickargo.master.dto.CkMstShipmentType;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.JobStates;
import com.guudint.clickargo.master.enums.Roles;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.master.dto.MstAccnType;

public class CkJobTruckApprovedListService extends AbstractCkListingService<TCkJobTruck, String, CkJobTruckApproved> {

	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(CkJobTruckApprovedListService.class);
	private static String AUDIT_TAG = "CK JOB TRUCK";
	private static String TABLE_NAME = "T_CK_JOB_TRUCK";
	private static String HISTORY = "history";
	private static String DEFAULT = "default";

	@Autowired
	private CkJobTruckServiceUtil ckJobTruckUtilService;

	@Autowired
	protected ClickargoAccnService clickargoAccnService;

	@Autowired
	protected ICkCsAccnService ckCsAccnService;

	public CkJobTruckApprovedListService() {
		super("ckJobTruckDao", AUDIT_TAG, TCkJobTruck.class.getName(), TABLE_NAME);

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkJobTruckApproved> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkJobTruckApproved dto = this.whereDto(filterRequest);
			if (null == dto)
				throw new ProcessingException("whereDto null");

			// This is use for customer service inbound; only retrieve if it's true
			if (dto.isForInboundCs()) {
				Principal principal = principalUtilService.getPrincipal();
				List<CoreAccn> accnsForCs = ckCsAccnService.getAssociatedAccounts(principal.getUserId(),
						ServiceTypes.CLICTRUCK);
				dto.setAccnForCsList(accnsForCs);
				// return if no accounts for cs
				if (accnsForCs != null && accnsForCs.isEmpty())
					return new ArrayList<CkJobTruckApproved>();
			}

			filterRequest.setTotalRecords(super.countByAnd(dto));
			String selectClause = "FROM TCkJobTruck o ";
			String orderByClauses = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkJobTruck> entities = this.findEntitiesByAnd(dto, selectClause, orderByClauses,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			List<CkJobTruckApproved> dtos = entities.stream().map(x -> {
				try {
					return dtoFromEntity(x, dto.isForInboundCs());
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
	protected CkJobTruckApproved dtoFromEntity(TCkJobTruck entity) throws ParameterException, ProcessingException {
		try {
			if (null == entity)
				throw new ParameterException("param entity null");

			CkJobTruckApproved dto = new CkJobTruckApproved(entity);
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
					// previously the approve date is stored in this column
					if (opRecordDate.get().getRcdDtApproved() == null) {
						// if none, get it from acknowledged date, new workflow added
						if (opRecordDate.get().getRcdUidBillAcknowledged() == null) {
							// but if acknowledged date is null, get it from bill approved
							dto.setBillingDate(opRecordDate.get().getRcdDtBillApproved());
						} else {
							dto.setBillingDate(opRecordDate.get().getRcdDtBillAcknowledged());
						}
					} else {
						dto.setBillingDate(opRecordDate.get().getRcdDtApproved());
					}

					// 20230905 Nina this is a new workflow added, thus is the ack column is empty,
					// set it to the bill approved date
					dto.setAcknowledgedBy(opRecordDate.get().getRcdUidBillAcknowledged() == null
							? opRecordDate.get().getRcdUidBillApproved()
							: opRecordDate.get().getRcdUidBillAcknowledged());
					dto.setAcknowledgedDate(opRecordDate.get().getRcdDtBillAcknowledged() == null
							? opRecordDate.get().getRcdDtBillApproved()
							: opRecordDate.get().getRcdDtBillAcknowledged());

					dto.setApprovedBy(
							opRecordDate.get().getRcdUidApproved() == null ? opRecordDate.get().getRcdUidBillApproved()
									: opRecordDate.get().getRcdUidApproved());
					dto.setApprovedDate(
							opRecordDate.get().getRcdDtApproved() == null ? opRecordDate.get().getRcdDtBillApproved()
									: opRecordDate.get().getRcdDtApproved());
				}

				Optional<MstAccnType> opAccnType = Optional
						.ofNullable(principalUtilService.getPrincipal().getCoreAccn().getTMstAccnType());
				if (opAccnType.isPresent()
						&& opAccnType.get().getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_SP.name())) {

					boolean isSpOpAdmin = principalUtilService.getPrincipal().getRoleList().stream()
							.anyMatch(e -> Arrays.asList(Roles.SP_OP_ADMIN.name()).contains(e));
					if (isSpOpAdmin && dto.isForInboundCs()) {
						// for SP OP Admin and for inbound payment
						dto.setPaymentDueDate(dto.getJobInPaymentDtDue());
					} else {
						dto.setPaymentDueDate(dto.getJobOutPaymentDtDue());
					}

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

	/**
	 * Overloaded to pass forInboundCs value from dto.
	 **/
	protected CkJobTruckApproved dtoFromEntity(TCkJobTruck entity, boolean isForInboundCs)
			throws ParameterException, ProcessingException {
		try {
			if (null == entity)
				throw new ParameterException("param entity null");

			CkJobTruckApproved dto = new CkJobTruckApproved(entity);
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

					// previously the approve date is stored in this column
					if (opRecordDate.get().getRcdDtApproved() == null) {
						// if none, get it from acknowledged date, new workflow added
						if (opRecordDate.get().getRcdUidBillAcknowledged() == null) {
							// but if acknowledged date is null, get it from bill approved
							dto.setBillingDate(opRecordDate.get().getRcdDtBillApproved());
						} else {
							dto.setBillingDate(opRecordDate.get().getRcdDtBillAcknowledged());
						}
					} else {
						dto.setBillingDate(opRecordDate.get().getRcdDtApproved());
					}

					// 20230905 Nina this is a new workflow added, thus is the ack column is empty,
					// set it to the bill approved date
					dto.setAcknowledgedBy(opRecordDate.get().getRcdUidBillAcknowledged() == null
							? opRecordDate.get().getRcdUidBillApproved()
							: opRecordDate.get().getRcdUidBillAcknowledged());
					dto.setAcknowledgedDate(opRecordDate.get().getRcdDtBillAcknowledged() == null
							? opRecordDate.get().getRcdDtBillApproved()
							: opRecordDate.get().getRcdDtBillAcknowledged());

					dto.setApprovedBy(
							opRecordDate.get().getRcdUidApproved() == null ? opRecordDate.get().getRcdUidBillApproved()
									: opRecordDate.get().getRcdUidApproved());
					dto.setApprovedDate(
							opRecordDate.get().getRcdDtApproved() == null ? opRecordDate.get().getRcdDtBillApproved()
									: opRecordDate.get().getRcdDtApproved());
				}

				Optional<MstAccnType> opAccnType = Optional
						.ofNullable(principalUtilService.getPrincipal().getCoreAccn().getTMstAccnType());
				if (opAccnType.isPresent()
						&& opAccnType.get().getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_SP.name())) {

					boolean isSpOpAdmin = principalUtilService.getPrincipal().getRoleList().stream()
							.anyMatch(e -> Arrays.asList(Roles.SP_OP_ADMIN.name()).contains(e));
					if (isSpOpAdmin && dto.isForInboundCs()) {
						// for SP OP Admin and for inbound payment
						dto.setPaymentDueDate(dto.getJobInPaymentDtDue());
					} else {
						dto.setPaymentDueDate(dto.getJobOutPaymentDtDue());
					}

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
	protected String getWhereClause(CkJobTruckApproved dto, boolean wherePrinted)
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

				// PAYMENT STATE
				searchStatement.append(getOperator(wherePrinted)).append("o.jobInPaymentState IN (:inPaymentState)");
				wherePrinted = true;

			} else if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_TO.name())) {
				// For Truck Operators
				searchStatement.append(getOperator(wherePrinted)).append("o.TCoreAccnByJobPartyTo.accnId = :accnId");
				wherePrinted = true;
				searchStatement.append(getOperator(wherePrinted))
						.append("o.TCkJob.TCkMstJobState.jbstId IN :jobTruckStates");
				wherePrinted = true;
			} else if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_SP.name())) {
				// 20230905 Nina Added checking of role SP_OP_ADMIN to display the inbound
				// payment and if forCS = true
				boolean isSpOpAdmin = principal.getRoleList().stream()
						.anyMatch(e -> Arrays.asList(Roles.SP_OP_ADMIN.name()).contains(e));
				if (isSpOpAdmin && dto.isForInboundCs()) {

					// Check if there is accounts associated to view inbound payment before proceed
					if (dto.getAccnForCsList() != null && dto.getAccnForCsList().size() > 0) {
						searchStatement.append(getOperator(wherePrinted))
								.append("o.TCkJob.TCkMstJobState.jbstId IN :jobTruckStates");
						wherePrinted = true;

						searchStatement.append(getOperator(wherePrinted))
								.append("o.TCoreAccnByJobPartyCoFf.accnId IN (:coFfAccnIds)");
						wherePrinted = true;

						searchStatement.append(getOperator(wherePrinted))
								.append("o.jobInPaymentState IN :inPaymentState");
						wherePrinted = true;
					}

				} else {
					isServiceProvider = true;
					searchStatement.append(getOperator(wherePrinted))
							.append("o.TCkJob.TCkMstJobState.jbstId IN :jobTruckStates");
					wherePrinted = true;

					if (dto.getHistory() != null && dto.getHistory().equalsIgnoreCase(HISTORY)) {
						searchStatement.append(getOperator(wherePrinted))
								.append("o.jobInPaymentState IN :inPaymentStates");
						wherePrinted = true;
					}

					searchStatement.append(getOperator(wherePrinted))
							.append("o.jobOutPaymentState IN :outPaymentStates");
					wherePrinted = true;
				}

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

			if (StringUtils.isNotBlank(dto.getApprovedBy())) {
				// verified by and date
				searchStatement.append(getOperator(wherePrinted))
						.append("o.TCkJob.TCkRecordDate.rcdUidApproved LIKE :approvedBy");
				wherePrinted = true;
			}

			Optional<Date> opApprovedDt = Optional.ofNullable(dto.getApprovedDate());
			if (opApprovedDt.isPresent() && null != opApprovedDt.get()) {
				searchStatement.append(getOperator(wherePrinted)
						+ "DATE_FORMAT(o.TCkJob.TCkRecordDate.rcdDtApproved,'%d/%m/%Y') = :approvedDate");
				wherePrinted = true;
			}

			// Acknowledged Date/By
			if (StringUtils.isNotBlank(dto.getAcknowledgedBy())) {
				// verified by and date
				searchStatement.append(getOperator(wherePrinted))
						.append("o.TCkJob.TCkRecordDate.rcdUidBillAcknowledged LIKE :acknowledgedBy");
				wherePrinted = true;
			}

			Optional<Date> opAckDate = Optional.ofNullable(dto.getAcknowledgedDate());
			if (opAckDate.isPresent() && null != opAckDate.get()) {
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

			if (dto.isOpmOnly()) {
				searchStatement.append(getOperator(wherePrinted) + "jobFinanceOpt IN :jobFinanceOpt");
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
	protected HashMap<String, Object> getParameters(CkJobTruckApproved dto)
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

//			parameters.put("accnId", accn.getAccnId());
			// Account Type Freight Forwarder
			if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_FF.name())
					|| accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_CO.name())
					|| accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_TO.name())) {

				parameters.put("accnId", accn.getAccnId());

				if (dto.getHistory() != null && dto.getHistory().equalsIgnoreCase(HISTORY)) {

					parameters.put("jobTruckStates", Arrays.asList(JobStates.APP_BILL.name()));
					parameters.put("inPaymentState",
							Arrays.asList(JobPaymentStates.PAYING.name(), JobPaymentStates.PAID.name()));

				} else {
					parameters.put("jobTruckStates", Arrays.asList(JobStates.APP_BILL.name()));
					parameters.put("inPaymentState", Arrays.asList(JobPaymentStates.NEW.name()));
				}
			}
			// Account Type SP
			else if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_SP.name())) {

				// 20230905 Nina Added checking of role SP_OP_ADMIN to display the inbound
				// payment and if forCS = true
				boolean isSpOpAdmin = principal.getRoleList().stream()
						.anyMatch(e -> Arrays.asList(Roles.SP_OP_ADMIN.name()).contains(e));
				if (isSpOpAdmin && dto.isForInboundCs()) {
					// Check if there is accounts associated to view inbound payment before proceed
					if (dto.getAccnForCsList() != null && dto.getAccnForCsList().size() > 0) {
						parameters.put("jobTruckStates", Arrays.asList(JobStates.APP_BILL.name()));
						parameters.put("coFfAccnIds",
								dto.getAccnForCsList().stream().map(e -> e.getAccnId()).collect(Collectors.toList()));

						if (dto.getHistory() != null && dto.getHistory().equalsIgnoreCase(HISTORY)) {
							parameters.put("inPaymentState",
									Arrays.asList(JobPaymentStates.PAYING.name(), JobPaymentStates.PAID.name()));
						} else {
							parameters.put("inPaymentState", Arrays.asList(JobPaymentStates.NEW.name()));
						}

					}

				} else {
					if (dto.getHistory() != null && dto.getHistory().equalsIgnoreCase(HISTORY)) {
						parameters.put("jobTruckStates", Arrays.asList(JobStates.APP_BILL.name()));
						parameters.put("inPaymentStates",
								Arrays.asList(JobPaymentStates.PAID.name(), JobPaymentStates.PAYING.name()));
						parameters.put("outPaymentStates", Arrays.asList(JobPaymentStates.PAID.name()));
					} else {
						parameters.put("jobTruckStates", Arrays.asList(JobStates.APP_BILL.name()));
						parameters.put("outPaymentStates", Arrays.asList(JobPaymentStates.NEW.name()));
					}
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

			if (StringUtils.isNotBlank(dto.getApprovedBy())) {
				parameters.put("approvedBy", dto.getApprovedBy());
			}

			Optional<Date> opApprovedDate = Optional.ofNullable(dto.getApprovedDate());
			if (opApprovedDate.isPresent() && null != opApprovedDate.get()) {
				parameters.put("approvedDate", sdfDate.format(opApprovedDate.get()));
			}

			if (StringUtils.isNotBlank(dto.getAcknowledgedBy())) {
				parameters.put("acknowledgedBy", dto.getAcknowledgedBy());
			}

			Optional<Date> opAckDate = Optional.ofNullable(dto.getAcknowledgedDate());
			if (opAckDate.isPresent() && null != opAckDate.get()) {
				parameters.put("acknowledgedDate", sdfDate.format(opAckDate.get()));
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

			// filter jobFinanceOpt = OC/OT
			if (dto.isOpmOnly()) {
				parameters.put("jobFinanceOpt", Arrays.asList(FinancingTypes.OC.name(), FinancingTypes.OT.name()));
			}

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
	protected CkJobTruckApproved whereDto(EntityFilterRequest filterRequest)
			throws ParameterException, ProcessingException {
		LOG.debug("whereDto");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");

			CkJobTruckApproved dto = new CkJobTruckApproved();
			CkJob ckJob = new CkJob();
			CkRecordDate recordDate = new CkRecordDate();
			CkMstJobState ckMstState = new CkMstJobState();
			CkMstShipmentType ckMstShpType = new CkMstShipmentType();

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

				if (entityWhere.getAttribute().equalsIgnoreCase("jobDtLupd"))
					dto.setJobDtLupd(sdfDate.parse(opValue.get()));

				if (entityWhere.getAttribute().equalsIgnoreCase("approvedDate"))
					dto.setApprovedDate(sdfDate.parse(opValue.get()));

				if (entityWhere.getAttribute().equalsIgnoreCase("approvedBy"))
					dto.setApprovedBy(opValue.get());

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

				if (entityWhere.getAttribute().equalsIgnoreCase("forInboundCs")) {
					dto.setForInboundCs(Boolean.valueOf(opValue.get()));
				}

				// history toggle
				if (entityWhere.getAttribute().equalsIgnoreCase(HISTORY)) {
					dto.setHistory(opValue.get());
				} else if (entityWhere.getAttribute().equalsIgnoreCase(DEFAULT)) {
					dto.setHistory(opValue.get());
				}

				// for opm filter
				if (entityWhere.getAttribute().equalsIgnoreCase("opmOnly"))
					dto.setOpmOnly(opValue.get().equalsIgnoreCase("Y") ? true : false);
			}

			ckJob.setTCkMstShipmentType(ckMstShpType);
			ckJob.setTCoreAccnByJobOwnerAccn(invToAccn);
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

		if (StringUtils.contains(newAttr, "approvedDate"))
			newAttr = newAttr.replace("approvedDate", "TCkJob.TCkRecordDate.rcdDtApproved");

		if (StringUtils.contains(newAttr, "approvedBy"))
			newAttr = newAttr.replace("approvedBy", "TCkJob.TCkRecordDate.rcdUidApproved");

		if (StringUtils.contains(newAttr, "acknowledgedDate"))
			newAttr = newAttr.replace("acknowledgedDate", "TCkJob.TCkRecordDate.rcdDtBillAcknowledged");

		if (StringUtils.contains(newAttr, "acknowledgedBy"))
			newAttr = newAttr.replace("acknowledgedBy", "TCkJob.TCkRecordDate.rcdUidBillAcknowledged");
		return newAttr;
	}

}
