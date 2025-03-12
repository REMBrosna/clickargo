import { Grid, Snackbar } from "@material-ui/core";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import C1PopUp from "app/c1component/C1PopUp";
import C1Alert from "app/c1component/C1Alert";
import C1Information from "app/c1component/C1Information";
import C1ListPanel from "app/c1component/C1ListPanel";
import { formatDate } from "app/c1utils/utility";
import useAuth from "app/hooks/useAuth";
import history from "history.js";
import { MatxLoading } from "matx";
import C1DataTable from "app/c1component/C1DataTable";
import useHttp from "app/c1hooks/http";
import { Add, VisibilityOutlined } from "@material-ui/icons";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import JobUpload, { formatUploadRst } from "./JobUpload";
import GridOnIcon from "@material-ui/icons/GridOn";

/** @description Listing for Account Management */
const JobUploadList = () => {
  const { t } = useTranslation(["admin", "common"]);

  const [refresh, setRefresh] = useState(0);
  const [loading, setLoading] = useState(false);
  const [tableLoading, setTableLoading] = useState(false);
  const { isLoading, res, error, urlId, sendRequest } = useHttp();

  const [isPopupViewDetail, setPopupViewDetail] = useState(false);
  const [uploadResult, setUploadResult] = useState("");
  const [upId, setUpId] = useState();
  const [openJobUploadPopUp, setOpenJobUploadPopUp] = useState(false);

  const { user } = useAuth();

  const [snackBarOptions, setSnackBarOptions] = useState({
    open: false,
    vertical: "top",
    horizontal: "center",
    msg: "",
    severity: "success",
  });

  const [data, setData] = useState([]);

  useEffect(() => {
    sendRequest(
      "/api/v1/clickargo/clictruck/jobUpload/list",
      "list",
      "get",
      null
    );
    // eslint-disable-next-line
  }, [refresh]);

  useEffect(() => {
    // let msg = "";
    // let severity = "success";
    if (!isLoading && !error && res) {
      switch (urlId) {
        case "list": {
          setData(res.data);
          break;
        }
        default:
          break;
      }
    } else if (error) {
      // msg = "Error encountered whilte trying to fetch data!";
      // severity = "error";
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isLoading, res, error, urlId]);

  const columns = [
    {
      name: "upId", // co Account status
      label: "",
      options: {
        display: "excluded",
        filter: false,
      },
    },
    {
      name: "upFileName",
      label: "File Name",
    },
    {
      name: "upFileSize",
      label: "File Size",
    },
    {
      name: "upTotalLines",
      label: "Total Rows",
    },
    {
      name: "totalSuccessJobIds",
      label: "Success Rows",
    },
    {
      name: "totalFailJobIds",
      label: "Fail Rows",
    },
    {
      name: "upDtCreate",
      label: "Create Time",
      options: {
        customBodyRender: (value, tableMeta, updateValue) => {
          return formatDate(value, true);
        },
      },
    },
    {
      name: "upUidCreate",
      label: "Create User",
    },
    {
      name: "action",
      label: "Action",
      options: {
        customBodyRender: (value, tableMeta, updateValue) => {
          return (
            <C1LabeledIconButton
              tooltip={t("buttons:view")}
              label={t("buttons:view")}
              action={() => {
                popupViewDetail(tableMeta.rowData[0]);
              }}
            >
              <VisibilityOutlined />
            </C1LabeledIconButton>
          );
        },
      },
    },
  ];

  const popupViewDetail = (selectedId) => {
    let upRstList = data.filter((upRst) => selectedId === upRst.upId);
    let upRst = null;
    if (upRstList && upRstList.length > 0) {
      upRst = upRstList[0];
    }
    setUpId(selectedId);
    setUploadResult(upRst);
    setPopupViewDetail(true);
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

  return (
    <React.Fragment>
      {loading && <MatxLoading />}
      {snackBar}
      <C1ListPanel
        routeSegments={[{ name: "Upload Excel File List" }]}
        information={<C1Information information="manageAccountListing" />}
        guideId="_"
        title={"Upload Excel File List"}
      >
        <C1DataTable
          url="/api/v1/clickargo/clictruck/jobUpload/list"
          isServer={false}
          dbName={{ list: data }}
          columns={columns}
          isRefresh={true}
          // title={t("account.list.headerAll")}
          defaultOrder="upDtCreate"
          defaultOrderDirection="desc"
          isShowDownload={false}
          isShowPrint={false}
          isShowFilterChip={true}

          showAdd = {
              {type: "popUp",
              popUpHandler: setOpenJobUploadPopUp}
          }

        />

        <C1PopUp
          maxWidth={"lg"}
          title={"Upload Excel File"}
          openPopUp={openJobUploadPopUp}
          setOpenPopUp={setOpenJobUploadPopUp}
        >
          <JobUpload />
        </C1PopUp>

        <C1PopUp
          title={`Upload Result`}
          openPopUp={isPopupViewDetail}
          setOpenPopUp={() => setPopupViewDetail(false)}
          maxWidth={"lg"}
        >
          <div style={{ textAlign: "center" }}>
            {formatUploadRst(uploadResult)}
          </div>
        </C1PopUp>
      </C1ListPanel>
    </React.Fragment>
  );
};

export default JobUploadList;
