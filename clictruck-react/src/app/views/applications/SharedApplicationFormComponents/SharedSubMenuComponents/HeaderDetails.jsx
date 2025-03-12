import React, { useState, useEffect } from "react";
import {
    MenuItem
} from "@material-ui/core";
import Grid from "@material-ui/core/Grid";
import { titleTab, useStyles } from "app/c1utils/styles";
import SessionCache from 'app/services/sessionCacheService.js';
import C1InputField from "app/c1component/C1InputField";
import { ports, terminals } from "fake-db/db/portsTerminals";
import C1SelectField from "app/c1component/C1SelectField";
import C1DateField from "app/c1component/C1DateField";

const VoyageDetailsSubTab = ({
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
    const gridClasses = useStyles();
    const [tabIndex, setTabIndex] = useState(0);
    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };
    const [voyageInput, setVoyageInputData] = useState(inputData);

    var countryListSession = SessionCache.getCountryList();

    const handleVoyageInputChange = (e) => {
        setVoyageInputData({ ...voyageInput, [e.target.name]: e.target.value });
    };

    const handleDateChange = (name, e) => {
        setVoyageInputData({ ...voyageInput, [name]: e });
    }

    useEffect(() => {
        setVoyageInputData(inputData);

    }, [inputData]);

    return (
        <React.Fragment>
            <Grid container alignItems="flex-start" spacing={3} className={gridClasses.gridContainer}>
                <Grid item lg={4} md={6} xs={12} >
                    <Grid container alignItems="center" spacing={3} className={gridClasses.gridContainer}>
                        <Grid item xs={12} >
                            <C1InputField label="VCR No"
                                name="vcrNo"
                                type="input"
                                disabled={true}
                                onChange={handleVoyageInputChange}
                                value={voyageInput.vcrNo}
                            />


                            <C1InputField label="Ship Name"
                                name="shipName"
                                type="input"
                                disabled={true}
                                onChange={handleVoyageInputChange}
                                value={voyageInput.shipName}
                            />
                            <C1InputField label="Ship Owner"
                                name="shipOwner"
                                type="input"
                                disabled={true}
                                onChange={handleVoyageInputChange}
                                value={voyageInput.shipOwner}
                            />
                            <C1InputField label="Flag State"
                                name="flageState"
                                type="input"
                                disabled={true}
                                onChange={handleVoyageInputChange}
                                value={voyageInput.flageState}
                            />

                        </Grid>
                    </Grid>
                </Grid>
                <Grid item lg={4} md={6} xs={12} >
                    <Grid container alignItems="center" spacing={3} className={gridClasses.gridContainer}>
                        <Grid item xs={12} >
                            <C1InputField label="IMO NO"
                                name="imoNo"
                                type="input"
                                disabled={true}
                                onChange={handleVoyageInputChange}
                                value={voyageInput.imoNo}
                            />

                            <C1InputField label="Call Sign"
                                name="callSign"
                                type="input"
                                disabled={true}
                                onChange={handleVoyageInputChange}
                                value={voyageInput.callSign}
                            />

                            <C1InputField label="GT"
                                name="gt"
                                type="input"
                                disabled={isDisabled}
                                onChange={handleVoyageInputChange}
                                value={voyageInput.gt}
                            />
                            <C1InputField label="NT"
                                name="nt"
                                type="input"
                                disabled={isDisabled}
                                onChange={handleVoyageInputChange}
                                value={voyageInput.nt}
                            />
                            <C1InputField label="DWT"
                                name="dwt"
                                type="input"
                                disabled={isDisabled}
                                onChange={handleVoyageInputChange}
                                value={voyageInput.dwt}
                            />



                        </Grid>
                    </Grid>
                </Grid>

                <Grid item lg={4} md={6} xs={12} >
                    <Grid container alignItems="center" spacing={3} className={gridClasses.gridContainer}>
                        <Grid item xs={12} >
                            <C1DateField label="Estd Date of Arrival"
                                name="arrivalDate"
                                type="date"
                                disabled={isDisabled}
                                onChange={handleDateChange}
                                value={voyageInput.arrivalDate}
                            />
                            {/* <C1CountryDropDownList
                                label="Port Of Arrival"
                                name="arrivalPort"
                                value={voyageInput.arrivalPort}
                                onChange={handleVoyageInputChange}
                                margin="normal"
                                variant="outlined"
                                viewType={viewType}

                                select
                                {...props}
                                InputLabelProps={{
                                    shrink: true,
                                }}
                            ></C1CountryDropDownList> */}

                            <C1SelectField
                                label="Port Of Arrival"
                                name="arrivalPort"
                                onChange={handleVoyageInputChange}
                                value={voyageInput.arrivalPort}
                                disabled={true}
                                options={
                                    ports.map((item, ind) => (
                                        <MenuItem value={item.value} key={ind}>{item.value} - {item.desc}</MenuItem>
                                    ))} />

                            <C1DateField label="Estd Date of Departure"
                                name="departureDate"
                                type="date"
                                disabled={isDisabled}
                                onChange={handleDateChange}
                                value={voyageInput.departureDate}
                            />
                            {/* <C1CountryDropDownList
                                label="Port Of Departure"
                                name="departurePort"
                                value={voyageInput.departurePort}
                                onChange={handleVoyageInputChange}
                                margin="normal"
                                variant="outlined"
                                viewType={viewType}
                                select
                                {...props}
                                InputLabelProps={{
                                    shrink: true,
                                }}
                            ></C1CountryDropDownList> */}

                            <C1SelectField
                                label="Port Of Departure"
                                name="departurePort"
                                onChange={handleVoyageInputChange}
                                value={voyageInput.departurePort}
                                disabled={true}
                                options={
                                    ports.map((item, ind) => (
                                        <MenuItem value={item.value} key={ind}>{item.value} - {item.desc}</MenuItem>
                                    ))} />


                            <C1InputField label="Address"
                                name="address"
                                type="input"
                                disabled={isDisabled}
                                onChange={handleVoyageInputChange}
                                value={voyageInput.address}
                            />

                        </Grid>
                    </Grid>
                </Grid>
            </Grid>
        </React.Fragment >

    );
};


export default VoyageDetailsSubTab;