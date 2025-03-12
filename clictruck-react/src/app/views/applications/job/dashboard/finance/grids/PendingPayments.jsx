import {
  Backdrop,
  Button,
  Checkbox,
  CircularProgress,
  Grid,
  Popover,
  Typography,
} from "@material-ui/core";
import Snackbar from "@material-ui/core/Snackbar";
import { makeStyles } from "@material-ui/core/styles";
import { VisibilityOutlined } from "@material-ui/icons";
import GetAppOutlinedIcon from "@material-ui/icons/GetAppOutlined";
import PaymentOutlinedIcon from "@material-ui/icons/PaymentOutlined";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useDispatch } from "react-redux";
import { useParams } from "react-router-dom/cjs/react-router-dom";
import DataTable from "app/atomics/organisms/DataTable";
import C1Alert from "app/c1component/C1Alert";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1PopUp from "app/c1component/C1PopUp";
import C1Warning from "app/c1component/C1Warning";
import useHttp from "app/c1hooks/http";
import { FINANCING_OPTIONS, JobStates, PaymentState } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import {
  customFilterDateDisplay,
  customNumFieldDisplay,
  formatDate,
  previewPDF,
} from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import history from "history.js";
import { MatxLoading } from "matx";
import ConfirmationDialog from "matx/components/ConfirmationDialog";
import { useHistory } from "react-router-dom/cjs/react-router-dom.min";

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
 * @description This is for listing the approved invoices and pending for payments. User can select and Pay multiple jobs.
 */
