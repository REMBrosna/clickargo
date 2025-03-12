import {
  amber,
  blue,
  cyan,
  green,
  lightGreen,
  orange,
  red,
} from "@material-ui/core/colors";
import Tooltip from "@material-ui/core/Tooltip";
import CloudUpload from "@material-ui/icons/CloudUpload";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import React from "react";

import ChipStatus from "app/atomics/atoms/ChipStatus";
import {
  AccountStatus,
  AccountTypesColumns,
  DocumentVerifyStatus,
  DoStates,
  DriverStates,
  JobStates,
  PaymentState,
  RecordStatus,
  RegistrationStatus,
  Status,
  TrackDeviceState,
} from "app/c1utils/const";
import { Typography } from "@material-ui/core";
import { RequestState } from "app/views/administrations/accnInquiry/AccnInquiryList";

/**Add new applicable status. */
export function getStatusDesc(status, extra, recovery, isChip = true) {
  let fontWeight = 800;
  let statusText = "";
  let statusColor = "";

  switch (status) {
    case TrackDeviceState.ACTIVATE.code:
      statusText = TrackDeviceState[status].desc;
      statusColor = "#00D16D";
      break;
    case TrackDeviceState.DEACTIVATE.code:
      statusText = TrackDeviceState[status].desc;
      statusColor = "#969696";
      break;
    case JobStates.NEW.code:
    case JobStates.NEW.desc:
    case RecordStatus.NEW.code:
      statusText = RecordStatus.NEW.desc;
      statusColor = "#FFC633";
      break;
    case TrackDeviceState.NEW.code:
      statusText = JobStates[status].desc;
      statusColor = "#FFC633";
      break;
    case JobStates.SUC.code:
      statusText = JobStates[status].desc;
      statusColor = "#00D16D";
      break;
    case JobStates.PAR.code:
      statusText = JobStates[status].desc;
      statusColor = "#FFC633";
      break;
    case JobStates.DLV.code:
      statusText = JobStates[status].desc;
      statusColor = "#00D16D";
      break;
    case JobStates.CAN.code:
    case JobStates.CAN.desc:
      statusText = JobStates[status].desc;
      statusColor = "#FF2E6C";
      break;
    case JobStates.CON.code:
    case JobStates.CON.desc:
      statusText = `${JobStates[status].desc} ${extra}`;
      statusColor = "#37B7FF";
      break;
    case JobStates.DEL.code:
    case JobStates.DEL.desc:
      statusText = JobStates[status].desc;
      statusColor = "#FF2E6C";
      break;
    case JobStates.DRF.code:
    case JobStates.DRF.desc:
    case Status.DRF.code:
    case Status.DRF.desc:
      statusText = JobStates[status].desc;
      statusColor = "#FFC633";
      break;
    case JobStates.SUB.code:
    case JobStates.SUB.desc:
    case Status.SUB.code:
    case Status.SUB.desc:
      statusText = JobStates[status].desc;
      statusColor = "#37B7FF";
      break;
    case JobStates.ACP.code:
    case JobStates.ACP.desc:
      statusText = JobStates[status].desc;
      statusColor = "#00D16D";
      break;
    case JobStates.DLV.code:
    case JobStates.DLV.desc:
      statusText = JobStates[status].desc;
      statusColor = "#00D16D";
      break;
    case JobStates.BILLED.code:
    case JobStates.BILLED.desc:
      statusText = JobStates[status].desc;
      statusColor = "#229881";
      break;
    case JobStates.COM.code:
    case JobStates.COM.desc:
      statusText = JobStates[status].desc;
      statusColor = "#00D16D";
      break;
    case JobStates.VER_BILL.code:
      statusText = JobStates[status].desc;
      statusColor = "#00D16D";
      break;
    case JobStates.ACK_BILL.code:
      statusText = JobStates[status].desc;
      statusColor = "#00D16D";
      break;
    case JobStates.APP_BILL.code:
      statusText = JobStates[status].desc;
      statusColor = "#00D16D";
      break;
    case JobStates.REJ_BILL.code:
      statusText = JobStates[status].desc;
      statusColor = "#FF2E6C";
      break;
    case Status.APP.code:
    case Status.APP.desc:
      statusText = Status[status].desc;
      statusColor = "#00D16D";
      break;
    case Status.VER.code:
    case Status.VER.desc:
      statusText = Status[status].desc;
      statusColor = "#00D16D";
      break;
    case Status.ACK.code:
    case Status.ACK.desc:
      statusText = "Acknowledged";
      statusColor = green[800];
      break;
    case Status.RET.code:
    case Status.RET.desc:
      statusText = "Returned";
      statusColor = cyan[800];
      break;
    case JobStates.REJ.code:
    case JobStates.REJ.desc:
    case Status.REJ.code:
    case Status.REJ.desc:
      statusText = JobStates[status].desc;
      statusColor = "#FF2E6C";
      break;
    case DoStates.EXP.code:
    case DoStates.EXP.desc:
    case Status.EXP.code:
    case Status.EXP.desc:
    case "X":
      statusText = Status[status].desc;
      statusColor = "#FF2E6C";
      break;
    case Status.PEN.code:
    case Status.PEN.desc:
      statusText = Status[status].desc;
      statusColor = "#D17100";
      break;
    case Status.INV.code:
    case Status.INV.desc:
      statusText = Status[status].desc;
      statusColor = "#00D16D";
      break;
    case Status.AMN.code:
    case Status.AMN.desc:
      statusText = "Amended";
      statusColor = cyan[800];
      break;
    case Status.ORT.code:
    case Status.ORT.desc:
      statusText = "Officer Returned";
      statusColor = cyan[800];
      break;
    case Status.PRA.code:
    case Status.PRA.desc:
      statusText = "Pre Approved";
      statusColor = green[800];
      break;
    // Added Driver States
    case DriverStates.ASSIGNED.code:
      statusText = DriverStates[status].desc;
      statusColor = "#00D16D";
      break;
    case DriverStates.MAINTENANCE.code:
      statusText = DriverStates[status].desc;
      statusColor = "#D17100";
      break;
    case DriverStates.UNASSIGNED.code:
      statusText = DriverStates[status].desc;
      statusColor = "#FFC633";
      break;
    case JobStates.PAID.code:
    case JobStates.PAID.desc:
    case DoStates.PAID.code:
    case DoStates.PAID.desc:
    case Status.PAY.code:
    case Status.PAY.desc:
    case PaymentState.PAID.code:
      statusText = JobStates.PAID.desc;
      statusColor = "#229881";
      break;
    case DoStates.CLM.code:
    case DoStates.CLM.desc:
      statusText = DoStates[status].desc;
      statusColor = "#00D16D";
      break;
    case DoStates.RDY.code:
    case DoStates.RDY.desc:
      statusText = "Ready";
      statusColor = orange[800];
      break;
    case DoStates.UNRDY.code:
    case DoStates.UNRDY.desc:
      statusText = "Unready";
      statusColor = red[800];
      break;
    case DoStates.ASG.code:
    case DoStates.ASG.desc:
      statusText = DoStates[status].desc;
      statusColor = "#00D16D";
      break;
    case JobStates.ONGOING.code:
    case JobStates.ONGOING.desc:
      statusText = JobStates[status].desc;
      statusColor = "#D17100";
      break;
    case JobStates.PMV.code:
    case JobStates.PMV.desc:
      statusText = JobStates[status].desc;
      statusColor = "#00D16D";
      break;
    case JobStates.PYG.code:
    case JobStates.PYG.desc:
    case PaymentState.PAYING.code:
      statusText = JobStates.PYG.desc;
      statusColor = "#00D16D";
      break;
    case JobStates.PROG.code:
    case JobStates.PROG.desc:
    case JobStates.ASG.code:
      statusText = "In Progress";
      statusColor = blue[800];
      break;
    case RecordStatus.ACTIVE.code:
      statusText = RecordStatus.ACTIVE.desc;
      statusColor = "#00D16D";
      break;
    // Updated css for Record Status
    case RecordStatus.INACTIVE.code:
      statusText = RecordStatus.INACTIVE.desc;
      statusColor = "#FF2E6C";
      break;
    case RecordStatus.DEACTIVE.code:
      statusText = RecordStatus.DEACTIVE.desc;
      statusColor = "#969696";
      break;
    case RecordStatus.SUSPENDED.code:
      statusText = RecordStatus.SUSPENDED.desc;
      statusColor = "#FF2E6C";
      break;
    case "Y":
      statusText = "Yes";
      statusColor = green[800];
      break;
    case "N":
      statusText = "No";
      statusColor = red[800];
      break;
    case RegistrationStatus.PENDING_ACCCN_ACTIVATION.code:
    case RegistrationStatus.PENDING_ACCCN_ACTIVATION.desc:
      statusText = RegistrationStatus.PENDING_ACCCN_ACTIVATION.desc;
      statusColor = cyan[800];
      break;
    case AccountStatus.REG_SUBMITTED.code:
    case AccountStatus.REG_SUBMITTED.desc:
      statusText = AccountStatus.REG_SUBMITTED.desc;
      statusColor = "#37B7FF";
      break;
    case AccountStatus.SUS_SUBMITTED.code:
    case AccountStatus.SUS_SUBMITTED.desc:
      statusText = AccountStatus.SUS_SUBMITTED.desc;
      statusColor = "#229881";
      break;
    case AccountStatus.TER_SUBMITTED.code:
    case AccountStatus.TER_SUBMITTED.desc:
      statusText = AccountStatus.TER_SUBMITTED.desc;
      statusColor = "#D17100";
      break;
    case AccountStatus.TER_APPROVED.code:
    case AccountStatus.TER_APPROVED.desc:
      statusText = AccountStatus.TER_APPROVED.desc;
      statusColor = "#969696";
      break;
    case AccountStatus.SUS_APPROVED.code:
    case AccountStatus.SUS_APPROVED.desc:
      statusText = AccountStatus.SUS_APPROVED.desc;
      statusColor = "#FF2E6C";
      break;
    case AccountStatus.RESUMPT_SUBMITTED.code:
    case AccountStatus.RESUMPT_SUBMITTED.desc:
      statusText = AccountStatus.RESUMPT_SUBMITTED.desc;
      statusColor = "#37B7FF";
      break;
    case DocumentVerifyStatus.NOV.name:
      statusText = "Not Verified";
      statusColor = red[800];
      break;
    case DocumentVerifyStatus.PEN.name:
      statusText = "Pending Return";
      statusColor = cyan[800];
      break;
    case DocumentVerifyStatus.VER.name:
      statusText = "Verified";
      statusColor = green[800];
      break;
    case RequestState.PENDING.desc:
      statusText = RequestState.PENDING.desc;
      statusColor = "#F3420E";
      break;
    case RequestState.INPROGRESS.desc:
      statusText = RequestState.INPROGRESS.desc;
      statusColor = "#F3C60E";
      break;
    case RequestState.COMPLETED.desc:
      statusText = RequestState.COMPLETED.desc;
      statusColor = "#00D16D";
      break;
    case "E":
      statusText = "Expired";
      statusColor = "#d91717";
      break;
    default:
      statusText = status;
      statusColor = "#969696";
      break;
  }

  return (
    <div>
      {isChip ? (
        <ChipStatus text={statusText} color={statusColor} />
      ) : (
        <small style={{ color: statusColor, fontWeight: fontWeight }}>
          {statusText}
        </small>
      )}
      <small>
        {recovery && (
          <Tooltip title={"This application under recovery mode"}>
            <CloudUpload
              style={{
                color: "blue",
                position: "absolute",
                marginLeft: "8px",
                marginTop: "-1px",
                cursor: "pointer",
              }}
            />
          </Tooltip>
        )}
      </small>
    </div>
  );
}

