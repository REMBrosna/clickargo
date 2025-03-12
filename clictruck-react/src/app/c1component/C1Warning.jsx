import { Button, Dialog } from "@material-ui/core";
import React from "react";
import PropTypes from 'prop-types';

const C1Warning = ({ warningTitle = "Warning", warningMessage, handleWarningAction }) => {

    return (
        <Dialog maxWidth="xs" open={warningMessage?.open} >
            <div className="p-8 text-center w-360 mx-auto">
                <h4 className="capitalize m-0 mb-2">{warningTitle}</h4>
                {warningMessage?.msg && warningMessage?.msg !== '' && <p>{warningMessage?.msg}</p>}
                {warningMessage?.hlMsg && warningMessage?.hlMsg !== '' && <b>{warningMessage?.hlMsg}</b>}
                {warningMessage?.subMsg && warningMessage?.subMsg !== '' && <p>{warningMessage?.subMsg}</p>}
                <div className="flex justify-center pt-2 m--2">
                    <Button
                        className="m-2 rounded hover-bg-primary px-6"
                        variant="outlined"
                        color="primary"
                        onClick={(e) => handleWarningAction(e)}
                    >
                        {"OK"}
                    </Button>
                </div>
            </div>
        </Dialog>
    );
};

C1Warning.propTypes = {
    warningMessage: PropTypes.shape({
        open: PropTypes.bool,
        msg: PropTypes.string,
        hlMsg: PropTypes.string,
        subMsg: PropTypes.string
    })
}

export default C1Warning;