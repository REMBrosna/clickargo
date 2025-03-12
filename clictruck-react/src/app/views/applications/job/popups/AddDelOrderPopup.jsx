import { Grid, IconButton, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Tooltip } from "@material-ui/core";
import MenuItem from "@material-ui/core/MenuItem";
import { makeStyles, withStyles } from '@material-ui/core/styles';
import DeleteOutlinedIcon from '@material-ui/icons/DeleteOutlined';
import DeleteIcon from '@material-ui/icons/DeleteOutlineOutlined';
import GetAppOutlinedIcon from '@material-ui/icons/GetAppOutlined';
import PublishOutlinedIcon from '@material-ui/icons/PublishOutlined';
import React, { useEffect, useState } from "react";

import C1DataTable from "app/c1component/C1DataTable";
import C1DataTableActions from "app/c1component/C1DataTableActions";
import C1FileUpload from "app/c1component/C1FileUpload";
import C1InputField from "app/c1component/C1InputField";
import C1SelectField from "app/c1component/C1SelectField";
import useHttp from "app/c1hooks/http";
import { ATTACH_TYPE } from "app/c1utils/const";
import { titleTab, useStyles } from "app/c1utils/styles";

const AddUnsignedDoPopup = (props) => {

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
        uploadDeliveryOrder,
        handleInputFileChangeSigned,
        handleInputChange,
        handlePopupInputChange,
        downloadDOHandler,
        downloadDOHandlerSigned,
        downloadDOHandlerUnsigned,
        deleteDOHandler,
        setStart,
        setStop,
        locale
    } = props

    /** ---------------- Declare states ------------------- */
    const { isLoading, res, urlId, sendRequest } = useHttp();

    const attTypes = [{ value: "OTH", desc: "OTHERS" }];

    /** --------------- Update states -------------------- */
    useEffect(() => {

    }, [viewType]);

    const [index, setIndex] = useState(0);
    const handleDefault = (e) => {
        setIndex(e[0])
    }

    useEffect(() => {
        if (!isLoading && res) {
            switch (urlId) {
                default: break;
            }
        }
    }, [isLoading, res, urlId]);

    let unsignedDoc = [];

    const cols = [
        // 0
        {
            name: "trId", // field name in the row object
            label: "Trip ID", // column title that will be shown in table
            options: {
                // display: true
                display: 'excluded'
            },
        },
        // 1
        {
            name: "tckJobTruck.jobId",
            label: "Job ID",
            options: {
                // display: true
                display: 'excluded'
            },
        },
        // 2
        {
            name: "tckCtTripDo.doId",
            label: "Doa ID",
            options: {
                // display: true
                display: 'excluded'
            },
        },
        // 3
        {
            name: "tckCtTripDo.doStatus",
            label: "doStatus",
            options: {
                // display: true
                display: 'excluded'
            },
        },
        // 4
        {
            name: "unsignedDo.doaId",
            label: "Unsigned DO",
            options: {
                display: 'excluded'
                // display: true
            },
        },
        // 5
        {
            name: "signedDo.doaId",
            label: "Signed DO",
            options: {
                // display: true
                display: 'excluded'
            },
        },
        // 6
        {
            name: "tckCtTripLocationByTrFrom.tlocLocName",
            label: locale("listing:orderDetails.from"),
            options: {
                sort: false,
            },
        },
        // 7
        {
            name: "tckCtTripLocationByTrTo.tlocLocName",
            label: locale("listing:orderDetails.to"),
            options: {
                sort: false,
            },
        },
        // 8
        {
            name: "tckCtTripDo.doNo",
            label: locale("listing:orderDetails.orderNum"),
            options: {
                customBodyRender: (value, tableMeta, updateValue) => {
                    if (tableMeta.rowData[4] !== null) {
                        return <strong>{value}</strong>
                    } else {
                        return <C1InputField
                            name={"doNo"}
                            required={true}
                            inputProps={{ maxLength: 255 }}
                            value={index === tableMeta.rowData[0] ? inputData?.doNo : null}
                            onChange={(e) => handlePopupInputChange(e, index)} />
                    }
                },
                sort: false,
            },
        },
        // 9
        {
            name: "",
            label: locale("listing:orderDetails.do"),
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
                                        value={index === tableMeta.rowData[0] ? null : null}
                                        // value={data?.doaName ? data?.doaName : locale("listing:attachments.nofilechosen")}
                                        inputProps={{
                                            placeholder: locale("listing:attachments.nofilechosen")
                                        }}
                                        fileChangeHandler={(e) => uploadDeliveryOrder(e, tripId, inputData?.doNo, "unsigned")}
                                        label={<PublishOutlinedIcon />}
                                        required
                                        disabled={inputData?.doNo && index === tableMeta.rowData[0] ? false : true}
                                        errors={errors && errors.data ? true : false}
                                        helperText={errors && errors.data ? errors.data : null}
                                    />
                                </Grid>
                                :
                                <Grid item xs={12}>
                                    <Tooltip title={locale("buttons:view")}>
                                        <IconButton aria-label="Preview" type="button"
                                            color="primary" onClick={() => downloadDOHandler(tableMeta.rowData[4])}>
                                            <GetAppOutlinedIcon />
                                        </IconButton>
                                    </Tooltip>
                                    {state.jbstId === 'ASG' &&
                                        <Tooltip title={locale("buttons:delete")}>
                                            <IconButton aria-label="Delete" type="button"
                                                color="primary" onClick={() => deleteDOHandler(tableMeta.rowData[4], "unsigned")}>
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
            label: locale("listing:orderDetails.signedDo"),
            options: {
                sort: false,
                display: state?.jbstId === 'ONGOING',
                customBodyRender: (value, tableMeta, updateValue) => {
                    let tripId = tableMeta.rowData[0];
                    let doId = tableMeta.rowData[2];
                    let doStatus = tableMeta.rowData[3];
                    let doUnsigned = tableMeta.rowData[4];
                    let doSigned = tableMeta.rowData[5];
                    setDoId(data?.doId === "" ? doId : data?.doId)
                    unsignedDoc = tableMeta.tableData.map(data => data[4])
                    // check if any unsigned doc is null, if true, cannot submit
                    setStart(!unsignedDoc.includes(null));
                    // if there's no signed document, BE shows tripDoAttach the same id
                    setStop(!unsignedDoc.includes(doSigned))
                    return <C1DataTableActions>
                        <Grid container alignItems="center" justifyContent="center">
                            {/** if there's no signed document, BE shows tripDoAttach the same id */}
                            {doSigned === doUnsigned ?
                                <Grid item xs={12}>
                                    <C1FileUpload
                                        // value={data?.doaName ? data?.doaName : locale("listing:attachments.nofilechosen")}
                                        inputProps={{
                                            placeholder: locale("listing:attachments.nofilechosen")
                                        }}
                                        fileChangeHandler={(e) => uploadDeliveryOrder(e, tripId, tableMeta.rowData[8], "signed")}
                                        label={<PublishOutlinedIcon />}
                                        required
                                        disabled={false}
                                        errors={errors && errors.data ? true : false}
                                        helperText={errors && errors.data ? errors.data : null}
                                    />
                                </Grid>
                                :
                                <Grid item xs={12}>
                                    <Tooltip title={locale("buttons:view")}>
                                        <IconButton aria-label="Preview" type="button"
                                            color="primary" onClick={() => downloadDOHandler(tableMeta.rowData[5])}>
                                            <GetAppOutlinedIcon />
                                        </IconButton>
                                    </Tooltip>
                                    <Tooltip title={locale("buttons:delete")}>
                                        <IconButton aria-label="Delete" type="button"
                                            color="primary" onClick={() => deleteDOHandler(tableMeta.rowData[5], "signed")}>
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
        // {
        //     name: "id",
        //     label: "#",
        //     options: {
        //         filter: true,
        //         sort: true,
        //         customBodyRender: (rowIndex, dataIndex) => {
        //             console.log("Data Index ", dataIndex.rowIndex)
        //             let index = dataIndex.rowIndex;
        //             return index;
        //         }
        //     }
        // }
    ]
    return (<React.Fragment>
        <div style={{ maxHeight: "500px", overflowY: "auto" }}>
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
                handleRowClick={(e) => handleDefault(e)}
                onRowClickEvent={true}
            />
        </div>

    </React.Fragment >

    );
};

export default AddUnsignedDoPopup;


