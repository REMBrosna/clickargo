import React from "react";

import C1DataTable from 'app/c1component/C1DataTable';
import C1DataTableActions from 'app/c1component/C1DataTableActions';

import C1ListPanel from "app/c1component/C1ListPanel";
import { useTranslation } from "react-i18next";
import { customFilterDateDisplay, formatDate } from "app/c1utils/utility";

const ExceptionList = () => {

    const { t } = useTranslation(['administration']);

    const columns = [
        {
            name: "id.expToken",
            label: t("exception.list.table.headers.expToken"),
            options: {
                sort: true,
                filter: true
            },
        },
        {
            name: "id.expCode",
            label: t("exception.list.table.headers.expCode"),
            options: {
                filter: true,
                sort: true,
            },
        },
        {
            name: "id.expMsg",
            label: t("exception.list.table.headers.expMsg"),
            options: {
                filter: true,
                sort: true,
            },
        },
        {
            name: "id.expAddinfo",
            label: t("exception.details.tabs.recordDetails.expAddinfo"),
            options: {
                filter: true,
                sort: true,
            },
        },
        {
            name: "id.expUid",
            label: t("exception.list.table.headers.expUid"),
            options: {
                filter: true,
                sort: true,
            },
        },
        {
            name: "id.expTimestamp",
            label: t("exception.details.tabs.recordDetails.expCreatedDate"),
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
                        viewPath={"/administrations/exception/view/" + tableMeta.rowData[0]}
                    />
                }
            },
        },
    ];

    return (
        <C1ListPanel
            routeSegments={[
                { name: t("exception.list.routeSegment") },
            ]}
        >
            <C1DataTable url="/api/co/common/entity/exceptions"
                         columns={columns}
                         title={t("exception.list.table.title")}
                         defaultOrder="id.expTimestamp"
                         defaultOrderDirection="desc"
            />
        </C1ListPanel>
    );
}

export default ExceptionList;