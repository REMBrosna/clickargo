import { Divider, Grid, makeStyles, Typography } from "@material-ui/core";
import GetAppIcon from '@material-ui/icons/GetAppOutlined';
import React, { useContext, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";

import ChipStatus from "app/atomics/atoms/ChipStatus";
import DataTable from "app/atomics/organisms/DataTable";
import C1InputField from "app/c1component/C1InputField";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import useHttp from "app/c1hooks/http";
import { RecordStatus, TaxReportStatus } from "app/c1utils/const";
import { iconStyles } from "app/c1utils/styles";
import { customFilterDateDisplay, downloadFile, formatDate, getValue, isArrayNotEmpty, previewPDF } from "app/c1utils/utility";
import useAuth from "app/hooks/useAuth";
import { MatxLoading } from "matx";

import TaxContext from "../TaxContext";

const useStyles = makeStyles((theme) => ({
    backdrop: {
        zIndex: theme.zIndex.drawer + 1,
        color: '#fff',
    },
    amountCell: {
        justifyContent: 'center',
        textAlign: 'right',
        display: 'flex',
        flex: 1
    }
}));

const TaxReports = () => {
    const { user } = useAuth();
    const { t } = useTranslation(["buttons", "payments", "common", "listing"]);
    const classes = iconStyles();
    const bdClasses = useStyles();
    const history = useHistory();

    const { refreshPage, setRefreshPage } = useContext(TaxContext)

    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();
    const [loading, setLoading] = useState(false);
    const [showHistory, setShowHistory] = useState(false);
    const [filterBy, setFilterBy] = useState([{ attribute: "history", value: "default" }]);
    const [isRefresh, setRefresh] = useState(false);
    const [reportName, setReportName] = useState("")

    const defaultSnackbarValue = {
        success: false,
        successMsg: "",
        error: false,
        errorMsg: "",
        redirectPath: "",
    };
    const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);


    useEffect(() => {
        if (!isLoading && !error && res) {
            if (urlId === "downloadExcelTax") {
                const downloadData = res?.data;

                downloadFile(`${reportName}`, downloadData);
                setRefresh(true)
                setRefreshPage(refreshPage + 1)
            }

        }

    }, [urlId, isLoading, error, res]);

    useEffect(() => {
        console.log("History", showHistory);
        if (showHistory) {
            setFilterBy([{ attribute: "history", value: "history" }]);
        } else {
            setFilterBy([{ attribute: "history", value: "default" }])

        }
    }, [showHistory]);

    const columns = [
        {
            name: "trId",
            label: t("listing:taxReport.reportId"),
        },
        {
            name: "trName",
            label: t("listing:taxReport.reportName"),
        },
        {
            name: "trNumRecords",
            label: t("listing:taxReport.noRecords"),
            options: {
                filter: true,
                sort: true,
                filterType: 'custom',
                filterOptions: {
                    display: (filterList, onChange, index, column) => {
                        return <C1InputField
                            label={column.label}
                            name={column.name}
                            isServer
                            type="number"
                            onChange={event => {
                                filterList[index][0] = event.target.value;
                                onChange(filterList[index], index, column);
                            }}
                            value={filterList[index][0] || ""} />
                    }
                },
            }
        },
        {
            name: "trDtCreate",
            label: t("listing:trucklist.dtCreate"),
            options: {
                sort: true,
                filter: true,
                filterType: 'custom',
                filterOptions: {
                    display: customFilterDateDisplay
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                }
            }
        },
        {
            name: "trDtLupd",
            label: t("listing:taxReport.dateDownloaded"),
            options: {
                sort: true,
                filter: true,
                filterType: 'custom',
                filterOptions: {
                    display: customFilterDateDisplay
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                }
            }
        },
        {
            name: "trStatus",
            label: t("listing:taxReport.status"),
            options: {
                sort: true,
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: [TaxReportStatus.ACTIVE.code, TaxReportStatus.DOWNLOADED.code],
                    renderValue: v => {
                        switch (v) {
                            case TaxReportStatus.ACTIVE.code: return TaxReportStatus.ACTIVE.desc;
                            case TaxReportStatus.DOWNLOADED.code: return TaxReportStatus.DOWNLOADED.desc;
                            default: break;
                        }
                    }
                },
                customFilterListOptions: {
                    render: v => {
                        switch (v) {
                            case TaxReportStatus.ACTIVE.code: return TaxReportStatus.ACTIVE.desc;
                            case TaxReportStatus.DOWNLOADED.code: return TaxReportStatus.DOWNLOADED.desc;
                            default: break;
                        }
                    }
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    let status = "";
                    switch (value) {
                        case "A": {
                            // status = "ACTIVE";
                            status = <ChipStatus text="Active" color="#00D16D" />
                            break;
                        }
                        case "D": {
                            // status = "DOWNLOADED";
                            status = <ChipStatus text="Downloaded" color="#0095A9" />
                            break;
                        }

                        default: break;
                    }
                    return status;
                }
            }
        },
        {
            name: "action",
            label: t("listing:taxReport.action"),
            options: {
                filter: false,
                sort: false,
                display: true,
                viewColumns: false,
                customHeadLabelRender: (columnMeta) => {
                    return <div style={{ textAlign: "center" }}>{columnMeta.label}</div>
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    const trId = tableMeta?.rowData[0];
                    const trName = tableMeta?.rowData[1];
                    return <Grid container direction="row" justifyContent='center' alignItems="center">
                        <Grid item sm={6} xs={6}>
                            <C1LabeledIconButton
                                tooltip={t("buttons:download")}
                                label={t("buttons:download")}
                                action={() => handleDownloadToggle(trId, trName)}
                            >
                                <GetAppIcon />
                            </C1LabeledIconButton>
                        </Grid>
                    </Grid>
                }
            }
        }
    ]

    const handleDownloadToggle = (fileId, fileName) => {
        setReportName(fileName)
        const url = `/api/v1/clickargo/clictruck/attach/taxreport/${fileId}`;
        sendRequest(url, 'downloadExcelTax', 'GET');
        setRefresh(false)
        // console.log("send request download", fileId);
    }

    const toggleHistory = (filter) => {
        setRefresh(false);
        setShowHistory(filter === 'history' ? true : false);
        setTimeout(() => setRefresh(true), 500);
        setTimeout(() => setLoading(false), 500);
    }

    return loading ? <MatxLoading /> : (
        <React.Fragment>

            <DataTable
                columns={columns}
                isServer={true}
                defaultOrder="trDtCreate"
                defaultOrderDirection={"desc"}
                isShowViewColumns={true}
                isShowDownload={true}
                isShowPrint={true}
                isShowFilter={true}
                url={"/api/v1/clickargo/clictruck/taxreport"}
                isRefresh={isRefresh}
                isShowToolbar={true}
                isShowPagination={true}
                showActiveHistoryButton={toggleHistory}
                isShowFilterChip
                filterBy={filterBy}
            />

        </React.Fragment>
    )

}

export default TaxReports;
