import {
  Backdrop,
  Button,
  Checkbox,
  CircularProgress,
  Dialog,
  Divider,
  FormControlLabel,
  Grid,
  makeStyles,
  Paper,
  Tabs,
} from "@material-ui/core";
import AccessTimeOutlinedIcon from "@material-ui/icons/AccessTimeOutlined";
import DescriptionOutlinedIcon from "@material-ui/icons/DescriptionOutlined";
import EventNoteOutlinedIcon from "@material-ui/icons/EventNoteOutlined";
import ExploreOutlinedIcon from "@material-ui/icons/ExploreOutlined";
import FileCopyOutlinedIcon from "@material-ui/icons/FileCopyOutlined";
import LocalShippingOutlinedIcon from "@material-ui/icons/LocalShippingOutlined";
import MessageOutlinedIcon from "@material-ui/icons/MessageOutlined";
import NearMeOutlinedIcon from "@material-ui/icons/NearMeOutlined";
import TocOutlinedIcon from "@material-ui/icons/TocOutlined";
import WorkOutlineOutlinedIcon from "@material-ui/icons/WorkOutlineOutlined";
import moment from "moment";
import React, {useEffect, useRef, useState} from "react";
import {useTranslation} from "react-i18next";
import {useHistory, useLocation, useParams} from "react-router-dom";

import C1AuditTab from "app/c1component/C1AuditTab";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DialogPrompt from "app/c1component/C1DialogPrompt";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1IconButton from "app/c1component/C1IconButton";
import C1InputField from "app/c1component/C1InputField";
import C1PopUp from "app/c1component/C1PopUp";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";
import C1TextArea from "app/c1component/C1TextArea";
import C1Warning from "app/c1component/C1Warning";
import useHttp from "app/c1hooks/http";
import useQuery from "app/c1hooks/useQuery";
import {
  AccountTypes,
  Actions,
  jobLoading,
  JobStates,
  jobSubType,
  Roles,
  T_CK_CT_DRV,
  T_CK_CT_VEH,
} from "app/c1utils/const";
import {getFormActionButton} from "app/c1utils/formActionUtils";
import {deepUpdateState} from "app/c1utils/stateUtils";
import {tabScroll} from "app/c1utils/styles";
import {encryptText, getValue, isArrayNotEmpty, isEditable, previewPDF, Uint8ArrayToString,} from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import TabLabel from "app/portedicomponent/TabLabel";
import {TabsWrapper} from "app/portedicomponent/TabsWrapper";
import {ConfirmationDialog, MatxLoading} from "matx";

import AddDelOrderPopup from "../popups/AddDelOrderPopup";
import JobTruckContext from "./JobTruckContext";
import JobAuthLetters from "./tabs/JobAuthLetters";
import JobDeliveryOrders from "./tabs/JobDeliveryOrders";
import JobDomesticInvoice from "./tabs/JobDomesticInvoice";
import JobDriverAssign from "./tabs/JobDriverAssign";
import JobInvoice from "./tabs/JobInvoice";
import JobNewDetails from "./tabs/JobNewDetails";
import JobRejectRemarks from "./tabs/JobRejectRemarks";
import JobTrack from "./tabs/JobTrack";
import JobTripCharges from "./tabs/JobTripCharges";
import JobTripChargesDomestic from "./tabs/JobTripChargesDomestic";
import _ from "lodash";
import DeliveredPopup from "../popups/DeliveredPopup";

const useStyles = makeStyles((theme) => ({
  backdrop: {
    zIndex: theme.zIndex.drawer + 1,
    color: "#fff",
  },
}));

