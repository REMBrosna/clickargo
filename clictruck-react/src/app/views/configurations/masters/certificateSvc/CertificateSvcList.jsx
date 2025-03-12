import C1DataTable from 'app/c1component/C1DataTable';
import C1DataTableActions from 'app/c1component/C1DataTableActions';
import C1ListPanel from "app/c1component/C1ListPanel";
import useHttp from "app/c1hooks/http";
import { getStatusDesc } from "app/c1utils/statusUtils";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";


const CertificateSvcList = ({ roleId, filterStatus }) => {

    const { t } = useTranslation(['masters']);

    const { isLoading, res, error, urlId, sendRequest } = useHttp();
    const [isRefresh, setRefresh] = useState(false);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (!isLoading && !error && res) {
            setRefresh(true);
            setLoading(false);
        }
    }, [isLoading, res, error, urlId]);

    const columns = ({ roleId, filterStatus }) => {
        return [
            {
                name: "certsId",
                label: t("certificateSvc.list.table.headers.certsId"), // column title that will be shown in table
                options: {
                    sort: true,
                    filter: true,
                }
            },
            {
                name: "certsVal",
                label: t("certificateSvc.list.table.headers.certsVal"), // column title that will be shown in table
                options: {
                    sort: true,
                    filter: true,
                }
            },
            {
                name: "certsModuleService", // field name in the row object
                label: t("certificateSvc.list.table.headers.certsModuleService"), // column title that will be shown in table
                options: {
                    sort: true,
                    filter: true,
                },
            },
            {
                name: "certsDecs",
                label: t("certificateSvc.list.table.headers.certsDecs"),
                options: {
                    filter: true,
                },
            },
            {
                name: "certsStatus",
                label: t("certificateSvc.list.table.headers.certsStatus"),
                options: {
                    filter: true,
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
                    customBodyRender: (value, tableMeta, updateValue) => {
                        return <C1DataTableActions
                            viewPath={"/master/certificateSvc/view/" + tableMeta.rowData[0]}
                        />
                    }
                },
            },
        ];
    }

    return (
        <div>
            <C1ListPanel
                routeSegments={[
                    { name: t("certificateSvc.list.routeSegment") },
                ]}
            >
                <C1DataTable url="/api/co/pedi/mst/entity/pediMstCertificateSvc/"
                    columns={columns({ roleId, filterStatus })}
                    title={t("certificateSvc.list.table.title")}
                    defaultOrder="certsDtUpdate"
                    isServer={true}
                    isRefresh={isRefresh}
                    defaultOrderDirection="desc"
                />
            </C1ListPanel>
        </div>
    );
}

export default CertificateSvcList;