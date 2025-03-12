import { CircularProgress, Grid, IconButton, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Tooltip } from "@material-ui/core";
import MenuItem from "@material-ui/core/MenuItem";
import { makeStyles, withStyles } from '@material-ui/core/styles';
import DeleteOutlinedIcon from '@material-ui/icons/DeleteOutlined';
import DeleteIcon from '@material-ui/icons/DeleteOutlineOutlined';
import GetAppOutlinedIcon from '@material-ui/icons/GetAppOutlined';
import PublishOutlinedIcon from '@material-ui/icons/PublishOutlined';
import React, { forwardRef, useEffect, useImperativeHandle, useState } from "react";

import C1DataTable from "app/c1component/C1DataTable";
import C1DataTableActions from "app/c1component/C1DataTableActions";
import C1FileUpload from "app/c1component/C1FileUpload";
import C1InputField from "app/c1component/C1InputField";
import C1SelectField from "app/c1component/C1SelectField";
import useHttp from "app/c1hooks/http";
import { ATTACH_TYPE } from "app/c1utils/const";
import { titleTab, useStyles } from "app/c1utils/styles";
import { getValue, Uint8ArrayToString } from "app/c1utils/utility";
import { MatxLoading } from "matx";
import { deepUpdateState } from "app/c1utils/stateUtils";

const StyledTableCell = withStyles((theme) => ({
    head: {
        backgroundColor: '#3C77D0',
        color: theme.palette.common.white,
    },
    body: {
        fontSize: 14,
    },
}))(TableCell);

