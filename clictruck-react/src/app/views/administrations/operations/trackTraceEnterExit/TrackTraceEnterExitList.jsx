import React from "react";
import { Backdrop, CircularProgress } from "@material-ui/core";
import Snackbar from "@material-ui/core/Snackbar";
import { makeStyles } from "@material-ui/core/styles";
import DataTable from "app/atomics/organisms/DataTable";
import C1Alert from "app/c1component/C1Alert";
import C1Information from "app/c1component/C1Information";
import C1ListPanel from "app/c1component/C1ListPanel";
import useHttp from "app/c1hooks/http";
import { customFilterDateDisplay, formatDate } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import ConfirmationDialog from "matx/components/ConfirmationDialog";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";

const useStyles = makeStyles((theme) => ({
  backdrop: {
    zIndex: 999999,
    color: "#fff",
  },
  amountCell: {
    justifyContent: "center",
    textAlign: "right",
    display: "flex",
    flex: 1,
  },
}));

const TrackTrackEnterExitList = ({}) => {
  const history = useHistory();
  const { t } = useTranslation([
    "buttons",
    "listing",
    "ffclaims",
    "common",
    "status",
    "payments",
  ]);
  const { isLoading, res, validation, error, urlId, sendRequest } = useHttp();
  const [showHistory, setShowHistory] = useState(false);
  const [filterBy, setFilterBy] = useState([
    { attribute: "history", value: "default" },
  ]);
  const [validationErrors, setValidationErrors] = useState({});
  const [openWarning, setOpenWarning] = useState(false);
  const [isRefresh, setRefresh] = useState(false);
  const [loading, setLoading] = useState(false);
  const [confirm, setConfirm] = useState({ id: null });
  const [open, setOpen] = useState(false);
  const [openActionConfirm, setOpenActionConfirm] = useState({
    action: null,
    open: false,
  });
  const [openSubmitConfirm, setOpenSubmitConfirm] = useState({
    action: null,
    open: false,
  });
  const [warningMessage, setWarningMessage] = useState({
    open: false,
    msg: "",
  });
  const [dataStatus, setDataStatus] = useState("");
  const { user } = useAuth();

  const bdClasses = useStyles();

  const columns = [
    {
      name: "jobPartyCoFf",
      label: t("listing:trackTrackEnterExit.COAccnId"),
    },
    {
      name: "tcoreAccnCO.accnName",
      label: t("listing:trackTrackEnterExit.COAccnName"),
    },
    {
      name: "jobPartyTo",
      label: t("listing:trackTrackEnterExit.TOAccnId"),
    },
    {
      name: "tcoreAccnTO.accnName",
      label: t("listing:trackTrackEnterExit.TOAccnName"),
    },
    {
      name: "trJob",
      label: t("listing:trackTrackEnterExit.jobId"),
    },
    {
      name: "rcdDtBillApproved",
      label: t("listing:trackTrackEnterExit.approveDate"),
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
      name: "address",
      label: t("listing:trackTrackEnterExit.Destination"),
    },
    {
      name: "schedulerTime",
      label: t("listing:trackTrackEnterExit.SchedulerTime"),
      options: {
        sort: true,
        filter: true,
        filterType: "custom",
        customBodyRender: (value, tableMeta, updateValue) => {
          if (tableMeta.rowData[tableMeta.rowData.length - 1]) {
            return formatDate(value, true);
          } else
            return <p style={{ color: "red" }}> {formatDate(value, true)} </p>;
        },
        filterOptions: {
          display: customFilterDateDisplay,
        },
      },
    },
    {
      name: "tliDtEnter30",
      label: t("listing:trackTrackEnterExit.EnterTime30"),
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
      name: "tliDtEnter100",
      label: t("listing:trackTrackEnterExit.EnterTime100"),
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
      name: "inSchedulerTime",
      label: "inSchedulerTime",
      options: {
        sort: false,
        filter: false,
        display: "none",
      },
    },
  ];

  useEffect(() => {
    if (!isLoading && !error && res && !validation) {
      setLoading(isLoading);
      if (urlId === "delete") {
        setLoading(false);
        setRefresh(true);
        setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
        setSnackBarState({
          ...snackBarState,
          open: true,
          msg: t("common:msg.deleteSuccess"),
        });
      }
    }

    if (error) {
      setLoading(false);
    }

    if (validation) {
      setValidationErrors({ ...validation });
      setLoading(false);

      //if validation contains SUBMIT API CALL FAILURE, prompt message
      // console.log(validation['Submit.API.call'])
      if (validation["Submit.API.call"]) {
        // alert(validation['Submit.API.call'])
        setOpenWarning(true);
        setWarningMessage(validation["Submit.API.call"]);
      }
    }
  }, [urlId, isLoading, res, error]);

  const [snackBarState, setSnackBarState] = useState({
    open: false,
    vertical: "top",
    horizontal: "center",
    msg: t("common:common.msg.deleted"),
    severity: "success",
  });

  const handleCloseSnackBar = () => {
    setSnackBarState({ ...snackBarState, open: false });
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
    } else if (action.toLowerCase() === "delete") {
      handleStoreDelete();
    }
  };

  const handleStoreDelete = () => {
    setLoading(true);
    setRefresh(false);
    sendRequest(
      `/api/v1/clickargo/clictruck/job/jobTermReq/${dataStatus?.id}`,
      "delete",
      "delete"
    );
  };

  const handleConfirmDelete = (id) => {
    setDataStatus({ ...dataStatus, ...{ status: "delete", id } });
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: "DELETE",
      open: true,
      msg: t("common:msg.deleteConfirm"),
    });
  };

  return (
    <>
      {snackBar}
      <C1ListPanel
        routeSegments={[
          {
            name: t("listing:trackTrackEnterExit.title"),
            path: "/administrations/trackTrackEnterExit/list",
          },
        ]}
        information={<C1Information information="manageUserListing" />}
        guideId="clictruck.administration.truck.performance.list"
        title={t("listing:trackTrackEnterExit.title")}
      >
        <DataTable
          url="/api/v1/clickargo/clictruck/administrator/vckctTrackLoc/"
          columns={columns}
          title=""
          defaultOrder="rcdDtBillApproved"
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
        />
      </C1ListPanel>

      <Backdrop open={loading} className={bdClasses.backdrop}>
        {" "}
        <CircularProgress color="inherit" />
      </Backdrop>
    </>
  );
};

export default withErrorHandler(TrackTrackEnterExitList);
