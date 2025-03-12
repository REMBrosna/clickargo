package com.guudint.clickargo.clictruck.sage.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.guudint.clickargo.clictruck.finacing.dao.CkCtDebitNoteDao;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtDebitNote;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtPlatformInvoiceDao;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoice;
import com.guudint.clickargo.clictruck.sage.dao.CkCtSageDao;
import com.guudint.clickargo.clictruck.sage.model.TCkCtSage;
import com.guudint.clickargo.clictruck.util.FileUtil;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.service.ICkSeqNoService;
import com.guudint.clickargo.common.service.impl.CkSeqNoServiceImpl.SeqNoCode;
import com.guudint.clickargo.master.dao.CoreAccnConfigDao;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.vcc.camelone.ccm.model.TCoreAccnConfig;
import com.vcc.camelone.ccm.model.TCoreAccnConfigId;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.attach.dto.CoreAttach;
import com.vcc.camelone.util.email.SysParam;

@Deprecated
@Service
public class SageService {

	private static Logger log = Logger.getLogger(SageService.class);

	// in T_CORE_ACCN_CONFIG
	public static final String ACCN_CONFIG_SAGE_ACCN_ID = "SAGE_ACCN_ID";

	@Autowired
	private CkCtPlatformInvoiceDao platformInvoiceDao;

	@Autowired
	private CkCtDebitNoteDao debitNoteDao;

	@Autowired
	private CoreAccnConfigDao coreAccnConfigDao;

	@Autowired
	private CkCtSageDao ckCtSageDao;

	@Autowired
	private ICkSeqNoService seqNoService;

	// @Autowired
	// @Qualifier("coreSysparamDao")
	// protected GenericDao<TCoreSysparam, String> coreSysparamDao;

	@Autowired
	protected SysParam sysParam;

	/**
	 * 
	 * @return
	 */
	public String getFileName(Date date) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH_mm_ss");
		String fileName = "AR Report " + dateFormat.format(date) + ".xls";

