import React from "react";

import C1DataTable from 'app/c1component/C1DataTable';
import C1DataTableActions from 'app/c1component/C1DataTableActions';

import C1ListPanel from "app/c1component/C1ListPanel";
import { useTranslation } from "react-i18next";
import { customFilterDateDisplay, formatDate } from "app/c1utils/utility";

const AuditList = () => {

    const { t } = useTranslation(['administration']);

    const columns = [
        {
            name: "audtId",
            label: t("audit.list.table.headers.audtId"),
            options: {
                sort: true,
                filter: true
            },
        },
        {
            name: "audtReckey",
            label: t("audit.list.table.headers.audtReckey"),
            options: {
                sort: true,
                filter: true
            },
        },
        {
            name: "audtEvent",
            label: t("audit.list.table.headers.audtEvent"),
            options: {
                filter: true,
            },
        },
        {
            name: "audtUid",
            label: t("audit.list.table.headers.audtUid"),
            options: {
                filter: true,
            },
        },
        {
            name: "audtUname",
            label: t("audit.list.table.headers.audtUname"),
            options: {
                filter: true,
            },
        },
        {
            name: "audtTimestamp",
            label: t("audit.details.tabs.recordDetails.audtCreatedDate"),
            options: {
                filter: true,
                filterType: 'custom',
                customFilterListOptions: {
                    render: v => v.map(l => l),
                    update: (filterList, filterPos, index) => {
                        filterList[index].splice(filterPos, 1);
                        return filterList;
                    }
                },
                filterOptions: {
                    display: customFilterDateDisplay
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, false);
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
                        viewPath={"/administrations/audit/view/" + tableMeta.rowData[0]}
                    />
                }
            },
        },
    ];

    return (
        <C1ListPanel
            routeSegments={[
                { name: t("audit.list.routeSegment") },
            ]}
        >
            <C1DataTable url="/api/co/common/entity/auditLog"
                columns={columns}
                title={t("audit.list.table.title")}
                defaultOrder="audtTimestamp"
                defaultOrderDirection="desc"
            />
        </C1ListPanel>
    );
}

export default AuditList;