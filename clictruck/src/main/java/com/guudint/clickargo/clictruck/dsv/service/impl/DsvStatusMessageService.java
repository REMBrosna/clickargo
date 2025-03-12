package com.guudint.clickargo.clictruck.dsv.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dsv.edi.xml.DSV_StatusMessage_v1.DSVStatusMessage;
import com.dsv.edi.xml.DSV_StatusMessage_v1.DateList;
import com.dsv.edi.xml.DSV_StatusMessage_v1.Header;
import com.dsv.edi.xml.DSV_StatusMessage_v1.Location;
import com.dsv.edi.xml.DSV_StatusMessage_v1.LocationIdentification;
import com.dsv.edi.xml.DSV_StatusMessage_v1.LocationList;
import com.dsv.edi.xml.DSV_StatusMessage_v1.Shipment;
import com.dsv.edi.xml.DSV_StatusMessage_v1.ShipmentId;
import com.dsv.edi.xml.DSV_StatusMessage_v1.ShipmentList;
import com.dsv.edi.xml.DSV_StatusMessage_v1.ShipmentStatus;
import com.dsv.edi.xml.DSV_StatusMessage_v1.Stage;
import com.dsv.edi.xml.DSV_StatusMessage_v1.Status;
import com.dsv.edi.xml.DSV_StatusMessage_v1.StatusDetailslist;
import com.dsv.edi.xml.DSV_StatusMessage_v1.TransportStage;
import com.dsv.edi.xml.DSV_StatusMessage_v1.TransportStageList;
import com.guudint.clickargo.clictruck.dsv.dao.CkCtShipmentDao;
import com.guudint.clickargo.clictruck.dsv.model.TCkCtShipment;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruckExt;
import com.guudint.clickargo.master.enums.ShipmentTypes;
import com.vcc.camelone.util.email.SysParam;
import com.vcc.camelone.util.xml.XmlUtil;

@Service
public class DsvStatusMessageService {

	private static Logger log = Logger.getLogger(DsvStatusMessageService.class);

	private static SimpleDateFormat yyyyMMddHHmmssSDF = new SimpleDateFormat("yyyyMMddHHmmss");

	@Autowired
	private CkCtShipmentDao shipmentDao;
	@Autowired
	protected SysParam sysParam;

	public DSVStatusMessage generateStatusMessage(String parentId, Date deliverDate, List<TCkJobTruckExt> extList) throws Exception {
		
		log.info( parentId + "  " );

		List<TCkCtShipment> ckCtDsvShipmentList = shipmentDao.fetchByJobId(parentId);

		if (ckCtDsvShipmentList == null || ckCtDsvShipmentList.size() == 0) {
			throw new Exception("Fail to find TCkCtDsvShipment: " + parentId);
		}
		TCkCtShipment tCkCtShipment = ckCtDsvShipmentList.get(0);
		String messageId = tCkCtShipment.getShMsgId();
		//String issueDate = shipment.getShDtIssue();
		
		// "mcStateModeOfTransport"
		String airOrSea = extList.stream().filter( ext -> "mcStateModeOfTransport".equalsIgnoreCase(ext.getJextKey())).map(TCkJobTruckExt::getJextVal).findFirst().orElse(null);

		String currentTime = yyyyMMddHHmmssSDF.format(deliverDate);
		
		String senderId = sysParam.getValString("CLICTRUCK_DSVAS_SENDER_ID", "VCC6408754183");

		Header header = new Header();
		com.dsv.edi.xml.DSV_StatusMessage_v1.Date documentDate = new com.dsv.edi.xml.DSV_StatusMessage_v1.Date();
		documentDate.setFormat("DateTimeSec");
		documentDate.setType("IssueDate");
		documentDate.setDateTime(currentTime);

		header.setMessageId(messageId);
		header.setSenderId(senderId); //
		header.setReceiverId("DSV");
		header.setDocumentCode("ConsignmentStatusReport");
		header.setDocumentDate(documentDate);
		header.setFunction("NEW");
		header.setVersion("1.0");

		//
		DateList shipmentDateList = new DateList();

		com.dsv.edi.xml.DSV_StatusMessage_v1.Date date = new com.dsv.edi.xml.DSV_StatusMessage_v1.Date();
		date.setFormat("DateTime");
		date.setType("IssueDate");
		date.setDateTime(currentTime);

		shipmentDateList.getDate().add(date);
		
		// 
		Location loc = new Location();
		loc.setType("BranchLocation");

		LocationIdentification locIde = new LocationIdentification();
		locIde.setType("ISOCountry");
		locIde.setLocationIdentifier("SG");

		loc.getLocationIdentification().add(locIde);

		//
		Status status = new Status();
		status.setType("Consignment");
		status.setStatusEventCode(this.getStatusEventCode(tCkCtShipment));
		
		//
		DateList dateList = new DateList();

		date = new com.dsv.edi.xml.DSV_StatusMessage_v1.Date();
		date.setFormat("DateTime");
		date.setType("StatusDate");
		date.setDateTime(currentTime);

		dateList.getDate().add(date);

		//
		LocationIdentification locIdentif = new LocationIdentification();
		locIdentif.setType("SiteId");
		locIdentif.setLocationName("Singapore");
		locIdentif.setLocationIdentifier("SGSIN");

		Location l = new Location();
		l.setType("StatusLocation");
		l.getLocationIdentification().add(locIdentif);

		LocationList locationList = new LocationList();
		locationList.getLocation().add(l);

		StatusDetailslist statusDetaisList = new StatusDetailslist();
		statusDetaisList.setStatus(status);
		statusDetaisList.setDates(dateList);
		statusDetaisList.setLocations(locationList);

		Stage stage = new Stage();
		stage.setType("MainCarriage");
		stage.setModeOfTransport(airOrSea);

		TransportStage ts = new TransportStage();
		ts.setStage(stage);

		statusDetaisList.setTransportStages(new TransportStageList());
		statusDetaisList.getTransportStages().getTransportStage().add(ts);

		ShipmentId shipmentId = new ShipmentId();
		shipmentId.setValue(messageId);
		
		Shipment shipment = new Shipment();
		shipment.setShipmentId(shipmentId);
		shipment.getStatusDetails().add(statusDetaisList);

		ShipmentList shipmentList = new ShipmentList();
		shipmentList.getShipment().add(shipment);

		ShipmentStatus shipmentStauts = new ShipmentStatus();
		
		shipmentStauts.setDates(dateList);
		shipmentStauts.setLocations(new com.dsv.edi.xml.DSV_StatusMessage_v1.LocationList());
		shipmentStauts.getLocations().getLocation().add(loc);
		shipmentStauts.setShipments(shipmentList);

		// // // DSVStatusMessage
		DSVStatusMessage statusMsg = new DSVStatusMessage();
		statusMsg.setHeader(header);
		statusMsg.setShipmentStatus(shipmentStauts);

		String statusMsgStr = XmlUtil.obj2Xml(statusMsg, DSVStatusMessage.class);
		log.info( parentId + "  " + statusMsgStr);
		
		return statusMsg;
	}
	
	private String getStatusEventCode(TCkCtShipment tCkCtShipment) {
		
		if(ShipmentTypes.EXPORT.name().equalsIgnoreCase(tCkCtShipment.getShDirection())) {
			// Export
			return "PickupCartageFinalised";
		}
		// Import
		return "DeliveryCartageFinalised";
	}

}
