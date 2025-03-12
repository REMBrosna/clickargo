package com.guudint.clickargo.clictruck.opm.third.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Hibernate;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.admin.contract.dao.CkCtContractDao;
import com.guudint.clickargo.clictruck.finacing.dao.CkCtDebitNoteDao;
import com.guudint.clickargo.clictruck.finacing.service.ITruckOperatorPayoutService;
import com.guudint.clickargo.clictruck.master.dao.CkCtMstOpmRateDao;
import com.guudint.clickargo.clictruck.opm.OpmConstants.OPM_OPT;
import com.guudint.clickargo.clictruck.opm.OpmConstants.Opm_Validation;
import com.guudint.clickargo.clictruck.opm.OpmException;
import com.guudint.clickargo.clictruck.opm.dao.CkOpmDao;
import com.guudint.clickargo.clictruck.opm.dao.CkOpmJournalDao;
import com.guudint.clickargo.clictruck.opm.dao.CkOpmSummaryDao;
import com.guudint.clickargo.clictruck.opm.service.IOpmProcessService;
import com.guudint.clickargo.clictruck.opm.service.IOpmService;
import com.guudint.clickargo.clictruck.opm.service.impl.OpmSftpService;
import com.guudint.clickargo.clictruck.opm.service.impl.OpmUpdateAccnStatusService;
import com.guudint.clickargo.clictruck.opm.service.impl.OpmUtilizeService;
import com.guudint.clickargo.clictruck.opm.third.dto.OpmCreditReq;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckDao;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkJobTruckService;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtPaymentDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtPlatformInvoiceDao;
import com.guudint.clickargo.common.dao.CkAccnDao;
import com.guudint.clickargo.common.dao.CkAccnOpmDao;
import com.guudint.clickargo.common.model.TCkAccn;
import com.guudint.clickargo.master.dao.CoreAccnDao;
import com.guudint.clickargo.payment.dao.CkPaymentTxnDao;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.audit.dto.CoreAuditlog;
import com.vcc.camelone.common.audit.service.impl.COAuditLogService;
import com.vcc.camelone.util.email.SysParam;

public abstract class OpmProcessService<T extends OpmCreditReq> implements IOpmProcessService<T> {

	private static Logger log = Logger.getLogger(OpmProcessService.class);

	SimpleDateFormat yyyyMMddSDF = new SimpleDateFormat("yyyyMMdd");
	protected static SimpleDateFormat ddMMMyySDF = new SimpleDateFormat("ddMMyy");

	protected static String ERROR_ERROR_CODE = "error_code";
	protected static String ERROR_ERROR_MSG = "error_msg";

	protected static final String KEY_OPM_MAX_LIMIT = "CLICTRUCK_OPM_MAX_LIMIT";

	@Autowired
	protected CoreAccnDao coreAccnDao;

	@Autowired
	protected CkCtContractDao ckCtContractDao;

	@Autowired
	protected CkAccnOpmDao ckCtAccnOpmDao;

	@Autowired
	protected CkAccnDao ckCtAccnDao;

	@Autowired
	protected CkOpmDao ckOpmDao;

	@Autowired
	protected CkOpmSummaryDao ckOpmSummaryDao;

	@Autowired
	protected IOpmService opmService;

	@Autowired
	protected CkPaymentTxnDao ckPaymentTxnDao;

	@Autowired
	protected CkCtPaymentDao ckCtPaymentDao;

	@Autowired
	protected CkOpmJournalDao ckOpmJournalDao;

	@Autowired
	protected CkCtPlatformInvoiceDao ckCtPlatformInvoiceDao;

	@Autowired
	CkCtDebitNoteDao ckCtDebitNoteDao;

	@Autowired
	protected CkJobTruckService ckJobTruckService;

	@Autowired
	protected ITruckOperatorPayoutService toPayoutService;

	@Autowired
	protected OpmUtilizeService utilizeService;

	@Autowired
	protected OpmSftpService opmSftpService;

	@Autowired
	protected CkCtMstOpmRateDao ckCtMstOpmRateDao;

	@Autowired
	protected OpmUtilizeService opmUtilizeService;

	@Autowired
	protected CkJobTruckDao ckJobTruckDao;

	protected FormulaEvaluator formulaEvaluator = null; //

	@Autowired
	protected COAuditLogService auditLogService;

	@Autowired
	protected OpmUpdateAccnStatusService opmUpdateAccnStatusService;

	@Autowired
	protected SysParam sysParam;

