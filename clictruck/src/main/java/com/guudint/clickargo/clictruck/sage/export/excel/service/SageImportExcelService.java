package com.guudint.clickargo.clictruck.sage.export.excel.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.sage.dao.CkSageIntegrationDao;
import com.guudint.clickargo.sage.dto.CkCtMstSageIntStateEnum;
import com.guudint.clickargo.sage.dto.CkCtMstSageIntTypeEnum;
import com.guudint.clickargo.sage.model.TCkCtMstSageIntState;
import com.guudint.clickargo.sage.model.TCkSageIntegration;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.util.email.SysParam;
import com.vcc.camelone.util.sftp.SFTPUtil;
import com.vcc.camelone.util.sftp.model.SFTPConfig;

@Service
public class SageImportExcelService {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(SageImportExcelService.class);

	@Autowired
	protected SysParam sysParam;

	@Autowired
	protected CkSageIntegrationDao sageIntegrartionDao;

	SimpleDateFormat yyyyMMddSDF = new SimpleDateFormat("yyyyMMdd");

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void process() throws IOException {

		// scan sftp folder.
		List<File> importFiles = new ArrayList<>();
		SFTPConfig sftpConfig = null;

		try {
			String parentPath = sysParam.getValString("CLICTRUCK_from_SAGE_PATH", "/home/vcc/sage300/fromSage/");
			Files.createDirectories(Paths.get(parentPath));

			// push to SFTP server CLICTRUCK_SAGE_SFTP
			String sageSftp = sysParam.getValString("CLICTRUCK_SAGE_SFTP", null);

			if (StringUtils.isNotBlank(sageSftp)) {
				sftpConfig = (new ObjectMapper()).readValue(sageSftp, SFTPConfig.class);

				SFTPUtil.get(sftpConfig, new File(parentPath), importFiles);
			}

		} catch (IOException e) {
			log.error("", e);
			throw e;
		}

		// update tables status.
		for (File excelFile : importFiles) {
			// update tables
			try {
				int rows = this.parseExcelFile(excelFile);
				log.info(String.format("File: %s has %d rows", excelFile, rows));

				TCkSageIntegration tCkSageInte = this.findByFileName(excelFile);

				if (null != tCkSageInte) {
					this.updateTableRecord(rows, excelFile, tCkSageInte);
				}

			} catch (Exception e) {
				log.error("", e);
				// sendEmailNotification();
			}
		}

		// remove sftp files
		if (importFiles != null && importFiles.size() > 0 && sftpConfig != null) {

			List<String> fileNameList = importFiles.stream().map(file -> file.getName()).collect(Collectors.toList());
			SFTPUtil.rm(sftpConfig, fileNameList);
		}
	}

	/**
	 * Parse import excel file to get fail rows;
	 * @param excelFile
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private int parseExcelFile(File excelFile) throws FileNotFoundException, IOException {
		HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(excelFile));
		HSSFSheet sheet = wb.getSheetAt(0);

		int lastRow = sheet.getLastRowNum(); // from 0
		log.info(String.format("File %s has %d rows", excelFile.getAbsoluteFile(), lastRow));

		return lastRow;
	}

	/**
	 * Find TCkSageIntegration by import file name
	 * @param excelFile
	 * @return
	 * @throws Exception
	 */
	private TCkSageIntegration findByFileName(File excelFile) throws Exception {

		String[] fileNameSplit = excelFile.getName().split("_");
		CkCtMstSageIntTypeEnum sageType = CkCtMstSageIntTypeEnum.findByFileName(fileNameSplit[0]);
		Date startDate = yyyyMMddSDF.parse(fileNameSplit[1]);
		Date endDate = yyyyMMddSDF.parse(fileNameSplit[2]);
		
		// plus 1 day for endDate
		Calendar cal = Calendar.getInstance();
		cal.setTime(endDate);
		cal.add(Calendar.DAY_OF_YEAR, 1);

		List<TCkSageIntegration> sageIntList = sageIntegrartionDao.findByTypeAndDate(sageType.name(), startDate,
				cal.getTime());

		if (sageIntList != null && sageIntList.size() > 0) {
			return sageIntList.get(0);
		}
		return null;
	}

	/**
	 * Update TCkSageIntegration status 
	 * @param fileRows
	 * @param excelFile
	 * @param sageInte
	 * @throws Exception
	 */
	private void updateTableRecord(int fileRows, File excelFile, TCkSageIntegration sageInte) throws Exception {

		boolean isSuccess = fileRows <= 0;

		sageInte.setTCkCtMstSageIntState(new TCkCtMstSageIntState(
				isSuccess ? CkCtMstSageIntStateEnum.COMPLETE.name() : CkCtMstSageIntStateEnum.ERROR.name(), null));
		
		sageInte.setSintNoSuccess(sageInte.getSintNoRecords() - fileRows);
		sageInte.setSintNoFail(fileRows );
		sageInte.setSintDtImport(new Date());
		sageInte.setSintLocImport(excelFile.getAbsolutePath());

		sageInte.setSintDtLupd(new Date());
		sageInte.setSintUidLupd(Constant.ACCN_CREATE_SYS_USER);

		sageIntegrartionDao.saveOrUpdate(sageInte);
	}
}
