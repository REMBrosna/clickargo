package com.guudint.clickargo.clictruck.admin.contract.scheduler;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContract;
import com.guudint.clickargo.clictruck.admin.contract.event.NotifyExpireEvent;
import com.guudint.clickargo.clictruck.admin.contract.service.impl.CkCtContractServiceImpl;
import com.guudint.clickargo.common.CkUtil;
import com.vcc.camelone.common.audit.dao.CoreAuditLogDao;
import com.vcc.camelone.common.audit.model.TCoreAuditlog;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.scheduler.dto.CoreScheduleJoblog;
import com.vcc.camelone.scheduler.service.AbstractJob;

import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

/**
 * This scheduler picks up contracts that are about to expire (i.e. 2 months,
 * 1 month, 1 week) prior
 *
 */
@Component
@EnableAsync
@EnableScheduling
public class ContractExpiryNotificationScheduler extends AbstractJob {

	// Static Attributes
	private static final Logger log = Logger.getLogger(ContractExpiryNotificationScheduler.class);
	
	// Attributes
	private final SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
	private final SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final String REMINDER_SENT = "NOTIFICATION FOR CONTRACT EXPIRY REMINDER SENT";
	
	@Autowired
	private CkCtContractServiceImpl contractService;

	@Autowired
	private CoreAuditLogDao auditLogDao;

	@Autowired
	protected ApplicationEventPublisher eventPublisher;
	
	public ContractExpiryNotificationScheduler() {
		super.setTaskName(this.getClass().getSimpleName());

	}

	@Override
//	@Scheduled(cron = "0 */2 * * * *") // runs every 2 minutes for testing
	@Scheduled(cron = "0 0 0 * * *") /* Run once daily */
	@SchedulerLock(name = "ContractExpiryNotificationScheduler", lockAtLeastFor = "1m")
	public void doJob() throws Exception {
		log.debug("doJob");
		LockAssert.assertLocked();
		log.info("ContractExpiryNotificationScheduler running in " + InetAddress.getLocalHost().getHostName());

		Calendar cal;
		
		// notify expiration 2 months prior
		cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 2);
		Date two_mo = cal.getTime();
		this.notifyExpirationTask(two_mo);
		Thread.sleep(5000);
		
		// notify expiration 1 month prior
		cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 1);
		Date one_mo = cal.getTime();
		this.notifyExpirationTask(one_mo);
		Thread.sleep(5000);
		
		// notify expiration 1 week prior
		cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 7);
		Date one_wk = cal.getTime();
		this.notifyExpirationTask(one_wk);
	}

	public void notifyExpirationTask(Date expiryDate) throws Exception {
		String taskNo = super.getTaskNo();
		CoreScheduleJoblog coreScheduleJoblog;
		try {
			// save scheduler log when tasks started
			coreScheduleJoblog = super.getTask(TASK_STATE.START, taskNo, TASK_STATE.START.toString());
			coreScheduleJoblog.setSjlName("Notification for contract expiration".concat(" - ").concat(sdfDateTime.format(Calendar.getInstance().getTime())));
			super.logTask(coreScheduleJoblog);
			log.info("ContractExpiryNotificationScheduler Started: " + Calendar.getInstance().getTime().toString());

			try {
				this.notifyExpireEvent(expiryDate);
				coreScheduleJoblog = super.getTask(TASK_STATE.COMPLETE, taskNo,ServiceStatus.STATUS.COMPLETED.toString(), "SUCCESS");
				coreScheduleJoblog.setSjlName("Notification for contract expiration completed".concat(" - ").concat(sdfDateTime.format(Calendar.getInstance().getTime())));
			} catch (Exception ex) {
				coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, ServiceStatus.STATUS.FAILED.toString(),ex.getMessage());
				coreScheduleJoblog.setSjlName("Notification for contract expiration failed".concat(" - ").concat(sdfDateTime.format(Calendar.getInstance().getTime())));
			}
			
			// save scheduler log when tasks completed/failed
			super.logTask(coreScheduleJoblog);
			log.info("ContractExpiryNotificationScheduler Ended: " + Calendar.getInstance().getTime().toString());
		} catch (Exception ex) {
			log.error("doJob", ex);
			// save scheduler log when tasks have exception
			coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, "EXCEPTION", ExceptionUtils.getStackTrace(ex));
			coreScheduleJoblog.setSjlName("Notification for contract expiration exception".concat(" - ").concat(sdfDateTime.format(Calendar.getInstance().getTime())));
			super.logTask(coreScheduleJoblog);
		}
	}
	
	/**
	 * 
	 * @param expiryDate
	 */
	private void notifyExpireEvent(Date expiryDate) {
		List<CkCtContract> nonExpiredApps = contractService.getAllContractsToExpire(expiryDate);
		for (CkCtContract contract : nonExpiredApps) {
			// Publish event
			NotifyExpireEvent notifyExpireEvent = new NotifyExpireEvent(this, contract, "");
			notifyExpireEvent.setContract(contract);
			notifyExpireEvent.setExpiredDate(sdfDate.format(contract.getConDtEnd()));
			eventPublisher.publishEvent(notifyExpireEvent);
			// Audit here
			audit(contract.getConId(), REMINDER_SENT);
		}
	}

	/**
	 * 
	 * @param key
	 * @param event
	 */
	private void audit(String key, String event) {
		Date now = Calendar.getInstance().getTime();
		try {

			TCoreAuditlog auditLog = new TCoreAuditlog();
			auditLog.setAudtId(CkUtil.generateId("CON"));
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
