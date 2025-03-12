import React, { useState, useEffect } from "react";
import C1DataTable from 'app/c1component/C1DataTable';
import C1ListPanel from "app/c1component/C1ListPanel";
import { Button, Tooltip } from "@material-ui/core";
import AddBoxIcon from '@material-ui/icons/AddBox';
import { Link } from "react-router-dom";
import {
    Grid,
    TextField,
    MenuItem,
    Select,
    Paper,
    Snackbar,
    Tabs,
    Tab,
    Divider,
    Card,

} from "@material-ui/core";

import { paymentAdviceGenDB } from "../../../../../fake-db/db/paymentAdviceGen";

import C1DataTableActions from 'app/c1component/C1DataTableActions';

const AdviceGenerationList = () => {

    const columns = [
        {
            name: "adviceRefNoNo",
            label: "Payment Advice Ref. No",
            options: {
                filter: true,
            },
        },
        
        
        {
            name: "docType",
            label: "Document Type",
            options: {
                filter: true,
            },
        },

        {
            name: "paymentAmount",
            label: "Payment Amount",
            options: {
                filter: true,
            },
        },
        {
            name: "paymentDate",
            label: "Payment Date",
            options: {
                filter: true,
            },
        },
        
        {
            name: "status",
            label: "Status",
            options: {
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    if (value === 'adviceGenerated')
                    return <small className="px-1 py-2px bg-light-red text-red border-radius-4">
                     Advice Generated
                        </small>;
                    else if (value === 'paid')
                        return <small className="px-1 py-2px bg-light-green text-green border-radius-4">
                            Paid
                        </small>;
                    else if (value === 'paymentPending')
                         return <small className="px-1 py-2px bg-light-red text-red border-radius-4">
                            Payment Pending
                        </small>;
                      
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
                        proceed: {
                            show: tableMeta.rowData[4] === 'adviceGenerated'  ? true : false,
                            path: "/payment/recieptUpload/edit/" + tableMeta.rowData[0]
                        },
                        edit: {
                            show: false,
                            path: "/payment/recieptUpload/edit/" + tableMeta.rowData[0]
                        },
                        view: {
                            show: tableMeta.rowData[4] === 'paid' || tableMeta.rowData[4] === 'paymentPending' ? true : false,
                            path: "/payment/recieptUpload/view/" + tableMeta.rowData[0]
                        }
                    }} />

                },
            },
        },
    ];


    return (
        <div>

            <C1ListPanel
                routeSegments={[
                    { name: "Reciept Upload list" },
                ]}>


<Card elevation={3}>
                  

                    <Grid container spacing={3} alignItems="center">
                        <Grid container item direction="column">

                            <Grid item xs={12}>
                                <C1DataTable url="/api/process/all"
                                    columns={columns}
                                    title="Receipt Upload"
                                    defaultOrder="adviceRefNoNo"
                                    dbName={paymentAdviceGenDB}
                                    showAdd={false}
                                />
                            </Grid>
                        </Grid>
                    </Grid>
                </Card>

            </C1ListPanel>
        </div>


    );
};

export default AdviceGenerationList;
