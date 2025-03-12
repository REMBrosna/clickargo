import { Divider, Grid, Typography } from "@material-ui/core";
import React from "react";
import { useStyles } from "app/c1utils/styles";

/**
 * @description Component to separate fields by section
 * 
 * @param title - header title
 * @param lg - specify size of component for larger screens
 * @param md - specify size of component for medium-sized screens
 * @param xs - specify size of component for small-sized screens
 * @param icon - specify icon to be placed beside the header title
 * @param children - component inside this component
 */
const C1CategoryBlock = ({
    title,
    lg,
    md,
    xs,
    icon,
    children,
    actionEl
}) => {

    const classes = useStyles();
    return (
        <Grid item xs={xs} md={md} lg={lg}>

            {
                title &&
                <React.Fragment>
                    <Grid container alignItems="center" justifyContent={actionEl ? "space-between" : "flex-start"} style={{marginLeft: -3}}>
                        <Grid item>
                            <Grid container item alignItems="center" justifyContent="flex-start" className={icon ? "p-1" : ""}>
                                {icon}<Typography variant="h6">{title}</Typography>
                            </Grid>
                        </Grid>
                        {
                            actionEl &&
                            <Grid item>
                                {actionEl}
                            </Grid>
                        }
                    </Grid>

                    <Divider sx={{ width: '100%' }} className={classes.divider} />
                </React.Fragment>
            }
            {children}

        </Grid>
    )

}

export default C1CategoryBlock;