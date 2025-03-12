package com.guudint.clickargo.clictruck.finacing.scheduler;

import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clicservice.dao.CkSuspensionLogDao;
import com.guudint.clickargo.clicservice.model.TCkSuspensionLog;
import com.guudint.clickargo.clictruck.admin.contract.dao.CkCtContractDao;
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContract;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContract;
import com.guudint.clickargo.clictruck.finacing.event.AccountSuspensionEvent;
import com.guudint.clickargo.common.CkUtil;
import com.vcc.camelone.ccm.dao.CoreAccnDao;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.audit.dto.CoreAuditlog;
import com.vcc.camelone.common.audit.service.impl.COAuditLogService;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.master.dto.MstAccnType;
import com.vcc.camelone.scheduler.dto.CoreScheduleJoblog;
import com.vcc.camelone.scheduler.service.AbstractJob;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

/**
 * Scheduler that monitors the contracts of the COFF and TO expiry and suspend
 * the CO/FF account.
 */
@Component
@EnableScheduling
@EnableAsync

public class ClickTruckContractMonitoringScheduler extends AbstractJob {

	private static final Logger log = Logger.getLogger(ClickTruckContractMonitoringScheduler.class);
	private static char STATUS_SUSPENDED = 'S';
	private static char STATUS_EXPIRED = 'E';

	@Autowired
	private CkCtContractDao ckCtContractDao;

	@Autowired
	private CoreAccnDao accnDao;

	@Autowired
	private COAuditLogService auditLogService;

	@Autowired
	private CkSuspensionLogDao ckSuspensionLogDao;

	@Autowired
	protected ApplicationEventPublisher eventPublisher;

	public ClickTruckContractMonitoringScheduler() {
		super.setTaskName(this.getClass().getSimpleName());

	}

	@Override
	@Scheduled(cron = "0 0 1 * * *") /* Run once daily */
	@SchedulerLock(name = "ClickTruckContractMonitoringScheduler", lockAtLeastFor = "1m")
	@Transactional
	public void doJob() throws Exception {
		// TODO Auto-generated method stub
		log.debug("doJobSuspendCO");

		log.info("ClickTruckContractMonitoringScheduler running in " + InetAddress.getLocalHost().getHostName());

		LockAssert.assertLocked();

		String taskNo = super.getTaskNo();
		CoreScheduleJoblog coreScheduleJoblog = null;

		try {

			coreScheduleJoblog = super.getTask(TASK_STATE.START, taskNo, TASK_STATE.START.toString());
			log.info("ClickTruckContractMonitoringScheduler Started: " + Calendar.getInstance().getTime().toString());

			try {
				updateAccountCOToSuspended();
				coreScheduleJoblog = super.getTask(TASK_STATE.COMPLETE, taskNo,
						ServiceStatus.STATUS.COMPLETED.toString(), "SUCCESS");
			} catch (Exception ex) {
				coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, ServiceStatus.STATUS.FAILED.toString(),
						ex.getMessage());
			}

			super.logTask(coreScheduleJoblog);
			log.info("ClickTruckContractMonitoringScheduler Ended: " + Calendar.getInstance().getTime().toString());
		} catch (Exception ex) {
			log.error("doJob", ex);
			coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, "EXCEPTION",
					ExceptionUtils.getStackTrace(ex));
			super.logTask(coreScheduleJoblog);
		}

	}

	/**
	 * Suspend CO Accounts X interval days after Contract Expired
	 * 
	 * @throws Exception
	 */

	private void updateAccountCOToSuspended() throws Exception {

		Optional<List<TCkCtContract>> ckContract = Optional.ofNullable(ckCtContractDao.findNotValidContract());

		String accnId = "";
		if (ckContract.isPresent()) {
			for (TCkCtContract contract : ckContract.get()) {
				accnId = contract.getTCoreAccnByConCoFf().getAccnId();

				TCoreAccn accnTmp = accnDao.find(accnId);
				if (accnTmp == null) {
					continue;
				}
				Hibernate.initialize(accnTmp.getTMstAccnType());
				accnTmp.setAccnStatus(STATUS_SUSPENDED);
				accnTmp.setAccnDtSusp(new Date());
				accnTmp.setAccnDtLupd(new Date());
				accnTmp.setAccnUidLupd(Constant.ACCN_CREATE_SYS_USER);
				accnDao.update(accnTmp);

				CoreAccn coreAccnDto = new CoreAccn(accnTmp);
				coreAccnDto.setTMstAccnType(new MstAccnType(accnTmp.getTMstAccnType()));

				TCkCtContract contractTemp = ckCtContractDao.find(contract.getConId());
				if (contractTemp == null) {
					continue;
				}
				Hibernate.initialize(contractTemp.getTCoreAccnByConCoFf());
				Hibernate.initialize(contractTemp.getTCoreAccnByConTo());
				contractTemp.setConDtLupd(new Date());
				contractTemp.setConUidLupd("SYS");
				contractTemp.setConStatus(STATUS_EXPIRED);
				ckCtContractDao.update(contractTemp);

				CkCtContract ckCtContractDto = new CkCtContract(contractTemp);
				ckCtContractDto.setTCoreAccnByConCoFf(new CoreAccn(contractTemp.getTCoreAccnByConCoFf()));
				ckCtContractDto.setTCoreAccnByConTo(new CoreAccn(contractTemp.getTCoreAccnByConTo()));

				CoreAuditlog audit = new CoreAuditlog();
				audit.setAudtReckey(accnTmp.getAccnId());
				audit.setAudtEvent("ACCN MODIFY");
				audit.setAudtRemarks("Suspended due to expired contract.");
				audit.setAudtAccnid("SYS");
				audit.setAudtUid("SYS");
				auditLogService.log(audit);

				addSuspensionLogs(accnId, contract.getConId());

				AccountSuspensionEvent accountSuspensionEvent = new AccountSuspensionEvent(this, coreAccnDto,
						ckCtContractDto, "CONTRACT_EXPIRY");
				eventPublisher.publishEvent(accountSuspensionEvent);
			}
		}
	}

	/**
	 * @param accnId
	 * @param remarks
	 * @throws Exception
	 */
	private void addSuspensionLogs(String accnId, String remarks) throws Exception {

		TCkSuspensionLog suspLog = new TCkSuspensionLog();
		suspLog.setSlId(CkUtil.generateId());
		suspLog.setSlAccnId(accnId);
		suspLog.setSlEvent("ACCN MODIFY");
		suspLog.setSlRemarks("Automated Suspension");
		suspLog.setSlDetails(remarks);
		suspLog.setSlDtCreate(new Date());
		suspLog.setSlUidCreate("SYS");
		ckSuspensionLogDao.add(suspLog);
	}

}
