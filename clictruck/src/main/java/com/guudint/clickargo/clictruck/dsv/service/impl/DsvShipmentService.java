package com.guudint.clickargo.clictruck.dsv.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.jfree.util.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsv.edi.xml.DSV_ShipmentMessage_v1.Contact;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.DSVShipmentMessage;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.Dimension;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.FreeText;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.GoodsItem;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.HandlingInstruction;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.Measurement;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.PackageId;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.Party;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.PartyDetails;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.Reference;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.ReferenceGroup;
import com.dsv.edi.xml.DSV_ShipmentMessage_v1.TransportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clicdo.common.IClicTruckConstant;
import com.guudint.clicdo.common.service.CkCtCommonService;
import com.guudint.clickargo.clictruck.common.constant.CkCtLocationConstant;
import com.guudint.clickargo.clictruck.common.constant.CkCtLocationConstant.LocationId;
import com.guudint.clickargo.clictruck.common.dao.CkCtLocationDao;
import com.guudint.clickargo.clictruck.common.model.TCkCtLocation;
import com.guudint.clickargo.clictruck.constant.TruckJobSourceEnum;
import com.guudint.clickargo.clictruck.dsv.dao.CkCtShipmentDao;
import com.guudint.clickargo.clictruck.dsv.dto.DsvMstShipmentStateEnum;
import com.guudint.clickargo.clictruck.dsv.model.TCkCtShipment;
import com.guudint.clickargo.clictruck.master.dao.CkCtMstPartyTypeDao;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstCargoType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstLocationType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstPartyType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstVehType;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkCtContactDetailDao;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkCtPartyDao;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckDao;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckExtDao;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtContactDetail;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtParty;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkJobTruckServiceUtil;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripCargoMmDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripChargeDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDoDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripLocationDao;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripCargoMm;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripCharge;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripDo;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripLocation;
import com.guudint.clickargo.clictruck.track.service.TrackTraceCoordinateService;
import com.guudint.clickargo.clictruck.util.NumberUtil;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.dao.CkRecordDateDao;
import com.guudint.clickargo.common.enums.CargoTypes;
import com.guudint.clickargo.common.model.TCkRecordDate;
import com.guudint.clickargo.job.dao.CkJobDao;
import com.guudint.clickargo.job.model.TCkJob;
import com.guudint.clickargo.manageaccn.dao.CkCoreAccnDao;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.JobStates;
import com.guudint.clickargo.master.enums.JobTypes;
import com.guudint.clickargo.master.enums.ShipmentTypes;
import com.guudint.clickargo.master.model.TCkMstJobState;
import com.guudint.clickargo.master.model.TCkMstJobType;
import com.guudint.clickargo.master.model.TCkMstShipmentType;
import com.guudint.clickargo.util.CkDateUtil;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.audit.model.TCoreAuditlog;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.config.dao.CoreSysparamDao;
import com.vcc.camelone.config.model.TCoreSysparam;
import com.vcc.camelone.util.common.CommonUtil;
import com.vcc.camelone.util.email.SysParam;
import com.vcc.camelone.util.sftp.model.SFTPConfig;

@Service
public class DsvShipmentService {

	private static Logger log = Logger.getLogger(DsvShipmentService.class);

	private static SimpleDateFormat yyyyMMddHHmmssSDF = new SimpleDateFormat("yyyyMMddHHmmss");
	private static SimpleDateFormat yyyyMMddSDF = new SimpleDateFormat("yyyyMMdd");

	public static final String LOC_DEPARTURE = "Departure";
	public static final String LOC_DESTINATION = "Destination";
	public static final String LOC_PLACEOFLOADING = "PlaceOfLoading";

	public static final String MODE_TRANSPORT_AIR = "Air";
	public static final String MODE_TRANSPORT_SEA = "Sea";

	public static final List<String> EVENT_CODES_FILTER = Arrays.asList("CCI", "Z05", "DDA");

	public static final String PARTY_ROLE_DELIVERYLOCALCARTAGE = "DeliveryLocalCartage";
	public static final String PARTY_ROLE_PICKUPLOCALCARTAGE = "PickupLocalCartage";

	private static final String JOB_ID_SEPARATE = "-";

	@Autowired
	CkRecordDateDao ckRecordDateDao;
	@Autowired
	CkJobDao ckJobDao;

	@Autowired
	CkCtContactDetailDao ckCtContactDetailDao;
	@Autowired
	CkJobTruckDao jobTruckDao;

	// CkCtLocationDao ckCtLocationDao;
	@Autowired
	CkCtTripLocationDao ckCtTripLocationDao;
	@Autowired
	CkCtLocationDao ckCtLocationDao;
	@Autowired
	CkCtTripChargeDao ckCtTripChargeDao;
	@Autowired
	CkCtTripDao ckCtTripDao;
	@Autowired
	CkCtTripCargoMmDao ckCtTripCargoMmDao;
	@Autowired
	CkCtShipmentDao shipmentDao;
	@Autowired
	CkJobTruckExtDao ckJobTruckExtDao;
	@Autowired
	CoreSysparamDao coreSysparamDao;

	@Autowired
	TrackTraceCoordinateService coordinateService;

	@Autowired
	protected SysParam sysParam;
	@Autowired
	private CkJobTruckServiceUtil ckJobTruckUtilService;
	@Autowired
	private CkCtPartyDao ckCtParty;
	@Autowired
	private CkCtMstPartyTypeDao mstPartyTypeDao;
	@Autowired
	private CkCoreAccnDao ckCoreAccnDao;

	@Autowired
	private CkCtCommonService ckCtCommonService;
	@Autowired
	private CkCtTripDoDao ckCtTripDoDao;
	@Autowired
	private GenericDao<TCoreAuditlog, String> auditLogDao;

	protected Date getDocumentDate(DSVShipmentMessage msg) {

		try {
			return yyyyMMddHHmmssSDF.parse(msg.getHeader().getDocumentDate().getDateTime());

		} catch (Exception e) {
			return null;
		}
	}

	protected ShipmentTypes getShipmentType(DSVShipmentMessage msg) throws Exception {

		Optional<com.dsv.edi.xml.DSV_ShipmentMessage_v1.TransportStage> tsOpt = this.getMainCarriageTransportStage(msg);

		if (tsOpt.isPresent()) {
			Optional<com.dsv.edi.xml.DSV_ShipmentMessage_v1.Location> departureLoc = tsOpt.get().getLocations()
					.getLocation().stream().filter(loc -> "Departure".equalsIgnoreCase(loc.getType())).findFirst();

			if (departureLoc.isPresent() && "SGSIN"
					.equalsIgnoreCase(departureLoc.get().getLocationIdentification().get(0).getLocationIdentifier())) {
				return ShipmentTypes.EXPORT;
			}
		}

		if (tsOpt.isPresent()) {
			Optional<com.dsv.edi.xml.DSV_ShipmentMessage_v1.Location> departureLoc = tsOpt.get().getLocations()
					.getLocation().stream().filter(loc -> "Destination".equalsIgnoreCase(loc.getType())).findFirst();

			if (departureLoc.isPresent() && "SGSIN"
					.equalsIgnoreCase(departureLoc.get().getLocationIdentification().get(0).getLocationIdentifier())) {
				return ShipmentTypes.IMPORT;
			}
		}

		return null;
	}

	protected JobTypes getJobType(ShipmentTypes shipType) {

		switch (shipType) {
		case IMPORT:
			return JobTypes.TRKI;
		default:
			return JobTypes.TRKO;
		}
	}

	protected String getShipperReference(DSVShipmentMessage msg) {

		Optional<Reference> reOpt = msg.getShipment().getReferences().getReferenceGroup().stream()
				.map(rg -> rg.getReference()).filter(refer -> "ShipperReference".equalsIgnoreCase(refer.getType()))
				.findFirst();

		if (reOpt.isPresent()) {
			return reOpt.get().getValue();
		} else {
			return msg.getHeader().getMessageId();
		}
		// return "";
	}

	protected String getOrderRef(DSVShipmentMessage msg) {

		List<String> orderRefList = msg.getShipment().getReferences().getReferenceGroup().stream()
				.map(rg -> rg.getReference()).filter(refer -> "OrderReference".equalsIgnoreCase(refer.getType()))
				.map(ref -> ref.getValue()).collect(Collectors.toList());

		String orderRef = StringUtils.join(orderRefList, ',');

		if (orderRef.length() > 250) {
			orderRef = orderRef.substring(0, 250);
		}

		if (StringUtils.isBlank(orderRef)) {
			orderRef = msg.getHeader().getMessageId();
		}

		return orderRef;
	}

