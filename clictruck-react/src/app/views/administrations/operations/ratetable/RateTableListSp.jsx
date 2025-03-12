import { Button, ButtonGroup, Dialog, Grid } from "@material-ui/core";
import Snackbar from "@material-ui/core/Snackbar";
import moment from "moment";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";

import C1Alert from "app/c1component/C1Alert";
import C1Button from "app/c1component/C1Button";
import C1DataTable from "app/c1component/C1DataTable";
import C1ListPanel from "app/c1component/C1ListPanel";
import C1PopUp from "app/c1component/C1PopUp";
import C1SelectField from "app/c1component/C1SelectField";
import useHttp from "app/c1hooks/http";
import { JobStates, RecordStatus } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import {
  customFilterDateDisplay,
  formatDate,
  getValue,
} from "app/c1utils/utility";
import { MatxLoading } from "matx";
import ConfirmationDialog from "matx/components/ConfirmationDialog";
import useAuth from "app/hooks/useAuth";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import { VisibilityOutlined } from "@material-ui/icons";
import C1FormButtons from "app/c1component/C1FormButtons";

const RateTableListSp = () => {
  const { t } = useTranslation([
    "buttons",
    "listing",
    "cargoowners",
    "administration",
  ]);

  const history = useHistory();

  const {
    isLoading,
    isFormSubmission,
    res,
    validation,
    error,
    urlId,
    sendRequest,
  } = useHttp();

  const [confirm, setConfirm] = useState({ jobId: null });
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [isRefresh, setRefresh] = useState(false);

  const [openAddPopUp, setOpenAddPopUp] = useState(false);
  const [coFf, setCoFf] = useState();
  const [deleteSuccess, setDeleteSuccess] = useState(false);
  const [openDeleteConfirm, setOpenDeleteConfirm] = useState({
    action: null,
    open: false,
  });
  const [filterHistory, setFilterHistory] = useState([
    { attribute: "history", value: "default" },
  ]);
  const [showHistory, setShowHistory] = useState(false);
  const [dataStatus, setDataStatus] = useState("");
  const [openSubmitConfirm, setOpenSubmitConfirm] = useState({
    action: null,
    open: false,
  });
  const { user } = useAuth();
  const [openWarning, setOpenWarning] = useState(false);
  const [warningMessage, setWarningMessage] = useState("");

  const isGli = true; //is gli will be change to gli approver or verifier
  //Remarks
  const popupDefaultValue = { jobRemarks: "" };
  const [popUpDetails, setPopUpDetails] = useState(popupDefaultValue);
  const [openRemarkDialog, setOpenRemarkDialog] = useState(false);

  let clicTruckUrl = `/api/v1/clickargo/clictruck/truck`;
  const handleDeActiveHandler = (id) => {
    setLoading(true);
    setRefresh(false);
    sendRequest(
      `${clicTruckUrl}/deactivate/ratetable/${id}`,
      "deactivate",
      "PUT",
      {}
    );
  };

  const handleActiveHandler = (id) => {
    setLoading(true);
    setRefresh(false);
    sendRequest(
      `${clicTruckUrl}/activate/ratetable/${id}`,
      "activate",
      "PUT",
      {}
    );
  };

  const handleConfirmSetActive = (id) => {
    setDataStatus({ ...dataStatus, ...{ status: "active", id } });
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: "SAVE",
      open: true,
      msg: t("common:msg.activeConfirm"),
    });
  };

  const handleConfirmSetInActive = (id) => {
    setDataStatus({ ...dataStatus, ...{ status: "deactive", id } });
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: "SAVE",
      open: true,
      msg: t("common:msg.inActiveConfirm"),
    });
  };

  const handleSubmitStatus = () => {
    setLoading(true);
    setOpenSubmitConfirm({ ...openSubmitConfirm, action: "", open: false });
    sendRequest(
      "/api/v1/clickargo/clictruck/administrator/ratetable/" +
        dataStatus.id +
        "/" +
        dataStatus.status,
      "setStatus",
      "put",
      null
    );
  };

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
        text={openSubmitConfirm?.msg}
        title={t("common:popup.confirmation")}
        onYesClick={(e) => eventHandler(openSubmitConfirm?.action)}
      />
    );
  }

  const eventHandler = (action) => {
    if (action.toLowerCase() === "save") {
      handleSubmitStatus();
    }
  };

  const columns = [
    {
      name: "rtId",
      label: " ",
      options: {
        display: "excluded",
        filter: false,
      },
    },
    {
      name: "rtName",
      label: t("administration:rateTableManagement.listing.name"),
      options: {
        filter: true,
      },
    },
    {
      name: "tcoreAccnByRtCompany.accnName",
      label: t("administration:rateTableManagement.listing.toAccn"),
      options: {
        filter: true,
      },
    },
    {
      name: "tcoreAccnByRtCoFf.accnName",
      label: t("administration:rateTableManagement.listing.coFfAccn"),
      options: {
        filter: true,
      },
    },
    {
      name: "rtDtStart",
      label: t("administration:rateTableManagement.listing.startDate"),
      options: {
        sort: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          return formatDate(value, true);
        },
        filter: true,
        filterType: "custom",
        filterOptions: {
          display: customFilterDateDisplay,
        },
      },
    },
    {
      name: "rtDtEnd",
      label: t("administration:rateTableManagement.listing.expiredDate"),
      options: {
        sort: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          return formatDate(value, true);
        },
        filter: true,
        filterType: "custom",
        filterOptions: {
          display: customFilterDateDisplay,
        },
      },
    },
    {
      name: "rtDtCreate",
      label: t("administration:rateTableManagement.listing.dateCreated"),
      options: {
        sort: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          return formatDate(value, true);
        },
        filter: true,
        filterType: "custom",
        filterOptions: {
          display: customFilterDateDisplay,
        },
      },
    },
    {
      name: "rtDtLupd",
      label: t("administration:rateTableManagement.listing.dateUpdated"),
      options: {
        sort: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          return formatDate(value, true);
        },
        filter: true,
        filterType: "custom",
        filterOptions: {
          display: customFilterDateDisplay,
        },
      },
    },
    {
      name: "rtStatus",
      label: t("administration:rateTableManagement.listing.status"),
      options: {
        sort: true,
        filter: true,
        filterType: "dropdown",
        customBodyRender: (value) => getStatusDesc(value),
        filterOptions: {
          names: [RecordStatus.ACTIVE.code, RecordStatus.INACTIVE.code],
          renderValue: (v) => {
            switch (v) {
              case RecordStatus.ACTIVE.code:
                return RecordStatus.ACTIVE.desc;
              case RecordStatus.INACTIVE.code:
                return RecordStatus.INACTIVE.desc;
              default:
                break;
            }
          },
        },
        customFilterListOptions: {
          render: (v) => {
            switch (v) {
              case RecordStatus.ACTIVE.code:
                return RecordStatus.ACTIVE.desc;
              case RecordStatus.INACTIVE.code:
                return RecordStatus.INACTIVE.desc;
              default:
                break;
            }
          },
        },
      },
    },
    {
      name: "",
      label: t("administration:rateTableManagement.listing.action"),
      options: {
        filter: false,
        sort: false,
        viewColumns: false,
        display: true,
        customHeadLabelRender: (columnMeta) => {
          return <div style={{ textAlign: "center" }}>{columnMeta.label}</div>;
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          let rtId = tableMeta.rowData[0];
          return (
            <Grid
              container
              direction="row"
              justifyContent="flex-start"
              alignItems="center"
              style={{ marginRight: "10px" }}
            >
              <Grid container item justifyContent="center" spacing={1}>
                <Grid item xs={4}>
                  <C1LabeledIconButton
                    tooltip={t("buttons:view")}
                    label={t("buttons:view")}
                    action={() =>
                      history.push(`/manage/ratetable-details/view/${rtId}`)
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
  ];

  const handleDeleteHandler = (e) => {
    if (confirm && !confirm.jobId) return;

    setLoading(true);
    if (openDeleteConfirm && openDeleteConfirm.action === "DELETE") {
      setOpen(false);
      sendRequest(
        "/api/v1/clickargo/clicdo/job/doiCo/" + confirm.jobId,
        "delete",
        "delete",
        {}
      );
    } else if (openDeleteConfirm && openDeleteConfirm.action === "CANCEL") {
      setOpen(false);
      sendRequest(
        "/api/v1/clickargo/clicdo/job/doiCo/" + confirm.jobId,
        "cancelJob",
        "get",
        null
      );
    }
  };

  const handleCancelConfirm = (e, jobId) => {
    e.preventDefault();
    setLoading(false);
    setRefresh(false);
    setConfirm({ ...confirm, jobId });
    setOpen(true);
    setOpenDeleteConfirm({
      ...openDeleteConfirm,
      action: "CANCEL",
      open: true,
    });
  };

  const toggleHistory = (filter) => {
    setRefresh(true);
    setShowHistory(filter === "history" ? true : false);
  };

  const handleDownloadBuildBody = (values) => {
    return (
      values?.length > 0 &&
      values.map((value) => {
        if (value.data[5])
          value.data[5] = moment(value?.data[5])
            .format("YYYY-MM-DD HH:mm")
            .toString();

        if (value.data[6]) {
          switch (value.data[6]) {
            case JobStates.ASG.code:
              value.data[6] = JobStates.PROG.desc;
              break;
            case JobStates.SUB.code:
              if (value.data[8] === "UNRDY") {
                value.data[6] = "Submitting";
              } else {
                value.data[6] = JobStates.SUB.desc;
              }
              break;
            case JobStates.COM.code:
              value.data[6] = JobStates.COM.desc;
              break;
            case JobStates.NEW.code:
              value.data[6] = JobStates.NEW.desc;
              break;
            case JobStates.PAID.code:
              value.data[6] = JobStates.PAID.desc;
              break;
            case JobStates.PMV.code:
              value.data[6] = JobStates.PMV.desc;
              break;
            case JobStates.DEL.code:
              value.data[6] = JobStates.DEL.desc;
              break;
            case JobStates.CAN.code:
              value.data[6] = JobStates.CAN.desc;
              break;
            case JobStates.REJ.code:
              value.data[6] = JobStates.REJ.desc;
              break;
            default:
              break;
          }
        }

        if (value.data[8]) value.data[8] = ""; //to exclude value in the CSV file

        return value;
      })
    );
  };

  useEffect(() => {
    if (showHistory) {
      setFilterHistory([{ attribute: "history", value: "history" }]);
    } else {
      setFilterHistory([{ attribute: "history", value: "default" }]);
    }
  }, [showHistory]);

  useEffect(() => {
    if (!isLoading && !error && res) {
      switch (urlId) {
        case "delete":
          setRefresh(true);
          setFilterHistory([{ attribute: "history", value: "default" }]);
          setDeleteSuccess(true);
          setSnackBarState({ ...snackBarState, open: true });
          setLoading(false);
          break;
        case "cancelJob":
          sendRequest(
            "/api/v1/clickargo/clicdo/job/doiCo/" + confirm.jobId,
            "cancelled",
            "put",
            { ...res.data, action: "CANCEL" }
          );
          break;
        case "cancelled":
          setRefresh(true);
          setFilterHistory([{ attribute: "history", value: "default" }]);
          setDeleteSuccess(true);
          setSnackBarState({
            ...snackBarState,
            msg: t("listing:coJob.msg.cancelSuccess"),
            open: true,
          });
          setLoading(false);
          break;
        case "getJob": {
          setPopUpDetails({ jobRemarks: res?.data?.tckJob?.jobRemarks });
          setLoading(false);
          break;
        }
        case "activate":
        case "deactivate": {
          setRefresh(true);
          break;
        }
        case "setStatus": {
          setLoading(false);
          setRefresh(true);
          setSnackBarState({
            ...snackBarState,
            open: true,
            msg: t("common:msg.updateSuccess"),
          });
        }
        default:
          break;
      }
    }
    // eslint-disable-next-line
  }, [isLoading, res, error, urlId]);

  const [snackBarState, setSnackBarState] = useState({
    open: false,
    vertical: "top",
    horizontal: "center",
    msg: t("listing:coJob.msg.deleteSuccess"),
    severity: "success",
  });
  const handleCloseSnackBar = () => {
    setSnackBarState({ ...snackBarState, open: false });
    setRefresh(false);
  };

  let snackBar = null;
  if (snackBarState.open) {
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

  const handleEventAdd = () => {
    setOpenAddPopUp(true);
    // history.push('/administrations/rateTable-management/new/-')
  };

  const handleEventUploadTemplete = () => {
    console.log("updaload");
    setOpenWarning(true);
    setWarningMessage(
      "For download/upload template it is to be implemented at later stage"
    );
  };

  const handleWarningAction = (e) => {
    setOpenWarning(false);
    setWarningMessage("");
  };

  return (
    <React.Fragment>
      {isLoading && <MatxLoading />}
      {snackBar}
      {confirmDialog}
      {confirm && confirm.jobId && (
        <ConfirmationDialog
          open={open}
          title={t("listing:coJob.popup.confirmation")}
          text={t("listing:coJob.msg.confirmation", {
            action: openDeleteConfirm?.action,
            id: confirm.jobId,
          })}
          onYesClick={() => handleDeleteHandler()}
          onConfirmDialogClose={() => setOpen(false)}
        />
      )}

      <C1ListPanel
        routeSegments={[
          { name: t("administration:rateTableManagement.breadCrumbs.list") },
        ]}
        guideId="clictruck.administration.ratetable.sp.list"
        title={t("administration:rateTableManagement.breadCrumbs.list")}
      >
        <C1DataTable
          url="/api/v1/clickargo/clictruck/administrator/ratetableSp"
          isServer={true}
          columns={columns}
          defaultOrder="rtDtCreate"
          defaultOrderDirection="desc"
          isRefresh={isRefresh}
          isShowToolbar
          isShowFilterChip
          isShowDownload={true}
          handleBuildBody={handleDownloadBuildBody}
          isShowPrint={true}
          isRowSelectable={false}
          viewTextFilter={
            <ButtonGroup
              color="primary"
              key="viewTextFilter"
              aria-label="outlined primary button group"
            >
              <C1FormButtons
                options={{
                  uploadTemplate: {
                    show: false,
                    eventHandler: handleEventUploadTemplete,
                  },
                  downloadTemplate: {
                    show: true,
                    eventHandler: handleEventUploadTemplete,
                  },
                  add: {
                    show: false,
                    eventHandler: handleEventAdd,
                  },
                }}
              />
            </ButtonGroup>
          }
        />
      </C1ListPanel>

      <C1PopUp
        title={t("common:remarks.title")}
        openPopUp={openRemarkDialog}
        setOpenPopUp={setOpenRemarkDialog}
        actionsEl={null}
      ></C1PopUp>

      <C1PopUp
        title={t("administration:rateTableManagement.listing.coFfSelect")}
        openPopUp={openAddPopUp}
        setOpenPopUp={setOpenAddPopUp}
        maxWidth={"sm"}
      >
        <Grid container spacing={3} alignItems="center">
          <Grid container item>
            <C1SelectField
              name="coFf"
              label={""}
              value={getValue(coFf)}
              required
              onChange={(e) => setCoFf(e?.target?.value)}
              isServer={true}
              options={{
                url: "/api/v1/clickargo/clictruck/selectOptions/getCoFfFilteredRtContracts",
                id: "accnId",
                desc: "accnName",
                isCache: false,
              }}
            />
          </Grid>
          <Grid container item>
            <C1Button
              text={t("listing:coJob.button.create")}
              color="primary"
              type="submit"
              disabled={coFf ? false : true}
              onClick={() =>
                history.push("/manage/ratetable-details/new/-", {
                  coFf: coFf,
                })
              }
            />
          </Grid>
        </Grid>
      </C1PopUp>

      <Dialog maxWidth="xs" open={openWarning}>
        <div className="p-8 text-center w-360 mx-auto">
          <h4 className="capitalize m-0 mb-2">{"Information"}</h4>
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

export default RateTableListSp;
