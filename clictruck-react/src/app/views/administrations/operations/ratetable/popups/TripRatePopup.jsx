import { Grid, IconButton, Tooltip, setRef } from "@material-ui/core";
import MenuItem from "@material-ui/core/MenuItem";
import AddBoxIcon from '@material-ui/icons/AddBoxOutlined';
import BorderColorOutlinedIcon from '@material-ui/icons/BorderColorOutlined';
import LocalAtmOutlinedIcon from '@material-ui/icons/LocalAtmOutlined';
import EditOutlinedIcon from '@material-ui/icons/EditOutlined';
import DeleteOutlineOutlinedIcon from '@material-ui/icons/DeleteOutlineOutlined';
import React, { useEffect, useState } from "react";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DateTimeField from "app/c1component/C1DateTimeField";
import C1InputField from "app/c1component/C1InputField";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";
import C1SelectField from "app/c1component/C1SelectField";
import useHttp from "app/c1hooks/http";
import { AccountTypes, CK_CT_LOCATION, CK_MST_VEH_TYPE } from "app/c1utils/const";
import NumFormat from "app/clictruckcomponent/NumFormat";
import useAuth from "app/hooks/useAuth";

import TripLocationPopup from "./TripLocationPopup";
import C1DataTable from "app/c1component/C1DataTable";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import TripLocationRatePopup from "./TripLocationRatePopup";
import { deepUpdateState } from "app/c1utils/statusUtils";
import { ConfirmationDialog } from "matx";
import { isEmpty } from "app/c1utils/utility";

