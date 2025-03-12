package com.guudint.clickargo.clictruck.truckInsurance.service;

import com.guudint.clickargo.clictruck.common.listener.AccnInquiryEvent;
import com.guudint.clickargo.clictruck.planexec.job.event.ClicTruckNotifTemplates;
import com.guudint.clickargo.clictruck.truckInsurance.dto.Company;
import com.guudint.clickargo.clictruck.truckInsurance.dto.Truck;
import com.guudint.clickargo.common.service.impl.CkNotificationUtilService;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.can.device.NotificationParam;
import com.vcc.camelone.can.service.impl.NotificationService;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.constant.NotificationType;
import com.vcc.camelone.util.email.SysParam;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;



@Component
public class InsuranceService implements ApplicationListener<AccnInquiryEvent> {
	private static final Logger log = Logger.getLogger(InsuranceService.class);
	public static final String CLICTRUCK_INSURER_ASSURE_TMPLT = "CLICTRUCK_INSURER_ASSURE_TMPLT";
	public static final String CLICTRUCK_INSURER_ASSURE_FORM = "CLICTRUCK_INSURER_ASSURE_FORM";
	public static final String TEMPLATE_APPLICATION_INSURANCE = "Template_Application_Insurance";
	@Autowired
	protected SysParam sysParam;
	@Autowired
	protected CkNotificationUtilService notificationUtilService;
	@Autowired
	protected NotificationService notificationService;
	@Autowired
	private ResourceLoader resourceLoader;

	@Override
	public void onApplicationEvent(AccnInquiryEvent event) {
		try {
			Company company = (Company) event.getSource();
			pushInsurance(company);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public void pushInsurance(Company company) throws ParameterException, IOException, EntityNotFoundException, ProcessingException {
		try {
			if (company == null)
				throw new ParameterException("param event is null");
			File excelFile = generateExcelFile(company);
			if (StringUtils.isNotBlank(company.getEmail())) {
				NotificationParam param = new NotificationParam();
				param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
				param.setTemplateId(ClicTruckNotifTemplates.INSERANCE_APPLICATION.getId());
				String recipientStr = sysParam.getValString(CLICTRUCK_INSURER_ASSURE_TMPLT, null);
				param.setRecipients(new ArrayList<>(Arrays.asList(recipientStr.split(","))));
				ArrayList<String> attachment = new ArrayList<>();
				attachment.add(excelFile.getAbsolutePath());
				param.setAttachments(attachment);
				notificationService.notify(param, NotificationType.SYNCH);
			}
		} catch (Exception ex) {
			log.error("Fail to send email." + ex.getMessage());
			throw ex;
		}
	}
	private File generateExcelFile(Company company) throws IOException {
		Resource resource = resourceLoader.getResource("classpath:excel/Template_Application_Insurance.xlsx");
		File templateFile = resource.getFile();
		File excelFile = File.createTempFile(TEMPLATE_APPLICATION_INSURANCE, ".xlsx");
		try (Workbook workbook = new XSSFWorkbook(Files.newInputStream(templateFile.toPath()));
			 FileOutputStream fos = new FileOutputStream(excelFile)) {
			Sheet sheet = workbook.getSheetAt(0);
			int rowNum = 2; // Start from the second row
			for (Truck truck : company.getTrucks()) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(company.getCompanyName());
				row.createCell(1).setCellValue(company.getEmail());
				row.createCell(2).setCellValue(company.getContactNo());
				row.createCell(3).setCellValue(truck.getLicenseNo());
				row.createCell(4).setCellValue(truck.getMakeAndModel());
				row.createCell(5).setCellValue(truck.getCoverage());
				row.createCell(6).setCellValue(truck.getUsage());
				row.createCell(7).setCellValue(truck.getClaims());
				row.createCell(8).setCellValue(truck.getSuspension());
			}
			workbook.write(fos);
		}
		return excelFile;
	}

	public ResponseEntity<Object> getInsuranceForm() {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			HashMap<String, Object> param = new HashMap<>();
			String recipientObject = sysParam.getValString(CLICTRUCK_INSURER_ASSURE_FORM, null);

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, List<String>> insuranceForm = objectMapper.readValue(recipientObject, new TypeReference<Map<String, List<String>>>() {});

			param.put("coverage", insuranceForm.get("coverage"));
			param.put("usage", insuranceForm.get("usage"));
			param.put("claims", insuranceForm.get("claims"));
			param.put("suspension", insuranceForm.get("suspension"));
			return new ResponseEntity<>(param, HttpStatus.OK);
		} catch (Exception ex) {
			log.error("Error while getting insurance form", ex);
			serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, ex.getMessage()));
			return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_GATEWAY);
		}
	}

}
