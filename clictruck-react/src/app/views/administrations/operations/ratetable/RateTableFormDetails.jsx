import { Divider, Grid, Paper, Tabs } from "@material-ui/core";
import AccessTimeOutlinedIcon from "@material-ui/icons/AccessTimeOutlined";
import LocalShippingOutlinedIcon from "@material-ui/icons/LocalShippingOutlined";
import moment from "moment";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";

import C1AuditTab from "app/c1component/C1AuditTab";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";
import useHttp from "app/c1hooks/http";
import { Actions, JobStates, RecordStatus } from "app/c1utils/const";
import { deepUpdateState } from "app/c1utils/stateUtils";
import { tabScroll } from "app/c1utils/styles";
import { getValue, isEditable } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";

import TabLabel from "app/portedicomponent/TabLabel";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";
import { ConfirmationDialog, MatxLoading } from "matx";

import TripRates from "./tabs/TripRates";
import RateTableComments from "./tabs/RateTableComments";
import ChatOutlinedIcon from "@material-ui/icons/ChatOutlined";

/** @description Component for rate table details for TO. */
const RateTableFormDetails = () => {
  const { t } = useTranslation(["cargoowners", "administration"]);

  const { viewType, id } = useParams();
  const history = useHistory();

  const tabList = [
    {
      text: t("administration:rateTableManagement.tabs.rateTable"),
      icon: <LocalShippingOutlinedIcon />,
    },
    {
      text: t("administration:rateTableManagement.tabs.comments"),
      icon: <ChatOutlinedIcon />,
    },
    {
      text: t("administration:rateTableManagement.tabs.audit"),
      icon: <AccessTimeOutlinedIcon />,
    },
  ];

  const [tabIndex, setTabIndex] = useState(0);
  const [isDisabled, setDisabled] = useState(isEditable(viewType));

  const coFf = history?.location?.state?.coFf;
  const [hasNew, setHasNew] = useState(false);

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
  const [openSubmitConfirm, setOpenSubmitConfirm] = useState({
    action: null,
    open: false,
  });
  const [validationErrors, setValidationErrors] = useState({});
  const [fileUploaded, setFileUploaded] = useState(false);
  const [action, setAction] = useState("");
  const [status, setStatus] = useState("");
  const [rtVehType, setRtVehType] = useState("");

  let rateTableUrl = `/api/v1/clickargo/clictruck/administrator/ratetable`;

  /** ------------------- Update states ----------------- */
  useEffect(() => {
    if (viewType === "new") {
      sendRequest(`${rateTableUrl}/` + id, "newRateTable", "GET", {});
    } else if (viewType === "view" || viewType === "edit") {
      setSnackBarOptions(defaultSnackbarValue);
      setLoading(true);
      setFileUploaded(true);
      sendRequest(`${rateTableUrl}/` + id, "getRateTable", "GET", null);
    }
  }, [id, viewType]);

  useEffect(() => {
    if (!isLoading && !error && res && !validation) {
      setLoading(isLoading);
      switch (urlId) {
        case "newRateTable": {
          setInputData({ ...res?.data, tcoreAccnByRtCoFf: { accnId: coFf } }); //setting default for documentType
          break;
        }
        case "createRateTable": {
          setInputData({ ...res?.data });
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: "Saved successfully",
            redirectPath:
              "/administrations/rateTable-management/edit/" + res?.data?.rtId,
          });
          break;
        }
        case "getRateTable": {
          let data = res.data;
          setInputData({ ...data });
          console.log("data", data);
          // setIsCargoOwner(data?.cargoOwner);
          // setIsFreightForwarder(data?.freightForwarder);

          break;
        }
        case "updateRateTable": {
          let data = res.data;
          setInputData({ ...data });
          setLoading(false);
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("cargoowners:msg.updateSuccess"),
          });
          break;
        }
        case "submitRateTable": {
          let data = res.data;
          setInputData({ ...data });
          setLoading(false);
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("cargoowners:msg.updateSuccess"),
            redirectPath: "/administrations/rateTable-management/list",
          });
          break;
        }
        case "deactivate":
        case "activate":
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: action,
            redirectPath: "/administrations/rateTable-management/list",
          });
          setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
          break;
        case "setStatus": {
          setLoading(false);
          setInputData({ ...inputData, rtStatus: status });
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
            redirectPath: "/administrations/rateTable-management/list",
          });
          break;
        }
        default:
          break;
      }
    }

    if (error) {
      //goes back to the screen
      setLoading(false);
    }

    //If validation has value then set to the errors
    if (validation) {
      setValidationErrors({ ...validation });
      setLoading(false);
      setSnackBarOptions(defaultSnackbarValue);
    }

    // eslint-disable-next-line
  }, [urlId, isLoading, isFormSubmission, res, validation, error]);

  /** ---------------- Event handlers ----------------- */
  const handleTabChange = (e, value) => {
    setTabIndex(value);
  };

  const handleInputChange = (e, name, value) => {
    console.log("handler", e.target.name, e.target.value, name, value);
    console.log("inputData befor handler", inputData);
    if (name && value) {
      if (name.includes("rtVehType")) {
        console.log("auto handler value", value);
        setRtVehType(getValue(value?.value));
        console.log("rtVehType", rtVehType);
      } else {
        console.log("handleInputChange autocomplete", name, value?.value);
        setInputData({
          ...inputData,
          ...deepUpdateState(inputData, name, value),
        });
        console.log("inputData after handler", inputData);
      }
    } else {
      const elName = e.target.name;
      // if (e.target.name && e.target.value)
      setInputData({
        ...inputData,
        ...deepUpdateState(inputData, elName, e.target.value),
      });
    }
  };

  const handleDateChange = (name, e) => {
    if (name === "rtDtStart") {
      let startDt = moment(e).format("YYYY/MM/DD");
      let expDt = moment(inputData?.rtDtEnd).format("YYYY/MM/DD");
      if (expDt < startDt) {
        setInputData({
          ...inputData,
          rtDtStart: e,
          rtDtEnd: e,
        });
      } else {
        setInputData({ ...inputData, ...deepUpdateState(inputData, name, e) });
      }
    } else if (name === "rtDtEnd") {
      setInputData({ ...inputData, ...deepUpdateState(inputData, name, e) });
    }
  };

  const handleExitOnClick = () => {
    history.push("/administrations/rateTable-management/list");
  };

  const handleSaveOnClick = () => {
    setLoading(true);
    setValidationErrors({});
    switch (viewType) {
      case "new":
        sendRequest(`${rateTableUrl}`, "createRateTable", "POST", {
          ...inputData,
        });
        break;
      case "edit":
        sendRequest(`${rateTableUrl}/` + id, "updateRateTable", "PUT", {
          ...inputData,
        });
        break;
      default:
        break;
    }
  };

  const handleSubmitOnClick = () => {
    setLoading(true);
    //put submit ratetable with action = submit
    sendRequest(`${rateTableUrl}/` + id, "submitRateTable", "PUT", {
      ...inputData,
      action: "SUBMIT",
    });
  };

  const handleSubmitDelete = () => {
    setLoading(true);
    setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
    sendRequest(
      "/api/v1/clickargo/clictruck/administrator/ratetable/" + id,
      "deleteData",
      "delete",
      { ...inputData }
    );
  };

  const handleSubmitStatus = () => {
    setLoading(true);
    const action = status === RecordStatus.ACTIVE.code ? "active" : "deactive";
    setOpenSubmitConfirm({ ...openSubmitConfirm, action: null, open: false });
    sendRequest(
      "/api/v1/clickargo/clictruck/administrator/ratetable/" +
        id +
        "/" +
        action,
      "setStatus",
      "put",
      null
    );
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

  let bcLabel = "View Rate Table";
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
          bcLabel = "Edit Rate Table";
          formButtons = (
            <C1FormButtons
              options={{
                back: { show: true, eventHandler: () => handleExitOnClick() },
                activate: {
                  show: inputData.rtStatus === RecordStatus.INACTIVE.code,
                  eventHandler: () => handleSetActiveOnClick(),
                },
                deactivate: {
                  show: inputData.rtStatus === RecordStatus.ACTIVE.code,
                  eventHandler: () => handleSetInActiveOnClick(),
                },
                save: {
                  show: true,
                  eventHandler: handleSaveOnClick,
                },
                submitOnClick: {
                  //only show this button if ratetable is already saved.
                  show: inputData?.hasNewTripRate === true || hasNew,
                  eventHandler: handleSubmitOnClick,
                },
                delete: {
                  show: true,
                  eventHandler: () => handleDeleteOnClick(),
                },
              }}
            />
          );
          break;
        case "view":
          formButtons = (
            <C1FormButtons
              options={{
                back: { show: true, eventHandler: () => handleExitOnClick() },
                activate: {
                  show: inputData.rtStatus === RecordStatus.INACTIVE.code,
                  eventHandler: () => handleSetActiveOnClick(),
                },
              }}
            />
          );
          break;
        case "new":
          bcLabel = "New Rate Table";
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
            name: t("administration:rateTableManagement.breadCrumbs.list"),
            path: "/administrations/rateTable-management/list",
          },
          {
            name:
              viewType === "new"
                ? t("administration:rateTableManagement.breadCrumbs.create")
                : viewType === "view"
                ? t("administration:rateTableManagement.breadCrumbs.view")
                : viewType === "edit"
                ? t("administration:rateTableManagement.breadCrumbs.edit")
                : t("administration:rateTableManagement.breadCrumbs.title"),
          },
        ]}
        title={bcLabel}
        titleStatus={inputData?.rtStatus || JobStates.DRF.code.toUpperCase()}
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
                    guideId="clictruck.administration.ratetable.details"
                    title="empty"
                    guideAlign="right"
                    open={false}
                  >
                    <TripRates
                      viewType={viewType}
                      handleInputChange={handleInputChange}
                      handleDateChange={handleDateChange}
                      isDisabled={isDisabled}
                      inputData={inputData}
                      errors={validationErrors}
                      fileUploaded={fileUploaded}
                      rtVehType={rtVehType}
                      setHasNew={setHasNew}
                    />
                  </C1TabInfoContainer>
                )}
                {tabIndex === 1 && (
                  <C1TabInfoContainer
                    guideId="clictruck.administration.ratetable.comments"
                    title="empty"
                    guideAlign="right"
                    open={false}
                  >
                    <RateTableComments inputData={inputData} />
                  </C1TabInfoContainer>
                )}
                {tabIndex === 2 && (
                  <C1TabInfoContainer
                    guideId="clictruck.administration.ratetable.audit"
                    title="empty"
                    guideAlign="right"
                    open={false}
                  >
                    <C1AuditTab
                      filterId={inputData.rtId ? inputData.rtId : "draft"}
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
    </React.Fragment>
  );
};

export default withErrorHandler(RateTableFormDetails);
