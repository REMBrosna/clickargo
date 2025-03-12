package com.guudint.clickargo.clictruck.dsv.pod;

import com.guudint.clicdo.common.service.CkCtCommonService;
import com.guudint.clickargo.clictruck.accnconfigex.service.ClictruckAccnConfigExService;
import com.guudint.clickargo.clictruck.apigateway.dto.Container;
import com.guudint.clickargo.clictruck.common.dao.CkCtEpodTemplateDao;
import com.guudint.clickargo.clictruck.common.dto.CkCtEpodTemplate;
import com.guudint.clickargo.clictruck.common.model.TCkCtEpodTemplate;
import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.guudint.clickargo.clictruck.dsv.service.IPodService;
import com.guudint.clickargo.clictruck.dsv.service.impl.DsvShipmentService;
import com.guudint.clickargo.clictruck.dsv.service.impl.SysFreightSftpUploaderService;
import com.guudint.clickargo.clictruck.jobupload.model.JobRecordTempate.JobRecordTempateItem;
import com.guudint.clickargo.clictruck.jobupload.service.JobUploadUtilService;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckExtDao;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruckExt;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkJobTruckService;
import com.guudint.clickargo.clictruck.planexec.trip.dao.*;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.service.TripMobileService;
import com.guudint.clickargo.clictruck.planexec.trip.model.*;
import com.guudint.clickargo.clictruck.util.NumberUtil;
import com.guudint.clickargo.common.CKCountryConfig;
import com.guudint.clickargo.common.dao.CkAccnDao;
import com.guudint.clickargo.common.model.TCkAccn;
import com.guudint.clickargo.job.model.TCkJob;
import com.guudint.clickargo.master.enums.ShipmentTypes;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.model.TCoreUsr;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.util.email.SysParam;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.guudint.clickargo.clictruck.planexec.trip.mobile.service.TripMobileService.TripAttachTypeEnum.*;

@Service
public class PodServiceImpl extends AbstractPodServiceImpl implements IPodService {

	private static Logger log = Logger.getLogger(PodServiceImpl.class);
	private static SimpleDateFormat yyyyMMdd_HHmmssSDF = new SimpleDateFormat("yyyyMMdd_HHmmss");
	private static final String EPOD_DEFAULT_TEMPLATE = "docs/epod/Epod.jrxml";

	@Autowired
	private SysParam sysParam;
	@Autowired
	private DsvShipmentService auxiliary;
	@Autowired
	private CkCtTripAttachDao attachDao;
	@Autowired
	@Qualifier("coreUserDao")
	private GenericDao<TCoreUsr, String> coreUserDao;
	@Autowired
	private CkJobTruckExtDao ckJobTruckExtDao;
	@Autowired
	private CkCtTripDao ckCtTripDao;
	@Autowired
	CkCtTripCargoMmDao tripCargoMmDao;
	@Autowired
	CkCtTripCargoFmDao tripCargoFmDao;
	@Autowired
	CkCtTripDoDao tripDoDao;
	@Autowired
	CkCtTripDao tripDao;
	@Autowired
	private CkAccnDao ckAccnDao;
	@Autowired
	private CkCtEpodTemplateDao ckCtEpodTemplateDao;
	@Autowired
	private CkCtCommonService ckCtCommonService;
	@Autowired
	private JobUploadUtilService jobUploadUtilService;
	@Autowired
	private CkJobTruckService ckJobTruckService;
	@Autowired
	ClictruckAccnConfigExService accnConfigExService;
	@Autowired
	private SysFreightSftpUploaderService sysFreightSftpUploaderService;

	@Transactional
	public String generateShipmentReport(String tripId) throws Exception {
		return generateShipmentReport(ckCtTripDao.find(tripId));
	}

