import { Grid } from "@material-ui/core";
import MenuItem from "@material-ui/core/MenuItem";
import React from "react";

import C1DateField from "app/c1component/C1DateField";
import C1InputField from "app/c1component/C1InputField";
import C1SelectField from "app/c1component/C1SelectField";
import C1TextArea from "app/c1component/C1TextArea";
import { AccountRemarksType } from "app/c1utils/const";

const AddRemarkPopup = (props) => {

    const {
        inputData,
        errors,
        handleInputChange,
        handleDateChange,
        locale
    } = props

    /** ---------------- Declare states ------------------- */



    /** --------------- Update states -------------------- */
    

    return (<React.Fragment>
        <Grid container spacing={2}>
            <Grid container item xs={12} direction='column'>
                <C1SelectField
                    label={locale("listing:remarks.rmkType")}
                    name="tckMstRemarkType.rtId"
                    required
                    value={inputData?.tckMstRemarkType.rtId ? inputData?.tckMstRemarkType.rtId : ''}
                    onChange={handleInputChange}
                    isServer={true}
                    disabled
                    optionsMenuItemArr={AccountRemarksType.map((item, ind) => (
                        <MenuItem value={item.id} key={ind}>
                            {item.desc}
                        </MenuItem>
                    ))}
                />
                <C1TextArea
                    label={locale("listing:remarks.rmk")}
                    name="arRemark"
                    textLimit={1024}
                    value={inputData?.arRemark}
                    onChange={handleInputChange}
                    required
                    error={errors && errors.arRemark ? true : false}
                    helperText={errors && errors.arRemark ? errors.arRemark : null}
                />
                <C1DateField
                    label={locale("listing:remarks.rmkDtCreate")}
                    name="atDtCreate"
                    value={inputData?.atDtCreate}
                    onChange={handleDateChange}
                    required
                    disabled
                    disablePast={true}
                    error={errors && errors.atDtCreate ? true : false}
                    helperText={errors && errors.atDtCreate ? errors.atDtCreate : null}
                />
                <C1InputField
                    label={locale("listing:remarks.rmkUidLupd")}
                    name="rmkUidLupd"
                    value={inputData?.atUidCreate ? inputData?.atUidCreate : ""}
                    onChange={handleInputChange}
                    required
                    disabled
                    error={errors && errors.atUidCreate ? true : false}
                    helperText={errors && errors.atUidCreate ? errors.atUidCreate : null}
                />
            </Grid>
        </Grid>
    </React.Fragment>);
};

export default AddRemarkPopup;


