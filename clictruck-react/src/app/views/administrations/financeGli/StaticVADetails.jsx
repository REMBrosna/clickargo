import { makeStyles } from "@material-ui/core";
import { Button, Grid, Tooltip } from "@material-ui/core";
import { Add} from "@material-ui/icons";
import LinkIcon from '@material-ui/icons/Link';
import NearMeOutlinedIcon from '@material-ui/icons/NearMeOutlined';
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import DataTable from "app/atomics/organisms/DataTable";
import C1InputField from "app/c1component/C1InputField";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1ListPanel from "app/c1component/C1ListPanel";
import C1PopUp from "app/c1component/C1PopUp";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";
import C1Warning from "app/c1component/C1Warning";
import useHttp from "app/c1hooks/http";
import { AccountTypes, StaticVAStates } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import { customFilterDateDisplay, formatDate } from "app/c1utils/utility";
import { ConfirmationDialog } from "matx";

const useStyles = makeStyles((theme) => ({
    backdrop: {
        zIndex: theme.zIndex.drawer + 1,
        color: '#fff',
    },
    amountCell: {
        justifyContent: 'center',
        textAlign: 'right',
        display: 'flex',
        flex: 1
    }
}));

const StaticVADetails = () => {
    // const { user } = useAuth();
    const { t } = useTranslation(["buttons", "payments", "common", "listing"]);
    // const classes = iconStyles();
    // const bdClasses = useStyles();
    // const history = useHistory();

    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();
    const [warningMessage, setWarningMessage] = useState({ open: false, msg: "" });
    const [loading, setLoading] = useState(false);
    const [showHistory, setShowHistory] = useState(false);
    const [isRefresh, setRefresh] = useState(false);
    const [filterBy, setFilterBy] = useState([
        { attribute: "history", value: "default" }
    ]);
    const [showPopUp, setShowPopUp] = useState(false)
    const [showCloseConfirm, setShowCloseConfirm] = useState(false)

    // data
    const [coOptionsArr, setCoOptionsArr] = useState([])
    const [inputData, setInputData] = useState({
        accnId: null,
        accnName: null,
        accnType: null
    })
    const [assignVA, setAssignVA] = useState(null)

    const defaultSnackbarValue = {
        success: false,
        successMsg: "",
        error: false,
        errorMsg: "",
        redirectPath: "",
    };
    const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);

    const columns = [
        {
            name: "id.acfgAccnid",
            label: t("listing:staticVAList.accountId")
        },
        {
            name: "tcoreAccn.accnName",
            label: t("listing:staticVAList.accountName"),
            options: {
                filter: true
            }
        },
        {
            name: "tcoreAccn.TMstAccnType.atypDescription",
            label: t("listing:staticVAList.accountType")
        },
        {
            name: "acfgVal",
            label: t("listing:staticVAList.staticVaNumber"),
            options: {
                filter: true
            }
        },
        {
            name: "acfgDtCreate",
            label: t("listing:staticVAList.dateCreate"),
            options: {
                filter: true,
                filterType: 'custom',
                customFilterListOptions: {
                    render: v => v.map(l => l),
                    update: (filterList, filterPos, index) => {
                        filterList[index].splice(filterPos, 1);
                        return filterList;
                    }
                },
                filterOptions: {
                    display: customFilterDateDisplay
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                }
            }
        },
        {
            name: "acfgDtLupd",
            label: t("listing:staticVAList.dateUpdated"),
            options: {
                filter: true,
                filterType: 'custom',
                customFilterListOptions: {
                    render: v => v.map(l => l),
                    update: (filterList, filterPos, index) => {
                        filterList[index].splice(filterPos, 1);
                        return filterList;
                    }
                },
                filterOptions: {
                    display: customFilterDateDisplay
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                }
            }
        },
        {
            name: "acfgStatus",
            label: t("listing:staticVAList.status"),
            options: {
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: Object.keys(StaticVAStates),
                    renderValue: v => {
                        return StaticVAStates[v].desc;
                    }
                },
                customFilterListOptions: {
                    render: v => {
                        return StaticVAStates[v].desc;
                    }
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return getStatusDesc(value);
                }
            }
        },
    ]

    useEffect(() => {
        if (showHistory) {
            setFilterBy([{ attribute: "history", value: "history" }]);
        } else {
            setFilterBy([{ attribute: "history", value: "default" }])
        }
    }, [showHistory]);

    React.useEffect(() => {
        if (!isLoading && !error && res && !validation) {
            switch (urlId) {
                case "getCoOptions": {
                    const data = res?.data?.data
                    setCoOptionsArr(data)
                    setShowPopUp(true)
                    break;
                }
                case "getStaticVA": {
                    const data = res?.data?.data
                    setAssignVA(data)
                    break
                }
                case "submitVA": {
                    setInputData({
                        accnId: null,
                        accnName: null,
                        accnType: null
                    })
                    setAssignVA(null)
                    setRefresh(true)
                    setShowPopUp(false)
                    setTimeout(() => setRefresh(false), 500)

                    break;
                }
                default:
                    break;
            }
        }
    }, [urlId, isLoading, error, res])

    const handleInputChange = (e) => {
        const { name, value } = e.target

        if (value !== undefined) {
            const data = coOptionsArr.find((item, i) => {
                return item.accnId === value
            })
            setInputData({
                accnId: data?.accnId,
                accnName: data?.accnName,
                accnType: AccountTypes.ACC_TYPE_CO.desc
            })
        } else {
            setInputData({
                accnId: null,
                accnName: null,
                accnType: null
            })
        }
    }

    const handleGenerateVa = (id) => {
        sendRequest(`/api/v1/clickargo/clictruck/accnConfig/staticVA/generateVA/${id}`, "getStaticVA", "get")
    }

    const handleSubmitChange = () => {
        if (assignVA !== null && inputData.accnId !== null) {
            const data = coOptionsArr.find((item, i) => item?.accnId === inputData.accnId)
            const reqBody = {
                id: {
                    acfgAccnid: inputData?.accnId,
                    acfgKey: null
                },
                acfgVal: assignVA,
                acfgDesc: null,
                acfgValidFromDt: null,
                acfgValidToDt: null,
                acfgSeq: null,
                acfgStatus: null,
                acfgDtCreate: null,
                acfgUidCreate: null,
                acfgDtLupd: null,
                acfgUidLupd: null,
                tcoreAccn: {
                    ...data
                }
            }
            sendRequest("api/v1/clickargo/clictruck/accnConfig/staticVA", "submitVA", "post", reqBody)

        } else {
            setWarningMessage({ open: true, msg: t("listing:staticVAList.incomplete") });
        }
    }

    const handleShowPopup = () => {
        if (showPopUp === true) {
            if (assignVA !== null) {
                setShowCloseConfirm(true)
                // setWarningMessage({ open: true, msg: "You have to submit the VA!" });
            } else {
                setShowPopUp(false)
                setAssignVA(null)
                setInputData({
                    accnId: null,
                    accnName: null,
                    accnType: null
                })
            }
        } else {
            sendRequest("/api/v1/clickargo/clictruck/accnConfig/staticVA/accnVAEnable", "getCoOptions", "get")
        }
    }

    const handleWarningAction = (e) => {
        setWarningMessage({ open: false, msg: "", hlMsg: "", subMsg: "" });
    };

    const handleCloseConfirmation = () => {
        setShowCloseConfirm(false)
        setShowPopUp(false)
        setAssignVA(null)
        setInputData({
            accnId: null,
            accnName: null,
            accnType: null
        })
    }
    const toggleHistory = (filter) => {
        setLoading(true);
        setRefresh(false);
        setShowHistory(filter === 'history' ? true : false);
        setTimeout(() => setRefresh(true), 500);
        setTimeout(() => setLoading(false), 500);
    }

    return (
        <React.Fragment>
            <C1ListPanel
                routeSegments={[{ name: t("listing:staticVAList.title") },]}
                guideId="clicdo.truck.users.list"
                title={t("listing:staticVAList.title")}>

                <DataTable
                    url="/api/v1/clickargo/clictruck/accnConfig/staticVA"
                    columns={columns}
                    title=""
                    defaultOrder="acfgDtCreate"
                    defaultOrderDirection="desc"
                    isServer={true}
                    isShowViewColumns={true}
                    isShowDownload={true}
                    isShowPrint={true}
                    isShowFilter={true}
                    isRefresh={isRefresh}
                    isShowFilterChip
                    filterBy={filterBy}
                    guideId={""}
                    customRowsPerPage={[10, 20]}
                    // showActiveHistoryButton={toggleHistory}
                    showAddButton={[{
                        label: t("listing:staticVAList.newVA").toUpperCase(),
                        action: handleShowPopup,
                        icon: <Add />
                    }]}
                />

            </C1ListPanel>

            <ConfirmationDialog
                open={showCloseConfirm}
                onYesClick={handleCloseConfirmation}
                onConfirmDialogClose={() => setShowCloseConfirm(false)}
                title={t("listing:coJob.popup.confirmation")}
                text={t("listing:staticVAList.reqClose")}
            />

            <C1PopUp
                title={t("listing:staticVAList.details")}
                openPopUp={showPopUp}
                setOpenPopUp={handleShowPopup}
                maxWidth={'sm'}
                actionsEl={
                    <Tooltip title={t("buttons:submit")}>
                        <Button style={{ float: 'right' }} onClick={handleSubmitChange}>
                            <NearMeOutlinedIcon fontSize="large" color="primary" />
                        </Button>
                    </Tooltip>
                }
            >
                <Grid>
                    {assignVA !== null ?
                        <C1InputField
                            required={true}
                            name={inputData?.accnId}
                            label={t("listing:staticVAList.comAccn")}
                            value={inputData?.accnName}
                            disabled
                        // value={inputData?.accnType !== null ? inputData?.accnType: ""}
                        />
                        :
                        <C1SelectAutoCompleteField
                            required={true}
                            name={inputData?.accnId}
                            label={t("listing:staticVAList.comAccn")}
                            value={inputData?.accnId}
                            onChange={(e, name, value) => handleInputChange({ target: { name, value: value?.value } })}
                            optionsMenuItemArr={coOptionsArr?.map((item, i) => {
                                return {
                                    value: item.accnId,
                                    desc: item.accnName
                                }
                            })}
                        />
                    }
                    <C1InputField
                        name="id"
                        disabled
                        value={inputData?.accnType !== null ? inputData?.accnType : ""}
                        label={t("listing:staticVAList.accnType")}
                    />
                    <Grid direction="row" container justifyContent="space-evenly" alignItems="center">
                        <Grid item xs={10}>
                            <C1InputField
                                label={t("listing:staticVAList.staticVaNumber")}
                                disabled
                                value={assignVA ? assignVA : null}
                            />
                        </Grid>
                        <Grid item xs={2} container justifyContent="flex-end">
                            {/* <Tooltip title={"Request for Static VA"}>
                                <IconButton color='primary' disabled={assignVA === null && inputData?.accnId !== null ? false : true} onClick={() => handleGenerateVa(inputData?.accnId)}>
                                    <LinkIcon />
                                </IconButton>
                            </Tooltip> */}
                            <C1LabeledIconButton
                                color={assignVA === null && inputData?.accnId !== null ? 'primary' : 'secondary'}
                                disabled={assignVA === null && inputData?.accnId !== null ? false : true}
                                tooltip={assignVA === null && inputData?.accnId !== null ? t("listing:staticVAList.reqVaMsg") : ""}
                                label={t("listing:staticVAList.reqVa")}
                                action={() => handleGenerateVa(inputData?.accnId)}>
                                <LinkIcon />
                            </C1LabeledIconButton>
                        </Grid>
                    </Grid>
                </Grid>
            </C1PopUp>

            <C1Warning warningMessage={warningMessage} handleWarningAction={handleWarningAction} />
        </React.Fragment>
    )
}

export default StaticVADetails