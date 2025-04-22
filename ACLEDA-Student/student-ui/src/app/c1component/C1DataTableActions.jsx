import React from "react";
import { IconButton, Tooltip, Menu, MenuItem, Badge } from "@material-ui/core";
import { Link } from "react-router-dom";
import PictureAsPdfIcon from "@material-ui/icons/PictureAsPdf";
import DescriptionIcon from "@material-ui/icons/Description";
import AttachmentIcon from "@material-ui/icons/Attachment";
import EditIcon from "@material-ui/icons/Edit";
import VisibilityIcon from "@material-ui/icons/Visibility";
import Comment from "@material-ui/icons/Comment";
import LiveHelpIcon from "@material-ui/icons/LiveHelp";
import FileCopyIcon from "@material-ui/icons/FileCopy";
import LinkOffIcon from "@material-ui/icons/LinkOff";
import LinkIcon from "@material-ui/icons/Link";
import DeleteIcon from "@material-ui/icons/Delete";
import PropTypes from "prop-types";
import ArrowRightAltIcon from "@material-ui/icons/ArrowRightAlt";
import CropRotateIcon from '@material-ui/icons/CropRotate';
import { useTranslation } from "react-i18next";
import useMediaQuery from '@material-ui/core/useMediaQuery';
import MonetizationOnOutlinedIcon from '@material-ui/icons/MonetizationOnOutlined';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import TextFormatOutlinedIcon from '@material-ui/icons/TextFormatOutlined';
import CallMissedOutgoingIcon from '@mui/icons-material/CallMissedOutgoing';
/**
 * @param previewEventHandler - Preview applications details
 * @param downloadDocsEventHandler - Download supporting documents
 * @param printCert - Preview application documents or certificates (document produced when approved)
 * @param editPath - Edit application details
 * @param returnPath - Edit application details
 * @param amendPath - Amend returned application details
 * @param viewPath - View application details
 * @param copyPath - Duplicate application details
 * @param cloneHandler - Clone application details
 * @param recoveryPath
 * @param queryPath
 * @param remarkPath
 * @param clonePath
 * @param activatePath - Activate application details
 * @param deactivatePath - Deactivate application details
 * @param removeEventHandler - Delete application detail
 * @param viewPopupEventHandler - View Popup Details
 * @param manualPaymentEventHandler
 * @param returnHandler
 * @param editEventHandler
 * @param returnEventHandler
 * @param activeEventHandler - Active popup confirm to active
 * @param deActiveEventHandler - deActive popup confirm to active
 * @param multiButton - define a print button with sub-menus that can invoke different print actions
 * @param proceedPath
 *
 * @param proceedEventHandler
 * @param children
 * @param generateCert
 */
