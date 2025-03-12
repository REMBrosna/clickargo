import React, { useState, useEffect } from "react";
import {
    Grid,
    Divider
} from "@material-ui/core";

import C1InputField from "app/c1component/C1InputField";
import { titleTab, useStyles } from "app/c1utils/styles";
import SessionCache from 'app/services/sessionCacheService.js';
import C1CountryDropDownList from "app/c1component/C1CountryDropDownList";


const ShipDetailsSubTab = ({
    handleSubmit,
    data,
    inputData,
    handleInputChange,
    handleValidate,
    viewType,
    isSubmitting,
    props }) => {

    let isDisabled = true;
    if (viewType === 'new')
        isDisabled = false;
    else if (viewType === 'edit')
        isDisabled = false;
    else if (viewType === 'view')
        isDisabled = true;

    if (isSubmitting)
        isDisabled = true;

    const classes = titleTab();
    const gridClass = useStyles();
    const [tabIndex, setTabIndex] = useState(0);
    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };
    const [shipInput, setShipInputData] = useState(inputData);

    var countryListSession = SessionCache.getCountryList();
    const handleShipInputChange = (e) => {
        setShipInputData({ ...shipInput, [e.currentTarget.name]: e.currentTarget.value });
    };

    useEffect(() => {
        setShipInputData(inputData);

    }, [inputData]);

    return (

        <div className={gridClass.gridContainer}>
            <Grid container spacing={3} alignItems="center">
                <Grid container item xs={4}>
                    <C1InputField
                        label="IMO Number"
                        name="imoNo"
                        type="input"
                        disabled
                        onChange={handleShipInputChange}
                        value={shipInput.impNo}
                    />


                </Grid>
                <Grid container item xs={4}>
                    <C1InputField
                        label="Call Sign"
                        name="callSign"
                        type="input"
                        disabled
                        onChange={handleShipInputChange}
                        value={shipInput.callSign}
                    />

                </Grid>
                <Grid container item xs={4}>
                    <C1InputField
                        label="Ship Type"
                        name="shipType"
                        type="input"
                        disabled
                        onChange={handleShipInputChange}
                        value={shipInput.shipType?shipInput.shipType:''}
                    />

                </Grid>
                <Grid container item xs={4}>
                    <C1InputField
                        label="Flag State"
                        name="flageState"
                        type="input"
                        disabled
                        onChange={handleShipInputChange}
                        value={shipInput.flageState}
                    />
                </Grid>

            </Grid>
            {/* Departure Details */}
            <Grid container spacing={3} alignItems="center">
                <Grid container item xs={12} className={classes.root}>
                    Shipping Line Particulars
                    </Grid>
            </Grid>
            <Divider className="mb-6" />

            <Grid container spacing={3} alignItems="center">
                <Grid container item xs={4}>
                    <C1InputField
                        label="Applicant Tin"
                        name="applicantTin"
                        type="input"
                        disabled
                        onChange={handleShipInputChange}
                        value={shipInput.applicantTin}
                    />

                </Grid>
                {/* <Grid container item xs={4}>
                    <C1InputField
                        label="Time Of Departure"
                        name="DepartureTime"
                        type="input"
                        disabled
                        onChange={handleShipInputChange}
                        value={shipInput.DepartureTime}
                    />

                </Grid> */}
                <Grid container item xs={4}>

                    <C1CountryDropDownList
                        label="Country"
                        name="shipCountry"
                        disabled={true}
                        value={shipInput.shipCountry}
                        onChange={handleShipInputChange}
                        className="min-w-280"
                        additionalProps={props}
                    ></C1CountryDropDownList>

                </Grid>

            </Grid>
            <br />

        </div>

    );
};

export default ShipDetailsSubTab;