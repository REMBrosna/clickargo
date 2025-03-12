package com.guudint.clickargo.clictruck.admin.contract.scheduler;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.admin.contract.constant.CkCtContractChargeConstant;
import com.guudint.clickargo.clictruck.admin.contract.constant.CkCtContractConstant;
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContract;
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContractReq;
import com.guudint.clickargo.clictruck.admin.contract.dto.ContractReqStateEnum;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContract;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContractCharge;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContractReq;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtMstContractReqState;
import com.guudint.clickargo.clictruck.admin.contract.service.impl.CkCtContractRequestServiceImpl;
import com.guudint.clickargo.clictruck.admin.contract.service.impl.CkCtContractServiceImpl;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.audit.dao.CoreAuditLogDao;
import com.vcc.camelone.common.audit.model.TCoreAuditlog;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.master.model.TMstBank;
import com.vcc.camelone.master.model.TMstCurrency;
import com.vcc.camelone.scheduler.dto.CoreScheduleJoblog;
import com.vcc.camelone.scheduler.service.AbstractJob;

import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

/**
 * This scheduler will pick up records from T_CK_CT_CONTRACT_REQ when the
 * {@code CR_DT_START} equals to current date of execution and the
 * {@code CR_STATE} is approved.
 */
@Component
@EnableAsync
@EnableScheduling
public class ContractUpdateScheduler extends AbstractJob {

	private static final Logger log = Logger.getLogger(ContractUpdateScheduler.class);

	@Autowired
	private CkCtContractRequestServiceImpl contractReqService;

	@Autowired
	@Qualifier("ckCtContractReqDao")
	private GenericDao<TCkCtContractReq, String> ckCtContractReqDao;

	@Autowired
	private CkCtContractServiceImpl contractService;

	@Autowired
	@Qualifier("ckCtContractDao")
	private GenericDao<TCkCtContract, String> ckCtContractDao;

	@Autowired
	@Qualifier("ckCtContractChargeDao")
	private GenericDao<TCkCtContractCharge, String> ckCtContractChargeDao;

	@Autowired
	private CoreAuditLogDao auditLogDao;

	public ContractUpdateScheduler() {
		super.setTaskName(this.getClass().getSimpleName());

	}

