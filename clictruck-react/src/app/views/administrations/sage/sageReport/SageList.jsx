import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import useHttp from "app/c1hooks/http";
import C1DataTable from "app/c1component/C1DataTable";
import C1DataTableActions from "app/c1component/C1DataTableActions";
import C1ListPanel from "app/c1component/C1ListPanel";
import { customFilterDateDisplay, formatDate, previewPDF } from "app/c1utils/utility";
import {Button, ButtonGroup} from "@material-ui/core";
import HistoryIcon from '@material-ui/icons/HistoryOutlined';
import AssignmentTurnedInOutlinedIcon from '@material-ui/icons/AssignmentTurnedInOutlined';

const SageList = () => {
    
    const { t } = useTranslation(["administration"]);

    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();

    const [loading, setLoading] = useState(false);

    const [isRefresh, setRefresh] = useState(false);
    const [showHistory, setShowHistory] = useState(false);
    const [filterBy, setFilterBy] = useState([{ attribute: "history", value: "default" }]);


    const columns = [
        {
            name: "sageId",
            label: "sageId",
            options: {
                display: "excluded",
                sort: false,
                filter: false,
            },
        },
        {
            name: "sageBatchNo",
            label: "Batch NO",
            options: {
                sort: true,
                filter: true,
            },
        },
        {
            name: "sageDtStart",
            label: "Start Date",
            options: {
                filterType: 'custom',
                customFilterListOptions: {
                    render: v => v.map(l => l),
                    update: (filterList, filterPos, index) => {
                        filterList[index].splice(filterPos, 1);
                        return filterList;
                    }
                },
                filterOptions: {
                    display: customFilterDateDisplay
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                }
            },
        },
        {
            name: "sageDtEnd",
            label: "End Date",
            options: {
                filterType: 'custom',
                customFilterListOptions: {
                    render: v => v.map(l => l),
                    update: (filterList, filterPos, index) => {
                        filterList[index].splice(filterPos, 1);
                        return filterList;
                    }
                },
                filterOptions: {
                    display: customFilterDateDisplay
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                }
            },
        },
        {
            name: "sageFileLoc",
            label: "sageFileLoc",
            options: {
                display: "excluded",
                filter:false,
            },
        },
        {
            name: "action",
            label: " ",
            options: {
                display: true,
                viewColumns: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    const sageId = tableMeta.rowData[0];
                    return (
                        <C1DataTableActions
                        downloadFileEventHandler={(e) => handleViewFile(e, sageId)} 
                        />
                    );
                },
            },
        },
    ];

    const handleViewFile = (e, sageId) => {
        setLoading(true);
        // /home/vcc/appAttachments/clictruck/0012/AR Report 04 Jul 2023 11_55_56.xls
        sendRequest(`/api/v1/clickargo/sage/downloadById/${sageId}`, "downloadFile");
    };

    const viewFile = (fileName, data) => {
        previewPDF(fileName, data);
    };
    useEffect(() => {
        if (!isLoading && !error && res) {
            setLoading(isLoading);
            switch (urlId) {
                
                case "downloadFile": {
                    viewFile(res?.data?.attName, res?.data?.attData);
                    setLoading(false)
                    break;
                }
                default: break;
            }
        }
    }, [urlId, isLoading, isFormSubmission, res, error]);

    useEffect(() => {
        if (showHistory) {
            setFilterBy([{ attribute: "history", value: "D" }]);
            console.log("history");
        } else {
            setFilterBy([{ attribute: "history", value: "A" }])
            console.log("default");
        }
    }, [showHistory]);

    const toggleHistory = (filter) => {
        setLoading(true);
        setRefresh(false);
        setShowHistory(filter === 'history' ? true : false);
        setTimeout(() => setRefresh(true), 500);
        setTimeout(() => setLoading(false), 500);
    }

    return (
        <div>
            <C1ListPanel routeSegments={[{ name: "Sage Excel Report" }]}>
                <C1DataTable
                    url="/api/v1/clickargo/clictruck/administrator/sage"
                    columns={columns}
                    title={"Sage Excel Report"}
                    defaultOrder="sageBatchNo"
                    isRefresh={isRefresh}
                    isServer={true}
                    viewTextFilter={
                        <React.Fragment key={0}>
                            <ButtonGroup style={{ marginRight: -24, height: 30 }} color="primary" key="viewTextFilter" aria-label="outlined primary button group">
                                <Button key="history" style={{ marginLeft: 10 }} startIcon={<HistoryIcon />} size="small" variant={showHistory ? "contained" : null} onClick={() => toggleHistory("history")}>{t("listing:common.history")}</Button>
                                <Button key="active" startIcon={<AssignmentTurnedInOutlinedIcon />} variant={!showHistory ? "contained" : null} size="small" onClick={() => toggleHistory("active")}>{t("listing:common.active")}</Button>
                            </ButtonGroup>
                        </React.Fragment>
                    }
                    filterBy={filterBy}
                />
            </C1ListPanel>
        </div>
    );
};

export default SageList;
