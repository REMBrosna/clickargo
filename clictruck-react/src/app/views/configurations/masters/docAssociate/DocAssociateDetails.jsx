import React from "react";
import {
    Grid,
    Box,
} from "@material-ui/core";
import { isEditable } from "app/c1utils/utility";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import { ApplicationType, ATTACH_TYPE, MST_PORT_KH, PEDI_MST_SHIP_TYPE_URL } from "app/c1utils/const";
import C1SelectField from "app/c1component/C1SelectField";
import RadioGroup from "@material-ui/core/RadioGroup";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Radio from "@material-ui/core/Radio";
import { useTranslation } from "react-i18next";
import MenuItem from "@material-ui/core/MenuItem";
import { useFetchDropdownData } from "app/c1hooks/dropdown";


const DocAssociateDetails = (
    {
        inputData, viewType,
        isSubmitting, locale,
        errors, handleInputChange,
        handleRadioChange
    }) => {

    const { t } = useTranslation(["master"]);
    let isDisabled = isEditable(viewType, isSubmitting);

    let parentPortDataList = useFetchDropdownData(MST_PORT_KH, undefined, 'portCode', 'portDescription', undefined, true);
    let shipTypeDataList = useFetchDropdownData(PEDI_MST_SHIP_TYPE_URL, undefined, 'shipTypeId', 'shipTypeName', undefined, true);

    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid container spacing={3} alignItems="center">
                    <Grid item lg={4} md={6} xs={12}>
                        <Grid item xs={12}>
                            <C1SelectField
                                value={inputData?.mstAttType?.mattId || ''}
                                required
                                name="mattId"
                                label={locale("masters:docAssociate.list.table.headers.supportName")}
                                onChange={handleInputChange}
                                disabled={isDisabled}
                                isServer={true}
                                options={{
                                    url: ATTACH_TYPE,
                                    id: 'mattId',
                                    desc: 'mattName',
                                    isCache: false
                                }}
                                error={errors?.["mstAttType.mattId"] ?? false}
                                helperText={errors?.["mstAttType.mattId"] ?? null}
                            />
                            <C1InputField
                                value={inputData?.suppDocShipType || ''}
                                name="suppDocShipType"
                                label={locale("masters:docAssociate.list.table.headers.shipTypeName")}
                                onChange={handleInputChange}
                                disabled={isDisabled}
                                select
                                >
                                <MenuItem value='' key='-2' >Select...</MenuItem>
                                <MenuItem value='ALL' key='-1' >ALL</MenuItem>
                                {shipTypeDataList && shipTypeDataList.map((d, ind) => (
                                    <MenuItem value={d.value} key={ind}> {d.desc} </MenuItem>
                                ))}
                            </C1InputField>
                        </Grid>
                    </Grid>
                    <Grid item lg={4} md={6} xs={12}>
                        <Grid item xs={12}>
                            <C1SelectField
                                value={inputData?.pediMstAppType?.appTypeId || ""}
                                required
                                name="appTypeId"
                                label={locale("masters:docAssociate.list.table.headers.appName")}
                                onChange={handleInputChange}
                                disabled={isDisabled}
                                optionsMenuItemArr={Object.values(ApplicationType).map((app) => {
                                    // Do not include ADSUB and DDSUB in the dropdown
                                    if (app.code !== ApplicationType.ADSUB.code
                                        && app.code !== ApplicationType.DDSUB.code) {
                                        return <MenuItem value={app.code} key={app.code}>{app.desc}</MenuItem>
                                    }

                                    return null;
                                })}
                            />

                            <Box
                                style={{ paddingTop: '15px' }}>{t("masters:docAssociate.list.table.headers.mandate")}</Box>
                            <Box>
                                <RadioGroup
                                    row
                                    name="suppDocAssMandatory"
                                    value={inputData?.suppDocAssMandatory ?? ""}
                                    onChange={handleRadioChange}
                                >
                                    <FormControlLabel
                                        disabled={isDisabled || inputData?.mstAttType?.mattId === "OTH"}
                                        control={<Radio />}
                                        value="Y"
                                        label={t("masters:docAssociate.list.table.headers.yes")}
                                        labelPlacement="start"
                                    />
                                    <FormControlLabel
                                        disabled={isDisabled || inputData?.mstAttType?.mattId === "OTH"}
                                        control={<Radio />}
                                        value="N"
                                        label={t("masters:docAssociate.list.table.headers.no")}
                                        labelPlacement="start"
                                    />
                                </RadioGroup>
                            </Box>
                        </Grid>
                    </Grid>

                    <Grid item lg={4} md={6} xs={12}>
                        <Grid item xs={12}>
                            <C1InputField
                                value={inputData?.suppDocParentPort || ""}
                                name="suppDocParentPort"
                                label={locale("masters:docAssociate.list.table.headers.parentPortName")}
                                onChange={handleInputChange}
                                disabled={isDisabled}
                                select
                                >
                                <MenuItem value='' key='-2' >Select...</MenuItem>
                                <MenuItem value='ALL' key='-1' >ALL</MenuItem>
                                {parentPortDataList && parentPortDataList.map((d, ind) => (
                                    <MenuItem value={d.value} key={ind}> {d.value + "-"} {d.desc} </MenuItem>
                                ))}
                            </C1InputField>

                            <C1InputField
                                label=""
                                style={{
                                    "visibility": "hidden"
                                }} />

                        </Grid>
                    </Grid>
                </Grid>
            </C1TabContainer>
        </React.Fragment>
    );
};

export default DocAssociateDetails;