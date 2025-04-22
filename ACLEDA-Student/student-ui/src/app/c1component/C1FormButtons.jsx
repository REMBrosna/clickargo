import IconButton from '@material-ui/core/IconButton';
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import Tooltip from '@material-ui/core/Tooltip';
import AssignmentReturn from "@material-ui/icons/AssignmentReturn";
import AssignmentTurnedIn from '@material-ui/icons/AssignmentTurnedIn';
import AttachmentIcon from '@material-ui/icons/Attachment';
import CheckIcon from '@material-ui/icons/Check';
import Check from '@material-ui/icons/Check';
import Close from '@material-ui/icons/Close';
import DescriptionIcon from '@material-ui/icons/Description';
import ExitIcon from "@material-ui/icons/ExitToApp";
import FileCopyIcon from '@material-ui/icons/FileCopy';
import AddCheckIcon from '@material-ui/icons/LibraryAddCheck';
import Link from "@material-ui/icons/Link";
import LinkOff from "@material-ui/icons/LinkOff";
import PictureAsPdfIcon from '@material-ui/icons/PictureAsPdf';
import PlaylistAddCheckIcon from '@material-ui/icons/PlaylistAddCheck';
import PrintIcon from '@material-ui/icons/Print';
import SaveIcon from '@material-ui/icons/Save';
import SyncIcon from '@material-ui/icons/Sync';
import PropTypes from 'prop-types';
import React from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";

