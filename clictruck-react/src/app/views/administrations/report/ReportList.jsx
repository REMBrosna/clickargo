import { Grid } from "@material-ui/core";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

import C1DataTable from 'app/c1component/C1DataTable';
import C1ListPanel from "app/c1component/C1ListPanel";
import useHttp from "app/c1hooks/http";

const ReportList = () => {

    const [isRefresh, setRefresh] = useState(false);

    let filteredDb = { list: new Array(0) };

    const [reportDB, setReportDB] = useState(filteredDb);
    const [initRptId, setInitRptId] = useState();

    const { isLoading, res, error, urlId, sendRequest } = useHttp();

    const { t } = useTranslation(['administration']);

    const reportGroupColumns = [
        {
            name: "id.rptcCat",
            options: {
                display: false,
            },
        },
        {
            name: "rptcDesc",
            label: t("report.list.reportGroup"),
            options: {
                display: true,
                sort: false,
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {

                    //setting the initReportId to first rowIndex
                    if (tableMeta.rowData && tableMeta.rowData[0] && tableMeta.rowIndex === 0) {
                        setInitRptId(tableMeta.rowData[0]);
                    }

                    return (
                        <div className="ml-3" style={{ cursor: 'pointer' }}
                            onClick={() => handleReportGroupRowClick(tableMeta.rowData[0])}>
                            <h5 className="my-0 text-15"> {value} </h5>
                        </div>
                    );
                }
            },
        },
    ];

    const reportsColumns = [
        {
            name: "rptRptid",
            options: {
                display: false,
            },
        },
        {
            name: "rptCat",
            label: t("report.list.reportGroup"),
            options: {
                display: false,
                sort: true,
                filter: true,
            },
        },
        {
            name: "rptName",
            label: t("report.list.reportName"),
            options: {
                sort: true,
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <div className="flex items-center">
                            <div className="ml-3">
                                <Link to={`/reports/generate/${tableMeta.rowData[0]}`}>
                                    <h5 className="my-0 text-15"> {value} </h5>
                                </Link>
                            </div>
                        </div>
                    );
                },
            },
        },
        {
            name: "rptTitle",
            label: t("report.list.reportTitle"),
            options: {
                sort: true,
                filter: true
            },
        },
    ];

    const handleReportGroupRowClick = (value) => {
        setRefresh(false);
        sendRequest("/api/app/report/list/" + value, "doGet", "get", null)
    };

    useEffect(() => {

        sendRequest("/api/app/report/list/" + initRptId, "doGet", "get", null)
        // eslint-disable-next-line
    }, [initRptId]);

    useEffect(() => {
        // let msg = "";
        // let severity = "success";
        if (!isLoading && !error && res) {
            switch (urlId) {
                case "doGet": {
                    let filteredDb = { list: res.data };
                    setReportDB(filteredDb);
                    setRefresh(true);
                    break;
                }
                default: break;
            }
        } else if (error) {
            // msg = "Error encountered whilte trying to fetch data!";
            // severity = "error";
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isLoading, res, error, urlId]);

    return (<React.Fragment>
        <C1ListPanel routeSegments={[
            { name: t("report.title.reports"), },
        ]} isOverFlow={false}>

            <Grid container alignItems="flex-start" justifyContent="flex-start" spacing={2}>
                <Grid item lg={4} md={6} sm={12}>
                    <C1DataTable
                        url="/api/app/report/group"
                        columns={reportGroupColumns}
                        title={t("report.list.reportGroup")}
                        defaultOrder="rptcDesc"
                        isServer={true}
                        isRefresh={isRefresh}
                        isShowDownload={false}
                        isShowFilter={false}
                        isShowPrint={false}
                        isShowViewColumns={false}
                        isShowPagination={false} />
                </Grid>
                <Grid item lg={8} md={6} sm={12}>
                    <C1DataTable
                        columns={reportsColumns}
                        title={t("report.title.reports")}
                        defaultOrder="name"
                        isServer={false}
                        isRefresh={isRefresh}
                        isShowDownload={false}
                        isShowFilter={false}
                        isShowPrint={false}
                        isShowViewColumns={false}
                        dbName={reportDB} />
                </Grid>

            </Grid>
        </C1ListPanel>
    </React.Fragment>
    );
}

export default ReportList;