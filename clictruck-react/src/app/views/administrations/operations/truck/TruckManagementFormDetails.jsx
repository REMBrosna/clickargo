import { Button, Dialog, Divider, Grid, Paper, Tabs } from "@material-ui/core";
import {AlarmAddOutlined, LocalShippingOutlined, Notifications} from "@material-ui/icons";
import AccessTimeOutlinedIcon from "@material-ui/icons/AccessTimeOutlined";
import moment from "moment";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";

import C1AuditTab from "app/c1component/C1AuditTab";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";
import useHttp from "app/c1hooks/http";
import { CK_CT_MST_CHASSIS } from "app/c1utils/const";
import { RecordStatus } from "app/c1utils/const";
import { getFormActionButton } from "app/c1utils/formActionUtils";
import { deepUpdateState } from "app/c1utils/stateUtils";
import { tabScroll } from "app/c1utils/styles";
import {
  isEditable,
  previewPDF,
  Uint8ArrayToString,
} from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import TabLabel from "app/portedicomponent/TabLabel";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";
import { ConfirmationDialog, MatxLoading } from "matx";
import TruckDetails from "./tabs/TruckDetails";
import NotificationFormDetails from "../../../configurations/notification/NotificationFormDetails";

const VextApiUrl = "/api/v1/clickargo/clictruck/administrator/vehExt";

