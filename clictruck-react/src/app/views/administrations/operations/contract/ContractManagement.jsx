import { Button, Dialog, Grid, Snackbar } from "@material-ui/core";
import { green, red } from "@material-ui/core/colors";
import { EditOutlined, VisibilityOutlined } from "@material-ui/icons";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import C1Alert from "app/c1component/C1Alert";
import C1DataTable from "app/c1component/C1DataTable";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1ListPanel from "app/c1component/C1ListPanel";
import useHttp from "app/c1hooks/http";
import { Status } from "app/c1utils/const";
import { customFilterDateDisplay, formatDate } from "app/c1utils/utility";
import history from "history.js";
import { ConfirmationDialog, MatxLoading } from "matx";

const ContractManagement = () => {
  const { t } = useTranslation([
    "buttons",
    "listing",
    "administration",
    "common",
    "cargoowners",
  ]);
  const [filterHistory, setFilterHistory] = useState([
    { attribute: "history", value: "default" },
  ]);
  const { isLoading, res, error, urlId, sendRequest } = useHttp();
  const [dataStatus, setDataStatus] = useState("");
  const [openSubmitConfirm, setOpenSubmitConfirm] = useState({
    action: null,
    open: false,
  });
  const [loading, setLoading] = useState(true);
  const [isRefresh, setRefresh] = useState(false);
  const [snackBarState, setSnackBarState] = useState({
    open: false,
    vertical: "top",
    horizontal: "center",
    msg: t("common:msg.deleteSuccess"),
    severity: "success",
  });
  const [openWarning, setOpenWarning] = useState(false);
  const [warningMessage, setWarningMessage] = useState("");

  const handleCloseSnackBar = () => {
    setRefresh(false);
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

  const handleConfirmSetActive = (id) => {
    console.log("id", id);
    setDataStatus({ ...dataStatus, ...{ status: "active", id } });
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: "SAVE",
      open: true,
      msg: t("common:msg.activeConfirm"),
    });
  };

  const handleConfirmSetInActive = (id) => {
    console.log("id", id);
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
      "/api/v1/clickargo/clictruck/administrator/contract/" +
        dataStatus.id +
        "/" +
        dataStatus.status,
      "setStatus",
      "put",
      null
    );
  };

  const eventHandler = (action) => {
    if (action.toLowerCase() === "save") {
      handleSubmitStatus();
    }
  };

  const columns = [
    {
      name: "conId",
      label: t("listing:common.id"),
      options: {
        display: "excluded",
        filter: false,
        sort: false,
      },
    },
    {
      name: "tcoreAccnByConTo.accnName",
      label: t("listing:contract.truckOperator"),
    },
    {
      name: "tcoreAccnByConCoFf.accnName",
      label: t("listing:contract.freightForwader"),
    },
    {
      name: "conDtStart",
      label: t("listing:common.startDate"),
      options: {
        filter: true,
        display: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          return formatDate(value, true);
        },
        filterType: "custom",
        filterOptions: {
          display: customFilterDateDisplay,
        },
      },
    },
    {
      name: "conDtEnd",
      label: t("listing:common.expiryDate"),
      options: {
        filter: true,
        display: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          return formatDate(value, true);
        },
        filterType: "custom",
        filterOptions: {
          display: customFilterDateDisplay,
        },
      },
    },
    {
      name: "conDtCreate",
      label: t("listing:common.dateCreated"),
      options: {
        filter: true,
        display: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          return formatDate(value, true);
        },
        filterType: "custom",
        filterOptions: {
          display: customFilterDateDisplay,
        },
      },
    },
    {
      name: "conDtLupd",
      label: t("listing:common.dateUpdated"),
      options: {
        filter: false,
        display: false,
        customBodyRender: (value, tableMeta, updateValue) => {
          return formatDate(value, true);
        },
        filterType: "custom",
        filterOptions: {
          display: customFilterDateDisplay,
        },
      },
    },
    {
      name: "conStatus",
      label: t("listing:common.status"),
      options: {
        filter: true,
        display: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          switch (value) {
            case Status.ACV.code:
              return (
                <small
                  className="px-3 py-6px border-radius-4"
                  style={{ backgroundColor: green[500], color: green[50] }}
                >
                  {Status.ACV.desc}
                </small>
              );
            case Status.NCV.code:
              return (
                <small
                  className="px-3 py-6px border-radius-4"
                  style={{ backgroundColor: red[500], color: red[50] }}
                >
                  {Status.NCV.desc}
                </small>
              );
            default:
              return (
                <small
                  className="px-3 py-6px border-radius-4"
                  style={{ backgroundColor: red[500], color: red[50] }}
                >
                  {Status.NCV.desc}
                </small>
              );
          }
        },
        filterType: "dropdown",
        filterOptions: {
          names: ["A", "I"],
          renderValue: (v) => {
            switch (v) {
              case "A":
                return "Active";
              case "I":
                return "InActive";
              default:
                break;
            }
          },
          customFilterListOptions: {
            render: (v) => {
              switch (v) {
                case "A":
                  return "Active";
                case "I":
                  return "InActive";
                default:
                  break;
              }
            },
          },
        },
      },
    },
    {
      name: "editable",
      label: "",
      options: {
        display: "excluded",
        filter: false,
        sort: false,
      },
    },
    {
      name: "conId",
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
          const isEditable = tableMeta.rowData[8];
          const id = tableMeta.rowData[0];
          return (
            <Grid
              container
              direction="row"
              justifyContent="flex-start"
              alignItems="center"
              style={{ minWidth: 120 }}
            >
              <Grid container item justifyContent="center" spacing={2}>
                {isEditable && false && (
                  <Grid item xs={4}>
                    <span style={{ minWidth: "48px" }}>
                      <C1LabeledIconButton
                        tooltip={t("buttons:edit")}
                        label={t("buttons:edit")}
                        action={() =>
                          history.push({
                            pathname: `/administrations/contract-management/edit/${id}`,
                            state: { from: "/opadmin/contracts" },
                          })
                        }
                      >
                        <EditOutlined />
                      </C1LabeledIconButton>
                    </span>
                  </Grid>
                )}
                {/* <Grid item xs={4}>
                                        <span style={{ minWidth: '48px' }}>
                                            {(status === Status.ACV.code) &&
                                                <C1LabeledIconButton
                                                    tooltip={t("buttons:deactivate")}
                                                    label={t("buttons:deactivate")}
                                                    action={() => handleConfirmSetInActive(id)}>
                                                    <LinkOffOutlined color="primary" />
                                                </C1LabeledIconButton>
                                            }

                                            {(status !== Status.ACV.code) &&
                                                <C1LabeledIconButton
                                                    tooltip={t("buttons:activate")}
                                                    label={t("buttons:activate")}
                                                    action={() => handleConfirmSetActive(id)}>
                                                    <LinkOutlined color="primary" />
                                                </C1LabeledIconButton>
                                            }
                                        </span>
                                    </Grid> */}
                <Grid item xs={4}>
                  <span style={{ minWidth: "48px" }}>
                    <C1LabeledIconButton
                      tooltip={t("buttons:view")}
                      label={t("buttons:view")}
                      action={() =>
                        history.push({
                          pathname: `/administrations/contract-management/view/${id}`,
                          state: {
                            from: isEditable
                              ? "/opadmin/contracts"
                              : "/administrations/contract-management/list",
                          },
                        })
                      }
                    >
                      <VisibilityOutlined />
                    </C1LabeledIconButton>
                  </span>
                </Grid>
              </Grid>
            </Grid>
          );
        },
      },
    },
  ];

  const handleEventDownloadTemplete = () => {
    console.log("download");
  };

  const handleEventUploadTemplete = () => {
    console.log("updaload");
    setOpenWarning(true);
    setWarningMessage(
      "For download/upload template it is to be implemented at later stage"
    );
  };

  const handleEventAdd = () => {
    history.push("/administrations/contract-management/new/-");
  };

  const handleWarningAction = (e) => {
    setOpenWarning(false);
    setWarningMessage("");
  };

  useEffect(() => {
    if (!isLoading && !error && res) {
      switch (urlId) {
        case "delete":
          setRefresh(true);
          setFilterHistory([{ attribute: "history", value: "default" }]);
          setSnackBarState({ ...snackBarState, open: true });
          setLoading(false);
          break;
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
  }, [isLoading, res, error, urlId]);

  return (
    <React.Fragment>
      {isLoading && <MatxLoading />}
      {confirmDialog}
      {snackBar}
      <C1ListPanel
        routeSegments={[{ name: t("administration:contractManagement.title") }]}
        guideId="clicdo.doi.co.jobs.list"
        title={t("administration:contractManagement.title")}
      >
        <C1DataTable
          url={"/api/v1/clickargo/clictruck/administrator/contract"}
          isServer={true}
          columns={columns}
          defaultOrder="conDtCreate"
          defaultOrderDirection="desc"
          isRefresh={isRefresh}
          isShowToolbar
          isShowFilterChip
          isShowDownload={true}
          isShowPrint={true}
          filterBy={
            history?.location?.pathname === "/opadmin/contracts"
              ? [{ attribute: "forCsView", value: true }]
              : null
          }
          isRowSelectable={false}
          guideId="clicdo.doi.co.jobs.list.table"
        />
      </C1ListPanel>

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

export default ContractManagement;