	protected Party getPartyByRole(DSVShipmentMessage msg, String roleName) {

		try {
			List<Party> partyList = msg.getShipment().getParties().getParty();

			for (Party party : partyList) {
				if (roleName.equalsIgnoreCase(party.getPartyDetails().getRole())) {
					return party;
				}
			}
		} catch (Exception e) {
			Log.error("Fail to get Party: " + roleName, e);
		}

		return null;
	}

	/**
	 * 
	 * @param locType: Departure, Destination, PlaceOfLoading
	 * @return
	 */
	@Deprecated
	protected String getLocationAddress(DSVShipmentMessage msg, String locType) {

		Optional<com.dsv.edi.xml.DSV_ShipmentMessage_v1.TransportStage> transportStage = msg.getShipment()
				.getTransportStages().getTransportStage().stream()
				.filter(ts -> "MainCarriage".contentEquals(ts.getStage().getType())).findFirst();

		if (transportStage.isPresent()) {

			if (LOC_DEPARTURE.equalsIgnoreCase(locType)) {

				Optional<com.dsv.edi.xml.DSV_ShipmentMessage_v1.Location> loc = transportStage.get().getLocations()
						.getLocation().stream().filter(l -> "Departure".equalsIgnoreCase(l.getType())).findFirst();

				if (loc.isPresent()) {
					return loc.get().getLocationIdentification().get(0).getLocationName();
				}
			}
			if (LOC_DESTINATION.equalsIgnoreCase(locType)) {

				Optional<com.dsv.edi.xml.DSV_ShipmentMessage_v1.Location> loc = transportStage.get().getLocations()
						.getLocation().stream().filter(l -> "Destination".equalsIgnoreCase(l.getType())).findFirst();

				if (loc.isPresent()) {
					return loc.get().getLocationIdentification().get(0).getLocationName();
				}
			}
			if (LOC_PLACEOFLOADING.equalsIgnoreCase(locType)) {

				Optional<com.dsv.edi.xml.DSV_ShipmentMessage_v1.Location> loc = transportStage.get().getLocations()
						.getLocation().stream().filter(l -> "PlaceOfLoading".equalsIgnoreCase(l.getType())).findFirst();

				if (loc.isPresent()) {
					return loc.get().getLocationIdentification().get(0).getLocationName();
				}
			}
		}
		return null;
	}

	protected Contact getPartyContactByRole(DSVShipmentMessage msg, String roleName) {

		Party partyTO = this.getPartyByRole(msg, roleName);
		if (null != partyTO && partyTO.getContact() != null && partyTO.getContact().size() > 0) {
			return partyTO.getContact().get(0);
		}

		return null;
	}

	protected Date getDepartureTime(DSVShipmentMessage msg) {

		String departureTime = null;

		List<com.dsv.edi.xml.DSV_ShipmentMessage_v1.TransportStage> transportStageList = msg.getShipment()
				.getTransportStages().getTransportStage().stream()
				.filter(ts -> "MainCarriage".contentEquals(ts.getStage().getType())).collect(Collectors.toList());

		Optional<com.dsv.edi.xml.DSV_ShipmentMessage_v1.Date> actualArrivalDate = transportStageList.stream()
				.map(ts -> ts.getDates()).flatMap(d -> d.getDate().stream())
				.filter(d -> "ActualDeparture".equalsIgnoreCase(d.getType())).findFirst();

		if (actualArrivalDate.isPresent()) {
			departureTime = actualArrivalDate.get().getDateTime();
		}

		if (StringUtils.isBlank(departureTime)) {

			Optional<com.dsv.edi.xml.DSV_ShipmentMessage_v1.Date> scheduledDepartureDate = transportStageList.stream()
					.map(ts -> ts.getDates()).flatMap(d -> d.getDate().stream())
					.filter(d -> "ScheduledDeparture".equalsIgnoreCase(d.getType())).findFirst();

			if (scheduledDepartureDate.isPresent()) {
				departureTime = scheduledDepartureDate.get().getDateTime();
			}
		}

		if (StringUtils.isNoneBlank(departureTime)) {
			try {
				return yyyyMMddHHmmssSDF.parse(departureTime);
			} catch (ParseException e) {
				Log.error("Fail to format DepartureTime: " + departureTime, e);
			}
		}
		return null;
	}

	protected String getEventCode(DSVShipmentMessage shipMsg) {

		// String longEvenCode =
		Optional<List<ReferenceGroup>> rgListOpt = this.getListReferenceGroup(shipMsg);

		if (rgListOpt.isPresent()) {
			String longEventCode = rgListOpt.get().stream().map(rg -> rg.getReference())
					.filter(ref -> "EventCode".equalsIgnoreCase(ref.getType())).map(ref -> ref.getValue()).findFirst()
					.orElse("");
			String[] eventCodeArray = longEventCode.split("/");
			return eventCodeArray[eventCodeArray.length - 1]; // get Lastone;
		}

		return "";
	}

	protected String getHouseBill(DSVShipmentMessage shipMsg) {

		Optional<List<ReferenceGroup>> rgListOpt = this.getListReferenceGroup(shipMsg);

		if (rgListOpt.isPresent()) {
			String houseBill = rgListOpt.get().stream().map(rg -> rg.getReference())
					.filter(ref -> "HouseBill".equalsIgnoreCase(ref.getType())).map(ref -> ref.getValue()).findFirst()
					.orElse("");
			return houseBill;
		}

		return "";
	}

	protected String getMasterBill(DSVShipmentMessage shipMsg) {

		Optional<com.dsv.edi.xml.DSV_ShipmentMessage_v1.TransportStage> tsOpt = this
				.getMainCarriageTransportStage(shipMsg);

		if (tsOpt.isPresent()) {
			Optional<com.dsv.edi.xml.DSV_ShipmentMessage_v1.Reference> optRef = tsOpt.get().getReferences()
					.getReferenceGroup().stream()
					.filter(refGroup -> "MasterBill".equalsIgnoreCase(refGroup.getReference().getType()))
					.map(refGroup -> refGroup.getReference()).findFirst();

			if (optRef.isPresent()) {
				return optRef.get().getValue();
			}
		}

		return "";
	}

	protected String getServiceCode(DSVShipmentMessage shipMsg) {

		Optional<List<TransportService>> tsListOpt = getListTransportService(shipMsg);

		if (tsListOpt.isPresent()) {
			return tsListOpt.get().stream().filter(ts -> ts.getServiceCode() != null).findFirst()
					.map(ts -> ts.getServiceCode()).get();
		}

		return "";
	}

	protected String getModeOfTransport(DSVShipmentMessage shipMsg) {

		Optional<com.dsv.edi.xml.DSV_ShipmentMessage_v1.TransportStage> tsOpt = this
				.getMainCarriageTransportStage(shipMsg);

		if (tsOpt.isPresent()) {
			return tsOpt.get().getStage().getModeOfTransport();
		}
		return "";
	}

	protected Date getIssueDate(DSVShipmentMessage shipMsg) throws ParseException {
		return yyyyMMddHHmmssSDF.parse(shipMsg.getHeader().getDocumentDate().getDateTime());
	}

	protected Optional<String> getActualDelivery(DSVShipmentMessage shipMsg) {
		return shipMsg.getShipment().getDates().getDate().stream().filter(d -> "ActualDelivery".equals(d.getType()))
				.map(d -> d.getDateTime()).findFirst();
	}

	protected Date getScheduledDeliveryDate(DSVShipmentMessage shipMsg) {
		try {
			Optional<String> planDateStrOpt = shipMsg.getShipment().getDates().getDate().stream()
					.filter(d -> "ScheduledDelivery".equals(d.getType())).map(d -> d.getDateTime()).findFirst();

			if (planDateStrOpt.isPresent()) {
				return yyyyMMddHHmmssSDF.parse(planDateStrOpt.get());
			}
		} catch (Exception e) {
		}
		return new Date();
	}

	protected Date getDateByType(DSVShipmentMessage shipMsg, String dateType) {
		try {
			Optional<String> planDateStrOpt = shipMsg.getShipment().getDates().getDate().stream()
					.filter(d -> dateType.equals(d.getType())).map(d -> d.getDateTime()).findFirst();

			if (planDateStrOpt.isPresent()) {
				return yyyyMMddHHmmssSDF.parse(planDateStrOpt.get());
			}
		} catch (Exception e) {
		}
		return new Date();
	}

