import { Grid, IconButton, Tooltip, setRef } from "@material-ui/core";
import MenuItem from "@material-ui/core/MenuItem";
import BorderColorOutlinedIcon from '@material-ui/icons/BorderColorOutlined';
import LocalAtmOutlinedIcon from '@material-ui/icons/LocalAtmOutlined';
import React, { useEffect, useState } from "react";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DateTimeField from "app/c1component/C1DateTimeField";
import C1InputField from "app/c1component/C1InputField";
import C1SelectField from "app/c1component/C1SelectField";
import NumFormat from "app/clictruckcomponent/NumFormat";

import C1DataTable from "app/c1component/C1DataTable";

const TripRatePopup = (props) => {

    const {
        inputData,
        isDisabled,
        locale,
        listLocationRate,
        handleInputChange
    } = props

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
    const [isRefresh, setRefresh] = useState(false);


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

    const columnsView = [
        {
            name: "rateId",
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
                    return value?.toLocaleString("in-ID", { maximumFractionDigits: 2, style: "currency", currency: "IDR" });
                },
            }
        },
    ]
    console.log('listLocationRate?.locationRate',listLocationRate?.locationRate)
    return (<React.Fragment>
        <Grid container spacing={2}>

            <Grid container item xs={12} sm={12} direction="column">
                <Grid container spacing={2} >
                    <Grid container item xs={12} sm={6}>
                        <C1SelectField
                            name="rateType"
                            label={locale("administration:rateTableManagement.tripRates.rateType")}
                            value={isDisabled ? listLocationRate?.trType : inputData?.rateType || ''}
                            onChange={handleInputChange}
                            required
                            disabled={isDisabled}
                            isServer={false}
                            optionsMenuItemArr={rateType.map((item, ind) => (
                                <MenuItem value={item.rateTypeId} key={ind}>
                                    {item.rateTypeName}
                                </MenuItem>
                            ))}
                        />
                    </Grid>
                    <Grid container item xs={12} sm={6}>
                        <C1InputField
                            name="trCharge"
                            label={locale("administration:rateTableManagement.tripRates.charge")}
                            value={inputData?.trCharge || ''}
                            onChange={handleInputChange}
                            required
                            disabled={true}
                            isServer={true}
                            InputProps={{
                                inputComponent: NumFormat
                            }}
                            inputProps={{
                                onKeyPress: onlyNumber
                            }}
                        />
                    </Grid>
                    <Grid container item xs={12} sm={12}>
                        <C1DataTable
                            dbName={{ list: listLocationRate?.locationRate }}
                            isServer={false}
                            columns={columnsView}
                            defaultOrder="trId"
                            defaultOrderDirection="asc"
                            isShowToolbar={false}
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
    </React.Fragment >
    );
};

export default TripRatePopup;


