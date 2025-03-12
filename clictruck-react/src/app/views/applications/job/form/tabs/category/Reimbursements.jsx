import {
  Box,
  Button,
  CircularProgress,
  Grid,
  Snackbar,
  Tooltip,
  Typography,
} from "@material-ui/core";
import { NearMeOutlined, VisibilityOutlined } from "@material-ui/icons";
import GetAppIcon from "@material-ui/icons/GetAppOutlined";
import DeleteOutlineOutlinedIcon from "@material-ui/icons/DeleteOutlineOutlined";
import EditOutlinedIcon from "@material-ui/icons/EditOutlined";
import SettingsBackupRestoreOutlinedIcon from "@material-ui/icons/SettingsBackupRestoreOutlined";
import React, {
  useContext,
  forwardRef,
  useEffect,
  useImperativeHandle,
  useState,
} from "react";
import { useTranslation } from "react-i18next";

import C1Alert from "app/c1component/C1Alert";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DataTable from "app/c1component/C1DataTable";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import useHttp from "app/c1hooks/http";
import { dialogStyles } from "app/c1utils/styles";
import {
  formatCurrency,
  isEmpty,
  previewPDF,
  Uint8ArrayToString,
} from "app/c1utils/utility";
import { ConfirmationDialog } from "matx";

import AddReimbursementPopup from "../../../popups/AddReimbursementPopup";
import JobTruckContext from "app/views/applications/job/form/JobTruckContext";

