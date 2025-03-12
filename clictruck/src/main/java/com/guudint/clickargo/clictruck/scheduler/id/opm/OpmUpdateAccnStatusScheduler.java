package com.guudint.clickargo.clictruck.scheduler.id.opm;

import java.net.InetAddress;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.opm.service.impl.OpmUpdateAccnStatusService;
import com.guudint.clickargo.clictruck.scheduler.AbstractClickTruckScheduler;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.scheduler.dto.CoreScheduleJoblog;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Component
@EnableScheduling
@EnableAsync
public class OpmUpdateAccnStatusScheduler extends AbstractClickTruckScheduler {

	// Static Attributes
	private static final Logger log = Logger.getLogger(OpmUpdateAccnStatusScheduler.class);
	
	
	@Autowired
	private OpmUpdateAccnStatusService opmUpdateAccnStatusService;

	public OpmUpdateAccnStatusScheduler() {
		super.setTaskName(this.getClass().getSimpleName());

	}

	@Override
//	@Scheduled(cron = "0 */1 * * * *") /* For testing, run every minute */
	@Scheduled(cron = "0 35 5 * * ?") // second, minute, hour, day, month, year // 05:35 every morning
	@SchedulerLock(name = "OpmUpdateAccnStatusScheduler", lockAtLeastFor = "30s")
	@Transactional
	public synchronized void doJob() throws Exception {
		String taskNo = super.getTaskNo();
		CoreScheduleJoblog coreScheduleJoblog = null;
		try {

			log.info("OpmLoadCsvFileScheduler running in " + InetAddress.getLocalHost().getHostName());

			if (!super.isIndonesia()) {
				log.info("Not run OpmUpdateAccnStatusScheduler because not ID country.");
				return;
			}

			coreScheduleJoblog = super.getTask(TASK_STATE.START, taskNo, TASK_STATE.START.toString());

			String jobResult = opmUpdateAccnStatusService.updateOpmAccnStatus();

			coreScheduleJoblog = super.getTask(TASK_STATE.COMPLETE, taskNo, ServiceStatus.STATUS.COMPLETED.toString(),
					jobResult);

		} catch (Exception e) {
			log.error("OpmLoadCsvFileScheduler", e);
			coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, "EXCEPTION",
					ExceptionUtils.getStackTrace(e));
		} finally {
			super.logTask(coreScheduleJoblog);
		}
	}

	
	
}
