import React, { useEffect, useState } from "react";
import Grid from "@material-ui/core/Grid";
import { Typography } from "@material-ui/core";


const C1RealtimeDT = () => {
    const [dateState, setDateState] = useState(new Date());
    useEffect(() => {
        setInterval(() => setDateState(new Date()), 30000);
    }, []);

    return (
        <Grid container justify="flex-end">
            <Grid item xs={6}>
                <Typography variant="h5" style={{ textAlign: 'right' }}> {dateState.toLocaleString('en-US', {
                    hour: 'numeric',
                    minute: 'numeric',
                    hour12: true,
                })}
                </Typography>
                <Typography variant="h6" style={{ textAlign: 'right' }}> {dateState.toLocaleString('en-US', {
                    weekday: 'short',
                    day: '2-digit',
                    month: 'short',
                    year: 'numeric',

                })}
                </Typography>


            </Grid>
        </Grid>);
}


export default C1RealtimeDT;