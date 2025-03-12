import { Button, ButtonGroup, Dialog, Grid, Snackbar } from "@material-ui/core";
import {
  EditOutlined,
  LinkOffOutlined,
  LinkOutlined,
  VisibilityOutlined,
} from "@material-ui/icons";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import C1Alert from "app/c1component/C1Alert";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1ListPanel from "app/c1component/C1ListPanel";
import useHttp from "app/c1hooks/http";
import { RecordStatus } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import { customFilterDateDisplay, formatDate } from "app/c1utils/utility";
import useAuth from "app/hooks/useAuth";
import history from "history.js";
import { ConfirmationDialog, MatxLoading } from "matx";
import DataTable from "app/atomics/organisms/DataTable";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import { colorCodes, displayColor } from "./deptconst";
import ChipStatus from "app/atomics/atoms/ChipStatus";

const DepartmentsList = () => {
  const { t } = useTranslation([
    "buttons",
    "administration",
    "common",
    "listing",
  ]);

  const [showHistory, setShowHistory] = useState(false);
  const [filterBy, setFilterBy] = useState([
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
  const { user } = useAuth();
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
    setRefresh(false);
    setDataStatus({ ...dataStatus, ...{ status: "active", id } });
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: "SAVE",
      open: true,
      msg: t("common:msg.activeConfirm"),
    });
  };

  const handleConfirmSetInActive = (id) => {
    setRefresh(false);
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
      "/api/v1/clickargo/clictruck/administrator/department/" +
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
      name: "deptId",
      label: "Name",
      options: {
        display: "excluded",
        filter: false,
      },
    },
    {
      name: "tcoreAccn.accnId",
      label: "",
      options: {
        display: "excluded",
        filter: false,
      },
    },
    {
      name: "deptName",
      label: t("administration:department.listing.name"),
      options: {
        filter: true,
        sort: true,
      },
    },
    {
      name: "deptDesc",
      label: t("administration:department.listing.desc"),
      options: {
        filter: true,
        sort: true,
      },
    },
    {
      name: "deptColor",
      label: t("administration:department.listing.colorCode"),
      options: {
        filter: true,
        filterType: "dropdown",
        filterOptions: {
          names: colorCodes?.map((el) => el?.code),
          renderValue: (v) => {
            console.log("value: ", v);
            return colorCodes?.find((el) => el?.code === v).value;
          },
        },
        customFilterListOptions: {
          render: (v) => {
            console.log("customFilterListOptions", v);
            return colorCodes?.find((el) => el?.code === v).value;
          },
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          return displayColor(value);
        },
      },
    },
    {
      name: "deptDtCreate",
      label: t("administration:department.listing.dtCreated"),
      options: {
        filter: true,
        display: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          return formatDate(value, false);
        },
        filterType: "custom",
        filterOptions: {
          display: customFilterDateDisplay,
        },
      },
    },
    {
      name: "deptDtLupd",
      label: t("administration:department.listing.dtLupd"),
      options: {
        filter: true,
        display: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          return formatDate(value, false);
        },
        filterType: "custom",
        filterOptions: {
          display: customFilterDateDisplay,
        },
      },
    },
    {
      name: "deptStatus",
      label: t("administration:department.listing.status"),
      options: {
        filter: true,
        display: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          let statusText = "";
          let statusColor = "";

          switch (value) {
            case RecordStatus.ACTIVE.code:
              statusText = RecordStatus.ACTIVE.desc;
              statusColor = "#00D16D";
              break;
            case RecordStatus.INACTIVE.code:
              statusText = RecordStatus.INACTIVE.desc;
              statusColor = "#FF2E6C";
              break;
            case RecordStatus.SUSPENDED.code:
              statusText = RecordStatus.SUSPENDED.desc;
              statusColor = "#FF2E6C";
              break;
            case "D":
              statusText = "Deleted";
              statusColor = "#dadce1";
              break;
            default:
              statusText = "New";
              statusColor = "#37B7FF";
              break;
          }

          return <ChipStatus text={statusText} color={statusColor} />;
        },
        filterType: "dropdown",
        filterOptions: {
          names: [
            RecordStatus.ACTIVE.code,
            RecordStatus.INACTIVE.code,
            RecordStatus.NEW.code,
            RecordStatus.SUSPENDED.code,
            "D",
          ],
          renderValue: (v) => {
            switch (v) {
              case RecordStatus.ACTIVE.code:
                return RecordStatus.ACTIVE.desc;
              case RecordStatus.INACTIVE.code:
                return RecordStatus.INACTIVE.desc;
              case RecordStatus.NEW.code:
                return RecordStatus.NEW.desc;
              case RecordStatus.SUSPENDED.code:
                return RecordStatus.SUSPENDED.desc;
              case "D":
                return "Deleted";
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
              case RecordStatus.NEW.code:
                return RecordStatus.NEW.desc;
              case RecordStatus.SUSPENDED.code:
                return RecordStatus.SUSPENDED.desc;
              case "D":
                return "Deleted";
              default:
                break;
            }
          },
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
        customHeadLabelRender: (columnMeta) => {
          return <div style={{ textAlign: "center" }}>{columnMeta.label}</div>;
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          const status = tableMeta.rowData[7];
          const id = tableMeta.rowData[0];
          return (
            <Grid
              container
              direction="row"
              justifyContent="space-between"
              alignItems="center"
              style={{ minWidth: 180 }}
            >
              <Grid container direction="row" justifyContent="space-between">
                <Grid container item alignItems="center">
                  {!showHistory && (
                    <Grid item xs={4}>
                      <C1LabeledIconButton
                        tooltip={t("buttons:edit")}
                        label={t("buttons:edit")}
                        action={() =>
                          history.push(`/administrations/department/edit/${id}`)
                        }
                      >
                        <EditOutlined />
                      </C1LabeledIconButton>
                    </Grid>
                  )}
                  <Grid item xs={4}>
                    {!showHistory && status === RecordStatus.ACTIVE.code && (
                      <C1LabeledIconButton
                        tooltip={t("buttons:deactivate")}
                        label={t("buttons:deactivate")}
                        action={() => handleConfirmSetInActive(id)}
                      >
                        <LinkOffOutlined />
                      </C1LabeledIconButton>
                    )}

                    {((!showHistory && status === RecordStatus.INACTIVE.code) ||
                      (showHistory && status !== "D")) && (
                      <C1LabeledIconButton
                        tooltip={t("buttons:activate")}
                        label={t("buttons:activate")}
                        action={() => handleConfirmSetActive(id)}
                      >
                        <LinkOutlined />
                      </C1LabeledIconButton>
                    )}
                  </Grid>
                  <Grid item xs={4}>
                    <C1LabeledIconButton
                      tooltip={t("buttons:view")}
                      label={t("buttons:view")}
                      action={() =>
                        history.push(`/administrations/department/view/${id}`)
                      }
                    >
                      <VisibilityOutlined />
                    </C1LabeledIconButton>
                  </Grid>
                </Grid>
              </Grid>
            </Grid>
          );
        },
      },
    },
  ];

  const handleWarningAction = (e) => {
    setOpenWarning(false);
    setWarningMessage("");
  };

  const toggleHistory = (filter) => {
    setLoading(true);
    setRefresh(false);
    setShowHistory(filter === "history" ? true : false);
    setTimeout(() => setRefresh(true), 500);
    setTimeout(() => setLoading(false), 500);
  };

  const handleAddDepartment = () => {
    history.push("/administrations/department/new/-");
  };

  useEffect(() => {
    if (!isLoading && !error && res) {
      setLoading(isLoading);
      switch (urlId) {
        case "delete":
          setRefresh(true);
          setFilterBy([{ attribute: "history", value: "default" }]);
          setSnackBarState({ ...snackBarState, open: true });

          break;
        case "activate":
        case "deactivate": {
          setRefresh(true);
          break;
        }

        case "setStatus": {
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

  useEffect(() => {
    if (showHistory) {
      setFilterBy([{ attribute: "history", value: "history" }]);
    } else {
      setFilterBy([{ attribute: "history", value: "default" }]);
    }
  }, [showHistory]);

  return (
    <React.Fragment>
      {isLoading && <MatxLoading />}
      {confirmDialog}
      {snackBar}
      <C1ListPanel
        routeSegments={[{ name: t("administration:department.listing.title") }]}
        guideId="null"
        title={t("administration:department.listing.title")}
      >
        <DataTable
          url={"/api/v1/clickargo/clictruck/administrator/department"}
          isServer={true}
          columns={columns}
          defaultOrder="deptDtCreate"
          defaultOrderDirection="desc"
          isRefresh={isRefresh}
          isShowToolbar
          isShowFilterChip
          isShowDownload={true}
          isShowPrint={true}
          isRowSelectable={false}
          filterBy={filterBy}
          showActiveHistoryButton={toggleHistory}
          viewTextFilter={
            <ButtonGroup
              color="primary"
              key="viewTextFilter"
              aria-label="outlined primary button group"
            >
              <C1FormButtons
                options={{
                  add: {
                    show: true,
                    eventHandler: handleAddDepartment,
                  },
                }}
              />
            </ButtonGroup>
          }
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

export default withErrorHandler(DepartmentsList);
