import { Grid, IconButton, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Tooltip } from "@material-ui/core";
import MenuItem from "@material-ui/core/MenuItem";
import { makeStyles, withStyles } from '@material-ui/core/styles';
import DeleteOutlinedIcon from '@material-ui/icons/DeleteOutlined';
import DeleteIcon from '@material-ui/icons/DeleteOutlineOutlined';
import GetAppOutlinedIcon from '@material-ui/icons/GetAppOutlined';
import PublishOutlinedIcon from '@material-ui/icons/PublishOutlined';
import React, { useEffect } from "react";

import C1DataTable from "app/c1component/C1DataTable";
import C1DataTableActions from "app/c1component/C1DataTableActions";
import C1FileUpload from "app/c1component/C1FileUpload";
import C1InputField from "app/c1component/C1InputField";
import C1SelectField from "app/c1component/C1SelectField";
import useHttp from "app/c1hooks/http";
import { ATTACH_TYPE } from "app/c1utils/const";
import { titleTab, useStyles } from "app/c1utils/styles";

const AddDoPopup = (props) => {

    const {
        inputData,
        data,
        state,
        doId,
        isRefresh,
        setDoId,
        truckJobId,
        viewType,
        errors,
        handleInputFileChange,
        handleInputFileChangeSigned,
        handleInputChange,
        handlePopupInputChange,
        downloadDOHandlerSigned,
        downloadDOHandlerUnsigned,
        deleteDOHandler,
        locale
    } = props

    /** ---------------- Declare states ------------------- */
    const { isLoading, res, urlId, sendRequest } = useHttp();

    const attTypes = [{ value: "OTH", desc: "OTHERS" }];

    /** --------------- Update states -------------------- */
    useEffect(() => {

    }, [viewType]);

    useEffect(() => {
        if (!isLoading && res) {
            switch (urlId) {
                default: break;
            }
        }
    }, [isLoading, res, urlId]);

    const cols = [
        // 0
        {
            name: "trId", // field name in the row object
            label: "Trip ID", // column title that will be shown in table
            options: {
                // display: true
                display: 'exluded'
            },
        },
        // 1
        {
            name: "tckJobTruck.jobId", // field name in the row object
            label: "Job ID", // column title that will be shown in table
            options: {
                // display: true
                display: 'exluded'
            },
        },
        // 2
        {
            name: "tckCtTripDo.doId", // field name in the row object
            label: "Doa ID", // column title that will be shown in table
            options: {
                // display: true
                display: 'exluded'
            },
        },
        // 3
        {
            name: "tckCtTripDo.doStatus", // field name in the row object
            label: "doStatus", // column title that will be shown in table
            options: {
                // display: true
                display: 'exluded'
            },
        },
        // 4
        {
            name: "tckCtTripDo.doUnsigned", // field name in the row object
            label: "Unsigned DO", // column title that will be shown in table
            options: {
                display: 'exluded'
                // display: true
            },
        },
        // 5
        {
            name: "tckCtTripDo.doSigned", // field name in the row object
            label: "Signed DO", // column title that will be shown in table
            options: {
                // display: true
                display: 'exluded'
            },
        },
        // 6
        {
            name: "tckCtTripLocationByTrFrom.tlocLocName", // field name in the row object
            label: "From", // column title that will be shown in table
            options: {
                sort: false,
            },
        },
        // 7
        {
            name: "tckCtTripLocationByTrTo.tlocLocName", // field name in the row object
            label: "To", // column title that will be shown in table
            options: {
                sort: false,
            },
        },
        // 8
        {
            name: "tckCtTripDo.doNo", // field name in the row object
            label: "Order No.", // column title that will be shown in table
            options: {
                customBodyRender: (value, tableMeta, updateValue) => {
                    if (state.jbstId === 'ONGOING') {
                        return value;
                    } else {
                        return <C1InputField
                            name={"doNo"}
                            required={true}
                            value={inputData?.doNo}
                            onChange={handlePopupInputChange} />
                    }
                },
                sort: false,
            },
        },
        // 9
        {
            name: "", // field name in the row object
            label: "DO", // column title that will be shown in table
            options: {
                sort: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    let tripId = tableMeta.rowData[0];
                    let doId = tableMeta.rowData[2];
                    let doStatus = tableMeta.rowData[3];
                    let doUnsigned = tableMeta.rowData[4];
                    setDoId(data?.doId === "" ? doId : data?.doId)
                    return <C1DataTableActions>
                        <Grid container alignItems="center" justifyContent="center">
                            {doUnsigned === null ?
                                <Grid item xs={12}>
                                    <C1FileUpload
                                        // inputLabel={locale("listing:attachments.docFile")}
                                        value={data?.doaName ? data?.doaName : locale("listing:attachments.nofilechosen")}
                                        fileChangeHandler={(e) => handleInputFileChange(e, tripId, inputData?.doNo)}
                                        label={<PublishOutlinedIcon />}
                                        required
                                        disabled={inputData?.doNo ? false : true}
                                        errors={errors && errors.data ? true : false}
                                        helperText={errors && errors.data ? errors.data : null}
                                    />
                                </Grid>
                                :
                                <Grid item xs={12}>
                                    <Tooltip title="View">
                                        <IconButton aria-label="Preview" type="button"
                                            color="primary" onClick={() => downloadDOHandlerUnsigned()}>
                                            <GetAppOutlinedIcon />
                                        </IconButton>
                                    </Tooltip>
                                    {state.jbstId === 'ASG' &&
                                        <Tooltip title="Delete">
                                            <IconButton aria-label="Delete" type="button"
                                                // color="primary" onClick={(e) => handleDeleteFile(e, row.attId)}>
                                                color="primary" onClick={() => deleteDOHandler()}>
                                                <DeleteOutlinedIcon />
                                            </IconButton>
                                        </Tooltip>
                                    }
                                </Grid>
                            }
                        </Grid>
                    </C1DataTableActions >
                }
            },
        },
        {
            name: "",
            label: "Signed DO",
            options: {
                sort: false,
                display: state?.jbstId === 'ONGOING',
                customBodyRender: (value, tableMeta, updateValue) => {
                    let tripId = tableMeta.rowData[0];
                    let doId = tableMeta.rowData[2];
                    let doStatus = tableMeta.rowData[3];
                    let doSigned = tableMeta.rowData[5];
                    setDoId(data?.doId === "" ? doId : data?.doId)
                    return <C1DataTableActions>
                        <Grid container alignItems="center" justifyContent="center">
                            {doSigned === null ?
                                <Grid item xs={12}>
                                    <C1FileUpload
                                        // inputLabel={locale("listing:attachments.docFile")}
                                        value={data?.doaName ? data?.doaName : locale("listing:attachments.nofilechosen")}
                                        fileChangeHandler={(e) => handleInputFileChangeSigned(e, tripId, doId)}
                                        label={<PublishOutlinedIcon />}
                                        required
                                        disabled={false}
                                        errors={errors && errors.data ? true : false}
                                        helperText={errors && errors.data ? errors.data : null}
                                    />
                                </Grid>
                                :
                                <Grid item xs={12}>
                                    <Tooltip title="View">
                                        <IconButton aria-label="Preview" type="button"
                                            color="primary" onClick={() => downloadDOHandlerSigned()}>
                                            <GetAppOutlinedIcon />
                                        </IconButton>
                                    </Tooltip>
                                    <Tooltip title="Delete">
                                        <IconButton aria-label="Delete" type="button"
                                            color="primary" onClick={() => deleteDOHandler()}>
                                            <DeleteOutlinedIcon />
                                        </IconButton>
                                    </Tooltip>
                                </Grid>
                            }
                        </Grid>
                    </C1DataTableActions >
                }
            },
        },
    ]
    return (<React.Fragment>

        <C1DataTable
            url={"/api/v1/clickargo/clictruck/job/trip"}
            isServer={true}
            columns={cols}
            defaultOrder="trId"
            defaultOrderDirection="desc"
            isRefresh={isRefresh}
            isShowToolbar={false}
            isShowFilterChip
            isShowDownload={false}
            isShowPrint={false}
            isShowViewColumns={false}
            isShowFilter={false}
            filterBy={
                [
                    { attribute: "tckJobTruck.jobId", value: truckJobId },
                    { attribute: "trStatus", value: 'A' }
                ]
            }
        />

    </React.Fragment >

    );
};

export default AddDoPopup;


