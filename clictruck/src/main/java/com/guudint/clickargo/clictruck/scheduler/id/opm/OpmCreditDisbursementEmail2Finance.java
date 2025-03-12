package com.guudint.clickargo.clictruck.scheduler.id.opm;

import java.net.InetAddress;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.opm.service.impl.OpmCreditDisbursementEmail2FinanceService;
import com.guudint.clickargo.clictruck.scheduler.AbstractClickTruckScheduler;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.scheduler.dto.CoreScheduleJoblog;

/*
 * Send an email to the GLI finance team at 7 PM every day. The email body should include all CD records for that day.
 */
@Component
@EnableAsync
public class OpmCreditDisbursementEmail2Finance extends AbstractClickTruckScheduler {

	private static Logger log = Logger.getLogger(OpmLoadCsvFileScheduler.class);
	
	@Autowired
	OpmCreditDisbursementEmail2FinanceService opmCreditDisbursementEmail2FinanceService;

	public OpmCreditDisbursementEmail2Finance() {
		super.setTaskName(this.getClass().getSimpleName());
	}

	@Override
	@Scheduled(cron = "0 0 19 * * ?") // second, minute, hour, day, month, year // per 5 minutes;
	public synchronized void doJob() throws Exception {
		String taskNo = super.getTaskNo();
		CoreScheduleJoblog coreScheduleJoblog = null;
		try {

			log.info("OpmCreditDisbursementEmail2Finance running in " + InetAddress.getLocalHost().getHostName());

			if (!super.isIndonesia()) {
				log.info("Not run OpmCreditDisbursementEmail2Finance because not ID country.");
				return;
			}

			coreScheduleJoblog = super.getTask(TASK_STATE.START, taskNo, TASK_STATE.START.toString());

			String jobResult = opmCreditDisbursementEmail2FinanceService.sendCdEmail2Finance(null);

			coreScheduleJoblog = super.getTask(TASK_STATE.COMPLETE, taskNo, ServiceStatus.STATUS.COMPLETED.toString(),
					jobResult);

		} catch (Exception e) {
			log.error("OpmCreditDisbursementEmail2Finance", e);
			coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, "EXCEPTION",
					ExceptionUtils.getStackTrace(e));
		} finally {
			super.logTask(coreScheduleJoblog);
		}
	}

}
