import React, {useEffect, useState} from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";

import C1DataTable from 'app/c1component/C1DataTable';
import C1ListPanel from "app/c1component/C1ListPanel";
import C1DataTableActions from 'app/c1component/C1DataTableActions';
import {getActiveMode, getDeActiveMode, getStatusDesc} from "../../../../c1utils/statusUtils";
import useHttp from "../../../../c1hooks/http";
import ConfirmationDialog from "../../../../../matx/components/ConfirmationDialog";

const NotificationTypeList = () => {

    const { t } = useTranslation(['masters']);
    const { isLoading, res, error, urlId, sendRequest } = useHttp();
    const [isRefresh, setRefresh] = useState(false);
    const [loading, setLoading] = useState(false);
    const [openPopupAction, setOpenPopupAction] = useState(false);
    const[action] = useState("");
    const [id] = useState("");
    useEffect(() => {
        if (!isLoading && !error && res) {
            if (urlId==='getForActive'){
                sendRequest("/api/v2/clickargo/master/ckCtMstAlert/"+ res.data?.altId +"/activate", "active", "put", res.data);
            }
            if (urlId==='active' || urlId==='deActive'){
                setRefresh(true);
                setLoading(false);
            }
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isLoading, res, error, urlId]);
    const columns = [
        {
            name: "altId", // field name in the row object
            label: t("notificationType.list.table.headers.notCode"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
                display: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <div className="flex items-center">
                            <div className="ml-3">
                                <Link to={"/master/agency/view/" + tableMeta.rowData[0]}>
                                    <h5 className="my-0 text-15">{value}</h5>
                                </Link>
                            </div>
                        </div>
                    );
                },
            },
        },
        {
            name: "altName",
            label: "Condition Name",
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <>
                            {(value ? value.split(",") : []).map((row, idx) => {
                                if (idx >= 5) {
                                    return;
                                }
                                return (
                                    <>
                                        <small
                                            className="px-2 py-4px border-radius-8"
                                            style={{backgroundColor: "#e0e0e0", color: "#2c2e32"}}>
                                            {row}
                                        </small>
                                    </>
                                )
                            })}
                        </>
                    );
                }
            },
        },
        {
            name: "altModule",
            label: "Module Type",
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <>
                            {(value ? value.split(",") : []).map((row, idx) => {
                                if (idx >= 5) {
                                    return;
                                }
                                return (
                                    <>
                                        <small
                                            className="px-2 py-4px border-radius-8"
                                            style={{backgroundColor: "#e0e0e0", color: "#2c2e32"}}>
                                            {row}
                                        </small>
                                    </>
                                )
                            })}
                        </>
                    );
                }
            },
        },
        {
            name: "altNotificationType",
            label: "Notification Type",
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <>
                            {(value ? value.split(",") : []).map((row, idx) => {
                                if (idx >= 5) {
                                    return;
                                }
                                return (
                                    <>
                                        <small
                                            className="px-2 py-4px border-radius-8"
                                            style={{backgroundColor: "#e0e0e0", color: "#2c2e32"}}>
                                            {row}
                                        </small>
                                    </>
                                )
                            })}
                        </>
                    );
                }
            },
        },
        {
            name: "altTemplateId",
            label: "Template Code",
            options: {
                filter: false,
            },
        },
        {
            name: "altConditionType",
            label: "Condition Type",
            options: {
                filter: false,
            },
        },
        {
            name: "altStatus",
            label: "Status",
            options: {
                sort: true,
                filter: false,
                filterType: "dropdown",
                customBodyRender: (value) => getStatusDesc(value),
                filterOptions: {
                    names: ["A", "I"],
                    renderValue: (v) => {
                        if (v === "A") {
                            return "Active";
                        } else if (v === "I") {
                            return "InActive";
                        }
                    },
                },
                customFilterListOptions: {
                    render: (v) => {
                        if (v === "A") {
                            return "Active";
                        } else if (v === "I") {
                            return "InActive";
                        }
                    },
                },
            },
        },
        {
            name: "action",
            label: " ",
            options: {
                filter: false,
                display: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return <C1DataTableActions
                        // editPath={"/master/notificationType/edit/" + tableMeta.rowData[0]}
                        viewPath={"/master/notificationType/view/" + tableMeta.rowData[0]}
                        // deActiveEventHandler={getDeActiveMode(tableMeta.rowData[6]) ? () => handleDeActiveHandler(tableMeta.rowData[0]): null}
                        // activeEventHandler={getActiveMode(tableMeta.rowData[6]) ? () => handleActiveHandler(tableMeta.rowData[0]): null}
                    />
                },
            },
        },
    ];


    const handleDeActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest("/api/v2/clickargo/master/ckCtMstAlert/"+ id, "deActive", "delete", {});
    }

    const handleActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest("/api/v2/clickargo/master/ckCtMstAlert/" + id, "getForActive", "get", {})
    }

    const handleConfirmAction = () => {
        setLoading(true);
        setRefresh(false);

        if(action === "delete") {
            sendRequest("/api/v2/clickargo/master/ckCtMstAlert/"+ id, "deActive", "delete", {});
        }else if(action === "active") {
            sendRequest("/api/v2/clickargo/master/ckCtMstAlert/" + id, "getForActive", "get", {})
        }
        setOpenPopupAction(false);
    }

    return (
        <C1ListPanel
            routeSegments={[
                { name: t("notificationType.list.routeSegment") },
            ]}>
            <C1DataTable url="/api/v2/clickargo/master/ckCtMstAlert"
                columns={columns}
                title={t("notificationType.list.table.title")}
                defaultOrder="altDtCreate"
                isServer={true}
                isRefresh={isRefresh}
                // showAdd={{
                //     path: "/master/notificationType/new"
                // }}
            />
            <ConfirmationDialog open={openPopupAction}
                                onConfirmDialogClose={() => setOpenPopupAction(false)}
                                text={t("common:confirmMsgs.confirm.content")}
                                title={t("common:confirmMsgs.confirm.title")}
                                onYesClick={(e) => handleConfirmAction(e)} />
        </C1ListPanel>

    );
};

export default NotificationTypeList;
