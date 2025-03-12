import Grid from "@material-ui/core/Grid";
import React from "react";

import C1InputField from "app/c1component/C1InputField";
import C1SelectField from "app/c1component/C1SelectField";
import { MST_CTRY_URL } from "app/c1utils/const";
import { useStyles } from "app/c1utils/styles";
import { getValue } from "app/c1utils/utility";

const UserDetail = ({
    inputData,
    handleInputChange,
    locale }) => {

    const classes = useStyles();

    return (
        <Grid container alignItems="flex-start" spacing={3} className={classes.gridContainer}>
            <Grid item lg={4} md={6} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12} >
                        <C1InputField
                            label={locale("userDetails.usrName")}
                            name="accnrAplName"
                            disabled={true}
                            required
                            onChange={handleInputChange}
                            value={getValue(inputData?.accnrAplName)} />

                        <C1InputField
                            label={locale("userDetails.usrPassNid")}
                            name="accnrAplPassNid"
                            disabled={true}
                            required
                            onChange={handleInputChange}
                            value={getValue(inputData?.accnrAplPassNid)} />

                        <C1InputField
                            label={locale("userDetails.usrTitle")}
                            name="accnrAplTitle"
                            disabled={true}
                            required
                            onChange={handleInputChange}
                            value={getValue(inputData?.accnrAplTitle)} />

                        <C1InputField
                            label={locale("userDetails.contactEmail")}
                            name="accnrAplEmail"
                            disabled={true}
                            required
                            onChange={handleInputChange}
                            value={getValue(inputData?.accnrAplEmail)} />

                    </Grid>
                </Grid>
            </Grid>

            <Grid item lg={4} md={6} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12}>
                        <C1InputField
                            label={locale("userDetails.contactTel")}
                            name="accnrAplTel"
                            disabled={true}
                            required
                            onChange={handleInputChange}
                            value={getValue(inputData?.accnrAplTel)}
                            inputProps={{
                                placeholder: locale("common:common.placeHolder.contactTel")
                            }} />

                        <C1InputField
                            label={locale("userDetails.addrLn1")}
                            name="accnrAplAddr1"
                            disabled={true}
                            required
                            onChange={handleInputChange}
                            value={getValue(inputData?.accnrAplAddr1)} />

                        <C1InputField
                            label={locale("userDetails.addrLn2")}
                            name="accnrAplAddr2"
                            disabled={true}
                            required={false}
                            onChange={handleInputChange}
                            value={getValue(inputData?.accnrAplAddr2)} />

                        <C1InputField
                            label={locale("userDetails.addrLn3")}
                            name="accnrAplAddr3"
                            disabled={true}
                            required={false}
                            onChange={handleInputChange}
                            value={getValue(inputData?.accnrAplAddr3)} />

                    </Grid>
                </Grid>
            </Grid>

            <Grid item lg={4} md={6} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12} >
                        <C1InputField
                            label={locale("userDetails.addrProv")}
                            name="accnrAplProv"
                            disabled={true}
                            required
                            onChange={handleInputChange}
                            value={getValue(inputData?.accnrAplProv)} />

                        <C1InputField
                            label={locale("userDetails.addrCity")}
                            name="accnrAplCity"
                            disabled={true}
                            required
                            onChange={handleInputChange}
                            value={getValue(inputData?.accnrAplCity)} />

                        <C1InputField
                            label={locale("userDetails.addrPcode")}
                            name="accnrAplPcode"
                            disabled={true}
                            required
                            onChange={handleInputChange}
                            value={getValue(inputData?.accnrAplPcode)} />

                        < C1SelectField
                            label={locale("userDetails.ctyCode")}
                            name="TMstCountryApl.ctyCode"
                            required
                            disabled={true}
                            onChange={handleInputChange}
                            value={getValue(inputData?.TMstCountryApl?.ctyCode)}
                            isServer={true}
                            isShowCode={true}
                            options={{
                                url: MST_CTRY_URL,
                                key: "country",
                                id: 'ctyCode',
                                desc: 'ctyDescription',
                                isCache: true
                            }} />

                    </Grid>
                </Grid>
            </Grid>
        </Grid>



    );
};
export default UserDetail;