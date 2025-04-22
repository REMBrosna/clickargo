import { Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from "@material-ui/core";
import React from "react";
import { useTranslation } from "react-i18next";

const C1ValidationDialog = ({ isOpen, alertMsg, closeHandler }) => {
    const { t } = useTranslation(["common"]);
    return <Dialog
        open={isOpen}
        onClose={closeHandler}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"            >
        <DialogTitle id="alert-dialog-title">
            {t("common:validationAlerts.title")}
        </DialogTitle>
        <DialogContent>
            <DialogContentText id="alert-dialog-description" color="inherit">
                {alertMsg}
            </DialogContentText>
        </DialogContent>
        <DialogActions>
            <Button onClick={closeHandler} color="primary" autoFocus>
                Ok
            </Button>
        </DialogActions>
    </Dialog>
}

export default C1ValidationDialog;