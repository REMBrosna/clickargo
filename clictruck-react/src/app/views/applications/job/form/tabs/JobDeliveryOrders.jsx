import { Grid, IconButton, Tooltip } from "@material-ui/core";
import CheckOutlinedIcon from '@material-ui/icons/CheckOutlined';
import DeleteOutlinedIcon from '@material-ui/icons/DeleteOutlined';
import GetAppOutlinedIcon from '@material-ui/icons/GetAppOutlined';
import PublishOutlinedIcon from '@material-ui/icons/PublishOutlined';
import RemoveOutlinedIcon from '@material-ui/icons/RemoveOutlined';
import React, { useState } from "react";

import C1DataTable from "app/c1component/C1DataTable";
import C1DataTableActions from "app/c1component/C1DataTableActions";
import C1FileUpload from "app/c1component/C1FileUpload";
import C1InputField from "app/c1component/C1InputField";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import useHttp from "app/c1hooks/http";
import { AccountTypes, JobStates } from "app/c1utils/const";
import useAuth from "app/hooks/useAuth";

const JobDeliveryOrders = (props) => {

    const {
        inputData,
        jobSource,
        isMobile,
        data,
        state,
        doId,
        isRefresh,
        setDoId,
        truckJobId,
        viewType,
        errors,
        validationErrors,
        // handleInputFileChange,
        uploadDeliveryOrder,
        // handleInputFileChangeSigned,
        // handleInputChange,
        handlePopupInputChange,
        downloadDOHandler,
        // downloadDOHandlerSigned,
        // downloadDOHandlerUnsigned,
        deleteDOHandler,
        setStart,
        setStop,
        locale,
        saveTripDo,
        editTripDo
    } = props

    /** ---------------- Declare states ------------------- */
    const { isLoading, res, error, urlId, sendRequest } = useHttp();

    const { user } = useAuth();
    const isCoFfUser = user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_CO.code
        || user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_FF.code;
    const isTruckingOperator = user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_TO.code;
    const isSourceXml = jobSource !== null && jobSource?.toLowerCase().indexOf('xml') !== -1 ? true : false;


    /** --------------- Update states -------------------- */
    const [index, setIndex] = useState(0);
    const handleDefault = (e) => {
        setIndex(e[0])
    }

    let unsignedDoc = [];
    let signedDoc = [];
    let doNos = [];

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
            name: "tckCtTripDo.doUnsigned",
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
            name: "tckCtTripLocationByTrFrom.tckCtLocation.locName",
            label: locale("listing:orderDetails.from"),
            options: {
                sort: false,
                setCellHeaderProps: () => { return { style: { minWidth: '250px' } } },
            },
        },
        // 7
        {
            name: "tckCtTripLocationByTrTo.tckCtLocation.locName",
            label: locale("listing:orderDetails.to"),
            options: {
                sort: false,
                setCellHeaderProps: () => { return { style: { minWidth: '250px' } } },
            },
        },
        // 8
        {
            name: "tckCtTripDo.doNo",
            label: locale("listing:orderDetails.orderNum"),
            options: {
                customBodyRender: (value, tableMeta, updateValue) => {
                    if (tableMeta.rowData[8]) {
                        return <><strong>{value}</strong>
                            {state.jbstId === JobStates.ASG.code && !isSourceXml &&
                                <Tooltip title={locale("buttons:change")}>
                                    <IconButton aria-label="Save" type="save"
                                        color="primary" onClick={() => editTripDo(tableMeta.rowData[5], tableMeta.rowData[8])}>
                                        <RemoveOutlinedIcon />
                                    </IconButton>
                                </Tooltip>
                            }
                        </>
                    } else {
                        return <span>
                            {isTruckingOperator && <Grid container alignItems="center" justifyContent="flex-start">
                                <span style={{ minWidth: '48px' }}>
                                    <C1InputField
                                        name={"doNo"}
                                        required={true}
                                        disabled={isCoFfUser || isMobile}
                                        inputProps={{ maxLength: 255 }}
                                        label={locale("listing:orderDetails.orderNum")}
                                        value={index === tableMeta.rowData[0] ? inputData?.doNo : null}
                                        onChange={(e) => handlePopupInputChange(e, index)}
                                        error={validationErrors && validationErrors['or-not-found'] ? true : false} />
                                </span>
                                {!isMobile && <span style={{ minWidth: '48px' }}>
                                    <Tooltip title={locale("buttons:save")}>
                                        <IconButton aria-label="Save" type="save"
                                            color={validationErrors && validationErrors['or-not-found'] ? "secondary" : "primary"}
                                            onClick={() => saveTripDo(inputData?.doNo, tableMeta.rowData[0])}>
                                            <CheckOutlinedIcon />
                                        </IconButton>
                                    </Tooltip>
                                </span>}
                            </Grid>}
                        </span>
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
                            {doUnsigned === null && state.jbstId === JobStates.ASG.code && isTruckingOperator &&
                                <Grid item xs={12}>
                                    <C1FileUpload
                                        value={index === tableMeta.rowData[0] ? null : null}
                                        inputProps={{
                                            placeholder: locale("listing:attachments.nofilechosen")
                                        }}
                                        inputLabel={locale("listing:orderDetails.doFile")}
                                        fileChangeHandler={(e) => uploadDeliveryOrder(e, tripId, tableMeta.rowData[8], "unsigned")}
                                        label={<PublishOutlinedIcon />}
                                        disabled={!tableMeta.rowData[8]}
                                        errors={errors && errors.data ? true : false}
                                        helperText={errors && errors.data ? errors.data : null}
                                    />
                                </Grid>
                            }
                            {doUnsigned === null && state.jbstId === JobStates.ASG.code && isCoFfUser &&
                                <Grid item xs={12}>
                                    {"-"}
                                </Grid>
                            }
                            {doUnsigned !== null && state.jbstId === JobStates.ASG.code &&
                                <Grid container alignItems="flex-start" justifyContent="flex-start">
                                    <C1LabeledIconButton
                                        tooltip={locale("buttons:download")}
                                        label={locale("buttons:download")}
                                        action={() => downloadDOHandler(tableMeta.rowData[4])}>
                                        <GetAppOutlinedIcon />
                                    </C1LabeledIconButton>
                                    {state.jbstId === 'ASG' && isTruckingOperator &&
                                        <C1LabeledIconButton
                                            tooltip={locale("buttons:delete")}
                                            label={locale("buttons:delete")}
                                            action={() => deleteDOHandler(tableMeta.rowData[4], "unsigned")}>
                                            <DeleteOutlinedIcon />
                                        </C1LabeledIconButton>
                                    }
                                </Grid>
                            }
                            {!doUnsigned && state.jbstId === JobStates.ONGOING.code &&
                                <Grid item xs={12}>
                                    {"-"}
                                </Grid>
                            }
                            {doUnsigned && state.jbstId === JobStates.ONGOING.code &&
                                <Grid container alignItems="flex-start" justifyContent="flex-start">
                                    <C1LabeledIconButton
                                        tooltip={locale("buttons:download")}
                                        label={locale("buttons:download")}
                                        action={() => downloadDOHandler(tableMeta.rowData[4])}>
                                        <GetAppOutlinedIcon />
                                    </C1LabeledIconButton>
                                    {state.jbstId === JobStates.ASG.code && isTruckingOperator &&
                                        <C1LabeledIconButton
                                            tooltip={locale("buttons:delete")}
                                            label={locale("buttons:delete")}
                                            action={() => deleteDOHandler(tableMeta.rowData[4], "unsigned")}>
                                            <DeleteOutlinedIcon />
                                        </C1LabeledIconButton>
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
                display: state?.jbstId === JobStates.ONGOING.code || state?.jbstId === JobStates.DLV.code,
                customBodyRender: (value, tableMeta, updateValue) => {
                    let tripId = tableMeta.rowData[0];
                    let doId = tableMeta.rowData[2];
                    let doStatus = tableMeta.rowData[3];
                    let doUnsigned = tableMeta.rowData[4];
                    const doSignedId = tableMeta.rowData[5];
                    setDoId(data?.doId === "" ? doId : data?.doId)
                    doNos = tableMeta.tableData.map(data => data[8])
                    unsignedDoc = tableMeta.tableData.map(data => data[4])
                    signedDoc = tableMeta.tableData.map(data => data[5])

                    // check if any unsigned doc is null, if true, cannot submit
                    setStart(!doNos.includes(null) && !doNos.includes(undefined));
                    // if there's no signed document, BE shows tripDoAttach the same id
                    setStop(!signedDoc.includes(null))
                    return <C1DataTableActions>
                        <Grid container alignItems="center" justifyContent="center">
                            {/** if there's no signed document, BE shows tripDoAttach the same id */}
                            {!doSignedId && state.jbstId === JobStates.ONGOING.code && isTruckingOperator &&
                                <Grid item xs={12}>
                                    <C1FileUpload
                                        inputProps={{
                                            placeholder: locale("listing:attachments.nofilechosen")
                                        }}
                                        inputLabel={locale("listing:orderDetails.signedDoFile")}
                                        fileChangeHandler={(e) => uploadDeliveryOrder(e, tripId, tableMeta.rowData[8], "signed")}
                                        label={<PublishOutlinedIcon />}
                                        required={isMobile && isSourceXml ? true : false}
                                        disabled={false}
                                        errors={validationErrors && validationErrors['pod-not-found'] ? true : false}
                                        helperText={errors && errors.data ? errors.data : null}
                                    />
                                </Grid>
                            }
                            {!doSignedId && state.jbstId === JobStates.ONGOING.code && isCoFfUser &&
                                <Grid item xs={12}>
                                    {"-"}
                                </Grid>
                            }
                            {(doSignedId && state.jbstId === JobStates.ONGOING.code || state?.jbstId === JobStates.DLV.code) &&
                                <Grid container alignItems="flex-start" justifyContent="flex-start">
                                    <C1LabeledIconButton
                                        tooltip={locale("buttons:download")}
                                        label={locale("buttons:download")}
                                        action={() => downloadDOHandler(doSignedId)}>
                                        <GetAppOutlinedIcon />
                                    </C1LabeledIconButton>
                                    {state.jbstId === JobStates.ONGOING.code && isTruckingOperator && <C1LabeledIconButton
                                        tooltip={locale("buttons:delete")}
                                        label={locale("buttons:delete")}
                                        action={() => deleteDOHandler(doSignedId, "signed")}>
                                        <DeleteOutlinedIcon />
                                    </C1LabeledIconButton>}
                                </Grid>
                            }
                        </Grid>
                    </C1DataTableActions >
                }
            },
        },
        // 11
        // {
        //     name: "tckCtTripDo.doId",
        //     label: "",
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
                    ]
                }
                handleRowClick={(e) => handleDefault(e)}
                onRowClickEvent={true}
            />
        </div>

    </React.Fragment >

    );
};

export default JobDeliveryOrders;


