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

import { commonApplicationDB } from "../../../../../fake-db/db/commonApplication";

import C1DataTableActions from 'app/c1component/C1DataTableActions';

import { useTranslation } from "react-i18next";

const CommonApplicationList = () => {

    const columns = [
        {
            name: "appId", // field name in the row object
            label: "Application No", // column title that will be shown in table
            options: {
                sort: true,
                filter: true
            },
        },
        // {
        //     name: "version",
        //     label: "version",
        //     options: {
        //         filter: true,
        //     },
        // },
        {
            name: "port",
            label: "Port",
            options: {
                filter: true,
            },
        },
        {
            name: "voyageNo",
            label: "Voyage No",
            options: {
                filter: true,
            },
        },
        {
            name: "shipName",
            label: "Ship Name",
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
                    if (value === 'Approved')
                        return <small className="px-1 py-2px bg-light-green text-green border-radius-4">
                            Approved
                        </small>;
                    else if (value === 'Pending')
                        return <small className="px-1 py-2px bg-light-red text-red border-radius-4">
                            Pending
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
                        edit: {
                            show: false,
                            path: "/applications/commonApplication/edit/" + tableMeta.rowData[0]
                        },
                        view: {
                            show: true,
                            path: "/applications/commonApplication/view/" + tableMeta.rowData[0]
                        }
                    }} />

                },
            },
        },
    ];



    const [details, setDetails] = useState({
        shippingLine: 'shipLine1',
        ship: 'ship1',
        voyageNo: 'ARV1234',
        application: 'NIL',
    });

    const handleInputChange = (e) => {
        setDetails({ ...details, [e.target.name]: e.target.value });
    };

    const { t } = useTranslation(["buttons"]);

    return (
        <div>

            <C1ListPanel
                routeSegments={[
                    { name: "Common Application list" },
                ]}>

                <Card elevation={3}>
                    <Divider className="mb-2" />

                    <Grid container spacing={3} alignItems="center">
                        <Grid container item xs={3}>
                            {/* <Grid item direction="column" >

                                <TextField
                                    fullWidth
                                    variant="outlined"
                                    className="min-w-220"
                                    label="Select Shipping Line"
                                    name="shippingLine"
                                    size="small"
                                    variant="outlined"
                                    value={details.shippingLine}
                                    onChange={handleInputChange}
                                    select

                                >
                                    <MenuItem value="shipLine1" key="shipLine1">Shipping Line 1</MenuItem>
                                    <MenuItem value="shipLine2" key="shipLine2">Shipping Line 2</MenuItem>
                                </TextField>

                            </Grid> */}
                            <Grid item direction="column">
                                <TextField
                                    fullWidth
                                    variant="outlined"
                                    className="min-w-220"
                                    label="Clearance No"
                                    name="ship"
                                    size="small"
                                    variant="outlined"
                                    value={details.ship}
                                    onChange={handleInputChange}
                                    select

                                >
                                    <MenuItem value="ship1" key="ship1">PEDI202012123</MenuItem>
                                    <MenuItem value="ship2" key="ship2">PEDI202013123</MenuItem>
                                </TextField>
                            </Grid>
                        </Grid>
                        {/* <Grid container item xs={3}>
                             <Grid item direction="column">
                                <TextField
                                    fullWidth
                                    variant="outlined"
                                    className="min-w-220"
                                    label="Select ship"
                                    name="ship"
                                    size="small"
                                    variant="outlined"
                                    value={details.ship}
                                    onChange={handleInputChange}
                                    select

                                >
                                    <MenuItem value="ship1" key="ship1">Ship 1</MenuItem>
                                    <MenuItem value="ship2" key="ship2">Ship 2</MenuItem>
                                </TextField>
                            </Grid> 
                        </Grid> */}

                        <Grid container item xs={3}>
                            <Grid item direction="column">
                                <TextField
                                    fullWidth
                                    variant="outlined"
                                    className="min-w-220"
                                    label="Select Voyage"
                                    name="voyageNo"
                                    size="small"
                                    variant="outlined"
                                    value={details.voyageNo}
                                    onChange={handleInputChange}
                                    select

                                >
                                    <MenuItem value="ARV1234" key="ARV1234">ARV 1234</MenuItem>
                                    <MenuItem value="ARV4323" key="ARV4323">ARV 4323</MenuItem>
                                </TextField>
                            </Grid>
                        </Grid>


                        <Grid container item xs={3}>
                            <Grid item direction="column">
                                <TextField
                                    fullWidth
                                    variant="outlined"
                                    className="min-w-220"
                                    label="Select Application"
                                    name="application"
                                    size="small"
                                    variant="outlined"
                                    value={details.application}
                                    onChange={handleInputChange}
                                    select

                                >
                                    <MenuItem value="NIL" key="NIL">NIL List</MenuItem>
                                    <MenuItem value="Pilot" key="Pilot">Pilot Order</MenuItem>
                                </TextField>
                            </Grid>
                        </Grid>

                        <Grid container item xs={3}>
                            <Grid item direction="column">
                                <Link to={
                                    {
                                        pathname: "/applications/commonApplication/new/0",
                                        appName: details.application
                                    }

                                } >
                                    <Tooltip title={t("buttons:add")} aria-label="add">
                                        <Button type="submit" color="primary" variant="contained" ><AddBoxIcon viewBox="1 -1 30 30" color="white"></AddBoxIcon>{t("buttons:newApp")} </Button>
                                    </Tooltip></Link>
                            </Grid>
                        </Grid>



                    </Grid>

                    <Grid container spacing={3} alignItems="center">
                        <Grid container item direction="column">

                            <Grid item xs={12}>
                                <C1DataTable url="/api/process/all"
                                    columns={columns}
                                    title="Common Application"
                                    defaultOrder="appId"
                                    dbName={commonApplicationDB}
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

export default CommonApplicationList;