export function getActiveMode(status) {
  return (
    RecordStatus.INACTIVE.code === status ||
    RecordStatus.SUSPENDED.code === status
  );
}

export function getDeActiveMode(status) {
  return RecordStatus.ACTIVE.code === status;
}

export function getEditMode(status) {
  return Status.DRF.code === status;
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
    Status.APP.code,
    Status.ACK.code,
    Status.REJ.code,
    Status.RET.code,
    Status.PEN.code,
    Status.PAY.code,
    Status.SUB.code,
    Status.VER.code,
    Status.ORT.code,
    Status.PRA.code,
    Status.EXP.code,
    Status.DRF.code,
    Status.AMN.code,
  ].includes(status);
}

export function getPreviewMode(status) {
  return [Status.APP.code, Status.ACK.code].includes(status);
}

export function getCloneMode(status) {
  return [
    Status.ACK.code,
    Status.SUB.code,
    Status.APP.code,
    Status.VER.code,
    Status.AMN.code,
    Status.EXP.code,
  ].includes(status);
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
  let sel = [];
  if (selector.indexOf(".") !== -1) {
    sel = selector.split(".");
  } else {
    sel.push(selector);
  }

  let newState = updateStateHelper(state, sel, newVal);
  if (autoAssign) return Object.assign(state, newState);

  return newState;
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
