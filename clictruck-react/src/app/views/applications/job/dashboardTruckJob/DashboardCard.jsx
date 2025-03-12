import { Table, TableBody, TableCell, TableRow, Typography } from '@material-ui/core';
import Card from '@material-ui/core/Card';
import CardActionArea from '@material-ui/core/CardActionArea';
import CardContent from '@material-ui/core/CardContent';
import CardHeader from '@material-ui/core/CardHeader';
import CardMedia from '@material-ui/core/CardMedia';
import grey from '@material-ui/core/colors/grey';
import { makeStyles } from '@material-ui/core/styles';
import React from 'react';
import { useTranslation } from "react-i18next";

const useStyles = makeStyles({
    root: {
        // maxWidth: 300,
        //padding: "5px 5px",
        maxHeight: 250,
        margin: '0 5px',

    },
    actionArea: {
        textAlign: 'left',

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
        height: 140,
        width: 150,
        //padding: '5px 5px',
        // margin: 'auto'
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

});

/**
 * @deprecated use app/clictruckcomponent/DashboardCard
 * */
export default function DashboardCard({
    docObj,
    toggleEventHandler,
    handleClickStatus
}) {
    const classes = useStyles();
    const { t } = useTranslation(["workflow"]);

    const convert = () => {
        return Object.values(docObj.transStatistic).map(e => {
            return e < 10 ? "0" + e : e;
        })
    }
    return (
        <Card className={classes.root} elevation={1} variant="outlined">
            <CardActionArea onClick={() => toggleEventHandler(docObj.id)} className={classes.actionArea} style={docObj?.state === 'active' ? { backgroundColor: "#13B1ED" } : { backgroundColor: "#B4BABD" }}>
                <CardHeader
                    title={docObj.title} className={classes.cardHeader} titleTypographyProps={{ variant: "h4", noWrap: true }} />
                {/* <CardMedia className={classes.media}
                    component="img"
                    alt={docObj.title}
                    image={`/assets/images/illustrations/${docObj.img}`}
                    title={docObj.title} /> */}
                <CardContent>
                    {docObj.transStatistic &&
                        <Table>
                            <TableBody>
                                <TableRow onClick={(e) => handleClickStatus(e, docObj.id, docObj.title)} style={{ border: 0, borderBottom: 0 }}>
                                    <TableCell style={{ color: '#fff', borderBottom: 0 }}>
                                        <Typography variant='h2' style={{ fontWeight: "bold", textAlign: "left" }}>
                                            {convert().join('/')}
                                        </Typography>
                                        <Typography variant='h5'>{Object.keys(docObj.transStatistic).join('/')} </Typography>
                                    </TableCell>

                                    <TableCell style={{ borderBottom: 0 }}>
                                        <img src={`/assets/images/illustrations/${docObj.img}`} className={classes.media} />
                                    </TableCell>

                                </TableRow>
                            </TableBody>
                        </Table>
                    }
                </CardContent>
            </CardActionArea>
        </Card >
    );
}