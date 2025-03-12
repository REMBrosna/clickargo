import {
  Box,
  Button,
  ButtonGroup,
  Grid,
  IconButton,
  Paper,
  Tooltip,
} from "@material-ui/core";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Radio from "@material-ui/core/Radio";
import RadioGroup from "@material-ui/core/RadioGroup";
import Snackbar from "@material-ui/core/Snackbar";
import { createTheme, MuiThemeProvider } from "@material-ui/core/styles";
import { makeStyles } from "@material-ui/core/styles";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import Typography from "@material-ui/core/Typography";
import { CancelOutlined, PriorityHighOutlined } from "@material-ui/icons";
import AddBoxIcon from "@material-ui/icons/AddBoxOutlined";
import CheckOutlinedIcon from "@material-ui/icons/CheckOutlined";
import CloudDownloadIcon from "@material-ui/icons/CloudDownloadOutlined";
import FileCopyOutlinedIcon from "@material-ui/icons/FileCopyOutlined";
import GetAppIcon from "@material-ui/icons/GetAppOutlined";
import HistoryIcon from "@material-ui/icons/HistoryOutlined";
import PaymentIcon from "@material-ui/icons/PaymentOutlined";
import PublishIcon from "@material-ui/icons/PublishOutlined";
import RedoOutlinedIcon from "@material-ui/icons/RedoOutlined";
import RefreshOutlinedIcon from "@material-ui/icons/RefreshOutlined";
import SearchIcon from "@material-ui/icons/SearchOutlined";
import { debounce } from "lodash";
import MUIDataTable from "mui-datatables";
import PropTypes from "prop-types";
import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useSelector } from "react-redux";
import { Link } from "react-router-dom";
import styled from "styled-components";

import C1CustomTooltip from "app/c1component/C1CustomTooltip";
import ViewColumnIcon from "app/c1component/CkColumnIcon";
import DownloadIcon from "app/c1component/CkDownloadIcon";
import FilterIcon from "app/c1component/CkFilterIcon";
import PrintIcon from "app/c1component/CkPrintIcon";
import { buttonStyles, titleTab, useStyles } from "app/c1utils/styles";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import axios, { sessionTimeout } from "axios.js";

import { MatxLoading } from "../../matx";
import { getDeepValue, Uint8ArrayToString } from "../c1utils/utility";
import C1Alert from "./C1Alert";
import C1Dialog from "./C1Dialog";
import C1InputField from "./C1InputField";
import CkDlDataIcon from "app/c1component/CkDlDataIcon";

/**
 * @description CamelOne custom datatable implementation.
 *
 * @param isServer - required boolean value to flag if datatable records are to be retrieved from server or a supplied array of records
 * @param url - URL to retrieve the records from. Required if isServer = true
 * @param dbName -  object contains {list: []} of records to be displayed in datatable. Required if isServer = false
 * @param title - title to be displayed on the upper left of the datatable
 * @param columns - required array of columns to be displayed in the datatable
 * @param defaultOrder - column name to be sorted
 * @param defaultOrderDirection - default is 'asc'
 * @param isShowToolbar - boolean value to display or not the toolbar located on the upper right of the datatable
 * @param showTemplate - when set should have {downloadHandler: func, uploadHandler: func} for download and upload handler of templates
 * @param showCustomDownload - when set should have {title: string, handler: func} for custom implementation of download of datatable records
 * @param showAdd - when set should have {type: ['popUp', 'redirect'], path: string, popUpHandler: func}. Path is required if type='redirect'
 * @param isShowFilter - boolean value to display filter icon
 * @param isShowFilterChip - boolean value to display filter chip (text display on the upper part of datatable when filtered)
 * @param isShowViewColumns - boolean value to display view columns option
 * @param isShowPrint - boolean value to display print icon
 * @param isShowDownload - boolean value to display download icon
 * @param isRowSelectable - boolean value if rows can be selected
 * @param isShowPagination - boolean value to display datatable pagination
 * @param filterBy - collection of key-value pair (attribute-value) to filter the datatable to
 * @param onFilterChange - callback function that triggers when filters have changed
 * @param onFilterChipClose - callback function that is triggered when a user clicks the "X" on a filter chip
 * @param customRowsPerPage - an array of rows option per page
 */
