package com.guudint.clickargo.clictruck.sage.export.service;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.clictruck.finacing.dao.CkCtDebitNoteDao;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtDebitNote;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.ClicTruckMiscService;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtPlatformInvoiceDao;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoice;
import com.guudint.clickargo.clictruck.sage.export.dto.CkCtSageExportJson;
import com.guudint.clickargo.clictruck.sage.export.dto.CkCtSageExportJson.ConsumerProviderBooking;
import com.guudint.clickargo.clictruck.sage.export.dto.CkCtSageExportJson.DebitNote;
import com.guudint.clickargo.clictruck.sage.export.dto.CkCtSageExportJson.Invoice;
import com.guudint.clickargo.clictruck.sage.export.dto.CkCtSageExportJson.Item;
import com.guudint.clickargo.clictruck.sage.export.dto.CkCtSageExportOutJson.Provider;
import com.guudint.clickargo.clictruck.sage.service.SageService;
import com.guudint.clickargo.clictruck.sage.service.SageUtil;
import com.guudint.clickargo.master.dao.CoreAccnConfigDao;
import com.guudint.clickargo.master.enums.Currencies;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.vcc.camelone.ccm.model.TCoreAccnConfig;
import com.vcc.camelone.ccm.model.TCoreAccnConfigId;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.util.email.SysParam;
import com.vcc.camelone.util.sftp.model.SFTPConfig;

@Deprecated
public abstract class SageExportJsonService {

	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(SageExportJsonService.class);

	public static final String SAGE_EXPORT_JSON_SERVICE = "ClicTruck";

	public static final String SAGE_EXPORT_JSON_TYPE_BOOK = "book";
	public static final String SAGE_EXPORT_JSON_TYPE_PAY_IN = "payment_in";
	public static final String SAGE_EXPORT_JSON_TYPE_PAY_OUT = "payment_out";

	@Autowired
	ClicTruckMiscService ctMiscService;

	@Autowired
	protected SysParam sysParam;

	@Autowired
	protected CoreAccnConfigDao coreAccnConfigDao;

	@Autowired
	protected CkCtPlatformInvoiceDao ckCtPlatformInvoiceDao;

	@Autowired
	protected CkCtDebitNoteDao ckCtDebitNoteDao;

	SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
	SimpleDateFormat jobApproveDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	SimpleDateFormat yyyyMMddSDF = new SimpleDateFormat("yyyy-MM-dd");

	ObjectMapper objectMapping = new ObjectMapper();

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void exportSageJson(Date date) {

		SageUtil sageUtil = new SageUtil();

		try {
			Date beginTime = sageUtil.getBeginDate(date);
			Date endTime = sageUtil.getEndDate(date);

			String dateFolderName = sdf.format(date);

			// file path
			String parePath = sysParam.getValString("CLICTRUCK_SAGE_JSON", "/home/vcc/sage300/");
			parePath = parePath + dateFolderName;
			Files.createDirectories(Paths.get(parePath));

			// create Json file List
			List<SageExportBookingDto> sageExportBookingDtoList = createSageExportDto(dateFolderName, beginTime,
					endTime);

			// save to storage;
			for (int id = 0; id < sageExportBookingDtoList.size(); id++) {

				SageExportBookingDto bookingDto = sageExportBookingDtoList.get(id);

				Files.write(Paths.get(parePath + "/" + bookingDto.fileName), bookingDto.jsonStr.getBytes());
			}

			// push to SFTP server

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("", e);
		}
	}

	protected abstract List<SageExportBookingDto> createSageExportDto(String dateFolderName, Date beginTime,
			Date endTime) throws Exception;

	protected ConsumerProviderBooking generateConsumerProviderBooking(String accnId, List<TCkCtPlatformInvoice> invList,
			List<TCkCtDebitNote> debitNoteList, boolean isTO) {

		TCoreAccnConfig coAccnConfig = coreAccnConfigDao.getByIdAndStatus(
				new TCoreAccnConfigId(accnId, SageService.ACCN_CONFIG_SAGE_ACCN_ID), Constant.ACTIVE_STATUS);

		if (coAccnConfig == null) {
			Log.error("Fail to find Sage id by accnId:" + accnId);
		}

		String coSageId = (coAccnConfig == null) ? "" : coAccnConfig.getAcfgVal();

		Invoice coInvoice = generateInvoice(invList, accnId, isTO);

		DebitNote coDebitNote = generateDebitNote(debitNoteList, accnId);

		ConsumerProviderBooking consumer = new ConsumerProviderBooking(coSageId, coInvoice, coDebitNote);

		return consumer;
	}

