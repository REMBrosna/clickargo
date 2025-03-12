import { Card, CardActionArea, CardHeader } from '@material-ui/core';
import CardContent from '@material-ui/core/CardContent';
import CardMedia from '@material-ui/core/CardMedia';
import FormControlLabel from "@material-ui/core/FormControlLabel";
import FormGroup from "@material-ui/core/FormGroup";
import Grid from "@material-ui/core/Grid";
import { makeStyles } from '@material-ui/core/styles';
import Switch from '@material-ui/core/Switch';
import React from "react";

import C1Information from 'app/c1component/C1Information';
import { useStyles } from "app/c1utils/styles";

const localStyles = makeStyles({
    root: {
        maxWidth: 250,
        //padding: "5px 5px",
        margin: '5px 5px'
    },
    actionArea: {
        textAlign: 'center',
    },
    progressLabel: {
        position: "absolute",
        width: "100%",
        height: "100%",
        zIndex: 1,
        maxHeight: "20px", // borderlinearprogress root.height
        textAlign: "center",
        display: "flex",
        alignItems: "center",
        "& span": {
            width: "100%"
        }
    },
    media: {
        height: 200,
        padding: '10px 10px',
        margin: 'auto'
    },
    cardHeader: {
        color: '#1a90ff',
        fontSize: '100px'
    },
    statusList: {
        fontWeight: 'bold',
        // '&:hover': {
        //     backgroundColor: grey[300]
        // },
    },
});

const Services = () => {

    const classes = useStyles();
    const localClasses = localStyles();
    const serviceMaps = [
        { id: "do", name: "ClicDO", img: "clicDO.png" },
        { id: "declare", name: "ClickDeclare", img: "clicDeclare.png" },
        { id: "truck", name: "ClicTruck", img: "clicTruck.png" },
        { id: "gatePass", name: "ClicGatePass", img: "clicGatePass.png" },
        { id: "depot", name: "ClickDepot", img: "clicDepo.png" },
    ]

    return (
        <React.Fragment>
            <Grid container justifyContent="center" spacing={3} className={classes.gridContainer}>
                <Grid item xs={12} >
                    <Grid container alignItems="center" spacing={3} >
                        <Grid item lg={12} md={12} xs={12}>
                            <Grid container spacing={3} alignItems="center" justify="center" style={{ marginLeft: 'auto' }}>
                                {serviceMaps && serviceMaps.map((svc, idx) => {
                                    let displayEl = null;
                                    if (svc && svc.id !== '') {
                                        displayEl =
                                            <Card elevation={0}>
                                                <CardActionArea className={localClasses.actionArea}>
                                                    <CardHeader
                                                        title="" className={localClasses.cardHeader} titleTypographyProps={{ variant: "subtitle2", noWrap: true }} />
                                                    <CardMedia
                                                        className={localClasses.media}
                                                        component="img"
                                                        alt={""}
                                                        image={`/assets/images/services/` + svc.img}
                                                        title={""}>
                                                    </CardMedia>
                                                    <CardContent>
                                                        <FormGroup>
                                                            <FormControlLabel style={{ minWidth: 0 }} labelPlacement="start" control={<Switch />} label={svc.name} />
                                                        </FormGroup>
                                                    </CardContent>
                                                </CardActionArea>

                                            </Card>;
                                    }
                                    return <Grid item key={idx}>
                                        {displayEl}
                                    </Grid>;
                                })}
                            </Grid>
                            <Grid item lg={12} md={12} xs={12} >
                                <C1Information information="serviceDetails" />
                            </Grid>
                        </Grid>
                    </Grid>
                </Grid>
            </Grid>
        </React.Fragment >

    );
};

export default Services;