import { Divider, Grid, Paper, Tabs } from "@material-ui/core";
import {Notifications, Schedule} from "@material-ui/icons";
import Assignment from "@material-ui/icons/AssignmentOutlined";
import ChatOutlinedIcon from "@material-ui/icons/ChatOutlined";
import FileCopy from "@material-ui/icons/FileCopyOutlined";
import NearMeOutlinedIcon from "@material-ui/icons/NearMeOutlined";
import SettingsOutlinedIcon from "@material-ui/icons/SettingsOutlined";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";

import C1AuditTab from "app/c1component/C1AuditTab";
import C1DialogPrompt from "app/c1component/C1DialogPrompt";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1IconButton from "app/c1component/C1IconButton";
import C1PopUp from "app/c1component/C1PopUp";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";
import useHttp from "app/c1hooks/http";
import {
  AccnProcessTypes,
  AccountTypes,
  AccountsProcessStates,
  Actions,
  COMMON_ATTACH_LIST_BY_ACCNID_URL,
  FINANCING_OPTIONS,
} from "app/c1utils/const";
import { getFormActionButton } from "app/c1utils/formActionUtils";
import { deepUpdateState } from "app/c1utils/stateUtils";
import { tabScroll } from "app/c1utils/styles";
import { getValue, isArrayNotEmpty, isStringEmpty } from "app/c1utils/utility";
import { decodeString, encodeString } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import TabLabel from "app/portedicomponent/TabLabel";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";
import { ConfirmationDialog, MatxLoading } from "matx";

import AccountRemarks from "./AccountRemarks";
import AccountSuppDocs from "./AccountSuppDocs";
import CompanyDetails from "./CompanyDetails";
import Configurations from "./Configurations";
import AddRemarkPopup from "./popups/AddRemarkPopup";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import AccountBalanceOutlinedIcon from "@material-ui/icons/AccountBalanceOutlined";
import { register } from "serviceWorker";
import C1Warning from "app/c1component/C1Warning";
import { debounce } from "lodash";
import NotificationFormDetails from "../../notification/NotificationFormDetails";

