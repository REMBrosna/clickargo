import { Button, Dialog, Divider, Grid, Paper, Tabs } from "@material-ui/core";
import AccessTimeOutlinedIcon from "@material-ui/icons/AccessTimeOutlined";
import AssignmentTurnedInOutlinedIcon from "@material-ui/icons/AssignmentTurnedInOutlined";
import NearMeOutlinedIcon from "@material-ui/icons/NearMeOutlined";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";

import C1AuditTab from "app/c1component/C1AuditTab";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1IconButton from "app/c1component/C1IconButton";
import C1PopUp from "app/c1component/C1PopUp";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";
import C1TextArea from "app/c1component/C1TextArea";
import useHttp from "app/c1hooks/http";
import {
  Actions,
  CK_ACTIVE_ACCOUNT_TO_ACCN,
  CK_ACTIVE_COFF_ACCN,
  CK_ACTIVE_COFF_ACCN_OPM,
  CK_ACTIVE_TO_ACCN_OPM,
  ContractRequestStates,
  FINANCING_MODELS,
} from "app/c1utils/const";
import { getFormActionButton } from "app/c1utils/formActionUtils";
import { deepUpdateState } from "app/c1utils/stateUtils";
import { tabScroll } from "app/c1utils/styles";
import { getValue, isEditable } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import TabLabel from "app/portedicomponent/TabLabel";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";
import { ConfirmationDialog, MatxLoading } from "matx";

import ContractReqDetails from "./tabs/ContractReqDetails";

