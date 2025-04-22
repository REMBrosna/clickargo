import { blue, cyan, green, orange, red } from "@material-ui/core/colors";
import Tooltip from "@material-ui/core/Tooltip";
import CloudUpload from "@material-ui/icons/CloudUpload";
import React from "react";
import BorderColorOutlinedIcon from '@material-ui/icons/BorderColorOutlined';
import DoneAllOutlinedIcon from '@material-ui/icons/DoneAllOutlined';
import { AccountTypesColumns, RecordStatus, RegistrationStatus, Status } from "app/c1utils/const";

/**Add new applicable status. */
export function getStatusDesc(status, extra, recovery, amended) {
    let statusRecovery;
    let statusAmended;
    switch (status) {
        case Status.DRF.code:
        case Status.DRF.desc:
            statusRecovery = <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: blue[200], color: blue[800] }}>
                Draft
            </small>
            break;
        case Status.SUB.code:
        case Status.SUB.desc:
            statusRecovery = <small
                className="px-1 py-2px border-radius-4"
                style={{ backgroundColor: orange[200], color: orange[800] }}>
                Submitted {extra}
            </small>;
            break;
        case Status.APP.code:
        case Status.APP.desc:
            statusRecovery = <small
                className="px-1 py-2px border-radius-4"
                style={{ backgroundColor: green[200], color: green[800] }}>
                Approved
            </small>;
            break;
        case Status.VER.code:
        case Status.VER.desc:
            statusRecovery = <small
                className="px-1 py-2px border-radius-4"
                style={{ backgroundColor: green[200], color: green[800] }}>
                Verified
            </small>;
            break;
        case Status.ACK.code:
        case Status.ACK.desc:
            statusRecovery = <small
                className="px-1 py-2px border-radius-4"
                style={{ backgroundColor: green[200], color: green[800] }}>
                Acknowledged

            </small>;
            break;
        case Status.RET.code:
        case Status.RET.desc:
            statusRecovery = <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: cyan[200], color: cyan[800] }}>
                Returned
            </small>;
            break;
        case Status.REJ.code:
        case Status.REJ.desc:
        case "R":
            statusRecovery = <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: red[200], color: red[800] }}>
                Rejected
            </small>;
            break;
        case Status.EXP.code:
        case Status.EXP.desc:
        case "X":
            statusRecovery = <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: red[200], color: red[800] }}>
                Expired
            </small>;
            break;
        case Status.PEN.code:
        case Status.PEN.desc:
            statusRecovery = <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: cyan[200], color: cyan[800] }}>
                Pending Payment
            </small>;
            break;
        case Status.INV.code:
        case Status.INV.desc:
            statusRecovery = <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: cyan[200], color: cyan[800] }}>
                Invoice Generated
            </small>;
            break;
        case Status.AMN.code:
        case Status.AMN.desc:
            statusRecovery = <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: cyan[200], color: cyan[800] }}>
                Amended
            </small>;
            break;
        case Status.ORT.code:
        case Status.ORT.desc:
            statusRecovery = <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: cyan[200], color: cyan[800] }}>
                Officer Returned
            </small>;
            break;
        case Status.PRA.code:
        case Status.PRA.desc:
            statusRecovery = <small
                className="px-1 py-2px border-radius-4"
                style={{ backgroundColor: green[200], color: green[800] }}>
                Pre Approved
            </small>
            break;
        case Status.PAY.code:
        case Status.PAY.desc:
            statusRecovery = <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: cyan[200], color: cyan[800] }}>
                Paid
            </small>;
            break;
        case RecordStatus.ACTIVE.code:
            return (
                <small
                    className="px-1 py-2px border-radius-4"
                    style={{ backgroundColor: green[200], color: green[800] }}>
                    Active
                </small>
            );
        case RecordStatus.INACTIVE.code:
            return (
                <small
                    className="px-1 py-2px border-radius-4"
                    style={{ backgroundColor: red[200], color: red[800] }}>
                    InActive
                </small>
            );
        case RecordStatus.SUSPENDED.code:
            return (
                <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: cyan[200], color: cyan[800] }}>
                    Suspended
                </small>
            );
        case "Y":
            return (
                <small
                    className="px-1 py-2px border-radius-4"
                    style={{ backgroundColor: green[200], color: green[800] }}>
                    Yes
                </small>
            );
        case "N":
            return (
                <small
                    className="px-1 py-2px border-radius-4"
                    style={{ backgroundColor: red[200], color: red[800] }}>
                    No
                </small>
            );
        case RegistrationStatus.PENDING_ACCCN_ACTIVATION.code:
        case RegistrationStatus.PENDING_ACCCN_ACTIVATION.desc:
            return (
                <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: cyan[200], color: cyan[800] }}>
                    Pending Account Activation
                </small>
            );
        case RegistrationStatus.PENDING_APPROVAL.code:
        case RegistrationStatus.PENDING_APPROVAL.desc:
            return (
                <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: cyan[200], color: cyan[800] }}>
                    Pending Approval
                </small>
            );
        case RegistrationStatus.APPROVED.code:
        case RegistrationStatus.APPROVED.desc:
            return (
                <small
                    className="px-1 py-2px border-radius-4"
                    style={{ backgroundColor: green[200], color: green[800] }}>
                    Approved
                </small>
            );
        default:
            break;
    }

    return (
        <div>{statusRecovery}
        <small>
                {recovery && (
                    <Tooltip title={'This application under recovery mode'}>
                        <CloudUpload
                            color={"primary"}
                            style={{
                            position: "absolute",
                            marginLeft: `${amended ? "33px" :  "8px"}`,
                            marginTop: "-1px",
                            cursor: "pointer"
                        }} />
                    </Tooltip>
                )}
            {amended && (
                <Tooltip title={'This application is amended'}>
                    <DoneAllOutlinedIcon
                        fontSize={"small"}
                        color={"primary"}
                        style={{
                        position: "absolute",
                        marginLeft: "8px",
                        marginTop: "-1px",
                        cursor: "pointer"
                    }} />
                </Tooltip>
            )}
            </small>
        </div>
    );
}