/***@description Component for truck job details. */
const JobTruckFormDetails = () => {
  const { t } = useTranslation([
    "job",
    "common",
    "cargoowners",
    "listing",
    "buttons",
    "financing",
  ]);

  const bdClasses = useStyles();
  const history = useHistory();
  const { user } = useAuth();

  let { viewType, jobId } = useParams();
  let location = useLocation();

  const { state } = location;
  if (state?.jobId) {
    jobId = state.jobId;
  }

  let encryptedJobId = encryptText(jobId, user?.coreAccn?.accnId, user?.id);

  const shipmentType = history?.location?.state?.shipmentType;
  const query = useQuery();
  const isTruckingOperator =
    user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_TO.code;
  const isFFCO =
    user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_FF_CO.code;
  const isSuspended = user?.coreAccn?.accnStatus === "S";
  //SAGAWA Account
  let sagawa = user?.coreAccn?.accnId;
  let defaultValueLoading = null;
  let defaultValueJobType = null;
  //tablist ----------------------------------------------------------------------------------------------------------------------------*/
  const tabList = [
    {
      id: "jobDetails",
      idx: 0,
      text: t("job:tabs.jobDetails"),
      icon: <WorkOutlineOutlinedIcon />,
    },
    {
      id: "fmTrip",
      idx: 1,
      text: t("job:tabs.tripCharges"),
      icon: <LocalShippingOutlinedIcon />,
    },
    {
      id: "mmTrip",
      idx: 2,
      text: t("job:tabs.tripCharges"),
      icon: <LocalShippingOutlinedIcon />,
    },
    {
      id: "documents",
      idx: 3,
      text: t("job:tabs.documents"),
      icon: <DescriptionOutlinedIcon />,
    },
    {
      id: "driver",
      idx: 4,
      text: t("job:tabs.driver"),
      icon: <LocalShippingOutlinedIcon />,
    },
    {
      id: "deliveryOrders",
      idx: 5,
      text: t("job:tabs.deliveryOrders"),
      icon: <TocOutlinedIcon />,
    },
    {
      id: "invoice",
      idx: 6,
      text: t("job:tabs.invoice"),
      icon: <EventNoteOutlinedIcon />,
    },
    {
      id: "midMileInvoice",
      idx: 7,
      text: t("job:tabs.invoice"),
      icon: <EventNoteOutlinedIcon />,
    },
    {
      id: "rejectRemarks",
      idx: 8,
      text: t("job:tabs.remarks"),
      icon: <MessageOutlinedIcon />,
    },
    {
      id: "audit",
      idx: 9,
      text: t("job:tabs.audit"),
      icon: <AccessTimeOutlinedIcon />,
    },
    {
      id: "tracking",
      idx: 10,
      text: t("job:tabs.tracking"),
      icon: <ExploreOutlinedIcon />,
    },
  ];

  const [dynamicTabs, setDynamicTabs] = useState([]);

  const [tabIndex, setTabIndex] = useState(0);
  const [isDisabled, setDisabled] = useState(isEditable(viewType));
  const [jobState, setJobState] = useState(JobStates.NEW.code);
  //initialize to not show fmtrip and mmtrip first
  const [showTabs, setShowTabs] = useState({
    driver: false,
    invoice: false,
    fmTrip: false,
    mmTrip: false,
    midMileInvoice: false,
    deliveryOrders: false,
    tracking: false,
  });

  const jobTripChargesRef = useRef();
  const invoiceRef = useRef();

  const [start, setStart] = useState(false);
  const [stop, setStop] = useState(false);
  const [startErrorOpen, setStartErrorOpen] = useState({
    msg: null,
    open: false,
  });
  const [stopErrorOpen, setStopErrorOpen] = useState({
    msg: null,
    open: false,
  });
  const [duplicateErrorOpen, setDuplicateErrorOpen] = useState({
    msg: null,
    open: false,
  });
  const [customErrorOpen, setCustomErrorOpen] = useState({
    title: "Error",
    msg: null,
    open: false,
  });

  /** ------------------ States ---------------------------------*/

  const {
    isLoading,
    isFormSubmission,
    res,
    validation,
    error,
    urlId,
    sendRequest,
  } = useHttp();

  const defaultSnackbarValue = {
    success: false,
    successMsg: "",
    error: false,
    errorMsg: "",
    redirectPath: "",
  };
  const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);
  const [loading, setLoading] = useState(true);
  const [inputData, setInputData] = useState({});

  const [controls, setControls] = useState([]);
  const [openSubmitConfirm, setOpenSubmitConfirm] = useState({
    action: null,
    open: false,
  });
  const [validationErrors, setValidationErrors] = useState({});
  const [openWarning, setOpenWarning] = useState(false);
  const [warningMessage, setWarningMessage] = useState("");
  const [fileUploaded, setFileUploaded] = useState(false);
  const [tcrData, setTcrData] = useState({
    tripCargoMmList: [],
    reimbursements: [],
  });
  const [tcrList, setTcrList] = useState({ list: [] });
  const [totalTripCharges, setTotalTripCharges] = useState({});
  const [invoiceData, setInvoiceData] = useState({});

  const [additionalFields, setAdditionalFields] = useState([]);
  const [addtionalFieldsInputData, setAdditionalFieldsInputData] = useState([]);

  const [isRefresh, setRefresh] = useState(false);
  const [doId, setDoId] = useState("");
  const [dlOpen, setDlOpen] = useState(false);
  const [openAddPopUp, setOpenAddPopUp] = useState(false);
  const [rejectRemarks, setRejectRemarks] = useState({
    open: false,
    msg: null,
  });
  const [jobRemarks, setJobRemarks] = useState({
    open: false,
    msg: null,
    remarksType: "V",
  });
  const [openClonePopUp, setOpenClonePopUp] = useState(false);
  const [openDeliveredPopUp, setOpenDeliveredPopUp] = useState(false);
  const [openSplitPopUp, setOpenSplitPopUp] = useState(false);
  const [cloneData, setCloneData] = useState({});
  const [numOfSplit, setNumOfSplit] = useState(2);  // default split is 2
  const [popUpFieldError, setPopUpFieldError] = useState({});
  const popupDoDefaultValue = {
    doNo: "",
    tckCtTrip: {
      trId: "",
    },
  };
  const [popUpDoDetails, setPopUpDoDetails] = useState(popupDoDefaultValue);
  const [domestic, setDomestc] = useState();
  const isToFinance = user?.authorities.some(
    (item) => item.authority === Roles?.FF_FINANCE?.code
  );
  const [warningProps, setWarningProps] = useState({ open: false, msg: "" });

  const popupAttDefaultValue = {
    doId: "",
    tckCtTrip: {
      trId: "",
    },
    doaName: "",
    doaData: null,
  };
  const [popUpAttDetails, setPopUpAttDetails] = useState(popupAttDefaultValue);

  //initial button handler --------------------------------------------------------------------------------------------------
  const ableToSaveWhenAcceptJob =  viewType === "edit" ||
      viewType === "new" ||
      (JobStates.ACP.code === inputData?.tckJob?.tckMstJobState?.jbstId &&
          isTruckingOperator &&
          (user?.role?.includes(Roles?.OFFICER.code) ||
              user?.role?.includes(Roles?.OP_OFFICER.code)));

  const ableToSaveModifiesTrip = viewType === "view" &&
      ([JobStates.ASG.code, JobStates.ONGOING.code, JobStates.ACP.code, JobStates.PAUSED.code].includes(inputData?.tckJob?.tckMstJobState?.jbstId) &&
          isTruckingOperator && (user?.role?.includes(Roles?.OFFICER.code) || user?.role?.includes(Roles?.OP_OFFICER.code)));

  const ableToModifiesCargo = viewType === "view" &&
      ([JobStates.ACP.code].includes(inputData?.tckJob?.tckMstJobState?.jbstId) &&
          isTruckingOperator && (user?.role?.includes(Roles?.OFFICER.code) || user?.role?.includes(Roles?.OP_OFFICER.code)));

  const initialButtons = {
    back: { show: true, eventHandler: () => handleExitOnClick() },
    save: {
      show: ableToSaveWhenAcceptJob || (ableToSaveModifiesTrip && inputData?.tckJob?.tckMstShipmentType?.shtId === "DOMESTIC"),
      eventHandler: () => handleSaveOnClick(),
    },
    stop: {
      show: [JobStates.ASG.code, JobStates.ONGOING.code, JobStates.PAUSED.code].includes(inputData?.tckJob?.tckMstJobState?.jbstId) && isTruckingOperator,
      eventHandler: () => eventHandler("STOP"),
    },
  };

  const acceptedStatus = [JobStates.ACP.code].includes(inputData?.tckJob?.tckMstJobState?.jbstId);

  const truckUrlApi = `/api/v1/clickargo/clictruck/job/truck/`;

  const isCoFfUser =
    user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_CO.code ||
    user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_FF.code;

  /** ------------------- Update states ----------------- */
  useEffect(() => {
    if (viewType === "new") {
      //moved this after tab,
      // sendRequest(`${truckUrlApi}/new/${shipmentType}`, "newJob", "GET");
    } else if (viewType === "view" || viewType === "edit") {
      setSnackBarOptions(defaultSnackbarValue);
      setLoading(true);
      setFileUploaded(true);

      // sendRequest(`/api/v1/clickargo/clictruck/accnconfig/tabs`, "getTabs", "GET");
      //moved this after tab,
      // sendRequest(`${truckUrlApi}` + encryptedJobId, "getJob", "GET", null);
    }

    sendRequest(
      `/api/v1/clickargo/clictruck/accnconfig/tabs`,
      "getTabs",
      "GET"
    );
    // eslint-disable-next-line
  }, [jobId, viewType]);

  useEffect(() => {
    if (!isLoading && !error && res && !validation) {
      if (urlId !== "getTabs") {
        //if it's getTabs, keep it loading
        setLoading(isLoading);
      }

      switch (urlId) {
        case "newJob": {
          let data = res?.data;
          setInputData({
            ...data,
            tckJob: {
              jobLoading: jobLoading.FTL.code,
              jobSubType: jobSubType.LOCAL.code,
            },
          });
          setShowTabs({
            ...showTabs,
            fmTrip: !data?.domestic,
            mmTrip: data?.domestic,
          });

          setDomestc(data?.domestic);

          let isTripChargesHidden = data?.hiddenFields?.includes("tripcharges");
          //update dynamic tab labe for trip  charges if tripCharges is in hidden field
          setDynamicTabs([
            ...dynamicTabs?.map((el) => {
              if (
                isTripChargesHidden &&
                (el?.id === "fmTrip" || el?.id === "mmTrip")
              ) {
                el.text = t("job:tabs.tripCargos");
              }
              return el;
            }),
          ]);
          break;
        }
        case "createJob": {
          setInputData({ ...res?.data });
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("cargoowners:msg.saveSuccess"),
            redirectPath: "/applications/services/job/truck/edit",
            redirectPathState: { state: { jobId: res?.data?.jobId } },
          });
          break;
        }
        case "getTabs": {
          if (isArrayNotEmpty(res?.data)) {
            let modTabArr = tabList?.filter((el) => {
              //if it's non finance, apply the tab filter
              if (inputData?.jobIsFinanced === "N")
                return res?.data?.includes(el?.id);
              else return el?.id;
            });

            setDynamicTabs(_.uniqBy([...dynamicTabs, ...modTabArr], 'id'));
          } else {
            setDynamicTabs([...tabList]);
          }

          if (viewType === "new") {
            sendRequest(`${truckUrlApi}new/${shipmentType}`, "newJob", "GET");
          } else if (viewType === "view" || viewType === "edit") {
            sendRequest(
              `${truckUrlApi}` + encryptedJobId,
              "getJob",
              "GET",
              null
            );
          }

          break;
        }
        case "getJob": {
          let data = res.data;
          let shipmentTypeData = data?.tckJob?.tckMstShipmentType?.shtId;

          if (data?.tckCtTripList === null) {
            setInputData({
              ...data,
              shipmentType: shipmentType ? shipmentType : shipmentTypeData,
            });
          } else {
            let tripLIstSort = data?.tckCtTripList;
            tripLIstSort.sort((a, b) => a.trSeq - b.trSeq);
            setInputData({
              ...data,
              shipmentType: shipmentType ? shipmentType : shipmentTypeData,
              tckCtTripList: tripLIstSort,
            });
          }

          if (data?.addtlFields) {
            setAdditionalFieldsInputData([...data?.addtlFields]);
          }

          let jobState = data?.tckJob?.tckMstJobState?.jbstId;
          let shipmentTypeState = data?.tckJob?.tckMstShipmentType?.shtName;

          //Invoice tab should not be shown by the co/ff if it is not billed yet
          let hideInvTab =
            jobState !== JobStates.BILLED.code && !isTruckingOperator;

          if (shipmentTypeState === "DOMESTIC") {
            setShowTabs({
              ...showTabs,
              driver: ![
                JobStates.NEW.code,
                JobStates.SUB.code,
                JobStates.CAN.code,
              ].includes(jobState),
              // invoice: ![JobStates.NEW.code, JobStates.SUB.code, JobStates.ACP.code, JobStates.ASG.code, JobStates.ONGOING.code].includes(jobState),
              midMileInvoice: hideInvTab
                ? false
                : ![
                    JobStates.NEW.code,
                    JobStates.SUB.code,
                    JobStates.ACP.code,
                    JobStates.ASG.code,
                    JobStates.ONGOING.code,
                    JobStates.CAN.code,
                    JobStates.PAUSED.code,
                  ].includes(jobState) && data?.jobIsFinanced !== "N",
              fmTrip: !data?.domestic,
              mmTrip: data?.domestic,
              deliveryOrders:
                data?.jobMobileEnabled === "Y" &&
                [JobStates.DLV.code].includes(jobState)
                  ? true
                  : isCoFfUser
                  ? false
                  : [
                      JobStates.ASG.code,
                      JobStates.ONGOING.code,
                      JobStates.PAUSED.code,
                    ].includes(jobState),
              tracking: [
                JobStates.ONGOING.code,
                JobStates.DLV.code,
                JobStates.BILLED.code,
                JobStates.VER.code,
                JobStates.APP.code,
              ].includes(jobState),
            });
          } else {
            setShowTabs({
              ...showTabs,
              driver: ![
                JobStates.NEW.code,
                JobStates.SUB.code,
                JobStates.CAN.code,
              ].includes(jobState),
              invoice: hideInvTab
                ? false
                : ![
                    JobStates.NEW.code,
                    JobStates.SUB.code,
                    JobStates.ACP.code,
                    JobStates.ASG.code,
                    JobStates.ONGOING.code,
                    JobStates.CAN.code,
                    JobStates.PAUSED.code,
                  ].includes(jobState) && data?.jobIsFinanced !== "N",
              // midMileInvoice: ![JobStates.NEW.code, JobStates.SUB.code, JobStates.ACP.code, JobStates.ASG.code, JobStates.ONGOING.code].includes(jobState),
              fmTrip: !data?.domestic,
              mmTrip: data?.domestic,
              deliveryOrders:
                data?.jobMobileEnabled === "Y" &&
                [JobStates.DLV.code].includes(jobState)
                  ? true
                  : isCoFfUser
                  ? false
                  : [
                      JobStates.ASG.code,
                      JobStates.ONGOING.code,
                      JobStates.PAUSED.code,
                    ].includes(jobState),
              tracking: [
                JobStates.ONGOING.code,
                JobStates.DLV.code,
                JobStates.BILLED.code,
                JobStates.VER.code,
                JobStates.APP.code,
              ].includes(jobState),
            });
          }
          setJobState({ ...jobState, ...data?.tckJob?.tckMstJobState?.jbstId });
          let crntJobState =
            data?.tckJob?.tckMstJobState?.jbstId === "INVVER"
              ? "VER"
              : data?.tckJob?.tckMstJobState?.jbstId;
          const reqBody = {
            entityType: "JOB_TRUCK",
            entityState: crntJobState,
            page: viewType.toUpperCase(),
          };
          if (query && query.get("tabIndex")) {
            setTabIndex(parseInt(query.get("tabIndex")));
          }

          setDomestc(data?.domestic);

          let isTripChargesHidden = data?.hiddenFields?.includes("tripcharges");
          //update dynamic tab labe for trip  charges if tripCharges is in hidden field
          setDynamicTabs([
            ...dynamicTabs?.map((el) => {
              if (
                isTripChargesHidden &&
                (el?.id === "fmTrip" || el?.id === "mmTrip")
              ) {
                el.text = t("job:tabs.tripCargos");
              }
              return el;
            }),
          ]);

          sendRequest(
            "/api/v1/clickargo/controls/",
            "fetchControls",
            "POST",
            reqBody
          );
          break;
        }
        case "updateJob": {
          let data = res.data;
          setInputData({ ...data });
          setLoading(false);
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("cargoowners:msg.updateSuccess")
          });
          break;
        }
        case "fetchControls": {
          let tmp = removeSaveButtonForFinanceDomestic(res.data);
          setControls(removeStartStopForMobileApps(tmp));
          // Removed icons for non-financed jobs
          setControls(removeIconsForNonFinance(tmp));

          //Call the additional fields based on  contract here
          sendRequest(
            `api/v1/clickargo/clictruck/job/truck/addtl/attr`,
            "loadAddtlFields",
            "post",
            {
              toAccnId: inputData?.tcoreAccnByJobPartyTo?.accnId,
              coAccnId: inputData?.tcoreAccnByJobPartyCoFf?.accnId,
            }
          );
          break;
        }
        case "download": {
          viewFile(res?.data?.attName, res?.data?.attData);
          break;
        }
        //disable redirect to dashboard and force reload for accept and assign job
        case "accept":
        case "assign": {
          let msg = t("common:common.msg.generalAction", {
            action: Actions[openSubmitConfirm?.action]?.result,
          });
          let prevUrl = history?.location?.state?.from;
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: msg,
          });
          window.location.reload(true);
          break;
        }
        case "stop":
        case "start":
        case "cancel":
        case "submit":
        case "reject":
        case "reject_bill":
        case "verify_bill":
        case "acknowledge_bill":
        case "approve_bill":
        case "verify":
        case "approve":
        case "billjob": {
          let msg = t("common:common.msg.generalAction", {
            action: Actions[openSubmitConfirm?.action]?.result,
          });
          let prevUrl = history?.location?.state?.from;
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: msg,
            redirectPath: prevUrl
              ? prevUrl
              : isTruckingOperator
              ? "/applications/services/job/to/truck"
              : "/applications/services/job/coff/truck",
          });
          break;
        }
        case "withdraw": {
          let data = res.data;
          let msg = t("common:common.msg.generalAction", {
            action: openSubmitConfirm?.action,
          });
          let prevUrl = history?.location?.state?.from;
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: msg,
            redirectPath: "/applications/services/job/truck/edit",
            redirectPathState: { state: { jobId: data?.jobId } },
          });
          // When a job is withdrawn inside the form, needs to reenable fields for edit without refreshing the page.
          setDisabled(false);
          break;
        }
        case "getDriver": {
          const data = res.data.aaData[0];
          setInputData({
            ...inputData,
            ...inputData.tckCtDrv,
            tckCtDrv: { ...inputData.tckCtDrv, ...data },
          });
          break;
        }
        case "getTruck": {
          const data = res.data.aaData[0];
          setInputData({
            ...inputData,
            ...inputData.tckCtVeh,
            tckCtVeh: { ...inputData.tckCtVeh, ...data },
          });
          break;
        }
        case "createDo":
        case "uploadFile": {
          if (res?.data.duplicateDoNo === true) {
            setRefresh(true);
            setDlOpen(false);
            // setOpenAddPopUp(true)
            // setPopUpDoDetails(popupDoDefaultValue);
            setDuplicateErrorOpen({
              ...duplicateErrorOpen,
              msg: t("job:msg.duplicateDoNoMsg"),
              open: true,
            });
          } else {
            setPopUpDoDetails(popupDoDefaultValue);
            setDlOpen(false);
            setRefresh(true);
            setPopUpAttDetails(res?.data);
            setDoId(res?.data?.doId);
          }
          break;
        }
        case "deleteTripDo": {
          setRefresh(true);
          break;
        }
        case "previewDo": {
          setDlOpen(false);
          if (res?.data) {
            previewPDF(res?.data?.doaName, res?.data?.doaData);
          }
          break;
        }
        case "previewUnsigned": {
          setDlOpen(false);
          if (res?.data) {
            previewPDF(res?.data?.doUnsignedName, res?.data?.doUnsignedData);
          }
          break;
        }
        case "previewSigned": {
          setDlOpen(false);
          if (res?.data) {
            previewPDF(res?.data?.doSignedName, res?.data?.doSignedData);
          }
          break;
        }
        case "deleteAttachment": {
          setDlOpen(false);
          setRefresh(true);
          setPopUpAttDetails(popupAttDefaultValue);
          setPopUpDoDetails(popupDoDefaultValue);
          setDoId(null);
          break;
        }

        case "delete": {
          let msg = t("common:common.msg.generalAction", {
            action: openSubmitConfirm?.action,
          });
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: msg,
            redirectPath: isTruckingOperator
              ? "/applications/services/job/to/truck"
              : "/applications/services/job/coff/truck",
          });
          break;
        }
        case "duplicateJOB": 
        case "splitJOB": {
          setLoading(false);
          let msg = t("common:common.msg.cloned");
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: msg,
            redirectPath: isTruckingOperator
              ? "/applications/services/job/to/truck"
              : "/applications/services/job/coff/truck",
          });
          break;
        }
        case "loadAddtlFields":
          if (res?.data) {
            //if there are records, then form the objects in the array
            setAdditionalFields([...res?.data]);
            setAdditionalFieldsInputData([
              ...addtionalFieldsInputData,
              ...res?.data?.map((el) => {
                return { tckCtConAddAttr: el, jaaValue: null };
              }),
            ]);
          } else {
            setAdditionalFields([]);
            setAdditionalFieldsInputData([]);
          }
        default:
          break;
      }
    }

    if (error) {
      //goes back to the screen
      setLoading(false);
    }

    // setOpenWarning(true)
    // setWarningMessage("TEST")
    //If validation has value then set to the errors
    if (validation) {
      setValidationErrors({ ...validation });
      setLoading(false);
      setSnackBarOptions(defaultSnackbarValue);

      if (validation["Submit.API.call"]) {
        // alert(validation['Submit.API.call'])
        setOpenWarning(true);
        setWarningMessage(validation["Submit.API.call"]);
      } else if (validation["do-pu-doc-missing"]) {
        const msg = validation["do-pu-doc-missing"];
        setTabIndex(3);
        //reloadTable();
        setOpenWarning(true);
        setWarningMessage(msg ? msg : t("common:common.msg.dopuMissing"));
      } else if (validation["dropoff-doc-missing"]) {
        const msg = validation["dropoff-doc-missing"];
        setTabIndex(3);
        //reloadTable();
        setOpenWarning(true);
        setWarningMessage(msg ? msg : t("common:common.msg.dropoffMissing"));
      } else if (validation["pickup-doc-missing"]) {
        const msg = validation["pickup-doc-missing"];
        setTabIndex(3);
        //reloadTable();
        setOpenWarning(true);
        setWarningMessage(msg ? msg : t("common:common.msg.pickupMissing"));
      } else if ("invoice-required" in validation) {
        const msg = validation["invoice-required"];
        setOpenWarning(true);
        setWarningMessage(msg ? msg : t("common:common.msg.invoceRequired"));
      } else if ("invalid-unsigned-file-format" in validation) {
        const msg = validation["invalid-unsigned-file-format"];
        setOpenWarning(true);
        setDlOpen(false);
        setWarningMessage(msg ? msg : "DO Invalid format");
      } else if ("invalid-signed-file-format" in validation) {
        const msg = validation["invalid-signed-file-format"];
        setOpenWarning(true);
        setDlOpen(false);
        setWarningMessage(msg ? msg : "POD Invalid format");
      } else if (validation["or-not-found"]) {
        const msg = validation["or-not-found"];
        setTabIndex(5);
        //reloadTable();
        setOpenWarning(true);
        setWarningMessage(msg ? msg : "OR Not found");
      } else if (validation["pod-not-found"]) {
        const msg = validation["pod-not-found"];
        setTabIndex(5);
        //reloadTable();
        setOpenWarning(true);
        setWarningMessage(msg ? msg : "POD Not found");
      } else {
        let keyList = Object.keys(validation);
        for (let key of keyList) {
          if (key.includes("invalidTabs.jobDetails")) {
            setTabIndex(0);
            setOpenWarning(true);
            setWarningMessage(t("common:common.msg.jobDetailsInc"));
            return;
          }
          if (key.includes("invalidTabs.fmTrip")) {
            setTabIndex(1);
            setOpenWarning(true);
            setWarningMessage(t("common:common.msg.fmTripInc"));
            return;
          }
          if (key.includes("invalidTabs.mmTrip")) {
            setTabIndex(2);
            setOpenWarning(true);
            setWarningMessage(t("common:common.msg.fmTripInc"));
            return;
          }
          if (key.includes("invalidTabs.driver")) {
            setTabIndex(4);
            setOpenWarning(true);
            setWarningMessage(t("common:common.msg.drvTrckInc"));
            return;
          }
          if (key.includes("invalidTabs.invoice")) {
            setTabIndex(6);
            setOpenWarning(true);
            setWarningMessage(t("common:common.msg.invRequired"));
            return;
          }
          if (key.includes("invalidTabs.midMileInvoice")) {
            setTabIndex(7);
            setOpenWarning(true);
            setWarningMessage(t("common:common.msg.invRequired"));
            return;
          } else {
            let valKey = validation[key];
            let label = validation[key];
            if (valKey.includes("credit") || valKey.includes("balance")) {
              label = t("financing:" + validation[key]);
            }

            setOpenWarning(true);
            setWarningMessage(label);
          }
        }
      }
    }

    // eslint-disable-next-line
  }, [urlId, isLoading, isFormSubmission, res, validation, error]);
  /** ---------------- Event handlers ----------------- */

  const removeSaveButtonForFinanceDomestic = (prameterControls) => {
    if (prameterControls && domestic && isToFinance) {
      prameterControls = prameterControls.filter(
        (c) => c.ctrlAction !== "SAVE"
      );
    }
    return prameterControls;
  };

  /*** Hide the START/DELIVERED button if mobile is enabled for the TO account. */
  const removeStartStopForMobileApps = (paramControls) => {
    if (paramControls && inputData?.jobMobileEnabled === "Y") {
      //get the job state
      let jobState = inputData?.tckJob?.tckMstJobState?.jbstId;
      if (jobState === JobStates.ASG.code)
        paramControls = paramControls.filter((c) => c.ctrlAction !== "START");
      //TO should be able to stop if driver forgot to click from mobile
      // else if (jobState === JobStates.ONGOING.code)
      //     paramControls = paramControls.filter(c => c.ctrlAction !== 'STOP');
    }

    return paramControls;
  };

  /*** Hide the below icons for non-financed jobs */
  const removeIconsForNonFinance = (paramControls) => {
    // Call removeStartStopForMobileApps to remove other icons in the event that job is both jobIsFinanced and jobMobileEnabled
    paramControls = removeStartStopForMobileApps(paramControls);
    if (paramControls && inputData?.jobIsFinanced === "N") {
      //get the job state
      let jobState = inputData?.tckJob?.tckMstJobState?.jbstId;
      if (jobState === JobStates.DLV.code)
        paramControls = paramControls.filter(
          (c) => c.ctrlAction !== "BILLJOB" && c.ctrlAction !== "SAVE"
        );
      else if (jobState === JobStates.BILLED.code)
        paramControls = paramControls.filter(
          (c) =>
            c.ctrlAction !== "VERIFY_BILL" && c.ctrlAction !== "REJECT_BILL"
        );
      else if (jobState === JobStates.VER_BILL.code)
        paramControls = paramControls.filter(
          (c) =>
            c.ctrlAction !== "ACKNOWLEDGE_BILL" &&
            c.ctrlAction !== "REJECT_BILL"
        );
    }

    return paramControls;
  };

  const handleTabChange = (e, value) => {
    setTabIndex(value);
    if (value !== 1) {
      //merge if the tabIndex is not 1, when user move from one tab to the other
      if (jobTripChargesRef?.current?.getTripDetails) {
        //trigger a merge to input data so that trip/cargoes state won't; only when there is change
        setInputData({
          ...inputData,
          tckCtTripList: [jobTripChargesRef?.current?.getTripDetails()],
        });
      }
    }

    // CT-124 [CO Operations-Trucking Jobs-Domestic] Added Trip Details Goes Away when We Switch Tab
    if (value !== 2) {
      if (jobTripChargesRef?.current?.getTripList) {
        setInputData({
          ...inputData,
          tckCtTripList: jobTripChargesRef?.current?.getTripList(),
        });
      }
    }

    if (value !== 6) {
      //merge the invoice
      if (invoiceRef?.current?.getInvoiceData()) {
        setInputData({
          ...inputData,
          toInvoiceList: [invoiceRef?.current?.getInvoiceData()],
        });
      }
    }

  };



  const handleInputChange = (e) => {
    const elName = e.target.name;
    if (elName === "shipmentType") {
      setInputData({
        ...inputData,
        tckJob: {
          ...inputData["tckJob"],
          tckMstShipmentType: { shtId: e.target.value },
        },
      });
    } else if (elName === "documentType") {
      setInputData({ ...inputData, documentType: e.target.value });
    } else if (elName === "tckCtDrv.drvName") {
      sendRequest(
        `${T_CK_CT_DRV}&mDataProp_1=drvLicenseNo&sSearch_1=${e.target.value}`,
        "getDriver"
      );
    } else if (elName === "tckCtVeh.vhPlateNo") {
      sendRequest(
        `${T_CK_CT_VEH}&mDataProp_1=vhPlateNo&sSearch_1=${e.target.value}`,
        "getTruck"
      );
    } else if (elName === "tcoreAccnByJobPartyTo.accnId") {
      //for the additional fields
      setInputData({
        ...inputData,
        ...deepUpdateState(inputData, elName, e.target.value),
      });

      sendRequest(
        `api/v1/clickargo/clictruck/job/truck/addtl/attr`,
        "loadAddtlFields",
        "post",
        {
          toAccnId: e?.target?.value,
          coAccnId: inputData?.tcoreAccnByJobPartyCoFf.accnId,
        }
      );
    } else if (elName === "jobLoading") {
      setInputData({
        ...inputData,
        tckJob: {
          ...inputData?.tckJob,
          jobLoading: e.target.value,
        },
      });
    } else if (elName === "jobSubType") {
      setInputData({
        ...inputData,
        tckJob: {
          ...inputData?.tckJob,
          jobSubType: e.target.value,
        },
      });
    } else {
      setInputData({
        ...inputData,
        ...deepUpdateState(inputData, elName, e.target.value),
      });
    }
  };

  /*** Additional fields input change handler */
  const handleAddtlFieldsInputChange = (e) => {
    const { name, value } = e?.target;
    setAdditionalFieldsInputData(
      addtionalFieldsInputData.map((el) => {
        if (el?.tckCtConAddAttr?.caaId === name) {
          return { ...el, jaaValue: value };
        } else return el;
      })
    );
  };

  const handlePopupInputChange = (e) => {
    const elName = e.target.name;
    if (elName === "doNo") {
      setPopUpDoDetails({
        ...popUpDoDetails,
        ...deepUpdateState(popUpDoDetails, elName, e.target.value.trim()),
      });
    }
  };

  const saveTripDo = (doNo, tripId) => {
    setDlOpen(true);
    const popUpDoDetails = {
      tckCtTrip: { trId: tripId },
      doNo: doNo,
    };
    if (!doNo) {
      setDlOpen(false);
      return;
    }
    setRefresh(false);
    sendRequest(
      `/api/v1/clickargo/clictruck/tripdo/doCreate`,
      "createDo",
      "POST",
      popUpDoDetails
    );
  };
  const editTripDo = (doaId, doNo) => {
    setRefresh(false);
    sendRequest(
      `/api/v1/clickargo/clictruck/tripdo/tripDo/doDelete?doaId=${doaId}&doNo=${doNo}`,
      "deleteTripDo",
      "DELETE",
      {}
    );
  };

  const handleDateChange = (name, e) => {
    if (name === "tckJob.tckRecordDate.rcdDtStart") {
      let startDt = moment(e).format("YYYY/MM/DD");
      let expDt = moment(inputData?.tckJob?.tckRecordDate?.rcdDtExpiry).format(
        "YYYY/MM/DD"
      );
      if (expDt < startDt) {
        setInputData({
          ...inputData,
          tckJob: {
            ...inputData["tckJob"],
            tckRecordDate: { rcdDtStart: e, rcdDtExpiry: e },
          },
        });
      } else {
        setInputData({ ...inputData, ...deepUpdateState(inputData, name, e) });
      }
    } else if (name === "tckJob.tckRecordDate.rcdDtExpiry") {
      setInputData({ ...inputData, ...deepUpdateState(inputData, name, e) });
    } else {
      setInputData({ ...inputData, ...deepUpdateState(inputData, name, e) });
    }
  };

  const handleExitOnClick = () => {
    let prevUrl = history?.location?.state?.from;
    if (prevUrl) {
      history.push(prevUrl);
    } else {
      if (isTruckingOperator)
        history.push("/applications/services/job/to/truck");
      else if (isFFCO) history.push("/applications/services/job/ffco/truck");
      else history.push("/applications/services/job/coff/truck");
    }
  };

  const handleValidateFields = () => {
    const errorField = {};
    if (inputData?.tcoreAccnByJobPartyTo === null || inputData?.tcoreAccnByJobPartyTo === "") {
      errorField['tcoreAccnByJobPartyTo.accnId'] = "Truck Operator is required, please select at lease one!";
    }
    if (inputData?.jobShipmentRef === null || inputData?.jobShipmentRef === "") {
      errorField['jobShipmentRef'] = "Shipment Refernece is required!";
    }
    return errorField;
  };

  const handleSaveOnClick = () => {
    setLoading(true);
    setValidationErrors({});
    switch (viewType) {
      case "new":
        let reqBody = {};
        if (!domestic) {
          reqBody = {
            ...inputData,
            shipmentType: shipmentType,
            ...(jobTripChargesRef?.current?.getTripDetails() == null
              ? null
              : {
                  tckCtTripList: [jobTripChargesRef?.current?.getTripDetails()],
                }),
          };
        } else {
          reqBody = {
            ...inputData,
            shipmentType: shipmentType,
            ...(jobTripChargesRef?.current?.getTripList() == null
              ? null
              : { tckCtTripList: jobTripChargesRef?.current?.getTripList() }),
          };
        }

        if (isArrayNotEmpty(addtionalFieldsInputData)) {
          reqBody = {
            ...reqBody,
            addtlFields: [...addtionalFieldsInputData],
          };
        }

        if(Object.keys(handleValidateFields()).length === 0){
          sendRequest(`${truckUrlApi}`, "createJob", "POST", reqBody);
        }else {
          setLoading(false)
          setValidationErrors({...handleValidateFields()});
        }
        break;
      case "view":
      case "edit":
        let data = {};
        if (!domestic) {
          //reset the action in case from action user decided to click Save instead.
          data = {
            ...inputData,
            ...(jobTripChargesRef?.current?.getTripDetails() == null
              ? null
              : {
                  tckCtTripList: [jobTripChargesRef?.current?.getTripDetails()],
                }),
            ...(invoiceRef?.current?.getInvoiceData() == null
              ? null
              : { toInvoiceList: [invoiceRef?.current?.getInvoiceData()] }),
            action: null,
            addtlFields: [...addtionalFieldsInputData],
          };

          //   sendRequest(`${truckUrlApi}` + jobId, "updateJob", "PUT", data);
        } else {
          data = {
            ...inputData,
            ...(jobTripChargesRef?.current?.getTripList() == null
              ? null
              : { tckCtTripList: jobTripChargesRef?.current?.getTripList() }),
            //CT-68 - reset the action in case from action user decided to click Save instead.
            action: null,
            addtlFields: [...addtionalFieldsInputData],
          };
        }
        if (acceptedStatus){
          data.modifyAcceptTrip = true
        }
        sendRequest(`${truckUrlApi}` + jobId, "updateJob", "PUT", data);
        break;
      default:
        break;
    }
  };

  const handleConfirmAction = (e) => {
    // Moved validation to BE
    // if (openSubmitConfirm?.action === 'START' && start === false) {
    //     // setOpenAddPopUp(true)
    //     setStartErrorOpen({ ...startErrorOpen, msg: t("job:msg.startErrorMsg"), open: true });
    //     setTabIndex(5);
    //     return;
    // }

    if (
      [
        "VERIFY_BILL",
        "REJECT_BILL",
        "ACKNOWLEDGE_BILL",
        "APPROVE_BILL",
        "REJECT",
      ].includes(openSubmitConfirm?.action) &&
      !jobRemarks?.msg
    ) {
      setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });

      if (
        ["VERIFY_BILL", "ACKNOWLEDGE_BILL", "APPROVE_BILL"].includes(
          openSubmitConfirm?.action
        ) &&
        jobRemarks?.open === true
      ) {
        setJobRemarks({
          ...jobRemarks,
          open: false,
          remarksType: (
            openSubmitConfirm?.action === "VERIFY_BILL"
              ? "V"
              : openSubmitConfirm?.action === "APPROVE_BILL" ||
                openSubmitConfirm?.action === "ACKNOWLEDGE_BILL"
              ? "A"
              : openSubmitConfirm?.action === "REJECT"
          )
            ? "J"
            : "R",
        });
      } else {
        setJobRemarks({
          ...jobRemarks,
          open: true,
          remarksType: (
            openSubmitConfirm?.action === "VERIFY_BILL"
              ? "V"
              : openSubmitConfirm?.action === "APPROVE_BILL" ||
                openSubmitConfirm?.action === "ACKNOWLEDGE_BILL"
              ? "A"
              : openSubmitConfirm?.action === "REJECT"
          )
            ? "J"
            : "R",
        });
        return;
      }
    }

    // Moved validation to BE
    //If the job source is xml and is mobile enabled, and the action is DELIVERY, make the POD required.
    // if (openSubmitConfirm?.action === 'STOP' && stop === false && inputData?.jobMobileEnabled === 'Y') {
    //     // setOpenAddPopUp(true)
    //     setStopErrorOpen({ ...stopErrorOpen, msg: t("job:msg.stopErrorMsg"), open: true });
    //     setTabIndex(5);
    //     return;
    // }

    setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
    setOpenAddPopUp(false);

    let errors = handleValidateFields();
    if (Object.keys(errors).length === 0) {
      setLoading(true);

      let reqBody = {};
      if (!domestic) {
        //setting input data in case validation exception is  thrown
        setInputData({
          ...inputData,
          action: openSubmitConfirm?.action,
          ...(jobTripChargesRef?.current?.getTripDetails() == null
            ? null
            : {
                tckCtTripList: [jobTripChargesRef?.current?.getTripDetails()],
              }),
          ...(invoiceRef?.current?.getInvoiceData() == null
            ? null
            : { toInvoiceList: [invoiceRef?.current?.getInvoiceData()] }),
        });
        reqBody = {
          ...inputData,
          action: openSubmitConfirm?.action,
          ...(jobTripChargesRef?.current?.getTripDetails() == null
            ? null
            : {
                tckCtTripList: [jobTripChargesRef?.current?.getTripDetails()],
              }),
          ...(invoiceRef?.current?.getInvoiceData() == null
            ? null
            : { toInvoiceList: [invoiceRef?.current?.getInvoiceData()] }),
          ...(jobRemarks?.msg ? { jobRemarks: jobRemarks?.msg } : null),
        };
      } else {
        const domesticsTrips = jobTripChargesRef?.current?.getTripList();
        reqBody = {
          ...inputData,
          action: openSubmitConfirm?.action,
          ...(Object.keys(invoiceData).length > 0
            ? { tckCtToInvoice: invoiceData }
            : null),
          ...(jobRemarks?.msg ? { jobRemarks: jobRemarks?.msg } : null),
          tckCtTripList: domesticsTrips
            ? domesticsTrips
            : inputData?.tckCtTripList,
        };
        setInputData({ ...inputData, ...reqBody });
      }

      sendRequest(
        `${truckUrlApi}` + jobId,
        openSubmitConfirm.action.toLowerCase(),
        "PUT",
          {
            ...reqBody,
            ignorePickupDropOfAtt: true // ignorePickupDropOfAtt: Makes PICKUP & DROP-OFF attachments non-mandatory when marking the job as Delivered.
          }
      );
    } else {
      return;
    }
  };

  const handleStartAction = (e) => {
    setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
    setLoading(true);
    if (openSubmitConfirm?.action === "START") {
      setPopUpDoDetails(popupDoDefaultValue);
      setLoading(false);
      // setOpenAddPopUp(true)
      handleConfirmAction(e);
    }
  };
  //
  // const handleStopAction = (e) => {
  //   setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
  //   setLoading(true);
  //   if (openSubmitConfirm?.action === "STOP") {
  //     setPopUpDoDetails(popupDoDefaultValue);
  //     setLoading(false);
  //     // setOpenAddPopUp(true)
  //     handleConfirmAction(e);
  //   }
  // };

  const handleAction = (e) => {
    if (openSubmitConfirm?.action === "START") {
      handleStartAction(e);
    } else if (openSubmitConfirm?.action === "STOP") {
      setOpenDeliveredPopUp(true);
      setOpenSubmitConfirm({action: openSubmitConfirm?.action, open: false});
    } else {
      handleConfirmAction(e);
    }
  };

  const downloadDOHandler = (id) => {
    sendRequest(
      `/api/v1/clickargo/clictruck/tripdo/tripDoAttach/fileData?id=${id}&type=doAttach`,
      "previewDo"
    );
  };

  const deleteDOHandler = (id, type) => {
    setRefresh(false);
    sendRequest(
      `/api/v1/clickargo/clictruck/tripdo/tripDoAttach/deleteDoAttach?id=${id}&type=${type}`,
      "deleteAttachment",
      "DELETE",
      {}
    );
  };

  const uploadDeliveryOrder = (e, tripId, doNo, type) => {
    e.preventDefault();
    const file = e.target.files[0];
    if (!file) return;

    const fileReader = new FileReader();
    fileReader.readAsArrayBuffer(file);
    fileReader.onload = (e) => {
      const uint8Array = new Uint8Array(e.target.result);
      const imgStr = Uint8ArrayToString(uint8Array);
      const base64Sign = btoa(imgStr);

      // Define popUpDoDetails as a new object
      const popUpDoDetails = {
        doaName: file.name,
        doaData: base64Sign,
        tckCtTrip: { trId: tripId },
        ckCtTripDo: { doNo: doNo },
      };

      setRefresh(false);
      setDlOpen(true);
      sendRequest(
          `/api/v1/clickargo/clictruck/tripdo/doattach?type=${type}`,
          "uploadFile",
          "POST",
          popUpDoDetails
      );
    };
  };


  const handleWarningAction = (e) => {
    if (validation?.["alreadyDroppedOff"] && Object.keys(validation).includes("alreadyDroppedOff")) {
      history.push("/applications/services/job/to/truck")
    }
    setOpenWarning(false);
    setWarningMessage("");
  };

  const handleWarningPopup = () => {
    setWarningProps({ open: false, msg: "" });
  };

  const handleSubmitOnClick = () => {
    setInputData({ ...inputData, action: "SUBMIT" });
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: "SUBMIT",
      open: true,
    });
  };

  const handleActionOnClick = (actionUp) => {
    setInputData({ ...inputData, action: actionUp });
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: actionUp,
      open: true,
    });
  };

  const eventHandler = (action) => {
    const lowerAction = action.toLowerCase();
    const simpleActions = ["cancel", "delete", "reject"];
    const confirmActions = ["verify_bill", "verify"];
    switch (lowerAction) {
      case "save":
        handleSaveOnClick();
        break;
      case "exit":
        handleExitOnClick();
        break;
      case "submit":
        handleSubmitOnClick();
        break;
      case "clone":
        handleOpenClonePopup();
        break;
      case "split":
        setOpenSplitPopUp(true);
        break;
      default:
        if (simpleActions.includes(lowerAction)) {
          handleActionOnClick(action);
        } else if (confirmActions.includes(lowerAction)) {
          if (!isTruckingOperator && isSuspended) {
            setCustomErrorOpen({
              ...customErrorOpen,
              title: t("job:msg.verifyError"),
              msg: t("job:msg.accnSuspnd"),
              open: true,
            });
          } else {
            setOpenSubmitConfirm({action, open: true});
          }
        } else {
          setOpenSubmitConfirm({action, open: true});
        }
        break;
    }
  };

  const viewFile = (fileName, data) => {
    previewPDF(fileName, data);
  };

  const handleOpenClonePopup = () => {
    setOpenClonePopUp(true);
  };

  const handleCloneOnChange = (e) => {
    const name = e.target.name;
    const value = e.target.value;
    if (name.includes("numberOfCopy")) {
      // set duplicate jobs to max of 99
      setCloneData({ ...cloneData, [name]: value?.slice(0, 2) });
    } else {
      const checked = e.target.checked;
      setCloneData({ ...cloneData, [name]: checked });
    }
  };

  const handleCloneSubmit = () => {
    if (
      cloneData?.numberOfCopy?.length == 0 ||
      cloneData?.numberOfCopy == "0" ||
      !cloneData?.numberOfCopy
    ) {
      setWarningProps({ open: true, msg: t("job:msg.noOfJobErr") });
    } else {
      setLoading(true);
      const url = `/api/v1/clickargo/clictruck/job/truck/${jobId}`;
      const method = "PUT";
      const action = "CLONE";
      const bodyReq = { ...inputData };

      for (const key in cloneData) {
        if (cloneData.hasOwnProperty(key)) {
          bodyReq[key] = cloneData[key];
        }
      }
      bodyReq["action"] = action;
      setOpenClonePopUp(false);
      sendRequest(url, "duplicateJOB", method, bodyReq);
    }
  };

  const handleSplitOnChange = (e) => {
    const name = e.target.name;
    const value = e.target.value;
    setNumOfSplit(value);

  };

  const handleSplitSubmit = () => {

    setLoading(true);
    // https://jira.vcargocloud.com/browse/CT2SG-228
    // If TO input 3, system will duplicate the job 2 times, resulting in a total of 3 jobs (i.e. 1 original, 2 duplicated).
    const url = `/api/v1/clickargo/clictruck/job/truck/splitJob/${jobId}?num=${numOfSplit-1}`;
    const method = "POST";

    setOpenSplitPopUp(false);
    sendRequest(url, "splitJOB", method);
  }

  useEffect(() => {
    // hidden invoice for FF-CO
    if (isFFCO) {
      if (showTabs.invoice || showTabs.midMileInvoice) {
        setShowTabs({ ...showTabs, invoice: false, midMileInvoice: false });
      }
    }
  }, [isFFCO, showTabs]);

  const isEditDriverTruck = () => {
    return inputData?.tckJob?.tckMstJobState?.jbstId === JobStates.ACP.code 
        &&  user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_TO.code
  }
  let bcLabel = t("cargoowners:form.viewJob");

  //--------------------form button ---------------------------------------------------------------------------------------------------------------------
  let formButtons;
  if (!loading) {
    formButtons = (
      <C1FormButtons
        options={{
          back: {
            show: true,
            eventHandler: handleExitOnClick,
          },
          save: {
            show: true,
            eventHandler: handleSaveOnClick,
          },
        }}
      />
    );

    if (viewType) {
      switch (viewType) {
        case "edit":
          bcLabel = t("form.editJob");
          formButtons = (
            <C1FormButtons
              options={getFormActionButton(
                initialButtons,
                controls,
                eventHandler
              )}
            />
          );
          break;
        case "view":
          formButtons = (
            <C1FormButtons
              options={getFormActionButton(
                initialButtons,
                controls,
                eventHandler
              )}
            />
          );
          break;
        case "new":
          bcLabel = t("form.newJob");
          break;
        default:
          break;
      }
    }
  }

  if ((viewType === "new" && !shipmentType) || (viewType != "new" && !jobId)) {
    handleExitOnClick();
  }

  const titleStatus =
      (inputData?.tckJob?.tckMstJobState?.jbstId === JobStates.NEW.code && inputData?.hasRemarks)
          ? JobStates.REJ.code
          : inputData?.tckJob?.tckMstJobState?.jbstId || JobStates.DRF.code.toUpperCase();

  return loading ? (
    <MatxLoading />
  ) : (
    <React.Fragment>
      <C1FormDetailsPanel
        breadcrumbs={[
          {
            name: "ClicTruck Job List",
            path: isTruckingOperator
              ? "/applications/services/job/to/truck"
              : "/applications/services/job/coff/truck",
          },
          {
            name:
              viewType === "new"
                ? t("cargoowners:form.newJobDetails")
                : viewType === "view"
                ? t("cargoowners:form.viewJobDetails")
                : viewType === "edit"
                ? t("cargoowners:form.editJobDetails")
                : t("cargoowners:form.jobDetails"),
          },
        ]}
        title={bcLabel}
        titleStatus={titleStatus}
        formButtons={formButtons}
        initialValues={{ ...inputData }}
        values={{ ...inputData }}
        snackBarOptions={snackBarOptions}
        isLoading={loading}
      >
        {(props) => (
          <Grid container spacing={3}>
            <Grid item xs={12}>
              <Paper>
                <Tabs
                  className="mt-4"
                  value={tabIndex}
                  onChange={handleTabChange}
                  indicatorColor="primary"
                  textColor="primary"
                  variant="scrollable"
                  scrollButtons="auto"
                >
                  {dynamicTabs &&
                    dynamicTabs.map((item, ind) => {
                      if (item?.id === "driver" && !showTabs?.driver)
                        return null;
                      else if (
                        item?.id == "deliveryOrders" &&
                        !showTabs?.deliveryOrders
                      )
                        return null;
                      else if (item?.id === "invoice" && !showTabs?.invoice)
                        return null;
                      else if (
                        item?.id === "midMileInvoice" &&
                        !showTabs?.midMileInvoice
                      )
                        return null;
                      else if (item?.id == "fmTrip" && !showTabs?.fmTrip)
                        return null;
                      else if (item?.id == "mmTrip" && !showTabs?.mmTrip)
                        return null;
                      else if (item?.id == "tracking" && !showTabs?.tracking)
                        return null;
                      else if (
                        item?.id == "rejectRemarks" &&
                        !inputData?.hasRemarks
                      )
                        return null;
                      else
                        return (
                          <TabsWrapper
                            className="capitalize"
                            value={item?.idx}
                            disabled={item?.disabled}
                            label={
                              <TabLabel
                                viewType={viewType}
                                key={ind}
                                tab={item}
                                errors={validationErrors}
                              />
                            }
                            key={item?.idx}
                            icon={item?.icon}
                            {...tabScroll(ind)}
                          />
                        );
                    })}
                </Tabs>
                <Divider className="mb-6" />
                <JobTruckContext.Provider
                  value={{
                    inputData,
                    addtionalFieldsInputData,
                    setInputData,
                    viewType,
                    handleInputChange,
                    handleDateChange,
                    handleAddtlFieldsInputChange,
                    locale: { t },
                    isDisabled,
                    errors: validationErrors,
                    jobState,
                    shipmentType,
                    tcrData,
                    setTcrData,
                    //handleInsertTcrList,
                    tcrList,
                    totalTripCharges,
                    invoiceData,
                    setInvoiceData,
                    setOpenWarning,
                    setWarningMessage,
                    isWithSession: true,
                    additionalFields,
                    snackBarOptions,
                    setSnackBarOptions,
                    ableToSaveModifiesTrip: ableToSaveModifiesTrip,
                    acceptedStatus: acceptedStatus,
                    ableToModifiesCargo: ableToModifiesCargo,
                  }}
                >
                  {tabIndex === 0 && (
                    <C1TabInfoContainer
                      guideId="clictruck.job.general.details"
                      title="empty"
                      guideAlign="right"
                      open={false}
                    >
                      <JobNewDetails
                        defaultValueLoading={defaultValueLoading}
                        defaultValueJobType={defaultValueJobType}
                        shipmentType={shipmentType}
                        errors={validationErrors}
                      />
                    </C1TabInfoContainer>
                  )}

                  {showTabs?.fmTrip && tabIndex === 1 && (
                    <C1TabInfoContainer
                      guideId="clictruck.job.trip.charges"
                      title="empty"
                      guideAlign="right"
                      open={false}
                    >
                      <JobTripCharges
                        ref={jobTripChargesRef}
                        errors={validationErrors}
                      />
                    </C1TabInfoContainer>
                  )}

                  {showTabs?.mmTrip && tabIndex === 2 && (
                    <C1TabInfoContainer
                      guideId="clictruck.job.trip.charges"
                      title="empty"
                      guideAlign="right"
                      open={false}
                    >
                      {
                        <JobTripChargesDomestic
                          ref={jobTripChargesRef}
                          errors={validationErrors}
                        />
                      }
                    </C1TabInfoContainer>
                  )}
                  {tabIndex === 3 && (
                    <C1TabInfoContainer
                      guideId="clictruck.job.supporting.docs"
                      title="empty"
                      guideAlign="right"
                      open={false}
                    >
                      <JobAuthLetters
                        viewType={viewType}
                        inputData={inputData}
                      />
                    </C1TabInfoContainer>
                  )}
                  {showTabs?.driver && tabIndex === 4 && (
                    <C1TabInfoContainer
                      guideId="clictruck.job.driver.truck"
                      title="empty"
                      guideAlign="right"
                      open={false}
                    >
                      <JobDriverAssign
                        errors={validationErrors}
                        inputData={inputData}
                        handleInputChange={handleInputChange}
                        setSnackBarOptions={setSnackBarOptions}
                      />
                    </C1TabInfoContainer>
                  )}
                  {/** Delivery Orders tab */}
                  {showTabs?.deliveryOrders && tabIndex === 5 && (
                    <C1TabInfoContainer
                      guideId="clictruck.job.delivery.order"
                      title="empty"
                      guideAlign="right"
                      open={false}
                    >
                      <JobDeliveryOrders
                        inputData={popUpDoDetails}
                        jobSource={inputData?.jobSource}
                        isMobile={inputData?.jobMobileEnabled === "Y"}
                        data={popUpAttDetails}
                        state={inputData?.tckJob?.tckMstJobState}
                        truckJobId={jobId}
                        doId={doId}
                        setDoId={setDoId}
                        setStart={setStart}
                        setStop={setStop}
                        isRefresh={isRefresh}
                        viewType={"view"}
                        handleInputChange={handleInputChange}
                        handlePopupInputChange={handlePopupInputChange}
                        uploadDeliveryOrder={uploadDeliveryOrder}
                        downloadDOHandler={downloadDOHandler}
                        deleteDOHandler={deleteDOHandler}
                        locale={t}
                        errors={popUpFieldError}
                        validationErrors={validationErrors}
                        saveTripDo={saveTripDo}
                        editTripDo={editTripDo}
                      />
                    </C1TabInfoContainer>
                  )}
                  {showTabs?.invoice && tabIndex === 6 && (
                    <C1TabInfoContainer
                      guideId="clictruck.job.invoice.details"
                      title="empty"
                      guideAlign="right"
                      open={false}
                    >
                      <JobInvoice
                        ref={invoiceRef}
                        tripId={inputData?.tckCtTripList[0]?.trId}
                        idx={0}
                      />
                    </C1TabInfoContainer>
                  )}
                  {showTabs?.midMileInvoice && tabIndex === 7 && (
                    <C1TabInfoContainer
                      guideId="clictruck.job.invoice.details"
                      title="empty"
                      guideAlign="right"
                      open={false}
                    >
                      <JobDomesticInvoice />
                    </C1TabInfoContainer>
                  )}
                  {inputData?.hasRemarks && tabIndex == 8 && (
                    <C1TabInfoContainer
                      guideId="clictruck.job.remarks"
                      title="empty"
                      guideAlign="right"
                      open={false}
                    >
                      <JobRejectRemarks
                        parentJobId={inputData?.tckJob?.jobId}
                      />
                    </C1TabInfoContainer>
                  )}
                  {tabIndex === 9 && (
                    <C1TabInfoContainer
                      guideId="clictruck.job.audit"
                      title="empty"
                      guideAlign="right"
                      open={false}
                    >
                      <C1AuditTab
                        filterId={inputData.jobId ? inputData.jobId : "draft"}
                      ></C1AuditTab>
                    </C1TabInfoContainer>
                  )}
                  {showTabs?.tracking && tabIndex === 10 && (
                    <C1TabInfoContainer
                      guideId="clictruck.job.tracking"
                      title="empty"
                      guideAlign="right"
                      open={false}
                    >
                      <JobTrack />
                    </C1TabInfoContainer>
                  )}
                </JobTruckContext.Provider>
              </Paper>
            </Grid>
          </Grid>
        )}
      </C1FormDetailsPanel>

      {/* For submit confirmation */}
      <ConfirmationDialog
        open={openSubmitConfirm?.open}
        onConfirmDialogClose={() =>
          setOpenSubmitConfirm({
            ...openSubmitConfirm,
            action: null,
            open: false,
          })
        }
        text={t("job:msg.confirmation", {
          action: Actions[openSubmitConfirm?.action]?.text,
        })}
        title={t("job:popup.confirmation")}
        onYesClick={(e) => handleAction(e)}
      />

      {/**  Add Delivery Order Popup*/}
      <C1PopUp
        maxWidth={"lg"}
        title={t("listing:orderDetails.title")}
        openPopUp={openAddPopUp}
        setOpenPopUp={setOpenAddPopUp}
        actionsEl={
          <C1IconButton tooltip={t("buttons:submit")} childPosition="right">
            <NearMeOutlinedIcon
              color="primary"
              fontSize="large"
              onClick={(e) => handleConfirmAction(e)}
            ></NearMeOutlinedIcon>
          </C1IconButton>
        }
      >
        <AddDelOrderPopup
          inputData={popUpDoDetails}
          data={popUpAttDetails}
          state={inputData?.tckJob?.tckMstJobState}
          truckJobId={jobId}
          doId={doId}
          setDoId={setDoId}
          setStart={setStart}
          setStop={setStop}
          isRefresh={isRefresh}
          viewType={"view"}
          handleInputChange={handleInputChange}
          handlePopupInputChange={handlePopupInputChange}
          uploadDeliveryOrder={uploadDeliveryOrder}
          downloadDOHandler={downloadDOHandler}
          deleteDOHandler={deleteDOHandler}
          locale={t}
          errors={popUpFieldError}
        />
      </C1PopUp>

      {/* For rejection remarks - TO BE REMOVED */}
      <C1PopUp
        maxWidth={"md"}
        title={t("listing:remarks.rejRmk")}
        openPopUp={rejectRemarks?.open}
        setOpenPopUp={setRejectRemarks}
        actionsEl={
          <C1IconButton
            disabled={!rejectRemarks?.msg}
            tooltip={t("buttons:submit")}
            childPosition="right"
          >
            <NearMeOutlinedIcon
              color="primary"
              fontSize="large"
              onClick={(e) => {
                setRejectRemarks({ ...rejectRemarks, open: false });
                return handleConfirmAction(e);
              }}
            ></NearMeOutlinedIcon>
          </C1IconButton>
        }
      >
        <C1TextArea
          textLimit={256}
          required
          name="rejectRemarks.msg"
          value={getValue(rejectRemarks?.msg)}
          onChange={(e) =>
            setRejectRemarks({ ...rejectRemarks, msg: e?.target?.value })
          }
        />
      </C1PopUp>

      {/* For Billing Action Remarks */}
      <C1PopUp
        maxWidth={"md"}
        title={t("listing:remarks.rmk")}
        openPopUp={jobRemarks?.open}
        setOpenPopUp={setJobRemarks}
        actionsEl={
          <C1IconButton
            disabled={
              ["REJECT", "REJECT_BILL"].includes(openSubmitConfirm?.action) &&
              !jobRemarks?.msg
                ? true
                : false
            }
            tooltip={t("buttons:submit")}
            childPosition="right"
          >
            <NearMeOutlinedIcon
              color="primary"
              fontSize="large"
              onClick={(e) => {
                setJobRemarks({ ...jobRemarks, open: false });
                return handleConfirmAction(e);
              }}
            ></NearMeOutlinedIcon>
          </C1IconButton>
        }
      >
        <C1TextArea
          textLimit={256}
          required={["REJECT", "REJECT_BILL"].includes(
            openSubmitConfirm?.action
          )}
          name="jobRemarks.msg"
          value={getValue(jobRemarks?.msg)}
          onChange={(e) =>
            setJobRemarks({ ...jobRemarks, msg: e?.target?.value })
          }
        />
      </C1PopUp>

      {/* Add Clone PopUp */}
      <C1PopUp
        maxWidth={"xs"}
        title={t("job:clone.duplicateJobs")}
        openPopUp={openClonePopUp}
        setOpenPopUp={setOpenClonePopUp}
        actionsEl={
          <C1IconButton tooltip={t("buttons:submit")} childPosition="right">
            <NearMeOutlinedIcon
              color="primary"
              fontSize="large"
              onClick={handleCloneSubmit}
            />
          </C1IconButton>
        }
      >
        <C1CategoryBlock
          icon={<FileCopyOutlinedIcon />}
          title={t("job:clone.cloneDetails")}
        >
          <Grid container alignItems="center" spacing={3}>
            <Grid item xs={12}>
              <C1InputField
                // set duplicate jobs to max of 99 -> handleCloneOnChange slice only 2 digits
                inputProps={{ min: 0, max: 99 }}
                name="numberOfCopy"
                type="number"
                isInteger={true}
                label={t("job:clone.numberOfJobs")}
                onChange={handleCloneOnChange}
                value={cloneData?.numberOfCopy ? cloneData?.numberOfCopy : 0}
              />
            </Grid>
            <Grid item xs={12}>
              <FormControlLabel
                control={
                  <Checkbox
                    name="generalDetailsClone"
                    checked={cloneData?.generalDetailsClone}
                    onChange={handleCloneOnChange}
                    // disabled={isDisabled}
                  />
                }
                label={t("job:clone.generalDetails")}
              />
            </Grid>
            <Grid item xs={12}>
              <FormControlLabel
                control={
                  <Checkbox
                    name="partiesDetailsClone"
                    checked={cloneData?.partiesDetailsClone}
                    onChange={handleCloneOnChange}
                    // disabled={isDisabled}
                  />
                }
                label={t("job:clone.partiesDetails")}
              />
            </Grid>
            <Grid item xs={12}>
              <FormControlLabel
                control={
                  <Checkbox
                    name="contactDetailsClone"
                    checked={cloneData?.contactDetailsClone}
                    onChange={handleCloneOnChange}
                    // disabled={isDisabled}
                  />
                }
                label={t("job:clone.contactDetails")}
              />
            </Grid>
            <Grid item xs={12}>
              <FormControlLabel
                control={
                  <Checkbox
                    name="locationDetailsClone"
                    checked={cloneData?.locationDetailsClone}
                    onChange={handleCloneOnChange}
                    // disabled={isDisabled}
                  />
                }
                label={t("job:clone.locationDetails")}
              />
            </Grid>
            <Grid item xs={12}>
              <FormControlLabel
                control={
                  <Checkbox
                    name="cargoDetailsClone"
                    checked={cloneData?.cargoDetailsClone}
                    onChange={handleCloneOnChange}
                    // disabled={isDisabled}
                  />
                }
                label={t("job:clone.cargoDetails")}
              />
            </Grid>
          </Grid>
        </C1CategoryBlock>
      </C1PopUp>

      {/* Split */}
      <C1PopUp
        maxWidth={"xs"}
        title={"Split Jobs"}
        openPopUp={openSplitPopUp}
        setOpenPopUp={setOpenSplitPopUp}
        actionsEl={
          <C1IconButton tooltip={t("buttons:submit")} childPosition="right">
            <NearMeOutlinedIcon
              color="primary"
              fontSize="large"
              onClick={handleSplitSubmit}
            />
          </C1IconButton>
        }
      >
        <Grid container alignItems="center" spacing={3}>
          <Grid item xs={12}>
            <C1InputField
              inputProps={{ min: 2, max: 20 }}
              name="numberOfCopy"
              type="number"
              isInteger={true}
              label={t("job:clone.numberOfJobs")}
              onChange={handleSplitOnChange}
              value={numOfSplit}
            />
          </Grid>
        </Grid>
      </C1PopUp>
      <DeliveredPopup
          translate={t}
          viewType={viewType}
          inputData={inputData}
          openPopUp={openDeliveredPopUp}
          handleSubmit={handleConfirmAction}
          setOpenPopUp={setOpenDeliveredPopUp}
      />
      <Dialog maxWidth="xs" open={openWarning}>
        <div className="p-8 text-center w-360 mx-auto">
          <h4 className="capitalize m-0 mb-2">{"Warning"}</h4>
          <p>{warningMessage}</p>
          <div className="flex justify-center pt-2 m--2">
            <Button
              className="m-2 rounded hover-bg-primary px-6"
              variant="outlined"
              color="primary"
              onClick={(e) => handleWarningAction(e)}
            >
              {t("job:popup.ok")}
            </Button>
          </div>
        </div>
      </Dialog>

      {/** Error prompt for start job */}
      <C1DialogPrompt
        confirmationObj={{
          openConfirmPopUp: startErrorOpen?.open,
          onConfirmationDialogClose: () =>
            setStartErrorOpen({ ...startErrorOpen, open: false }),
          text: startErrorOpen?.msg,
          title: t("job:msg.startErrorTitle"),
          onYesClick: () =>
            setStartErrorOpen({ ...startErrorOpen, open: false }),
          yesBtnText: "Ok",
        }}
      />
      {/** Error prompt for stop job */}
      <C1DialogPrompt
        confirmationObj={{
          openConfirmPopUp: stopErrorOpen?.open,
          onConfirmationDialogClose: () =>
            setStopErrorOpen({ ...stopErrorOpen, open: false }),
          text: stopErrorOpen?.msg,
          title: t("job:msg.stopErrorTitle"),
          onYesClick: () => setStopErrorOpen({ ...stopErrorOpen, open: false }),
          yesBtnText: "Ok",
        }}
      />
      {/** Error prompt for duplicate Do No */}
      <C1DialogPrompt
        confirmationObj={{
          openConfirmPopUp: duplicateErrorOpen?.open,
          onConfirmationDialogClose: () =>
            setDuplicateErrorOpen({ ...duplicateErrorOpen, open: false }),
          text: duplicateErrorOpen?.msg,
          title: t("job:msg.error"),
          onYesClick: () =>
            setDuplicateErrorOpen({ ...duplicateErrorOpen, open: false }),
          yesBtnText: "Ok",
        }}
      />
      {/** Custom Error prompt */}
      <C1DialogPrompt
        confirmationObj={{
          openConfirmPopUp: customErrorOpen?.open,
          onConfirmationDialogClose: () =>
            setCustomErrorOpen({ ...customErrorOpen, open: false }),
          text: customErrorOpen?.msg,
          title: customErrorOpen?.title,
          onYesClick: () =>
            setCustomErrorOpen({ ...customErrorOpen, open: false }),
          yesBtnText: "Ok",
        }}
      />

      <Backdrop open={dlOpen} className={bdClasses.backdrop}>
        {" "}
        <CircularProgress color="inherit" />
      </Backdrop>

      <C1Warning
        warningMessage={warningProps}
        handleWarningAction={handleWarningPopup}
      />
    </React.Fragment>
  );
};

export default withErrorHandler(JobTruckFormDetails);
