import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import Typography from "@material-ui/core/Typography";
import CloseIcon from '@material-ui/icons/Close';
import PropTypes from 'prop-types';
import React from "react";

import { dialogStyles } from "app/c1utils/styles";
import C1IconButton from "../../../../../c1component/C1IconButton";
import AddCircleIcon from "@material-ui/icons/AddCircle";

/**
 * Popup container.
 */
const DoTaskDetailsPopup = ({
    title,
    children,
    openPopUp,
    setOpenPopUp,
    setAddBtnHandler,
    maxWidth
}) => {

    const classes = dialogStyles();

    return (
        <Dialog open={openPopUp || false} maxWidth={maxWidth ? maxWidth : "md"}
            classes={{ paper: classes.dialogWrapper, paperFullWidth: classes.paperFullWidth }}
            fullWidth={true} >
            <DialogTitle className={classes.dialogTitle}>
                <div>
                    <Typography variant="h6" component="div">{title}
                        <Button className={classes.dialogButtonSpace}>
                            <CloseIcon color="secondary" fontSize="large" onClick={() => setOpenPopUp(false)}></CloseIcon>
                        </Button>
                        <C1IconButton tooltip="Add" childPosition="right">
                            <AddCircleIcon color="primary" fontSize="large" onClick={setAddBtnHandler}></AddCircleIcon>
                        </C1IconButton>
                    </Typography>
                </div>

            </DialogTitle>
            <DialogContent dividers classes={{ root: classes.dialogContentRoot }}>
                {children}
            </DialogContent>
        </Dialog>
    )
}


DoTaskDetailsPopup.propTypes = {
    title: PropTypes.string,
    children: PropTypes.object,
    openPopUp: PropTypes.bool,
    setOpenPopUp: PropTypes.func,
    setAddBtnHandler: PropTypes.func
}
export default DoTaskDetailsPopup;