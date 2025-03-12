import { makeStyles } from "@material-ui/core";
import { Divider, Grid, InputAdornment, Paper, Tabs } from "@material-ui/core";
import { Backdrop } from "@material-ui/core";
import CircularProgress from "@material-ui/core/CircularProgress";
import { CloudDownloadOutlined, DeleteOutlined } from "@material-ui/icons";
import DescriptionOutlinedIcon from "@material-ui/icons/DescriptionOutlined";
import GetAppOutlinedIcon from "@material-ui/icons/GetAppOutlined";
import LibraryAddCheckOutlinedIcon from "@material-ui/icons/LibraryAddCheckOutlined";
import NearMeOutlinedIcon from "@material-ui/icons/NearMeOutlined";
import PaymentOutlinedIcon from "@material-ui/icons/PaymentOutlined";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DataTable from "app/c1component/C1DataTable";
import C1DateField from "app/c1component/C1DateField";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1IconButton from "app/c1component/C1IconButton";
import C1InputField from "app/c1component/C1InputField";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1PopUp from "app/c1component/C1PopUp";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";
import C1Warning from "app/c1component/C1Warning";
import useHttp from "app/c1hooks/http";
import { AccountTypes, JobStates, Roles } from "app/c1utils/const";
import { getFormActionButton } from "app/c1utils/formActionUtils";
import { iconStyles } from "app/c1utils/styles";
import { tabScroll } from "app/c1utils/styles";
import { downloadFile, getValue, previewPDF } from "app/c1utils/utility";
import NumFormat from "app/clictruckcomponent/NumFormat";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import TabLabel from "app/portedicomponent/TabLabel";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";
import { ConfirmationDialog, MatxLoading } from "matx";
import { getQueryParam } from "utils";

import PaymentTxnLogs from "./PaymentTxnLogs";

const useStyles = makeStyles((theme) => ({
  backdrop: {
    zIndex: theme.zIndex.drawer + 1,
    color: "#fff",
  },
  amountCell: {
    justifyContent: "center",
    textAlign: "right",
    display: "flex",
    flex: 1,
  },
}));

/**
 * @description screen for displaying the payment details during confirmation and history. If the txnId is present that is history. Otherwise, it's payment confirmation.
 * And the details are formed from backend in memory until it is confirmed by the user.
 */