	@Override
	public void validateReq(T req, String financer, Map<Integer, OpmException> errMap) throws OpmException {

		// 1. Check if the tax no exist
		List<TCoreAccn> accnList = null;
		try {
			accnList = coreAccnDao.findAllByUen(req.getTax_no());
		} catch (Exception e1) {
			throw new OpmException(e1);
		}

		// 1.1
		if (accnList != null) {
			if (accnList.size() == 0) {
				throw new OpmException(Opm_Validation.NOT_FIND, "Tax No.");
			} else if (accnList.size() > 1) {
				throw new OpmException(Opm_Validation.MULTI, "Tax No.");
			}
		} else {
			throw new OpmException(Opm_Validation.NOT_FIND, "Tax No.");
		}

		// 2. Check if account can be found using the tax_no
		try {
			this.findTCoreAccn(req.getTax_no());
		} catch (Exception e) {
			log.error("Fail to find account by UEN: " + req.getTax_no(), e);
			throw new OpmException(Opm_Validation.NOT_FIND, "Tax No.");
		}

	}

	@Override
	public String getAccnIdList(List<T> reqList) throws Exception {

		String accnIdList = reqList.stream().map(req -> req.getTax_no()).distinct().collect(Collectors.joining(","));

		return accnIdList;
	}

	@Override
	public String getJobTruckIdList(List<T> reqList) throws Exception {
		return null;
	}

	@Override
	public String generateErrorFile(String originalFileStr, Map<Integer, OpmException> errsMap, String financer)
			throws Exception {

		Workbook wb = null;
		byte[] outArray = null;

		String errorFileOutputPath = null;

		File originalFile = new File(originalFileStr);

		String path = opmSftpService.getLocalOpmPath(financer);
		String fileName = String.format("ERR_%s_%s.xlsx", originalFile.getName().substring(0, 2),
				yyyyMMddSDF.format(new Date()));
		errorFileOutputPath = path + File.separator + fileName;

		FileUtils.copyFile(originalFile, new File(errorFileOutputPath));

		try (InputStream is = new FileInputStream(errorFileOutputPath);
				ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();) {

			wb = new XSSFWorkbook(is);

			this.fillErrorFileData(wb, errsMap);

			wb.write(outByteStream);
			outArray = outByteStream.toByteArray();
			outByteStream.close();
			wb.close();

			FileOutputStream out = new FileOutputStream(new File(errorFileOutputPath));
			out.write(outArray);
			out.close();

		} catch (Exception e) {
			Log.error("Fail to create error excel file: ", e);
			throw e;
		}
		return errorFileOutputPath;
	}

	protected void fillErrorFileData(Workbook wb, Map<Integer, OpmException> errsMap) {

		Sheet sheet = wb.getSheetAt(0);

		sheet.getRow(0).createCell(this.getErrorCodeColumn()).setCellValue(OpmProcessService.ERROR_ERROR_CODE);
		sheet.getRow(0).createCell(this.getErrorCodeColumn() + 1).setCellValue(OpmProcessService.ERROR_ERROR_MSG);

		// fill header: error_code, error_msg;
		for (Map.Entry<Integer, OpmException> entry : errsMap.entrySet()) {

			Integer rowId = entry.getKey();
			OpmException exp = entry.getValue();

			String errorCode = exp.getCode();
			if (StringUtils.isBlank(errorCode)) {
				errorCode = Opm_Validation.UNKNOW.getCode();
			}

			sheet.getRow(rowId).createCell(this.getErrorCodeColumn()).setCellValue(exp.getCode());
			sheet.getRow(rowId).createCell(this.getErrorCodeColumn() + 1).setCellValue(exp.getMessage());
		}
		// Need not remove correct rows.
		/*-
		List<Integer> errorRowList = errsMap.keySet().stream().sorted((a, b) -> Integer.compare(b, a))
				.collect(Collectors.toList());
		
		List<Integer> rows = IntStream.rangeClosed(1, sheet.getLastRowNum()).boxed()
				.sorted((a, b) -> Integer.compare(b, a)).collect(Collectors.toList());
		
		rows.removeAll(errorRowList);
		
		rows.forEach(rowId -> {
			log.info("rowId: " + rowId + " " + sheet.getLastRowNum());
		
			if (rowId == sheet.getLastRowNum()) {
				sheet.removeRow(sheet.getRow(rowId));
			} else {
				sheet.shiftRows(rowId + 1, sheet.getLastRowNum(), -1);
			}
		});
		*/
		// removeEmptyRows(sheet);

		// revert and shiftRow
		/*-
		List<Map.Entry<Integer, OpmException>> sortedList = 
				errsMap.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.reverseOrder() )).collect(Collectors.toList());
		
		for(Map.Entry<Integer, OpmException> entry: sortedList) {
			int rowId = entry.getKey();
			//sheet.shiftRows(rowId+1, sheet.getLastRowNum(), -1);
			sheet.removeRow(sheet.getRow(rowId)); // only set this row as blank row.
		}
		*/

	}

