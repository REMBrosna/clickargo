import C1DataTable from 'app/c1component/C1DataTable';
import C1DataTableActions from 'app/c1component/C1DataTableActions';
import C1ListPanel from "app/c1component/C1ListPanel";
import useHttp from "app/c1hooks/http";
import { getStatusDesc } from "app/c1utils/statusUtils";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";


const CertificateConfigList = () => {

    const { t } = useTranslation(['masters']);

    const { isLoading, res, error, urlId, sendRequest } = useHttp();
    const [isRefresh, setRefresh] = useState(false);
    const [loading, setLoading] = useState(false);

    //const [confirm, setConfirm] = useState({ id: null });
    const [open, setOpen] = useState(false);

    useEffect(() => {
        if (!isLoading && !error && res) {
            setOpen(false);
            setRefresh(true);
            setLoading(false);
        }
    }, [isLoading, res, error, urlId]);

    const columns = [
        {
            name: "certId",
            label: t("certificateConfig.list.table.headers.certId"), // column title that will be shown in table
            options: {
                sort: false,
                filter: false,
                display: false,
            }
        },
        {
            name: "pediCertificateService.certsId",
            label: t("certificateConfig.list.table.headers.certsId"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
            }
        },
        {
            name: "certTemplateType", // field name in the row object
            label: t("certificateConfig.list.table.headers.certTemplateType"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
            },
        },
        {
            name: "certSubReportKey",
            label: t("certificateConfig.list.table.headers.certSubReportKey"),
            options: {
                filter: true,
            },
        },
        {
            name: "certJrxmlPath",
            label: t("certificateConfig.list.table.headers.certJrxmlPath"),
            options: {
                filter: true,
                sort: true,
            },
        },
        {
            name: "certReportTitle",
            label: t("certificateConfig.list.table.headers.certReportTitle"),
            options: {
                filter: true,
                sort: true,
            },
        },
        {
            name: "certReportTitleKh",
            label: t("certificateConfig.list.table.headers.certReportTitleKh"),
            options: {
                filter: true,
                sort: true,
            },
        },
        {
            name: "certStatus",
            label: t("certificateConfig.list.table.headers.certStatus"),
            options: {
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: ["Active", "Inactive"],
                    renderValue: v => {
                        switch (v) {
                            case "Active": return "Active";
                            case "Inactive": return "Inactive";
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
            name: "action",
            label: " ",
            options: {
                filter: false,
                display: true,
                viewColumns: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return <C1DataTableActions
                        editPath={"/master/certificateConfig/edit/" + tableMeta.rowData[0]}
                        viewPath={"/master/certificateConfig/view/" + tableMeta.rowData[0]}
                    />
                }
            },
        },
    ];

    // const handleDeleteHandler = (e) => {
    //     if (confirm && !confirm.id)
    //         return;

    //     setLoading(true);
    //     setRefresh(false);
    //     setTimeout(() => sendRequest("/api/co/master/entity/attType/" + confirm.id, "delete", "delete", {}), 1000);
    // }

    return (
        <div>
            {/* {confirm?.id && (
                <ConfirmationDialog
                    open={open}
                    title={t("common:confirmMsgs.delete.title")}
                    text={t("common:confirmMsgs.delete.content", { appnId: confirm?.id })}
                    onYesClick={() => handleDeleteHandler()}
                    onConfirmDialogClose={() => setOpen(false)}
                />
            )} */}

            <C1ListPanel
                routeSegments={[
                    { name: t("certificateConfig.list.routeSegment") },
                ]}
            >
                <C1DataTable url="/api/co/pedi/mst/entity/pediMstCertificateConfig/"
                    columns={columns}
                    title={t("certificateConfig.list.table.title")}
                    defaultOrder="certDtUpdate"
                    isServer={true}
                    isRefresh={isRefresh}
                    defaultOrderDirection="desc"
                />
            </C1ListPanel>
        </div>
    );
}

export default CertificateConfigList;