	@Override
//	@Scheduled(cron = "0 */2 * * * *") // runs every 2 minutes for testing
	@Scheduled(cron = "0 59 23 * * *") // Runs daily at 11:59 PM
	@SchedulerLock(name = "ContractUpdateScheduler", lockAtLeastFor = "1m")
	public void doJob() throws Exception {
		LockAssert.assertLocked();
		String taskNo = super.getTaskNo();
		CoreScheduleJoblog coreScheduleJoblog = null;
		try {

			coreScheduleJoblog = super.getTask(TASK_STATE.START, taskNo, TASK_STATE.START.toString());
			log.info("ContractUpdateScheduler Started: " + Calendar.getInstance().getTime().toString());

			executeContractUpdate(null);
			coreScheduleJoblog = super.getTask(TASK_STATE.COMPLETE, taskNo,
					ServiceStatus.STATUS.COMPLETED.toString(), "SUCCESS");

			super.logTask(coreScheduleJoblog);
			log.info("ContractUpdateScheduler Ended: " + Calendar.getInstance().getTime().toString());
		} catch (Exception ex) {
			log.error("doJob", ex);
			coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, "EXCEPTION",
					ExceptionUtils.getStackTrace(ex));
			super.logTask(coreScheduleJoblog);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void executeContractUpdate(Calendar cal) throws Exception {
		log.info("executeContractUpdate");
		// init current date
		if( cal == null) {
			cal = Calendar.getInstance();
		}

		// Retrieve all requests with the start date = current date and states are
		// NEW_APPROVED and UPDATE_APPROVED.
		List<CkCtContractReq> reqList = contractReqService.getContractRequestsByDateAndState(cal.getTime(),
				Arrays.asList(ContractReqStateEnum.NEW_APPROVED.name(), ContractReqStateEnum.UPDATE_APPROVED.name(),
						ContractReqStateEnum.RENEWAL_APPROVED.name()));

		// Regardless of the state of the contract request, NEW_APPROVED or
		// UPDATE_APPROVE, the previous contract will
		// be updated. No need to create new record in t_ck_ct_contract
		if (reqList != null && reqList.size() > 0) {
			for (CkCtContractReq req : reqList) {
				// Retrieve the contracts by accounts if the status is RENEWAL_APPROVED /
				// UPDATE_APPROVED, otherwise, insert
				log.info("Id: " + req.getCrId() + "  name: " + req.getCrName() + " " + req.getTCkCtMstContractReqState());
				
				if (req.getTCkCtMstContractReqState().getStId()
						.equalsIgnoreCase(ContractReqStateEnum.UPDATE_APPROVED.name())
						|| req.getTCkCtMstContractReqState().getStId()
								.equalsIgnoreCase(ContractReqStateEnum.RENEWAL_APPROVED.name())) {

					// search for active and possibly expired to update
					CkCtContract contract = contractService.getContractByAccounts(req.getTCoreAccnByCrTo().getAccnId(),
							req.getTCoreAccnByCrCoFf().getAccnId(), null, null,
							Arrays.asList(RecordStatus.ACTIVE.getCode(), CkCtContract.STATUS_EXPIRED));
					CkCtContract updatedContract = contractService.copyContractReqToContract(req, contract);
					// copy the ids from contract to updatedContract
					TCkCtContract entity = new TCkCtContract();
					updatedContract.copyBeanProperties(entity);
					// manually set from contract for some properties
					entity.setConId(contract.getConId());
					// in case the status is Expired
					entity.setConStatus(RecordStatus.ACTIVE.getCode());
					entity.setConDtCreate(new Date());
					entity.setConUidCreate("SYS");
					entity.setTCoreAccnByConTo(updatedContract.getTCoreAccnByConTo().toEntity(new TCoreAccn()));
					entity.setTCoreAccnByConCoFf(updatedContract.getTCoreAccnByConCoFf().toEntity(new TCoreAccn()));
					entity.setTMstBank(updatedContract.getTMstBank().toEntity(new TMstBank()));
					entity.setTMstCurrency(updatedContract.getTMstCurrency().toEntity(new TMstCurrency()));
					
					// Update Contract charge TO
					TCkCtContractCharge toCharge = new TCkCtContractCharge();
					updatedContract.getTCkCtContractChargeByConChargeTo().toEntity(toCharge);
					toCharge.setConcId(contract.getTCkCtContractChargeByConChargeTo().getConcId());
					ckCtContractChargeDao.update(toCharge);
					entity.setTCkCtContractChargeByConChargeTo(toCharge);

					// Updat Contract charge CO/FF
					TCkCtContractCharge coFfCharge = new TCkCtContractCharge();
					updatedContract.getTCkCtContractChargeByConChargeCoFf().toEntity(coFfCharge);
					coFfCharge.setConcId(contract.getTCkCtContractChargeByConChargeCoFf().getConcId());
					ckCtContractChargeDao.update(coFfCharge);
					entity.setTCkCtContractChargeByConChargeCoFf(coFfCharge);

					// Update OPM charge
					TCkCtContractCharge opmCharge = new TCkCtContractCharge();
					updatedContract.getTCkCtContractChargeByConOpm().toEntity(opmCharge);
					if(contract.getTCkCtContractChargeByConOpm() == null) {
						opmCharge.setConcId(
								CkUtil.generateId(CkCtContractChargeConstant.Prefix.PREFIX_CK_CT_CONTRACT_CHARGE));
						ckCtContractChargeDao.add(opmCharge);
					} else {
						opmCharge.setConcId(contract.getTCkCtContractChargeByConOpm().getConcId());
						ckCtContractChargeDao.update(opmCharge);
					}
					
					entity.setTCkCtContractChargeByConOpm(opmCharge);

					entity.setConDtLupd(new Date());
					entity.setConUidLupd("SYS");
					ckCtContractDao.update(entity);

					audit(entity.getConId(), "CONTRACT UPDATED FROM CONTRACT REQUEST");
				} else {
					// fetched first to check if there's really no co/ff - to pair existing
					CkCtContract contract = contractService.getContractByAccounts(req.getTCoreAccnByCrTo().getAccnId(),
							req.getTCoreAccnByCrCoFf().getAccnId(), null, null, null);

					if (contract == null) {
						CkCtContract newContract = contractService.copyContractReqToContract(req, contract);
						TCkCtContract entity = new TCkCtContract();
						newContract.copyBeanProperties(entity);
						// manually set from contract for some properties
						entity.setConId(CkUtil.generateId(CkCtContractConstant.Prefix.PREFIX_CK_CT_CONTRACT));
						entity.setConName(req.getCrName());
						entity.setConStatus(RecordStatus.ACTIVE.getCode());
						entity.setConDescription(req.getCrDescription());
						entity.setTCoreAccnByConTo(newContract.getTCoreAccnByConTo().toEntity(new TCoreAccn()));
						entity.setTCoreAccnByConCoFf(newContract.getTCoreAccnByConCoFf().toEntity(new TCoreAccn()));
						entity.setTMstCurrency(newContract.getTMstCurrency().toEntity(new TMstCurrency()));
						entity.setConFinanceModel(req.getCrFinanceModel());

						// Update Contract charge TO
						TCkCtContractCharge toCharge = new TCkCtContractCharge();
						newContract.getTCkCtContractChargeByConChargeTo().toEntity(toCharge);
						toCharge.setConcId(
								CkUtil.generateId(CkCtContractChargeConstant.Prefix.PREFIX_CK_CT_CONTRACT_CHARGE));
						ckCtContractChargeDao.add(toCharge);
						entity.setTCkCtContractChargeByConChargeTo(toCharge);

						// Updat Contract charge CO/FF
						TCkCtContractCharge coFfCharge = new TCkCtContractCharge();
						newContract.getTCkCtContractChargeByConChargeCoFf().toEntity(coFfCharge);
						coFfCharge.setConcId(
								CkUtil.generateId(CkCtContractChargeConstant.Prefix.PREFIX_CK_CT_CONTRACT_CHARGE));
						ckCtContractChargeDao.add(coFfCharge);
						entity.setTCkCtContractChargeByConChargeCoFf(coFfCharge);

						// update contract for opm
						TCkCtContractCharge opmCharge = new TCkCtContractCharge();
						newContract.getTCkCtContractChargeByConOpm().toEntity(opmCharge);
						opmCharge.setConcId(
								CkUtil.generateId(CkCtContractChargeConstant.Prefix.PREFIX_CK_CT_CONTRACT_CHARGE));
						ckCtContractChargeDao.add(opmCharge);
						entity.setTCkCtContractChargeByConOpm(opmCharge);

						entity.setConDtCreate(new Date());
						entity.setConUidCreate("SYS");
						entity.setConDtLupd(new Date());
						entity.setConUidLupd("SYS");
						ckCtContractDao.add(entity);

						audit(entity.getConId(), "CONTRACT CREATED FROM CONTRACT REQUEST");

					}

				}

				// update the status of contract request
				TCkCtContractReq reqEntity = ckCtContractReqDao.find(req.getCrId());
				log.info("Update status Id: " + req.getCrId() + "  reqEntity: " + reqEntity);
				
				if (reqEntity != null) {
					TCkCtMstContractReqState reqState = new TCkCtMstContractReqState(
							ContractReqStateEnum.EXPORTED.name());
					reqEntity.setTCkCtMstContractReqState(reqState);
					reqEntity.setCrUidLupd("SYS");
					reqEntity.setCrDtLupd(new Date());
					ckCtContractReqDao.update(reqEntity);
				}

			}
		}
	}

	private void audit(String key, String event) {
		Date now = Calendar.getInstance().getTime();
		try {

			TCoreAuditlog auditLog = new TCoreAuditlog();
			auditLog.setAudtId(CkUtil.generateId("CR"));
			auditLog.setAudtReckey(key);
			auditLog.setAudtTimestamp(now);
			auditLog.setAudtEvent(event);
			auditLog.setAudtUid("SYS");
			auditLog.setAudtRemoteIp("");
			auditLog.setAudtUname("SYS");
			auditLog.setAudtRemarks("-");
			auditLogDao.add(auditLog);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
