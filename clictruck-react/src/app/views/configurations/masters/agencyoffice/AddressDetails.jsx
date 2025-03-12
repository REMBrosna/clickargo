import React from "react";
import {
    Grid,
    TextField,
} from "@material-ui/core";

import C1AgencyDropDownList from "app/c1component/dropdownList/C1AgencyDropDownList";
import AgencyOfficeAddrList from "./AgencyOfficeAddrList";
import { useTranslation } from "react-i18next";

const AddressDetails = ({
    inputData,
    handleInputChange,
    handleAgencySelectChange,
    handleAgencyOfficeIdChange,
    viewType,
    isSubmitting,
    errors, }) => {

    let isDisabled = true;
    if (viewType === 'new')
        isDisabled = false;
    else if (viewType === 'edit')
        isDisabled = false;
    else if (viewType === 'view')
        isDisabled = true;

    if (isSubmitting)
        isDisabled = true;

    const { t } = useTranslation(["masters"]);

    return (
        <div>
            <Grid container spacing={3} direction="row" display="flex" justify="flex-start" alignItems="flex-start" >

                <Grid item xs={6}>
                    <TextField
                        fullWidth
                        size="small"
                        className="m-2"
                        disabled={isDisabled}
                        label={t("masters:contract.list.table.headers.fax")}
                        name="agoFax"
                        variant="outlined"
                        onChange={handleInputChange}
                        value={inputData.agoFax || ''}
                        InputLabelProps={{
                            shrink: true,
                        }}
                        error={errors && errors.agoFax ? true : false}
                        helperText={errors && errors.agoFax ? errors.agoFax : null}
                        inputProps={{
                            placeholder: t("common:common.placeHolder.contactFax")
                        }}
                    />
                    <TextField
                        fullWidth
                        size="small"
                        className="m-2"
                        disabled={isDisabled}
                        label={t("masters:contract.list.table.headers.phoneNumber")}
                        name="agoTel"
                        variant="outlined"
                        onChange={handleInputChange}
                        value={inputData.agoTel || ''}
                        InputLabelProps={{
                            shrink: true,
                        }}
                        inputProps={{
                            placeholder: t("common:common.placeHolder.contactTel")
                        }}
                        error={errors && errors.agoTel ? true : false}
                        helperText={errors && errors.agoTel ? errors.agoTel : null}
                    />
                    <TextField
                        fullWidth
                        size="small"
                        className="m-2"
                        disabled={isDisabled}
                        type="email"
                        label={t("masters:contract.list.table.headers.email")}
                        name="agoEmail"
                        variant="outlined"
                        onChange={handleInputChange}
                        value={inputData.agoEmail || ''}
                        InputLabelProps={{
                            shrink: true,
                        }}
                        error={errors && errors.agoEmail ? true : false}
                        helperText={errors && errors.agoEmail ? errors.agoEmail : null}
                    />
                    <TextField
                        fullWidth
                        size="small"
                        className="m-2"
                        disabled={isDisabled}
                        label={t("masters:contract.list.table.headers.postCode")}
                        name="agoPcode"
                        variant="outlined"
                        onChange={handleInputChange}
                        value={inputData.agoPcode || ''}
                        InputLabelProps={{
                            shrink: true,
                        }}
                        error={errors && errors.agoPcode ? true : false}
                        helperText={errors && errors.agoPcode ? errors.agoPcode : null}
                    />
                </Grid>
                <Grid item xs={6}>
                    <TextField
                        fullWidth
                        size="small"
                        className="m-2"
                        disabled={isDisabled}
                        label={t("masters:contract.list.table.headers.address1")}
                        name="agoAddr1"
                        variant="outlined"
                        onChange={handleInputChange}
                        value={inputData.agoAddr1 || ''}
                        InputLabelProps={{
                            shrink: true,
                        }}
                        error={errors && errors.agoAddr1 ? true : false}
                        helperText={errors && errors.agoAddr1 ? errors.agoAddr1 : null}
                    />
                    <TextField
                        fullWidth
                        size="small"
                        className="m-2"
                        disabled={isDisabled}
                        label={t("masters:contract.list.table.headers.address2")}
                        name="agoAddr2"
                        variant="outlined"
                        onChange={handleInputChange}
                        value={inputData.agoAddr2 || ''}
                        InputLabelProps={{
                            shrink: true,
                        }}
                        error={errors && errors.agoAddr2 ? true : false}
                        helperText={errors && errors.agoAddr2 ? errors.agoAddr2 : null}
                    />
                    <TextField
                        fullWidth
                        size="small"
                        className="m-2"
                        disabled={isDisabled}
                        label={t("masters:contract.list.table.headers.address3")}
                        name="agoAddr3"
                        variant="outlined"
                        onChange={handleInputChange}
                        value={inputData.agoAddr3 || ''}
                        InputLabelProps={{
                            shrink: true,
                        }}
                        error={errors && errors.agoAddr3 ? true : false}
                        helperText={errors && errors.agoAddr3 ? errors.agoAddr3 : null}
                    />
                    <TextField
                        fullWidth
                        size="small"
                        className="m-2"
                        disabled={isDisabled}
                        label={t("masters:contract.list.table.headers.cityCode")}
                        name="agoCtycode"
                        variant="outlined"
                        onChange={handleInputChange}
                        value={inputData.agoCtycode || ''}
                        InputLabelProps={{
                            shrink: true,
                        }}
                        error={errors && errors.agoCtycode ? true : false}
                        helperText={errors && errors.agoCtycode ? errors.agoCtycode : null}
                    />
                    <TextField
                        fullWidth
                        size="small"
                        className="m-2"
                        disabled={isDisabled}
                        label={t("masters:contract.list.table.headers.addressProvince")}
                        name="agoProv"
                        variant="outlined"
                        onChange={handleInputChange}
                        value={inputData.agoProv || ''}
                        InputLabelProps={{
                            shrink: true,
                        }}
                        error={errors && errors.agoProv ? true : false}
                        helperText={errors && errors.agoProv ? errors.agoProv : null}
                    />
                </Grid>
            </Grid>

        </div>
    );
};

export default AddressDetails;