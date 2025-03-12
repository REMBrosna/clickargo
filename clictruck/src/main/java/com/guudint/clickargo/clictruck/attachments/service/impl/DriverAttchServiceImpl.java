package com.guudint.clickargo.clictruck.attachments.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.guudint.clickargo.clictruck.attachments.service.ICtAttachmentService;
import com.guudint.clickargo.clictruck.common.dto.CkCtDrv;
import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;
import com.guudint.clickargo.clictruck.dto.CargoFields;
import com.guudint.clickargo.clictruck.dto.DrvOrderDetails;
import com.guudint.clickargo.clictruck.dto.GeneralFields;
import com.guudint.clickargo.clictruck.dto.LocationFields;
import com.guudint.clickargo.clictruck.master.constant.ContainerLoad;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkJobTruckService;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripCargoFm;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripCargoMm;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.job.model.TCkJob;
import com.guudint.clickargo.master.enums.JobStates;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.config.model.TCoreSysparam;

public class DriverAttchServiceImpl implements ICtAttachmentService<CkCtDrv> {

	// Static Attributes
	///////////////////
	private static Logger log = Logger.getLogger(DriverAttchServiceImpl.class);

	@Autowired
	@Qualifier("ckJobDao")
	private GenericDao<TCkJob, String> ckJobDao;

	@Autowired
	private CkJobTruckService ckJobTruckService;
	
	@Autowired
	private GenericDao<TCkCtDrv, String> ckCtDrvDao;
	
	@Autowired
	@Qualifier("coreSysparamDao")
	protected GenericDao<TCoreSysparam, String> coreSysparamDao;
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public String getAttachment(String invNo) 
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception {
		log.debug("getAttachment");
		return null;
	}

	@Override
	public CkCtDrv getAttachmentObj(String dtoId)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Map<String, Object> getAttachmentByJobId(String jobId, Principal principal) throws ParameterException {
		return null;
	}

	@Transactional
	@Override
	public Map<String, Object> getAttachment2(String drvId) throws ParameterException {
		
		if (StringUtils.isBlank(drvId)) {
			throw new ParameterException("param drvId null");
		}
		
		Map<String, Object> jobOrders = new HashMap<>();
		List<DrvOrderDetails> orderDetailsList = new ArrayList<DrvOrderDetails>();
		
		try {
			// What should be the JobStates?
			List<CkJobTruck> ckJobTruckList = ckJobTruckService.findJobTrucksByDrvId(drvId, 
					JobStates.ASG.name(), JobStates.ONGOING.name(), JobStates.DLV.name());
			for (CkJobTruck ckJobTruck : ckJobTruckList) {
				TCkJob ckJob = ckJobDao.find(ckJobTruck.getTCkJob().getJobId());
				TCkCtDrv tckCtDrv =  ckCtDrvDao.find(drvId);
				CkCtTrip tckCtTrip = null != ckJobTruck.getTckCtTripList()
						&& !ObjectUtils.isEmpty(ckJobTruck.getTckCtTripList()) ? ckJobTruck.getTckCtTripList().get(0)
								: null;
				
				if (null != tckCtTrip) {
					DrvOrderDetails orderDetails = new DrvOrderDetails();
					// ckCtCargoFm is always one for firstmile?
					CkCtTripCargoFm ckCtTripCargoFm = null != tckCtTrip.getTckCtTripCargoFmList()
							&& !ObjectUtils.isEmpty(tckCtTrip.getTckCtTripCargoFmList())
									? tckCtTrip.getTckCtTripCargoFmList().get(0)
									: null;
					// ckCtCargoMm can be zero or more?
					List<CkCtTripCargoMm> ckCtTripCargoMm = null != tckCtTrip.getTripCargoMmList()
							&& !ObjectUtils.isEmpty(tckCtTrip.getTripCargoMmList()) ? tckCtTrip.getTripCargoMmList()
									: null;

					GeneralFields generalFields = this.generateGeneralFields(ckJobTruck, ckJob, tckCtDrv, tckCtTrip,
							ckCtTripCargoFm, null != ckCtTripCargoMm ? ckCtTripCargoMm.get(0) : null);
					LocationFields locationFields = this.generateLocationFields(tckCtTrip);
					List<CargoFields> cargoFieldsList = this.generateCargoFields(ckJobTruck, ckJob, ckCtTripCargoFm,
							null != ckCtTripCargoMm ? ckCtTripCargoMm : null);

					orderDetails.setGeneralFields(generalFields);
					orderDetails.setLocationFields(locationFields);
					// Assuming that cargo is a list, to cater multiple cargoes
					orderDetails.setCargoFieldsList(cargoFieldsList);
					orderDetailsList.add(orderDetails);
				}
			}
			
		} catch (Exception ex) {
			log.error("getAttachment2 ", ex);
		}
				
		jobOrders.put("orderDetails", orderDetailsList);
		
		return jobOrders;
	}

