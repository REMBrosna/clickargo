import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardActionArea from '@material-ui/core/CardActionArea';
import CardContent from '@material-ui/core/CardContent';
import CardMedia from '@material-ui/core/CardMedia';
import CardHeader from '@material-ui/core/CardHeader';
import grey from '@material-ui/core/colors/grey';
import { convertKeyToText } from "./ShippingLineDocumentsWorkflowUtil";
import { useTranslation } from "react-i18next";
import { Table, TableBody, TableRow, TableCell } from '@material-ui/core';

const useStyles = makeStyles({
    root: {
        //maxWidth: 250,
        //padding: "5px 5px",
        margin: '0 5px'
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
        height: 140,
        width: 150,
        padding: '5px 5px',
        margin: 'auto'
    },
    cardHeader: {
        color: '#1a90ff',
        fontSize: '100px'
    },
    statusList: {
        fontWeight: 'bold',
        '&:hover': {
            backgroundColor: grey[300]
        },
    },

});


export default function SLDashboardCard({
    docObj,
    toggleEventHandler,
    handleClickStatus
}) {
    const classes = useStyles();
    const { t } = useTranslation(["workflow"]);
    return (
        <Card className={classes.root} elevation={0} variant="outlined">
            <CardActionArea onClick={() => toggleEventHandler(docObj.id)} className={classes.actionArea} style={docObj?.state === 'active' ? { backgroundColor: grey[100] } : null}>
                <CardHeader
                    title={docObj.title} className={classes.cardHeader} titleTypographyProps={{ variant: "subtitle2", noWrap: true }} />
                <CardMedia className={classes.media}
                    component="img"
                    alt={docObj.title}
                    image={`/assets/images/illustrations/${docObj.img}`}
                    title={docObj.title}
                />
                <CardContent>
                    {docObj.transStatistic && <Table>
                        {docObj.transStatistic && Object.keys(docObj.transStatistic).map(function (key) {
                            return <TableBody key={key}>
                                <TableRow className={classes.tableBody} onClick={(e) => handleClickStatus(e, docObj.id, key)}>
                                    <TableCell style={{ textAlign: "right", width: "80%", padding: 0, border: 0 }}><span className={classes.statusList}>{key + ":"} </span></TableCell>
                                    <TableCell style={{ textAlign: "right", width: "20%", padding: 0, border: 0 }}><span className={classes.statusList}>{docObj.transStatistic[key] < 10 ? "0" + docObj.transStatistic[key] : docObj.transStatistic[key]} </span></TableCell>
                                </TableRow>
                            </TableBody>;

                        })}
                    </Table>}
                </CardContent>
            </CardActionArea>
        </Card >
    );
}