package com.guudint.clickargo.clictruck.report.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.master.enums.Roles;
//import com.guudint.pedi.app.common.controller.ControllerUtil;
//import com.guudint.pedi.app.workflow.model.TPediAppAccnAssn;
//import com.guudint.pedi.common.util.PrincipalUtilService;
//import com.guudint.pedi.enums.RolesEnum;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.model.TCoreAccnConfig;
import com.vcc.camelone.ccm.model.TCoreAccnConfigId;
import com.vcc.camelone.common.controller.entity.EntityFilterResponse;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.PermissionException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;
import com.vcc.camelone.reports.constant.MstFilType;
import com.vcc.camelone.reports.controller.PortalReportController;
import com.vcc.camelone.reports.dto.CoreRpt;
import com.vcc.camelone.reports.dto.MstRptCat;
import com.vcc.camelone.reports.dto.OnlineReport;
import com.vcc.camelone.reports.dto.ReportFilterColumn;
import com.vcc.camelone.reports.dto.RptFilColListbox;
import com.vcc.camelone.reports.service.IPortalReportService;
import com.vcc.camelone.util.PrincipalUtilService;

@RequestMapping(value = "/api/app/report")
@CrossOrigin
@RestController
public class ClicTruckReportListController extends PortalReportController {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(ClicTruckReportListController.class);

	@Autowired
	private IPortalReportService portalReportService;

	@Autowired
	private PrincipalUtilService principalUtilService;

