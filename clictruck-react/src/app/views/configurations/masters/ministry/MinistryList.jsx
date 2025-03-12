import React from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";

import C1DataTable from 'app/c1component/C1DataTable';
import C1ListPanel from "app/c1component/C1ListPanel";
import C1DataTableActions from 'app/c1component/C1DataTableActions';

const MinistryList = () => {

    const { t } = useTranslation(['masters']);
    const columns = [
        {
            name: "minCode", // field name in the row object
            label: t("ministry.list.table.headers.minCode"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <div className="flex items-center">
                            <div className="ml-3">
                                <Link to={"/master/ministry/view/" + tableMeta.rowData[0]}>
                                    <h5 className="my-0 text-15">{value}</h5>
                                </Link>
                            </div>
                        </div>
                    );
                },
            },
        },
        {
            name: "minDesc",
            label: t("ministry.list.table.headers.minDesc"),
            options: {
                filter: true,
            },
        },
        {
            name: "minDescOth",
            label: t("ministry.list.table.headers.minDescOth"),
            options: {
                filter: true,
            },
        },
        {
            name: "minRegNo",
            label: t("ministry.list.table.headers.minRegNo"),
            options: {
                filter: true,
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
                        editPath={"/master/ministry/edit/" + tableMeta.rowData[0]}
                        viewPath={"/master/ministry/view/" + tableMeta.rowData[0]} />
                },
            },
        },
    ];



    return (
        <C1ListPanel
            routeSegments={[
                { name: t("ministry.list.routeSegment") },
            ]}>
            <C1DataTable url="/api/co/ccm/entity/ministry"
                columns={columns}
                title={t("ministry.list.table.title")}
                defaultOrder="minCode"
                statusFieldName="minStatus"
                showAdd={{
                    path: "/master/ministry/new"
                }}
            />
        </C1ListPanel>
    );
};

export default MinistryList;
