import React, { useState, useEffect } from "react";
import useHttp from "app/c1hooks/http";
import C1DataTable from 'app/c1component/C1DataTable';
import C1DataTableActions from 'app/c1component/C1DataTableActions';
import C1ListPanel from "app/c1component/C1ListPanel";
import { useTranslation } from "react-i18next";
import { RecordStatus } from "app/c1utils/const";
import { getActiveMode, getDeActiveMode, getStatusDesc } from "app/c1utils/statusUtils";

const AttachTypeList = () => {

    const { t } = useTranslation(['masters']);
    const { isLoading, res, error, urlId, sendRequest } = useHttp();
    const [isRefresh, setRefresh] = useState(false);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (!isLoading && !error && res) {
            if (urlId === 'getForActive') {
                sendRequest("/api/co/master/entity/attType/" + res.data.mattId + "/activate", "active", "put", res.data)
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
            name: "mattId",
            label: t("attachType.list.table.headers.mattId"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
            }
        },
        {
            name: "mattName", // field name in the row object
            label: t("attachType.list.table.headers.mattName"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
            },
        },
        {
            name: "mattDesc",
            label: t("attachType.list.table.headers.mattDesc"),
            options: {
                filter: true,
            },
        },
        {
            name: "mattDescOth",
            label: t("attachType.list.table.headers.mattDescOth"),
            options: {
                filter: true,
                sort: true,
            },
        },
        {
            name: "mattExpiry",
            label: t("attachType.list.table.headers.mattExpiry"),
            options: {
                filter: true,
                sort: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: ['Y', 'N'],
                    renderValue: v => {
                        switch (v) {
                            case 'Y': return "Yes";
                            case 'N': return "No";
                            default: break;
                        }
                    }
                },
                customFilterListOptions: {
                    render: v => {
                        switch (v) {
                            case 'Y': return "Yes";
                            case 'N': return "No";
                            default: break;
                        }
                    }
                },
            },
        },
        {
            name: "mattRefNo",
            label: t("attachType.list.table.headers.mattRefNo"),
            options: {
                filter: true,
                sort: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: ['Y', 'N'],
                    renderValue: v => {
                        switch (v) {
                            case 'Y': return "Yes";
                            case 'N': return "No";
                            default: break;
                        }
                    }
                },
                customFilterListOptions: {
                    render: v => {
                        switch (v) {
                            case 'Y': return "Yes";
                            case 'N': return "No";
                            default: break;
                        }
                    }
                },
            },
        },
        {
            name: "mattStatus",
            label: t("attachType.list.table.headers.mattStatus"),
            options: {
                filter: true,
                sort: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: [RecordStatus.ACTIVE.code, RecordStatus.INACTIVE.code],
                    renderValue: v => {
                        switch (v) {
                            case RecordStatus.ACTIVE.code: return RecordStatus.ACTIVE.desc;
                            case RecordStatus.INACTIVE.code: return RecordStatus.INACTIVE.desc;
                            default: break;
                        }
                    }
                },
                customFilterListOptions: {
                    render: v => {
                        switch (v) {
                            case RecordStatus.ACTIVE.code: return RecordStatus.ACTIVE.desc;
                            case RecordStatus.INACTIVE.code: return RecordStatus.INACTIVE.desc;
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
                        editPath={getDeActiveMode(tableMeta.rowData[6]) ? "/master/attachType/edit/" + tableMeta.rowData[0] : null}
                        viewPath={"/master/attachType/view/" + tableMeta.rowData[0]}
                        deActiveEventHandler={getDeActiveMode(tableMeta.rowData[6]) ? () => handleDeActiveHandler(tableMeta.rowData[0]) : null}
                        activeEventHandler={getActiveMode(tableMeta.rowData[6]) ? () => handleActiveHandler(tableMeta.rowData[0]) : null}
                    />
                }
            },
        },
    ];

    const handleDeActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest("/api/co/master/entity/attType/" + id, "deActive", "delete", {});
    }

    const handleActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest("/api/co/master/entity/attType/" + id, "getForActive", "get", {})
    }

    return (
        <div>
            <C1ListPanel
                routeSegments={[
                    { name: t("attachType.list.routeSegment") },
                ]}
            >
                <C1DataTable url="/api/co/master/entity/attType/"
                    showAdd={{
                        path: "/master/attachType/new"
                    }}
                    columns={columns}
                    title={t("attachType.list.table.title")}
                    defaultOrder="mattDtCreate"
                    isServer={true}
                    isRefresh={isRefresh}
                    defaultOrderDirection="desc"
                />
            </C1ListPanel>
        </div>
    );
}

export default AttachTypeList;