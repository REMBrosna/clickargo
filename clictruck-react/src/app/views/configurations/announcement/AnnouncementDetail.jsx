import React from "react";
import Grid from "@material-ui/core/Grid";
import {isEditable} from "app/c1utils/utility";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import {Applications} from "../../../c1utils/const";
import C1SelectField from "../../../c1component/C1SelectField";
import C1DateField from "../../../c1component/C1DateField";
import FormControlLabel from "@material-ui/core/FormControlLabel";

import Switch from "@material-ui/core/Switch";
import MenuItem from "@material-ui/core/MenuItem";

const AnnouncementTypeDetail = (
    {
        inputData,
        viewType,
        isSubmitting,
        locale,
        handleInputChange,
        handleChangeSwitch,
        handleDateChange
    }) => {

    let isDisabled = isEditable(viewType, isSubmitting);

    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid container spacing={3} alignItems="center">
                    <Grid item lg={4} md={6} xs={12}>
                        <Grid item xs={12}>
                            <C1InputField
                                onChange={handleInputChange}
                                required
                                disabled={isDisabled}
                                label={locale("announcement.details.tabs.recordDetails.name")}
                                name="canuDescription"
                                value={inputData?.canuDescription}
                            />

                            <C1DateField
                                label={locale("announcement.details.tabs.recordDetails.fromDate")}
                                name="canuDtFrom"
                                type="date"
                                disabled={isDisabled}
                                required
                                onChange={handleDateChange}
                                value={inputData?.canuDtFrom}
                                disablePast={true}
                            />
                        </Grid>
                    </Grid>
                    <Grid item lg={4} md={6} xs={12}>
                        <Grid item xs={12}>
                            <C1SelectField
                                value={inputData?.TCoreApps?.appsCode || ""}
                                required
                                name="appsCode"
                                label={locale("announcement.details.tabs.recordDetails.application")}
                                onChange={handleInputChange}
                                disabled={isDisabled}
                                optionsMenuItemArr={Object.values(Applications).map((app) => {
                                    return <MenuItem value={app.code} key={app.code}>{app.desc}</MenuItem>
                                })}
                            />

                            <C1DateField
                                label={locale("announcement.details.tabs.recordDetails.display")}
                                name="canuDtDisplay"
                                disabled={isDisabled}
                                required
                                type="date"
                                onChange={handleDateChange}
                                value={inputData?.canuDtDisplay}
                                disablePast={true}
                            />
                        </Grid>
                    </Grid>

                    <Grid item lg={4} md={6} xs={12}>
                        <Grid item xs={12}>
                            <C1SelectField
                                value={inputData?.TMstAnnouncementType?.anypId || ''}
                                required
                                name="anypId"
                                label={locale("announcement.details.tabs.recordDetails.announceType")}
                                onChange={handleInputChange}
                                disabled={isDisabled}
                                isServer={true}
                                options={{
                                    url: "/api/co/anncmt/entity/anncmtType",
                                    id: 'anypId',
                                    desc: 'anypDescription',
                                    isCache: false
                                }}
                            />

                            <C1DateField
                                label={locale("announcement.details.tabs.recordDetails.endDate")}
                                name="canuDtEnd"
                                type="date"
                                disabled={isDisabled}
                                required
                                onChange={handleDateChange}
                                value={inputData?.canuDtEnd}
                                disablePast={true}
                            />
                        </Grid>
                    </Grid>

                    <Grid item lg={12} md={12} xs={12}>
                        <Grid item xs={12}>
                            <C1InputField
                                onChange={handleInputChange}
                                required
                                disabled={isDisabled}
                                label={locale("announcement.details.tabs.recordDetails.subject")}
                                name="canuSubject"
                                value={inputData?.canuSubject}
                            />
                        </Grid>

                        <Grid item xs={12}>
                            <C1InputField
                                onChange={handleInputChange}
                                required
                                disabled={isDisabled}
                                label={locale("announcement.details.tabs.recordDetails.content")}
                                name="canuContent"
                                value={inputData?.canuContent}
                                multiline={true}
                                rows={6}
                            />
                        </Grid>

                        <Grid container spacing={2} alignItems="center">
                            <Grid item xs={6}>
                                <C1InputField
                                    onChange={handleInputChange}
                                    required
                                    disabled={isDisabled}
                                    label={locale("announcement.details.tabs.recordDetails.url")}
                                    name="canuUrl"
                                    value={inputData?.canuUrl}
                                />
                            </Grid>

                            <Grid item xs={6}>
                                <FormControlLabel
                                    control={
                                        <Switch
                                            checked={inputData?.canuPublic === 'Y'}
                                            onChange={handleChangeSwitch}
                                            name="canuPublic"
                                            color="primary"
                                        />
                                    }
                                    label={locale("announcement.list.table.headers.public")}
                                />
                            </Grid>
                        </Grid>

                        {/*<Grid item xs={12}>*/}
                        {/*    <Box>*/}
                        {/*        <RadioGroup*/}
                        {/*            row*/}
                        {/*            name="suppDocAssMandatory"*/}
                        {/*            value={inputData?.suppDocAssMandatory ?? ""}*/}
                        {/*            onChange={handleRadioChange}*/}
                        {/*        >*/}
                        {/*            <FormControlLabel*/}
                        {/*                disabled={isDisabled}*/}
                        {/*                control={<Radio/>}*/}
                        {/*                value="Y"*/}
                        {/*                label={locale("announcement.details.tabs.recordDetails.all")}*/}
                        {/*                labelPlacement="start"*/}
                        {/*            />*/}
                        {/*            <FormControlLabel*/}
                        {/*                disabled={isDisabled}*/}
                        {/*                control={<Radio/>}*/}
                        {/*                value="N"*/}
                        {/*                label={locale("announcement.details.tabs.recordDetails.non")}*/}
                        {/*                labelPlacement="start"*/}
                        {/*            />*/}
                        {/*        </RadioGroup>*/}
                        {/*    </Box>*/}
                        {/*</Grid>*/}
                    </Grid>
                </Grid>
            </C1TabContainer>
        </React.Fragment>
    );
};

export default AnnouncementTypeDetail;