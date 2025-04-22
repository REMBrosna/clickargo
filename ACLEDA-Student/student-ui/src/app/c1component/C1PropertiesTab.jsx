import React from "react";
import { useTranslation } from "react-i18next";
import Grid from "@material-ui/core/Grid";
import C1GridContainer from "app/c1component/C1TabContainer";
import C1InputField from "./C1InputField";
import C1DateField from "./C1DateField";

const C1PropertiesTab = ({ dtCreated, usrCreated, dtLupd, usrLupd, handleInputChange }) => {

    const { t } = useTranslation(["common"]);

    return (
        <C1GridContainer>
            <Grid item xs={3} lg={3} md={2} sm={2}>
                <C1DateField
                    label={t("properties.dtCreated")}
                    value={dtCreated || null}
                    onChange={handleInputChange}
                    disabled />
            </Grid>
            <Grid item xs={3} lg={3} md={2} sm={2}>
                <C1InputField
                    label={t("properties.usrCreated")}
                    value={usrCreated || ""}
                    onChange={handleInputChange}
                    disabled />
            </Grid>
            <Grid item xs={3} lg={3} md={2} sm={2}>
                <C1DateField
                    label={t("properties.dtLupd")}
                    value={dtLupd || null}
                    onChange={handleInputChange}
                    disabled />
            </Grid>
            <Grid item xs={3} lg={3} md={2} sm={2}>
                <C1InputField
                    label={t("properties.usrLupd")}
                    value={usrLupd || ""}
                    onChange={handleInputChange}
                    disabled />

            </Grid>
        </C1GridContainer>
    );
}

export default C1PropertiesTab;