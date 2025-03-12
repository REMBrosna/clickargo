import React, { useState, useEffect } from "react";
import {
    Grid,
    TextField,
    MenuItem,
    InputAdornment,
    IconButton
} from "@material-ui/core";
import { MatxLoading } from "matx";
import { useStyles } from "app/c1utils/styles";
import C1InputField from "app/c1component/C1InputField";
import { titleTab } from "app/c1utils/styles";
import { makeStyles } from '@material-ui/core/styles';
import { docUploadDB } from "fake-db/db/docUpload";
import C1DataTable from 'app/c1component/C1DataTable';
import SearchIcon from '@material-ui/icons/Search';
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
const columns = [
    {
        name: "transNo", // field name in the row object
        label: "Transaction No.", // column title that will be shown in table

    },

    {
        name: "vcrno",
        label: "VCR No",
        options: {
            filter: true,
        },
    },

    {
        name: "uploadfor",
        label: "Upload For",
        options: {
            filter: true,
        },
    },

    {
        name: "fileType",
        label: "File Type",
        options: {
            filter: true,
        },
    },

    {
        name: "status",
        label: "Status",
        options: {
            filter: true,
        },
    },




];

const useTableStyle = makeStyles({
    table: {
        minWidth: 450,
    },
    column: {
        width: 300,
    },
});


function createData(numner, docType) {
    return { numner, docType };
}

const buttonUseStyles = makeStyles((theme) => ({
    buttonSpace: {
        float: "right"
    },
}));


const CommonApplicationListDetails = ({
    handleSubmit,
    data,
    inputData,
    handleInputChange,
    handleRadioChange,
    handleInputChangeSwitch,
    handleValidate,
    viewType,
    flag,
    errors,
    isSubmitting }) => {

    let isDisabled = true;
    const classes = useStyles();
    const tableCls = useTableStyle();

    if (viewType === 'new')
        isDisabled = false;
    else if (viewType === 'edit')
        isDisabled = false;
    else if (viewType === 'view')
        isDisabled = true;

    if (isSubmitting)
        isDisabled = true;

    const classe = titleTab();
    const localClasses = buttonUseStyles();
    const [loading, setLoading] = useState(false);
    let bcLabel = 'Document Upload';
    let viewBack = <C1FormButtons showDownload="true" showsubmitapp="true" showCancel="true" />;

    return (
        <React.Fragment>
            {loading && <MatxLoading />}

            <C1FormDetailsPanel
                isForm="true"
                routeSegments={[
                    { name: "Pilot Order List", path: "/applications/pilotOrder/list" },
                    { name: bcLabel },
                ]}
                cardHeader={bcLabel}
                actionBtns={viewBack}
                formInitialValues={{ ...inputData }}
                formValues={{ ...inputData }}
                formOnSubmit={(values, isSubmitting) => handleSubmit(values, isSubmitting)}
                formValidate={handleValidate}>
                {(props) => (
                    <Grid container alignItems="flex-start" spacing={3} className={classes.gridContainer}>
                        <Grid item lg={4} md={6} xs={12} >
                            <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                                <Grid item xs={12} >
                                    <TextField
                                        fullWidth
                                        required
                                        size="medium"
                                        margin="normal"
                                        label="Select Voyage Type"
                                        name="portAss"
                                        variant="outlined"
                                        onChange={handleInputChange}

                                        InputLabelProps={{
                                            shrink: true,
                                        }}
                                        select
                                        {...props}
                                    >
                                        <MenuItem value='' key=''>  </MenuItem>

                                        <MenuItem value="Inward" key="Inward"> Inward </MenuItem>
                                        <MenuItem value="Outward" key="Outward"> Outward </MenuItem>

                                    </TextField>






                                    <TextField
                                        fullWidth
                                        required
                                        size="medium"
                                        margin="normal"
                                        label="Upload For"
                                        name="portAss"
                                        variant="outlined"
                                        onChange={handleInputChange}

                                        InputLabelProps={{
                                            shrink: true,
                                        }}
                                        select
                                        {...props}
                                    >
                                        <MenuItem value='' key=''>  </MenuItem>

                                        <MenuItem value="Crew List" key="Crew List"> Crew List </MenuItem>
                                        <MenuItem value="Passenger List" key="Passenger List"> Passenger List </MenuItem>
                                        <MenuItem value="Cargo" key="Cargo"> Cargo </MenuItem>
                                        <MenuItem value="Ship Store" key="Ship Store"> Ship Store </MenuItem>
                                        <MenuItem value="Dangerous Goods" key="Dangerous Goods"> Dangerous Goods </MenuItem>

                                    </TextField>
                                </Grid>
                            </Grid>
                        </Grid>

                        <Grid item lg={4} md={6} xs={12} >
                            <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                                <Grid item xs={12} >


                                    <TextField
                                        fullWidth
                                        required
                                        size="medium"
                                        margin="normal"
                                        label="Select Application Type"
                                        name="portAss"
                                        variant="outlined"
                                        onChange={handleInputChange}

                                        InputLabelProps={{
                                            shrink: true,
                                        }}
                                        select
                                        {...props}
                                    >
                                        <MenuItem value='' key=''>  </MenuItem>

                                        <MenuItem value="Pre Arrival" key="Pre Arrival"> Pre Arrival </MenuItem>
                                        <MenuItem value="Arrival" key="Arrival"> Arrival </MenuItem>

                                    </TextField>

                                    <C1InputField
                                        label="Choose File"
                                        name="signUpload"
                                        type="file"
                                        required
                                        onChange={handleInputChange}

                                        error={errors && errors.signUpload ? true : false}
                                        helperText={errors && errors.signUpload ? errors.signUpload : null}
                                    />
                                </Grid>
                            </Grid>
                        </Grid>

                        <Grid item lg={4} md={6} xs={12} >
                            <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                                <Grid item xs={12} >
                                    <TextField
                                        label="Search VCR"
                                        margin="normal"
                                        size="medium"
                                        fullWidth
                                        variant="outlined"
                                        InputLabelProps={{
                                            shrink: true,
                                        }}
                                        InputProps={{
                                            endAdornment: (
                                                <InputAdornment>
                                                    <IconButton>
                                                        <SearchIcon />
                                                    </IconButton>
                                                </InputAdornment>
                                            )
                                        }}
                                    />


                                </Grid>
                            </Grid>

                        </Grid>

                    </Grid>
                )}
            </C1FormDetailsPanel >
            <C1DataTable url="/api/co/master/entity/country"
                columns={columns}
                title="Document Upload List"
                defaultOrder="ucrNo"
                showToolbar={false}
                dbName={docUploadDB}
                showAdd={
                    false
                }
            />
        </React.Fragment>

    );
};


export default CommonApplicationListDetails;