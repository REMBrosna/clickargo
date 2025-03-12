import React from "react";
import C1DataTable from 'app/c1component/C1DataTable';
import C1ListPanel from "app/c1component/C1ListPanel";
import C1DataTableActions from 'app/c1component/C1DataTableActions';
import { agentAssDB } from "../../../../../fake-db/db/agentAss";

const agentAssList = () => {

    const columns = [
        
        {
            name: "agentName",
            label: "Agent Name",
            options: {
                filter: true,
            },
        },

        {
            name: "agentTIN",
            label: "Agent TIN",
            options: {
                filter: true,
            },
        },

        {
            name: "assType",
            label: "Association Type",
            options: {
                filter: true,
            },
        },

        {
            name: "portAss",
            label: "Port",
            options: {
                filter: true,
            },
        },

        {
            name: "ctyUidCreate",
            label: "Created By",
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
                    return <C1DataTableActions params={{
                        edit: {
                            show: true,
                            path: "/association/agentAss/edit/" + tableMeta.rowData[1]
                        },
                        view: {
                            show: true,
                            path: "/association/agentAss/view/" + tableMeta.rowData[1]
                        },
                        delete: {
                            show: true,
                            path: "/association/agentAss/delete/" + tableMeta.rowData[1]
                        }
                    }} />

                },
            },
        },
    ];


    return (
        <C1ListPanel
            routeSegments={[
                { name: "Aggent Association List" },
            ]}>
            <C1DataTable url="/api/co/master/entity/country"
                columns={columns}
                title="Aggent Association List"
                defaultOrder="agentName"
                dbName={agentAssDB}
                showAdd={{
                    path: "/association/agentAss/new/0"
                }}
            />
        </C1ListPanel>
    );
};

export default agentAssList;