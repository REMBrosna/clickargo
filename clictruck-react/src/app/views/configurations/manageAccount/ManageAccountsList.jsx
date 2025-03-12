import { Grid, Snackbar } from "@material-ui/core";
import { DeleteOutlined, EditOutlined } from "@material-ui/icons";
import { Add } from "@material-ui/icons";
import BlockOutlinedIcon from "@material-ui/icons/BlockOutlined";
import PauseCircleOutlineOutlinedIcon from "@material-ui/icons/PauseCircleOutlineOutlined";
import SettingsBackupRestoreOutlinedIcon from "@material-ui/icons/SettingsBackupRestoreOutlined";
import VisibilityIcon from "@material-ui/icons/VisibilityOutlined";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import ChipStatus from "app/atomics/atoms/ChipStatus";
import DataTable from "app/atomics/organisms/DataTable";
import C1Alert from "app/c1component/C1Alert";
import C1Information from "app/c1component/C1Information";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1ListPanel from "app/c1component/C1ListPanel";
import {
  AccountsProcessStates,
  AccountStatus,
  AccountTypes,
  Actions,
  RecordStatus,
  Roles,
} from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import {
  customFilterDateDisplay,
  encodeString,
  formatDate,
  isFinanceApprover,
  isSpL1,
} from "app/c1utils/utility";
import useAuth from "app/hooks/useAuth";
import history from "history.js";
import { ConfirmationDialog, MatxLoading } from "matx";

import useHttp from "../../../c1hooks/http";

