import { Button, Grid, makeStyles, Tooltip } from "@material-ui/core";
import { NearMeOutlined } from "@material-ui/icons";
import Add from '@material-ui/icons/AddOutlined';
import EditOutlined from '@material-ui/icons/EditOutlined';
import LinkOffIcon from "@material-ui/icons/LinkOffOutlined";
import LinkIcon from "@material-ui/icons/LinkOutlined";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import DataTable from "app/atomics/organisms/DataTable";
import C1InputField from "app/c1component/C1InputField";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1ListPanel from "app/c1component/C1ListPanel";
import C1PopUp from "app/c1component/C1PopUp";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";
import useHttp from "app/c1hooks/http";
import { RecordStatus } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import { dialogStyles } from "app/c1utils/styles";
import { customFilterDateDisplay, formatDate } from "app/c1utils/utility";
import useAuth from "app/hooks/useAuth";

export default function BankAccountList() {

    const { user } = useAuth();
    const isSpOpAdmin = user.role.includes("SP_OP_ADMIN");
    //redirect to homepage if role is not SP OP ADMIN
    if (!isSpOpAdmin) { window.location.replace("/") };

    const [bankList, setBankList] = useState([]);


    const { isLoading, res, validation, error, urlId, sendRequest } = useHttp();
    useEffect(() => {
        if (!isLoading && !error && res && !validation) {
            switch (urlId) {
                case "getAccn":
                        const acfgVal = res?.data?.acfgVal;
                        const parts = acfgVal?.split(":");
                        const bankCode = parts?.[0];
                        const bankNo = parts?.[1];
                        const bankName = parts?.[2];

                        setInputValue({
                            ...inputValue,
                            accnType: {
                                value: res?.data?.tcoreAccn?.TMstAccnType?.atypId,
                                desc: res?.data?.tcoreAccn?.TMstAccnType?.atypDescription
                            },
                            accnId: {
                                value: res?.data?.tcoreAccn?.accnId,
                                desc: res?.data?.tcoreAccn?.accnName
                            },
                            bankNo: bankNo,
                            bankCode: {
                                value: bankCode,
                                desc: bankCode
                            },
                            bankName: bankName,
                            initialData: res?.data,
                        });

                    break;
                case "getAccnNew":
                        console.log("getaccnnew", res);
                        const accnId = res?.data?.accnId;

                        setInputValue({
                            ...inputValue,
                            accnType: {
                                value: res?.data?.TMstAccnType?.atypId,
                                desc: res?.data?.TMstAccnType?.atypDescription
                            },
                            initialData: {
                                id: {
                                    acfgAccnid: accnId,
                                    acfgKey: "BANK_DETAIL",
                                },
                                acfgVal: "",
                            }
                        })
                    break;
                case "getAccnForRemove":
                        sendRequest(`/api/v1/clickargo/clictruck/accnConfig/bankDetail/ ${res?.data?.id?.acfgAccnid}:BANK_DETAIL"`, "submitRemove", "PUT", { ...res?.data, acfgStatus: "I" });
                    break;
                case "getAccnForActivate":
                        sendRequest(`/api/v1/clickargo/clictruck/accnConfig/bankDetail/${res?.data?.id?.acfgAccnid}:BANK_DETAIL`, "submitRemove", "PUT", { ...res?.data, acfgStatus: "A" });
                    break;
                case "submitRemove":
                        setTableRefresh(true);
                    break;
                case "submitAcfgVal":
                        togglePopUp();
                        setTableRefresh(true);
                    break;
                case "getBankList":
                        // console.log("getBankList",res?.data);
                        setBankList(res?.data);
                        break;
                default:
                    break;
            }
        }
    }, [isLoading, res, error, validation]);


    useEffect(() => {
        sendRequest('/api/co/master/entity/bank', "getBankList", "get");
    }, [])

    const filterByAccnTypeTO = { attribute: "TCoreAccn.TMstAccnType.atypId", value: "ACC_TYPE_TO" };
    const filterByAccnStatusA = { attribute: "acfgStatus", value: "A" };
    const filterByAccnStatusD = { attribute: "acfgStatus", value: "D" };
    const filterByAccnStatusI = { attribute: "acfgStatus", value: "I" };
    const filterByAcfgKey = { attribute: "id.acfgKey", value: "BANK_DETAIL" };
    // const defaultFilter = { filterByAccnTypeTO, filterByAccnStatusA, filterByAcfgKey };
    const defaultFilter =
        [{ attribute: "id.acfgKey", value: "BANK_DETAIL" }, { attribute: "TCoreAccn.TMstAccnType.atypId", value: "ACC_TYPE_TO" }, { attribute: "acfgStatus", value: "A" }];
    const historyFilter =
        [{ attribute: "id.acfgKey", value: "BANK_DETAIL" }, { attribute: "TCoreAccn.TMstAccnType.atypId", value: "ACC_TYPE_TO" }, { attribute: "acfgStatus", value: "I" }];

    // console.log("default filter", defaultFilter);
    const [filterBy, setFilterBy] = useState(defaultFilter);

    const [tableRefresh, setTableRefresh] = useState(false);
    useEffect(() => {
        if (tableRefresh === true) {
            setTimeout(() => setTableRefresh(false), 100);
        }
    }, [tableRefresh]);

    const [popupAccnId, setPopUpAccnId] = useState('');
    const [accnIdDisable, setAccnIdDisable] = useState(false);

    const inputErrorDefault = { bankName: false, bankNo: false, bankCode: false };
    const [inputError, setInputError] = useState(inputErrorDefault);

    const [showHistory, setShowHistory] = useState(false);
    const toggleHistory = (filter) => {

        if (filter === "history") {
            setShowHistory(true);
            setFilterBy(historyFilter);
        } else {
            setShowHistory(false);
            setFilterBy(defaultFilter);
        }
        setTableRefresh(true);
    };

    const inputValueDefault =
    {
        accnId: "",
        accnType: "",
        bankCode: "",
        bankNo: "",
        bankName: "",
    };
    const [inputValue, setInputValue] = useState(inputValueDefault);

    const [newData, setNewData] = useState(false);
    const [popUp, setPopUp] = useState(false);
    const togglePopUp = () => {
        if (popUp) {
            setInputValue(inputValueDefault);
            setAccnIdDisable(false);
            setNewData(false);
            setPopUp(false);
            // setPopUpAccnId('');

        } else {
            setPopUp(true);
        }
    };

    const handleChange = (e, name, value) => {
        if (e.target.name != null) {
            name = e.target.name;
            value = e.target.value;
        }
        // console.log("handleChange",name, value);
        setInputValue({ ...inputValue, [name]: value });

        if (name === "accnId") {
            const id1 = value?.value;
            const id2 = "BANK_DETAIL";
            const url = "/api/co/ccm/entity/accn/" + id1;
            // const url="/api/v1/clickargo/clictruck/accnConfig/"+id1+":"+id2;
            const urlId = "getAccnNew";
            const method = "GET";
            const body = "";
            console.log("sendrequest", url, urlId, method, body);
            sendRequest(url, urlId, method, body);
        }

        if (name === "bankCode") {
            setTimeout(() => console.log(inputValue), 3000);
        }

    };

    const handleEditDetail = (e, accnId) => {
        console.log("edit", e, accnId);
        setAccnIdDisable(true);
        const id1 = accnId;
        const id2 = "BANK_DETAIL";
        const url = "/api/v1/clickargo/clictruck/accnConfig/bankDetail/" + id1 + ":" + id2;
        const urlId = "getAccn";
        const method = "GET";
        const body = "";
        console.log("sendrequest", url, urlId, method, body);
        sendRequest(url, urlId, method, body);

        setPopUp(true);

    }

    const handleRemoveDetail = (e, accnId) => {
        console.log("remove", accnId);
        console.log("edit", e, accnId);
        setAccnIdDisable(true);
        const id1 = accnId;
        const id2 = "BANK_DETAIL";
        const url = "/api/v1/clickargo/clictruck/accnConfig/bankDetail/" + id1 + ":" + id2;
        const urlId = "accnDelete"
        const method = "DELETE"
        const body = "";
        console.log("sendrequest", url, urlId, method, body);
        sendRequest(url, urlId, method, body);
    }

    const handleDeactivateDetail = (e, accnId) => {
        setAccnIdDisable(true);
        const id1 = accnId;
        const id2 = "BANK_DETAIL";
        const url = "/api/v1/clickargo/clictruck/accnConfig/bankDetail/" + id1 + ":" + id2;
        const urlId = "getAccnForRemove";
        const method = "GET";
        const body = "";
        sendRequest(url, urlId, method, body);
    }

    const handleActivateDetail = (e, accnId) => {
        setAccnIdDisable(true);
        const id1 = accnId;
        const id2 = "BANK_DETAIL";
        const url = "/api/v1/clickargo/clictruck/accnConfig/bankDetail/" + id1 + ":" + id2;
        const urlId = "getAccnForActivate";
        const method = "GET";
        const body = "";
        console.log("sendrequest", url, urlId, method, body);
        sendRequest(url, urlId, method, body);
    }

    const handleNewDetail = () => {
        setNewData(true);
        togglePopUp();
    }

    const inputValidation = () => {

        let validation = inputErrorDefault;
        const fieldsToCheck = ['bankName', 'bankNo', 'bankCode'];
        let allFieldsValid = true;

        fieldsToCheck.forEach(field => {
            if (!inputValue[field]) {
                validation = { ...validation, [field]: true }
                allFieldsValid = false;

                console.log("validation fail", field)
            } else {
                validation = { ...validation, [field]: false }
            }
        });

        setInputError(validation);
        return allFieldsValid;

    }

    const submitInputValue = () => {

        if (inputValidation()) {

            //send request

            const submitValue = { ...inputValue.initialData, acfgVal: inputValue.bankCode.value + ":" + inputValue.bankNo + ":" + inputValue.bankName };

            const id1 = inputValue?.accnId?.value;
            const id2 = "BANK_DETAIL";
            var url = "/api/v1/clickargo/clictruck/accnConfig/bankDetail/" + id1 + ":" + id2;
            var urlId = "submitAcfgVal";
            var method = "put";
            var body = submitValue;

            if (newData) {
                const now = new Date();
                const year = now.getFullYear();
                const month = String(now.getMonth() + 1).padStart(2, '0');
                const day = String(now.getDate()).padStart(2, '0');
                const currentDateTime = Date.now();
                const endDateTime = 4070908800000;

                body = { ...submitValue, acfgValidFromDt: currentDateTime, acfgValidToDt: endDateTime, acfgDesc: "TO Merchant Bank ", acfgSeq: 1, acfgStatus: "A" }

                url = "/api/v1/clickargo/clictruck/accnConfig/bankDetail/"
                method = "post"

                // console.log("newDta", url, urlId, method, body);
            }
            // console.log(url, urlId, method, body);
            sendRequest(url, urlId, method, body);
        }

    };

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
    }));
    const bdClasses = useStyles();

    const { t } = useTranslation(["buttons", "opadmin"])

    const tableColumns =
        [{
            name: "tcoreAccn.accnId",
            label: t("opadmin:bankList.accnId")
        }, {
            name: "tcoreAccn.accnName",
            label: t("opadmin:bankList.accnName")
        }, {
            name: "tcoreAccn.TMstAccnType.atypDescription",
            label: t("opadmin:bankList.accnType")
        }, {
            name: "acfgVal",
            label: t("opadmin:bankList.bankNo"),
            options: {
                customBodyRender: (value, tableMeta, updateValue) => {
                    if (value) {
                        const parts = value.split(":");
                        const bankCode = parts[0] + " : " + parts[1];
                        return bankCode;
                    }
                }
            }
        }, {
            name: "acfgVal",
            label: t("opadmin:bankList.bankName"),
            options: {
                customBodyRender: (value, tableMeta, updateValue) => {
                    if (value) {
                        const parts = value.split(":");
                        const bankName = parts[2];
                        return bankName;
                    }
                }
            }
        }, {
            name: "acfgDtCreate",
            label: t("opadmin:bankList.acfgDtCreate"),
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
        }, {
            name: "acfgDtLupd",
            label: t("opadmin:bankList.acfgDtLupd"),
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
        }, {
            name: "acfgStatus",
            label: t("opadmin:bankList.acfgStatus"),
            options: {
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: [
                        RecordStatus.ACTIVE.code,
                        RecordStatus.INACTIVE.code,
                        // RecordStatus.DEACTIVE.code
                    ],
                    renderValue: v => {
                        switch (v) {
                            case RecordStatus.ACTIVE.code: return RecordStatus.ACTIVE.desc;
                            case RecordStatus.INACTIVE.code: return RecordStatus.INACTIVE.desc;
                            case RecordStatus.DEACTIVE.code: return RecordStatus.DEACTIVE.desc;
                            default: break;
                        }
                    }
                },
                customFilterListOptions: {
                    render: v => {
                        switch (v) {
                            case RecordStatus.ACTIVE.code: return RecordStatus.ACTIVE.desc;
                            case RecordStatus.INACTIVE.code: return RecordStatus.INACTIVE.desc;
                            case RecordStatus.DEACTIVE.code: return RecordStatus.DEACTIVE.desc;
                            default: break;
                        }
                    }
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    // console.log(value, getStatusDesc(value));
                    return getStatusDesc(value);
                }
            },
        }, {
            name: "action",
            label: t("opadmin:bankList.action"),
            options: {
                filter: false,
                sort: false,
                customHeadLabelRender: (columnMeta) => {
                    return <div style={{ textAlign: "center" }}>{columnMeta.label}</div>
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    const accnId = tableMeta?.rowData[0];
                    return (
                        <Grid container spacing={3} direction="row" justifyContent="center" alignItems="center">
                            {!showHistory &&
                                <>
                                    <Grid item xs={4}>
                                        <C1LabeledIconButton
                                            tooltip={t("buttons:edit")}
                                            label={t("buttons:edit")}
                                            action={(e) => { handleEditDetail(e, accnId) }}>
                                            <EditOutlined />
                                        </C1LabeledIconButton>
                                    </Grid>
                                    <Grid item xs={4}>
                                        <C1LabeledIconButton
                                            tooltip={t("buttons:deactivate")}
                                            label={t("buttons:deactivate")}
                                            action={(e) => { handleDeactivateDetail(e, accnId) }}>
                                            <LinkOffIcon />
                                        </C1LabeledIconButton>
                                    </Grid>
                                </>
                            }
                            {showHistory &&
                                <Grid item xs={4}>
                                    <C1LabeledIconButton
                                        tooltip={t("buttons:activate")}
                                        label={t("buttons:activate")}
                                        action={(e) => { handleActivateDetail(e, accnId) }}>
                                        <LinkIcon />
                                    </C1LabeledIconButton>
                                </Grid>
                            }
                        </Grid>
                    )
                }
            }
        }]

    const dialogClasses = dialogStyles();
    const elAction =
        <Tooltip title={t("buttons:submit")} className={dialogClasses.dialogButtonSpace} >
            <Button onClick={() => submitInputValue()} >
                <NearMeOutlined color="primary" fontSize="large" />
            </Button>
        </Tooltip>
        ;

    return (
        <C1ListPanel
            routeSegments={[{ name: t("opadmin:bankList.judul") },]}
            guideId="clicdo.truck.users.list"
            title={t("opadmin:bankList.judul")}>
            <DataTable
                url="/api/v1/clickargo/clictruck/accnConfig/bankDetail/"
                isServer={true}
                isRefresh={tableRefresh}
                filterBy={filterBy}
                columns={tableColumns}
                defaultOrder="acfgDtLupd"
                defaultOrderDirection="desc"
                showActiveHistoryButton={toggleHistory}
                showAddButton={[
                    {
                        label: t("opadmin:bankList.tambah").toUpperCase(),
                        icon: <Add />,
                        action: () => handleNewDetail()
                    }
                ]}
                isShowFilterChip={true}
            />

            <C1PopUp
                title={t("opadmin:title.bankTitle")}
                openPopUp={popUp}
                setOpenPopUp={togglePopUp}
                maxWidth={"sm"}
                actionsEl={elAction}
                accnId={popupAccnId}  >

                <C1SelectAutoCompleteField
                    label={t("opadmin:bankList.comAccn")}
                    name="accnId"
                    isServer={!accnIdDisable}
                    disabled={accnIdDisable}
                    options={{
                        // url: "/api/co/ccm/entity/accn",
                        // url: "/api/co/ccm/entity/accn/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=accnName&iColumns=2&mDataProp_1=TMstAccnType.atypId&sSearch_1=ACC_TYPE_TO",
                        url: "/api/v1/clickargo/clictruck/accn/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=desc&iSortingCols=1&mDataProp_1=TMstAccnType.atypId&sSearch_1=ACC_TYPE_TO&mDataProp_0=accnId",
                        key: "accnId",
                        id: "accnId",
                        desc: "accnName"
                    }}
                    value={inputValue?.accnId}
                    onChange={(e, name, value) => handleChange(e, name, value)}

                />
                <C1SelectAutoCompleteField
                    label={t("opadmin:bankList.accnType")}
                    name="accnType"
                    value={inputValue?.accnType}
                    options={{
                        key: "atypId",
                        id: "atypId",
                        desc: "atypDescription"
                    }}

                    disabled={true}
                />
                {/* <C1InputField
                    label="Bank Account Code"
                    name="bankCode"
                    onChange={handleChange}
                    value={inputValue?.bankCode}
                    required
                    error={inputError?.bankCode}
                /> */}
                <C1SelectAutoCompleteField
                    label={t("opadmin:bankList.bankCode")}
                    name="bankCode"
                    isServer={false}
                    optionsMenuItemArr={bankList?.map((item, i) => {
                        return {
                            value: item.bankId,
                            desc: item.bankId
                        }
                    })}
                    value={inputValue?.bankCode?.desc ? inputValue?.bankCode?.desc : inputValue?.bankCode}
                    onChange={(e, name, value) => handleChange(e, name, value)}
                    required
                    error={inputError?.bankCode}
                />
                <C1InputField
                    label={t("opadmin:bankList.bankNo")}
                    name="bankNo"
                    onChange={handleChange}
                    value={inputValue?.bankNo}
                    required
                    error={inputError?.bankNo}
                />
                <C1InputField
                    label={t("opadmin:bankList.bankName")}
                    name="bankName"
                    onChange={handleChange}
                    value={inputValue?.bankName}
                    required
                    error={inputError?.bankName}
                />

            </C1PopUp>
        </C1ListPanel>

    );
}