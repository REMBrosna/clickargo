import { Grid } from "@material-ui/core";
import React, { useEffect } from "react";
import { getValue } from "app/c1utils/utility";
import C1FileUpload from "app/c1component/C1FileUpload";
import C1InputField from "app/c1component/C1InputField";
import C1SelectField from "app/c1component/C1SelectField";
import useHttp from "app/c1hooks/http";
import C1DateField from "app/c1component/C1DateField";
import { MST_ACCN_ATT_TYPE_OPTIONAL, MST_ACCN_ATT_TYPE_MANDATORY } from "app/c1utils/const";

const AddAccnAttPopup = (props) => {

    const {
        inputData,
        errors,
        handleInputFileChange,
        handleInputChange,
        handleDateChange,
        locale,
        enableDd
    } = props

    /** ---------------- Declare states ------------------- */
    const { isLoading, res, urlId, sendRequest } = useHttp();

    /** --------------- Update states -------------------- */
    useEffect(() => {
        if (!isLoading && res) {
            switch (urlId) {
                default: break;
            }
        }
    }, [isLoading, res, urlId]);

    return (<React.Fragment>
        <Grid container spacing={2} >
            <Grid container item xs={12} sm={6} direction="column">
                <C1SelectField
                    label={locale("listing:attachments.docType")}
                    name="tckMstAccnAttType.id.atWorkflow"
                    required
                    value={getValue(inputData?.tckMstAccnAttType?.id?.atWorkflow)}
                    onChange={handleInputChange}
                    isServer={true}
                    disabled={enableDd ? false : true}
                    options={{
                        url: enableDd ? MST_ACCN_ATT_TYPE_OPTIONAL : MST_ACCN_ATT_TYPE_MANDATORY,
                        id: 'tckMstWorkflowType.wktId',
                        desc: 'tckMstWorkflowType.wktName',
                        isCache: false
                    }}
                    error={!!errors.atId}
                    helperText={errors.atId ?? null}
                />
                <C1InputField
                    label={locale("listing:attachments.docNo")}
                    name="aatNo"
                    value={getValue(inputData?.aatNo)}
                    onChange={handleInputChange}
                    required
                    error={errors && errors.aatNo ? true : false}
                    helperText={errors && errors.aatNo ? errors.aatNo : null}
                />
            </Grid>

            <Grid container item xs={12} sm={6} direction="column">
                <Grid item xs={12}>
                    <C1DateField
                        label={locale("listing:attachments.dtValidity")}
                        name="atDtValidility"
                        value={getValue(inputData?.atDtValidility)}
                        onChange={handleDateChange}
                        required
                        disablePast={true}
                        error={errors && errors.atDtValidility ? true : false}
                        helperText={errors && errors.atDtValidility ? errors.atDtValidility : null}
                    />
                    <C1FileUpload
                        inputLabel={locale("listing:attachments.docFile")}
                        inputProps={{
                            placeholder: locale("listing:attachments.nofilechosen")
                        }}
                        value={getValue(inputData?.aatName)}
                        fileChangeHandler={handleInputFileChange}
                        label={locale("listing:attachments.browse")}
                        required
                        disabled={false}
                        errors={errors && errors.aatName ? true : false}
                        helperText={errors && errors.aatName ? errors.aatName : null}
                    />
                </Grid>
            </Grid>
        </Grid>
    </React.Fragment>);
};

export default AddAccnAttPopup;


