package com.guudint.clickargo.clictruck.opm.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.planexec.job.event.ClicTruckNotifTemplates;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtPlatformInvoiceDao;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoice;
import com.guudint.clickargo.common.service.impl.CkNotificationUtilService;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.can.device.NotificationParam;
import com.vcc.camelone.can.model.TCoreNotificationLog;
import com.vcc.camelone.can.service.impl.NotificationService;
import com.vcc.camelone.util.email.SysParam;

@Service
public class OpmCreditDisbursementEmail2FinanceService {

	private static Logger log = Logger.getLogger(OpmCreditDisbursementEmail2FinanceService.class);

	@Autowired
	CkCtPlatformInvoiceDao platformInvoiceDao;
	@Autowired
	protected SysParam sysParam;
	@Autowired
	protected CkNotificationUtilService notificationUtilService;
	@Autowired
	protected NotificationService notificationService;

	SimpleDateFormat yyyyMMddSDF = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Transactional
	public String sendCdEmail2Finance(Date date) {

		if (null == date) {
			date = new Date();
		}

		try {
			// Load all Platform invoice list;
			List<TCkCtPlatformInvoice> pfInvList = platformInvoiceDao.findByPaidDateAndAccnType(getStartOfDay(date),
					getEndOfDay(date), AccountTypes.ACC_TYPE_TO.name());

			// prepare email body;
			String emailBody = this.buildEmailbody(pfInvList);
			log.info("CD Email body: " + emailBody);

			// send email;
			this.sendDSVEmailNotification("CT2 Credit Disbursement Report[" + yyyyMMddSDF.format(date) + "]",
					emailBody);

		} catch (Exception e) {
			log.error("", e);
		}
		return "";
	}

	private String buildEmailbody(List<TCkCtPlatformInvoice> pfInvList) {

		StringBuilder sb = new StringBuilder();

		pfInvList = pfInvList.stream().sorted((a, b) -> (a.getInvDtPaid().compareTo(b.getInvDtPaid())))
				.collect(Collectors.toList());

		sb.append("    <style>\n" + "        table, th, td {\n" + "            border: 1px solid black;\n"
				+ "            border-collapse: collapse;\n" + "        }\n" + "        th, td {\n"
				+ "            padding: 8px;\n" + "            text-align: left;\n" + "        }\n" + "    </style>");

		sb.append("<table>");
		sb.append("<tr>");
		sb.append("<td>Seq</td>");
		sb.append("<td>Job Id</td>");
		sb.append("<td>TO Account Name</td>");
		sb.append("<td>Time</td>");
		sb.append("<td>Inv NO</td>");
		sb.append("<td>Inv Amt</td>");
		sb.append("<td>Inv Vat</td>");
		sb.append("<td>Inv Stamp Duty</td>");
		sb.append("<td>inv Total Amt</td>");
		sb.append("</tr>");

		for (int i = 0; i < pfInvList.size(); i++) {

			TCkCtPlatformInvoice inv = pfInvList.get(i);

			sb.append("<tr>");
			sb.append("<td>" + (i + 1) + "</td>");
			sb.append("<td>" + inv.getInvJobId() + "</td>");
			sb.append("<td>" + inv.getTCoreAccnByInvTo().getAccnName() + "</td>");
			sb.append("<td>" + getPaidDate(inv.getInvDtPaid()) + "</td>");
			sb.append("<td>" + inv.getInvNo() + "</td>");
			sb.append("<td>" + convert2Long(inv.getInvAmt()) + "</td>");
			sb.append("<td>" + convert2Long(inv.getInvVat()) + "</td>");
			sb.append("<td>" + convert2Long(inv.getInvStampDuty()) + "</td>");
			sb.append("<td>" + convert2Long(inv.getInvTotal()) + "</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");

		return sb.toString();
	}

	@Transactional
	public void sendDSVEmailNotification(String emailSubject, String emailBody) throws Exception {

		try {
			NotificationParam param = new NotificationParam();
			param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
			param.setTemplateId(ClicTruckNotifTemplates.BLANK.getId());

			String recipientStr = sysParam.getValString("CLICTRUCK_FINANCE_EMAIL_RECEIVER", "Zhang.Ji@guud.company");

			HashMap<String, String> contentFields = new HashMap<>();
			contentFields.put(":sp_details", emailBody);

			param.setContentFeilds(contentFields);

			param.setRecipients(new ArrayList<>(Arrays.asList(recipientStr.split(","))));

			HashMap<String, String> subjectFields = new HashMap<>();
			subjectFields.put(":subject", emailSubject);
			param.setSubjectFields(subjectFields);

			TCoreNotificationLog notifLog = notificationUtilService.saveNotificationLog(param.toJson(), null, false);
			log.info("TCoreNotificationLog id: " + notifLog.getNlogId());
			notificationService.sendNotificationsByLogId(notifLog.getNlogId());
			// notificationService.notifySyn(param);

		} catch (Exception e) {
			log.error("Fail to send email." + emailSubject, e);
			throw e;
		}
	}

	private String getPaidDate(Date paidDate) {
		try {
			return dateFormat.format(paidDate);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * remove decimal;
	 * 
	 * @param amt
	 * @return
	 */
	private String convert2Long(BigDecimal amt) {

		if (null == amt) {
			return "";
		}
		try {
			return amt.longValue() + "";
		} catch (Exception e) {
			return amt.toString();
		}
	}

	private static Date getStartOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	private static Date getEndOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

}
