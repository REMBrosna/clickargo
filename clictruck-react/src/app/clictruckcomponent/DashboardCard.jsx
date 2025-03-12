import { Grid, Typography } from '@material-ui/core';
import Card from '@material-ui/core/Card';
import CardActionArea from '@material-ui/core/CardActionArea';
import CardContent from '@material-ui/core/CardContent';
import grey from '@material-ui/core/colors/grey';
import { makeStyles } from '@material-ui/core/styles';
import React, { useEffect, useState } from 'react';

const useStyles = makeStyles({
    root: {
        maxHeight: 250,
        margin: '0 5px',
        height: '100%'

    },
    actionArea: {
        textAlign: 'left',
        height: '100%'

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
        height: 120
    },
    cardHeader: {
        color: '#fff',
        fontSize: '100px'
    },
    statusList: {
        fontWeight: 'bold',
        textAlign: "left",
        '&:hover': {
            backgroundColor: grey[300]
        },
    },
    subtitle: {
        textAlign: "left",
        fontWeight: 500,
        textTransform: 'capitalize'
    }
});

export default function DashboardCard({
    docObj,
    toggleEventHandler,
    handleClickStatus
}) {
    const classes = useStyles();

    const [statistics, setStatistics] = useState([])

    useEffect(() => {
        let obj = docObj?.transStatistic;
        if (obj) {
            Object.keys(obj).forEach((item) => {
                if (obj[item] < 10) {
                    obj[item] = `0${parseInt(obj[item])}`
                }
            })
            setStatistics(obj)
        }
     // eslint-disable-next-line   
    }, [])

    const toTitleCase = (str) => {
        const titleCase = str
            .replaceAll('_', ' ')
            .toLowerCase()
            .split(' ')
            .map(word => {
                return word.charAt(0).toUpperCase() + word.slice(1);
            })
            .join(' ');

        return titleCase;
    }

    return (
        <Card className={classes.root} elevation={1} variant="outlined">
            <CardActionArea onClick={() => toggleEventHandler(docObj.id)} className={classes.actionArea} style={docObj?.state === 'active' ? { backgroundColor: "#13B1ED" } : { backgroundColor: "#B4BABD" }}>
                <CardContent onClick={(e) => handleClickStatus(e, docObj.id, docObj.title)}>
                    <Grid container spacing={2} direction='row' justifyContent='space-between' alignItems='stretch' wrap='nowrap' style={{ color: '#fff' }}>
                        <Grid item style={{ marginLeft: '10px' }}>
                            <Grid container direction="column" justifyContent="space-between" alignItems="flex-start" style={{ height: '100%' }}>
                                <Grid item>
                                    <Typography variant='h5' style={{ textAlign: "left", fontWeight: 600 }}>{docObj.title}</Typography>
                                </Grid>
                                <Grid item>
                                    <Typography variant='h4' style={{ textAlign: "left", marginBottom: 5, fontWeight: 600 }}>{Object.values(statistics).join(' / ')}</Typography>
                                    <Typography variant='h6' className={classes.subtitle}>{toTitleCase(Object.keys(statistics).join(' / '))}</Typography>
                                </Grid>
                            </Grid>
                        </Grid>
                        <Grid item>
                            <img src={`/assets/images/illustrations/${docObj.img}`} className={classes.media} />
                        </Grid>
                    </Grid>
                </CardContent>
            </CardActionArea>
        </Card >
    );
}