import React from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";

import C1DataTable from 'app/c1component/C1DataTable';
import C1ListPanel from "app/c1component/C1ListPanel";
import C1DataTableActions from 'app/c1component/C1DataTableActions';

const AgencyList = () => {

    const { t } = useTranslation(['masters']);

    const columns = [
        {
            name: "agyCode", // field name in the row object
            label: t("agency.list.table.headers.agyCode"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <div className="flex items-center">
                            <div className="ml-3">
                                <Link to={"/master/agency/view/" + tableMeta.rowData[0]}>
                                    <h5 className="my-0 text-15">{value}</h5>
                                </Link>
                            </div>
                        </div>
                    );
                },
            },
        },
        {
            name: "agyDesc",
            label: t("agency.list.table.headers.agyDesc"),
            options: {
                filter: true,
            },
        },
        {
            name: "agyDescOth",
            label: t("agency.list.table.headers.agyDescOth"),
            options: {
                filter: true,
            },
        },
        {
            name: "agyRegNo",
            label: t("agency.list.table.headers.agyRegNo"),
            options: {
                filter: true,
            },
        },
        {
            name: "TCoreMinistry.minCode",
            label: t("agency.list.table.headers.TCoreMinistry"),
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
                        editPath={"/master/agency/edit/" + tableMeta.rowData[0]}
                        viewPath={"/master/agency/view/" + tableMeta.rowData[0]} />
                },
            },
        },
    ];



    return (
        <C1ListPanel
            routeSegments={[
                { name: t("agency.list.routeSegment") },
            ]}>
            <C1DataTable url="/api/co/ccm/entity/agency"
                columns={columns}
                title={t("agency.list.table.title")}
                defaultOrder="agyCode"
                isServer={true}
                showAdd={{
                    path: "/master/agency/new"
                }}
            />
        </C1ListPanel>
    );
};

export default AgencyList;