/** Pop up to add trip rates associated to the trip rate table. */
const TripRatePopup = (props) => {

    const {
        setInputData,
        inputData,
        mainInputData,
        isDisabled,
        viewType,
        errors,
        handleInputFileChange,
        handleInputChange,
        locale,
        listLocationRate,
        setListLocationRate,
        rtVehType,
        vehList
    } = props

    console.log("TripRatePopup inputData", inputData, listLocationRate);
    // Styles
    const inputStyle = {
        textAlign: 'right'
    }

    let defaultLocationRate = {
        tckCtLocationByTrLocTo: { locId: "" },
        tckCtLocationByTrLocFrom: { locId: "" },
        trCharge: '',
    }

    /** ---------------- Declare states ------------------- */
    const { isLoading, res, validation, error, urlId, sendRequest } = useHttp();

    const [locationState, setLocationState] = useState([])
    const [openLocationPopup, setOpenLocationPopup] = useState(false)
    const [openLocationRatePopup, setOpenLocationRatePopup] = useState(false)
    const [popupLocationRate, setPopupLocationRate] = useState(defaultLocationRate);
    const [isRefresh, setRefresh] = useState(false);
    const [onAdd, setOnAdd] = useState(false);
    const [onReloadRate, setOnReloadRate] = useState(false);
    const [locationRate, setLocationRate] = useState(listLocationRate?.locationRate || []);
    const [action, setAction] = useState('');
    const [indexRate, setIndexRate] = useState('');
    const { user } = useAuth();
    const [popUpFieldError, setPopUpFieldError] = useState({});
    const [vehListStatus, setVehListStatus] = useState(true);
    
    const [openDeleteConfirm, setOpenDeleteConfirm] = useState({ action: null, open: false, id: null });  
    
    useEffect(() => {
        getLocationData();

        if(isDisabled){
            let filter = vehList.filter(item => item.vhtyId === inputData?.tckCtMstVehType.vhtyId)
            if(filter.length === 0){
                setVehListStatus(false)
            }
        }
    }, [])

    useEffect(() => {
        if(inputData?.trType === 'S'){
            var filteredData = locationRate.filter(function(elemetn,index) {
                return index === 0;
            });
            setLocationRate(filteredData)
            setOnReloadRate(true);
        }
    }, [inputData?.trType])

    useEffect(() => {
        if (!isLoading && !error && res && !validation) {
            switch (urlId) {
                case "getLocation": {
                    setLocationState(res?.data?.aaData);
                    break;
                }
                default: break;
            }
        }
    }, [urlId, res, isLoading, error, validation]);

    useEffect(() => {
        if (TOaccn){
            getLocationData()
        }
        if(onReloadRate){
            setOnReloadRate(false)
            setListLocationRate({...listLocationRate, "locationRate": locationRate})
            setRefresh(true)
            setOnAdd(true)
            
            console.log('locationRate 2',locationRate);
        }
        if(onAdd){
            setOnAdd(false)
            setOpenLocationRatePopup(false);
            setPopupLocationRate(defaultLocationRate)
            setRefresh(false);
            
            console.log('listLocationRate',listLocationRate);
        }
    }, [TOaccn, inputData, onAdd, onReloadRate])

    const TOaccn =
        //is user TO?
        user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_TO.code ?
            //if yes then get acnnid from principal
            user?.coreAccn?.accnId :
            //if no then get accnid from inputdata
            mainInputData?.tcoreAccnByRtCompany?.accnId;

    const getLocationData = () => {
        sendRequest(`${CK_CT_LOCATION}&mDataProp_2=TcoreAccn.accnId&sSearch_2=${TOaccn}`, 'getLocation', 'GET')
    }

    const handleAfterClose = () => {
        setOpenLocationPopup(false)
        getLocationData();
    }

    const handleCloseLocationRate = () => {
        setOpenLocationRatePopup(false)
    }

    const addLocationRate = () => {
        setPopUpFieldError({})
        setAction('NEW');
        setPopupLocationRate(defaultLocationRate)
        setOpenLocationRatePopup(true)
    }

    const updateLocationRate = (idx) => {
        setPopUpFieldError({})
        setAction('UPDATE');

        var filteredData = locationRate.filter(function(elemetn,index) {
            return index === idx;
        });

        setPopupLocationRate({
            tckCtLocationByTrLocTo: { locId: filteredData[0]?.tckCtLocationByTrLocTo.locId },
            tckCtLocationByTrLocFrom: { locId: filteredData[0]?.tckCtLocationByTrLocFrom.locId },
            trCharge: filteredData[0]?.trCharge,
        })

        setIndexRate(idx)
        setOpenLocationRatePopup(true)
    }

    const onlyNumber = (e) => {
        if (e.charCode < 48) {
            return e.preventDefault();
        }
    }

    const rateType = [
        {
            rateTypeId: 'S',
            rateTypeName: 'Single'
        },
        {
            rateTypeId: 'M',
            rateTypeName: 'Multi Drop'
        },
    ];
    
    const columns = [
        {
            name: "rtId",
            label: "Id",
            options: {
                filter: false,
                display: "excluded"
            }
        },
        {
            name: "tckCtLocationByTrLocFrom.locName",
            label: locale("administration:rateTableManagement.listing.from"),
            options: {
                sort: false,
            }
        },
        {
            name: "tckCtLocationByTrLocFrom.tckCtMstLocationType.lctyName",
            label: locale("administration:rateTableManagement.listing.locationType"),
            options: {
                sort: false,
            }
        },
        {
            name: "tckCtLocationByTrLocTo.locName",
            label: locale("administration:rateTableManagement.listing.to"),
            options: {
                sort: false,
            }
        },
        {
            name: "tckCtLocationByTrLocTo.tckCtMstLocationType.lctyName",
            label: locale("administration:rateTableManagement.listing.locationType"),
            options: {
                sort: false,
            }
        },
        {
            name: "trCharge",
            label: locale("administration:rateTableManagement.listing.price"),
            options: {
                display: true,
                filter: true,
                sort: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return value?.toLocaleString("in-ID", { maximumFractionDigits: 0, style: "currency", currency: "IDR" });
                },
            }
        },
        {
            name: "",
            label: locale("administration:rateTableManagement.listing.action"),
            options: {
                filter: false,
                sort: false,
                viewColumns: false,
                display: true,
                customHeadLabelRender: (columnMeta) => {
                    return <div style={{ textAlign: "center" }}>{columnMeta.label}</div>
                },
                customBodyRender: (value, tableMeta, updateValue) => {

                    const index = tableMeta.rowIndex;

                    return <Grid container direction="row"
                        justifyContent="flex-start" alignItems="center" style={{ marginRight: "10px" }}>
                        <Grid container item justifyContent="space-between">
                            <Grid item xs={6}>
                                <C1LabeledIconButton
                                    tooltip={locale("buttons:edit")}
                                    label={locale("buttons:edit")}
                                    action={() => updateLocationRate(index)}>
                                    <EditOutlinedIcon />
                                </C1LabeledIconButton>
                            </Grid>
                            <Grid item xs={6}>
                                <C1LabeledIconButton
                                    tooltip={locale("buttons:delete")}
                                    label={locale("buttons:delete")}
                                    action={() => handlePopupDeleteConfirm(index)}>
                                    <DeleteOutlineOutlinedIcon />
                                </C1LabeledIconButton>
                            </Grid>
                        </Grid>
                    </Grid>

                },
            },
        },
    ]

    const columnsView = [
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
            label: locale("administration:rateTableManagement.listing.from"),
            options: {
                sort: false,
            }
        },
        {
            name: "tckCtLocationByTrLocFrom.tckCtMstLocationType.lctyName",
            label: locale("administration:rateTableManagement.listing.locationType"),
            options: {
                sort: false,
            }
        },
        {
            name: "tckCtLocationByTrLocTo.locName",
            label: locale("administration:rateTableManagement.listing.to"),
            options: {
                sort: false,
            }
        },
        {
            name: "tckCtLocationByTrLocTo.tckCtMstLocationType.lctyName",
            label: locale("administration:rateTableManagement.listing.locationType"),
            options: {
                sort: false,
            }
        },
        {
            name: "trCharge",
            label: locale("administration:rateTableManagement.listing.price"),
            options: {
                display: true,
                filter: true,
                sort: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return value?.toLocaleString("in-ID", { maximumFractionDigits: 0, style: "currency", currency: "IDR" });
                },
            }
        },
    ]

    const handlePopupDeleteConfirm = (index) => {
        setOpenDeleteConfirm({ ...openDeleteConfirm, action: 'DELETE', open: true, id: index, msg: locale('common:msg.deleteConfirm') })
    }

    const handlePopupInputChange = (e) => {
        const elName = e.target.name;
        if (elName === 'trCharge') {
            setPopupLocationRate({ ...popupLocationRate, "trCharge": e.target.value });
        } else {
            setPopupLocationRate({ ...popupLocationRate, ...deepUpdateState(popupLocationRate, elName, e.target.value) });
            console.log("popupLocationRate", popupLocationRate);
        }
    };

    const handleActionOnClick = () => {
        if (!isEmpty(handlePopUpFieldValidate(action))) {
            setPopUpFieldError(handlePopUpFieldValidate(action));
        } else {
            if(action === 'NEW'){
                addTripRate();
                setAction('');
            }else if(action === 'UPDATE'){
                updateTripRate();
                setAction('');
            }
        }
    }

    const addTripRate = () => {

        let locFrom = 0;
        let charge = 0;

        if(locationRate.length > 0){
            let last = locationRate[locationRate.length - 1];
            locFrom = locationState.filter((item) => item.locId === last.trId)
            charge = parseInt(charge)
        }else{
            locFrom = locationState.filter((item) => item.locId === popupLocationRate?.tckCtLocationByTrLocFrom?.locId)
            charge = parseInt(popupLocationRate?.trCharge)
            
            setInputData({ ...inputData, "trCharge": charge });
        }

        let locTo = locationState.filter((item) => item.locId === popupLocationRate?.tckCtLocationByTrLocTo?.locId)

        setLocationRate([...locationRate,{
            trId: locTo[0]?.locId,
            tckCtLocationByTrLocFrom: {
                locId: locFrom[0]?.locId,
                locName: locFrom[0]?.locName,
                tckCtMstLocationType:{
                    lctyName: locFrom[0]?.tckCtMstLocationType?.lctyName
                }
            },
            tckCtLocationByTrLocTo: {
                locId: locTo[0]?.locId,
                locName: locTo[0]?.locName,
                tckCtMstLocationType:{
                    lctyName: locTo[0]?.tckCtMstLocationType?.lctyName
                }
            },
            trCharge: parseInt(charge)
        }]);
        
        setOnReloadRate(true);
        
    }

    const updateTripRate = () => {

        let locTo = 0;
        let charge = 0;
        let chargeDefault = 0;
        
        if(indexRate === 0){
            charge = parseInt(popupLocationRate?.trCharge)
            if((indexRate+1) < locationRate.length){
                let last = locationRate[indexRate + 1];
                locTo = locationState.filter((item) => item.locId === last.trId)
            }
            setInputData({ ...inputData, "trCharge": charge });
        }else{
            if((indexRate+1) < locationRate.length){
                let last = locationRate[indexRate + 1];
                locTo = locationState.filter((item) => item.locId === last.trId)
                charge = parseInt(charge)
            }
        }
        let locFrom = locationState.filter((item) => item.locId === popupLocationRate?.tckCtLocationByTrLocFrom?.locId)
        let locToFrom = locationState.filter((item) => item.locId === popupLocationRate?.tckCtLocationByTrLocTo?.locId)

            var filteredData = locationRate.map((item,index) => {
                if(index === indexRate){
                    return {
                        trId: locToFrom[0]?.locId,
                        tckCtLocationByTrLocFrom: {
                            locId: locFrom[0]?.locId,
                            locName: locFrom[0]?.locName,
                            tckCtMstLocationType:{
                                lctyName: locFrom[0]?.tckCtMstLocationType?.lctyName
                            }
                        },
                        tckCtLocationByTrLocTo: {
                            locId: locToFrom[0]?.locId,
                            locName: locToFrom[0]?.locName,
                            tckCtMstLocationType:{
                                lctyName: locToFrom[0]?.tckCtMstLocationType?.lctyName
                            }
                        },
                        trCharge: parseInt(charge)
                    }
                }else if(index === indexRate + 1){
                    return {
                        trId: locTo[0]?.locId,
                        tckCtLocationByTrLocFrom: {
                            locId: locToFrom[0]?.locId,
                            locName: locToFrom[0]?.locName,
                            tckCtMstLocationType:{
                                lctyName: locToFrom[0]?.tckCtMstLocationType?.lctyName
                            }
                        },
                        tckCtLocationByTrLocTo: {
                            locId: locTo[0]?.locId,
                            locName: locTo[0]?.locName,
                            tckCtMstLocationType:{
                                lctyName: locTo[0]?.tckCtMstLocationType?.lctyName
                            }
                        },
                        trCharge: parseInt(chargeDefault)
                    }
                }else{
                    return item;
                }
            })
            
            setLocationRate(filteredData)
            setOnReloadRate(true);
        
        
    }

    const deleteTripRate = (idx) => {
        
        if(idx === 0){
            setLocationRate([])
            setInputData({ ...inputData, "trCharge": '' });
        }else if(idx === locationRate.length-1){
            var filteredData = locationRate.filter(function(elemetn,index) {
                return index !== idx;
            });
            setLocationRate(filteredData)
        }else{
            var filteredData = locationRate.filter(function(elemetn,index) {
                return index !== idx;
            }).map((item,index) => {
                if(index === idx){
                    return {
                        trId: item.tckCtLocationByTrLocTo.locId,
                        tckCtLocationByTrLocFrom: {
                            locId: locationRate[idx-1]?.tckCtLocationByTrLocTo.locId,
                            locName: locationRate[idx-1]?.tckCtLocationByTrLocTo.locName,
                            tckCtMstLocationType:{
                                lctyName: locationRate[idx-1]?.tckCtLocationByTrLocTo.tckCtMstLocationType?.lctyName
                            }
                        },
                        tckCtLocationByTrLocTo: {
                            locId: item.tckCtLocationByTrLocTo.locId,
                            locName: item.tckCtLocationByTrLocTo.locName,
                            tckCtMstLocationType:{
                                lctyName: item.tckCtLocationByTrLocTo.tckCtMstLocationType?.lctyName
                            }
                        },
                        trCharge: parseInt(item.trCharge)
                    }
                }else{
                    return item;
                }
            })
            
            setLocationRate(filteredData)
        }
        
        setOpenDeleteConfirm({ action: null, open: false, id: null })
        setOnReloadRate(true);
        // const rateToBefore = locationRate[index]
    }

    const handlePopUpFieldValidate = (action) => {
        let errors_validation = {};

        if((action === 'NEW' && locationRate.length === 0) || (action === 'UPDATE' && indexRate === 0)){
            if (!popupLocationRate?.tckCtLocationByTrLocFrom?.locId || popupLocationRate?.tckCtLocationByTrLocFrom?.locId === '') {
                errors_validation.fromLocId = locale("common:validationMsgs.required");
            }
            if (!popupLocationRate?.tckCtLocationByTrLocTo?.locId || popupLocationRate?.tckCtLocationByTrLocTo?.locId === '') {
                errors_validation.toLocId = locale("common:validationMsgs.required");
            }
            if (!popupLocationRate?.trCharge || popupLocationRate?.trCharge === '') {
                errors_validation.trCharge = locale("common:validationMsgs.required");
            }
        }else if((action === 'NEW' && locationRate.length > 0) || (action === 'UPDATE' && indexRate > 0)){
            if (!popupLocationRate?.tckCtLocationByTrLocTo?.locId || popupLocationRate?.tckCtLocationByTrLocTo?.locId === '') {
                errors_validation.toLocId = locale("common:validationMsgs.required");
            }
        }
        
        return errors_validation;
    }

    return (<React.Fragment>
        <Grid container spacing={2}>
            <Grid container item xs={12} sm={4} direction="column">
                    <C1CategoryBlock>
                    <C1SelectAutoCompleteField  
                            disabled={isDisabled}
                            name="tckCtMstVehType.vhtyId"
                            label={locale("administration:rateTableManagement.details.vehType")}
                            optionsMenuItemArr={vehListStatus ? 
                                vehList.map((item, i) => {
                                    return {
                                        value: item.vhtyId,
                                        desc: item.vhtyName
                                    }
                                }) : [{
                                        value: inputData?.tckCtMstVehType?.vhtyId,
                                        desc: inputData?.tckCtMstVehType?.vhtyName
                                }]
                            }
                            value={rtVehType || inputData?.tckCtMstVehType.vhtyId}
                            onChange={(e, name, value) => handleInputChange({ target: { name, value: value?.value } })}
                            required
                            error={!!errors.vhtyId}
                            helperText={errors.vhtyId ?? null}
                            />
                    </C1CategoryBlock>
                </Grid>
            <Grid container item xs={12} sm={4} direction="column">
                <C1CategoryBlock>
                    <C1SelectAutoCompleteField
                        name="trType"
                        label={locale("administration:rateTableManagement.tripRates.rateType")}
                        value={isDisabled ? inputData?.trType : inputData?.trType || ''}
                        onChange={(e, name, value) => handleInputChange({ target: { name, value: value?.value } })}
                        required
                        disabled={isDisabled}
                        isServer={false}
                        optionsMenuItemArr={rateType.map((item, ind) => {
                            return {
                                value: item.rateTypeId,
                                desc: item.rateTypeName
                            }
                        })}
                        error={!!errors.trType}
                        helperText={errors.trType ?? null}
                    />
                </C1CategoryBlock>
            </Grid>
            <Grid container item xs={12} sm={4} direction="column">
                <C1CategoryBlock>
                    <C1InputField
                            name="trCharge"
                            label={locale("administration:rateTableManagement.tripRates.charge")}
                            value={inputData?.trCharge || ''}
                            onChange={handleInputChange}
                            required
                            disabled={true}
                            isServer={true}
                            error={!!errors.trCharge}
                            helperText={errors.trCharge ?? null}
                            InputProps={{
                                inputComponent: NumFormat
                            }}
                            inputProps={{
                                // CT-14 - [Admin][Rate Table] -Trip Amount view Page Must be Aligned Properly
                                // style: inputStyle, 
                                onKeyPress: onlyNumber
                            }}
                        />
                </C1CategoryBlock>
            </Grid>
            <Grid container item xs={12} sm={12} direction="column">
                <Grid container spacing={2} >
                    <Grid container item xs={12} sm={12}>
                        <C1DataTable
                            dbName={{ list: listLocationRate?.locationRate }}
                            isServer={false}
                            columns={isDisabled ? columnsView : columns}
                            defaultOrder="trSeq"
                            defaultOrderDirection="asc"
                            showAdd={((inputData?.trType === 'S' && locationRate.length === 0) || inputData?.trType === 'M' || inputData?.trType === '') && !isDisabled ? {
                                type: "popUp",
                                popUpHandler: addLocationRate,
                            } : null}
                            isShowToolbar
                            isRefresh={isRefresh}
                            isShowFilter={false}
                            isShowViewColumns={false}
                            isShowFilterChip={false}
                            isShowDownload={false}
                            isShowPrint={false}
                            isRowSelectable={false}
                        />
                    </Grid>
                </Grid>
            </Grid>

            <Grid item lg={12} md={12} xs={12} >
                <C1CategoryBlock icon={<BorderColorOutlinedIcon />} title={locale("administration:rateTableManagement.tripRates.properties")}>
                    <Grid container spacing={2} >
                        <Grid container item xs={12} sm={6} direction="column">
                            <C1InputField
                                label={locale("administration:rateTableManagement.tripRates.createdBy")}
                                name="trUidCreate"
                                value={inputData?.trUidCreate || ''}
                                disabled
                                isServer={true} />
                            <C1DateTimeField
                                label={locale("administration:rateTableManagement.tripRates.createdDt")}
                                name="trDtCreate"
                                value={inputData?.trDtCreate || ''}
                                disabled
                                isServer={true} />
                        </Grid>
                        <Grid container item xs={12} sm={6} direction="column">
                            <C1InputField
                                label={locale("administration:rateTableManagement.tripRates.updatedBy")}
                                name="trUidLupd"
                                value={inputData?.trUidLupd || ''}
                                disabled
                                isServer={true} />
                            <C1DateTimeField
                                label={locale("administration:rateTableManagement.tripRates.updatedDt")}
                                name="trDtLupd"
                                value={inputData?.trDtLupd || ''}
                                disabled
                                isServer={true} />
                        </Grid>
                    </Grid>
                </C1CategoryBlock>
            </Grid>
        </Grid>
        <TripLocationPopup open={openLocationPopup} handleAfterClose={handleAfterClose} />
        <TripLocationRatePopup 
            indexRate={indexRate}
            action={action}
            open={openLocationRatePopup} 
            handleAfterClose={handleCloseLocationRate} 
            inputData={popupLocationRate} 
            locationState={locationState} 
            handleInputChange={handlePopupInputChange} 
            handleActionOnClick={handleActionOnClick} 
            locationRate={listLocationRate?.locationRate}
            errors={popUpFieldError}
             />

        <ConfirmationDialog
            open={openDeleteConfirm?.open}
            onConfirmDialogClose={() => setOpenDeleteConfirm({ ...openDeleteConfirm, action: null, open: false, id: null })}
            text={openDeleteConfirm?.msg}
            title={locale("common:popup.confirmation")}
            onYesClick={(e) => deleteTripRate(openDeleteConfirm?.id)} />
    </React.Fragment >
    );
};

export default TripRatePopup;


