package com.guudint.clickargo.clictruck.scheduler.id;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clicservice.dao.CkSuspensionLogDao;
import com.guudint.clickargo.clicservice.model.TCkSuspensionLog;
import com.guudint.clickargo.clictruck.finacing.event.AccountSuspensionEvent;
import com.guudint.clickargo.clictruck.finacing.service.IPlatformInvoiceService;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtPlatformInvoice;
import com.guudint.clickargo.clictruck.scheduler.AbstractClickTruckScheduler;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.vcc.camelone.ccm.dao.CoreAccnDao;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.audit.dto.CoreAuditlog;
import com.vcc.camelone.common.audit.service.impl.COAuditLogService;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.config.model.TCoreSysparam;
import com.vcc.camelone.master.dto.MstAccnType;
import com.vcc.camelone.scheduler.dto.CoreScheduleJoblog;

import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

/**
 * Scheduler to monitor unpaid invoices and suspend relavant CO/FF accounts. 
 */
@Component
@EnableScheduling
@EnableAsync
public class ClicTruckUnpaidInvMonitoringScheduler extends AbstractClickTruckScheduler {

	// Static Attributes
	private static final Logger log = Logger.getLogger(ClicTruckUnpaidInvMonitoringScheduler.class);
	private static final String KEY_DAYS_FROM_DUEDT_SUSPENSION = "CLICTRUCK_DAYS_FROM_DUEDT";
	private static char STATUS_SUSPENDED = 'S';

	@Autowired
	@Qualifier("coreSysparamDao")
	private GenericDao<TCoreSysparam, String> coreSysparamDao;

	@Autowired
	private CoreAccnDao accnDao;

	@Autowired
	protected ApplicationEventPublisher eventPublisher;

	@Autowired
	private IPlatformInvoiceService platformFeeService;

	@Autowired
	private COAuditLogService auditLogService;

	@Autowired
	private CkSuspensionLogDao ckSuspensionLogDao;

	SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public ClicTruckUnpaidInvMonitoringScheduler() {
		super.setTaskName(this.getClass().getSimpleName());

	}

	@Override
//	@Scheduled(cron = "0 */1 * * * *") /* For testing, run every minute */
	@Scheduled(cron = "0 0 0 * * *") /* Run once daily */
	@SchedulerLock(name = "ClicTruckUnpaidInvMonitoringScheduler", lockAtLeastFor = "30s")
	@Transactional
	public void doJob() throws Exception {
		log.debug("doJob");

		log.info("ClicTruckUnpaidInvMonitoringScheduler running in " + InetAddress.getLocalHost().getHostName());

		LockAssert.assertLocked();

		String taskNo = super.getTaskNo();
		CoreScheduleJoblog coreScheduleJoblog = null;

		try {

			if (!super.isIndonesia()) {
				log.info("Do not run ClicTruckUnpaidInvMonitoringScheduler because not ID country.");
				return;
			}

			coreScheduleJoblog = super.getTask(TASK_STATE.START, taskNo, TASK_STATE.START.toString());
			log.info("ClicTruckUnpaidInvMonitoringScheduler Started: " + Calendar.getInstance().getTime().toString());

			try {
				updateAccountStatusToSuspended();
				coreScheduleJoblog = super.getTask(TASK_STATE.COMPLETE, taskNo,
						ServiceStatus.STATUS.COMPLETED.toString(), "SUCCESS");
			} catch (Exception ex) {
				coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, ServiceStatus.STATUS.FAILED.toString(),
						ex.getMessage());
			}

			super.logTask(coreScheduleJoblog);
			log.info("ClicTruckUnpaidInvMonitoringScheduler Ended: " + Calendar.getInstance().getTime().toString());
		} catch (Exception ex) {
			log.error("doJob", ex);
			coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, "EXCEPTION",
					ExceptionUtils.getStackTrace(ex));
			super.logTask(coreScheduleJoblog);
		}
	}

	/**
	 * Suspend CO Accounts X interval days after DN Due Date
	 * 
	 * @throws Exception
	 */
	private void updateAccountStatusToSuspended() throws Exception {

		Calendar cal = Calendar.getInstance();
		// Current date - (no. of days the inv. due date is considered unpaid)
		cal.add(Calendar.DATE, -getSysParamVal(KEY_DAYS_FROM_DUEDT_SUSPENSION, 1));
		Date expiryDate = cal.getTime();

		// Get a list of all invoices that will be suspended, CO/FF only
		List<CkCtPlatformInvoice> platformInvoices = platformFeeService.getDueInvoicesToDate(expiryDate,
				AccountTypes.ACC_TYPE_CO.name(), AccountTypes.ACC_TYPE_FF.name());

		Map<String, List<String>> mapDueInvByAccn = new HashMap<>();

		if (platformInvoices != null && platformInvoices.size() > 0) {
			for (CkCtPlatformInvoice platformInvoice : platformInvoices) {

				mapDueInvByAccn.compute(platformInvoice.getTCoreAccnByInvTo().getAccnId(),
						(accnId, listUnpaidInvoices) -> {

							if (Objects.isNull(listUnpaidInvoices)) {
								listUnpaidInvoices = new ArrayList<String>();
							}

							listUnpaidInvoices.add(platformInvoice.getInvNo());

							return listUnpaidInvoices;
						});
			}
		}

		if (!mapDueInvByAccn.isEmpty()) {
			for (Map.Entry<String, List<String>> entry : mapDueInvByAccn.entrySet()) {
				String accnId = entry.getKey();
				TCoreAccn accnTmp = accnDao.find(accnId);
				if(accnTmp==null) {
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

				CoreAuditlog audit = new CoreAuditlog();
				audit.setAudtReckey(accnTmp.getAccnId());
				audit.setAudtEvent("ACCN MODIFY");
				audit.setAudtRemarks("Suspended due to unpaid invoices.");
				audit.setAudtAccnid("SYS");
				audit.setAudtUid("SYS");
				auditLogService.log(audit);

				this.addSuspensionLogs(accnId, StringUtils.join(entry.getValue(), ", "));
				
				AccountSuspensionEvent accountSuspensionEvent = new AccountSuspensionEvent(this, coreAccnDto, null, "UNPAID_INVOICE");
				eventPublisher.publishEvent(accountSuspensionEvent);
			}

		}

	}

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

	/**
	 * 
	 * @param key
	 * @param defVal
	 * @return
	 */
	private Integer getSysParamVal(String key, Integer defVal) {
		try {
			TCoreSysparam sys = coreSysparamDao.find(key);
			if (sys != null)
				return Integer.valueOf(sys.getSysVal());
		} catch (Exception e) {
			log.error("getSysParamVal", e);
			return defVal;
		}

		return defVal;
	}

}
