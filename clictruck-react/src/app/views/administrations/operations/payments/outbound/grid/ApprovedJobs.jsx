import {
  Badge,
  Button,
  Checkbox,
  Grid,
  Popover,
  Tooltip,
  Typography,
} from "@material-ui/core";
import Snackbar from "@material-ui/core/Snackbar";
import { makeStyles } from "@material-ui/core/styles";
import { VisibilityOutlined } from "@material-ui/icons";
import CreditCardOutlinedIcon from "@material-ui/icons/CreditCardOutlined";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";

import DataTable from "app/atomics/organisms/DataTable";
import C1Alert from "app/c1component/C1Alert";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1PopUp from "app/c1component/C1PopUp";
import C1Warning from "app/c1component/C1Warning";
import useHttp from "app/c1hooks/http";
import useQuery from "app/c1hooks/useQuery";
import { JobStates, PaymentState, ShipmentTypes } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import {
  customFilterDateDisplay,
  customNumFieldDisplay,
  formatDate,
  previewPDF,
} from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import { MatxLoading } from "matx";
import ConfirmationDialog from "matx/components/ConfirmationDialog";
import C1InputField from "app/c1component/C1InputField";

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

const ApprovedJobs = () => {
  console.log("Approved Jobs for outbound");
  const history = useHistory();
  const { t } = useTranslation([
    "buttons",
    "listing",
    "ffclaims",
    "common",
    "status",
    "payments",
  ]);
  const { isLoading, res, error, urlId, sendRequest } = useHttp();
  const bdClasses = useStyles();
  const [showHistory, setShowHistory] = useState(false);
  const [filterBy, setFilterBy] = useState([
    { attribute: "history", value: "default" },
  ]);

  // const [toAccnSelected, setToAccnSelected] = useState();

  const [isRefresh, setRefresh] = useState(false);
  const [loading, setLoading] = useState(false);
  // eslint-disable-next-line
  const [success, setSuccess] = useState(false);
  // eslint-disable-next-line
  const [confirm, setConfirm] = useState({ id: null });
  const [open, setOpen] = useState(false);
  // eslint-disable-next-line
  const [openActionConfirm, setOpenActionConfirm] = useState({
    action: null,
    open: false,
  });
  const [warningMessage, setWarningMessage] = useState({
    open: false,
    msg: "",
  });
  const [showEmptyObjectPopUp, setShowEmptyObjectPopUp] = useState(false);
  const [anchorEl, setAnchorEl] = React.useState(null);
  const [staticJobId, setStaticJobId] = useState(null);

  const openPopover = Boolean(anchorEl);

  const [selectedRowIds, setSelectedRowIds] = useState([]);
  //State to load the TO accounts that has jobs only
  const [toWithJobs, setToWithJobs] = useState([]);

  let rowData = [];

  // useEffect(() => {
  //     sendRequest("/api/v1/clickargo/clictruck/misc/towithjobs", "getToWithJobs", "get")
  // }, []);

  const hasSuspendedAccounts = rowData.some((data) => data[15] === "S");

  const columns = [
    {
      name: "",
      label: "",
      options: {
        display: false,
        sort: false,
        filter: false,
        viewColumns: false,
        customHeadLabelRender: (columnMeta, handleToggleColumn, sortOrder) => {
          return (
            <Checkbox
              disabled={showHistory || hasSuspendedAccounts}
              checked={
                selectedRowIds.length > 0 &&
                selectedRowIds.length === rowData.length
              }
              onChange={({ target: { checked } }) => {
                checked ? setSelectedRowIds(rowData) : setSelectedRowIds([]);
              }}
            />
          );
        },
        customBodyRender: (status, tableMeta, updateValue, value) => {
          const id = tableMeta.rowData[1];
          const jobOwnerAccnStatus = tableMeta.rowData[15];
          const isOwnerSuspended = jobOwnerAccnStatus === "S";
          rowData = tableMeta.tableData
            .filter((data) => data[15] !== "S")
            .map((data) => data[1]);

          return (
            <Checkbox
              disableRipple={true}
              disabled={isOwnerSuspended || showHistory}
              checked={selectedRowIds.includes(id)}
              onChange={({ target: { checked } }) =>
                checked && !isOwnerSuspended
                  ? setSelectedRowIds(
                      selectedRowIds.filter((rowId) => rowId !== id).concat(id)
                    )
                  : setSelectedRowIds(
                      selectedRowIds.filter((rowId) => rowId !== id)
                    )
              }
            />
          );
        },
      },
    },

    {
      name: "jobId",
      label: t("listing:trucklist.jobid"),
      // options: {
      //     filter: true,
      //     sort: true,
      // }
    },
    {
      name: "tckJob.tckMstShipmentType.shtId",
      label: t("listing:trucklist.type"),
      options: {
        filter: true,
        filterType: "dropdown",
        filterOptions: {
          names: Object.keys(ShipmentTypes),
          renderValue: (v) => {
            return ShipmentTypes[v].desc;
          },
        },
        customFilterListOptions: {
          render: (v) => {
            return ShipmentTypes[v].desc;
          },
        },
      },
    },
    {
      name: "invoiceFromAccn.accnName",
      label: t("listing:verified.invoiceFrom"),
    },
    {
      name: "tckJob.tcoreAccnByJobOwnerAccn.accnName",
      label: t("listing:verified.invoiceTo"),
      options: {
        filter: true,
        sort: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          const isOwnerSuspended = tableMeta.rowData[15] === "S";
          if (isOwnerSuspended)
            return (
              <Tooltip title="Account is Suspended !">
                <Badge badgeContent={"!"} color="secondary">
                  <b>{value}</b>
                </Badge>
              </Tooltip>
            );
          else return value;
        },
      },
    },
    {
      name: "jobNoTrips",
      label: t("listing:verified.noOfTrips"),
      options: {
        filter: true,
        sort: true,
        filterType: "custom",
        filterOptions: {
          display: (filterList, onChange, index, column) => {
            return (
              <C1InputField
                label={column.label}
                name={column.name}
                isServer
                type="number"
                onChange={(event) => {
                  filterList[index][0] = event.target.value;
                  onChange(filterList[index], index, column);
                }}
                value={filterList[index][0] || ""}
              />
            );
          },
        },
      },
    },
    {
      name: "jobTotalCharge",
      label: t("listing:verified.charges"),
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
    {
      name: "jobTotalReimbursements",
      label: t("listing:verified.reimbursement"),
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
    {
      name: "billingDate",
      label: t("listing:verified.invoiceAppDt"),
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
      name: "paymentDueDate",
      label: t("listing:verified.paymentDueDate"),
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
      name: "tckJob.tckMstJobState.jbstId",
      label: t("listing:trucklist.status"),
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
    {
      name: "approvedDate",
      label: t("listing:verified.approvedDate"),
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
      name: "approvedBy",
      label: t("listing:verified.approvedBy"),
    },
    {
      name: "jobOutPaymentState",
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
      name: "action",
      label: t("listing:common.action"),
      options: {
        filter: false,
        sort: false,
        display: true,
        viewColumns: false,
        setCellHeaderProps: () => {
          return { style: { justifyContent: "center" } };
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          const status = tableMeta.rowData[8];
          const id = tableMeta.rowData[1];

          return (
            <Grid
              container
              direction="row"
              justifyContent="space-between"
              alignItems="center"
            >
              {popoverDownload({ id: staticJobId })}
              {/* <Grid item sm={6} xs={6}>
                            <C1LabeledIconButton
                                tooltip={t("buttons:download")}
                                label={t("buttons:download")}
                                action={(e) => handleOpenDownloadToggle(e, id)}
                            >
                                <GetAppIcon />
                            </C1LabeledIconButton>
                        </Grid> */}
              <Grid item sm={3} xs={6}>
                <C1LabeledIconButton
                  tooltip={t("buttons:view")}
                  label={t("buttons:view")}
                  action={() =>
                    history.push({
                      pathname: `/applications/services/job/truck/view?tabIndex=5`,
                      state: { from: "/opadmin/outboundpayments", jobId: id },
                    })
                  }
                >
                  <VisibilityOutlined />
                </C1LabeledIconButton>
              </Grid>
            </Grid>
          );
        },
      },
    },
    {
      name: "tckJob.tcoreAccnByJobOwnerAccn.accnStatus",
      options: {
        filter: false,
        sort: false,
        display: "excluded",
      },
    },
  ];

  const popoverDownload = ({ id }) => {
    return (
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
              onClick={() => downloadFileHandler("toInvoice", id)}
            >
              <Typography>{t("listing:invoices.invJob")}</Typography>
            </Button>
          </Grid>
          <Grid item>
            <Button
              style={{ textTransform: "none" }}
              onClick={() => downloadFileHandler("platformInvoice", id)}
            >
              <Typography>{t("listing:invoices.invPf")}</Typography>
            </Button>
          </Grid>
          <Grid item>
            <Button
              style={{ textTransform: "none" }}
              onClick={() => downloadFileHandler("debitNote", id)}
            >
              <Typography>{t("listing:invoices.invDn")}</Typography>
            </Button>
          </Grid>
        </Grid>
      </Popover>
    );
  };

  const handleOpenDownloadToggle = (event, id) => {
    setStaticJobId(id);
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handlePayButton = () => {
    if (selectedRowIds?.length === 0) {
      setWarningMessage({
        open: true,
        msg: t("listing:payments.errorNoJobNoToSelected"),
      });
    } else {
      history.push(
        `/applications/finance/payments/details?jobIds=${selectedRowIds.join(
          ","
        )}`,
        {
          from: `/applications/services/gli/dashboard`,
          // for: toAccnSelected
        }
      );
    }
  };

  const handleWarningAction = (e) => {
    setWarningMessage({ open: false, msg: "", hlMsg: "", subMsg: "" });
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

  const downloadFileHandler = (fileEntity, fileId) => {
    const dlApi = `/api/v1/clickargo/clictruck/attach/byJobId/${fileEntity}/${fileId}`;
    sendRequest(dlApi, "downloadFile", "get");
  };

  // const handleTruckOptChange = (e) => {
  //     const { name, value } = e.target
  //     setToAccnSelected(value);
  //     let fBy = filterBy;
  //     if (value) {
  //         let fByMap = fBy.map(e => {
  //             if (e?.attribute === "invoiceFromAccn.accnId") {
  //                 console.log("attribute?")
  //                 e.value = value;
  //             }

  //             console.log("e", e);
  //             return e;
  //         });

  //         if (fByMap.length == 1) {
  //             //checking if the fbyMap lenght is 1 then that means the invoiceFromAccn is not in the map yet so push it
  //             fBy.push({ attribute: name, value: value });
  //         }

  //     } else {
  //         //removed to reload the table
  //         fBy.splice(fBy.findIndex(e => e?.attribute === 'invoiceFromAccn.accnId'), 1);
  //     }
  //     setFilterBy(fBy);
  //     setRefresh(true);
  //     //resetting to false for reloading later
  //     setTimeout(() => setRefresh(false), 500);

  // }

  useEffect(() => {
    if (showHistory) {
      setFilterBy([{ attribute: "history", value: "history" }]);
    } else {
      setFilterBy([{ attribute: "history", value: "default" }]);
    }
  }, [showHistory]);

  useEffect(() => {
    if (!isLoading && !error && res) {
      setLoading(isLoading);
      // if (urlId === 'getToWithJobs') {
      //     console.log("res?data: ", res?.data);
      //     setToWithJobs([...res?.data]);
      //     //set the selected account to first element
      //     if (isArrayNotEmpty(res?.data)) {
      //         setToAccnSelected(res?.data[0]?.accnId);

      //         if (res?.data[0]?.accnId) {
      //             console.log("test");
      //             let fBy = filterBy;
      //             fBy.push({ attribute: "invoiceFromAccn.accnId", value: res?.data[0]?.accnId });
      //             setRefresh(true);
      //             //resetting to false
      //             setTimeout(() => setRefresh(false), 500);
      //         }
      //     }
      // } else
      if (urlId === "downloadFile") {
        if (res?.data != null) {
          const fileName = res?.data?.filename;
          const data = res?.data?.data;

          previewPDF(fileName != null ? fileName : "preview.pdf", data);
        } else {
          setShowEmptyObjectPopUp(true);
        }
      }
    }
  }, [urlId, isLoading, res, error]);

  const toggleHistory = (filter) => {
    setLoading(true);
    setRefresh(false);
    setShowHistory(filter === "history" ? true : false);
    setTimeout(() => setRefresh(true), 500);
    setTimeout(() => setLoading(false), 500);
  };

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
  const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);

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

  let elPayAction = (
    <Grid container spacing={1}>
      <Grid item>
        <Button
          variant="contained"
          style={{
            backgroundColor: "#14b1ed",
            color: "#fff",
            padding: "10px 20px 10px 20px",
            fontWeight: "bold",
          }}
          onClick={() => handlePayButton()}
          startIcon={<CreditCardOutlinedIcon />}
        >
          {t("common:common.buttons.pay")}
        </Button>
      </Grid>
    </Grid>
  );

  return (
    <React.Fragment>
      {loading && <MatxLoading />}
      {snackBar}
      {confirm && confirm.id && (
        <ConfirmationDialog
          open={open}
          title={t("listing:coJob.popup.confirmation")}
          text={t("listing:coJob.msg.confirmation", {
            action: openActionConfirm?.action,
            id: confirm.id,
          })}
          onYesClick={() => handleActionHandler()}
          onConfirmDialogClose={() => setOpen(false)}
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
      {popoverDownload({ id: staticJobId })}
      <DataTable
        url="/api/v1/clickargo/clictruck/invoice/job/approved"
        columns={columns}
        title=""
        defaultOrder="jobDtCreate"
        defaultOrderDirection="desc"
        isServer={true}
        isShowViewColumns={true}
        isShowDownload={true}
        isShowPrint={true}
        isShowFilter={true}
        isRefresh={isRefresh}
        isShowFilterChip
        filterBy={filterBy}
        guideId={""}
        showActiveHistoryButton={toggleHistory}
        customRowsPerPage={[10, 20]}
        // showAddButton={[
        //     {
        //         label: 'PAY',
        //         icon: <CreditCardOutlinedIcon />,
        //         action: handlePayButton
        //     }
        // ]}
      />
      <C1Warning
        warningMessage={warningMessage}
        handleWarningAction={handleWarningAction}
      />
    </React.Fragment>
  );
};

export default withErrorHandler(ApprovedJobs);
