import Grid from "@material-ui/core/Grid";
import { makeStyles } from "@material-ui/core/styles";
import React from "react";

import C1TextArea from 'app/c1component/C1TextArea';

const useStyles = makeStyles(({ palette, ...theme }) => ({

    card: {
        width: "1200!important",
        borderRadius: 12,
        margin: "5rem",
    },

    root: {
        backgroundColor: '#3C77D0',
        borderColor: palette.divider,
        display: "table",
        height: "var(--topbar-height)",
        borderBottom: "1px solid transparent",
        paddingTop: "1rem",
        paddingBottom: "1rem",
        zIndex: 98,
        paddingLeft: "1.75rem",
        [theme.breakpoints.down("sm")]: {
            paddingLeft: "1rem",
        },
    },

    brandText: {
        color: palette.primary.contrastText,
    },

}));


const Information = ({ information }) => {

    const classes = useStyles();

    const getInformation = (info) => {
        if (info === "companyDetails" || info === "userDetails")
            return "*This administrator will be emailed with login credentials once the company account has been approved by Clickargo system administrators. If there are queries associated to the registration process, the registered administrator will be notified either via mobile or email.";
        else if (info === "serviceDetails")
            return "*Click on the toggle to indicate your company's interest in subscribing any of the Clic's services."
        else if (info === "documentDetails")
            return "*Please upload all mandatory documents in PDF format to facilitate the registration process. These documents will be reviewed by the system administrators and if there are queries, the administrator specified in the previous tab will be contacted for resolution.";
        else if (info === "onboarding")
            return "*All accounts are registered in the list above. Online registrations are automatically added into this list. Use the Add function to add a new account manually. The View function is present for ACTIVE records that are approved by the management. The Edit function is to be used for editing NEW record for submission to management for approval or to update ACTIVE record. Once ACTIVE is changed, it is be updated to UPDATED before it becomes VERIFIED and PENDING_APPROVAL."
    }

    return (
        <React.Fragment>
            <Grid item lg={12} md={12} xs={12}>
                <Grid container alignItems="flex-start" spacing={1} className={classes.gridContainer}>
                    <C1TextArea
                        label="Information"
                        name=" "
                        textLimit={512}
                        type="input"
                        disabled={true}
                        value={getInformation(information)}
                        error={" "}
                        helperText={" "}
                        inputProps={{ maxLength: 512 }}
                    />
                </Grid>
            </Grid>
        </React.Fragment >

    );
};

export default Information;