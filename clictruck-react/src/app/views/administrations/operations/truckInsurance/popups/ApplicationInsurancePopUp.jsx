import React from "react";
import { Box, Button, Grid } from "@material-ui/core";
import C1PopUp from "app/c1component/C1PopUp";
import {
    AddBoxOutlined as AddBoxIcon,
    LocalShippingOutlined,
} from "@material-ui/icons";
import DescriptionIcon from "@material-ui/icons/DescriptionOutlined";
import { useStyles } from "../../../../../c1utils/styles";
import C1SelectAutoCompleteField from "../../../../../c1component/C1SelectAutoCompleteField";
import C1SelectAutoCompleteFieldChanged from "../../../../../clictruckcomponent/C1SelectAutoCompleteFieldChanged";
import C1TextArea from "../../../../../c1component/C1TextArea";
const ApplicationInsurancePopUp = ({
    t,
    error,
    formData,
    openPopUp,
    setOpenPopUp,
    openPopDetails,
    handlePopUpChange,
    handleAddTrucks,
    handleAutoCompleteInput,

   }) => {
    const classes = useStyles();
    return (
        <C1PopUp
            title={""}
            openPopUp={openPopUp}
            setOpenPopUp={() => setOpenPopUp(false)}
            actionsEl={
                <Button
                    style={{ margin: "5px", float: "right" }}
                    type="button"
                    variant="contained"
                    color="primary"
                    size="large"
                    onClick={handleAddTrucks}
                >
                    <AddBoxIcon /> &nbsp; Add
                </Button>
            }
        >
            <Grid container spacing={3}>
                <Grid item md={6} sm={6} xs={12} style={{ paddingBottom: "0px" }}>
                    <Box className={classes.gridContainer}>
            <span style={{ display: "flex", alignItems: "center" }}>
              <LocalShippingOutlined /> &nbsp;Truck Details
            </span>
                    </Box>
                    <Grid item xs={12}>
                        <C1SelectAutoCompleteField
                            disabled={false}
                            name="licenseNo"
                            label={t("administration:truckInsurance.plateNumber")}
                            isServer={true}
                            value={openPopDetails?.licenseNo ?? ""}
                            onChange={handleAutoCompleteInput}
                            options={{
                                url: `/api/v1/dropdown/ckCtVehList`,
                                key: "dtoId",
                                id: "vhPlateNo",
                                desc: "vhPlateNo",
                                isCache: false,
                            }}
                            error={error && error.licenseNo ? true : false}
                            helperText={error && error.licenseNo ? error.licenseNo : null}
                        />
                        <C1TextArea
                            label={t("administration:truckInsurance.makeModel")}
                            name="makeAndModel"
                            multiline
                            required
                            textLimit={1024}
                            disabled={false}
                            value={openPopDetails?.makeAndModel}
                            onChange={handlePopUpChange}
                            error={error && error.makeAndModel ? true : false}
                            helperText={error && error.makeAndModel ? error.makeAndModel : null}
                        />
                    </Grid>
                </Grid>
                <Grid item md={6} sm={6} xs={12}>
                    <Box className={classes.gridContainer}>
            <span style={{ display: "flex", alignItems: "center" }}>
              <DescriptionIcon /> &nbsp;Insurance&nbsp;Details
            </span>
                    </Box>
                    <Grid
                        container
                        alignItems="center"
                        spacing={1}
                        className={classes.gridContainer}
                    >
                        <Grid item xs={12}>
                            <C1SelectAutoCompleteFieldChanged
                                label={t("administration:truckInsurance.coverage")}
                                name="coverage"
                                required
                                value={openPopDetails?.coverage}
                                isShowOtherOption={true}
                                onChange={handleAutoCompleteInput}
                                disabled={false}
                                error={error && error.coverage ? true : false}
                                helperText={error && error.coverage ? error.coverage : null}
                                otherOptions={formData?.coverage ? formData.coverage.map(value => ({value: value, desc: value})) : []}
                            />
                            <C1SelectAutoCompleteFieldChanged
                                label={t("administration:truckInsurance.usage")}
                                name="usage"
                                required
                                value={openPopDetails?.usage}
                                isShowOtherOption={true}
                                onChange={handleAutoCompleteInput}
                                disabled={false}
                                error={error && error.usage ? true : false}
                                helperText={error && error.usage ? error.usage : null}
                                otherOptions={formData?.usage ? formData.usage.map(value => ({value: value, desc: value})) : []}
                            />
                            <C1SelectAutoCompleteFieldChanged
                                label={t("administration:truckInsurance.claims")}
                                name="claims"
                                required
                                value={openPopDetails?.claims}
                                isShowOtherOption={true}
                                onChange={handleAutoCompleteInput}
                                disabled={false}
                                error={error && error.claims ? true : false}
                                helperText={error && error.claims ? error.claims : t("administration:truckInsurance.selectYesOrNo")}
                                otherOptions={formData?.claims ? formData.claims.map(value => ({value: value, desc: value})) : []}
                            />
                            <C1SelectAutoCompleteFieldChanged
                                label={t("administration:truckInsurance.suspension")}
                                name="suspension"
                                required
                                value={openPopDetails?.suspension}
                                isShowOtherOption={true}
                                onChange={handleAutoCompleteInput}
                                disabled={false}
                                error={error && error.suspension ? true : false}
                                helperText={error && error.suspension ? error.suspension : t("administration:truckInsurance.suspensionOfDriving")}
                                otherOptions={formData?.suspension ? formData.suspension.map(value => ({value: value, desc: value})) : []}
                            />
                        </Grid>
                    </Grid>
                </Grid>
            </Grid>
        </C1PopUp>
    );
};
export default ApplicationInsurancePopUp;