const C1DataTableActions = ({
    previewEventHandler,
    downloadDocsEventHandler,
    printCert,
    viewPopupEventHandler,
    editPath,
    returnPath,
    returnHandler,
    returnEventHandler,
    recoveryPath,
    amendPath,
    viewPath,
    queryPath,
    remarkPath,
    cloneHandler,
    clonePath,
    copyPath,
    activatePath,
    deactivatePath,
    removeEventHandler,
    manualPaymentEventHandler,
    editEventHandler,
    activeEventHandler,
    deActiveEventHandler,
    proceedPath,
    proceedEventHandler,
    amendEventHandler,
    multiButton,
    children,
    generateCert,
}) => {

    const { t } = useTranslation(["buttons"]);
    const mobileScreen = useMediaQuery(theme => theme.breakpoints.down('xs'));
    const [anchorEl1, setAnchorEl1] = React.useState(null);

    const handleClose1 = () => {
        setAnchorEl1(null);
    };

    const handleClick1 = (event) => {
        setAnchorEl1(event.currentTarget);
    };

    let multiBtn = null;
    if (multiButton?.options) {
        multiBtn =
            <div>
                <Tooltip title={t("buttons:printCert")}>
                    <IconButton
                        aria-label="Print Certificates"
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
                    {
                        (multiButton.options) ? (multiButton.options.map((item, idx) => (
                            <MenuItem onClick={item.eventHandler} key={idx}>
                                <PictureAsPdfIcon color="primary" /> {item.menuLabel}
                            </MenuItem>
                        ))) : ""
                    }
                </Menu>
            </div>;

    }

    return (
        <div className="flex items-center">
            {mobileScreen ? null : <div className="flex-grow"></div>}
            {previewEventHandler && <Tooltip title={t("buttons:preview")}>
                <IconButton aria-label="Preview Details" type="button" color="primary" onClick={previewEventHandler} >
                    <DescriptionIcon />
                </IconButton>
            </Tooltip>}

            {downloadDocsEventHandler && <Tooltip title={t("buttons:dlSuppDocs")}>
                <IconButton aria-label="Download Supporting Documents" type="button" color="primary" onClick={downloadDocsEventHandler} >
                    <AttachmentIcon />
                </IconButton>
            </Tooltip>}

            {children}

            {printCert && <Tooltip title={t("buttons:print") + ` ${printCert.what ? printCert.what : ''}`}>
                <IconButton aria-label="Print" type="button" color="primary" onClick={printCert.event} >
                    <PictureAsPdfIcon />
                </IconButton>
            </Tooltip>
            }

            {generateCert && <Tooltip title={t("buttons:generate") + ` ${generateCert.what ? generateCert.what : ''}`}>
                <IconButton aria-label="Print" type="button" color="primary" onClick={generateCert.event} >
                    <CropRotateIcon />
                </IconButton>
            </Tooltip>
            }

            {multiBtn}


            {/*to={returnPath}
            {returnPath && <Link  onClick={returnHandler}>
                <Tooltip title={t("buttons:return")}>
                    <IconButton>
                        <CallMissedOutgoingIcon color="primary" sx={{ fontSize: 30 }}/>
                    </IconButton>
                </Tooltip>
            </Link>
            }*/}

            {
                returnHandler && <Tooltip title={t("buttons:return")}>
                    <IconButton onClick={returnHandler} >
                        <CallMissedOutgoingIcon color="primary"  sx={{ fontSize: 30 }} />
                    </IconButton>
                </Tooltip>
            }

            {recoveryPath && <Link to={recoveryPath}>
                <Tooltip title={t("buttons:recovery")}>
                    <IconButton>
                        <RotateLeftIcon color="primary" />
                    </IconButton>
                </Tooltip>
            </Link>
            }

            {clonePath && <Tooltip title={t("buttons:clone")}>
                <IconButton onClick={clonePath} >
                    <FileCopyIcon color="primary" />
                </IconButton>
            </Tooltip>
            }

            {amendPath && <Link to={amendPath}>
                <Tooltip title={t("buttons:amend")}>
                    <IconButton>
                        <EditIcon color="primary" />
                    </IconButton>
                </Tooltip>
            </Link>
            }

            {queryPath && <Link to={queryPath?.path}>
                <Tooltip title={t("buttons:query")}>
                    <IconButton>
                        <Badge color="error" badgeContent={queryPath?.count} max={100}>
                            <LiveHelpIcon color="primary" />
                        </Badge>
                    </IconButton>
                </Tooltip>
            </Link>
            }

            {remarkPath && (
                <Tooltip title={t("buttons:message")}>
                    <IconButton onClick={remarkPath.onClick}>
                        <Badge color="error" badgeContent={remarkPath.count} max={100}>
                            <Comment color="primary" />
                        </Badge>
                    </IconButton>
                </Tooltip>
            )}

            {editPath && <Link to={editPath}>
                <Tooltip title={t("buttons:edit")}>
                    <IconButton>
                        <EditIcon color="primary" />
                    </IconButton>
                </Tooltip>
            </Link>
            }

            {
                copyPath && <Link to={copyPath}>
                    <Tooltip title={t("buttons:duplicate")}>
                        <IconButton>
                            <FileCopyIcon color="primary" />
                        </IconButton>
                    </Tooltip>
                </Link>
            }

            {
                cloneHandler && <Tooltip title={t("buttons:cloneToAd")}>
                    <IconButton onClick={cloneHandler} >
                        <FileCopyIcon color="primary" />
                    </IconButton>
                </Tooltip>
            }

            {
                activatePath && <Link to={activatePath}>
                    <Tooltip title={t("buttons:activate")}>
                        <IconButton>
                            <LinkIcon color="primary" />
                        </IconButton>
                    </Tooltip>
                </Link>
            }

            {
                deactivatePath && <Link to={deactivatePath}>
                    <Tooltip title={t("buttons:deactivate")}>
                        <IconButton>
                            <LinkOffIcon color="primary" />
                        </IconButton>
                    </Tooltip>
                </Link>
            }


            {
                removeEventHandler && <Tooltip title={t("buttons:delete")}>
                    <IconButton onClick={removeEventHandler} >
                        <DeleteIcon color="primary" />
                    </IconButton>
                </Tooltip>
            }

            {
                manualPaymentEventHandler && <Tooltip title={t("buttons:manualPayment")}>
                    <IconButton onClick={manualPaymentEventHandler} >
                        <MonetizationOnOutlinedIcon color="primary" />
                    </IconButton>
                </Tooltip>
            }

            {
                editEventHandler && <Tooltip title={t("buttons:edit")}>
                    <IconButton onClick={editEventHandler} >
                        <EditIcon color="primary" />
                    </IconButton>
                </Tooltip>
            }

            {
                returnEventHandler && <Tooltip title={t("buttons:edit")}>
                    <IconButton onClick={returnEventHandler} >
                        <CallMissedOutgoingIcon color="primary"  sx={{ fontSize: 30 }}/>
                    </IconButton>
                </Tooltip>
            }

            {
                activeEventHandler && <Tooltip title={t("buttons:activate")}>
                    <IconButton onClick={activeEventHandler}>
                        <LinkIcon color="primary" />
                    </IconButton>
                </Tooltip>
            }

            {
                deActiveEventHandler && <Tooltip title={t("buttons:deactivate")}>
                    <IconButton onClick={deActiveEventHandler}>
                        <LinkOffIcon color="primary" />
                    </IconButton>
                </Tooltip>
            }

            {
                viewPopupEventHandler && <Tooltip title={t("buttons:view")}>
                    <IconButton onClick={viewPopupEventHandler} >
                        <VisibilityIcon color="primary" />
                    </IconButton>
                </Tooltip>
            }

            {
                proceedPath && <Link to={proceedPath}>
                    <Tooltip title={t("buttons:proceed")}>
                        <IconButton>
                            <ArrowRightAltIcon color="primary" />
                        </IconButton>
                    </Tooltip>
                </Link>
            }

            {
                proceedEventHandler && <Tooltip title={t("buttons:proceed")}>
                    <IconButton onClick={proceedEventHandler} >
                        <ArrowRightAltIcon color="primary" />
                    </IconButton>
                </Tooltip>
            }

            {
                amendEventHandler && <Link to={amendEventHandler}>
                    <Tooltip title={t("buttons:amend")}>
                        <IconButton>
                            <TextFormatOutlinedIcon color="primary" fontSize={"large"}/>
                        </IconButton>
                    </Tooltip>
                </Link>
            }

            {viewPath && <Link to={viewPath}>
                <Tooltip title={t("buttons:view")}>
                    <IconButton>
                        <VisibilityIcon color="primary" />
                    </IconButton>
                </Tooltip>
            </Link>
            }

        </div >);
}

C1DataTableActions.propTypes = {
    previewEventHandler: PropTypes.any,
    downloadDocsEventHandler: PropTypes.any,
    printCert: PropTypes.exact({
        what: PropTypes.string,
        event: PropTypes.any,
    }),
    generateCert: PropTypes.exact({
        what: PropTypes.string,
        event: PropTypes.any,
    }),
    viewPopupEventHandler: PropTypes.any,
    editPath: PropTypes.any,
    recoveryPath: PropTypes.any,
    amendPath: PropTypes.any,
    viewPath: PropTypes.any,
    queryPath: PropTypes.exact({
        path: PropTypes.any,
        count: PropTypes.any
    }),
    remarkPath: PropTypes.exact({
        path: PropTypes.any,
        count: PropTypes.any
    }),
    copyPath: PropTypes.any,
    clonePath: PropTypes.any,
    activatePath: PropTypes.any,
    deactivatePath: PropTypes.any,
    proceedPath: PropTypes.any,
    editEventHandler: PropTypes.func,
    returnEventHandler: PropTypes.func,
    removeEventHandler: PropTypes.func,
    multiButton: PropTypes.exact({
        options: PropTypes.array,
    }),
};

export default C1DataTableActions;
