import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import C1DataTable from 'app/c1component/C1DataTable';
import C1ListPanel from "app/c1component/C1ListPanel";
import C1DataTableActions from 'app/c1component/C1DataTableActions';

import { useTranslation } from "react-i18next";
import useHttp from "app/c1hooks/http";
import { getActiveMode, getDeActiveMode, getStatusDesc } from "app/c1utils/statusUtils";

const HsCodeList = () => {

    const { t } = useTranslation(['masters']);
    const [isRefresh, setRefresh] = useState(false);
    const [loading, setLoading] = useState(false);
    const { isLoading, res, error, urlId, sendRequest } = useHttp();

    useEffect(() => {
        if (!isLoading && !error && res) {
            if (urlId === 'getForActive') {
                sendRequest("/api/co/master/entity/hsCode/" + res.data.hsCode + "/activate", "active", "put", res.data)
            }
            if (urlId === 'active' || urlId === 'deActive') {
                setRefresh(true);
                setLoading(false);
            }
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isLoading, res, error, urlId]);

    const columns = [
        {
            name: "hsCode", // field name in the row object
            label: t("hsCode.list.table.headers.hsCode"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <div className="flex items-center">
                            <div className="ml-3">
                                <Link to={"/master/hsCode/view/" + tableMeta.rowData[0]}>
                                    <h5 className="my-0 text-15">{value}</h5>
                                </Link>
                            </div>
                        </div >
                    );
                },
            },
        },
        {
            name: "hsDescription",
            label: t("hsCode.list.table.headers.hsDescription"),
            options: {
                filter: true,
            },
        },
        {
            name: "hsStatus",
            label: t("hsCode.list.table.headers.hsStatus"),
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
                        editPath={getDeActiveMode(tableMeta.rowData[2]) ? "/master/hsCode/edit/" + tableMeta.rowData[0] : null}
                        viewPath={"/master/hsCode/view/" + tableMeta.rowData[0]}
                        deActiveEventHandler={getDeActiveMode(tableMeta.rowData[2]) ? () => handleDeActiveHandler(tableMeta.rowData[0]) : null}
                        activeEventHandler={getActiveMode(tableMeta.rowData[2]) ? () => handleActiveHandler(tableMeta.rowData[0]) : null}
                    />
                }
            },
        },
    ];

    const handleDeActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest("/api/co/master/entity/hsCode/" + id, "deActive", "delete", {});
    }

    const handleActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest("/api/co/master/entity/hsCode/" + id, "getForActive", "get", {})
    }

    return (
        <C1ListPanel
            routeSegments={[
                { name: t("hsCode.list.routeSegment") },
            ]}>
            <C1DataTable url="/api/co/master/entity/hsCode"
                columns={columns}
                title={t("hsCode.list.table.title")}
                defaultOrder="hsCode"
                statusFieldName="hsStatus"
                isRefresh={isRefresh}
                showAdd={{
                    path: "/master/hsCode/new"
                }}
            />
        </C1ListPanel>
    );
};

export default HsCodeList;
