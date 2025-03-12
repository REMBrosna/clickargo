import { Grid, makeStyles } from "@material-ui/core";
import SettingsBackupRestoreOutlinedIcon from '@material-ui/icons/SettingsBackupRestoreOutlined';
import { useEffect, useState } from "react";
import React from "react"
import { useTranslation } from "react-i18next";

import DataTable from "app/atomics/organisms/DataTable";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import useHttp from "app/c1hooks/http";
import { RecordStatus, RegistrationStatus } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import { formatDate } from "app/c1utils/utility";
import { customFilterDateDisplay } from "app/c1utils/utility";
import { MatxLoading } from "matx";

import ActionPopUp from "../popup/ActionPopUp";

const SuspendAccount = (props) => {
    const { t } = useTranslation(["admin", "buttons", "common"]);
    const [isRefresh, setRefresh] = useState(false);
    const { isLoading, res, error, urlId, sendRequest } = useHttp();
    const { setRefreshPage } = props;

    const [loading, setLoading] = useState(false);
    const [showHistory, setShowHistory] = useState(false);
    const [popUp, setPopUp] = useState(false);
    const [accnId, setAccnId] = useState("");
    const [popUpAction, setPopUpAction] = useState("Resume");
    const [filterBy, setFilterBy] = useState([{ attribute: "history", value: "default" }]);

    const useStyles = makeStyles((theme) => ({
        scrollbarStyles: {
            overflowY: "auto",
            maxHeight: "400px",
            msOverflowStyle: "none",
            scrollbarWidth: 0,
            scrollbarColor: "#fff"
        },
        dateRowContainer: {
            paddingLeft: 16,

        }
    }))
    const bdClasses = useStyles();

    useEffect(() => {
        if (showHistory) {
            setFilterBy([{ attribute: "history", value: "history" }]);
        } else {
            setFilterBy([{ attribute: "history", value: "default" }])
        }
    }, [showHistory]);

    useEffect(() => {
        if (!error && res) {
            if (urlId === 'getForDeActive') {
                const bodyReq = { ...res.data, accnStatus: "I" };
                sendRequest("/api/co/ccm/entity/accn/" + res.data.accnId, "deActive", "put", bodyReq);
            } else if (urlId === 'getForActive') {
                const bodyReq = { ...res.data, accnStatus: "A" };
                sendRequest("/api/co/ccm/entity/accn/" + res.data.accnId, "deActive", "put", bodyReq);
            }
            if (urlId === 'active' || urlId === 'deActive') {
                setRefresh(true);
                document.body.style.cursor = 'default';
            }
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isLoading, res, error, urlId]);

    const toggleHistory = (filter) => {
        setLoading(true);
        setRefresh(false);
        setShowHistory(filter === 'history' ? true : false);
        setTimeout(() => setRefresh(true), 500);
        setTimeout(() => setLoading(false), 500);
    }

    // const handleDeActiveHandler = (id) => {
    //     setRefresh(false);
    //     sendRequest(`/api/co/ccm/entity/accn/${id}`, "getForDeActive", "get", {});
    // }

    // const handleActiveHandler = (id) => {
    //     setRefresh(false);
    //     sendRequest(`/api/co/ccm/entity/accn/${id}`, "getForActive", "get", {});
    // }

    const togglePopUp = (accnPopUp, act) => {
        if (accnPopUp) {
            setAccnId(accnPopUp);
            setPopUpAction(act);
        }
        setPopUp(popUp ? false : true);
    }

    const columns = [
        {
            name: "accnId", // field name in the row object
            label: t("account.list.accnId"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
            },
        },
        {
            name: "accnName",
            label: t("account.list.accnName"),
            options: {
                filter: true,
            },
        },
        {
            name: "TMstAccnType.atypDescription",
            label: t("account.list.accnType"),
            options: {
                filter: true,
            },
        },
        {
            name: "accnDtReg",
            label: t("account.list.accnDtCreate"),
            options: {
                filter: true,
                filterType: 'custom',
                display: true,
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
                    return <div className={bdClasses.dateRowContainer}>
                        {formatDate(value, true)}
                    </div>

                }
            },
        },
        {
            name: "accnDtSusp",
            label: t("account.list.sspdt"),
            options: {
                filter: true,
                filterType: 'custom',
                display: true,
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
                    return <div className={bdClasses.dateRowContainer}>
                        {formatDate(value, true)}
                    </div>

                }
            },
        },
        {
            name: "accnDtReins",
            label: t("account.list.rsmdt"),
            options: {
                filter: true,
                filterType: 'custom',
                display: true,
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
                    return <div className={bdClasses.dateRowContainer}>
                        {formatDate(value, true)}
                    </div>

                }
            },
        },
        {
            name: "accnStatus",
            label: t("account.list.accnStatus"),
            options: {
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: [
                        RecordStatus.ACTIVE.code,
                        RegistrationStatus.EXPIRED.code,
                        RecordStatus.SUSPENDED.code
                    ],
                    renderValue: v => {
                        switch (v) {
                            case RecordStatus.ACTIVE.code: return RecordStatus.ACTIVE.desc;
                            case RegistrationStatus.EXPIRED.code: return RegistrationStatus.EXPIRED.desc;
                            case RegistrationStatus.PENDING_ACCCN_ACTIVATION.code: return RegistrationStatus.PENDING_ACCCN_ACTIVATION.desc;
                            case RecordStatus.SUSPENDED.code: return RecordStatus.SUSPENDED.desc;
                            case RecordStatus.DEACTIVE.code: return RecordStatus.DEACTIVE.desc;
                            default: break;
                        }
                    }
                },
                customFilterListOptions: {
                    render: v => {
                        switch (v) {
                            case RecordStatus.ACTIVE.code: return RecordStatus.ACTIVE.desc;
                            case RegistrationStatus.PENDING_ACCCN_ACTIVATION.code: return RegistrationStatus.PENDING_ACCCN_ACTIVATION.desc;
                            case RecordStatus.SUSPENDED.code: return RecordStatus.SUSPENDED.desc;
                            case RecordStatus.DEACTIVE.code: return RecordStatus.DEACTIVE.desc;
                            default: break;
                        }
                    }
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return getStatusDesc(value);
                }
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
                    const status = tableMeta.rowData[6];
                    return (
                        <Grid container direction="row" justifyContent="flex-end" alignItems="center">
                            {!showHistory &&
                                <C1LabeledIconButton
                                    tooltip={t("buttons:unsuspend")}
                                    label={t("buttons:unsuspend")}
                                    action={(status === RecordStatus.SUSPENDED.code) ? () => togglePopUp(tableMeta.rowData[0], "Resume") : null}>
                                    <SettingsBackupRestoreOutlinedIcon />
                                </C1LabeledIconButton>
                            }
                        </Grid>
                    )

                },
            },
        },
    ];

    return (
        <React.Fragment>
            {loading && <MatxLoading />}
            <DataTable
                url={`/api/v1/clickargo/clictruck/account/accnResumption`}
                filterBy={filterBy}
                columns={columns}
                defaultOrder="accnDtCreate"
                defaultOrderDirection="desc"
                isRefresh={isRefresh}
                showActiveHistoryButton={toggleHistory}
                isShowFilterChip
                isShowFilter={true}
            />
            <ActionPopUp
                openPopUp={popUp}
                action={popUpAction}
                togglePopUp={togglePopUp}
                // handleSelected={handleSelectedRate}
                accnId={accnId}
                setRefresh={setRefresh}
                setRefreshPage={setRefreshPage}
            />
        </React.Fragment>
    )
}

export default SuspendAccount;