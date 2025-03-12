import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import useHttp from "app/c1hooks/http";
import C1DataTable from "app/c1component/C1DataTable";
import C1ListPanel from "app/c1component/C1ListPanel";
import C1DataTableActions from "app/c1component/C1DataTableActions";
import {getActiveMode, getDeActiveMode, getStatusDesc} from "app/c1utils/statusUtils";
import { MST_PROVINCE_URL } from "app/c1utils/const";
import ConfirmationDialog from "../../../../../matx/components/ConfirmationDialog";

const ProvinceList = () => {
    const { t } = useTranslation(["masters", "common"]);
    const { isLoading, res, error, urlId, sendRequest } = useHttp();
    const [isRefresh, setRefresh] = useState(false);
    const [loading, setLoading] = useState(false);
    const [openPopupAction, setOpenPopupAction] = useState(false);
    const[action, setAction] = useState("");
    const [id, setId] = useState("");

    useEffect(() => {
        if (!isLoading && !error && res) {
            if (urlId==='getForActive'){
                sendRequest("/api/co/pedi/mst/entity/pediMstProvince/"+ res.data.portTeminalId +"/activate", "active", "put", res.data);
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
            name: "provinceId",
            label: t("masters:province.list.table.headers.provId"),
            options: {
                sort: true,
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <div className="flex items-center">
                            <div className="ml-3">
                                <Link to={"/master/province/view" + tableMeta.rowData[0]}>
                                    <h5 className="my-0 text-15"> {value} </h5>
                                </Link>
                            </div>
                        </div>
                    );
                },
            },
        },
        {
            name: "provinceDescription",
            label: t("masters:province.list.table.headers.provDesc"),
            options: {
                filter: true,
            },
        },
        {
            name: "mstCountry.ctyDescription",
            label: t("masters:province.list.table.headers.country"),
            options: {
                filter: true,
            },
        },
        {
            name: "provinceStatus",
            label: t("masters:province.list.table.headers.status"),
            options: {
                sort: true,
                filter: true,
                filterType: "dropdown",
                customBodyRender: (value, tableMeta, updateValue) => getStatusDesc(value),
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
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <C1DataTableActions
                            editPath={getDeActiveMode(tableMeta.rowData[3]) ? "/master/province/edit/" + tableMeta.rowData[0]: null}
                            viewPath={"/master/province/view/" + tableMeta.rowData[0]}
                            deActiveEventHandler={getDeActiveMode(tableMeta.rowData[3]) ? () => handleDeActiveHandler(tableMeta.rowData[0]): null}
                            activeEventHandler={getActiveMode(tableMeta.rowData[3]) ? () => handleActiveHandler(tableMeta.rowData[0]): null}
                        />
                    );
                },
            },
        },
    ];

    const handleDeActiveHandler = (id) => {

        setLoading(true);
        setRefresh(false);
        sendRequest("/api/co/pedi/mst/entity/pediMstProvince/"+ id, "deActive", "delete", {});

        /*setOpenPopupAction(true);
        setAction("delete")
        setId(id);*/
    }

    const handleActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest("/api/co/pedi/mst/entity/pediMstProvince/" + id, "getForActive", "get", {})

        /*setOpenPopupAction(true);
        setAction("active")
        setId(id);*/
    }

    const handleConfirmAction = () => {
        setLoading(true);
        setRefresh(false);

        if(action === "delete") {
            sendRequest("/api/co/pedi/mst/entity/pediMstProvince/"+ id, "deActive", "delete", {});
        }else if(action === "active") {
            sendRequest("/api/co/pedi/mst/entity/pediMstProvince/" + id, "getForActive", "get", {})
        }
        setOpenPopupAction(false);
    }

    return (
        <div>
            <C1ListPanel routeSegments={[{ name: t("masters:province.list.routeSegment") }]}>
                <C1DataTable
                    url={MST_PROVINCE_URL}
                    columns={columns}
                    title={t("masters:province.list.table.title")}
                    defaultOrder="provinceDescription"
                    isServer={true}
                    isRefresh={isRefresh}
                    showAdd={{
                        path: "/master/province/new",
                    }}
                />
            </C1ListPanel>

            <ConfirmationDialog open={openPopupAction}
                                onConfirmDialogClose={() => setOpenPopupAction(false)}
                                text={t("common:confirmMsgs.confirm.content")}
                                title={t("common:confirmMsgs.confirm.title")}
                                onYesClick={(e) => handleConfirmAction(e)} />
        </div>
    );
};

export default ProvinceList;