	protected Optional<String> getActualCollection(DSVShipmentMessage shipMsg) {

		return shipMsg.getShipment().getDates().getDate().stream().filter(d -> "ActualCollection".equals(d.getType()))
				.map(d -> d.getDateTime()).findFirst();
	}

	protected List<TCkCtParty> getAllParties(DSVShipmentMessage shipMsg) {

		List<Party> partyList = shipMsg.getShipment().getParties().getParty();
		List<TCkCtParty> ckCtParties = new ArrayList<>();

		for (Party party : partyList) {

			PartyDetails partyDetails = party.getPartyDetails();

			TCkCtParty ckParty = new TCkCtParty();
			ckParty.setTCkCtMstPartyType(new TCkCtMstPartyType(partyDetails.getRole().toUpperCase()));
			ckParty.setPtyId(CkUtil.generateId(TCkCtParty.PREFIX_ID));
			ckParty.setPtyXmlId(partyDetails.getId());
			ckParty.setPtyName(partyDetails.getPartyName());
			ckParty.setPtyAddr1(partyDetails.getAddressLine1());
			ckParty.setPtyAddr2(partyDetails.getAddressLine2());
			ckParty.setPtyAddr3(partyDetails.getAddressLine3());
			ckParty.setPtyPcode(partyDetails.getPostCode());
			ckParty.setPtyCity(partyDetails.getCityName());
			ckParty.setPtyCtycode(partyDetails.getCountryCode()); // country code

			ckParty.setPtyStatus(Constant.ACTIVE_STATUS);
			ckParty.setPtyUidCreate("sys");
			ckParty.setPtyDtCreate(new Date());

			ckCtParties.add(ckParty);
		}

		return ckCtParties;
	}

	protected DsvPickUp getPickUpLocation(DSVShipmentMessage shipMsg) throws Exception {

		String pickUpName = "";
		String pickUpAddress = "";
		String SERVICE_CODE_LCL = "LCL";

		ShipmentTypes shipmentType = this.getShipmentType(shipMsg);
		String modeOfTransport = this.getModeOfTransport(shipMsg);
		String serviceCode = this.getServiceCode(shipMsg);

		//
		if (ShipmentTypes.IMPORT == shipmentType && MODE_TRANSPORT_AIR.equalsIgnoreCase(modeOfTransport)) {
			pickUpName = "InterOffice Dummy SIN - AIR";
			pickUpAddress = "119 Airport Cargo Road Cargo Agents Megaplex 1 #03-08 (3M) Singapore 819454 REP. OF SINGAPORE";
		}

		//
		if (ShipmentTypes.IMPORT == shipmentType && MODE_TRANSPORT_SEA.equalsIgnoreCase(modeOfTransport)) {
			if (SERVICE_CODE_LCL.equalsIgnoreCase(serviceCode)) {
				// LCL
				pickUpName = "A&T CFS PTE. LTD";
				pickUpAddress = "Blk 513 Kampong Bahru Road #01-122 Keppel Distripark Singapore 099449 REP. OF SINGAPORE";
			} else {
				// not LCL
				pickUpName = "PORT OF SINGAPORE";
				pickUpAddress = "SINGAPORE REP. OF SINGAPORE";
			}
		}

		// IMPORT, SEA, LCL Ship to
		if (ShipmentTypes.IMPORT == shipmentType && MODE_TRANSPORT_SEA.equalsIgnoreCase(modeOfTransport)
				&& SERVICE_CODE_LCL.equalsIgnoreCase(serviceCode)) {

			Party party = this.getPartyByRole(shipMsg, "ShipTo");

			if (party != null && party.getPartyDetails() != null) {
				pickUpName = party.getPartyDetails().getPartyName();
				pickUpAddress = this.getAddressByParty(party.getPartyDetails());
			}
		}

		// Export, Despatch
		if (ShipmentTypes.EXPORT == shipmentType) {
			Party party = this.getPartyByRole(shipMsg, "Despatch");
			if (party != null && party.getPartyDetails() != null) {
				pickUpName = party.getPartyDetails().getPartyName();
				pickUpAddress = this.getAddressByParty(party.getPartyDetails());
			}
		}

		return new DsvPickUp(pickUpName, pickUpAddress);
	}

	protected DsvDropOff getDropOffLocation(DSVShipmentMessage shipMsg) throws Exception {

		String dropOffName = "";
		String dropOffAddress = "";
		String SERVICE_CODE_LCL = "LCL";
		String SERVICE_CODE_FCL = "FCL";
		String SERVICE_CODE_LSE = "LSE";

		ShipmentTypes shipmentType = this.getShipmentType(shipMsg);
		String modeOfTransport = this.getModeOfTransport(shipMsg);
		String serviceCode = this.getServiceCode(shipMsg);

		// Export
		if (ShipmentTypes.EXPORT == shipmentType) {
			if (SERVICE_CODE_FCL.equalsIgnoreCase(serviceCode)) {
				// FCL
				dropOffName = "PORT OF SINGAPORE";
				dropOffAddress = "SINGAPORE REP. OF SINGAPORE";
			} else if (SERVICE_CODE_LCL.equalsIgnoreCase(serviceCode)) {
				// LCL
				dropOffName = "A&T CFS PTE. LTD";
				dropOffAddress = "Blk 513 Kampong Bahru Road #01-122 Keppel Distripark Singapore 099449 REP. OF SINGAPORE";
			}
		}

		// import or(export and LSE)
		if (ShipmentTypes.IMPORT == shipmentType
				|| (ShipmentTypes.EXPORT == shipmentType && SERVICE_CODE_LSE.equalsIgnoreCase(serviceCode))) {
			// Delivery
			Party party = this.getPartyByRole(shipMsg, "Delivery");

			if (party != null && party.getPartyDetails() != null) {
				dropOffName = party.getPartyDetails().getPartyName();
				dropOffAddress = this.getAddressByParty(party.getPartyDetails());
			}
		}

		// Export Sea,
		if (ShipmentTypes.EXPORT == shipmentType && MODE_TRANSPORT_SEA.equalsIgnoreCase(modeOfTransport)
				&& SERVICE_CODE_LCL.equalsIgnoreCase(serviceCode)) {
			// ShipFrom
			Party party = this.getPartyByRole(shipMsg, "ShipFrom");
			if (party != null && party.getPartyDetails() != null) {

				dropOffName = party.getPartyDetails().getPartyName();
				dropOffAddress = this.getAddressByParty(party.getPartyDetails());
			}
		}
		return new DsvDropOff(dropOffName, dropOffAddress);
	}

	@Transactional
	public TCkJobTruck createJob(DSVShipmentMessage msg) throws Exception {

		// prepare JobId, jobTruckId
		String jobId = null;
		String jobTruckId = null;

		String[] jobIdAndJobTruckid = this.getJobIdAndJobTruckId(msg.getHeader().getMessageId());
		if (jobIdAndJobTruckid != null && jobIdAndJobTruckid.length > 0) {

			jobId = jobIdAndJobTruckid[0];

			if (jobIdAndJobTruckid.length > 1) {
				jobTruckId = jobIdAndJobTruckid[1];
			}
		}

		// TCkJobTruck
		TCkJobTruck jobTruck = this.prepareJobTruck(msg, jobId, jobTruckId);

		ckRecordDateDao.add(jobTruck.getTCkJob().getTCkRecordDate());
		ckJobDao.add(jobTruck.getTCkJob());

		ckCtContactDetailDao.add(jobTruck.getTCkCtContactDetailByJobContactCoFf());
		ckCtContactDetailDao.add(jobTruck.getTCkCtContactDetailByJobContactTo());
		jobTruckDao.add(jobTruck);

		this.createTrip(msg, jobTruck);

		this.saveOrUpdateParty(this.getAllParties(msg));

		this.audit(jobTruck.getJobId(), "CREATED");

		return jobTruck;
	}

