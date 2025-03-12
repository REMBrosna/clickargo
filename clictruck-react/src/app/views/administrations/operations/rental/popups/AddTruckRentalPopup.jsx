import { Grid } from "@material-ui/core";
import MenuItem from "@material-ui/core/MenuItem";
import React, { useEffect } from "react";

import C1InputField from "app/c1component/C1InputField";
import C1SelectField from "app/c1component/C1SelectField";
import useHttp from "app/c1hooks/http";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import DescriptionIcon from '@material-ui/icons/DescriptionOutlined';
import C1DateField from "app/c1component/C1DateField";

const AddTruckRentalPopup = (props) => {

    const {
        inputData,
        viewType,
        handleInputChange,
        handleDateChange,
        locale,
        errors
    } = props

    /** ---------------- Declare states ------------------- */
    const { isLoading, res, urlId, sendRequest } = useHttp();

    const attTypes = [{ value: "OTH", desc: "OTHERS" }];

    /** --------------- Update states -------------------- */
    useEffect(() => {

    }, [viewType]);

    useEffect(() => {
        if (!isLoading && res) {
            switch (urlId) {
                default: break;
            }
        }
    }, [isLoading, res, urlId]);

    return (<React.Fragment>
            <Grid container spacing={3}>
                <Grid item md={6} xs={12}>
                    <C1CategoryBlock icon={<DescriptionIcon />} title={locale("administration:rentalManagement.rentalDetails.generalDetails")}>
                        <Grid container alignItems="center" spacing={3}>
                            <Grid item xs={12} >
                                <C1SelectField
                                    label={locale("administration:rentalManagement.rentalDetails.truckType")}
                                    name="tmstAttType.mattId"
                                    required
                                    value={inputData?.tmstAttType?.mattId ? inputData?.tmstAttType?.mattId : ''}
                                    onChange={handleInputChange}
                                    isServer={true}
                                    // options={{
                                    //     url: ATTACH_TYPE,
                                    //     key: "mattId",
                                    //     id: 'mattId',
                                    //     desc: 'mattName',
                                    //     isCache: false
                                    // }}
                                    optionsMenuItemArr={attTypes.map((item, ind) => (
                                        <MenuItem value={item.value} key={ind}>
                                            {item.desc}
                                        </MenuItem>
                                    ))}
                                    error={!!errors?.mattId}
                                    helperText={errors?.mattId ?? null}
                                />
                                <C1InputField
                                    label={locale("administration:rentalManagement.rentalDetails.noOfTruck")}
                                    name="noOfTruck"
                                    type="number"
                                    onChange={handleInputChange}
                                    value={inputData?.noOfTruck || ''} />
                            </Grid>
                        </Grid>
                    </C1CategoryBlock>
                </Grid>
                     
                <Grid item md={6} xs={12} >
                    <C1CategoryBlock icon={<DescriptionIcon />} title={locale("administration:rentalManagement.rentalDetails.generalDetails")}>
                        <Grid container alignItems="center" spacing={3}>
                            <Grid item xs={12} >
                                <C1InputField
                                    label={locale("administration:rentalManagement.rentalDetails.rentalValue")}
                                    name="rentalValue"
                                    type="number"
                                    onChange={handleInputChange}
                                    value={inputData?.rentalValue || ''} />
                                <C1SelectField
                                    label={locale("administration:rentalManagement.rentalDetails.rentalRate")}
                                    name="rentalRate"
                                    required
                                    value={inputData?.rentalRate}
                                    onChange={handleInputChange}
                                    isServer={true}
                                    // options={{
                                    //     url: ATTACH_TYPE,
                                    //     key: "mattId",
                                    //     id: 'mattId',
                                    //     desc: 'mattName',
                                    //     isCache: false
                                    // }}
                                    optionsMenuItemArr={attTypes.map((item, ind) => (
                                        <MenuItem value={item.value} key={ind}>
                                            {item.desc}
                                        </MenuItem>
                                    ))}
                                    error={!!errors?.mattId}
                                    helperText={errors?.mattId ?? null}
                                />
                            </Grid>
                        </Grid>
                    </C1CategoryBlock>
                </Grid>

                <Grid item xs={12} >
                    <C1CategoryBlock icon={<DescriptionIcon />} title={locale("administration:rentalManagement.rentalDetails.properties")}>
                        <Grid container spacing={3}>
                            <Grid item md={6} xs={12}>
                                <C1InputField
                                        label={locale("administration:rentalManagement.rentalDetails.createdBy")}
                                        value={inputData?.createdBy}
                                        name="createdBy"
                                        required
                                        disabled
                                        onChange={handleInputChange}
                                        error={!!errors?.mattId}
                                        helperText={errors?.mattId ?? null}
                                    />

                                    <C1DateField
                                        label={locale("administration:rentalManagement.rentalDetails.createdDate")}
                                        name="createdDate"
                                        required
                                        value={inputData?.createdDate}
                                        disabled
                                        onChange={handleDateChange}
                                        disablePast={true}
                                        error={!!errors?.mattId}
                                        helperText={errors?.mattId ?? null}
                                    />
                            </Grid>

                            <Grid item md={6} xs={12}>
                                <C1InputField
                                        label={locale("administration:rentalManagement.rentalDetails.updatedBy")}
                                        value={inputData?.updatedBy}
                                        name="updatedBy"
                                        required
                                        disabled
                                        onChange={handleInputChange}
                                        error={!!errors?.mattId}
                                        helperText={errors?.mattId ?? null}
                                    />

                                    <C1DateField
                                        label={locale("administration:rentalManagement.rentalDetails.updatedDate")}
                                        name="updatedDate"
                                        required
                                        value={inputData?.updatedDate}
                                        disabled
                                        onChange={handleDateChange}
                                        disablePast={true}
                                        error={!!errors?.mattId}
                                        helperText={errors?.mattId ?? null}
                                    />
                            </Grid>
                        </Grid>
                    </C1CategoryBlock>
                </Grid>
            </Grid>
    </React.Fragment >

    );
};

export default AddTruckRentalPopup;