	/**
	 * This method generates the cargo information fields in the orders detail
	 * @param ckJobTruck
	 * @param ckJob
	 * @param ckCtTripCargoFm
	 * @param ckCtTripCargoMm
	 * @return
	 */
	private List<CargoFields> generateCargoFields(CkJobTruck ckJobTruck, TCkJob ckJob, CkCtTripCargoFm ckCtTripCargoFm,
			List<CkCtTripCargoMm> ckCtTripCargoMm) {
		
		List<CargoFields> cargoFieldList = new ArrayList<CargoFields>();
			
		int seq = 1;
		if (ckJobTruck.isDomestic()) {
			if (null != ckCtTripCargoMm && !ObjectUtils.isEmpty(ckCtTripCargoMm)) {
				for (CkCtTripCargoMm cargoMm : ckCtTripCargoMm) {
					CargoFields cargoFields = new CargoFields();
					
					// NO CORRESPONDING COLUMN IN T_CK_CT_TRIP_CARGO_MM
					cargoFields.setCargoType(ICkConstant.DASH);
					cargoFields.setSize(ICkConstant.DASH);
					
					// CG_CARGO_WEIGHT
					cargoFields.setWeight(cargoMm != null && cargoMm.getCgCargoWeight() != null && cargoMm.getCgCargoWeight() > 0
							? String.valueOf(cargoMm.getCgCargoWeight())
							: ICkConstant.DASH);
					// CG_CARGO_VOLUMNE
					cargoFields.setVolWeight(cargoMm != null && cargoMm.getCgCargoVolume() != null && cargoMm.getCgCargoVolume() > 0
							? String.valueOf(cargoMm.getCgCargoVolume())
							: ICkConstant.DASH);

					cargoFields.setTruckType(ckJobTruck.getTCkCtMstVehType() != null
									? ckJobTruck.getTCkCtMstVehType().getVhtyDesc()
									: ICkConstant.DASH);
					cargoFields.setStatus(
							ckJob != null && ckJob.getTCkMstJobState() != null
									? ckJob.getTCkMstJobState().getJbstDesc()
									: ICkConstant.DASH);
					cargoFields.setSeq(seq);
					cargoFieldList.add(cargoFields);
					seq++;
				}
			}
		} else {
			CargoFields cargoFields = new CargoFields();
			
			// NO CORRESPONDING COLUMN IN T_CK_CT_TRIP_CARGO_FM
			cargoFields.setCargoType(ICkConstant.DASH);
			cargoFields.setSize(ICkConstant.DASH);
			cargoFields.setWeight(ICkConstant.DASH);
			cargoFields.setVolWeight(ICkConstant.DASH);
			
			cargoFields.setTruckType(
					null != ckJobTruck.getTCkCtMstVehType() ? ckJobTruck.getTCkCtMstVehType().getVhtyDesc()
							: ICkConstant.DASH);
			cargoFields.setStatus(
					null != ckJob.getTCkMstJobState() ? ckJob.getTCkMstJobState().getJbstDesc() : ICkConstant.DASH);
			cargoFields.setSeq(seq);
			cargoFieldList.add(cargoFields);
			seq++;
		}
		
		return cargoFieldList;
	}

	/**
	 * This method generates the location information fields in the orders detail
	 * @param tckCtTrip
	 * @return
	 */
	private LocationFields generateLocationFields(CkCtTrip tckCtTrip) {
		
		LocationFields locationFields = new LocationFields();
		locationFields.setLocationFrom(tckCtTrip.getTCkCtTripLocationByTrFrom().getTCkCtLocation().getLocAddress());
		locationFields.setLocationTo(tckCtTrip.getTCkCtTripLocationByTrTo().getTCkCtLocation().getLocAddress());
		
		return locationFields;
	}