const PendingPayments = ({
  roleId,
  filterStatus,
  onFilterChipClose,
  onFilterChange,
}) => {
  const bdClasses = useStyles();

  const { t } = useTranslation([
    "buttons",
    "listing",
    "common",
    "status",
    "payments",
  ]);

  const {
    isLoading,
    isFormSubmission,
    validation,
    res,
    error,
    urlId,
    sendRequest,
  } = useHttp();
  const { viewType } = useParams();

  // eslint-disable-next-line
  const [confirm, setConfirm] = useState({ id: null });
  // eslint-disable-next-line
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  const [isRefresh, setRefresh] = useState(false);

  // eslint-disable-next-line
  const [success, setSuccess] = useState(false);
  const [openActionConfirm, setOpenActionConfirm] = useState({
    action: null,
    open: false,
  });
  const [warningMessage, setWarningMessage] = useState({
    open: false,
    msg: "",
  });
  const dispatch = useDispatch();

  const historyPage = useHistory();
  const [showHistory, setShowHistory] = useState(false);
  const [filterBy, setFilterBy] = useState([
    { attribute: "history", value: "default" },
  ]);
  const [showEmptyObjectPopUp, setShowEmptyObjectPopUp] = useState(false);
  const [selectedRowIds, setSelectedRowIds] = useState([]);
  let rowData = [];
  // state for open popover
  const [anchorEl, setAnchorEl] = React.useState(null);
  const openPopover = Boolean(anchorEl);
  const [staticJobId, setStaticJobId] = useState(null);

  useEffect(() => {
    if (!isLoading && !error && res && !validation) {
      if (urlId === "downloadFile") {
        if (res.data.data != null) {
          const fileName = res?.data?.filename;

          previewPDF(
            fileName != null ? fileName : "preview.pdf",
            res?.data?.data
          );
        } else {
          setShowEmptyObjectPopUp(true);
        }
      } else if (urlId === "getInvoiceForConfirmation") {
        if (res?.data) {
          history.push(
            `/applications/finance/payments/details?jobIds=${selectedRowIds.join(
              ","
            )}`,
            { from: "/applications/services/job/coff/truck" }
          );
        }
      }
    }

    if (validation) {
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

  const hasInvalidDueDates = rowData.some((data) =>
    formatDate(data[8], false)?.includes("2100")
  );

  const columns = [
    //0
    {
      name: "checkbox",
      label: "",
      options: {
        sort: false,
        filter: false,
        display: !showHistory,
        viewColumns: false,
        customHeadLabelRender: (columnMeta, handleToggleColumn, sortOrder) => {
          console.log("hasInvalidDueDates", hasInvalidDueDates);
          return (
            <Checkbox
              disableRipple={true}
              disabled={showHistory}
              checked={
                selectedRowIds.length > 0 &&
                selectedRowIds.length === rowData.length
              }
              onChange={({ target: { checked } }) => {
                return checked
                  ? setSelectedRowIds(rowData)
                  : setSelectedRowIds([]);
              }}
            />
          );
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          const id = tableMeta.rowData[1];
          const status = tableMeta.rowData[9];

          const isDtDueInvalid = formatDate(
            tableMeta.rowData[8],
            false
          )?.includes("2100");

          const appBill = status === JobStates.APP_BILL.code;

          rowData = tableMeta.tableData
            .filter((data) => {
              return (
                data[9] === JobStates.APP_BILL.code &&
                !formatDate(data[8], false)?.includes("2100")
              );
            })
            .map((data) => data[1]);

          return (
            <Checkbox
              disableRipple={true}
              disabled={appBill && !isDtDueInvalid ? false : true}
              checked={
                selectedRowIds.includes(id) &&
                status === JobStates.APP_BILL.code &&
                !isDtDueInvalid
              }
              onChange={({ target: { checked } }) => {
                return checked && !isDtDueInvalid
                  ? setSelectedRowIds(
                      selectedRowIds.filter((rowId) => rowId !== id).concat(id)
                    )
                  : setSelectedRowIds(
                      selectedRowIds.filter((rowId) => rowId !== id)
                    );
              }}
            />
          );
        },
      },
    },
    //1
    {
      name: "jobId",
      label: t("listing:finance.invId"),
    },
    //2
    {
      name: "jobShipmentRef",
      label: t("listing:trucklist.shipref"),
    },
    //3
    {
      name: "invoiceFromAccn.accnName",
      label: t("listing:finance.invFrom"),
    },
    //4
    {
      name: "jobNoTrips",
      label: t("listing:finance.noOfTrips"),
      options: {
        sort: true,
        filter: true,
        filterType: "custom",
        customFilterListOptions: {
          render: (v) => v.map((l) => l),
          update: (filterList, filterPos, index) => {
            filterList[index].splice(filterPos, 1);
            return filterList;
          },
        },
        filterOptions: {
          display: customNumFieldDisplay,
        },
      },
    },
    //5
    {
      name: "jobTotalCharge",
      label: t("listing:finance.charges"),
      options: {
        sort: true,
        filter: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          return (
            <div className={bdClasses.amountCell}>
              <p>
                {" "}
                {value
                  ? value.toLocaleString("id-ID", {
                      maximumFractionDigits: 0,
                      style: "currency",
                      currency: "IDR",
                    })
                  : "-"}
              </p>
            </div>
          );
        },
        filterType: "custom",
        customFilterListOptions: {
          render: (v) => v.map((l) => l),
          update: (filterList, filterPos, index) => {
            filterList[index].splice(filterPos, 1);
            return filterList;
          },
        },
        filterOptions: {
          display: customNumFieldDisplay,
        },
      },
    },
    //6
    {
      name: "jobTotalReimbursements",
      label: t("listing:finance.reimbursement"),
      options: {
        sort: true,
        filter: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          return (
            <div className={bdClasses.amountCell}>
              <p>
                {" "}
                {value
                  ? value.toLocaleString("id-ID", {
                      maximumFractionDigits: 0,
                      style: "currency",
                      currency: "IDR",
                    })
                  : "-"}
              </p>
            </div>
          );
        },
        filterType: "custom",
        customFilterListOptions: {
          render: (v) => v.map((l) => l),
          update: (filterList, filterPos, index) => {
            filterList[index].splice(filterPos, 1);
            return filterList;
          },
        },
        filterOptions: {
          display: customNumFieldDisplay,
        },
      },
    },
    //7
    {
      name: "billingDate",
      label: t("listing:finance.billingDate"),
      options: {
        sort: true,
        filter: true,
        filterType: "custom",
        customBodyRender: (value, tableMeta, updateValue) => {
          return formatDate(value, true);
        },
        filterOptions: {
          display: customFilterDateDisplay,
        },
      },
    },
    //8
    {
      name: "paymentDueDate",
      label: t("listing:verified.paymentDueDate"),
      options: {
        sort: true,
        filter: true,
        filterType: "custom",
        customBodyRender: (value, tableMeta, updateValue) => {
          let formattedDate = formatDate(value, true);
          return formattedDate.includes("2100") ? "-" : formattedDate;
        },
        filterOptions: {
          display: customFilterDateDisplay,
        },
      },
    },
    //9
    {
      name: "tckJob.tckMstJobState.jbstId",
      label: t("listing:finance.status"),
      options: {
        filter: true,
        filterType: "dropdown",
        filterOptions: {
          names: Object.keys(JobStates),
          renderValue: (v) => {
            return JobStates[v].desc;
          },
        },
        customFilterListOptions: {
          render: (v) => {
            return JobStates[v].desc;
          },
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          return getStatusDesc(value);
        },
      },
    },
    //10
    {
      name: "acknowledgedDate",
      label: t("listing:finance.approvedDate"),
      options: {
        sort: true,
        filter: true,
        filterType: "custom",
        customBodyRender: (value, tableMeta, updateValue) => {
          return formatDate(value, true);
        },
        filterOptions: {
          display: customFilterDateDisplay,
        },
      },
    },
    {
      name: "acknowledgedBy",
      label: t("listing:finance.approvedBy"),
      options: {
        sort: true,
        filter: true,
      },
    },
    {
      name: "jobInPaymentState",
      label: t("listing:finance.paymentState"),
      options: {
        filter: true,
        filterType: "dropdown",
        filterOptions: {
          names: Object.keys(PaymentState),
          renderValue: (v) => {
            return PaymentState[v].desc;
          },
        },
        customFilterListOptions: {
          render: (v) => {
            return PaymentState[v].desc;
          },
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          return getStatusDesc(value);
        },
      },
    },
    {
      name: "",
      label: t("listing:common.action"),
      options: {
        filter: false,
        sort: false,
        display: true,
        viewColumns: false,
        setCellHeaderProps: () => {
          return { style: { justifyContent: "center", paddingLeft: "3%" } };
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          const id = tableMeta.rowData[1];
          return (
            <Grid
              container
              direction="row"
              justifyContent="flex-start"
              alignItems="center"
              style={{ marginRight: "10px", minWidth: "110px" }}
            >
              <Popover
                open={openPopover}
                anchorEl={anchorEl}
                onClose={handleClose}
                anchorOrigin={{
                  vertical: "bottom",
                  horizontal: "left",
                }}
              >
                <Grid container direction={"column"} alignItems={"flex-start"}>
                  <Grid item>
                    <Button
                      style={{ textTransform: "none" }}
                      onClick={() =>
                        downloadFileHandler("toInvoice", staticJobId)
                      }
                    >
                      <Typography>Job Invoice</Typography>
                    </Button>
                  </Grid>
                  <Grid item>
                    <Button
                      style={{ textTransform: "none" }}
                      onClick={() =>
                        downloadFileHandler("platformInvoice", staticJobId)
                      }
                    >
                      <Typography>{t("listing:invoices.invPf")}</Typography>
                    </Button>
                  </Grid>
                  <Grid item>
                    <Button
                      style={{ textTransform: "none" }}
                      onClick={() =>
                        downloadFileHandler("debitNote", staticJobId)
                      }
                    >
                      <Typography>{t("listing:invoices.invDn")}</Typography>
                    </Button>
                  </Grid>
                  <Grid item>
                    <Button
                      style={{ textTransform: "none" }}
                      onClick={() =>
                        downloadFileHandler("taxinvoice", staticJobId)
                      }
                    >
                      <Typography>{t("listing:invoices.invTax")}</Typography>
                    </Button>
                  </Grid>
                </Grid>
              </Popover>
              <Grid container item justifyContent="center" spacing={4}>
                <Grid item sm={6} xs={6}>
                  <C1LabeledIconButton
                    tooltip={t("buttons:download")}
                    label={t("buttons:download")}
                    action={(e) => handleOpenDownloadToggle(e, id)}
                  >
                    <GetAppOutlinedIcon />
                  </C1LabeledIconButton>
                </Grid>
                <Grid item xs={3}>
                  <C1LabeledIconButton
                    tooltip={t("buttons:view")}
                    label={t("buttons:view")}
                    action={() =>
                      history.push({
                        pathname: `/applications/services/job/truck/view`,
                        state: {
                          from: "/applications/services/job/coff/truck",
                          jobId: id,
                        },
                      })
                    }
                  >
                    <VisibilityOutlined />
                  </C1LabeledIconButton>
                </Grid>
              </Grid>
            </Grid>
          );
        },
      },
    },
    {
      name: "jobFinanceOpt",
      options: {
        filter: false,
        sort: false,
        display: "excluded",
      },
    },
  ];

  const handleOpenDownloadToggle = (event, id) => {
    setStaticJobId(id);
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleActionHandler = (e) => {
    if (confirm && !confirm.id) return;
    setLoading(true);
    if (openActionConfirm && openActionConfirm.action === "DELETE") {
      setOpen(false);
    } else if (openActionConfirm && openActionConfirm.action === "CANCEL") {
      setOpen(false);
    } else if (openActionConfirm && openActionConfirm.action === "REJECT") {
      setOpen(false);
    }
  };

  const toggleHistory = (filter) => {
    setLoading(true);
    setRefresh(false);
    setShowHistory(filter === "history" ? true : false);
    setTimeout(() => setRefresh(true), 500);
    setTimeout(() => setLoading(false), 500);
  };

  const handleConfirmPayment = (id) => {
    if (selectedRowIds.length === 0) {
      setWarningMessage({
        open: true,
        msg: t("listing:payments.errorNoSelectMsg"),
      });
    } else {
      let forToAccount = historyPage?.location?.state?.for;
      let reqBody = { jobIds: selectedRowIds.join(",") };

      if (forToAccount) {
        reqBody = { jobIds: selectedRowIds.join(","), toAccnId: forToAccount };
      }
      sendRequest(
        `/api/v1/clickargo/clictruck/payment/invoice/details`,
        "getInvoiceForConfirmation",
        "post",
        reqBody
      );
    }
  };

  const handleWarningAction = (e) => {
    setWarningMessage({ open: false, msg: "", hlMsg: "", subMsg: "" });
  };

  useEffect(() => {
    if (showHistory) {
      setFilterBy([{ attribute: "history", value: "history" }]);
    } else {
      setFilterBy([{ attribute: "history", value: "default" }]);
    }
  }, [showHistory]);

  const [snackBarState, setSnackBarState] = useState({
    open: false,
    vertical: "top",
    horizontal: "center",
    msg: t("common:common.msg.deleted"),
    severity: "success",
  });

  const defaultSnackbarValue = {
    success: false,
    successMsg: "",
    error: false,
    errorMsg: "",
    redirectPath: "",
  };

  const handleCloseSnackBar = () => {
    setSnackBarState({ ...snackBarState, open: false });
  };

  let snackBar = null;
  if (success) {
    const anchorOriginV = snackBarState.vertical;
    const anchorOriginH = snackBarState.horizontal;

    snackBar = (
      <Snackbar
        anchorOrigin={{ vertical: anchorOriginV, horizontal: anchorOriginH }}
        open={snackBarState.open}
        onClose={handleCloseSnackBar}
        autoHideDuration={3000}
        key={anchorOriginV + anchorOriginH}
      >
        <C1Alert
          onClose={handleCloseSnackBar}
          severity={snackBarState.severity}
        >
          {snackBarState.msg}
        </C1Alert>
      </Snackbar>
    );
  }

  let elPayJobs = (
    <Grid container spacing={1}>
      <Grid item>
        <Button
          style={{
            backgroundColor: "#14b1ed",
            color: "#fff",
            padding: "10px 20px 10px 20px",
            fontWeight: "bold",
          }}
          onClick={() => handleConfirmPayment()}
          startIcon={<PaymentOutlinedIcon />}
        >
          {t("common:common.buttons.pay")}
        </Button>
      </Grid>
    </Grid>
  );

  const downloadFileHandler = (fileEntity, fileId) => {
    const dlApi = `/api/v1/clickargo/clictruck/attach/byJobId/${fileEntity}/${fileId}`;
    sendRequest(dlApi, "downloadFile", "get");
  };

  return (
    <React.Fragment>
      {loading && <MatxLoading />}
      {snackBar}
      {confirm && confirm.id && (
        <ConfirmationDialog
          open={openActionConfirm.open}
          title={t("listing:coJob.popup.confirmation")}
          text={t("listing:coJob.msg.confirmation", {
            action: openActionConfirm?.action,
            id: confirm.id,
          })}
          onYesClick={() => handleActionHandler()}
          onConfirmDialogClose={() =>
            setOpenActionConfirm({ ...openActionConfirm, open: false })
          }
        />
      )}

      {/* temporary popup to handle empty response at download file */}
      <C1PopUp
        openPopUp={showEmptyObjectPopUp}
        setOpenPopUp={setShowEmptyObjectPopUp}
        maxWidth={"sm"}
      >
        <Grid container alignItems="center" justifyContent="center">
          <Typography>Sorry, this file doesn't exist</Typography>
        </Grid>
      </C1PopUp>

      <DataTable
        url="/api/v1/clickargo/clictruck/invoice/job/approved"
        columns={columns}
        title=""
        defaultOrder="jobDtLupd"
        defaultOrderDirection="desc"
        isServer={true}
        isShowViewColumns={true}
        isShowDownload={true}
        isShowPrint={true}
        isShowFilter={true}
        isRefresh={isRefresh}
        isShowFilterChip
        isRowSelectable
        filterBy={filterBy}
        customRowsPerPage={[10, 20]}
        guideId={"clicdo.doi.ff.bl.list.table"}
        showActiveHistoryButton={toggleHistory}
        showAddButton={[
          {
            show: !showHistory,
            label: t("listing:finance.pay").toUpperCase(),
            icon: <PaymentOutlinedIcon />,
            action: handleConfirmPayment,
          },
        ]}
      />

      <C1Warning
        warningMessage={warningMessage}
        handleWarningAction={handleWarningAction}
      />
    </React.Fragment>
  );
};

export default withErrorHandler(PendingPayments);
