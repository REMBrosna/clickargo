import React from "react";
import {
    Grid,
    TextField,
    MenuItem,
} from "@material-ui/core";

import { useStyles } from "app/c1utils/styles";
import { Formik, Form } from "formik";
import C1InputField from "app/c1component/C1InputField";

const AgentAssListDetails = ({
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

                                    {/* <TextField
                                fullWidth
                                required
                                size="medium"
                                className="m-2"
                                disabled={isDisabled}
                                label="Agent TIN"
                                name="agentTIN"
                                variant="outlined"
                                onChange={handleInputChange}
                                value={inputData.agentTIN}
                                InputLabelProps={{
                                    shrink: true,
                                }}
                                 inputProps={{ readOnly: true }}
                                 InputProps={{
                                     endAdornment:
                                         <InputAdornment position='end'>
                                             <C1FormPopup dataTable="true" columns={columns}
                                                 title="Aggent TIN List"
                                                 dbName={agentAssDB}
                                                 disabled={(viewType === 'view') ? true : false} />
                                         </InputAdornment>
                                 }}
                            /> */}
                                    {/* form="true"  fieldList={fieldList} */}

                                    <C1InputField
                                        label="Agent TIN"
                                        name="agentTIN"
                                        disabled={isDisabled}
                                        required
                                        onChange={handleInputChange}
                                        value={inputData.agentTIN}
                                        error={errors && errors.agentTIN ? true : false}
                                        helperText={errors && errors.agentTIN ? errors.agentTIN : null}
                                    />

                                    <C1InputField
                                        label="Agent Name"
                                        name="agentName"
                                        disabled={true}
                                        required
                                        onChange={handleInputChange}
                                        value={inputData.agentName}
                                        error={errors && errors.agentName ? true : false}
                                        helperText={errors && errors.agentName ? errors.agentName : null}
                                    />

                                </Grid>
                            </Grid>
                        </Grid>

                        <Grid item lg={4} md={6} xs={12} >
                            <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                                <Grid item xs={12} >

                                    <C1InputField
                                        label="Agent Address"
                                        name="agentAddr"
                                        disabled={true}
                                        required
                                        onChange={handleInputChange}
                                        value={inputData.agentAddr}
                                        error={errors && errors.agentAddr ? true : false}
                                        helperText={errors && errors.agentAddr ? errors.agentAddr : null}
                                    />

                                    <C1InputField
                                        label="Agent Country"
                                        name="agentCty"
                                        disabled={true}
                                        required
                                        onChange={handleInputChange}
                                        value={inputData.agentCty}
                                        error={errors && errors.agentCty ? true : false}
                                        helperText={errors && errors.agentCty ? errors.agentCty : null}
                                    />

                                </Grid>
                            </Grid>
                        </Grid>
                        <Grid item lg={4} md={6} xs={12} >
                            <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                                <Grid item xs={12} >

                                    <C1InputField
                                        label="Agent Email"
                                        name="agentEmail"
                                        disabled={true}
                                        required
                                        onChange={handleInputChange}
                                        value={inputData.agentEmail}
                                        error={errors && errors.agentEmail ? true : false}
                                        helperText={errors && errors.agentEmail ? errors.agentEmail : null}
                                    />

                                    <C1InputField
                                        label="Agent Ph"
                                        name="agentPh"
                                        disabled={true}
                                        required
                                        onChange={handleInputChange}
                                        value={inputData.agentPh}
                                        error={errors && errors.agentPh ? true : false}
                                        helperText={errors && errors.agentPh ? errors.agentPh : null}
                                    />

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
                                        disabled={isDisabled}
                                        label="Port Association"
                                        name="portAss"
                                        variant="outlined"
                                        onChange={handleInputChange}
                                        value={inputData.portAss}
                                        InputLabelProps={{
                                            shrink: true,
                                        }}
                                        select
                                        {...props}
                                    >
                                        <MenuItem value='' key=''>  </MenuItem>

                                        <MenuItem value="PHN" key="PHN"> PHN </MenuItem>
                                        <MenuItem value="SHV" key="SHV"> SHV </MenuItem>

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
                                        disabled={isDisabled}
                                        label="Association Type"
                                        name="assType"
                                        variant="outlined"
                                        onChange={handleInputChange}
                                        value={inputData.assType}
                                        InputLabelProps={{
                                            shrink: true,
                                        }}
                                        select
                                        {...props}
                                    >
                                        <MenuItem value='' key=''>  </MenuItem>

                                        <MenuItem value="ALL" key="ALL"> ALL  </MenuItem>
                                        <MenuItem value="PARTIAL" key="PARTIAL"> PARTIAL </MenuItem>

                                    </TextField>
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

export default AgentAssListDetails;