	@Autowired
	@Qualifier("coreAccnConfigDao")
	private GenericDao<TCoreAccnConfig, TCoreAccnConfigId> coreAccnConfigDao;

//	@Autowired
//	@Qualifier("pediAppAccnAssnDao")
//	private GenericDao<TPediAppAccnAssn, String> pediAppAccnAssnDao;
//
//	@Autowired
//	@Qualifier("controllerUtil")
//	private ControllerUtil controllerUtil;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/group/list", method = RequestMethod.GET)
	public ResponseEntity<Object> listReportGroup() {

		ServiceStatus serviceStatus = new ServiceStatus();

		try {

//			controllerUtil.checkPermission();

			Principal principal = principalUtilService.getPrincipal();
			if (principal == null)
				throw new ParameterException("principal is null");

			List<MstRptCat> getRptCatList = portalReportService.getRptCatList(principal);
			List<String> uniqueId =new ArrayList<>();
			List<MstRptCat> filteredCatList = new ArrayList<>();
			//Filter the report category specially if principal has multiple rules that applies to all report categories
			 getRptCatList.forEach(e-> {
				if(!uniqueId.contains(e.getId().getRptcCat())) {
					uniqueId.add(e.getId().getRptcCat());
					filteredCatList.add(e);
				}
			});
			
			List<Object> entities = List.class.cast(filteredCatList);
			EntityFilterResponse filterResponse = new EntityFilterResponse();
			filterResponse.setiTotalRecords(entities.size());
			filterResponse.setiTotalDisplayRecords(filteredCatList.size());
			filterResponse.setAaData((ArrayList<Object>) entities);
			return ResponseEntity.ok(filterResponse);

		} catch (PermissionException ex) {
			log.error("listReportGroup", ex);
			serviceStatus.setStatus(STATUS.PERMISSION_FAILED);
			serviceStatus.setErr(new ServiceError(403, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.FORBIDDEN);
		} catch (Exception e) {
			log.error("listReportGroup", e);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}

	}

	/**
	 * @param rptCatId
	 * @return
	 */
	@RequestMapping(value = "/list/{rptCatId}", method = RequestMethod.GET)
	public ResponseEntity<Object> listReports(@PathVariable String rptCatId) {

		ServiceStatus serviceStatus = new ServiceStatus();

		try {

//			controllerUtil.checkPermission();

			Principal principal = principalUtilService.getPrincipal();
			if (principal == null)
				throw new ParameterException("principal is null");

			List<CoreRpt> reportList = portalReportService.getReportList(rptCatId, principal);
			return ResponseEntity.ok(reportList);
		} catch (PermissionException ex) {
			log.error("listReports", ex);
			serviceStatus.setStatus(STATUS.PERMISSION_FAILED);
			serviceStatus.setErr(new ServiceError(403, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.FORBIDDEN);
		} catch (Exception ex) {
			log.error("listReports", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}

	}

	/**
	 * @param reportId
	 * @return
	 */
	@RequestMapping(value = "/filter/{reportId}", method = RequestMethod.GET)
	public ResponseEntity<Object> viewReport(@PathVariable String reportId) {

		ServiceStatus serviceStatus = new ServiceStatus();

		try {

//			controllerUtil.checkPermission();

			Principal principal = principalUtilService.getPrincipal();
			if (principal == null)
				throw new ParameterException("principal is null");

			if (principal.getCoreAccn() == null)
				throw new ParameterException("coreAccn is null");

			OnlineReport onlineReport = portalReportService.getOnlineReport(reportId,
					principal.getCoreAccn().getAccnId(), principal.getUserId());
			log.debug("onlineReport = " + onlineReport.toJson());

			String portAssocAccn = null;
			String accnType = null;
			try {

				TCoreAccnConfigId id = new TCoreAccnConfigId();
				id.setAcfgAccnid(principal.getCoreAccn().getAccnId());
				id.setAcfgKey("PORT");

				TCoreAccnConfig accnConfig = coreAccnConfigDao.find(id);
				if (accnConfig != null) {
//					RptFilColListbox lb = (RptFilColListbox) fc;
					String[] accnConfigAssoc = accnConfig.getAcfgVal().split(";");
					portAssocAccn = accnConfigAssoc[0];// first token is the portcode
					accnType = accnConfigAssoc[1];
//					return lb.getOptions().get(portAssocAccn);
				}

			} catch (Exception ex) {
				log.error("viewReport", ex);
			}
			
//			boolean isReportAdmin = principal.getRoleList().contains(RolesEnum.REPORT_OFFICER_ADMIN.name());
			boolean isReportAdmin = principal.getRoleList().contains(Roles.ADMIN.name());

			if(!isReportAdmin) {
				List<ReportFilterColumn> updatedFilter = new ArrayList<>();

				// iterate through the getRptFilterColumns
				for (ReportFilterColumn fc : onlineReport.getRptFilterColumns()) {
					// only applies to the below criteria
					if (fc.getFilColCode().contains("_PORT") && !fc.getFilColCode().contains("_PORT_")
							&& fc.getFilColType().equalsIgnoreCase(String.valueOf(MstFilType.LISTBOX))) {
						RptFilColListbox lb = (RptFilColListbox) fc;
						if (lb.getOptions().containsKey(portAssocAccn)) {
							// clean up lb.getOptions and remain only the first one found
							Map<String, String> nOptions = new HashMap<String, String>();
							nOptions.put(portAssocAccn, lb.getOptions().get(portAssocAccn));
							lb.setOptions(nOptions);
							lb.setValue(portAssocAccn);
							updatedFilter.add(lb);

						}
					} else if (fc.getFilColCode().contains("AGENCY")
							&& fc.getFilColType().equalsIgnoreCase(String.valueOf(MstFilType.LISTBOX))) {
						RptFilColListbox lb = (RptFilColListbox) fc;
						if (lb.getOptions().containsKey(accnType)) {
							// clean up lb.getOptions and remain only the first one found
							Map<String, String> nOptions = new HashMap<String, String>();
							nOptions.put(accnType, lb.getOptions().get(accnType));
							lb.setOptions(nOptions);
							lb.setValue(accnType);
							updatedFilter.add(lb);

						}
					} else {
						updatedFilter.add(fc);
					}
				}

				onlineReport.setRptFilterColumns(updatedFilter);
			}
			

			return ResponseEntity.ok(onlineReport);
		} catch (PermissionException ex) {
			log.error("viewReport", ex);
			serviceStatus.setStatus(STATUS.PERMISSION_FAILED);
			serviceStatus.setErr(new ServiceError(403, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.FORBIDDEN);
		} catch (Exception ex) {
			log.error("viewReport", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}

	}

}
