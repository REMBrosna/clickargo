import React from 'react';
import { makeStyles, withStyles } from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardActionArea from '@material-ui/core/CardActionArea';
import Grid from '@material-ui/core/Grid';
import CardContent from '@material-ui/core/CardContent';
import CardMedia from '@material-ui/core/CardMedia';
import Typography from '@material-ui/core/Typography';
import CardHeader from '@material-ui/core/CardHeader';
import LinearProgress from '@material-ui/core/LinearProgress';
import PropTypes from 'prop-types';
import AddBoxIcon from '@material-ui/icons/AddBox';
import Button from '@material-ui/core/Button';
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";

const useStyles = makeStyles({
    root: {
        //maxWidth: 250
        margin: '0 20px'
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
        width: 143,
        margin: '0 auto',
    },
    cardHeader: {
        color: '#1a90ff',
        fontSize: '100px'
    },
});


const BorderLinearProgress = withStyles((theme) => ({
    root: {
        height: 20,
        borderRadius: 5,
    },
    colorPrimary: {
        backgroundColor: theme.palette.grey[theme.palette.type === 'light' ? 300 : 700],
    },
    bar: {
        borderRadius: 5,
        backgroundColor: '#1a90ff',
    },

}))(LinearProgress);

const scrollToAnchor = (anchorName) => {
    if (anchorName) {
        let anchorElement = document.getElementById(anchorName);
        if (anchorElement) {
            console.log("anchorElement", anchorElement);
            anchorElement.scrollIntoView();
        }
    }
}

/**
 * 
 * @deprecated to be removed and replaced with module specific document card 
 * since vessel call and workbench has different display
 */
export default function C1DocumentCard({ docObj, imagePath }) {
    const classes = useStyles();
    const { t } = useTranslation(["buttons"]);
    let elementToDisplay = (
        <div>
            <div className={classes.progressLabel}>
                <span> {docObj.status}%</span>
            </div>
            <BorderLinearProgress variant="determinate" value={docObj.status} />
        </div>
    );

    if (docObj.status == 0) {
        elementToDisplay =
            <div style={{ alignItems: 'center', textAlign: 'center', alignContent: 'center' }}>
                <Link to={
                    {
                        pathname: docObj.uriPathNewApp
                    }
                } >
                    <Button
                        variant="contained"
                        color="primary"
                        size="small"
                        className={classes.button}
                        startIcon={<AddBoxIcon />}>
                        {t("buttons:newApp")}
                </Button>
                </Link>
            </div>
    }

    return (
        <Card className={classes.root} elevation={0} onClick={() => { scrollToAnchor(docObj.title) }}>
            {/* <CardActionArea> */}
            <CardHeader
                title={docObj.title} className={classes.cardHeader} titleTypographyProps={{ variant: "subtitle2", noWrap: true, align: 'center' }} />
            <CardMedia className={classes.media}
                component="img"
                alt={docObj.title}
                image={`/assets/images/doctypes/${docObj.img}`}
                title={docObj.title} />
            <CardContent>
                <Typography variant="subtitle2" style={{ color: '#1a90ff' }} align="center" component="p">
                    {docObj.statusLabel}
                </Typography>
                {elementToDisplay}
            </CardContent>
            {/* </CardActionArea> */}
        </Card>
    );
}


C1DocumentCard.propTypes = {
    docObj: PropTypes.shape({
        id: PropTypes.string,
        docType: PropTypes.string,
        img: PropTypes.string,
        status: PropTypes.number,
        statusLabel: PropTypes.string,
        title: PropTypes.string,
        img: PropTypes.string,
        uriPathNewApp: PropTypes.string

    })
}

