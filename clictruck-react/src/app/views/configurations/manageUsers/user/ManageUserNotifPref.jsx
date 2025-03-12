import React from "react";
import Grid from "@material-ui/core/Grid";
import { useStyles } from "app/c1component/C1Styles";
import FormGroup from '@material-ui/core/FormGroup';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Switch from '@material-ui/core/Switch';



import C1TabContainer from "app/c1component/C1TabContainer";
import { isEditable } from "app/c1utils/utility";
import { Typography } from "@material-ui/core";



const ManageUserNotifPref = ({
    inputData,
    handleInputChange,
    viewType,
    isSubmitting,
    locale }) => {
    const classes = useStyles();

    let isDisabled = isEditable(viewType, isSubmitting);


    return <React.Fragment>
        <C1TabContainer>
            <Grid item lg={12} md={12} xs={12} >
                <Typography variant="subtitle2" gutterBottom component="div"><b className="m-3">{locale("admin:user.details.notifPref.heading")}</b> </Typography>
            </Grid>
            <Grid item lg={4} md={8} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12} >
                        <FormGroup>
                            <FormControlLabel control={<Switch checked={inputData.email === 'Y'}
                                disabled={isDisabled}
                                name="email"
                                onChange={handleInputChange}
                            />} label={locale("admin:user.details.notifPref.email")} />
                        </FormGroup>
                    </Grid>
                </Grid>
            </Grid>
            <Grid item lg={4} md={8} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12} >
                        <FormGroup>
                            <FormControlLabel control={<Switch checked={inputData.sms === 'Y'}
                                name="sms"
                                onChange={handleInputChange}
                                disabled={isDisabled} />}
                                label={locale("admin:user.details.notifPref.sms")} />
                        </FormGroup>
                    </Grid>
                </Grid>
            </Grid>
            <Grid item lg={4} md={8} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12} >
                        <FormGroup>
                            <FormControlLabel control={<Switch checked={inputData.telegram === 'Y'}
                                name="telegram"
                                onChange={handleInputChange}
                                disabled={isDisabled} />}
                                label={locale("admin:user.details.notifPref.tlg")} />
                            {/* <FormControlLabel disabled control={<Switch />} label="Disabled" /> */}
                        </FormGroup>

                    </Grid>
                </Grid>
            </Grid>

        </C1TabContainer>
    </React.Fragment>;

};
export default ManageUserNotifPref;