	@Transactional
	public TCkJobTruck updateJob(DSVShipmentMessage msg, String jobId, int version) throws Exception {

		// get job
		TCkJob ckJob = ckJobDao.find(jobId);
		if (null == ckJob) {
			throw new Exception("Fail to find job: " + jobId);
		}
		// get job truck.
		List<TCkJobTruck> jobTruckList = jobTruckDao.findByParentId(ckJob.getJobId());
		if (null == jobTruckList || jobTruckList.size() == 0) {
			throw new Exception("Fail to find jobTruck: " + ckJob.getJobId());
		}

		// update Job
		TCkJobTruck jobTruck = jobTruckList.get(0);
		Hibernate.initialize(jobTruck.getTCkJob());
		Hibernate.initialize(jobTruck.getTCkJob().getTCkRecordDate());
		ckJob = jobTruck.getTCkJob();
		TCkRecordDate recordDate = ckJob.getTCkRecordDate();

		// TCkJobTruck
		TCkJobTruck updatedJobTruck = this.prepareJobTruck(msg, null, null);

		// beanCopy job, truck,
		BeanUtils.copyProperties(updatedJobTruck.getTCkJob().getTCkRecordDate(), recordDate, "rcdId");

		BeanUtils.copyProperties(updatedJobTruck.getTCkJob(), ckJob, "jobId");

		BeanUtils.copyProperties(updatedJobTruck, jobTruck, "jobId", "TCkJob", "TCkCtContactDetailByJobContactCoFf",
				"TCkCtContactDetailByJobContactTo");

		BeanUtils.copyProperties(updatedJobTruck.getTCkCtContactDetailByJobContactCoFf(),
				jobTruck.getTCkCtContactDetailByJobContactCoFf(), "cdId");

		BeanUtils.copyProperties(updatedJobTruck.getTCkCtContactDetailByJobContactTo(),
				jobTruck.getTCkCtContactDetailByJobContactTo(), "cdId");

		ckJob.setTCkRecordDate(recordDate);

		// saveOrUpdate
		ckRecordDateDao.saveOrUpdate(recordDate);
		ckJobDao.saveOrUpdate(ckJob);

		ckCtContactDetailDao.saveOrUpdate(jobTruck.getTCkCtContactDetailByJobContactCoFf());
		ckCtContactDetailDao.saveOrUpdate(jobTruck.getTCkCtContactDetailByJobContactTo());
		jobTruckDao.saveOrUpdate(jobTruck);

		// delete trip
		ckJobTruckUtilService.removeExistingData(new CkJobTruck(jobTruck));
		// create trip
		this.createTrip(msg, jobTruck);

		this.saveOrUpdateParty(this.getAllParties(msg));

		this.audit(jobTruck.getJobId(), "UPDATED");

		return jobTruck;
	}

