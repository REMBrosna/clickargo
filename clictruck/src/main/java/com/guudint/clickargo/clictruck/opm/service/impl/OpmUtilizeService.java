package com.guudint.clickargo.clictruck.opm.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clicdo.common.service.CkCtCommonService;
import com.guudint.clickargo.clictruck.dsv.service.impl.DsvJobPhotoService;
import com.guudint.clickargo.clictruck.finacing.dao.CkCtToInvoiceDao;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtToInvoice;
import com.guudint.clickargo.clictruck.opm.OpmConstants;
import com.guudint.clickargo.clictruck.opm.OpmConstants.OPM_OPT;
import com.guudint.clickargo.clictruck.opm.OpmConstants.OPM_TRACK_RADIUS;
import com.guudint.clickargo.clictruck.opm.dao.CkOpmSftpLogDao;
import com.guudint.clickargo.clictruck.opm.model.TCkOpmSftpLog;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckDao;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtPlatformInvoiceDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDoAttachDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDoDao;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoice;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripDo;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripDoAttach;
import com.guudint.clickargo.clictruck.sage.service.SageUtil;
import com.guudint.clickargo.clictruck.track.dao.CkCtTrackLocDao;
import com.guudint.clickargo.clictruck.track.model.TCkCtTrackLoc;
import com.guudint.clickargo.clictruck.track.model.TCkCtTrackLocItem;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.master.dao.MstBankDao;
import com.vcc.camelone.util.email.SysParam;
import com.vcc.camelone.util.sftp.SFTPUtil;
import com.vcc.camelone.util.sftp.model.SFTPConfig;
import com.vcc.camelone.util.sftp.model.SFTPStatus;

@Service
public class OpmUtilizeService {

	private static Logger log = Logger.getLogger(OpmUtilizeService.class);

	SimpleDateFormat yyyyMMddSDF = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmss");

	@Autowired
	CkJobTruckDao ckJobTruckDao;

	@Autowired
	SysParam sysParam;

	@Autowired
	MstBankDao bankDao;

	@Autowired
	CkOpmSftpLogDao ckOpmSftpLogDao;

	@Autowired
	OpmSftpService opmSftpService;

	@Autowired
	private CkCtCommonService ckCtCommonService;

	@Autowired
	private CkCtToInvoiceDao ckCtToInvoiceDao;

	@Autowired
	private CkCtPlatformInvoiceDao platformInvoiceDao;

	@Autowired
	private CkCtTripDao ckCtTripDao;

	@Autowired
	private CkCtTripDoDao ckCtTripDoDao;

	@Autowired
	private CkCtTripDoAttachDao ckCtTripDoAttachDao;

	@Autowired
	private CkCtTrackLocDao ckCtTrackLocDao;

	@Autowired
	private DsvJobPhotoService jobPhotoService;

