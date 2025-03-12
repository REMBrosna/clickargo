import React, { useState, useEffect } from "react";
import {
    Grid,
    Tabs,
    Tab,
    Divider
} from "@material-ui/core";

import C1AuditTab from "app/c1component/C1AuditTab";
import SupportingDocs from "./SharedSubMenuComponents/SupportingDocsEP";

const renderSupportingDocs = (tabs) => {

    return (
        <div>
            <h2>SupportingDocs</h2>
        </div>
    )

}

const renderAudits = (tabs) => {

    return (
        <div>
            <h2>Audits</h2>
        </div>
    )

}

const AttachDocs_AuditTab = ({
    handleSubmit,
    data,
    inputData,
    handleInputChange,
    handleValidate,
    viewType,
    subTabs,
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

    const [tabIndex, setTabIndex] = useState(0);
    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };
    //let { supportingDocs } = inputData;
    return (

        <div>
            <Tabs
                className="mt-4"
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
            <Grid container spacing={3} alignItems="center">
                <Grid container item direction="column">
                    <Grid item xs={12}>

                        {tabIndex === 0 && <SupportingDocs handleSubmit={handleSubmit}
                            data={data} inputData={inputData}
                            handleInputChange={handleInputChange}
                            handleValidate={handleValidate}
                            viewType={viewType}
                            isSubmitting={isSubmitting}
                            props={props} />}
                        {tabIndex === 1 && <C1AuditTab appStatus={inputData.status} />}

                    </Grid>
                </Grid>
            </Grid>
        </div>

    );
};

export default AttachDocs_AuditTab;