import { Button, Dialog, Divider, Grid, Paper, Tabs } from "@material-ui/core";
import AccessTimeOutlinedIcon from "@material-ui/icons/AccessTimeOutlined";
import WorkOutlineOutlinedIcon from "@material-ui/icons/WorkOutlineOutlined";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";

import C1AuditTab from "app/c1component/C1AuditTab";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";
import useHttp from "app/c1hooks/http";
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
import TabLabel from "app/portedicomponent/TabLabel";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";
import { ConfirmationDialog, MatxLoading } from "matx";

import DriverDetails from "./tabs/DriverDetails";
import {Notifications} from "@material-ui/icons";
import NotificationFormDetails from "../../../configurations/notification/NotificationFormDetails";
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import useAuth from "../../../../hooks/useAuth";

const DriverManagementFormDetails = () => {
  const { t } = useTranslation(["administration", "common"]);

  const { viewType, driverId } = useParams();
  const history = useHistory();
  const { user } = useAuth();
  const tabList = [
    {
      text: t("administration:driverManagement.tabs.details"),
      icon: <WorkOutlineOutlinedIcon />,
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
      text: t("administration:driverManagement.tabs.audits"),
      icon: <AccessTimeOutlinedIcon />,
    },
  ];

  const [tabIndex, setTabIndex] = useState(0);
  // eslint-disable-next-line
  const [isDisabled, setDisabled] = useState(isEditable(viewType));

  /** ------------------ States ---------------------------------*/

  const { isLoading, res, validation, error, urlId, sendRequest } = useHttp();

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
  // eslint-disable-next-line
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

  const [editable, setEditable] = useState(false);

  const initialButtons = {
    back: { show: true, eventHandler: () => handleExitOnClick() },
    save: {
      show: viewType === "edit",
      eventHandler: () => handleSubmitOnClick(),
    },
    delete: { show: false, eventHandler: () => handleDeleteOnClick() },
  };
  /** ------------------- Update states ----------------- */
  useEffect(() => {
    if (viewType === "new") {
      sendRequest(
        "/api/v1/clickargo/clictruck/administrator/driver/-",
        "getData",
        "get",
        null
      );
    } else if (viewType === "view" || viewType === "edit") {
      sendRequest(
        "/api/v1/clickargo/clictruck/administrator/driver/" + driverId,
        "getData",
        "get",
        null
      );
    }
    // eslint-disable-next-line
  }, [driverId, viewType]);

  useEffect(() => {
    if (!isLoading && !error && res && !validation) {
      switch (urlId) {
        case "createData": {
          setLoading(false);
          setInputData({ ...res?.data });
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("common:msg.saveSuccess"),
          });
          break;
        }
        case "getData": {
          if (viewType === "new") {
            setInputData({ ...res.data, drvMobileId: "", drvMobilePassword: "" });
          }else{
            setInputData({ ...res.data });
          }
          
          setStatus(res.data?.drvStatus);
          setLoading(false);
          break;
        }
        case "updateData": {
          setInputData({ ...res.data });
          setStatus(res.data?.drvStatus);
          setLoading(false);
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("common:msg.updateSuccess"),
          });
          break;
        }
        case "setStatus": {
          setLoading(false);
          setInputData({ ...inputData, drvStatus: status });
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("common:msg.updateSuccess"),
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
        } case "resetPassword": {
          setLoading(false);
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: res?.data,
          });
          break;
        }
        default:
          break;
      }
    }

    if (error) {
      setLoading(false);
    }

    //If validation has value then set to the errors
    if (validation) {
      setValidationErrors({ ...validation });
      setLoading(false);
      setSnackBarOptions(defaultSnackbarValue);

      //if validation contains SUBMIT API CALL FAILURE, prompt message
      // console.log(validation['Submit.API.call'])
      if (validation["Submit.API.call"]) {
        // alert(validation['Submit.API.call'])
        setOpenWarning(true);
        setWarningMessage(validation["Submit.API.call"]);
      }
    }

    if (inputData) {
      console.log("input Data State", inputData);
    }

    // eslint-disable-next-line
  }, [urlId, res, isLoading, error, validation]);

  /** ---------------- Event handlers ----------------- */
  const handleTabChange = (e, value) => {
    setTabIndex(value);
  };

  const handleInputChange = (e) => {
    const elName = e.target.name;
    if (elName === "drvPhone") {
      const re = /^\+?[0-9]*$/;
      if (e.target.value === "" || re.test(e.target.value)) {
        setInputData({
          ...inputData,
          ...deepUpdateState(inputData, elName, e.target.value),
        });
      }
    }else if(elName === "drvMobileId"){
      setInputData({
        ...inputData,
        ...deepUpdateState(inputData, elName, e.target.value.toUpperCase()),
      });
    } else {
      setInputData({
        ...inputData,
        ...deepUpdateState(inputData, elName, e.target.value),
      });
    }
  };

  const handleEditPwChange = (e) => {
    setEditable(!editable);
    setInputData({
      ...inputData,
      drvMobilePassword: null,
      drvEditPassword: true,
    });
  };

  const handleDateChange = (name, e) => {
    setInputData({ ...inputData, ...deepUpdateState(inputData, name, e) });
  };

  const handleExitOnClick = () => {
    history.push("/administrations/driver-management/list");
  };

  const handleSaveOnClick = () => {
    setLoading(true);
    setValidationErrors({});
    setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
    switch (viewType) {
      case "new":
        sendRequest(
          "/api/v1/clickargo/clictruck/administrator/driver",
          "createData",
          "post",
          { ...inputData }
        );
        break;
      case "edit":
        sendRequest(
          "/api/v1/clickargo/clictruck/administrator/driver/" + driverId,
          "updateData",
          "put",
          { ...inputData }
        );
        break;
      default:
        break;
    }
  };

  const handleSubmitDelete = () => {
    setLoading(true);
    setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
    sendRequest(
      "/api/v1/clickargo/clictruck/administrator/driver/" + driverId,
      "deleteData",
      "delete",
      { ...inputData }
    );
  };

  const handleSubmitResetPassword = () => {
    setLoading(true);
    setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
    sendRequest(
      "/api/v1/clickargo/clictruck/administrator/driver/resetPassword/" + driverId,
      "resetPassword",
      "get",
      {  }
    );
  };
  const handleSubmitStatus = () => {
    setLoading(true);
    const action =
      status === RecordStatus.INACTIVE.code ? "deactive" : "active";
    setOpenSubmitConfirm({ ...openSubmitConfirm, action: null, open: false });
    sendRequest(
      "/api/v1/clickargo/clictruck/administrator/driver/" +
        driverId +
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

  const handleDeleteOnClick = () => {
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: "DELETE",
      open: true,
      msg: t("common:msg.deleteConfirm"),
    });
  };

  const handleResetOnClick = () => {
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: "RESET_PASSWORD",
      open: true,
      msg: "Are you sure you want to reset driver password?",
    });
  };

  const eventHandler = (action) => {
    if (action.toLowerCase() === "save") {
      handleSaveOnClick();
    } else if (action.toLowerCase() === "delete") {
      handleSubmitDelete();
    } else if (action.toLowerCase() === "reset_password") {
      handleSubmitResetPassword();
    }  else if (action.toLowerCase() === "status") {
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
          ...{ base64File: base64Sign, drvLicensePhotoName: file.name },
        });
        setFileUploaded(true);
      };
    } else {
      setValidationErrors(errors);
    }
  };

  const handleSignatureValidate = (uploadFileType) => {
    const errors = {};
    const images = ["image/png", "image/jpeg"];
    if (uploadFileType && !images.includes(uploadFileType)) {
      errors.fhotoFileButton = t("common:common.msg.nonImageNotAllowed");
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
      ? t("administration:driverManagement.form.edit")
      : t("administration:driverManagement.form.view");
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
            show: viewType !== 'view',
            eventHandler: handleSubmitOnClick,
          },
        }}
      />
    );

    if (viewType) {
      switch (viewType) {
        case "edit":
          bcLabel = t("administration:driverManagement.form.edit");
          formButtons = (
            <C1FormButtons
              options={{
                ...getFormActionButton(initialButtons, controls, eventHandler),
                ...{
                  activate: {
                    show: inputData.drvStatus === RecordStatus.INACTIVE.code,
                    eventHandler: () => handleSetActiveOnClick(),
                  },
                  deactivate: {
                    show: inputData.drvStatus === RecordStatus.ACTIVE.code,
                    eventHandler: () => handleSetInActiveOnClick(),
                  },
                },
              }}
            >
              <C1LabeledIconButton
                  tooltip={"Reset Password"}
                  label={"Reset Password"}
                  action={() => handleResetOnClick()}
              >
                <RotateLeftIcon color="primary" />
              </C1LabeledIconButton>
            </C1FormButtons>
          );
          break;
        case "new":
          bcLabel = t("administration:driverManagement.breadCrumbs.create");
          break;
        default:
          break;
      }
    }
  }
  const moduleName = "driver";
  const commonId = inputData?.drvId;
  const isUserTo = user?.coreAccn?.TMstAccnType?.atypId === "ACC_TYPE_TO";
  return loading ? (
    <MatxLoading />
  ) : (
    <React.Fragment>
      <C1FormDetailsPanel
        breadcrumbs={[
          {
            name: t("administration:driverManagement.breadCrumbs.list"),
            path: "/administrations/driver-management/list",
          },
          {
            name:
              viewType === "new"
                ? t("administration:driverManagement.breadCrumbs.create")
                : viewType === "view"
                ? t("administration:driverManagement.breadCrumbs.view")
                : viewType === "edit"
                ? t("administration:driverManagement.breadCrumbs.edit")
                : t("administration:driverManagement.breadCrumbs.edit"),
          },
        ]}
        title={bcLabel}
        titleStatus={inputData.drvStatus}
        formButtons={formButtons}
        initialValues={{ ...inputData }}
        values={{ ...inputData }}
        snackBarOptions={{
          ...snackBarOptions,
          redirectPath: "/administrations/driver-management/list",
        }}
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
                    guideId="clictruck.administration.driver.details"
                    title="empty"
                    guideAlign="right"
                    open={false}
                  >
                    <DriverDetails
                      viewType={viewType}
                      handleInputChange={handleInputChange}
                      handleDateChange={handleDateChange}
                      handleInputFileChange={onFileChangeHandler}
                      handleViewFile={handleViewFile}
                      isDisabled={isDisabled}
                      inputData={inputData}
                      errors={validationErrors}
                      fileUploaded={fileUploaded}
                      handleEditPwChange={handleEditPwChange}
                      editable={editable}
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
                          handleExitOnClick={handleExitOnClick}
                          moduleName={moduleName}
                          inputData={inputData}
                          handleInputChange={handleInputChange}
                          handleViewFile={handleViewFile}
                          handleDateChange={handleDateChange}
                          isDisabled={isDisabled}
                          errors={validationErrors}

                      />
                    </C1TabInfoContainer>
                )}
                {tabIndex === (isUserTo ? 2 : 1) && (
                  <C1TabInfoContainer
                    guideId="clictruck.administration.driver.audit"
                    title="empty"
                    guideAlign="right"
                    open={false}
                  >
                    <C1AuditTab
                      filterId={inputData?.drvId ? inputData?.drvId : "empty"}
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

export default withErrorHandler(DriverManagementFormDetails);
