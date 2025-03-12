package com.guudint.clickargo.clictruck.dsv.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsv.edi.xml.DSV_ShipmentMessage_v1.Contact;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.ControlTotal;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.DSVShipmentMessage;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.Dimension;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.DimensionsList;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.EquipmentUnit;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.FreeText;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.GoodsItem;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.Location;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.LocationIdentification;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.Measurement;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.Party;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.PaymentInstructions;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.ReferenceGroup;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.Requirement;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.Terms;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.TransportService;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.TransportStage;
import com.google.gson.Gson;
import com.guudint.clickargo.clictruck.constant.TruckTripAttachmentEnum;
import com.guudint.clickargo.clictruck.dsv.constant.DsvPodConstant;
import com.guudint.clickargo.clictruck.dsv.dao.CkCtShipmentDao;
import com.guudint.clickargo.clictruck.dsv.model.TCkCtShipment;
import com.guudint.clickargo.clictruck.dsv.service.IPodService;
import com.guudint.clickargo.clictruck.dto.DsvFieldSea;
import com.guudint.clickargo.clictruck.dto.DsvFields;
import com.guudint.clickargo.clictruck.dto.DsvSubLine;
import com.guudint.clickargo.clictruck.dto.DsvSublineSea;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckDao;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDoAttachDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDoDao;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripDo;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripDoAttach;
import com.guudint.clickargo.clictruck.util.NumberUtil;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.RecordStatus;

@Service
public class DsvPodService {

	private static Logger log = Logger.getLogger(DsvPodService.class);

	public static char EPOD_STATUS_SEND = 'S';

	public static String DSV_EMAIL_NOTIFY_TYPE_EPOD = "POD";
	public static String DSV_EMAIL_NOTIFY_TYPE_PHO = "PHO";


	@Autowired
	private ApplicationContext context;

	@Autowired
	private CkCtShipmentDao shipmentDao;
	@Autowired
	private CkJobTruckDao ckJobTruckDao;
	@Autowired
	private CkCtTripDao ckCtTripDao;
	@Autowired
	private CkCtTripDoDao ckCtTripDoDao;
	@Autowired
	private CkCtTripDoAttachDao tripDoAttachDao;
	@Autowired
	private DsvUtilService dsvUtilService;

	//private String dsvBeanName = "podDsvService";

	@Transactional
	public TCkCtTripDoAttach createTripDoAttach(String jobId, String ePodFile) throws Exception {

		// TCkCtTripDo ckCtTripDo = new TCkCtTripDo();
		TCkCtTripDoAttach ckCtTripDoAttach = new TCkCtTripDoAttach();

		try {

			List<TCkCtShipment> shipmentList = shipmentDao.fetchByJobId(jobId);

			if (shipmentList == null || shipmentList.size() == 0) {
				return null;
			}

			List<TCkJobTruck> jobTruckList = ckJobTruckDao.findByParentId(jobId);

			if (jobTruckList == null || jobTruckList.size() == 0) {
				return null;
			}

			List<TCkCtTrip> ckCtTripList = ckCtTripDao.findByJobId(jobTruckList.get(0).getJobId());

			if (ckCtTripList != null && ckCtTripList.size() > 0) {

				TCkCtTrip trip = ckCtTripList.get(0);

				//String filePath = jobTruckPod.getJpodDocLoc(); // This path is important.
				String fileName = FilenameUtils.getName(ePodFile);

				ckCtTripDoAttach.setDoaId(CkUtil.generateId(ICkConstant.PREFIX_JOB_ATT));
				ckCtTripDoAttach.setTCkCtTrip(trip);
				ckCtTripDoAttach.setDoaName(fileName);
				ckCtTripDoAttach.setDoaLoc(ePodFile);
				ckCtTripDoAttach.setDoaSource(TruckTripAttachmentEnum.SCHEDULER.name());

				ckCtTripDoAttach.setDoaStatus(RecordStatus.ACTIVE.getCode());
				ckCtTripDoAttach.setDoaDtCreate(Calendar.getInstance().getTime());
				ckCtTripDoAttach.setDoaUidCreate("SYS");
				ckCtTripDoAttach.setDoaDtLupd(Calendar.getInstance().getTime());
				ckCtTripDoAttach.setDoaUidLupd("SYS");

				tripDoAttachDao.saveOrUpdate(ckCtTripDoAttach);

				// find and update T_CK_CT_TRIP_DO
				List<TCkCtTripDo> tripDoList = ckCtTripDoDao.findByTripId(trip.getTrId());

				if (tripDoList != null && tripDoList.size() > 0) {
					tripDoList.get(0).setDoSigned(ckCtTripDoAttach.getDoaId());
					tripDoList.get(0).setDoDtLupd(new Date());
					ckCtTripDoDao.saveOrUpdate(tripDoList.get(0));
				}
				//
			}
		} catch (Exception e) {
			log.error("Fail add data to trip do and trip do attach ", e);
		}
		return ckCtTripDoAttach;
	}
/*-
	public TCkJobTruckPod createPod(String jobId) throws Exception {

		TCkJobTruckPod ckJobTruckPod = new TCkJobTruckPod();
		ckJobTruckPod.setJpodJobId(jobId);
		ckJobTruckPod.setJpodTemplateBean(dsvBeanName);

		ckJobTruckPod.setJpodDtCreate(new Date());
		ckJobTruckPod.setJpodStatus(Constant.ACTIVE_STATUS);

		ckJobTruckPodDao.saveOrUpdate(ckJobTruckPod);

		return ckJobTruckPod;
	}

	public TCkJobTruckPod update(String jobId) throws Exception {

		TCkJobTruckPod ckJobTruckPod = ckJobTruckPodDao.find(jobId);

		if (null != ckJobTruckPod) {
			ckJobTruckPod.setJpodDocLoc(jobId);
			ckJobTruckPod.setJpodDtDocCreate(new Date());

			ckJobTruckPod.setJpodDtLupd(new Date());

			ckJobTruckPodDao.saveOrUpdate(ckJobTruckPod);

			return ckJobTruckPod;
		}
		return null;
	}
*/
	public String generatePODfile(String jobId) throws Exception {

		try {
			IPodService iPodService = (IPodService) context.getBean("podDsvService");

			if (null == iPodService) {
				throw new Exception("Fail to find bean: " + "podDsvService");
			}

			// generate PDF file
			String filePath = iPodService.generateShipmentReport(jobId);

			return filePath;

		} catch (Exception e) {
			log.error("Fail to genearete ePod file ", e);
			throw e;
		} 
	}

