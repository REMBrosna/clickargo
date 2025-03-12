import { Grid, IconButton, InputAdornment } from "@material-ui/core";
import DescriptionIcon from '@material-ui/icons/DescriptionOutlined';
import GetAppIcon from "@material-ui/icons/GetApp";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1FileUpload from "app/c1component/C1FileUpload";
import C1InputField from "app/c1component/C1InputField";
import C1PopUp from "app/c1component/C1PopUp";
import C1SelectField from "app/c1component/C1SelectField";
import C1TextArea from "app/c1component/C1TextArea";
import useHttp from "app/c1hooks/http";
import { CK_MST_REIMBURSE_TYPE } from "app/c1utils/const";
import NumFormat from "app/clictruckcomponent/NumFormat";

const AddReimbursementPopup = (props) => {

    const {
        inputData,
        errors,
        openAddPopUp,
        setOpenAddPopUp,
        actionEl,
        action,
        handleInputChange,
        isDisabled,
        handleDownloadFile
    } = props

    // Styles
    const inputStyle = {
        textAlign: 'right'
    }

    /** ---------------- Declare states ------------------- */
    const { isLoading, res, urlId, sendRequest } = useHttp();
    const [total, setTotal] = useState(0)

    const { t } = useTranslation(["job"]);

    /** --------------- Update states -------------------- */
    useEffect(() => {
        const calculate = parseInt(inputData.trPrice) + parseInt(inputData.trTax);
        setTotal(calculate ? calculate : 0)
    }, [inputData])

    useEffect(() => {
        if (!isLoading && res) {
            switch (urlId) {
                default: break;
            }
        }
    }, [isLoading, res, urlId]);

    let popupTitle = t("job:tripDetails.viewReimbursement");
    switch (action) {
        case "create":
            popupTitle = t("job:tripDetails.addReimbursement");
            break;
        case "update":
            popupTitle = t("job:tripDetails.editReimbursement");
            break;
        default:
            break;
    }

    return <C1PopUp
        title={popupTitle}
        openPopUp={openAddPopUp}
        setOpenPopUp={setOpenAddPopUp}
        actionsEl={!isDisabled && actionEl}
    >
        <Grid container alignItems="flex-start" spacing={3}>
            <Grid item md={6} xs={12} >
                <C1CategoryBlock icon={<DescriptionIcon />} title={t("job:tripDetails.generalDetails")}>
                    <C1SelectField
                        name="tckCtMstReimbursementType.rbtypId"
                        label={t("job:tripDetails.type")}
                        error={!!errors?.rbtypId}
                        helperText={errors?.rbtypId ?? null}
                        value={inputData?.tckCtMstReimbursementType?.rbtypId}
                        isServer={true}
                        options={{
                            url: CK_MST_REIMBURSE_TYPE,
                            id: "rbtypId",
                            desc: "rbtypName",
                            isCache: true
                        }}
                        onChange={handleInputChange}
                        disabled={isDisabled}
                        required
                    />
                    <Grid container spacing={1} direction="row" alignItems="center">
                        <Grid item xs={inputData?.trReceiptName ? 10 : 12}>
                            <C1FileUpload
                                inputProps={{ placeholder: t("listing:attachments.nofilechosen") }}
                                name="trReceiptName"
                                label={t("job:tripDetails.browse")}
                                inputLabel={t("job:tripDetails.receipt")}
                                value={inputData?.trReceiptName}
                                fileChangeHandler={handleInputChange}
                                errors={!!errors.trReceiptName}
                                helperText={errors.trReceiptName ?? null}
                                disabled={isDisabled}
                                required
                            />
                        </Grid>
                        {inputData?.trReceiptName &&
                            <Grid item xs={2}>
                                <IconButton style={{ marginTop: 5 }} onClick={() => handleDownloadFile(inputData?.base64File, inputData?.trReceiptName)}>
                                    <GetAppIcon fontSize="large" />
                                </IconButton>
                            </Grid>
                        }
                    </Grid>
                    <C1TextArea
                        name={`trRemarks`}
                        label={t("job:tripDetails.description")}
                        disabled={isDisabled}
                        value={inputData.trRemarks}
                        multiline
                        textLimit={512}
                        onChange={handleInputChange}
                    />

                </C1CategoryBlock>
            </Grid>
            <Grid item md={6} xs={12}>
                <C1CategoryBlock icon={<DescriptionIcon />} title={t("job:tripDetails.reimbursementCost")}>
                    <C1InputField
                        name="trPrice"
                        label={t("job:tripDetails.price")}
                        onChange={handleInputChange}
                        value={inputData?.trPrice}
                        disabled={isDisabled}
                        inputProps={{ style: inputStyle }}
                        error={!!errors.trPrice}
                        helperText={errors.trPrice ?? null}
                        required
                        InputProps={{
                            inputComponent: NumFormat,
                            startAdornment:
                                <InputAdornment position="start" style={{ paddingRight: "8px" }}>
                                    Rp
                                </InputAdornment>
                        }}
                    />
                </C1CategoryBlock>
            </Grid>
        </Grid>
    </C1PopUp >
};

export default AddReimbursementPopup;