const ContractReqFormDetails = () => {
  const { t } = useTranslation(["administration", "common"]);

  const { viewType, contractReqId } = useParams();
  const history = useHistory();

  const tabList = [
    {
      text: t("administration:contractManagement.tabs.details"),
      icon: <AssignmentTurnedInOutlinedIcon />,
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
  const [remarksAction, setRemarksAction] = useState({
    open: false,
    action: null,
    text: null,
  });

  const [isToOpm, setIsToOpm] = useState(false);
  const [isCoOpm, setIsCoOpm] = useState(false);
  const [coOptionsUrl, setCoOptionsUrl] = useState(CK_ACTIVE_COFF_ACCN);
  const [toOptionsUrl, setToOptionsUrl] = useState(CK_ACTIVE_ACCOUNT_TO_ACCN);
  //state holder to set the financing model if isToOpm or isCoOpm changes
  const [enableBankCharges, setEnableBankCharges] = useState(false);

  const initialButtons = {
    back: { show: true, eventHandler: () => handleExitOnClick() },
    save: {
      show: viewType === "edit",
      eventHandler: () => handleSaveOnClick(),
    },
  };
  /** ------------------- Update states ----------------- */
  useEffect(() => {
    if (viewType === "new") {
      if (contractReqId === "-") {
        sendRequest(
          "/api/v1/clickargo/clictruck/administrator/contractReq/-",
          "newContractReq",
          "get",
          null
        );
      } else {
        let contractId = history?.location?.state?.contractId;
        sendRequest(
          `/api/v1/clickargo/clictruck/administrator/contractReq/${contractId}`,
          "newContractReq",
          "get",
          null
        );
      }
    } else if (viewType === "view" || viewType === "edit") {
      sendRequest(
        "/api/v1/clickargo/clictruck/administrator/contractReq/" +
          contractReqId,
        "getData",
        "get",
        null
      );
    }
  }, [contractReqId, viewType]);

  useEffect(() => {
    if (!isLoading && !error && res && !validation) {
      switch (urlId) {
        case "newContractReq":
          setLoading(false);
          console.log("newContractReq: ", res?.data);
          setInputData({ ...res?.data });
          break;
        case "createData": {
          setLoading(false);
          setInputData({ ...res?.data });
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("common:msg.saveSuccess"),
            redirectPath: "/opadmin/contractrequest/edit/" + res?.data?.crId,
          });
          break;
        }
        case "getData": {
          let data = { ...res.data };
          setInputData(data);
          const reqBody = {
            entityType: "CONTRACT_REQUEST",
            entityState:
              ContractRequestStates[data?.tckCtMstContractReqState?.stId]
                ?.altCode,
            page: viewType.toUpperCase(),
          };
          sendRequest(
            "/api/v1/clickargo/controls/",
            "fetchControls",
            "POST",
            reqBody
          );
          break;
        }
        case "fetchControls": {
          setControls([...res?.data]);
          setLoading(false);
          break;
        }
        case "updateData": {
          setInputData({ ...res.data });
          setLoading(false);
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("common:msg.updateSuccess"),
          });
          break;
        }

        case "submit":
        case "approve":
        case "reject":
        case "delete": {
          setInputData({ ...res.data });
          setLoading(false);
          let msg = t("common:common.msg.generalAction", {
            action: Actions[openSubmitConfirm?.action]?.result,
          });
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: msg,
            redirectPath: "/opadmin/contractrequest/list",
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
        case "getIsToAccnOpm":
          if (res?.data) {
            setIsToOpm(res?.data?.accountOpm);
            //if TO selected is opm, and the CO is opm, reload CO
            if (res?.data?.accountOpm === true && isCoOpm) {
              //then change the url for the CO selection url
              setCoOptionsUrl(CK_ACTIVE_COFF_ACCN_OPM);
            }
          }
          break;
        case "getIsCOAccnOpm":
          setIsCoOpm(res?.data?.accountOpm);
          //if CO selected is OPM, and the TO is opm, reload TO
          if (res?.data?.accountOpm === true && isToOpm) {
            setToOptionsUrl(CK_ACTIVE_TO_ACCN_OPM);
          }
          break;
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

      if (validation["contract-req-not-allowed"]) {
        setOpenWarning(true);
        setWarningMessage(validation["contract-req-not-allowed"]);
      }
    }

    // eslint-disable-next-line
  }, [urlId, res, isLoading, error, validation]);

  useEffect(() => {
    if (isToOpm) {
      setInputData({ ...inputData, crFinanceModel: "OPM_OT" });
      setEnableBankCharges(true);
    } else if (isCoOpm) {
      setInputData({ ...inputData, crFinanceModel: "OPM_OC" });
      setEnableBankCharges(true);
    } else {
      setInputData({
        ...inputData,
        crFinanceModel: "BSF",
        tmstBank: { ...inputData?.tmstBank, bankId: null },
        additionalTaxOpm: false,
        withholdTaxOpm: false,
        tckCtContractChargeByCrChargeOpm: {
          ...inputData?.tckCtContractChargeByCrChargeOpm,
          concPltfeeAmt: 0,
          concPltfeeType: null,
          concAddtaxAmt: 0,
          concAddtaxType: null,
          concWhtaxAmt: 0,
          concWhtaxType: null,
        },
      });
      setEnableBankCharges(false);
    }
  }, [isToOpm, isCoOpm]);

  /** ---------------- Event handlers ----------------- */
  const handleTabChange = (e, value) => {
    setTabIndex(value);
  };

  const handleInputChange = (e) => {
    const elName = e.target.name;
    console.log("elName:", elName);
    if (e.target.type === "checkbox") {
      setInputData({ ...inputData, [elName]: e.target.checked });
    } else {
      setInputData({
        ...inputData,
        ...deepUpdateState(inputData, elName, e.target.value),
      });

      //if TO is selected, either of the two co/ff - to is selected, if the account is opm the selection will be reloaded
      if (elName === "tcoreAccnByCrTo.accnId") {
        //check if the account selected is OPM, if it's not then no need to filter
        sendRequest(
          `/api/v1/clickargo/clictruck/administrator/ckAccountExtOpm/${e.target?.value}`,
          "getIsToAccnOpm"
        );
      }

      //if CO is selected, either of the two co/ff - to is selected, if the account is opm the selection will be reloaded
      if (elName === "tcoreAccnByCrCoFf.accnId") {
        sendRequest(
          `/api/v1/clickargo/clictruck/administrator/ckAccountExtOpm/${e.target?.value}`,
          "getIsCOAccnOpm"
        );
      }
    }

    validationFieldCheck(inputData);
  };

  const handleDateChange = (name, e) => {
    setInputData({ ...inputData, ...deepUpdateState(inputData, name, e) });
  };

  const validationFieldCheck = (data) => {
    const to = data?.tckCtContractChargeByCrChargeTo;
    const coff = data?.tckCtContractChargeByCrChargeCoFf;
    const bank = data?.tckCtContractChargeByCrChargeOpm;
    let valData = {};

    if (to.concPltfeeType === "P" && to.concPltfeeAmt > 100) {
      valData = {
        ...valData,
        "TCkCtContractChargeByCrChargeTo.concPltfeeAmt": t(
          "common:validationMsgs.maxValidation",
          { maxValue: "100%" }
        ),
      };
    }

    if (to.concAddtaxType === "P" && to.concAddtaxAmt > 100) {
      valData = {
        ...valData,
        "TCkCtContractChargeByCrChargeTo.concAddtaxAmt": t(
          "common:validationMsgs.maxValidation",
          { maxValue: "100%" }
        ),
      };
    }

    if (to.concWhtaxType === "P" && to.concWhtaxAmt > 100) {
      valData = {
        ...valData,
        "TCkCtContractChargeByCrChargeTo.concWhtaxAmt": t(
          "common:validationMsgs.maxValidation",
          { maxValue: "100%" }
        ),
      };
    }

    if (coff.concPltfeeType === "P" && coff.concPltfeeAmt > 100) {
      valData = {
        ...valData,
        "TCkCtContractChargeByCrChargeCoFf.concPltfeeAmt": t(
          "common:validationMsgs.maxValidation",
          { maxValue: "100%" }
        ),
      };
    }

    if (coff.concAddtaxType === "P" && coff.concAddtaxAmt > 100) {
      valData = {
        ...valData,
        "TCkCtContractChargeByCrChargeCoFf.concAddtaxAmt": t(
          "common:validationMsgs.maxValidation",
          { maxValue: "100%" }
        ),
      };
    }

    if (coff.concWhtaxType === "P" && coff.concWhtaxAmt > 100) {
      valData = {
        ...valData,
        "TCkCtContractChargeByCrChargeCoFf.concWhtaxAmt": t(
          "common:validationMsgs.maxValidation",
          { maxValue: "100%" }
        ),
      };
    }

    //bank
    if (bank.concPltfeeType === "P" && bank.concPltfeeAmt > 100) {
      valData = {
        ...valData,
        "TCkCtContractChargeByCrChargeOpm.concPltfeeAmt": t(
          "common:validationMsgs.maxValidation",
          { maxValue: "100%" }
        ),
      };
    }

    if (bank.concAddtaxType === "P" && bank.concAddtaxAmt > 100) {
      valData = {
        ...valData,
        "TCkCtContractChargeByCrChargeOpm.concAddtaxAmt": t(
          "common:validationMsgs.maxValidation",
          { maxValue: "100%" }
        ),
      };
    }

    if (bank.concWhtaxType === "P" && bank.concWhtaxAmt > 100) {
      valData = {
        ...valData,
        "TCkCtContractChargeByCrChargeOpm.concWhtaxAmt": t(
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
        sendRequest(
          "/api/v1/clickargo/clictruck/administrator/contractReq",
          "createData",
          "post",
          { ...inputData }
        );
        break;
      case "edit":
        sendRequest(
          "/api/v1/clickargo/clictruck/administrator/contractReq/" +
            contractReqId,
          "updateData",
          "put",
          { ...inputData, action: null }
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
      "/api/v1/clickargo/clictruck/administrator/contract/" + contractReqId,
      "deleteData",
      "delete",
      { ...inputData }
    );
  };

  const handleWarningAction = (e) => {
    setOpenWarning(false);
    setWarningMessage("");
  };

  const handleConfirmAction = (e) => {
    setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });

    //prompt remarks for approve & reject
    if (
      openSubmitConfirm?.action === "APPROVE" ||
      openSubmitConfirm?.action === "REJECT"
    ) {
      setRemarksAction({
        ...remarksAction,
        open: true,
        action: openSubmitConfirm?.action,
      });
    } else {
      sendRequest(
        `/api/v1/clickargo/clictruck/administrator/contractReq/${contractReqId}`,
        openSubmitConfirm.action.toLowerCase(),
        "PUT",
        { ...inputData }
      );
    }
  };

  const handleRemarkActionSubmit = (e) => {
    let reqBody = { ...inputData };
    if (remarksAction?.action === "APPROVE")
      reqBody = { ...inputData, crCommentApprover: remarksAction?.text };
    else if (remarksAction?.action === "REJECT")
      reqBody = { ...inputData, crComment: remarksAction?.text };

    sendRequest(
      `/api/v1/clickargo/clictruck/administrator/contractReq/${contractReqId}`,
      openSubmitConfirm.action.toLowerCase(),
      "PUT",
      reqBody
    );
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
    // const action = status === RecordStatus.INACTIVE.code ? 'deactive' : 'active';
    setOpenSubmitConfirm({ ...openSubmitConfirm, action: null, open: false });
  };

  const handleActionOnClick = (actionUp) => {
    setInputData({ ...inputData, action: actionUp });
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: actionUp,
      open: true,
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

  const handleExitOnClick = () => {
    let from = history?.location?.state?.from;
    history.push(from ? from : "/opadmin/contractrequest/list");
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

  let bcLabel =
    viewType === "edit"
      ? t("administration:contractManagement.form.edit")
      : t("administration:contractManagement.form.view");
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
            show: viewType !== "view",
            eventHandler: handleSaveOnClick,
          },
        }}
      />
    );

    if (viewType) {
      switch (viewType) {
        case "edit":
          bcLabel = t("administration:contractManagement.form.edit");
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
          bcLabel = t("administration:contractManagement.breadCrumbs.create");
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
            name: t("administration:contractManagement.breadCrumbs.list"),
            path: "/opadmin/contractrequest/list",
          },
          {
            name:
              viewType === "new"
                ? t("administration:contractManagement.breadCrumbs.create")
                : viewType === "view"
                ? t("administration:contractManagement.breadCrumbs.view")
                : viewType === "edit"
                ? t("administration:contractManagement.breadCrumbs.edit")
                : t("administration:contractManagement.breadCrumbs.edit"),
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
                    <ContractReqDetails
                      viewType={viewType}
                      handleInputChange={handleInputChange}
                      handleDateChange={handleDateChange}
                      isDisabled={isDisabled}
                      inputData={inputData}
                      errors={validationErrors}
                      coOptionsUrl={coOptionsUrl}
                      toOptionsUrl={toOptionsUrl}
                      enableBankCharges={enableBankCharges}
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
                    <C1AuditTab filterId={contractReqId}></C1AuditTab>
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
              {t("common:popup.ok")}
            </Button>
          </div>
        </div>
      </Dialog>

      {/* For Reject remarks */}
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

export default withErrorHandler(ContractReqFormDetails);
