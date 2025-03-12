import { Button, Dialog, Divider, Grid, Paper, Tabs } from "@material-ui/core";
import AccessTimeOutlinedIcon from "@material-ui/icons/AccessTimeOutlined";
import SyncAltOutlinedIcon from "@material-ui/icons/SyncAltOutlined";
import WorkOutlineOutlinedIcon from "@material-ui/icons/WorkOutlineOutlined";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";

import C1AuditTab from "app/c1component/C1AuditTab";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";
import useHttp from "app/c1hooks/http";
import { RecordStatus, Roles } from "app/c1utils/const";
import { getFormActionButton } from "app/c1utils/formActionUtils";
import { deepUpdateState } from "app/c1utils/stateUtils";
import { tabScroll } from "app/c1utils/styles";
import { isEditable } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import TabLabel from "app/portedicomponent/TabLabel";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";
import { ConfirmationDialog, MatxLoading } from "matx";

import ContractDetails from "./tabs/ContractDetails";

/**@description Read only component of the contract details. */
const ContractManagementFormDetails = () => {
  const { t } = useTranslation(["administration", "common"]);

  const { viewType, contractId } = useParams();
  const history = useHistory();
  const { user } = useAuth();
  let isL1 = false;
  if (
    user?.authorities.some((el) => [Roles.SP_L1.code].includes(el.authority))
  ) {
    isL1 = true;
  }
  const tabList = [
    {
      text: t("administration:contractManagement.tabs.details"),
      icon: <WorkOutlineOutlinedIcon />,
    },
    {
      text: t("administration:contractManagement.tabs.audits"),
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
        "/api/v1/clickargo/clictruck/administrator/contract/-",
        "getData",
        "get",
        null
      );
    } else if (viewType === "view" || viewType === "edit") {
      sendRequest(
        "/api/v1/clickargo/clictruck/administrator/contract/" + contractId,
        "getData",
        "get",
        null
      );
    }
  }, [contractId, viewType]);

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
            redirectPath:
              "/administrations/contract-management/edit/" + res?.data?.conId,
          });
          break;
        }
        case "getData": {
          let data = { ...res.data };
          if (!data?.tckCtContractChargeByConChargeTo?.concPltfeeType) {
            data = {
              ...data,
              tckCtContractChargeByConChargeTo: {
                ...data.tckCtContractChargeByConChargeTo,
                concPltfeeType: "F",
              },
            };
          }
          if (!data?.tckCtContractChargeByConChargeTo?.concAddtaxType) {
            data = {
              ...data,
              tckCtContractChargeByConChargeTo: {
                ...data.tckCtContractChargeByConChargeTo,
                concAddtaxType: "F",
              },
            };
          }
          if (!data?.tckCtContractChargeByConChargeTo?.concWhtaxType) {
            data = {
              ...data,
              tckCtContractChargeByConChargeTo: {
                ...data.tckCtContractChargeByConChargeTo,
                concWhtaxType: "F",
              },
            };
          }
          if (!data?.tckCtContractChargeByConChargeCoFf?.concPltfeeType) {
            data = {
              ...data,
              tckCtContractChargeByConChargeCoFf: {
                ...data.tckCtContractChargeByConChargeCoFf,
                concPltfeeType: "F",
              },
            };
          }
          if (!data?.tckCtContractChargeByConChargeCoFf?.concAddtaxType) {
            data = {
              ...data,
              tckCtContractChargeByConChargeCoFf: {
                ...data.tckCtContractChargeByConChargeCoFf,
                concAddtaxType: "F",
              },
            };
          }
          if (!data?.tckCtContractChargeByConChargeCoFf?.concWhtaxType) {
            data = {
              ...data,
              tckCtContractChargeByConChargeCoFf: {
                ...data.tckCtContractChargeByConChargeCoFf,
                concWhtaxType: "F",
              },
            };
          }

          if (
            data?.tckCtContractChargeByConChargeTo?.concAddtaxAmt !== 0 &&
            data?.tckCtContractChargeByConChargeTo?.concAddtaxAmt !== null
          ) {
            data = { ...data, additionalTaxTo: true };
          }

          if (
            data?.tckCtContractChargeByConChargeTo?.concWhtaxAmt !== 0 &&
            data?.tckCtContractChargeByConChargeTo?.concWhtaxAmt !== null
          ) {
            data = { ...data, witholdTaxTo: true };
          }

          if (
            data?.tckCtContractChargeByConChargeCoFf?.concAddtaxAmt !== 0 &&
            data?.tckCtContractChargeByConChargeCoFf?.concAddtaxAmt !== null
          ) {
            data = { ...data, additionalTaxCoFf: true };
          }

          if (
            data?.tckCtContractChargeByConChargeCoFf?.concWhtaxAmt !== 0 &&
            data?.tckCtContractChargeByConChargeCoFf?.concWhtaxAmt !== null
          ) {
            data = { ...data, witholdTaxCoFf: true };
          }

          data = {
            ...data,
            conPaytermCoFf: String(data?.conPaytermCoFf),
            conPaytermTo: String(data?.conPaytermTo),
          };

          console.log("data", data);
          setInputData(data);
          setStatus(data?.conId);
          setLoading(false);
          break;
        }
        case "updateData": {
          setInputData({ ...res.data });
          setStatus(res.data?.conId);
          setLoading(false);
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
            redirectPath: "/administrations/contract-management/list",
          });
          break;
        }
        case "setStatus": {
          setLoading(false);
          setInputData({ ...inputData, conStatus: status });
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("common:msg.updateSuccess"),
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
      console.log("validation", validation);
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
    if (e.target.type === "checkbox") {
      setInputData({
        ...inputData,
        ...deepUpdateState(inputData, elName, e.target.checked),
      });
    } else {
      setInputData({
        ...inputData,
        ...deepUpdateState(inputData, elName, e.target.value),
      });
    }
    console.log("input data", inputData);
    validationCheck(inputData);
  };

  const handleDateChange = (name, e) => {
    setInputData({ ...inputData, ...deepUpdateState(inputData, name, e) });
  };

  const handleExitOnClick = () => {
    let from = history?.location?.state?.from;
    history.push(from ? from : "/administrations/contract-management/list");
  };

  const validationCheck = (data) => {
    const to = data?.tckCtContractChargeByConChargeTo;
    const coff = data?.tckCtContractChargeByConChargeCoFf;
    let valData = {};

    if (to.concPltfeeType === "P" && to.concPltfeeAmt > 100) {
      valData = {
        ...valData,
        "TCkCtContractChargeByConChargeTo.concPltfeeAmt": t(
          "common:validationMsgs.maxValidation",
          { maxValue: "100%" }
        ),
      };
    }

    if (to.concAddtaxType === "P" && to.concAddtaxAmt > 100) {
      valData = {
        ...valData,
        "TCkCtContractChargeByConChargeTo.concAddtaxAmt": t(
          "common:validationMsgs.maxValidation",
          { maxValue: "100%" }
        ),
      };
    }

    if (to.concWhtaxType === "P" && to.concWhtaxAmt > 100) {
      valData = {
        ...valData,
        "TCkCtContractChargeByConChargeTo.concWhtaxAmt": t(
          "common:validationMsgs.maxValidation",
          { maxValue: "100%" }
        ),
      };
    }

    if (coff.concPltfeeType === "P" && coff.concPltfeeAmt > 100) {
      valData = {
        ...valData,
        "TCkCtContractChargeByConChargeCoFf.concPltfeeAmt": t(
          "common:validationMsgs.maxValidation",
          { maxValue: "100%" }
        ),
      };
    }

    if (coff.concAddtaxType === "P" && coff.concAddtaxAmt > 100) {
      valData = {
        ...valData,
        "TCkCtContractChargeByConChargeCoFf.concAddtaxAmt": t(
          "common:validationMsgs.maxValidation",
          { maxValue: "100%" }
        ),
      };
    }

    if (coff.concWhtaxType === "P" && coff.concWhtaxAmt > 100) {
      valData = {
        ...valData,
        "TCkCtContractChargeByConChargeCoFf.concWhtaxAmt": t(
          "common:validationMsgs.maxValidation",
          { maxValue: "100%" }
        ),
      };
    }

    if (valData) {
      setValidationErrors(valData);
    } else {
      setValidationErrors({});
    }
  };

  const handleSaveOnClick = () => {
    setLoading(true);
    setValidationErrors({});
    setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
    switch (viewType) {
      case "new":
        delete inputData.fFAddCheck;
        delete inputData.fFWithHoldCheck;
        delete inputData.tOAddCheck;
        delete inputData.tOWithHoldCheck;
        delete inputData.tOFeeCheck;
        sendRequest(
          "/api/v1/clickargo/clictruck/administrator/contract",
          "createData",
          "post",
          { ...inputData }
        );
        break;
      case "edit":
        sendRequest(
          "/api/v1/clickargo/clictruck/administrator/contract/" + contractId,
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
      "/api/v1/clickargo/clictruck/administrator/contract/" + contractId,
      "deleteData",
      "delete",
      { ...inputData }
    );
  };

  const handleWarningAction = (e) => {
    setOpenWarning(false);
    setWarningMessage("");
  };

  const handleSubmitOnClick = () => {
    Object.keys(validationErrors).length === 0 &&
      setOpenSubmitConfirm({
        ...openSubmitConfirm,
        action: "SAVE",
        open: true,
        msg: t("common:msg.saveConfirm"),
      });
  };

  const handleSubmitStatus = () => {
    setLoading(true);
    const action =
      status === RecordStatus.INACTIVE.code ? "deactive" : "active";
    setOpenSubmitConfirm({ ...openSubmitConfirm, action: null, open: false });
    // sendRequest("/api/v1/clickargo/clictruck/administrator/contract/" + contractId + "/" + action, "setStatus", "put", null);
    if (action === "deactive") {
      sendRequest(
        "/api/v1/clickargo/clictruck/administrator/contract/" + contractId,
        "updateData",
        "put",
        { ...inputData, conStatus: RecordStatus.INACTIVE.code }
      );
    } else if (action === "active") {
      sendRequest(
        "/api/v1/clickargo/clictruck/administrator/contract/" + contractId,
        "updateData",
        "put",
        { ...inputData, conStatus: "A" }
      );
    }
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
      ? t("administration:contractManagement.form.edit")
      : t("administration:contractManagement.form.viewContract");
  let formButtons;
  if (!loading) {
    formButtons = (
      <C1FormButtons
        options={{
          back: {
            show: true,
            eventHandler: handleExitOnClick,
          },
          updateRedirect: {
            show: viewType === "view",
            eventHandler: handleSubmitOnClick,
          },
        }}
      >
        {isL1 && (
          <C1LabeledIconButton
            tooltip={t("buttons:update")}
            label={t("buttons:update")}
            action={() =>
              history.push({
                pathname: `/opadmin/contractrequest/new/0`,
                state: {
                  contractId: contractId,
                  from: `/administrations/contract-management/view/${contractId}`,
                },
              })
            }
          >
            <SyncAltOutlinedIcon color="primary" />
          </C1LabeledIconButton>
        )}
      </C1FormButtons>
    );
  }

  return loading ? (
    <MatxLoading />
  ) : (
    <React.Fragment>
      <C1FormDetailsPanel
        breadcrumbs={[
          {
            name: t("administration:contractManagement.title"),
            path: "/opadmin/contracts",
          },
          {
            name:
              viewType === "new"
                ? t("administration:contractManagement.breadCrumbs.create")
                : viewType === "view"
                ? t(
                    "administration:contractManagement.breadCrumbs.viewContract"
                  )
                : viewType === "edit"
                ? t(
                    "administration:contractManagement.breadCrumbs.editContract"
                  )
                : t(
                    "administration:contractManagement.breadCrumbs.editContract"
                  ),
          },
        ]}
        title={bcLabel}
        titleStatus={inputData.conStatus}
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
                    guideId="clicdo.doi.co.jobs.tabs.details"
                    title="empty"
                    guideAlign="right"
                    open={false}
                  >
                    <ContractDetails
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
                    guideId="clicdo.doi.co.jobs.tabs.audit"
                    title="empty"
                    guideAlign="right"
                    open={false}
                  >
                    <C1AuditTab
                      filterId={inputData?.conId ? inputData.conId : "empty"}
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

export default withErrorHandler(ContractManagementFormDetails);