	/**
	 * 
	 * @param msgId
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public TCkCtShipment isUpdateJob(String msgId) throws Exception {

		List<TCkCtShipment> shipmentList = shipmentDao.fetchByShipmentId(msgId,
				DsvMstShipmentStateEnum.JOB_CREATED.name(), DsvMstShipmentStateEnum.JOB_UPDATED.name());

		if (shipmentList == null || shipmentList.size() == 0) {
			// didn't have shipment with JOB_CREATED or JOB_UPDATED status by
			// msgId/shimentId
			return null;
		}

		// Only get latest one record.
		TCkCtShipment shipment = shipmentList.get(0);
		TCkJob ckJob = shipment.getTCkJob();

		if (null != ckJob && null != ckJob.getTCkMstJobState()
				&& StringUtils.isNotBlank(ckJob.getTCkMstJobState().getJbstId())) {

			log.info("job status:" + ckJob.getTCkMstJobState().getJbstId());

			if (JobStates.ACP.name().equalsIgnoreCase(ckJob.getTCkMstJobState().getJbstId())) {
				// if ACCEPTED status, replace old existing CkJob.
				return shipment;
			}
		}
		// not Submit status, can't replace
		return null;
	}

	public String isIgnoreShipmentWhenMigration(DSVShipmentMessage shipMsg) throws Exception {

		TCoreSysparam scheduledShipmentArrivalParam = coreSysparamDao
				.find("CLICKTRUCK_DSV_CHECK_SCHEDULEDSHIPMENTARRIVAL");

		if (scheduledShipmentArrivalParam == null || !"Y".equalsIgnoreCase(scheduledShipmentArrivalParam.getSysVal())) {
			return null;
		}

		Optional<com.dsv.edi.xml.DSV_ShipmentMessage_v1.TransportStage> tsOpt = this
				.getMainCarriageTransportStage(shipMsg);

		if (tsOpt.isPresent() && tsOpt.get().getDates() != null && tsOpt.get().getDates().getDate() != null) {

			com.dsv.edi.xml.DSV_ShipmentMessage_v1.Date scheduledShipmentArrivalDate = tsOpt.get().getDates().getDate()
					.stream().filter(date -> "ScheduledShipmentArrival".equalsIgnoreCase(date.getType())).findFirst()
					.orElse(null);

			if (scheduledShipmentArrivalDate != null && scheduledShipmentArrivalDate.getDateTime() != null
					&& scheduledShipmentArrivalDate.getDateTime().length() >= 8) {

				
				Date date = new CkDateUtil().clearHourMintueSecond(null);
				
				if (date.compareTo(yyyyMMddSDF.parse(scheduledShipmentArrivalDate.getDateTime().substring(0, 8))) > 0) {

					return "Do not load XML job because ScheduledShipmentArrival("
							+ scheduledShipmentArrivalDate.getDateTime() + ") less than today: ";
				}
			}
		}
		return null;
	}

	public String isIgnoreShipment(DSVShipmentMessage shipMsg) throws Exception {

		// Case1 : If mode of transport is Air and event code not in ('CCI',
		// 'Z05','DDA')

		String modeOfTransport = this.getModeOfTransport(shipMsg);
		String eventCode = this.getEventCode(shipMsg);

		if (MODE_TRANSPORT_AIR.equalsIgnoreCase(modeOfTransport) && !EVENT_CODES_FILTER.contains(eventCode)) {
			return "R1: Don't create job: mode of transport is Air and event code not in ('CCI', 'Z05')";
		}

		// Case 2: If direction is Import and mode of transport is air and issue_date >
		// 2021-08-01
		// and has <Date Type="ActualDelivery" Format="DateTimeSec">

		ShipmentTypes shipType = getShipmentType(shipMsg);

		Date date20210801 = yyyyMMddSDF.parse("20210801");
		Date date20210701 = yyyyMMddSDF.parse("20210701");

		Date issueDate = this.getIssueDate(shipMsg);
		Optional<String> actualDelivery = this.getActualDelivery(shipMsg);
		Optional<String> actualCollection = this.getActualCollection(shipMsg);

		if (ShipmentTypes.IMPORT == shipType && MODE_TRANSPORT_AIR.equalsIgnoreCase(modeOfTransport)
				&& date20210801.compareTo(issueDate) < 0 && actualDelivery.isPresent()) {

			return "R2: Don't create job: IMPORT and Air and issue_date >2021-08-01 and has ActualDelivery.";
		}

		// Case 3: If direction is Import and mode of transport is sea and issue_date >
		// 2021-07-01
		// and has <Date Type="ActualDelivery" Format="DateTimeSec">
		if (ShipmentTypes.IMPORT == shipType && MODE_TRANSPORT_SEA.equalsIgnoreCase(modeOfTransport)
				&& date20210701.compareTo(issueDate) < 0 && actualDelivery.isPresent()) {

			return "R3: Don't create job: IMPORT and Sea and issue_date >2021-07-01 and has ActualDelivery.";
		}

		// Case 4: If direction is Export and mode of transport is Sea and issued_date >
		// 2021-07-01
		// and has <Date Type="ActualCollection" Format="DateTimeSec"> so the job wont
		// be created too

		if (ShipmentTypes.EXPORT == shipType && MODE_TRANSPORT_SEA.equalsIgnoreCase(modeOfTransport)
				&& date20210701.compareTo(issueDate) < 0 && actualCollection.isPresent()) {

			return "R4: Don't create job: EXPORT and Sea and issue_date >2021-07-01 and has ActualCollection.";
		}

		// skip Job if can't find TO
		try {
			this.getTOAccn(shipMsg);
		} catch (Exception e) {
			return "Fail to find TO account: " + e.getMessage();
		}

		return null;
	}

	public String getLocalDsvPath() throws IOException {

		String rootPath = ckCtCommonService.getCkCtAttachmentPathRoot();

		String parentPath = rootPath + File.separator + "fromDsv" + File.separator + yyyyMMddSDF.format(new Date());

		Files.createDirectories(Paths.get(parentPath));

		return parentPath;
	}

	public SFTPConfig getDsvSftpConfig() {

		try {
			String sftpDsv = sysParam.getValString("CLICTRUCK_SFTP_DSV", null);

			return (new ObjectMapper()).readValue(sftpDsv, SFTPConfig.class);

		} catch (Exception e) {
			Log.error("", e);
		}
		return null;
	}

	private String[] getJobIdAndJobTruckId(String msgId) throws Exception {

		// All shipment
		List<TCkCtShipment> shipmentList = shipmentDao.fetchByShipmentId(msgId);
		if (null == shipmentList || shipmentList.size() == 0) {
			return null;
		}

		// All shipment with JobId
		List<TCkCtShipment> shipmentJobIdList = shipmentList.stream()
				.filter(sp -> sp.getTCkJob() != null && StringUtils.isNotBlank(sp.getTCkJob().getJobId()))
				.collect(Collectors.toList());

		if (null == shipmentJobIdList || shipmentJobIdList.size() == 0) {
			return null;
		}

		//
		TCkCtShipment firstShipment = shipmentJobIdList.get(shipmentJobIdList.size() - 1);
		String jobId = firstShipment.getTCkJob().getJobId();
		jobId = jobId.split(JOB_ID_SEPARATE)[0];

		//
		String jobTruckId = null;

		List<TCkCtShipment> shipmentJobTruckIdList = shipmentJobIdList.stream()
				.filter(sp -> sp.getTCkJob() != null && StringUtils.isNotBlank(sp.getTCkJob().getJobId()))
				.collect(Collectors.toList());

		Collections.reverse(shipmentJobTruckIdList);

		for (TCkCtShipment sp : shipmentJobTruckIdList) {

			List<TCkJobTruck> jobTruckList = jobTruckDao.findByParentId(sp.getTCkJob().getJobId());

			if (jobTruckList != null && jobTruckList.size() > 0
					&& StringUtils.isNotBlank(jobTruckList.get(0).getJobId())) {
				jobTruckId = jobTruckList.get(0).getJobId();
				jobTruckId = jobTruckId.split(JOB_ID_SEPARATE)[0];
				break;
			}
		}

		int jobSeq = shipmentJobIdList.size();
		jobId = jobId + JOB_ID_SEPARATE + jobSeq;

		if (StringUtils.isNotBlank(jobTruckId)) {
			jobTruckId = jobTruckId + JOB_ID_SEPARATE + jobSeq;
		}
		return new String[] { jobId, jobTruckId };
	}

	private String getAddressByParty(PartyDetails partyDetails) {

		StringBuilder pickUpAddressSb = new StringBuilder();
		if (StringUtils.isNotBlank(partyDetails.getAddressLine1())) {
			pickUpAddressSb.append(partyDetails.getAddressLine1()).append(" ");
		}
		if (StringUtils.isNotBlank(partyDetails.getAddressLine2())) {
			pickUpAddressSb.append(partyDetails.getAddressLine2()).append(" ");
		}
		if (StringUtils.isNotBlank(partyDetails.getAddressLine3())) {
			pickUpAddressSb.append(partyDetails.getAddressLine3()).append(" ");
		}
		if (StringUtils.isNotBlank(partyDetails.getCityName())) {
			pickUpAddressSb.append(partyDetails.getCityName()).append(" ");
		}
		if (StringUtils.isNotBlank(partyDetails.getPostCode())) {
			pickUpAddressSb.append(partyDetails.getPostCode()).append(" ");
		}
		if (StringUtils.isNotBlank(partyDetails.getCountryCode())) {
			pickUpAddressSb.append(partyDetails.getCountryCode());
		}

		return pickUpAddressSb.toString();
	}

	private Optional<List<ReferenceGroup>> getListReferenceGroup(DSVShipmentMessage shipMsg) {
		return Optional.of(shipMsg).map(DSVShipmentMessage::getShipment)
				.map(com.dsv.edi.xml.DSV_ShipmentMessage_v1.Shipment::getReferences)
				.map(rgl -> rgl.getReferenceGroup());
	}

	private Optional<List<TransportService>> getListTransportService(DSVShipmentMessage shipMsg) {

		return Optional.of(shipMsg).map(DSVShipmentMessage::getShipment)
				.map(com.dsv.edi.xml.DSV_ShipmentMessage_v1.Shipment::getTransportServices)
				.map(ts -> ts.getTransportService());
	}

	private Optional<com.dsv.edi.xml.DSV_ShipmentMessage_v1.TransportStage> getMainCarriageTransportStage(
			DSVShipmentMessage shipMsg) {

		return shipMsg.getShipment().getTransportStages().getTransportStage().stream()
				.filter(ts -> "MainCarriage".contentEquals(ts.getStage().getType())).findFirst();
	}

	private TCkJobTruck prepareJobTruck(DSVShipmentMessage msg, String jobId, String jobTruckId) throws Exception {

		if (StringUtils.isBlank(jobId)) {
			jobId = CkUtil.generateId(ICkConstant.PREFIX_PARENT_JOB);
		}

		if (StringUtils.isBlank(jobTruckId)) {
			jobTruckId = CkUtil.generateId(IClicTruckConstant.PREFIX_CK_TRUCK_JOB);
		}

		String dsvasAccnId = sysParam.getValString("CLICTRUCK_DSVAS_ACCN_ID", "DSVAS");
		log.info("dsvasAccnId: " + dsvasAccnId);

		TCoreAccn accnCo = ckCoreAccnDao.find(dsvasAccnId);
		accnCo.setAccnId(dsvasAccnId);

		TCoreAccn accnTo = getTOAccn(msg);
		log.info("accnTo: " + accnTo.getAccnId());

		TCkCtMstVehType mstVehType = new TCkCtMstVehType(ICkConstant.VEHICLE_TYPE_UNDEFINE, "");

		//

		// TCkRecordDate
		TCkRecordDate jobRecordDate = new TCkRecordDate();
		jobRecordDate.setRcdId(CkUtil.generateId());
		jobRecordDate.setRcdDtSubmit(this.getDocumentDate(msg));

		// TCkJob
		TCkJob job = new TCkJob();
		job.setTCkRecordDate(jobRecordDate);
		job.setJobId(jobId);
		job.setTCkMstJobState(new TCkMstJobState(JobStates.ACP.name(), JobStates.ACP.name()));

		// ShipmentTypes shipType = this.getShipmentType(msg);
		// DOMESTIC type job
		ShipmentTypes shipType = ShipmentTypes.DOMESTIC;
		job.setTCkMstShipmentType(new TCkMstShipmentType(shipType.getId(), null));
		job.setTCkMstJobType(new TCkMstJobType(this.getJobType(shipType).name(), null));
		job.setTCoreAccnByJobToAccn(accnTo);
		job.setTCoreAccnByJobOwnerAccn(accnCo);

		if (AccountTypes.ACC_TYPE_FF.name().equalsIgnoreCase(accnCo.getTMstAccnType().getAtypId())) {
			// FF
			job.setTCoreAccnByJobFfAccn(accnCo);
			job.setTCoreAccnByJobCoAccn(null);
		} else {
			// CO
			job.setTCoreAccnByJobFfAccn(null);
			job.setTCoreAccnByJobCoAccn(accnCo);
		}

		job.setJobStatus(Constant.ACTIVE_STATUS);
		job.setJobDtCreate(new Date());

		// TCkCtContactDetail TO
		TCkCtContactDetail tContactTo = new TCkCtContactDetail();
		tContactTo.setCdId(CkUtil.generateIdSynch(CkJobTruckServiceUtil.CD_PREFIX));
		tContactTo.setCdStatus(Constant.ACTIVE_STATUS);
		tContactTo.setCdDtCreate(new Date());
		tContactTo.setCdName("");

		// Contact contactTO = this.getPartyContactByRole(msg, "Consignee");
		if (null != accnTo.getAccnContact()) {
			tContactTo.setCdName(" ");
			tContactTo.setCdPhone(accnTo.getAccnContact().getContactTel());
			tContactTo.setCdEmail(accnTo.getAccnContact().getContactEmail());
		}

		// TCkCtContactDetail CO
		TCkCtContactDetail tContactCo = new TCkCtContactDetail();
		tContactCo.setCdId(CkUtil.generateIdSynch(CkJobTruckServiceUtil.CD_PREFIX));
		tContactCo.setCdStatus(Constant.ACTIVE_STATUS);
		tContactCo.setCdDtCreate(new Date());
		tContactCo.setCdName(" ");

		// Contact contactCO = this.getPartyContactByRole(msg, "Consignor");
		if (null != accnCo.getAccnContact()) {
			tContactCo.setCdName(" ");
			tContactCo.setCdPhone(accnCo.getAccnContact().getContactTel());
			tContactCo.setCdEmail(accnCo.getAccnContact().getContactEmail());
		}

		// TCkJobTruck
		TCkJobTruck jobTruck = new TCkJobTruck();

		jobTruck.setJobId(jobTruckId);
		jobTruck.setTCkJob(job);
		jobTruck.setTCkCtContactDetailByJobContactTo(tContactTo);
		jobTruck.setTCkCtContactDetailByJobContactCoFf(tContactCo);

		jobTruck.setTCoreAccnByJobPartyCoFf(accnCo);
		jobTruck.setTCoreAccnByJobPartyTo(accnTo);

		jobTruck.setJobDtBooking(this.getDateByType(msg, "Booked"));
		jobTruck.setJobDtPlan(this.getDateByType(msg, "IssueDate"));

		jobTruck.setTCkCtMstVehType(mstVehType);
		jobTruck.setJobVehOth(null);
		jobTruck.setJobCustomerRef(this.getcustomerRef(msg));
		jobTruck.setJobShipmentRef(msg.getHeader().getMessageId());

		jobTruck.setJobTotalCharge(BigDecimal.ONE);

		// new CkTruckMiscMobileService().checkAndSetFinanceOptions()
		jobTruck.setJobIsFinanced('N');
		// new CkTruckMiscMobileService().checkAndSetMobileEnable()
		jobTruck.setJobMobileEnabled('Y');
		jobTruck.setJobSource(TruckJobSourceEnum.DSV_XML.name());

		jobTruck.setJobStatus(Constant.ACTIVE_STATUS);
		jobTruck.setJobUidCreate("XML");
		jobTruck.setJobDtCreate(new Date());
		jobTruck.setJobDtLupd(new Date()); // order by this field in frontEnd.

		return jobTruck;
	}

	private String getcustomerRef(DSVShipmentMessage msg) {

		String masterbill = this.getMasterBill(msg);
		String housebill = this.getHouseBill(msg);

		StringJoiner joiner = new StringJoiner(",");

		if (!masterbill.isEmpty()) {
			joiner.add(masterbill);
		}
		if (!housebill.isEmpty()) {
			joiner.add(housebill);
		}

		return joiner.toString();
		// return (masterbill + "," + housebill).trim().replaceFirst("^,",
		// "").replaceAll(",$", "");
	}

	private void createTrip(DSVShipmentMessage msg, TCkJobTruck jobTruck) throws Exception {

		String accnId = jobTruck.getTCoreAccnByJobPartyTo().getAccnId();

		String fromLocAddress = this.getPickUpLocation(msg).getPickUpAddress();
		String fromLocName = (this.getLocNameFromAddress(fromLocAddress));

		String toLocAddress = this.getDropOffLocation(msg).getDropOffAddress();
		String toLocName = this.getLocNameFromAddress(toLocAddress);

		// From location;
		TCkCtLocation fromLoc = ckCtLocationDao
				.findByNameAndCompanyAndAddress(accnId, LocationId.ADDRESS, fromLocName, fromLocAddress).orElse(null);
		if (null == fromLoc) {
			// create new location;
			fromLoc = this.createLocation(accnId, LocationId.ADDRESS, fromLocName, fromLocAddress);
		}
		// To location;
		TCkCtLocation toLoc = ckCtLocationDao
				.findByNameAndCompanyAndAddress(accnId, LocationId.ADDRESS, toLocName, toLocAddress).orElse(null);
		if (null == toLoc) {
			// create new location;
			toLoc = this.createLocation(accnId, LocationId.ADDRESS, toLocName, toLocAddress);
		}

		// TCkCtTripLocation
		TCkCtTripLocation locFrom = new TCkCtTripLocation();
		locFrom.setTlocId(CkUtil.generateIdSynch(CkJobTruckServiceUtil.TRIPLOC_PREFIX));
		locFrom.setTCkCtLocation(fromLoc);
		locFrom.setTlocLocAddress(fromLocAddress); // departure
		locFrom.setTlocLocName(fromLocName);
		locFrom.setTlocLocGps(fromLoc.getLocGps());
		locFrom.setTlocStatus(Constant.ACTIVE_STATUS);
		locFrom.setTlocDtCreate(new Date());

		TCkCtTripLocation locTo = new TCkCtTripLocation();
		locTo.setTlocId(CkUtil.generateIdSynch(CkJobTruckServiceUtil.TRIPLOC_PREFIX));
		locTo.setTCkCtLocation(toLoc);

		locTo.setTlocLocAddress(toLocAddress); // destination
		locTo.setTlocLocName(toLocName);
		locTo.setTlocLocGps(toLoc.getLocGps());
		locTo.setTlocStatus(Constant.ACTIVE_STATUS);
		locTo.setTlocDtCreate(new Date());

		/*-
		TCkCtTripLocation locDepot = new TCkCtTripLocation();
		locDepot.setTlocId(CkUtil.generateIdSynch(CkJobTruckServiceUtil.TRIPLOC_PREFIX));
		locDepot.setTCkCtLocation(othLoc);
		locDepot.setTlocStatus(Constant.ACTIVE_STATUS);
		locDepot.setTlocDtCreate(new Date());
		
		if (ShipmentTypes.IMPORT == this.getShipmentType(msg)) {
			locFrom.setTlocLocAddress(getLocationAddress(msg, LOC_DEPARTURE)); // departure
			locTo.setTlocLocAddress(getLocationAddress(msg, LOC_DESTINATION)); // destination
			// locDepot.setTlocLocName(getLocationName(msg, LOC_DESTINATION)); //
			// destination
		} else {
			// export
			// locDepot.setTlocLocName(getLocationName(msg, LOC_DEPARTURE)); //
			locFrom.setTlocLocAddress(getLocationAddress(msg, LOC_DEPARTURE)); // departure
			locTo.setTlocLocAddress(getLocationAddress(msg, LOC_DESTINATION)); // destination
		}
		locDepot.setTlocLocGps(this.getLocGPS(locDepot));
		*/

