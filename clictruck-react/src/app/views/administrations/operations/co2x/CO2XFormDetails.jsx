import { Button, Dialog, Divider, Grid, Paper, Tabs } from "@material-ui/core";
import AccessTimeOutlinedIcon from "@material-ui/icons/AccessTimeOutlined";
import WorkOutlineOutlinedIcon from "@material-ui/icons/WorkOutlineOutlined";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";

import C1AuditTab from "app/c1component/C1AuditTab";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";
import useHttp from "app/c1hooks/http";
import { RecordStatus } from "app/c1utils/const";
import { getFormActionButton } from "app/c1utils/formActionUtils";
import { deepUpdateState } from "app/c1utils/stateUtils";
import { tabScroll } from "app/c1utils/styles";
import { isEditable } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import TabLabel from "app/portedicomponent/TabLabel";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";
import { ConfirmationDialog, MatxLoading } from "matx";
import CO2XDetails from "./CO2XDetails";

const CO2XFormDetails = () => {
  const { t } = useTranslation(["administration", "common"]);

  const { viewType, id } = useParams();
  const history = useHistory();

  const tabList = [
    {
      text: t("administration:co2x.form.tab.details"),
      icon: <WorkOutlineOutlinedIcon />,
    },
    {
      text: t("administration:co2x.form.tab.audits"),
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
  const [status, setStatus] = useState("");

  const [editable, setEditable] = useState(false);

  const initialButtons = {
    back: { show: true, eventHandler: () => handleExitOnClick() },
    save: {
      show: viewType === "edit",
      eventHandler: () => handleSubmitOnClick(),
    },
    delete: {
      show: inputData?.co2xStatus !== "D" ? true : false,
      eventHandler: () => handleDeleteOnClick(),
    },
  };
  /** ------------------- Update states ----------------- */
  useEffect(() => {
    if (viewType === "new") {
      sendRequest(
        "/api/v1/clickargo/clictruck/administrator/co2x/-",
        "getData",
        "get",
        null
      );
    } else if (viewType === "view" || viewType === "edit") {
      sendRequest(
        "/api/v1/clickargo/clictruck/administrator/co2x/" + id,
        "getData",
        "get",
        null
      );
    }
    // eslint-disable-next-line
  }, [id, viewType]);

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
          setInputData({
            ...res.data,
          });

          setLoading(false);
          break;
        }
        case "updateData": {
          setInputData({ ...res.data });
          setStatus(res.data?.co2xStatus);
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
          setInputData({ ...inputData, co2xStatus: status });
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
  };

  const handleDateChange = (name, e) => {
    setInputData({ ...inputData, ...deepUpdateState(inputData, name, e) });
  };

  const handleExitOnClick = () => {
    history.push("/opadmin/co2x/list");
  };

  const handleSaveOnClick = () => {
    setLoading(true);
    setValidationErrors({});
    setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
    switch (viewType) {
      case "new":
        sendRequest(
          "/api/v1/clickargo/clictruck/administrator/co2x",
          "createData",
          "post",
          { ...inputData }
        );
        break;
      case "edit":
        sendRequest(
          "/api/v1/clickargo/clictruck/administrator/co2x/" + id,
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
      "/api/v1/clickargo/clictruck/administrator/co2x/" + id,
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
      "/api/v1/clickargo/clictruck/administrator/co2x/" + id + "/" + action,
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
      ? t("administration:co2x.form.edit")
      : t("administration:co2x.form.view");
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
          bcLabel = t("administration:co2x.form.edit");
          formButtons = (
            <C1FormButtons
              options={{
                ...getFormActionButton(initialButtons, controls, eventHandler),
                ...{
                  activate: {
                    show: inputData.co2xStatus === RecordStatus.INACTIVE.code,
                    eventHandler: () => handleSetActiveOnClick(),
                  },
                  deactivate: {
                    show: inputData.co2xStatus === RecordStatus.ACTIVE.code,
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
          bcLabel = t("administration:co2x.form.new");
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
            name: t("administration:co2x.listing.title"),
            path: "/opadmin/co2x/list",
          },
        ]}
        title={bcLabel}
        formButtons={formButtons}
        initialValues={{ ...inputData }}
        values={{ ...inputData }}
        snackBarOptions={{
          ...snackBarOptions,
          redirectPath: "/opadmin/co2x/list",
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
                    <CO2XDetails
                      viewType={viewType}
                      handleInputChange={handleInputChange}
                      handleDateChange={handleDateChange}
                      isDisabled={isDisabled}
                      inputData={inputData}
                      errors={validationErrors}
                      editable={editable}
                    />
                  </C1TabInfoContainer>
                )}
                {tabIndex === 1 && (
                  <C1TabInfoContainer
                    guideId="clictruck.administration.driver.audit"
                    title="empty"
                    guideAlign="right"
                    open={false}
                  >
                    <C1AuditTab filterId={id ?? "empty"}></C1AuditTab>
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

export default withErrorHandler(CO2XFormDetails);
