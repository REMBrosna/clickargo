import { Box, Button } from "@material-ui/core";
import IconButton from "@material-ui/core/IconButton";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import Tooltip from "@material-ui/core/Tooltip";
import {
  CancelOutlined,
  GetAppOutlined,
  PublishOutlined,
} from "@material-ui/icons";
import AddBoxIcon from "@material-ui/icons/AddBoxOutlined";
import AssignmentReturn from "@material-ui/icons/AssignmentReturnOutlined";
import AttachmentIcon from "@material-ui/icons/AttachmentOutlined";
import BlockIcon from "@material-ui/icons/Block";
import CheckIcon from "@material-ui/icons/CheckOutlined";
import Close from "@material-ui/icons/CloseOutlined";
import DeleteIcon from "@material-ui/icons/DeleteOutlineOutlined";
import DescriptionIcon from "@material-ui/icons/DescriptionOutlined";
import DoneAllOutlinedIcon from "@material-ui/icons/DoneAllOutlined";
import ExitIcon from "@material-ui/icons/ExitToAppOutlined";
import FileCopyIcon from "@material-ui/icons/FileCopyOutlined";
import HelpOutlineIcon from "@material-ui/icons/HelpOutline";
import LinkOff from "@material-ui/icons/LinkOffOutlined";
import Link from "@material-ui/icons/LinkOutlined";
import LocalShippingOutlinedIcon from "@material-ui/icons/LocalShippingOutlined";
import NearMeIcon from "@material-ui/icons/NearMeOutlined";
import PauseCircleOutlineOutlinedIcon from "@material-ui/icons/PauseCircleOutlineOutlined";
import PaymentIcon from "@material-ui/icons/PaymentOutlined";
import PictureAsPdfIcon from "@material-ui/icons/PictureAsPdfOutlined";
import PlayCircleOutlineOutlinedIcon from "@material-ui/icons/PlayCircleOutlineOutlined";
import PlaylistAddCheckOutlinedIcon from "@material-ui/icons/PlaylistAddCheckOutlined";
import ReceiptOutlinedIcon from "@material-ui/icons/ReceiptOutlined";
import SaveIcon from "@material-ui/icons/SaveOutlined";
import SettingsBackupRestoreOutlinedIcon from "@material-ui/icons/SettingsBackupRestoreOutlined";
import SpellcheckOutlinedIcon from "@material-ui/icons/SpellcheckOutlined";
import SyncIcon from "@material-ui/icons/SyncOutlined";
import PropTypes from "prop-types";
import React from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";

import C1LabeledIconButton from "./C1LabeledIconButton";

