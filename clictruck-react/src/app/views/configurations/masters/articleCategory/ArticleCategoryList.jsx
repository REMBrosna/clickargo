import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import useHttp from "app/c1hooks/http";
import C1DataTable from "app/c1component/C1DataTable";
import C1ListPanel from "app/c1component/C1ListPanel";
import C1DataTableActions from "app/c1component/C1DataTableActions";
import {getActiveMode, getDeActiveMode, getStatusDesc} from "app/c1utils/statusUtils";
import { PEDI_MST_ARTICLE_CAT_URL } from "app/c1utils/const";
import ConfirmationDialog from "../../../../../matx/components/ConfirmationDialog";

const ArticleCategoryList = () => {
    const { t } = useTranslation(["masters", "common"]);
    const { isLoading, res, error, urlId, sendRequest } = useHttp();
    const [isRefresh, setRefresh] = useState(false);
    const [loading, setLoading] = useState(false);
    const [openPopupAction, setOpenPopupAction] = useState(false);
    const[action] = useState("");
    const [id] = useState("");

    useEffect(() => {
        if (!isLoading && !error && res) {
            if (urlId==='getForActive'){
                sendRequest("/api/co/pedi/mst/entity/pediMstArticleCat/"+ res.data.arcCode +"/activate", "active", "put", res.data);
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
            name: "arcCode",
            label: t("masters:articleCat.list.table.headers.arcCode"),
            options: {
                sort: true,
                filter: true
            },
        },
        {
            name: "arcName",
            label: t("masters:articleCat.list.table.headers.arcName"),
            options: {
                filter: true,
            },
        },
        {
            name: "arcDesc",
            label: t("masters:articleCat.list.table.headers.arcDesc"),
            options: {
                filter: true,
            },
        },
        {
            name: "arcStatus",
            label: t("masters:articleCat.list.table.headers.status"),
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
                            editPath={getDeActiveMode(tableMeta.rowData[3]) ? "/master/articleCategory/edit/" + tableMeta.rowData[0]: null}
                            viewPath={"/master/articleCategory/view/" + tableMeta.rowData[0]}
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
        sendRequest("/api/co/pedi/mst/entity/pediMstArticleCat/"+ id, "deActive", "delete", {});
    }

    const handleActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest("/api/co/pedi/mst/entity/pediMstArticleCat/" + id, "getForActive", "get", {})
    }

    const handleConfirmAction = () => {
        setLoading(true);
        setRefresh(false);

        if(action === "delete") {
            sendRequest("/api/co/pedi/mst/entity/pediMstArticleCat/"+ id, "deActive", "delete", {});
        }else if(action === "active") {
            sendRequest("/api/co/pedi/mst/entity/pediMstArticleCat/" + id, "getForActive", "get", {})
        }
        setOpenPopupAction(false);
    }

    return (
        <div>
            <C1ListPanel routeSegments={[{ name: t("masters:articleCat.list.routeSegment") }]}>
                <C1DataTable
                    url={PEDI_MST_ARTICLE_CAT_URL}
                    columns={columns}
                    title={t("masters:articleCat.list.table.title")}
                    defaultOrder="arcCode"
                    isServer={true}
                    isRefresh={isRefresh}
                    showAdd={{
                        path: "/master/articleCategory/new",
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

export default ArticleCategoryList;