/** @description Listing for Account Management */
const ManageAccountsList = () => {
  const { t } = useTranslation(["admin", "buttons", "common"]);

  const [isRefresh, setRefresh] = useState(false);
  const [loading, setLoading] = useState(false);
  const [tableLoading, setTableLoading] = useState(false);
  const { isLoading, res, error, urlId, sendRequest } = useHttp();

  const { user } = useAuth();
  const isLevel1 = isSpL1([user.authorities]);
  const isFinanceHead = isFinanceApprover([user.authorities]);

  const [snackBarOptions, setSnackBarOptions] = useState({
    open: false,
    vertical: "top",
    horizontal: "center",
    msg: "",
    severity: "success",
  });

  let showAdd = false;
  if (
    user?.authorities.some((el) => [Roles.SP_L1.code].includes(el.authority))
  ) {
    showAdd = true;
  }

  const [showHistory, setShowHistory] = useState(false);
  const [filterBy, setFilterBy] = useState([
    { attribute: "history", value: "default" },
  ]);

  const [accountId, setAccountId] = useState("");
  const [openSubmitConfirm, setOpenSubmitConfirm] = useState({
    action: null,
    open: false,
  });

  useEffect(() => {
    if (showHistory) {
      setFilterBy([{ attribute: "history", value: "history" }]);
    } else {
      setFilterBy([{ attribute: "history", value: "default" }]);
    }
  }, [showHistory]);

  useEffect(() => {
    if (!error && res) {
      if (urlId === "getForDeActive") {
        const bodyReq = { ...res.data, accnStatus: "I" };
        sendRequest(
          "/api/co/ccm/entity/accn/" + res.data.accnId,
          "deActive",
          "put",
          bodyReq
        );
      } else if (urlId === "getForActive") {
        const bodyReq = { ...res.data, accnStatus: "A" };
        sendRequest(
          "/api/co/ccm/entity/accn/" + res.data.accnId,
          "deActive",
          "put",
          bodyReq
        );
      }
      if (urlId === "active" || urlId === "deActive") {
        setRefresh(true);
        setSnackBarOptions({
          ...snackBarOptions,
          open: true,
          message:
            urlId === "active"
              ? t("admin:account.msg.activatedSuccess")
              : t("admin:account.msg.deactivatedSuccess"),
        });
      }
      if (urlId === "getAccnForSuspend") {
        sendRequest(
          `/api/v1/clickargo/manageaccn/${accountId}/suspend`,
          "accnSuspend",
          "PUT",
          { accnDetails: { ...res?.data?.accnDetails }, action: "SUBMIT" }
        );
      }
      if (urlId === "accnSuspend") {
        setRefresh(true);
        setLoading(false);
        setSnackBarOptions({
          ...snackBarOptions,
          open: true,
          message: t("admin:account.msg.accnSuspended"),
        });
      }
      if (urlId === "getAccnForTerminate") {
        sendRequest(
          `/api/v1/clickargo/manageaccn/${accountId}/terminate`,
          "accnTerminate",
          "PUT",
          { accnDetails: { ...res?.data?.accnDetails }, action: "SUBMIT" }
        );
      }
      if (urlId === "accnTerminate") {
        setRefresh(true);
        setLoading(false);
        setSnackBarOptions({
          ...snackBarOptions,
          open: true,
          message: t("admin:account.msg.accnTerminate"),
        });
      }
      if (urlId === "deleteAccn") {
        setRefresh(true);
        setLoading(false);
        setSnackBarOptions({
          ...snackBarOptions,
          open: true,
          message: t("admin:account.msg.deleted"),
        });
        setOpenSubmitConfirm({
          ...openSubmitConfirm,
          action: null,
          open: false,
        });
      }
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isLoading, res, error, urlId]);

  const columns = [
    {
      name: "accnId", // field name in the row object
      label: t("admin:account.list.accnId"), // column title that will be shown in table
      options: {
        sort: true,
        filter: true,
      },
    },
    {
      name: "accnStatus",
      label: "",
      options: {
        display: "excluded",
        filter: false,
      },
    },
    {
      name: "TMstAccnType.atypDescription",
      label: t("admin:account.list.accnType"),
      options: {
        filter: true,
        filterType: "dropdown",
        filterOptions: {
          names: [
            AccountTypes.ACC_TYPE_CO.desc,
            AccountTypes.ACC_TYPE_FF.desc,
            AccountTypes.ACC_TYPE_TO.desc,
            AccountTypes.ACC_TYPE_SP.desc,
          ],
          renderValue: (v) => {
            switch (v) {
              case AccountTypes.ACC_TYPE_CO.desc:
                return AccountTypes.ACC_TYPE_CO.desc;
              case AccountTypes.ACC_TYPE_FF.desc:
                return AccountTypes.ACC_TYPE_FF.desc;
              case AccountTypes.ACC_TYPE_TO.desc:
                return AccountTypes.ACC_TYPE_TO.desc;
              case AccountTypes.ACC_TYPE_SP.desc:
                return AccountTypes.ACC_TYPE_SP.desc;
              default:
                break;
            }
          },
        },
        customFilterListOptions: {
          render: (v) => {
            switch (v) {
              case AccountTypes.ACC_TYPE_CO.desc:
                return AccountTypes.ACC_TYPE_CO.desc;
              case AccountTypes.ACC_TYPE_FF.desc:
                return AccountTypes.ACC_TYPE_FF.desc;
              case AccountTypes.ACC_TYPE_TO.desc:
                return AccountTypes.ACC_TYPE_TO.desc;
              case AccountTypes.ACC_TYPE_SP.desc:
                return AccountTypes.ACC_TYPE_SP.desc;
              default:
                break;
            }
          },
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          return value;
        },
      },
    },
    {
      name: "accnName",
      label: t("admin:account.list.accnName"),
    },
    {
      name: "accnCoyRegn",
      label: t("admin:account.list.accnCoyRegn"),
    },
    {
      name: "accnContact.contactTel",
      label: t("admin:account.list.accnPhone"),
    },
    {
      name: "accnContact.contactFax",
      label: t("admin:account.list.accnFax"),
      options: {
        display: "excluded",
        filter: false,
      },
    },
    {
      name: "accnContact.contactEmail",
      label: t("admin:account.list.accnEmail"),
    },
    {
      name: "accnDtCreate",
      label: t("admin:account.list.accnDtCreate"),
      options: {
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
          display: customFilterDateDisplay,
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          return formatDate(value, false);
        },
      },
    },
    {
      name: "accnUidCreate",
      label: t("admin:account.list.accnUidCreate"),
      options: {
        display: "excluded",
        filter: false,
      },
    },
    {
      name: "accnDtReg",
      label: t("admin:account.list.accnDtSubmitted"),
      options: {
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
          display: customFilterDateDisplay,
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          const accnStatus = tableMeta.rowData[1];
          return accnStatus === RecordStatus.NEW.code
            ? "-"
            : formatDate(value, false);
        },
      },
    },
    {
      name: "accnUidCreate",
      label: t("admin:account.list.accnUidSubmitted"),
      options: {
        display: "excluded",
        filter: false,
      },
    },
    {
      name: "accnStatus",
      label: t("admin:account.list.accnStatus"),
      options: {
        filter: true,
        filterType: "dropdown",
        filterOptions: {
          names: Object.keys(AccountsProcessStates),
          renderValue: (v) => {
            return AccountsProcessStates[v].desc;
          },
        },
        customFilterListOptions: {
          render: (v) => {
            return AccountsProcessStates[v].desc;
          },
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          let color = "#FF2E6C";
          switch (value) {
            case AccountsProcessStates.R.code:
              color = "#37B7FF";
              break;
            case AccountsProcessStates.P.code:
              color = "#229881";
              break;
            case AccountsProcessStates.V.code:
              color = "#D17100";
              break;
            case AccountsProcessStates.S.code:
              color = "#FF2E6C";
              break;
            case AccountsProcessStates.T.code:
            case AccountsProcessStates.X.code:
              color = "#969696";
              break;
            case AccountsProcessStates.Q.code:
              color = "#37B7FF";
              break;
            default:
              return getStatusDesc(value);
          }
          return (
            <ChipStatus
              text={AccountsProcessStates[value]?.desc}
              color={color}
            />
          );
        },
      },
    },
    {
      name: "action",
      label: " ",
      options: {
        filter: false,
        sort: false,
        display: true,
        viewColumns: false,
        customBodyRender: (value, tableMeta, updateValue) => {
          const accnId = tableMeta.rowData[0];
          const accnStatus = tableMeta.rowData[1];
          return (
            <Grid
              container
              direction="row"
              justifyContent="flex-start"
              alignItems="center"
              style={{ marginRight: "10px", minWidth: "200px" }}
            >
              <Grid container item justifyContent="center" spacing={1}>
                <Grid item xs={3}>
                  {[
                    AccountsProcessStates.N.code,
                    AccountsProcessStates.A.code,
                  ].includes(accnStatus) &&
                    isLevel1 && (
                      <C1LabeledIconButton
                        tooltip={t("buttons:edit")}
                        label={t("buttons:edit")}
                        action={() =>
                          history.push(
                            `/manageAccount/edit/${encodeString(accnId)}`
                          )
                        }
                      >
                        <EditOutlined />
                      </C1LabeledIconButton>
                    )}
                </Grid>

                {(isLevel1 ||
                  showHistory ||
                  [
                    AccountsProcessStates.P.code,
                    AccountsProcessStates.R.code,
                    AccountsProcessStates.V.code,
                  ].includes(accnStatus) ||
                  isFinanceHead) && (
                  <Grid item xs={3}>
                    <C1LabeledIconButton
                      tooltip={t("buttons:view")}
                      label={t("buttons:view")}
                      action={() =>
                        history.push(
                          `/manageAccount/view/${encodeString(accnId)}`
                        )
                      }
                    >
                      <VisibilityIcon />
                    </C1LabeledIconButton>
                  </Grid>
                )}

                {(isLevel1 || isFinanceHead) &&
                  AccountsProcessStates.N.code.includes(accnStatus) && (
                    <Grid item xs={3}>
                      <C1LabeledIconButton
                        tooltip={t("buttons:delete")}
                        label={t("buttons:delete")}
                        action={() => {
                          setOpenSubmitConfirm({
                            ...openSubmitConfirm,
                            action: "DELETE",
                            open: true,
                            id: accnId,
                          });
                        }}
                      >
                        <DeleteOutlined />
                      </C1LabeledIconButton>
                    </Grid>
                  )}

                <Grid item xs={3}>
                  {accnStatus === AccountStatus.SUS_APPROVED.code &&
                    isLevel1 && (
                      <C1LabeledIconButton
                        tooltip={t("buttons:unsuspend")}
                        label={t("buttons:unsuspend")}
                        action={() =>
                          history.push(
                            `/manageAccount/resumption/${encodeString(accnId)}`
                          )
                        }
                      >
                        <SettingsBackupRestoreOutlinedIcon />
                      </C1LabeledIconButton>
                    )}
                  {accnStatus === RecordStatus.ACTIVE.code && isLevel1 && (
                    <C1LabeledIconButton
                      tooltip={t("buttons:suspend")}
                      label={t("buttons:suspend")}
                      action={() =>
                        history.push(
                          `/manageAccount/suspend/${encodeString(accnId)}`
                        )
                      }
                    >
                      <PauseCircleOutlineOutlinedIcon />
                    </C1LabeledIconButton>
                  )}
                </Grid>

                <Grid item xs={3}>
                  {accnStatus === RecordStatus.ACTIVE.code && isLevel1 && (
                    <C1LabeledIconButton
                      tooltip={t("buttons:terminate")}
                      label={t("buttons:terminate")}
                      action={() =>
                        history.push(
                          `/manageAccount/terminate/${encodeString(accnId)}`
                        )
                      }
                    >
                      <BlockOutlinedIcon />
                    </C1LabeledIconButton>
                  )}
                </Grid>
              </Grid>
            </Grid>
          );
        },
      },
    },
  ];

  const toggleHistory = (filter) => {
    setLoading(true);
    setRefresh(false);
    setShowHistory(filter === "history" ? true : false);
    setTimeout(() => setRefresh(true), 500);
    setTimeout(() => setLoading(false), 500);
  };

  const handleSuspend = (id) => {
    setLoading(true);
    setRefresh(false);
    setAccountId(id);
    sendRequest(
      `/api/v1/clickargo/manageaccn/${id}`,
      "getAccnForSuspend",
      "GET"
    );
  };

  const handleTerminate = (id) => {
    setLoading(true);
    setRefresh(false);
    setAccountId(id);
    sendRequest(
      `/api/v1/clickargo/manageaccn/${id}`,
      "getAccnForTerminate",
      "GET"
    );
  };

  const handleDeActiveHandler = (id) => {
    setRefresh(false);
    sendRequest(`/api/co/ccm/entity/accn/${id}`, "getForDeActive", "get", {});
  };

  const handleActiveHandler = (id) => {
    setRefresh(false);
    sendRequest(`/api/co/ccm/entity/accn/${id}`, "getForActive", "get", {});
  };

  const handleSnackBarClose = () => {
    setSnackBarOptions({ ...snackBarOptions, open: false });
  };

  const handleEventAddAccount = () => {
    history.push("/manageAccount/new/-");
  };
  const handleAction = (action, id) => {
    setLoading(true);
    switch (action) {
      case "DELETE":
        handleDelete(id);
        break;
      default:
        console.log("handleAction", action);
        break;
    }
  };

  const handleConfirm = () => {};

  const handleDelete = (id) => {
    setRefresh(false);
    sendRequest(`/api/co/ccm/entity/accn/${id}`, "deleteAccn", "DELETE");
  };

  let snackBar = null;
  if (snackBarOptions && snackBarOptions && snackBarOptions.open) {
    const anchorOriginV = snackBarOptions.vertical;
    const anchorOriginH = snackBarOptions.horizontal;

    snackBar = (
      <Snackbar
        anchorOrigin={{ vertical: anchorOriginV, horizontal: anchorOriginH }}
        open={snackBarOptions.open}
        onClose={handleSnackBarClose}
        autoHideDuration={snackBarOptions.severity === "success" ? 2000 : 3000}
        key={anchorOriginV + anchorOriginH}
      >
        <C1Alert
          onClose={handleSnackBarClose}
          severity={snackBarOptions.severity}
        >
          {snackBarOptions.message}
        </C1Alert>
      </Snackbar>
    );
  }

  return (
    <React.Fragment>
      {loading && <MatxLoading />}
      {snackBar}
      <C1ListPanel
        routeSegments={[{ name: t("account.list.headerAll") }]}
        information={<C1Information information="manageAccountListing" />}
        guideId="clicdo.truck.users.list"
        title={t("account.list.headerAll")}
      >
        <DataTable
          url="/api/v1/clickargo/manageaccn"
          isServer={true}
          columns={columns}
          isRefresh={isRefresh}
          // title={t("account.list.headerAll")}
          defaultOrder="accnDtCreate"
          defaultOrderDirection="desc"
          showDownload={false}
          showPrint={false}
          filterBy={filterBy}
          isShowFilterChip={true}
          showAddButton={
            showAdd
              ? [
                  {
                    label: t("admin:account.list.newAccn").toUpperCase(),
                    action: () => handleEventAddAccount(),
                    icon: <Add />,
                  },
                ]
              : null
          }
          showActiveHistoryButton={toggleHistory}
        />
      </C1ListPanel>
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
        onYesClick={() => {
          handleAction(openSubmitConfirm?.action, openSubmitConfirm?.id);
        }}
      />
    </React.Fragment>
  );
};

export default ManageAccountsList;
