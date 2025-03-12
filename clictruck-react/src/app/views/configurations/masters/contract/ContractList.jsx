import React, { useState, useEffect } from "react";

import useHttp from "app/c1hooks/http";
import C1DataTable from 'app/c1component/C1DataTable';
import C1DataTableActions from 'app/c1component/C1DataTableActions';

import C1ListPanel from "app/c1component/C1ListPanel";
import { useTranslation } from "react-i18next";
import { formatDate } from "app/c1utils/utility";

import { getStatusDesc } from "app/c1utils/statusUtils";

import ConfirmationDialog from "matx/components/ConfirmationDialog";
import { RecordStatus, Status } from "app/c1utils/const";
import useAuth from "app/hooks/useAuth";
import * as C1Utils from "app/c1utils/utility";

const ContractList = () => {

    const { t } = useTranslation(['masters']);

    const { isLoading, res, error, urlId, sendRequest } = useHttp();
    const [isRefresh, setRefresh] = useState(false);
    const [loading, setLoading] = useState(false);

    const [confirm, setConfirm] = useState({ radio: null });
    const [open, setOpen] = useState(false);
    const { user } = useAuth();

    const accountType = user?.coreAccn?.TMstAccnType?.atypId;
    const isShippingAgent = accountType === "ACC_TYPE_SHIP_AGENT";
    const isAdmin = accountType === "ACC_TYPE_MPWT";

    useEffect(() => {
        if (!isLoading && !error && res) {
            setOpen(false);
            setRefresh(true);
            setLoading(false);
        }
    }, [isLoading, res, error, urlId]);

    const handleDeleteConfirm = (e, radio, vat) => {
        setLoading(false);
        setConfirm({ ...confirm, radio: radio, vat: vat });
        setOpen(true);
    }


    const columns = [
        {
            name: "radio",
            label: t("contract.list.table.headers.radio"), // column title that will be shown in table
            options: {
                filter: false,
                display: false,
            },
        },
        {
            name: "aconShlName", // field name in the row object
            label: t("contract.list.table.headers.companyName"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
            },
        },
        {
            name: "aconSagName",
            label: t("contract.list.table.headers.aconSagName"),
            options: {
                filter: true,
            },
        },
        {
            name: "aconDtCreate",
            label: t("contract.list.table.headers.aconDtCreate"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                }
            },
        },
        {
            name: "accnExpiryDate",
            label: t("contract.list.table.headers.accnExpiryDate"),
            options: {
                filter: true,
                customBodyRender: (value) => C1Utils.formatDate(value, true),
                filterType: "custom",
                filterOptions: {
                    display: C1Utils.customFilterDateDisplay
                },
            },
        },
        {
            name: "aconStatus",
            label: t("contract.list.table.headers.status"),
            options: {
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: [Status.DRF.code, Status.SUB.code, Status.APP.code],
                    renderValue: v => {
                        switch (v) {
                            case Status.DRF.code: return Status.DRF.desc;
                            case Status.SUB.code: return Status.SUB.desc;
                            case Status.APP.code: return Status.APP.desc;
                            default: break;
                        }
                    }
                },
                customFilterListOptions: {
                    render: v => {
                        switch (v) {
                            case Status.DRF.code: return Status.DRF.desc;
                            case Status.SUB.code: return Status.SUB.desc;
                            case Status.APP.code: return Status.APP.desc;
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
            name: "aconRecStatus",
            label: t("contract.list.table.headers.recordStatus"),
            options: {
                filter: true,
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
                        editPath={(tableMeta.rowData[5] === Status.SUB.code || tableMeta.rowData[5] === Status.DRF.code ||
                            (tableMeta.rowData[5] === Status.APP.code && isAdmin))
                            && tableMeta.rowData[6] === "A"
                            && "/master/contract/edit/" + tableMeta.rowData[0]}
                        viewPath={"/master/contract/view/" + tableMeta.rowData[0]}
                    />
                }
            },
        },
    ];

    const handleDeleteHandler = (e) => {
        if (confirm && !confirm.radio)
            return;

        setLoading(true);
        setRefresh(false);
        setTimeout(() => sendRequest("/api/contracts/" + confirm.radio, "delete", "delete", {}), 1000);
    }

    return (
        <div>
            {confirm?.radio && (
                <ConfirmationDialog
                    open={open}
                    title={t("common:confirmMsgs.delete.title")}
                    text={t("common:confirmMsgs.delete.content", { appnId: confirm?.vat })}
                    onYesClick={() => handleDeleteHandler()}
                    onConfirmDialogClose={() => setOpen(false)}
                />
            )}

            <C1ListPanel
                routeSegments={[
                    { name: t("contract.list.routeSegment") },
                ]}
            >
                <C1DataTable url="/api/contracts"
                    showAdd={isShippingAgent ? {
                        path: "/master/contract/new"
                    } : null}
                    columns={columns}
                    title={t("contract.list.table.title")}
                    defaultOrder="aconDtLupd"
                    isServer={true}
                    isRefresh={isRefresh}
                    isShowDownload={false}
                    isShowPrint={false}
                />
            </C1ListPanel>
        </div>
    );
}

export default ContractList;