import { Grid } from "@material-ui/core";
import Snackbar from "@material-ui/core/Snackbar";
import { Description, DescriptionOutlined } from "@material-ui/icons";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import DataTable from "app/atomics/organisms/DataTable";
import C1Alert from "app/c1component/C1Alert";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1Warning from "app/c1component/C1Warning";
import useHttp from "app/c1hooks/http";
import { customFilterDateDisplay, formatDate } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import { MatxLoading } from "matx";
import { previewPDF } from "app/c1utils/utility";
import C1Information from "app/c1component/C1Information";
import C1ListPanel from "app/c1component/C1ListPanel";
import DoneOutlinedIcon from "@material-ui/icons/DoneOutlined";
import ConfirmationDialog from "matx/components/ConfirmationDialog";
import { ServiceTypes } from "app/c1utils/const";

const SageIntegrations = ({}) => {
  const { t } = useTranslation([
    "buttons",
    "listing",
    "ffclaims",
    "common",
    "status",
    "payments",
  ]);
  const { isLoading, res, error, urlId, sendRequest } = useHttp();
  const [showHistory, setShowHistory] = useState(false);
  const [fileName, setFileName] = useState(null);
  const [selectedId, setSelectedId] = useState(null);
  const [filterBy, setFilterBy] = useState([
    { attribute: "history", value: "default" },
  ]);
  const [isRefresh, setRefresh] = useState(false);
  const [refreshPage, setRefreshPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [warningMessage, setWarningMessage] = useState({
    open: false,
    msg: "",
  });
  const [openSubmitConfirm, setOpenSubmitConfirm] = useState({
    action: null,
    open: false,
  });

  const columns = [
    {
      name: "sintId",
      label: "Id",
      options: {
        filter: true,
        display: true,
      },
    },
    {
      name: "sintDtStart",
      label: t("listing:sageIntegrations.dateStart"),
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
      name: "sintDtEnd",
      label: t("listing:sageIntegrations.dateEnd"),
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
      name: "TCkCtMstSageIntType.sitId",
      label: t("listing:sageIntegrations.fileType"),
    },
    {
      name: "sintNoRecords",
      label: t("listing:sageIntegrations.noRecords"),
      options: {
        filter: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          return value ? value : "-";
        },
      },
    },
    {
      name: "sintNoSuccess",
      label: t("listing:sageIntegrations.noSuccess"),
      options: {
        filter: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          return value ? value : "-";
        },
      },
    },
    {
      name: "sintNoFail",
      label: t("listing:sageIntegrations.noFailed"),
      options: {
        filter: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          return value ? value : "-";
        },
      },
    },
    {
      name: "sintLocExport",
      label: "sintLocExport",
      options: {
        filter: false,
        display: "excluded",
      },
    },
    {
      name: "sintLocImport",
      label: "sintLocImport",
      options: {
        filter: false,
        display: "excluded",
      },
    },
    {
      name: "TCkCtMstSageIntState.sisId",
      label: t("listing:sageIntegrations.status"),
    },
    {
      name: "action",
      label: "Download",
      options: {
        filter: false,
        sort: false,
        display: true,
        viewColumns: false,
        setCellHeaderProps: () => {
          return { style: { justifyContent: "center" } };
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          const sintLocExport = tableMeta.rowData[7];
          const sintLocImport = tableMeta.rowData[8];

          return (
            <Grid
              container
              justifyContent="flex-start"
              spacing={1}
              alignItems="center"
              style={{ minWidth: 130 }}
            >
              <Grid item>
                <C1LabeledIconButton
                  tooltip={`ClicTruck`}
                  label={`ClicTruck`}
                  action={(e) => handleDownloadFile(sintLocExport)}
                >
                  <DescriptionOutlined />
                </C1LabeledIconButton>
              </Grid>
              {sintLocImport && (
                <Grid item>
                  <C1LabeledIconButton
                    tooltip={`SAGE`}
                    label={`SAGE`}
                    action={(e) => handleDownloadFile(sintLocImport)}
                  >
                    <Description />
                  </C1LabeledIconButton>
                </Grid>
              )}
            </Grid>
          );
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
        customHeadLabelRender: (columnMeta) => {
          return <div style={{ textAlign: "center" }}>{columnMeta.label}</div>;
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          const id = tableMeta.rowData[0];
          const status = tableMeta.rowData[9];

          return (
            <Grid
              container
              direction="row"
              justifyContent="center"
              alignItems="center"
              style={{ minWidth: 120 }}
            >
              <Grid item xs={4}>
                {status === "SUBMITTED" && (
                  <C1LabeledIconButton
                    tooltip={"APPROVE"}
                    label={"APPROVE"}
                    action={(e) => handleConfirm(id, "approve")}
                  >
                    <DoneOutlinedIcon />
                  </C1LabeledIconButton>
                )}
              </Grid>
              <Grid item xs={4}>
                {status === "ERROR" && (
                  <C1LabeledIconButton
                    tooltip={"Acknowledge"}
                    label={"Acknowledge"}
                    action={(e) => handleConfirm(id, "acknowledge")}
                  >
                    <DoneOutlinedIcon />
                  </C1LabeledIconButton>
                )}
              </Grid>
            </Grid>
          );
        },
      },
    },
  ];

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

  useEffect(() => {
    if (!isLoading && !error && res) {
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

  const handleCloseSnackBar = () => {
    setSnackBarState({ ...snackBarState, open: false });
  };

  const handleDownloadFile = (filePath) => {
    let filePathArray = filePath.split("/");
    setFileName(filePathArray[filePathArray.length - 1]);

    let base64Filename = Buffer.from(filePath).toString("base64");
    const dlApi = `/api/v1/clickargo/clicktruck/sage/download/${base64Filename}`;
    sendRequest(dlApi, "downloadFile", "GET");
  };

  const handleApproveClick = () => {
    const dlApi = `/api/v1/clickargo/clicktruck/sage/route/${selectedId}/APPROVE`;
    sendRequest(dlApi, "approve", "GET");
  };
  const handleAcknowledgeClick = () => {
    const dlApi = `/api/v1/clickargo/clicktruck/sage/route/${selectedId}/COMPLETE`;
    sendRequest(dlApi, "acknowledge", "GET");
  };
  const handleConfirm = (id, action) => {
    setSelectedId(id);
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: action,
      open: true,
      msg: t("common:msg.deleteConfirm"),
    });
  };
  const eventHandler = (action) => {
    if (action.toLowerCase() === "approve") {
      handleApproveClick();
    } else if (action.toLowerCase() === "acknowledge") {
      handleAcknowledgeClick();
    }
  };

  useEffect(() => {
    if (!isLoading && !error && res) {
      if (urlId === "downloadFile") {
        previewPDF(fileName, res?.data);
      }
      if (urlId === "approve") {
        setLoading(false);
        setRefresh(true);
        setRefreshPage(refreshPage + 1);
        setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
        setSnackBarState({
          ...snackBarState,
          open: true,
          msg: "Approve Successful!",
        });
      }
      if (urlId === "acknowledge") {
        setLoading(false);
        setRefresh(true);
        setRefreshPage(++refreshPage);
        setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
        setSnackBarState({
          ...snackBarState,
          open: true,
          msg: "Acknowledge Successful!",
        });
      }
    }
  }, [urlId, isLoading, error, res]);

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

  return (
    <>
      {loading && <MatxLoading />}
      {snackBar}
      {confirmDialog}
      <C1ListPanel
        routeSegments={[
          {
            name: "Sage Integrations",
            path: "/applications/services/gli/sage-integrations",
          },
        ]}
        information={<C1Information information="manageUserListing" />}
        guideId="clicdo.truck.users.list"
        title="Sage Integrations"
      >
        <DataTable
          url="/api/v1/clickargo/clictruck/administrator/sageIntegrartion"
          columns={columns}
          title=""
          defaultOrder="id"
          defaultOrderDirection="desc"
          isServer={true}
          isShowViewColumns={true}
          isShowDownload={true}
          isShowPrint={true}
          isShowFilter={true}
          isRefresh={isRefresh}
          isShowFilterChip
          filterBy={[...filterBy, ...[{attribute: "TCkMstServiceType.svctId", value: ServiceTypes.CLICTRUCK.code}]]}
          guideId={""}
          showActiveHistoryButton={toggleHistory}
          customRowsPerPage={[10, 20]}
        />

        <C1Warning
          warningMessage={warningMessage}
          handleWarningAction={handleWarningAction}
        />
      </C1ListPanel>
    </>
  );
};

export default withErrorHandler(SageIntegrations);