const Reimbursements = forwardRef((props, ref) => {
  const {
    tripId,
    jobId,
    viewType,
    showAddButton,
    showEditButton,
    showDeleteButton,
    disabled,
    chargeAmount,
  } = props;

  /** ---------------- Declare states ------------------- */
  const { isLoading, res, urlId, sendRequest, error } = useHttp();

  const [action, setAction] = useState(null);
  const dialogClasses = dialogStyles();
  const [disburseData, setDisburseData] = useState({});
  const [isRefresh, setIsRefresh] = useState(false);
  const [loading, setLoading] = useState(false);
  const [openAddPopUp, setOpenAddPopUp] = React.useState(false);
  const [chargePrice, setChargePrice] = useState({});
  const [snackBarState, setSnackBarState] = useState({
    open: false,
    vertical: "top",
    horizontal: "center",
    msg: "",
    severity: "success",
  });
  const [openSubmitConfirm, setOpenSubmitConfirm] = useState({
    action: null,
    open: false,
  });
  const [popUpFieldError, setPopUpFieldError] = useState({});
  const [isDisable, setIsDisable] = useState(false);
  const [filterBy, setFilterBy] = useState([]);

  const { t } = useTranslation(["job", "common", "button"]);

  useImperativeHandle(ref, () => ({
    getTotalReimbursementAmt: () => {
      return chargePrice?.totalReimbursement;
    },
  }));

  const { inputData } = useContext(JobTruckContext);

  /** --------------- Update states -------------------- */
  useEffect(() => {
    getTotalCharge();
  }, []);

  useEffect(() => {
    setChargePrice({ ...chargePrice, totalJobCharge: parseInt(chargeAmount) });
  }, [chargeAmount]);

  useEffect(() => {
    if (!isLoading && res && !error) {
      if (urlId === "getInitReimburse") {
        setDisburseData(res?.data);
        setAction("create");
        setOpenAddPopUp(true);
      } else if (urlId === "getReimburseById") {
        setDisburseData(res?.data);
        setOpenAddPopUp(true);
      } else if (urlId === "submitReimburse") {
        setOpenAddPopUp(false);
        setIsRefresh(true);
        setLoading(false);
        setSnackBarState({
          ...snackBarState,
          ...{ open: true, msg: t("common:genericMsgs.success") },
        });
        getTotalCharge();
        setPopUpFieldError({});
      } else if (urlId === "deleteReimburse") {
        setIsRefresh(true);
        setLoading(false);
        setSnackBarState({
          ...snackBarState,
          ...{ open: true, msg: t("common:genericMsgs.success") },
        });
        setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
        getTotalCharge();
      } else if (urlId === "getTotalCharge") {
        const price = res?.data?.data;
        setChargePrice(price);
      }
    }
  }, [isLoading, res, urlId, error]);

  const getTotalCharge = () => {
    if (tripId !== 0) {
      sendRequest(
        `api/v1/clickargo/clictruck/tripcharges/calculation/total/${tripId}`,
        "getTotalCharge",
        "get"
      );
    }
  };

  const handleSaveDisburse = () => {
    if (!isEmpty(handlePopUpFieldValidate())) {
      setPopUpFieldError(handlePopUpFieldValidate());
    } else {
      setLoading(true);
      const data = {
        ...disburseData,
        ...{
          tckCtTrip: { trId: tripId },
          trStatus: "A",
          trTotal: parseInt(disburseData?.trPrice),
        },
      };
      sendRequest(
        `/api/v1/clickargo/clictruck/truck/reimburse`,
        "submitReimburse",
        "POST",
        data
      );
    }
  };

  const handleUpdateDisburse = () => {
    if (!isEmpty(handlePopUpFieldValidate())) {
      setPopUpFieldError(handlePopUpFieldValidate());
    } else {
      setLoading(true);
      const data = {
        ...disburseData,
        ...{ trTotal: parseInt(disburseData?.trPrice) },
      };
      sendRequest(
        `/api/v1/clickargo/clictruck/truck/reimburse/${disburseData.trId}`,
        "submitReimburse",
        "PUT",
        data
      );
    }
  };

  const handleDeleteDisburse = (id) => {
    setLoading(true);
    setIsRefresh(false);
    sendRequest(
      `/api/v1/clickargo/clictruck/truck/reimburse/${id}`,
      "deleteReimburse",
      "DELETE"
    );
  };

  const handleDownloadDisburse = (data, fileName) => {
    previewPDF(fileName, data);
  };

  const popupViewHandler = (id, viewOnly, action) => {
    setAction(action);
    setIsRefresh(false);
    setIsDisable(viewOnly);
    setPopUpFieldError({});
    sendRequest(
      `/api/v1/clickargo/clictruck/truck/reimburse/${id}`,
      "getReimburseById",
      "GET",
      null
    );
  };

  const handlePopupOpen = () => {
    setIsRefresh(false);
    setIsDisable(false);
    setPopUpFieldError({});
    sendRequest(
      `/api/v1/clickargo/clictruck/truck/reimburse/-`,
      "getInitReimburse",
      "GET"
    );
  };

  const validatePrice = (num) => {
    const reg = /^-?\d+\.?\d*$/;
    return (
      num === "" || reg.test(num) || (num.charAt(0) === "-" && num.length === 1)
    );
  };

  const handleSignatureValidate = (uploadFileType) => {
    const errors = {};
    const images = ["image/png", "image/jpeg", "application/pdf"];
    if (uploadFileType && !images.includes(uploadFileType)) {
      errors.trReceiptName = t("common:common.msg.rmbrsmntFileFormat");
    }
    if (uploadFileType === "") {
      errors.trReceiptName = t("common:common.msg.noFileUploded");
    }
    return errors;
  };

  const handleInputReimburseChange = (e) => {
    const { name, value } = e.target;

    if (name === "tckCtMstReimbursementType.rbtypId") {
      setDisburseData({
        ...disburseData,
        tckCtMstReimbursementType: {
          ...disburseData.tckCtMstReimbursementType,
          rbtypId: value,
        },
      });
    } else if (e.target?.files) {
      let file = e.target.files[0];
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
          setDisburseData({
            ...disburseData,
            ...{ base64File: base64Sign, trReceiptName: file.name },
          });
        };
      } else {
        setPopUpFieldError({ ...popUpFieldError, ...errors });
      }
    } else if (name === "trPrice" || name === "trTax") {
      if (validatePrice(value)) {
        setDisburseData({ ...disburseData, [name]: value });
      }
    } else {
      setDisburseData({ ...disburseData, [name]: value });
    }
  };

  let actionEl;
  if (action === "create") {
    actionEl = (
      <Tooltip title={t("buttons:add")}>
        <Button
          disabled={loading}
          onClick={() => handleSaveDisburse()}
          className={dialogClasses.dialogButtonSpace}
        >
          {loading ? (
            <CircularProgress color="inherit" size={30} />
          ) : (
            <NearMeOutlined color="primary" fontSize="large" />
          )}
        </Button>
      </Tooltip>
    );
  } else if (action === "update") {
    actionEl = (
      <Tooltip title={t("buttons:update")}>
        <Button
          disabled={loading}
          onClick={() => handleUpdateDisburse()}
          className={dialogClasses.dialogButtonSpace}
        >
          {loading ? (
            <CircularProgress color="inherit" size={30} />
          ) : (
            <NearMeOutlined color="primary" fontSize="large" />
          )}
        </Button>
      </Tooltip>
    );
  } else {
    actionEl = null;
  }

  const handlePopUpFieldValidate = () => {
    let errors = {};
    if (disburseData?.tckCtMstReimbursementType?.rbtypId === "") {
      errors.rbtypId = t("common:validationMsgs.required");
    }
    if (disburseData?.trReceiptName === "") {
      errors.trReceiptName = t("common:validationMsgs.required");
    }
    if (disburseData?.trPrice === "" || disburseData?.trPrice === 0) {
      errors.trPrice = t("common:validationMsgs.required");
    }
    if (!(parseInt(disburseData?.trPrice) + parseInt(disburseData?.trTax))) {
      errors.trTotal = t("common:validationMsgs.required");
    }
    return errors;
  };

  const handleCloseSnackBar = () => {
    setSnackBarState({ ...snackBarState, open: false });
  };

  let snackBar = null;
  if (snackBarState && snackBarState.open) {
    const anchorOriginV = snackBarState.vertical;
    const anchorOriginH = snackBarState.horizontal;

    snackBar = (
      <Snackbar
        anchorOrigin={{ vertical: anchorOriginV, horizontal: anchorOriginH }}
        open={snackBarState.open}
        autoHideDuration={3000}
        onClose={handleCloseSnackBar}
        key={anchorOriginV + anchorOriginH}
      >
        <C1Alert severity={snackBarState.severity}>{snackBarState.msg}</C1Alert>
      </Snackbar>
    );
  }

  let confirmDialog = "";
  if (openSubmitConfirm.open) {
    confirmDialog = (
      <ConfirmationDialog
        open={openSubmitConfirm?.open}
        onConfirmDialogClose={() =>
          setOpenSubmitConfirm({
            ...openSubmitConfirm,
            action: null,
            open: false,
          })
        }
        text={t("common:msg.deleteConfirm")}
        title={t("common:popup.confirmation")}
        onYesClick={(e) => handleDeleteDisburse(openSubmitConfirm?.id)}
      />
    );
  }

  const columns = [
    {
      name: "trId",
      label: "S/No",
    },
    {
      name: "tckCtMstReimbursementType.rbtypName",
      label: t("job:tripDetails.type"),
    },
    {
      name: "trRemarks",
      label: t("job:tripDetails.description"),
    },
    {
      name: "trPrice",
      label: t("job:tripDetails.price"),
      options: {
        filter: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          if (!value) return 0;
          return formatCurrency(value, "IDR");
        },
      },
    },
    {
      name: "base64File",
      label: "file",
      options: {
        display: false,
        filter: false,
      },
    },
    {
      name: "trReceiptName",
      label: "File Name",
      options: {
        display: false,
        filter: false,
      },
    },
    {
      name: "",
      label: t("job:tripDetails.action"),
      options: {
        filter: false,
        sort: false,
        viewColumns: false,
        display: true,
        setCellHeaderProps: () => {
          return { style: { textAlign: "center", width: "25%" } };
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          const fileName = tableMeta.rowData[5];
          return (
            <Grid
              container
              direction="row"
              justifyContent="space-between"
              alignItems="center"
              style={{ minWidth: "120px" }}
            >
              <Grid container item justifyContent="center" spacing={3}>
                {showEditButton && (
                  <Grid item xs={3}>
                    <C1LabeledIconButton
                      tooltip={t("buttons:edit")}
                      label={t("buttons:edit")}
                      action={() =>
                        popupViewHandler(tableMeta.rowData[0], false, "update")
                      }
                    >
                      <EditOutlinedIcon />
                    </C1LabeledIconButton>
                  </Grid>
                )}
                <Grid item xs={3}>
                  <C1LabeledIconButton
                    tooltip={t("buttons:download")}
                    label={t("buttons:download")}
                    action={() =>
                      handleDownloadDisburse(tableMeta.rowData[4], fileName)
                    }
                  >
                    <GetAppIcon />
                  </C1LabeledIconButton>
                </Grid>
                <Grid item xs={3}>
                  <C1LabeledIconButton
                    tooltip={t("buttons:view")}
                    label={t("buttons:view")}
                    action={() =>
                      popupViewHandler(tableMeta.rowData[0], true, "view")
                    }
                  >
                    <VisibilityOutlined />
                  </C1LabeledIconButton>
                </Grid>
                {showDeleteButton && (
                  <Grid item xs={3}>
                    <C1LabeledIconButton
                      tooltip={t("buttons:delete")}
                      label={t("buttons:delete")}
                      action={() =>
                        setOpenSubmitConfirm({
                          ...openSubmitConfirm,
                          open: true,
                          id: tableMeta.rowData[0],
                        })
                      }
                    >
                      <DeleteOutlineOutlinedIcon />
                    </C1LabeledIconButton>
                  </Grid>
                )}
              </Grid>
            </Grid>
          );
        },
      },
    },
  ];

  useEffect(() => {
    if (jobId) {
      setFilterBy([{ attribute: "TCkCtTrip.TCkJobTruck.jobId", value: jobId }]);
    } else if (tripId) {
      setFilterBy([{ attribute: "TCkCtTrip.trId", value: tripId }]);
    }
  }, [tripId, jobId]);

  return (
    <>
      {snackBar}
      {confirmDialog}
      {/* The backdrop causes different shade of grey on the background when Reimbursement tab is selected in the Trips and Reimbursement Details popup
        {
            <Backdrop
                sx={{ color: '#fff', zIndex: 999999 }}
                open={true}
            >
                <CircularProgress color="inherit" />
            </Backdrop>
        }
        */}
      <Box
        component={`div`}
        sx={{
          opacity: disabled ? 0.3 : 1,
          pointerEvents: disabled ? "none" : "",
        }}
      >
        <C1CategoryBlock
          icon={<SettingsBackupRestoreOutlinedIcon />}
          title={t("job:tripDetails.reimbursement")}
        >
          <Grid container alignItems="center" spacing={3}>
            <Grid item xs={12}>
              {filterBy && filterBy.length > 0 && (
                <C1DataTable
                  url={
                    tripId === 0
                      ? null
                      : "/api/v1/clickargo/clictruck/truck/reimburse"
                  }
                  isServer={tripId === 0 ? false : true}
                  dbName={tripId === 0 ? { list: [] } : null}
                  columns={columns}
                  defaultOrder="trDtCreate"
                  defaultOrderDirection="desc"
                  isRefresh={isRefresh}
                  isShowDownload={false}
                  isShowToolbar
                  isShowPrint={false}
                  isShowViewColumns={false}
                  isShowFilter={false}
                  showAdd={
                    showAddButton
                      ? {
                          type: "popUp",
                          popUpHandler: handlePopupOpen,
                        }
                      : null
                  }
                  filterBy={filterBy}
                  guideId="clicdo.doi.co.jobs.tabs.authorisation.table"
                />
              )}
              <Grid
                container
                alignItems="flex-end"
                spacing={3}
                style={{ marginTop: 10 }}
              >
                <Grid item xs={6} />
                <Grid container justifyContent="flex-end">
                  <Grid item xs={6} style={{ paddingRight: 10 }}>
                    <Grid
                      container
                      direction="row"
                      justifyContent="space-between"
                      style={{ marginBottom: 10 }}
                    >
                      <Typography variant="h6">
                        {t("job:tripDetails.tripReimbursementCharge")}
                      </Typography>
                      {jobId && (
                        <Typography variant="h6">
                          {formatCurrency(
                            inputData?.jobTotalReimbursements
                              ? inputData?.jobTotalReimbursements
                              : 0,
                            "IDR"
                          )}
                        </Typography>
                      )}
                      {!jobId && (
                        <Typography variant="h6">
                          {formatCurrency(
                            chargePrice?.totalReimbursement
                              ? chargePrice?.totalReimbursement
                              : 0,
                            "IDR"
                          )}
                        </Typography>
                      )}
                    </Grid>
                  </Grid>
                </Grid>
              </Grid>
            </Grid>
          </Grid>
        </C1CategoryBlock>
      </Box>
      <Grid
        container
        alignItems="flex-end"
        spacing={3}
        style={{ marginTop: 10 }}
      >
        <Grid item xs={6} />
        <Grid container justifyContent="flex-end">
          <Grid item xs={6} style={{ paddingRight: 10 }}>
            <Grid container direction="row" justifyContent="space-between">
              <Typography variant="h6">
                {t("job:tripDetails.totalTripChargeNoIDR")}
              </Typography>

              {jobId && (
                <Typography
                  style={{
                    color: "red",
                  }}
                  variant="h6"
                >
                  {formatCurrency(
                    inputData?.jobTotalCharge +
                      inputData?.jobTotalReimbursements,
                    "IDR"
                  )}
                </Typography>
              )}

              {!jobId && (
                <Typography
                  style={{
                    color: "red",
                  }}
                  variant="h6"
                >
                  {formatCurrency(
                    chargePrice?.totalJobCharge
                      ? chargePrice?.totalJobCharge
                      : 0,
                    "IDR"
                  )}
                </Typography>
              )}
            </Grid>
          </Grid>
        </Grid>
      </Grid>

      <AddReimbursementPopup
        inputData={disburseData}
        isDisabled={isDisable}
        errors={popUpFieldError}
        handleDownloadFile={handleDownloadDisburse}
        openAddPopUp={openAddPopUp}
        setOpenAddPopUp={setOpenAddPopUp}
        actionEl={actionEl}
        action={action}
        handleInputChange={handleInputReimburseChange}
      />
    </>
  );
});

export default Reimbursements;