	/**
	 * This method generates the general information fields in the orders detail
	 * @param ckJobTruck
	 * @param ckJob
	 * @param tckCtDrv
	 * @param tckCtTrip
	 * @param ckCtTripCargoFm
	 * @param ckCtTripCargoMm
	 * @return
	 */
	private GeneralFields generateGeneralFields(CkJobTruck ckJobTruck, TCkJob ckJob, TCkCtDrv tckCtDrv,
			CkCtTrip tckCtTrip, CkCtTripCargoFm ckCtTripCargoFm, CkCtTripCargoMm ckCtTripCargoMm) {
		
		GeneralFields generalFields = new GeneralFields();
		
		SimpleDateFormat sdfShort = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		generalFields.setUsername(ckJobTruck.getTCkCtDrv().getDrvMobileId());
		generalFields.setJobNo(ckJobTruck.getJobId());
		generalFields.setCusRefNo(
				StringUtils.isNotEmpty(ckJobTruck.getJobCustomerRef()) ? ckJobTruck.getJobCustomerRef()
						: ICkConstant.DASH);
		generalFields.setShipmentType(null != ckJob.getTCkMstShipmentType()
				&& StringUtils.isNotEmpty(ckJob.getTCkMstShipmentType().getShtName())
						? ckJob.getTCkMstShipmentType().getShtName()
						: ICkConstant.DASH);
		generalFields.setPlanDate(
				null != ckJobTruck.getJobDtPlan() ? sdfLong.format(ckJobTruck.getJobDtPlan()) : ICkConstant.DASH);
		generalFields.setBookDate(null != ckJobTruck.getJobDtBooking() ? sdfShort.format(ckJobTruck.getJobDtBooking())
				: ICkConstant.DASH);
		generalFields
				.setDeliveryDate(null != ckJobTruck.getJobDtDelivery() ? sdfLong.format(ckJobTruck.getJobDtDelivery())
						: ICkConstant.DASH);
		
		if (ckJobTruck.isDomestic()) {
			// CG_CNT_FULL_LOAD NOT IN T_CK_CT_TRIP_CARGO_MM, NO CORRESPONDING VALUE
			generalFields.setLoading(ICkConstant.DASH);
			// CG_CARGO_TYPE IN T_CK_CT_TRIP_CARGO_MM
			generalFields.setGoodsInfo(null != ckCtTripCargoMm && null != ckCtTripCargoMm.getTCkCtMstCargoType()
					? ckCtTripCargoMm.getTCkCtMstCargoType().getCrtypDesc()
					: ICkConstant.DASH);
		} else {
			// CG_CNT_FULL_LOAD IN T_CK_CT_TRIP_CARGO_FM
			String loadingStr = ICkConstant.DASH;
			Character loading = null != ckCtTripCargoFm && null != ckCtTripCargoFm.getCgCntFullLoad()
					? ckCtTripCargoFm.getCgCntFullLoad() : ICkConstant.DASH.charAt(0);
					switch(loading) {
					case 'P':
						loadingStr = ContainerLoad.P.getAltLabel();
						break;
					case 'F':
						loadingStr = ContainerLoad.F.getAltLabel();
						break;
					case 'E':
						loadingStr = ContainerLoad.E.getAltLabel();
						break;
					default:
						loadingStr = ICkConstant.DASH;
						break;
					}
			generalFields.setLoading(loadingStr);
			// CG_CARGO_TYPE IN T_CK_CT_TRIP_CARGO_FM
			generalFields.setGoodsInfo(null != ckCtTripCargoFm && null != ckCtTripCargoFm.getTCkCtMstCargoType()
					? ckCtTripCargoFm.getTCkCtMstCargoType().getCrtypDesc()
					: ICkConstant.DASH);
		}

		generalFields.setEstimatedPricing(
				null != ckJobTruck.getJobTotalCharge() ? ckJobTruck.getJobTotalCharge() : new BigDecimal(0));
		generalFields.setEmailNotif(null != tckCtDrv.getDrvEmail() ? tckCtDrv.getDrvEmail() : ICkConstant.DASH);
		
		return generalFields;
	}

}
