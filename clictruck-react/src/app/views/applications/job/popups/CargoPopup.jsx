import React, {useEffect, useState} from "react";
import {useStyles} from "../../../../c1utils/styles";
import {useTranslation} from "react-i18next";
import useHttp from "../../../../c1hooks/http";
import {MatxLoading} from "../../../../../matx";
import Grid from "@material-ui/core/Grid";
import C1CategoryBlock from "../../../../c1component/C1CategoryBlock";
import DescriptionIcon from "@material-ui/icons/DescriptionOutlined";
import C1SelectField from "../../../../c1component/C1SelectField";
import {Box, Button, MenuItem} from "@material-ui/core";
import C1InputField from "../../../../c1component/C1InputField";
import C1TextArea from "../../../../c1component/C1TextArea";
import C1Alert from "../../../../c1component/C1Alert";
import Snackbar from "@material-ui/core/Snackbar";
import C1SelectAutoCompleteField from "../../../../c1component/C1SelectAutoCompleteField";
import {getValue} from "../../../../c1utils/utility";

const CargoPopup = (prop) => {
    const {
        errors,
        cargoData,
        cargoTypes,
        isDisabled,
        setCargoData,
        handleSave,
        notEditable,
        handleOnClose,
        handleInputCargo,
        ableToModifiesCargo,
        handleAutoComplete
    } = prop;

    const classes = useStyles();
    const { t } = useTranslation(['job']);
    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();
    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: 'top',
        horizontal: 'center',
        msg: 'success',
        severity: 'success'
    });

    useEffect(() => {
        if (cargoData?.cgCargoWeight && !cargoData?.tckCtMstUomWeight?.weiId) {
            setCargoData((prevData) => ({
                ...prevData,
                tckCtMstUomWeight: { weiId: "KGM" },
            }));
        } else if (cargoData?.cgCargoVolume && !cargoData?.tckCtMstUomVolume?.volId){
            setCargoData((prevData) => ({
                ...prevData,
                tckCtMstUomVolume: { volId: "SM3" },
            }));
        }
    }, [cargoData?.cgCargoWeight, cargoData?.cgCargoVolume]);

    const handleClose = () => {
        setSnackBarState({ ...snackBarState, open: false });
    };
    let snackBar;
    if (isSubmitSuccess) {
        const anchorOriginV = snackBarState.vertical;
        const anchorOriginH = snackBarState.horizontal;

        snackBar = <Snackbar
            anchorOrigin={{ vertical: anchorOriginV, horizontal: anchorOriginH }}
            open={snackBarState.open}
            onClose={handleClose}
            autoHideDuration={3000}
            key={anchorOriginV + anchorOriginH
            }>
            <C1Alert onClose={handleClose} severity={snackBarState.severity}>
                {snackBarState.msg}
            </C1Alert>
        </Snackbar>;
    }
    return (
        <React.Fragment>
            {isLoading && <MatxLoading />}
            {snackBar}
            <Grid container alignItems="stretch" spacing={2} className={classes.gridContainer}>
                <Grid item xs={12}>
                    <C1CategoryBlock
                        icon={<DescriptionIcon color={"primary"}/>}
                        title={t("job:tripDetails.generalDetails")}
                    />
                </Grid>
                <Grid item xs={6}>
                    <C1SelectField
                        name="tckCtMstCargoType.crtypId"
                        label={t("job:tripDetails.type")}
                        value={cargoData?.tckCtMstCargoType?.crtypId ?? ""}
                        onChange={handleInputCargo}
                        isServer={true}
                        disabled={notEditable || (ableToModifiesCargo ? !ableToModifiesCargo : isDisabled)}
                        optionsMenuItemArr={cargoTypes.map((cargoType, ind) => (
                            <MenuItem value={cargoType.crtypId} key={cargoType?.crtypId}>
                                {cargoType.crtypName}
                            </MenuItem>
                        ))}
                        error={errors["tckCtMstCargoType.crtypId"] !== undefined}
                        helperText={errors["tckCtMstCargoType.crtypId"] || ""}
                    />
                    <C1InputField
                        name="cgCargoQty"
                        label={t("job:tripDetails.quantity")}
                        type="number"
                        disabled={notEditable || (ableToModifiesCargo ? !ableToModifiesCargo : isDisabled)}
                        onChange={handleInputCargo}
                        value={cargoData?.cgCargoQty ?? ""}
                        error={errors["cgCargoQty"] !== undefined}
                        helperText={errors["cgCargoQty"] || ""}
                    />
                </Grid>
                <Grid item xs={6}>
                    <C1InputField
                        name="cgCargoMarksNo"
                        label={t("job:tripDetails.marksNo")}
                        disabled={notEditable || (ableToModifiesCargo ? !ableToModifiesCargo : isDisabled)}
                        onChange={handleInputCargo}
                        value={cargoData?.cgCargoMarksNo ?? ""}
                        error={errors["cgCargoMarksNo"] !== undefined}
                        helperText={errors["cgCargoMarksNo"] || ""}
                    />
                    <C1InputField
                        name="cgCargoQtyUom"
                        label={t("job:tripDetails.quantityUom")}
                        disabled={notEditable || (ableToModifiesCargo ? !ableToModifiesCargo : isDisabled)}
                        onChange={handleInputCargo}
                        value={cargoData?.cgCargoQtyUom}
                        error={errors["cgCargoQty"] !== undefined}
                        helperText={errors["cgCargoQty"] || ""}
                    />
                </Grid>

                <Grid item xs={12}>
                    <C1CategoryBlock
                        icon={<DescriptionIcon color={"primary"}/>}
                        title={t("job:tripDetails.weightDimension")}
                    >
                    </C1CategoryBlock>
                </Grid>
                <Grid item xs={3}>
                    <C1InputField
                        name="cgCargoWeight"
                        label={t("job:tripDetails.weight")}
                        type="number"
                        // required
                        disabled={notEditable || (ableToModifiesCargo ? !ableToModifiesCargo : isDisabled)}
                        onChange={handleInputCargo}
                        value={cargoData?.cgCargoWeight ?? ""}
                        error={errors["cgCargoWeight"] !== undefined}
                        helperText={errors["cgCargoWeight"] || ""}
                    />
                    <C1InputField
                        name="cgCargoLength"
                        label={t("job:tripDetails.length")}
                        type="number"
                        disabled={notEditable || (ableToModifiesCargo ? !ableToModifiesCargo : isDisabled)}
                        onChange={handleInputCargo}
                        value={cargoData?.cgCargoLength}
                    />
                </Grid>
                <Grid item xs={3}>
                    <C1SelectAutoCompleteField
                        name="tckCtMstUomWeight.weiId"
                        label={t("job:tripDetails.cgCargoWeightUom")}
                        disabled={notEditable || (ableToModifiesCargo ? !ableToModifiesCargo : isDisabled)}
                        onChange={handleAutoComplete}
                        value={cargoData?.tckCtMstUomWeight?.weiId ?? ""}
                        isServer={true}
                        disablePortal={false}
                        options={{
                            url: `/api/v1/clickargo/clictruck/master/uomWeight`,
                            key: "weiId",
                            id: 'weiId',
                            desc: 'weiDesc',
                            isCache: true
                        }}
                        error={errors["cgCargoWeightUom"] !== undefined}
                        helperText={errors["cgCargoWeightUom"] ?? ""}
                    />
                    <C1InputField
                        name="cgCargoWidth"
                        label={t("job:tripDetails.width")}
                        type="number"
                        disabled={notEditable || (ableToModifiesCargo ? !ableToModifiesCargo : isDisabled)}
                        onChange={handleInputCargo}
                        value={cargoData?.cgCargoWidth ?? ""}
                    />
                </Grid>
                <Grid item xs={3}>
                    <C1InputField
                        name="cgCargoVolume"
                        label={t("job:tripDetails.volume")}
                        type="number"
                        disabled={notEditable || (ableToModifiesCargo ? !ableToModifiesCargo : isDisabled)}
                        onChange={handleInputCargo}
                        value={cargoData?.cgCargoVolume ?? ""}
                        error={errors["cgCargoVolume"] !== undefined}
                        helperText={errors["cgCargoVolume"] || ""}
                    />
                    <C1InputField
                        name="cgCargoHeight"
                        label={t("job:tripDetails.height")}
                        type="number"
                        disabled={notEditable || (ableToModifiesCargo ? !ableToModifiesCargo : isDisabled)}
                        onChange={handleInputCargo}
                        value={cargoData?.cgCargoHeight ?? ""}
                    />
                </Grid>
                <Grid item xs={3}>
                    <C1SelectAutoCompleteField
                        name="tckCtMstUomVolume.volId"
                        label={t("job:tripDetails.volumeUom")}
                        disabled={notEditable || (ableToModifiesCargo ? !ableToModifiesCargo : isDisabled)}
                        onChange={handleAutoComplete}
                        value={cargoData?.tckCtMstUomVolume?.volId ?? ""}
                        isServer={true}
                        disablePortal={false}
                        options={{
                            url: `/api/v1/clickargo/clictruck/master/uomVolume`,
                            key: "volId",
                            id: 'volId',
                            desc: 'volDesc',
                            isCache: true
                        }}
                        error={errors["cgCargoVolume"] !== undefined}
                        helperText={errors["cgCargoVolume"] || ""}
                    />
                    <C1SelectAutoCompleteField
                        label={t("job:tripDetails.sizeUom")}
                        name="tckCtMstUomSize.sizId"
                        disabled={notEditable || (ableToModifiesCargo ? !ableToModifiesCargo : isDisabled)}
                        onChange={handleAutoComplete}
                        value={getValue(cargoData?.tckCtMstUomSize?.sizId)}
                        isServer={true}
                        disablePortal={false}
                        options={{
                            url: `/api/v1/clickargo/clictruck/master/uomSize`,
                            key: "sizId",
                            id: 'sizId',
                            desc: 'sizDesc',
                            isCache: true
                        }}
                        error={errors["cgCargoQty"] !== undefined}
                        helperText={errors["cgCargoQty"] || ""}
                    />
                </Grid>

                <Grid item xs={12}>
                    <C1CategoryBlock
                        icon={<DescriptionIcon color={"primary"}/>}
                        title={t("job:tripDetails.descriptionInstruction")}
                    >
                    </C1CategoryBlock>
                </Grid>
                <Grid item xs={6}>
                    <C1TextArea
                        name="cgCargoDesc"
                        label={t("job:tripDetails.description")}
                        multiline
                        textLimit={2048}
                        disabled={notEditable || (ableToModifiesCargo ? !ableToModifiesCargo : isDisabled)}
                        onChange={handleInputCargo}
                        value={cargoData?.cgCargoDesc ?? ""}
                    />
                </Grid>
                <Grid item xs={6}>
                    <C1TextArea
                        name="cgCargoSpecialInstn"
                        label={t("job:tripDetails.specialInstruction")}
                        multiline
                        textLimit={2048}
                        disabled={notEditable || (ableToModifiesCargo ? !ableToModifiesCargo : isDisabled)}
                        onChange={handleInputCargo}
                        value={cargoData?.cgCargoSpecialInstn ?? ""}
                    />
                </Grid>

                <div style={{display: 'flex', justifyContent: 'flex-end', width: '100%', margin: '20px'}}>
                    {notEditable || (ableToModifiesCargo ? !ableToModifiesCargo : isDisabled) ? (

                            <Button
                                variant="contained"
                                color="default"
                                onClick={handleOnClose}
                                style={{color: 'white', backgroundColor: 'orange'}}
                            >
                                Cancel
                            </Button>

                    ) : (
                        <>
                        <Button
                            variant="contained"
                            color="secondary"
                            onClick={handleSave}
                            style={{marginRight: '20px', color: 'white', backgroundColor: '#3d5afe'}}
                        >
                            Save
                        </Button>
                        <Button
                        variant="contained"
                        color="default"
                        onClick={handleOnClose}
                    style={{color: 'white', backgroundColor: 'orange'}}
                >
                    Cancel
                </Button>
                        </>
                )}
                </div>
            </Grid>
        </React.Fragment>
    );
};

export default CargoPopup;