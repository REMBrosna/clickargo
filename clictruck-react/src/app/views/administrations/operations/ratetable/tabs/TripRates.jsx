import { Button, Grid, Tooltip } from "@material-ui/core";
import { NearMeOutlined } from "@material-ui/icons";
import BorderColorOutlinedIcon from '@material-ui/icons/BorderColorOutlined';
import CalendarTodayIcon from '@material-ui/icons/CalendarTodayOutlined';
import CheckCircleOutlineOutlinedIcon from '@material-ui/icons/CheckCircleOutlineOutlined';
import DeleteOutlineOutlinedIcon from '@material-ui/icons/DeleteOutlineOutlined';
import DescriptionIcon from '@material-ui/icons/DescriptionOutlined';
import PersonIcon from '@material-ui/icons/PersonOutlineOutlined';
import VisibilityOutlinedIcon from '@material-ui/icons/VisibilityOutlined';
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import ChipStatus from "app/atomics/atoms/ChipStatus";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DataTable from "app/c1component/C1DataTable";
import C1DateField from "app/c1component/C1DateField";
import C1IconButton from "app/c1component/C1IconButton";
import C1InputField from "app/c1component/C1InputField";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1PopUp from "app/c1component/C1PopUp";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";
import C1SelectField from "app/c1component/C1SelectField";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1TextArea from "app/c1component/C1TextArea";
import C1Warning from "app/c1component/C1Warning";
import useHttp from "app/c1hooks/http";
import { AccountTypes, CK_ACCOUNT_CO_FF_ACCN_TYPE, CK_ACCOUNT_TO_ACCN_TYPE, MST_CURRENCY_URL, RecordStatus } from "app/c1utils/const";
import { deepUpdateState } from "app/c1utils/statusUtils";
import { dialogStyles } from "app/c1utils/styles";
import { customFilterDateDisplay, customNumFieldDisplay, formatDate, getValue, isArrayNotEmpty, isEditable, isEmpty } from "app/c1utils/utility";
import useAuth from 'app/hooks/useAuth';

import CommentsOfRateTablePopUp from "../popups/CommentsOfRateTablePopUp";
import TripRatePopup from "../popups/TripRatePopup";

