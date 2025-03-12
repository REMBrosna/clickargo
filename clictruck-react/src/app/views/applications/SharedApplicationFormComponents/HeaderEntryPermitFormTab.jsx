import React, { useState } from "react";
import {
    Grid,
    Tabs,
    Button,
    Tab,
    Divider
} from "@material-ui/core";

import { makeStyles } from '@material-ui/core/styles';
import C1InputField from "app/c1component/C1InputField";
import HeaderDetails from "./SharedSubMenuComponents/HeaderDetails";
import VesselDetails from "./SharedSubMenuComponents/VesselDetails";
import C1AuditTab from "app/c1component/C1AuditTab";
import SupportingDocs from "./SharedSubMenuComponents/SupportingDocsEP";
import { useStyles } from "app/c1utils/styles";

const renderShipDetails = (tabs) => {

    return (
        <div>
            <h2>ship</h2>
        </div>
    )

}


const renderRemarks = (tabs) => {

    return (
        <div>
            <Grid container spacing={3} alignItems="center">

                <Grid container item xs={12} >
                    Verifier Remarks
                </Grid>

                <Grid container item xs={4} >
                    <C1InputField
                        label="UserID & Level"
                        name=""
                        type="input"
                        disabled={true}
                        value="Verifier-1"
                    />
                </Grid>
                <Grid container item xs={4} >
                    <C1InputField
                        label="Remarks Date"
                        name=""
                        type="input"
                        disabled={true}
                        value="2020-09-09"
                    />
                </Grid>
                <Grid container item xs={4} >
                    <C1InputField
                        label="Office Code"
                        name=""
                        type="input"
                        disabled={true}
                        value="CHQ-41"
                    />
                </Grid>
                <Grid container item xs={12} >
                    <C1InputField
                        label="Remarks"
                        name=""
                        type="input"
                        disabled={false}
                        value="Remarks from Officer"
                    />
                </Grid>


                <Grid container item xs={12} >
                    Approver Remarks
                </Grid>

                <Grid container item xs={4} >
                    <C1InputField
                        label="UserID & Level"
                        name=""
                        type="input"
                        disabled={true}
                        value="Approver-1"
                    />
                </Grid>
                <Grid container item xs={4} >
                    <C1InputField
                        label="Remarks Date"
                        name=""
                        type="input"
                        disabled={true}
                        value="2020-09-09"
                    />
                </Grid>
                <Grid container item xs={4} >
                    <C1InputField
                        label="Office Code"
                        name=""
                        type="input"
                        disabled={true}
                        value="CHQ-41"
                    />
                </Grid>
                <Grid container item xs={12} >
                    <C1InputField
                        label="Remarks"
                        name=""
                        type="input"
                        disabled={false}
                        value="Remarks from Officer"
                    />
                </Grid>

                <Grid container item xs={12} >
                    <Button variant="contained" color="primary">
                        Save
                    </Button>

                </Grid>

            </Grid>
            <br />
        </div>
    )

}

const HeaderEntryPermitFormTab = ({
    handleSubmit,
    data,
    inputData,
    handleInputChange,
    handleValidate,
    viewType,
    subTabs,
    isSubmitting,
    props }) => {

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

    const [tabIndex, setTabIndex] = useState(0);
    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };
    const localClasses = buttonUseStyles();
    let { voyageDetails, shipDetails } = inputData;

    return (
        // <Formik
        //     initialValues={{ ...data }}
        //     onSubmit={(values, isSubmitting) => handleSubmit(values, isSubmitting)}
        //     enableReinitialize={true}
        //     values={{ ...data }}
        //     validate={handleValidate}
        // >
        // {(props) => (
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

            <Grid container spacing={3} alignItems="center" className={classes.gridContainer}>
                <Grid container item direction="column">
                    <Grid item xs={12}>
                        {tabIndex === 0 &&
                            <HeaderDetails handleSubmit={handleSubmit}
                                data={data} inputData={voyageDetails}
                                handleInputChange={handleInputChange}
                                handleValidate={handleValidate}
                                viewType={viewType}
                                isSubmitting={isSubmitting}
                                props={props} />
                        }
                        {tabIndex === 1 && <VesselDetails handleSubmit={handleSubmit}
                            data={data} inputData={shipDetails}
                            handleInputChange={handleInputChange}
                            handleValidate={handleValidate}
                            viewType={viewType}
                            isSubmitting={isSubmitting}
                            props={props} />}
                        {tabIndex === 2 && <SupportingDocs handleSubmit={handleSubmit}
                            data={data} inputData={inputData}
                            handleInputChange={handleInputChange}
                            handleValidate={handleValidate}
                            viewType={viewType}
                            isSubmitting={isSubmitting}
                            props={props} />}
                        {tabIndex === 3 && <C1AuditTab filterId={inputData.appId} appStatus={inputData.status} />}
                    </Grid>
                </Grid>
            </Grid>


        </div>
        // )}
        // {/* </Formik> */}
    );
};

const buttonUseStyles = makeStyles((theme) => ({
    buttonSpace: {
        float: "right"
    },
}));

export default HeaderEntryPermitFormTab;