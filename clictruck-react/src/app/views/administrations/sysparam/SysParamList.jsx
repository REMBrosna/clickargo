import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import C1DataTableActions from "app/c1component/C1DataTableActions";
import C1ListPanel from "app/c1component/C1ListPanel";
import { getStatusDesc } from "app/c1utils/statusUtils";
import DataTable from "app/atomics/organisms/DataTable";
import CachedOutlinedIcon from '@material-ui/icons/CachedOutlined';
import useHttp from "app/c1hooks/http";
import CircularProgress from "@material-ui/core/CircularProgress";
import { Backdrop } from "@material-ui/core";
import C1Warning from "app/c1component/C1Warning";

const SysParamList = () => {
    const { t } = useTranslation(["administration"]);

    const [loading, setLoading] = useState(false);

    const { isLoading, res, error, urlId, sendRequest } = useHttp();
    const [warningMessage, setWarningMessage] = useState({ open: false, msg: "" });

    useEffect(() => {
        if (!isLoading && !error && res) {
            setLoading(isLoading);

            switch (urlId) {
                case "refreshSysParam":
                    setWarningMessage({...warningMessage, open: true, msg: "Successfully refreshed sysparam cache!"});
                    break;
                default:
                    break;
            }

           
        }

        // eslint-disable-next-line
    }, [urlId, isLoading, res, error]);

    const columns = [
        {
            name: "sysKey",
            label: t("administration:sysParam.list.table.headers.sysKey"),
            options: {
                sort: true,
                filter: true,
            },
        },
        {
            name: "sysVal",
            label: t("administration:sysParam.list.table.headers.sysVal"),
            options: {
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return value && value.length > 50 ? value?.substring(0, 50) + "..." : value;
                    
                },
            },
        },
        {
            name: "sysDesc",
            label: t("administration:sysParam.list.table.headers.sysDesc"),
            options: {
                filter: true,
            },
        },
        {
            name: "sysStatus",
            label: t("administration:sysParam.list.table.headers.sysStatus"),
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
                            editPath={`/administrations/sysparam/edit/${tableMeta.rowData[0]}`}
                            viewPath={`/administrations/sysparam/view/${tableMeta.rowData[0]}`}
                        />
                    );
                },
            },
        },
    ];

    const handleRefresh = () => {
        setLoading(true);
        sendRequest(`admin/cache/refreshSysParam`, "refreshSysParam", "get");
    }

    return (
        <div>
             <C1Warning warningTitle="Success" warningMessage={warningMessage} handleWarningAction={()=> setWarningMessage({ open: false, msg: "", hlMsg: "", subMsg: "" })} />
             <Backdrop open={loading} style={{ zIndex: 999999, color: '#fff',}}> <CircularProgress color="inherit" /></Backdrop>
            <C1ListPanel routeSegments={[{ name: t("administration:sysParam.list.routeSegment") }]}
                guideId="clicdo.doi.co.jobs.list"
                title={t("administration:sysParam.list.table.title")}>
                <DataTable 
                    url="/api/v1/clickargo/sysadmin/sysParam"
                    columns={columns}
                    defaultOrder="sysKey"
                    isServer={true}
                    filterBy={[{ attribute: "sysKey", value: "CLICTRUCK" }]}
                    showAddButton={[{
                        label: t("administration:sysParam.list.table.refreshBtn").toUpperCase(),
                        action: handleRefresh,
                        icon: <CachedOutlinedIcon />
                    }]}
                />
              
            </C1ListPanel>
        </div>
    );
};

export default SysParamList;