/**@description Manage Account form details. */
const AccountProfileFormDetails = () => {
  //useParams hook to acces the dynamic pieaces of the URL
  let { viewType, id } = useParams();

  let isFromProfile = id === "my";

  let history = useHistory();
  const { t } = useTranslation([
    "register",
    "common",
    "admin",
    "buttons",
    "listing",
    "cargoowners",
    "opadmin",
  ]);
  const { user } = useAuth();
  const [controls, setControls] = useState([]);

  const {
    isLoading,
    isFormSubmission,
    res,
    validation,
    error,
    urlId,
    sendRequest,
  } = useHttp();

  //useState with initial value of 0 for tabIndex
  const [tabIndex, setTabIndex] = useState(0);
  const [loading, setLoading] = useState(false);
  const [inputData, setInputData] = useState({});
  const [validationErrors, setValidationErrors] = useState({});
  const [accountId, setAccountId] = useState("");
  const [decodedId, setDecodedId] = useState({});
  const [hideSave, setHideSave] = useState(false);
  const [uniqueTaxCheck, setUniqueTaxCheck] = useState({
    loading: false,
    errMsg: null,
  });

  //for financing related
  const [showFinancer, setShowFinancer] = useState(false);
  const [accnType, setAccnType] = useState();
  const [registerFinanceBtn, setRegisterFinanceBtn] = useState({
    show: false,
    url: null,
  });

  const [openSubmitConfirm, setOpenSubmitConfirm] = useState({
    action: null,
    open: false,
  });
  const [accnProcessType, setAccnProcessType] = useState("ACCN_REGISTRATION");
  const defaultSnackbarValue = {
    success: false,
    successMsg: "",
    error: false,
    errorMsg: "",
    redirectPath: "",
  };
  const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);

  const tabList = [
    {
      name: "cmpnyDtls",
      text: t("register:companyDetails.title"),
      icon: <FileCopy />,
      show: true
    },
    ...(user?.coreAccn?.TMstAccnType?.atypId === "ACC_TYPE_TO"
        ? [
          {
            text: t("Alerts"),
            icon: <Notifications />,
            show: true
          },
        ]
        : []),
    {
      name: "cnfgrtn",
      text: t("register:configurations.title"),
      icon: <SettingsOutlinedIcon />,
      show: true
    },
    { text: t("register:suppDocs.docs"), icon: <Assignment />, show: true },
    { text: t("register:remarks.title"), icon: <ChatOutlinedIcon />, show: [AccountTypes.ACC_TYPE_SP.code].includes(user?.coreAccn?.TMstAccnType?.atypId)},
    { text: t("common:audits.title"), icon: <Schedule />, show: true },
  ];

  const initialButtons = {
    back: { show: !isFromProfile, eventHandler: () => handleExitOnClick() },
    save: {
      show: (viewType == "new" && !hideSave) || viewType == "edit",
      eventHandler: () => handleSaveOnClick(),
    },
  };

  const [regDocs, setRegDocs] = useState([]);

  const [openRemarksPopup, setOpenRemarksPopup] = useState(false);
  const [popUpFieldError, setPopUpFieldError] = useState({});

  const remarksDefaultValue = {
    arRemark: "",
    atDtCreate: new Date(),
    atUidCreate: user?.id,
    tckMstRemarkType: {
      rtId: "",
    },
    tckMstWorkflowType: {
      wktId: "",
    },
  };

  const [remarksDetails, setRemarksDetails] = useState(remarksDefaultValue);
  const [terminableError, setTerminableError] = useState({
    msg: null,
    open: false,
  });

  const [displayEmpty, setDisplayEmpty] = useState(false);
  const [warningProps, setWarningProps] = useState({ open: false, msg: "" });

  useEffect(() => {
    setLoading(true);
    if (viewType !== "new") {
      if (id === "my") {
        if (user?.coreAccn?.accnId) {
          let accId = user.coreAccn.accnId;
          setAccountId(accId);
          sendRequest(
            `/api/v1/clickargo/manageaccn/${encodeString(accId)}`,
            "getAccn",
            "get"
          );
        }
      } else {
        setAccountId(id);
        setDecodedId(decodeString(id));
        sendRequest(`/api/v1/clickargo/manageaccn/${id}`, "getAccn", "get", {});
      }
    } else {
      sendRequest(`/api/v1/clickargo/manageaccn/-`, "newAccn", "get", {});
    }

    // eslint-disable-next-line
  }, [id]);

  useEffect(() => {
    if (!isLoading && !error && res && !validation) {
      setLoading(isLoading);
      switch (urlId) {
        case "newAccn": {
          setInputData({ ...inputData, ...res?.data });
          break;
        }
        case "getAccn":
          setHideSave(false);
          setSnackBarOptions({
            ...snackBarOptions,
            success: false,
          });

          setAccnType(res?.data?.accnDetails?.TMstAccnType.atypId);

          const financeOptionRes = modifySelectedOpmByAccnType(
            res?.data?.accnDetails?.TMstAccnType.atypId,
            res?.data?.financeOptions,
            true
          );
          setInputData({
            ...inputData,
            ...res?.data,
            financeOptions: financeOptionRes,
          });

          const showRegisterToFinancer =
            financeOptionRes === FINANCING_OPTIONS.OPM.code &&
            res?.data?.accnDetails?.TMstAccnType.atypId !==
              AccountTypes.ACC_TYPE_SP &&
            isFromProfile &&
            !res?.data?.opmRegistered;

          setRegisterFinanceBtn({
            ...registerFinanceBtn,
            show: showRegisterToFinancer,
            url: res?.data?.financerUrl,
          });

          setAccnProcessType(res?.data?.accnProcessType);
          let entityType = AccnProcessTypes.ACCN_REGISTRATION.code;
          if (viewType === "suspend") {
            entityType = AccnProcessTypes.ACCN_SUSPENSION.code;
            setAccnProcessType(AccnProcessTypes.ACCN_SUSPENSION.code);
          } else if (viewType === "terminate") {
            entityType = AccnProcessTypes.ACCN_TERMINATION.code;
            setAccnProcessType(AccnProcessTypes.ACCN_TERMINATION.code);
          } else if (viewType === "resumption") {
            entityType = AccnProcessTypes.ACCN_RESUMPTION.code;
            setAccnProcessType(AccnProcessTypes.ACCN_RESUMPTION.code);
          }

          const reqBody = {
            entityType: entityType,
            entityState:
              AccountsProcessStates[res?.data?.accnDetails?.accnStatus]
                ?.altCode,
            page: ["suspend", "terminate", "resumption"].includes(viewType)
              ? "VIEW"
              : viewType.toUpperCase(),
          };

          sendRequest(
            "/api/v1/clickargo/controls/",
            "fetchControls",
            "POST",
            reqBody
          );
          break;
        case "fetchControls": {
          // setControls([...res?.data]);
          let tmp = displaySaveIconOnly(res.data);
          setControls(tmp);
          break;
        }
        case "create":
          setInputData({ ...res?.data });
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("common:msg.saveSuccess"),
            redirectPath: `/manageAccount/edit/${encodeString(
              res?.data?.accnDetails?.accnId
            )}`,
          });
          break;
        case "update":
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("common:msg.updateSuccess"),
          });
          setInputData({
            ...res?.data,
            financeOptions: modifySelectedOpmByAccnType(
              res?.data?.accnDetails?.TMstAccnType.atypId,
              res?.data?.financeOptions,
              true
            ),
          });
          break;
        case "submit":
        case "approve":
        case "reject":
        case "suspend":
        case "terminate":
        case "resumption":
        case "delete":
          if (res?.data.accnTerminable === true) {
            setLoading(false);
            setTerminableError({
              ...terminableError,
              msg: t("common:msg.nonTerminableMsg"),
              open: true,
            });
          } else {
            let msg = t("common:common.msg.generalAction", {
              action: Actions[openSubmitConfirm?.action]?.result,
            });
            setLoading(false);
            setSnackBarOptions({
              ...snackBarOptions,
              success: true,
              successMsg: msg,
              redirectPath: "/manageAccounts/all/list",
            });
          }
          break;
        case "accnRegisterDelete":
          setLoading(false);
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("common:msg.deleteSuccess"),
            redirectPath: "/manageAccounts/all/list",
          });
          break;
        case "deActive":
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("admin:account.msg.deactivatedSuccess"),
            redirectPath: "/manageAccounts/all/list",
          });
          break;
        case "activate":
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("admin:account.msg.activatedSuccess"),
            redirectPath: "/manageAccounts/all/list",
          });
          break;
        case "getDocumentList":
          // setDocumentListData([...res?.data])
          break;
        case "loadSuppDocs":
          const newSuppDocs = res?.data?.map((value) => value);
          setRegDocs([...newSuppDocs]);
          if (id === "my")
            sendRequest(
              `${COMMON_ATTACH_LIST_BY_ACCNID_URL}${accountId}`,
              "loadExistingSuppDocs",
              "get"
            );
          else
            sendRequest(
              `${COMMON_ATTACH_LIST_BY_ACCNID_URL}${decodeString(id)}`,
              "loadExistingSuppDocs",
              "get"
            );
          break;
        case "loadExistingSuppDocs":
          setLoading(isLoading);
          const tempExistingDocArr = [...regDocs].map((value) => {
            const newValue = { ...value };
            const newExistingRegDocs = res?.data?.aaData?.find(
              (doc) =>
                doc?.tckMstAccnAttType?.tckMstWorkflowType?.wktId ===
                value?.wfTypeId
            );
            newValue.aatId = newExistingRegDocs?.aatId;
            newValue.aatName = newExistingRegDocs?.aatName;
            newValue.atDtValidility = newExistingRegDocs?.atDtValidility;
            newValue.atUidLupd = newExistingRegDocs?.atUidLupd;
            newValue.atDtLupd = newExistingRegDocs?.atDtLupd;
            newValue.aatLoc = newExistingRegDocs?.aatLoc || "";
            return newValue;
          });
          setRegDocs(tempExistingDocArr);
          const requiredDocs = regDocs.find((doc) => doc.mandatory === true);
          setDisplayEmpty(
            requiredDocs === undefined && !isArrayNotEmpty(res?.data?.aaData)
          );
          break;
        case "checkRegNo":
          let result = res?.data;

          setUniqueTaxCheck({
            ...uniqueTaxCheck,
            loading: false,
            errMsg:
              result === "available" ? null : "Tax Registration No. exists!",
          });
          //   setUniqueTaxCheckLoading(res?.data === "a");
          break;
        default:
          break;
      }
    }

    if (error) {
      //goes back to the screen
      setLoading(false);
      setHideSave(false);
    }

    if (validation) {
      // set errors and remark to default
      setPopUpFieldError({});
      setRemarksDetails(remarksDefaultValue);
      setValidationErrors({ ...validation });
      setLoading(false);
      setHideSave(false);

      let keyList = Object.keys(validation);
      if (keyList.length > 0) {
        for (let key of keyList) {
          if (key.includes("invalidTabs.cmpnyDtls")) {
            setTabIndex(0);
            break;
          } else if (key.includes("invalidTabs.cnfgrtn")) {
            setTabIndex(1);
            break;
          }
        }
      }
    }

    // eslint-disable-next-line
  }, [urlId, isLoading, isFormSubmission, res, validation, error]);

  const modifySelectedOpmByAccnType = (accnType, financeOption, isFull) => {
    let result = financeOption;

    if (
      isFull &&
      [FINANCING_OPTIONS.OC.code, FINANCING_OPTIONS.OT.code].includes(
        financeOption
      )
    ) {
      setShowFinancer(true);
      result = FINANCING_OPTIONS.OPM.code;
    } else {
      if (financeOption === FINANCING_OPTIONS.OPM.code) {
        setShowFinancer(true);
        if (
          accnType === AccountTypes.ACC_TYPE_CO.code ||
          accnType === AccountTypes.ACC_TYPE_FF.code
        ) {
          result = isFull
            ? FINANCING_OPTIONS.OPM.code
            : FINANCING_OPTIONS.OC.code;
        } else if (accnType === AccountTypes.ACC_TYPE_TO.code) {
          result = isFull
            ? FINANCING_OPTIONS.OPM.code
            : FINANCING_OPTIONS.OT.code;
        }
      } else {
        setShowFinancer(false);
      }
    }

    return result ?? FINANCING_OPTIONS.BC.code;
  };

  const handleTabChange = (e, value) => {
    //setSubmitSuccess(false);
    setTabIndex(value);
    if (value === 2 && viewType !== "new") {
      sendRequest(
        "/api/v1/clickargo/manageaccn/suppDocs",
        "loadSuppDocs",
        "get"
      );
    }
  };

  const handleRemarksInputChange = (e) => {
    const elName = e.target.name;
    setRemarksDetails({
      ...remarksDetails,
      ...deepUpdateState(remarksDetails, elName, e?.target?.value),
    });
  };

  const handleInputChange2 = (e, nameA, valueA) => {
    const elName = e.target.name;
    if (nameA && valueA) {
      //this is for autocompleteselect handler
      setInputData({
        ...inputData,
        ...deepUpdateState(inputData, nameA, valueA?.value),
      });
    } else {
      //this is default handler
      const { name, value } = e.target;

      if (
        elName === "accnDetails.accnContact.contactTel" ||
        elName === "accnDetails.accnContact.contactFax"
      ) {
        const rgx = /^\+?[0-9]*$/;
        if (e.target.value === "" || rgx.test(e.target.value)) {
          setInputData({
            ...inputData,
            ...deepUpdateState(inputData, elName, value),
          });
        }
      } else if (elName === "financeOptions") {
        //check if account type is already set
        if (inputData?.accnDetails?.TMstAccnType.atypId) {
          setAccnType(inputData?.accnDetails?.TMstAccnType.atypId);
        }
        setInputData({
          ...inputData,
          ...deepUpdateState(inputData, elName, value),
        });

        if (value === FINANCING_OPTIONS.OPM.code) setShowFinancer(true);
        else setShowFinancer(false);
      } else {
        //set the account type upon selection
        if (name === "accnDetails.TMstAccnType.atypId") setAccnType(value);

        setInputData({
          ...inputData,
          ...deepUpdateState(inputData, name, value),
        });
      }
    }
  };

  const handleOnBlurCheck = (e) => {
    setUniqueTaxCheck({ ...uniqueTaxCheck, loading: true });
    setTimeout(() => {
      //setUniqueTaxCheck((prevState) => ({ ...prevState, loading: true }));

      sendRequest(
        `/api/v1/clickargo/manageaccn/check/registrationNo`,
        "checkRegNo",
        "post",
        { ...inputData?.accnDetails }
      );
    }, 2000);
  };

  const handleDateChange = (name, e) => {
    setInputData({ ...inputData, [name]: e });
  };

  const handleExitOnClick = () => {
    isFromProfile ? history.goBack() : history.push("/manageAccounts/all/list");
  };

  const handleActionOnClick = (actionUp) => {
    setInputData({ ...inputData, action: actionUp });
    // CT-172 - This is removed so that the icon will not redirect to suspend/terminate route
    // if (actionUp === "SUSPEND" && viewType === 'edit') {
    //     history.push(`/manageAccount/suspend/${encodeString(inputData?.accnId)}`)
    //     setControls(controls.filter(c => c.ctrlAction === actionUp));
    // } else if (actionUp === "TERMINATE" && viewType === 'edit') {
    //     history.push(`/manageAccount/terminate/${encodeString(inputData?.accnId)}`)
    //     setControls(controls.filter(c => c.ctrlAction === actionUp));
    // } else {
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: actionUp,
      open: true,
    });
    // }
  };

  const eventHandler = (action) => {
    if (action.toLowerCase() === "exit") {
      handleExitOnClick();
    } else if (action.toLowerCase() === "save") {
      handleSaveOnClick();
    } else {
      handleActionOnClick(action);
    }
  };

  const handleSaveOnClick = () => {
    setLoading(true);
    setSnackBarOptions(defaultSnackbarValue);
    setHideSave(true);
    setValidationErrors({});
    switch (viewType) {
      case "new":
        sendRequest(`/api/v1/clickargo/manageaccn/create`, "create", "POST", {
          accnDetails: { ...inputData?.accnDetails },
          accnProcessType: AccnProcessTypes.ACCN_REGISTRATION.code,
          sageAccpacId: inputData?.sageAccpacId,
          financeOptions: modifySelectedOpmByAccnType(
            accnType,
            inputData?.financeOptions
          ),
          financer: inputData?.financer,
          mobileEnabled: inputData?.mobileEnabled,
          ckAccn: inputData?.ckAccn
        });
        break;
      case "edit":
        sendRequest(
          `/api/v1/clickargo/manageaccn/${accountId}`,
          "update",
          "PUT",
          {
            bgImageWl: inputData?.bgImageWl
              ? { ...inputData?.bgImageWl }
              : null,
            companyLogo: inputData?.companyLogo
              ? { ...inputData?.companyLogo }
              : null,
            accnDetails: { ...inputData?.accnDetails },
            sageAccpacId: inputData?.sageAccpacId,
            financeOptions: modifySelectedOpmByAccnType(
              accnType,
              inputData?.financeOptions
            ),
            financer: inputData?.financer,
            mobileEnabled: inputData?.mobileEnabled,
            ckAccn: inputData?.ckAccn
          }
        );
        break;
      default:
        break;
    }
  };

  const handlePopupRemarksSubmit = (action) => {
    // also check if empty string
    if (
      getValue(remarksDetails?.arRemark) === "" ||
      isStringEmpty(getValue(remarksDetails?.arRemark))
    ) {
      //check if the remarks is actually entered whether be it approved or rejected
      setPopUpFieldError({ arRemark: t("common:validationMsgs.required") });
      return;
    }
    //close the popupremarks
    setOpenRemarksPopup(false);
    sendRequest(
      `/api/v1/clickargo/manageaccn/${id}`,
      openSubmitConfirm.action.toLowerCase(),
      "PUT",
      {
        accnDetails: { ...inputData?.accnDetails },
        ckAccn: { ...inputData?.ckAccn },
        action: openSubmitConfirm.action,
        sageAccpacId: inputData?.sageAccpacId,
        financeOptions: modifySelectedOpmByAccnType(
          accnType,
          inputData?.financeOptions
        ),
        financer: inputData?.financer,
        mobileEnabled: inputData?.mobileEnabled,
        accnProcessType: accnProcessType,
        remarks: remarksDetails?.arRemark,
      }
    );
  };

  const handleConfirmAction = (e) => {
    setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
    //do not present remarks as Delete can only be done by the non-submitted record
    if (openSubmitConfirm?.action !== "DELETE") {
      let remarksType = "";
      switch (accnProcessType) {
        case AccnProcessTypes.ACCN_REGISTRATION.code:
          remarksType =
            openSubmitConfirm?.action === "APPROVE"
              ? "REG_APPROVE"
              : openSubmitConfirm?.action == "REJECT"
              ? "REG_REJECT"
              : "REG_REQ";
          break;
        case AccnProcessTypes.ACCN_SUSPENSION.code:
          remarksType =
            openSubmitConfirm?.action === "APPROVE"
              ? "SUS_APPROVE"
              : openSubmitConfirm?.action == "REJECT"
              ? "SUS_REJECT"
              : "SUS_REQ";
          break;
        case AccnProcessTypes.ACCN_TERMINATION.code:
          remarksType =
            openSubmitConfirm?.action === "APPROVE"
              ? "TERM_APPROVE"
              : openSubmitConfirm?.action == "REJECT"
              ? "TERM_REJECT"
              : "TERM_REQ";
          break;
        case AccnProcessTypes.ACCN_RESUMPTION.code:
          remarksType =
            openSubmitConfirm?.action === "APPROVE"
              ? "RESUMPT_APPROVE"
              : openSubmitConfirm?.action == "REJECT"
              ? "RESUMPT_REJECT"
              : "RESUMPT_REQ";
        default:
          break;
      }
      // CT-172 [START]
      if (openSubmitConfirm?.action === "SUSPEND") {
        setAccnProcessType(AccnProcessTypes.ACCN_SUSPENSION.code);
        remarksType = "SUS_REQ";
      }
      if (openSubmitConfirm?.action === "TERMINATE") {
        setAccnProcessType(AccnProcessTypes.ACCN_TERMINATION.code);
        remarksType = "TERM_REQ";
      }
      if (openSubmitConfirm?.action === "RESUMPTION") {
        setAccnProcessType(AccnProcessTypes.ACCN_RESUMPTION.code);
        remarksType = "RESUMPT_REQ";
      }
      // CT-172 [END]
      setRemarksDetails({
        ...remarksDetails,
        ...deepUpdateState(
          remarksDetails,
          "tckMstRemarkType.rtId",
          remarksType
        ),
      });
      setOpenRemarksPopup(true);
    } else {
      sendRequest(
        `/api/v1/clickargo/manageaccn/${id}`,
        openSubmitConfirm.action.toLowerCase(),
        "PUT",
        {
          accnDetails: { ...inputData?.accnDetails },
          ckAccn: { ...inputData?.ckAccn },
          action: openSubmitConfirm.action,
          sageAccpacId: inputData?.sageAccpacId,
          financeOptions: inputData?.financeOptions,
          mobileEnabled: inputData?.mobileEnabled,
          accnProcessType: accnProcessType,
          remarks: remarksDetails?.arRemark,
        }
      );
    }
  };

  const handleRegisterBankOnClick = (url) => {
    if (url) window.open(url, "_blank", "noreferrer");
    else
      setWarningProps({
        ...warningProps,
        open: true,
        msg: "Financer has no URL set.",
      });
  };

  const displaySaveIconOnly = (prameterControls) => {
    if (prameterControls && isFromProfile) {
      prameterControls = prameterControls.filter(
        (c) => c.ctrlAction === "SAVE"
      );
    }

    return prameterControls;
  };

  const reloadTable = () => {
    sendRequest("/api/v1/clickargo/manageaccn/suppDocs", "loadSuppDocs", "get");
  };

  let bcLabel = "Account Profile";
  let formButtons;

  if (!loading) {
    formButtons = (
      <C1FormButtons
        options={getFormActionButton(initialButtons, controls, eventHandler)}
      >
        {registerFinanceBtn?.show && (
          <C1LabeledIconButton
            tooltip={"Register to Bank Financer"}
            label={"Register to Bank"}
            action={() => handleRegisterBankOnClick(registerFinanceBtn?.url)}
          >
            <AccountBalanceOutlinedIcon color="primary" />
          </C1LabeledIconButton>
        )}
      </C1FormButtons>
    );
  }
  const moduleName = "Job";
  const commonId = inputData?.accnDetails?.accnId;
  const isUserTo = user?.coreAccn?.TMstAccnType?.atypId === "ACC_TYPE_TO";
  return loading && inputData ? (
    <MatxLoading />
  ) : (
    <React.Fragment>
      <C1FormDetailsPanel
        breadcrumbs={[{ name: bcLabel }]}
        title={bcLabel}
        titleStatus={inputData?.accnDetails?.accnStatus}
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
                    {tabList &&
                        tabList.map((item, ind) => {
                            return item?.show && (
                                <TabsWrapper
                                    className="capitalize"
                                    value={ind}
                                    disabled={item.disabled}
                                    label={
                                        <TabLabel
                                            viewType={viewType}
                                            invalidTabs={inputData.invalidTabs}
                                            errors={validationErrors}
                                            tab={item}
                                        />
                                    }
                                    key={ind}
                                    icon={item.icon}
                                    {...tabScroll(ind)}
                                />
                            )
                        })}
                  </Tabs>
                  <Divider className="mb-6" />

                  {tabIndex === 0 && (
                      <CompanyDetails
                          inputData={inputData}
                          handleInputChange={handleInputChange2}
                          handleDateChange={handleDateChange}
                          viewType={viewType}
                          errors={!validationErrors ? props.errors : validationErrors}
                          locale={t}
                          handleUniqueness={handleOnBlurCheck}
                          uniqueTaxCheck={uniqueTaxCheck}
                      />
                  )}

                  {/* Conditionally render NotificationFormDetails based on user type */}
                  {tabIndex === 1 && isUserTo && (
                      <NotificationFormDetails
                          commonId={commonId}
                          moduleName={moduleName}
                          inputData={inputData}
                          setInputData={setInputData}
                          handleInputChange={handleInputChange2}
                          handleDateChange={handleDateChange}
                          viewType={viewType}
                          errors={!validationErrors ? props.errors : validationErrors}
                          locale={t}
                          enableFinancing={!isFromProfile}
                          showFinancer={showFinancer}
                      />
                  )}

                  {/* Adjust the tabIndex for the remaining tabs */}
                  {tabIndex === (isUserTo ? 2 : 1) && (
                      <Configurations
                          inputData={inputData}
                          setInputData={setInputData}
                          handleInputChange={handleInputChange2}
                          handleDateChange={handleDateChange}
                          viewType={viewType}
                          errors={!validationErrors ? props.errors : validationErrors}
                          locale={t}
                          enableFinancing={!isFromProfile}
                          showFinancer={showFinancer}
                      />
                  )}

                  {tabIndex === (isUserTo ? 3 : 2) && (
                      <AccountSuppDocs
                          inputData={inputData}
                          docs={regDocs}
                          reloadTable={reloadTable}
                          viewType={viewType}
                          locale={t}
                          displayEmpty={displayEmpty}
                      />
                  )}

                  {tabIndex === (isUserTo ? 4 : 3) && (
                      <C1TabInfoContainer>
                        <AccountRemarks inputData={inputData} locale={t} />
                      </C1TabInfoContainer>
                  )}

                  {tabIndex === (isUserTo ? 5 : 4) && (
                      <C1TabInfoContainer>
                        <C1AuditTab
                            filterId={
                              id === "my"
                                  ? accountId
                                  : viewType === "new"
                                      ? id
                                      : decodedId
                            }
                        />
                      </C1TabInfoContainer>
                  )}
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
        text={t("common:msg.confirmation", {
          action: Actions[openSubmitConfirm?.action]?.text,
        })}
        title={t("common:popup.confirmation")}
        onYesClick={(e) => handleConfirmAction(e)}
      />

      <C1DialogPrompt
        confirmationObj={{
          openConfirmPopUp: terminableError?.open,
          onConfirmationDialogClose: () =>
            setTerminableError({ ...terminableError, open: false }),
          text: terminableError?.msg,
          title: t("common:msg.error"),
          onYesClick: () =>
            setTerminableError({ ...terminableError, open: false }),
          yesBtnText: "Ok",
        }}
      />

      <C1PopUp
        title={t("common:remarks.title")}
        openPopUp={openRemarksPopup}
        setOpenPopUp={() => {
          setOpenRemarksPopup(false);
          //reset the remarks details and errors
          setRemarksDetails(remarksDefaultValue);
          setPopUpFieldError({});
        }}
        actionsEl={
          <C1IconButton tooltip={t("buttons:submit")} childPosition="right">
            <NearMeOutlinedIcon
              color="primary"
              fontSize="large"
              onClick={(e) => handlePopupRemarksSubmit(e)}
            ></NearMeOutlinedIcon>
          </C1IconButton>
        }
      >
        <AddRemarkPopup
          inputData={remarksDetails}
          handleInputChange={handleRemarksInputChange}
          handleDateChange={handleDateChange}
          locale={t}
          errors={popUpFieldError}
        />
      </C1PopUp>

      <C1Warning
        warningMessage={warningProps}
        handleWarningAction={() => {
          setWarningProps({ ...warningProps, open: false, msg: null });
        }}
      />
    </React.Fragment>
  );
};

export default withErrorHandler(AccountProfileFormDetails);