const TruckManagementFormDetails = () => {
  const { t } = useTranslation(["administration", "common"]);

  const { viewType, truckId } = useParams();
  const history = useHistory();
  const { user } = useAuth();

  const tabList = [
    {
      text: t("administration:truckManagement.tabs.truckDetails"),
      icon: <LocalShippingOutlined />,
    },
    ...(user?.coreAccn?.TMstAccnType?.atypId === "ACC_TYPE_TO"
        ? [
          {
            text: t("Alerts"),
            icon: <Notifications />,
          },
        ]
        : []),
    {
      text: t("administration:truckManagement.tabs.audits"),
      icon: <AccessTimeOutlinedIcon />,
    },
  ];

  const [tabIndex, setTabIndex] = useState(0);
  const [isDisabled, setDisabled] = useState(isEditable(viewType));

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
    redirectPath: "/administrations/truck-management/list",
  };
  const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);
  const [loading, setLoading] = useState(true);
  const [inputData, setInputData] = useState({});
  const [vextInputData, setVextInputData] = useState({});
  const [controls, setControls] = useState([]);
  const [openSubmitConfirm, setOpenSubmitConfirm] = useState({
    action: null,
    open: false,
  });
  const [validationErrors, setValidationErrors] = useState({});
  const [openWarning, setOpenWarning] = useState(false);
  const [warningMessage, setWarningMessage] = useState("");
  const [fileUploaded, setFileUploaded] = useState(false);
  const [status, setStatus] = useState("");

  const [chassisNoList, setChassisNoList] = useState([]);

  let truckOther = [
    {
      chsId: "OTHERS",
      chsNo: "OTHERS",
    },
  ];

  const initialButtons = {
    back: { show: true, eventHandler: () => handleExitOnClick() },
    save: {
      show: viewType === "edit",
      eventHandler: () => handleSubmitOnClick(),
    },
    // delete: {
    //   show: viewType !== "view",
    //   eventHandler: () => handleDeleteOnClick() },
  };
  /** ------------------- Update states ----------------- */
  useEffect(() => {
    if (viewType === "new") {
      sendRequest(
        "/api/v1/clickargo/clictruck/administrator/vehicle/-",
        "getData",
        "get",
        null
      );
    } else if (viewType === "view" || viewType === "edit") {
      setSnackBarOptions(defaultSnackbarValue);
      setLoading(true);
      setFileUploaded(true);
      sendRequest(
        "/api/v1/clickargo/clictruck/administrator/vehicle/" + truckId,
        "getData",
        "get",
        null
      );
    }
  }, [truckId, viewType]);

  useEffect(() => {
    if (error) {
      setLoading(false);
    }
    if (!isLoading && !error && res && !validation) {
      setLoading(isLoading);
      switch (urlId) {
        case "createData": {
          setInputData({ ...res?.data });
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("common:msg.saveSuccess"),
          });
          break;
        }
        case "getData": {
          setInputData({
            ...res.data,
            vhChassisNo: res?.data?.vhChassisNo?.includes("OTHERS")
              ? "OTHERS"
              : res?.data?.vhChassisNo,
          });
          // Fetch chassis numbers based on chassis type id and accn id
          if (res?.data?.tckCtMstChassisType?.chtyId)
            fetchChassisNumList(
              res?.data?.tckCtMstChassisType?.chtyId,
              res?.data?.tcoreAccn?.accnId
            );
          break;
        }
        case "updateData": {
          ///update truck data
          setInputData({ ...res.data });
          setStatus(res.data?.vhStatus);
          setLoading(false);

          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("common:msg.updateSuccess"),
            redirectPath: "",
          });
          break;
        }
        case "setMaintenanceDate": {
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("common:msg.updateSuccess") + " Maintenance",
            redirectPath: "",
          });
          break;
        }
        case "setExpirationDate": {
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("common:msg.updateSuccess") + " Expiration",
            redirectPath: "",
          });
          break;
        }
        case "setStatus": {
          setInputData({ ...inputData, vhStatus: status });
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("common:msg.updateSuccess"),
          });
          break;
        }
        case "download": {
          viewFile(res?.data?.attName, res?.data?.attData);
          break;
        }
        case "submit": {
          let msg = t("cargoowners:msg.submitSuccess");
          if (openSubmitConfirm && openSubmitConfirm.action === "CANCEL") {
            msg = t("cargoowners:msg.cancelSuccess");
          } else if (
            openSubmitConfirm &&
            openSubmitConfirm.action === "DELETE"
          ) {
            msg = t("cargoowners:msg.deleteSuccess");
          }
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: msg,
            redirectPath: "/applications/services/co/list",
          });
          break;
        }
        case "deleteData": {
          setLoading(false);
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("common:msg.deleteSuccess"),
          });
          break;
        }
        case "fetchChassisNoList":
          setChassisNoList([...res?.data?.aaData, ...truckOther]);
          break;
        default:
          break;
      }
    }
    //If validation has value then set to the errors
    if (validation) {
      setValidationErrors({ ...validation });
      setLoading(false);
      setSnackBarOptions(defaultSnackbarValue);
      //if validation contains SUBMIT API CALL FAILURE, prompt message
      if (validation["Submit.API.call"]) {
        // alert(validation['Submit.API.call'])
        setOpenWarning(true);
        setWarningMessage(validation["Submit.API.call"]);
      }

      switch (urlId) {
        case "setMaintenanceDate":
          setValidationErrors((v) => ({ ...v, maintenance: validation }));
          setSnackBarOptions((s) => ({
            ...s,
            success: false,
            error: true,
            errorMsg: "Failed to save Maintenance monitoring data",
            redirectPath: "",
          }));
          break;
        case "setExpirationDate":
          setValidationErrors((v) => ({ ...v, expiry: validation }));
          setSnackBarOptions((s) => ({
            ...s,
            success: false,
            error: true,
            errorMsg: "Failed to save Expiration monitoring data",
            redirectPath: "",
          }));
          break;
        default:
          break;
      }
    }

    // eslint-disable-next-line
  }, [urlId, isLoading, isFormSubmission, res, validation, error]);

  /** ---------------- Event handlers ----------------- */
  const handleTabChange = (e, value) => {
    setTabIndex(value);
  };

  // Fetch chassis numbers based on accnId and chassis type id (size).
  const fetchChassisNumList = (chtyId, accnId) => {
    sendRequest(
      CK_CT_MST_CHASSIS +
        `&mDataProp_2=TCkCtMstChassisType.chtyId&sSearch_2=${chtyId}&mDataProp_3=TCoreAccn.accnId&sSearch_3=${accnId}`,
      "fetchChassisNoList",
      "GET"
    );
  };

  const handleInputChange = (e) => {
    const elName = e.target.name;
    setInputData({
      ...inputData,
      ...deepUpdateState(inputData, elName, e.target.value),
    });
    if (elName === "tckCtMstChassisType.chtyId") {
      if (inputData?.tckCtMstChassisType?.chtyId)
        // Fetch chassis numbers based on chassis type id and accn id
        fetchChassisNumList(
          inputData?.tckCtMstChassisType?.chtyId,
          inputData?.tcoreAccn?.accnId
        );
    }
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
    }
  };

  const handleExitOnClick = () => {
    history.push("/administrations/truck-management/list");
  };

  const handleSaveOnClick = () => {
    setValidationErrors({});
    setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
    let reqBody = {
      ...inputData,
      maintenance:
        vextInputData?.maintenance?.vextNotify === "Y"
          ? vextInputData?.maintenance
          : null,
      expiry:
        vextInputData?.expiry?.vextNotify === "Y"
          ? vextInputData?.expiry
          : null,
    };
    switch (viewType) {
      case "new":
        sendRequest(
          "/api/v1/clickargo/clictruck/administrator/vehicle",
          "createData",
          "post",
          reqBody
        );
        break;
      case "edit":
        sendRequest(
          "/api/v1/clickargo/clictruck/administrator/vehicle/" + truckId,
          "updateData",
          "put",
          reqBody
        );
        setLoading(false);
        break;
      default:
        break;
    }
  };

  const handleSubmitDelete = () => {
    setLoading(true);
    setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
    sendRequest(
      "/api/v1/clickargo/clictruck/administrator/vehicle/" + truckId,
      "deleteData",
      "delete",
      { ...inputData }
    );
  };

  const handleSubmitStatus = () => {
    setLoading(true);
    const action =
      status === RecordStatus.INACTIVE.code ? "deactive" : "active";
    setOpenSubmitConfirm({ ...openSubmitConfirm, action: null, open: false });
    sendRequest(
      "/api/v1/clickargo/clictruck/administrator/vehicle/" +
        truckId +
        "/" +
        action,
      "setStatus",
      "put",
      null
    );
  };

  const handleWarningAction = (e) => {
    setOpenWarning(false);
    setWarningMessage("");
  };

  const handleSubmitOnClick = () => {
    if (!inputData?.tckCtMstChassisType?.chtyId) {
      setInputData({ ...inputData, tckCtMstChassisType: null });
    }
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: "SAVE",
      open: true,
      msg: t("common:msg.saveConfirm"),
    });
  };

  const handleSetActiveOnClick = () => {
    setStatus(RecordStatus.ACTIVE.code);
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: "STATUS",
      open: true,
      msg: t("common:msg.activeConfirm"),
    });
  };

  const handleSetInActiveOnClick = () => {
    setStatus(RecordStatus.INACTIVE.code);
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: "STATUS",
      open: true,
      msg: t("common:msg.inActiveConfirm"),
    });
  };

  const handleCancelOnClick = () => {
    setInputData({ ...inputData, action: "CANCEL" });
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: "CANCEL",
      open: true,
    });
  };

  const handleDeleteOnClick = () => {
    setInputData({ ...inputData, action: "DELETE" });
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: "DELETE",
      open: true,
      msg: t("common:msg.deleteConfirm"),
    });
  };

  const eventHandler = (action) => {
    if (action.toLowerCase() === "save") {
      handleSaveOnClick();
    } else if (action.toLowerCase() === "delete") {
      handleSubmitDelete();
    } else if (action.toLowerCase() === "status") {
      handleSubmitStatus();
    } else {
      setOpenSubmitConfirm({ action: action, open: true });
    }
  };

  const onFileChangeHandler = (e) => {
    e.preventDefault();
    let file = e.target.files[0];

    if (!file) {
      // didn't select file
      return;
    }

    let errors = handleSignatureValidate(file.type);
    if (Object.keys(errors).length === 0) {
      const fileReader = new FileReader();
      fileReader.readAsArrayBuffer(e.target.files[0]);
      fileReader.onload = (e) => {
        const uint8Array = new Uint8Array(e.target.result);
        if (uint8Array.byteLength === 0) {
          return;
        }
        let imgStr = Uint8ArrayToString(uint8Array);
        let base64Sign = btoa(imgStr);
        setInputData({
          ...inputData,
          ...{ base64File: base64Sign, vhPhotoName: file.name },
        });
        setFileUploaded(true);
      };
    } else {
      setValidationErrors(errors);
    }
  };

  const handleSignatureValidate = (uploadFileType) => {
    const errors = {};
    const images = [
      "image/png",
      "image/jpeg",
      "application/pdf",
      "application/msword",
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    ];
    if (uploadFileType && !images.includes(uploadFileType)) {
      errors.fhotoFileButton = t("common:common.msg.allowedTruckPhoto");
    }
    if (uploadFileType === "") {
      errors.fhotoFileButton = t("common:common.msg.noFileUploded");
    }
    return errors;
  };

  const handleViewFile = (fileName, data) => {
    viewFile(fileName, data);
  };

  const viewFile = (fileName, data) => {
    previewPDF(fileName, data);
  };

  let bcLabel =
    viewType === "edit"
      ? t("administration:truckManagement.form.editTruckList")
      : t("administration:truckManagement.form.viewTruckList");
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
            eventHandler: handleSubmitOnClick,
          },
        }}
      />
    );

    if (viewType) {
      switch (viewType) {
        case "edit":
          bcLabel = t("administration:truckManagement.form.editTruckList");
          formButtons = (
            <C1FormButtons
              options={{
                ...getFormActionButton(initialButtons, controls, eventHandler),
                ...{
                  activate: {
                    show: inputData.vhStatus === RecordStatus.INACTIVE.code,
                    eventHandler: () => handleSetActiveOnClick(),
                  },
                  deactivate: {
                    show: inputData.vhStatus === RecordStatus.ACTIVE.code,
                    eventHandler: () => handleSetInActiveOnClick(),
                  },
                },
              }}
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
            ></C1FormButtons>
          );
          break;
        case "new":
          bcLabel = t("administration:truckManagement.breadCrumbs.createTruck");
          break;
        default:
          break;
      }
    }
  }
  const moduleName = "Truck";
  const commonId = inputData?.vhId;
  const isUserTo = user?.coreAccn?.TMstAccnType?.atypId === "ACC_TYPE_TO";
  return loading ? (
    <MatxLoading />
  ) : (
    <React.Fragment>
      <C1FormDetailsPanel
        breadcrumbs={[
          {
            name: t("administration:truckManagement.breadCrumbs.truckList"),
            path: "/administrations/truck-management/list",
          },
          {
            name:
              viewType === "new"
                ? t("administration:truckManagement.breadCrumbs.createTruck")
                : viewType === "view"
                ? t("administration:truckManagement.breadCrumbs.viewTruckList")
                : viewType === "edit"
                ? t("administration:truckManagement.breadCrumbs.editTruckList")
                : t("administration:truckManagement.breadCrumbs.editTruckList"),
          },
        ]}
        titleStatus={inputData.vhStatus}
        title={bcLabel}
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
                      return (
                        <TabsWrapper
                          // CPEDI-193
                          style={
                            ind === 4 ? { backgroundColor: "#e4effa" } : {}
                          }
                          className="capitalize"
                          value={ind}
                          disabled={item.disabled}
                          label={
                            <TabLabel
                              viewType={viewType}
                              invalidTabs={inputData.invalidTabs}
                              tab={item}
                            />
                          }
                          key={ind}
                          icon={item.icon}
                          {...tabScroll(ind)}
                        />
                      );
                    })}
                </Tabs>
                <Divider className="mb-6" />
                {tabIndex === 0 && (
                  <C1TabInfoContainer
                    guideId="clictruck.administration.truck.details"
                    title="empty"
                    guideAlign="right"
                    open={false}
                  >
                    <TruckDetails
                      viewType={viewType}
                      handleInputChange={handleInputChange}
                      handleDateChange={handleDateChange}
                      handleInputFileChange={onFileChangeHandler}
                      handleViewFile={handleViewFile}
                      isDisabled={isDisabled}
                      inputData={inputData}
                      errors={validationErrors}
                      fileUploaded={fileUploaded}
                      chassisNoList={chassisNoList}
                      setVextInputData={setVextInputData}
                    />
                  </C1TabInfoContainer>
                )}
                {tabIndex === 1 && isUserTo && (
                    <C1TabInfoContainer
                        guideId="clictruck.administration.truck.details"
                        title="empty"
                        guideAlign="right"
                        open={false}
                    >
                      <NotificationFormDetails
                        commonId={commonId}
                        moduleName={moduleName}
                        handleInputChange={handleInputChange}
                        handleViewFile={handleViewFile}
                        handleDateChange={handleDateChange}
                        isDisabled={isDisabled}
                        errors={validationErrors}
                        chassisNoList={chassisNoList}
                        viewType={viewType}

                      />
                    </C1TabInfoContainer>
                )}
                {tabIndex === (isUserTo ? 2 : 1) && (
                  <C1TabInfoContainer
                    guideId="clictruck.administration.truck.audit"
                    title="empty"
                    guideAlign="right"
                    open={false}
                  >
                    <C1AuditTab
                      filterId={inputData?.vhId ? inputData?.vhId : "empty"}
                    ></C1AuditTab>
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
        text={openSubmitConfirm?.msg}
        title={t("common:popup.confirmation")}
        onYesClick={(e) => eventHandler(openSubmitConfirm?.action)}
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
              {t("cargoowners:popup.ok")}
            </Button>
          </div>
        </div>
      </Dialog>
    </React.Fragment>
  );
};

export default withErrorHandler(TruckManagementFormDetails);
