import React, {useEffect, useState} from "react";

import C1DataTable from 'app/c1component/C1DataTable';
import C1DataTableActions from 'app/c1component/C1DataTableActions';
import { Link } from "react-router-dom";
import C1ListPanel from "app/c1component/C1ListPanel";
import { useTranslation } from "react-i18next";
import useHttp from "../../../../c1hooks/http";
import {getActiveMode, getDeActiveMode, getStatusDesc} from "../../../../c1utils/statusUtils";

const CurrencyCodeList = () => {

    const { t } = useTranslation(['masters']);
    const [isRefresh, setRefresh] = useState(false);
    const [loading, setLoading] = useState(false);
    const { isLoading, res, error, urlId, sendRequest } = useHttp();

    useEffect(() => {
        if (!isLoading && !error && res) {
            if (urlId==='getForActive'){
                sendRequest("/api/co/master/entity/currency/"+ res.data.ccyCode +"/activate", "active", "put", res.data)
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
            name: "ccyCode", // field name in the row object
            label: t("currency.list.table.headers.ccyCode"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <div className="flex items-center">
                            <div className="ml-3">
                                <Link to={"/master/currency/view/" + tableMeta.rowData[0]}>
                                    <h5 className="my-0 text-15">{value}</h5>
                                </Link>
                            </div>
                        </div>
                    );
                },
            },
        },
        {
            name: "ccyDescription",
            label: t("currency.list.table.headers.ccyDescription"),
            options: {
                filter: true,
            },
        },
        {
            name: "ccyDescriptionOth",
            label: t("currency.list.table.headers.ccyDescriptionOth"),
            options: {
                filter: true,
            },
        },
        {
            name: "ccyStatus",
            label: t("currency.list.table.headers.ccyStatus"),
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
                        editPath={getDeActiveMode(tableMeta.rowData[3]) ? "/master/currency/edit/" + tableMeta.rowData[0]: null}
                        viewPath={"/master/currency/view/" + tableMeta.rowData[0]}
                        deActiveEventHandler={getDeActiveMode(tableMeta.rowData[3]) ? () => handleDeActiveHandler(tableMeta.rowData[0]): null}
                        activeEventHandler={getActiveMode(tableMeta.rowData[3]) ? () => handleActiveHandler(tableMeta.rowData[0]): null}
                    />
                }
            },
        },
    ];

    const handleDeActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest("/api/co/master/entity/currency/"+ id, "deActive", "delete", {});
    }

    const handleActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest("/api/co/master/entity/currency/" + id, "getForActive", "get", {})
    }

    return (
        <C1ListPanel
            routeSegments={[
                { name: t("currency.list.routeSegment") },
            ]}
        >
            <C1DataTable url="/api/co/master/entity/currency"
                columns={columns}
                title={t("currency.list.table.title")}
                defaultOrder="ccyCode"
                isRefresh={isRefresh}
                statusFieldName="ccyStatus"
                showAdd={{
                    path: "/master/currency/new"
                }} />
        </C1ListPanel>


    );
}

export default CurrencyCodeList;