package com.guudint.clickargo.clictruck.planexec.job.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.guudint.clickargo.clictruck.common.dto.CkCtDept;
import com.guudint.clickargo.clictruck.common.dto.CkCtDrv;
import com.guudint.clickargo.clictruck.common.dto.CkCtVeh;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtDebitNote;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtToInvoice;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehType;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripLocation;
import com.guudint.clickargo.common.enums.JobActions;
import com.guudint.clickargo.job.dto.CkJob;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkJobTruck extends AbstractDTO<CkJobTruck, TCkJobTruck> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 4325845488472059155L;

	// Attributes
	/////////////
	private String jobId;
	private CkCtContactDetail TCkCtContactDetailByJobContactTo;
	private CkCtContactDetail TCkCtContactDetailByJobContactCoFf;
	private CkCtDebitNote TCkCtDebitNoteByJobInvoiceeDebitNote;
	private CkCtDebitNote TCkCtDebitNoteByJobInvoicerDebitNote;
	private CkCtDrv TCkCtDrv;
	private Map<String, Object> jobDrvOth;
	private CkCtMstVehType TCkCtMstVehType;
	private CkCtToInvoice TCkCtToInvoice;
	private CkCtVeh TCkCtVeh;
	private Map<String, Object> jobVehOth;
	private CkJob TCkJob;
	private CoreAccn TCoreAccnByJobPartyCoFf;
	private CoreAccn TCoreAccnByJobPartyTo;
	private Date jobDtBooking;
	private Date jobDtPlan;
	private Date jobDtDelivery;
	private Date jobDtStart;
	private Date jobDtEnd;
	private Short jobNoTrips;
	private String jobShipmentRef;
	private String jobCustomerRef;
	private BigDecimal jobTotalCharge;
	private BigDecimal jobTotalReimbursements;
	private BigDecimal jobTotalPlatformFees;
	private String jobInPaymentState;
	private String jobOutPaymentState;
	private Character jobStatus;
	private Date jobDtCreate;
	private String jobUidCreate;
	private Date jobDtLupd;
	private String jobUidLupd;
	private Date jobInPaymentDtDue;
	private Date jobOutPaymentDtDue;

	private JobActions action;
	private String history;
	// holder from frontend to determine if to display bill jobs for co/ff to accept
	// or reject
	private boolean isBilledJob;

	// holder from frontend to determine if to display jobs for billing for TO to
	// add invoice
	private boolean isJobForBilling;

	private List<CkCtTrip> tckCtTripList;
	private CkCtTripLocation pickUp;
	private CkCtTripLocation lastDrop;
	private boolean isDomestic;

	// holder for the shipment type to be populated later when saving
	private String shipmentType;

	private Boolean generalDetailsClone;
	private Boolean partiesDetailsClone;
	private Boolean contactDetailsClone;
	private Boolean locationDetailsClone;
	private Boolean cargoDetailsClone;
	private Integer numberOfCopy;

	// holder for the TO invoices uploaded
	private List<CkCtToInvoice> toInvoiceList;

	// TODO to remove below
	// Holder for reject remarks
	private boolean hasRejections;
	private String rejectRemarks;

	// Holder for remarks : reject | reject_bill | verify_bill | approve_bill
	private boolean hasRemarks;
	private String jobRemarks;
	private String auditRemark;

	// 2.1.0
	private Character jobIsFinanced;
	private Character jobMobileEnabled;
	private String jobSource;

	// 2.1.0 SG Requirement
	private List<String> hiddenFields;
	// Holder for customer ref (mawb/hawb)
	private String driverRefNo;

	private boolean isMissingDropOff;
	private boolean isMissingPickup;
	private boolean isModifyAcceptTrip;
	private boolean ignorePickupDropOfAtt;

	// 2.1.0 SG Requirement for Additional Configurable fields
	private boolean isShowAdditionalFields;

	// OPM
	private String jobFinanceOpt;
	private String jobFinancer;
	private Date jobDtOpmUtilize;

	private List<CkJobTruckAddAttr> addtlFields;

	// 2.1.1 SG2 Department categorization
	private CkCtDept TCkCtDeptByJobCoDepartment;
	private CkCtDept TCkCtDeptByJobToDepartment;

	//Sagawa sum Qty

	private String sumQty;
	private String sumWeight;

	private String mawb;
	private String hawb;

	// Constructors
	///////////////
	public CkJobTruck() {
	}

	public CkJobTruck(TCkJobTruck entity) {
		super(entity);
	}

	/**
	 * @param jobId
	 * @param tCkCtContactDetailByJobContactTo
	 * @param tCkCtContactDetailByJobContactCoFf
	 * @param tCkCtDebitNoteByJobInvoiceeDebitNote
	 * @param tCkCtDebitNoteByJobInvoicerDebitNote
	 * @param tCkCtDrv
	 * @param jobDrvOth
	 * @param tCkCtMstVehType
	 * @param tCkCtToInvoice
	 * @param tCkCtVeh
	 * @param jobVehOth
	 * @param tCkJob
	 * @param tCoreAccnByJobPartyCoFf
	 * @param tCoreAccnByJobPartyTo
	 * @param jobDtBooking
	 * @param jobDtPlan
	 * @param jobDtDelivery
	 * @param jobDtStart
	 * @param jobDtEnd
	 * @param jobNoTrips
	 * @param jobShipmentRef
	 * @param jobCustomerRef
	 * @param jobStatus
	 * @param jobDtCreate
	 * @param jobUidCreate
	 * @param jobDtLupd
	 * @param jobUidLupd
	 */
	public CkJobTruck(String jobId, CkCtContactDetail tCkCtContactDetailByJobContactTo,
			CkCtContactDetail tCkCtContactDetailByJobContactCoFf, CkCtDebitNote tCkCtDebitNoteByJobInvoiceeDebitNote,
			CkCtDebitNote tCkCtDebitNoteByJobInvoicerDebitNote, CkCtDrv tCkCtDrv, Map<String, Object> DrvOthDto,
			CkCtMstVehType tCkCtMstVehType, CkCtToInvoice tCkCtToInvoice, CkCtVeh tCkCtVeh,
			Map<String, Object> jobVehOth, CkJob tCkJob, CoreAccn tCoreAccnByJobPartyCoFf,
			CoreAccn tCoreAccnByJobPartyTo, Date jobDtBooking, Date jobDtPlan, Date jobDtDelivery, Date jobDtStart,
			Date jobDtEnd, Short jobNoTrips, Character jobStatus, String jobShipmentRef, String jobCustomerRef,
			Date jobDtCreate, String jobUidCreate, Date jobDtLupd, String jobUidLupd) {
		super();
		this.jobId = jobId;
		TCkCtContactDetailByJobContactTo = tCkCtContactDetailByJobContactTo;
		TCkCtContactDetailByJobContactCoFf = tCkCtContactDetailByJobContactCoFf;
		TCkCtDebitNoteByJobInvoiceeDebitNote = tCkCtDebitNoteByJobInvoiceeDebitNote;
		TCkCtDebitNoteByJobInvoicerDebitNote = tCkCtDebitNoteByJobInvoicerDebitNote;
		TCkCtDrv = tCkCtDrv;
		this.jobDrvOth = DrvOthDto;
		TCkCtMstVehType = tCkCtMstVehType;
		TCkCtToInvoice = tCkCtToInvoice;
		TCkCtVeh = tCkCtVeh;
		this.jobVehOth = jobVehOth;
		TCkJob = tCkJob;
		TCoreAccnByJobPartyCoFf = tCoreAccnByJobPartyCoFf;
		TCoreAccnByJobPartyTo = tCoreAccnByJobPartyTo;
		this.jobDtBooking = jobDtBooking;
		this.jobDtPlan = jobDtPlan;
		this.jobDtDelivery = jobDtDelivery;
		this.jobDtStart = jobDtStart;
		this.jobDtEnd = jobDtEnd;
		this.jobNoTrips = jobNoTrips;
		this.jobShipmentRef = jobShipmentRef;
		this.jobCustomerRef = jobCustomerRef;
		this.jobStatus = jobStatus;
		this.jobDtCreate = jobDtCreate;
		this.jobUidCreate = jobUidCreate;
		this.jobDtLupd = jobDtLupd;
		this.jobUidLupd = jobUidLupd;
	}

	@Override
	public String getDtoId() {
		return this.getJobId();
	}

	///////
	public BigDecimal computeTotalAmt() {

		BigDecimal totalAmt = BigDecimal.ZERO;

		if (null != jobTotalCharge) {
			totalAmt = totalAmt.add(jobTotalCharge);
		}

		if (null != jobTotalReimbursements) {
			totalAmt = totalAmt.add(jobTotalReimbursements);
		}

		return totalAmt;
	}

	// Properties
	/////////////
	/**
	 * @return the jobId
	 */
	public String getJobId() {
		return jobId;
	}

	/**
	 * @param jobId the jobId to set
	 */
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	/**
	 * @return the tCkCtContactDetailByJobContactTo
	 */
	public CkCtContactDetail getTCkCtContactDetailByJobContactTo() {
		return TCkCtContactDetailByJobContactTo;
	}

	/**
	 * @param tCkCtContactDetailByJobContactTo the tCkCtContactDetailByJobContactTo
	 *                                         to set
	 */
	public void setTCkCtContactDetailByJobContactTo(CkCtContactDetail tCkCtContactDetailByJobContactTo) {
		TCkCtContactDetailByJobContactTo = tCkCtContactDetailByJobContactTo;
	}

	/**
	 * @return the tCkCtContactDetailByJobContactCoFf
	 */
	public CkCtContactDetail getTCkCtContactDetailByJobContactCoFf() {
		return TCkCtContactDetailByJobContactCoFf;
	}

	/**
	 * @param tCkCtContactDetailByJobContactCoFf the
	 *                                           tCkCtContactDetailByJobContactCoFf
	 *                                           to set
	 */
	public void setTCkCtContactDetailByJobContactCoFf(CkCtContactDetail tCkCtContactDetailByJobContactCoFf) {
		TCkCtContactDetailByJobContactCoFf = tCkCtContactDetailByJobContactCoFf;
	}

	/**
	 * @return the tCkCtDebitNoteByJobInvoiceeDebitNote
	 */
	public CkCtDebitNote getTCkCtDebitNoteByJobInvoiceeDebitNote() {
		return TCkCtDebitNoteByJobInvoiceeDebitNote;
	}

	/**
	 * @param tCkCtDebitNoteByJobInvoiceeDebitNote the
	 *                                             tCkCtDebitNoteByJobInvoiceeDebitNote
	 *                                             to set
	 */
	public void setTCkCtDebitNoteByJobInvoiceeDebitNote(CkCtDebitNote tCkCtDebitNoteByJobInvoiceeDebitNote) {
		TCkCtDebitNoteByJobInvoiceeDebitNote = tCkCtDebitNoteByJobInvoiceeDebitNote;
	}

	/**
	 * @return the tCkCtDebitNoteByJobInvoicerDebitNote
	 */
	public CkCtDebitNote getTCkCtDebitNoteByJobInvoicerDebitNote() {
		return TCkCtDebitNoteByJobInvoicerDebitNote;
	}

	/**
	 * @param tCkCtDebitNoteByJobInvoicerDebitNote the
	 *                                             tCkCtDebitNoteByJobInvoicerDebitNote
	 *                                             to set
	 */
	public void setTCkCtDebitNoteByJobInvoicerDebitNote(CkCtDebitNote tCkCtDebitNoteByJobInvoicerDebitNote) {
		TCkCtDebitNoteByJobInvoicerDebitNote = tCkCtDebitNoteByJobInvoicerDebitNote;
	}

	/**
	 * @return the tCkCtDrv
	 */
	public CkCtDrv getTCkCtDrv() {
		return TCkCtDrv;
	}

	/**
	 * @param tCkCtDrv the tCkCtDrv to set
	 */
	public void setTCkCtDrv(CkCtDrv tCkCtDrv) {
		TCkCtDrv = tCkCtDrv;
	}

	public Map<String, Object> getJobDrvOth() {
		return jobDrvOth;
	}

	public void setJobDrvOth(Map<String, Object> jobDrvOth) {
		this.jobDrvOth = jobDrvOth;
	}

	/**
	 * @return the tCkCtMstVehType
	 */
	public CkCtMstVehType getTCkCtMstVehType() {
		return TCkCtMstVehType;
	}

	/**
	 * @param tCkCtMstVehType the tCkCtMstVehType to set
	 */
	public void setTCkCtMstVehType(CkCtMstVehType tCkCtMstVehType) {
		TCkCtMstVehType = tCkCtMstVehType;
	}

	/**
	 * @return the tCkCtToInvoice
	 */
	public CkCtToInvoice getTCkCtToInvoice() {
		return TCkCtToInvoice;
	}

	/**
	 * @param tCkCtToInvoice the tCkCtToInvoice to set
	 */
	public void setTCkCtToInvoice(CkCtToInvoice tCkCtToInvoice) {
		TCkCtToInvoice = tCkCtToInvoice;
	}

	/**
	 * @return the tCkCtVeh
	 */
	public CkCtVeh getTCkCtVeh() {
		return TCkCtVeh;
	}

	/**
	 * @param tCkCtVeh the tCkCtVeh to set
	 */
	public void setTCkCtVeh(CkCtVeh tCkCtVeh) {
		TCkCtVeh = tCkCtVeh;
	}

	public Map<String, Object> getJobVehOth() {
		return jobVehOth;
	}

	public void setJobVehOth(Map<String, Object> JobVehOth) {
		this.jobVehOth = JobVehOth;
	}

	/**
	 * @return the tCkJob
	 */
	public CkJob getTCkJob() {
		return TCkJob;
	}

	/**
	 * @param tCkJob the tCkJob to set
	 */
	public void setTCkJob(CkJob tCkJob) {
		TCkJob = tCkJob;
	}

	/**
	 * @return the tCoreAccnByJobPartyCoFf
	 */
	public CoreAccn getTCoreAccnByJobPartyCoFf() {
		return TCoreAccnByJobPartyCoFf;
	}

	/**
	 * @param tCoreAccnByJobPartyCoFf the tCoreAccnByJobPartyCoFf to set
	 */
	public void setTCoreAccnByJobPartyCoFf(CoreAccn tCoreAccnByJobPartyCoFf) {
		TCoreAccnByJobPartyCoFf = tCoreAccnByJobPartyCoFf;
	}

	/**
	 * @return the tCoreAccnByJobPartyTo
	 */
	public CoreAccn getTCoreAccnByJobPartyTo() {
		return TCoreAccnByJobPartyTo;
	}

	/**
	 * @param tCoreAccnByJobPartyTo the tCoreAccnByJobPartyTo to set
	 */
	public void setTCoreAccnByJobPartyTo(CoreAccn tCoreAccnByJobPartyTo) {
		TCoreAccnByJobPartyTo = tCoreAccnByJobPartyTo;
	}

	/**
	 * @return the jobDtBooking
	 */
	public Date getJobDtBooking() {
		return jobDtBooking;
	}

	/**
	 * @param jobDtBooking the jobDtBooking to set
	 */
	public void setJobDtBooking(Date jobDtBooking) {
		this.jobDtBooking = jobDtBooking;
	}

	/**
	 * @return the jobDtPlan
	 */
	public Date getJobDtPlan() {
		return jobDtPlan;
	}

	/**
	 * @param jobDtPlan the jobDtPlan to set
	 */
	public void setJobDtPlan(Date jobDtPlan) {
		this.jobDtPlan = jobDtPlan;
	}

	/**
	 * @return the jobDtDelivery
	 */
	public Date getJobDtDelivery() {
		return jobDtDelivery;
	}

	/**
	 * @param jobDtDelivery the jobDtDelivery to set
	 */
	public void setJobDtDelivery(Date jobDtDelivery) {
		this.jobDtDelivery = jobDtDelivery;
	}

	/**
	 * @return the jobDtStart
	 */
	public Date getJobDtStart() {
		return jobDtStart;
	}

	/**
	 * @param jobDtStart the jobDtStart to set
	 */
	public void setJobDtStart(Date jobDtStart) {
		this.jobDtStart = jobDtStart;
	}

	/**
	 * @return the jobDtEnd
	 */
	public Date getJobDtEnd() {
		return jobDtEnd;
	}

	/**
	 * @param jobDtEnd the jobDtEnd to set
	 */
	public void setJobDtEnd(Date jobDtEnd) {
		this.jobDtEnd = jobDtEnd;
	}

	/**
	 * @return the jobNoTrips
	 */
	public Short getJobNoTrips() {
		return jobNoTrips;
	}

	/**
	 * @param jobNoTrips the jobNoTrips to set
	 */
	public void setJobNoTrips(Short jobNoTrips) {
		this.jobNoTrips = jobNoTrips;
	}

	/**
	 * @return the jobShipmentRef
	 */
	public String getJobShipmentRef() {
		return jobShipmentRef;
	}

	/**
	 * @param jobShipmentRef the jobShipmentRef to set
	 */
	public void setJobShipmentRef(String jobShipmentRef) {
		this.jobShipmentRef = jobShipmentRef;
	}

	/**
	 * @return the jobCustomerRef
	 */
	public String getJobCustomerRef() {
		return jobCustomerRef;
	}

	/**
	 * @param jobCustomerRef the jobCustomerRef to set
	 */
	public void setJobCustomerRef(String jobCustomerRef) {
		this.jobCustomerRef = jobCustomerRef;
	}

	/**
	 * @return the jobStatus
	 */
	public Character getJobStatus() {
		return jobStatus;
	}

	/**
	 * @param jobStatus the jobStatus to set
	 */
	public void setJobStatus(Character jobStatus) {
		this.jobStatus = jobStatus;
	}

	/**
	 * @return the jobDtCreate
	 */
	public Date getJobDtCreate() {
		return jobDtCreate;
	}

	/**
	 * @param jobDtCreate the jobDtCreate to set
	 */
	public void setJobDtCreate(Date jobDtCreate) {
		this.jobDtCreate = jobDtCreate;
	}

	/**
	 * @return the jobUidCreate
	 */
	public String getJobUidCreate() {
		return jobUidCreate;
	}

	/**
	 * @param jobUidCreate the jobUidCreate to set
	 */
	public void setJobUidCreate(String jobUidCreate) {
		this.jobUidCreate = jobUidCreate;
	}

	/**
	 * @return the jobDtLupd
	 */
	public Date getJobDtLupd() {
		return jobDtLupd;
	}

	/**
	 * @param jobDtLupd the jobDtLupd to set
	 */
	public void setJobDtLupd(Date jobDtLupd) {
		this.jobDtLupd = jobDtLupd;
	}

	/**
	 * @return the jobUidLupd
	 */
	public String getJobUidLupd() {
		return jobUidLupd;
	}

	/**
	 * @param jobUidLupd the jobUidLupd to set
	 */
	public void setJobUidLupd(String jobUidLupd) {
		this.jobUidLupd = jobUidLupd;
	}

	/**
	 * @return the action
	 */
	public JobActions getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(JobActions action) {
		this.action = action;
	}

	/**
	 * @return the history
	 */
	public String getHistory() {
		return history;
	}

	/**
	 * @param history the history to set
	 */
	public void setHistory(String history) {
		this.history = history;
	}

	/**
	 * @return the tckCtTripList
	 */
	public List<CkCtTrip> getTckCtTripList() {
		return tckCtTripList;
	}

	/**
	 * @param tckCtTripList the tckCtTripList to set
	 */
	public void setTckCtTripList(List<CkCtTrip> tckCtTripList) {
		this.tckCtTripList = tckCtTripList;
	}

	/**
	 * @return the pickUp
	 */
	public CkCtTripLocation getPickUp() {
		return pickUp;
	}

	/**
	 * @param pickUp the pickUp to set
	 */
	public void setPickUp(CkCtTripLocation pickUp) {
		this.pickUp = pickUp;
	}

	/**
	 * @return the lastDrop
	 */
	public CkCtTripLocation getLastDrop() {
		return lastDrop;
	}

	/**
	 * @param lastDrop the lastDrop to set
	 */
	public void setLastDrop(CkCtTripLocation lastDrop) {
		this.lastDrop = lastDrop;
	}

	public String getShipmentType() {
		return shipmentType;
	}

	public void setShipmentType(String shipmentType) {
		this.shipmentType = shipmentType;
	}

	public boolean isDomestic() {
		return isDomestic;
	}

	public void setDomestic(boolean isDomestic) {
		this.isDomestic = isDomestic;
	}

	public boolean isBilledJob() {
		return isBilledJob;
	}

	public void setBilledJob(boolean isBilledJob) {
		this.isBilledJob = isBilledJob;
	}

	public boolean isJobForBilling() {
		return isJobForBilling;
	}

	public void setJobForBilling(boolean isJobForBilling) {
		this.isJobForBilling = isJobForBilling;
	}

	public BigDecimal getJobTotalCharge() {
		return jobTotalCharge;
	}

	public void setJobTotalCharge(BigDecimal jobTotalCharge) {
		this.jobTotalCharge = jobTotalCharge;
	}

	public String getJobInPaymentState() {
		return jobInPaymentState;
	}

	public void setJobInPaymentState(String jobInPaymentState) {
		this.jobInPaymentState = jobInPaymentState;
	}

	public String getJobOutPaymentState() {
		return jobOutPaymentState;
	}

	public void setJobOutPaymentState(String jobOutPaymentState) {
		this.jobOutPaymentState = jobOutPaymentState;
	}

	public BigDecimal getJobTotalReimbursements() {
		return jobTotalReimbursements;
	}

	public void setJobTotalReimbursements(BigDecimal jobTotalReimbursements) {
		this.jobTotalReimbursements = jobTotalReimbursements;
	}

	public BigDecimal getJobTotalPlatformFees() {
		return jobTotalPlatformFees;
	}

	public void setJobTotalPlatformFees(BigDecimal jobTotalPlatformFees) {
		this.jobTotalPlatformFees = jobTotalPlatformFees;
	}

	public Boolean getGeneralDetailsClone() {
		return generalDetailsClone;
	}

	public void setGeneralDetailsClone(Boolean generalDetailsClone) {
		this.generalDetailsClone = generalDetailsClone;
	}

	public Boolean getPartiesDetailsClone() {
		return partiesDetailsClone;
	}

	public void setPartiesDetailsClone(Boolean partiesDetailsClone) {
		this.partiesDetailsClone = partiesDetailsClone;
	}

	public Boolean getContactDetailsClone() {
		return contactDetailsClone;
	}

	public void setContactDetailsClone(Boolean contactDetailsClone) {
		this.contactDetailsClone = contactDetailsClone;
	}

	public Boolean getLocationDetailsClone() {
		return locationDetailsClone;
	}

	public void setLocationDetailsClone(Boolean locationDetailsClone) {
		this.locationDetailsClone = locationDetailsClone;
	}

	public Boolean getCargoDetailsClone() {
		return cargoDetailsClone;
	}

	public void setCargoDetailsClone(Boolean cargoDetailsClone) {
		this.cargoDetailsClone = cargoDetailsClone;
	}

	public Integer getNumberOfCopy() {
		return numberOfCopy;
	}

	public void setNumberOfCopy(Integer numberOfCopy) {
		this.numberOfCopy = numberOfCopy;
	}

	public List<CkCtToInvoice> getToInvoiceList() {
		return toInvoiceList;
	}

	public void setToInvoiceList(List<CkCtToInvoice> toInvoiceList) {
		this.toInvoiceList = toInvoiceList;
	}

	public boolean isHasRejections() {
		return hasRejections;
	}

	public void setHasRejections(boolean hasRejections) {
		this.hasRejections = hasRejections;
	}

	public String getRejectRemarks() {
		return rejectRemarks;
	}

	public void setRejectRemarks(String rejectRemarks) {
		this.rejectRemarks = rejectRemarks;
	}

	public Date getJobInPaymentDtDue() {
		return jobInPaymentDtDue;
	}

	public void setJobInPaymentDtDue(Date jobInPaymentDtDue) {
		this.jobInPaymentDtDue = jobInPaymentDtDue;
	}

	public Date getJobOutPaymentDtDue() {
		return jobOutPaymentDtDue;
	}

	public void setJobOutPaymentDtDue(Date jobOutPaymentDtDue) {
		this.jobOutPaymentDtDue = jobOutPaymentDtDue;
	}

	// Override Methods
	///////////////////
	/**
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * 
	 */
	@Override
	public int compareTo(CkJobTruck o) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.COAbstractEntity#init()
	 * 
	 */
	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the jobRemarks
	 */
	public String getJobRemarks() {
		return jobRemarks;
	}

	/**
	 * @param jobRemarks the jobRemarks to set
	 */
	public void setJobRemarks(String jobRemarks) {
		this.jobRemarks = jobRemarks;
	}

	/**
	 * @return the hasRemarks
	 */
	public boolean isHasRemarks() {
		return hasRemarks;
	}

	/**
	 * @param hasRemarks the hasRemarks to set
	 */
	public void setHasRemarks(boolean hasRemarks) {
		this.hasRemarks = hasRemarks;
	}

	/**
	 * @return the jobIsFinanced
	 */
	public Character getJobIsFinanced() {
		return jobIsFinanced;
	}

	/**
	 * @param jobIsFinanced the jobIsFinanced to set
	 */
	public void setJobIsFinanced(Character jobIsFinanced) {
		this.jobIsFinanced = jobIsFinanced;
	}

	public Character getJobMobileEnabled() {
		return jobMobileEnabled;
	}

	public void setJobMobileEnabled(Character jobMobileEnabled) {
		this.jobMobileEnabled = jobMobileEnabled;
	}

	public String getJobSource() {
		return jobSource;
	}

	public void setJobSource(String jobSource) {
		this.jobSource = jobSource;
	}

	public List<String> getHiddenFields() {
		return hiddenFields;
	}

	public void setHiddenFields(List<String> hiddenFields) {
		this.hiddenFields = hiddenFields;
	}

	/**
	 * @return the isMissingDropOff
	 */
	public boolean isMissingDropOff() {
		return isMissingDropOff;
	}

	/**
	 * @param isMissingDropOff the isMissingDropOff to set
	 */
	public void setMissingDropOff(boolean isMissingDropOff) {
		this.isMissingDropOff = isMissingDropOff;
	}

	/**
	 * @return the isMissingPickup
	 */
	public boolean isMissingPickup() {
		return isMissingPickup;
	}

	/**
	 * @param isMissingPickup the isMissingPickup to set
	 */
	public void setMissingPickup(boolean isMissingPickup) {
		this.isMissingPickup = isMissingPickup;
	}

	public String getDriverRefNo() {
		return driverRefNo;
	}

	public void setDriverRefNo(String driverRefNo) {
		this.driverRefNo = driverRefNo;
	}

	public boolean isShowAdditionalFields() {
		return isShowAdditionalFields;
	}

	public void setShowAdditionalFields(boolean isShowAdditionalFields) {
		this.isShowAdditionalFields = isShowAdditionalFields;
	}

	public List<CkJobTruckAddAttr> getAddtlFields() {
		return addtlFields;
	}

	public void setAddtlFields(List<CkJobTruckAddAttr> addtlFields) {
		this.addtlFields = addtlFields;
	}

	public String getJobFinanceOpt() {
		return jobFinanceOpt;
	}

	public void setJobFinanceOpt(String jobFinanceOpt) {
		this.jobFinanceOpt = jobFinanceOpt;
	}

	public String getJobFinancer() {
		return jobFinancer;
	}

	public void setJobFinancer(String jobFinancer) {
		this.jobFinancer = jobFinancer;
	}

	public Date getJobDtOpmUtilize() {
		return jobDtOpmUtilize;
	}

	public void setJobDtOpmUtilize(Date jobDtOpmUtilize) {
		this.jobDtOpmUtilize = jobDtOpmUtilize;
	}

	/**
	 * @return the tCkCtDeptByJobCoDepartment
	 */
	public CkCtDept getTCkCtDeptByJobCoDepartment() {
		return TCkCtDeptByJobCoDepartment;
	}

	/**
	 * @param tCkCtDeptByJobCoDepartment the tCkCtDeptByJobCoDepartment to set
	 */
	public void setTCkCtDeptByJobCoDepartment(CkCtDept tCkCtDeptByJobCoDepartment) {
		TCkCtDeptByJobCoDepartment = tCkCtDeptByJobCoDepartment;
	}

	/**
	 * @return the tCkCtDeptByJobToDepartment
	 */
	public CkCtDept getTCkCtDeptByJobToDepartment() {
		return TCkCtDeptByJobToDepartment;
	}

	/**
	 * @param tCkCtDeptByJobToDepartment the tCkCtDeptByJobToDepartment to set
	 */
	public void setTCkCtDeptByJobToDepartment(CkCtDept tCkCtDeptByJobToDepartment) {
		TCkCtDeptByJobToDepartment = tCkCtDeptByJobToDepartment;
	}

	public boolean isModifyAcceptTrip() {
		return isModifyAcceptTrip;
	}

	public void setModifyAcceptTrip(boolean modifyAcceptTrip) {
		isModifyAcceptTrip = modifyAcceptTrip;
	}

	public boolean isIgnorePickupDropOfAtt() {
		return ignorePickupDropOfAtt;
	}

	public void setIgnorePickupDropOfAtt(boolean ignorePickupDropOfAtt) {
		this.ignorePickupDropOfAtt = ignorePickupDropOfAtt;
	}

	public String getSumQty() {
		return sumQty;
	}

	public void setSumQty(String sumQty) {
		this.sumQty = sumQty;
	}

	public String getSumWeight() {
		return sumWeight;
	}

	public void setSumWeight(String sumWeight) {
		this.sumWeight = sumWeight;
	}

	public String getMawb() {
		return mawb;
	}

	public void setMawb(String mawb) {
		this.mawb = mawb;
	}

	public String getHawb() {
		return hawb;
	}

	public void setHawb(String hawb) {
		this.hawb = hawb;
	}

	public String getAuditRemark() {
		return auditRemark;
	}

	public void setAuditRemark(String auditRemark) {
		this.auditRemark = auditRemark;
	}
}
