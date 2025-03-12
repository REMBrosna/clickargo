import React, { useEffect, useState } from "react";
import { Grid, IconButton, Tooltip, Snackbar, Dialog, Button } from "@material-ui/core";
import useHttp from "app/c1hooks/http";
import { formatDate } from "app/c1utils/utility";
import { useTranslation } from "react-i18next";
import DataTable from "app/atomics/organisms/DataTable";
import C1ListPanel from "app/c1component/C1ListPanel";

const CreditLimitAudit = ({viewType}) => {

    const { t } = useTranslation(["buttons", "listing", "common", "cargoowners"]);
    
    const [loading, setLoading] = useState(false)
    const [isRefresh, setRefresh] = useState(false);

    const columns = [
        {
            name: "audtEvent",
            label: t("listing:audits.event")
        },
        {
            name: "audtTimestamp",
            label: t("listing:audits.dateTime"),
            options: {
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                }
            }
        },
        {
            name: "audtRemarks",
            label: t("listing:audits.remarks")
        },
        {
            name: "audtUid",
            label: t("listing:audits.userId")
        },
        {
            name: "audtUname",
            label: t("listing:audits.userName")
        },
    ]
  
    return (
        <React.Fragment>
            <DataTable
                url="/api/v1/clickargo/clictruck/creditlimit/audit"
                columns={columns}
                title="Audit Credit Limit Update"
                defaultOrder="audtTimestamp"
                defaultOrderDirection="desc"
                isServer={true}
                isShowViewColumns={false}
                isShowDownload={false}
                isShowPrint={false}
                isShowFilter={false}
                isRefresh={isRefresh}
                guideId={""}
                customRowsPerPage={[10, 20]}
            />
        </React.Fragment>
    )
}

export default CreditLimitAudit