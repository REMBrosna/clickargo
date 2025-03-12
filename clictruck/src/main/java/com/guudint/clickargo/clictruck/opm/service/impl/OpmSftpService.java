package com.guudint.clickargo.clictruck.opm.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clicdo.common.service.CkCtCommonService;
import com.guudint.clickargo.clictruck.dsv.service.impl.DsvUtilService;
import com.guudint.clickargo.clictruck.opm.OpmConstants;
import com.guudint.clickargo.clictruck.opm.OpmException;
import com.guudint.clickargo.clictruck.opm.dao.CkOpmSftpLogDao;
import com.guudint.clickargo.clictruck.opm.model.TCkOpmSftpLog;
import com.guudint.clickargo.clictruck.opm.service.IOpmProcessService;
import com.guudint.clickargo.clictruck.opm.third.dto.OpmCreditReq;
import com.guudint.clickargo.clictruck.opm.third.service.impl.OpmCreditApproveServiceImpl;
import com.guudint.clickargo.clictruck.opm.third.service.impl.OpmCreditDisbursementServiceImpl;
import com.guudint.clickargo.clictruck.opm.third.service.impl.OpmCreditRepaymentServiceImpl;
import com.guudint.clickargo.clictruck.opm.third.service.impl.OpmSuspensionServiceReq;
import com.guudint.clickargo.clictruck.opm.third.service.impl.OpmTerminationServiceImpl;
import com.guudint.clickargo.clictruck.opm.third.service.impl.OpmUnSuspensionServiceReq;
import com.guudint.clickargo.common.CkUtil;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.master.dao.MstBankDao;
import com.vcc.camelone.master.model.TMstBank;
import com.vcc.camelone.util.email.SysParam;
import com.vcc.camelone.util.sftp.SFTPUtil;
import com.vcc.camelone.util.sftp.model.SFTPConfig;

@Service
public class OpmSftpService {

	private static Logger log = Logger.getLogger(OpmSftpService.class);
	private static SimpleDateFormat yyyyMMddSDF = new SimpleDateFormat("yyyyMMdd");

	@Autowired
	protected SysParam sysParam;
	@Autowired
	private CkCtCommonService ckCtCommonService;

	@Autowired
	protected CkOpmSftpLogDao ckOpmSftpLogDao;

	@Autowired
	protected MstBankDao mstBankDao;

	@Autowired
	protected ApplicationContext applicationContext;