	@Transactional
	public String generateShipmentReport(TCkCtTrip trip) throws JRException {

		try {
			TCkJobTruck jobTruck = trip.getTCkJobTruck();

			//get EPod ID template from extension
			String ePodId = getEPodId(jobTruck);
			CkCtEpodTemplate podTemplate = this.getJrxmlFromEpodTemplate(jobTruck, ePodId);

			String jrxmlBasePath = auxiliary.getSysParam(CtConstant.KEY_JRXML_BASE_PATH);
			String epodJrxml = this.getJrxmlFile(jobTruck, podTemplate);

			log.info("trip id: " + trip.getTrId() + "  " + epodJrxml);

			List<ItemHelper> items = getCargoList(jobTruck, trip.getTrId());
			Map<String, Object> parameters = getParameters(jobTruck, trip, items, jrxmlBasePath, podTemplate);

			Map<String, Object> extParameters = addParametersFromExts(jobTruck);
			this.printParam(extParameters);
			List<Container> containerList = getDetails(jobTruck.getJobId());

			// Set extra fields to datasource
			this.setExtraFieldsToDatasource(items, extParameters, parameters);
			parameters.putAll(extParameters);
			this.printParam(parameters);

			Map<String, Object> mapParam = new HashMap<>();
			if (extParameters.get("ITEM UNIT") != null){
				items.forEach(val -> val.setUom((String) extParameters.get("ITEM UNIT")));
			}
			if (extParameters.get("MODEL_NAME") != null){
				items.get(0).setUom((String) extParameters.get("MODEL_NAME"));
			}
			if (extParameters.get("C_WEIGHT") != null){
				items.get(0).setcWeight((String) extParameters.get("C_WEIGHT"));
			}
			if (extParameters.get("NO_OF_BOTTLES") != null && !items.isEmpty()){
				items.get(0).setNoOfBottle((String) extParameters.get("NO_OF_BOTTLES"));
			}

			parameters.put("cargoLines", new JRBeanCollectionDataSource(items));
			parameters.put("containerList", new JRBeanCollectionDataSource(containerList));
			mapParam.put("PARAM_INFO", parameters);
			JasperReport jasperReport = super.getReportTemplate(jrxmlBasePath, epodJrxml);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, mapParam, new JREmptyDataSource());

			// Generate file path
			String filePath = "";
			if (Objects.nonNull(podTemplate) && isSysFreightEPodTemplate(podTemplate)){
				filePath = sysFreightSftpUploaderService.generateEPodFileNameSysFreight(jobTruck);
			}else {
				filePath = this.generatePodFileName(jobTruck, trip);
			}
			File file = new File(filePath);
			// Create parent directories if they don't exist
			if (!file.getParentFile().exists()) {
				boolean dirsCreated = file.getParentFile().mkdirs();
				if (!dirsCreated) {
					throw new IOException("Failed to create directories for " + file.getParentFile().getAbsolutePath());
				}
			}

			log.info("Generated file path: " + filePath);
			// Handle file existence
			while (file.exists()) {
				log.warn("File already exists: {}. Retrying with a new name." + filePath);
				Thread.sleep(1000);
				if (Objects.nonNull(podTemplate) && isSysFreightEPodTemplate(podTemplate)){
					filePath = sysFreightSftpUploaderService.generateEPodFileNameSysFreight(jobTruck);
				}else {
					filePath = this.generatePodFileName(jobTruck, trip);
				}
				file = new File(filePath);
			}

			try (OutputStream outputStream = Files.newOutputStream(file.toPath())) {
				JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
			}
			//After process exportReportToPdfStream then upload EPod file to SFTP
			if (Objects.nonNull(podTemplate) && isSysFreightEPodTemplate(podTemplate)) {
				sysFreightSftpUploaderService.processSysFreightUploadFiles(jobTruck, filePath);
			}
			return filePath;

		} catch (Throwable e) {
			e.printStackTrace();
			log.error("trip id: " + trip.getTrId(), e);
			throw new JRException("Error generating report: " + e.getMessage());
		}
	}
	private void setExtraFieldsToDatasource(List<ItemHelper> items, Map<String, Object> extParameters, Map<String, Object> parameters) {
		for (ItemHelper item : items) {
			item.setModelName(getStringValue(extParameters.get("MODEL_NAME")));
			item.setDropOffAddress(getStringValue(parameters.get("dropoffLocation")));
			item.setRemark(getStringValue(parameters.get("dropoffLocationRemark")));
			item.setExtPo(getStringValue(extParameters.get("PO")));
			item.setcWeight(getStringValue(extParameters.get("C_WEIGHT")));
		}
	}
	private String getStringValue(Object value) {
		return value == null ? "" : value.toString();
	}

	public String generatePodFileName(TCkJobTruck jobTruck, TCkCtTrip trip) throws Exception {

		// Generate the output file path for the job
		String outputFilePath = ckCtCommonService.getCkCtAttachmentPathJob(jobTruck.getJobId(), true);
		// Example: /home/vcc/appAttachments/clictruck/20231207/CKJA231206161441663

		// Use the current date as the delivery date
		Date deliverDate = new Date();

		// Append the generated POD file name to the output file path
        return outputFilePath + File.separator + this.getPodFileName(jobTruck, deliverDate);
	}
	protected Map<String, Object> getParameters(TCkJobTruck jobTruck, TCkCtTrip trip, List<ItemHelper> cargoLines,
			String jrxmlBasePath, CkCtEpodTemplate podTemplate) throws Exception {

		Map<String, Object> parameters = new HashMap<>();

		List<TCkCtTripAttach> ckCtTripAttach = attachDao.findByTrIdAndAtyIds(
			trip.getTrId(),
			Arrays.asList(
					PHOTO_PICKUP,
					PHOTO_DROPOFF,
					SIGNATURE,
					DOCUMENT,
					PHOTO
			)
		);

		for (TCkCtTripAttach attachment : ckCtTripAttach) {
			if (attachment != null
					&& attachment.getTCkCtMstTripAttachType() != null
					&& attachment.getTCkCtMstTripAttachType().getAtypName() != null) {

				TripMobileService.TripAttachTypeEnum attachType = TripMobileService.TripAttachTypeEnum.valueOf(
						attachment.getTCkCtMstTripAttachType().getAtypId()
				);

				switch (attachType) {
					case SIGNATURE:
						parameters.put(PodParameters.signatureImage, attachment.getAtLoc());
						break;
					case PHOTO_PICKUP:
						parameters.put(PodParameters.tripPickupPhoto, attachment.getAtLoc());
						break;
					case PHOTO_DROPOFF:
						parameters.put(PodParameters.tripDropOffPhoto, attachment.getAtLoc());
						break;
					case DOCUMENT:
						parameters.put(PodParameters.tripDocument, attachment.getAtLoc());
					case PHOTO:
						parameters.put(PodParameters.tripPhoto, attachment.getAtLoc());
						break;
					default:
						break;
				}
			}
		}

		this.addLogo(jobTruck, jrxmlBasePath, parameters, podTemplate);

		this.addSubReport(parameters, jrxmlBasePath, podTemplate);

		this.addAccnInfo(jobTruck, parameters);

		this.addJobTruckTrip(jobTruck, trip, parameters);

		this.addJobContact(jobTruck, parameters);

		this.addTruckInOutTime(jobTruck, trip, parameters);

		this.addPickUpDropoffLoc(jobTruck, trip, parameters);

		this.addCargo(cargoLines, parameters);

		this.addTruck(jobTruck, parameters);

		this.addDriver(jobTruck, parameters);
		this.userFfName(jobTruck.getJobUidCreate(), parameters);

		parameters.put(PodParameters.issueDate, parameters.get(PodParameters.truckTimeOut));
		parameters.put(PodParameters.driverRemarks, "-");
		parameters.put(PodParameters.damagedGoods, "-");

		return parameters;
	}

	private void addLogo(TCkJobTruck jobTruck, String jrxmlBasePath, Map<String, Object> parameters,
			CkCtEpodTemplate podTemplate) {

		String logo = null;

		if (podTemplate != null && StringUtils.isNoneBlank(podTemplate.getEpodEpodLogo())) {
			logo = podTemplate.getEpodEpodLogo();
		} else {
			logo = "docs/epod/LOGO_CLIC.png";
		}
		parameters.put(PodParameters.logo, jrxmlBasePath + logo);
		parameters.put(PodParameters.MILLESIMA_LOGO, jrxmlBasePath + "docs/epod/LOGO_MILLESIMA.png");
		parameters.put(PodParameters.HUBDISTRIBUTORS, jrxmlBasePath + "docs/epod/LOGO_HUB_BANNER.png");
	}

	private void addSubReport(Map<String, Object> parameters, String jrxmlBasePath, CkCtEpodTemplate podTemplate) {
		if (podTemplate != null && StringUtils.isNotBlank(podTemplate.getEpodEpodSubReport())) {
			parameters.put("subReport", jrxmlBasePath + podTemplate.getEpodEpodSubReport());
		}
	}

	private void addJobTruckTrip(TCkJobTruck jobTruck, TCkCtTrip trip, Map<String, Object> parameters) {

		parameters.put(PodParameters.jobId, jobTruck.getJobId());
		parameters.put(PodParameters.shipmentRef, jobTruck.getJobShipmentRef());
		parameters.put(PodParameters.customerRef, jobTruck.getJobCustomerRef());

		List<TCkCtTripDo> tripDoList;
		try {
			tripDoList = tripDoDao.findByTripId(trip.getTrId());

			if (tripDoList != null && tripDoList.size() > 0) {
				parameters.put(PodParameters.doNO, tripDoList.get(0).getDoNo());
			}
		} catch (Exception e) {
			log.error("", e);
		}
	}

	private void addJobContact(TCkJobTruck jobTruck, Map<String, Object> parameters) {

		if (jobTruck.getTCkCtContactDetailByJobContactCoFf() != null) {
			parameters.put(PodParameters.jobContactNameFf,
					jobTruck.getTCkCtContactDetailByJobContactCoFf().getCdName());
			parameters.put(PodParameters.jobContactTelFf,
					jobTruck.getTCkCtContactDetailByJobContactCoFf().getCdPhone());
			parameters.put(PodParameters.jobcontactEmailFf,
					jobTruck.getTCkCtContactDetailByJobContactCoFf().getCdEmail());
		}
		if (jobTruck.getTCkCtContactDetailByJobContactTo() != null) {
			parameters.put(PodParameters.jobContactNameTo, jobTruck.getTCkCtContactDetailByJobContactTo().getCdName());
			parameters.put(PodParameters.jobContactTelTo, jobTruck.getTCkCtContactDetailByJobContactTo().getCdPhone());
			parameters.put(PodParameters.jobcontactEmailTo,
					jobTruck.getTCkCtContactDetailByJobContactTo().getCdEmail());
		}
	}

	private void addTruckInOutTime(TCkJobTruck jobTruck, TCkCtTrip trip, Map<String, Object> parameters) throws Exception {

		Date startDate = this.getStartDateTime(jobTruck, trip);
		Date completeDate = this.getEndDateTime(jobTruck, trip);

		if (startDate != null) {
			parameters.put(PodParameters.truckTimeIn, convertDateTimeBaseOnCountryCode(startDate));
		}
		if (completeDate != null) {
			parameters.put(PodParameters.truckTimeOut, convertDateTimeBaseOnCountryCode(completeDate));
		}
	}

	private void addPickUpDropoffLoc(TCkJobTruck jobTruck, TCkCtTrip trip, Map<String, Object> parameters) {
		// Domestic(from-> to), export(Depot->to)
		String shipmentType = jobTruck.getTCkJob().getTCkMstShipmentType().getShtId();
		String pickupLoc = null;
		String pickupLocName = null;
		String dropoffLoc = null;
		String dropoffLocName = null;
		String dropoffLocationRemark = null;
		String toLocCargoRec = "";
		String dropOffSchedulerDateTime = "";

		if (ShipmentTypes.DOMESTIC.name().equalsIgnoreCase(shipmentType)
				|| ShipmentTypes.IMPORT.name().equalsIgnoreCase(shipmentType)) {

			pickupLocName = trip.getTCkCtTripLocationByTrFrom().getTlocLocName();
			pickupLoc = this.getLocDetail(trip.getTCkCtTripLocationByTrFrom());

		} else {
			// Export
			pickupLocName = trip.getTCkCtTripLocationByTrDepot().getTlocLocName();
			pickupLoc = this.getLocDetail(trip.getTCkCtTripLocationByTrDepot());
		}

		if (ShipmentTypes.DOMESTIC.name().equalsIgnoreCase(shipmentType) ||
				ShipmentTypes.EXPORT.name().equalsIgnoreCase(shipmentType) ||
				"LOCAL".equalsIgnoreCase(shipmentType)) {

			dropoffLocName = trip.getTCkCtTripLocationByTrTo().getTlocLocName();
			dropoffLoc = this.getLocDetail(trip.getTCkCtTripLocationByTrTo());
			dropoffLocationRemark = trip.getTCkCtTripLocationByTrTo() != null
					? trip.getTCkCtTripLocationByTrTo().getTlocRemarks()
					: "";

		} else {
			// Import(From->Depot)
			dropoffLocName = trip.getTCkCtTripLocationByTrDepot().getTlocLocName();
			dropoffLoc = this.getLocDetail(trip.getTCkCtTripLocationByTrDepot());
		}
		parameters.put(PodParameters.pickupLocation, pickupLoc);
		parameters.put(PodParameters.pickupLocationName, pickupLocName);
		parameters.put(PodParameters.dropoffLocation, dropoffLoc);
		parameters.put(PodParameters.dropoffLocationName, dropoffLocName);
		parameters.put(PodParameters.dropoffLocationRemark, dropoffLocationRemark);
		parameters.put(PodParameters.cargoRecipient,
				trip.getTCkCtTripLocationByTrTo() != null &&
				trip.getTCkCtTripLocationByTrTo().getTlocCargoRec() != null ?
				trip.getTCkCtTripLocationByTrTo().getTlocCargoRec() : "");
		parameters.put(PodParameters.dropoffLocationMobile,
				trip.getTCkCtTripLocationByTrTo() != null &&
				trip.getTCkCtTripLocationByTrTo().getTlocMobileNo() != null
				? trip.getTCkCtTripLocationByTrTo().getTlocMobileNo() : ""
		);

		if (StringUtils.isNotBlank(pickupLoc)) {
			String postcode = pickupLoc.replaceAll(".*?(\\d{6}).*", "$1");
			// return original string if not match
			if (!pickupLoc.equalsIgnoreCase(postcode)) {
				parameters.put(PodParameters.pickupLocationPostcode, postcode);
			}
		}

		if (StringUtils.isNotBlank(dropoffLoc)) {
			String postcode = dropoffLoc.replaceAll(".*?(\\d{6}).*", "$1");
			// return original string if not match
			if (!dropoffLoc.equalsIgnoreCase(postcode)) {
				parameters.put(PodParameters.dropoffLocationPostcode, postcode);
			}
		}
		parameters.put(PodParameters.pickupLocationMobile,
				trip.getTCkCtTripLocationByTrFrom() != null &&
						trip.getTCkCtTripLocationByTrFrom().getTlocMobileNo() != null
						? trip.getTCkCtTripLocationByTrFrom().getTlocMobileNo() : ""
		);
		parameters.put(PodParameters.pickupLocationRemarks,
				trip.getTCkCtTripLocationByTrFrom() != null
						? trip.getTCkCtTripLocationByTrFrom().getTlocRemarks()
						: "");
	}

	private void addCargo(List<ItemHelper> cargoLines, Map<String, Object> parameters) {

		if (cargoLines != null && cargoLines.size() > 0) {

			ItemHelper cargo = cargoLines.get(0);

			parameters.put(PodParameters.cargoType, cargo.getCargoType());
			parameters.put(PodParameters.cargoQty, cargo.getQuantity());
			parameters.put(PodParameters.cargoGwt, cargo.getWeight());
			parameters.put(PodParameters.cargoVolume, cargo.getVolume());
			parameters.put(PodParameters.cargoDesc, cargo.getGoodsDescription());
			parameters.put(PodParameters.cargoSpecial, cargo.getSpecialInstruction());
			parameters.put(PodParameters.cargoMarksNo, cargo.getMarksNo());
		}

		if (cargoLines != null && cargoLines.size() > 0) {
			double totalQty = cargoLines.stream()
					.map(cargo -> NumberUtil.toBigDecimal(cargo.getQuantity()).doubleValue()).mapToDouble(f -> f).sum();

			double totalWeight = cargoLines.stream()
					.map(cargo -> NumberUtil.toBigDecimal(cargo.getWeight()).doubleValue()).mapToDouble(f -> f).sum();

			double totalVolume = cargoLines.stream()
					.map(cargo -> NumberUtil.toBigDecimal(cargo.getVolume()).doubleValue()).mapToDouble(f -> f).sum();

			parameters.put(PodParameters.totalQty, totalQty);
			parameters.put(PodParameters.totalWeight, totalWeight);
			parameters.put(PodParameters.totalVolume, totalVolume);

			boolean isDanger = cargoLines.stream().anyMatch( cargo -> cargo.getCargoType().toUpperCase().indexOf("DANGER") > -1);
			parameters.put(PodParameters.cargoIsDanger, isDanger?"Yes":"No");

		}
	}

	private void addTruck(TCkJobTruck jobTruck, Map<String, Object> parameters) {
		String truckNo = this.getTruckNo(jobTruck);
		parameters.put(PodParameters.truckNo, truckNo);
		parameters.put(PodParameters.truckType, this.getTruckType(jobTruck));
	}

	private void userFfName(String jobUidCreate,  Map<String, Object> parameters) throws Exception {
		TCoreUsr coreUsr = coreUserDao.find(jobUidCreate);
		if (null != coreUsr) {
			parameters.put(PodParameters.userFfName, coreUsr.getUsrName());
		}
	}

	private void addDriver(TCkJobTruck jobTruck, Map<String, Object> parameters) {

		String driverName = null;
		String driverMobile = null;
		String driverIc = null;

		if (jobTruck.getTCkCtDrv() != null) {
			driverName = jobTruck.getTCkCtDrv().getDrvName();
			driverMobile = jobTruck.getTCkCtDrv().getDrvPhone();
			driverIc = jobTruck.getTCkCtDrv().getDrvLicenseNo();
		}
		parameters.put(PodParameters.driverName, driverName);
		parameters.put(PodParameters.driverMobile, driverMobile);
		parameters.put(PodParameters.driverIc, driverIc);
	}

	private void addAccnInfo(TCkJobTruck jobTruck, Map<String, Object> parameters) {

		parameters.put(PodParameters.accnNameFf, jobTruck.getTCoreAccnByJobPartyCoFf().getAccnName());
		parameters.put(PodParameters.accnAddressFf, this.getAccnAddress(jobTruck.getTCoreAccnByJobPartyCoFf()));
		if (jobTruck.getTCoreAccnByJobPartyCoFf().getAccnContact() != null) {
			String contactTel = Optional.ofNullable(jobTruck.getTCoreAccnByJobPartyCoFf())
					.map(accn -> accn.getAccnContact())
					.map(contact -> contact.getContactTel())
					.orElse("");

			String city = Optional.ofNullable(jobTruck.getTCoreAccnByJobPartyCoFf())
					.map(accn -> accn.getAccnAddr())
					.map(addr -> addr.getAddrCity())
					.orElse("");

			String postalCode = Optional.ofNullable(jobTruck.getTCoreAccnByJobPartyCoFf())
					.map(accn -> accn.getAccnAddr())
					.map(addr -> addr.getAddrPcode())
					.orElse("");
			String email = Optional.ofNullable(jobTruck.getTCoreAccnByJobPartyCoFf())
					.map(accn -> accn.getAccnContact())
					.map(contact -> contact.getContactEmail())
					.orElse("");
			String contactFax = Optional.ofNullable(jobTruck.getTCoreAccnByJobPartyCoFf())
					.map(accn -> accn.getAccnContact())
					.map(contact -> contact.getContactFax())
					.orElse("");
			parameters.put(PodParameters.accnTelFf, contactTel);
			parameters.put(PodParameters.fFcity, city);
			parameters.put(PodParameters.fFPostalCode, postalCode);
			parameters.put(PodParameters.fFAccntEmail, email);
			parameters.put(PodParameters.fFContactFax, contactFax);
		}

		parameters.put(PodParameters.accnNameTo, jobTruck.getTCoreAccnByJobPartyTo().getAccnName());
		parameters.put(PodParameters.accnAddressTo, this.getAccnAddress(jobTruck.getTCoreAccnByJobPartyTo()));
		if (jobTruck.getTCoreAccnByJobPartyTo().getAccnContact() != null) {
			String contactTel = Optional.ofNullable(jobTruck.getTCoreAccnByJobPartyTo())
					.map(accn -> accn.getAccnContact())
					.map(contact -> contact.getContactTel())
					.orElse("");
			String city = Optional.ofNullable(jobTruck.getTCoreAccnByJobPartyTo())
					.map(accn -> accn.getAccnAddr())
					.map(addr -> addr.getAddrCity())
					.orElse("");
			String postalCode = Optional.ofNullable(jobTruck.getTCoreAccnByJobPartyTo())
					.map(accn -> accn.getAccnAddr())
					.map(addr -> addr.getAddrPcode())
					.orElse("");
			parameters.put(PodParameters.accnTelTo, contactTel);
			parameters.put(PodParameters.toCity, city);
			parameters.put(PodParameters.toPostalCode, postalCode);
		}

		TCoreAccn ffco = jobTruck.getTCkJob().getTCoreAccnByJobSlAccn();
		if (ffco != null) {
			parameters.put(PodParameters.accnNameFfCo, ffco.getAccnName());
			parameters.put(PodParameters.accnAddressFfCo, this.getAccnAddress(ffco));
			if (ffco.getAccnContact() != null) {
				parameters.put(PodParameters.accnTelFfCo, ffco.getAccnContact().getContactTel());
			}
		}
	}

	private String getLocDetail(TCkCtTripLocation ckTripLoc) {

		if (ckTripLoc != null) {
			// get drop location from Trip
			if (StringUtils.isNotBlank(ckTripLoc.getTlocLocAddress())) {

				return ckTripLoc.getTlocLocAddress();

			} else if (StringUtils.isNotBlank(ckTripLoc.getTlocLocName())) {

				return ckTripLoc.getTlocLocName();
			}
		}
		return null;
	}

	private String getAccnAddress(TCoreAccn accn) {
		String address = "";
		if (accn.getAccnAddr() != null) {
			if (StringUtils.isNoneBlank(accn.getAccnAddr().getAddrLn1())) {
				address += accn.getAccnAddr().getAddrLn1() + "\n";
			}
			if (StringUtils.isNoneBlank(accn.getAccnAddr().getAddrLn2())) {
				address += accn.getAccnAddr().getAddrLn2() + "\n";
			}
			if (StringUtils.isNoneBlank(accn.getAccnAddr().getAddrLn3())) {
				address += accn.getAccnAddr().getAddrLn3();
			}
		}
		return address;
	}

	private String getDropOffLocation(TCkJobTruck jobTruck) throws Exception {

		List<TCkCtTrip> tripList = tripDao.findByJobId(jobTruck.getJobId());

		if (tripList != null && tripList.size() > 0) {
			TCkCtTrip trip = tripList.get(tripList.size() - 1);
			if (trip.getTCkCtTripLocationByTrTo() != null) {
				return trip.getTCkCtTripLocationByTrTo().getTlocLocAddress();
			}
		}

		return "-";
	}

	private Date getStartDateTime(TCkJobTruck jobTruck, TCkCtTrip trip) throws Exception {

		Date date = null;

		if (ShipmentTypes.DOMESTIC.name().equalsIgnoreCase(jobTruck.getTCkJob().getTCkMstShipmentType().getShtId())) {

			if (trip.getTCkCtTripLocationByTrFrom() != null
					&& trip.getTCkCtTripLocationByTrFrom().getTlocDtStart() != null) {

				date = trip.getTCkCtTripLocationByTrFrom().getTlocDtStart();
			}
		}
		if (null == date) {
			date = jobTruck.getTCkJob().getTCkRecordDate().getRcdDtStart();
		}
		return date;
	}

	private Date getEndDateTime(TCkJobTruck jobTruck, TCkCtTrip trip) {

		Date date = null;

		if (ShipmentTypes.DOMESTIC.name().equalsIgnoreCase(jobTruck.getTCkJob().getTCkMstShipmentType().getShtId())) {

			if (trip.getTCkCtTripLocationByTrTo() != null
					&& trip.getTCkCtTripLocationByTrFrom().getTlocDtEnd() != null) {

				date = trip.getTCkCtTripLocationByTrTo().getTlocDtEnd();
			}
		}
		if (null == date) {
			date = jobTruck.getTCkJob().getTCkRecordDate().getRcdDtComplete();
		}
		return date;
	}

	private String getTruckNo(TCkJobTruck jobTruck) {

		if (jobTruck.getTCkCtVeh() != null) {
			return jobTruck.getTCkCtVeh().getVhPlateNo();
		}
		try {
			String jobVehOth = jobTruck.getJobVehOth();
			if (StringUtils.isNotBlank(jobVehOth)) {
				// Map<String, String> map = new ObjectMapper().readValue(jobVehOth,
				// HashMap.class);
				Map<String, Object> map = ckJobTruckService.stringToObject(jobVehOth);
				return map.get("vhPlateNo").toString();
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return "-";
	}

	private String getTruckType(TCkJobTruck jobTruck) {
		if (jobTruck.getTCkCtVeh() != null && jobTruck.getTCkCtVeh().getTCkCtMstVehType() != null) {
			return jobTruck.getTCkCtVeh().getTCkCtMstVehType().getVhtyName();
		}
		return "-";
	}

	protected List<ItemHelper> getCargoList(TCkJobTruck jobTruck, String tripId) {

		String shipmentType = jobTruck.getTCkJob().getTCkMstShipmentType().getShtId();

		if (ShipmentTypes.DOMESTIC.name().equalsIgnoreCase(shipmentType)) {
			return this.getCargoListMm(jobTruck, tripId);
		} else {
			return this.getCargoListFm(jobTruck, tripId);
		}
	}

	protected List<ItemHelper> getCargoListMm(TCkJobTruck jobTruck, String tripId) {

		List<ItemHelper> cargoList = new ArrayList<>();

		try {
			// List<TCkCtTripCargoMm> cargoMmList =
			// tripCargoMmDao.findTripCargoFmmsByJobId(jobTruck.getJobId());
			List<TCkCtTripCargoMm> cargoMmList = tripCargoMmDao.findTripCargoFmmsByTripId(tripId);

			int itemNo = 1;
			for (TCkCtTripCargoMm cargoMm : cargoMmList) {
				ItemHelper cargo = new ItemHelper();
				cargo.setItemNo(itemNo++);

				cargo.setCargoType(getCargoType(cargoMm));
				cargo.setGoodsDescription(getValueIfNotBlank(cargoMm.getCgCargoDesc()));
				cargo.setSpecialInstruction(getValueIfNotBlank(cargoMm.getCgCargoSpecialInstn()));
				cargo.setQuantity(getQuantity(cargoMm));
				cargo.setSize(getSize(cargoMm));
				cargo.setTruckType(getTruckType(jobTruck));
				cargo.setVolume(getVolume(cargoMm));
				cargo.setWeight(getValueAsString(cargoMm));
				cargo.setMarksNo(cargoMm.getCgCargoMarksNo());

				cargoList.add(cargo);
			}
		} catch (Exception e) {
			Log.error("", e);
		}

		return cargoList;
	}

	private String getCargoType(TCkCtTripCargoMm cargoMm) {
		return cargoMm.getTCkCtMstCargoType() != null ? cargoMm.getTCkCtMstCargoType().getCrtypName() : "";
	}

	private String getValueIfNotBlank(String value) {
		return StringUtils.isNotBlank(value) ? value : "";
	}

	private String getQuantity(TCkCtTripCargoMm cargoMm) {
		if (cargoMm.getCgCargoQty() != null) {
			String uom = cargoMm.getCgCargoQtyUom() != null ? cargoMm.getCgCargoQtyUom() : "";
			return cargoMm.getCgCargoQty().toString() + uom;
		}
		return null;
	}

	private String getSize(TCkCtTripCargoMm cargoMm) {
		if (cargoMm.getCgCargoLength() != null && cargoMm.getCgCargoWidth() != null && cargoMm.getCgCargoHeight() != null) {
			String unit = cargoMm.getTCkCtMstUomSize() != null ? cargoMm.getTCkCtMstUomSize().getSizDesc() : "";
			return cargoMm.getCgCargoLength() + " x " + cargoMm.getCgCargoWidth() + " x " + cargoMm.getCgCargoHeight() + unit;
		}
		return null;
	}
	private String getVolume(TCkCtTripCargoMm cargoMm) {
		if (cargoMm.getCgCargoVolume() != null) {
			String unit = cargoMm.getTCkCtMstUomVolume() != null ? cargoMm.getTCkCtMstUomVolume().getVolDesc() : "";
			return cargoMm.getCgCargoVolume().toString() + unit;
		}
		return null;
	}

	private String getValueAsString(TCkCtTripCargoMm cargoMm) {
		if (cargoMm.getCgCargoWeight() == null) {
			return null;
		}
		String weightUom = (cargoMm.getTCkCtMstUomWeight() != null && cargoMm.getTCkCtMstUomWeight().getWeiDesc() != null)
				? cargoMm.getTCkCtMstUomWeight().getWeiDesc()
				: "";
		return cargoMm.getCgCargoWeight().toString() + weightUom;
	}

	protected List<ItemHelper> getCargoListFm(TCkJobTruck jobTruck, String tripId) {

		List<ItemHelper> cargoList = new ArrayList<>();

		try {
			List<TCkCtTripCargoFm> fmList = tripCargoFmDao.findTripCargoFmsByTripId(tripId);

			for (TCkCtTripCargoFm cargoFm : fmList) {

				ItemHelper cargo = new ItemHelper();

				cargo.setGoodsDescription(cargoFm.getCgCargoDesc());

				if (cargoFm.getTCkCtMstCargoType() != null) {
					cargo.setCargoType(cargoFm.getTCkCtMstCargoType().getCrtypName());
				}

				if (jobTruck.getTCkCtMstVehType() != null) {
					cargo.setTruckType(jobTruck.getTCkCtMstVehType().getVhtyName());
				}

				cargoList.add(cargo);
			}

		} catch (Exception e) {
			Log.error("", e);
		}

		return cargoList;
	}

	protected Map<String, Object> addParametersFromExts(TCkJobTruck jobTruck) throws Exception {

		Map<String, Object> parameters = new HashMap<>();

		String jobId = jobTruck.getTCkJob().getJobId();

		List<TCkJobTruckExt> ckJobTruckExts = super.loadJObTruckExt(jobId);

		List<JobRecordTempateItem> templateItemList = this.getJobUploadTemplate(jobTruck);

		for (TCkJobTruckExt truckExt : ckJobTruckExts) {
			if (!parameters.containsKey(truckExt.getJextKey())) {
				String key = this.getFieldByExtKey(truckExt.getJextKey(), templateItemList);
				parameters.put(key.toUpperCase(), truckExt.getJextValAsStrOrObj());
			}
		}
		return parameters;
	}

	private List<JobRecordTempateItem> getJobUploadTemplate(TCkJobTruck jobTruck) {

		List<JobRecordTempateItem> templateItemList = null;
		try {
			// get by TO
			templateItemList = jobUploadUtilService
					.getJobUploadTemplate(jobTruck.getTCoreAccnByJobPartyTo().getAccnId());
		} catch (Exception e) {

		}
		try {
			if (templateItemList == null) {
				// get by CO
				templateItemList = jobUploadUtilService
						.getJobUploadTemplate(jobTruck.getTCoreAccnByJobPartyCoFf().getAccnId());
			}
		} catch (Exception e) {

		}
		return templateItemList;
	}

	private String getFieldByExtKey(String extKey, List<JobRecordTempateItem> templateItemList) {

		if (templateItemList == null || templateItemList.size() == 0) {
			return extKey;
		}
		try {
			return templateItemList.stream().filter(ti -> ti.getLabel().equalsIgnoreCase(extKey)).findFirst().get()
					.getField();
		} catch (Exception e) {

		}
		return extKey;
	}

	protected String getJrxmlFile(TCkJobTruck jobTruck, CkCtEpodTemplate podTemplate) {

		String jrxml = null;
		String ffAccnId = jobTruck.getTCoreAccnByJobPartyCoFf().getAccnId();
		try {
			if (podTemplate != null) {
				// 1: check CkCtEpodTemplate
				jrxml = podTemplate.getEpodEpodJrxml();
			}

			if (StringUtils.isBlank(jrxml)) {
				// 4: default;
				jrxml = EPOD_DEFAULT_TEMPLATE;
			}

		} catch (Exception e) {
			log.error(e);
		}

		log.info("ffAccnId: " + ffAccnId + " jrxml: " + jrxml + " jobId: " + jobTruck.getJobId());

		return jrxml;
	}

	/**
	 * Fetch Jrxml from T_CK_CT_EPOD_TEMPLATE
	 *
	 * @param jobTruck
	 * @return
	 * @throws Exception
	 */
	private CkCtEpodTemplate getJrxmlFromEpodTemplate(TCkJobTruck jobTruck, String epodId) throws Exception {

		Optional<TCkJobTruck> optJobTruck = Optional.of(jobTruck);

		final String ffAccnId = optJobTruck.map(TCkJobTruck::getTCoreAccnByJobPartyCoFf).map(TCoreAccn::getAccnId).orElse("");
		final String toAccnId = optJobTruck.map(TCkJobTruck::getTCoreAccnByJobPartyTo).map(TCoreAccn::getAccnId).orElse("");
		final String coAccnId = optJobTruck.map(TCkJobTruck::getTCkJob).map(TCkJob::getTCoreAccnByJobSlAccn)
				.map(TCoreAccn::getAccnId).orElse("");

		List<TCkCtEpodTemplate> tEpodTemplateList = ckCtEpodTemplateDao.getAll();
		List<CkCtEpodTemplate> epodTemplateList = tEpodTemplateList.stream()
				.map(CkCtEpodTemplate::new)
				.collect(Collectors.toList());

		epodTemplateList.forEach(et -> {
			if (et.getEpodAccnFf() == null) {
				et.setEpodAccnFf("");
			}
			if (et.getEpodAccnTo() == null) {
				et.setEpodAccnTo("");
			}
			if (et.getEpodAccnCo() == null) {
				et.setEpodAccnCo("");
			}
		});


		// Collect matches for FF, TO, CO
		List<CkCtEpodTemplate> matchingTemplates = epodTemplateList.stream()
				.filter(t -> ffAccnId.equalsIgnoreCase(t.getEpodAccnFf()))
				.filter(t -> toAccnId.equalsIgnoreCase(t.getEpodAccnTo()))
				.filter(t -> coAccnId.equalsIgnoreCase(t.getEpodAccnCo()))
				.collect(Collectors.toList());

		// Filter by epodId if provided
		if (StringUtils.isNotBlank(epodId) && !matchingTemplates.isEmpty() && matchingTemplates.size() > 1) {
			return epodTemplateList.stream()
					.filter(e -> epodId.equals(e.getEpodId()))
					.findFirst()
					.orElse(null);
		}

		// Additional filters for partial matches
		if (matchingTemplates.isEmpty()) {
			matchingTemplates = epodTemplateList.stream()
					.filter(t -> ffAccnId.equalsIgnoreCase(t.getEpodAccnFf()))
					.filter(t -> toAccnId.equalsIgnoreCase(t.getEpodAccnTo()))
					.filter(t -> "".equalsIgnoreCase(t.getEpodAccnCo()))
					.collect(Collectors.toList());
		}

		if (matchingTemplates.isEmpty()) {
			matchingTemplates = epodTemplateList.stream()
					.filter(t -> ffAccnId.equalsIgnoreCase(t.getEpodAccnFf()))
					.filter(t -> "".equalsIgnoreCase(t.getEpodAccnTo()))
					.filter(t -> "".equalsIgnoreCase(t.getEpodAccnCo()))
					.collect(Collectors.toList());
		}

		if (matchingTemplates.isEmpty()) {
			matchingTemplates = epodTemplateList.stream()
					.filter(t -> "".equalsIgnoreCase(t.getEpodAccnFf()))
					.filter(t -> toAccnId.equalsIgnoreCase(t.getEpodAccnTo()))
					.filter(t -> "".equalsIgnoreCase(t.getEpodAccnCo()))
					.collect(Collectors.toList());
		}

		if (matchingTemplates.isEmpty()) {
			matchingTemplates = epodTemplateList.stream()
					.filter(t -> "".equalsIgnoreCase(t.getEpodAccnFf()))
					.filter(t -> "".equalsIgnoreCase(t.getEpodAccnTo()))
					.filter(t -> coAccnId.equalsIgnoreCase(t.getEpodAccnCo()))
					.collect(Collectors.toList());
		}

		return matchingTemplates.isEmpty() ? null : matchingTemplates.get(0);
	}

	public ZonedDateTime convertDateTimeBaseOnCountryCode(Date utcDate) throws Exception {
		if (utcDate == null) {
			throw new IllegalArgumentException("UTC date cannot be null");
		}

		String timeZone = "UTC";
		CKCountryConfig countryConfig = accnConfigExService.getCtryEnv();
		if (countryConfig != null && countryConfig.getCountry() != null) {
			String countryCode = countryConfig.getCountry().toUpperCase();
			switch (countryCode) {
				case "SG":
					timeZone = "Asia/Singapore"; // Singapore Time (UTC+8)
					break;
				case "ID":
					timeZone = "Asia/Jakarta"; // Indonesia Time (UTC+7)
					break;
				default:
					timeZone = "UTC";
					break;
			}
		}

		ZonedDateTime utcZonedDateTime = utcDate.toInstant().atZone(ZoneId.of("UTC"));
		ZonedDateTime localZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.of(timeZone));

		log.info("convertDateTimeBaseOnCountryCode - Input Date: " + utcDate +
				", Country: " + (countryConfig != null ? countryConfig.getCountry().toUpperCase() : "Unknown") +
				", UTC Time: " + utcZonedDateTime +
				", Converted Time: " + localZonedDateTime +
				", Final Result: " + Date.from(localZonedDateTime.toInstant()));

		return localZonedDateTime;
	}

	/**
	 * Get jrxml from T_CK_ACCN
	 *
	 * @param jobTruck
	 * @return
	 * @throws Exception
	 */
	private String getJrxmlFromCkAccn(TCkJobTruck jobTruck) throws Exception {

		// FF
		String ffAccnId = jobTruck.getTCoreAccnByJobPartyCoFf().getAccnId();
		TCkAccn ckAccn = ckAccnDao.find(ffAccnId);

		if (ckAccn != null && StringUtils.isNotBlank(ckAccn.getCaccnEpodJrxml())) {
			return ckAccn.getCaccnEpodJrxml();
		} else {
			// TO
			ckAccn = ckAccnDao.find(jobTruck.getTCoreAccnByJobPartyTo().getAccnId());

			if (ckAccn != null && StringUtils.isNotBlank(ckAccn.getCaccnEpodJrxml())) {
				return ckAccn.getCaccnEpodJrxml();
			}
		}
		return null;
	}

	/**
	 * if FF is CSQ, FF-CO is MLS(MILLESIMA), return template in T_CK_ACCN; if FF is
	 * CSQ, FF-CO is NOT MLS(MILLESIMA), return default template; others, return
	 * null;
	 * 
	 * @param jobTruck
	 * @return
	 * @throws Exception
	 */
	@Deprecated // move the logic to TCkCtPodTempalte
	private String getJrxmFileSpecialAccn(TCkJobTruck jobTruck) throws Exception {

		String specialJrxml = null;
		String ffAccnId = jobTruck.getTCoreAccnByJobPartyCoFf().getAccnId();

		// https://jira.vcargocloud.com/browse/CT2SG-223
		// FF is CSP, FF-CO is MLS
		// CLASQUIND
		String csqFfAccnId = sysParam.getValString("CLICTRUCK_CLASQUIND_FF_ACCN", "CSP");
		if (StringUtils.equalsIgnoreCase(ffAccnId, csqFfAccnId)) {

			String ffCoAccnId = Optional.ofNullable(jobTruck.getTCkJob().getTCoreAccnByJobSlAccn())
					.map(TCoreAccn::getAccnId).orElse(null);
			String csqFfCoAccnId = sysParam.getValString("CLICTRUCK_CLASQUIND_FFCO_ACCN", "MLS");

			if (StringUtils.equalsIgnoreCase(ffCoAccnId, csqFfCoAccnId)) {
				TCkAccn ckAccn = ckAccnDao.find(ffAccnId);

				if (ckAccn != null && StringUtils.isNotBlank(ckAccn.getCaccnEpodJrxml())) {
					specialJrxml = ckAccn.getCaccnEpodJrxml();
				}
			}
			if (StringUtils.isBlank(specialJrxml)) {
				specialJrxml = EPOD_DEFAULT_TEMPLATE;
			}
		}

		log.info("ffAccnId: " + ffAccnId + " specialJrxml: " + specialJrxml + " jobId: " + jobTruck.getJobId());

		return specialJrxml;
	}

	@Transactional
	public List<Container> getDetails(String jobId) throws Exception {
		Map<Integer, Container> containerMap = new TreeMap<>(); // TreeMap for auto-sorted order
		List<TCkJobTruckExt> truckExtList = ckJobTruckExtDao.findAllByJobTruckId(jobId);

		for (TCkJobTruckExt ext : truckExtList) {
			String key = ext.getJextKey();
			String value = ext.getJextVal();
			try {
				if (key.startsWith("containers.")) {
					// Extract base key and sequence number
					String[] parts = key.split("\\.");
					String containerKey = parts[1];
					String[] containerParts = containerKey.split("_");

					String baseKey = containerParts[0];
					int seq = containerParts.length > 1 ? Integer.parseInt(containerParts[1]) : 0;

					// Retrieve or create the container for this sequence
					Container currentContainer = containerMap.computeIfAbsent(seq, k -> initializeContainerWithDefaults());

					// Set the container field value
					Field containerField = Container.class.getDeclaredField(baseKey);
					containerField.setAccessible(true);

					if (value != null && !value.trim().isEmpty()) {
						containerField.set(currentContainer, convertValue(containerField.getType(), value));
					}
				}
			} catch (NoSuchFieldException e) {
				log.warn("Key '{}' does not match any field in the Container class: {}"+ key+ e.getMessage());
			} catch (IllegalAccessException e) {
				log.error("Error setting field '{}' in the Container class: {}"+ key + e);
			} catch (NumberFormatException e) {
				log.warn("Invalid sequence format in key '{}': {}"+ key + e.getMessage());
			}
		}

		return new ArrayList<>(containerMap.values());
	}
	/**
	 * Initializes a new Container object with all string fields set to empty strings.
	 */
	private Container initializeContainerWithDefaults() {
		Container container = new Container();
		for (Field field : Container.class.getDeclaredFields()) {
			field.setAccessible(true);
			try {
				if (field.getType().equals(String.class)) {
					field.set(container, "");
				}
			} catch (IllegalAccessException e) {
				log.error("Error initializing field '{}' in Container class: {}"+ field.getName()+ e.getMessage());
			}
		}
		return container;
	}

	private Object convertValue(Class<?> fieldType, String value) {
		if (value == null) {
			return null;
		}
		if (fieldType.equals(String.class)) {
			return value;
		}
		if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
			return Integer.parseInt(value);
		}
		if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
			return Double.parseDouble(value);
		}
		if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
			return Float.parseFloat(value);
		}
		if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
			return Long.parseLong(value);
		}
		if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
			return Boolean.parseBoolean(value);
		}
		return value;
	}

	private String getEPodId(TCkJobTruck jobTruck) throws Exception {
		return Optional.ofNullable(ckJobTruckExtDao.findByJobIdContainEpodId(jobTruck.getJobId()))
				.map(TCkJobTruckExt::getJextVal)
				.orElse("");
	}
	public boolean isSysFreightEPodTemplate(CkCtEpodTemplate podTemplate) throws Exception {
		String sftpSysfreightAccount = auxiliary.getSysParam("SYSFREIGHT_CLICTRUCK_ACCN");

		JSONObject jsonObject = new JSONObject(sftpSysfreightAccount);

		String accnTo = jsonObject.getString("accnTo");
		String accnFf = jsonObject.getString("accnff");
		String accnCo = jsonObject.getString("accnCo");

		boolean matchesAccnFf = Objects.equals(podTemplate.getEpodAccnFf(), accnFf);
		boolean matchesAccnCo = Objects.equals(podTemplate.getEpodAccnCo(), accnCo);
		boolean matchesAccnTo = Objects.equals(podTemplate.getEpodAccnTo(), accnTo);

		return matchesAccnFf || (matchesAccnCo && matchesAccnTo);
	}
	private String getPodFileName(TCkJobTruck jobTruck, Date date) {
		// Format the file name as <datetime>_<jobid>_POD.pdf
		return String.format("%s_%s_POD.pdf", yyyyMMdd_HHmmssSDF.format(date), jobTruck.getJobId());
	}
    private void printParam(Map<String, Object> parameters) {

		log.info("Parameters: " + parameters);

		for (Map.Entry<String, Object> entry : parameters.entrySet()) {
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}
	}
	public static class ItemHelper implements Serializable {

		private static final long serialVersionUID = 1L;

		private int itemNo;
		private String cargoType;
		private String quantity;
		private String qtyUom;
		private String uom;
		private String truckType;
		private String size;
		private String goodsDescription;
		private String weight;
		private String volume;
		private String volumeUom;
		private String sizeUom;
		private String specialInstruction;
		private String marksNo;

		//Extra fields
		private String modelName;
		private String dropOffAddress;
		private String remark;
		private String extPo;
		private String cWeight;
		private String noOfBottle;

		public ItemHelper() {
		}


		public ItemHelper(String cargoType, String quantity) {
			this.cargoType = cargoType;
			this.quantity = quantity;
		}

		public String getCargoType() {
			return cargoType;
		}

		public void setCargoType(String cargoType) {
			this.cargoType = cargoType;
		}

		public String getQuantity() {
			return quantity;
		}

		public void setQuantity(String quantity) {
			this.quantity = quantity;
		}

		public String getTruckType() {
			return truckType;
		}

		public void setTruckType(String truckType) {
			this.truckType = truckType;
		}

		public String getSize() {
			return size;
		}

		public void setSize(String size) {
			this.size = size;
		}

		public String getGoodsDescription() {
			return goodsDescription;
		}

		public void setGoodsDescription(String goodsDescription) {
			this.goodsDescription = goodsDescription;
		}

		public String getWeight() {
			return weight;
		}

		public void setWeight(String weight) {
			this.weight = weight;
		}

		public String getVolume() {
			return volume;
		}

		public void setVolume(String volume) {
			this.volume = volume;
		}

		public String getSpecialInstruction() {
			return specialInstruction;
		}

		public void setSpecialInstruction(String specialInstruction) {
			this.specialInstruction = specialInstruction;
		}

		public String getMarksNo() {
			return marksNo;
		}

		public void setMarksNo(String marksNo) {
			this.marksNo = marksNo;
		}

		public String getUom() {
			return uom;
		}

		public void setUom(String uom) {
			this.uom = uom;
		}

		public String getModelName() {
			return modelName;
		}

		public void setModelName(String modelName) {
			this.modelName = modelName;
		}
		public String getDropOffAddress() {
			return dropOffAddress;
		}

		public void setDropOffAddress(String dropOffAddress) {
			this.dropOffAddress = dropOffAddress;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public String getExtPo() {
			return extPo;
		}

		public void setExtPo(String extPo) {
			this.extPo = extPo;
		}

		public String getcWeight() {
			return cWeight;
		}

		public void setcWeight(String cWeight) {
			this.cWeight = cWeight;
		}

		public int getItemNo() {
			return itemNo;
		}

		public void setItemNo(int itemNo) {
			this.itemNo = itemNo;
		}

		public String getNoOfBottle() {
			return noOfBottle;
		}

		public void setNoOfBottle(String noOfBottle) {
			this.noOfBottle = noOfBottle;
		}

		public String getQtyUom() {
			return qtyUom;
		}

		public void setQtyUom(String qtyUom) {
			this.qtyUom = qtyUom;
		}

		public String getVolumeUom() {
			return volumeUom;
		}

		public void setVolumeUom(String volumeUom) {
			this.volumeUom = volumeUom;
		}

		public String getSizeUom() {
			return sizeUom;
		}

		public void setSizeUom(String sizeUom) {
			this.sizeUom = sizeUom;
		}
	}
}
