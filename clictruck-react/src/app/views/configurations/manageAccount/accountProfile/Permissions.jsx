import React from "react";
import Grid from "@material-ui/core/Grid";
import { useStyles } from "app/c1component/C1Styles";
import FormGroup from '@material-ui/core/FormGroup';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Switch from '@material-ui/core/Switch';



import C1TabContainer from "app/c1component/C1TabContainer";
import { Typography } from "@material-ui/core";
import { isEditable } from "app/c1utils/utility";



const Permissions = ({
    inputData,
    handleCanProcessShipClearanceInputChange,
    viewType,
    locale }) => {
    const classes = useStyles();
    let isDisabled = isEditable(viewType);

    return <React.Fragment>
        <C1TabContainer>
            <Grid item lg={12} md={12} xs={12} >
                <Typography variant="subtitle2" gutterBottom component="div"><b className="m-3">{locale("register:permissions.heading")}</b> </Typography>
            </Grid>
            <Grid item lg={4} md={8} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12} >
                        <FormGroup>
                            <FormControlLabel control={<Switch checked={inputData?.pediAccnExt?.acetCanProcessShipClearance === undefined
                                || inputData?.pediAccnExt?.acetCanProcessShipClearance === 'Y'}
                                disabled={isDisabled}
                                name="acetCanProcessShipClearance"
                                onChange={handleCanProcessShipClearanceInputChange}
                            />} label={locale("register:permissions.shipClearanceProcess")} />
                        </FormGroup>
                    </Grid>
                </Grid>
            </Grid>

        </C1TabContainer>
    </React.Fragment>;

};
export default Permissions;