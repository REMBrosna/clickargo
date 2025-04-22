import React from "react";
import { useStyles } from "app/c1utils/styles";
import Grid from "@material-ui/core/Grid";

const C1TabContainer = ({ children }) => {
    const classes = useStyles();
    return (
        <Grid container alignItems="flex-start" spacing={3} className={classes.gridContainer}>
            {children}
        </Grid>
    );
}

export default C1TabContainer;