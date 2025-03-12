package com.guudint.clickargo.clictruck.scheduler.id;

import java.net.InetAddress;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.guudint.clickargo.clictruck.sage.service.SageService;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.scheduler.dto.CoreScheduleJoblog;
import com.vcc.camelone.scheduler.service.AbstractJob;

//@Component
//@EnableAsync
@Deprecated
public class SageScheduler extends AbstractJob {

	private static Logger LOG = Logger.getLogger(SageScheduler.class);

	@Autowired
	private SageService sageService;

	public SageScheduler() {
		super.setTaskName(this.getClass().getSimpleName());
	}

	// This runs everyday at 2am
	@Override
	//@Scheduled(cron = "0 0 2 * * ?")
	public void doJob() throws Exception {
		String taskNo = super.getTaskNo();
		CoreScheduleJoblog coreScheduleJoblog = null;
		try {

			LOG.info("SageScheduler running in " + InetAddress.getLocalHost().getHostName());

			coreScheduleJoblog = super.getTask(TASK_STATE.START, taskNo, TASK_STATE.START.toString());
			Date now = Calendar.getInstance().getTime();

			this.generateSageYesterday(now);

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

	private void generateSageYesterday(Date date) throws Exception {

		String xlsFileName = sageService.getFileName(new Date());

		Date bDate = this.getBeginDate(date);
		Date eDate = this.getEndnDate(date);

		// store excel in db.
		sageService.getExcelReport(bDate, eDate, xlsFileName);

	}

	/**
	 * 
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	private Date getBeginDate(Date date) throws ParseException {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();

	}

	/**
	 * 
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	private Date getEndnDate(Date date) throws ParseException {

		Date bDate = this.getBeginDate(date);

		Calendar cal = Calendar.getInstance();
		cal.setTime(bDate);
		cal.add(Calendar.DAY_OF_YEAR, 1);
		cal.add(Calendar.SECOND, -1);

		return cal.getTime();
	}
}
