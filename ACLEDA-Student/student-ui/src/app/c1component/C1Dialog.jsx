import React from "react";
import { withStyles } from '@material-ui/core/styles';
import { Button, Dialog, Grid, IconButton, Typography } from "@material-ui/core";
import MuiDialogTitle from '@material-ui/core/DialogTitle';
import MuiDialogContent from '@material-ui/core/DialogContent';
import MuiDialogActions from '@material-ui/core/DialogActions';
import CloseIcon from '@material-ui/icons/Close';

import PropTypes from 'prop-types';
import { useTranslation } from "react-i18next";

const styles = (theme) => ({
    root: {
        margin: 0,
        padding: theme.spacing(2),
    },
    closeButton: {
        position: 'absolute',
        right: theme.spacing(1),
        top: theme.spacing(1),
        color: theme.palette.secondary.main
    },
});

const DialogTitle = withStyles(styles)((props) => {
    const { children, classes, onClose, ...other } = props;
    return (
        <MuiDialogTitle disableTypography className={classes.root} {...other}>
            <Typography variant="h6">{children}</Typography>
            {onClose ? (
                <IconButton aria-label="close" className={classes.closeButton} onClick={onClose}>
                    <CloseIcon fontSize="large" />
                </IconButton>
            ) : null}
        </MuiDialogTitle>
    );
});

const DialogContent = withStyles((theme) => ({
    root: {
        padding: theme.spacing(2),
    },
}))(MuiDialogContent);

const DialogActions = withStyles((theme) => ({
    root: {
        margin: 0,
        padding: theme.spacing(2),
    },
}))(MuiDialogActions);

const C1Dialog = ({ title, isOpen, actionsEl, showActions = true, handleSaveEvent, handleCloseEvent, children, maxWidth, scroll }) => {
    const { t } = useTranslation(["buttons"]);
    let btnActionsEl = <Button variant="contained"
        color="primary"
        size="large"
        fullWidth
        onClick={handleSaveEvent}>{t("buttons:save")}</Button>

    if (actionsEl) {
        btnActionsEl = actionsEl;
    }

    if (!showActions)
        btnActionsEl = null;



    return (
        <React.Fragment>
            <Dialog onClose={handleCloseEvent} aria-labelledby="customized-dialog-title"
                open={isOpen}
                modal="true"
                fullWidth={true}
                maxWidth={maxWidth ? maxWidth : "sm"}
                scroll={scroll}>
                <DialogTitle id="customized-dialog-title" onClose={() => handleCloseEvent()}>
                    {title}
                </DialogTitle>
                <DialogContent dividers>
                    {children}
                </DialogContent>
                <DialogActions>
                    <Grid container alignItems="flex-end" spacing={1} direction="row"
                        justify="flex-end">
                        <Grid item lg={3} md={6} xs={12}>
                            {btnActionsEl}
                        </Grid>
                    </Grid>
                </DialogActions>
            </Dialog>
        </React.Fragment>
    );
}

C1Dialog.propTypes = {
    title: PropTypes.string,
    isOpen: PropTypes.bool,
    actionsEl: PropTypes.element,
    handleSaveEvent: PropTypes.func,
    handleCloseEvent: PropTypes.func,
    children: PropTypes.any
}

export default C1Dialog;