		return fileName;
	}

	public byte[] getExcelReport(Date beginDate, Date endDate, String fileName) throws Exception {

		HSSFWorkbook wb = null;
		byte[] outArray = null;

		TCkCtSage sage = new TCkCtSage();

		try (InputStream is = SageService.class.getClassLoader().getResourceAsStream("SageTemplate.xls");
				ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();) {

			wb = new HSSFWorkbook(is);

			initStyle(wb);

			// Prepare data;
			List<InvoiceDto> invoiceList = this.findInvoices(beginDate, endDate);
			List<InvoiceLineDto> invoiceLineList = this.createInvoiceLines(invoiceList);

			// fill to Excel
			this.addInvoiceSheet(wb, "Invoices", invoiceList);
			this.addInvoiceDetailSheet(wb, "Invoice_Details", invoiceLineList);
			this.addInvoicePaymentSchedule(wb, "Invoice_Payment_Schedules", invoiceList);

			wb.write(outByteStream);
			outArray = outByteStream.toByteArray();
			outByteStream.close();
			wb.close();

			String batchNo = invoiceList.size() > 0 ? invoiceList.get(0).getBatchNo() : "";
			//
			sage.setSageId(CkUtil.generateId(TCkCtSage.PREFIX_ID));
			sage.setSageBatchNo(batchNo);
			sage.setSageDtStart(beginDate);
			sage.setSageDtEnd(endDate);

			String filePath = this.save2Storage(outArray, fileName, batchNo);
			sage.setSageFileLoc(filePath);

			sage.setSageStatus(Constant.ACTIVE_STATUS);
			sage.setSageDtCreate(new Date());
			sage.setSageUidCreate("sys");

		} catch (Exception e) {
			log.error("", e);
			e.printStackTrace();
			throw e;
		} finally {
			try {
				ckCtSageDao.saveOrUpdate(sage);
			} catch (Exception e) {
				log.error("Fail to save to TCkCtSage table ", e);
			}
		}
		return outArray;
	}

	/**
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public String getFileBody(String filePathBase64) throws Exception {

		// decode filePath
		String filePath = new String(Base64.getDecoder().decode(filePathBase64.getBytes()));
		
		String fileFolder = sysParam.getValString("CLICTRUCK-STORE-FILEPATH", "/home,/data");

		boolean isInSecurityFolder = Arrays.asList(fileFolder.split(",")).stream()
				.anyMatch(f -> filePath.indexOf(f) == 0);

		if (!isInSecurityFolder) {
			log.error("" + filePath + " not in " + fileFolder);
			throw new Exception(filePath + " is not correct.");
		}
		Path path = Paths.get(filePath);

		byte[] arr = Files.readAllBytes(path);
		
		String base64FileBody = Base64.getEncoder().encodeToString(arr);
		
		return base64FileBody;
	}

	/**
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public CoreAttach getFileBodyBySageId(String sageId, boolean isUpdate2DownloadStatus) throws Exception {

		// decode filePath
		TCkCtSage sage = ckCtSageDao.find(sageId);
		
		if( null == sage) {
			log.error("Not find sage: " + sageId );
			throw new Exception(sageId + " is not correct.");
		}
		
		String filePath = sage.getSageFileLoc();

		byte[] arr = Files.readAllBytes(Paths.get(filePath));

		CoreAttach coreAttach = new CoreAttach();
		coreAttach.setAttData(arr);
		
		String[] pathArray = filePath.split("/");
		coreAttach.setAttName(pathArray[pathArray.length-1]);
		
		if(isUpdate2DownloadStatus ) {
			sage.setSageStatus('D');
			ckCtSageDao.saveOrUpdate(sage);
		}
		
		return coreAttach;
	}
	/**
	 * 
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	protected List<InvoiceDto> findInvoices(Date beginDate, Date endDate) throws Exception {

		String batchNo = this.getRandomBatchNo();

		List<InvoiceDto> invoiceList = new ArrayList<>();

		// PlatForm fee
		List<TCkCtPlatformInvoice> pfIlist = platformInvoiceDao.findPlatformInvoices(beginDate, endDate);

		pfIlist.sort((p1, p2) -> (p1.getInvDtIssue().compareTo(p2.getInvDtIssue())));

		// Debit Note
		// CoreAccn accnGli = ctAccnService.getAccountByType(AccountTypes.ACC_TYPE_SP);
		String gliAnncId = sysParam.getValString("CLICKTRUCK-GLI-ACCNID", "GLI");

		List<TCkCtDebitNote> debitNoteList = debitNoteDao.findDebitNotes(beginDate, endDate, gliAnncId);

		// first add all Plaftformfee
		for (TCkCtPlatformInvoice pfI : pfIlist) {

			InvoiceDto inv = new InvoiceDto(pfI, null);

			String sageAccnId = this.getAccnConfig(pfI.getTCoreAccnByInvTo().getAccnId(), ACCN_CONFIG_SAGE_ACCN_ID);

			inv.setBatchNo(batchNo);
			inv.setAccnId(pfI.getTCoreAccnByInvTo().getAccnId());
			inv.setSageAccnId(sageAccnId);
			inv.setAccountName(pfI.getTCoreAccnByInvTo().getAccnName());
			inv.setInvNo(pfI.getInvNo());
			inv.setInvDate(pfI.getInvDtIssue());
			inv.setFiscYear(getYear(pfI.getInvDtIssue()));
			inv.setTotalAmt(pfI.getInvTotal());

			inv.setJobId(pfI.getInvJobId());
			// set is Platform fee to TO
			inv.setPlatFormFee2TV(AccountTypes.ACC_TYPE_TO.name()
					.equalsIgnoreCase(pfI.getTCoreAccnByInvTo().getTMstAccnType().getAtypId()));

			invoiceList.add(inv);
		}

		for (TCkCtDebitNote dn : debitNoteList) {

			invoiceList.add(this.dn2InvDto(dn, batchNo));
		}

		for (int i = 0; i < invoiceList.size(); i++) {
			invoiceList.get(i).setItem(i + 1);
		}

		return invoiceList;
	}

	private InvoiceDto dn2InvDto(TCkCtDebitNote dn, String batchNo) {

		InvoiceDto inv = new InvoiceDto(null, dn);

		inv.setBatchNo(batchNo);
		inv.setAccnId(dn.getTCoreAccnByDnTo().getAccnId());

		String sageAccnId = this.getAccnConfig(dn.getTCoreAccnByDnTo().getAccnId(), ACCN_CONFIG_SAGE_ACCN_ID);
		inv.setSageAccnId(sageAccnId);

		inv.setAccountName(dn.getTCoreAccnByDnTo().getAccnName());
		inv.setInvNo(dn.getDnNo() + "-DN");
		inv.setInvDate(dn.getDnDtDue());
		inv.setFiscYear(getYear(dn.getDnDtDue()));
		inv.setTotalAmt(dn.getDnAmt());
		inv.setJobId(dn.getDnJobId());

		return inv;
	}

	/**
	 * 
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	protected List<InvoiceLineDto> createInvoiceLines(List<InvoiceDto> invoiceList) throws Exception {

		List<InvoiceLineDto> invoiceLineList = new ArrayList<>();

		for (InvoiceDto invDto : invoiceList) {

			if (invDto.getTCkCtPlatformInvoice() != null) {
				TCkCtPlatformInvoice plI = invDto.getTCkCtPlatformInvoice();
				// invoice;
				InvoiceLineDto invIine = new InvoiceLineDto(invDto);
				invIine.setLine(InvoiceLineDto.CTNLINE20);
				invIine.setDesc("CLICTRUCK-" + invDto.getJobId());
				invIine.setAmt(plI.getInvAmt());
				if (invDto.isPlatFormFee2TV()) {
					// to TO
					invIine.setIdacctrev(FeeType.PlatformFee2Tv.getSeqNO());
				} else {
					// to CO
					invIine.setIdacctrev(FeeType.PlatformFee2Co.getSeqNO());
				}
				invoiceLineList.add(invIine);
				///////////////////////////////////////////////////////////////////////////// tax
				invIine = new InvoiceLineDto(invDto);
				invIine.setLine(InvoiceLineDto.CTNLINE40);

				invIine.setDesc(plI.getInvSageTaxNo());

				invIine.setAmt(plI.getInvVat());
				invIine.setIdacctrev(FeeType.TAX.getSeqNO());
				invoiceLineList.add(invIine);
			} else if (invDto.getTCkCtDebitNote() != null) {
				// DebitNote;
				TCkCtDebitNote dn = invDto.getTCkCtDebitNote();

				InvoiceLineDto invIine = new InvoiceLineDto(invDto);
				invIine.setLine(InvoiceLineDto.CTNLINE20);
				invIine.setDesc("CLICTRUCK " + invDto.getJobId());
				invIine.setAmt(dn.getDnTotal());
				invIine.setIdacctrev(FeeType.debitNote.getSeqNO());
				invoiceLineList.add(invIine);
			}
		}

		return invoiceLineList;
	}

	private String getRandomBatchNo() throws Exception {

		String randomInt = seqNoService.getNextSequence(SeqNoCode.CT_SAGE_BATCH_NO.name());
		return randomInt;
	}

	/**
	 * 
	 * @param wb
	 * @param sheetName
	 * @param invoiceList
	 * @throws Exception
	 */
	protected void addInvoiceSheet(HSSFWorkbook wb, String sheetName, List<InvoiceDto> invoiceList) throws Exception {

		HSSFSheet sheet = getSheet(wb, sheetName); // wb.getSheetAt( 0 ); // wb.createSheet( sheetName );

		HSSFCellStyle style = (HSSFCellStyle) wb.createCellStyle();
		style.setDataFormat(wb.createDataFormat().getFormat("0.00"));
		style.setWrapText(true);

		sheet.setColumnWidth(0, 256 * 15);
		sheet.setColumnWidth(1, 256 * 15);
		sheet.setColumnWidth(2, 256 * 15);
		sheet.setColumnWidth(3, 256 * 25);
		sheet.setColumnWidth(4, 256 * 15);

		sheet.setColumnWidth(5, 256 * 50);
		sheet.setColumnWidth(6, 256 * 15);
		sheet.setColumnWidth(7, 256 * 15);
		sheet.setColumnWidth(8, 256 * 15);
		sheet.setColumnWidth(9, 256 * 15);
		sheet.setColumnWidth(10, 256 * 15);
		sheet.setColumnWidth(11, 256 * 15);
		sheet.setColumnWidth(12, 256 * 15);

		int rowid = 0;
		int cellid = 0;

		HSSFRow row = sheet.getRow(rowid);
		row.setHeightInPoints(25);
		row.setRowStyle(style);

		row.setRowStyle(style);

		rowid++;

		for (InvoiceDto inv : invoiceList) {

			cellid = 0;
			row = sheet.createRow(rowid);
			rowid++;

			HSSFCell cell = row.createCell(cellid++);
			cell.setCellValue(inv.getBatchNo());

			cell = row.createCell(cellid++);
			cell.setCellValue(inv.getItem());

			cell = row.createCell(cellid++);
			cell.setCellValue(inv.getSageAccnId());

			cell = row.createCell(cellid++);
			cell.setCellValue(inv.getInvNo());

			cell = row.createCell(cellid++);
			cell.setCellValue(1);

			cell = row.createCell(cellid++);
			cell.setCellValue(inv.getAccountName());

			cell = row.createCell(cellid++);
			cell.setCellValue(getInoviceDate(inv.getInvDate()));

			cell = row.createCell(cellid++);
			cell.setCellValue(inv.getFiscYear());

			cell = row.createCell(cellid++);
			cell.setCellValue("05");

			cell = row.createCell(cellid++);
			cell.setCellValue("IDR");

			cell = row.createCell(cellid++);
			cell.setCellValue("SP");

			cell = row.createCell(cellid++);
			cell.setCellValue(1);

			cell = row.createCell(cellid++);
			cell.setCellValue("N30");
		}

		row = sheet.createRow(rowid);

	}

	/**
	 * 
	 * @param wb
	 * @param sheetName
	 * @param invoiceList
	 * @throws Exception
	 */
	protected void addInvoiceDetailSheet(HSSFWorkbook wb, String sheetName, List<InvoiceLineDto> invoiceLineList)
			throws Exception {

		HSSFSheet sheet = getSheet(wb, sheetName); // wb.getSheetAt( 0 ); // wb.createSheet( sheetName );

		HSSFCellStyle style = (HSSFCellStyle) wb.createCellStyle();
		style.setDataFormat(wb.createDataFormat().getFormat("0.00"));
		style.setWrapText(true);

		sheet.setColumnWidth(0, 256 * 15);
		sheet.setColumnWidth(1, 256 * 15);
		sheet.setColumnWidth(2, 256 * 15);

		sheet.setColumnWidth(3, 256 * 50);
		sheet.setColumnWidth(4, 256 * 15);
		sheet.setColumnWidth(5, 256 * 15);

		int rowid = 0;
		int cellid = 0;

		HSSFRow row = sheet.getRow(rowid);
		row.setHeightInPoints(25);
		row.setRowStyle(style);

		row.setRowStyle(style);

		rowid++;

		for (InvoiceLineDto inv : invoiceLineList) {

			cellid = 0;
			row = sheet.createRow(rowid);
			rowid++;

			HSSFCell cell = row.createCell(cellid++);
			cell.setCellValue(inv.getInvDto().getBatchNo());

			cell = row.createCell(cellid++);
			cell.setCellValue(inv.getInvDto().getItem());

			cell = row.createCell(cellid++);
			cell.setCellValue(inv.getLine());

			cell = row.createCell(cellid++);
			cell.setCellValue(inv.getDesc());

			cell = row.createCell(cellid++);
			cell.setCellValue(inv.getAmt().doubleValue());

			cell = row.createCell(cellid++);
			cell.setCellValue(inv.getIdacctrev());

		}

		row = sheet.createRow(rowid);

	}

	/**
	 * 
	 * @param wb
	 * @param sheetName
	 * @param invoiceList
	 * @throws Exception
	 */
	protected void addInvoicePaymentSchedule(HSSFWorkbook wb, String sheetName, List<InvoiceDto> invoiceList)
			throws Exception {

		HSSFSheet sheet = getSheet(wb, sheetName); // wb.getSheetAt( 0 ); // wb.createSheet( sheetName );

		HSSFCellStyle style = (HSSFCellStyle) wb.createCellStyle();
		style.setDataFormat(wb.createDataFormat().getFormat("0.00"));
		style.setWrapText(true);

		sheet.setColumnWidth(0, 256 * 20);
		sheet.setColumnWidth(1, 256 * 20);
		sheet.setColumnWidth(2, 256 * 20);
		sheet.setColumnWidth(3, 256 * 20);
		sheet.setColumnWidth(4, 256 * 20);

		int rowid = 0;
		int cellid = 0;

		HSSFRow row = sheet.getRow(rowid);
		row.setHeightInPoints(25);
		row.setRowStyle(style);

		row.setRowStyle(style);

		rowid++;

		for (InvoiceDto inv : invoiceList) {

			cellid = 0;
			row = sheet.createRow(rowid);
			rowid++;

			HSSFCell cell = row.createCell(cellid++);
			cell.setCellValue(inv.getBatchNo());

			cell = row.createCell(cellid++);
			cell.setCellValue(inv.getItem());

			cell = row.createCell(cellid++);
			cell.setCellValue(1);

			cell = row.createCell(cellid++);
			cell.setCellValue(getInoviceDate(inv.getInvDate()));

			cell = row.createCell(cellid++);
			cell.setCellValue(inv.getTotalAmt().doubleValue());
		}

		row = sheet.createRow(rowid);
	}

	private String save2Storage(byte[] data, String filename, String batchNo) {

		String basePath;
		try {
			// basePath =
			// coreSysparamDao.find(CtConstant.KEY_ATTCH_BASE_LOCATION).getSysVal();
			basePath = sysParam.getValString(CtConstant.KEY_ATTCH_BASE_LOCATION, "/home/vcc/appAttachments/clictruck/");

			return FileUtil.saveAttachment(batchNo, basePath, filename, data);
		} catch (Exception e) {
			log.error("Fail to save sage Excel file in storage.", e);
		}
		return null;
	}

	protected HSSFCell createCellHeader(HSSFRow row, int cellId) {
		HSSFCell cell = row.createCell(cellId);
		cell.setCellStyle(styleHeader);
		return cell;
	}

	protected HSSFSheet getSheet(HSSFWorkbook workbook, String sheetName) {

		HSSFSheet spreadsheet = null; // wb.getSheetAt( 0 ); // wb.createSheet( sheetName );

		if (workbook.getNumberOfSheets() != 0) {
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				if (workbook.getSheetName(i).equals(sheetName)) {
					spreadsheet = workbook.getSheet(sheetName);
				}
			}
		}
		if (spreadsheet == null) {
			// Create new sheet to the workbook if empty
			spreadsheet = workbook.createSheet(sheetName);
		}
		return spreadsheet;
	}

	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	/**
	 * 
	 * @param period, yyyyMM
	 * @return
	 */
	protected String getInoviceDate(Date invDate) {

		if (null == invDate) {
			return "";
		}
		return sdf.format(invDate);
	}

	private int getYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}

	private String getAccnConfig(String accnId, String key) {

		TCoreAccnConfig accnConfig = coreAccnConfigDao.getByIdAndStatus(new TCoreAccnConfigId(accnId, key),
				Constant.ACTIVE_STATUS);

		if (null != accnConfig) {
			return accnConfig.getAcfgVal();
		}
		return null;
	}

	protected HSSFCellStyle styleHeader;
	protected HSSFCellStyle styleHeader2;
	protected HSSFCellStyle styleYellowBackground;

	protected void initStyle(HSSFWorkbook wb) {
		styleHeader = wb.createCellStyle();
		styleHeader.setWrapText(true);
		// styleHeader.setFillBackgroundColor( HSSFColor.BLACK.index );
		styleHeader.setFillForegroundColor(HSSFColor.HSSFColorPredefined.SKY_BLUE.getIndex());
		styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		HSSFFont font = wb.createFont();
		font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		styleHeader.setFont(font);

		/////////////////////////////////////
		styleHeader2 = wb.createCellStyle();
		styleHeader2.setWrapText(true);
		// styleHeader.setFillBackgroundColor( HSSFColor.BLACK.index );
		styleHeader2.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
		styleHeader2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		font = wb.createFont();
		font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		styleHeader2.setFont(font);

		/////////////////////////////////////
		styleYellowBackground = wb.createCellStyle();
		styleYellowBackground.setWrapText(true);
		// styleHeader.setFillBackgroundColor( HSSFColor.BLACK.index );
		styleYellowBackground.setFillForegroundColor(HSSFColor.HSSFColorPredefined.YELLOW.getIndex());
		styleYellowBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		font = wb.createFont();
		font.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
		styleYellowBackground.setFont(font);
		styleYellowBackground.setDataFormat(wb.createDataFormat().getFormat("0.00"));

	}

	public static class InvoiceDto implements Serializable {

		private static final long serialVersionUID = 1L;

		String batchNo;
		int item;
		String accnId;
		String sageAccnId;
		String invNo;
		String accountName;
		Date invDate;
		int fiscYear;
		String currenty;
		String rateType = "SP";
		int exchangeRatehc = 1;
		String termCode = "N30";

		BigDecimal totalAmt = BigDecimal.ZERO;
		String jobId;

		boolean platFormFee2TV; // TV

		TCkCtPlatformInvoice TCkCtPlatformInvoice = null;
		TCkCtDebitNote TCkCtDebitNote = null;

		public InvoiceDto(TCkCtPlatformInvoice TCkCtPlatformInvoice, TCkCtDebitNote TCkCtDebitNote) {
			this.TCkCtPlatformInvoice = TCkCtPlatformInvoice;
			this.TCkCtDebitNote = TCkCtDebitNote;
		}

		public String getBatchNo() {
			return batchNo;
		}

		public void setBatchNo(String batchNo) {
			this.batchNo = batchNo;
		}

		public int getItem() {
			return item;
		}

		public void setItem(int item) {
			this.item = item;
		}

		public String getAccnId() {
			return accnId;
		}

		public void setAccnId(String accnId) {
			this.accnId = accnId;
		}

		public String getSageAccnId() {
			return sageAccnId;
		}

		public void setSageAccnId(String sageAccnId) {
			this.sageAccnId = sageAccnId;
		}

		public String getInvNo() {
			return invNo;
		}

		public void setInvNo(String invNo) {
			this.invNo = invNo;
		}

		public String getAccountName() {
			return accountName;
		}

		public void setAccountName(String accountName) {
			this.accountName = accountName;
		}

		public Date getInvDate() {
			return invDate;
		}

		public void setInvDate(Date invDate) {
			this.invDate = invDate;
		}

		public int getFiscYear() {
			return fiscYear;
		}

		public void setFiscYear(int fiscYear) {
			this.fiscYear = fiscYear;
		}

		public String getCurrenty() {
			return currenty;
		}

		public void setCurrenty(String currenty) {
			this.currenty = currenty;
		}

		public String getRateType() {
			return rateType;
		}

		public void setRateType(String rateType) {
			this.rateType = rateType;
		}

		public int getExchangeRatehc() {
			return exchangeRatehc;
		}

		public void setExchangeRatehc(int exchangeRatehc) {
			this.exchangeRatehc = exchangeRatehc;
		}

		public String getTermCode() {
			return termCode;
		}

		public void setTermCode(String termCode) {
			this.termCode = termCode;
		}

		public String getJobId() {
			return jobId;
		}

		public void setJobId(String jobId) {
			this.jobId = jobId;
		}

		public BigDecimal getTotalAmt() {
			return totalAmt;
		}

		public void setTotalAmt(BigDecimal totalAmt) {
			this.totalAmt = totalAmt;
		}

		public boolean isPlatFormFee2TV() {
			return platFormFee2TV;
		}

		public void setPlatFormFee2TV(boolean platFormFee2TV) {
			this.platFormFee2TV = platFormFee2TV;
		}

		public TCkCtPlatformInvoice getTCkCtPlatformInvoice() {
			return TCkCtPlatformInvoice;
		}

		public void setTCkCtPlatformInvoice(TCkCtPlatformInvoice tCkCtPlatformInvoice) {
			TCkCtPlatformInvoice = tCkCtPlatformInvoice;
		}

		public TCkCtDebitNote getTCkCtDebitNote() {
			return TCkCtDebitNote;
		}

		public void setTCkCtDebitNote(TCkCtDebitNote tCkCtDebitNote) {
			TCkCtDebitNote = tCkCtDebitNote;
		}

	}

	public static class InvoiceLineDto implements Serializable {

		private static final long serialVersionUID = 1L;

		public static int CTNLINE20 = 20;
		public static int CTNLINE40 = 40;

		int line; // 20 //40
		String desc;
		BigDecimal amt = BigDecimal.ZERO;
		int idacctrev;

		InvoiceDto invDto;

		public InvoiceLineDto(InvoiceDto invDto) {
			this.invDto = invDto;
		}

		public int getLine() {
			return line;
		}

		public void setLine(int line) {
			this.line = line;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public BigDecimal getAmt() {
			return amt;
		}

		public void setAmt(BigDecimal amt) {
			this.amt = amt;
		}

		public int getIdacctrev() {
			return idacctrev;
		}

		public void setIdacctrev(int idacctrev) {
			this.idacctrev = idacctrev;
		}

		public InvoiceDto getInvDto() {
			return invDto;
		}

		public void setInvDto(InvoiceDto invDto) {
			this.invDto = invDto;
		}

	}

	public static enum FeeType {

		PlatformFee2Tv(42231), TAX(24099), debitNote(13112), PlatformFee2Co(42230);

		private int seqNO;

		private FeeType(int seqNO) {
			this.seqNO = seqNO;
		}

		public int getSeqNO() {
			return seqNO;
		}
	}
}
