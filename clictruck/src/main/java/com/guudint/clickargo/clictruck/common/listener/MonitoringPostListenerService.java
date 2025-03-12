package com.guudint.clickargo.clictruck.common.listener;

import com.guudint.clickargo.admin.service.util.ClickargoAccnService;
import com.guudint.clickargo.clictruck.common.dto.CkCtDrv;
import com.guudint.clickargo.clictruck.common.dto.CkCtVeh;
import com.guudint.clickargo.clictruck.common.service.impl.CkCtDrvServiceImpl;
import com.guudint.clickargo.clictruck.common.service.impl.CkCtVehServiceImpl;
import com.guudint.clickargo.clictruck.notification.dto.CkCtAlert;
import com.guudint.clickargo.clictruck.notification.model.TCkCtAlert;
import com.guudint.clickargo.common.enums.ClickargoNotifTemplates;
import com.guudint.clickargo.common.enums.WorkflowTypeEnum;
import com.guudint.clickargo.common.event.listener.IMonitoringListener;
import com.guudint.clickargo.common.service.impl.CkNotificationUtilService;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.cac.model.TCoreUsrRole;
import com.vcc.camelone.cac.model.TCoreUsrRoleId;
import com.vcc.camelone.can.device.NotificationParam;
import com.vcc.camelone.can.service.impl.NotificationService;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.config.model.TCoreSysparam;
import com.vcc.camelone.core.model.TCoreApps;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Optional;

public class MonitoringPostListenerService implements IMonitoringListener<TCkCtAlert, CkCtAlert> {

	private static Logger LOG = Logger.getLogger(MonitoringPostListenerService.class);

	@Autowired
	protected CkNotificationUtilService notificationUtilService;

	@Autowired
	protected ClickargoAccnService clickargoAccnService;

	@Autowired
	@Qualifier("coreAppsDao")
	private GenericDao<TCoreApps, String> coreAppsDao;
	
	@Autowired
	protected GenericDao<TCoreUsrRole, TCoreUsrRoleId> coreUsrRoleDao;
	@Autowired
	protected NotificationService notificationService;

	@Autowired
	private CkCtVehServiceImpl ckCtVehService;
	@Autowired
	private CkCtDrvServiceImpl ckCtDrvService;
	@Autowired
	protected GenericDao<TCoreSysparam, String> coreSysparamDao;

	private final String url = "/manageAccount/view/";

	public void sendEmailMonitoring(CkCtAlert dto, Principal principal) throws Exception {
		LOG.info("sendEmailMonitoring");
		if (null != dto) {
			notificationUtilService.saveNotificationLog(notificationParam(dto).toJson(), principal, true);
		}
	}
	
