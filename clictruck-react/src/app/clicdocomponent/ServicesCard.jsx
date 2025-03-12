import Card from '@material-ui/core/Card';
import CardActionArea from '@material-ui/core/CardActionArea';
import CardContent from '@material-ui/core/CardContent';
import CardHeader from '@material-ui/core/CardHeader';
import CardMedia from '@material-ui/core/CardMedia';
import grey from '@material-ui/core/colors/grey';
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import React from 'react';
import { useHistory } from "react-router-dom";

import { Roles, Status } from "app/c1utils/const";

import useAuth from "../hooks/useAuth";

const useStyles = makeStyles({
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


export default function C1DocumentCard({
    docObj,
    toggleEventHandler,
    handleClickStatus
}) {
    const { user } = useAuth();
    const classes = useStyles();
    const history = useHistory();

    const routeChange = (e) => {
        console.log("USER ", user)
        let map = new Set(user.authorities.map((el) => el.authority));
        const isForwarder = map.has(Roles.FORWARDER_USER.code)
        let path = '/applications/jobs/list';
        if (isForwarder) {
            path = `/applications/services/bl`;
        }
        // let path = `/workbench`;
        history.push(path);
    }
    return (
        <Card className={classes.root} elevation={0}>
            <CardActionArea onClick={() => routeChange(docObj.id)} className={classes.actionArea}>
                <CardHeader
                    title="" className={classes.cardHeader} titleTypographyProps={{ variant: "subtitle2", noWrap: true }} />
                <CardMedia className={classes.media}
                    component="img"
                    alt={docObj.title}
                    image={`/assets/images/services/${docObj.img}`}
                    title={docObj.title} />
                <CardContent>
                    <Typography variant="subtitle2" color="textPrimary" component="p">
                        <span className={classes.statusList}>{docObj.title}</span>
                    </Typography>
                </CardContent>
            </CardActionArea>
        </Card>
    );
}