	public String scanFromSftp(String financer) throws Exception {

		SFTPConfig sftpConfig = this.getDsvSftpConfig(financer);

		String parentPath = this.getLocalOpmPath(financer);

		List<String> successList = new ArrayList<>();
		List<String> failList = new ArrayList<>();

		if (sftpConfig != null) {

			// download 50 files together
			List<File> files = this.loadFilesFromSftp(sftpConfig, parentPath);

			log.info("files: " + files);

			if (files != null && files.size() > 0) {

				log.info("file.size() " + files.size());

				for (File file : files) {
					// Check if the filename has the current date, then proceed. Otherwise, ignore
					// or move to history so no unnecessary processing will be done
					try {
						if (isProcessFile(file.getName())) {
							// process 1 by 1
							log.info(file.getAbsolutePath() + "csv file path:");
							this.processCsvFile(null, file, financer);

							log.info(file.getAbsolutePath() + " begin remove file to history filder");
							this.mvFile2HisotoryFolder(sftpConfig, file);

							log.info(file.getAbsolutePath() + " begin remove file to history filder");
							this.rmFileFromSftp(sftpConfig, file);
							// success
							successList.add(file.getName());
						}
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

	public void processByOpmSftpLogId(String opmSftpLogId) throws Exception {

		this.processCsvFile(opmSftpLogId, null, null);
	}

	public SFTPConfig getDsvSftpConfig(String financer) throws Exception {

		try {
			TMstBank bank = mstBankDao.find(financer);

			// String sftpDsv = sysParam.getValString("CLICTRUCK_SFTP_OCBC", null);
			String connDetail = bank.getBankConnectionDetails();

			if (StringUtils.isEmpty(connDetail)) {
				throw new Exception("Bank Connction detail is empty: " + financer);
			}

			return (new ObjectMapper()).readValue(connDetail, SFTPConfig.class);

		} catch (Exception e) {
			Log.error("", e);
			throw e;
		}
	}

	public String getLocalOpmPath(String financer) throws IOException {

		String rootPath = ckCtCommonService.getCkCtAttachmentPathRoot();

		String parentPath = rootPath + File.separator + financer + File.separator + yyyyMMddSDF.format(new Date());

		Files.createDirectories(Paths.get(parentPath));

		return parentPath;
	}

	private List<File> loadFilesFromSftp(SFTPConfig sftpConfig, String parentPath) throws Exception {

		List<File> importFiles = new ArrayList<>();

		// Max download 50 files
		int maxDownloadFiles = sysParam.getValInteger("CLICTRUCK_DSV_MAX_DOWNLOAD_FILES", 50);
		DsvUtilService.getSftpFiles(sftpConfig, new File(parentPath), importFiles, maxDownloadFiles);

		return importFiles;
	}

	private boolean isProcessFile(String filename) throws Exception {

		String[] fileNameSplit = filename.split("\\.");
		if (fileNameSplit.length == 2) {
			if (!"XLSX".equalsIgnoreCase(fileNameSplit[1])) {
				throw new Exception("File name not correct, should be xlsx file " + filename);
			}
		} else {
			throw new Exception("File name not correct! " + filename);
		}
		// Get the date substring of the file
		String[] tokens = fileNameSplit[0].split("_");
		if (tokens.length == 2) {
			String dateStr = tokens[1];
			try {
				Date dateInFile = yyyyMMddSDF.parse(dateStr);
				if (dateInFile.compareTo(new Date()) > 0) {
					throw new Exception("File name date not correct! " + filename);
				}
			} catch (Exception e) {
				throw new Exception("File name should be like XX_yyyyMMdd.xlsx: " + filename);
			}
		} else {
			throw new Exception("File name not correct! " + filename);
		}

		TCkOpmSftpLog opmSftpLog = ckOpmSftpLogDao.findByFileName(filename);

		if(null != opmSftpLog) {
			throw new Exception("Already processed: " + filename);
		}

		return true;
	}

	private void rmFileFromSftp(SFTPConfig sftpConfig, File file) throws Exception {

		List<String> fileNameList = new ArrayList<>();
		fileNameList.add(file.getName());
		// remove from output path
		SFTPUtil.rm(sftpConfig, fileNameList);
	}

	private void mvFile2HisotoryFolder(SFTPConfig sftpConfig, File file) throws Exception {

		String hisotryFolderName = sysParam.getValString("CLICTRUCK_OPM_HISTORY_FOLDER", "history");

		DsvUtilService.moveFileFromOut2HistoryFolder(sftpConfig, Arrays.asList(file.getName()), hisotryFolderName);
	}

	@SuppressWarnings("unchecked")
	private void processCsvFile(String opmSftpLogId, File file, String financer) throws Exception {

		//
		TCkOpmSftpLog opmLog = null;
		if (StringUtils.isBlank(opmSftpLogId)) {
			opmLog = new TCkOpmSftpLog(CkUtil.generateId(TCkOpmSftpLog.PREFIX_ID));
			opmLog.setOpmslFinancer(financer);
			opmLog.setOpmslDirection(OpmConstants.OPM_SFTP_DIRECTION_IN);

			opmLog.setOpmslFileName(file.getName());
			opmLog.setOpmslFilePath(file.getAbsolutePath());
			opmLog.setOpmslFileSize(Long.valueOf(file.length()).intValue());
			opmLog.setOpmslDtFileCreate(new Date());
			opmLog.setOpmslDtCreate(new Date());
			opmLog.setOpmslUidCreate("Scheduler");

		} else {
			opmLog = ckOpmSftpLogDao.find(opmSftpLogId);
			opmLog.setOpmslDtLupd(new Date());

			file = new File(opmLog.getOpmslFilePath());
			financer = opmLog.getOpmslFinancer();

		}

		String fileType = file.getName().substring(0, 2); // first 2 characters
		opmLog.setOpmslFileType(fileType);

		try {

			IOpmProcessService<?> processService = this.getOpmProcessServiceByFileType(fileType, file);

			List<OpmCreditReq> reqList = new ArrayList<>();
			Map<Integer, OpmException> errMap = new HashMap<>();

			reqList = (List<OpmCreditReq>) processService.parseFile(file.getAbsolutePath(), errMap);

			IOpmProcessService<OpmCreditReq> processReqService = (IOpmProcessService<OpmCreditReq>) processService;

			reqList.forEach(System.out::println);

			for (int i = 0; i < reqList.size(); i++) {

				OpmCreditReq req = reqList.get(i);

				try {
					processReqService.process(req, financer, errMap);

					opmLog.setOpmslAccnIds(processReqService.getAccnIdList(reqList));
					opmLog.setOpmslJobIds(processReqService.getJobTruckIdList(reqList));

				} catch (OpmException e) {
					log.error("", e);
					errMap.put(req.getRowId(), e);
				} catch (Exception e) {
					log.error("", e);
					errMap.put(req.getRowId(), new OpmException(e));
				}
			}
			// generate and push error file to SFTP
			if (errMap.size() > 0) {
				this.generateAndPushErrrorFile2SFTP(file.getAbsolutePath(), processReqService, errMap, opmLog,
						financer);

				throw new Exception(errMap.toString());
			}
			opmLog.setOpmslStatus(Constant.ACTIVE_STATUS);
		} catch (Exception e) {

			log.error("", e);
			String msg = e.getMessage();
			opmLog.setOpmslRemark((msg.length() > 1000) ? msg.substring(0, 1000) : msg);
			opmLog.setOpmslStatus(Constant.INACTIVE_STATUS);

		} finally {
			try {
				ckOpmSftpLogDao.saveOrUpdate(opmLog);
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}

	private void generateAndPushErrrorFile2SFTP(String originalFile, IOpmProcessService<OpmCreditReq> processReqService,
			Map<Integer, OpmException> errMap, TCkOpmSftpLog opmLog, String finnancer) throws Exception {

		try {
			String errorFile = processReqService.generateErrorFile(originalFile, errMap, finnancer);
			File file = new File(errorFile);

			opmLog.setOpmslDtErrorFileCreate(new Date());
			opmLog.setOpmslErrorAccnIds(null);
			opmLog.setOpmslErrorJobIds(null);
			opmLog.setOpmslErrorFileName(file.getName());
			opmLog.setOpmslErrorFilePath(errorFile);

			List<File> fileList = Arrays.asList(file);
			// String sftpDsv = sysParam.getValString("CLICTRUCK_SFTP_OCBC", null);

			// SFTPConfig sftpConfig = (new ObjectMapper()).readValue(sftpDsv,
			// SFTPConfig.class);
			SFTPConfig sftpConfig = this.getDsvSftpConfig(finnancer);
			SFTPUtil.store(sftpConfig, fileList);

			opmLog.setOpmslDtErrorFilePush2sftp(new Date());
		} catch (Exception e) {
			log.error("Fail to create or push error file: ", e);
			String msg = e.getMessage();
			opmLog.setOpmslRemark((msg.length() > 1000) ? msg.substring(0, 1000) : msg);
			opmLog.setOpmslErrorFileRemark(msg);
		}
	}

	private IOpmProcessService<?> getOpmProcessServiceByFileType(String fileType, File file) throws Exception {

		IOpmProcessService<?> processService = null;

		switch (fileType) {
		case OpmConstants.OPM_CSVFILE_PREFIX_APPROVAL:

			// Map<OpmCreditApprovalReq, String> errorCpList = this.processApproval(file,
			// financer, opmLog);
			processService = applicationContext.getBean(OpmCreditApproveServiceImpl.class);
			break;
		case OpmConstants.OPM_CSVFILE_PREFIX_DISBURSEMENT:

			// Map<OpmCreditDisbursementReq, String> errorCDList =
			// this.processDisbursement(file, financer, opmLog);
			processService = applicationContext.getBean(OpmCreditDisbursementServiceImpl.class);
			break;
		case OpmConstants.OPM_CSVFILE_PREFIX_REPAYMENT:

			// Map<OpmCreditRepaymentReq, String> errorCRList = this.processRepayment(file,
			// financer, opmLog);
			processService = applicationContext.getBean(OpmCreditRepaymentServiceImpl.class);
			break;
		case OpmConstants.OPM_CSVFILE_PREFIX_TERMINATION:

			// Map<OpmCreditTerminationReq, String> errorCTList =
			// this.processTermination(file, financer, opmLog);
			processService = applicationContext.getBean(OpmTerminationServiceImpl.class);

			break;
		case OpmConstants.OPM_CSVFILE_PREFIX_SUSPENSION:
			// Map<OpmCreditSuspensionReq, String> errorCSList =
			// this.processSuspension(file, financer, opmLog);
			processService = applicationContext.getBean(OpmSuspensionServiceReq.class);

			break;
		case OpmConstants.OPM_CSVFILE_PREFIX_UNSUSPENSION:
			// Map<OpmCreditUnSuspensionReq, String> errorCEList =
			// this.processUnsuspension(file, financer, opmLog);
			processService = applicationContext.getBean(OpmUnSuspensionServiceReq.class);

			break;
		default:
			throw new Exception("File name format is not correct: " + file.getName());
		}

		return processService;
	}

	/*-
	private Map<OpmCreditApprovalReq, String> processApproval(File file, String financer, TCkOpmSftpLog opmLog)
			throws Exception {
	
		List<OpmCreditApprovalReq> reqList = opmExelService.parseApproveFile(file.getAbsolutePath());
	
		String accnIdList = reqList.stream().map(req -> req.getTax_no()).distinct().collect(Collectors.joining(","));
		opmLog.setOpmslAccnIds(accnIdList);
	
		reqList.forEach(System.out::println);
	
		Map<OpmCreditApprovalReq, String> errMap = new HashMap<>();
	
		for (OpmCreditApprovalReq req : reqList) {
	
			try {
				opmService.creditApprove(req, financer);
	
			} catch (Exception e) {
				log.error("", e);
				errMap.put(req, e.getMessage());
			}
		}
		return errMap;
	}
	
	private Map<OpmCreditDisbursementReq, String> processDisbursement(File file, String financer, TCkOpmSftpLog opmLog)
			throws Exception {
	
		List<OpmCreditDisbursementReq> reqList = opmExelService.parseDisbursementFile(file.getAbsolutePath());
	
		String accnIdList = reqList.stream().map(req -> req.getTax_no()).distinct().collect(Collectors.joining(","));
		opmLog.setOpmslAccnIds(accnIdList);
	
		reqList.forEach(System.out::println);
	
		Map<OpmCreditDisbursementReq, String> errMap = new HashMap<>();
	
		for (OpmCreditDisbursementReq req : reqList) {
			try {
				opmService.creditDisbursement(req, financer);
			} catch (Exception e) {
				log.error("", e);
				errMap.put(req, e.getMessage());
			}
		}
		return errMap;
	}
	
	private Map<OpmCreditRepaymentReq, String> processRepayment(File file, String financer, TCkOpmSftpLog opmLog)
			throws Exception {
	
		List<OpmCreditRepaymentReq> reqList = opmExelService.parseRepaymentFile(file.getAbsolutePath());
	
		String accnIdList = reqList.stream().map(req -> req.getTax_no()).distinct().collect(Collectors.joining(","));
		opmLog.setOpmslAccnIds(accnIdList);
	
		reqList.forEach(System.out::println);
	
		Map<OpmCreditRepaymentReq, String> errMap = new HashMap<>();
	
		for (OpmCreditRepaymentReq req : reqList) {
			try {
				opmService.creditRepayment(req, financer);
			} catch (Exception e) {
				log.error("", e);
				errMap.put(req, e.getMessage());
			}
		}
		return errMap;
	}
	
	private Map<OpmCreditTerminationReq, String> processTermination(File file, String financer, TCkOpmSftpLog opmLog)
			throws Exception {
	
		List<OpmCreditTerminationReq> reqList = opmExelService.parseTerminationFile(file.getAbsolutePath());
	
		String accnIdList = reqList.stream().map(req -> req.getTax_no()).distinct().collect(Collectors.joining(","));
		opmLog.setOpmslAccnIds(accnIdList);
	
		reqList.forEach(System.out::println);
	
		Map<OpmCreditTerminationReq, String> errMap = new HashMap<>();
	
		for (OpmCreditTerminationReq req : reqList) {
			try {
				opmService.creditTermination(req, financer);
			} catch (Exception e) {
				log.error("", e);
				errMap.put(req, e.getMessage());
			}
		}
		return errMap;
	}
	
	private Map<OpmCreditSuspensionReq, String> processSuspension(File file, String financer, TCkOpmSftpLog opmLog)
			throws Exception {
	
		List<OpmCreditSuspensionReq> reqList = opmExelService.parseSuspensionFile(file.getAbsolutePath());
	
		String accnIdList = reqList.stream().map(req -> req.getTax_no()).distinct().collect(Collectors.joining(","));
		opmLog.setOpmslAccnIds(accnIdList);
	
		reqList.forEach(System.out::println);
	
		Map<OpmCreditSuspensionReq, String> errMap = new HashMap<>();
	
		for (OpmCreditSuspensionReq req : reqList) {
			try {
				opmService.creditSuspension(req, financer);
			} catch (Exception e) {
				log.error("", e);
				errMap.put(req, e.getMessage());
			}
		}
		return errMap;
	}
	
	private Map<OpmCreditUnSuspensionReq, String> processUnsuspension(File file, String financer, TCkOpmSftpLog opmLog)
			throws Exception {
	
		List<OpmCreditUnSuspensionReq> reqList = opmExelService.parseUnsuspensionFile(file.getAbsolutePath());
	
		String accnIdList = reqList.stream().map(req -> req.getTax_no()).distinct().collect(Collectors.joining(","));
		opmLog.setOpmslAccnIds(accnIdList);
	
		reqList.forEach(System.out::println);
	
		Map<OpmCreditUnSuspensionReq, String> errMap = new HashMap<>();
	
		for (OpmCreditUnSuspensionReq req : reqList) {
			try {
				opmService.creditUnSuspension(req, financer);
			} catch (Exception e) {
				log.error("", e);
				errMap.put(req, e.getMessage());
			}
		}
		return errMap;
	}
	*/
	///////////////
}
