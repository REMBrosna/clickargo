import React, {useEffect, useState} from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import C1DataTable from 'app/c1component/C1DataTable';
import C1ListPanel from 'app/c1component/C1ListPanel';
import C1DataTableActions from 'app/c1component/C1DataTableActions';
import useHttp from "../../../../c1hooks/http";
import {getActiveMode, getDeActiveMode, getStatusDesc} from "../../../../c1utils/statusUtils";

const UOMCodesList = () => {

    const { t } = useTranslation(['masters']);
    const [isRefresh, setRefresh] = useState(false);
    const [loading, setLoading] = useState(false);
    const { isLoading, res, error, urlId, sendRequest } = useHttp();

    useEffect(() => {
        if (!isLoading && !error && res) {
            if (urlId==='getForActive'){
                sendRequest("/api/co/master/entity/uom/"+ res.data.uomCode +"/activate", "active", "put", res.data)
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
            name: "uomCode", // field name in the row object
            label: t("uom.list.table.headers.uomCode"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <div className="flex items-center">
                            <div className="ml-3">
                                <Link to={"/master/uom/view/" + tableMeta.rowData[0]}>
                                    <h5 className="my-0 text-15">{value}</h5>
                                </Link>
                            </div>
                        </div>
                    );
                },
            },
        },
        {
            name: "uomDescription",
            label: t("uom.list.table.headers.uomDescription"),
            options: {
                filter: true,
            },
        },
        {
            name: "uomDescriptionOth",
            label: t("uom.list.table.headers.otherLangDesc"),
            options: {
                filter: true,
            },
        },
        {
            name: "uomStatus",
            label: t("uom.list.table.headers.uomStatus"),
            options: {
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: ["A", "I"],
                    renderValue: v => {
                        switch (v) {
                            case 'A': return "Active";
                            case 'I': return "Inactive";
                            default: break;
                        }

                    }
                },
                customFilterListOptions: {
                    render: v => {
                        switch (v) {
                            case 'A': return "Active";
                            case 'I': return "Inactive";
                            default: break;
                        }
                    }
                },
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
                        editPath={getDeActiveMode(tableMeta.rowData[3])? "/master/uom/edit/" + tableMeta.rowData[0]: null}
                        viewPath={"/master/uom/view/" + tableMeta.rowData[0]}
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
        sendRequest("/api/co/master/entity/uom/"+ id, "deActive", "delete", {});
    }

    const handleActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest("/api/co/master/entity/uom/" + id, "getForActive", "get", {})
    }

    return (
        <C1ListPanel routeSegments={[
            { name: t("uom.list.routeSegment") },
        ]} >
            <C1DataTable url="/api/co/master/entity/uom"
                columns={columns}
                title={t("uom.list.table.title")}
                defaultOrder="uomCode"
                statusFieldName="uomStatus"
                isRefresh={isRefresh}
                showAdd={{
                    path: "/master/uom/new/0"
                }} />
        </C1ListPanel>

    );
};

export default UOMCodesList;