/** TripRate management */
const TripRates = ({
    inputData,
    handleInputChange,
    handleDateChange,
    viewType,
    isDisabled,
    errors,
    setHasNew
}) => {

    const { user } = useAuth();

    const { t } = useTranslation(["cargoowners", "administration", "button", "common"]);
    const dialogClasses = dialogStyles();

    /** ------------------ States ---------------------------------*/
    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();

    const [warningMessage, setWarningMessage] = useState({ open: false, msg: "", hlMsg: "", subMsg: "" });

    const [openAddPopUp, setOpenAddPopUp] = useState(false);
    // eslint-disable-next-line
    const [openLocationPopup, setOpenLocationPopup] = useState(false)
    const [popUpFieldError, setPopUpFieldError] = useState({});
    const [viewOnly, setViewOnly] = useState(false);
    const [popupAction, setPopupAction] = useState(null);
    const [isRefresh, setRefresh] = useState(false);

    const [vehList, setVehList] = useState([]);
    const [rtVehSelType, setRtVehSelType] = useState("");

    let popupDefaultValue = {
        // trId: generateID("JKSM"),
        tckCtRateTable: { rtId: inputData?.rtId },
        tckCtLocationByTrLocTo: { locId: "" },
        tckCtLocationByTrLocFrom: { locId: "" },
        tckCtMstVehType: { vhtyId: rtVehSelType },
        trCharge: '',
    }

    let defaultListLocationRate = {
        tckCtRateTable: { rtId: inputData?.rtId },
        locationRate: [],
    }

    
    const [listLocationRate, setListLocationRate] = useState(defaultListLocationRate);
    const [popUpDetails, setPopUpDetails] = useState(popupDefaultValue);

    let tripRateUrl = `/api/v1/clickargo/clictruck/administrator/triprate/`;

    /** ------------------- Update states ----------------- */
    useEffect(() => {
        const TOaccn =
        //is user TO?
        user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_TO.code ?
            //if yes then get acnnid from principal
            user?.coreAccn?.accnId :
            //if no then get accnid from inputdata
            inputData?.tcoreAccnByRtCompany?.accnId;
            
        const vehlistUrl = `/api/v1/clickargo/clictruck/vehicle/veh-type/${TOaccn}`;
        sendRequest(vehlistUrl, "getVeh", "GET");
    // eslint-disable-next-line
    }, []);

    // reset is refresh status
    useEffect(() => { if (isRefresh) setRefresh(false) }, [isRefresh]);

    useEffect(() => {
        if (!isLoading && !error && res && !validation) {
            switch (urlId) {
                case "getTripRate": {
                    let listData = res.data?.tckCtTripRates;
                    let listArray = [];
                    setPopUpDetails({ ...res.data })

                    if(res.data?.trType === "S"){
                        listArray.push(
                            {
                                "rateId": res.data?.tckCtLocationByTrLocTo?.locId,
                                "tckCtLocationByTrLocFrom": {
                                    "locId": res.data?.tckCtLocationByTrLocFrom?.locId,
                                    "locName": res.data?.tckCtLocationByTrLocFrom?.locName,
                                    "tckCtMstLocationType": {
                                        "lctyName": res.data?.tckCtLocationByTrLocFrom?.tckCtMstLocationType?.lctyName
                                    }
                                },
                                "tckCtLocationByTrLocTo": {
                                    "locId": res.data?.tckCtLocationByTrLocTo?.locId,
                                    "locName": res.data?.tckCtLocationByTrLocTo?.locName,
                                    "tckCtMstLocationType": {
                                        "lctyName": res.data?.tckCtLocationByTrLocTo?.tckCtMstLocationType?.lctyName
                                    }
                                },
                                "trCharge": res.data?.trCharge
                            }
                        )
                    }else{
                    listData.sort((a, b) => a.trSeq - b.trSeq)
                            .map((item)=>{
                                listArray.push(
                                        {
                                            "rateId": item?.tckCtLocationByTrLocTo?.locId,
                                            "tckCtLocationByTrLocFrom": {
                                                "locId": item?.tckCtLocationByTrLocFrom?.locId,
                                                "locName": item?.tckCtLocationByTrLocFrom?.locName,
                                                "tckCtMstLocationType": {
                                                    "lctyName": item?.tckCtLocationByTrLocFrom?.tckCtMstLocationType?.lctyName
                                                }
                                            },
                                            "tckCtLocationByTrLocTo": {
                                                "locId": item?.tckCtLocationByTrLocTo?.locId,
                                                "locName": item?.tckCtLocationByTrLocTo?.locName,
                                                "tckCtMstLocationType": {
                                                    "lctyName": item?.tckCtLocationByTrLocTo?.tckCtMstLocationType?.lctyName
                                                }
                                            },
                                            "trCharge": item?.trCharge
                                        }
                                )

                                return item;
                            });
                    }
                    
                    setListLocationRate({
                        "trType": res.data?.trType,
                        "tckCtRateTable": {
                            "rtId": res.data?.trId
                        },
                        "locationRate": listArray
                    });

                    setOpenAddPopUp(true);
                    // setListLocationRate({
                    //     "trType": "S",
                    //     "tckCtRateTable": {
                    //         "rtId": "CKCTRT2023052238157"
                    //     },
                    //     "locationRate": [
                    //         {
                    //             "rateId": "CKCTLOC2023052189200",
                    //             "tckCtLocationByTrLocFrom": {
                    //                 "locId": "CKCTLOC2023052159319",
                    //                 "locName": "Location1",
                    //                 "tckCtMstLocationType": {
                    //                     "lctyName": "ADDRESS"
                    //                 }
                    //             },
                    //             "tckCtLocationByTrLocTo": {
                    //                 "locId": "CKCTLOC2023052189200",
                    //                 "locName": "Location 2",
                    //                 "tckCtMstLocationType": {
                    //                     "lctyName": "ADDRESS"
                    //                 }
                    //             },
                    //             "trCharge": 1000000
                    //         }
                    //     ]
                    // })
                    break;
                }
                case "createTripRate": {
                    setOpenAddPopUp(false);
                    setRefresh(true);
                    setHasNew(true);
                    break;
                }
                case "updateTripRate": {
                    setOpenAddPopUp(false);
                    setRefresh(true);
                    break;
                }
                case "deleteTripRate": {
                    setRefresh(true);
                    document.body.style.cursor = 'default';
                    break;
                }
                case "getVeh": {
                    setVehList([...res?.data?.data]);
                    break;
                }
                default: break;
            }
        }
        if (error) {
            //goes back to the screen
            // setLoading(false);
        }
        //If validation has value then set to the errors
        if (validation) {
            let keyList = Object.keys(validation);
            if (keyList.length > 0) {
                for (let key of keyList) {
                    if (key.includes("rate-trip-duplicate")) {
                        setWarningMessage({ open: true, msg: t("administration:rateTableManagement.msg.duplicate", { rateTripDetails: validation[key] }), hlMsg: null, subMsg: null });
                    } else {
                        setWarningMessage({ open: true, msg: validation[key], hlMsg: null, subMsg: null });
                    }
                }
            }
            if (urlId === "createTripRate") {
                let errors = {};
                errors.fromLocId = validation.trCharge;
                errors.toLocId = validation.trCharge;
                errors.vhtyId = validation.trCharge;
                setPopUpFieldError(errors);
            }
        }

        // eslint-disable-next-line
    }, [urlId, isLoading, isFormSubmission, res, validation, error]);

    const popUpAddHandler = () => {
        console.log("popUpAddHandler", popupDefaultValue);
        setViewOnly(false);
        setPopupAction('create');
        setOpenAddPopUp(true);
        setPopUpFieldError({});
        setPopUpDetails(popupDefaultValue);
        setListLocationRate(defaultListLocationRate);
    };

    const createTripRate = () => {
        if (!isEmpty(handlePopUpFieldValidate())) {
            setPopUpFieldError(handlePopUpFieldValidate());
        } else {
            // setOpenAddPopUp(false)
            if(listLocationRate?.locationRate.length < 2 && popUpDetails?.trType === "M"){
                setWarningMessage({ open: true, msg: t("administration:rateTableManagement.msg.minimum"), hlMsg: null, subMsg: null });
            }else{
                setRefresh(false);
    
                const payload = {
                    trType:popUpDetails?.trType,
                    tckCtRateTable: {rtId: listLocationRate?.tckCtRateTable?.rtId},
                    tckCtTripRates: listLocationRate?.locationRate,
                    //set the from/to location here from the listLocationRate[0] and listLocationRate[size-1]
                    tckCtLocationByTrLocFrom: isArrayNotEmpty(listLocationRate?.locationRate) ?  {...listLocationRate?.locationRate[0]?.tckCtLocationByTrLocFrom} : null,
                    tckCtLocationByTrLocTo: isArrayNotEmpty(listLocationRate?.locationRate) ? {...listLocationRate?.locationRate[listLocationRate?.locationRate.length - 1]?.tckCtLocationByTrLocTo} : null,
                    tckCtMstVehType: {vhtyId: popUpDetails?.tckCtMstVehType?.vhtyId}
                }
    
                if(popUpDetails?.trType === "S"){
                    payload.trCharge = listLocationRate?.locationRate?.[0]?.trCharge
                }

                sendRequest(`${tripRateUrl}`, "createTripRate", "POST", { ...payload });

            }
        }
    }

    const deleteTripRate = (id) => {
        // TODO: Add confirmation
        setOpenAddPopUp(false)
        setRefresh(false);
        document.body.style.cursor = 'wait';
        sendRequest(`${tripRateUrl}${id}`, "deleteTripRate", "DELETE", {});
    }

    const updateTripRate = (id) => {
        if (!isEmpty(handlePopUpFieldValidate())) {
            setPopUpFieldError(handlePopUpFieldValidate());
        } else {
            // setOpenAddPopUp(false)
            setRefresh(false);
            sendRequest(`${tripRateUrl}${popUpDetails.trId}`, "updateTripRate", "PUT", { ...popUpDetails });
        }
    }

    const handlePopUpFieldValidate = () => {
        let errors = {};
        if (!popUpDetails?.trType || popUpDetails?.trType === '') {
            errors.trType = t("common:validationMsgs.required");
        }
        if (!popUpDetails?.trCharge || popUpDetails?.trCharge === '') {
            errors.trCharge = t("common:validationMsgs.required");
        }
        if (!popUpDetails?.tckCtMstVehType.vhtyId || popUpDetails?.tckCtMstVehType.vhtyId === '') {
            errors.vhtyId = t("common:validationMsgs.required");
        }
        return errors;
    }

    const handleWarningAction = (e) => {
        setWarningMessage({ open: false, msg: "", hlMsg: "", subMsg: "" })
    };

    const popupViewHandler = (id, viewOnly, action) => {
        setViewOnly(viewOnly)
        setPopupAction(action);
        setPopUpFieldError({});
        // setPopUpDetails(popupDefaultValue);
        sendRequest(`${tripRateUrl}${id}`, "getTripRate", "GET", null)
    };

    const handlePopupInputChange = (e) => {
        const elName = e.target.name;
        console.log("elName")
        if (elName === 'trCharge') {
            setPopUpDetails({ ...popUpDetails, "trCharge": e.target.value });
        } else {
            setPopUpDetails({ ...popUpDetails, ...deepUpdateState(popUpDetails, elName, e.target.value) });
            console.log("popupdetails", popUpDetails);
        }
    };

    const handleChangeVehType = (e, name, value) => {
        setRtVehSelType(getValue(value?.value));
        setRefresh(true);
    }

    const columns = [
        {
            name: "trId",
            label: "Id",
            options: {
                filter: false,
                display: "excluded"
            }
        },
        {
            name: "tckCtLocationByTrLocFrom.locName",
            label: t("administration:rateTableManagement.listing.from"),
        },
        {
            name: "tckCtLocationByTrLocFrom.tckCtMstLocationType.lctyName",
            label: t("administration:rateTableManagement.listing.locationType"),
            options: {
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: ["Address", "Region"],
                    renderValue: v => {
                        switch (v) {
                            case "Address": return "Address";
                            case "Region": return "Region";
                            default: break;
                        }
                    }
                },
            },
        },
        {
            name: "tckCtLocationByTrLocTo.locName",
            label: t("administration:rateTableManagement.listing.to"),
        },
        {
            name: "tckCtLocationByTrLocTo.tckCtMstLocationType.lctyName",
            label: t("administration:rateTableManagement.listing.locationType"),
            options: {
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: ["Address", "Region"],
                    renderValue: v => {
                        switch (v) {
                            case "Address": return "Address";
                            case "Region": return "Region";
                            default: break;
                        }
                    }
                },
            },
        },
        {
            name: "tckCtMstVehType.vhtyName",
            label: t("administration:rateTableManagement.listing.truckType"),
            options: {
                sort: true,
                filter: true,
                filterType: "dropdown",
                // customBodyRender: (value) => getStatusDesc(value),
                filterOptions: {
                    names: ["CDD", "CDD LONG", "CDE", "CONTAINER 20FT", "CONTAINER 40FT", "VAN", "WING BOX"],
                    renderValue: (v) => {
                        switch (v) {
                            case "CDD": return "CDD";
                            case "CDD LONG": return "CDD Long";
                            case "CDE": return "CDE";
                            case "CONTAINER 20FT": return "Container 20FT";
                            case "CONTAINER 40FT": return "Container 40FT";
                            case "VAN": return "Van";
                            case "WING BOX": return "Wing Box";
                            default: break
                        }
                    },
                },
                customFilterListOptions: {
                    render: (v) => {
                        switch (v) {
                            case "CDD": return "CDD";
                            case "CDD LONG": return "CDD Long";
                            case "CDE": return "CDE";
                            case "CONTAINER 20FT": return "Container 20FT";
                            case "CONTAINER 40FT": return "Container 40FT";
                            case "VAN": return "Van";
                            case "WING BOX": return "Wing Box";
                            default: break
                        }
                    },
                },
            },
        },
        {
            name: "trType",
            label: t("administration:rateTableManagement.listing.rateType"),
            options: {
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: ["S", "M"],
                    renderValue: v => {
                        switch (v) {
                            case "S": return "Single";
                            case "M": return "MultiDrop";
                            default: break;
                        }
                    }
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    switch (value) {
                        case "M": return 'MultiDrop'
                        case "S": return 'Single';
                        default: return 'Single';
                    }
                },
                customFilterListOptions: {
                    render: (v) => {
                        switch (v) {
                            case "S": return "Single";
                            case "M": return "Multi-Drop";
                            default: break
                        }
                    },
                }
            },
        },
        {
            name: "trCharge",
            label: t("administration:rateTableManagement.listing.price"),
            options: {
                display: true,
                filter: true,
                sort: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return value ? value?.toLocaleString("in-ID", { maximumFractionDigits: 2, style: "currency", currency: "IDR" }) : '0.00';
                },
                filterType: 'custom',
                customFilterListOptions: {
                    render: v => v.map(l => l),
                    update: (filterList, filterPos, index) => {
                        filterList[index].splice(filterPos, 1);
                        return filterList;
                    }
                },
                filterOptions: {
                    display: customNumFieldDisplay
                }
            }
        },
        {
            name: "trDtCreate",
            label: t("administration:rateTableManagement.listing.dateCreated"),
            options: {
                sort: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                },
                filter: true,
                filterType: 'custom',
                filterOptions: {
                    display: customFilterDateDisplay
                },
            },
        },
        {
            name: "trDtLupd",
            label: t("administration:rateTableManagement.listing.dateUpdated"),
            options: {
                sort: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                },
                filter: true,
                filterType: 'custom',
                filterOptions: {
                    display: customFilterDateDisplay
                },
            },
        },
        {
            name: "trStatus",
            label: t("administration:rateTableManagement.listing.dateUpdated"),
            options: {
                sort: true,
                filter: true,
                filterType: "dropdown",
                customBodyRender: (value, tableMeta, updateValue) => {
                    switch (value) {
                        case "I": return <ChipStatus text={RecordStatus.INACTIVE.desc} color="#FF2E6C" />;
                        case "A": return <ChipStatus text={RecordStatus.ACTIVE.desc} color="#00D16D" />;
                        case "D": return <ChipStatus text={RecordStatus.DEACTIVE.desc} color="#969696" />;
                        case "N": return <ChipStatus text={RecordStatus.NEW.desc} color="#F7E52F" />;
                        case "S": return <ChipStatus text={RecordStatus.SUBMITTED.desc} color="#F7962F" />;
                        case "V": return <ChipStatus text={RecordStatus.VERIFIED.desc} color="#0095A9" />;
                        default: return null;
                    }
                },
                filterOptions: {
                    names: ["A", "I", "D", "N", "S", "V"],
                    renderValue: (v) => {
                        switch (v) {
                            case "A": return "Active";
                            case "I": return "Inactive";
                            case "D": return "Deleted";
                            case "N": return "New";
                            case "S": return "Submitted";
                            case "V": return "Verified";
                            default: return null;
                        }

                    },
                },
                customFilterListOptions: {
                    render: (v) => {
                        if (v === "A") {
                            return "Active";
                        } else if (v === "I") {
                            return "InActive";
                        }
                    },
                },
            },
        },
        {
            name: "",
            label: t("administration:rateTableManagement.listing.action"),
            options: {
                filter: false,
                sort: false,
                viewColumns: false,
                display: true,
                customHeadLabelRender: (columnMeta) => {
                    return <div style={{ textAlign: "center" }}>{columnMeta.label}</div>
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    let status = tableMeta.rowData[9];
                    return <Grid container direction="row"
                        justifyContent="flex-start" alignItems="center" style={{ marginRight: "10px" }}>
                        <Grid container item justifyContent="space-between">
                            <Grid item xs={6}>
                                <C1LabeledIconButton
                                    tooltip={t("buttons:view")}
                                    label={t("buttons:view")}
                                    action={() => popupViewHandler(tableMeta.rowData[0], true, "read")}>
                                    <VisibilityOutlinedIcon />
                                </C1LabeledIconButton>
                            </Grid>
                            <Grid item xs={6}>
                                {/* do not delete when state is verified */}
                                {viewType === 'edit' && !["V", "I"].includes(status) &&
                                    <C1LabeledIconButton
                                        tooltip={t("buttons:delete")}
                                        label={t("buttons:delete")}
                                        action={() => deleteTripRate(tableMeta.rowData[0])}>
                                        <DeleteOutlineOutlinedIcon />
                                    </C1LabeledIconButton>
                                }
                            </Grid>
                        </Grid>
                    </Grid>

                },
            },
        },
    ]

    let actionEl;
    if (popupAction === "create") {
        actionEl = <Tooltip title={t("buttons:add")}>
            <Button onClick={() => createTripRate()} className={dialogClasses.dialogButtonSpace}>
                <NearMeOutlined color="primary" fontSize="large" />
            </Button>
        </Tooltip>
    } else if (popupAction === "update") {
        actionEl = <C1IconButton tooltip={t("buttons:update")} childPosition="right">
            <CheckCircleOutlineOutlinedIcon color="primary" fontSize="large" onClick={() => updateTripRate()} />
        </C1IconButton>
    } else {
        actionEl = null;
    }


    return (
        <React.Fragment>
            <Grid item xs={12}>
                <C1TabContainer>

                    <Grid item lg={4} md={6} xs={12} >
                        <C1CategoryBlock icon={<DescriptionIcon />} title={t("administration:rateTableManagement.details.generalDetails")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1InputField
                                        label={"ID"}
                                        name="rtId"
                                        disabled
                                        onChange={handleInputChange}
                                        value={inputData?.rtId || '-'} />
                                    <C1InputField
                                        label={t("administration:rateTableManagement.details.name")}
                                        name="rtName"
                                        required
                                        disabled={isDisabled}
                                        onChange={handleInputChange}
                                        value={inputData?.rtName || ''}
                                        error={errors['rtName'] !== undefined}
                                        helperText={errors['rtName'] || ''} />
                                    <C1TextArea
                                        multiline
                                        textLimit={1024}
                                        label={t("administration:rateTableManagement.details.description")}
                                        name="rtDescription"
                                        disabled={isDisabled}
                                        onChange={handleInputChange}
                                        value={inputData?.rtDescription || ''} />
                                    <C1SelectField
                                        name="tmstCurrency.ccyCode"
                                        label={t("administration:rateTableManagement.details.currency")}
                                        value={inputData?.tmstCurrency?.ccyCode || ''}
                                        onChange={e => handleInputChange(e)}
                                        required
                                        disabled={true}
                                        isServer={true}
                                        options={{
                                            url: MST_CURRENCY_URL,
                                            key: "ccyCode",
                                            id: 'ccyCode',
                                            // Show code or description
                                            // desc: 'ccyCode',
                                            desc: 'ccyDescription',
                                            isCache: true
                                        }}
                                        error={errors['TMstCurrency.ccyCode'] !== undefined}
                                        helperText={errors['TMstCurrency.ccyCode'] || ''}
                                    />
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>

                    <Grid item lg={4} md={6} xs={12}>
                        <C1CategoryBlock icon={<PersonIcon />} title={t("administration:rateTableManagement.details.partiesDetails")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1SelectField
                                        isServer={true}
                                        required
                                        disabled={true}
                                        name="tcoreAccnByRtCompany.accnId"
                                        label={t("administration:rateTableManagement.details.accnTo")}
                                        value={inputData?.tcoreAccnByRtCompany?.accnId || ''}
                                        onChange={handleInputChange}
                                        options={{
                                            url: CK_ACCOUNT_TO_ACCN_TYPE,
                                            key: "account",
                                            id: "accnId",
                                            desc: "accnName",
                                            isCache: false,
                                        }}
                                        error={errors['tcoreAccnByRtCompany'] !== undefined}
                                        helperText={errors['tcoreAccnByRtCompany'] || ''}
                                    />
                                    {/* Freight Forwarder / Cargo Owner Selection */}
                                    <C1SelectAutoCompleteField
                                        isServer={true}
                                        required
                                        disabled={true}
                                        name="tcoreAccnByRtCoFf.accnId"
                                        label={t("administration:rateTableManagement.details.accnCoFf")}
                                        value={inputData?.tcoreAccnByRtCoFf?.accnId}
                                        onChange={(e, name, value) => handleInputChange({ target: { name, value: value?.value } })}
                                        options={{
                                            url: CK_ACCOUNT_CO_FF_ACCN_TYPE,
                                            key: "account",
                                            id: "accnId",
                                            desc: "accnName",
                                            isCache: false,
                                        }}
                                        error={errors['TCoreAccnByRtCoFf.accnId'] !== undefined}
                                        helperText={errors['TCoreAccnByRtCoFf.accnId'] || ''}
                                    />
                                    <C1SelectAutoCompleteField

                                        disabled={isDisabled}
                                        name="rtVehSelType"
                                        label={t("administration:rateTableManagement.details.vehType")}
                                        optionsMenuItemArr={vehList ? vehList.map((item, i) => {
                                            return {
                                                value: item.vhtyId,
                                                desc: item.vhtyName
                                            }
                                        }) : []}
                                        // optionsMenuItemArr={[]}
                                        value={rtVehSelType}
                                        onChange={handleChangeVehType}
                                        // options={{
                                        //     url: T_CK_CT_VEH + `&mDataProp_1=vhStatus&sSearch_1=A&mDataProp_2=TCoreAccn.accnId&sSearch_2=${user?.coreAccn?.accnId}`,
                                        //     id: "tckCtMstVehType.vhtyId",
                                        //     desc: "tckCtMstVehType.vhtyName",
                                        //     isCache: false,
                                        // }}
                                        error={errors['TCoreAccnByRtCoFf.accnId'] !== undefined}
                                        helperText={errors['TCoreAccnByRtCoFf.accnId'] || ''}
                                    />
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                        <Grid item style={{ height: '39px' }}></Grid>
                        <C1CategoryBlock icon={<CalendarTodayIcon />} title={t("administration:rateTableManagement.details.validityDetails")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1DateField
                                        label={t("administration:rateTableManagement.details.startDate")}
                                        name="rtDtStart"
                                        required
                                        value={inputData?.rtDtStart || ''}
                                        disabled={isDisabled}
                                        onChange={handleDateChange}
                                        disablePast={true}
                                    />
                                    <C1DateField
                                        label={t("administration:rateTableManagement.details.endDate")}
                                        name="rtDtEnd"
                                        required
                                        value={inputData?.rtDtEnd || ''}
                                        disabled={isDisabled}
                                        onChange={handleDateChange}
                                        disablePast={true}
                                        minDate={inputData?.rtDtStart ? inputData?.rtDtStart : new Date()}
                                    />
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>

                    <Grid container item lg={4} md={6} xs={12} direction="column">
                        <C1CategoryBlock icon={<BorderColorOutlinedIcon />} title={t("administration:rateTableManagement.details.properties")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1InputField
                                        label={t("administration:rateTableManagement.details.createdBy")}
                                        value={inputData?.rtUidCreate || ''}
                                        name="rtUidCreate"
                                        disabled={true}
                                    />
                                    <C1DateField
                                        label={t("administration:rateTableManagement.details.createdDate")}
                                        name="rtDtCreate"
                                        value={inputData?.rtDtCreate || ''}
                                        disabled={true}
                                    />
                                    <C1InputField
                                        label={t("administration:rateTableManagement.details.updatedBy")}
                                        value={inputData?.rtUidLupd || ''}
                                        name="rtUidLupd"
                                        disabled={true}
                                    />
                                    <C1DateField
                                        label={t("administration:rateTableManagement.details.updatedDate")}
                                        name="rtDtLupd"
                                        value={inputData?.rtDtLupd || ''}
                                        disabled={true}
                                    />
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>
                    <Grid item lg={12} md={12} xs={12} >
                        <C1CategoryBlock icon={<DescriptionIcon />} title={t("administration:rateTableManagement.details.rateTable")}>
                            <C1DataTable
                                url={"/api/v1/clickargo/clictruck/administrator/triprate"}
                                isServer={viewType !== 'new'}
                                columns={columns}
                                defaultOrder="trId"
                                defaultOrderDirection="asc"
                                showAdd={!isDisabled && viewType !== 'new' ? {
                                    type: "popUp",
                                    popUpHandler: popUpAddHandler,
                                } : null}
                                isShowToolbar
                                isShowFilterChip
                                isShowDownload={true}
                                isRefresh={isRefresh}
                                // handleBuildBody={handleDownloadBuildBody}
                                isShowPrint={true}
                                isRowSelectable={false}
                                filterBy={[
                                    { attribute: "tckCtRateTable.rtId", value: inputData?.rtId },
                                    { attribute: "tckCtMstVehType.vhtyId", value: rtVehSelType }
                                ]}
                                guideId="clicdo.doi.co.jobs.list.table"
                            />
                            <C1PopUp
                                title={t("administration:rateTableManagement.tripRates.title")}
                                openPopUp={openAddPopUp}
                                setOpenPopUp={setOpenAddPopUp}
                                actionsEl={actionEl}>
                                <TripRatePopup
                                    setInputData={setPopUpDetails}
                                    inputData={popUpDetails}
                                    mainInputData={inputData}
                                    isDisabled={isEditable(viewType) || viewOnly}
                                    handleInputChange={handlePopupInputChange}
                                    locale={t}
                                    errors={popUpFieldError}
                                    handleLocationPopupOpen={() => setOpenLocationPopup(true)}
                                    rtVehType={rtVehSelType}
                                    listLocationRate={listLocationRate}
                                    setListLocationRate={setListLocationRate}
                                    vehList={vehList}
                                />
                            </C1PopUp>
                            <CommentsOfRateTablePopUp />
                        </C1CategoryBlock>
                    </Grid>
                </C1TabContainer>

            </Grid>
            {/* For warning messages */}
            <C1Warning warningMessage={warningMessage} handleWarningAction={handleWarningAction} />

        </React.Fragment>
    );
};

export default TripRates;