	protected abstract int getErrorCodeColumn();

	/////////////
	@Transactional(propagation = Propagation.REQUIRED)
	protected TCoreAccn findTCoreAccn(String uen) throws Exception {

		TCoreAccn accn = coreAccnDao.findAccnByUen(uen);

		if (null == accn) {
			throw new Exception("Fail to find core account by uen: " + uen);
		}

		Hibernate.initialize(accn.getTMstAccnType());
		return accn;
	}

	protected TCkAccn findTckAccn(String accnId, String uen, String financer) throws Exception {

		TCkAccn ckAccn = ckCtAccnDao.find(accnId);
		if (null == ckAccn) {
			throw new Exception("NOT OPM acount in TCkAccn : " + accnId);
		}

		// convert Enum to ArrayList
		List<String> opmType = Arrays.stream(OPM_OPT.class.getEnumConstants()).map(Enum::name)
				.collect(Collectors.toList());

		if (!opmType.contains(ckAccn.getCaccnFinancingType())
				|| !financer.equalsIgnoreCase(ckAccn.getCaccnFinancer())) {
			throw new Exception("financer type is not correct for uen: " + uen);
		}
		return ckAccn;
	}

	protected long getOpmMaxLimit() throws Exception {
		// 3_000_000_000L
		long maxLimit = sysParam.getValBigDecimal(KEY_OPM_MAX_LIMIT, new BigDecimal(3_000_000_000L)).longValue();
		return maxLimit;
	}

	//////////////// Excel cell helper
	protected String getStringFromCell(Cell cell) {
		if (cell == null) {
			return null;
		}
		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return null;
			} else {
				return String.valueOf(cell.getNumericCellValue());
			}
		case FORMULA:
			return cell.getCellFormula();
		case BLANK:
			return null;
		default:
			return null;
		}
	}

	protected Long getLongFromCell(Cell cell, String errorFieldName) throws OpmException {
		Double d = null;
		try {
			d = this.getDoubleFromCell(cell);
		} catch (Exception e) {
			throw new OpmException(Opm_Validation.NOT_NUMBER, errorFieldName);
		}

		if (null == d) {
			throw new OpmException(Opm_Validation.NOT_NUMBER, errorFieldName);
		}

		long l = d.longValue();
		if (d.doubleValue() != l) {
			throw new OpmException(Opm_Validation.NOT_LONG_NUMBER, errorFieldName);
		}
		return l;
	}

	protected Long getLongFromCell(Cell cell, long defaultVal, String errorFieldName) throws OpmException {
		Double d = null;
		try {
			d = this.getDoubleFromCell(cell);
		} catch (Exception e) {
			throw new OpmException(Opm_Validation.NOT_NUMBER, errorFieldName);
		}

		if (null == d) {
			return defaultVal;
		}

		long l = d.longValue();
		if (d.doubleValue() != l) {
			throw new OpmException(Opm_Validation.NOT_LONG_NUMBER, errorFieldName);
		}
		return l;
	}

	protected Double getDoubleFromCell(Cell cell) {
		if (cell == null) {
			return null;
		}
		switch (cell.getCellType()) {
		case STRING:
			return Double.valueOf(cell.getStringCellValue());
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return null;
			} else {
				return cell.getNumericCellValue();
			}
		case FORMULA:
			// Compute value for Formula type cell
			CellValue cellValue = formulaEvaluator.evaluate(cell);
			return Double.valueOf(cellValue.getNumberValue());
		case BLANK:
			return null;
		default:
			return null;
		}
	}

	protected Date getDateFromCell(Cell cell) throws ParseException {
		if (cell == null) {
			return null;
		}
		switch (cell.getCellType()) {
		case STRING:
			return ddMMMyySDF.parse(cell.getStringCellValue());
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue();
			} else {
				return null;
			}
		case FORMULA:
			return null;
		case BLANK:
			return null;
		default:
			return null;
		}
	}

	protected void insertAccnAuditLog(String accnId, String remarks) throws Exception {

		CoreAuditlog audit = new CoreAuditlog();
		audit.setAudtReckey(accnId);
		audit.setAudtEvent("ACCN MODIFY");
		audit.setAudtRemarks(remarks);
		audit.setAudtAccnid("SYS");
		audit.setAudtUid("SYS");
		auditLogService.log(audit);
	}
}
