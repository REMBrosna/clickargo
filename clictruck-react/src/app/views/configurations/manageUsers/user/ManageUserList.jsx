import { Grid, Snackbar, Typography } from "@material-ui/core";
import {
  Add,
  EditOutlined,
  LinkOffOutlined,
  LinkOutlined,
  VisibilityOutlined,
} from "@material-ui/icons";
import moment from "moment/moment";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import GridActionButton from "app/atomics/organisms/GridActionButton";
import C1Alert from "app/c1component/C1Alert";
import C1DataTable from "app/c1component/C1DataTable";
import C1Information from "app/c1component/C1Information";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1ListPanel from "app/c1component/C1ListPanel";
import useHttp from "app/c1hooks/http";
import { RecordStatus, Roles } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import {
  customFilterDateDisplay,
  formatDate,
  userRolesDesc,
  userRolesDescChip,
} from "app/c1utils/utility";
import { encodeString } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import history from "history.js";
import { MatxLoading } from "matx";
import ConfirmationDialog from "matx/components/ConfirmationDialog";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import SettingsBackupRestoreOutlinedIcon from "@material-ui/icons/SettingsBackupRestoreOutlined";

const ManageUserList = () => {
  const { t } = useTranslation(["admin", "common", "buttons"]);
  const { user } = useAuth();

  const [params, setParams] = useState({ accnId: "", num: 1 }); //default to 1
  const [isRefresh, setRefresh] = useState(false);
  const [loading, setLoading] = useState(false);
  const { isLoading, res, error, urlId, sendRequest } = useHttp();
  const [snackBarOptions, setSnackBarOptions] = useState({
    open: false,
    vertical: "top",
    horizontal: "center",
    msg: "",
    severity: "success",
  });

  const [confirm, setConfirm] = useState({ id: "", open: false, action: null });

  const manageUsrUrl = `/api/v1/clickargo/clictruck/manageusr`;

  // let map = new Set(user.authorities.map((el) => el.authority));
  // const isAdministrator = (map.has(Roles.SYS_SUPER_ADMIN.code) || map.has(Roles.ADMIN.code))

  useEffect(() => {
    if (user) {
      setParams({ ...params, accnId: user?.coreAccn?.accnId });
    }
    // eslint-disable-next-line
  }, [user]);

  useEffect(() => {
    if (!isLoading && !error && res) {
      if (urlId === "getForDeActive") {
        let isUserLoggedIn = res?.data?.loggedIn;
        if (isUserLoggedIn) {
          setLoading(false);
          setConfirm({
            ...confirm,
            id: res?.data?.coreUsr?.usrUid,
            open: true,
            action: "deactivate",
          });
        } else {
          setLoading(false);
          sendRequest(
            `${manageUsrUrl}/update/deactivate/` +
              encodeString(res?.data?.coreUsr?.usrUid),
            "deActive",
            "PUT"
          );
        }
      } else if (urlId === "getForActive") {
        setLoading(false);
        sendRequest(
          `${manageUsrUrl}/update/activate/` +
            encodeString(res.data.coreUsr.usrUid),
          "active",
          "PUT"
        );
      }else if (urlId === "GET_FOR_UNSUSPEND") {
        setLoading(false);
        sendRequest(`${manageUsrUrl}/update/unsuspend/${encodeString(res.data.coreUsr.usrUid)}` , "USR_UNSUSPEND", "PUT");
      }

      if (urlId === "active" || urlId === "deActive") {
        if (urlId === "deActive") {
          setConfirm({ id: null, open: false, action: null });
        }
        setRefresh(true);
        setLoading(false);
        setSnackBarOptions({
          ...snackBarOptions,
          open: true,
          severity: "success",
          message:
            urlId === "active"
              ? t("admin:user.msg.activatedSuccess")
              : t("admin:user.msg.deactivatedSuccess"),
        });
      }else if (urlId === 'USR_UNSUSPEND') {
        setRefresh(true);
        setLoading(false);
        setSnackBarOptions({
          ...snackBarOptions,
          open: true,
          severity: "success",
          message: urlId === "usrUnsuspend" ? t("admin:user.msg.unsuspended") : t("admin:user.msg.suspended"),
        });
      }
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isLoading, res, error, urlId]);

  const handleEventAddUser = () => {
    history.push({
      pathname: "/manageUsers/user/new/0",
      state: {
        from: "manageUser",
      },
    });
  };

  const handleBuildBody = (values) => {
    return (
      values?.length > 0 &&
      values.map((value) => {
        value.data[2] = userRolesDesc(value?.data[2]);
        value.data[3] = moment(value?.data[3]).format("DD/MM/YYYY");
        value.data[4] = moment(value?.data[4]).format("DD/MM/YYYY");
        return value;
      })
    );
  };

  const handleUnsuspendHandler = (id) => {
      setLoading(true);
      setRefresh(false);
      sendRequest(`${manageUsrUrl}/${encodeString(id)}`, "GET_FOR_UNSUSPEND", "GET", {});
  }

  const columns = [
    {
      name: "usrUid", // field name in the row object
      label: t("user.list.usrUid"), // column title that will be shown in table
      options: {
        customBodyRender: (value, tableMeta, updateValue) => {
          const usrUid = tableMeta.rowData[0];
          // const accnId = tableMeta.rowData[1];
          // return accnId+"_"+usrUid;
          return usrUid;
        },
      },
    },
    {
      name: "usrName",
      label: t("user.list.usrName"),
    },
    {
      name: "usrContact.contactEmail",
      label: t("user.list.usrEmail"),
    },
    {
      name: "TCoreRoles",
      label: t("user.list.usrRoles"),
      options: {
        filter: false,
        sort: false,
        // setCellHeaderProps: () => { return { style: { width: '30%' } } },
        customBodyRender: (value, tableMeta, updateValue) => {
          // return userRolesDesc(value);
          return userRolesDescChip(value);
        },
      },
    },
    {
      name: "usrDtCreate",
      label: t("user.list.usrDtCreate"),
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
          return formatDate(value, true);
        },
      },
    },
    {
      name: "usrDtLupd",
      label: t("user.list.usrDtModified"),
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
          return formatDate(value, true);
        },
      },
    },
    {
      name: "usrStatus",
      label: t("user.list.usrStatus"),
      options: {
        filter: true,
        filterType: "dropdown",
        filterOptions: {
          names: [
            RecordStatus.ACTIVE.code,
            RecordStatus.INACTIVE.code,
            RecordStatus.SUSPENDED.code,
          ],
          renderValue: (v) => {
            switch (v) {
              case RecordStatus.ACTIVE.code:
                return RecordStatus.ACTIVE.desc;
              case RecordStatus.INACTIVE.code:
                return RecordStatus.INACTIVE.desc;
              case RecordStatus.SUSPENDED.code:
                return RecordStatus.SUSPENDED.desc;
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
              case RecordStatus.SUSPENDED.code:
                return RecordStatus.SUSPENDED.desc;
              default:
                break;
            }
          },
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          return getStatusDesc(value);
        },
      },
    },
    {
      name: "action",
      label: " ",
      options: {
        filter: false,
        display: true,
        viewColumns: false,
        setCellHeaderProps: () => {
          return { style: { width: "13.5%" } };
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          const usrUid = tableMeta.rowData[0];
          const usrStatus = tableMeta.rowData[6];
          return (
            <Grid
              container
              direction="row"
              justifyContent="center"
              alignItems="center"
            >
              <Grid
                container
                direction="row"
                justifyContent="flex-end"
                spacing={4}
              >
                {usrStatus === RecordStatus.ACTIVE.code && (
                  <Grid item xs={4}>
                    <C1LabeledIconButton
                      tooltip={t("buttons:edit")}
                      label={t("buttons:edit")}
                      //action={() => history.push(`/manageUsers/user/edit/${usrUid}`)}
                      action={() =>
                        history.push({
                          pathname: `/manageUsers/user/edit/id`,
                          state: { usrUid },
                        })
                      }
                    >
                      <EditOutlined />
                    </C1LabeledIconButton>
                  </Grid>
                )}
                <Grid item xs={4}>
                  {usrStatus === RecordStatus.ACTIVE.code && (
                    <C1LabeledIconButton
                      tooltip={t("buttons:deactivate")}
                      label={t("buttons:deactivate")}
                      action={() => handleDeActiveHandler(tableMeta.rowData[0])}
                    >
                      <LinkOffOutlined />
                    </C1LabeledIconButton>
                  )}
                  {usrStatus !== RecordStatus.ACTIVE.code &&
                    usrStatus !== RecordStatus.SUSPENDED.code && (
                      <C1LabeledIconButton
                        tooltip={t("buttons:activate")}
                        label={t("buttons:activate")}
                        action={() => handleActiveHandler(tableMeta.rowData[0])}
                      >
                        <LinkOutlined />
                      </C1LabeledIconButton>
                    )}
                  {usrStatus === RecordStatus.SUSPENDED.code &&
                    <C1LabeledIconButton
                        tooltip={t("buttons:unsuspend")}
                        label={t("buttons:unsuspend")}
                        action={() => handleUnsuspendHandler(usrUid)}>
                      <SettingsBackupRestoreOutlinedIcon />
                    </C1LabeledIconButton>
                  }
                </Grid>
                <Grid item xs={4}>
                  <C1LabeledIconButton
                    tooltip={t("buttons:view")}
                    label={t("buttons:view")}
                    //action={() => history.push(`/manageUsers/user/view/${usrUid}`)}
                    action={() =>
                      history.push({
                        pathname: `/manageUsers/user/view/id`,
                        state: { usrUid },
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
  ];

  const handleDeActiveHandler = (id) => {
    setLoading(true);
    setRefresh(false);
    sendRequest(
      `${manageUsrUrl}/${encodeString(id)}`,
      "getForDeActive",
      "GET",
      {}
    );
  };

  const handleActiveHandler = (id) => {
    setLoading(true);
    setRefresh(false);
    sendRequest(
      `${manageUsrUrl}/${encodeString(id)}`,
      "getForActive",
      "GET",
      {}
    );
  };

  const handleConfirmYesAction = () => {
    setLoading(true);
    setRefresh(false);
    sendRequest(
      `${manageUsrUrl}/update/deactivate/${encodeString(confirm?.id)}`,
      "deActive",
      "PUT"
    );
  };

  const handleSnackBarClose = () => {
    setSnackBarOptions({ ...snackBarOptions, open: false });
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

  // added style for the title
  let elTitle = (
    <Typography
      variant="h5"
      align="justify"
      style={{ color: "#34314C", marginTop: "10px" }}
    >
      {t("admin:user.list.title")}
    </Typography>
  );

  return (
    <React.Fragment>
      {loading && <MatxLoading />}
      {snackBar}

      {confirm && confirm.open && (
        <ConfirmationDialog
          title={
            confirm.action === "deactivate"
              ? t("admin:user.deactivate.title")
              : ""
          }
          open={confirm.open}
          text={
            confirm.action === "deactivate"
              ? t("admin:user.deactivate.content", { userId: confirm.id })
              : ""
          }
          onYesClick={() => handleConfirmYesAction()}
          onConfirmDialogClose={() => setConfirm({ ...confirm, open: false })}
        />
      )}

      <C1ListPanel
        routeSegments={[{ name: t("admin:user.list.title") }]}
        information={<C1Information information="manageUserListing" />}
        guideId="clictruck.account.admin.users.list"
        title={t("admin:user.list.title")}
      >
        <GridActionButton
          showAddButton={[
            {
              show: true,
              label: t("admin:user.list.addTitle").toUpperCase(),
              action: () => handleEventAddUser(),
              icon: <Add />,
            },
          ]}
        />
        <C1DataTable
          url={manageUsrUrl}
          isServer={true}
          columns={columns}
          defaultOrder={['usrUid', 'usrDtCreate']}
          defaultOrderDirection="desc"
          isShowFilterChip
          isShowDownload={true}
          isShowPrint={true}
          isShowToolbar={true}
          isRefresh={isRefresh}
          isRowSelectable={false}
          handleBuildBody={handleBuildBody}
        />
      </C1ListPanel>
    </React.Fragment>
  );
};

export default withErrorHandler(ManageUserList);
