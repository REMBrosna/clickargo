import React from "react";
import {
    Grid,
    TextField,
    MenuItem,
} from "@material-ui/core";

import C1InputField from "app/c1component/C1InputField";
import { useStyles } from "app/c1utils/styles";
const CrewDetails = ({
    handleSubmit,
    data,
    inputData,
    handleInputChange,
    handleValidate,
    viewType,
    props,
    isSubmitting }) => {
    const classes = useStyles();
    let isDisabled = true;
    if (viewType === 'new')
        isDisabled = false;
    else if (viewType === 'edit')
        isDisabled = false;
    else if (viewType === 'view')
        isDisabled = true;

    if (isSubmitting)
        isDisabled = true;


    return (
        <Grid container alignItems="flex-start" spacing={3} className={classes.gridContainer}>
            <Grid item lg={4} md={6} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12} >
                        <C1InputField

                            label="Family Name"
                            name="familyName"
                            disabled={isDisabled}
                            required
                            onChange={handleInputChange}
                            value={inputData.familyName}
                            error={props.errors && props.errors.familyName ? true : false}
                            helperText={props.errors && props.errors.familyName ? props.errors.familyName : null}
                        />
                        <C1InputField
                            label="Given Name"
                            name="givenName"
                            disabled={isDisabled}
                            required
                            onChange={handleInputChange}
                            value={inputData.givenName}
                            error={props.errors && props.errors.givenName ? true : false}
                            helperText={props.errors && props.errors.givenName ? props.errors.givenName : null}
                        />
                        <C1InputField
                            label="Rank / Rating"
                            name="rank"
                            disabled={isDisabled}
                            required
                            onChange={handleInputChange}
                            value={inputData.rank}
                            error={props.errors && props.errors.rank ? true : false}
                            helperText={props.errors && props.errors.rank ? props.errors.rank : null}
                        />
                        <C1InputField
                            label="Date Of Birth"
                            name="dob"
                            disabled={isDisabled}
                            type="date"
                            required
                            onChange={handleInputChange}
                            value={inputData.dob}
                            error={props.errors && props.errors.dob ? true : false}
                            helperText={props.errors && props.errors.dob ? props.errors.dob : null}
                        />
                        <C1InputField
                            label="Place of birth"
                            name="placeOfBirth"
                            disabled={isDisabled}
                            required
                            onChange={handleInputChange}
                            value={inputData.placeOfBirth}
                            error={props.errors && props.errors.placeOfBirth ? true : false}
                            helperText={props.errors && props.errors.placeOfBirth ? props.errors.placeOfBirth : null}
                        />
                        <TextField
                            fullWidth
                            required
                            size="medium"
                            margin="normal"
                            disabled={isDisabled}
                            label="Nationality"
                            name="nationality"
                            variant="outlined"
                            onChange={handleInputChange}
                            value={inputData.nationality}
                            InputLabelProps={{
                                shrink: true,
                            }}
                            select
                            {...props}
                        >
                            <MenuItem value='' key=''>  </MenuItem>

                            <MenuItem value="AG" key="AG"> Argentiana </MenuItem>
                            <MenuItem value="AT" key="AT"> Atlanta </MenuItem>
                        </TextField>
                        <TextField
                            fullWidth
                            required
                            size="medium"
                            margin="normal"
                            disabled={isDisabled}
                            label="Gender"
                            name="gender"
                            variant="outlined"
                            onChange={handleInputChange}
                            value={inputData.gender}
                            InputLabelProps={{
                                shrink: true,
                            }}
                            select
                            {...props}
                        >
                            <MenuItem value='' key=''>  </MenuItem>

                            <MenuItem value="Male" key="Male"> Male </MenuItem>
                            <MenuItem value="Female" key="Female"> Female </MenuItem>
                        </TextField>
                    </Grid>
                </Grid>
            </Grid>

            <Grid item lg={4} md={6} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12} >
                        <TextField
                            fullWidth
                            required
                            size="medium"
                            margin="normal"
                            disabled={isDisabled}
                            label="Identity Doc Type"
                            name="identityDocType"
                            variant="outlined"
                            onChange={handleInputChange}
                            value={inputData.identityDocType}
                            InputLabelProps={{
                                shrink: true,
                            }}
                            select
                            {...props}
                        >
                            <MenuItem value='' key=''>  </MenuItem>

                            <MenuItem value="Passport" key="PA"> Passport </MenuItem>
                            <MenuItem value="Driving Licence" key="DL"> Driving Licence </MenuItem>
                            <MenuItem value="I Card" key="DL"> I Card </MenuItem>

                        </TextField>
                        <C1InputField
                            label="Identity Doc No"
                            name="passportNo"
                            disabled={isDisabled}
                            required
                            onChange={handleInputChange}
                            value={inputData.passportNo}
                            error={props.errors && props.errors.passportNo ? true : false}
                            helperText={props.errors && props.errors.passportNo ? props.errors.passportNo : null}
                        />
                        <C1InputField
                            label="Issuing State of Identity"
                            name="issuingStateOfIdnetity"
                            disabled={isDisabled}
                            required
                            onChange={handleInputChange}
                            value={inputData.issuingStateOfIdnetity}
                            error={props.errors && props.errors.issuingStateOfIdnetity ? true : false}
                            helperText={props.errors && props.errors.issuingStateOfIdnetity ? props.errors.issuingStateOfIdnetity : null}
                        />
                        <C1InputField
                            label="Expiry Date of Identity"
                            type="date"
                            name="expiryDateOfIdentity"
                            disabled={isDisabled}
                            required
                            onChange={handleInputChange}
                            value={inputData.expiryDateOfIdentity}
                            error={props.errors && props.errors.expiryDateOfIdentity ? true : false}
                            helperText={props.errors && props.errors.expiryDateOfIdentity ? props.errors.expiryDateOfIdentity : null}
                        />

                    </Grid>
                </Grid>
            </Grid>
            <Grid item lg={4} md={6} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12} >

                        <C1InputField
                            label="Vaccination No"
                            name="vaccinationNo"
                            disabled={isDisabled}
                            required
                            onChange={handleInputChange}
                            value={inputData.vaccinationNo}
                            error={props.errors && props.errors.vaccinationNo ? true : false}
                            helperText={props.errors && props.errors.vaccinationNo ? props.errors.vaccinationNo : null}
                        />
                        <C1InputField
                            label="Type Of Vaccination"
                            name="typeOfVaccination"
                            disabled={isDisabled}
                            required
                            onChange={handleInputChange}
                            value={inputData.typeOfVaccination}
                            error={props.errors && props.errors.typeOfVaccination ? true : false}
                            helperText={props.errors && props.errors.typeOfVaccination ? props.errors.typeOfVaccination : null}
                        />
                        <C1InputField
                            label="Expiry Date"
                            name="expDate"
                            type="date"
                            disabled={isDisabled}
                            required
                            onChange={handleInputChange}
                            value={inputData.expDate}
                            error={props.errors && props.errors.expDate ? true : false}
                            helperText={props.errors && props.errors.expDate ? props.errors.expDate : null}
                        />


                    </Grid>
                </Grid>
            </Grid>

        </Grid>

    );
};

export default CrewDetails;