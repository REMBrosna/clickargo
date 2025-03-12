import React, {useEffect, useState} from "react";
import C1DataTable from 'app/c1component/C1DataTable';
import C1ListPanel from "app/c1component/C1ListPanel";

import C1DataTableActions from 'app/c1component/C1DataTableActions';
import { Link } from "react-router-dom";
import { MST_THRUSTER_TYPE_URL } from "app/c1utils/const";
import { useTranslation } from "react-i18next";
import {getActiveMode, getDeActiveMode, getStatusDesc} from "app/c1utils/statusUtils";
import useHttp from "../../../../c1hooks/http";

const ThrusterTypesList = () => {
    const { t } = useTranslation(["masters"]);
    const { isLoading, res, error, urlId, sendRequest } = useHttp();
    const [isRefresh, setRefresh] = useState(false);
    const [loading, setLoading] = useState(false);

    const handleDeActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest("/api/co/pedi/mst/entity/pediMstThrusterType/"+ id, "deActive", "delete", {});
    }

    const handleActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest("/api/co/pedi/mst/entity/pediMstThrusterType/" + id, "getForActive", "get", {})
    }

    useEffect(() => {
        if (!isLoading && !error && res) {
            if (urlId==='getForActive'){
                sendRequest("/api/co/pedi/mst/entity/pediMstThrusterType/"+ res.data.neCode +"/activate", "active", "put", res.data);
            }
                setRefresh(true);
                setLoading(false);

        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isLoading, res, error, urlId]);

    const columns = [
        {
            name: "ttCode", // field name in the row object
            label: t("masters:thrusterType.list.table.headers.ttCode"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <div className="flex items-center">
                            <div className="ml-3">
                                <Link to={"/master/thrusterType/view/" + tableMeta.rowData[0]}>
                                    <h5 className="my-0 text-15">{value}</h5>
                                </Link>
                            </div>
                        </div>
                    );
                },
            },
        },
        
        {
            name: "ttName",
            label: t("masters:thrusterType.list.table.headers.ttName"),
            options: {
                filter: true,
            },
        },
        {
            name: "ttDesc",
            label: t("masters:thrusterType.list.table.headers.ttDesc"),
            options: {
                filter: true,
            },
        },
        {
            name: "ttStatus",
            label: t("masters:thrusterType.list.table.headers.ttStatus"),
            options: {
                sort: true,
                filter: true,
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
                viewColumns: false,
                customBodyRender: (value, tableMeta) => {
                    return (
                        <C1DataTableActions
                            editPath={getDeActiveMode(tableMeta.rowData[3]) ? "/master/thrusterType/edit/" + tableMeta.rowData[0]: null}
                            viewPath={"/master/thrusterType/view/" + tableMeta.rowData[0]}
                            deActiveEventHandler={getDeActiveMode(tableMeta.rowData[3]) ? () => handleDeActiveHandler(tableMeta.rowData[0]): null}
                            activeEventHandler={getActiveMode(tableMeta.rowData[3]) ? () => handleActiveHandler(tableMeta.rowData[0]): null}
                        />
                    );
                },
            },
        },
    ];



    return (
        <C1ListPanel
            routeSegments={[
                { name: t("masters:thrusterType.list.routeSegment") },
            ]}>
            <C1DataTable url={MST_THRUSTER_TYPE_URL}
                columns={columns}
                isRefresh={isRefresh}
                title={t("masters:thrusterType.details.breadCrumbs.main")}
                defaultOrder="ttCode"
                showAdd={{
                    path: "/master/thrusterType/new"
                }}
            />
        </C1ListPanel>
    );
};

export default ThrusterTypesList;
