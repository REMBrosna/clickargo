import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import Tooltip from "@material-ui/core/Tooltip";
import Typography from "@material-ui/core/Typography";
import CloseIcon from '@material-ui/icons/Close';
import NearMeIcon from '@material-ui/icons/NearMeOutlined';
import PropTypes from 'prop-types';
import React from "react";
import { dialogStyles } from "app/c1utils/styles";
import Slide from "@material-ui/core/Slide";



/**
 * Popup container for clictruck domestic job popup with submit button
 */
const Transition = React.forwardRef(function Transition(props, ref) {
    return (
        <Slide
            direction="down"
            ref={ref}
            {...props}
            timeout={300} // Faster transition
            easing="cubic-bezier(0.25, 0.8, 0.25, 1)" // Smooth easing for fluid motion
        />
    );
});
const C1PopUp = ({
    title,
    children,
    actionsEl,
    openPopUp,
    setOpenPopUp,
    setSubmitButton,
    maxWidth,
    maxHeight,
    overflowY,
    customStyles,
    disableCloseButton = false, // Default to false, meaning the close button is enabled
}) => {

    const classes = dialogStyles();

    return (
        <Dialog
            open={openPopUp || false}
            maxWidth={maxWidth ? maxWidth : "md"}
            TransitionComponent={Transition}
            classes={{
                paper: classes.dialogWrapper,
                paperFullWidth:
                classes.paperFullWidth
            }}
            style={customStyles}
            fullWidth={true}
            onClose={() => setOpenPopUp(false)}
            disableEscapeKeyDown={false}
        >
            <DialogTitle className={classes.dialogTitle}>
                <div>
                    <Typography variant="h6" component="div">{title}
                            {!disableCloseButton && (
                                <Tooltip title="Close">
                                    <Button
                                        onClick={() => setOpenPopUp(false)} // Close dialog when clicked
                                        className={classes.dialogButtonSpace}
                                    >
                                        <CloseIcon color="secondary" fontSize="large" />
                                    </Button>
                                </Tooltip>
                            )}
                            {setSubmitButton && (
                                <Tooltip title="Submit">
                                    <Button onClick={() => setSubmitButton(false)} className={classes.dialogButtonSpace}>
                                        <NearMeIcon color="primary" fontSize="large" />
                                    </Button>
                                </Tooltip>
                            )}
                        {actionsEl}
                    </Typography>
                </div>

            </DialogTitle>
            <DialogContent
                dividers
                classes={{
                    root: { overflowY: overflowY || 'visible' }, // Defaults to 'visible' if not provided
                    scrollableContent: {
                        maxHeight: maxHeight || 'none', // Defaults to 'none' if not provided
                        overflowY: overflowY || 'auto', // Defaults to 'auto' if not provided
                    },
                }}
            >
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
    actionsEl: PropTypes.element,
    maxWidth: PropTypes.oneOfType([
        PropTypes.string,
        PropTypes.bool
    ]),
    maxHeight: PropTypes.oneOfType([
        PropTypes.string,
        PropTypes.number
    ])
}
export default C1PopUp;