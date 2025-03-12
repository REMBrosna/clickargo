package com.guudint.clickargo.clictruck.scheduler.sg;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.dsv.service.IDsvService;
import com.guudint.clickargo.clictruck.dsv.service.impl.DsvShipmentService;
import com.guudint.clickargo.clictruck.scheduler.AbstractClickTruckScheduler;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.scheduler.dto.CoreScheduleJoblog;
import com.vcc.camelone.util.email.SysParam;
import com.vcc.camelone.util.sftp.model.SFTPConfig;

/**
 * Process XML files when DSV Air/Sea put XML files in SFTP folder.
 *
 */
@Component
@EnableAsync
public class DSVLoadXMLFileScheduler extends AbstractClickTruckScheduler {

	private static Logger log = Logger.getLogger(DSVLoadXMLFileScheduler.class);

	@Autowired
	private IDsvService dsvService;

	@Autowired
	protected SysParam sysParam;

	@Autowired
	DsvShipmentService dsvServiceAuxiliary;

	public DSVLoadXMLFileScheduler() {
		super.setTaskName(this.getClass().getSimpleName());
	}

	@Override
//	@Scheduled(cron = "0 */5 * * * ?") // second, minute, hour, day, month, year // per 5 minutes;
	public synchronized void doJob() throws Exception {
		String taskNo = super.getTaskNo();
		CoreScheduleJoblog coreScheduleJoblog = null;
		try {

			log.info("DSVLoadXMLFileScheduler running in " + InetAddress.getLocalHost().getHostName());

			if (!super.isSingapore()) {
				log.info("Skipping DSVLoadXMLFileScheduler because the country is not Singapore.");
				coreScheduleJoblog = super.getTask(TASK_STATE.COMPLETE, taskNo, ServiceStatus.STATUS.COMPLETED.toString());
				return;
			}

			log.info("Task starting: " + taskNo);
			coreScheduleJoblog = super.getTask(TASK_STATE.START, taskNo, TASK_STATE.START.toString());

			String jobResult = this.doTask();
			log.info("Task completed: " + taskNo + " with result: " + jobResult);
			coreScheduleJoblog = super.getTask(TASK_STATE.COMPLETE, taskNo, ServiceStatus.STATUS.COMPLETED.toString(),
					jobResult);

		} catch (Exception e) {
			log.error("Error occurred in DSVLoadXMLFileScheduler, taskNo: " + taskNo, e);
			coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, "EXCEPTION",
					ExceptionUtils.getStackTrace(e));
		} finally {
			log.info("Logging task state for taskNo: " + taskNo + " with state: " + coreScheduleJoblog);
			super.logTask(coreScheduleJoblog);
		}
	}

	private String doTask() throws Exception {
		log.info("doTask");
		SFTPConfig sftpConfig = dsvServiceAuxiliary.getDsvSftpConfig();

		String parentPath = dsvServiceAuxiliary.getLocalDsvPath();

		List<String> successList = new ArrayList<>();
		List<String> failList = new ArrayList<>();

		if (sftpConfig != null) {

			// download 50 files together
			List<File> files = dsvService.loadFilesFromSftp(sftpConfig, parentPath);

			log.info("files: " + files);

			if (files != null && files.size() > 0) {

				log.info("file.size() " + files.size());

				for (File file : files) {

					try {
						// process 1 by 1
						log.info(file.getAbsolutePath() + "xml file path:");
						dsvService.processDsvFile(file);

						log.info(file.getAbsolutePath() + " begin remove file ");
						dsvService.mvFile2HisotoryFolder(sftpConfig, file);
						// success
						successList.add(file.getName());

					} catch (Exception e) {
						// failed
						log.error("Fail to process " + file.getName(), e);
						failList.add(file.getName() + " " + e.getMessage());
					}
				}
			}
		}
		if (failList.size() > 0) {
			throw new Exception(failList.toString() + " ;;; " + successList.toString());
		}
		return successList.size() + " ;;; " + successList.toString();
	}
}
