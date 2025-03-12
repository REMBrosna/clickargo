package com.guudint.clicdo.common.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clicdo.common.DashboardJson;
import com.guudint.clicdo.common.enums.DashboardStatus;
import com.guudint.clicdo.common.enums.DashboardTypes;
import com.guudint.clickargo.clictruck.admin.dao.AccnDao;
import com.guudint.clickargo.clictruck.finacing.dto.JobPaymentStates;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.sage.dao.CkCtSageTaxDao;
import com.guudint.clickargo.clictruck.util.NumberUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.JobStates;
import com.guudint.clickargo.master.enums.Roles;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.guudint.clickargo.payment.enums.PaymentStates;
import com.guudint.clickargo.payment.model.TCkPaymentTxn;
import com.guudint.clickargo.tax.dao.CkTaxReportDao;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.ParameterException;

@Service
public class DashboardService {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(DashboardService.class);

	@Autowired
	@Qualifier("ckJobTruckDao")
	private GenericDao<TCkJobTruck, String> ckJobTruckDao;

	@Autowired
	@Qualifier("ckPaymentTxnDao")
	private GenericDao<TCkPaymentTxn, String> ckPaymentTxnDao;

	@Autowired
	private CkCtSageTaxDao ckCtSageTaxDao;

	@Autowired
	private CkTaxReportDao ckTaxReportDao;

	@Autowired
	AccnDao accnDao;

