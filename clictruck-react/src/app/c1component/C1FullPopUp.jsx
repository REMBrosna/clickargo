import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import Slide from '@material-ui/core/Slide';
import { useTranslation } from "react-i18next";

const useStyles = makeStyles((theme) => ({
    appBar: {
        position: 'relative',
    },
    title: {
        marginLeft: theme.spacing(2),
        flex: 1,
    },
    topPadding: {
        paddingTop: '100px',
    }
}));

const Transition = React.forwardRef(function Transition(props, ref) {
    return <Slide direction="up" ref={ref} {...props} />;
});

const FullScreenDialog = (props) => {

    const { t } = useTranslation(["buttons"]);

    const { title, children, openPopUp, setOpenPopUp } = props;

    const classes = useStyles();
    return (
        <div>
            <Dialog fullScreen className={classes.topPadding} open={openPopUp} onClose={() => setOpenPopUp(false)} TransitionComponent={Transition}>
                <AppBar className={classes.appBar}>
                    <Toolbar>
                        {/* <IconButton edge="start" color="inherit" onClick={() => setOpenPopUp(false)} aria-label="close">
                            <CloseIcon />
                        </IconButton> */}
                        <Typography variant="h6" className={classes.title}>
                            {title}
                        </Typography>
                        <Button autoFocus color="inherit" onClick={() => setOpenPopUp(false)}>
                            {t("buttons:close")}
                        </Button>
                    </Toolbar>
                </AppBar>
                {children}
            </Dialog>
        </div>
    );
}

export default FullScreenDialog;
