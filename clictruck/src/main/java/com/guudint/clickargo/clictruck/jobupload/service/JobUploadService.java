package com.guudint.clickargo.clictruck.jobupload.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clicdo.common.service.CkCtCommonService;
import com.guudint.clickargo.clictruck.admin.contract.dao.CkCtContractDao;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContract;
import com.guudint.clickargo.clictruck.common.dao.CkCtVehDao;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.guudint.clickargo.clictruck.constant.CtConstant.AccountTypeEnum;
import com.guudint.clickargo.clictruck.constant.CtConstant.JobRecordFieldEnum;
import com.guudint.clickargo.clictruck.jobupload.dao.CkJobUploadDao;
import com.guudint.clickargo.clictruck.jobupload.dto.JobRecord;
import com.guudint.clickargo.clictruck.jobupload.model.JobRecordTempate.JobRecordTempateItem;
import com.guudint.clickargo.clictruck.jobupload.model.TCkJobUpload;
import com.guudint.clickargo.clictruck.jobupload.validator.JobUploadServiceValidator;
import com.guudint.clickargo.clictruck.master.dao.CkCtMstVehTypeDao;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstVehType;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.util.ExcelPOIUtil;
import com.guudint.clickargo.clictruck.util.ObjectUtil;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.dao.CkAccnDao;
import com.guudint.clickargo.common.model.TCkAccn;
import com.guudint.clickargo.master.dao.CoreAccnDao;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.util.CkDateUtil;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.config.dao.CoreSysparamDao;
import com.vcc.camelone.config.model.TCoreSysparam;
import com.vcc.camelone.util.PrincipalUtilService;
import com.vcc.camelone.util.email.SysParam;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobUploadService {

	private static final Logger log = Logger.getLogger(JobUploadService.class);
	private static final SimpleDateFormat yyyyMMddSDF = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat yyyy_MM_ddSDF = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat HH_mm_ss = new SimpleDateFormat("HH:mm:ss");
	private static final SimpleDateFormat yyyyMMddHHmmssSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static final String INIT_SHIPMENT_REF = "init_shipment_ref";
	private static final String INIT_TO_LOCATION_NAME= "init_to_location_name";
	private static final String INIT_TO_LOCATION = "init_to_location";
	private static final String INIT_FROM_LOCATION_NAME = "init_from_location_name";
	private static final String INIT_FROM_LOCATION = "init_from_location";

	private static final String CARGO_ERROR_MSG = "Value '%s' should match the corresponding column in row %d due to having the same %s and %s!";
	private static final String TRIP_ERROR_MSG = "Value '%s' should match the corresponding column in row %d due to having the same %s!";
	private static final String REQUIRED = "Column %s is required!";
	private static final String GREATER_VALUE = "Column %s value must be greater than 0!";
	private static final String NOT_EXIST = "Column %s value '%s' does not exist!";
	private static final String CONTRACT_TO = "Column %s value '%s' TO is not %s!";
	private static final String CONTRACT_FF = "Column %s value '%s' CO/FF is not %s!";
	private static final String INVALID_DATE_TIME = "Column %s value is invalid date time format(dd/MM/yyyy HH:mm:ss eg. 30/12/2025 12:00:00)!";
	private static final String INVALID_DATE = "Column %s value is invalid date time format(dd/MM/yyyy eg. 30/12/2025)!";
	private static final String INVALID_TRUCK_TYPE = "Invalid truck type for '%s' truck!";
	private static final String TRIP_LOC_ERROR_MSG = "Value '%s' should not match the corresponding column in row %d due to having the same %s!";

	@Autowired
	protected SysParam sysParam;
	@Autowired
	private CkCtCommonService ckCtCommonService;
	@Autowired
	private CkJobUploadDao ckJobUploadDao;
	@Autowired
	private CoreSysparamDao coreSysparamDao;
	@Autowired
	private CkAccnDao ckAccnDao;
	@Autowired
	private JobUploadUtilService jobUploadUtilService;
	@Autowired
	private CkCtContractDao ckCtContractDao;
	@Autowired
	private PrincipalUtilService principalUtilService;
	@Autowired
	private CkCtMstVehTypeDao ckCtMstVehTypeDao;
	@Autowired
	private CkCtVehDao ckCtVehDao;
	@Autowired
	private CoreAccnDao coreAccnDao;
	@Autowired
	private JobUploadServiceValidator serviceValidator;

	private boolean isXlsFormat = false;

	ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Download Excel template by accnId;
	 * @param accnId
	 * @return
	 * @throws Exception
	 */
	public String downloadExcelExample(String accnId) throws Exception {

		if(StringUtils.isBlank(accnId)) {
			throw new Exception("Parameter accnId is empty or null ");
		}

		TCkAccn ckAccn = ckAccnDao.find( accnId );

		String filePath = null;

		if(null != ckAccn && StringUtils.isNoneBlank(ckAccn.getCaccnExcelTemplateExample())) {
			// in T_CK_ACCN
			filePath = ckAccn.getCaccnExcelTemplateExample();
		} else {
			// Default
			filePath = jobUploadUtilService.getSysParam("CLICTRUCK_UPLOAD_EXCEL_TEMPLATE", "/docs/UploadExcelTemplate/DefaultTemplate.xlsx");
		}
		// root path
		String rootPath = jobUploadUtilService.getSysParam(CtConstant.KEY_JRXML_BASE_PATH, "/home/vcc/jasper/");

		byte[] byteArray = Files.readAllBytes(Paths.get(rootPath + filePath));
		String base64 = Base64.getEncoder().encodeToString(byteArray);

		return base64;
	}

	@SuppressWarnings("unused")
	public TCkJobUpload uploadExcel(Principal principal, byte[] excelData, String fileName) throws Exception {

		if (principal == null)
			throw new ParameterException("param principal null");
		if (excelData == null || excelData.length == 0)
			throw new ParameterException("param excelBody null");

		// Store file in local storage;
		String accnId = principal.getUserAccnId();
		String localFilePath = storeExcelFile(accnId, excelData, fileName);

		TCkJobUpload jobUpload = null;
		jobUpload = new TCkJobUpload(CkUtil.generateId(TCkJobUpload.PREFIX_ID));
		jobUpload.setUpAccnId(accnId);
		jobUpload.setUpFileName(fileName);
		jobUpload.setUpFilePath(localFilePath);
		jobUpload.setUpFileSize(excelData.length);
		jobUpload.setUpDtCreate(new Date());
		jobUpload.setUpUidCreate(principal.getUserId());

		List<JobRecord> jobRecordList = new ArrayList<>();
		Map<Integer, String> successJobMap = new HashMap<>(); // Map<rowId, jobId> 2:JOBID, 4:JOBID, 6:JOBID, 8:JOBID
		LinkedHashMap<String, String> failLinesMaps = new LinkedHashMap<>(); // Map<rowId, errorMsg> 1:errA, 3:errB, 5:errC, 7:errD
		StringBuilder remark = new StringBuilder();

		try {
			// XLS or XLSX
			if (ExcelPOIUtil.isXlsxFormat(excelData)) {
				isXlsFormat = false;
			} else if (ExcelPOIUtil.isXlsFormat(excelData)) {
				isXlsFormat = true;
			} else {
				throw new Exception("Unknown file format.");
			}

			// fetch JSON template
			TCkAccn ckAccn = ckAccnDao.find(accnId);
			//if (null == ckAccn || StringUtils.isBlank(ckAccn.getCaccnExcelTemplate())) {
			//	throw new Exception("Fail to find job excel JSON template: " + accnId);
			//}

			List<JobRecordTempateItem> templateItemList = jobUploadUtilService.getJobUploadTemplate(accnId);

			// Convert to JobRecords;

			// first Sheet
			try (FileInputStream fileInputStream = new FileInputStream(new File(localFilePath));
				 Workbook workbook = isXlsFormat ? new HSSFWorkbook(fileInputStream)
						 : new XSSFWorkbook(fileInputStream);) {

				Sheet sheet0 = workbook.getSheetAt(0);
				Sheet sheet1 = null;
				if (workbook.getNumberOfSheets() > 1) {
					sheet1 = workbook.getSheetAt(1);
				}

				// if (null == sheet1 || ExcelPOIUtil.isSheetEmpty(sheet1)) {
				if (null != ckAccn && StringUtils.isNoneBlank(ckAccn.getCaccnExcelTemplate())) {
					// Only 1 sheet, compatible version 1
					Row sheetRow = sheet0.getRow(0);
					if (ExcelPOIUtil.isRowEmpty(sheetRow)) {
						throw new Exception("First line is empty.");
					}

					Map<Integer, String> sheetTitleMap = this.parseExcelTitleRowForExtendSheetWithHiddenCol(sheetRow,templateItemList, false);

					for (int rowId = 1; rowId < sheet0.getPhysicalNumberOfRows(); rowId++) {
						try {
							sheetRow = sheet0.getRow(rowId);
							if (ExcelPOIUtil.isRowEmpty(sheetRow)) {
								if (isBlankRow(sheetRow)) { // deep check for all columns are empty
									failLinesMaps.put(Integer.toString(rowId + 1), "blank row");
								}
								continue;
							}

							JobRecord jobRecord = this.convertRow2JobRecord(sheetTitleMap, templateItemList, sheetRow,
									failLinesMaps, jobRecordList, sheet0.getRow(0));
							jobRecord.setRowId(rowId);
							jobRecordList.add(jobRecord);

							log.info("jobRecord: " + jobRecord);

						} catch (Exception e) {
							log.error("", e);
							failLinesMaps.put(Integer.toString(rowId+1), e.getMessage());
						}
					}
				} else {
					// Parse sheet0 and first row
					Row sheet0Row = sheet0.getRow(0);
					if (ExcelPOIUtil.isRowEmpty(sheet0Row)) {
						throw new Exception("First line is empty.");
					}
					Map<Integer, String> sheet1TitleMap = null;
					// Parse sheet1 and first row

					// Parse first row
					Row sheet1Row = null;
					if( null != sheet1) {
						sheet1Row = sheet1.getRow(0);
						if (!ExcelPOIUtil.isRowEmpty(sheet1Row)) {
							sheet1TitleMap = this.parseExcelTitleRowForExtendSheet(sheet1Row, true);
						}
					}

					for (int rowId = 1; rowId < sheet0.getPhysicalNumberOfRows(); rowId++) {
						try {
							sheet0Row = sheet0.getRow(rowId);
							if (ExcelPOIUtil.isRowEmpty(sheet0Row)) {
								log.info("Skipping empty row: " + rowId);
								continue;
							}

							//Default excel converter
							JobRecord jobRecord = convertDefaultExcel(templateItemList, sheet0Row, failLinesMaps, jobRecordList, sheet0.getRow(0));
							jobRecord.setRowId(rowId);
							jobRecordList.add(jobRecord);

							// sheet1
							if( null != sheet1) {
								sheet1Row = sheet1.getRow(rowId);
								if (ExcelPOIUtil.isRowEmpty(sheet1Row)) {
									log.info("Skipping empty row in sheet1: " + rowId);
									continue;
								}

								if (null != sheet1TitleMap) {
									Map<String, Object> extendAttrs = this.convertRow2Map(sheet1TitleMap, sheet1Row);
									jobRecord.setExtendAttrs(extendAttrs);
									this.setupSepcialAttribute(jobRecord, templateItemList);
								}
							}

							log.info("jobRecord: " + jobRecord);

						} catch (Exception e) {
							log.error("", e);
							failLinesMaps.put(Integer.toString(rowId+1), e.getMessage());
						}
					}
				}

			} catch (Exception e) {
				log.error("", e);
				throw new Exception(e);
			}

			// Merge for Multi-Drop;
			this.setupMulitiDrop(jobRecordList);

			this.validateDateOfDeliverWhenMigration(jobRecordList, failLinesMaps);
			this.validateSameStartAndEndLocationForMultiDrop(jobRecordList, failLinesMaps);
			this.validateShouldSameStartLocationForMultiDrop(jobRecordList, failLinesMaps);

			// Create Job
			if (jobRecordList.size() == 1){ // single trip
				// Create Job
				for (JobRecord jobRecord : jobRecordList) {
					// Only create job when parent job is null
					if (jobRecord.getParentJobRecord() == null && !failLinesMaps.containsKey(String.valueOf(jobRecord.getRowId())) && failLinesMaps.isEmpty()) {
						try {
							TCkJobTruck jobTruck = jobUploadUtilService.createJob(principal, accnId, jobRecord);
							successJobMap.put(jobRecord.getRowId()+1, jobTruck.getJobId());
							jobRecord.getMultiDrops().forEach(
									subJobRecord -> successJobMap.put(subJobRecord.getRowId()+1, jobTruck.getJobId()));
						} catch (Exception e) {
							log.error("Fail to crate job, rowId: " + jobRecord.getRowId()+1, e);
							failLinesMaps.put(String.valueOf(jobRecord.getRowId()+1), e.getMessage());
							jobRecord.getMultiDrops().forEach(subJobRecord -> failLinesMaps.put(String.valueOf(subJobRecord.getRowId()+1), e.getMessage()));
						}
					}
				}
			} else if (jobRecordList.size() > 1) { // multiple trips
				// Create Job
				List<TCkJobTruck> jobTrucksObjectList = new ArrayList<>();
				List<TCkCtTrip> existTrips = new ArrayList<>();
				int tripSeq = 1;
				for (JobRecord jobRecord : jobRecordList) {
					tripSeq++;
					// Only create job when parent job is null
					if (jobRecord.getParentJobRecord() == null && !failLinesMaps.containsKey(String.valueOf(jobRecord.getRowId())) && failLinesMaps.isEmpty()) {
						try {
							TCkJobTruck jobTruck = jobUploadUtilService.createJobWithMultiDrop(principal, accnId, jobRecord,jobTrucksObjectList, tripSeq, existTrips);
							successJobMap.put(jobRecord.getRowId()+1, jobTruck.getJobId());
							jobRecord.getMultiDrops().forEach(
									subJobRecord -> successJobMap.put(subJobRecord.getRowId()+1, jobTruck.getJobId()));
						} catch (Exception e) {
							log.error("Fail to crate job, rowId: " + jobRecord.getRowId()+1, e);
							failLinesMaps.put(String.valueOf(jobRecord.getRowId()+1), e.getMessage());
							jobRecord.getMultiDrops().forEach(subJobRecord -> failLinesMaps.put(String.valueOf(subJobRecord.getRowId()+1), e.getMessage()));
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("", e);
			remark.append(e.getMessage()).append("\n");
		} finally {
			try {
				int totalLines = 0;
				if (successJobMap.size() > 0) {
					jobUpload.setUpSuccessJobIds(objectMapper.writeValueAsString(successJobMap));
					totalLines = totalLines + successJobMap.size();
				}
				if (!failLinesMaps.isEmpty()) {
					Set<String> uniqueRows = failLinesMaps.keySet().stream()
							.map(key -> key.split(":")[0])
							.collect(Collectors.toSet());

					jobUpload.setUpFailLines(objectMapper.writeValueAsString(failLinesMaps));
					totalLines += uniqueRows.size();
				}
				jobUpload.setUpTotalLines(totalLines);
				jobUpload.setUpRemark(remark.toString());

				ckJobUploadDao.add(jobUpload);
			} catch (Exception e) {
				log.error("", e);
				// throw e;
			}
		}
		return jobUpload;
	}

	private JobRecord convertRow2JobRecord(Map<Integer, String> titleMap, List<JobRecordTempateItem> titleTempate,
										   Row row, LinkedHashMap<String, String> failLinesMaps, List<JobRecord> jobRecordList,Row colHeader) throws Exception {

		CoreAccn currentUserLogin = principalUtilService.getPrincipal().getCoreAccn();
		JobRecord jobRecord = new JobRecord();
		int visibleIndex = 0;
		LinkedHashMap<String, String> initValue = new LinkedHashMap<>();

		for (int i = 0; i < titleTempate.size(); i++) {
			JobRecordTempateItem jobRecordTempateItem = titleTempate.get(i);
			if (jobRecordTempateItem == null) {
				failLinesMaps.put("Row " + row.getRowNum(), "Template item is null at index: " + i);
				continue;
			}
			Cell cell = null;
			JobRecordFieldEnum field = null;

			if (JobRecordFieldEnum.isValidEnum(jobRecordTempateItem.getField()) != null) {
				field = JobRecordFieldEnum.valueOf(jobRecordTempateItem.getField().toUpperCase());
			}

			if (jobRecordTempateItem.getHidden() != null && jobRecordTempateItem.getHidden().equals("Y")) {
				if (jobRecordTempateItem.getType() != null && jobRecordTempateItem.getType().equals("init")){
					initValue.put(jobRecordTempateItem.getField(), jobRecordTempateItem.getDefault());
				} else {
					field = JobRecordFieldEnum.valueOf(jobRecordTempateItem.getField().toUpperCase());
				}
			} else {
				// Get the cell corresponding to the visible index
				cell = row.getCell(visibleIndex);
				visibleIndex++;
			}

			String cellTitle = titleMap.get(i);

			if (StringUtils.isBlank(cellTitle)) {
				continue;
			}

			if (field != null){
				this.validateCell(cell, field, failLinesMaps, row,jobRecordTempateItem, currentUserLogin, jobRecordList,initValue, colHeader, visibleIndex);
			}

			if (null == field) {
				if (cell != null) {
					String cellValue = ExcelPOIUtil.getCellValueAsString(cell);
					if (StringUtils.isNotBlank(cellValue)) {
						jobRecord.getExtendAttrs().put(cellTitle, cellValue);
						this.validateExtCell(cellTitle,cellValue, failLinesMaps, row,jobRecordTempateItem, jobRecordList,initValue, colHeader, visibleIndex);
					}
				} else {
					// Log or handle the case where cell is null
					System.out.println("Cell is null for cellTitle: " + cellTitle);
				}
			} else {
				switch (field) {
					case CUSTOMER:
					case CONTRACT_ID:
						jobRecord.setContractId(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()));
						break;
					case COUNTRY:
						jobRecord.setCountry(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()));
						break;
					case START_LOCATION:
					case PICKUP_ADDRESS:
					case PICK_UP_ADDRESS:
						jobRecord.setStartLoc(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()));
						jobRecord.setStartLocAddress(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()));
						break;
					case FROM_LOCATION_DETAILS:
						jobRecord.setStartLocAddress(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()));
						break;
					case DELIVERY_ADDRESS:
					case DROPOFF_ADDRESS:
					case END_LOCATION:
						jobRecord.setEndLoc(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()));
						jobRecord.setEndLocAddress(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()));
						break;
					case TO_LOCATION_DETAILS:
						jobRecord.setEndLocAddress(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()));
						break;
					case DATE_OF_DELIVERY:
						jobRecord.setDateOfDelivery(this.getCellValueForDate(cell, jobRecordTempateItem.getDefault()));
						jobRecord.setFromLocDateTime(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()));
						jobRecord.setToLocDateTime(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()));
						break;
					case TIME_OF_DELIVERY:
						jobRecord.setTimeOfDelivery(this.getCellValueForTime(cell, jobRecordTempateItem.getDefault()));
						break;
					case PLAN_DATE_CSP:
					case PLAN_DATE:
					case JOB_DATE:
						if (cell != null) {
							Date date = null;

							// Check if the cell is numeric and formatted as a date
							if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
								date = cell.getDateCellValue();
							}
							// Handle string cell type
							else if (cell.getCellType() == CellType.STRING) {
								date = this.getCellValueForDate(cell, jobRecordTempateItem.getDefault());
							}

							if (date != null) {
								jobRecord.setPlanDate(date);
								jobRecord.setBookingDate(date);
							} else {
								System.out.println("Failed to parse date for JOB_DATE.");
							}
						}
						break;

					case BOOKING_DATE_CSQ:
					case BOOKING_DATE:
						jobRecord.setBookingDate(this.getCellValueForDate(cell, jobRecordTempateItem.getDefault()));
						break;
					case LOADING_CSQ:
					case LOADING:
						jobRecord.setLoading(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()));
						break;
					case EMAIL_NOTIFICATION:
						jobRecord.setEmailNotification(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()));
						break;
					case ITEM_DESCRIPTION:
					case DESCRIPTION_CLASQUIN:
					case DESCRIPTION_DEFAULT:
					case DESC:
						jobRecord.setDescription(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()));
						break;
					case PAYMENT_METHOD:
						jobRecord.setPaymentMethod(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()));
						break;
					case CARGO_TYPE_CLASQUIN:
					case CARGO_TYPE:
						jobRecord.setCargoType(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()));
						break;
					case CARGO_WEIGHT_CLASQUIN:
					case ITEM_WEIGHT:
					case CARGO_WEIGHT:
					case WEIGHT:
						jobRecord.setCargoWeight(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()));
						break;
					case CARGO_TRUCK_TYPE_CLASQUIN:
					case CARGO_TRUCK_TYPE:
					case TRUCK_TYPE:
						jobRecord.setCargoTruckType(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()));
						break;
					case CARGO_VOLUME:
					case VOLUME:
						if (StringUtils.isNotBlank(this.getCellValueForString(cell, jobRecordTempateItem.getDefault())) &&
								Objects.nonNull(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()))){
							jobRecord.setCgCargoVolume(Double.valueOf(this.getCellValueForString(cell, jobRecordTempateItem.getDefault())));
						}
						break;
					case TRUCK_LICENSE_PLATE_NUMBER:
						jobRecord.setTruckPlateNo(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()));
						break;
					case DRIVER_USERNAME:
						jobRecord.setDriverName(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()));
						break;
					case JOB_LINKING_NUMBER:
						jobRecord.setLinkingNumber(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()));
						break;
					case REMARK:
					case ADDITIONAL_INFO:
					case DROPOFF_REMARK:
						jobRecord.setToLocRemark(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()));
						break;
					case REMARK_TVH:
						jobRecord.setFromLocRemarks(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()));
						break;
					case SHIPMENT_NO:
					case INVOICE_NUMBER:
					case CUSTOMER_INVOICE_NUMBER:
					case SHIPMENT_REF_NO:
						jobRecord.setShipmentRefNo(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()));
						break;
					case QTY:
					case CASES_QTY:
					case QUANTITY:
						String cellValue = this.getCellValueForString(cell, jobRecordTempateItem.getDefault());
						if (cellValue == null || cellValue.trim().isEmpty()) {
							jobRecord.setCgCargoQty(0.0);
						} else {
							jobRecord.setCgCargoQty(Double.valueOf(cellValue));
						}
						break;
					case LENGTH_CM:
						String length = this.getCellValueForString(cell, jobRecordTempateItem.getDefault());
						if (length == null || length.trim().isEmpty()) {
							jobRecord.setCgCargoLength(0.0);
						} else {
							double lengthValue = convertCmToMeter(Double.parseDouble(length));
							jobRecord.setCgCargoLength(lengthValue);
						}
						break;
					case WIDTH_CM:
						String cargoWidth = this.getCellValueForString(cell, jobRecordTempateItem.getDefault());
						if (cargoWidth == null || cargoWidth.trim().isEmpty()) {
							jobRecord.setCgCargoWidth(0.0);
						} else {
							double widthValue = convertCmToMeter(Double.parseDouble(cargoWidth));
							jobRecord.setCgCargoWidth(widthValue);
						}

						break;
					case HEIGHT_CM:
						String cargoHeight = this.getCellValueForString(cell, jobRecordTempateItem.getDefault());
						if (cargoHeight == null || cargoHeight.trim().isEmpty()) {
							jobRecord.setCgCargoHeight(0.0);
						} else {
							double heightValue = convertCmToMeter(Double.parseDouble(cargoHeight));
							jobRecord.setCgCargoHeight(heightValue);
						}
						break;
					case CONTACT_NUMBER:
					case PHONE_NUMBER_NOTIFICATION:
						jobRecord.setToLocMobileNumber(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()));
						break;
					case PICKUP_DATETIME:
						if (jobRecord.getPlanDate() != null){
							jobRecord.setFromLocDateTime(String.format(jobRecordTempateItem.getDefault(),convert2DateTime(jobRecord.getPlanDate().toString(), null)));
						}
						break;
					case DROPOFF_DATETIME:
						if (jobRecord.getPlanDate() != null){
							jobRecord.setToLocDateTime(String.format(jobRecordTempateItem.getDefault(),convert2DateTime(jobRecord.getPlanDate().toString(), null)));
						}
						break;
					case TO_LOCATION_MOBILE:
						jobRecord.setToLocMobileNumber(jobRecordTempateItem.getDefault());
						break;
					case BOOKING_DATE_CSP:
						if (jobRecord.getPlanDate() != null){
							jobRecord.setBookingDate(jobRecord.getPlanDate());
						}
						break;
					case CARGO_OWNER_CSP:
						if (this.getCellValueForString(cell, jobRecordTempateItem.getDefault()) != null) {
							jobRecord.setCargoOwner(this.getCellValueForString(cell, jobRecordTempateItem.getDefault()));
						}
						break;
					case DATE_OF_DELIVERY_CSP:
						Date utcDate = getCellValueForDateTime(cell, jobRecordTempateItem.getDefault());
						SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
						jobRecord.setToLocDateTime(outputFormat.format(utcDate));
						break;
					default:
						break;
				}
			}
		}
		return jobRecord;
	}

	private void validateCell(Cell cell, JobRecordFieldEnum field, LinkedHashMap<String, String> error, Row rowNum,
			JobRecordTempateItem jobRecordTempateItem, CoreAccn currentUserLogin, List<JobRecord> jobRecordList,
			LinkedHashMap<String, String> initValue, Row header, int visibleIndex) throws Exception {

		String rowNumAndField = rowNum.getRowNum() + 1 + ":" + visibleIndex + ":"+ jobRecordTempateItem.getLabel();

		switch (field) {
			case CONTRACT_ID:
				String contractName = ExcelPOIUtil.getCellValueAsString(cell);
				if (StringUtils.isBlank(contractName)) {
					error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
				} else {
					TCkCtContract existingContract = this.getExistingContract(contractName);
					if (Objects.isNull(existingContract)) {
						error.put(rowNumAndField, String.format(NOT_EXIST, jobRecordTempateItem.getLabel(), contractName));
					} else if (AccountTypes.ACC_TYPE_TO.name().equalsIgnoreCase(currentUserLogin.getTMstAccnType().getAtypId())) {
						if (!currentUserLogin.getAccnId().equalsIgnoreCase(existingContract.getTCoreAccnByConTo().getAccnId())) {
							error.put(rowNumAndField, String.format(CONTRACT_TO, jobRecordTempateItem.getLabel(), contractName, currentUserLogin.getAccnId()));
						}
					} else if (!currentUserLogin.getAccnId().equalsIgnoreCase(existingContract.getTCoreAccnByConCoFf().getAccnId())) {
						error.put(rowNumAndField, String.format(CONTRACT_FF, jobRecordTempateItem.getLabel(), contractName, currentUserLogin.getAccnId()));
					} else {
						if (jobRecordList.size() > 0) {
							List<JobRecord> existingTrips = jobRecordList.stream()
									.filter(val -> val.getShipmentRefNo().trim().equalsIgnoreCase(ExcelPOIUtil.getCellValueAsString(rowNum.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))))))
									.collect(Collectors.toList());

							if (!existingTrips.isEmpty()) {
								JobRecord firstTrip = existingTrips.get(0);
								if (!this.isMatchingContract(firstTrip, contractName)){
									error.put(rowNumAndField, String.format(TRIP_ERROR_MSG, contractName, firstTrip.getRowId() + 1, jobRecordTempateItem.getLabel()));
								}
							}
						}
					}
				}
				break;
			// For Sagawa Only TO & FF
			case CUSTOMER:
				String customerContract = cell != null ? ExcelPOIUtil.getCellValueAsString(cell) : jobRecordTempateItem.getDefault();
				if (StringUtils.isBlank(customerContract)) {
					error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
				} else {
					TCkCtContract existingContract = null;
					if (AccountTypeEnum.isValid(currentUserLogin.getAccnId())) {
						// Find Contract for Sagawa FF
						if (customerContract.equalsIgnoreCase("SSA")){
							existingContract = this.getContractByFF(currentUserLogin.getAccnId(), customerContract.toUpperCase());
						}else {
							existingContract = this.getContractByFF(currentUserLogin.getAccnId(), AccountTypeEnum.valueOf(customerContract.toUpperCase()).getValue());
						}
					} else {
						// Find Contract for Sagawa TO
						existingContract = this.getContractByFF(AccountTypeEnum.valueOf(customerContract.toUpperCase()).getValue(), "SSA");
					}

					// Validate contract existence and ownership
					if (existingContract == null) {
						error.put(rowNumAndField, String.format(NOT_EXIST, jobRecordTempateItem.getLabel(), customerContract));
					} else if (AccountTypes.ACC_TYPE_TO.name().equalsIgnoreCase(currentUserLogin.getTMstAccnType().getAtypId())) {
						if (!currentUserLogin.getAccnId().equalsIgnoreCase(existingContract.getTCoreAccnByConTo().getAccnId())) {
							error.put(rowNumAndField, String.format(CONTRACT_TO, jobRecordTempateItem.getLabel(), customerContract, currentUserLogin.getAccnId()));
						}
					} else {
						if (!currentUserLogin.getAccnId().equalsIgnoreCase(existingContract.getTCoreAccnByConCoFf().getAccnId())) {
							error.put(rowNumAndField, String.format(CONTRACT_FF, jobRecordTempateItem.getLabel(), customerContract, currentUserLogin.getAccnId()));
						}
					}
				}

				break;

			case CUSTOMER_REF:
				String customerRef = ExcelPOIUtil.getCellValueAsString(cell);
				if (!StringUtils.isBlank(customerRef)){
					if (jobRecordList.size() > 0) {
						List<JobRecord> existingTrips = jobRecordList.stream()
								.filter(val -> val.getShipmentRefNo().trim().equalsIgnoreCase(ExcelPOIUtil.getCellValueAsString(rowNum.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))))))
								.collect(Collectors.toList());

						if (!existingTrips.isEmpty()) {
							JobRecord firstTrip = existingTrips.get(0);
							String invRef = String.valueOf(header.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))).getStringCellValue());
							if (!this.isMatchingCustomerRef(firstTrip, customerRef)){
								error.put(rowNumAndField, String.format(TRIP_ERROR_MSG, customerRef, firstTrip.getRowId() + 1, invRef));
							}
						}
					}
				}
				break;

			case FROM_LOCATION_NAME:
				String fromLocDef = ExcelPOIUtil.getCellValueAsString(cell);
				if (!StringUtils.isBlank(fromLocDef)) {
					if (jobRecordList.size() > 0) {
						List<JobRecord> existingTrips = jobRecordList.stream()
								.filter(val -> val.getShipmentRefNo().trim().equalsIgnoreCase(ExcelPOIUtil.getCellValueAsString(rowNum.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))))))
								.collect(Collectors.toList());

						if (!existingTrips.isEmpty()) {
							JobRecord firstTrip = existingTrips.get(0);
							String invRef = String.valueOf(header.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))).getStringCellValue());
							if (!this.isMatchingStartLocation(firstTrip, fromLocDef)){
								error.put(rowNumAndField, String.format(TRIP_ERROR_MSG, fromLocDef, firstTrip.getRowId() + 1, invRef));
							}
						}
					}
				}
				break;
			case PICK_UP_ADDRESS:
			case START_LOCATION:
			case PICKUP_ADDRESS:
			case FROM_LOCATION_DETAILS:
			case START_FROM_LOCATION:
				String fromLoc = ExcelPOIUtil.getCellValueAsString(cell);
				if (StringUtils.isBlank(fromLoc)) {
					error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
				}else {
					if (jobRecordList.size() > 0) {
						List<JobRecord> existingTrips = jobRecordList.stream()
								.filter(val -> val.getShipmentRefNo().trim().equalsIgnoreCase(ExcelPOIUtil.getCellValueAsString(rowNum.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))))))
								.collect(Collectors.toList());

						if (!existingTrips.isEmpty()) {
							JobRecord firstTrip = existingTrips.get(0);
							String invRef = String.valueOf(header.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))).getStringCellValue());
							if (!this.isMatchingStartLocation(firstTrip, fromLoc)){
								error.put(rowNumAndField, String.format(TRIP_ERROR_MSG, fromLoc, firstTrip.getRowId() + 1, invRef));
							}
						}
					}
				}
				break;
			case INVOICE_NUMBER:
			case SHIPMENT_NO:
			case SHIPMENT_REF_NO:
			case SHIPMENT_REF:
				String invNo = ExcelPOIUtil.getCellValueAsString(cell);
				if (StringUtils.isBlank(invNo)){
					error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
				}
				break;
			case JOB_SUB_TYPE:
				String jobSubType = ExcelPOIUtil.getCellValueAsString(cell);
				if (!StringUtils.isBlank(jobSubType)){
					if (jobRecordList.size() > 0) {
						List<JobRecord> existingTrips = jobRecordList.stream()
								.filter(val -> val.getShipmentRefNo().trim().equalsIgnoreCase(ExcelPOIUtil.getCellValueAsString(rowNum.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))))))
								.collect(Collectors.toList());

						if (!existingTrips.isEmpty()) {
							JobRecord firstTrip = existingTrips.get(0);
							String invRef = String.valueOf(header.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))).getStringCellValue());
							if (!this.isMatchingJobSubType(firstTrip, jobSubType)){
								error.put(rowNumAndField, String.format(TRIP_ERROR_MSG, jobSubType, firstTrip.getRowId() + 1, invRef));
							}
						}
					}
				}
				break;
			case LOADING:
				String loading = ExcelPOIUtil.getCellValueAsString(cell);
				if (StringUtils.isBlank(loading)){
					error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
				}else {
					if (jobRecordList.size() > 0) {
						List<JobRecord> existingTrips = jobRecordList.stream()
								.filter(val -> val.getShipmentRefNo().trim().equalsIgnoreCase(ExcelPOIUtil.getCellValueAsString(rowNum.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))))))
								.collect(Collectors.toList());

						if (!existingTrips.isEmpty()) {
							JobRecord firstTrip = existingTrips.get(0);
							String invRef = String.valueOf(header.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))).getStringCellValue());
							if (!this.isMatchingLoading(firstTrip, loading)){
								error.put(rowNumAndField, String.format(TRIP_ERROR_MSG, loading, firstTrip.getRowId() + 1, invRef));
							}
						}
					}
				}
				break;
			case FROM_LOCATION_MOBILE_NUMBER:
				String fromMobile = ExcelPOIUtil.getCellValueAsString(cell);
				if (!StringUtils.isBlank(fromMobile)){
					if (jobRecordList.size() > 0) {
						List<JobRecord> existingTrips = jobRecordList.stream()
								.filter(val -> val.getShipmentRefNo().trim().equalsIgnoreCase(ExcelPOIUtil.getCellValueAsString(rowNum.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))))))
								.collect(Collectors.toList());

						if (!existingTrips.isEmpty()) {
							JobRecord firstTrip = existingTrips.get(0);
							if (!this.isMatchingStartLocationMobilePhone(firstTrip, fromMobile)){
								error.put(rowNumAndField, String.format(TRIP_ERROR_MSG, fromMobile, firstTrip.getRowId() + 1, jobRecordTempateItem.getLabel()));
							}
						}
					}
				}
				break;
			case FROM_LOCATION_REMARKS:
				String fromRemark = ExcelPOIUtil.getCellValueAsString(cell);
				if (!StringUtils.isBlank(fromRemark)){
					if (jobRecordList.size() > 0) {
						List<JobRecord> existingTrips = jobRecordList.stream()
								.filter(val -> val.getShipmentRefNo().trim().equalsIgnoreCase(ExcelPOIUtil.getCellValueAsString(rowNum.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))))))
								.collect(Collectors.toList());

						if (!existingTrips.isEmpty()) {
							JobRecord firstTrip = existingTrips.get(0);
							if (!this.isMatchingStartLocationRemark(firstTrip, fromRemark)){
								error.put(rowNumAndField, String.format(TRIP_ERROR_MSG, fromRemark, firstTrip.getRowId() + 1, jobRecordTempateItem.getLabel()));
							}
						}
					}
				}
				break;

			case ADDITIONAL_INFO:
				processCellValidationTo(cell, jobRecordList, initValue, rowNum, header, error, rowNumAndField, "DELIVERY_NOTE");
				break;

			case TO_LOCATION_REMARKS:
				processCellValidationTo(cell, jobRecordList, initValue, rowNum, header, error, rowNumAndField, "TO_LOCATION_REMARKS");
				break;

			case REMARK:
				processCellValidationTo(cell, jobRecordList, initValue, rowNum, header, error, rowNumAndField, "REMARK");
				break;

			case CONTACT_NUMBER:
				processCellValidationTo(cell, jobRecordList, initValue, rowNum, header, error, rowNumAndField, "CONTACT_NUMBER");
				break;

			case PHONE_NUMBER_NOTIFICATION:
				String toMobileNotif = ExcelPOIUtil.getCellValueAsString(cell);
				if (jobRecordList.size() > 0) {
					List<JobRecord> existingTrips = jobRecordList.stream()
							.filter(val -> val.getShipmentRefNo().trim().equalsIgnoreCase(ExcelPOIUtil.getCellValueAsString(rowNum.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))))))
							.collect(Collectors.toList());

					if (!existingTrips.isEmpty()) {
						JobRecord firstTrip = existingTrips.get(0);
						if (!this.isMatchingEndLocationMobilePhone(firstTrip, toMobileNotif)){
							error.put(rowNumAndField, String.format(TRIP_ERROR_MSG, toMobileNotif, firstTrip.getRowId() + 1, jobRecordTempateItem.getLabel()));
						}
					}
				}
				break;
			case TO_LOCATION_MOBILE_NUMBER:
				String toMobile = ExcelPOIUtil.getCellValueAsString(cell);
				if (StringUtils.isBlank(toMobile)){
					error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
				}else {
					processCellValidationTo(cell, jobRecordList, initValue, rowNum, header, error, rowNumAndField, "TO_LOCATION_MOBILE_NUMBER");
				}
				break;
			case TO_LOCATION_NAME:
				String toLocDef = ExcelPOIUtil.getCellValueAsString(cell);
				if (!StringUtils.isBlank(toLocDef)){
					if (jobRecordList.size() > 0) {
						List<JobRecord> existingTrips = jobRecordList.stream()
								.filter(val -> val.getShipmentRefNo().trim().equalsIgnoreCase(ExcelPOIUtil.getCellValueAsString(rowNum.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))))))
								.collect(Collectors.toList());

						if (!existingTrips.isEmpty()) {
							JobRecord firstTrip = existingTrips.get(0);
							if (this.isMatchingEndLocation(firstTrip, String.valueOf(rowNum.getCell(Integer.parseInt(initValue.get(INIT_FROM_LOCATION)))))){
								error.put(rowNumAndField, String.format(TRIP_LOC_ERROR_MSG, toLocDef, firstTrip.getRowId() + 1, jobRecordTempateItem.getLabel()));
							}
						}
					}
				}
				break;
			case DELIVERY_ADDRESS:
			case DROPOFF_ADDRESS:
			case END_LOCATION:
			case TO_LOCATION_DETAILS:
				String toLoc = ExcelPOIUtil.getCellValueAsString(cell);
				if (StringUtils.isBlank(toLoc)){
					error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
				}else {
					if (jobRecordList.size() > 0) {
						List<JobRecord> existingTrips = jobRecordList.stream()
								.filter(val -> val.getShipmentRefNo().trim().equalsIgnoreCase(ExcelPOIUtil.getCellValueAsString(rowNum.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))))))
								.collect(Collectors.toList());

						if (!existingTrips.isEmpty()) {
							JobRecord firstTrip = existingTrips.get(0);
							if (this.isMatchingEndLocation(firstTrip, String.valueOf(rowNum.getCell(Integer.parseInt(initValue.get(INIT_FROM_LOCATION)))))){
								error.put(rowNumAndField, String.format(TRIP_LOC_ERROR_MSG, toLoc, firstTrip.getRowId() + 1, jobRecordTempateItem.getLabel()));
							}
						}
					}
				}
				break;
			case COUNTRY:
			case CARGO_TYPE_CLASQUIN:
				String value = ExcelPOIUtil.getCellValueAsString(cell);
				if (StringUtils.isBlank(value)){
					error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
				}
				break;
			case CARGO_TRUCK_TYPE_CLASQUIN:
			case CARGO_TRUCK_TYPE:
				String truckType = ExcelPOIUtil.getCellValueAsString(cell);
				if (StringUtils.isBlank(truckType)) {
					error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
				}else {
					TCkCtMstVehType ckCtMstVehType = this.getVehType(truckType);
					if(ckCtMstVehType == null){
						error.put(rowNumAndField, String.format(NOT_EXIST, jobRecordTempateItem.getLabel(), truckType));
					}
					if (jobRecordList.size() > 0) {
						List<JobRecord> existingTrips = jobRecordList.stream()
								.filter(val -> val.getShipmentRefNo().trim().equalsIgnoreCase(ExcelPOIUtil.getCellValueAsString(rowNum.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))))))
								.collect(Collectors.toList());

						if (!existingTrips.isEmpty()) {
							JobRecord firstTrip = existingTrips.get(0);
							String invRef = String.valueOf(header.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))).getStringCellValue());
							if (!this.isMatchingTruckType(firstTrip, truckType)){
								error.put(rowNumAndField, String.format(TRIP_ERROR_MSG, truckType, firstTrip.getRowId() + 1,invRef));
							}
						}
					}
