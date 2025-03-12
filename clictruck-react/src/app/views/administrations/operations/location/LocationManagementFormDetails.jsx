import { Divider, Grid, Paper, Tabs, Dialog, Button } from "@material-ui/core";
import React, { useEffect, useState } from "react";
import { useHistory, useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";

import C1AuditTab from "app/c1component/C1AuditTab";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import useHttp from "app/c1hooks/http";
import { deepUpdateState } from "app/c1utils/stateUtils";
import { tabScroll } from "app/c1utils/styles";
import { isEditable } from "app/c1utils/utility";
import { getFormActionButton } from "app/c1utils/formActionUtils";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import TabLabel from "app/portedicomponent/TabLabel";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";
import { ConfirmationDialog, MatxLoading } from "matx";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";
import WorkOutlineOutlinedIcon from "@material-ui/icons/WorkOutlineOutlined";
import AccessTimeOutlinedIcon from "@material-ui/icons/AccessTimeOutlined";
import DriverDetails from "./tabs/LocationDetails";
import DriverAudits from "./tabs/LocationAudits";
import { RecordStatus } from "app/c1utils/const";

const LocationManagementFormDetails = () => {
  const { t } = useTranslation(["administration"]);

  const { viewType, locId } = useParams();
  const history = useHistory();

  const tabList = [
    {
      text: t("administration:locationManagement.tabs.details"),
      icon: <WorkOutlineOutlinedIcon />,
    },
    {
      text: t("administration:locationManagement.tabs.audits"),
      icon: <AccessTimeOutlinedIcon />,
    },
  ];

  const [tabIndex, setTabIndex] = useState(0);
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
  const [controls, setControls] = useState([]);
  const [openSubmitConfirm, setOpenSubmitConfirm] = useState({
    action: null,
    open: false,
  });
  const [validationErrors, setValidationErrors] = useState({});
  const [openWarning, setOpenWarning] = useState(false);
  const [warningMessage, setWarningMessage] = useState("");
  const [status, setStatus] = useState("");

  const initialButtons = {
    back: { show: true, eventHandler: () => handleExitOnClick() },
    save: {
      show: viewType === "edit",
      eventHandler: () => handleSubmitOnClick(),
    },
    delete: { show: true, eventHandler: () => handleDeleteOnClick() },
  };
  /** ------------------- Update states ----------------- */
  useEffect(() => {
    if (viewType === "new") {
      sendRequest(
        "/api/v1/clickargo/clictruck/administrator/location/-",
        "getData",
        "get",
        null
      );
    } else if (viewType === "view" || viewType === "edit") {
      sendRequest(
        "/api/v1/clickargo/clictruck/administrator/location/" + locId,
        "getData",
        "get",
        null
      );
    }
  }, [locId, viewType]);

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
          setInputData({ ...res.data });
          setStatus(res.data?.locStatus);
          setLoading(false);
          break;
        }
        case "updateData": {
          setInputData({ ...res.data });
          setStatus(res.data?.locStatus);
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
          setInputData({ ...inputData, locStatus: status });
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
    setInputData({
      ...inputData,
      ...deepUpdateState(inputData, elName, e.target.value),
    });
    console.log("input data", inputData);
  };

  const handleDateChange = (name, e) => {
    setInputData({ ...inputData, ...deepUpdateState(inputData, name, e) });
  };

  const handleExitOnClick = () => {
    history.push("/administrations/location-management/list");
  };

  const handleSaveOnClick = () => {
    setLoading(true);
    setValidationErrors({});
    setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
    switch (viewType) {
      case "new":
        sendRequest(
          "/api/v1/clickargo/clictruck/administrator/location",
          "createData",
          "post",
          { ...inputData }
        );
        break;
      case "edit":
        sendRequest(
          "/api/v1/clickargo/clictruck/administrator/location/" + locId,
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
      "/api/v1/clickargo/clictruck/administrator/location/" + locId,
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
      "/api/v1/clickargo/clictruck/administrator/location/" +
        locId +
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

  let bcLabel =
    viewType === "edit"
      ? t("administration:locationManagement.form.edit")
      : t("administration:locationManagement.form.view");
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
          bcLabel = t("administration:locationManagement.form.edit");
          formButtons = (
            <C1FormButtons
              options={{
                ...getFormActionButton(initialButtons, controls, eventHandler),
                ...{
                  activate: {
                    show: inputData.locStatus === RecordStatus.INACTIVE.code,
                    eventHandler: () => handleSetActiveOnClick(),
                  },
                  deactivate: {
                    show: inputData.locStatus === RecordStatus.ACTIVE.code,
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
          bcLabel = t("administration:locationManagement.breadCrumbs.create");
          break;
        default:
          break;
      }
    }
  }

  return loading ? (
    <MatxLoading />
  ) : (
    <React.Fragment>
      <C1FormDetailsPanel
        breadcrumbs={[
          {
            name: t("administration:locationManagement.breadCrumbs.list"),
            path: "/administrations/location-management/list",
          },
          {
            name:
              viewType === "new"
                ? t("administration:locationManagement.breadCrumbs.create")
                : viewType === "view"
                ? t("administration:locationManagement.breadCrumbs.view")
                : viewType === "edit"
                ? t("administration:locationManagement.breadCrumbs.edit")
                : t("administration:locationManagement.breadCrumbs.edit"),
          },
        ]}
        title={bcLabel}
        titleStatus={inputData.locStatus}
        formButtons={formButtons}
        initialValues={{ ...inputData }}
        values={{ ...inputData }}
        snackBarOptions={{
          ...snackBarOptions,
          redirectPath: "/administrations/location-management/list",
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
                    guideId="clictruck.administration.location.details"
                    title="empty"
                    guideAlign="right"
                    open={false}
                  >
                    <DriverDetails
                      viewType={viewType}
                      handleInputChange={handleInputChange}
                      handleDateChange={handleDateChange}
                      isDisabled={isDisabled}
                      inputData={inputData}
                      errors={validationErrors}
                    />
                  </C1TabInfoContainer>
                )}

                {tabIndex === 1 && (
                  <C1TabInfoContainer
                    guideId="clictruck.administration.location.audit"
                    title="empty"
                    guideAlign="right"
                    open={false}
                  >
                    <C1AuditTab
                      filterId={inputData?.locId ? inputData?.locId : "empty"}
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

export default withErrorHandler(LocationManagementFormDetails);
