import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import Typography from '@material-ui/core/Typography';
import CardHeader from '@material-ui/core/CardHeader';


const useStyles = makeStyles({
    root: {
        width: '100%',
        margin: '0 auto',
    },
    // media: {
    //     height: 140,
    //     width: 143,
    //     margin: '0 auto',
    // },
    cardHeader: {
        color: '#fff',
        fontSize: '100px',
        backgroundColor: '#1976d2'
    },
});


const scrollToAnchor = (anchorName) => {
    if (anchorName) {
        let anchorElement = document.getElementById(anchorName);
        if (anchorElement) {
            console.log("anchorElement", anchorElement);
            anchorElement.scrollIntoView();
        }
    }
}

const AnnouncementCard = ({ data }) => {
    const classes = useStyles();

    let arrContents = data.canuContent && data.canuContent.includes("<br>") ? data.canuContent.split("<br>") : [data.canuContent];



    return (
        <Card className={classes.root} onClick={() => { scrollToAnchor(data.canuSubject) }} style={{ overflow: 'auto' }}>
            {/* <CardActionArea> */}
            <CardHeader
                title={data.canuSubject} className={classes.cardHeader}
                subheader={data.canuDescription}
                subheaderTypographyProps={{ variant: "subtitle2", align: 'center' }}
                titleTypographyProps={{ variant: "subtitle1", align: 'center' }} />
            {/* <CardMedia   alt={data.canuSubject}
                title={data.canuSubject} /> */}
            <CardContent component="div">
                {arrContents.map((el, idx) => (
                    <Typography key={idx} variant="body2" component="p" align="justify" paragraph gutterBottom >
                        {el}
                    </Typography>
                ))}

            </CardContent>
        </Card >
    );
}

export default AnnouncementCard;



