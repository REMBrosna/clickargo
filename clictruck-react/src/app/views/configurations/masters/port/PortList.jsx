import React, {useEffect, useState} from "react";
import { useTranslation } from "react-i18next";

import C1DataTable from 'app/c1component/C1DataTable';
import C1ListPanel from "app/c1component/C1ListPanel";

import C1DataTableActions from 'app/c1component/C1DataTableActions';
import { Link } from "react-router-dom";
import {getActiveMode, getDeActiveMode, getStatusDesc} from "../../../../c1utils/statusUtils";
import useHttp from "../../../../c1hooks/http";


const PortList = () => {

    const { t } = useTranslation(['masters']);
    const [isRefresh, setRefresh] = useState(false);
    const [loading, setLoading] = useState(false);
    const { isLoading, res, error, urlId, sendRequest } = useHttp();

    useEffect(() => {
        if (!isLoading && !error && res) {
            if (urlId==='getForActive'){
                sendRequest("/api/co/master/entity/port/"+ res.data.uomCode +"/activate", "active", "put", res.data)
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
            name: "portCode", // field name in the row object
            label: t("ports.list.table.headers.portCode"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <div className="flex items-center">
                            <div className="ml-3">
                                <Link to={"/master/port/view/" + tableMeta.rowData[0]}>
                                    <h5 className="my-0 text-15">{value}</h5>
                                </Link>
                            </div>
                        </div>
                    );
                },
            },
        },
        {
            name: "portDescription",
            label: t("ports.list.table.headers.portDescription"),
            options: {
                filter: true,
            },
        },
        {
            name: "portDescriptionOth",
            label: t("ports.list.table.headers.portDescriptionOth"),
            options: {
                filter: true,
            },
        },
        {
            name: "TMstCountry.ctyDescription",
            label: t("ports.list.table.headers.TMstCountry"),
            options: {
                filter: true,
            },
        },
        {
            name: "TMstPortType.porttDescription",
            label: t("ports.list.table.headers.TMstPortType"),
            options: {
                filter: true,
            },
        },
        {
            name: "portStatus",
            label: t("ports.list.table.headers.portStatus"),
            options: {
                filter: true,
                sort: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return getStatusDesc(value);
                }
            },
        },
        {
            name: "action",
            label: " ",
            options: {
                filter: false,
                display: true,
                viewColumns: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return <C1DataTableActions
                        editPath={getDeActiveMode(tableMeta.rowData[5]) ?"/master/port/edit/" + tableMeta.rowData[0]: null}
                        viewPath={"/master/port/view/" + tableMeta.rowData[0]}
                        deActiveEventHandler={getDeActiveMode(tableMeta.rowData[5]) ? () => handleDeActiveHandler(tableMeta.rowData[0]): null}
                        activeEventHandler={getActiveMode(tableMeta.rowData[5]) ? () => handleActiveHandler(tableMeta.rowData[0]): null}
                    />
                },
            },
        },
    ];

    const handleDeActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest("/api/co/master/entity/port/"+ id, "deActive", "delete", {});
    }

    const handleActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest("/api/co/master/entity/port/" + id, "getForActive", "get", {})
    }

    return (
        <C1ListPanel
            routeSegments={[
                { name: t("ports.list.routeSegment") },
            ]}>
            <C1DataTable url="/api/co/master/entity/port"
                columns={columns}
                title={t("ports.list.table.title")}
                defaultOrder="portCode"
                statusFieldName="portStatus"
                isRefresh={isRefresh}
                showAdd={{
                    path: "/master/port/new"
                }}
            />
        </C1ListPanel>
    );
};

export default PortList;
