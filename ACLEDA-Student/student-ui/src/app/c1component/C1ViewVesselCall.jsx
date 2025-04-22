import React, { useState } from "react";
import PropTypes from 'prop-types'
import IconButton from '@material-ui/core/IconButton';

import Tooltip from '@material-ui/core/Tooltip';
import { Snackbar } from "@material-ui/core";
import Link from "@material-ui/icons/Link";
import C1Alert from "./C1Alert";
import C1FullPopUp from "app/c1component/C1FullPopUp";
import Clearance from "app/views/applications/vesselCall/tabs/Clearance";
import { useTranslation } from "react-i18next";

/**
 * @param appType - appType for upload/download in UPPERCASE. For example: VP, VC, DOS
 * @param disabled - boolean value to flag if button is disabled or not
 */
const C1ViewVesselCall = ({ inputData, viewType, disabled }) => {

    const { t } = useTranslation(["buttons", "vc"]);

    const [snackBarState, setSnackBarState] = useState({ open: false, vertical: 'top', horizontal: 'center', msg: '', severity: 'success' });
    const [openVesselCallPopUp, setOpenVesselCallPopUp] = useState(false);


    const handleViewVesselCall = (e) => {
        setOpenVesselCallPopUp(true);
    }



    const handleClose = () => {
        setSnackBarState({ ...snackBarState, open: false });
    };

    const anchorOriginV = snackBarState.vertical;
    const anchorOriginH = snackBarState.horizontal;
    const snackBar = <Snackbar
        anchorOrigin={{ vertical: anchorOriginV, horizontal: anchorOriginH }}
        open={snackBarState.open}
        onClose={handleClose}
        autoHideDuration={3000}
        key={anchorOriginV + anchorOriginH
        }>
        <C1Alert onClose={handleClose} severity={snackBarState.severity}>
            {snackBarState.msg}
        </C1Alert>
    </Snackbar>;

    //Application template download
    let viewVesselCall = <Tooltip title={t("buttons:viewVc")}>
        <IconButton aria-label="Vessel Call" type="button" color="primary" disabled={disabled}
            onClick={() => handleViewVesselCall()} >
            {/* onClick={(e) => { props.showViewPopUp && props.showViewPopUp.handleClick(e); }}> */}
            <Link />
        </IconButton>
    </Tooltip>;


    return <React.Fragment>
        {viewVesselCall}
        {snackBar}
        <C1FullPopUp
            title={t("vc:app.type")}
            openPopUp={openVesselCallPopUp}
            setOpenPopUp={setOpenVesselCallPopUp}
        >
            {/* <UCRFormDetails appId={appId} appType="dos" /> */}
            <Clearance inputData={inputData} viewOnly={true} />
        </C1FullPopUp>
    </React.Fragment>
}

C1ViewVesselCall.propTypes = {
    appType: PropTypes.string,
    disabled: PropTypes.bool,
}

C1ViewVesselCall.defaultProps = {
    disabled: false
}

export default C1ViewVesselCall;