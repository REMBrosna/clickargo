import { Divider, Grid, Paper, Tabs } from "@material-ui/core";
import AccessTimeOutlinedIcon from "@material-ui/icons/AccessTimeOutlined";
import LocalShippingOutlinedIcon from "@material-ui/icons/LocalShippingOutlined";
import ChatOutlinedIcon from "@material-ui/icons/ChatOutlined";
import moment from "moment";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";

import C1AuditTab from "app/c1component/C1AuditTab";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";
import useHttp from "app/c1hooks/http";
import { Actions, JobStates } from "app/c1utils/const";
import { deepUpdateState } from "app/c1utils/stateUtils";
import { tabScroll } from "app/c1utils/styles";
import {
  getValue,
  isEditable,
  isFinanceApprover,
  isSpL1,
} from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import NearMeOutlinedIcon from "@material-ui/icons/NearMeOutlined";
import TabLabel from "app/portedicomponent/TabLabel";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";
import { ConfirmationDialog, MatxLoading } from "matx";

import TripRates from "./tabs/TripRates";
import RateTableComments from "./tabs/RateTableComments";
import useAuth from "app/hooks/useAuth";
import { getFormActionButton } from "app/c1utils/formActionUtils";
import C1PopUp from "app/c1component/C1PopUp";
import C1TextArea from "app/c1component/C1TextArea";
import C1IconButton from "app/c1component/C1IconButton";

const RateTableFormDetailsSp = () => {
  const { t } = useTranslation(["common", "administration"]);

  const { user } = useAuth();
  const isApprover = isFinanceApprover([user.authorities]);
  const isLevel1 = isSpL1([user.authorities]);
  // const isApprover = user.authorities.includes("SP_FIN_HD");

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
  // eslint-disable-next-line
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
  const [rtVehType, setRtVehType] = useState("");
  const [controls, setControls] = useState([]);
  const [remarksAction, setRemarksAction] = useState({
    open: false,
    action: null,
    text: null,
  });

  // let clicTruckUrl = `/api/v1/clickargo/clictruck/truck`;
  let clicTruckUrl = `/api/v1/clickargo/clictruck/administrator/ratetableSp`;

  /** ------------------- Update states ----------------- */
  useEffect(() => {
    if (viewType === "view") {
      setSnackBarOptions(defaultSnackbarValue);
      setLoading(true);
      setFileUploaded(true);
      sendRequest(`${clicTruckUrl}/` + id, "getRateTable", "GET", null);
    }

    // eslint-disable-next-line
  }, [id, viewType]);

  useEffect(() => {
    if (!isLoading && !error && res && !validation) {
      setLoading(isLoading);
      switch (urlId) {
        case "getRateTable": {
          let data = res.data;
          setInputData({ ...data });

          const hasSubmit = data?.hasSubmitTripRate;
          const hasVerify = data?.hasVerifyTripRate;

          let rtState = "APP";
          if (isLevel1 && hasSubmit) {
            rtState = "SUB";
          } else if (isApprover && hasVerify) {
            rtState = "VER";
          }
          //fetch control from database
          const reqBody = {
            entityType: "TRIP_RATE_TABLE",
            entityState: rtState,
            page: viewType.toUpperCase(),
          };

          sendRequest(
            "/api/v1/clickargo/controls/",
            "fetchControls",
            "post",
            reqBody
          );
          break;
        }

        case "fetchControls": {
          setControls([...res.data]);
          break;
        }
        case "reject":
        case "approve":
        case "verify":
          setLoading(false);
          let msg = t("common:common.msg.generalAction", {
            action: Actions[openSubmitConfirm?.action]?.result,
          });
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: msg,
            redirectPath: "/manage/ratetable",
          });
          break;
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
        setRtVehType(getValue(value?.value));
      } else {
        setInputData({
          ...inputData,
          ...deepUpdateState(inputData, name, value),
        });
      }
    } else {
      const elName = e.target.name;
      if (e.target.name && e.target.value)
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
    history.push("/manage/ratetable");
  };

  const handleSaveOnClick = () => {
    setLoading(true);
    setValidationErrors({});
    switch (viewType) {
      case "new":
        sendRequest(`${clicTruckUrl}`, "createRateTable", "POST", {
          ...inputData,
        });
        break;
      case "edit":
        sendRequest(`${clicTruckUrl}/` + id, "updateRateTable", "PUT", {
          ...inputData,
        });
        break;
      default:
        break;
    }
  };

  // FORM CONTROL SETTINGS
  const initialButtons = {
    back: { show: true, eventHandler: () => handleExitOnClick() },
  };

  const eventHandler = (action) => {
    if (action.toLowerCase() === "exit") {
      handleExitOnClick();
    } else {
      handleActionOnClick(action);
    }
  };

  const handleActionOnClick = (actionUp) => {
    setInputData({ ...inputData, action: actionUp });
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: actionUp,
      open: true,
    });
  };

  const handleConfirmAction = (e) => {
    setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
    //prompt remarks for approve & reject
    if (
      openSubmitConfirm?.action === "APPROVE" ||
      openSubmitConfirm?.action === "VERIFY" ||
      openSubmitConfirm?.action === "REJECT"
    ) {
      setRemarksAction({
        ...remarksAction,
        open: true,
        action: openSubmitConfirm?.action,
      });
    } else {
      sendRequest(
        `/api/v1/clickargo/clictruck/administrator/ratetable/${id}`,
        openSubmitConfirm.action.toLowerCase(),
        "PUT",
        { ...inputData }
      );
    }
  };

  const handleRemarkActionSubmit = (e) => {
    let reqBody = { ...inputData, actionRemarks: remarksAction?.text };
    sendRequest(
      `/api/v1/clickargo/clictruck/administrator/ratetable/${id}`,
      openSubmitConfirm.action.toLowerCase(),
      "PUT",
      reqBody
    );
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
            path: "/manage/ratetable",
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
                    guideId="clictruck.administration.ratetable.sp.details"
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
        text={t("common:msg.confirmation", {
          action: Actions[openSubmitConfirm?.action]?.text,
        })}
        title={t("common:popup.confirmation")}
        onYesClick={(e) => handleConfirmAction(e)}
      />

      {/* PopUp for Remark */}
      <C1PopUp
        maxWidth={"md"}
        title={`${Actions[remarksAction?.action]?.text} Remarks`}
        openPopUp={remarksAction?.open}
        setOpenPopUp={setRemarksAction}
        actionsEl={
          <C1IconButton
            disabled={!remarksAction?.text}
            tooltip={t("buttons:submit")}
            childPosition="right"
          >
            <NearMeOutlinedIcon
              color="primary"
              fontSize="large"
              onClick={(e) => {
                setRemarksAction({ ...remarksAction, open: false });
                return handleRemarkActionSubmit(e);
              }}
            ></NearMeOutlinedIcon>
          </C1IconButton>
        }
      >
        <C1TextArea
          textLimit={512}
          required
          name="remarksAction.text"
          value={getValue(remarksAction?.text)}
          onChange={(e) =>
            setRemarksAction({ ...remarksAction, text: e?.target?.value })
          }
        />
      </C1PopUp>
    </React.Fragment>
  );
};

export default withErrorHandler(RateTableFormDetailsSp);