	public void sendPodEmail(String jobId, String epodFile) throws Exception {

		// get messageId
		String messageId = null;

		List<TCkCtShipment> shipmentList = shipmentDao.fetchByJobId(jobId);
		if (shipmentList == null || shipmentList.size() == 0) {
			throw new Exception("Fail to find shipment by Job id: " + jobId);
		}
		messageId = shipmentList.get(0).getShMsgId();

		// send ePOD email.
		dsvUtilService.sendDSVEmailNotification(epodFile, messageId, DSV_EMAIL_NOTIFY_TYPE_EPOD);
	}

	/**
	 * convert DSVShipmentMessage to Map,
	 * 
	 * @param msg
	 * @return
	 */
	public Map<String, String> convert2Map(DSVShipmentMessage msg) {

		Map<String, String> map = new HashMap<>();

		map.put(DsvPodConstant.MESSAGE_ID, Optional.ofNullable(msg.getHeader().getMessageId()).orElse(" "));
		map.put(DsvPodConstant.SENDER_ID, Optional.ofNullable(msg.getHeader().getSenderId()).orElse(" "));
		map.put(DsvPodConstant.RECEIVER_ID, Optional.ofNullable(msg.getHeader().getReceiverId()).orElse(" "));
		map.put(DsvPodConstant.HEADER_DOCUMENT_DATE,
				Optional.ofNullable(msg.getHeader().getDocumentDate().getDateTime()).orElse(" "));
		map.put(DsvPodConstant.SHIPMENT_ID, Optional.ofNullable(msg.getShipment().getShipmentId()).orElse(" "));

		Optional<List<com.dsv.edi.xml.DSV_ShipmentMessage_v1.Date>> listDates = Optional.ofNullable(msg.getShipment())
				.map(sp -> sp.getDates()).map(dts -> dts.getDate());
		if (listDates.isPresent()) {
			for (com.dsv.edi.xml.DSV_ShipmentMessage_v1.Date dates : listDates.get()) {
				String dateTime = Optional.ofNullable(dates.getDateTime()).orElse(" ");
				dateTime = this.convertDateFormat(dateTime);
				
				switch (dates.getType()) {
				case "IssueDate":
					map.put(DsvPodConstant.ISSUE_DATE, dateTime);
					break;
				case "OrderReceived":
					map.put(DsvPodConstant.ORDER_RECEIVED_DATE, dateTime);
					break;
				case "Collection":
					map.put(DsvPodConstant.COLLECTION_DATE, dateTime);
					break;
				case "DeliveryRequested":
					map.put(DsvPodConstant.DELIVERY_REQUESTED_DATE, dateTime);
					break;
				case "EstimatedCollection":
					map.put(DsvPodConstant.ESTIMATED_COLLECTION_DATE, dateTime);
					break;
				case "ScheduledCollection":
					map.put(DsvPodConstant.SCHEDULED_COLLECTION_DATE, dateTime);
					break;
				case "ActualCollection":
					map.put(DsvPodConstant.ACTUAL_COLLECTION_DATE, dateTime);
					break;
				case "EstimatedDelivery":
					map.put(DsvPodConstant.ESTIMATED_DELIVERY_DATE, dateTime);
					break;
				case "Booked":
					map.put(DsvPodConstant.BOOKED_DATE, dateTime);
					break;
				case "HouseBillIssued":
					map.put(DsvPodConstant.HOUSE_BILL_ISSUED_DATE, dateTime); 
					break;
				default:
					break;
				}
			}
		}

		Optional<List<TransportService>> tsListOpt = Optional.ofNullable(msg.getShipment())
				.map(sp -> sp.getTransportServices()).map(ts -> ts.getTransportService());
		if (tsListOpt.isPresent()) {
			for (TransportService trService : tsListOpt.get()) {
				map.put(DsvPodConstant.TRANSPORT_SERVICE_CODE,
						Optional.ofNullable(trService.getServiceCode()).orElse(" "));
				map.put(DsvPodConstant.TRANSPORT_CARGO_TYPE, Optional.ofNullable(trService.getCargoType()).orElse(" "));
				map.put(DsvPodConstant.TRANSPORT_PACKAGING_STATE,
						Optional.ofNullable(trService.getPackagingState()).orElse(" "));
				map.put(DsvPodConstant.TRANSPORT_PRIORITY_CODE,
						Optional.ofNullable(trService.getPriorityCode()).orElse(" "));
			}
		}

		Optional<List<FreeText>> listFt = Optional.ofNullable(msg.getShipment()).map(sp -> sp.getFreeTextData())
				.map(ft -> ft.getFreeText());
		if (listFt.isPresent()) {
			for (FreeText freeText : listFt.get()) {
				switch (freeText.getType()) {
				case "NatureOfCargo":
					handleFreeText(map, freeText, "natureOfCargo");
					break;
				case "GoodsDescription":
					handleFreeText(map, freeText, "goodsDescription");
					break;
				case "BasicGoodsDescription":
					handleFreeText(map, freeText, "basicGoodsDescription");
					break;
				case "DropModePickup":
					handleFreeText(map, freeText, "dropModePickup");
					break;
				case "DropModeDelivery":
					handleFreeText(map, freeText, "dropModeDelivery");
					break;
				default:
				}
			}
		}

		Optional<List<ControlTotal>> listCt = Optional.ofNullable(msg.getShipment()).map(sp -> sp.getControlTotals())
				.map(ct -> ct.getControlTotal());
		if (listCt.isPresent()) {
			for (ControlTotal controlTotal : listCt.get()) {
				switch (controlTotal.getType()) {
				case "NumberOfPackages":
					handleControlTotal(map, controlTotal, "numberOfPackages");
					break;
				case "GrossWeight":
					map.put("grossWeightQuantity", Optional.ofNullable(controlTotal.getQuantity()).orElse(""));
					map.put("grossWeightUom", controlTotal.getUom().equalsIgnoreCase("KGM") ? "KG"
							: Optional.ofNullable(controlTotal.getUom()).orElse(""));
					break;
				case "ChargeableWeight":
					map.put("chargeableWeightQuantity", Optional.ofNullable(controlTotal.getQuantity()).orElse(""));
					map.put("chargeableWeightUom", controlTotal.getUom().equalsIgnoreCase("KGM") ? "KG"
							: Optional.ofNullable(controlTotal.getUom()).orElse(""));
					break;
				case "Volume":
					map.put("volumeQuantity", Optional.ofNullable(controlTotal.getQuantity()).orElse(""));
					map.put("volumeUom", controlTotal.getUom().equalsIgnoreCase("MTQ") ? "M3"
							: Optional.ofNullable(controlTotal.getUom()).orElse(""));
					break;
				default:
				}

			}
		}

		Optional<List<Location>> listLc = Optional.ofNullable(msg.getShipment()).map(sp -> sp.getLocations())
				.map(loc -> loc.getLocation());
		if (listLc.isPresent()) {
			for (Location location : listLc.get()) {
				if (location.getType().equals("BranchLocation")) {
					map.put("branchLocationType", location.getType());
					Optional<List<LocationIdentification>> listLocIde = Optional
							.ofNullable(location.getLocationIdentification());
					if (listLocIde.isPresent()) {
						for (LocationIdentification identification : listLocIde.get()) {
							if (identification.getType().equals("DSVBranch")) {
								map.put("dsvBranchLocationIdentifier",
										Optional.ofNullable(identification.getLocationIdentifier()).orElse(""));
								map.put("dsvBranchLocationName",
										Optional.ofNullable(identification.getLocationName()).orElse(""));
							}
						}
					}

				}
			}
		}

		Optional<Terms> termsOpt = Optional.ofNullable(msg.getShipment()).map(sh -> sh.getTermsOfDelivery())
				.map(td -> td.getTerms());

		map.put(DsvPodConstant.TG_CODE_ID, termsOpt.map(t -> t.getCodeId()).orElse(""));
		map.put(DsvPodConstant.TG_DELIVERY_CODE, termsOpt.map(t -> t.getDeliveryCode()).orElse(""));
		map.put(DsvPodConstant.TG_TERM_CODE, termsOpt.map(t -> t.getTermCode()).orElse(""));
		map.put(DsvPodConstant.TG_TRANSPORT_CHARGES_CODE, termsOpt.map(t -> t.getTransportChargesCode()).orElse(""));

		Optional<List<Requirement>> listReq = Optional.ofNullable(msg.getShipment()).map(sp -> sp.getRequirements())
				.map(req -> req.getRequirement());
		if (listReq.isPresent()) {
			for (Requirement requirement : listReq.get()) {
				map.put("requirementInvolvementCode",
						Optional.ofNullable(requirement.getGovernmentalRequirements().getInvolvementCode()).orElse(""));
				map.put("requirementTransportMovementCode", Optional
						.ofNullable(requirement.getGovernmentalRequirements().getTransportMovementCode()).orElse(""));
			}
		}

		Optional<List<PaymentInstructions>> listPay = Optional.ofNullable(msg.getShipment())
				.map(sp -> sp.getChargeMethodology()).map(cm -> cm.getPaymentInstructions());
		if (listPay.isPresent()) {
			for (PaymentInstructions paymentInstructions : listPay.get()) {
				map.put("paymentInstructionCode",
						Optional.ofNullable(paymentInstructions.getPaymentInstruction().getPaymentCode()).orElse(""));
			}
		}

		Optional<List<ReferenceGroup>> listRefGroup = Optional.ofNullable(msg.getShipment())
				.map(sp -> sp.getReferences()).map(ref -> ref.getReferenceGroup());
		if (listRefGroup.isPresent()) {
			for (ReferenceGroup referenceGroup : listRefGroup.get()) {
				switch (referenceGroup.getReference().getType()) {
				case "EventCode":
					handleReferenceGroup(map, referenceGroup, "eventCode");
					break;
				case "ConsigneeReference":
					handleReferenceGroup(map, referenceGroup, "consigneeReference");
					break;
				case "HouseBill":
					handleReferenceGroup(map, referenceGroup, "houseBill");
					break;
				case "BookingReference":
					handleReferenceGroup(map, referenceGroup, "bookingReference");
					break;
				case "ServiceCount":
					handleReferenceGroup(map, referenceGroup, "serviceCount");
					break;
				case "ServiceCategoryReference":
					handleReferenceGroup(map, referenceGroup, "serviceCategoryReference");
					break;
				case "InspectionType":
					handleReferenceGroup(map, referenceGroup, "inspectionType");
					break;
				case "AviationSecurity":
					handleReferenceGroup(map, referenceGroup, "aviationSecurity");
					break;
				case "OrderReference":
					handleReferenceGroup(map, referenceGroup, "orderReference");
					break;
				case "ShipperReference":
					handleReferenceGroup(map, referenceGroup, "shipperReference");
					break;
				default:
					break;
				}
			}
		}

		Optional<List<TransportStage>> listTs = Optional.ofNullable(msg.getShipment())
				.map(sp -> sp.getTransportStages()).map(ts -> ts.getTransportStage());
		if (listTs.isPresent()) {
			for (TransportStage transportStage : listTs.get()) {
				switch (transportStage.getStage().getType()) {
				case "MainCarriage":
					handleTransportStage(map, transportStage, "mc");
					break;
				case "PlannedLeg1":
					handleTransportStage(map, transportStage, "pl1");
					break;
				default:
				}
				Optional<List<com.dsv.edi.xml.DSV_ShipmentMessage_v1.Date>> listDate = Optional
						.ofNullable(transportStage.getDates()).map(dts -> dts.getDate());
				if (listDate.isPresent()) {
					for (com.dsv.edi.xml.DSV_ShipmentMessage_v1.Date date : listDate.get()) {
						String dateTime = Optional.ofNullable(date.getDateTime()).orElse("");
						dateTime = this.convertDateFormat(dateTime);
						
						switch (date.getType()) {
						case "ScheduledDeparture":
							map.put(DsvPodConstant.T_STAGE_SCHEDULED_DEPARTURE, dateTime);
							break;
						case "ScheduledShipmentDeparture":
							map.put(DsvPodConstant.T_STAGE_SCHEDULED_SHIPMENT_DEPARTURE, dateTime);
							break;
						case "ScheduledArrival":
							map.put(DsvPodConstant.T_STAGE_SCHEDULED_ARRIVAL, dateTime);
							break;
						case "ScheduledShipmentArrival":
							map.put(DsvPodConstant.T_STAGE_SCHEDULED_SHIPMENT_ARRIVAL, dateTime);
							break;
						default:
						}
					}
				}

				Optional<List<Location>> listLocTs = Optional.ofNullable(transportStage.getLocations())
						.map(loc -> loc.getLocation());
				if (listLocTs.isPresent()) {
					for (Location location : listLocTs.get()) {
						switch (location.getType()) {
						case "Departure":
							handleLocation(map, location, "departure");
							break;
						case "Destination":
							handleLocation(map, location, "destination");
							break;
						case "PlaceOfLoading":
							handleLocation(map, location, "placeOfLoading");
							break;
						default:
							break;
						}

					}
				}
				Optional<List<ReferenceGroup>> listRg = Optional.ofNullable(transportStage.getReferences())
						.map(ref -> ref.getReferenceGroup());
				if (listRg.isPresent()) {
					for (ReferenceGroup referenceGroup : listRg.get()) {
						switch (referenceGroup.getReference().getType()) {
						case "MasterBill":
							handleReferenceGroup(map, referenceGroup, "masterBill");
							break;
						case "TransportInstructionNumber":
							handleReferenceGroup(map, referenceGroup, "transportInstructionNumber");
							break;
						case "TransportMasterType":
							handleReferenceGroup(map, referenceGroup, "transportMasterType");
							break;
						case "ConsolContainerMode":
							handleReferenceGroup(map, referenceGroup, "consolContainerMode");
							break;
						case "ConsolServiceLevel":
							handleReferenceGroup(map, referenceGroup, "consolServiceLevel");
							break;
						case "StandardCarrierAlphaCode":
							handleReferenceGroup(map, referenceGroup, "standardCarrierAlphaCode");
							break;
						case "TypeOfService":
							handleReferenceGroup(map, referenceGroup, "typeOfService");
							break;
						default:
						}
					}
				}

			}
		}

		Optional<List<Party>> listParty = Optional.ofNullable(msg.getShipment()).map(sp -> sp.getParties())
				.map(pt -> pt.getParty());
		if (listParty.isPresent()) {
			for (Party partyDetails : listParty.get()) {
				switch (partyDetails.getPartyDetails().getRole()) {
				case "DSVOperator":
					handlePartyDetails(map, partyDetails, "dsvOperator");
					break;
				case "FreightForwarder":
					handlePartyDetails(map, partyDetails, "freightForwarder");
					break;
				case "Consignor":
					handlePartyDetails(map, partyDetails, "consignor");
					break;
				case "ShipTo":
					handlePartyDetails(map, partyDetails, "shipTo");
					break;
				case "Despatch":
					handlePartyDetails(map, partyDetails, "despatch");
					break;
				case "Delivery":
					handlePartyDetails(map, partyDetails, "delivery");
					break;
				case "ShipFrom":
					handlePartyDetails(map, partyDetails, "shipFrom");
					break;
				case "DeliveryLocalCartage":
					handlePartyDetails(map, partyDetails, "deliveryLocalCartage");
					break;
				case "Consignee":
					handlePartyDetails(map, partyDetails, "consignee");
					break;
				default:
				}
			}
		}

		List<DsvFields> dsvLines = new ArrayList<>();
		Optional<List<GoodsItem>> listGoodItem = Optional.ofNullable(msg.getShipment()).map(sp -> sp.getGoodsItems())
				.map(gi -> gi.getGoodsItem());
		if (listGoodItem.isPresent()) {
			DsvFields dsvFields = new DsvFields();
			for (GoodsItem item : listGoodItem.get()) {
				DsvSubLine dsvSubLine = new DsvSubLine();
				dsvSubLine.setQty(Optional.ofNullable(item.getItem().getPackageQty()).orElse(" "));
				dsvSubLine.setType(Optional.ofNullable(item.getItem().getPackageType()).orElse(" "));

				if (item.getMeasurements() != null && item.getMeasurements().getMeasurement().size() != 0
						&& item.getMeasurements().getMeasurement() != null) {
					for (int i = 0; i < item.getMeasurements().getMeasurement().size(); i++) {
						if (item.getMeasurements().getMeasurement().get(i).getType().equalsIgnoreCase("GrossWeight")) {
							dsvSubLine.setWeight(
									Optional.ofNullable(item.getMeasurements().getMeasurement().get(i).getMeasure())
											.orElse(""));
							dsvSubLine.setWeightUom(
									item.getMeasurements().getMeasurement().get(i).getUom().equalsIgnoreCase("KGM")
											? "KG"
											: Optional
													.ofNullable(item.getMeasurements().getMeasurement().get(i).getUom())
													.orElse(""));
						} else if (item.getMeasurements().getMeasurement().get(i).getType().equals("Volume")) {
							dsvSubLine.setVolume(item.getMeasurements().getMeasurement().get(i).getMeasure());
							dsvSubLine.setVolumeUom(
									item.getMeasurements().getMeasurement().get(i).getUom().equalsIgnoreCase("MTQ")
											? "M3"
											: Optional
													.ofNullable(item.getMeasurements().getMeasurement().get(i).getUom())
													.orElse(""));
						}
					}
				}
				Optional<DimensionsList> dimension = Optional.ofNullable(item.getDimensions());
				if (dimension.isPresent()) {
					for (int i = 0; i < item.getDimensions().getDimension().size(); i++) {
						dsvSubLine.setLength(
								Optional.ofNullable(item.getDimensions().getDimension().get(i).getLength()).orElse(""));
						dsvSubLine.setWidth(
								Optional.ofNullable(item.getDimensions().getDimension().get(i).getWidth()).orElse(""));
						dsvSubLine.setHeight(
								Optional.ofNullable(item.getDimensions().getDimension().get(i).getHeight()).orElse(""));
						dsvSubLine.setHeightUom(
								item.getDimensions().getDimension().get(i).getUom().equalsIgnoreCase("CMT") ? "CM"
										: Optional.ofNullable(item.getDimensions().getDimension().get(i).getUom())
												.orElse(""));
					}
				}
				dsvSubLine.setUndg("-");
				dsvFields.getDsvLines().add(dsvSubLine);

				map.put(DsvPodConstant.GOOD_ITEM_NUMBER,
						Optional.ofNullable(item.getItem().getItemNumber()).orElse(" "));
				map.put(DsvPodConstant.GOOD_PACKAGE_QTY,
						Optional.ofNullable(item.getItem().getPackageQty()).orElse(" "));
				map.put(DsvPodConstant.GOOD_PACKAGE_TYPE,
						Optional.ofNullable(item.getItem().getPackageType()).orElse(" "));
				
				if( item.getProductIds() != null && item.getProductIds().getProductId() != null && item.getProductIds().getProductId().size()>0 ) {
				map.put(DsvPodConstant.COMMODITY,
						Optional.ofNullable(item.getProductIds().getProductId().get(0).getItemId()).orElse(" "));
				}

			}
			dsvLines = Arrays.asList(dsvFields);

			String goodsItemJson = convertGoodsItemToJson(dsvLines);
			map.put(DsvPodConstant.LIST_GOOD_ITEM, goodsItemJson);
			
			//
	        Map<String, Integer> totalQtyByType = dsvFields.getDsvLines().stream()
	                .collect(Collectors.groupingBy(DsvSubLine::getType, 
	                                               Collectors.summingInt(s -> NumberUtil.toInteger(s.getQty())) ));

            StringBuilder packages = new StringBuilder();
            totalQtyByType.forEach((type, totalQty) -> {
                packages.append(totalQty).append(" ").append(type).append(" ");
            });

            if (packages.length() > 0) {
                packages.setLength(packages.length() - 1);
            }
			map.put(DsvPodConstant.PACKAGES, packages.toString());
		}

		List<DsvFieldSea> dsvLinesSea = new ArrayList<>();
		Optional<List<EquipmentUnit>> equipList = Optional.ofNullable(msg.getShipment())
				.map(sp -> sp.getEquipmentUnits()).map(eu -> eu.getEquipmentUnit());
		if (equipList.isPresent()) {
			map.put(DsvPodConstant.UNIT_ID, Optional.ofNullable(equipList.get().get(0).getEquipmentType().getUnitId()).orElse(""));
			DsvFieldSea dsvFieldSea = new DsvFieldSea();
			for (EquipmentUnit unit : equipList.get()) {
				DsvSublineSea dsvSublineSea = new DsvSublineSea();
				dsvSublineSea.setUnitId(Optional.ofNullable(unit.getEquipmentType().getUnitId()).orElse(""));
				dsvSublineSea.setSealId(Optional.ofNullable(unit.getSealIds().getSeal().get(0).getSealId()).orElse(""));
				dsvSublineSea.setCount("1");
				dsvSublineSea.setIsoCode(Optional.ofNullable(unit.getEquipmentType().getISOCode()).orElse(""));
				for (GoodsItem item : listGoodItem.get()) {
					dsvSublineSea.setQty(Optional.ofNullable(item.getItem().getPackageQty()).orElse(""));
					dsvSublineSea.setType(Optional.ofNullable(item.getItem().getPackageType()).orElse(""));

					Optional<List<Measurement>> listMean = Optional.ofNullable(item.getMeasurements())
							.map(ms -> ms.getMeasurement());
					if (listMean.isPresent()) {
						for (Measurement means : listMean.get()) {
							if (means.getType().equalsIgnoreCase("GrossWeight")) {
								dsvSublineSea.setWeight(means.getMeasure());
								dsvSublineSea.setWeightUom(means.getUom().equalsIgnoreCase("KGM") ? "KG"
										: Optional.ofNullable(means.getUom()).orElse(""));
							} else if (means.getType().equalsIgnoreCase("Volume")) {
								dsvSublineSea.setVolume(means.getMeasure());
								dsvSublineSea.setVolumeUom(means.getUom().equalsIgnoreCase("MTQ") ? "M3"
										: Optional.ofNullable(means.getUom()).orElse(""));
							}
						}
					}
					Optional<DimensionsList> dimension = Optional.ofNullable(item.getDimensions());
					if (dimension.isPresent()) {
						for (Dimension dimen : dimension.get().getDimension()) {
							dsvSublineSea.setLength(Optional.ofNullable(dimen.getLength()).orElse(""));
							dsvSublineSea.setWidth(Optional.ofNullable(dimen.getWidth()).orElse(""));
							dsvSublineSea.setHeight(Optional.ofNullable(dimen.getHeight()).orElse(""));
							dsvSublineSea.setHeightUom(dimen.getUom().equalsIgnoreCase("CMT") ? "CM"
									: Optional.ofNullable(dimen.getUom()).orElse(""));
						}
					}
					dsvSublineSea.setUndg("-");
				}
				dsvFieldSea.getDsvSeaLines().add(dsvSublineSea);
			}
			dsvLinesSea = Arrays.asList(dsvFieldSea);

			String equipUnitJson = conversEquipUnitToJson(dsvLinesSea);
			map.put(DsvPodConstant.LIST_EQUIP_UNIT, equipUnitJson);
		}

		return map;
	}

