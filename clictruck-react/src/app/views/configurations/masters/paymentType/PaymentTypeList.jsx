import React, {useEffect, useState} from "react";
import C1DataTable from 'app/c1component/C1DataTable';
import C1ListPanel from "app/c1component/C1ListPanel";

import C1DataTableActions from 'app/c1component/C1DataTableActions';
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import {getActiveMode, getDeActiveMode, getStatusDesc} from "app/c1utils/statusUtils";
import useHttp from "../../../../c1hooks/http";
import {MST_PAYMENT_TYPE_TABLE_URL} from "../../../../c1utils/const";

const PaymentTypesList = () => {
    const { t } = useTranslation(["masters"]);
    const { isLoading, res, error, urlId, sendRequest } = useHttp();
    const [isRefresh, setRefresh] = useState(false);
    const [loading, setLoading] = useState(false);

    const handleDeActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest("/api/co/pedi/mst/entity/pediMstPaymentType/"+ id, "deActive", "delete", {});
    }

    const handleActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest("/api/co/pedi/mst/entity/pediMstPaymentType/" + id, "getForActive", "get", {})
    }

    useEffect(() => {
        if (!isLoading && !error && res) {
            if (urlId==='getForActive'){
                sendRequest("/api/co/pedi/mst/entity/pediMstPaymentType/"+ res.data.ptCode +"/activate", "active", "put", res.data);
            }
                setRefresh(true);
                setLoading(false);

        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isLoading, res, error, urlId]);

    const columns = [
        {
            name: "ptCode", // field name in the row object
            label: t("masters:paymentType.list.table.headers.ptCode"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <div className="flex items-center">
                            <div className="ml-3">
                                <Link to={"/master/paymentType/view/" + tableMeta.rowData[0]}>
                                    <h5 className="my-0 text-15">{value}</h5>
                                </Link>
                            </div>
                        </div>
                    );
                },
            },
        },
        
        {
            name: "ptName",
            label: t("masters:paymentType.list.table.headers.ptName"),
            options: {
                filter: true,
            },
        },
        {
            name: "ptDesc",
            label: t("masters:paymentType.list.table.headers.ptDesc"),
            options: {
                filter: true,
            },
        },
        {
            name: "ptStatus",
            label: t("masters:paymentType.list.table.headers.ptStatus"),
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
                            editPath={getDeActiveMode(tableMeta.rowData[3]) ? "/master/paymentType/edit/" + tableMeta.rowData[0]: null}
                            viewPath={"/master/paymentType/view/" + tableMeta.rowData[0]}
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
                { name: t("masters:paymentType.list.routeSegment") },
            ]}>
            <C1DataTable url={MST_PAYMENT_TYPE_TABLE_URL}
                columns={columns}
                isRefresh={isRefresh}
                title={t("masters:paymentType.details.breadCrumbs.main")}
                defaultOrder="ptCode"
                showAdd={{
                    path: "/master/paymentType/new"
                }}
            />
        </C1ListPanel>
    );
};

export default PaymentTypesList;
