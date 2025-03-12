import React, { useState, useEffect } from "react";
import {
    Grid,
    TextField,
    Paper,
    Snackbar,
    Tabs,
    Tab,
    Divider
} from "@material-ui/core";


export const renderVoyageDetails = ({ inputData,
    handleInputChange,
    handleValidate,
    viewType, props, isDisabled }) => {

    return (
        <div>
            <Grid container alignItems="center" xs={12}>
                <Grid container item direction="column" >
                    <Grid item xs={6}>
                        <TextField
                            className="m-2"
                            label="Voyage Number"
                            name="ctyCode"
                            fullWidth
                            required
                            size="small"
                            disabled={(viewType === 'edit' || viewType === 'view') ? true : false}
                            variant="outlined"
                            onChange={handleInputChange}
                            value={inputData.ctyCode}
                            InputLabelProps={{
                                shrink: true,
                            }}
                            error={props.errors.ctyCode ? true : false}
                            helperText={props.errors.ctyCode ? props.errors.ctyCode : null}
                        />
                    </Grid>
                    <Grid item xs={6}>
                        <TextField
                            fullWidth
                            required
                            size="small"
                            className="m-2"
                            disabled={isDisabled}
                            label="Port Of Arrival"
                            name="ctyDescription"
                            variant="outlined"
                            onChange={handleInputChange}
                            value={inputData.ctyDescription}
                            InputLabelProps={{
                                shrink: true,
                            }}
                            error={props.errors.ctyDescription ? true : false}
                            helperText={props.errors.ctyDescription ? props.errors.ctyDescription : null}
                        />
                    </Grid>

                </Grid>
                <Grid container item direction="column" >
                    <Grid item xs={6}>
                        <TextField
                            className="m-2"
                            label="Name Of Master"
                            name="ctyCode"
                            fullWidth
                            required
                            size="small"
                            disabled={(viewType === 'edit' || viewType === 'view') ? true : false}
                            variant="outlined"
                            onChange={handleInputChange}
                            value={inputData.ctyCode}
                            InputLabelProps={{
                                shrink: true,
                            }}
                            error={props.errors.ctyCode ? true : false}
                            helperText={props.errors.ctyCode ? props.errors.ctyCode : null}
                        />
                    </Grid>
                    <Grid item xs={6}>
                        <TextField
                            fullWidth
                            required
                            size="small"
                            className="m-2"
                            disabled={isDisabled}
                            label="Date Of Arrival"
                            name="ctyDescription"
                            variant="outlined"
                            onChange={handleInputChange}
                            value={inputData.ctyDescription}
                            InputLabelProps={{
                                shrink: true,
                            }}
                            error={props.errors.ctyDescription ? true : false}
                            helperText={props.errors.ctyDescription ? props.errors.ctyDescription : null}
                        />
                    </Grid>

                </Grid>
            </Grid>

        </div>
    )

}

const renderShipDetails = (tabs) => {

    return (
        <div>
            <h2>ship</h2>
        </div>
    )

}

const HeaderFormTab = ({
    handleSubmit,
    data,
    inputData,
    handleInputChange,
    handleValidate,
    viewType,
    subTabs,
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

    const [tabIndex, setTabIndex] = useState(0);
    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };

    return (
        <Formik
            initialValues={{ ...data }}
            onSubmit={(values, isSubmitting) => handleSubmit(values, isSubmitting)}
            enableReinitialize={true}
            values={{ ...data }}
            validate={handleValidate}
        >
            {(props) => (
                <div>
                    <Tabs
                        className="mt-3"
                        value={tabIndex}
                        onChange={handleTabChange}
                        indicatorColor="primary"
                        textColor="primary"
                    >
                        {subTabs.map((item, ind) => (
                            <Tab className="capitalize" value={ind} label={item.text} key={ind} icon={item.icon} />
                        ))}
                    </Tabs>
                    <Divider className="mb-6" />
                    <Grid container spacing={12} alignItems="center">

                        {tabIndex === 0 && renderVoyageDetails({ inputData, handleInputChange, handleValidate, viewType, props, isDisabled })}
                        {tabIndex === 1 && renderShipDetails({ inputData })}

                    </Grid>
                </div>
            )}
        </Formik>
    );
};

export default HeaderFormTab;