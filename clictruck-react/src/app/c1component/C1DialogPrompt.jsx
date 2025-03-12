import React from "react";
import { Button, Dialog } from "@material-ui/core";
import PropTypes from 'prop-types';
import { useTranslation } from "react-i18next";


const C1DialogPrompt = ({ confirmationObj }) => {
    const { t } = useTranslation(["buttons"]);


    return (
        <React.Fragment>
            <Dialog maxWidth="xs" open={confirmationObj.openConfirmPopUp} onClose={confirmationObj.onConfirmationDialogClose}>
                <div className="p-8 text-center w-360 mx-auto">
                    <h4 className="capitalize m-0 mb-2">{confirmationObj.title}</h4>
                    <p>{confirmationObj.text}</p>
                    <div className="flex justify-center pt-2 m--2">
                        {confirmationObj.yesBtnText && <Button
                            className="m-2 rounded hover-bg-primary px-6"
                            variant="outlined"
                            color="primary"
                            disabled={false}
                            onClick={confirmationObj.onYesClick}  >
                            {t(confirmationObj.yesBtnText)}
                        </Button>}
                        {confirmationObj.noBtnText && <Button
                            className="m-2 rounded hover-bg-secondary px-6"
                            variant="outlined"
                            color="secondary"
                            disabled={false}
                            onClick={confirmationObj.onConfirmationDialogClose} >
                            {t(confirmationObj.noBtnText)}
                        </Button>}
                    </div>
                </div>
            </Dialog>
        </React.Fragment >
    );
}

C1DialogPrompt.propTypes = {
    confirmationObj: PropTypes.exact({
        openConfirmPopUp: PropTypes.bool,
        onConfirmationDialogClose: PropTypes.func,
        text: PropTypes.string,
        title: PropTypes.string,
        onYesClick: PropTypes.func,
        yesBtnText: PropTypes.string,
        noBtnText: PropTypes.string
    })
}

export default C1DialogPrompt;