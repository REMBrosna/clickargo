import React from "react";
import C1DataTable from 'app/c1component/C1DataTable';
import C1ListPanel from "app/c1component/C1ListPanel";
import { crewDB } from "../../../../fake-db/db/crew";
import C1DataTableActions from 'app/c1component/C1DataTableActions';
import { formatDate } from "app/c1utils/utility";
// import useAuth from "app/hooks/useAuth";

const CountryList = () => {

    const columns = [
        {
            name: "id", // field name in the row object
            label: "ID", // column title that will be shown in table

        },
        {
            name: "name",
            label: "Name",
            options: {
                filter: true,
            },
        },
        {
            name: "passportNo",
            label: "Passport No",
            options: {
                filter: true,
            },
        },
        {
            name: "rank",
            label: "Rank",
            options: {
                filter: true,
            },
        },
        {
            name: "createdBy",
            label: "Created By",
            options: {
                filter: true,
            },
        },
        {
            name: "createdDate",
            label: "Created Date",
            options: {
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
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
                    return <C1DataTableActions params={{
                        edit: {
                            show: true,
                            path: "/configuration/crewList/edit/" + tableMeta.rowData[0]
                        },
                        view: {
                            show: true,
                            path: "/configuration/crewList/view/" + tableMeta.rowData[0]
                        }
                    }} />

                },
            },
        },
    ];



    return (
        <C1ListPanel
            routeSegments={[
                { name: "Crew List" },
            ]}>
            <C1DataTable url="/api/co/master/entity/country"
                columns={columns}
                title="Crew List"
                defaultOrder="imoNo"
                dbName={crewDB}
                showAdd={{
                    path: "/configuration/crewList/new/0"
                }}
            />
        </C1ListPanel>
    );
};

export default CountryList;
