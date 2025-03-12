import React from "react";
import {
    Grid,
    TextField,
    MenuItem,
} from "@material-ui/core";

import C1InputField from "app/c1component/C1InputField";
import { useStyles } from "app/c1utils/styles";

import C1FormButtons from "app/c1component/C1FormButtons";
import { Formik, Form } from "formik";

const DocRepoListDetails = ({
    handleSubmit,
    data,
    inputData,
    handleInputChange,
    handleValidate,
    viewType,
    errors,
    isSubmitting }) => {

    let isDisabled = true;
    const classes = useStyles();

    if (viewType === 'new')
        isDisabled = false;
    else if (viewType === 'edit')
        isDisabled = false;
    else if (viewType === 'view')
        isDisabled = true;

    if (isSubmitting)
        isDisabled = true;


    return (
        <Formik
            initialValues={{ ...data }}
            onSubmit={(values, isSubmitting) => handleSubmit(values, isSubmitting)}
            enableReinitialize={true}
            values={{ ...data }}
            validate={handleValidate}
        >
            {(props) => (
                <Form className="p-4">

                    <Grid container alignItems="flex-start" spacing={3} className={classes.gridContainer}>
                        <Grid item lg={4} md={6} xs={12} >
                            <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                                <Grid item xs={12} >

                                    <TextField
                                        fullWidth
                                        required
                                        size="medium"
                                        margin="normal"
                                        disabled={isDisabled}
                                        label="Doc Type"
                                        name="docType"
                                        variant="outlined"
                                        onChange={handleInputChange}
                                        value={inputData.docType}
                                        InputLabelProps={{
                                            shrink: true,
                                        }}
                                        select
                                        {...props}
                                    >
                                        <MenuItem value='' key=''>  </MenuItem>

                                        <MenuItem value="TRADER_REG_DOC" key="TRADER-REG_DOC"> TRADER REG DOC </MenuItem>
                                        <MenuItem value="VESSEL_CERT" key="VESSEL_CERT"> VESSEL CERT </MenuItem>
                                        <MenuItem value="SHIP_REG" key="SHIP_REG"> SHIP REG </MenuItem>
                                        <MenuItem value="SHIP_MANIFEST" key="SHIP_MANIFEST"> SHIP MANIFEST </MenuItem>

                                    </TextField>

                                    <TextField
                                        fullWidth
                                        required
                                        size="medium"
                                        margin="normal"
                                        disabled={isDisabled}
                                        label="Shipping Line"
                                        name="shippingLine"
                                        variant="outlined"
                                        onChange={handleInputChange}
                                        value={inputData.shippingLine}
                                        InputLabelProps={{
                                            shrink: true,
                                        }}
                                        select
                                        {...props}
                                    >
                                        <MenuItem value='' key=''>  </MenuItem>

                                        <MenuItem value="Shipping Line 1" key="Shipping Line 1"> Shipping Line 1 </MenuItem>
                                        <MenuItem value="Shipping Line 2" key="Shipping Line 2"> Shipping Line 2 </MenuItem>

                                    </TextField>

                                    <TextField
                                        fullWidth
                                        required
                                        size="medium"
                                        margin="normal"
                                        disabled={isDisabled}
                                        label="Vessel"
                                        name="vesselID"
                                        variant="outlined"
                                        onChange={handleInputChange}
                                        value={inputData.vesselID}
                                        InputLabelProps={{
                                            shrink: true,
                                        }}
                                        select
                                        {...props}
                                    >
                                        <MenuItem value='' key=''>  </MenuItem>

                                        <MenuItem value="BLUEMOON" key="BLUEMOON"> BLUEMOON  </MenuItem>
                                        <MenuItem value="VESSEL1" key="VESSEL1"> VESSEL1 </MenuItem>
                                        <MenuItem value="VOYAGER 1" key="VOYAGER 1"> VOYAGER 1 </MenuItem>
                                        <MenuItem value="SEA STAR" key="SEA STAR"> SEA STAR </MenuItem>



                                    </TextField>


                                </Grid>
                            </Grid>
                        </Grid>

                        <Grid item lg={4} md={6} xs={12} >
                            <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                                <Grid item xs={12} >

                                    <C1InputField
                                        label="Doc Ref No"
                                        name="docRefNo"
                                        disabled={isDisabled}
                                        required
                                        onChange={handleInputChange}
                                        value={inputData.docRefNo}
                                        error={errors && errors.docRefNo ? true : false}
                                        helperText={errors && errors.docRefNo ? errors.docRefNo : null}
                                    />

                                    <C1InputField
                                        label="Doc Upload"
                                        name="DocUpload"
                                        disabled={isDisabled}
                                        type="file"
                                        required
                                        onChange={handleInputChange}
                                        value={inputData.DocUpload}
                                        error={errors && errors.DocUpload ? true : false}
                                        helperText={errors && errors.DocUpload ? errors.DocUpload : null}
                                    />


                                </Grid>
                            </Grid>
                        </Grid>
                        <Grid item lg={4} md={6} xs={12} >
                            <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                                <Grid item xs={12} >

                                    <C1InputField
                                        label="Doc Date"
                                        name="uploadedDate"
                                        type="date"
                                        disabled={isDisabled}
                                        required
                                        onChange={handleInputChange}
                                        value={inputData.uploadedDate}
                                        error={errors && errors.uploadedDate ? true : false}
                                        helperText={errors && errors.uploadedDate ? errors.uploadedDate : null}
                                    />

                                    <C1InputField
                                        label="Doc Expiry Date"
                                        name="docExpDate"
                                        type="date"
                                        disabled={isDisabled}
                                        required
                                        onChange={handleInputChange}
                                        value={inputData.docExpDate}
                                        error={errors && errors.docExpDate ? true : false}
                                        helperText={errors && errors.docExpDate ? errors.docExpDate : null}
                                    />

                                </Grid>
                            </Grid>
                        </Grid>

                    </Grid>

                    {/* {(viewType === 'view') ? null : <C1FormButtons showSubmit="true" showCancel="true" />} */}
                </Form>
            )}
        </Formik>
    );
};

export default DocRepoListDetails;