const PaymentDetails = () => {
  const { viewType, txnId } = useParams();
  const { t } = useTranslation(["buttons", "payments", "common"]);
  const classes = iconStyles();
  const bdClasses = useStyles();
  const history = useHistory();
  const queryParam = getQueryParam();
  const { user } = useAuth();
  const jobIds = queryParam["jobIds"];

  let forToAccount = history?.location?.state?.for;
  //check if current user is service provider
  let isGli =
    user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_SP.code;
  let isCO =
    user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_CO.code;
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
  const [loading, setLoading] = useState(true);
  const [invLoadDlOpen, setInvLoadDlOpen] = useState(false);
  const [controls, setControls] = useState([]);
  const [selectedJobsForPayment, setSelectedJobsForPayment] = useState();
  const [initialButtons, setinitialButtons] = useState({
    submitOnClick: {
      show: txnId ? false : true,
      eventHandler: () => handleSubmitPaymentClick(),
    },
    back: { show: true, eventHandler: () => handleExitOnClick() },
  });

  const [rejectRemarks, setRejectRemarks] = useState({
    open: false,
    msg: null,
  });
  const [warningMessage, setWarningMessage] = useState({
    open: false,
    msg: "",
    hlMsg: "",
    subMsg: "",
  });
  const [confirmation, setConfirmation] = useState({
    title: null,
    action: null,
    open: false,
    msg: null,
    onYesClick: null,
  });
  const [paymentDetails, setPaymentDetails] = useState({});
  const [tabIndex, setTabIndex] = useState(0);

  const defaultSnackbarValue = {
    success: false,
    successMsg: "",
    error: false,
    errorMsg: "",
    redirectPath: "",
  };
  const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);
  /** ------------------- Update states ----------------- */
  useEffect(() => {
    setLoading(true);
    if (!txnId) {
      let reqBody = { jobIds: jobIds };
      if (forToAccount) {
        reqBody = { jobIds: jobIds, toAccnId: forToAccount };
      }
      sendRequest(
        `/api/v1/clickargo/clictruck/payment/invoice/details`,
        "getInvoiceForConfirmation",
        "post",
        reqBody
      );
    } else {
      sendRequest(
        `/api/v1/clickargo/clictruck/payment/txn/${txnId}`,
        "getTxnDetails",
        "get"
      );
    }
  }, []);

  useEffect(() => {
    if (!isLoading && !error && res && !validation) {
      setLoading(isLoading);
      switch (urlId) {
        case "getInvoice": {
          setInvLoadDlOpen(false);
          if (res?.data) {
            viewFile(res.data);
          }
          break;
        }
        case "getInvoiceForConfirmation": {
          if (res?.data) {
            setSelectedJobsForPayment({ ...res?.data });
          }
          break;
        }
        case "getTxnDetails": {
          if (res?.data) {
            console.log("get some buttons", res?.data);
            setSelectedJobsForPayment({ ...res?.data?.jobPaymentDetails });
            setPaymentDetails(res?.data);
            if (
              user?.authorities?.some((el) =>
                [
                  Roles.FINANCE_VERIFIER.code,
                  Roles.FINANCE_APPROVER.code,
                ].includes(el.authority)
              )
            ) {
              let paymentState = "NEW";
              if (res?.data?.ptxPaymentState === "VERIFIED")
                paymentState = JobStates.VER.code;
              else if (res?.data?.ptxPaymentState === "APPROVED")
                paymentState = JobStates.APP.code;
              else if (res?.data?.ptxPaymentState === "REJECTED")
                paymentState = JobStates.REJ.code;
              else if (res?.data?.ptxPaymentState === "FAIL")
                paymentState = JobStates.FAIL.code;
              else {
                console.log("else?", res?.data?.ptxPaymentState);
                paymentState = res?.data?.ptxPaymentState;
              }

              if (paymentState !== "CANCELLED") {
                const reqBody = {
                  entityType: "JOB_TRUCK_OUT_PAY",
                  entityState: paymentState,
                  page: "VIEW",
                };
                sendRequest(
                  "/api/v1/clickargo/controls/",
                  "fetchControls",
                  "POST",
                  reqBody
                );
              }
            }
          }
          break;
        }
        case "paySelectedJobs": {
          if (res?.data) {
            if (isGli) {
              history.push("/applications/services/gli/dashboard");
            } else {
              history.push("/applications/services/job/coff/truck");
            }
          }
          break;
        }
        case "fetchControls": {
          setControls(res?.data);
          break;
        }
        case "verify_bill":
        case "approve_bill":
        case "reject_bill": {
          history.push("/applications/services/gli/dashboard");
          break;
        }
        case "getFile": {
          setInvLoadDlOpen(false);
          viewFile({ filename: "File.pdf", data: res?.data });
          break;
        }
        case "cancelpayment": {
          setSnackBarOptions({
            ...snackBarOptions,
            success: true,
            successMsg: t("common:msg.cancelSuccess"),
            redirectPath: history.goBack(),
          });
          break;
        }
        case "downloadFile": {
          downloadFile(res?.data?.filename, res?.data?.data);
          break;
        }
        default:
          break;
      }
    }

    if (validation) {
      setLoading(false);
      setConfirmation({
        ...confirmation,
        open: false,
      });
      let keyList = Object.keys(validation);
      if (keyList.length > 0) {
        for (let key of keyList) {
          if (key.includes("processing-txn")) {
            setWarningMessage({
              open: true,
              msg: validation[key],
              hlMsg: null,
              subMsg: null,
            });
          } else if (key.includes("job-in-pay-processing")) {
            setWarningMessage({
              open: true,
              msg: t("payments:errors.jobInPayProcessing", {
                jobIds: validation[key],
              }),
              hlMsg: null,
              subMsg: null,
            });
          } else if (key.includes("existing-new-txn")) {
            setWarningMessage({
              open: true,
              msg: t("payments:errors.existingNewTxn", {
                txnId: validation[key],
              }),
              hlMsg: null,
              subMsg: null,
            });
          } else if (key.includes("job-out-not-paid-yet")) {
            setWarningMessage({
              open: true,
              msg: t("payments:errors.jobNotOutPayYet", {
                jobIds: validation[key],
              }),
              hlMsg: null,
              subMsg: null,
            });
          } else {
            setWarningMessage({
              open: true,
              msg: validation[key],
              hlMsg: null,
              subMsg: null,
            });
          }
        }
      }
    }
  }, [urlId, isLoading, isFormSubmission, res, error]);

  const tabList = [
    { text: "Payment", icon: <DescriptionOutlinedIcon /> },
    { text: "Audit", icon: <DescriptionOutlinedIcon /> },
  ];

  /** ---------------- Event handlers ----------------- */

  const handleTabChange = (e, value) => {
    setTabIndex(value);
  };

  const handleExitOnClick = () => {
    let prevUrl = history?.location?.state?.from;
    if (prevUrl) history.push(prevUrl);
    else history.push("/payments/active/list");
  };

  const eventHandler = (action) => {
    console.log("evenrhandler", action);
    if (action.toLowerCase() === "pay" || action.toLowerCase() === "submit") {
      handleSubmitPaymentClick();
    } else if (
      action.toLowerCase() === "verify_bill" ||
      action.toLowerCase() === "approve_bill"
    ) {
      setConfirmation({
        ...confirmation,
        open: true,
        title: t("payments:paymentDetails.label.confirmAction"),
        msg: t("payments:paymentDetails.msg.confirmAction", { action: action }),
        onYesClick: () => confirmPaymentAction(action),
      });
    } else if (action.toLowerCase() === "reject_bill") {
      handleSubmitReject();
    } else if (action.toLowerCase() === "exit") {
      handleExitOnClick();
    }
  };

  const handleSubmitPaymentClick = () => {
    setConfirmation({
      ...confirmation,
      open: true,
      title: t("payments:paymentDetails.label.confirmPay"),
      msg: t("payments:paymentDetails.msg.confirmPay", {
        selectedRowIds: jobIds.split(",").join(", "),
      }),
      onYesClick: () => confirmPaymentHandler(),
    });
  };

  const confirmPaymentHandler = () => {
    if (isGli) {
      console.log("for To acount: ", isGli);
      sendRequest(
        `/api/v1/clickargo/clictruck/payment/pay/out`,
        "paySelectedJobs",
        "post",
        { jobIds: jobIds.split(","), toAccnId: forToAccount }
      );
    } else {
      sendRequest(
        `/api/v1/clickargo/clictruck/payment/pay/in`,
        "paySelectedJobs",
        "post",
        { jobIds: jobIds.split(",") }
      );
    }

    setLoading(true);
  };

  const handleSubmitReject = () => {
    setRejectRemarks({ ...rejectRemarks, open: true });
  };

  const confirmPaymentAction = (action) => {
    setConfirmation({
      ...confirmation,
      open: false,
    });

    setLoading(true);
    sendRequest(
      `/api/v1/clickargo/clictruck/payment/txn/${txnId}`,
      action.toLowerCase(),
      "put",
      { action: action }
    );
  };

  const handleViewFile = (id, invType) => {
    setInvLoadDlOpen(true);
    if (txnId) {
      sendRequest(
        `/api/v1/clickargo/clictruck/attach/ctPayment/${id}`,
        "getFile"
      );
    } else {
      if (invType === "PLATFORM_FEE") {
        sendRequest(
          `/api/v1/clickargo/clictruck/attach/platformInvoice/${id}`,
          "getFile"
        );
      } else if (invType === "DEBIT_NOTE") {
        sendRequest(
          `/api/v1/clickargo/clictruck/attach/debitNote/${id}`,
          "getFile"
        );
      }
    }
    //if there is txnId the doId is set to the dopRef, otherwise it will be the doId from the job selected initially
    // let reqBod = txnId ? { invId: doId, isTemp: false } : { doId: doId, invType: invType, isTemp: true, parentJobId: parentJobId, jobType: jobType }
    // sendRequest(`/api/v1/clickargo/clictruck/attach/`, "getInvoice", "post", reqBod);
  };

  const viewFile = (data) => {
    previewPDF(data?.filename, data?.data);
  };

  const handleWarningAction = (e) => {
    setWarningMessage({ open: false, msg: "", hlMsg: "", subMsg: "" });
  };

  const confirmCancel = () => {
    setConfirmation({ ...confirmation, open: false });
    sendRequest(
      `/api/v1/clickargo/clictruck/payment/txn/${
        isGli ? "OUTBOUND" : "INBOUND"
      }/${paymentDetails?.ptxId}`,
      "cancelpayment",
      "delete"
    );
  };

  const handleSetCancelClick = () => {
    setConfirmation({
      ...confirmation,
      open: true,
      title: t("payments:paymentDetails.label.confirmCancel"),
      msg: t("payments:paymentDetails.msg.confirmAction", { action: "CANCEL" }),
      onYesClick: confirmCancel,
    });
  };

  let formButtons;
  if (!loading) {
    formButtons = (
      <>
        {viewType != "view" &&
          (paymentDetails?.ptxPaymentState === "PAYING" ||
            paymentDetails?.ptxPaymentState === "NEW") && (
            <C1LabeledIconButton
              tooltip={t("buttons:cancel")}
              label={t("buttons:cancel")}
              action={handleSetCancelClick}
            >
              <DeleteOutlined color="primary" />
            </C1LabeledIconButton>
          )}
        <C1FormButtons
          options={getFormActionButton(initialButtons, controls, eventHandler)}
        />
      </>
    );
  }

  const invItemsCols = [
    {
      name: "seq",
      label: t("payments:paymentDetails.list.table.headers.slNo"),
      options: {
        sort: true,
        filter: false,
      },
    },
    {
      name: "invDesc",
      label: t("payments:paymentDetails.list.table.headers.itemDescription"),
      options: {
        sort: true,
        filter: false,
      },
    },
    {
      name: "invNo",
      label: t("payments:paymentDetails.list.table.headers.invoiceNo"),
      options: {
        sort: true,
        filter: false,
      },
    },
    {
      name: "qty",
      label: t("payments:paymentDetails.list.table.headers.quantity"),
      options: {
        sort: true,
        filter: false,
      },
    },
    {
      name: "invCurrency",
      label: t("payments:paymentDetails.list.table.headers.currency"),
      options: {
        sort: true,
        filter: false,
      },
    },
    {
      name: "invAmt",
      label: t("payments:paymentDetails.list.table.headers.amount"),
      options: {
        sort: true,
        filter: false,
        customBodyRender: (value, tableMeta, updateValue) => {
          let isPf = tableMeta.rowData[1]?.includes("Platform Fee");
          if (!value) return "-";
          let displayStr = "";
          if (tableMeta.rowData[4]) {
            if (tableMeta.rowData[4] === "IDR")
              return (
                <div className={bdClasses.amountCell}>
                  <p>
                    {isPf && !isCO && "("}
                    {
                      (displayStr +=
                        value.toLocaleString("id-ID", {
                          maximumFractionDigits: 0,
                          style: "currency",
                          currency: "IDR",
                        }) + "\n")
                    }
                    {isPf && !isCO && ")"}
                  </p>
                </div>
              );
            else if (tableMeta.rowData[4] === "USD")
              return (
                <div className={bdClasses.amountCell}>
                  <p>
                    {
                      (displayStr +=
                        value.toLocaleString("en-US", {
                          maximumFractionDigits: 2,
                          style: "currency",
                          currency: "USD",
                        }) + "\n")
                    }
                  </p>
                </div>
              );
            else displayStr += value + " " + tableMeta.rowData[4] + "\n";
          }
          return displayStr;
        },
      },
    },
    {
      name: "invType",
      label: "",
      options: {
        sort: false,
        filter: false,
        display: "excluded",
      },
    },
    {
      name: "id",
      label: "",
      options: {
        sort: false,
        display: "excluded",
        filter: false,
      },
    },
    {
      name: "parentJobId",
      label: "",
      options: {
        sort: false,
        filter: false,
        display: "excluded",
      },
    },
    {
      name: "fileLocation",
      label: "",
      options: {
        sort: false,
        filter: false,
        display: "excluded",
      },
    },
    {
      name: "",
      label: t("payments:paymentDetails.list.table.headers.invoices"),
      options: {
        sort: false,
        filter: false,
        customHeadLabelRender: (columnMeta) => {
          return <div style={{ textAlign: "center" }}>{columnMeta.label}</div>;
        },
        customBodyRender: (noValue, tableMeta, updateValue) => {
          const id = tableMeta.rowData[7];
          const invType = tableMeta.rowData[6];
          const fileLoc = tableMeta.rowData[9];
          return (
            <Grid container alignItems={"center"} justifyContent={"center"}>
              {/* <Tooltip title={t("common:tooltip.viewDtls")}>
                            <span>
                                <IconButton onClick={() => handleViewFile(id, invType)} size="small"
                                    classes={{ label: classes.iconButton }} color="primary" disabled={!fileLoc}>
                                    <GetAppOutlinedIcon />
                                    <div className={classes.iconText}>{t("buttons:details").toUpperCase()}</div>
                                </IconButton>
                            </span>
                        </Tooltip> */}
              <C1LabeledIconButton
                disabled={!fileLoc ? true : false}
                color={!fileLoc ? "secondary" : "primary"}
                tooltip={!fileLoc ? "" : t("common:tooltip.viewDtls")}
                label={t("buttons:details")}
                action={() => handleViewFile(id, invType)}
              >
                <GetAppOutlinedIcon />
              </C1LabeledIconButton>
            </Grid>
          );
        },
      },
    },
  ];

  const handleActionDownload = () => {
    setLoading(true);
    sendRequest(
      `/api/v1/clickargo/clictruck/attach/byParam/ctPayment/${paymentDetails?.ptxId}`,
      "downloadFile",
      "get"
    );
  };

  const actionDownload = txnId ? (
    <C1LabeledIconButton
      tooltip={t("buttons:download")}
      label={t("buttons:download")}
      action={handleActionDownload}
    >
      <CloudDownloadOutlined />
    </C1LabeledIconButton>
  ) : (
    false
  );

  return loading ? (
    <MatxLoading />
  ) : (
    <React.Fragment>
      <C1FormDetailsPanel
        breadcrumbs={[{ name: t("payments:paymentDetails.label.paymentDet") }]}
        title={t("payments:paymentDetails.label.paymentDet")}
        formButtons={formButtons}
        initialValues={{}}
        snackBarOptions={snackBarOptions}
        isLoading={loading}
      >
        {(props) => (
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
                        className="capitalize"
                        value={ind}
                        disabled={item.disabled}
                        label={
                          <TabLabel
                            viewType={"view"}
                            tab={item}
                            errors={undefined}
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
                  guideId="clictruck.payment.details"
                  title="empty"
                  guideAlign="right"
                  open={false}
                >
                  <Grid container spacing={3}>
                    <C1CategoryBlock
                      xs={12}
                      icon={<DescriptionOutlinedIcon />}
                      title={t("payments:paymentDetails.label.genDetails")}
                    >
                      <Grid
                        xs={12}
                        container
                        item
                        spacing={3}
                        alignItems="center"
                      >
                        <Grid item xs={4}>
                          <C1InputField
                            label={t(
                              "payments:paymentDetails.label.paymentRef"
                            )}
                            name={"paymentRef"}
                            disabled
                            value={selectedJobsForPayment?.paymentRef || "-"}
                          />
                        </Grid>
                        <Grid item xs={4}>
                          <C1DateField
                            label={t(
                              "payments:paymentDetails.label.billingDate"
                            )}
                            name={"billingDate"}
                            disableHighlightToday={true}
                            disabled={true}
                            value={selectedJobsForPayment?.billingDate || ""}
                          />
                        </Grid>
                        <Grid item xs={4}>
                          <C1DateField
                            label={t("payments:paymentDetails.label.paidDate")}
                            name={"paidDate"}
                            disableHighlightToday={true}
                            disabled={true}
                            value={selectedJobsForPayment?.paidDate || ""}
                          />
                        </Grid>
                      </Grid>
                    </C1CategoryBlock>
                    <C1CategoryBlock
                      xs={12}
                      icon={<LibraryAddCheckOutlinedIcon />}
                      title={t("payments:paymentDetails.label.listInvoices")}
                      actionEl={actionDownload}
                    >
                      <Grid
                        xs={12}
                        container
                        item
                        spacing={3}
                        alignItems="center"
                      >
                        <Grid item xs={12}>
                          <Paper>
                            <Divider className="mb-6" />
                            {
                              <C1TabInfoContainer guideId="clicdo.job-payment.job-listing.table">
                                <C1DataTable
                                  columns={invItemsCols}
                                  dbName={{
                                    list: selectedJobsForPayment?.invoiceDetails,
                                  }}
                                  defaultOrder="slNo"
                                  defaultOrderDirection={"asc"}
                                  //       filterBy={filterBy}
                                  isServer={false}
                                  isShowViewColumns={true}
                                  isShowDownload={false}
                                  isShowPrint={false}
                                  //   isShowFilter={true}
                                  isRefresh={false}
                                  isRowSelectable={false}
                                  isShowToolbar={false}
                                  isShowDownloadData={false}
                                  isShowPagination={true}
                                />
                              </C1TabInfoContainer>
                            }
                          </Paper>
                        </Grid>
                      </Grid>
                    </C1CategoryBlock>
                    <Grid container item spacing={3} alignItems="center">
                      <Grid item xs={8} />
                      <C1CategoryBlock
                        xs={4}
                        icon={<PaymentOutlinedIcon />}
                        title={t("payments:paymentDetails.label.total")}
                      >
                        <Grid
                          xs={12}
                          container
                          item
                          spacing={2}
                          alignItems="center"
                        >
                          <Grid item xs={isGli && !txnId ? 12 : 6}>
                            <C1InputField
                              label={t(
                                "payments:paymentDetails.label.fields.totalIdr"
                              )}
                              name="totalIdr"
                              disabled
                              onChange={() => {}}
                              inputProps={{ style: { textAlign: "right" } }}
                              InputProps={{
                                inputComponent: NumFormat,
                                startAdornment: (
                                  <InputAdornment
                                    position="start"
                                    style={{ paddingRight: "8px" }}
                                  >
                                    Rp
                                  </InputAdornment>
                                ),
                              }}
                              value={selectedJobsForPayment?.totalIdr}
                            />
                          </Grid>
                          {isGli && !txnId ? null : (
                            <Grid item xs={6}>
                              <C1InputField
                                label={
                                  isGli
                                    ? t(
                                        "payments:paymentDetails.label.fields.viaBiFast"
                                      )
                                    : t(
                                        "payments:paymentDetails.label.fields.viaIdrVa"
                                      )
                                }
                                name="IdrVANo"
                                disabled
                                onChange={() => {}}
                                value={selectedJobsForPayment?.vaIdr}
                              />
                            </Grid>
                          )}
                        </Grid>
                        {(isGli && !txnId) || !isGli ? null : (
                          <Grid
                            xs={12}
                            container
                            item
                            spacing={2}
                            alignItems="center"
                          >
                            <Grid item xs={12}>
                              <C1InputField
                                label={t(
                                  "payments:paymentDetails.label.fields.bankAccnName"
                                )}
                                name="bankAccnName"
                                disabled
                                onChange={() => {}}
                                value={selectedJobsForPayment?.bankAccnName}
                              />
                            </Grid>
                          </Grid>
                        )}
                      </C1CategoryBlock>
                    </Grid>
                  </Grid>
                </C1TabInfoContainer>
              )}
              {tabIndex === 1 && (
                <C1TabInfoContainer
                  guideId="clictruck.payment.audit"
                  title="empty"
                  guideAlign="right"
                  open={false}
                >
                  <PaymentTxnLogs filterId={txnId} />
                </C1TabInfoContainer>
              )}
            </Paper>
          </Grid>
        )}
      </C1FormDetailsPanel>
      {/** Confirmation for verify button */}

      <ConfirmationDialog
        open={confirmation?.open}
        title={confirmation?.title}
        text={confirmation?.msg}
        onYesClick={confirmation?.onYesClick}
        onConfirmDialogClose={() =>
          setConfirmation({ ...confirmation, open: false, msg: null })
        }
      />

      {/* For downloading of invoice */}
      <Backdrop open={invLoadDlOpen} className={bdClasses.backdrop}>
        {" "}
        <CircularProgress color="inherit" />
      </Backdrop>

      <C1Warning
        warningMessage={warningMessage}
        handleWarningAction={handleWarningAction}
      />

      <C1PopUp
        maxWidth={"md"}
        title={`Rejection Remarks`}
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
                return confirmPaymentAction("reject_bill");
              }}
            ></NearMeOutlinedIcon>
          </C1IconButton>
        }
      >
        <C1InputField
          required
          name="rejectRemarks.msg"
          value={getValue(rejectRemarks?.msg)}
          onChange={(e) =>
            setRejectRemarks({ ...rejectRemarks, msg: e?.target?.value })
          }
        />
      </C1PopUp>
    </React.Fragment>
  );
};

export default withErrorHandler(PaymentDetails);
