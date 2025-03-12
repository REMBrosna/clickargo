import React,{useState, useEffect} from "react";

import useHttp from "app/c1hooks/http";
import C1DataTable from 'app/c1component/C1DataTable';
import C1DataTableActions from 'app/c1component/C1DataTableActions';

import C1ListPanel from "app/c1component/C1ListPanel";
import { useTranslation } from "react-i18next";

import {getActiveMode, getDeActiveMode, getStatusDesc} from "app/c1utils/statusUtils";

const DocAssociateList = () => {

    const { t } = useTranslation(['masters']);
    const { isLoading, res, error, urlId, sendRequest } = useHttp();
    const [isRefresh, setRefresh] = useState(false);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (!isLoading && !error && res) {
            if (urlId==='getForActive'){
                sendRequest("/api/co/pedi/mst/entity/pediMstSuppDocAssociation/"+ res.data.suppDocAssId +"/activate", "active", "put", res.data);
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
            name: "suppDocAssId",
            label: t("docAssociate.list.table.headers.id"), // column title that will be shown in table
            options: {
                display: true,
                sort: true,
                filter: false,
            }
        },
        {
            name: "mstAttType.mattName", // field name in the row object
            label: t("docAssociate.list.table.headers.supportName"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
            },
        },
        {
            name: "pediMstAppType.appTypeName",
            label: t("docAssociate.list.table.headers.appName"),
            options: {
                filter: true,
            },
        },
        {
            name: "suppDocShipType",
            label: t("docAssociate.list.table.headers.shipTypeName"),
            options: {
                filter: true,
                sort: true,
            },
        },{
            name: "suppDocParentPort",
            label: t("docAssociate.list.table.headers.parentPortName"),
            options: {
                filter: true,
                sort: true,
            },
        },
        {
            name: "suppDocAssMandatory",
            label: t("docAssociate.list.table.headers.mandate"),
            options: {
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: ["Y", "N"],
                    renderValue: v => {
                        switch (v) {
                            case 'Y': return "Yes";
                            case 'N': return "No";
                            default: break;
                        }

                    }
                },
                sort: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return getStatusDesc(value);
                }
            },
        },
        {
            name: "suppDocAssStatus",
            label: t("docAssociate.list.table.headers.status"),
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
                        editPath={getDeActiveMode(tableMeta.rowData[6]) ? "/master/docAssociate/edit/" + tableMeta.rowData[0]: null}
                        viewPath={"/master/docAssociate/view/" + tableMeta.rowData[0]}
                        deActiveEventHandler={getDeActiveMode(tableMeta.rowData[6]) ? () => handleDeActiveHandler(tableMeta.rowData[0]): null}
                        activeEventHandler={getActiveMode(tableMeta.rowData[6]) ? () => handleActiveHandler(tableMeta.rowData[0]): null}
                    />
                }
            },
        },
    ];

    const handleDeActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest("/api/co/pedi/mst/entity/pediMstSuppDocAssociation/"+ id, "deActive", "delete", {});
    }

    const handleActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest("/api/co/pedi/mst/entity/pediMstSuppDocAssociation/" + id, "getForActive", "get", {})
    }

    return (
        <div>
            <C1ListPanel
                routeSegments={[
                    { name: t("docAssociate.list.routeSegment") },
                ]}
            >
            <C1DataTable url="/api/co/pedi/mst/entity/pediMstSuppDocAssociation"
                showAdd={{
                    path: "/master/docAssociate/new"
                }}
                columns={columns}
                title={t("docAssociate.list.table.title")}
                defaultOrder="suppDocAssDtLupd"
                isServer={true}
                defaultOrderDirection="desc"
                isRefresh={isRefresh}
            />
            </C1ListPanel>
        </div>
    );
}

export default DocAssociateList;