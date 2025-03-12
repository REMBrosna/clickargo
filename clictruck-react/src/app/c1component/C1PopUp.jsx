import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import Tooltip from "@material-ui/core/Tooltip";
import Typography from "@material-ui/core/Typography";
import CloseIcon from '@material-ui/icons/Close';
import PropTypes from 'prop-types';
import React from "react";

import { dialogStyles } from "app/c1utils/styles";

/**
 * Popup container.
 */
const C1PopUp = ({
    title,
    children,
    actionsEl,
    openPopUp,
    setOpenPopUp,
    maxWidth,
    scroll = 'paper'
}) => {


    const classes = dialogStyles();

    return (
        <Dialog open={openPopUp || false} maxWidth={maxWidth ? maxWidth : "md"}
            classes={{ paper: classes.dialogWrapper, paperFullWidth: classes.paperFullWidth }}
            fullWidth={true}
            scroll={scroll ? scroll : 'body'}>
            <DialogTitle className={classes.dialogTitle}>
                <div>
                    <Typography variant="h6" component="div">{title}
                        <Tooltip title="Close">
                            <Button onClick={() => setOpenPopUp(false)} className={classes.dialogButtonSpace}>
                                <CloseIcon color="secondary" fontSize="large" />
                            </Button>
                        </Tooltip>

                        {actionsEl}
                    </Typography>
                </div>

            </DialogTitle>
            <DialogContent dividers classes={{ root: classes.dialogContentRoot }} style={{overflow: "auto", maxHeight: '100vh'}}>
                {children}
            </DialogContent>
        </Dialog>
    )
}


C1PopUp.propTypes = {
    title: PropTypes.string,
    children: PropTypes.object,
    openPopUp: PropTypes.bool,
    setOpenPopUp: PropTypes.func,
    actionsEl: PropTypes.element
}
export default C1PopUp;