const AddDelOrderPopupRef = forwardRef(({ jobId, locale }, ref) => {

    useImperativeHandle(ref, () => ({
        getDeliveryOrder: () => {
            return {};
        }
    }));


    /** ---------------- Declare states ------------------- */
    const { isLoading, res, urlId, sendRequest } = useHttp();
    const [rowTrips, setRowTrips] = useState([]);
    const [loading, setLoading] = useState(true);

    /** --------------- Update states -------------------- */

    useEffect(() => {
        if (jobId)
            sendRequest(`/api/v1/clickargo/clictruck/job/truck/trips/${jobId}`, "loadTrips", "get");
        // 
    }, [jobId]);

    useEffect(() => {
        if (!isLoading && res) {
            setLoading(isLoading);
            switch (urlId) {
                case "loadTrips": {
                    console.log("trips: ", res?.data);
                    setRowTrips([...res?.data]);
                }
                case "uploadFile": {
                    console.log("uploadFile", res?.data);
                    let attchTripId = res?.data?.tckCtTrip?.trId;
                    rowTrips.forEach(e => {
                        if (e.trId === attchTripId) {
                            console.log("found!");
                        }
                    });
                    // if (res?.data.duplicateDoNo === true) {
                    //     setRefresh(true)
                    //     setDlOpen(false)
                    //     setOpenAddPopUp(true)
                    //     setDuplicateErrorOpen({ ...duplicateErrorOpen, msg: t("job:msg.duplicateDoNoMsg"), open: true });
                    // } else {
                    //     setDlOpen(false)
                    //     setRefresh(true)
                    //     setPopUpAttDetails(res?.data)
                    //     setDoId(res?.data?.doId);
                    //     setPopUpDoDetails(popupDoDefaultValue);
                    // }
                    break;
                }
                default: break;
            }
        }
    }, [isLoading, res, urlId]);

    const handleInputChange = (e, idx) => {
        const { name, value } = e.target;
        const newList = [...rowTrips];
        newList[idx] = { ...newList[idx], ...deepUpdateState(newList[idx], name, value) };
        console.log("newList", newList);
        setRowTrips(newList)
    }

    const handleFileUpload = (e, idx, tripId, doNo, type) => {
        e.preventDefault();
        var file = e.target.files[0];
        if (!file)
            return;

        const fileReader = new FileReader();
        fileReader.readAsArrayBuffer(file);
        fileReader.onload = e => {
            const uint8Array = new Uint8Array(e.target.result);
            var imgStr = Uint8ArrayToString(uint8Array);
            var base64Sign = btoa(imgStr);

            let rowTripDoAttach = {};
            if (type === "unsigned") {
                rowTripDoAttach = rowTrips[idx]?.unsignedDo;
                //rowTripDoAttach = rowTrips?.find(e => e?.trId === tripId)?.unsignedDo;
            }
            else {
                rowTripDoAttach = rowTrips[idx]?.signedDo;
                // rowTripDoAttach = rowTrips?.find(e => e?.trId === tripId)?.signedDo;
            }

            const popUpDoDetails = { ...rowTripDoAttach, doaName: file.name, doaData: base64Sign, tckCtTrip: { trId: tripId }, ckCtTripDo: { doNo: doNo } }
            console.log("popUpDoDetails ", popUpDoDetails)

            sendRequest(`/api/v1/clickargo/clictruck/tripdo/doattach?type=${type}`, "uploadFile", "POST", popUpDoDetails);
        };
    }

    return (loading ? <CircularProgress /> : <React.Fragment>
        <TableContainer component={Paper}>
            <Table aria-label="simple table">
                <TableHead>
                    <TableRow>
                        <StyledTableCell align="center">{locale("listing:orderDetails.from")}</StyledTableCell>
                        <StyledTableCell align="center">{locale("listing:orderDetails.to")}</StyledTableCell>
                        <StyledTableCell align="center">{locale("listing:orderDetails.orderNum")}</StyledTableCell>
                        <StyledTableCell align="center">{locale("listing:orderDetails.do")}</StyledTableCell>
                        <StyledTableCell align="center">{locale("listing:orderDetails.signedDo")}</StyledTableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {rowTrips.map((el, idx) => {
                        console.log("element", el);
                        return <TableRow key={el?.trId}>
                            <TableCell align="center">{el?.tckCtTripLocationByTrFrom?.tckCtLocation?.locName}</TableCell>
                            <TableCell align="center">{el?.tckCtTripLocationByTrTo?.tckCtLocation?.locName}</TableCell>
                            <TableCell align="center">
                                <C1InputField
                                    name={"tckCtTripDo.doNo"}
                                    required={true}
                                    inputProps={{ maxLength: 255 }}
                                    value={getValue(el?.tckCtTripDo?.doNo)}
                                    onChange={(e) => handleInputChange(e, idx)} />
                            </TableCell>
                            <TableCell align="center">
                                <C1FileUpload
                                    value={""}
                                    // value={data?.doaName ? data?.doaName : locale("listing:attachments.nofilechosen")}
                                    inputProps={{
                                        placeholder: locale("listing:attachments.nofilechosen")
                                    }}
                                    fileChangeHandler={(e) => handleFileUpload(e, idx, el?.trId, el?.tckCtTripDo?.doNo, "unsigned")}
                                    label={<PublishOutlinedIcon />}
                                    // required
                                    disabled={el?.tckCtTripDo?.doNo ? false : true}

                                />
                            </TableCell>
                            <TableCell align="center">
                                <C1FileUpload
                                    value={""}
                                    // value={data?.doaName ? data?.doaName : locale("listing:attachments.nofilechosen")}
                                    inputProps={{
                                        placeholder: locale("listing:attachments.nofilechosen")
                                    }}
                                    fileChangeHandler={(e) => handleFileUpload(e, idx, el?.trId, el?.tckCtTripDo?.doNo, "unsigned")}
                                    label={<PublishOutlinedIcon />}
                                    // required
                                    disabled={el?.tckCtTripDo?.doNo ? false : true}

                                />
                            </TableCell>
                        </TableRow>
                    })}
                </TableBody>
            </Table>
        </TableContainer>

    </React.Fragment >

    );
});

export default AddDelOrderPopupRef;


