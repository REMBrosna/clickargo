import { Grid } from "@material-ui/core";
import MenuItem from "@material-ui/core/MenuItem";
import React, { useEffect, useState } from "react";

import C1FileUpload from "app/c1component/C1FileUpload";
import C1SelectField from "app/c1component/C1SelectField";
import useHttp from "app/c1hooks/http";
import { TRIP_ATTACH_TYPE } from "app/c1utils/const";
import { getValue } from "app/c1utils/utility";
import C1TextArea from "../../../../c1component/C1TextArea";
import Typography from "@material-ui/core/Typography";

const style = {
    display: "inline-block",
    maxWidth: "300px",
    overflow: "hidden",
    textOverflow: "ellipsis",
    whiteSpace: "nowrap",
    verticalAlign: "middle"
}

const AddTripAttPopup = (props) => {

    const {
        inputData,
        errors,
        handleInputFileChange,
        handleInputChange,
        locale,
        tripList
    } = props

    /** ---------------- Declare states ------------------- */
    const { isLoading, res, urlId, sendRequest } = useHttp();

    const [attTypeList, setAttTypeList] = useState([]);

    /** --------------- Update states -------------------- */
    useEffect(() => {
        sendRequest(TRIP_ATTACH_TYPE, 'getTripAttTypeList', 'GET');
    }, []);

    useEffect(() => {
        if (!isLoading && res) {
            switch (urlId) {
                case "getTripAttTypeList":
                    setAttTypeList(res?.data)
                    break;
                default: break;
            }
        }
    }, [isLoading, res, urlId]);

    const isImage = ['PHOTO_PICKUP', 'PHOTO_DROPOFF', 'SIGNATURE'].includes(inputData?.tckCtMstTripAttachType?.atypId);

    return (<React.Fragment>

        <Grid container spacing={2} >
            <Grid container item xs={12} sm={6} direction="column">
                <C1SelectField
                    label={locale("listing:attachments.attType")}
                    name="tckCtMstTripAttachType.atypId"
                    required
                    value={inputData?.tckCtMstTripAttachType?.atypId ? inputData?.tckCtMstTripAttachType?.atypId : ""}
                    onChange={handleInputChange}
                    isServer={true}
                    options={{
                        url: TRIP_ATTACH_TYPE,
                        key: "atypId",
                        id: 'atypId',
                        desc: 'atypName',
                        isCache: false
                    }}
                    error={errors["TCkCtMstTripAttachType.atypId"] !== undefined}
                    helperText={errors["TCkCtMstTripAttachType.atypId"] || ""}
                />
                <C1SelectField
                    label={locale("listing:attachments.trip")}
                    name="tckCtTrip.trId"
                    required
                    value={
                        tripList?.length === 1
                            ? tripList[0]?.trId
                            : inputData?.tckCtTrip?.trId || ""
                    }
                    disabled={tripList?.length === 1}
                    onChange={handleInputChange}
                    isServer={true}
                    optionsMenuItemArr={tripList.map((row) => {
                        const locFrom = row?.tckCtTripLocationByTrFrom.tlocLocAddress;
                        const locTo = row?.tckCtTripLocationByTrTo?.tlocLocAddress;
                        return (
                            <MenuItem value={row.trId} key={row.trId}>
                                <Typography variant="body1" style={style}>
                                    <strong>From:&nbsp;</strong> <span>{locFrom}&nbsp;</span>
                                </Typography>
                                <Typography variant="body1"  style={style}>
                                    <strong>&nbsp;To:</strong> <span>&nbsp;{locTo}</span>
                                </Typography>
                            </MenuItem>
                        );
                    })}
                    error={Boolean(errors?.["TCkCtTrip.trId"])}
                    helperText={errors?.["TCkCtTrip.trId"] || ""}
                />
            </Grid>

            <Grid container item xs={12} sm={6} direction="column">
                <Grid item xs={12}>
                    <C1FileUpload
                        inputLabel={isImage ? locale("listing:attachments.docFileImg") : locale("listing:attachments.docFile")}
                        inputProps={{ placeholder: locale("listing:attachments.nofilechosen") }}
                        value={getValue(inputData?.atName)}
                        fileChangeHandler={handleInputFileChange}
                        label={locale("listing:attachments.browse").toUpperCase()}
                        required={!isImage}
                        disabled={false}
                        errors={errors["atName"] !== undefined}
                        helperText={errors["atName"] || ""}
                    />
                    <C1TextArea
                        name="atComment"
                        label={locale("listing:attachments.comment")}
                        multiline
                        textLimit={1024}
                        value={getValue(inputData?.atComment)}
                        onChange={handleInputChange}
                        isabled={false}
                    />
                </Grid>
            </Grid>
        </Grid>
    </React.Fragment >

    );
};

export default AddTripAttPopup;