	private NotificationParam notificationParam(CkCtAlert dto) throws Exception{
		NotificationParam param = new NotificationParam();

		ArrayList<String> recipients = new ArrayList<>();
		recipients.add(dto.getAltRepCon());
		param.setRecipients(recipients);
		HashMap<String, String> subjectFields = new HashMap<>();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		
		TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
		
		HashMap<String, String> contentFields = new HashMap<>();
		CkCtVeh ckCtVeh = ckCtVehService.findById(dto.getAltReferId());
		CkCtDrv ckCtDrv = ckCtDrvService.findById(dto.getAltReferId());
		switch (dto.getCkCtMstAlert().getAltName()) {
		case "Vehicle Maintenance":
			if(dto.getCkCtMstAlert().getAltConditionType().equalsIgnoreCase("DISTANCE")) {
				param.setTemplateId(ClickargoNotifTemplates.VEH_DISTANCE.getId());
				subjectFields.put(":subject", dto.getCkCtMstAlert().getAltName());
				contentFields.put(":monitoring", dto.getCkCtMstAlert().getAltName() + " " + dto.getAltReferId());
				contentFields.put(":accnName", ckCtVeh.getTCoreAccn().getAccnName() != null ? ckCtVeh.getTCoreAccn().getAccnName() : "-");
				contentFields.put(":accnRegLink", apps != null ? apps.getAppsLaunchUrl() + encodeUrlId(ckCtVeh.getTCoreAccn().getAccnId()) : "-");
				if ("SG".equalsIgnoreCase(dto.getCoreAccn().getCityCode())) {
					Optional<TCoreSysparam> opTCoreSysparam = Optional.ofNullable(coreSysparamDao.find("CLICTRUCK_ADDR_SG_API_CALL"));
					if (opTCoreSysparam.isPresent()) {
						try {
							TCoreSysparam sysparam = opTCoreSysparam.get();
							JSONObject jsonSpDetails = new JSONObject(sysparam.getSysVal());

							// Construct the sp_details string
							String spDetailsMapped = jsonSpDetails.getString("accnName") + " " +
									jsonSpDetails.getString("addrCity") + " " +
									jsonSpDetails.getString("addrPcode") + ", " +
									jsonSpDetails.getString("ctyDescription");

							contentFields.put(":sp_details", spDetailsMapped);
						} catch (JSONException e) {
							LOG.error("Error parsing sysparam JSON", e);
							// Fallback if JSON parsing fails
							contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
						}
					} else {
						// Fallback if sysparam is not found
						contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
					}
				} else {
					// Default case if not SG
					contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
				}
				break;
			} else if(dto.getCkCtMstAlert().getAltConditionType().equalsIgnoreCase("DAYS_BEFORE")) {
				param.setTemplateId(ClickargoNotifTemplates.VEH_EXPIRY.getId());
				subjectFields.put(":subject", dto.getCkCtMstAlert().getAltName());
				contentFields.put(":monitoring", dto.getCkCtMstAlert().getAltName() + " " + dto.getAltReferId());
				contentFields.put(":accnName", ckCtVeh.getTCoreAccn().getAccnName() != null ? ckCtVeh.getTCoreAccn().getAccnName() : "-");
				contentFields.put(":accnRegLink", apps != null ? apps.getAppsLaunchUrl() + encodeUrlId(ckCtVeh.getTCoreAccn().getAccnId()) : "-");
				if ("SG".equalsIgnoreCase(dto.getCoreAccn().getCityCode())) {
					Optional<TCoreSysparam> opTCoreSysparam = Optional.ofNullable(coreSysparamDao.find("CLICTRUCK_ADDR_SG_API_CALL"));
					if (opTCoreSysparam.isPresent()) {
						try {
							TCoreSysparam sysparam = opTCoreSysparam.get();
							JSONObject jsonSpDetails = new JSONObject(sysparam.getSysVal());

							// Construct the sp_details string
							String spDetailsMapped = jsonSpDetails.getString("accnName") + " " +
									jsonSpDetails.getString("addrCity") + " " +
									jsonSpDetails.getString("addrPcode") + ", " +
									jsonSpDetails.getString("ctyDescription");

							contentFields.put(":sp_details", spDetailsMapped);
						} catch (JSONException e) {
							LOG.error("Error parsing sysparam JSON", e);
							// Fallback if JSON parsing fails
							contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
						}
					} else {
						// Fallback if sysparam is not found
						contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
					}
				} else {
					// Default case if not SG
					contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
				}
				break;
			}
		case "Driver License Expired":
			param.setTemplateId(ClickargoNotifTemplates.DRIVER_LICENSE.getId());
			subjectFields.put(":subject", dto.getCkCtMstAlert().getAltName());
			contentFields.put(":monitoring", dto.getCkCtMstAlert().getAltName() + " " + dto.getAltReferId());
			contentFields.put(":accnName", ckCtDrv.getTCoreAccn().getAccnName() != null ?  ckCtDrv.getTCoreAccn().getAccnName() : "-");
			contentFields.put(":accnRegLink", apps != null ? apps.getAppsLaunchUrl() + encodeUrlId(ckCtDrv.getTCoreAccn().getAccnId()) : "-");
			if ("SG".equalsIgnoreCase(dto.getCoreAccn().getCityCode())) {
				Optional<TCoreSysparam> opTCoreSysparam = Optional.ofNullable(coreSysparamDao.find("CLICTRUCK_ADDR_SG_API_CALL"));
				if (opTCoreSysparam.isPresent()) {
					try {
						TCoreSysparam sysparam = opTCoreSysparam.get();
						JSONObject jsonSpDetails = new JSONObject(sysparam.getSysVal());

						// Construct the sp_details string
						String spDetailsMapped = jsonSpDetails.getString("accnName") + " " +
								jsonSpDetails.getString("addrCity") + " " +
								jsonSpDetails.getString("addrPcode") + ", " +
								jsonSpDetails.getString("ctyDescription");

						contentFields.put(":sp_details", spDetailsMapped);
					} catch (JSONException e) {
						LOG.error("Error parsing sysparam JSON", e);
						// Fallback if JSON parsing fails
						contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
					}
				} else {
					// Fallback if sysparam is not found
					contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
				}
			} else {
				// Default case if not SG
				contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			}
			break;
		case "Vehicle Parking Cert Expired":
			param.setTemplateId(ClickargoNotifTemplates.VPC_EXPIRY.getId());
			subjectFields.put(":subject", dto.getCkCtMstAlert().getAltName());
			contentFields.put(":monitoring", dto.getCkCtMstAlert().getAltName() + " " + dto.getAltReferId());
			contentFields.put(":accnName", ckCtVeh.getTCoreAccn().getAccnName() != null ? ckCtVeh.getTCoreAccn().getAccnName() : "-");
			contentFields.put(":accnRegLink", apps != null ? apps.getAppsLaunchUrl() + encodeUrlId(ckCtVeh.getTCoreAccn().getAccnId()) : "-");
			if ("SG".equalsIgnoreCase(dto.getCoreAccn().getCityCode())) {
				Optional<TCoreSysparam> opTCoreSysparam = Optional.ofNullable(coreSysparamDao.find("CLICTRUCK_ADDR_SG_API_CALL"));
				if (opTCoreSysparam.isPresent()) {
					try {
						TCoreSysparam sysparam = opTCoreSysparam.get();
						JSONObject jsonSpDetails = new JSONObject(sysparam.getSysVal());

						// Construct the sp_details string
						String spDetailsMapped = jsonSpDetails.getString("accnName") + " " +
								jsonSpDetails.getString("addrCity") + " " +
								jsonSpDetails.getString("addrPcode") + ", " +
								jsonSpDetails.getString("ctyDescription");

						contentFields.put(":sp_details", spDetailsMapped);
					} catch (JSONException e) {
						LOG.error("Error parsing sysparam JSON", e);
						// Fallback if JSON parsing fails
						contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
					}
				} else {
					// Fallback if sysparam is not found
					contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
				}
			} else {
				// Default case if not SG
				contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			}
			break;
		case "Vehicle Insurance Expired":
			param.setTemplateId(ClickargoNotifTemplates.INSURANCE.getId());
			subjectFields.put(":subject", dto.getCkCtMstAlert().getAltName());
			contentFields.put(":monitoring", dto.getCkCtMstAlert().getAltName() + " " + dto.getAltReferId());
			contentFields.put(":accnName", ckCtVeh.getTCoreAccn().getAccnName() != null ? ckCtVeh.getTCoreAccn().getAccnName() : "-");
			contentFields.put(":accnRegLink", apps != null ? apps.getAppsLaunchUrl() + encodeUrlId(ckCtVeh.getTCoreAccn().getAccnId()) : "-");
			if ("SG".equalsIgnoreCase(dto.getCoreAccn().getCityCode())) {
				Optional<TCoreSysparam> opTCoreSysparam = Optional.ofNullable(coreSysparamDao.find("CLICTRUCK_ADDR_SG_API_CALL"));
				if (opTCoreSysparam.isPresent()) {
					try {
						TCoreSysparam sysparam = opTCoreSysparam.get();
						JSONObject jsonSpDetails = new JSONObject(sysparam.getSysVal());

						// Construct the sp_details string
						String spDetailsMapped = jsonSpDetails.getString("accnName") + " " +
								jsonSpDetails.getString("addrCity") + " " +
								jsonSpDetails.getString("addrPcode") + ", " +
								jsonSpDetails.getString("ctyDescription");

						contentFields.put(":sp_details", spDetailsMapped);
					} catch (JSONException e) {
						LOG.error("Error parsing sysparam JSON", e);
						// Fallback if JSON parsing fails
						contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
					}
				} else {
					// Fallback if sysparam is not found
					contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
				}
			} else {
				// Default case if not SG
				contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			}
			break;
		default:
			break;
		}
		param.setSubjectFields(subjectFields);
		param.setContentFeilds(contentFields);
		return param;
	}

	protected String encodeUrlId(String id) {
		String newUrl = "";
		try {
			byte[] encodedBytes = Base64.getEncoder().encode(id.getBytes());
			String encodedString = new String(encodedBytes);
			newUrl = url + encodedString;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return newUrl;
	}

}