const C1FormButtons = ({ options, children }) => {
  const { t } = useTranslation(["buttons"]);
  const history = useHistory();

  const [anchorEl, setAnchorEl] = React.useState(null);
  const [anchorEl1, setAnchorEl1] = React.useState(null);
  const greyColor = "#0000008a";

  let elDelete = null;
  if (options.delete && options.delete.show) {
    elDelete = (
      <C1LabeledIconButton
        tooltip={t("buttons:delete")}
        label={t("buttons:delete")}
        action={options.delete.eventHandler}
      >
        <DeleteIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  let elDeleteReg = null;
  if (options.deleteReg && options.deleteReg.show) {
    elDeleteReg = (
      <C1LabeledIconButton
        tooltip={t("buttons:delete")}
        label={t("buttons:delete")}
        action={options.deleteReg.eventHandler}
      >
        <DeleteIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  let elApproveReg = null;
  if (options.approveReg && options.approveReg.show) {
    elApproveReg = (
      <C1LabeledIconButton
        tooltip={t("buttons:approveReg")}
        label={t("buttons:approve")}
        action={options.approveReg.eventHandler}
      >
        <CheckIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  let elRejectReg = null;
  if (options.rejectReg && options.rejectReg.show) {
    elRejectReg = (
      <C1LabeledIconButton
        tooltip={t("buttons:rejectReg")}
        label={t("buttons:reject")}
        action={options.rejectReg.eventHandler}
      >
        <BlockIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  let elSuspendSub = null;
  if (options.suspendSub && options.suspendSub.show) {
    elSuspendSub = (
      <C1LabeledIconButton
        tooltip={t("buttons:suspend")}
        label={t("buttons:suspend")}
        action={options.suspendSub.eventHandler}
      >
        <PauseCircleOutlineOutlinedIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  let elSuspendApp = null;
  if (options.suspendApp && options.suspendApp.show) {
    elSuspendApp = (
      <C1LabeledIconButton
        tooltip={t("buttons:approveSus")}
        label={t("buttons:approve")}
        action={options.suspendApp.eventHandler}
      >
        <CheckIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  let elSuspendRej = null;
  if (options.suspendRej && options.suspendRej.show) {
    elSuspendRej = (
      <C1LabeledIconButton
        tooltip={t("buttons:rejectSus")}
        label={t("buttons:reject")}
        action={options.suspendRej.eventHandler}
      >
        <CancelOutlined color="primary" />
      </C1LabeledIconButton>
    );
  }

  let elTerminateSub = null;
  if (options.terminateSub && options.terminateSub.show) {
    elTerminateSub = (
      <C1LabeledIconButton
        tooltip={t("buttons:terminate")}
        label={t("buttons:terminate")}
        action={options.terminateSub.eventHandler}
      >
        <BlockIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  let elTerminateApp = null;
  if (options.terminateApp && options.terminateApp.show) {
    elTerminateApp = (
      <C1LabeledIconButton
        tooltip={t("buttons:approveTer")}
        label={t("buttons:approve")}
        action={options.terminateApp.eventHandler}
      >
        <CheckIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  let elTerminateRej = null;
  if (options.terminateRej && options.terminateRej.show) {
    elTerminateRej = (
      <C1LabeledIconButton
        tooltip={t("buttons:rejectTer")}
        label={t("buttons:reject")}
        action={options.terminateRej.eventHandler}
      >
        <CancelOutlined color="primary" />
      </C1LabeledIconButton>
    );
  }

  //Application saved as draft
  let elSave = null;
  if (options.save && options.save.show) {
    elSave = (
      <C1LabeledIconButton
        tooltip={t("buttons:save")}
        label={t("buttons:save")}
        action={options.save.eventHandler}
      >
        <SaveIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  //Application submit form
  let elSubmit = null;
  if (options.submit) {
    elSubmit = (
      <C1LabeledIconButton
        tooltip={t("buttons:submit")}
        label={t("buttons:submit")}
        action={options.submit.eventHandler}
      >
        <NearMeIcon color="primary" />
      </C1LabeledIconButton>
    );
    // <Tooltip title={t("buttons:submit")}>
    //     <IconButton aria-label="Submit Form" type="submit" color="primary">
    //         <NearMeIcon color="primary" />
    //     </IconButton>
    // </Tooltip>;
  }

  //Application submit onClick
  let elSubmitBtn = null;
  if (options.submitOnClick && options.submitOnClick.show) {
    elSubmitBtn = (
      <C1LabeledIconButton
        tooltip={t("buttons:submit")}
        label={t("buttons:submit")}
        action={options.submitOnClick.eventHandler}
      >
        <NearMeIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  //Application Assign
  let elAssign = null;
  if (options.assign && options.assign.show) {
    elAssign = (
      <C1LabeledIconButton
        tooltip={t("buttons:assignTooltip")}
        label={t("buttons:assignTooltip")}
        action={options.assign.eventHandler}
      >
        <LocalShippingOutlinedIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  //Application Assign
  let elStart = null;
  if (options.start && options.start.show) {
    elStart = (
      <C1LabeledIconButton
        tooltip={t("buttons:start")}
        label={t("buttons:start")}
        action={options.start.eventHandler}
      >
        <PlayCircleOutlineOutlinedIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  let elStop = null;
  if (options.stop && options.stop.show) {
    elStop = (
      <C1LabeledIconButton
        tooltip={t("buttons:stop")}
        label={t("buttons:stop")}
        action={options.stop.eventHandler}
      >
        <LocalShippingOutlinedIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  let elBillJob = null;
  if (options.billjob && options.billjob.show) {
    elBillJob = (
      <C1LabeledIconButton
        tooltip={t("buttons:billJob")}
        label={t("buttons:billJob")}
        action={options.billjob.eventHandler}
      >
        <ReceiptOutlinedIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  let elVerifyPayment = null;
  if (options.verifypayment && options.verifypayment.show) {
    elVerifyPayment = (
      <C1LabeledIconButton
        tooltip={t("buttons:verifyPayment")}
        label={t("buttons:verifyPayment")}
        action={options.verifypayment.eventHandler}
      >
        <PlaylistAddCheckOutlinedIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  //Application approval payment action
  let elApprovePayment = null;
  if (options.approvepayment && options.approvepayment.show) {
    elApprovePayment = (
      <C1LabeledIconButton
        tooltip={t("buttons:approvePayment")}
        label={t("buttons:approvePayment")}
        action={options.approvepayment.eventHandler}
      >
        <PlaylistAddCheckOutlinedIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  //Application reject payment action
  let elRejectPayment = null;
  if (options.rejectPayment && options.rejectPayment.show) {
    elRejectPayment = (
      <C1LabeledIconButton
        tooltip={t("buttons:rejectPayment")}
        label={t("buttons:rejectPayment")}
        action={options.rejectPayment.eventHandler}
      >
        <Close color="primary" />
      </C1LabeledIconButton>
    );
  }

  //Application print only applicable for approved application
  let elPrint = null;
  if (options.print && options.print.show) {
    elPrint = (
      <C1LabeledIconButton
        tooltip={t("buttons:print")}
        label={t("buttons:print")}
        action={options.print.eventHandler}
      >
        <PictureAsPdfIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  //Application acknowledge action
  let eAcknowledge = null;
  if (options.acknowledge && options.acknowledge.show) {
    eAcknowledge = (
      <C1LabeledIconButton
        tooltip={t("buttons:acknowledge")}
        label={t("buttons:acknowledge")}
        action={options.acknowledge.eventHandler}
      >
        <CheckIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  //Application approval action
  let elApprove = null;
  if (options.approve && options.approve.show) {
    elApprove = (
      <C1LabeledIconButton
        tooltip={t("buttons:approve")}
        label={t("buttons:approve")}
        action={options.approve.eventHandler}
      >
        <CheckIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  //Application Acceptance
  let elAccept = null;
  if (options.accept && options.accept.show) {
    elAccept = (
      <C1LabeledIconButton
        tooltip={t("buttons:accept")}
        label={t("buttons:accept")}
        action={options.accept.eventHandler}
      >
        <SpellcheckOutlinedIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  //Application reject action
  let elReject = null;
  if (options.reject && options.reject.show) {
    elReject = (
      <C1LabeledIconButton
        tooltip={t("buttons:reject")}
        label={t("buttons:reject")}
        action={options.reject.eventHandler}
      >
        <BlockIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  let elWithdraw = null;
  if (options.withdraw && options.withdraw.show) {
    elWithdraw = (
      <C1LabeledIconButton
        tooltip={t("buttons:withdraw")}
        label={t("buttons:withdraw")}
        action={options.withdraw.eventHandler}
      >
        <SettingsBackupRestoreOutlinedIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  //Application verify action
  let elVerify = null;
  if (options.verify && options.verify.show) {
    elVerify = (
      <C1LabeledIconButton
        tooltip={t("buttons:verify")}
        label={t("buttons:verify")}
        action={options.verify.eventHandler}
      >
        <CheckIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  //Application return to verifier action
  let elReVerify = null;
  if (options.reverify && options.reverify.show) {
    elReVerify = (
      <C1LabeledIconButton
        tooltip={t("buttons:retDocVerify")}
        label={t("buttons:retDocVerify")}
        action={options.reverify.eventHandler}
      >
        <DoneAllOutlinedIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  //Application return to verifier action
  let elRefresh = null;
  if (options.refresh && options.refresh.show) {
    elRefresh = (
      <C1LabeledIconButton
        tooltip={t("buttons:refresh")}
        label={t("buttons:refresh")}
        action={options.refresh.eventHandler}
      >
        <SyncIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  //Application return to verifier action
  let elValidate = null;
  if (options.validate && options.validate.show) {
    elRefresh = (
      <C1LabeledIconButton
        tooltip={t("buttons:validate")}
        label={t("buttons:validate")}
        action={options.validate.eventHandler}
      >
        <SyncIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  let elReturn = null;
  if (options.return && options.return.show) {
    elReturn = (
      <C1LabeledIconButton
        tooltip={t("buttons:returnToApp")}
        label={t("buttons:returnToApp")}
        action={options.return.eventHandler}
      >
        <AssignmentReturn color="primary" />
      </C1LabeledIconButton>
    );
  }

  //Duplicate application
  let elDuplicate = null;
  if (
    (options.duplicate && options.duplicate.show) ||
    (options.clone && options.clone.show)
  ) {
    elDuplicate = (
      <C1LabeledIconButton
        tooltip={t("buttons:duplicate")}
        label={t("buttons:duplicate")}
        action={options.duplicate.eventHandler}
      >
        <FileCopyIcon color="primary" />
      </C1LabeledIconButton>
    );
  }
  
  let elSplit = null;
  if (
    (options.split && options.split.show)
  ) {
    elDuplicate = (
      <C1LabeledIconButton
        tooltip={t("buttons:split")}
        label={t("buttons:split")}
        action={options.split.eventHandler}
      >
        <FileCopyIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  //Activate action
  let elActivate = null;
  if (options.activate && options.activate.show) {
    elActivate = (
      <C1LabeledIconButton
        tooltip={t("buttons:activate")}
        label={t("buttons:activate")}
        action={options.activate.eventHandler}
      >
        <Link color="primary" />
      </C1LabeledIconButton>
    );
  }

  //Deactivate action
  let elDeactivate = null;
  if (options.deactivate && options.deactivate.show) {
    elDeactivate = (
      <C1LabeledIconButton
        tooltip={t("buttons:deactivate")}
        label={t("buttons:deactivate")}
        action={options.deactivate.eventHandler}
      >
        <LinkOff color="primary" />
      </C1LabeledIconButton>
    );
  }

  //Suspend action
  let elSuspend = null;
  if (options.suspend && options.suspend.show) {
    elSuspend = (
      <C1LabeledIconButton
        tooltip={t("buttons:suspend")}
        label={t("buttons:suspend")}
        action={options.suspend.eventHandler}
      >
        <PauseCircleOutlineOutlinedIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  //Terminate action
  let elTerminate = null;
  if (options.terminate && options.terminate.show) {
    elTerminate = (
      <C1LabeledIconButton
        tooltip={t("buttons:terminate")}
        label={t("buttons:terminate")}
        action={options.terminate.eventHandler}
      >
        <BlockIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  //Resumption action
  let elResumption = null;
  if (options.resumption && options.resumption.show) {
    elResumption = (
      <C1LabeledIconButton
        tooltip={t("buttons:unsuspend")}
        label={t("buttons:unsuspend")}
        action={options.resumption.eventHandler}
      >
        <SettingsBackupRestoreOutlinedIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  //DetailsPreview is for the summary details preview
  let elAppPreview = null;
  if (options.appPreview && options.appPreview) {
    elAppPreview = (
      <C1LabeledIconButton
        tooltip={`Details Preview`}
        label={`Details Preview`}
        action={options.appPreview.eventHandler}
      >
        <DescriptionIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  //Download all supporting documents
  let elDlSuppDocs = null;
  if (options.downloadSd && options.downloadSd.show) {
    elDlSuppDocs = (
      <Tooltip title={t("buttons:dlSuppDocs")}>
        <IconButton
          aria-label="Download Supporting Docs"
          type="button"
          color="primary"
          href={options.downloadSd.path}
          target="_blank"
          download
        >
          <AttachmentIcon />
        </IconButton>
      </Tooltip>
    );
  }

  // Confirm
  let elConfirm = null;
  if (options.confirm && options.confirm.show) {
    elConfirm = (
      <C1LabeledIconButton
        tooltip={`Confirm`}
        label={`Confirm`}
        action={options.confirm.eventHandler}
      >
        <CheckIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  let elPay = null;
  if (options.pay && options.pay.show) {
    elPay = (
      <C1LabeledIconButton
        tooltip={`Pay`}
        label={`Pay`}
        action={options.pay.eventHandler}
      >
        <PaymentIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  let elBack = null;
  if (options.back && options.back.show) {
    elBack = (
      <C1LabeledIconButton
        tooltip={t("buttons:exit")}
        label={t("buttons:exit")}
        color="secondary"
        action={options.back.eventHandler || history.goBack()}
      >
        <ExitIcon color="secondary" />
      </C1LabeledIconButton>
    );
  }

  let elCancel = null;
  if (options.cancel && options.cancel.show) {
    elCancel = (
      <C1LabeledIconButton
        tooltip={t("buttons:cancel")}
        label={t("buttons:cancel")}
        action={options.cancel.eventHandler}
      >
        <CancelOutlined color="secondary" />
      </C1LabeledIconButton>
    );
  }

  let elQuery = null;
  if (options?.query && options?.query?.show) {
    elQuery = (
      <C1LabeledIconButton
        tooltip={t("buttons:btnQuery")}
        label={t("buttons:btnQuery")}
        action={options.query.eventHandler}
      >
        <HelpOutlineIcon color="secondary" />
      </C1LabeledIconButton>
    );
  }

  let elDownloadTemplate = null;
  if (options?.downloadTemplate && options?.downloadTemplate?.show) {
    elDownloadTemplate = (
      <Box sx={{ marginBottom: 10 }}>
        <C1LabeledIconButton
          tooltip={t("buttons:downloadTemplate")}
          label={t("buttons:download")}
          style={{ color: greyColor, fontSize: "0.6rem", fontWeight: 600 }}
          action={options.downloadTemplate.eventHandler}
        >
          <GetAppOutlined
            style={{ color: greyColor, marginBottom: -3 }}
            fontSize="small"
          />
        </C1LabeledIconButton>
      </Box>
    );
  }

  let elUploadTemplate = null;
  if (options?.uploadTemplate && options?.uploadTemplate?.show) {
    elUploadTemplate = (
      <Box sx={{ marginBottom: 10 }}>
        <C1LabeledIconButton
          tooltip={t("buttons:uploadTemplate")}
          label={t("buttons:upload")}
          style={{ color: greyColor, fontSize: "0.6rem", fontWeight: 600 }}
          action={options.uploadTemplate.eventHandler}
        >
          <PublishOutlined
            style={{ color: greyColor, marginBottom: -3 }}
            fontSize="small"
          />
        </C1LabeledIconButton>
      </Box>
    );
  }

  let elAdd = null;
  if (options?.add && options?.add?.show) {
    elAdd = (
      <Box sx={{ marginBottom: 10 }}>
        <C1LabeledIconButton
          tooltip={t("buttons:add")}
          label={t("buttons:add")}
          style={{ color: greyColor, fontSize: "0.6rem", fontWeight: 600 }}
          action={options.add.eventHandler}
        >
          <AddBoxIcon
            style={{ color: greyColor, marginBottom: -3 }}
            fontSize="small"
          />
        </C1LabeledIconButton>
      </Box>
    );
  }

  let elVerifyBill = null;
  if (options?.verify_bill && options?.verify_bill?.show) {
    elVerifyBill = (
      <C1LabeledIconButton
        tooltip={t("buttons:verifyBill")}
        label={t("buttons:verifyBill")}
        action={options.verify_bill.eventHandler}
      >
        <PlaylistAddCheckOutlinedIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  let elAckBill = null;
  if (options?.acknowledge_bill && options?.acknowledge_bill?.show) {
    elAckBill = (
      <C1LabeledIconButton
        tooltip={t("buttons:approveBill")}
        label={t("buttons:approveBill")}
        action={options.acknowledge_bill.eventHandler}
      >
        <DoneAllOutlinedIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  let elApproveBill = null;
  if (options?.approve_bill && options?.approve_bill?.show) {
    elApproveBill = (
      <C1LabeledIconButton
        tooltip={t("buttons:approveBill")}
        label={t("buttons:approveBill")}
        action={options.approve_bill.eventHandler}
      >
        <SpellcheckOutlinedIcon color="primary" />
      </C1LabeledIconButton>
    );
  }

  let elRejectBill = null;
  if (options?.reject_bill && options?.reject_bill?.show) {
    elRejectBill = (
      <C1LabeledIconButton
        tooltip={t("buttons:rejectBill")}
        label={t("buttons:rejectBill")}
        action={options.reject_bill.eventHandler}
      >
        <Close color="primary" />
      </C1LabeledIconButton>
    );
  }

  const handleClick1 = (event) => {
    setAnchorEl1(event.currentTarget);
  };
  const handleClose1 = () => {
    setAnchorEl1(null);
  };

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };
  const handleClose = () => {
    setAnchorEl(null);
  };

  let printCert = null;
  if (
    (options?.poc && options?.poc?.show) ||
    (options?.qfd && options?.qfd?.show) ||
    (options?.frp && options?.frp?.show)
  ) {
    printCert = (
      <div>
        <Tooltip title={t("buttons:printCert")}>
          <IconButton
            aria-label="Download Supporting Docs"
            type="button"
            color="primary"
            onClick={handleClick1}
          >
            <PictureAsPdfIcon />
          </IconButton>
        </Tooltip>
        <Menu
          id="simple-menu1"
          anchorEl={anchorEl1}
          keepMounted
          open={Boolean(anchorEl1)}
          onClose={handleClose1}
        >
          {options?.qfd?.show ? (
            <MenuItem onClick={options.qfd.eventHandler}>
              <PictureAsPdfIcon color="primary" /> Quarantine for Departure
            </MenuItem>
          ) : null}
          {options?.poc?.show ? (
            <MenuItem onClick={options.poc.eventHandler}>
              <PictureAsPdfIcon color="primary" /> Port Clearance Certificate
            </MenuItem>
          ) : null}
          {options?.frp?.show ? (
            <MenuItem onClick={options.frp.eventHandler}>
              <PictureAsPdfIcon color="primary" /> Free Pratique Certificate
            </MenuItem>
          ) : null}
        </Menu>
      </div>
    );
  }

  //Preview Details is for the summary the application form detail
  let elPreview = null;
  if (options?.preview && options?.preview?.show) {
    if (options?.fal && options?.fal?.show) {
      elPreview = (
        <div>
          <Tooltip title={t("buttons:preview")}>
            <IconButton
              aria-label="Download Supporting Docs"
              type="button"
              color="primary"
              onClick={handleClick}
            >
              <DescriptionIcon />
            </IconButton>
          </Tooltip>
          <Menu
            id="simple-menu"
            anchorEl={anchorEl}
            keepMounted
            open={Boolean(anchorEl)}
            onClose={handleClose}
          >
            <MenuItem onClick={options.preview.eventHandler}>
              <DescriptionIcon color="primary" /> Preview Details
            </MenuItem>

            {options?.dsa?.show ? (
              <MenuItem onClick={options.dsa.eventHandler}>
                <DescriptionIcon color="primary" />{" "}
                {t("buttons:declarationOfArrival")}
              </MenuItem>
            ) : null}
            {options?.dsd?.show ? (
              <MenuItem onClick={options.dsd.eventHandler}>
                <DescriptionIcon color="primary" />{" "}
                {t("buttons:declarationOfDeparture")}
              </MenuItem>
            ) : null}

            <MenuItem onClick={options.fal.eventHandlerFal1}>
              <DescriptionIcon color="primary" /> IMO General Declaration (FAL
              form 1)
            </MenuItem>
            <MenuItem onClick={options.fal.eventHandlerFal2}>
              <DescriptionIcon color="primary" /> Cargo Declaration (FAL form 2)
            </MenuItem>
            <MenuItem onClick={options.fal.eventHandlerFal3}>
              <DescriptionIcon color="primary" /> Ship's Stores Declaration (FAL
              form 3){" "}
            </MenuItem>
            <MenuItem onClick={options.fal.eventHandlerFal4}>
              <DescriptionIcon color="primary" /> Crew's Effects Declaration
              (FAL form 4)
            </MenuItem>
            <MenuItem onClick={options.fal.eventHandlerFal5}>
              <DescriptionIcon color="primary" /> Crew List (FAL form 5)
            </MenuItem>
            <MenuItem onClick={options.fal.eventHandlerFal6}>
              <DescriptionIcon color="primary" /> Passenger List (FAL form 6)
            </MenuItem>
            <MenuItem onClick={options.fal.eventHandlerFal7}>
              <DescriptionIcon color="primary" /> Dangerous Goods (FAL form 7){" "}
            </MenuItem>
            {options?.maritime?.show ? (
              <MenuItem onClick={options.fal.eventHandlerMaritime}>
                <DescriptionIcon color="primary" /> Maritime Declaration of
                Health{" "}
              </MenuItem>
            ) : null}
            {options?.outbound?.show ? (
              <MenuItem onClick={options.fal.eventHandlerOutbound}>
                <DescriptionIcon color="primary" /> Declaration of Health for
                Out Bound Vessel{" "}
              </MenuItem>
            ) : null}
          </Menu>
        </div>
      );
    } else {
      elPreview = (
        <Tooltip title={t("buttons:preview")}>
          <IconButton
            aria-label="Preview Details"
            type="button"
            color="primary"
            onClick={options.preview.eventHandler}
          >
            <DescriptionIcon />
          </IconButton>
        </Tooltip>
      );
    }
  }

  return (
    <React.Fragment>
      <div className="flex items-center">
        <div className="flex-grow"></div>
        {elVerifyBill}
        {elAckBill}
        {elApproveBill}
        {elRejectBill}
        {elVerifyPayment}
        {elApprovePayment}
        {elRejectPayment}
        {printCert}
        {elSubmit}
        {elVerify}
        {elConfirm}
        {elPay}
        {elSave}
        {elAssign}
        {elStart}
        {elStop}
        {elBillJob}
        {elSubmitBtn}
        {elRefresh}
        {elValidate}
        {elCancel}
        {elDelete}
        {elDeleteReg}
        {elApproveReg}
        {elRejectReg}
        {elSuspendSub}
        {elSuspendApp}
        {elSuspendRej}
        {elTerminateSub}
        {elTerminateApp}
        {elTerminateRej}
        {elPreview}
        {elPrint}
        {eAcknowledge}
        {elApprove}
        {elReturn}
        {elReVerify}
        {elAccept}
        {elReject}
        {elWithdraw}
        {elActivate}
        {elDeactivate}
        {elSuspend}
        {elTerminate}
        {elResumption}
        {elDuplicate}
        {elSplit}
        {elAppPreview}
        {elDlSuppDocs}
        {elQuery}
        {children}
        {elBack}
        {elDownloadTemplate}
        {elUploadTemplate}
        {elAdd}
      </div>
    </React.Fragment>
  );
};

C1FormButtons.propTypes = {
  options: PropTypes.exact({
    save: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    submit: PropTypes.bool,
    submitOnClick: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    assign: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    start: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    stop: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    billjob: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    approve: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    deleteReg: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    approveReg: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    rejectReg: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    suspendSub: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    suspendApp: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    suspendRej: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    terminateSub: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    terminateApp: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    terminateRej: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    verifypayment: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    approvepayment: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    rejectpayment: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    verify: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    acknowledge: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    reject: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    duplicate: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    activate: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    deactivate: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    suspend: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    terminate: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    resumption: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    print: PropTypes.exact({
      show: PropTypes.bool,
      what: PropTypes.string,
      eventHandler: PropTypes.func,
    }),
    delete: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    query: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),

    accept: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),

    reverify: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),

    withdraw: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),

    return: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    downloadTemplate: PropTypes.exact({
      show: PropTypes.bool,
      path: PropTypes.string,
      eventHandler: PropTypes.func,
    }),
    uploadTemplate: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    downloadSd: PropTypes.exact({
      show: PropTypes.bool,
      path: PropTypes.string,
    }),
    appPreview: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    preview: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    back: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    fal: PropTypes.exact({
      show: PropTypes.bool,
      eventHandlerFal1: PropTypes.func,
      eventHandlerFal2: PropTypes.func,
      eventHandlerFal3: PropTypes.func,
      eventHandlerFal4: PropTypes.func,
      eventHandlerFal5: PropTypes.func,
      eventHandlerFal6: PropTypes.func,
      eventHandlerFal7: PropTypes.func,
      eventHandlerMaritime: PropTypes.func,
      eventHandlerOutbound: PropTypes.func,
    }),
    frp: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    poc: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    qfd: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),

    dsd: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    dsa: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    refresh: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    validate: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    confirm: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    pay: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    cancel: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    add: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    verify_bill: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    acknowledge_bill: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    approve_bill: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
    reject_bill: PropTypes.exact({
      show: PropTypes.bool,
      eventHandler: PropTypes.func,
    }),
  }),
};

C1FormButtons.defaultProps = {
  options: {
    save: {
      show: true,
    },
    submit: true,
    submitOnclick: {
      show: true,
    },
    approve: {
      show: true,
    },
    approvePayment: {
      show: true,
    },
    rejectPayment: {
      show: true,
    },
    verify: {
      show: true,
    },
    reject: {
      show: true,
    },
    duplicate: {
      show: true,
    },
    activate: {
      show: true,
    },
    deactivate: {
      show: true,
    },
    suspend: {
      show: true,
    },
    print: {
      show: true,
      what: "Certificate",
    },
    reverify: {
      show: true,
    },
    downloadTemplate: {
      show: true,
    },
    uploadTemplate: {
      show: true,
    },
    downloadSd: {
      show: true,
    },
    appPreview: {
      show: true,
    },
    preview: {
      show: true,
    },
    back: {
      show: true,
    },
    customs: {
      show: true,
    },
    fal: {
      show: true,
    },
    frp: {
      show: true,
    },
    poc: {
      show: true,
    },
    qfd: {
      show: true,
    },
    dsd: {
      show: true,
    },
    dsa: {
      show: true,
    },
    refresh: {
      show: true,
    },
    validate: {
      show: true,
    },
    maritime: {
      show: true,
    },
    outbound: {
      show: true,
    },
    confirm: {
      show: true,
    },
    pay: {
      show: true,
    },
    cancel: {
      show: true,
    },
    add: {
      show: true,
    },
  },
};

export default C1FormButtons;
