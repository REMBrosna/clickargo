package com.guudint.clickargo.clictruck.dsv.pod;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.guudint.clickargo.clictruck.dsv.constant.DsvPodConstant;
import com.guudint.clickargo.clictruck.dsv.model.TCkCtShipment;
import com.guudint.clickargo.clictruck.dsv.service.IPodService;
import com.guudint.clickargo.clictruck.dto.DsvFieldSea;
import com.guudint.clickargo.clictruck.dto.DsvFields;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruckExt;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripAttach;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripLocation;
import com.guudint.clickargo.job.model.TCkJob;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service("podDsvService")
public class PodDsvServiceImpl extends AbstractPodServiceImpl implements IPodService {
	
	@Override
	@Transactional
	public String generateShipmentReport(String jobId) throws JRException {

		try {

			List<TCkJobTruckExt> ckJobTruckExts = super.loadJObTruckExt(jobId);
			
			Map<String, Object> parameters = new HashMap<>();

			List<DsvFields> dsvLines = new ArrayList<>();
			List<DsvFieldSea> dsvFieldSeas = new ArrayList<>();
			for (TCkJobTruckExt tCkJobTruckExt : ckJobTruckExts) {
				String jextKey = tCkJobTruckExt.getJextKey();
				if (jextKey != null && jextKey.equalsIgnoreCase(DsvPodConstant.LIST_GOOD_ITEM)) {
					dsvLines = convertJsonToGoodsItemList(tCkJobTruckExt.getJextVal());
				} else if (jextKey != null && jextKey.equalsIgnoreCase(DsvPodConstant.LIST_EQUIP_UNIT)) {
					dsvFieldSeas = convertJsonToEquipUnit(tCkJobTruckExt.getJextVal());
				} else {
					parameters.put(tCkJobTruckExt.getJextKey(), tCkJobTruckExt.getJextVal());
				}
			}

			List<Object> listLines = new ArrayList<>();
			listLines.addAll(dsvLines);
			listLines.addAll(dsvFieldSeas);

			Optional<List<TCkJobTruck>> jobTruckList = Optional.ofNullable(ckJobTruckDao.findByParentId(jobId));
			TCkJobTruck jobTruck = jobTruckList.get().get(0);
			
			setParameterFromDb(parameters, jobTruck);

			String jrxmlBasePath = auxiliary.getSysParam(CtConstant.KEY_JRXML_BASE_PATH);
			String basePath = auxiliary.getSysParam(CtConstant.KEY_JRXML_BASE_PATH);
			String jrxmlDsvShipmentPath = null;
			File subReportFile = null;
			JRBeanCollectionDataSource dataSource = null;
			if (parameters.get(DsvPodConstant.MC_STATE_MODE_OF_TRANSPORT).equals("Sea")) {
				jrxmlDsvShipmentPath = auxiliary.getSysParam(CtConstant.KEY_JRXML_DSV_SHIPMENT_SEA_PATH);
				subReportFile = ResourceUtils.getFile(jrxmlBasePath.concat("docs/").concat("epod/SubReportDsvSea.jasper"));
				dataSource = new JRBeanCollectionDataSource(dsvFieldSeas);
			} else if (parameters.get(DsvPodConstant.MC_STATE_MODE_OF_TRANSPORT).equals("Air")) {
				jrxmlDsvShipmentPath = auxiliary.getSysParam(CtConstant.KEY_JRXML_DSV_SHIPMENT_AIR_PATH);
				subReportFile = ResourceUtils.getFile(jrxmlBasePath.concat("docs/").concat("epod/SubReportDsvAir.jasper"));
				dataSource = new JRBeanCollectionDataSource(dsvLines);
			} else {
				throw new Exception("Job Not Sea and Air");
			}
			parameters.put("gli_logo", jrxmlBasePath + "docs/ClicLogo.png");
			List<TCkCtTripAttach> ckCtTripAttach = attachDao.findByJobId(jobId);
			for (TCkCtTripAttach tCkCtTripAttach : ckCtTripAttach) {
				if (tCkCtTripAttach.getTCkCtMstTripAttachType().getAtypName().equals("SIGNATURE")) {
					parameters.put(DsvPodConstant.SIGNATURE, tCkCtTripAttach.getAtLoc());
				}
			}

			Map<String, Object> mapParam = new HashMap<>();

			parameters.put("listSub", subReportFile);
			mapParam.put("PARAM_INFO", parameters);

			JasperReport jasperReport = super.getReportTemplate(basePath, jrxmlDsvShipmentPath);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, mapParam, dataSource);

			String filePath = this.generatePodFileName(jobTruck, null);

			OutputStream outputStream = new FileOutputStream(filePath);
			JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

			return filePath;

		} catch (Exception e) {
			e.printStackTrace();
			throw new JRException("Error generating report: " + e.getMessage());
		}

	}

	public String generatePodFileName(TCkJobTruck jobTruck, TCkCtTrip trip) throws Exception {

		List<TCkCtShipment> shipmentList = shipmentDao.fetchByJobId(jobTruck.getTCkJob().getJobId());

		String outputFilePath = ckCtCommonService.getCkCtAttachmentPathJob(jobTruck.getJobId(), true);
		
		Date deliverDate = new Date();
		
		TCkJob ckJob = jobTruck.getTCkJob();
		if(ckJob != null && ckJob.getTCkRecordDate() != null 
				&& ckJob.getTCkRecordDate().getRcdDtComplete() != null ) {
			deliverDate = ckJob.getTCkRecordDate().getRcdDtComplete();
		}

		String filePath = outputFilePath + File.separator
				+ this.getPodFileName(shipmentList.get(0).getShMsgId(), shipmentList.get(0).getShDirection(), deliverDate);

		return filePath;
	}
	

	
	private void setParameterFromDb(Map<String, Object> parameters, TCkJobTruck jobTruck) throws Exception {

		parameters.put(DsvPodConstant.COMPANY_NAME,
				jobTruck.getTCoreAccnByJobPartyCoFf().getAccnName());
		parameters.put(DsvPodConstant.COMPANY_REG,
				jobTruck.getTCoreAccnByJobPartyCoFf().getAccnPassNid());
		parameters.put(DsvPodConstant.COMPANY_ADDRESS,
				jobTruck.getTCoreAccnByJobPartyCoFf().getAccnAddr().getAddrLn1());
		parameters.put(DsvPodConstant.COMPANY_COUNTRY,
				jobTruck.getTCoreAccnByJobPartyCoFf().getAccnNationality());

		TCkCtDrv tCkCtDrv = jobTruck.getTCkCtDrv();
		if (tCkCtDrv != null) {
			parameters.put(DsvPodConstant.DRIVER_NAME, Optional.ofNullable(tCkCtDrv.getDrvName()).orElse(""));
		}
		
		TCkCtVeh tCkCtVeh = jobTruck.getTCkCtVeh();
		if (tCkCtVeh != null) {
			parameters.put(DsvPodConstant.VH_PLATE_NO, Optional.ofNullable(tCkCtVeh.getVhPlateNo()).orElse(""));
		}
		
		Date startDate = jobTruck.getTCkJob().getTCkRecordDate().getRcdDtStart();
		Date completeDate = jobTruck.getTCkJob().getTCkRecordDate().getRcdDtComplete();
		
		if(startDate != null) {
			parameters.put(DsvPodConstant.JOB_START_DATE, ddMMMyySDF.format(startDate) );
			parameters.put(DsvPodConstant.JOB_START_TIME, HHmmSDF.format(startDate));
		}

		if(completeDate != null) {
			parameters.put(DsvPodConstant.JOB_DELIVER_TIME, HHmmSDF.format(completeDate));
		}
		
		List<TCkCtTrip> tripList = ckCtTripDao.findByJobId(jobTruck.getJobId());
		
		if(tripList != null && tripList.size() > 0) {
			TCkCtTripLocation fromLoc = tripList.get(0).getTCkCtTripLocationByTrFrom();
			TCkCtTripLocation toLoc = tripList.get(0).getTCkCtTripLocationByTrTo();
			if( fromLoc != null) {
				parameters.put(DsvPodConstant.JOB_LOC_PICKUP, fromLoc.getTlocLocAddress());
			}
			if( toLoc != null) {
				parameters.put(DsvPodConstant.JOB_LOC_DROPOFF, toLoc.getTlocLocAddress());
			}
		}
		
	}

	/**
	 * 20211005_122447_SWRO0007149_POD.pdf 20211105_111844_SSIN0252132-NSH2_POD.pdf
	 * 
	 * @param messageId
	 * @return
	 */
	private String getPodFileName(String messageId, String shipDirection, Date date) {
		// If it's export, it's POP. If it's import, it's POD
		return String.format("%s_%s_%s.pdf", yyyyMMdd_HHmmssSDF.format(date), messageId,
				"Import".equalsIgnoreCase(shipDirection) ? "POD" : "POP");
	}

	private static List<DsvFields> convertJsonToGoodsItemList(String jsonString) {
		Gson gson = new Gson();
		Type listType = new TypeToken<List<DsvFields>>() {
		}.getType();
		return gson.fromJson(jsonString, listType);
	}

	private List<DsvFieldSea> convertJsonToEquipUnit(String jextVal) {
		Gson gson = new Gson();
		Type listType = new TypeToken<List<DsvFieldSea>>() {
		}.getType();
		return gson.fromJson(jextVal, listType);
	}

}
