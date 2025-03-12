import React from 'react';
import { Grid } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import C1InputField from "app/c1component/C1InputField";
import stampImage from "../../../../MatxLayout/Layout2/sample_stamp.jpg";
import clsx from "clsx";
import { useTranslation } from "react-i18next";
const useStyles = makeStyles({
    chooseFile: {
        margin: "2rem"
    },
    submitButton: {
        marginTop: 20
    },
    img: {
        width: "50%",
        marginTop: -90
    }
});
const CompanyStampDetails = () => {
    const classes = useStyles();
    const { t } = useTranslation(["register"]);
    return (
        <Grid container spacing={3} alignContent="flex-start" alignItems="flex-start" justify="flex-start">
            <Grid container xs={6} item direction="row" alignContent="flex-start" alignItems="flex-start" justify="flex-start">
                <Grid item xs={12}>
                    <C1InputField
                        label={t("companyStamp.btnUpload")}
                        name="signUpload"
                        type="file"
                        margin="normal"
                        size="medium"
                        disabled={true}

                    />
                    <div className="flex items-center h-full">
                        <img
                            className="h-100"
                            src={stampImage}
                            alt=""
                        />
                        <span className={clsx("font-medium text-2 mx-4", classes.brandText)}>{t("companyStamp.stampPhotoSpec1")}:<br />
                         * {t("companyStamp.stampPhotoSpec2")}<br />
                         * {t("companyStamp.stampPhotoSpec4")}
</span>
                    </div>
                    {/* <div>
                <img src={stampImage} alt="" className={classes.img}/>
            </div> */}
                </Grid>
            </Grid>
        </Grid>
    );
}

export default CompanyStampDetails;