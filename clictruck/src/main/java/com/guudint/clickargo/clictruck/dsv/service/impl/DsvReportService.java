package com.guudint.clickargo.clictruck.dsv.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guudint.clicdo.common.service.CkCtCommonService;
import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;
import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.guudint.clickargo.clictruck.dsv.dao.CkCtShipmentDao;
import com.guudint.clickargo.clictruck.dsv.model.TCkCtShipment;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckDao;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckExtDao;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruckExt;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripAttachDao;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripAttach;
import com.guudint.clickargo.common.service.ICkSession;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ProcessingException;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class DsvReportService {
	
	private static Logger log = Logger.getLogger(DsvReportService.class);

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	private static SimpleDateFormat yyyyMMdd_HHmmssSDF = new SimpleDateFormat("yyyyMMdd_HHmmss");
	SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yy HH:mm");
	@Autowired
	private CkJobTruckExtDao ckJobTruckExtDao;
	@Autowired
	private DsvShipmentService auxiliary;
	@Autowired
	private CkCtTripAttachDao attachDao;
	@Autowired
	private CkJobTruckDao ckJobTruckDao;
	@Autowired
	private CkCtShipmentDao shipmentDao;
	@Autowired
	private CkCtCommonService ckCtCommonService;
	@Autowired
	protected ICkSession ckSession;
	
	@Transactional
	public byte[] getReportByJobId(String jobId) throws Exception {
		log.info("getReportByJobId");
		
		try {
			
			Principal principal = ckSession.getPrincipal();
            if (principal == null)
                throw new ProcessingException("principal is null");
            
            List<TCkJobTruckExt> ckJobTruckExts = ckJobTruckExtDao.findAllByJobId(jobId);

            Map<String, String> parameters = new HashMap<>();
            List<Map<String, Object>> dataList = new ArrayList<>();
            
            for (int i = 0; i < ckJobTruckExts.size(); i++) {
            	String jextKey = ckJobTruckExts.get(i).getJextKey();
            	Map<String, Object> data = new HashMap<>();
            	if (jextKey != null && jextKey.contains("goodPackageQty")) {
					data.put(ckJobTruckExts.get(i).getJextKey(), ckJobTruckExts.get(i).getJextVal());
					dataList.add(data);
				} else if (jextKey != null && jextKey.contains("goodPackageType")) {
					data.put(ckJobTruckExts.get(i).getJextKey(), ckJobTruckExts.get(i).getJextVal());
					dataList.add(data);
				} else if (jextKey != null && jextKey.contains("grossWeightMeasure")) {
					data.put(ckJobTruckExts.get(i).getJextKey(), ckJobTruckExts.get(i).getJextVal());
					dataList.add(data);
				} else if (jextKey != null && jextKey.contains("grossWeightUom")) {
					data.put(ckJobTruckExts.get(i).getJextKey(), ckJobTruckExts.get(i).getJextVal());
					dataList.add(data);
				} else if (jextKey != null && jextKey.contains("VolumeMeasure")) {
					data.put(ckJobTruckExts.get(i).getJextKey(), ckJobTruckExts.get(i).getJextVal());
					dataList.add(data);
				} else if (jextKey != null && jextKey.contains("VolumeUom")) {
					data.put(ckJobTruckExts.get(i).getJextKey(), ckJobTruckExts.get(i).getJextVal());
					dataList.add(data);
				} else if (jextKey != null && jextKey.contains("grossLength")) {
					data.put(ckJobTruckExts.get(i).getJextKey(), ckJobTruckExts.get(i).getJextVal());
					dataList.add(data);
				} else if (jextKey != null && jextKey.contains("grossWidth")) {
					data.put(ckJobTruckExts.get(i).getJextKey(), ckJobTruckExts.get(i).getJextVal());
					dataList.add(data);
				} else if (jextKey != null && jextKey.contains("grossHeight")) {
					data.put(ckJobTruckExts.get(i).getJextKey(), ckJobTruckExts.get(i).getJextVal());
					dataList.add(data);
				}
			}
            
            for (TCkJobTruckExt tCkJobTruckExt : ckJobTruckExts) {
                String jextKey = tCkJobTruckExt.getJextKey();
                if (jextKey != null && jextKey.contains("Date")) {
                    String jextVal = tCkJobTruckExt.getJextVal();
                    Date date = dateFormat.parse(jextVal);
                    String formattedDate = outputFormat.format(date);
                    parameters.put(tCkJobTruckExt.getJextKey(), formattedDate);
                }  else{
                    parameters.put(tCkJobTruckExt.getJextKey(), tCkJobTruckExt.getJextVal());
                }
                
            }
            Optional<List<TCkJobTruck>> jobTruckList = Optional.ofNullable(ckJobTruckDao.findByParentId(jobId));
            String jobTruckId = null;
            if (jobTruckList.isPresent()) {
            	jobTruckId = jobTruckList.get().get(0).getJobId();
			}
			Optional<TCkCtDrv> tCkCtDrv = jobTruckList.map(jt -> jt.get(0).getTCkCtDrv());
			if (tCkCtDrv.isPresent()) {
				parameters.put("driverName", Optional.ofNullable(tCkCtDrv.get().getDrvName()).orElse(""));
			}
			
			
			
			List<TCkCtShipment> shipmentList = shipmentDao.fetchByJobId(jobId);
			
			String jrxmlBasePath = auxiliary.getSysParam(CtConstant.KEY_JRXML_BASE_PATH);
			String basePath = auxiliary.getSysParam(CtConstant.KEY_JRXML_BASE_PATH);
			String jrxmlDsvShipmentPath = null;

			String outputFilePath = ckCtCommonService.getCkCtAttachmentPathJob(jobTruckId, true);

			if(parameters.get("mcStateModeOfTransport").equals("Sea")) {
				jrxmlDsvShipmentPath = auxiliary.getSysParam(CtConstant.KEY_JRXML_DSV_SHIPMENT_SEA_PATH);
			}else if(parameters.get("mcStateModeOfTransport").equals("Air")) {
				jrxmlDsvShipmentPath = auxiliary.getSysParam(CtConstant.KEY_JRXML_DSV_SHIPMENT_AIR_PATH);
			} else {
				throw new Exception("Job not Sea and Air");
			}
			parameters.put("gli_logo", jrxmlBasePath + "docs/ClicLogo.png");
            List<TCkCtTripAttach> ckCtTripAttach = attachDao.findByJobId(jobId);
            for (TCkCtTripAttach tCkCtTripAttach : ckCtTripAttach) {
                if (tCkCtTripAttach.getTCkCtMstTripAttachType().getAtypName().equals("SIGNATURE")) {
                    parameters.put("signature", tCkCtTripAttach.getAtLoc());
                }
            }
			JasperReport jasperReport = getReportTemplate(basePath, jrxmlDsvShipmentPath);

			// Convert the List to JRDataSource
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(ckJobTruckExts);

			Map<String, Object> mapParam = new HashMap<>();
			mapParam.put("SUBREPORT_DIR", basePath.concat("docs/"));  // Directory where subreports are located
			mapParam.put("SUBREPORT_FILE", "SubReportDsv.jasper");  // Subreport file name

			mapParam.put("PARAM_INFO", parameters);
			mapParam.put("LIST_ITEM", dataList);
			
			

			// Use JRDataSource instead of List<TCkJobTruckExt>
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, mapParam, dataSource);

			String messageId = parameters.get("messageId");
		    String filePath = outputFilePath + File.separator + this.getPodFileName(messageId, shipmentList.get(0).getShDirection());

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                outputStream.writeTo(fileOutputStream);
            }
            return outputStream.toByteArray();
        } catch (Exception e) {
            // Handle exceptions as needed
            throw new RuntimeException("Error generating shipment report PDF", e);
        }
	}
	
	private static JasperReport getReportTemplate(String basePath, String jrXmlString) throws Exception {
		return JasperCompileManager.compileReport(basePath.concat(jrXmlString));
	}
	
	private String getPodFileName(String messageId, String shipDirection) {
		//If it's export, it's POP. If it's import, it's POD
		return String.format("%s_%s_%s.pdf", yyyyMMdd_HHmmssSDF.format(new Date()), messageId, "Import".equalsIgnoreCase(shipDirection)?"POD":"POP");
	}

}
