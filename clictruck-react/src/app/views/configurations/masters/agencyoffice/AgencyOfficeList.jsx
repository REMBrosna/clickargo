import React from "react";

import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";

import C1DataTable from 'app/c1component/C1DataTable';
import C1ListPanel from "app/c1component/C1ListPanel";
import C1DataTableActions from 'app/c1component/C1DataTableActions';


const AgencyOfficeList = () => {

    const { t } = useTranslation(['masters']);

    const columns = [
        {
            name: "id.agoCode", // field name in the row object
            label: t("agencyOffice.list.table.headers.agoCode"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <div className="flex items-center">
                            <div className="ml-3">
                                <Link to={"/master/agencyOffice/view/" + tableMeta.rowData[0] + ":" + tableMeta.rowData[1]}>
                                    <h5 className="my-0 text-15">{value}</h5>
                                </Link>
                            </div>
                        </div>
                    );
                },
            },
        },
        {
            name: "id.agoAgyCode",
            label: t("agencyOffice.list.table.headers.agoAgyCode"),
            options: {
                filter: true,
            },
        },
        {
            name: "agoDesc",
            label: t("agencyOffice.list.table.headers.agoDesc"),
            options: {
                filter: true,
            },
        },
        {
            name: "agoDescOth",
            label: t("agencyOffice.list.table.headers.agoDescOth"),
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
                        editPath={"/master/agencyOffice/edit/" + tableMeta.rowData[0] + ":" + tableMeta.rowData[1]}
                        viewPath={"/master/agencyOffice/view/" + tableMeta.rowData[0] + ":" + tableMeta.rowData[1]} />
                },
            },
        },
    ];



    return (
        <C1ListPanel
            routeSegments={[
                { name: t("agencyOffice.list.routeSegment") },
            ]}>
            <C1DataTable url="/api/co/ccm/entity/agencyOffice"
                columns={columns}
                title={t("agencyOffice.list.table.title")}
                defaultOrder="id.agoCode"
                statusFieldName="agoStatus"
                showAdd={{
                    path: "/master/agencyOffice/new"
                }}
            />
        </C1ListPanel>
    );
};

export default AgencyOfficeList;