//					else {
//						boolean vehCompanyValid = this.isVehCompanyValid(ckCtMstVehType.getVhtyId(), currentUserLogin);
//						if (!vehCompanyValid){
//							error.put(rowNumAndField, String.format(INVALID_TRUCK_TYPE, truckType));
//						}
//					}
				}
				break;
			case JOB_DATE:
			case PLAN_DATE:
			case BOOKING_DATE:
				if (cell == null) {
					error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
				} else {
					if (cell.getCellType() == CellType.NUMERIC) {
						Date dateValue = cell.getDateCellValue();
						if (dateValue == null) {
							error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
						}
					} else if (StringUtils.isBlank(cell.toString())) {
						error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
					} else {
						if (jobRecordList.size() > 0) {
							List<JobRecord> existingTrips = jobRecordList.stream()
									.filter(val -> val.getShipmentRefNo().trim().equalsIgnoreCase(String.valueOf(rowNum.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))))))
									.collect(Collectors.toList());

							if (!existingTrips.isEmpty()) {
								JobRecord firstTrip = existingTrips.get(0);
								String invRef = String.valueOf(header.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))).getStringCellValue());
								if (!isMatchingJobDate(firstTrip, cell.toString())){
									error.put(rowNumAndField, String.format(TRIP_ERROR_MSG, cell.toString(), firstTrip.getRowId() + 1, invRef));
								}
							}
						}
					}
				}
				break;
			case DATE_OF_DELIVERY:
				if (cell == null) {
					error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
				} else {
					if (cell.getCellType() == CellType.NUMERIC && cell.getDateCellValue() == null) {
						error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
					} else if (StringUtils.isBlank(cell.toString())) {
						error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
					} else {
						processCellValidationTo(cell, jobRecordList, initValue, rowNum, header, error, rowNumAndField, "DATE_OF_DELIVERY");
					}
				}
				break;
			case TO_LOCATION_DATE_TIME:
				if (cell == null) {
					error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
				} else {
					if (cell.getCellType() == CellType.NUMERIC && cell.getDateCellValue() == null) {
						error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
					} else if (StringUtils.isBlank(cell.toString())) {
						error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
					} else {
						processCellValidationTo(cell, jobRecordList, initValue, rowNum, header, error, rowNumAndField, "TO_LOCATION_DATE_TIME");
					}
				}
				break;
			case FROM_LOCATION_DATE_TIME:
				if (cell == null) {
					error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
				} else {
					if (cell.getCellType() == CellType.NUMERIC && cell.getDateCellValue() == null) {
						error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
					} else if (StringUtils.isBlank(cell.toString())) {
						error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
					}
					if (jobRecordList.size() > 0) {
						List<JobRecord> existingTrips = jobRecordList.stream()
								.filter(val -> val.getShipmentRefNo().trim().equalsIgnoreCase(ExcelPOIUtil.getCellValueAsString(rowNum.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))))))
								.collect(Collectors.toList());

						if (!existingTrips.isEmpty()) {
							JobRecord firstTrip = existingTrips.get(0);
							String invRef = String.valueOf(header.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))).getStringCellValue());
							if (!isMatchingJobFromDateTime(firstTrip, cell.toString())) {
								error.put(rowNumAndField, String.format(TRIP_ERROR_MSG, cell.toString(), firstTrip.getRowId() + 1, invRef));
							}
						}
					}
				}
				break;
			case CASES_QTY:
				if (cell != null) {
					double qty = Double.parseDouble(ExcelPOIUtil.getCellValueAsString(cell));
					if (qty <= 0) {
						error.put(rowNumAndField, String.format(GREATER_VALUE, jobRecordTempateItem.getLabel()));
					}
				} else {
					error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
				}
				break;
			case CARGO_OWNER_CSP:
				String coId = ExcelPOIUtil.getCellValueAsString(cell);
				if(coId != null) {
					TCoreAccn coAccn = this.getExistingCompanyAccount(coId);
					if (coAccn == null) {
						error.put(rowNumAndField, String.format(NOT_EXIST, jobRecordTempateItem.getLabel(), coId));
					}
				}
				break;
			case DATE_OF_DELIVERY_CSP:
				if (cell == null) {
					error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
				} else {
					if (cell.getCellType() == CellType.NUMERIC && cell.getDateCellValue() == null) {
						error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
					} else if (StringUtils.isBlank(cell.toString())) {
						error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
					} else if (!isDateValid(convert2DateTime(cell.getDateCellValue().toString(), "dd/MM/yyyy HH:mm:ss"), null)) {
						error.put(rowNumAndField, String.format(INVALID_DATE_TIME, jobRecordTempateItem.getLabel()));
					}
				}
				break;
			case PLAN_DATE_CSP:
				if (cell == null) {
					error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
				} else {
					if (cell.getCellType() == CellType.NUMERIC && cell.getDateCellValue() == null) {
						error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
					} else if (StringUtils.isBlank(cell.toString())) {
						error.put(rowNumAndField, String.format(REQUIRED, jobRecordTempateItem.getLabel()));
					} else if (!isDateValid(convert2DateTime(cell.getDateCellValue().toString(), "dd/MM/yyyy HH:mm:ss"), "dd/MM/yyyy")) {
						error.put(rowNumAndField, String.format(INVALID_DATE, jobRecordTempateItem.getLabel()));
					}
				}
				break;
			default:
				break;
		}
	}

	private void validateExtCell(
			String cellField,
			String cellValue,
			LinkedHashMap<String, String> error,
			Row rowNum,
			JobRecordTempateItem jobRecordTempateItem,
			List<JobRecord> jobRecordList,
			LinkedHashMap<String, String> initValue,
			Row header,
			int visibleIndex
	) throws Exception {

		String rowNumAndField = rowNum.getRowNum() + 1 + ":" + visibleIndex + ":" + jobRecordTempateItem.getLabel();
		switch (cellField){
			case "Job Type":
			case "Customer Name":
				if (jobRecordList.size() > 0) {
					List<JobRecord> existingTrips = jobRecordList.stream()
							.filter(val -> val.getShipmentRefNo().trim().equalsIgnoreCase(ExcelPOIUtil.getCellValueAsString(rowNum.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))))))
							.collect(Collectors.toList());

					if (!existingTrips.isEmpty()) {
						JobRecord firstTrip = existingTrips.get(0);
						String invRef = String.valueOf(header.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))).getStringCellValue());
						if(!this.isMatchingExtField(firstTrip, cellValue, cellField)) {
							error.put(rowNumAndField, String.format(TRIP_ERROR_MSG, cellValue, firstTrip.getRowId() + 1, invRef));
						}
					}
				}
				break;
			case "Deliver To":
			case "Contact Number":
			case "Contact Person":
			case "Postal Code":
			case "End Consignee Name":
				if (jobRecordList.size() > 0) {
					List<JobRecord> existingTrips = jobRecordList.stream()
							.filter(val -> val.getShipmentRefNo().trim().equalsIgnoreCase(ExcelPOIUtil.getCellValueAsString(rowNum.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))))))
							.collect(Collectors.toList());

					if (!existingTrips.isEmpty()) {
						JobRecord firstTrip = existingTrips.get(0);
						String invRef = String.valueOf(header.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))).getStringCellValue());
						String toField = String.valueOf(header.getCell(Integer.parseInt(initValue.get(INIT_TO_LOCATION))).getStringCellValue());
						this.isNotMatchingExtFieldWithEndLoc(
								firstTrip,
								cellValue,
								cellField,
								String.valueOf(rowNum.getCell(Integer.parseInt(initValue.get(INIT_FROM_LOCATION)))),
								String.valueOf(rowNum.getCell(Integer.parseInt(initValue.get(INIT_TO_LOCATION)))),
								existingTrips,
								invRef,
								toField,
								rowNumAndField,
								error
						);
					}
				}
				break;
			default:
				break;
		}

	}

	private JobRecord convertDefaultExcel(List<JobRecordTempateItem> titleTemplate, Row row,
												  LinkedHashMap<String, String> error, List<JobRecord> jobRecordList,
												  Row header) throws Exception {

		CoreAccn currentUserLogin = principalUtilService.getPrincipal().getCoreAccn();
		JobRecord jobRecord = new JobRecord();
		LinkedHashMap<String, String> initValue = new LinkedHashMap<>();
		int visibleIndex = 0;

		for (JobRecordTempateItem jti : titleTemplate) {
			Cell cell = null;
			JobRecordFieldEnum field = null;

			if (JobRecordFieldEnum.isValidEnum(jti.getField()) != null) {
				field = JobRecordFieldEnum.valueOf(jti.getField().toUpperCase());
			}

			if (jti.getHidden() != null && jti.getHidden().equals("Y")) {
				if (jti.getType() != null && jti.getType().equals("init")) {
					initValue.put(jti.getField(), jti.getDefault());
				} else {
					field = JobRecordFieldEnum.valueOf(jti.getField().toUpperCase());
				}
			} else {
				cell = row.getCell(visibleIndex);
				visibleIndex++;
			}

			if (field != null) {
				validateCell(cell, field, error, row, jti, currentUserLogin, jobRecordList, initValue, header, visibleIndex);
				switch (field) {
					case CONTRACT_ID:
						jobRecord.setContractId(this.getCellValueForString(cell, jti.getDefault()));
						break;
					case JOB_SUB_TYPE:
						jobRecord.setJobSubType(this.getCellValueForString(cell, jti.getDefault()));
					case SHIPMENT_REF:
						jobRecord.setShipmentRefNo(this.getCellValueForString(cell, jti.getDefault()));
						break;
					case CUSTOMER_REF:
						jobRecord.setJobCustomerRef(this.getCellValueForString(cell, jti.getDefault()));
						break;
					case LOADING:
						jobRecord.setLoading(this.getCellValueForString(cell, jti.getDefault()));
						break;
					case BOOKING_DATE:
						jobRecord.setBookingDate(this.getCellValueForDate(cell, jti.getDefault()));
						break;
					case PLAN_DATE:
						jobRecord.setPlanDate(this.getCellValueForDate(cell, jti.getDefault()));
						break;
					case CARGO_TRUCK_TYPE:
						jobRecord.setCargoTruckType(this.getCellValueForString(cell, jti.getDefault()));
						break;
					case FROM_LOCATION_NAME:
						jobRecord.setStartLoc(this.getCellValueForString(cell, jti.getDefault()));
						break;
					case FROM_LOCATION_DETAILS:
						jobRecord.setStartLocAddress(this.getCellValueForString(cell, jti.getDefault()));
						break;
					case FROM_LOCATION_DATE_TIME:
						jobRecord.setFromLocDateTime(convert2DateTime(this.getCellValueForString(cell, jti.getDefault()), "dd/MM/yyyy HH:mm:ss"));
						break;
					case FROM_LOCATION_MOBILE_NUMBER:
						jobRecord.setFromLocMobileNumber(this.getCellValueForString(cell, jti.getDefault()));
						break;
					case FROM_LOCATION_REMARKS:
						jobRecord.setFromLocRemarks(this.getCellValueForString(cell, jti.getDefault()));
						break;
					case TO_LOCATION_NAME:
						jobRecord.setEndLoc(this.getCellValueForString(cell, jti.getDefault()));
						break;
					case TO_LOCATION_DETAILS:
						jobRecord.setEndLocAddress(this.getCellValueForString(cell, jti.getDefault()));
						break;
					case TO_LOCATION_DATE_TIME:
						jobRecord.setToLocDateTime(convert2DateTime(this.getCellValueForString(cell, jti.getDefault()), "dd/MM/yyyy HH:mm:ss"));
						break;
					case TO_LOCATION_RECIPIENT_NAME:
						jobRecord.setToLocCargoRec(this.getCellValueForString(cell, jti.getDefault()));
						break;
					case TO_LOCATION_MOBILE_NUMBER:
						jobRecord.setToLocMobileNumber(this.getCellValueForString(cell, jti.getDefault()));
						break;
					case TO_LOCATION_REMARKS:
						jobRecord.setToLocRemark(this.getCellValueForString(cell, jti.getDefault()));
						break;
					case CARGO_TYPE:
						jobRecord.setCargoType(this.getCellValueForString(cell, jti.getDefault()));
						break;
					case CARGO_QTY:
						jobRecord.setCgCargoQty(parseDoubleOrNull(this.getCellValueForString(cell, jti.getDefault())));
						break;
					case CARGO_QTY_UOM:
						jobRecord.setCargoQtyUom(this.getCellValueForString(cell, jti.getDefault()));
						break;
					case CARGO_MAKES_NO:
						jobRecord.setCgCargoMarksNo(this.getCellValueForString(cell, jti.getDefault()));
						break;
					case CARGO_LENGTH:
						jobRecord.setCgCargoLength(parseDoubleOrNull(this.getCellValueForString(cell, jti.getDefault())));
						break;
					case CARGO_WIDTH:
						jobRecord.setCgCargoWidth(parseDoubleOrNull(this.getCellValueForString(cell, jti.getDefault())));
						break;
					case CARGO_HEIGHT:
						jobRecord.setCgCargoHeight(parseDoubleOrNull(this.getCellValueForString(cell, jti.getDefault())));
						break;
					case CARGO_SIZE_UOM:
						jobRecord.setCgCargoSizeUom(getCellValueForString(cell, jti.getDefault()));
						break;
					case CARGO_WEIGHT:
						jobRecord.setCgCargoWeight(parseDoubleOrNull(this.getCellValueForString(cell, jti.getDefault())));
						break;
					case CARGO_WEIGHT_UOM:
						jobRecord.setCgCargoWeightUom(this.getCellValueForString(cell, jti.getDefault()));
						break;
					case CARGO_VOLUME:
						jobRecord.setCgCargoVolume(parseDoubleOrNull(this.getCellValueForString(cell, jti.getDefault())));
						break;
					case CARGO_VOLUME_UOM:
						jobRecord.setCgCargoVolumeUom(getCellValueForString(cell, jti.getDefault()));
						break;
					case DESCRIPTION_DEFAULT:
						jobRecord.setDescription(this.getCellValueForString(cell, jti.getDefault()));
						break;
					case SPECIAL_INSTRUCTION:
						jobRecord.setCgCargoSpecialInstn(this.getCellValueForString(cell, jti.getDefault()));
						break;
					default:
						break;
				}
			}
		}
		return jobRecord;
	}

	/**
	 * Parse excel first row to Map<rowId, cellValue>
	 *
	 * @param row
	 * @return
	 * @throws Exception
	 */
	private Map<Integer, String> parseExcelTitleRowForExtendSheet(Row row, boolean isReplace) throws Exception {

		Map<Integer, String> titleMap = new HashMap<>();

		for (int i = 0; i < row.getLastCellNum(); i++) {
			Cell cell = row.getCell(i);
			if (cell != null) {
				switch (cell.getCellType()) {
					case STRING:
						// First line should be string
						String cellValue = cell.getStringCellValue();
						if(cellValue != null) {
							if(isReplace) {
								// remove all special and blank characters, change to upper case also
								cellValue = cellValue.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
							}
							if (StringUtils.isNoneBlank(cellValue)) {
								titleMap.put(i, cellValue);
							}
						}
						break;
					default:
						break;
				}
			}
		}
		return titleMap;
	}

	private Map<Integer, String> parseExcelTitleRowForExtendSheetWithHiddenCol(Row row, List<JobRecordTempateItem> recordTempate, boolean isReplace) throws Exception {

		Map<Integer, String> titleMap = new HashMap<>();
		for (int i = 0; i < recordTempate.size(); i++) {
			JobRecordTempateItem cellLabel = recordTempate.get(i);
			if (cellLabel != null) {
				String cellTitle = cellLabel.getLabel();
				if(isReplace) {
					cellTitle = cellLabel.getLabel().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
				}
				if (StringUtils.isNoneBlank(cellTitle)) {
					titleMap.put(i, cellTitle);
				}
			}
		}
		return titleMap;
	}

	private Map<String, Object> convertRow2Map(Map<Integer, String> titleMap, Row row) throws ParseException {

		Map<String, Object> extendAttrs = new HashMap<>();

		for (int i = 0; i < row.getLastCellNum(); i++) {
			Object cellValue = ExcelPOIUtil.getCellValueAsStringOrDate(row.getCell(i));
			if (StringUtils.isNoneBlank(ObjectUtil.objToStr(cellValue)) && StringUtils.isNotBlank(titleMap.get(i))) {
				extendAttrs.put(titleMap.get(i), cellValue);
			}

		}
		return extendAttrs;
	}

	@SuppressWarnings("unused")
	private Map<String, String> convertRow2Map(Map<Integer, String> titleMap, Row row,
											   List<JobRecordTempateItem> templateItemList) throws ParseException {

		Map<String, String> extendAttrs = new HashMap<>();

		for (int i = 0; i < row.getLastCellNum(); i++) {
			String cellValue = ExcelPOIUtil.getCellValueAsString(row.getCell(i));
			if (StringUtils.isNoneBlank(cellValue) && StringUtils.isNotBlank(titleMap.get(i))) {
				extendAttrs.put(titleMap.get(i), cellValue);
			}

		}
		return extendAttrs;
	}

	private JobRecord setupSepcialAttribute(JobRecord jobRecord, List<JobRecordTempateItem> templateItemList) {

		String diverNameLabel = templateItemList.stream()
				.filter(ti -> JobRecordFieldEnum.DRIVER_USERNAME.getCode().equalsIgnoreCase(ti.getField()))
				.map(ti -> ti.getLabel()).findFirst().orElse(null);

		String truckPlateNoLabel = templateItemList.stream()
				.filter(ti -> JobRecordFieldEnum.TRUCK_LICENSE_PLATE_NUMBER.getCode().equalsIgnoreCase(ti.getField()))
				.map(ti -> ti.getLabel()).findFirst().orElse(null);

		String jobLinkingNumberLabel = templateItemList.stream()
				.filter(item -> JobRecordFieldEnum.JOB_LINKING_NUMBER.getCode().equalsIgnoreCase(item.getField()))
				.findFirst().map(item -> item.getLabel()).orElse(null);

		//
		if (jobRecord != null && jobRecord.getExtendAttrs() != null) {
			if (StringUtils.isNoneBlank(diverNameLabel)
					&& StringUtils.isNoneBlank(ObjectUtil.objToStr(jobRecord.getExtendAttrs().get(diverNameLabel)))) {
				jobRecord.setDriverName(ObjectUtil.objToStr(jobRecord.getExtendAttrs().get(diverNameLabel)));
			}
		}
		if (jobRecord != null && jobRecord.getExtendAttrs() != null) {
			if (StringUtils.isNoneBlank(truckPlateNoLabel)
					&& StringUtils.isNoneBlank(ObjectUtil.objToStr(jobRecord.getExtendAttrs().get(truckPlateNoLabel)))) {
				jobRecord.setTruckPlateNo(ObjectUtil.objToStr(jobRecord.getExtendAttrs().get(truckPlateNoLabel)));
			}
		}
		if (jobRecord != null && jobRecord.getExtendAttrs() != null) {
			if (StringUtils.isNoneBlank(jobLinkingNumberLabel)
					&& StringUtils.isNoneBlank(ObjectUtil.objToStr(jobRecord.getExtendAttrs().get(jobLinkingNumberLabel)))) {
				jobRecord.setLinkingNumber(ObjectUtil.objToStr(jobRecord.getExtendAttrs().get(jobLinkingNumberLabel)));
			}
		}
		return null;
	}

	private void setupMulitiDrop(List<JobRecord> jobRecordList) {

		jobRecordList.sort((a, b) -> a.getRowId() - b.getRowId());

		for (int i = 0; i < jobRecordList.size(); i++) {

			JobRecord jobRecord = jobRecordList.get(i);
			String linkingNumber = jobRecord.getLinkingNumber();

			if (StringUtils.isNotBlank(linkingNumber) && (jobRecord.getParentJobRecord() == null)) {
				// sub records
				for (int j = i + 1; j < jobRecordList.size(); j++) {

					JobRecord subJobRecord = jobRecordList.get(j);

					if (linkingNumber.equalsIgnoreCase(subJobRecord.getLinkingNumber())) {
						// same number;
						subJobRecord.setParentJobRecord(jobRecord);
						jobRecord.getMultiDrops().add(subJobRecord);
					}
				}
			}
		}
	}

	private void validateDateOfDeliverWhenMigration(List<JobRecord> jobRecordList, Map<String, String> failLinesMaps) throws Exception {

		TCoreSysparam checkDeliveryDateParam = coreSysparamDao
				.find("CLICKTRUCK_DSV_EXCEL_UPLOAD_CHECK_DELIVERY_DATE");

		if (checkDeliveryDateParam == null || !"Y".equalsIgnoreCase(checkDeliveryDateParam.getSysVal())) {
			return ;
		}
		
		jobRecordList.sort((a, b) -> a.getRowId() - b.getRowId());

		Date now = new CkDateUtil().clearHourMintueSecond(null);

		for (JobRecord jobRecord : jobRecordList) {
			
			if (now.compareTo(jobRecord.getDateOfDelivery()) > 0) {

				failLinesMaps.put(String.valueOf(jobRecord.getRowId()), "Deliver date " + jobRecord.getDateOfDelivery() +  " is less than today");
			}
			if (StringUtils.isBlank(jobRecord.getShipmentRefNo())) {

				failLinesMaps.put(String.valueOf(jobRecord.getRowId()), "Shipment Ref is required!");
			}
		}
	}

	private void validateSameStartAndEndLocationForMultiDrop(List<JobRecord> jobRecordList,
															 LinkedHashMap<String, String> failLinesMaps) {

		jobRecordList.sort((a, b) -> a.getRowId() - b.getRowId());

		for (JobRecord jobRecord : jobRecordList) {

			if (jobRecord.getMultiDrops() != null && jobRecord.getMultiDrops().size() > 0) {
				// sub records
				List<JobRecord> multiDrops = jobRecord.getMultiDrops();

				for (JobRecord subJob : multiDrops) {
					if (this.sameLocation(jobRecord, subJob)) {
						String errorMsg = "Row " + jobRecord.getRowId() + " with row " + subJob.getRowId()
								+ " are same start and end location.";
						failLinesMaps.put(String.valueOf(jobRecord.getRowId()+1), errorMsg);
						multiDrops.forEach(subJobRecord -> failLinesMaps.put(String.valueOf(subJobRecord.getRowId()+1), errorMsg));
						break;
					}
				}

				for (int i = 0; i < multiDrops.size(); i++) {
					JobRecord jr = multiDrops.get(i);
					for (int j = i + 1; j < multiDrops.size(); j++) {
						JobRecord subJob = multiDrops.get(j);

						if (this.sameLocation(jr, subJob)) {
							String errorMsg = "Row " + jr.getRowId() + " with row " + subJob.getRowId()
									+ " are same start and end location.";
							failLinesMaps.put(String.valueOf(jobRecord.getRowId()+1), errorMsg);
							multiDrops.forEach(subJobRecord -> failLinesMaps.put(String.valueOf(subJobRecord.getRowId()+1), errorMsg));
							break;
						}
					}
				}
			}
		}
	}

	private void validateShouldSameStartLocationForMultiDrop(List<JobRecord> jobRecordList,
															 Map<String, String> failLinesMaps) {

		jobRecordList.sort((a, b) -> a.getRowId() - b.getRowId());

		for (JobRecord jobRecord : jobRecordList) {

			if (jobRecord.getMultiDrops() != null && jobRecord.getMultiDrops().size() > 0) {
				// sub records
				List<JobRecord> multiDrops = jobRecord.getMultiDrops();

				for (JobRecord subJob : multiDrops) {
					if ( ! jobRecord.getStartLoc().equalsIgnoreCase(subJob.getStartLoc() )) {
						String errorMsg = "Row " + jobRecord.getRowId() + " with row " + subJob.getRowId()
								+ " are not same start location.";
						failLinesMaps.put(String.valueOf(jobRecord.getRowId()), errorMsg);
						multiDrops.forEach(subJobRecord -> failLinesMaps.put(String.valueOf(subJobRecord.getRowId()), errorMsg));
						break;
					}
				}
				/*-
				for (int i = 0; i < multiDrops.size(); i++) {
					JobRecord jr = multiDrops.get(i);
					for (int j = i + 1; j < multiDrops.size(); j++) {
						JobRecord subJob = multiDrops.get(j);

						if (jobRecord.getEndLoc().equalsIgnoreCase(subJob.getEndLoc() )) {
							String errorMsg = "Row " + jr.getRowId() + " with row " + subJob.getRowId()
									+ " are same end location.";
							failLinesMaps.put(jobRecord.getRowId(), errorMsg);
							multiDrops.forEach(subJobRecord -> failLinesMaps.put(subJobRecord.getRowId(), errorMsg));
							break;
						}
					}
				}
				*/
			}
		}
	}
	
	private boolean sameLocation(JobRecord jr1, JobRecord jr2) {

		if (StringUtils.isNotBlank(jr1.getStartLoc()) && StringUtils.isNotBlank(jr1.getEndLoc())
				&& StringUtils.isNotBlank(jr2.getStartLoc()) && StringUtils.isNotBlank(jr2.getEndLoc())) {
			// same start and end location;
			return jr1.getStartLoc().equalsIgnoreCase(jr2.getStartLoc())
					&& jr1.getEndLoc().equalsIgnoreCase(jr2.getEndLoc());

		}
		return false;
	}

	private String getCellValueForString(Cell cell, String defaultVal) {
		if (null == cell) {
			return defaultVal;
		}

		String cellVal = ExcelPOIUtil.getCellValueAsString(cell);
		if (StringUtils.isNotBlank(cellVal)) {
			return cellVal;
		}
		return defaultVal;
	}
	private Date getCellValueForDate(Cell cell, String defaultVal) throws ParseException {
		// Define date formats for parsing and output
		SimpleDateFormat dd_MM_yyyySDF = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat yyyy_MM_ddSDF = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy"); // Default format

		Date date = null;

		if (cell != null) {
			if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
				date = cell.getDateCellValue();
			} else if (cell.getCellType() == CellType.STRING) {
				String cellValue = cell.getStringCellValue().trim();
				date = parseDateString(cellValue, dd_MM_yyyySDF, yyyy_MM_ddSDF);
			}
		}
		if (date == null && defaultVal != null) {
			date = parseDateString(defaultVal, dd_MM_yyyySDF, yyyy_MM_ddSDF);
		}
		if (date != null) {
			System.out.println("Formatted Date: " + outputFormat.format(date));
		} else {
			System.out.println("Date parsing failed. Default value could not be parsed.");
		}

		return date;
	}

	private Date getCellValueForDateTime(Cell cell, String defaultVal) throws ParseException {
		// Input format in SGT (UTC+8)
		SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		inputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

		Date date = null;

		if (cell != null) {
			switch (cell.getCellType()) {
				case NUMERIC:
					if (DateUtil.isCellDateFormatted(cell)) {
						// Directly convert numeric Excel date to UTC
						date = convertToUTCDate(cell.getDateCellValue());
					}
					break;

				case STRING:
					String cellValue = cell.getStringCellValue().trim();
					date = parseDateString(cellValue, inputFormat);
					break;

				default:
					break;
			}
		}

		// Use default value if no valid date is found
		if (date == null && defaultVal != null && !defaultVal.trim().isEmpty()) {
			date = parseDateString(defaultVal, inputFormat);
		}

		return date;
	}

	/**
	 * Converts a Date to UTC timezone.
	 */
	private Date convertToUTCDate(Date date) throws ParseException {
		SimpleDateFormat utcFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String utcDateString = utcFormat.format(date);
		return utcFormat.parse(utcDateString);
	}

	private Date parseDateString(String dateString, SimpleDateFormat... formats) throws ParseException {
		for (SimpleDateFormat format : formats) {
			try {
				return format.parse(dateString);
			} catch (ParseException e) {
				// Log the failed format for debugging
				System.out.println("Failed to parse date with format: " + format.toPattern());
			}
		}
		return null;
	}
	private Date getCellValueForTime(Cell cell, String defaultVal) throws ParseException {
		if (null != cell && cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)
				&& cell.getDateCellValue() != null) {
			return cell.getDateCellValue();
		}
		Date date = null;

		try {
			if (null != cell && (cell.getCellType() == CellType.STRING || cell.getCellType() == CellType.NUMERIC)) {
				date = HH_mm_ss.parse(cell.getStringCellValue());
			} else {
				date = HH_mm_ss.parse(defaultVal);
			}
		} catch (Exception ignored) {

		}
		return date;
	}

	private String storeExcelFile(String accnId, byte[] excelData, String fileName) throws IOException {

		//
		String newFileName = this.getLocalJobUploadPath() + File.separator + accnId + "_" + System.nanoTime() + "_"
				+ fileName;

		FileUtils.writeByteArrayToFile(new File(newFileName), excelData);
		return newFileName;
	}

	private String getLocalJobUploadPath() throws IOException {

		String rootPath = ckCtCommonService.getCkCtAttachmentPathRoot();

		String parentPath = rootPath + File.separator + "JobUpload" + File.separator + yyyyMMddSDF.format(new Date());

		Files.createDirectories(Paths.get(parentPath));

		return parentPath;
	}

	public TCkCtContract getExistingContract(String contractName) throws Exception {
		return ckCtContractDao.findByName(contractName).orElse(null);
	}

	public TCoreAccn getExistingCompanyAccount(String co) throws Exception {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("accn", co);
		parameters.put("accnStatus",RecordStatus.ACTIVE.getCode());
		String hql = "FROM TCoreAccn o WHERE (o.accnId = :accn OR o.accnName = :accn OR o.accnNameOth = :accn) AND o.accnStatus = :accnStatus ";
		List<TCoreAccn> entity = coreAccnDao.getByQuery(hql, parameters);
		return (entity != null && !entity.isEmpty()) ? entity.get(0) : null;
	}

	public TCkCtContract getContractByFF(String FFAccnId, String TOAccnId) throws Exception {
		String hql = "FROM TCkCtContract o where o.TCoreAccnByConCoFf.accnId=:ffAccnId AND o.TCoreAccnByConTo.accnId=:toAccnId AND conStatus=:status";
		Map<String, Object> params = new HashMap<>();
		params.put("ffAccnId", FFAccnId);
		params.put("toAccnId", TOAccnId);
		params.put("status", RecordStatus.ACTIVE.getCode());
		List<TCkCtContract> ckCtContracts = ckCtContractDao.getByQuery(hql,params);
		if (ckCtContracts != null && !ckCtContracts.isEmpty()){
			return ckCtContracts.get(0);
		}
		return null;
	}

	private TCkCtContract getContractByFFOnly(String FFAccnId) throws Exception {
		String hql = "FROM TCkCtContract o where o.TCoreAccnByConCoFf.accnId=:ffAccnId AND conStatus=:status";
		Map<String, Object> params = new HashMap<>();
		params.put("ffAccnId", FFAccnId);
		params.put("status", RecordStatus.ACTIVE.getCode());
		List<TCkCtContract> ckCtContracts = ckCtContractDao.getByQuery(hql,params);
		if (ckCtContracts != null && !ckCtContracts.isEmpty()){
			return ckCtContracts.get(0);
		}
		return null;
	}

	public TCkCtMstVehType getVehType(String vhtyIdOrName) throws Exception {

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("vhtyIdOrName", vhtyIdOrName);
		parameters.put("vhtyStatus",RecordStatus.ACTIVE.getCode());
		String hql = "FROM TCkCtMstVehType o WHERE (o.vhtyId = :vhtyIdOrName OR o.vhtyName = :vhtyIdOrName OR o.vhtyDesc = :vhtyIdOrName OR o.vhtyDescOth = :vhtyIdOrName) AND o.vhtyStatus = :vhtyStatus ";

		List<TCkCtMstVehType> vehType = ckCtMstVehTypeDao.getByQuery(hql,parameters);
		if(vehType != null && vehType.size() > 0) {
			return vehType.get(0);
		}
		return null;
	}

	private boolean isVehCompanyValid(String vehTypeId, CoreAccn currentUserLogin) throws Exception {

		ArrayList<String> vehTypeList = new ArrayList<>();
		String hql = "FROM TCkCtVeh o where TCoreAccn.accnId = :companyId and vhStatus = :status";
		TCkCtContract contract = this.getContractByFFOnly(currentUserLogin.getAccnId());
		Map<String, Object> params = new HashMap<>();
		String toId = "";
		if (contract != null && contract.getTCoreAccnByConTo().getAccnId() != null){
			toId = contract.getTCoreAccnByConTo().getAccnId();
		}
		params.put("companyId", toId);
		params.put("status", RecordStatus.ACTIVE.getCode());
		List<TCkCtVeh> companyVehList = ckCtVehDao.getByQuery(hql,params);
		if (companyVehList == null || companyVehList.isEmpty()){
			return false;
		}
		companyVehList.stream()
				.map(v -> v.getTCkCtMstVehType().getVhtyId())
				.filter(vhtyId -> !vhtyId.isEmpty())
				.forEach(vehTypeList::add);
		return vehTypeList.stream().anyMatch(v -> v.equalsIgnoreCase(vehTypeId));
	}

	public String convert2DateTime(String dateTimeStr, String pattern) throws Exception {
		TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");

		SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		inputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

		SimpleDateFormat inputFormat24Hour = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		inputFormat24Hour.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

		SimpleDateFormat inputFormat24Hour2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		inputFormat24Hour2.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

		if (pattern == null){
			pattern = "dd/MM/yyyy";
		}
		SimpleDateFormat outputFormat = new SimpleDateFormat(pattern);
		outputFormat.setTimeZone(utcTimeZone);

		if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
			return null;
		}

		try {
			// Parse the input string into a Date object
			Date parsedDate = inputFormat.parse(dateTimeStr);

			// Format the Date object into the desired output format
			return outputFormat.format(parsedDate);
		} catch (ParseException e) {
			try {
				// Parse the input string into a Date object
				Date parsedDate = inputFormat24Hour.parse(dateTimeStr);

				// Format the Date object into the desired output format
				return outputFormat.format(parsedDate);
			} catch (ParseException e1) {
				try {
					// Parse the input string into a Date object
					Date parsedDate = inputFormat24Hour2.parse(dateTimeStr);

					// Format the Date object into the desired output format
					return outputFormat.format(parsedDate);
				} catch (ParseException e2) {
					log.error(String.format("Fail to parse %s to date", dateTimeStr));
				}
			}
		}
		return null;
	}

	public static double convertCmToMeter(double centimeters) {
		if (centimeters < 0) {
			throw new IllegalArgumentException("Centimeter value cannot be negative.");
		}
		return centimeters / 100.0; // 1 meter = 100 centimeters
	}

	public static boolean isDateValid(String date, String pattern) {
		if (pattern == null) {
			pattern = "dd/MM/yyyy HH:mm:ss";
		}
		String dateStr = date;
		DateFormat df = new SimpleDateFormat(pattern);
		df.setLenient(false);
		try {
			df.parse(dateStr);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	public boolean isMatchingJobDate(JobRecord existTrip, String fromLoc) {
		// Parse `fromLoc` into a LocalDate
		LocalDate inputDate = parseDate(fromLoc);
		if (inputDate == null) {
			return false; // Return false if the input date is invalid
		}

		// Convert existing trip dates to LocalDate using the appropriate time zone
		LocalDate planDate = convertToLocalDate(existTrip.getPlanDate(), ZoneId.of("Asia/Singapore"));
		LocalDate bookingDate = convertToLocalDate(existTrip.getBookingDate(), ZoneId.of("Asia/Singapore"));

		// Compare the dates
		return inputDate.isEqual(planDate) || inputDate.isEqual(bookingDate);
	}

	public boolean isMatchingJobFromDateTime(JobRecord existTrip, String fromLocDateTime) throws Exception {
		log.info("->> isMatchingJobFromDateTime val1:"+ existTrip.getFromLocDateTime()+ ", val2: "+ fromLocDateTime+":"+JobUploadUtilService.normalizeDate(fromLocDateTime, false));
		return isEqual(existTrip.getFromLocDateTime(), JobUploadUtilService.normalizeDate(fromLocDateTime, false));
	}

	public boolean isMatchingStartLocation(JobRecord existTrip, String fromLoc) {
		return isEqual(existTrip.getStartLoc(), fromLoc) ||
				isEqual(existTrip.getStartLocAddress(), fromLoc);
	}

	public boolean isMatchingEndLocation(JobRecord existTrip,String fromLoc) {
		return this.isMatchingStartLocation(existTrip, fromLoc) && isEqual(existTrip.getEndLoc(), fromLoc) ||
				isEqual(existTrip.getEndLocAddress(), fromLoc);
	}

	public boolean isMatchingContract(JobRecord existTrip, String contract) {
		return isEqual(existTrip.getContractId(), contract);
	}

	public boolean isMatchingCustomerRef(JobRecord existTrip, String ref) {
		return isEqual(existTrip.getJobCustomerRef(), ref);
	}

	public boolean isMatchingStartLocationMobilePhone(JobRecord existTrip, String mobile) {
		return isEqual(existTrip.getFromLocMobileNumber(),mobile);
	}

	public boolean isMatchingEndLocationMobilePhone(JobRecord existTrip, String mobile) {
		return isEqual(existTrip.getToLocMobileNumber(),mobile);
	}

	public boolean isMatchingStartLocationRemark(JobRecord existTrip, String locRemark) {
		return isEqual(existTrip.getFromLocRemarks(), locRemark);
	}

	public boolean isMatchingJobSubType(JobRecord existTrip, String subType) {
		return isEqual(existTrip.getJobSubType(), subType);
	}

	public boolean isMatchingLoading(JobRecord existTrip, String loading) {
		return isEqual(existTrip.getLoading(), loading);
	}

	public boolean isMatchingTruckType(JobRecord existTrip, String truckType) {
		return isEqual(existTrip.getCargoTruckType(), truckType);
	}

	public boolean isMatchingExtField(JobRecord existTrip,String cellValue, String cellField) {
		return isEqual(existTrip.getExtendAttrs().get(cellField).toString(), cellValue);
	}

	public void isNotMatchingExtFieldWithEndLoc(
			JobRecord existTrip,
			String cellValue,
			String cellField,
			String fromLoc,
			String endLoc,
			List<JobRecord> existingTrips,
			String invRef,
			String toField,
			String rowNumAndField,
			LinkedHashMap<String, String> error
	) {
		if (existingTrips.size() > 0) {
			List<JobRecord> toLocTrips = existingTrips.stream()
					.filter(val -> (isEqual(val.getEndLoc(), endLoc) || isEqual(val.getEndLocAddress(), endLoc)))
					.collect(Collectors.toList());

			if (!toLocTrips.isEmpty()) {
				JobRecord toLocTrip = toLocTrips.get(0);
				if (this.isMatchingStartLocation(existTrip, fromLoc) && !isEqual(toLocTrip.getExtendAttrs().get(cellField).toString(), cellValue)){
					error.put(rowNumAndField, String.format(CARGO_ERROR_MSG , cellValue, toLocTrip.getRowId() + 1, invRef, toField));
				}
			}
		}
	}

	private boolean isEqual(String str1, String str2) {
		return str1 != null && str2 != null && str1.trim().equals(str2.trim());
	}

	private LocalDate parseDate(String dateStr) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
			return LocalDate.parse(dateStr, formatter);
		} catch (Exception e) {
			System.err.println("Invalid date format: " + dateStr);
			return null;
		}
	}

	private LocalDate convertToLocalDate(Date date, ZoneId timeZone) {
		if (date == null) {
			return null;
		}
		return ZonedDateTime.ofInstant(date.toInstant(), timeZone).toLocalDate();
	}

	private Double parseDoubleOrNull(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		try {
			return Double.valueOf(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public void isMatchingToLOcDateTime(
			JobRecord existTrip,
			String fromLoc,
			String toLoc,
			String cellValue,
			List<JobRecord> existingTrips,
			String rowNumAndField,
			LinkedHashMap<String, String> error,
			String colTitle,
			String colTitle2
	) {

		if (existingTrips.size() > 0) {
			List<JobRecord> toLocTrips = existingTrips.stream()
					.filter(val -> (isEqual(val.getEndLoc(), toLoc) || isEqual(val.getEndLocAddress(), toLoc)))
					.collect(Collectors.toList());

			if (!toLocTrips.isEmpty()) {
				JobRecord toLocTrip = toLocTrips.get(0);
				if (this.isMatchingStartLocation(existTrip, fromLoc) && (!isEqual(toLocTrip.getToLocDateTime(), cellValue))){
					error.put(rowNumAndField, String.format(CARGO_ERROR_MSG , cellValue, toLocTrip.getRowId() + 1, colTitle, colTitle2));
				}
			}
		}
	}

	public void isMatchingToLOcDateTimeAndDetails(
			JobRecord existTrip,
			String fromLoc,
			String toLoc,
			String fromLocDetails,
			String toLocDetails,
			String cellValue,
			List<JobRecord> existingTrips,
			String rowNumAndField,
			LinkedHashMap<String, String> error,
			String colTitle,
			String colTitle2
	) throws Exception {

		if (existingTrips.size() > 0) {
			List<JobRecord> toLocTrips = existingTrips.stream()
					.filter(val -> (isEqual(val.getEndLoc(), toLoc) && isEqual(val.getEndLocAddress(), toLocDetails)))
					.collect(Collectors.toList());

			if (!toLocTrips.isEmpty()) {
				JobRecord toLocTrip = toLocTrips.get(0);
				log.info("->> isMatchingToLOcDateTimeAndDetails {} val1:"+ toLocTrip.getToLocDateTime()+ ", val2: "+ cellValue+":"+JobUploadUtilService.normalizeDate(cellValue,true));
				if (isEqual(existTrip.getStartLoc(), fromLoc) &&
					isEqual(existTrip.getStartLocAddress(), fromLocDetails) &&
						(!isEqual(toLocTrip.getToLocDateTime(), JobUploadUtilService.normalizeDate(cellValue,true)))
				){
					error.put(rowNumAndField, String.format(CARGO_ERROR_MSG , JobUploadUtilService.normalizeDate(cellValue,true), toLocTrip.getRowId() + 1, colTitle, colTitle2));
				}
			}
		}
	}

	public void isMatchingToLocMobileDetails(
			JobRecord existTrip,
			String fromLoc,
			String toLoc,
			String fromLocDetails,
			String toLocDetails,
			String cellValue,
			List<JobRecord> existingTrips,
			String rowNumAndField,
			LinkedHashMap<String, String> error,
			String colTitle,
			String colTitle2
	) {

		if (existingTrips.size() > 0) {
			List<JobRecord> toLocTrips = existingTrips.stream()
					.filter(val -> (isEqual(val.getEndLoc(), toLoc) && isEqual(val.getEndLocAddress(), toLocDetails)))
					.collect(Collectors.toList());

			if (!toLocTrips.isEmpty()) {
				JobRecord toLocTrip = toLocTrips.get(0);
				if (isEqual(existTrip.getStartLoc(), fromLoc) && isEqual(existTrip.getStartLocAddress(), fromLocDetails) && (!isEqual(toLocTrip.getToLocMobileNumber(), cellValue))){
					error.put(rowNumAndField, String.format(CARGO_ERROR_MSG , cellValue, toLocTrip.getRowId() + 1, colTitle, colTitle2));
				}
			}
		}
	}

	public void isMatchingToLocRemarkDetails(
			JobRecord existTrip,
			String fromLoc,
			String toLoc,
			String fromLocDetails,
			String toLocDetails,
			String cellValue,
			List<JobRecord> existingTrips,
			String rowNumAndField,
			LinkedHashMap<String, String> error,
			String colTitle,
			String colTitle2
	) {

		if (existingTrips.size() > 0) {
			List<JobRecord> toLocTrips = existingTrips.stream()
					.filter(val -> (isEqual(val.getEndLoc(), toLoc) && isEqual(val.getEndLocAddress(), toLocDetails)))
					.collect(Collectors.toList());

			if (!toLocTrips.isEmpty()) {
				JobRecord toLocTrip = toLocTrips.get(0);
				if (isEqual(existTrip.getStartLoc(), fromLoc) && isEqual(existTrip.getStartLocAddress(), fromLocDetails) && (!isEqual(toLocTrip.getToLocRemark(), cellValue))){
					error.put(rowNumAndField, String.format(CARGO_ERROR_MSG , cellValue, toLocTrip.getRowId() + 1, colTitle, colTitle2));
				}
			}
		}
	}

	public void isMatchingEndLocationRemark(
			JobRecord existTrip,
			String fromLoc,
			String toLoc,
			String toLocRemark,
			List<JobRecord> existingTrips,
			String rowNumAndField,
			LinkedHashMap<String, String> error,
			String colTitle,
			String colTitle2
	) {

		if (existingTrips.size() > 0) {
			List<JobRecord> toLocTrips = existingTrips.stream()
					.filter(val -> (isEqual(val.getEndLoc(), toLoc) || isEqual(val.getEndLocAddress(), toLoc)))
					.collect(Collectors.toList());

			if (!toLocTrips.isEmpty()) {
				JobRecord toLocTrip = toLocTrips.get(0);
				if (this.isMatchingStartLocation(existTrip, fromLoc) && (!isEqual(toLocTrip.getToLocRemark(), toLocRemark))){
					error.put(rowNumAndField, String.format(CARGO_ERROR_MSG , toLocRemark, toLocTrip.getRowId() + 1, colTitle, colTitle2));
				}
			}
		}
	}

	public void isMatchingEndLocationContactNumber(
			JobRecord existTrip,
			String fromLoc,
			String toLoc,
			String value,
			List<JobRecord> existingTrips,
			String rowNumAndField,
			LinkedHashMap<String, String> error,
			String colTitle,
			String colTitle2
	) {

		if (existingTrips.size() > 0) {
			List<JobRecord> toLocTrips = existingTrips.stream()
					.filter(val -> (isEqual(val.getEndLoc(), toLoc) || isEqual(val.getEndLocAddress(), toLoc)))
					.collect(Collectors.toList());

			if (!toLocTrips.isEmpty()) {
				JobRecord toLocTrip = toLocTrips.get(0);
				if (this.isMatchingStartLocation(existTrip, fromLoc) && (!isEqual(toLocTrip.getToLocMobileNumber(), value))){
					error.put(rowNumAndField, String.format(CARGO_ERROR_MSG , value, toLocTrip.getRowId() + 1, colTitle, colTitle2));
				}
			}
		}
	}

	public void processCellValidationTo(
			Cell cell,
			List<JobRecord> jobRecordList,
			LinkedHashMap<String, String> initValue,
			Row rowNum,
			Row header,
			LinkedHashMap<String, String> error,
			String rowNumAndField,
			String field
	) throws Exception {

		String cellValue = ExcelPOIUtil.getCellValueAsString(cell);
		if (StringUtils.isBlank(cellValue) || jobRecordList.isEmpty()) {
			return;
		}

		List<JobRecord> existingTrips = jobRecordList.stream()
				.filter(val -> val.getShipmentRefNo().trim().equalsIgnoreCase(
						ExcelPOIUtil.getCellValueAsString(rowNum.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))))))
				.collect(Collectors.toList());

		if (existingTrips.isEmpty()) {
			return;
		}

		JobRecord firstTrip = existingTrips.get(0);
		String colTitle = header.getCell(Integer.parseInt(initValue.get(INIT_TO_LOCATION))).getStringCellValue();
		String colTitle2 = header.getCell(Integer.parseInt(initValue.get(INIT_SHIPMENT_REF))).getStringCellValue();
		String fromLocName = String.valueOf(rowNum.getCell(Integer.parseInt(initValue.get(INIT_FROM_LOCATION_NAME))));
		String fromLocDetails = String.valueOf(rowNum.getCell(Integer.parseInt(initValue.get(INIT_FROM_LOCATION))));
		String toLocName = String.valueOf(rowNum.getCell(Integer.parseInt(initValue.get(INIT_TO_LOCATION_NAME))));
		String toLocDetails = String.valueOf(rowNum.getCell(Integer.parseInt(initValue.get(INIT_TO_LOCATION))));

		switch (field){
			case "DATE_OF_DELIVERY":
				isMatchingToLOcDateTime(
						firstTrip,
						fromLocDetails,
						toLocDetails,
						cellValue,
						existingTrips,
						rowNumAndField,
						error,
						colTitle,
						colTitle2
				);
				break;
			case "TO_LOCATION_MOBILE_NUMBER":
				isMatchingToLocMobileDetails(
						firstTrip,
						fromLocName,
						toLocName,
						fromLocDetails,
						toLocDetails,
						cellValue,
						existingTrips,
						rowNumAndField,
						error,
						colTitle,
						colTitle2
				);
				break;
			case "TO_LOCATION_DATE_TIME":
				isMatchingToLOcDateTimeAndDetails(
						firstTrip,
						fromLocName,
						toLocName,
						fromLocDetails,
						toLocDetails,
						cellValue,
						existingTrips,
						rowNumAndField,
						error,
						colTitle,
						colTitle2
				);
				break;
			case "TO_LOCATION_REMARKS":
				isMatchingToLocRemarkDetails(
						firstTrip,
						fromLocName,
						toLocName,
						fromLocDetails,
						toLocDetails,
						cellValue,
						existingTrips,
						rowNumAndField,
						error,
						colTitle,
						colTitle2
				);
				break;
			case "REMARK":
			case "DELIVERY_NOTE":
				isMatchingEndLocationRemark(
						firstTrip,
						fromLocDetails,
						toLocDetails,
						cellValue,
						existingTrips,
						rowNumAndField,
						error,
						colTitle,
						colTitle2
				);
				break;
			case "CONTACT_NUMBER":
				isMatchingEndLocationContactNumber(
						firstTrip,
						fromLocDetails,
						toLocDetails,
						cellValue,
						existingTrips,
						rowNumAndField,
						error,
						colTitle,
						colTitle2
				);
				break;
			default:
				break;
		}

	}

	private static boolean isBlankRow(Row row) {
		for (Cell cell : row)
			if (getCellValue(cell).isEmpty()) return false;
		return true;
	}

	private static String getCellValue(Cell cell) {
		return (cell == null || cell.getCellType() == CellType.BLANK) ? "" :
				cell.getCellType() == CellType.STRING ? cell.getStringCellValue().trim() :
						cell.getCellType() == CellType.NUMERIC && !DateUtil.isCellDateFormatted(cell) ?
								String.valueOf(cell.getNumericCellValue()) : "";
	}
}
