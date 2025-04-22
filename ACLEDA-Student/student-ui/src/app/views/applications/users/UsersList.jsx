import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next"
import C1DataTable from 'app/c1component/C1DataTable';
import C1DataTableActions from 'app/c1component/C1DataTableActions';
import C1ListPanel from "app/c1component/C1ListPanel";
import useHttp from "app/c1hooks/http";
import ConfirmationDialog from "matx/components/ConfirmationDialog";
import {formatDate} from "../../../c1utils/utility";
import useAuth from "../../../hooks/useAuth";
import C1PopUp from "../../../c1component/C1PopUp";
import UserPopUpMessage from "../messagePopUp/PrivateChatPopUpMessage";
import {Snackbar} from "@material-ui/core";
import C1Alert from "../../../c1component/C1Alert";
import {useHistory} from "react-router-dom";
import {MatxLoading} from "../../../../matx";
import {getStatusDesc} from "../../../c1utils/statusUtils";

const UsersList = () => {

    const auth = useAuth();
    const isAdmin = auth?.user?.roles?.some(role => role?.name === 'ROLE_ADMIN')
    const isStudent = auth?.user?.roles?.some(role => role?.name === 'ROLE_STUDENT')
    const { t } = useTranslation(["student", "common"]);
    const [confirm, setConfirm] = useState({ id: null });
    const [open, setOpen] = useState(false);
    const [dtRefresh, setDtRefresh] = useState(false);
    const [loading, setLoading] = useState(false);
    const [confirmAction, setConfirmAction] = useState("");
    const [openPopUp, setOpenPopUp] = useState(false);
    const [errors, setErrors] = useState({});
    const [popUpUsername, setPopUpUsername] = useState("");
    const [userSender, setSender] = useState("");
    const [isRefresh, setRefresh] = useState(false);
    const [message, setMessage] = useState([]);
    const history = useHistory();
    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    const [validationErrors, setValidationErrors] = useState([]);
    const { isLoading, res, validation, error, urlId, sendRequest } = useHttp();
    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: "top",
        horizontal: "center",
        msg: "",
        severity: "success",
        redirectUrl: ''
    });

    console.log("dtRefresh", dtRefresh)
    const columns = [
        {
            name: "id",
            label: "ID",
        },
        {
            name: "username",
            options: {
                filter: false,
                display: false,
                viewColumns: false,
            },
        },
        {
            name: "messageCount",
            options: {
                filter: false,
                display: false,
                viewColumns: false,
            },
        },
        {
            name: "fullName",
            label: "Name",
            options: {
                filter: true,
            },
        },
        {
            name: "dtOfBirth",
            label: "Date Of Birth",
            options: {
                filter: true,
                customBodyRender: (value) => {
                    return formatDate(value, true);
                }
            },
        },
        {
            name: "gender",
            label: "Gender",
            options: {
                filter: true,
            },
        },
        {
            name: "address",
            label: "Address",
            options: {
                filter: true,
            },
        },
        {
            name: "conNumber",
            label: "Phone Number",
            options: {
                filter: true
            },
        },
        {
            name: "email",
            label: "Email",
            options: {
                filter: false
            },
        },
        {
            name: "usrDtCreate",
            label: "Created Date",
            options: {
                filter: true,
                customBodyRender: (value) => {
                    return formatDate(value, true);
                }
            },
        },
        {
            name: "status",
            label: "Status",
            options: {
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return getStatusDesc(value);
                },
            },
        },
        {
            name: "action",
            label: " ",
            options: {
                filter: false,
                display: true,
                viewColumns: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    let remarkCount = tableMeta.rowData[2];
                    return (<C1DataTableActions
                        editPath={`/student/applicationStudent/edit/${tableMeta.rowData[0]}`}
                        viewPath={`/student/applicationStudent/view/${tableMeta.rowData[0]}`}
                        removeEventHandler={!isStudent ? (e) => handleDeleteConfirm(e, tableMeta.rowData[0]) : null}
                        // remarkPath={(e) => handleMessagePopUp(e)}
                        remarkPath={
                            isAdmin || remarkCount && remarkCount > 0
                                ? {onClick: (e) => handleMessagePopUp(e, tableMeta.rowData[1]),
                                    count: remarkCount}
                                : null
                        }
                    />);
                },
            },
        },
    ];

    useEffect(() => {
        setLoading(false)
        if (isStudent){
            const receiver = auth.user.username;
            sendRequest(`/receiver/${receiver}`, 'FETCH', 'GET', null);
        }
        console.log("urlId", urlId)
        if (urlId === "DELETE") {
            setOpen(false);
            setDtRefresh(true);
            setLoading(false);
        }
    }, [userSender]);


    useEffect(() => {
        if (!isLoading && !error && res) {
            setLoading(false);
            switch (urlId) {
                case "FETCH":{
                    setMessage(res.data);
                    console.log("data", res.data?.[0]);
                    setSender(res.data?.[0]?.userSender?.username);
                    break;
                }
                case "DELETE": {
                    setLoading(false);
                    setOpen(false); // ✅ close the confirmation dialog
                    setSnackBarState({
                        ...snackBarState,
                        open: true,
                        msg: t("common:common.msg.deleted"),
                        severity: "success"
                    });
                    setSubmitSuccess(true);
                    break;
                }

                default:
                    break;
            }
            setRefresh(true);
            setDtRefresh(true);
        }
        if (validation) {
            //currently only tin is validated backend. TODO will have to change this to implement properly.
            setValidationErrors({ ...validation });
            setLoading(false);
        }

        if (error) {
            setLoading(false);
        }
        // eslint-disable-next-line
    }, [isLoading, res, error, urlId, history]);
    const handleConfirmAction = (e) => {
        setDtRefresh(false);
        if (confirmAction === "DELETE") {
            handleDeleteHandler(e);
        }
    }
    const handleDeleteHandler = (e) => {
        if (confirm && !confirm.id)
            return;
        setLoading(true);
        sendRequest("api/v1/users/" + confirm.id, "DELETE", "DELETE", {})
    }

    const handleDeleteConfirm = (e, id) => {
        setConfirmAction("DELETE");
        e.preventDefault();
        setConfirm({ ...confirm, id: id });
        setOpen(true);
    }

    const handleOnClose = (e) => {
        setOpenPopUp(false)
    }
    const handleMessagePopUp = (e, key) => {
        setLoading(false)
        setPopUpUsername(key)
        e.stopPropagation()
        setOpenPopUp(true);
        setErrors({});
    };
    const handleSnackbarClose = () => {
        setSnackBarState({ ...snackBarState, open: false });

        if (snackBarState && snackBarState.redirectUrl && snackBarState.severity === 'success') {
            //only redirect if it's success
            let url = snackBarState.redirectUrl;
            history.push(url);
        }
    };
    let snackBar;
    if (isSubmitSuccess) {
        const anchorOriginV = snackBarState.vertical;
        const anchorOriginH = snackBarState.horizontal;

        snackBar = (
            <Snackbar
                anchorOrigin={{ vertical: anchorOriginV, horizontal: anchorOriginH }}
                open={snackBarState.open}
                onClose={handleSnackbarClose}
                autoHideDuration={3000}
                key={anchorOriginV + anchorOriginH}
            >
                <C1Alert onClose={handleSnackbarClose} severity={snackBarState.severity}>
                    {snackBarState.msg}
                </C1Alert>
            </Snackbar>
        );
    }
    return (<React.Fragment>
            {snackBar}
            {isLoading && <MatxLoading />}
            {confirm && confirm.id && (
                <ConfirmationDialog
                    title={t("common:confirmMsgs.confirm.title")}
                    open={open}
                    text={t("common:confirmMsgs.confirm.content", {
                        action: confirmAction,
                        type: confirm.type,
                        id: confirm.id
                    })}
                    onYesClick={() => handleConfirmAction()}
                    onConfirmDialogClose={() => setOpen(false)}
                />
            )}

            <C1PopUp
                title={"Message Alert"}
                openPopUp={openPopUp}
                setOpenPopUp={setOpenPopUp}
                maxWidth="lg"
                maxHeight="500px"
                overflowY="auto"
                flex-wrap="none"
                customStyles={{
                    backgroundColor: "rgba(244,244,244,0.11)",
                    borderRadius: "12px",
                    boxShadow: "0 4px 10px rgba(0, 0, 0, 0.2)",
                    bottom: "100px",
                }}
                disableCloseButton={true}
            >
                <UserPopUpMessage
                    auth={auth}
                    sender={userSender}
                    popUpUsername={popUpUsername}
                    usrMessage={message}
                    errors={errors}
                    handleOnClose={handleOnClose}
                />
            </C1PopUp>
        <C1ListPanel
            routeSegments={[
                { name: "Application Student" },
            ]}>
            <C1DataTable 
                url={'/api/v1/users'}
                columns={columns}
                title={"Student List"}
                defaultOrder="usrDtCreate"
                isServer={true}
                isShowDownload={false}
                isShowPrint={false}
                isRowSelectable={false}
                isShowToolbar
                isRefresh={dtRefresh}
                filterBy={[
                    { attribute: "username" , value : auth?.user?.username}
                ]}
                // defaultOrderDirection={"asc"}
                showAdd={ isAdmin && {
                 path: "/student/applicationStudent/new/0"}
                }
            />
        </C1ListPanel>
    </React.Fragment>
    );
};

export default UsersList;
