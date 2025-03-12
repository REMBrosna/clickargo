import React from "react";
import {
    Grid,
    TextField,
    Divider,
    FormControl,
    FormGroup,
    FormControlLabel,
    FormLabel,
    MenuItem,
} from "@material-ui/core";

import { Formik, Form } from "formik";
import { titleTab } from "app/c1utils/styles";

import C1DataTable from 'app/c1component/C1DataTable';
import C1DataTableActions from 'app/c1component/C1DataTableActions';
import C1InputField from "app/c1component/C1InputField";


const columns = [
    {
        name: "paymentRefNo", // field name in the row object
        label: "Payment Ref. No", // column title that will be shown in table
        options: {
            sort: true,
            filter: true
        },
    },
    {
        name: "appId",
        label: "Application Ref. No",
        options: {
            filter: true,
        },
    },
    {
        name: "feeType",
        label: "Fee Type",
        options: {
            filter: true,
        },
    },
    {
        name: "paymentAmount",
        label: "Payment Amount",
        options: {
            filter: true,
        },
    },

];


const PilotOrderDetails = ({
    handleSubmit,
    data,
    inputData,
    handleInputChange,
    handleInputChangeSwitch,
    handleValidate,
    viewType,
    errors,
    isSubmitting }) => {

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


                    {/* {(viewType === 'view') ? null : <C1FormButtons showSubmit="true" showCancel="true" />} */}


                    <Grid container spacing={3} alignItems="center">
                        <Grid container item direction="column">
                            <Grid item xs={12}>
                                <C1DataTable url="/api/process/all"
                                    columns={columns}
                                    title="Payment Details"
                                    defaultOrder="paymentRefNo"
                                    showToolbar={false}
                                    dbName={inputData}
                                    showAdd={false}></C1DataTable>

                            </Grid>
                        </Grid>
                    </Grid>
                    <br />

                    <div style={{ display: "flex", justifyContent: 'flex-end', fontWeight: 'bold' }}>
                        Total Amount: $300
                    </div>

                    <div style={{ display: "flex", justifyContent: 'space-between', fontWeight: 'bold' }}>
                        <text >Paid Amount: $200</text>
                        <text>Outstanding Balance Amount: $100</text>
                    </div>

                    <br />
                    <Divider className="mb-6" />


                    <Grid container spacing={3} >
                        <Grid item xs={4}>
                            <TextField
                                fullWidth
                                required
                                size="medium"
                                margin="normal"
                                disabled={isDisabled}
                                label="Payment Bank"
                                name="paymentBank"
                                variant="outlined"
                                onChange={handleInputChange}
                                value={inputData.paymentBank}
                                InputLabelProps={{
                                    shrink: true,
                                }}
                                select
                                {...props}
                            >
                                <MenuItem value='' key=''>  </MenuItem>

                                <MenuItem value="HSBC" key="HSBC"> HSBC </MenuItem>
                                <MenuItem value="CIMB" key="CIMB"> CIMB </MenuItem>

                            </TextField>
                        </Grid>


                        <Grid item xs={4}>

                            <C1InputField
                                label="Payment Date"
                                name="paymentDate"
                                required
                                onChange={handleInputChange}
                                value={inputData.paymentDate}
                                error={errors && errors.paymentDate ? true : false}
                                helperText={errors && errors.paymentDate ? errors.paymentDate : null}
                            />

                        </Grid>

                        <Grid item xs={4}>

                            <C1InputField
                                label="Receipt Upload"
                                name="receiptUpload"
                                type="file"
                                required
                                onChange={handleInputChange}
                                value={inputData.receiptUpload}
                                error={errors && errors.receiptUpload ? true : false}
                                helperText={errors && errors.receiptUpload ? errors.receiptUpload : null}
                            />
                        </Grid>

                        <Grid item xs={4}>

                            <C1InputField
                                label="Bank Transaction No"
                                name="bankTransNo"
                                required
                                onChange={handleInputChange}
                                value={inputData.bankTransNo}
                                error={errors && errors.bankTransNo ? true : false}
                                helperText={errors && errors.bankTransNo ? errors.bankTransNo : null}
                            />

                        </Grid>

                        <Grid item xs={4}>

                            <C1InputField
                                label="Payment Advice Date"
                                name="paymentAdviceDate"
                                required
                                onChange={handleInputChange}
                                value={inputData.paymentAdviceDate}
                                error={errors && errors.paymentAdviceDate ? true : false}
                                helperText={errors && errors.paymentAdviceDate ? errors.paymentAdviceDate : null}
                            />

                        </Grid>

                        <Grid item xs={12}>

                            <C1InputField
                                label="Remarks"
                                name="remarks"
                                required
                                onChange={handleInputChange}
                                value={inputData.remarks}
                                error={errors && errors.remarks ? true : false}
                                helperText={errors && errors.remarks ? errors.remarks : null}
                            />

                        </Grid>

                    </Grid>
                </Form>
            )}
        </Formik>
    );
};

export default PilotOrderDetails;