	@Transactional(propagation = Propagation.REQUIRED)
	public String pushUtilizeFile2Bank(Date date) {

		String jobResult = null;

		try {

			if (null == date) {
				// date is yesterday
				date = new Date();
				date = new SageUtil().getYesterdayBeginDate(date);
			}

			// 1: list all jobs
			List<TCkJobTruck> jobTruckList = ckJobTruckDao.findByFinancerAndDocVerifyDate(
					Arrays.asList(OPM_OPT.OC.name(), OPM_OPT.OT.name()), new SimpleDateFormat("yyyyMMdd").format(date));

			// split by Bank
			Map<String, List<TCkJobTruck>> jotTruckMap = jobTruckList.stream()
					.collect(Collectors.groupingBy(TCkJobTruck::getJobFinancer));

			for (Map.Entry<String, List<TCkJobTruck>> entry : jotTruckMap.entrySet()) {

				TCkOpmSftpLog opmLog = new TCkOpmSftpLog(CkUtil.generateId(TCkOpmSftpLog.PREFIX_ID));

				try {

					String bankId = entry.getKey();
					log.info("bank id: " + bankId);

					List<TCkJobTruck> jotTruckBankList = entry.getValue();

					//
					opmLog.setOpmslFinancer(bankId);
					opmLog.setOpmslDirection(OpmConstants.OPM_SFTP_DIRECTION_OUT);
					opmLog.setOpmslFileType(OpmConstants.OPM_CSVFILE_PREFIX_UTILIZATION);
					//

					if (jotTruckBankList != null && jotTruckBankList.size() > 0) {
						// 2: generate excel file

						String accnIdList = jotTruckBankList.stream()
								.map(jt -> jt.getTCoreAccnByJobPartyTo().getAccnId()).distinct()
								.collect(Collectors.joining(","));

						String jobIdList = jotTruckBankList.stream().map(jt -> jt.getJobId()).distinct()
								.collect(Collectors.joining(","));

						opmLog.setOpmslAccnIds(accnIdList);
						opmLog.setOpmslJobIds(jobIdList);

						String utilizeFilePath = this.generateUtilizeFile(jobTruckList);
						File utilizeFile = new File(utilizeFilePath);

						opmLog.setOpmslFileName(utilizeFile.getName());
						opmLog.setOpmslFilePath(utilizeFilePath);
						opmLog.setOpmslFileSize(Long.valueOf(utilizeFile.length()).intValue());
						opmLog.setOpmslDtFileCreate(new Date());

						// 3: push Excel to SFTP
						this.putFile2Sftp(Arrays.asList(utilizeFile), bankId);

						opmLog.setOpmslDtFilePush2sftp(new Date());

						// 4: push ePod to SFTP
						for (TCkJobTruck jobTruck : jobTruckList) {

							List<String> epodList = this.listEpodFileList(bankId, jobTruck.getJobId());
							if (epodList != null && epodList.size() > 0) {

								List<File> epodFileList = epodList.stream().map(file -> new File(file))
										.collect(Collectors.toList());

								this.putFile2Sftp(epodFileList, bankId);
							}
						}
						// 5: push INV to SFTP
						for (TCkJobTruck jobTruck : jobTruckList) {

							String invFile = this.getInvFile(bankId, jobTruck.getJobId());

							if (StringUtils.isNoneBlank(invFile)) {
								this.putFile2Sftp(Arrays.asList(new File(invFile)), bankId);
							}
						}
						// 6: update job_truck table.
						List<String> jobTruckIdList = jotTruckBankList.stream().map(jt -> jt.getJobId())
								.collect(Collectors.toList());

						ckJobTruckDao.updateUtilizeDate(jobTruckIdList);

						opmLog.setOpmslStatus(Constant.ACTIVE_STATUS);
					}
				} catch (Exception e) {

					log.error("", e);
					String msg = e.getMessage();
					opmLog.setOpmslRemark((msg.length() > 1000) ? msg.substring(0, 1000) : msg);
					opmLog.setOpmslStatus(Constant.INACTIVE_STATUS);

				} finally {
					try {
						opmLog.setOpmslDtCreate(new Date());
						opmLog.setOpmslUidCreate("Scheduler");

						ckOpmSftpLogDao.add(opmLog);
					} catch (Exception e) {
						log.error("", e);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			jobResult = e.getMessage();
			return jobResult;
		}

		return jobResult;
	}

	private String generateUtilizeFile(List<TCkJobTruck> jobTruckList) throws Exception {

		try {

			TCkJobTruck jtTmp = jobTruckList.get(0);

			String filePath = this.getOpsAbsoluteFileName(jtTmp.getJobFinancer(),
					OpmConstants.OPM_CSVFILE_PREFIX_UTILIZATION, null);

			// List<TCkCtMstOpmRate> opmRateList = ckCtMstOpmRateDao.getAll();

			// workbook object
			XSSFWorkbook workbook = new XSSFWorkbook();

			// spreadsheet object
			XSSFSheet spreadsheet = workbook.createSheet("Credit Utilize");

			spreadsheet.setColumnWidth(0, 256 * 20);
			spreadsheet.setColumnWidth(1, 256 * 20);
			spreadsheet.setColumnWidth(2, 256 * 10);
			spreadsheet.setColumnWidth(3, 256 * 10);
			spreadsheet.setColumnWidth(4, 256 * 10);
			spreadsheet.setColumnWidth(5, 256 * 25);
			spreadsheet.setColumnWidth(6, 256 * 20);

			// Invoice
			spreadsheet.setColumnWidth(7, 256 * 10);
			spreadsheet.setColumnWidth(8, 256 * 10);
			spreadsheet.setColumnWidth(9, 256 * 15);
			spreadsheet.setColumnWidth(10, 256 * 40);

			// Pod
			spreadsheet.setColumnWidth(11, 256 * 15);
			spreadsheet.setColumnWidth(12, 256 * 40);

			// GPS
			spreadsheet.setColumnWidth(13, 256 * 10);
			spreadsheet.setColumnWidth(14, 256 * 20);
			spreadsheet.setColumnWidth(15, 256 * 10);
			spreadsheet.setColumnWidth(16, 256 * 20);

			// creating a row object
			XSSFRow row;
			int rowid = 0;
			row = spreadsheet.createRow(rowid++);

			int cellid = 0;
			row.createCell(cellid++).setCellValue("tax_no_debtor");
			row.createCell(cellid++).setCellValue("tax_no_beneficiary");
			row.createCell(cellid++).setCellValue("total"); // job total amount
			row.createCell(cellid++).setCellValue("tenor");
			row.createCell(cellid++).setCellValue("platform_fee");
			row.createCell(cellid++).setCellValue("cargo_owner");
			row.createCell(cellid++).setCellValue("remark");

			row.createCell(cellid++).setCellValue("invoice.no");
			row.createCell(cellid++).setCellValue("invoice.issue_date");
			row.createCell(cellid++).setCellValue("invoice.amt");
			row.createCell(cellid++).setCellValue("invoice.document");

			row.createCell(cellid++).setCellValue("pod.no");
			row.createCell(cellid++).setCellValue("pod.document");

			row.createCell(cellid++).setCellValue("gps[0].resolution");
			row.createCell(cellid++).setCellValue("gps[0].timestamp");
			row.createCell(cellid++).setCellValue("gps[1].resolution");
			row.createCell(cellid++).setCellValue("gps[1].timestamp");

			// writing the data into the sheets...

			for (TCkJobTruck jobTruck : jobTruckList) {

				// fetch invoice;
				List<TCkCtToInvoice> tCkCtToInvoices = ckCtToInvoiceDao.findByJobId(jobTruck.getJobId());
				String invNo = "";
				Date invIssueDate = null;
				if (tCkCtToInvoices != null && tCkCtToInvoices.size() > 0) {
					invNo = tCkCtToInvoices.get(0).getInvNo();
					invIssueDate = tCkCtToInvoices.get(0).getInvDtIssue();
				}
				// fetch DO
				String doNo = "";
				List<TCkCtTrip> tripList = ckCtTripDao.findByJobId(jobTruck.getJobId());
				if (tripList != null && tripList.size() > 0) {
					List<TCkCtTripDo> tripDoList = ckCtTripDoDao.findByTripId(tripList.get(0).getTrId());
					if (tripDoList != null && tripDoList.size() > 0) {
						doNo = tripDoList.get(0).getDoNo();
					}
				}
				// Track and Trace
				Map<Integer, TCkCtTrackLocItem> itemMap = this.getTruckGps(jobTruck.getJobId());

				String item30Gps = itemMap.getOrDefault(OPM_TRACK_RADIUS.RADIUS_30.getRadius(), new TCkCtTrackLocItem())
						.getTliGps();
				String item100Gps = itemMap
						.getOrDefault(OPM_TRACK_RADIUS.RADIUS_100.getRadius(), new TCkCtTrackLocItem()).getTliGps();

				//
				row = spreadsheet.createRow(rowid++);

				cellid = 0;
				BigDecimal totalAmt = jobTruck.computeTotalAmt();
				totalAmt = totalAmt.setScale(0, RoundingMode.HALF_UP);

				BigDecimal platFormFee = this.computePlatFormFeeFromBank(jobTruck);
				platFormFee = platFormFee.setScale(0, RoundingMode.HALF_UP);

				if (this.isOcTruck(jobTruck)) {
					// OC
					row.createCell(cellid++).setCellValue(jobTruck.getTCoreAccnByJobPartyCoFf().getAccnCoyRegn());
				} else {
					// OT
					row.createCell(cellid++).setCellValue(jobTruck.getTCoreAccnByJobPartyTo().getAccnCoyRegn());
				}
				row.createCell(cellid++).setCellValue(jobTruck.getTCoreAccnByJobPartyTo().getAccnCoyRegn());
				row.createCell(cellid++).setCellValue(totalAmt.toString());
				row.createCell(cellid++).setCellValue(30); // payment term
				row.createCell(cellid++).setCellValue(platFormFee.toString()); // compute amount from OCBC
				row.createCell(cellid++).setCellValue(jobTruck.getTCoreAccnByJobPartyCoFf().getAccnName());
				row.createCell(cellid++).setCellValue(jobTruck.getJobId());

				// INV
				row.createCell(cellid++).setCellValue(invNo);

				Cell cell = row.createCell(cellid++);
				cell.setCellStyle(this.getDateStyle(workbook));
				cell.setCellValue(invIssueDate);

				row.createCell(cellid++).setCellValue(jobTruck.getJobTotalCharge().toString()); // TO invoice amount is
																								// same as
																								// jobTotalCharge()
				row.createCell(cellid++).setCellValue(this.getInvFileName(jobTruck.getJobId(), invNo));

				row.createCell(cellid++).setCellValue(doNo);
				row.createCell(cellid++).setCellValue(this.getPodFileName(jobTruck.getJobId(), doNo));

				Cell cell30 = row.createCell(cellid++);
				Cell cell30GPS = row.createCell(cellid++);
				Cell cell100 = row.createCell(cellid++);
				Cell cell100GPS = row.createCell(cellid++);

				if (StringUtils.isNoneBlank(item30Gps)) {
					cell30.setCellValue(OPM_TRACK_RADIUS.RADIUS_30.getRadius());
					cell30GPS.setCellValue(item30Gps);
				}
				if (StringUtils.isNoneBlank(item100Gps)) {
					cell100.setCellValue(OPM_TRACK_RADIUS.RADIUS_100.getRadius());
					cell100GPS.setCellValue(item100Gps);
				}
			}

			// .xlsx is the format for Excel Sheets...
			// writing the workbook into the file...
			FileOutputStream out;
			out = new FileOutputStream(new File(filePath));

			workbook.write(out);
			workbook.close();
			out.close();

			return filePath;

		} catch (Exception e) {
			log.error("", e);
			throw e;
		}

	}

	private List<String> listEpodFileList(String bankId, String jobTruckId) throws Exception {

		List<String> epodList = new ArrayList<>();

		// 1: list all files at TCkCtTripAttach
		List<TCkCtTripDoAttach> doAttachList = ckCtTripDoAttachDao.findByJobId(jobTruckId);

		// 2: construct file path
		String rootPath = ckCtCommonService.getCkCtAttachmentPathJob(bankId, true);
		String parentPath = rootPath + File.separator + jobTruckId;

		if (!new File(parentPath).exists()) {
			new File(parentPath).mkdirs();
		}

		boolean isMorePodfile = doAttachList.size() > 1;

		for (int i = 0; i < doAttachList.size(); i++) {

			TCkCtTripDoAttach doAttach = doAttachList.get(i);

			String epodFile = "";

			if (!isMorePodfile) {
				// single file
				epodFile = parentPath + File.separator + jobTruckId + "_POD.pdf";
			} else {
				// multi file
				epodFile = parentPath + File.separator + jobTruckId + "_" + i + "_POD.pdf";
			}
			
			if(new File(epodFile).exists()) {
				// rename file
				String bakEpodFile = epodFile + (dateFormat.format(new Date())) ;
				new File(epodFile).renameTo(new File(bakEpodFile));
			}

			if (doAttach.getDoaLoc().toUpperCase().endsWith(".PDF") && new File(doAttach.getDoaLoc()).exists()) {
				// PDF file
				Files.copy(new File(doAttach.getDoaLoc()).toPath(), new File(epodFile).toPath());
			} else {
				// image
				jobPhotoService.mergeImage2Pdf(Arrays.asList(doAttach.getDoaLoc()), epodFile);
			}
			epodList.add(epodFile);
		}
		return epodList;
	}

	private String getInvFile(String bankId, String jobTruckId) throws Exception {

		List<TCkCtToInvoice> tCkCtToInvoices = ckCtToInvoiceDao.findByJobId(jobTruckId);

		String invPath = tCkCtToInvoices.stream().filter(inv -> StringUtils.isNotBlank(inv.getInvLoc())).findFirst()
				.map(inv -> inv.getInvLoc()).orElse(null);

		String rootPath = ckCtCommonService.getCkCtAttachmentPathJob(bankId, true);
		String parentPath = rootPath + File.separator + jobTruckId;
		if (!new File(parentPath).exists()) {
			new File(parentPath).mkdirs();
		}

		String bankInvFile = parentPath + File.separator + jobTruckId + "_Invoice.pdf";

		if(new File(bankInvFile).exists()) {
			// rename file
			String bakBankInvFile = bankInvFile + (dateFormat.format(new Date())) ;
			new File(bankInvFile).renameTo(new File(bakBankInvFile));
		}
		
		if (invPath != null) {
			if (invPath.toUpperCase().endsWith(".PDF")) {
				// PDF file
				Files.copy(new File(invPath).toPath(), new File(bankInvFile).toPath());
			} else {
				// image
				jobPhotoService.mergeImage2Pdf(Arrays.asList(invPath), bankInvFile);
			}
			return bankInvFile;
		}
		return null;
	}

	private String getOpsAbsoluteFileName(String bankId, String fileType, Date date) throws ParameterException {

		if (null == date) {
			date = new Date();
		}

		String parentPath = ckCtCommonService.getCkCtAttachmentPathJob(bankId, true);

		// CA_20240312.xls
		String fileName = String.format("%s_%s.xlsx", fileType, yyyyMMddSDF.format(date));

		return parentPath + File.separator + fileName;
	}

	private String getInvFileName(String jobTruckId, String invNo) {
		return jobTruckId + "_" + "Invoice.pdf";
	}

	private String getPodFileName(String jobTruckId, String podNo) {
		return jobTruckId + "_" + "POD.pdf";
	}

	private CellStyle getDateStyle(XSSFWorkbook wb) {

		CellStyle cellStyle = wb.createCellStyle();
		CreationHelper createHelper = wb.getCreationHelper();

		wb.getCreationHelper();
		short format = createHelper.createDataFormat().getFormat("d/m/yyyy");
		cellStyle.setDataFormat(format);

		return cellStyle;
	}

	public BigDecimal computePlatFormFeeFromBank(TCkJobTruck jobTruck) throws Exception {

		List<TCkCtPlatformInvoice> invList = platformInvoiceDao.findByJobIdAndInvTo(jobTruck.getJobId(),
				AccountTypes.ACC_TYPE_TO.name());
		
		if(invList == null || invList.size() == 0) {
			throw new Exception("Fail to find Platformfee for job: " + jobTruck.getJobId());
		}
		return invList.get(0).getInvTotal();
		/*-
				// check payment Term is 30 days
				TCkCtMstOpmRate opmRate = opmRateList.stream()
						.filter(opmR -> (jobTruck.getJobFinancer().equalsIgnoreCase(opmR.getTMstBank().getBankId()) && (true)))
						.findFirst().orElseThrow(() -> new Exception(""));
		
				return jobTruck.computeTotalAmt().multiply(new BigDecimal(opmRate.getOpmrPercentGli().toString()));
				*/

	}

	private void putFile2Sftp(List<File> fileList, String bankId) throws Exception {

		SFTPConfig sftpConfig = opmSftpService.getDsvSftpConfig(bankId);

		SFTPStatus status = SFTPUtil.store(sftpConfig, fileList);
		
		if(SFTPStatus.STATE.COMPLETE != status.getState()) {
			throw new Exception("Fail to push file to SFTP: " + bankId + " " + status.getMsg());
		}
	}

	private boolean isOcTruck(TCkJobTruck jobTruck) {

		return OPM_OPT.OC.name().equalsIgnoreCase(jobTruck.getJobFinanceOpt());
	}

	private Map<Integer, TCkCtTrackLocItem> getTruckGps(String jobId) {

		Map<Integer, TCkCtTrackLocItem> itemMap = new HashMap<Integer, TCkCtTrackLocItem>();

		try {
			List<TCkCtTrackLoc> locList = ckCtTrackLocDao.findByJobId(jobId);
			if (locList != null && locList.size() > 0) {

				Optional<TCkCtTrackLoc> optLoc = locList.stream()
						.filter(l -> l.getTCkCtTrackLocItems() != null && l.getTCkCtTrackLocItems().size() > 0)
						.findFirst();

				if (optLoc.isPresent() && optLoc.get().getTCkCtTrackLocItems() != null
						&& optLoc.get().getTCkCtTrackLocItems().size() > 0) {

					Map<Integer, List<TCkCtTrackLocItem>> itemMapList = optLoc.get().getTCkCtTrackLocItems().stream()
							.collect(Collectors.groupingBy(TCkCtTrackLocItem::getTliRadius));

					itemMapList.forEach((k, v) -> {
						if (v != null && v.size() > 0) {
							itemMap.put(k, v.get(0));
						}
					});
				}
			}
		} catch (Exception e) {
			// don't throw Exception.
			log.error("Fail to get TCkCtTrackLoc by JobTruckId: " + jobId, e);
		}
		return itemMap;
	}
}