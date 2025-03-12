import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import useHttp from "app/c1hooks/http";
import C1DataTable from "app/c1component/C1DataTable";
import C1ListPanel from "app/c1component/C1ListPanel";
import C1DataTableActions from "app/c1component/C1DataTableActions";
import { getActiveMode, getDeActiveMode, getStatusDesc } from "app/c1utils/statusUtils";
import { MST_PORT_TERMINAL_URL, MST_PORT_BY_COUNTRY, RecordStatus } from "app/c1utils/const";

import C1SelectField from "app/c1component/C1SelectField";

const PortTerminalList = () => {
    const { t } = useTranslation(["masters", "common"]);
    const { isLoading, res, error, urlId, sendRequest } = useHttp();
    const [isRefresh, setRefresh] = useState(false);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (!isLoading && !error && res) {
            if (urlId === 'getForActive') {
                sendRequest("/api/co/pedi/mst/entity/pediMstPortTerminal/" + res.data.portTeminalId + "/activate", "active", "put", res.data);
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
            name: "portTeminalId",
            label: t("masters:portTerminal.list.table.headers.portTermId"),
            options: {
                sort: true,
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <div className="flex items-center">
                            <div className="ml-3">
                                <Link to={"/master/portTerminal/view/" + tableMeta.rowData[0]}>
                                    <h5 className="my-0 text-15"> {value} </h5>
                                </Link>
                            </div>
                        </div>
                    );
                },
            },
        },
        {
            name: "mstPort.portDescription",
            options: {
                filter: false,
                display: "excluded",
            },
        },
        {
            name: "mstPort.portCode",
            label: t("masters:portTerminal.list.table.headers.port"),
            options: {
                filter: true,
                filterType: 'custom',
                customBodyRender: (value, tableMeta, updateValue) => {
                    return `${tableMeta.rowData[1]}`;
                },
                filterOptions: {
                    display: (filterList, onChange, index, column) => {
                        return <C1SelectField
                            label={column.label}
                            name={column.name}
                            isServer
                            onChange={event => {
                                filterList[index][0] = event.target.value;
                                onChange(filterList[index], index, column);
                            }}
                            isShowCode={true}
                            value={filterList[index][0] || ""}
                            options={{
                                url: MST_PORT_BY_COUNTRY + 'KH',
                                id: 'portCode',
                                desc: 'portDescription',
                                isCache: false
                            }} />
                    }

                }
            },
        },
        {
            name: "portTeminalName",
            label: t("masters:portTerminal.list.table.headers.portTermName"),
            options: {
                filter: true,
            },
        },
        {
            name: "portTeminalDesc",
            label: t("masters:portTerminal.list.table.headers.portTermDesc"),
            options: {
                filter: true,
            },
        },
        {
            name: "portTeminalStatus",
            label: t("masters:portTerminal.list.table.headers.status"),
            options: {
                sort: true,
                filter: true,
                filterType: "dropdown",
                customBodyRender: (value, tableMeta, updateValue) => getStatusDesc(value),
                filterOptions: {
                    names: [RecordStatus.ACTIVE.code, RecordStatus.INACTIVE.code],
                    renderValue: (v) => {
                        if (v === RecordStatus.ACTIVE.code) {
                            return RecordStatus.ACTIVE.desc;
                        } else if (v === RecordStatus.INACTIVE.code) {
                            return RecordStatus.INACTIVE.desc;
                        }
                    },
                },
                customFilterListOptions: {
                    render: (v) => {
                        if (v === RecordStatus.ACTIVE.code) {
                            return RecordStatus.ACTIVE.desc;
                        } else if (v === RecordStatus.INACTIVE.code) {
                            return RecordStatus.INACTIVE.desc;
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
                            editPath={getDeActiveMode(tableMeta.rowData[5]) ? "/master/portTerminal/edit/" + tableMeta.rowData[0] : null}
                            viewPath={"/master/portTerminal/view/" + tableMeta.rowData[0]}
                            deActiveEventHandler={getDeActiveMode(tableMeta.rowData[5]) ? () => handleDeActiveHandler(tableMeta.rowData[0]) : null}
                            activeEventHandler={getActiveMode(tableMeta.rowData[5]) ? () => handleActiveHandler(tableMeta.rowData[0]) : null}
                        />
                    );
                },
            },
        },
    ];

    const handleDeActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest("/api/co/pedi/mst/entity/pediMstPortTerminal/" + id, "deActive", "delete", {});
    }

    const handleActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest("/api/co/pedi/mst/entity/pediMstPortTerminal/" + id, "getForActive", "get", {})
    }

    return (
        <div>
            <C1ListPanel routeSegments={[{ name: t("masters:portTerminal.list.routeSegment") }]}>
                <C1DataTable
                    url={MST_PORT_TERMINAL_URL}
                    columns={columns}
                    title={t("masters:portTerminal.list.table.title")}
                    defaultOrder="portTeminalDesc"
                    isServer={true}
                    isRefresh={isRefresh}
                    filterBy={[{ attribute: "portTerminalStatus", value: "A" }]}
                    showAdd={{
                        path: "/master/portTerminal/new",
                    }}
                />
            </C1ListPanel>
        </div>
    );
};

export default PortTerminalList;