	protected Invoice generateInvoice(List<TCkCtPlatformInvoice> invList, String accnId, boolean isTO) {

		TCkCtPlatformInvoice pfinv = invList.stream()
				.filter(inv -> accnId.equalsIgnoreCase(inv.getTCoreAccnByInvTo().getAccnId())).findFirst()
				.orElseGet(null);

		if (pfinv == null) {
			Log.error("Fail to find invoice: " + accnId);
			return null;
		}
		Invoice inv = new Invoice();
		inv.no = pfinv.getInvId();
		inv.issuedDate = yyyyMMddSDF.format(pfinv.getInvDtIssue());
		inv.ccy = Currencies.IDR.name();
		inv.amount = pfinv.getInvAmt().longValue();
		inv.vat = pfinv.getInvVat().longValue();
		inv.duty = pfinv.getInvStampDuty().longValue();
		inv.taxNo = pfinv.getInvSageTaxNo();
		inv.total = pfinv.getInvTotal().longValue();

		inv.terms = "N" + ((pfinv.getInvDtDue().getTime() - pfinv.getInvDtIssue().getTime()) / (1000 * 3600 * 24));

		if (isTO) {
			inv.amount = -inv.amount;
			inv.vat = -inv.vat;
			inv.duty = -inv.duty;
			inv.total = -inv.total;
		}

		return inv;
	}

	private DebitNote generateDebitNote(List<TCkCtDebitNote> debitNoteList, String accnId) {

		TCkCtDebitNote ctDn = null;

		ctDn = debitNoteList.stream().filter(dn -> (accnId.equalsIgnoreCase(dn.getTCoreAccnByDnTo().getAccnId()))
				|| accnId.equalsIgnoreCase(dn.getTCoreAccnByDnFrom().getAccnId())).findFirst().orElseGet(null);

		DebitNote dn = new DebitNote();
		dn.no = ctDn.getDnId();
		dn.issuedDate = yyyyMMddSDF.format(ctDn.getDnDtIssue());
		dn.ccy = Currencies.IDR.name();
		dn.amount = ctDn.getDnAmt().longValue();
		dn.duty = ctDn.getDnStampDuty().longValue();
		dn.total = ctDn.getDnTotal().longValue();

		dn.terms = "N" + ((ctDn.getDnDtDue().getTime() - ctDn.getDnDtIssue().getTime()) / (1000 * 3600 * 24));

		return dn;
	}

	protected long getTotal(Provider provider) {

		long total = 0;
		// change to Lamda express?
		for (Item item : provider.items) {
			total = total + (item.debitNote.total + item.invoice.total);
		}

		return total;
	}
	
	public static synchronized void store(SFTPConfig config, String path, List<File> files) {

		JSch jsch = new JSch();
		Session session = null;
		try {
			session = jsch.getSession(config.getUserId(), config.getHost(), 22);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(config.getPassword());
			session.connect();

			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			for (File file : files) {
				FileInputStream fis = new FileInputStream(file);
				sftpChannel.put(fis, config.getPath() + "/" + path + "/" + file.getName()); // File.separator
				fis.close();
			}
			sftpChannel.exit();
			session.disconnect();
		} catch (Exception ex) {
			LOG.error("Fail to store to SFTP", ex);
		}
	}

	public class SageExportBookingDto {

		public int id;
		public String fileName;
		public CkCtSageExportJson sageExportJson;
		public String jsonStr;

		public SageExportBookingDto() {
		}

		public SageExportBookingDto(int id, String fileName, CkCtSageExportJson sageExportJson) {
			super();
			this.id = id;
			this.fileName = fileName;
			this.sageExportJson = sageExportJson;
		}

		@Override
		public String toString() {
			return "SageExportBookingDto [id=" + id + ", fileName=" + fileName + ", sageExportJson=" + sageExportJson
					+ ", jsonStr=" + jsonStr + "]";
		}

	}

}
