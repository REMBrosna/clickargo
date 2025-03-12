package com.guudint.clickargo.clictruck.scheduler.id;

import java.net.InetAddress;
import java.util.Calendar;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.sage.export.excel.service.SageExportExcelPayoutService;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.scheduler.dto.CoreScheduleJoblog;

@Component
@EnableAsync
public class SageExportPayoutScheduler extends AbstractSageExportScheduler {

	private static Logger LOG = Logger.getLogger(SageExportPayoutScheduler.class);

	@Autowired
	private SageExportExcelPayoutService sageExportExcelPayoutService;

	public SageExportPayoutScheduler() {
		super.setTaskName(this.getClass().getSimpleName());
	}

	// This runs everyday at 2am
	@Override
	@Scheduled(cron = "0 27 0 1,16 * ?") // second, minute, hour, day, month, year
	public void doJob() throws Exception {
		String taskNo = super.getTaskNo();
		CoreScheduleJoblog coreScheduleJoblog = null;
		try {

			if (!super.isIndonesia()) {
				LOG.info("Not run SageExportPayoutScheduler because not ID country.");
				return;
			}

			LOG.info("SageScheduler running in " + InetAddress.getLocalHost().getHostName());

			coreScheduleJoblog = super.getTask(TASK_STATE.START, taskNo, TASK_STATE.START.toString());
			
			Calendar now = Calendar.getInstance();
			int d = now.get(Calendar.DAY_OF_MONTH);
			
			sageExportExcelPayoutService.exportSageExcel(super.getBeginDate(d), super.getEndDate());


			coreScheduleJoblog = super.getTask(TASK_STATE.COMPLETE, taskNo, ServiceStatus.STATUS.COMPLETED.toString(),
					"SUCCESS");

		} catch (Exception e) {
			LOG.error("TruckOperatorPayoutScheduler", e);
			coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, "EXCEPTION",
					ExceptionUtils.getStackTrace(e));
			throw e;
		} finally {
			super.logTask(coreScheduleJoblog);
		}
	}

}