		// set GPS in TCkCtTripLocation

		// TCkCtTripCharge
		TCkCtTripCharge tripCharge = new TCkCtTripCharge(
				CkUtil.generateIdSynch(CkJobTruckServiceUtil.TRIP_CHARGE_PREFIX), 'Y');
		tripCharge.setTcStatus(Constant.ACTIVE_STATUS);
		tripCharge.setTcDtCreate(new Date());

		// TCkCtTrip
		TCkCtTrip trip = new TCkCtTrip(CkUtil.generateIdSynch(CkJobTruckServiceUtil.TRIP_PREFIX));
		trip.setTCkJobTruck(jobTruck);
		trip.setTCkCtTripLocationByTrFrom(locFrom);
		trip.setTCkCtTripLocationByTrTo(locTo);
		// trip.setTCkCtTripLocationByTrDepot(locDepot);
		trip.setTCkCtTripCharge(tripCharge);

		trip.setTrChargeOpen('Y');
		trip.setTrStatus(Constant.ACTIVE_STATUS);
		trip.setTrDtCreate(new Date());

		// TCkCtTripCargoMm
		List<TCkCtTripCargoMm> cargoMmList = this.createCargoMM(msg, trip);

		// TCkCtTripDo
		TCkCtTripDo ckCtTripDo = new TCkCtTripDo();
		ckCtTripDo.setDoId(CkUtil.generateId(ICkConstant.PREFIX_JOB_ATT));
		ckCtTripDo.setTCkCtTrip(trip);
		// ckCtTripDo.setDoNo(msg.getHeader().getMessageId()); // shipment Message Id
		String doNo;
		doNo = "M-" + jobTruck.getJobId() + "-DO";
		doNo = doNo.replace("CKCTJ", "");
		ckCtTripDo.setDoNo(doNo);

		ckCtTripDo.setDoStatus(RecordStatus.ACTIVE.getCode());
		ckCtTripDo.setDoDtCreate(Calendar.getInstance().getTime());
		ckCtTripDo.setDoUidCreate("SYS");
		ckCtTripDo.setDoDtLupd(Calendar.getInstance().getTime());
		ckCtTripDo.setDoUidLupd("SYS");

		// ckCtTripLocationDao.add(locDepot);
		ckCtTripLocationDao.add(locFrom);
		ckCtTripLocationDao.add(locTo);

		ckCtTripChargeDao.add(tripCharge);
		ckCtTripDao.add(trip);

