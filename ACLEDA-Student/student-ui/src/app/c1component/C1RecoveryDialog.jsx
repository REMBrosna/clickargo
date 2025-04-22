import React, {useState} from "react";
import {useTranslation} from "react-i18next";
import {withStyles} from "@material-ui/core/styles";
import {isEmpty} from "app/c1utils/utility";
import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import C1InputField from "app/c1component/C1InputField";
import MuiDialogTitle from "@material-ui/core/DialogTitle";
import MuiDialogContent from "@material-ui/core/DialogContent";
import MuiDialogActions from "@material-ui/core/DialogActions";
import IconButton from "@material-ui/core/IconButton";
import CloseIcon from "@material-ui/icons/Close";
import Typography from "@material-ui/core/Typography";
import {Grid} from "@material-ui/core";
import C1DateField from "./C1DateField";

const styles = (theme) => ({
    root: {
        margin: 0,
        padding: theme.spacing(2),
    },
    closeButton: {
        position: "absolute",
        right: theme.spacing(1),
        top: theme.spacing(1),
        color: theme.palette.secondary.main,
    },
});

const DialogTitle = withStyles(styles)((props) => {
    const {children, classes, onClose, ...other} = props;
    return (
        <MuiDialogTitle disableTypography className={classes.root} {...other}>
            <Typography variant="h6">{children}</Typography>
            {onClose ? (
                <IconButton aria-label="close" className={classes.closeButton} onClick={onClose}>
                    <CloseIcon fontSize="large"/>
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
        padding: theme.spacing(1),
    },
}))(MuiDialogActions);

export default function C1RecoveryDialog({isOpen, title, handleClose, handleClickSaveBtn, inputData, handleInputChange, error, handleDateChange}) {
    const {t} = useTranslation(["common", "buttons"]);
    const handleLocalClose = () => {
        handleClose();
    }
    return (
        <React.Fragment>
            <Dialog
                onClose={handleLocalClose}
                aria-labelledby="customized-dialog-title"
                open={isOpen}
                modal="true"
                fullWidth={true}
                maxWidth={"sm"}>
                <DialogTitle id="customized-dialog-title" onClose={handleLocalClose}>
                    {title}
                </DialogTitle>
                <DialogContent dividers>
                    <C1DateField
                        label='Action Date (Manual)'
                        name="pediApps.appnRecoveryActionDate"
                        disabled={false}
                        required={true}
                        onChange={handleDateChange}
                        value={inputData?.pediApps?.appnRecoveryActionDate}
                        error={error && error.appnRecoveryActionDate ? true : false}
                        helperText={error && error.appnRecoveryActionDate}
                        disableFuture={true}
                    />
                    <C1InputField
                        label={title}
                        multiline
                        rows={3}
                        name="appnRecoveryActionRemark"
                        required={true}
                        disabled={false}
                        onChange={(e) => handleInputChange(e, "pediApps")}
                        value={inputData?.pediApps?.appnRecoveryActionRemark}
                        error={error && error.appnRecoveryRemark ? true : false}
                        helperText={error && error.appnRecoveryActionRemark}
                    />
                </DialogContent>
                <DialogActions>
                    <Grid
                        container
                        alignItems="flex-end"
                        spacing={1}
                        direction="row"
                        justify="flex-end">
                        <Grid item lg={3} md={6} xs={12}>
                            <Button
                                variant="contained"
                                color="primary"
                                size="large"
                                fullWidth
                                onClick={handleClickSaveBtn}>
                                {t("buttons:yes")}
                            </Button>
                        </Grid>
                    </Grid>
                </DialogActions>
            </Dialog>
        </React.Fragment>
    );
}
