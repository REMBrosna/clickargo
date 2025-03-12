import React, { useState, useEffect } from "react";
import {
    Grid,
    TextField,
    Divider
} from "@material-ui/core";
import MenuItem from "@material-ui/core/MenuItem";
import C1InputField from "../../../../c1component/C1InputField";
import { titleTab, useStyles } from "app/c1utils/styles";
import C1CountryDropDownList from "app/c1component/dropdownList/C1CountryDropDownList";
import C1DateField from "app/c1component/C1DateField";
import C1SelectField from "app/c1component/C1SelectField";
import { ports } from "fake-db/db/portsTerminals";
import { portDB } from "fake-db/db/port";

const VoyageDetailsSubTab = ({
    handleSubmit,
    data,
    inputData,
    handleInputChange,
    handleDateChange,
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
    const [voyageInput, setVoyageInputData] = useState(inputData);

    const handleVoyageInputChange = (e) => {
        setVoyageInputData({ ...voyageInput, [e.target.name]: e.target.value });
    };

    useEffect(() => {
        setVoyageInputData(inputData);

    }, [inputData]);

    const dutyPaidAtOptions = [
        { value: "", desc: "" },
        { value: "KOHROKAR", desc: "Koh Rokar" },
        { value: "KAOMSAMNOR", desc: "Kaom Samnor" },
        { value: "PORT", desc: "Port" },
        { value: "DUTYTAXEXEMPTION", desc: "Duty Tax Exemption" }
    ];


    return (
        <div className={gridClass.gridContainer}>
            <Grid container spacing={3} alignItems="center">
                <Grid container item xs={4}>
                    <C1InputField
                        label="Vessel Call Ref No"
                        name="vcrNo"
                        type="input"
                        disabled
                        onChange={handleVoyageInputChange}
                        value={voyageInput.vcrNo}
                    />
                </Grid>
                <Grid container item xs={4}>

                    <C1InputField
                        label="Voyage Number"
                        name="voyageNo"
                        type="input"
                        disabled
                        onChange={handleVoyageInputChange}
                        value={voyageInput.voyageNo}
                    />

                </Grid>
                <Grid container item xs={4}>
                    <C1InputField
                        label="Name Of Master"
                        name="voyageName"
                        disabled
                        onChange={handleVoyageInputChange}
                        value={voyageInput.voyageName}
                    />

                </Grid>
            </Grid>



            <Grid container spacing={3} alignItems="center">
                <Grid container item xs={12} className={classes.root}>
                    Arrival Details
                    </Grid>
            </Grid>
            <Divider className="mb-6" />

            <Grid container spacing={3} alignItems="center">
                <Grid container item xs={4}>
                    <C1SelectField
                        label="Port Of Arrival"
                        name="arrivalPort"
                        value={voyageInput.arrivalPort}
                        onChange={handleVoyageInputChange}
                        required
                        disabled
                        options={
                            ports.map((item, ind) => {
                                if (item.countryID === 'KH') {
                                    return <MenuItem value={item.value} key={ind}>{item.value} - {item.desc}</MenuItem>;
                                }

                            })} />
                </Grid>
                <Grid container item xs={4}>
                    <C1DateField
                        label="Date Of Arrival"
                        name="arrivalDate"
                        disabled
                        onChange={handleDateChange}
                        value={voyageInput.arrivalDate}
                    />

                </Grid>
                <Grid container item xs={4}>
                    <C1InputField
                        label="Time Of Arrival(hh:mm:ss)"
                        name="arrivalTime"
                        type="input"
                        disabled
                        onChange={handleVoyageInputChange}
                        value={voyageInput.arrivalTime}
                    />


                </Grid>
                <Grid container item xs={4}>
                    <C1CountryDropDownList
                        label="Country Last Port Of Call"
                        name="lastPortCountry"
                        value={voyageInput.lastPortCountry}
                        onChange={handleVoyageInputChange}
                        className="min-w-280"
                        additionalProps={{ required: true, disabled: viewType === 'new' ? false : isDisabled }}
                        viewType={viewType}
                    ></C1CountryDropDownList>

                </Grid>

                <Grid container item xs={4}>
                    <C1SelectField
                        label="Port"
                        name="lastPort"
                        onChange={handleVoyageInputChange}
                        value={voyageInput.lastPort}
                        required
                        disabled={isDisabled}
                        options={
                            portDB.list.map((item, ind) => {
                                if (item.PORT_COUNTRY === voyageInput.lastPortCountry) {
                                    return <MenuItem value={item.PORT_CODE} key={ind}>{item.PORT_CODE} - {item.PORT_DESCRIPTION}</MenuItem>;
                                }
                            })} />
                </Grid>
                <Grid container item xs={4}>
                    <C1SelectField
                        disabled={isDisabled}
                        label="Duty Paid At"
                        required
                        name="dutyPaidAt"
                        onChange={handleVoyageInputChange}
                        value={voyageInput.dutyPaidAt}
                        options={
                            dutyPaidAtOptions.map((item, ind) => {

                                return <MenuItem value={item.value} key={ind}>{item.desc}</MenuItem>;


                            })} />
                </Grid>

            </Grid>
            {/* Departure Details */}
            <Grid container spacing={3} alignItems="center">
                <Grid container item xs={12} className={classes.root}>
                    Departure Details
                    </Grid>
            </Grid>
            <Divider className="mb-6" />

            <Grid container spacing={3} alignItems="center">
                <Grid container item xs={4}>
                    <C1SelectField
                        label="Port Of Departure"
                        name="departurePort"
                        value={voyageInput.departurePort}
                        onChange={handleVoyageInputChange}
                        required
                        disabled
                        options={
                            ports.map((item, ind) => {
                                if (item.countryID === 'KH') {
                                    return <MenuItem value={item.value} key={ind}>{item.value} - {item.desc}</MenuItem>;
                                }

                            })} />
                </Grid>
                <Grid container item xs={4}>
                    <C1DateField
                        label="Date Of Departure"
                        name="departureDate"
                        disabled
                        onChange={handleDateChange}
                        value={voyageInput.departureDate}
                    />

                </Grid>
                <Grid container item xs={4}>
                    <C1InputField
                        label="Time Of Departure"
                        name="DepartureTime"
                        type="input"
                        disabled
                        onChange={handleVoyageInputChange}
                        value={voyageInput.DepartureTime}
                    />

                </Grid>
                <Grid container item xs={4}>
                    <C1CountryDropDownList
                        label="Country Next Port Of Call"
                        name="nextPortCountry"
                        value={voyageInput.nextPortCountry}
                        onChange={handleVoyageInputChange}
                        className="min-w-280"
                        additionalProps={{ required: true, disabled: viewType === 'new' ? false : isDisabled }}
                        viewType={viewType}
                    />

                </Grid>
                <Grid container item xs={4}>
                    <C1SelectField
                        label="Next Port Call"
                        name="nextPort"
                        value={voyageInput.nextPort}
                        onChange={handleVoyageInputChange}
                        required
                        disabled={isDisabled}
                        options={
                            portDB.list.map((item, ind) => {
                                if (item.PORT_COUNTRY === voyageInput.nextPortCountry) {
                                    return <MenuItem value={item.PORT_CODE} key={ind}>{item.PORT_CODE} - {item.PORT_DESCRIPTION}</MenuItem>;
                                }
                            })} />
                </Grid>

            </Grid>
            <br />

        </div >

    );
};

export default VoyageDetailsSubTab;