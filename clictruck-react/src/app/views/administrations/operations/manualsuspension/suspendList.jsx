import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import C1DataTable from 'app/c1component/C1DataTable';
import C1DataTableActions from 'app/c1component/C1DataTableActions';
import C1ListPanel from "app/c1component/C1ListPanel";
import { RecordStatus, RegistrationStatus } from "app/c1utils/const";
import { getActiveMode, getDeActiveMode, getStatusDesc } from "app/c1utils/statusUtils";
import { formatDate } from "app/c1utils/utility";

import useHttp from "app/c1hooks/http";

const ManageAccountsListSuspended = () => {

    const { t } = useTranslation(["admin", "common"]);
    const [isRefresh, setRefresh] = useState(false);
    const { isLoading, res, error, urlId, sendRequest } = useHttp();
    const [snackBarOptions, setSnackBarOptions] = useState({
        open: false,
        vertical: 'top',
        horizontal: 'center',
        msg: '',
        severity: 'success'
    });

    useEffect(() => {
        if ( !error && res) {
            if (urlId === 'getForDeActive') {
                const bodyReq = { ...res.data, accnStatus:"I" };
                sendRequest("/api/co/ccm/entity/accn/" + res.data.accnId, "deActive", "put", bodyReq);
            } else if (urlId === 'getForActive') {
                const bodyReq = { ...res.data, accnStatus:"A" }; 
                sendRequest("/api/co/ccm/entity/accn/" + res.data.accnId, "deActive", "put", bodyReq);
            }
            if (urlId === 'active' || urlId === 'deActive') {
                setRefresh(true);
                setSnackBarOptions({
                    ...snackBarOptions,
                    open: true,
                    message: urlId === "active" ? t("admin:account.msg.activatedSuccess") : t("admin:account.msg.deactivatedSuccess"),
                });
            }
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isLoading, res, error, urlId]);

    const columns = [
        {
            name: "accnId", // field name in the row object
            label: t("account.list.accnId"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
            },
        },
        {
            name: "TMstAccnType.atypDescription",
            label: t("account.list.accnType"),
            options: {
                filter: true,
            },
        },
        {
            name: "accnName",
            label: t("account.list.accnName"),
            options: {
                filter: true,
            },
        },
        {
            name: "accnDtCreate",
            label: t("account.list.accnDtCreate"),
            options: {
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, false);
                }
            },
        },
        {
            name: "accnStatus",
            label: t("account.list.accnStatus"),
            options: {
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: [RecordStatus.ACTIVE.code, RegistrationStatus.EXPIRED.code, RegistrationStatus.PENDING_ACCCN_ACTIVATION.code, RecordStatus.SUSPENDED.code],
                    renderValue: v => {
                        switch (v) {
                            case RecordStatus.ACTIVE.code: return RecordStatus.ACTIVE.desc;
                            case RegistrationStatus.EXPIRED.code: return RegistrationStatus.EXPIRED.desc;
                            case RegistrationStatus.PENDING_ACCCN_ACTIVATION.code: return RegistrationStatus.PENDING_ACCCN_ACTIVATION.desc;
                            case RecordStatus.SUSPENDED.code: return RecordStatus.SUSPENDED.desc;
                            default: break;
                        }
                    }
                },
                customFilterListOptions: {
                    render: v => {
                        switch (v) {
                            case RecordStatus.ACTIVE.code: return RecordStatus.ACTIVE.desc;
                            case RegistrationStatus.PENDING_ACCCN_ACTIVATION.code: return RegistrationStatus.PENDING_ACCCN_ACTIVATION.desc;
                            case RecordStatus.SUSPENDED.code: return RecordStatus.SUSPENDED.desc;
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
                    return (
                    <C1DataTableActions
                        // editPath={`/manageAccount/edit/${tableMeta.rowData[0]}`}
                        deActiveEventHandler={getDeActiveMode(tableMeta.rowData[4]) ? () => handleDeActiveHandler(tableMeta.rowData[0]) : null}
                        activeEventHandler={getActiveMode(tableMeta.rowData[4]) ? () => handleActiveHandler(tableMeta.rowData[0]) : null}
                        viewPath={`/manageAccount/view/${tableMeta.rowData[0]}`} 
                    />
                        );

                },
            },
        },
    ];

    const handleDeActiveHandler = (id) => {
        setRefresh(false);
        sendRequest(`/api/co/ccm/entity/accn/${id}`, "getForDeActive", "get", {});
    }

    const handleActiveHandler = (id) => {
        setRefresh(false);
        sendRequest(`/api/co/ccm/entity/accn/${id}`, "getForActive", "get", {});
    }

    return (
        <C1ListPanel
            routeSegments={[
                { name: t("account.list.headerSuspended"), },
            ]}>
            <C1DataTable url="/api/co/ccm/entity/accn"
                columns={columns}
                isRefresh={isRefresh}
                title={t("account.list.headerSuspended")}
                defaultOrder="accnDtCreate"
                defaultOrderDirection="desc"
                showDownload={false}
                showPrint={false}
                filterBy={
                    [
                        { attribute: "accnStatus", value: "S" }
                    ]
                }
            />
        </C1ListPanel>
    );
};

export default ManageAccountsListSuspended;
