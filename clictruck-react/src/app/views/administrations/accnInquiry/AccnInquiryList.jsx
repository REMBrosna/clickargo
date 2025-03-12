import { Grid } from "@material-ui/core";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import { VisibilityOutlined } from "@material-ui/icons";
import React, { useEffect, useState } from "react";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import history from "history.js";
import { customFilterDateDisplay, formatDate } from "app/c1utils/utility";
import useHttp from "app/c1hooks/http";
import { MatxLoading } from "matx";
import C1ListPanel from "app/c1component/C1ListPanel";
import DataTable from "app/atomics/organisms/DataTable";
import C1Information from "app/c1component/C1Information";
import { useTranslation } from "react-i18next";

export const RequestState = {
  PENDING: { code: "PENDING", desc: "PENDING" },
  INPROGRESS: { code: "INPROGRESS", desc: "IN-PROGRESS" },
  COMPLETED: { code: "COMPLETED", desc: "COMPLETED" },
};
const AccnInquiryList = () => {
  const [loading, setLoading] = useState(false);
  const [showHistory, setShowHistory] = useState(false);
  const { t } = useTranslation(["opadmin"]);
  const { isLoading, res, error, urlId, sendRequest } = useHttp();
  const [isRefresh, setRefresh] = useState(false);
  const [filterBy, setFilterBy] = useState([
    { attribute: "history", value: "default" },
  ]);

  const columns = [
    {
      name: "airId",
      label: "",
      options: {
        display: "excluded",
      },
    },
    {
      name: "tcoreAccn.accnName",
      label: t("accnInq.listing.accnName"),
      options: {
        filter: true,
        sort: true,
      },
    },
    {
      name: "airEmailReq",
      label: t("accnInq.listing.emailTo"),
      options: {
        filter: true,
      },
    },
    {
      name: "tcoreUsr.usrName",
      label: t("accnInq.listing.processBy"),
      options: {
        filter: true,
        sort: true,
      },
    },
    {
      name: "airDtCreate",
      label: t("accnInq.listing.reqDt"),
      options: {
        filter: true,
        sort: true,
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
      name: "airDtLupd",
      label: t("accnInq.listing.updateDt"),
      options: {
        filter: true,
        sort: true,
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
      name: "airReqState",
      label: t("accnInq.listing.state"),
      options: {
        filter: true,
        sort: true,
        filterOptions: {
          names: Object.keys(RequestState),
          renderValue: (v) => RequestState[v].desc,
        },
        customFilterListOptions: {
          render: (v) => RequestState[v].desc,
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          let statusText = "";
          let statusColor = "";
          switch (value) {
            case RequestState.PENDING.code:
              statusText = RequestState[value].desc;
              statusColor = "#F3420E";
              break;
            case RequestState.INPROGRESS.code:
              statusText = RequestState.INPROGRESS.desc;
              statusColor = "#F3C60E";
              break;

            case RequestState.COMPLETED.code:
              statusText = RequestState.COMPLETED.desc;
              statusColor = "#00D16D";
              break;
            default:
              break;
          }
          return (
            <small style={{ color: statusColor, fontWeight: 800 }}>
              {statusText}
            </small>
          );
        },
      },
    },
    {
      name: "action",
      label: t("accnInq.listing.action"),
      options: {
        filter: false,
        sort: false,
        display: true,
        customHeadLabelRender: (columnMeta) => {
          return <div style={{ textAlign: "center" }}>{columnMeta.label}</div>;
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          const accnReqId = tableMeta.rowData[0];

          return (
            <Grid
              container
              direction="row"
              justifyContent="center"
              alignItems="center"
            >
              <Grid item xs={12}>
                <C1LabeledIconButton
                  tooltip="View Request"
                  label="View Request"
                  action={() =>
                    history.push(`/opadmin/inquiry/accn/view/${accnReqId}`)
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
  ];

  useEffect(() => {
    if (showHistory) {
      setFilterBy([{ attribute: "history", value: "history" }]);
    } else {
      setFilterBy([{ attribute: "history", value: "default" }]);
    }
  }, [showHistory]);

  const toggleHistory = (filter) => {
    setLoading(true);
    setRefresh(false);
    setShowHistory(filter === "history" ? true : false);
    setTimeout(() => setRefresh(true), 500);
    setTimeout(() => setLoading(false), 500);
  };

  return (
    <React.Fragment>
      {loading && <MatxLoading />}
      <C1ListPanel
        routeSegments={[{ name: t("accnInq.listing.title") }]}
        guideId="clicdo.truck.users.list"
        title={t("accnInq.listing.title")}
        information={<C1Information information="manageAccountListing" />}
      >
        <DataTable
          url="/api/v1/clickargo/clictruck/inquiry/accn"
          isServer={true}
          columns={columns}
          isRefresh={isRefresh}
          defaultOrder="airDtCreate"
          defaultOrderDirection="desc"
          showDownload={false}
          showPrint={false}
          filterBy={filterBy}
          isShowFilterChip={true}
          showActiveHistoryButton={toggleHistory}
        />
      </C1ListPanel>
    </React.Fragment>
  );
};

export default withErrorHandler(AccnInquiryList);