export function getActiveMode(status) {
    return RecordStatus.INACTIVE.code === status || RecordStatus.SUSPENDED.code === status;
}

export function getDeActiveMode(status) {
    return RecordStatus.ACTIVE.code === status;
}

export function getEditMode(status) {
    return Status.DRF.code === status;
}

export function getAmendmentMode(status) {
    return [Status.DRF.code,Status.REJ.code].includes(status) ;
}

// for PAN, to enable Edit for all states
export function getEditModeForPAN(status) {
    return [Status.DRF.code, Status.SUB.code, Status.ACK.code].includes(status);
}

export function getAmendMode(status) {
    return [Status.RET.code, Status.AMN.code].includes(status);
}

/**Add corresponding status that allows view mode */
export function getViewMode(status) {
    return [
        Status.APP.code, Status.ACK.code,
        Status.REJ.code, Status.RET.code,
        Status.PEN.code, Status.PAY.code,
        Status.SUB.code, Status.VER.code,
        Status.ORT.code, Status.PRA.code,
        Status.EXP.code, Status.DRF.code,
        Status.AMN.code
    ].includes(status);
}

export function getPreviewMode(status) {
    return [Status.APP.code, Status.ACK.code].includes(status);
}

export function getCloneMode(status) {
    return [Status.ACK.code, Status.SUB.code, Status.APP.code, Status.VER.code, Status.AMN.code, Status.EXP.code].includes(status);
}

function updateStateHelper(state, arrSelector, newVal) {
    if (arrSelector.length > 1) {
        let field = arrSelector.shift();
        let subObj = {};

        try {
            subObj = { ...updateStateHelper(state[field], arrSelector, newVal) };
        } catch {
            subObj = { ...updateStateHelper(state, arrSelector, newVal) };
        }

        return { ...state, [field]: subObj };
    } else {
        let updatedState = {};
        updatedState[arrSelector.shift()] = newVal;
        return { ...state, ...updatedState };
    }
}

//For deep update of states
export function deepUpdateState(state, selector, newVal, autoAssign = true) {
    if (selector.indexOf(".") !== -1) {
        let sel = selector.split(".");
        let newState = updateStateHelper(state, sel, newVal);
        if (autoAssign) return Object.assign(state, newState);

        return newState;
    }

    return state;
}

////////////////////////////////////////////////////////////////////////////////

export function renderStatusCode(v) {
    switch (v) {
        case Status.DRF.code:
            return Status.DRF.desc;
        case Status.SUB.code:
            return Status.SUB.desc;
        case Status.REJ.code:
            return Status.REJ.desc;
        case Status.APP.code:
            return Status.APP.desc;
        case Status.ACK.code:
            return Status.ACK.desc;
        case Status.PEN.code:
            return Status.PEN.desc;
        case Status.VER.code:
            return Status.VER.desc;
        case Status.AMN.code:
            return Status.AMN.desc;
        case Status.EXP.code:
            return Status.EXP.desc;
        case Status.ORT.code:
            return Status.ORT.desc;
        default:
            break;
    }
}

export function renderAccountTypeCode(v) {
    switch (v) {
        case AccountTypesColumns.ACC_TYPE_SHIP_LINE.code:
            return AccountTypesColumns.ACC_TYPE_SHIP_LINE.desc;
        case AccountTypesColumns.ACC_TYPE_SHIP_AGENT.code:
            return AccountTypesColumns.ACC_TYPE_SHIP_AGENT.desc;
        default:
            break;
    }
}
