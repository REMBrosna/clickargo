import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { withStyles } from "@material-ui/core/styles";
import { isEmpty } from "app/c1utils/utility";
import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import C1InputField from "app/c1component/C1InputField";
import MuiDialogTitle from "@material-ui/core/DialogTitle";
import MuiDialogContent from "@material-ui/core/DialogContent";
import MuiDialogActions from "@material-ui/core/DialogActions";
import IconButton from "@material-ui/core/IconButton";
import CloseIcon from "@material-ui/icons/Close";
import Typography from "@material-ui/core/Typography";
import { Grid } from "@material-ui/core";
import ConfirmationDialog from "matx/components/ConfirmationDialog";

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
        padding: theme.spacing(1),
    },
}))(MuiDialogActions);

export default function C1QueryRemarkDialog({ isOpen, title, handleClose, handleSave }) {
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");
    const { t } = useTranslation(["common", "buttons"]);

    //confirmation when saving
    const [openRemarksConfirm, setOpenRemarksConfirm] = useState(false);
    const [openQueryConfirm, setOpenQueryConfirm] = useState(false);

    const handleInputMessageChange = (e) => {
        setMessage(e.target.value);
    };

    const handleLocalClose = () => {
        handleClose();
        setMessage("");
        setError("");
    }

    const handleClickSaveBtn = (e) => {
        if (!isEmpty(handleDialogValidate())) {
            return;
        }

        if (t("common:queries.altTitle") === title)
            setOpenQueryConfirm(true);
        else
            setOpenRemarksConfirm(true);

        //handleSave({ message });
        //setMessage("");//removed to retain value when confirmation popup appears from C1FormButtonsQueryRemark.jsx
    };

    const handleLocalSave = () => {
        if (t("common:queries.altTitle") === title)
            setOpenQueryConfirm(false);
        else
            setOpenRemarksConfirm(false);
        handleSave({ message });
        setMessage("");
    }



    const handleDialogValidate = () => {
        let error = {};

        if (/^\s*$/.test(message)) {
            error.message = t("common:validationMsgs.required");
        }

        if(message?.length > 10000){
            error.message = t("common:validationMsgs.overLength");
        }
        setError(error);
        return error;
    };

    return (
        <React.Fragment>
            <Dialog
                onClose={handleLocalClose}
                aria-labelledby="customized-dialog-title"
                open={isOpen}
                modal="true"
                fullWidth={true}
                maxWidth={"sm"} >
                <DialogTitle id="customized-dialog-title" onClose={handleLocalClose}>
                    {title}
                </DialogTitle>
                <DialogContent dividers>
                    <C1InputField
                        label={title}
                        multiline
                        rows={3}
                        name="message"
                        required={true}
                        disabled={false}
                        onChange={handleInputMessageChange}
                        value={message}
                        error={error && error.message ? true : false}
                        helperText={error && error.message} />
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
                                onClick={(e) => handleClickSaveBtn(e)} >
                                {t("buttons:save")}
                            </Button>
                        </Grid>
                    </Grid>
                </DialogActions>
            </Dialog>

            <ConfirmationDialog
                open={openQueryConfirm}
                onConfirmDialogClose={() => setOpenQueryConfirm(false)}
                text={t("common:queries.msgs.confirm")}
                title={t("common:confirmMsgs.confirm.title")}
                onYesClick={handleLocalSave} />

            <ConfirmationDialog
                open={openRemarksConfirm}
                onConfirmDialogClose={() => setOpenRemarksConfirm(false)}
                text={t("common:remarks.msgs.confirm")}
                title={t("common:confirmMsgs.confirm.title")}
                onYesClick={handleLocalSave} />

        </React.Fragment>
    );
}
