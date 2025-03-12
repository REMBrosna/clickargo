package com.guudint.clickargo.clictruck.scheduler.id;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.guudint.clickargo.clictruck.finacing.dao.CkCtDebitNoteDao;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtDebitNote;
import com.guudint.clickargo.clictruck.finacing.service.IPaymentService.InvoiceTypes;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtPlatformInvoiceDao;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoice;
import com.guudint.clickargo.clictruck.scheduler.AbstractClickTruckScheduler;
import com.guudint.clickargo.clictruck.util.FileUtil;
import com.guudint.clickargo.estamp.dto.AuthTokenResponse;
import com.guudint.clickargo.estamp.dto.CkEstampDoc;
import com.guudint.clickargo.estamp.dto.StampDownloadResponse;
import com.guudint.clickargo.estamp.model.TCkEstampDoc;
import com.guudint.clickargo.external.services.IStampGateway;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.config.model.TCoreSysparam;
import com.vcc.camelone.scheduler.dto.CoreScheduleJoblog;

import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Component
@EnableScheduling
@EnableAsync
public class ClictruckEStampDownloadScheduler extends AbstractClickTruckScheduler {
	private static final Logger log = Logger.getLogger(ClictruckEStampDownloadScheduler.class);

	@Autowired
	IStampGateway stampGateway;

	@Autowired
	GenericDao<TCkEstampDoc, String> ckEstampDocDao;

	@Autowired
	private CkCtDebitNoteDao ckCtDebitNoteDao;

	@Autowired
	private CkCtPlatformInvoiceDao ckCtPlatformInvoiceDao;

	@Autowired
	@Qualifier("coreSysparamDao")
	protected GenericDao<TCoreSysparam, String> coreSysparamDao;

	public ClictruckEStampDownloadScheduler() {
		super.setTaskName(this.getClass().getSimpleName());

	}

	/*
	 * At least one instance of the task should run, a delay between finish time of
	 * execution and start time of the next execution of the task. Delay by 5
	 * seconds
	 */
	@Scheduled(fixedDelay = 5 * 60 * 1000)
	@Transactional
	@Override
	@SchedulerLock(name = "ClictruckEStampDownloadScheduler", lockAtLeastFor = "60s")
	public void doJob() throws Exception {
		
		LockAssert.assertLocked();
		
		String taskNo = super.getTaskNo();
		CoreScheduleJoblog coreScheduleJoblog = null;
		try {

			if (!super.isIndonesia()) {
				log.info("Do not run ClictruckEStampDownloadScheduler because not ID country.");
				return;
			}

			coreScheduleJoblog = super.getTask(TASK_STATE.START, taskNo, TASK_STATE.START.toString());
			log.debug("ClictruckEStampDownloadScheduler Started: " + Calendar.getInstance().getTime().toString());

			try {
				String basePath = getSysParam(CtConstant.KEY_ATTCH_BASE_LOCATION);
				DetachedCriteria criteria = DetachedCriteria.forClass(TCkEstampDoc.class);
				criteria.add(Restrictions.eq("esdStampStatus", CkEstampDoc.Status.SUCCESS.name()));
				List<TCkEstampDoc> tCkEstampDocs = ckEstampDocDao.getByCriteria(criteria);
				
				// login first before proceed, this is to keep the refreshh token updated
				AuthTokenResponse auth = stampGateway.login();
				
				if (tCkEstampDocs != null && tCkEstampDocs.size() > 0) {
					for (TCkEstampDoc eDoc : tCkEstampDocs) {
						StampDownloadResponse response = stampGateway.stampDocumentDownload(eDoc, auth);
						if (response.getContent() != null && response.getContent().length > 0) {
							// Query from the respective table to determine the job this invoice is
							// associated to
							// Parent Folder, init to the invId (dn_no or pf_no)
							String jobFolder = eDoc.getEsdInvId();
							if (eDoc.getEsdInvType().equalsIgnoreCase(InvoiceTypes.DEBIT_NOTE.name())) {
								TCkCtDebitNote dn = ckCtDebitNoteDao.find(eDoc.getEsdInvId());
								jobFolder = dn.getDnJobId();
							} else if (eDoc.getEsdInvType().equalsIgnoreCase(InvoiceTypes.PLATFORM_FEE.name())) {
								TCkCtPlatformInvoice pf = ckCtPlatformInvoiceDao.find(eDoc.getEsdInvId());
								jobFolder = pf.getInvJobId();
							}

							FileUtil.saveAttachment(jobFolder, basePath, response.getFilename(), response.getContent());
						}
					}
				}

				coreScheduleJoblog = super.getTask(TASK_STATE.COMPLETE, taskNo,
						ServiceStatus.STATUS.COMPLETED.toString(), "SUCCESS");
			} catch (Exception ex) {
				coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, ServiceStatus.STATUS.FAILED.toString(),
						ex.getMessage());
			}

			super.logTask(coreScheduleJoblog);
			log.debug("ClictruckEStampDownloadScheduler Ended: " + Calendar.getInstance().getTime().toString());
		} catch (Exception ex) {
			log.error("doJob", ex);
			coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, "EXCEPTION",
					ExceptionUtils.getStackTrace(ex));
			super.logTask(coreScheduleJoblog);
		}
	}

	protected String getSysParam(String key) throws Exception {
		if (StringUtils.isBlank(key))
			throw new ParameterException("param key null or empty");

		TCoreSysparam sysParam = coreSysparamDao.find(key);
		if (sysParam != null) {
			return sysParam.getSysVal();
		}

		throw new EntityNotFoundException("sys param config " + key + " not set");
	}
}