	private String image = "DO_CLAIM.png";
	private final char TAX_INVOICE_STATUS_EXPORTED = 'E';
	public final char ACCOUNT_SUSPEND = 'S';

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<DashboardJson> getDashboardStats(Principal principal) throws ParameterException, Exception {
		if (null == principal)
			throw new ParameterException("param principal null");

		List<DashboardJson> dashboardBeanList = new ArrayList<>();

		String accnType = Optional.ofNullable(principal.getCoreAccn().getTMstAccnType().getAtypId()).orElse(null);
		boolean isFinance = principal.getRoleList().stream().anyMatch(e -> Arrays
				.asList(Roles.FF_FINANCE.name(), Roles.SP_FIN_ADMIN.name(), Roles.SP_FIN_HD.name()).contains(e));

		// BL, DO Claim, DO Extension, etc
		List<DashboardTypes> dashTypeList = null;
		AccountTypes eAccnType = AccountTypes.valueOf(accnType);
		if (eAccnType != null) {
			switch (eAccnType) {
			case ACC_TYPE_FF:
			case ACC_TYPE_CO:
				dashTypeList = new ArrayList<>();
				dashTypeList.add(DashboardTypes.TRUCK_JOBS);
				dashTypeList.add(DashboardTypes.BILLED_JOBS);

				if (isFinance) {
					dashTypeList.add(DashboardTypes.APPROVED_JOBS);
					dashTypeList.add(DashboardTypes.JOB_PAYMENTS);
				} else {
					dashTypeList.add(DashboardTypes.VERIFIED_JOBS);
				}
				break;
			case ACC_TYPE_TO:
				dashTypeList = new ArrayList<>();
				dashTypeList.add(DashboardTypes.TRUCK_JOBS);
				dashTypeList.add(DashboardTypes.JOB_BILLING);
				if (isFinance) {
					dashTypeList.add(DashboardTypes.JOB_PAYMENTS);
				}

				break;
			case ACC_TYPE_SP:
				if (isFinance) {
					dashTypeList = Arrays.asList(DashboardTypes.APPROVED_JOBS, DashboardTypes.JOB_PAYMENTS);
				} else
					dashTypeList = Arrays.asList(DashboardTypes.TRUCK_JOBS, DashboardTypes.BILLED_JOBS);
				break;
			case ACC_TYPE_FF_CO:
				dashTypeList = new ArrayList<>();
				dashTypeList.add(DashboardTypes.TRUCK_JOBS);
				break;
			default:
				break;
			}
		}

		// Iterate through the dashType and add the status as the key for statistics
		// below
		if (dashTypeList != null) {
			for (DashboardTypes d : dashTypeList) {
				DashboardJson db = new DashboardJson(d.name(), d.getDesc());
				db.setTransStatistic(new HashMap<String, Integer>());
				db.setAccnType(accnType);
				if (d.name().equalsIgnoreCase(DashboardTypes.TRUCK_JOBS.name())) {
					if (StringUtils.equalsIgnoreCase(accnType, AccountTypes.ACC_TYPE_CO.name())
							|| StringUtils.equalsIgnoreCase(accnType, AccountTypes.ACC_TYPE_FF.name())) {
						// for co/ff active - status includes NEW, SUB, ACP, ASG, ONGOING, DLV
						db.getTransStatistic().put(DashboardStatus.ACTIVE.name(), 0);

					} else if (StringUtils.equalsIgnoreCase(accnType, AccountTypes.ACC_TYPE_TO.name())) {
						// for TO,- status includes SUB, ACCEPTED
						db.getTransStatistic().put(DashboardStatus.NEW.name(), 0);
						// for TO - status includes ASSIGNED, ONGOING
						db.getTransStatistic().put(DashboardStatus.ONGOING.name(), 0);
					} else if (StringUtils.equalsIgnoreCase(accnType, AccountTypes.ACC_TYPE_FF_CO.name())) {
						// for TO,- status includes SUB, ACCEPTED
						db.getTransStatistic().put(DashboardStatus.NEW.name(), 0);
						// for TO - status includes ASSIGNED, ONGOING
						db.getTransStatistic().put(DashboardStatus.ONGOING.name(), 0);
					}

				} else if (d.name().equalsIgnoreCase(DashboardTypes.BILLED_JOBS.name())) {
					// bill jobs pending verification/approval - status includes BILLED/VERIFIED
					db.getTransStatistic().put(DashboardStatus.PENDING_VERIFICATION.name(), 0);

				} else if (d.name().equalsIgnoreCase(DashboardTypes.JOB_BILLING.name())) {
					// jobs for billing - status includes DLVRD
					db.getTransStatistic().put(DashboardStatus.SUBMITTED.name(), 0);
					// jobs for billing - status incldues INVAPP
					db.getTransStatistic().put(DashboardStatus.READY.name(), 0);

				} else if (d.name().equalsIgnoreCase(DashboardTypes.VERIFIED_JOBS.name())) {
					db.getTransStatistic().put(DashboardStatus.VERIFIED.name(), 0);

				} else if (d.name().equalsIgnoreCase(DashboardTypes.APPROVED_JOBS.name())) {
					db.getTransStatistic().put(DashboardStatus.APPROVED.name(), 0);

				} else {
					// default statistics
					db.getTransStatistic().put(DashboardStatus.ACTIVE.name(), 0);
				}

				dashboardBeanList.add(db);
			}
		}

		// Go through the dashboardBeanList and update the statistics. The table sources
		// maybe different
		dashboardBeanList.stream().forEach(dbEl -> {
			try {

				// Check if the account type is ff/co, before determining the dashboard type
				if (dbEl.getAccnType().equalsIgnoreCase(AccountTypes.ACC_TYPE_FF.name())
						|| dbEl.getAccnType().equalsIgnoreCase(AccountTypes.ACC_TYPE_CO.name())) {

					doNonFinance(dbEl, principal);

					if (isFinance) {
						doFinance(dbEl, principal);
					}
					// else {
					// doNonFinance(dbEl, principal);
					// }

				} else if (dbEl.getAccnType().equalsIgnoreCase(AccountTypes.ACC_TYPE_SP.name())) {
					if (isFinance)
						doGliFinance(dbEl, principal);

				} else {
					doTruckingOperator(dbEl, principal);
				}

			} catch (Exception ex) {
				log.error("error in statistics", ex);
				ex.printStackTrace();
			}
		});

		if (dashboardBeanList.size() > 0) {
			IntStream.range(0, dashboardBeanList.size()).forEach(idx -> dashboardBeanList.get(idx).setId(idx));
		}

		return dashboardBeanList;

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<DashboardJson> getDocBillingVerificationStats(Principal principal)
			throws ParameterException, Exception {
		if (null == principal)
			throw new ParameterException("param principal null");

		List<DashboardJson> dashboardBeanList = new ArrayList<>();

		boolean isRoleValid = principal.getRoleList().stream().anyMatch(e -> Arrays
				.asList(Roles.SP_OP_ADMIN.name(), Roles.SP_L1.name(), Roles.SP_FIN_ADMIN.name(), Roles.SP_FIN_HD.name())
				.contains(e));

		// BL, DO Claim, DO Extension, etc
		List<DashboardTypes> dashTypeList = new ArrayList<>();
		String accnType = Optional.ofNullable(principal.getCoreAccn().getTMstAccnType().getAtypId()).orElse(null);
		if (isRoleValid)
			dashTypeList.add(DashboardTypes.DOCUMENT_VERIFICATIONS);

		// Iterate through the dashType and add the status as the key for statistics
		// below
		if (dashTypeList != null) {
			for (DashboardTypes d : dashTypeList) {
				DashboardJson db = new DashboardJson(d.name(), d.getDesc());
				db.setTransStatistic(new HashMap<String, Integer>());
				db.setAccnType(accnType);
				if (d.name().equalsIgnoreCase(DashboardTypes.DOCUMENT_VERIFICATIONS.name())) {
					db.getTransStatistic().put(DashboardStatus.PENDING_DOC_VERIFY.name(), 0);
					db.getTransStatistic().put(DashboardStatus.DOC_VERIFIED.name(), 0);
				}

				dashboardBeanList.add(db);
			}
		}

		// Go through the dashboardBeanList and update the statistics. The table sources
		// maybe different
		dashboardBeanList.stream().forEach(dbEl -> {
			try {

				if (dbEl.getAccnType().equalsIgnoreCase(AccountTypes.ACC_TYPE_SP.name())) {
					if (isRoleValid)
						doGliOperationsAdmin(dbEl, principal);
				}
			} catch (Exception ex) {
				log.error("error in statistics", ex);
				ex.printStackTrace();
			}
		});

		if (dashboardBeanList.size() > 0) {
			IntStream.range(0, dashboardBeanList.size()).forEach(idx -> dashboardBeanList.get(idx).setId(idx));
		}

		return dashboardBeanList;

	}

	@SuppressWarnings("rawtypes")
	private void doGliOperationsAdmin(DashboardJson dbEl, Principal principal) throws Exception {
		StringBuilder hqlStat = new StringBuilder(
				"SELECT SUM(CASE WHEN o.TCkJob.TCkMstJobState.jbstId IN (:overStates) THEN 1 ELSE 0 END) as OVER_STATE, ");
		hqlStat.append(
				" SUM(CASE WHEN o.TCkJob.TCkMstJobState.jbstId IN (:underStates) THEN 1 ELSE 0 END) as UNDER_STATE ");
		hqlStat.append(" FROM TCkJobTruck o ");
		hqlStat.append(" WHERE o.jobStatus=:jobStatus")
				.append(" AND o.TCkJob.TCkMstJobState.jbstId in (:includeStates) ");
		Map<String, Object> params = new HashMap<>();
		params.put("jobStatus", RecordStatus.ACTIVE.getCode());

		if (dbEl.getDbType().equalsIgnoreCase(DashboardTypes.DOCUMENT_VERIFICATIONS.name())) {
			params.put("overStates", Arrays.asList(JobStates.ACK_BILL.name()));
			params.put("underStates", Arrays.asList(JobStates.APP_BILL.name()));
			params.put("includeStates", Arrays.asList(JobStates.ACK_BILL.name(), JobStates.APP_BILL.name()));

			List<TCkJobTruck> list = ckJobTruckDao.getByQuery(hqlStat.toString(), params);
			if (list != null && list.size() > 0) {
				Iterator it = list.iterator();
				while (it.hasNext()) {
					Object[] obj = (Object[]) it.next();
					dbEl.getTransStatistic().put(DashboardStatus.PENDING_DOC_VERIFY.name(),
							NumberUtil.toInteger(obj[0]));
					dbEl.getTransStatistic().put(DashboardStatus.DOC_VERIFIED.name(), NumberUtil.toInteger(obj[1]));
				}
			}

		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<DashboardJson> getDashboardStatsTaxModules(Principal principal) throws ParameterException, Exception {
		if (null == principal)
			throw new ParameterException("param principal null");

		List<DashboardJson> dashboardBeanList = new ArrayList<>();

		String accnType = Optional.ofNullable(principal.getCoreAccn().getTMstAccnType().getAtypId()).orElse(null);
		boolean isTax = principal.getRoleList().stream()
				.anyMatch(e -> Arrays.asList(Roles.ADMIN.name(), Roles.SP_FIN_ADMIN.name()).contains(e));

		// BL, DO Claim, DO Extension, etc
		List<DashboardTypes> dashTypeList = null;
		AccountTypes eAccnType = AccountTypes.valueOf(accnType);
		if (eAccnType != null && eAccnType == AccountTypes.ACC_TYPE_SP) {
			if (isTax)
				dashTypeList = Arrays.asList(DashboardTypes.SEQUENCE, DashboardTypes.REPORTS, DashboardTypes.INVOICES);
		}

		// Iterate through the dashType and add the status as the key for statistics
		// below
		if (dashTypeList != null) {
			for (DashboardTypes d : dashTypeList) {
				DashboardJson db = new DashboardJson(d.name(), d.getDesc());
				db.setTransStatistic(new TreeMap<String, Integer>());
				db.setAccnType(accnType);
				if (d.name().equalsIgnoreCase(DashboardTypes.SEQUENCE.name())) {
					db.getTransStatistic().put(DashboardStatus.USED.name(), 0);
					db.getTransStatistic().put(DashboardStatus.TOTAL.name(), 0);
					db.setImage(image);
				} else if (d.name().equalsIgnoreCase(DashboardTypes.REPORTS.name())) {
					db.getTransStatistic().put(DashboardStatus.READY_REPORT.name(), 0);
					db.setImage(image);
				} else if (d.name().equalsIgnoreCase(DashboardTypes.INVOICES.name())) {
					db.getTransStatistic().put(DashboardStatus.WITHOUT_PDF.name(), 0);
					db.setImage(image);

				} else {
					// default statistics
					db.getTransStatistic().put(DashboardStatus.ACTIVE.name(), 0);
				}

				dashboardBeanList.add(db);
			}
		}

		// Go through the dashboardBeanList and update the statistics. The table sources
		// maybe different
		dashboardBeanList.stream().forEach(dbEl -> {
			try {
				if (dbEl.getAccnType().equalsIgnoreCase(AccountTypes.ACC_TYPE_SP.name())) {
					doGliFinanceTaxModules(dbEl, principal);
				}

			} catch (Exception ex) {
				log.error("error in statistics", ex);
				ex.printStackTrace();
			}
		});

		if (dashboardBeanList.size() > 0) {
			IntStream.range(0, dashboardBeanList.size()).forEach(idx -> dashboardBeanList.get(idx).setId(idx));
		}

		return dashboardBeanList;

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<DashboardJson> getDashboardAccountSuspension(Principal principal) throws ParameterException, Exception {
		if (null == principal)
			throw new ParameterException("param principal null");

		List<DashboardJson> dashboardBeanList = new ArrayList<>();

		String accnType = Optional.ofNullable(principal.getCoreAccn().getTMstAccnType().getAtypId()).orElse(null);
		boolean isAccn = principal.getRoleList().stream().anyMatch(e -> Arrays
				.asList(Roles.ADMIN.name(), Roles.SP_FIN_ADMIN.name(), Roles.SP_OP_ADMIN.name()).contains(e));

		// BL, DO Claim, DO Extension, etc
		List<DashboardTypes> dashTypeList = null;
		AccountTypes eAccnType = AccountTypes.valueOf(accnType);
		if (eAccnType != null && eAccnType == AccountTypes.ACC_TYPE_SP) {
			if (isAccn)
				dashTypeList = Arrays.asList(DashboardTypes.SUSPENSION, DashboardTypes.RESUMPTION);
		}

		// Iterate through the dashType and add the status as the key for statistics
		// below
		if (dashTypeList != null) {
			for (DashboardTypes d : dashTypeList) {
				DashboardJson db = new DashboardJson(d.name(), d.getDesc());
				db.setTransStatistic(new TreeMap<String, Integer>());
				db.setAccnType(accnType);
				if (d.name().equalsIgnoreCase(DashboardTypes.SUSPENSION.name())) {
					db.getTransStatistic().put(DashboardStatus.ACTIVE_ACCN.getDesc(), 0);
					db.setImage("REPORTS.png");
				} else if (d.name().equalsIgnoreCase(DashboardTypes.RESUMPTION.name())) {
					db.getTransStatistic().put(DashboardStatus.SUSPEND_ACCN.getDesc(), 0);
					db.setImage("REPORTS.png");
				}

				dashboardBeanList.add(db);
			}
		}

		// Go through the dashboardBeanList and update the statistics. The table sources
		// maybe different
		dashboardBeanList.stream().forEach(dbEl -> {
			try {
				if (dbEl.getAccnType().equalsIgnoreCase(AccountTypes.ACC_TYPE_SP.name())) {
					doGli(dbEl, principal);
				}

			} catch (Exception ex) {
				log.error("error in statistics", ex);
				ex.printStackTrace();
			}
		});

		if (dashboardBeanList.size() > 0) {
			IntStream.range(0, dashboardBeanList.size()).forEach(idx -> dashboardBeanList.get(idx).setId(idx));
		}

		return dashboardBeanList;

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<DashboardJson> getDashboardOutboundPayment(Principal principal) throws ParameterException, Exception {
		if (null == principal)
			throw new ParameterException("param principal null");

		List<DashboardJson> dashboardBeanList = new ArrayList<>();

		String accnType = Optional.ofNullable(principal.getCoreAccn().getTMstAccnType().getAtypId()).orElse(null);
		boolean isAccn = principal.getRoleList().stream()
				.anyMatch(e -> Arrays.asList(Roles.SP_OP_ADMIN.name()).contains(e));

		List<DashboardTypes> dashTypeList = null;
		AccountTypes eAccnType = AccountTypes.valueOf(accnType);
		if (eAccnType != null && eAccnType == AccountTypes.ACC_TYPE_SP) {
			if (isAccn)
				dashTypeList = Arrays.asList(DashboardTypes.APPROVED_JOBS, DashboardTypes.JOB_PAYMENTS);
		}

		if (dashTypeList != null) {
			for (DashboardTypes d : dashTypeList) {
				DashboardJson db = new DashboardJson(d.name(), d.getDesc());
				db.setTransStatistic(new TreeMap<String, Integer>());
				db.setAccnType(accnType);
				dashboardBeanList.add(db);
			}
		}

		dashboardBeanList.stream().forEach(dbEl -> {
			try {
				if (dbEl.getAccnType().equalsIgnoreCase(AccountTypes.ACC_TYPE_SP.name())) {
					if (dbEl.getDbType().equalsIgnoreCase(DashboardTypes.APPROVED_JOBS.name())) {
						doGliFinance(dbEl, principal);
					} else if (dbEl.getDbType().equalsIgnoreCase(DashboardTypes.JOB_PAYMENTS.name())) {
						doJobPaymentsTxn(dbEl, principal);
					}
				}

			} catch (Exception ex) {
				log.error("error in statistics", ex);
				ex.printStackTrace();
			}
		});

		if (dashboardBeanList.size() > 0) {
			IntStream.range(0, dashboardBeanList.size()).forEach(idx -> dashboardBeanList.get(idx).setId(idx));
		}

		return dashboardBeanList;

	}

	// Helper Methods
	private void doFinance(DashboardJson dbEl, Principal principal) throws Exception {

		if (dbEl.getDbType().equalsIgnoreCase(DashboardTypes.VERIFIED_JOBS.name())
				|| dbEl.getDbType().equalsIgnoreCase(DashboardTypes.APPROVED_JOBS.name())) {

			StringBuilder hqlStat = new StringBuilder("SELECT COUNT(o) FROM TCkJobTruck o ");
			hqlStat.append(" WHERE o.jobStatus=:jobStatus")
					.append(" AND o.TCkJob.TCkMstJobState.jbstId not in (:excludeStates) ");
			hqlStat.append(" AND o.TCoreAccnByJobPartyCoFf.accnId = :accnId");

			Map<String, Object> params = new HashMap<>();
			params.put("jobStatus", RecordStatus.ACTIVE.getCode());
			params.put("excludeStates", Arrays.asList(JobStates.CAN.name(), JobStates.REJ.name(), JobStates.DEL.name(),
					JobStates.REJ.name()));
			params.put("accnId", principal.getCoreAccn().getAccnId());

			if (dbEl.getDbType().equalsIgnoreCase(DashboardTypes.VERIFIED_JOBS.name())) {
				hqlStat.append(" and o.TCkJob.TCkMstJobState.jbstId IN (:activeStates)");
				params.put("activeStates", Arrays.asList(JobStates.VER.name(), JobStates.VER_BILL.name()));

				dbEl.getTransStatistic().put(DashboardStatus.VERIFIED.name(),
						ckJobTruckDao.count(hqlStat.toString(), params));
			} else {
				hqlStat.append(" and o.TCkJob.TCkMstJobState.jbstId IN (:activeStates)");
				params.put("activeStates", Arrays.asList(JobStates.APP.name(), JobStates.APP_BILL.name()));

				dbEl.getTransStatistic().put(DashboardStatus.APPROVED.name(),
						ckJobTruckDao.count(hqlStat.toString(), params));
			}

		} else if (dbEl.getDbType().equalsIgnoreCase(DashboardTypes.JOB_PAYMENTS.name())) {
			doJobPaymentsTxn(dbEl, principal);
		}
	}

	private void doNonFinance(DashboardJson dbEl, Principal principal) throws Exception {
		StringBuilder hqlStat = new StringBuilder("SELECT COUNT(o) FROM TCkJobTruck o ");
		hqlStat.append(" WHERE o.jobStatus=:jobStatus")
				.append(" AND o.TCkJob.TCkMstJobState.jbstId not in (:excludeStates) ");
		Map<String, Object> params = new HashMap<>();
		params.put("jobStatus", RecordStatus.ACTIVE.getCode());
		params.put("excludeStates",
				Arrays.asList(JobStates.CAN.name(), JobStates.REJ.name(), JobStates.DEL.name(), JobStates.REJ.name()));

		// check if FF or TO, for filtering the account
		if (dbEl.getAccnType().equalsIgnoreCase(AccountTypes.ACC_TYPE_FF.name())) {
			hqlStat.append(
					" AND (o.TCkJob.TCoreAccnByJobOwnerAccn.accnId = :accnId OR o.TCkJob.TCoreAccnByJobFfAccn.accnId = :accnId) ");
		} else if (dbEl.getAccnType().equalsIgnoreCase(AccountTypes.ACC_TYPE_CO.name())) {
			hqlStat.append(
					" AND (o.TCkJob.TCoreAccnByJobOwnerAccn.accnId = :accnId OR o.TCkJob.TCoreAccnByJobCoAccn.accnId = :accnId) ");

		}

		params.put("accnId", principal.getCoreAccn().getAccnId());

		// Then check for the dashboard type
		if (dbEl.getDbType().equalsIgnoreCase(DashboardTypes.TRUCK_JOBS.name())) {
			hqlStat.append(" and o.TCkJob.TCkMstJobState.jbstId IN (:activeStates)");
			params.put("activeStates", Arrays.asList(JobStates.NEW.name(), JobStates.SUB.name(), JobStates.ACP.name(),
					JobStates.ASG.name(), JobStates.ONGOING.name(), JobStates.DLV.name()));

			dbEl.getTransStatistic().put(DashboardStatus.ACTIVE.name(),
					ckJobTruckDao.count(hqlStat.toString(), params));

		} else if (dbEl.getDbType().equalsIgnoreCase(DashboardTypes.BILLED_JOBS.name())) {
			// for billed job, added ver_bill. It will only move to approved when it is
			// app_bill
			hqlStat.append(" and o.TCkJob.TCkMstJobState.jbstId IN (:activeStates)");
			params.put("activeStates", Arrays.asList(JobStates.BILLED.name(), JobStates.VER_BILL.name(), JobStates.ACK_BILL.name()));

			dbEl.getTransStatistic().put(DashboardStatus.PENDING_VERIFICATION.name(),
					ckJobTruckDao.count(hqlStat.toString(), params));
			// Added VERIFIED_JOBS
		} else if (dbEl.getDbType().equalsIgnoreCase(DashboardTypes.VERIFIED_JOBS.name())) {
			hqlStat.append(" AND o.TCoreAccnByJobPartyCoFf.accnId = :accnId");
			hqlStat.append(" and o.TCkJob.TCkMstJobState.jbstId IN (:activeStates)");

			params.put("activeStates", Arrays.asList(JobStates.VER.name(), JobStates.VER_BILL.name()));
			dbEl.getTransStatistic().put(DashboardStatus.VERIFIED.name(),
					ckJobTruckDao.count(hqlStat.toString(), params));
		}
	}

	private void doGliFinance(DashboardJson dbEl, Principal principal) throws Exception {

		if (dbEl.getDbType().equalsIgnoreCase(DashboardTypes.APPROVED_JOBS.name())) {

			StringBuilder hqlStat = new StringBuilder("SELECT COUNT(o) FROM TCkJobTruck o ");
			hqlStat.append(" WHERE o.jobStatus=:jobStatus")
					.append(" AND o.TCkJob.TCkMstJobState.jbstId not in (:excludeStates) ");

			Map<String, Object> params = new HashMap<>();
			params.put("jobStatus", RecordStatus.ACTIVE.getCode());
			params.put("excludeStates", Arrays.asList(JobStates.CAN.name(), JobStates.REJ.name(), JobStates.DEL.name(),
					JobStates.REJ.name()));

			// not filtered by account for GLI since it is not captured from tckjobtruck

			hqlStat.append(" and o.TCkJob.TCkMstJobState.jbstId IN (:activeStates)");
			hqlStat.append(" and o.jobOutPaymentState in (:outPayStates)");

			params.put("activeStates", Arrays.asList(JobStates.APP.name(), JobStates.APP_BILL.name()));
			params.put("outPayStates", Arrays.asList(JobPaymentStates.NEW.name()));

			dbEl.getTransStatistic().put(DashboardStatus.APPROVED.name(),
					ckJobTruckDao.count(hqlStat.toString(), params));

		} else if (dbEl.getDbType().equalsIgnoreCase(DashboardTypes.JOB_PAYMENTS.name())) {
			doJobPaymentsTxn(dbEl, principal);
		}

	}

	private void doGliFinanceTaxModules(DashboardJson dbEl, Principal principal) throws Exception {

		if (dbEl.getDbType().equalsIgnoreCase(DashboardTypes.SEQUENCE.name())) {
			StringBuilder hqlStat = new StringBuilder(
					"SELECT (o.stRangeCurrent - o.stRangeBegin) + 1 FROM TCkCtSageTax o ");
			hqlStat.append(" WHERE o.stStatus=:status");

			StringBuilder hqlStat2 = new StringBuilder(
					"SELECT (o.stRangeEnd - o.stRangeBegin) + 1 FROM TCkCtSageTax o ");
			hqlStat2.append(" WHERE o.stStatus=:status");

			Map<String, Object> params = new HashMap<>();

			params.put("status", RecordStatus.ACTIVE.getCode());
			Map<String, Integer> unsortedTransStatistic = new HashMap<>();
			unsortedTransStatistic.put(DashboardStatus.USED.name(), ckCtSageTaxDao.count(hqlStat.toString(), params));
			unsortedTransStatistic.put(DashboardStatus.TOTAL.name(), ckCtSageTaxDao.count(hqlStat2.toString(), params));
			Map<String, Integer> sortedTransStatistic = new TreeMap<>(Comparator.reverseOrder());
			sortedTransStatistic.putAll(unsortedTransStatistic);
			dbEl.setTransStatistic(sortedTransStatistic);

		} else if (dbEl.getDbType().equalsIgnoreCase(DashboardTypes.REPORTS.name())) {
			StringBuilder hqlStat = new StringBuilder("SELECT COUNT(o) FROM TCkTaxReport o ");
			hqlStat.append(" WHERE o.trStatus = :status");
			hqlStat.append(" and o.trService = :trService");

			Map<String, Object> params = new HashMap<>();
			params.put("status", RecordStatus.ACTIVE.getCode());
			params.put("trService", ServiceTypes.CLICTRUCK.name());

			Integer count = ckTaxReportDao.count(hqlStat.toString(), params);
			dbEl.getTransStatistic().put(DashboardStatus.READY_REPORT.name(), count);

		} else if (dbEl.getDbType().equalsIgnoreCase(DashboardTypes.INVOICES.name())) {
			StringBuilder hqlStat = new StringBuilder("SELECT COUNT(o) FROM TCkTaxInvoice o ");
			hqlStat.append(" WHERE o.tiStatus = :status");
			hqlStat.append(" and o.tiService = :tiService");

			Map<String, Object> params = new HashMap<>();
			params.put("status", TAX_INVOICE_STATUS_EXPORTED);
			params.put("tiService", ServiceTypes.CLICTRUCK.name());

			Integer count = ckTaxReportDao.count(hqlStat.toString(), params);
			dbEl.getTransStatistic().put(DashboardStatus.WITHOUT_PDF.name(), count);

		}

	}

	private void doGli(DashboardJson dbEl, Principal principal) throws Exception {

		if (dbEl.getDbType().equalsIgnoreCase(DashboardTypes.SUSPENSION.name())) {
			StringBuilder hqlStat = new StringBuilder("SELECT COUNT(o) FROM TCoreAccn o ");
			hqlStat.append(" WHERE o.accnStatus=:status AND o.TMstAccnType.atypId IN (:accnType)");

			Map<String, Object> params = new HashMap<>();
			params.put("status", RecordStatus.ACTIVE.getCode());
			List<String> accnTypes = new ArrayList<>();
			accnTypes.add(AccountTypes.ACC_TYPE_CO.getDesc());
			accnTypes.add(AccountTypes.ACC_TYPE_FF.getDesc());
			params.put("accnType", accnTypes);

			Integer count = accnDao.count(hqlStat.toString(), params);
			dbEl.getTransStatistic().put(DashboardStatus.ACTIVE_ACCN.getDesc(), count);

		} else if (dbEl.getDbType().equalsIgnoreCase(DashboardTypes.RESUMPTION.name())) {
			StringBuilder hqlStat = new StringBuilder("SELECT COUNT(o) FROM TCoreAccn o ");
			hqlStat.append(" WHERE o.accnStatus=:status AND o.TMstAccnType.atypId IN (:accnType)");

			Map<String, Object> params = new HashMap<>();
			params.put("status", ACCOUNT_SUSPEND);
			List<String> accnTypes = new ArrayList<>();
			accnTypes.add(AccountTypes.ACC_TYPE_CO.getDesc());
			accnTypes.add(AccountTypes.ACC_TYPE_FF.getDesc());
			params.put("accnType", accnTypes);

			Integer count = accnDao.count(hqlStat.toString(), params);
			dbEl.getTransStatistic().put(DashboardStatus.SUSPEND_ACCN.getDesc(), count);

		}

	}

	@SuppressWarnings("rawtypes")
	private void doTruckingOperator(DashboardJson dbEl, Principal principal) throws Exception {
		// This is for TO.
		// Updated script for stats for job_billing that it should only count for financed/extended finance
		StringBuilder hqlStat = new StringBuilder();
		if(dbEl.getDbType().equalsIgnoreCase(DashboardTypes.JOB_BILLING.name())) {
			hqlStat.append("SELECT SUM(CASE WHEN o.TCkJob.TCkMstJobState.jbstId IN (:overStates) AND o.jobIsFinanced in ('F','E')  THEN 1 ELSE 0 END) as OVER_STATE, ");
		} else {
			hqlStat.append("SELECT SUM(CASE WHEN o.TCkJob.TCkMstJobState.jbstId IN (:overStates) THEN 1 ELSE 0 END) as OVER_STATE, ");
		}
				
		hqlStat.append(
				" SUM(CASE WHEN o.TCkJob.TCkMstJobState.jbstId IN (:underStates) THEN 1 ELSE 0 END) as UNDER_STATE ");
		hqlStat.append(" FROM TCkJobTruck o ");
		hqlStat.append(" WHERE o.jobStatus=:jobStatus")
				.append(" AND o.TCkJob.TCkMstJobState.jbstId in (:includeStates) ");
		Map<String, Object> params = new HashMap<>();
		params.put("jobStatus", RecordStatus.ACTIVE.getCode());

		// check if FF or TO, for filtering the account
		if (dbEl.getAccnType().equalsIgnoreCase(AccountTypes.ACC_TYPE_TO.name())) {
			hqlStat.append(" AND o.TCkJob.TCoreAccnByJobToAccn.accnId = :accnId ");
		}else if (dbEl.getAccnType().equalsIgnoreCase(AccountTypes.ACC_TYPE_FF_CO.name())) {
			hqlStat.append(" AND (o.TCkJob.TCoreAccnByJobSlAccn.accnId = :accnId) ");
		}
		params.put("accnId", principal.getCoreAccn().getAccnId());

		if (dbEl.getDbType().equalsIgnoreCase(DashboardTypes.TRUCK_JOBS.name())) {
			params.put("overStates", Arrays.asList(JobStates.SUB.name(), JobStates.ACP.name(), JobStates.ASG.name()));
			params.put("underStates", Arrays.asList(JobStates.ONGOING.name()));
			params.put("includeStates", Arrays.asList(JobStates.SUB.name(), JobStates.ACP.name(), JobStates.ASG.name(),
					JobStates.ONGOING.name(), JobStates.DLV.name()));

			List<TCkJobTruck> list = ckJobTruckDao.getByQuery(hqlStat.toString(), params);
			if (list != null && list.size() > 0) {
				Iterator it = list.iterator();
				while (it.hasNext()) {
					Object[] obj = (Object[]) it.next();
					dbEl.getTransStatistic().put(DashboardStatus.NEW.name(), NumberUtil.toInteger(obj[0]));
					dbEl.getTransStatistic().put(DashboardStatus.ONGOING.name(), NumberUtil.toInteger(obj[1]));
				}
			}

		} else if (dbEl.getDbType().equalsIgnoreCase(DashboardTypes.JOB_PAYMENTS.name())) {
			doJobPaymentsTxn(dbEl, principal);
		} else {
			// jobs for billing
			params.put("overStates", Arrays.asList(JobStates.DLV.name()));
			params.put("underStates", Arrays.asList(JobStates.BILLED.name()));
			params.put("includeStates", Arrays.asList(JobStates.DLV.name(), JobStates.BILLED.name()));
			List<TCkJobTruck> list = ckJobTruckDao.getByQuery(hqlStat.toString(), params);
			if (list != null && list.size() > 0) {
				Iterator it = list.iterator();
				while (it.hasNext()) {
					Object[] obj = (Object[]) it.next();
					dbEl.getTransStatistic().put(DashboardStatus.READY.name(),
							obj[0] == null ? 0 : Integer.valueOf(String.valueOf(obj[0])));
					dbEl.getTransStatistic().put(DashboardStatus.SUBMITTED.name(),
							obj[1] == null ? 0 : Integer.valueOf(String.valueOf(obj[1])));

				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void doJobPaymentsTxn(DashboardJson dbEl, Principal principal) throws Exception {
		StringBuilder hqlStat = new StringBuilder(
				"SELECT SUM(CASE WHEN o.ptxPaymentState in (:oversStates) THEN 1 ELSE 0 END ) AS OVER_STATE, ");
		hqlStat.append(" SUM(CASE WHEN o.ptxPaymentState in (:underStates) THEN 1 ELSE 0 END) as UNDER_STATE ");
		hqlStat.append(" FROM TCkPaymentTxn o ");
		hqlStat.append(" WHERE o.ptxPaymentState in (:includeStates) ")
				.append(" AND o.TCoreAccnByPtxPayer.accnId = :accnId ").append(" AND o.ptxStatus=:status");
		Map<String, Object> params = new HashMap<>();
		params.put("oversStates", Arrays.asList(PaymentStates.PAYING.getCode(), PaymentStates.VER_BILL.getCode(),
				PaymentStates.APP_BILL.getCode(), PaymentStates.NEW.getCode()));
		params.put("underStates", Arrays.asList(PaymentStates.PAID.getCode(), PaymentStates.FAILED.getCode()));
		params.put("includeStates", Arrays.asList(PaymentStates.PAYING.getCode(), PaymentStates.PAID.getCode(),
				PaymentStates.VER_BILL.getCode(), PaymentStates.APP_BILL.getCode(), PaymentStates.NEW.getCode()));
		params.put("accnId", principal.getCoreAccn().getAccnId());
		params.put("status", RecordStatus.ACTIVE.getCode());

		List<TCkPaymentTxn> list = ckPaymentTxnDao.getByQuery(hqlStat.toString(), params);
		if (list != null && list.size() > 0) {
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Object[] obj = (Object[]) it.next();
				dbEl.getTransStatistic().put(DashboardStatus.ACTIVE.name(),
						obj[0] == null ? 0 : Integer.valueOf(String.valueOf(obj[0])));
				dbEl.getTransStatistic().put(DashboardStatus.PAID.name(),
						obj[1] == null ? 0 : Integer.valueOf(String.valueOf(obj[1])));
			}
		}
	}
}