		for (TCkCtTripCargoMm cargoMm : cargoMmList) {
			ckCtTripCargoMmDao.add(cargoMm);
		}

		ckCtTripDoDao.saveOrUpdate(ckCtTripDo);
	}

	private List<TCkCtTripCargoMm> createCargoMM(DSVShipmentMessage msg, TCkCtTrip trip) {

		List<TCkCtTripCargoMm> cargoMmList = new ArrayList<>();

		List<GoodsItem> goodsItem = msg.getShipment().getGoodsItems().getGoodsItem();

		Date now = new Date();

		if (null != goodsItem && goodsItem.size() > 0) {

			for (int i = 0; i < goodsItem.size(); i++) {

				GoodsItem gi = goodsItem.get(i);

				TCkCtTripCargoMm cargoMm = new TCkCtTripCargoMm(
						CkUtil.generateIdSynch(CkJobTruckServiceUtil.TRIP_CRG_FM_PREFIX), trip);

				cargoMm.setTCkCtMstCargoType(this.getCargoType(gi));

				cargoMm.setCgStatus(Constant.ACTIVE_STATUS);
				cargoMm.setCgDtCreate(now);

				if (gi.getDimensions() != null && gi.getDimensions().getDimension() != null
						&& gi.getDimensions().getDimension().size() > 0) {

					List<Dimension> dimension = gi.getDimensions().getDimension();
					cargoMm.setCgCargoLength(Double.valueOf(dimension.get(0).getLength()));
					cargoMm.setCgCargoWidth(Double.valueOf((dimension.get(0).getWidth())));
					cargoMm.setCgCargoHeight(Double.valueOf(dimension.get(0).getHeight()));
					cargoMm.setCgCargoQty((double) CommonUtil.convert2Int(dimension.get(0).getNumberOfUnits(), 0));
				}

				this.setWeightVolume(cargoMm, gi);

				cargoMm.setCgCargoMarksNo(this.getCargoFmMarks(gi));
				cargoMm.setCgCargoDesc(this.getFmDesc(gi));
				cargoMm.setCgCargoSpecialInstn(this.getFmInstruction(gi));

				cargoMmList.add(cargoMm);
			}
		}

		return cargoMmList;
	}

	private TCkCtMstCargoType getCargoType(GoodsItem gi) {

		try {
			String natureOfGoods = gi.getFreeTextData().getFreeText().get(0).getTextLine1();
			switch (natureOfGoods) {
			case "General":
				return new TCkCtMstCargoType(CargoTypes.GENERAL.name());
			case "Hazardous":
				return new TCkCtMstCargoType(CargoTypes.DANGEROUS.name());
			default:
				break;
			}

		} catch (Exception e) {
			log.error("getCargoType(): " + e.getMessage());
		}
		return null;
	}

	private void setWeightVolume(TCkCtTripCargoMm cargoMm, GoodsItem gi) {

		try {
			if (gi.getMeasurements() != null && gi.getMeasurements().getMeasurement() != null
					&& gi.getMeasurements().getMeasurement().size() > 0) {

				for (Measurement measurement : gi.getMeasurements().getMeasurement()) {

					double measure = NumberUtil.toInteger(measurement.getMeasure());

					if ("GrossWeight".equalsIgnoreCase(measurement.getType())) {
						cargoMm.setCgCargoWeight(measure);
						;
					} else if ("Volume".equalsIgnoreCase(measurement.getType())) {
						cargoMm.setCgCargoVolume(measure);
					}
				}
			}
		} catch (Exception e) {
			log.error("getCargoType(): " + e.getMessage());
		}
	}

	protected String getFmDesc(GoodsItem gi) {

		StringBuilder fmDesc = new StringBuilder();

		try {
			if (gi.getItem() != null) {
				fmDesc.append("Item Number: " + gi.getItem().getItemNumber() + "\n");
				fmDesc.append("Package Qty: " + gi.getItem().getPackageQty() + "\n");
				fmDesc.append("Package Type: " + gi.getItem().getPackageType() + "\n");
			}

			if (gi.getFreeTextData().getFreeText() != null) {
				for (FreeText freeText : gi.getFreeTextData().getFreeText()) {

					fmDesc.append("FreeText Type: " + freeText.getType() + "\n");

					if (StringUtils.isNotBlank(freeText.getTextLine1())) {
						fmDesc.append("FreeText Text Line 1: " + freeText.getTextLine1() + "\n");
					}
					if (StringUtils.isNotBlank(freeText.getTextLine2())) {
						fmDesc.append("FreeText Text Line 2: " + freeText.getTextLine2() + "\n");
					}
					if (StringUtils.isNotBlank(freeText.getTextLine3())) {
						fmDesc.append("FreeText Text Line 3: " + freeText.getTextLine3() + "\n");
					}
					if (StringUtils.isNotBlank(freeText.getTextLine4())) {
						fmDesc.append("FreeText Text Line 4: " + freeText.getTextLine4() + "\n");
					}
					if (StringUtils.isNotBlank(freeText.getTextLine5())) {
						fmDesc.append("FreeText Text Line 5: " + freeText.getTextLine5() + "\n");
					}
				}
			}
			if (gi.getReferences() != null && gi.getReferences().getReferenceGroup() != null) {
				for (ReferenceGroup rg : gi.getReferences().getReferenceGroup()) {
					if (rg.getReference() != null) {
						fmDesc.append("Reference Type : " + rg.getReference().getType() + "\n");
						fmDesc.append("Reference Value : " + rg.getReference().getValue() + "\n");
						if (StringUtils.isNotBlank(rg.getReference().getIdentifier())) {
							fmDesc.append("Reference Indentifier : " + rg.getReference().getIdentifier() + "\n");
						}
					}
				}
			}
			if (gi.getPackageIds() != null && gi.getPackageIds().getPackageId() != null) {
				for (PackageId pakcageId : gi.getPackageIds().getPackageId()) {

					fmDesc.append("Package Instruction Code : " + pakcageId.getInstructionCode() + "\n");

					if (pakcageId.getMarks() != null) {
						for (String shippingMark : pakcageId.getMarks().getShippingMark()) {
							fmDesc.append("Package shipping Mark : " + shippingMark + "\n");
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("getFmDesc: " + e);
		}
		Log.info("fmDesc:" + fmDesc);
		return fmDesc.toString();
	}

	private String getCargoFmMarks(GoodsItem gi) {

		if (gi.getPackageIds() != null && gi.getPackageIds().getPackageId() != null) {
			for (PackageId pakcageId : gi.getPackageIds().getPackageId()) {

				if (pakcageId.getMarks() != null) {
					for (String shippingMark : pakcageId.getMarks().getShippingMark()) {
						// fmDesc.append("Package shipping Mark : " + shippingMark + "\n");
						return shippingMark;
					}
				}
			}
		}
		return null;
	}

	protected String getFmInstruction(GoodsItem gi) {

		StringBuilder instrucDesc = new StringBuilder();

		try {
			if (gi.getHandlingInstructions() != null && gi.getHandlingInstructions().getHandlingInstruction() != null) {

				for (HandlingInstruction instruc : gi.getHandlingInstructions().getHandlingInstruction()) {
					instrucDesc.append("Handling Instruction Code: " + instruc.getDescriptionCode() + "\n");
					instrucDesc.append("Handling Instruction Description: " + instruc.getDescription() + "\n");

				}
			}
		} catch (Exception e) {
			log.error("getFmInstruction: " + e.getMessage());
		}
		Log.info("instrucDesc:" + instrucDesc);
		return instrucDesc.toString();
	}

	/**
	 * The trip location name to be same as the first 20-25 characters of the
	 * address?
	 * 
	 * @param locAddress
	 * @return
	 */
	public String getLocNameFromAddress(String locAddress) {

		if (StringUtils.isBlank(locAddress)) {
			return locAddress;
		}

		String[] locNameArray = locAddress.split(" ");
		String locName = "";

		for (String name : locNameArray) {

			locName = locName + name + " ";

			if (locName.length() > 20) {
				break;
			}
		}
		return locName.trim();
	}

	/*-
	private void setCargo(DSVShipmentMessage msg, TCkCtTripCargoMm cargoMm) {
	
		List<GoodsItem> goodsItem = msg.getShipment().getGoodsItems().getGoodsItem();
	
		if (null != goodsItem && goodsItem.size() > 0) {
	
			if (goodsItem.get(0).getDimensions() != null && goodsItem.get(0).getDimensions().getDimension() != null
					&& goodsItem.get(0).getDimensions().getDimension().size() > 0) {
	
				List<Dimension> dimension = goodsItem.get(0).getDimensions().getDimension();
				cargoMm.setCgCargoLength((short) CommonUtil.convert2Int(dimension.get(0).getLength(), 0));
				cargoMm.setCgCargoWidth((short) CommonUtil.convert2Int(dimension.get(0).getWidth(), 0));
				cargoMm.setCgCargoHeight((short) CommonUtil.convert2Int(dimension.get(0).getHeight(), 0));
				cargoMm.setCgCargoQty((short) CommonUtil.convert2Int(dimension.get(0).getNumberOfUnits(), 0));
			}
		}
	}
	*/
	private void saveOrUpdateParty(List<TCkCtParty> partList) throws Exception {

		try {
			List<TCkCtParty> saveOrUpdateParties = new ArrayList<>();

			List<TCkCtParty> allParties = ckCtParty.getAll();

			//
			for (TCkCtParty party : partList) {

				if (StringUtils.isBlank(party.getPtyName())) {
					continue;
				}

				String partyName = party.getPtyName().replaceAll(" ", ""); // replace blank characters.

				Optional<TCkCtParty> existingPartyOpt = allParties.stream()
						.filter(p -> partyName.equalsIgnoreCase(p.getPtyName().replaceAll(" ", ""))).findFirst();

				if (existingPartyOpt.isPresent()) {
					// update
					TCkCtParty existingParty = existingPartyOpt.get();
					BeanUtils.copyProperties(party, existingParty, "ptyId");
					existingParty.setPtyDtLupd(new Date());
				} else {
					saveOrUpdateParties.add(party);
				}
			}

			Log.info("saveOrUpdateParties:" + saveOrUpdateParties.size());

			// Save to db;
			for (TCkCtParty party : saveOrUpdateParties) {
				saveOrUpdatePartyType(party.getTCkCtMstPartyType());
				ckCtParty.saveOrUpdate(party);
			}

		} catch (Exception e) {
			throw new Exception("Fail to save or update Party", e);
		}

	}

	private void saveOrUpdatePartyType(TCkCtMstPartyType mstPartyTypeParam) throws Exception {

		TCkCtMstPartyType mstPartyType = mstPartyTypeDao.find(mstPartyTypeParam.getPtId());

		if (mstPartyType == null) {
			mstPartyTypeParam.setPtName(mstPartyTypeParam.getPtId());
			mstPartyTypeParam.setPtStatus(Constant.ACTIVE_STATUS);
			mstPartyTypeParam.setPtDtCreate(new Date());

			mstPartyTypeDao.add(mstPartyTypeParam);
		}
	}

	private TCoreAccn getTOAccn(DSVShipmentMessage shipMsg) throws Exception {

		ShipmentTypes shipmentType = this.getShipmentType(shipMsg);

		Party party;

		if (ShipmentTypes.EXPORT == shipmentType) {

			// PickupLocalCartage
			party = this.getPartyByRole(shipMsg, PARTY_ROLE_PICKUPLOCALCARTAGE);
			if (party == null) {
				throw new Exception("Fail to find PickupLocalCartage Party");
			}
			if (party.getPartyDetails() == null || StringUtils.isBlank(party.getPartyDetails().getPartyName())) {
				throw new Exception("Fail to find PickupLocalCartage Party");
			}

		} else if (ShipmentTypes.IMPORT == shipmentType) {

			// DeliveryLocalCartage
			party = this.getPartyByRole(shipMsg, PARTY_ROLE_DELIVERYLOCALCARTAGE);
			if (party == null) {
				throw new Exception("Fail to find DeliveryLocalCartage Party");
			}
			if (party.getPartyDetails() == null || StringUtils.isBlank(party.getPartyDetails().getPartyName())) {
				throw new Exception("Fail to find DeliveryLocalCartage Party");
			}
		} else {
			throw new Exception("Job is not IMPORT and not EXPORT.");
		}

		String accnName = party.getPartyDetails().getPartyName();

		List<TCoreAccn> accnList = ckCoreAccnDao.findByAccnName(accnName);

		if (accnList == null || accnList.size() == 0) {
			throw new Exception("Fail to find account: " + accnName);
		}

		Optional<TCoreAccn> optToAccn = accnList.stream()
				.filter(accn -> AccountTypes.ACC_TYPE_TO.name().equalsIgnoreCase(accn.getTMstAccnType().getAtypId()))
				.findFirst();

		if (!optToAccn.isPresent()) {
			throw new Exception("Account is not TO: " + accnName);
		}

		return optToAccn.get();
	}

	private TCkCtLocation createLocation(String accnId, String locType, String locName, String locAddress)
			throws Exception {

		TCkCtLocation loc = new TCkCtLocation();

		loc.setLocId(CkUtil.generateId(CkCtLocationConstant.Prefix.PREFIX_CK_CT_LOCATION));
		loc.setTCkCtMstLocationType(new TCkCtMstLocationType(locType, ""));
		loc.setTCoreAccn(new TCoreAccn(accnId, null, ' ', null));

		loc.setLocAddress(locAddress);
		loc.setLocName(locName);

		loc.setLocStatus(Constant.ACTIVE_STATUS);
		loc.setLocDtCreate(new Date());
		loc.setLocUidCreate("sys");

		try {
			loc.setLocGps(getLocGPS(loc));
		} catch (Exception e) {
			log.error("Fail to find location: " + loc, e);
		}

		ckCtLocationDao.add(loc);

		return loc;
	}

	/**
	 * 
	 * @param loc
	 * @return
	 */
	private String getLocGPS(TCkCtLocation loc) {

		String gps = null;

		// get GPS from location address
		if (StringUtils.isNotBlank(loc.getLocAddress())) {
			gps = coordinateService.fetchCoordinate(loc.getLocAddress());
		}

		// get GPS from location name
		if (null == gps && StringUtils.isNotBlank(loc.getLocName())) {
			gps = coordinateService.fetchCoordinate(loc.getLocName());
		}
		return gps;
	}

	public String getSysParam(String key) throws Exception {
		if (StringUtils.isBlank(key))
			throw new ParameterException("param key null or empty");

		TCoreSysparam sysParam = coreSysparamDao.find(key);
		if (sysParam != null) {
			return sysParam.getSysVal();
		}

		throw new EntityNotFoundException("sys param config " + key + " not set");
	}

	private void audit(String jobTruckId, String action) {
		Date now = Calendar.getInstance().getTime();
		try {

			TCoreAuditlog tCoreAuditlog = new TCoreAuditlog(null, "JOB " + action + " FROM XML", now, "Scheduler",
					jobTruckId);

			tCoreAuditlog.setAudtReckey(jobTruckId);
			tCoreAuditlog.setAudtRemoteIp("-");
			tCoreAuditlog.setAudtUname("Scheduler");
			tCoreAuditlog.setAudtRemarks("JOB " + action + " FROM DSV XML");
			tCoreAuditlog.setAudtParam1("TCKJOBTRUCK" + jobTruckId);
			auditLogDao.add(tCoreAuditlog);
		} catch (Exception e) {
			log.error("recordAudit", e);
		}
	}
	/////

	////
	public static class DsvPickUp {

		private String pickUpName;
		private String pickUpAddress;

		public DsvPickUp() {
			super();
		}

		public DsvPickUp(String pickUpName, String pickUpAddress) {
			super();
			this.pickUpName = pickUpName;
			this.pickUpAddress = pickUpAddress;
		}

		public String getPickUpName() {
			return pickUpName;
		}

		public void setPickUpName(String pickUpName) {
			this.pickUpName = pickUpName;
		}

		public String getPickUpAddress() {
			return pickUpAddress;
		}

		public void setPickUpAddress(String pickUpAddress) {
			this.pickUpAddress = pickUpAddress;
		}
	}

	public static class DsvDropOff {

		private String dropOffName;
		private String dropOffAddress;

		public DsvDropOff() {
			super();
		}

		public DsvDropOff(String dropOffName, String dropOffAddress) {
			super();
			this.dropOffName = dropOffName;
			this.dropOffAddress = dropOffAddress;
		}

		public String getDropOffName() {
			return dropOffName;
		}

		public void setDropOffName(String dropOffName) {
			this.dropOffName = dropOffName;
		}

		public String getDropOffAddress() {
			return dropOffAddress;
		}

		public void setDropOffAddress(String dropOffAddress) {
			this.dropOffAddress = dropOffAddress;
		}

	}
}
