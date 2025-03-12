import Grid from "@material-ui/core/Grid";
import React from "react";

import C1InputField from "app/c1component/C1InputField";
import C1SelectField from "app/c1component/C1SelectField";
import { MST_CTRY_URL } from "app/c1utils/const";
import { useStyles } from "app/c1utils/styles";
import { getValue } from "app/c1utils/utility";

const AdminDetails = ({
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
                            name="usrName"
                            disabled={true}
                            required
                            onChange={handleInputChange}
                            value={inputData?.usrName || ''} />

                        <C1InputField
                            label={locale("userDetails.usrPassNid")}
                            name="usrPassNid"
                            disabled={true}
                            required
                            onChange={handleInputChange}
                            value={inputData?.usrPassNid || ''} />

                        <C1InputField
                            label={locale("userDetails.usrTitle")}
                            name="usrTitle"
                            disabled={true}
                            required
                            onChange={handleInputChange}
                            value={inputData?.usrTitle || ""} />

                        <C1InputField
                            label={locale("userDetails.contactEmail")}
                            name="contactEmail"
                            disabled={true}
                            required
                            onChange={handleInputChange}
                            value={inputData?.usrContact?.contactEmail || ""} />

                    </Grid>
                </Grid>
            </Grid>

            <Grid item lg={4} md={6} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12}>
                        <C1InputField
                            label={locale("userDetails.contactTel")}
                            name="contactTel"
                            disabled={true}
                            required
                            onChange={handleInputChange}
                            value={inputData?.usrContact?.contactTel || ""}
                            inputProps={{
                                placeholder: locale("common:common.placeHolder.contactTel")
                            }} />

                        <C1InputField
                            label={locale("userDetails.addrLn1")}
                            name="addrLn1"
                            disabled={true}
                            required
                            onChange={handleInputChange}
                            value={inputData?.usrAddr?.addrLn1 || ''} />

                        <C1InputField
                            label={locale("userDetails.addrLn2")}
                            name="addrLn2"
                            disabled={true}
                            required={false}
                            onChange={handleInputChange}
                            value={inputData?.usrAddr?.addrLn2 || ''} />

                        <C1InputField
                            label={locale("userDetails.addrLn3")}
                            name="addrLn3"
                            disabled={true}
                            required={false}
                            onChange={handleInputChange}
                            value={inputData?.usrAddr?.addrLn3 || ''} />

                    </Grid>
                </Grid>
            </Grid>

            <Grid item lg={4} md={6} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12} >
                        < C1SelectField
                            label={locale("userDetails.ctyCode")}
                            name="TMstCountryApl.ctyCode"
                            required
                            disabled={true}
                            onChange={handleInputChange}
                            value={getValue(inputData?.usrAddr?.addrCtry?.ctyCode)}
                            isServer={true}
                            isShowCode={true}
                            options={{
                                url: MST_CTRY_URL,
                                key: "country",
                                id: 'ctyCode',
                                desc: 'ctyDescription',
                                isCache: true
                            }} />

                        <C1InputField
                            label={locale("userDetails.addrProv")}
                            name="addrProv"
                            disabled={true}
                            required
                            onChange={handleInputChange}
                            value={inputData?.usrAddr?.addrProv || ''} />

                        <C1InputField
                            label={locale("userDetails.addrCity")}
                            name="addrCity"
                            disabled={true}
                            required
                            onChange={handleInputChange}
                            value={inputData?.usrAddr?.addrCity || ''} />

                        <C1InputField
                            label={locale("userDetails.addrPcode")}
                            name="addrPcode"
                            disabled={true}
                            required
                            onChange={handleInputChange}
                            value={inputData?.usrAddr?.addrPcode || ''} />

                    </Grid>
                </Grid>
            </Grid>
        </Grid>



    );
};
export default AdminDetails;