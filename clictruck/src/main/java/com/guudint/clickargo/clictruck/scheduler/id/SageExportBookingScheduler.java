package com.guudint.clickargo.clictruck.scheduler.id;

import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.sage.export.excel.service.SageExportExcelBookingService;
import com.guudint.clickargo.clictruck.sage.export.excel.service.SageExportExcelService;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.scheduler.dto.CoreScheduleJoblog;

@Component
@EnableAsync
public class SageExportBookingScheduler extends AbstractSageExportScheduler {

	private static Logger LOG = Logger.getLogger(SageExportBookingScheduler.class);

	@Autowired
	private SageExportExcelBookingService sageExportExcelBookingService;

	public SageExportBookingScheduler() {
		super.setTaskName(this.getClass().getSimpleName());
	}

	// This runs everyday at 2am
	@Override
	@Scheduled(cron = "0 7 0 1,16 * ?")
	public void doJob() throws Exception {
		String taskNo = super.getTaskNo();
		CoreScheduleJoblog coreScheduleJoblog = null;
		try {

			if (!super.isIndonesia()) {
				LOG.info("Not run SageExportBookingScheduler because not ID country.");
				return;
			}
			
			LOG.info("SageScheduler running in " + InetAddress.getLocalHost().getHostName());

			coreScheduleJoblog = super.getTask(TASK_STATE.START, taskNo, TASK_STATE.START.toString());
			
			Calendar now = Calendar.getInstance();
			int d = now.get(Calendar.DAY_OF_MONTH);
			
			Date beginDate = super.getBeginDate(d);
			Date endDate = super.getEndDate();
			
			sageExportExcelBookingService.exportSageExcel(beginDate, endDate);

			String bDate = SageExportExcelService.yyyyMMddSDF.format(beginDate);
			String eDate = SageExportExcelService.yyyyMMddSDF.format(endDate);
			sageExportExcelBookingService.sendEmailToFinance("Please check Sage Excel files [" + bDate + "-" + eDate + "]","");


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