	private String conversEquipUnitToJson(List<DsvFieldSea> dsvLinesSea) {
		try {
			Gson gson = new Gson();
			return gson.toJson(dsvLinesSea);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String convertGoodsItemToJson(List<DsvFields> dsvLines) {
		try {
			Gson gson = new Gson();
			return gson.toJson(dsvLines);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void handlePartyDetails(Map<String, String> map, Party partyDetails, String role) {
		map.put(role + "Id", Optional.ofNullable(partyDetails.getPartyDetails().getId()).orElse(""));
		map.put(role + "Role", Optional.ofNullable(partyDetails.getPartyDetails().getRole()).orElse(""));
		map.put(role + "PartyName", Optional.ofNullable(partyDetails.getPartyDetails().getPartyName()).orElse(""));
		map.put(role + "AddressLine1",
				Optional.ofNullable(partyDetails.getPartyDetails().getAddressLine1()).orElse(""));
		map.put(role + "AddressLine2",
				Optional.ofNullable(partyDetails.getPartyDetails().getAddressLine2()).orElse(""));
		map.put(role + "AddressLine3",
				Optional.ofNullable(partyDetails.getPartyDetails().getAddressLine3()).orElse(""));
		map.put(role + "Assignor", Optional.ofNullable(partyDetails.getPartyDetails().getAssignor()).orElse(""));
		map.put(role + "CityName", Optional.ofNullable(partyDetails.getPartyDetails().getCityName()).orElse(""));
		map.put(role + "CountryCode", Optional.ofNullable(partyDetails.getPartyDetails().getCountryCode()).orElse(""));
		map.put(role + "PostCode", Optional.ofNullable(partyDetails.getPartyDetails().getPostCode()).orElse(""));
		map.put(role + "State", Optional.ofNullable(partyDetails.getPartyDetails().getState()).orElse(""));

		Optional<List<Contact>> listCon = Optional.ofNullable(partyDetails.getContact());
		if (listCon.isPresent()) {
			for (Contact contact : listCon.get()) {
				map.put(role + "Email", Optional.ofNullable(contact.getEmail()).orElse(""));
				map.put(role + "Name", Optional.ofNullable(contact.getName()).orElse(""));
				map.put(role + "Phone", Optional.ofNullable(contact.getPhone()).orElse(""));
			}
		}
	}

	private void handleFreeText(Map<String, String> map, FreeText freeText, String role) {
		map.put(role + "DescriptionCode", Optional.ofNullable(freeText.getDescriptionCode()).orElse(" "));
		map.put(role + "TextLine1", Optional.ofNullable(freeText.getTextLine1()).orElse(""));
		map.put(role + "TextLine2", Optional.ofNullable(freeText.getTextLine2()).orElse(""));
		map.put(role + "TextLine3", Optional.ofNullable(freeText.getTextLine3()).orElse(""));
		map.put(role + "TextLine4", Optional.ofNullable(freeText.getTextLine4()).orElse(""));
		map.put(role + "TextLine5", Optional.ofNullable(freeText.getTextLine5()).orElse(""));
	}

	private void handleControlTotal(Map<String, String> map, ControlTotal controlTotal, String role) {
		map.put(role + "Quantity", Optional.ofNullable(controlTotal.getQuantity()).orElse(""));
		map.put(role + "Uom", controlTotal.getUom().equalsIgnoreCase("KGM") ? "KG"
				: Optional.ofNullable(controlTotal.getUom()).orElse(""));
	}

	private void handleTransportStage(Map<String, String> map, TransportStage transportStage, String role) {
		map.put(role + "StateCarrierId", Optional.ofNullable(transportStage.getStage().getCarrierId()).orElse(""));
		map.put(role + "StateCarrierName", Optional.ofNullable(transportStage.getStage().getCarrierName()).orElse(""));
		map.put(role + "StateModeOfTransport",
				Optional.ofNullable(transportStage.getStage().getModeOfTransport()).orElse(""));
		map.put(role + "StateTransportId", Optional.ofNullable(transportStage.getStage().getTransportId()).orElse(""));
		map.put(role + "StateTransportMeansName",
				Optional.ofNullable(transportStage.getStage().getTransportMeansName()).orElse(""));
		map.put(role + "StateTransportMeansNationality",
				Optional.ofNullable(transportStage.getStage().getTransportMeansNationality()).orElse(""));
		map.put(role + "StateTransportType",
				Optional.ofNullable(transportStage.getStage().getTransportType()).orElse(""));

	}

	private void handleLocation(Map<String, String> map, Location location, String role) {
		map.put(role + "LocationIdentifier", location.getLocationIdentification().get(0).getLocationIdentifier());
		map.put(role + "LocationName", location.getLocationIdentification().get(0).getLocationName());
		map.put(role + "Type", location.getLocationIdentification().get(0).getType());

	}

	private void handleReferenceGroup(Map<String, String> map, ReferenceGroup referenceGroup, String role) {
		map.put(role + "CustomFieldType",
				Optional.ofNullable(referenceGroup.getReference().getCustomFieldType()).orElse(""));
		map.put(role + "Identifier", Optional.ofNullable(referenceGroup.getReference().getIdentifier()).orElse(""));
		map.put(role + "Value", Optional.ofNullable(referenceGroup.getReference().getValue()).orElse(""));

	}
	
	private static SimpleDateFormat sdf14 = new SimpleDateFormat("yyyyMMddHHmmss");
	private static SimpleDateFormat sdf12 = new SimpleDateFormat("yyyyMMddHHmm");
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
	
	/**
	 * Convert yyyyMMddHHmmss to dd-MMM-yy
	 * @param dateStr
	 * @return
	 */
	private String convertDateFormat(String dateStr) {
		
		if(StringUtils.isBlank(dateStr)) {
			return dateStr;
		}
		try {
			if( dateStr.trim().length() == 14) {
				return sdf.format(sdf14.parse(dateStr));
			}
			return sdf.format(sdf12.parse(dateStr));
		} catch(Exception e) {
			log.error("",e);
		}
		return dateStr;
	}
}
