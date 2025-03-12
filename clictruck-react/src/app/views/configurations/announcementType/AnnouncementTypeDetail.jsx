
import React from "react";
import {Grid} from "@material-ui/core";
import {isEditable} from "app/c1utils/utility";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";

const DocAssociateDetails = (
    {
        inputData, viewType,
        isSubmitting, locale,
        errors, handleInputChange,
        handleRadioChange
    }) => {

    let isDisabled = isEditable(viewType, isSubmitting);
    
   
    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid container spacing={3} alignItems="center">
                    <Grid item lg={4} md={6} xs={12}>
                        <Grid item xs={12}>
                            <C1InputField
                                label={locale("announcementType.list.table.headers.anypId")}
                                name="anypId"
                                disabled={viewType == 'edit'}
                                required
                                onChange={handleInputChange}
                                value={inputData.anypId}
                            />
                        </Grid>
                    </Grid>

                    <Grid item lg={4} md={6} xs={12}>
                        <Grid item xs={12}>
                            <C1InputField
                                label={locale("announcementType.list.table.headers.anypDescription")}
                                name="anypDescription"
                                disabled={isDisabled}
                                required
                                onChange={handleInputChange}
                                value={inputData.anypDescription}
                            />
                        </Grid>
                    </Grid>

                    <Grid item lg={4} md={6} xs={12}>
                        <Grid item xs={12}>
                            <C1InputField
                                label={locale("announcementType.list.table.headers.anypIconName")}
                                name="anypIconName"
                                disabled={isDisabled}
                                required
                                onChange={handleInputChange}
                                value={inputData.anypIconName}
                            />
                        </Grid>
                    </Grid>
                </Grid>
            </C1TabContainer>
        </React.Fragment>
    );
};

export default DocAssociateDetails;