const C1FormButtons = ({ options, children }) => {
    const { t } = useTranslation(["buttons"]);
    const history = useHistory();

    //Application saved as draft
    let elSave = null;
    if (options.save && options.save.show) {
        elSave = <Tooltip title={t("buttons:save")}>
            <IconButton aria-label="save" color="primary" type="button" onClick={options.save.eventHandler}>
                <SaveIcon />
            </IconButton>
        </Tooltip>;
    }


    //Application submit form
    let elSubmit = null;
    if (options.submit) {
        elSubmit = <Tooltip title={t("buttons:submitForm")}>
            <IconButton aria-label="Submit Form" type="submit" color="primary">
                <AssignmentTurnedIn />
            </IconButton>
        </Tooltip>;
    }

    //Application submit onClick
    let elSubmitBtn = null;
    if (options.submitOnClick && options.submitOnClick.show) {
        elSubmitBtn =
            <Tooltip title={t("buttons:submit")} >
                <IconButton aria-label="Submit" type="button" color="primary" onClick={options.submitOnClick.eventHandler}>
                    <AssignmentTurnedIn />
                </IconButton>
            </Tooltip>;
    }

    //Application approval payment action
    let elApprovePayment = null;
    if (options.approvePayment && options.approvePayment.show) {
        elApprovePayment = <Tooltip title={t("buttons:approvePayment")}>
            <IconButton aria-label="approve" color="primary" onClick={options.approvePayment.eventHandler}>
                <Check />
            </IconButton>
        </Tooltip>;
    }

    //Application reject payment action
    let elRejectPayment = null;
    if (options.rejectPayment && options.rejectPayment.show) {
        elRejectPayment = <Tooltip title={t("buttons:rejectPayment")}>
            <IconButton aria-label="reject" color="primary" type="button" onClick={options.rejectPayment.eventHandler}>
                <Close />
            </IconButton>
        </Tooltip>;
    }

    //Application print only applicable for approved application
    let elPrint = null;
    if (options.print && options.print.show) {
        elPrint = <Tooltip title={t("buttons:print") + ` ${options.print.what ? options.print.what : ''}`}>
            <IconButton aria-label="print" color="primary" onClick={options.print.eventHandler}>
                <PictureAsPdfIcon />
            </IconButton>
        </Tooltip>;
    }

    let elCustoms = null;
    if (options.customs && options.customs.show) {
        elCustoms = <Tooltip title={t("buttons:printQuarCert")}>
            <IconButton aria-label="print" color="primary" onClick={options.customs.eventHandler}>
                <PrintIcon />
            </IconButton>
        </Tooltip>;
    }

    //Application acknowledge action
    let eAcknowledge = null;
    if (options.acknowledge && options.acknowledge.show) {
        eAcknowledge = <Tooltip title={t("buttons:acknowledge")}>
            <IconButton aria-label="acknowledge" color="primary" onClick={options.acknowledge.eventHandler}>
                <CheckIcon />
            </IconButton>
        </Tooltip>;
    }

    //Application approval action
    let elApprove = null;
    if (options.approve && options.approve.show) {
        elApprove = <Tooltip title={t("buttons:approve")}>
            <IconButton aria-label="approve" color="primary" onClick={options.approve.eventHandler}>
                <Check />
            </IconButton>
        </Tooltip>;
    }

    //Application Fast Track approval action
    let ftApprove = null;
    if (options.ftApprove && options.ftApprove.show) {
        ftApprove = <Tooltip title={t("buttons:ftApprove")}>
            <IconButton aria-label="FTApprove" color="primary" onClick={options.ftApprove.eventHandler}>
                <AddCheckIcon />
            </IconButton>
        </Tooltip>;
    }

    //Application reject action
    let elReject = null;
    if (options.reject && options.reject.show) {
        elReject = <Tooltip title={t("buttons:reject")}>
            <IconButton aria-label="reject" color="primary" type="button" onClick={options.reject.eventHandler}>
                <Close />
            </IconButton>
        </Tooltip>;
    }

    //Application verify action
    let elVerify = null;
    if (options.verify && options.verify.show) {
        elVerify = <Tooltip title={t("buttons:verify")}>
            <IconButton aria-label="Verify" color="primary" onClick={options.verify.eventHandler}>
                <PlaylistAddCheckIcon />
            </IconButton>
        </Tooltip>;
    }

    //Application return to verifier action
    let elReVerify = null;
    if (options.reVerify && options.reVerify.show) {
        elReVerify = <Tooltip title={t("buttons:returnToVer")}>
            <IconButton aria-label="return" color="primary" onClick={options.reVerify.eventHandler}>
                <AssignmentReturn />
            </IconButton>
        </Tooltip>;
    }

    //Application return to verifier action
    let elRefresh = null;
    if (options.refresh && options.refresh.show) {
        elRefresh = <Tooltip title={t("buttons:refresh")}>
            <IconButton aria-label="refresh" color="primary" onClick={options.refresh.eventHandler}>
                <SyncIcon />
            </IconButton>
        </Tooltip>;
    }

    //Application return to verifier action
    let elValidate = null;
    if (options.validate && options.validate.show) {
        elValidate = <Tooltip title={t("buttons:validate")}>
            <IconButton aria-label="validate" color="primary" onClick={options.validate.eventHandler}>
                <SyncIcon />
            </IconButton>
        </Tooltip>;
    }

    let elReturn = null;
    if (options.return && options.return.show) {
        elReturn = <Tooltip title={t("buttons:returnToApp")}>
            <IconButton aria-label="return" color="primary" onClick={options.return.eventHandler}>
                <AssignmentReturn />
            </IconButton>
        </Tooltip>;
    }

    //Duplicate application
    let elDuplicate = null;
    if (options.duplicate && options.duplicate.show) {
        elDuplicate = <Tooltip title={t("buttons:duplicate")}>
            <IconButton aria-label="duplicate" color="primary" onClick={options.duplicate.eventHandler}>
                <FileCopyIcon />
            </IconButton>
        </Tooltip>;
    }

    //Activate action
    let elActivate = null;
    if (options.activate && options.activate.show) {
        elActivate = <Tooltip title={t("buttons:activate")}>
            <IconButton aria-label="activate" color="primary" onClick={options.activate.eventHandler}>
                <Link />
            </IconButton>
        </Tooltip>;
    }

    //Deactivate action
    let elDeactivate = null;
    if (options.deactivate && options.deactivate.show) {
        elDeactivate = <Tooltip title={t("buttons:deactivate")}>
            <IconButton aria-label="deactivate" color="primary" onClick={options.deactivate.eventHandler}>
                <LinkOff />
            </IconButton>
        </Tooltip>;
    }

    //DetailsPreview is for the summary details preview
    let elAppPreview = null;
    if (options.appPreview && options.appPreview) {
        elAppPreview = <Tooltip title="Details Preview">
            <IconButton aria-label="Details Preview" type="button" color="primary" onClick={options.appPreview.eventHandler} >
                <DescriptionIcon />
            </IconButton>
        </Tooltip>;
    }


    //Download all supporting documents
    let elDlSuppDocs = null;
    if (options.downloadSd && options.downloadSd.show) {
        elDlSuppDocs = <Tooltip title={t("buttons:dlSuppDocs")}>
            <IconButton aria-label="Download Supporting Docs" type="button" color="primary"
                href={options.downloadSd.path} target="_blank" download>
                <AttachmentIcon />
            </IconButton>
        </Tooltip>;
    }


    let elBack = null;
    if (options.back && options.back.show) {
        elBack = <Tooltip title={t("buttons:exit")}>
            <IconButton aria-label="exit" type="button" onClick={options.back.eventHandler || history.goBack()} color="secondary">
                <ExitIcon />
            </IconButton>
        </Tooltip>;
    }

    const [anchorEl, setAnchorEl] = React.useState(null);
    const [anchorEl1, setAnchorEl1] = React.useState(null);

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
    if ((options?.poc && options?.poc?.show) || (options?.qfd && options?.qfd?.show) || (options?.frp && options?.frp?.show)) {
        printCert = <div>
            <Tooltip title={t("buttons:printCert")}>
                <IconButton aria-label="Download Supporting Docs" type="button" color="primary" onClick={handleClick1}>
                    <PictureAsPdfIcon />
                </IconButton>
            </Tooltip>
            <Menu
                id="simple-menu1"
                anchorEl={anchorEl1}
                keepMounted
                open={Boolean(anchorEl1)}
                onClose={handleClose1}>
                {options?.qfd?.show ? <MenuItem onClick={options.qfd.eventHandler}><PictureAsPdfIcon color="primary" /> Quarantine for Departure</MenuItem> : null}
                {options?.poc?.show ? <MenuItem onClick={options.poc.eventHandler}><PictureAsPdfIcon color="primary" /> Port Clearance Certificate</MenuItem> : null}
                {options?.frp?.show ? <MenuItem onClick={options.frp.eventHandler}><PictureAsPdfIcon color="primary" /> Free Pratique Certificate</MenuItem> : null}
            </Menu>
        </div>
    }

    //Preview Details is for the summary the application form detail
    let elPreview = null;
    if (options?.preview && options?.preview?.show) {
        if (options?.fal && options?.fal?.show) {
            elPreview = <div>
                <Tooltip title={t("buttons:preview")}>
                    <IconButton aria-label="Download Supporting Docs" type="button" color="primary" onClick={handleClick}>
                        <DescriptionIcon />
                    </IconButton>
                </Tooltip>
                <Menu
                    id="simple-menu"
                    anchorEl={anchorEl}
                    keepMounted
                    open={Boolean(anchorEl)}
                    onClose={handleClose}>
                    <MenuItem onClick={options.preview.eventHandler}><DescriptionIcon color="primary" /> Preview Details</MenuItem>

                    {options?.dsa?.show ? <MenuItem onClick={options.dsa.eventHandler}><DescriptionIcon color="primary" /> {t("buttons:declarationOfArrival")}</MenuItem> : null}
                    {options?.dsd?.show ? <MenuItem onClick={options.dsd.eventHandler}><DescriptionIcon color="primary" /> {t("buttons:declarationOfDeparture")}</MenuItem> : null}

                    <MenuItem onClick={options.fal.eventHandlerFal1}><DescriptionIcon color="primary" /> IMO General Declaration (FAL form 1)</MenuItem>
                    <MenuItem onClick={options.fal.eventHandlerFal2}><DescriptionIcon color="primary" /> Cargo Declaration (FAL form 2)</MenuItem>
                    <MenuItem onClick={options.fal.eventHandlerFal3}><DescriptionIcon color="primary" /> Ship's Stores Declaration (FAL form 3) </MenuItem>
                    <MenuItem onClick={options.fal.eventHandlerFal4}><DescriptionIcon color="primary" /> Crew's Effects Declaration (FAL form 4)</MenuItem>
                    <MenuItem onClick={options.fal.eventHandlerFal5}><DescriptionIcon color="primary" /> Crew List (FAL form 5)</MenuItem>
                    <MenuItem onClick={options.fal.eventHandlerFal6}><DescriptionIcon color="primary" /> Passenger List (FAL form 6)</MenuItem>
                    <MenuItem onClick={options.fal.eventHandlerFal7}><DescriptionIcon color="primary" /> Dangerous Goods (FAL form 7) </MenuItem>
                    {options?.maritime?.show ? <MenuItem onClick={options.fal.eventHandlerMaritime}><DescriptionIcon color="primary" /> Maritime Declaration of Health </MenuItem> : null}
                    {options?.outbound?.show ? <MenuItem onClick={options.fal.eventHandlerOutbound}><DescriptionIcon color="primary" /> Declaration of Health for Out Bound Vessel </MenuItem> : null}


                </Menu>
            </div>
        } else {
            elPreview = <Tooltip title={t("buttons:preview")}>
                <IconButton aria-label="Preview Details" type="button" color="primary" onClick={options.preview.eventHandler} >
                    <DescriptionIcon />
                </IconButton>
            </Tooltip>;
        }
    }


    return (
        <React.Fragment>
            <div className="flex items-center" >
                <div className="flex-grow"></div>
                {elApprovePayment}
                {elRejectPayment}
                {printCert}
                {elSave}
                {elSubmit}
                {elSubmitBtn}
                {elRefresh}
                {elValidate}
                {elPreview}
                {elPrint}
                {elCustoms}
                {eAcknowledge}
                {elApprove}
                {ftApprove}
                {elReturn}
                {elReVerify}
                {elVerify}
                {elReject}
                {elActivate}
                {elDeactivate}
                {elDuplicate}
                {elAppPreview}
                {elDlSuppDocs}
                {children}
                {elBack}
            </div>
        </React.Fragment>
    );
}

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
        approve: PropTypes.exact({
            show: PropTypes.bool,
            eventHandler: PropTypes.func,
        }),
        approvePayment: PropTypes.exact({
            show: PropTypes.bool,
            eventHandler: PropTypes.func,
        }),
        rejectPayment: PropTypes.exact({
            show: PropTypes.bool,
            eventHandler: PropTypes.func,
        }),
        verify: PropTypes.exact({
            show: PropTypes.bool,
            eventHandler: PropTypes.func
        }),
        acknowledge: PropTypes.exact({
            show: PropTypes.bool,
            eventHandler: PropTypes.func
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
            eventHandler: PropTypes.func
        }),
        print: PropTypes.exact({
            show: PropTypes.bool,
            what: PropTypes.string,
            eventHandler: PropTypes.func
        }),

        customs: PropTypes.exact({
            show: PropTypes.bool,
            eventHandler: PropTypes.func
        }),

        reVerify: PropTypes.exact({
            show: PropTypes.bool,
            eventHandler: PropTypes.func
        }),
        return: PropTypes.exact({
            show: PropTypes.bool,
            eventHandler: PropTypes.func
        }),
        downloadTemplate: PropTypes.exact({
            show: PropTypes.bool,
            path: PropTypes.string
        }),
        uploadTemplate: PropTypes.exact({
            show: PropTypes.bool,
            eventHandler: PropTypes.func,
        }),
        downloadSd: PropTypes.exact({
            show: PropTypes.bool,
            path: PropTypes.string
        }),
        appPreview: PropTypes.exact({
            show: PropTypes.bool,
            eventHandler: PropTypes.func
        }),
        preview: PropTypes.exact({
            show: PropTypes.bool,
            eventHandler: PropTypes.func
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
        maritime: PropTypes.exact({
            show: PropTypes.bool,
            eventHandler: PropTypes.func,
        }),
        validate: PropTypes.exact({
            show: PropTypes.bool,
            eventHandler: PropTypes.func,
        })
    })
}

C1FormButtons.defaultProps = {
    options: {
        save: {
            show: true
        },
        submit: true,
        submitOnclick: {
            show: true
        },
        approve: {
            show: true
        },
        approvePayment: {
            show: true
        },
        rejectPayment: {
            show: true
        },
        verify: {
            show: true
        },
        reject: {
            show: true
        },
        duplicate: {
            show: true
        },
        activate: {
            show: true
        },
        deactivate: {
            show: true
        },
        print: {
            show: true,
            what: 'Certificate'
        },
        reverify: {
            show: true
        },
        downloadTemplate: {
            show: true
        },
        uploadTemplate: {
            show: true
        },
        downloadSd: {
            show: true
        },
        appPreview: {
            show: true
        },
        preview: {
            show: true
        },
        back: {
            show: true
        },
        customs: {
            show: true
        },
        fal: {
            show: true
        },
        frp: {
            show: true
        },
        poc: {
            show: true
        },
        qfd: {
            show: true
        },
        dsd: {
            show: true
        },
        dsa: {
            show: true
        },
        refresh: {
            show: true
        },
        validate: {
            show: true
        },
        maritime: {
            show: true
        },
        outbound: {
            show: true
        }
    }
}


export default C1FormButtons;