const C1DataTable = ({
  id,
  isServer = true,
  url,
  dbName,
  isRefresh = false,
  title,
  columns,
  defaultOrder,
  defaultOrderDirection = "asc",
  isShowToolbar = true,
  isShowDownloadData = true,
  showTemplate,
  showCustomDownload,
  isNilRecord,
  minHeightToolBar = false,
  showManualAdd,
  showBlClaim,
  showFfSubmit,
  showHistory,
  viewHistory,
  showAdd,
  showClaim,
  showVerify,
  showAction,
  showReturned,
  isShowFilter = true,
  isShowFilterChip = false,
  isShowViewColumns = true,
  isShowPrint = true,
  isShowDownload = true,
  isRowSelectable = true,
  isShowPagination = true,
  filterBy,
  csvFileName = "tableDownload",
  filterByOffline,
  handleRowClick,
  onRowClickEvent,
  handleBuildBody,
  onFilterChipClose,
  onFilterChange,
  guideId,
  viewTextFilter,
  showPay,
  setMaxHeight,
  customRowsPerPage,
  customPerPage,
  expandableRows = false,
  expandableRowsHeader = false,
  expandableRowsOnClick = true,
  isRowExpandable,
  rowsExpanded,
  renderExpandableRow,
  onRowExpansionChange,
}) => {
  const TextWrapper = styled(Typography)`
    color: #ff6161;
    margin: 0.5rem;
    display: flex;
    justify-content: center;
    box-shadow: 0px 0px 1px #ffb1b1;
    padding: 0.5rem;
    font-size: 12px;
  `;

  const iconStyles = makeStyles((theme) => ({
    iconButton: {
      display: "flex",
      flexDirection: "column",
      justifyContent: "center",
      alignItems: "center",
      width: "35px",
      height: "35px",
      "&:hover": {
        color: "#3F51B5",
      },
    },
    iconText: {
      fontSize: ".6rem",
      fontFamily: "Poppins",
      textTransform: "uppercase",
    },
  }));

  const classes = useStyles();
  const buttonClass = iconStyles();
  let userGuide = useSelector(({ userGuide }) => userGuide);
  const titleRadio = titleTab();
  const getMuiTheme = () =>
    createTheme({
      typography: {
        fontFamily: ["Poppins"].join(","),
      },
      overrides: {
        MUIDataTableFilterList: {
          chip: {
            display: isShowFilterChip === true ? "display" : "none",
          },
        },
        MuiToolbar: {
          root: {
            display:
              title || isShowToolbar || isShowDownloadData ? "flex" : "none",
            alignItems: "center",
          },
        },
        MuiTablePagination: {
          toolbar: {
            //to show pagination if isShowPagination or title is set
            display: !isShowPagination
              ? "none"
              : title || isShowPagination
              ? "flex"
              : "none",
          },
        },
        MuiTableCell: {
          head: {
            backgroundColor: "#EFF2F5",
            color: "#000",
            fontSize: "0.813rem",
            paddingTop: 10,
            paddingBottom: 10,
          },
          body: {
            paddingTop: 15,
            paddingBottom: 15,
            fontSize: "0.813rem",
          },
          root: {
            fontSize: "0.813rem",
          },
        },
        MUIDataTableSelectCell: {
          headerCell: {
            backgroundColor: "#EFF2F5",
          },
          expandDisabled: {
            // Soft hide the button.
            visibility: "hidden",
          },
        },
        MUIDataTableHeadCell: {
          sortActive: {
            paddingLeft: 16,
            color: "#000",
          },
          data: {
            backgroundColor: "#EFF2F5",
            color: "#000",
            display: "inline-block",
            fontWeight: "500",
            fontSize: "0.813rem",
            textAlign: "left",
          },
        },
        MUIDataTableToolbar: {
          root: {
            color: "#0772BA",
            display:
              title || isShowToolbar || isShowDownloadData ? "flex" : "none",
            minHeight: minHeightToolBar
              ? "0px"
              : !isShowToolbar && !title
              ? "10px"
              : "64px",
          },
          actions: {
            display: "inline-table",
          },
          left: {
            display: "flex",
            textAlign:
              title != "" && React.isValidElement(title) ? "left" : "center",
            marginLeft:
              title != "" && React.isValidElement(title) ? "-20px" : 0,
          },
        },
        MUIDataTable: {
          responsiveBase: {
            maxHeight: setMaxHeight ? setMaxHeight : "",
          },
        },
        MuiTypography: {
          body2: {
            fontSize: "0.813rem",
          },
          body1: {
            fontSize: "0.813rem",
          },
        },
        MuiList: {},
        MUIDataTableJumpToPage: {
          select: {
            fontSize: "0.813rem",
          },
        },
        MuiTablePagination: {
          input: {
            fontSize: "0.813rem",
          },
        },
        MuiMenuItem: {
          root: {
            fontSize: "0.813rem",
          },
        },
        MUIDataTableToolbar: {
          root: {
            minHeight: 44,
          },
        },
        MUIDataTableViewCol: {
          label: {
            fontSize: "0.813rem",
          },
        },
        MuiIconButton: {
          colorPrimary: "#0a72ba",
        },
        MuiToolbar: {
          root: {
            display: "flex",
            justifyContent: "space-between",
            alignItems: "flex-end",
          },
        },
        MuiTablePagination: {
          toolbar: {
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
          },
        },
      },
    });

  const [c1DtState, setC1DtState] = useState({
    page: 0,
    count: 1,
    rowsPerPage: customPerPage ? customPerPage : 10,
    previousPageNo: 0,
    sortOrder: {
      name: defaultOrder,
      direction:
        defaultOrderDirection === undefined ? "asc" : defaultOrderDirection,
    },
    data: [["Loading Data..."]],
    isLoading: false,
  });

  const [snackBarState, setSnackBarState] = useState({
    open: false,
    message: "",
    vertical: "top",
    horizontal: "center",
    severity: "success",
  });

  const isAppendAlwaysNo = showTemplate?.uploadHandler?.isAppendAlwaysNo;

  const [refreshTable, setRefreshTable] = useState(false);
  const [isDialogUpOpen, setOpenUpDialog] = useState(false);
  const [upLoading, setUpLoading] = useState(false);
  const [uploadErrorMessage, setUploadErrorMessage] = useState("");
  const [uploadErrors, setUploadErrors] = useState([]);
  const [fileData, setFileData] = useState({ name: "", data: "" });
  const [upSuccessMsg, setUpSuccessMsg] = useState("");
  const [isAppend, setIsAppend] = useState(isAppendAlwaysNo ? false : true);

  //delay for filter search
  const debouncedFilterSearch = useCallback(
    debounce((tableState) => search(tableState), 1000, { maxWait: 2000 })
  );

  useEffect(() => {
    if (isServer || fileData) {
      setRefreshTable(isServer);
      setC1DtState({ ...c1DtState, isLoading: true });
    }
  }, [fileData]);

  //to retrigger only if there is change in dbName
  useEffect(() => {
    if (dbName && !isServer) {
      if (isRefresh) {
        setC1DtState({ ...c1DtState, isLoading: true });
      }

      let dbNameData = { list: [...dbName.list] };
      let dbDataLen = dbName.list.length;
      //expects only one element TODO for multiple filterBy
      if (filterByOffline && filterByOffline.length > 0) {
        dbNameData.list = dbName?.list?.filter((el) => {
          let { attribute, value } = filterByOffline[0];

          let elVal = getDeepValue(el, attribute);
          return elVal === value;
        });
        dbDataLen = dbNameData.list.length;
      }

      setC1DtState({ ...c1DtState, data: dbNameData?.list, count: dbDataLen });
      // setC1DtState({ ...c1DtState, data: dbName.list, count: dbName.list.length });
    } else if (isServer && isRefresh) {
      setRefreshTable(isRefresh);
      setC1DtState({ ...c1DtState, isLoading: true });
    }

    // eslint-disable-next-line
  }, [dbName, isRefresh, isServer, isShowFilterChip]);

  const setData = (res, isLoading) => {
    setC1DtState({
      isLoading: isLoading,
      data: res.data,
      count: res.count,
      page: res.page,
    });
  };

  const getData = (tableState) => {
    setC1DtState({ ...c1DtState, isLoading: true });
    apiRequest(tableState).then((res) => {
      setData(res, false);
    });
  };

  /*Called when action from onTableChange is sort */
  const sort = (tableState) => {
    setC1DtState({ ...c1DtState, isLoading: true });
    apiRequest(tableState).then((res) => {
      setData(res, false);
    });
  };

  const changePage = (tableState) => {
    setC1DtState({ isLoading: true });
    apiRequest(tableState).then((res) => {
      setData(res, false);
    });
  };

  const search = (tableState) => {
    setC1DtState({ isLoading: false });
    apiRequest(tableState).then((res) => {
      setData(res, false);
    });
  };

  const reset = (tableState) => {
    setC1DtState({ isLoading: false });
    tableState.filterList = [];
    apiRequest(tableState).then((res) => {
      setData(res, false);
    });
  };

  const apiRequest = (tableState) => {
    //The base url from set in props
    if (!url) {
      setC1DtState({ ...c1DtState, isLoading: true });
    }

    let baseRequestUrl = url;
    return new Promise((resolve, reject) => {
      let displayStart = getDisplayStart(tableState);
      let postRequesturl =
        "/list?sEcho=3&iDisplayStart=" +
        displayStart +
        "&iDisplayLength=" +
        tableState.rowsPerPage +
        getSortParam(columns, tableState) +
        getQueryFieldParams(columns, tableState);

      if (isServer) {
        let requestUrl = baseRequestUrl + postRequesturl;
        setC1DtState({ ...c1DtState, previousPageNo: tableState.rowsPerPage });

        axios
          .get(requestUrl)
          .then((result) => {
            resolve({
              data: result.data.aaData,
              page: displayStart > 0 ? tableState.page : 0,
              count: result.data.iTotalDisplayRecords,
            });
          })
          .catch((error) => {
            reject({ err: { msg: sessionTimeout } });
          });
      } else {
        if (dbName) {
          //start JIRA CLICDO-159 [ClicDO-FF][DO Claim Jobs] When click at sort feature in Bill of Lading pop-up, system will display all authorizer data
          let dbNameData = { list: [...dbName.list] };
          let dbDataLen = dbName.list.length;
          if (filterByOffline && filterByOffline.length > 0) {
            dbNameData.list = dbName?.list?.filter((el) => {
              let { attribute, value } = filterByOffline[0];

              let elVal = getDeepValue(el, attribute);
              return elVal === value;
            });
            dbDataLen = dbNameData.list.length;
            resolve({
              data: dbNameData.list,
              page: displayStart > 0 ? tableState.page : 0,
              count: dbDataLen,
            });
            //end JIRA CLICDO-159 [ClicDO-FF][DO Claim Jobs] When click at sort feature in Bill of Lading pop-up, system will display all authorizer data
          } else {
            resolve({
              data: dbName.list,
              page: displayStart > 0 ? tableState.page : 0,
              count: dbName && dbName.list ? dbName.list.length : 0,
            });
          }
        }
      }
    });
  };

  const getDisplayStart = (tableState) => {
    if (c1DtState?.previousPageNo === tableState?.page) {
      return 0;
    }

    return tableState?.page * tableState?.rowsPerPage;
  };

  const getQueryFieldParams = (columns, tableState) => {
    let idx = 0;
    let [sortFieldName, sortDirection] = getSortFieldNameAndDirection(
      columns,
      tableState
    );

    let fieldParams = "";

    if (sortDirection) {
      fieldParams = "&mDataProp_" + idx + "=" + sortFieldName;
      idx++;
    }

    //prioritize filterBy?
    if (filterBy) {
      for (let i = 0; i < filterBy?.length; i++) {
        let { attribute, value } = filterBy[i];

        if (attribute && value) {
          fieldParams +=
            "&mDataProp_" +
            idx +
            "=" +
            attribute +
            "&sSearch_" +
            idx +
            "=" +
            value;
          idx++;
        }
      }
    }

    if (tableState?.filterList) {
      for (let i = 0; i < tableState?.filterList?.length; i++) {
        let filter = tableState?.filterList[i];

        if (filter.length > 0) {
          fieldParams +=
            "&mDataProp_" +
            idx +
            "=" +
            tableState.columns[i].name +
            "&sSearch_" +
            idx +
            "=" +
              encodeURIComponent(filter);
          idx++;
        }
      }
    }

    fieldParams = fieldParams + "&iColumns=" + idx;
    return fieldParams;
  };

  const getSortParam = (columns, tableState) => {
    // eslint-disable-next-line
    let [sortFieldName, sortDirection] = getSortFieldNameAndDirection(
      columns,
      tableState
    );
    if (sortDirection) {
      return "&iSortCol_0=0&sSortDir_0=" + sortDirection + "&iSortingCols=1";
    }

    return "&iSortCol_0=0&sSortDir_0=asc&iSortingCols=0";
  };

  const getQueryFieldParamsWithout = (tableState) => {
    let idx = 0;

    let fieldParams = "";

    //prioritize filterBy?
    if (filterBy) {
      for (let i = 0; i < filterBy?.length; i++) {
        let { attribute, value } = filterBy[i];

        if (attribute && value) {
          fieldParams +=
            "&mDataProp_" +
            idx +
            "=" +
            attribute +
            "&sSearch_" +
            idx +
            "=" +
            value;
          idx++;
        }
      }
    }

    if (tableState?.filterList) {
      for (let i = 0; i < tableState?.filterList?.length; i++) {
        let filter = tableState?.filterList[i];

        if (filter.length > 0) {
          fieldParams +=
            "&mDataProp_" +
            idx +
            "=" +
            tableState.columns[i].name +
            "&sSearch_" +
            idx +
            "=" +
            filter;
          idx++;
        }
      }
    }

    fieldParams = fieldParams + "&iColumns=" + idx;
    return fieldParams;
  };

  const getSortFieldNameAndDirection = (columns, tableState) => {
    if (tableState?.sortOrder && tableState?.sortOrder?.name) {
      setC1DtState({
        ...c1DtState,
        sortOrder: {
          name: tableState?.sortOrder?.name,
          direction: tableState?.sortOrder?.direction,
        },
      });
      return [tableState.sortOrder.name, tableState.sortOrder.direction];
    }

    return getDefaultSortFieldNameAndDirection(columns);
  };

  const getDefaultSortFieldNameAndDirection = (columns) => {
    let columnsTmp = columns;
    columnsTmp = columnsTmp.filter((column) => {
      return !column?.options?.sort;
    });

    if (columnsTmp) {
      return [columnsTmp[0].field, columnsTmp[0].options.sort];
    }

    return null;
  };
  const handleFormatData = (values) => {
    if (handleBuildBody) {
      return handleBuildBody(values);
    } else {
      return values;
    }
  };

  const handleDownloadData = (fileName, url) => {
    let displayStart = getDisplayStart(C1DataTable?.tableState);
    let postRequesturl =
      "/download?sEcho=3&iDisplayStart=" +
      displayStart +
      "&iDisplayLength=" +
      1000 +
      getSortParam(columns, C1DataTable?.tableState) +
      getQueryFieldParamsWithout(C1DataTable.tableState);
    let requestUrl = url + postRequesturl;
    axios
      .get(requestUrl, { responseType: "blob" })
      .then(({ data }) => {
        const downloadUrl = window.URL.createObjectURL(new Blob([data]));
        const link = document.createElement("a");
        link.href = downloadUrl;
        link.setAttribute("download", `${fileName}.xlsx`);
        document.body.appendChild(link);
        link.click();
        link.remove();
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const handleDownload = (fileName, url) => {
    axios
      .get(url, { responseType: "blob" })
      .then(({ data }) => {
        const downloadUrl = window.URL.createObjectURL(new Blob([data]));
        const link = document.createElement("a");
        link.href = downloadUrl;
        link.setAttribute("download", `${fileName}.xlsx`);
        document.body.appendChild(link);
        link.click();
        link.remove();
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const handleClickDownloadDataEvent = (eventValue) => {
    if (typeof eventValue === "function") {
      return eventValue();
    }
    if (typeof eventValue === "object" && eventValue !== null) {
      return handleDownloadData(eventValue?.fileName, eventValue?.downLoadUrl);
    }
  };

  const handleClickDownloadEvent = (eventValue) => {
    if (typeof eventValue === "function") {
      return eventValue();
    }
    if (typeof eventValue === "object" && eventValue !== null) {
      return handleDownload(eventValue?.fileName, eventValue.downLoadUrl);
    }
  };

  const handleClickUploadEvent = (eventValue) => {
    if (typeof eventValue === "function") {
      return eventValue();
    }
    if (typeof eventValue === "object" && eventValue !== null) {
      setOpenUpDialog(true);
    }
  };

  const handleUpload = (eventValue) => {
    if (fileData.data == null || fileData.data === "") return;
    setUpLoading(true);
    axios
      .post(eventValue?.uploadUrl, {
        parentAppId: eventValue.parentAppId,
        append: isAppend,
        file: fileData.data,
      })
      .then((result) => {
        setUpLoading(true);
        if (result?.data?.err?.code === -100) {
          setUploadErrors([...result?.data?.data]);
        } else {
          setFileData({
            name: result?.data?.code,
            data: result?.data?.message,
          });
          setOpenUpDialog(false);
          setUploadErrors([]);
          setUpLoading(false);
          setIsAppend(isAppendAlwaysNo ? false : true);
          setSnackBarState({
            ...snackBarState,
            open: true,
            success: true,
            message: translate("common:supportingDocs.msg.uploadSuccess"),
          });
        }
      })
      .catch((error) => {
        if (error?.data) {
          const errorList = [];
          for (const key in error?.data) {
            errorList.push({ field: error?.data[key], valueKey: key });
          }
          setUploadErrors(errorList);
        }
        if (error?.message) {
          setUploadErrorMessage(error?.message);
        }
        setUpLoading(false);
      });
  };

  const handleFileChangeHandler = (e) => {
    e.preventDefault();
    const file = e.target.files[0];
    if (!file) return;

    const isXlsx =
      file.type ===
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    if (!isXlsx) {
      setFileData({});
      setUploadErrorMessage("File uploaded must be .xlsx");
      return;
    } else {
      setUploadErrorMessage("");
    }

    const fileReader = new FileReader();
    fileReader.readAsArrayBuffer(file);
    fileReader.onload = (e) => {
      const uint8Array = new Uint8Array(e.target.result);

      const imgStr = Uint8ArrayToString(uint8Array);
      const base64Sign = btoa(imgStr);

      setFileData({ ...fileData, name: file.name, data: base64Sign });
    };
  };

  const handleChange = (e) => {
    isAppendAlwaysNo
      ? setIsAppend(false)
      : setIsAppend(e.target.value == "Y" ? true : false);
  };

  const handleSnackBarClose = () => {
    setSnackBarState({ ...snackBarState, open: false });
  };
  const isSnackBarOpen = snackBarState.open;

  const { t } = useTranslation(["buttons"]);
  const { t: tDos } = useTranslation(["dos"]);
  const { t: translate } = useTranslation(["admin", "common"]);

  const components = {
    icons: {
      SearchIcon,
      PrintIcon,
      DownloadIcon,
      ViewColumnIcon,
      FilterIcon,
    },
    Tooltip: C1CustomTooltip,
  };

  return (
    <>
      {isSnackBarOpen && (
        <Snackbar
          anchorOrigin={{
            vertical: snackBarState.vertical,
            horizontal: snackBarState.horizontal,
          }}
          open={isSnackBarOpen}
          onClose={handleSnackBarClose}
          autoHideDuration={3000}
        >
          <C1Alert
            onClose={handleSnackBarClose}
            severity={snackBarState.severity}
          >
            {snackBarState.message}
          </C1Alert>
        </Snackbar>
      )}
      <Grid container>
        <Grid item xs={12}>
          <Paper id={id || title} className={classes.dataTablePaper}>
            <MuiThemeProvider theme={getMuiTheme}>
              <MUIDataTable
                title={title}
                data={c1DtState.data || [["Loading Data..."]]}
                columns={columns}
                components={components}
                options={{
                  textLabels: {
                    body: {
                      noMatch: isNilRecord
                        ? "NIL"
                        : translate("common:genericMsgs.emptyTable"),
                    },
                  },
                  customToolbar: () => {
                    let elTemplate = [];
                    if (isShowDownloadData) {
                      if (showTemplate) {
                        if (showTemplate.downloadDataHandler) {
                          elTemplate.push(
                            <Tooltip
                              title={t("buttons:downloadData")}
                              aria-label="add"
                              key="isShowDownloadData"
                            >
                              <IconButton
                                aria-label="reject"
                                type="button"
                                onClick={() =>
                                  handleClickDownloadDataEvent(
                                    showTemplate.downloadDataHandler
                                  )
                                }
                              >
                                <CkDlDataIcon />
                              </IconButton>
                            </Tooltip>
                          );
                        }
                      }
                    }
                    if (isShowToolbar) {
                      if (showTemplate) {
                        if (showTemplate.downloadHandler) {
                          elTemplate.push(
                            <Tooltip
                              title={t("buttons:download")}
                              aria-label="add"
                              key="templateDownload"
                            >
                              <IconButton
                                aria-label="reject"
                                type="button"
                                onClick={() =>
                                  handleClickDownloadEvent(
                                    showTemplate.downloadHandler
                                  )
                                }
                              >
                                <GetAppIcon color="primary" />
                              </IconButton>
                            </Tooltip>
                          );
                        }

                        if (showTemplate.uploadHandler) {
                          elTemplate.push(
                            <Tooltip
                              title={t("buttons:upload")}
                              aria-label="add"
                              key="templateUpload"
                            >
                              <IconButton
                                aria-label="reject"
                                type="button"
                                onClick={() =>
                                  handleClickUploadEvent(
                                    showTemplate.uploadHandler
                                  )
                                }
                              >
                                <PublishIcon color="primary" />
                              </IconButton>
                            </Tooltip>
                          );
                        }
                      }
                      if (showManualAdd) {
                        if (showManualAdd.type === "popUp") {
                          elTemplate.push(
                            // <Tooltip title={"Manually Add"} aria-label="add" key="manualAdd">
                            // <Button onClick={showManualAdd.popUpHandler}><PublishIcon viewBox="0 0 24 24" color="primary" /></Button>
                            <span key="showManualAdd">
                              <Button
                                onClick={showManualAdd.popUpHandler}
                                size="small"
                                variant="contained"
                                color="primary"
                              >
                                <PublishIcon />
                                {t("buttons:mnAdd")}
                              </Button>
                              &nbsp;
                            </span>
                            // </Tooltip>
                          );
                        } else {
                          elTemplate.push(
                            <Link to={showManualAdd.path} key="AddPath">
                              <Tooltip
                                title={t("buttons:mnAdd")}
                                aria-label="add"
                              >
                                <Button>
                                  <PublishIcon
                                    viewBox="0 0 24 24"
                                    color="primary"
                                  />
                                </Button>
                              </Tooltip>
                            </Link>
                          );
                        }
                      }

                      if (showBlClaim) {
                        if (showBlClaim.type === "popUp") {
                          elTemplate.push(
                            <Tooltip
                              key="showBlClaim"
                              title={t("buttons:claim")}
                            >
                              <IconButton
                                onClick={showBlClaim.popUpHandler}
                                classes={{ label: buttonClass.iconButton }}
                              >
                                <FileCopyOutlinedIcon viewBox="0 0 24 24" />
                                <div className={buttonClass.iconText}>
                                  {t("buttons:claim")}
                                </div>
                              </IconButton>
                            </Tooltip>
                          );
                        } else {
                          elTemplate.push(
                            <Link to={showBlClaim.path} key="ClaimPath">
                              <Tooltip
                                title={t("buttons:claim")}
                                aria-label="add"
                              >
                                <Button>
                                  <CheckOutlinedIcon
                                    viewBox="0 0 24 24"
                                    color="primary"
                                  ></CheckOutlinedIcon>
                                </Button>
                              </Tooltip>
                            </Link>
                          );
                        }
                      }

                      if (showFfSubmit) {
                        if (showFfSubmit.type === "popUp") {
                          elTemplate.push(
                            <Tooltip
                              title={t("buttons:ffSubmit")}
                              key="showFfSubmit"
                            >
                              <IconButton
                                onClick={showFfSubmit.popUpHandler}
                                classes={{ label: buttonClass.iconButton }}
                              >
                                <RedoOutlinedIcon viewBox="0 0 24 24" />
                                <div className={buttonClass.iconText}>
                                  {t("buttons:submit")}
                                </div>
                              </IconButton>
                            </Tooltip>
                          );
                        } else {
                          elTemplate.push(
                            <Link to={showFfSubmit.path} key="ClaimPath">
                              <Tooltip
                                title={t("buttons:claim")}
                                aria-label="add"
                              >
                                <Button>
                                  <RedoOutlinedIcon
                                    viewBox="0 0 24 24"
                                    color="primary"
                                  ></RedoOutlinedIcon>
                                </Button>
                              </Tooltip>
                            </Link>
                          );
                        }
                      }

                      if (showHistory) {
                        elTemplate.push(
                          <Link to={showHistory.path} key="History">
                            <Tooltip
                              title={
                                showHistory.title
                                  ? showHistory.title
                                  : t("buttons:history")
                              }
                              aria-label={
                                showHistory.title
                                  ? showHistory.title
                                  : t("buttons:history")
                              }
                            >
                              <Button>
                                <HistoryIcon
                                  viewBox="0 0 24 24"
                                  color="primary"
                                ></HistoryIcon>
                              </Button>
                            </Tooltip>
                          </Link>
                        );
                      }

                      if (viewHistory) {
                        elTemplate.push(
                          <Tooltip
                            title={viewHistory.title}
                            key="CustomDownload"
                          >
                            <IconButton
                              aria-label="history"
                              type="button"
                              color="primary"
                              onClick={viewHistory.handler}
                            >
                              {viewHistory?.icon ? (
                                viewHistory?.icon
                              ) : (
                                <HistoryIcon />
                              )}
                            </IconButton>
                          </Tooltip>
                        );
                      }

                      if (viewTextFilter) {
                        elTemplate.push(viewTextFilter);
                        // elTemplate.push(<ButtonGroup color="primary" key="viewTextFilter" aria-label="outlined primary button group">
                        //     {viewTextFilter.map((item, idx) => {
                        //         return <Button key={idx} startIcon={item.icon} size="small" onClick={viewTextFilter.eventHandler}>{item.label}</Button>
                        //     })}
                        // </ButtonGroup>);
                      }

                      if (showAdd) {
                        if (showAdd.type === "popUp") {
                          elTemplate.push(
                            <Button
                              className="hover-bg-primary"
                              style={{
                                marginBottom: 10,
                                marginTop: 7,
                                backgroundColor: "#37B7FF",
                              }}
                              key="showAddPopup"
                              onClick={showAdd.popUpHandler}
                              size="small"
                              variant="contained"
                              color="primary"
                            >
                              <AddBoxIcon />
                              {t("buttons:add")}
                            </Button>
                          );
                        } else {
                          elTemplate.push(
                            <Link to={showAdd.path} key="AddPath">
                              <Tooltip title={t("buttons:add")} key="AddPath">
                                <IconButton
                                  classes={{ label: buttonClass.iconButton }}
                                >
                                  <AddBoxIcon viewBox="0 0 24 24" />
                                  <div className={buttonClass.iconText}>
                                    {t("buttons:add")}
                                  </div>
                                </IconButton>
                              </Tooltip>
                            </Link>
                          );
                        }
                      }

                      if (showClaim) {
                        if (showClaim.type === "popUp") {
                          elTemplate.push(
                            // <Tooltip title={t("buttons:claim")} aria-label="add" key="Claim">
                            //     <Button onClick={showClaim.popUpHandler}><RefreshOutlinedIcon viewBox="0 0 24 24" color="primary" /></Button>
                            // </Tooltip>
                            <Button
                              key="showClaimPopup"
                              onClick={showClaim.popUpHandler}
                              size="small"
                              variant="contained"
                              color="primary"
                            >
                              <CheckOutlinedIcon />
                              {t("buttons:claim")}
                            </Button>
                          );
                        } else {
                          elTemplate.push(
                            <Link to={showClaim.path} key="ClaimPath">
                              <Tooltip
                                title={t("buttons:claim")}
                                aria-label="add"
                              >
                                <Button>
                                  <CheckOutlinedIcon
                                    viewBox="0 0 24 24"
                                    color="primary"
                                  ></CheckOutlinedIcon>
                                </Button>
                              </Tooltip>
                            </Link>
                          );
                        }
                      }

                      if (showVerify) {
                        if (showVerify.type === "popUp") {
                          elTemplate.push(
                            <Button
                              key="showVerifyPopup"
                              onClick={showVerify.popUpHandler}
                              size="small"
                              variant="contained"
                              color="primary"
                            >
                              <CheckOutlinedIcon />
                              {t("buttons:verify")}
                            </Button>
                          );
                        } else {
                          elTemplate.push(
                            <Link to={showVerify.path} key="verify">
                              <Tooltip
                                title={t("buttons:verify")}
                                aria-label="add"
                              >
                                <Button>
                                  <CheckOutlinedIcon
                                    viewBox="0 0 24 24"
                                    color="primary"
                                  ></CheckOutlinedIcon>
                                </Button>
                              </Tooltip>
                            </Link>
                          );
                        }
                      }

                      if (showAction) {
                        if (showAction.type === "popUp") {
                          elTemplate.push(
                            <ButtonGroup
                              style={{ marginRight: -24 }}
                              color="primary"
                              key="viewTextFilter"
                              aria-label="outlined primary button group"
                            >
                              <Button
                                key="showActionPopupVerify"
                                onClick={showAction.popUpHandlerVerify}
                                size="small"
                                variant="contained"
                                color="primary"
                              >
                                <CheckOutlinedIcon />
                                {t("buttons:verify")}
                              </Button>
                              <Button
                                key="showActionPopupReject"
                                onClick={showAction.popUpHandlerReject}
                                size="small"
                                variant="contained"
                                style={{
                                  backgroundColor: "red",
                                  marginLeft: "10px",
                                }}
                              >
                                <CancelOutlined />
                                {t("buttons:reject")}
                              </Button>
                            </ButtonGroup>
                          );
                        }
                      }

                      if (showReturned) {
                        if (showReturned.type === "popUp") {
                          elTemplate.push(
                            <Button
                              key="showReturnedPopup"
                              onClick={showReturned.popUpHandler}
                              size="small"
                              variant="contained"
                              color="primary"
                            >
                              <CheckOutlinedIcon />
                              {t("buttons:returned")}
                            </Button>
                          );
                        } else {
                          elTemplate.push(
                            <Link to={showReturned.path} key="verify">
                              <Tooltip
                                title={t("buttons:returned")}
                                aria-label="add"
                              >
                                <Button>
                                  <CheckOutlinedIcon
                                    viewBox="0 0 24 24"
                                    color="primary"
                                  ></CheckOutlinedIcon>
                                </Button>
                              </Tooltip>
                            </Link>
                          );
                        }
                      }

                      if (showCustomDownload) {
                        elTemplate.push(
                          <Tooltip
                            title={showCustomDownload.title}
                            key="CustomDownload"
                          >
                            <IconButton
                              aria-label="download"
                              type="button"
                              color="primary"
                              onClick={showCustomDownload.handler}
                            >
                              <GetAppIcon />
                            </IconButton>
                          </Tooltip>
                        );
                      }

                      if (showPay) {
                        elTemplate.push(
                          <Tooltip title={t("buttons:pay")} key="Pay">
                            <IconButton
                              aria-label="pay"
                              type="button"
                              onClick={showPay.handler}
                              classes={{ label: buttonClass.iconButton }}
                            >
                              {showPay?.icon ? showPay?.icon : <PaymentIcon />}
                              <div className={buttonClass.iconText}>
                                {t("buttons:pay")}
                              </div>
                            </IconButton>
                          </Tooltip>
                        );
                      }
                    }

                    return (
                      <React.Fragment>
                        {elTemplate.map((el) => el)}
                      </React.Fragment>
                    );
                  },
                  pagination: isShowPagination,
                  fixedHeader: false,
                  count: c1DtState.count,
                  page: c1DtState.page,
                  rowsPerPage: c1DtState.rowsPerPage,
                  jumpToPage: isShowPagination,
                  sortOrder: c1DtState.sortOrder,
                  download:
                    isShowDownload === undefined ? true : isShowDownload,
                  filter: isShowFilter === undefined ? true : isShowFilter,
                  print: isShowPrint === undefined ? true : isShowPrint,
                  viewColumns:
                    isShowViewColumns === undefined ? true : isShowViewColumns,
                  selectableRows:
                    isRowSelectable === undefined ? "multiple" : "none",
                  filterType: "textField",
                  responsive: "simple",
                  tableBodyHeight: "auto",
                  tableBodyMaxHeight: "auto",
                  enableNestedDataAccess: ".",
                  elevation: 0,
                  rowsPerPageOptions: customRowsPerPage
                    ? customRowsPerPage
                    : [10, 20, 40, 80, 100],
                  //disables the search icon in the toolbar
                  search: false,
                  serverSide: isServer,
                  // customSort: (data, colIndex, order, meta) => {
                  //     return data.sort((a, b) => {
                  //         return (a.data[colIndex].length < b.data[colIndex].length ? -1 : 1) * (order === 'desc' ? 1 : -1);
                  //     });
                  // },
                  onTableChange: (action, tableState) => {
                    // a developer could react to change on an action basis or
                    // examine the state as a whole and do whatever they want
                    switch (action) {
                      case "changePage":
                        changePage(tableState);
                        break;
                      case "sort":
                        sort(tableState);
                        break;
                      case "changeRowsPerPage":
                        getData(tableState);
                        break;
                      case "filterChange":
                        debouncedFilterSearch(tableState);
                        break;
                      case "resetFilters":
                        reset(tableState);
                        break;
                      default:
                        if (refreshTable) {
                          setRefreshTable(false);
                          getData(tableState);
                        }
                        break;
                    }
                  },
                  onTableInit: (action, tableState) => {
                    getData(tableState);
                  },
                  onFilterChipClose: onFilterChipClose,
                  onFilterChange: onFilterChange,
                  onRowClick: (e) => {
                    if (onRowClickEvent) {
                      handleRowClick(e);
                    }
                  },
                  onDownload: (buildHead, buildBody, columns, data) => {
                    return (
                      "\uFEFF" +
                      buildHead(columns) +
                      buildBody(handleFormatData(data))
                    );
                  },
                  downloadOptions: {
                    filename: csvFileName + ".csv",
                    separator: ",",
                    filterOptions: {
                      useDisplayedColumnsOnly: true,
                      useDisplayedRowsOnly: true,
                    },
                  },
                  expandableRows,
                  expandableRowsHeader,
                  expandableRowsOnClick,
                  isRowExpandable,
                  rowsExpanded,
                  renderExpandableRow,
                  onRowExpansionChange,
                }}
              />
            </MuiThemeProvider>
          </Paper>
        </Grid>
        {guideId && (
          <Grid item xs={12}>
            <Typography
              variant="subtitle2"
              align="justify"
              style={{
                color: "rgba(0, 0, 0, .50)",
                fontWeight: "300",
                fontSize: "small",
              }}
            >
              {userGuide?.find((e) => e.cmguComponentId === guideId)?.cmguGuide}
            </Typography>
          </Grid>
        )}
      </Grid>
      <C1Dialog
        title={translate("user.list.bulkUpload")}
        isOpen={isDialogUpOpen}
        actionsEl={
          <Button
            fullWidth
            size="large"
            color={"primary"}
            variant="contained"
            disabled={upLoading}
            onClick={() => handleUpload(showTemplate.uploadHandler)}
          >
            {translate("user.list.btnUpload")}
          </Button>
        }
        handleCloseEvent={() => {
          setOpenUpDialog(false);
          setUploadErrors([]);
          setUploadErrorMessage("");
          setFileData({});
        }}
      >
        {upLoading && <MatxLoading />}
        <Box className={classes.gridContainer}>
          <Box className={titleRadio.root}>{t("buttons:radioTitle")}</Box>
        </Box>
        <RadioGroup
          row
          aria-label="yesOrno"
          defaultValue={isAppendAlwaysNo ? "N" : "Y"}
        >
          <FormControlLabel
            value="Y"
            control={
              <Radio
                color="primary"
                disabled={isAppendAlwaysNo}
                onChange={(e) => handleChange(e)}
              />
            }
            label={tDos("app.dos.tabs.headerDetails.fields.yes")}
            labelPlacement="start"
          />
          <FormControlLabel
            value="N"
            control={
              <Radio color="primary" onChange={(e) => handleChange(e)} />
            }
            label={tDos("app.dos.tabs.headerDetails.fields.no")}
            labelPlacement="start"
          />
        </RadioGroup>
        <C1InputField
          required
          disabled={upLoading}
          label=""
          name="data"
          inputProps={{
            accept: ".xlsx",
          }}
          onChange={handleFileChangeHandler}
          type="file"
          helperText="Must be .xlsx"
        />

        <Box className={classes.gridContainer}>
          <Box className={titleRadio.root}>
            {uploadErrorMessage && (
              <TextWrapper variant="h6" gutterBottom component="div">
                <PriorityHighOutlined style={{ fontSize: "1rem" }} />
                {uploadErrorMessage}
              </TextWrapper>
            )}
          </Box>
        </Box>
        {uploadErrors.length > 0 && (
          <TableContainer component={Paper} sx={{ width: 400 }}>
            <Table
              sx={{ minWidth: 650 }}
              size="small"
              aria-label="a dense table"
            >
              <TableHead>
                <TableRow>
                  <TableCell style={{ width: 100 }} align="left">
                    Row No.
                  </TableCell>
                  <TableCell align="left">Description</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {uploadErrors?.map((value, key) => (
                  <TableRow
                    key={key}
                    sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
                  >
                    <TableCell style={{ width: 20 }} align="left">
                      {value?.valueKey}
                    </TableCell>
                    <TableCell align="left">{value?.field}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </C1Dialog>
    </>
  );
};

C1DataTable.propTypes = {
  isServer: PropTypes.bool,
  dbName: PropTypes.exact({
    list: PropTypes.array,
  }),
  url: PropTypes.string,
  title: PropTypes.any,
  columns: PropTypes.array.isRequired,
  defaultOrder: PropTypes.string,
  defaultOrderDirection: PropTypes.oneOf(["asc", "desc"]),
  showTemplate: PropTypes.exact({
    downloadDataHandler: PropTypes.object,
    downloadHandler: PropTypes.func || PropTypes.object,
    uploadHandler: PropTypes.func || PropTypes.object,
  }),
  showCustomDownload: PropTypes.exact({
    title: PropTypes.string,
    handler: PropTypes.func,
  }),
  showManualAdd: PropTypes.shape({
    path: PropTypes.string,
    popUpHandler: PropTypes.func,
  }),
  showBlClaim: PropTypes.shape({
    type: PropTypes.oneOf(["popUp", "redirect"]),
    path: PropTypes.string,
    popUpHandler: PropTypes.func,
  }),
  showHistory: PropTypes.shape({
    type: PropTypes.oneOf(["redirect"]),
    path: PropTypes.string,
    title: PropTypes.string,
  }),
  viewHistory: PropTypes.exact({
    title: PropTypes.string,
    handler: PropTypes.func,
    icon: PropTypes.any,
  }),
  viewTextFilter: PropTypes.element,
  showAdd: PropTypes.shape({
    type: PropTypes.oneOf(["popUp", "redirect"]),
    path: PropTypes.string,
    popUpHandler: PropTypes.func,
  }),
  showClaim: PropTypes.shape({
    type: PropTypes.oneOf(["popUp", "redirect"]),
    path: PropTypes.string,
    popUpHandler: PropTypes.func,
  }),
  showFfSubmit: PropTypes.shape({
    type: PropTypes.oneOf(["popUp", "redirect"]),
    path: PropTypes.string,
    popUpHandler: PropTypes.func,
  }),
  showVerify: PropTypes.shape({
    type: PropTypes.oneOf(["popUp", "redirect"]),
    path: PropTypes.string,
    popUpHandler: PropTypes.func,
  }),
  showAction: PropTypes.shape({
    type: PropTypes.oneOf(["popUp", "redirect"]),
    path: PropTypes.string,
    popUpHandler: PropTypes.func,
  }),
  isShowToolbar: PropTypes.bool,
  isShowDownloadData: PropTypes.bool,
  isShowFilter: PropTypes.bool,
  isShowFilterChip: PropTypes.bool,
  isShowViewColumns: PropTypes.bool,
  isShowDownload: PropTypes.bool,
  isShowPrint: PropTypes.bool,
  isRowSelectable: PropTypes.bool,
  isShowPagination: PropTypes.bool,
  filterBy: PropTypes.array,
  onFilterChange: PropTypes.func,
  onFilterChipClose: PropTypes.func,
  guideId: PropTypes.string,
  showPay: PropTypes.exact({
    title: PropTypes.string,
    handler: PropTypes.func,
    icon: PropTypes.any,
  }),
  customRowsPerPage: PropTypes.array,
  customPerPage: PropTypes.number,
};

export default withErrorHandler(C1DataTable);
