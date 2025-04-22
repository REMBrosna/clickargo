import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardActionArea from '@material-ui/core/CardActionArea';
import CardContent from '@material-ui/core/CardContent';
import CardMedia from '@material-ui/core/CardMedia';
import Typography from '@material-ui/core/Typography';
import CardHeader from '@material-ui/core/CardHeader';
import grey from '@material-ui/core/colors/grey';

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


export default function C1DocumentCard({
    docObj,
    toggleEventHandler,
    handleClickStatus
}) {
    const classes = useStyles();

    return (
        <Card className={classes.root} elevation={0}>
            <CardActionArea onClick={() => toggleEventHandler(docObj.id)} className={classes.actionArea}>
                <CardHeader
                    title={docObj.title} className={classes.cardHeader} titleTypographyProps={{ variant: "subtitle2", noWrap: true }} />
                <CardMedia className={classes.media}
                    component="img"
                    alt={docObj.title}
                    image={`/assets/images/doctypesPort/${docObj.img}`}
                    title={docObj.title}
                />
                <CardContent>

                    {docObj.transStatistic && Object.keys(docObj.transStatistic).map(function (key) {
                        return <Typography variant="body1" key={key} color="textPrimary" component="p"
                            onClick={(e) => handleClickStatus(e, docObj.id, key)}>
                            <span className={classes.statusList}>{key + " : " + docObj.transStatistic[key]} </span>
                        </Typography>
                    })}
                </CardContent>
            </CardActionArea>
        </Card>
    );
}