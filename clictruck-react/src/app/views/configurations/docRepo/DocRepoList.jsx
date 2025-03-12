import React from "react";
import C1DataTable from 'app/c1component/C1DataTable';
import C1ListPanel from "app/c1component/C1ListPanel";
import C1DataTableActions from 'app/c1component/C1DataTableActions';
import { docRepoDB } from "../../../../fake-db/db/docRepo";
import { formatDate } from "app/c1utils/utility";

const documentRepoList = () => {

    const columns = [
        {
            name: "accnID", // field name in the row object
            label: "Shipping Line", // column title that will be shown in table

        },

        {
            name: "vesselID",
            label: "Vessel ID",
            options: {
                filter: true,
            },
        },

        {
            name: "docType",
            label: "Doc Type",
            options: {
                filter: true,
            },
        },

        {
            name: "uploadedBy",
            label: "Uploaded By",
            options: {
                filter: true,
            },
        },

        {
            name: "uploadedDate",
            label: "Uploaded Date",
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
                            path: "/configuration/docRepo/edit/" + tableMeta.rowData[0]
                        },
                        view: {
                            show: true,
                            path: "/configuration/docRepo/view/" + tableMeta.rowData[0]
                        },
                        delete: {
                            show: false,
                            path: "/configuration/docRepo/delete/" + tableMeta.rowData[0]
                        }
                    }} />

                },
            },
        },
    ];

    return (
        <C1ListPanel
            routeSegments={[
                { name: "Document Repository List" },
            ]}>
            <C1DataTable url="/api/co/master/entity/country"
                columns={columns}
                title="Document Repository List"
                defaultOrder="accnID"
                dbName={docRepoDB}
                showAdd={{
                    path: "/configuration/docRepo/new/0"
                }}
            />
        </C1ListPanel>
    );
};

export default documentRepoList;