import {Divider, Grid} from "@material-ui/core";
import DescriptionIcon from '@material-ui/icons/DescriptionOutlined';
import React, {useEffect, useState} from "react";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1TabContainer from "app/c1component/C1TabContainer";
import Box from "@material-ui/core/Box";
import C1SelectAutoCompleteField from "../../../../../c1component/C1SelectAutoCompleteField";
import PostAddIcon from '@material-ui/icons/PostAdd';
import ListItemText from "@material-ui/core/ListItemText";
import makeStyles from "@material-ui/core/styles/makeStyles";
import SettingsApplicationsIcon from '@material-ui/icons/SettingsApplications';
import C1InputField from "../../../../../c1component/C1InputField";
import Button from "@material-ui/core/Button";
import AssignmentIcon from '@material-ui/icons/Assignment';
import Carousel from "react-elastic-carousel";
import LeaseApplicationPopupForm from "../popups/LeaseApplicationPopupForm";
import Snackbar from "@material-ui/core/Snackbar";
import C1Alert from "../../../../../c1component/C1Alert";
import ConfirmationDialog from "../../../../../../matx/components/ConfirmationDialog";
import useHttp from "../../../../../c1hooks/http";
import ImageCards from "./ImageCard";
import FilterNoneIcon from '@material-ui/icons/FilterNone';
import EcoIcon from '@material-ui/icons/Eco';
import TabIcon from '@material-ui/icons/Tab';

const useStyles = makeStyles((theme) => ({
    root: {
        width: '100%',
        backgroundColor: theme.palette.background.paper,
    },
}));

const TrucksRentalDetails = (props) => {

    const {
        page,
        errors,
        setPage,
        viewType,
        editable,
        translate,
        setErrors,
        inputData,
        providers,
        isDisabled,
        setInputData,
    } = props;

    const classes = useStyles();
    const { isLoading, res, error, urlId, sendRequest } = useHttp();

    const [popUp, setPopUp] = useState(false)
    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: "top",
        horizontal: "center",
        msg: translate("common:common.msg.deleted"),
        severity: "success",
    });
    const [open, setOpen] = useState(false);
    const [loading, setLoading] = useState(false);
    const [success, setSuccess] = useState(false);
    const [imageSource, setImageSource] = useState("")
    const [openActionConfirm, setOpenActionConfirm] = useState({ action: null, open: false });
    const [confirm, setConfirm] = useState({ trucksName: null });

    useEffect(() => {
        if (!isLoading && !error && res) {
            switch (urlId) {
                case "CREATE":
                    setLoading(false);
                    setSuccess(true)
                    setSnackBarState({
                        ...snackBarState,
                        open: true,
                        success: true,
                        severity: "success" ,
                        msg: translate(`common:common.msg.submitted`),
                    });
                    break;
                default:
                    break;
            }
        }
    }, [isLoading, error, res, urlId]);

    const handleOnSelectChange = (e, name, obj, reason) => {
        setInputData(pre => ({...pre, [name]: obj?.value}))
        setPage({current: 1, currentIndex: 0 ,allPage: providers?.find(val => val?.provider === obj?.value)?.trucks?.length})
        if (reason === 'clear') {
            setPage({current: 0, allPage: 0})
        }
    }

    const handleInputChange = (e) => {
        const { value, name } = e.target;
        setInputData(pre => ({...pre, [name]: value}))
    }

    const handlePopupForm = () => {
        setPopUp(true)
    }

    const handleOnSwipeChange = (currentItem, currentIndex) => {
        setImageSource("")
        setPage(pre => ({...pre, current: currentIndex + 1, currentIndex: currentIndex}))
        setInputData(pre => ({
            ...pre,
            truck: "",
            lease: "",
            price: "",
            quantity: ""
        }))
    };

    const handleCloseSnackBar = () => {
        setSnackBarState({ ...snackBarState, open: false });
        setPopUp(false)
    };

    const handleActionConfirm = () => {
        if (confirm && !confirm.trucksName) return;
        setLoading(true);
        setOpen(false)
        switch (openActionConfirm?.action) {
            case "SUBMIT":
                const [month, price] = inputData?.lease?.split(":")
                inputData.lease = month;
                sendRequest("/api/v1/clickargo/clictruck/administrator/rentalApp", "CREATE", "POST", { ...inputData });
                break;
            default:
                break;
        }
    };

    let snackBar = null;
    if (success) {
        const anchorOriginV = snackBarState.vertical;
        const anchorOriginH = snackBarState.horizontal;
        snackBar = (
            <Snackbar
                anchorOrigin={{ vertical: anchorOriginV, horizontal: anchorOriginH }}
                open={snackBarState.open}
                onClose={handleCloseSnackBar}
                autoHideDuration={3000}
                key={anchorOriginV + anchorOriginH}
            >
                <C1Alert
                    onClose={handleCloseSnackBar}
                    severity={snackBarState.severity}
                >
                    {snackBarState.msg}
                </C1Alert>
            </Snackbar>
        );
    };
    const subVal = providers?.find(val => val?.provider === inputData?.provider);

    return (<>
        <Grid item xs={12} style={{marginTop: "32px"}}>
            {snackBar}
            <C1TabContainer style={{borderBottom: "2px solid #d3d8da3d"}}>
                <Grid item sm={6} xs={12}>
                    <Box style={{width: "50%"}}>
                        <C1SelectAutoCompleteField
                            optionsMenuItemArr={providers?.map((item, i) => {
                                return {
                                    value: item?.provider,
                                    desc: item?.provider
                                }
                            })}
                            required
                            name="provider"
                            label={translate("administration:trucksRental.rentals.provider")}
                            value={inputData?.provider}
                            onChange={handleOnSelectChange}
                            error={errors && errors["provider"] || undefined}
                            helperText={errors && errors["provider"] || ''}
                        />
                    </Box>
                </Grid>
                <Grid item sm={6} xs={12}>
                    <Box style={{alignContent: "center", float: "right", paddingTop: "24px"}}>
                        <span style={{ background: '#0772ba0f', padding: '10px', borderRadius: '22px', fontWeight: "600" }}>{`${page?.current}/${page?.allPage}`}</span>
                    </Box>
                </Grid>
            </C1TabContainer>
        </Grid>

        {subVal?.trucks?.length > 0 && (
            <Carousel
                breakPoints={subVal?.trucks}
                initialActiveIndex={page.currentIndex}
                onChange={(currentItemObject, currentPageIndex) => handleOnSwipeChange(currentItemObject, currentPageIndex)}
            >
                {subVal?.trucks?.map((val,ind) => {
                    return (
                        <Grid item xs={12} style={{marginTop: "32px"}} key={ind}>
                            <C1TabContainer>
                                <Grid item lg={4} md={4} sm={12} xs={12}>
                                    <Grid container alignItems="center" spacing={1}  style={{ boxShadow: "0px 0px 2px #2f70b87d", borderRadius: "10px", marginTop: "15px"}}>
                                        <Grid item xs={12} style={{display: "flex", justifyContent: "center" }}>
                                            <Box style={{width: "100%", display: "contents"}}>
                                                <h5 style={{position: "absolute"}}>{val?.name || "NA"}</h5>
                                                <ImageCards
                                                    fileName={val?.picture}
                                                    imageSource={imageSource}
                                                    setImageSource={setImageSource}
                                                    currentIndex={page?.currentIndex}
                                                    currentImg={subVal?.trucks?.[page?.currentIndex]}
                                                />
                                            </Box>
                                        </Grid>
                                    </Grid>
                                </Grid >
                                <Grid item lg={8} md={8} sm={12} xs={12}>
                                    <C1TabContainer>
                                        <Grid item lg={5} md={5} sm={12} xs={12}>
                                            <C1CategoryBlock icon={<DescriptionIcon />} title={translate("administration:trucksRental.rentals.description")}>
                                                <Grid container alignItems="center" spacing={1}>
                                                    <Grid
                                                        item
                                                        xs={12}
                                                        style={{
                                                            overflowY: 'scroll',
                                                            overflowX: "hidden",
                                                            scrollbarWidth: "thin",
                                                            maxHeight: '250px',
                                                            transition: '0.3s',
                                                            borderRadius: '5px',
                                                            marginTop: "4px"
                                                        }}
                                                    >
                                                        {val?.description?.map((desc, index) => {
                                                            return (
                                                                <C1TabContainer style={{margin: "2px"}}>
                                                                    <Grid item lg={12} md={12} sm={6} xs={12} style={{display: "flex"}}>
                                                                        <AssignmentIcon color={"primary"}/>
                                                                        <ListItemText primary={`Cargo Length: ${desc?.cargoLength || "NA"}`} />
                                                                    </Grid>
                                                                    <Grid item lg={12} md={12} sm={6} xs={12} style={{display: "flex"}}>
                                                                       <AssignmentIcon color={"primary"}/>
                                                                       <ListItemText primary={`Cargo Width: ${desc?.cargoWidth || "NA"}`} />
                                                                    </Grid>
                                                                    <Grid item lg={12} md={12} sm={6} xs={12} style={{display: "flex"}}>
                                                                       <AssignmentIcon color={"primary"}/>
                                                                       <ListItemText primary={`Cargo Height: ${desc?.cargoHeight || "NA"}`} />
                                                                    </Grid>
                                                                    <Grid item lg={12} md={12} sm={6} xs={12} style={{display: "flex"}}>
                                                                       <AssignmentIcon color={"primary"}/>
                                                                       <ListItemText primary={`Volume: ${desc?.volume || "NA"}`} />
                                                                    </Grid>
                                                                    <Grid item lg={12} md={12} sm={6} xs={12} style={{display: "flex"}}>
                                                                       <AssignmentIcon color={"primary"}/>
                                                                       <ListItemText primary={`Max Power: ${desc?.maxPower || "NA"}`} />
                                                                    </Grid>
                                                                    <Grid item lg={12} md={12} sm={6} xs={12} style={{display: "flex"}}>
                                                                       <AssignmentIcon color={"primary"}/>
                                                                        <ListItemText primary={`Max Torque: ${desc?.maxTorque || "NA"}`} />
                                                                    </Grid>
                                                                    <Grid item lg={12} md={12} sm={6} xs={12} style={{display: "flex"}}>
                                                                       <AssignmentIcon color={"primary"}/>
                                                                        <ListItemText primary={`Payload: ${desc?.payload || "NA"}`} />
                                                                    </Grid>
                                                                </C1TabContainer>
                                                            )
                                                        })}
                                                    </Grid>
                                                </Grid>
                                            </C1CategoryBlock>
                                        </Grid >
                                        <Grid item lg={7} md={7} sm={12} xs={12} >
                                            <C1CategoryBlock icon={<FilterNoneIcon />} title={translate("administration:trucksRental.rentals.features")}>
                                                <Grid container alignItems="center" spacing={1}>
                                                    <Grid
                                                        item xs={12}
                                                        style={{
                                                            overflowY: 'scroll',
                                                            overflowX: "hidden",
                                                            scrollbarWidth: "thin",
                                                            maxHeight: '250px',
                                                            transition: '0.3s',
                                                            borderRadius: '5px',
                                                            marginTop: "4px"
                                                        }}
                                                    >
                                                        {val?.features?.map((feature, index) => {
                                                            return (
                                                                <C1TabContainer style={{margin: "2px"}}>
                                                                     <Grid item lg={6} md={12} sm={6} xs={12} style={{display: "flex"}}>
                                                                        <SettingsApplicationsIcon color={"primary"}/>
                                                                        <ListItemText primary={`Range KM: ${feature?.rangeKm || "NA"}`} />
                                                                    </Grid>
                                                                     <Grid item lg={6} md={12} sm={6} xs={12} style={{display: "flex"}}>
                                                                        <SettingsApplicationsIcon color={"primary"}/>
                                                                        <ListItemText primary={`Onboard Charging: ${feature?.onboardCharging || "NA"}`} />
                                                                    </Grid>
                                                                     <Grid item lg={6} md={12} sm={6} xs={12} style={{display: "flex"}}>
                                                                        <SettingsApplicationsIcon color={"primary"}/>
                                                                        <ListItemText primary={`Cargo Area MM: ${feature?.cargoAreaMm || "NA"}`} />
                                                                    </Grid>
                                                                     <Grid item lg={6} md={12} sm={6} xs={12} style={{display: "flex"}}>
                                                                        <SettingsApplicationsIcon color={"primary"}/>
                                                                        <ListItemText primary={`Battery Capacity: ${feature?.batteryCapacity || "NA"}`} />
                                                                    </Grid>
                                                                     <Grid item lg={6} md={12} sm={6} xs={12} style={{display: "flex"}}>
                                                                        <SettingsApplicationsIcon color={"primary"}/>
                                                                        <ListItemText primary={`Range: ${feature?.range || "NA"}`} />
                                                                    </Grid>
                                                                     <Grid item lg={6} md={12} sm={6} xs={12} style={{display: "flex"}}>
                                                                        <SettingsApplicationsIcon color={"primary"}/>
                                                                        <ListItemText primary={`Panel: ${feature?.panel || "NA"}`} />
                                                                    </Grid>
                                                                     <Grid item lg={6} md={12} sm={6} xs={12} style={{display: "flex"}}>
                                                                        <SettingsApplicationsIcon color={"primary"}/>
                                                                        <ListItemText primary={`Laden: ${feature?.laden || "NA"}`} />
                                                                    </Grid>
                                                                     <Grid item lg={6} md={12} sm={6} xs={12} style={{display: "flex"}}>
                                                                        <SettingsApplicationsIcon color={"primary"}/>
                                                                        <ListItemText primary={`Screen Display: ${feature?.screenDisplay || "NA"}`} />
                                                                    </Grid>
                                                                     <Grid item lg={6} md={12} sm={6} xs={12} style={{display: "flex"}}>
                                                                        <SettingsApplicationsIcon color={"primary"}/>
                                                                        <ListItemText primary={`Others: ${feature?.others || "NA"}`} />
                                                                    </Grid>
                                                                     <Grid item lg={6} md={12} sm={6} xs={12} style={{display: "flex"}}>
                                                                        <SettingsApplicationsIcon color={"primary"}/>
                                                                        <ListItemText primary={`Curb Weight: ${feature?.curbWeight || "NA"}`} />
                                                                    </Grid>
                                                                     <Grid item lg={6} md={12} sm={6} xs={12} style={{display: "flex"}}>
                                                                        <SettingsApplicationsIcon color={"primary"}/>
                                                                        <ListItemText primary={`Max Weight: ${feature?.maxWeight || "NA"}`} />
                                                                    </Grid>
                                                                     <Grid item lg={6} md={12} sm={6} xs={12} style={{display: "flex"}}>
                                                                        <SettingsApplicationsIcon color={"primary"}/>
                                                                        <ListItemText primary={`Cargo Area MM: ${feature?.cargo_area_mm || "NA"}`} />
                                                                    </Grid>
                                                                     <Grid item lg={6} md={12} sm={6} xs={12} style={{display: "flex"}}>
                                                                        <SettingsApplicationsIcon color={"primary"}/>
                                                                        <ListItemText primary={`Tech: ${feature?.Tech || "NA"}`} />
                                                                    </Grid>
                                                                    <Grid item lg={6} md={12} sm={6} xs={12} style={{display: "flex"}}>
                                                                        <SettingsApplicationsIcon color={"primary"}/>
                                                                        <ListItemText primary={`Estimate Range: ${feature?.estRange || "NA"}`} />
                                                                    </Grid>
                                                                     <Grid item lg={6} md={12} sm={6} xs={12} style={{display: "flex"}}>
                                                                        <SettingsApplicationsIcon color={"primary"}/>
                                                                        <ListItemText primary={`Laden Weight: ${feature?.laden_weight || "NA"}`} />
                                                                    </Grid>
                                                                     <Grid item lg={6} md={12} sm={6} xs={12} style={{display: "flex"}}>
                                                                        <SettingsApplicationsIcon color={"primary"}/>
                                                                        <ListItemText primary={`Kerb Weight: ${feature?.kerbWeight || "NA"}`} />
                                                                    </Grid>
                                                                    <Grid item lg={6} md={12} sm={6} xs={12} style={{display: "flex"}}>
                                                                        <SettingsApplicationsIcon color={"primary"}/>
                                                                        <ListItemText primary={`Tonnage: ${feature?.tonnage || "NA"}`} />
                                                                    </Grid>
                                                                </C1TabContainer>
                                                            )
                                                        })}
                                                    </Grid>
                                                </Grid>
                                            </C1CategoryBlock>
                                        </Grid >
                                        <Grid item lg={6} md={6} sm={12} xs={12} >
                                            <C1CategoryBlock icon={<EcoIcon />} title={translate("administration:trucksRental.rentals.leasePlans")}>
                                                <Grid container alignItems="center" spacing={1}>
                                                    <Grid item lg={12} md={12} sm={6} xs={12}>
                                                        <C1SelectAutoCompleteField
                                                            optionsMenuItemArr={val?.plans?.map((item, i) => {
                                                                return {
                                                                    value: `${item?.months}:${item?.price}`,
                                                                    desc: `${item?.months} Months`
                                                                }
                                                            })}
                                                            required
                                                            name="lease"
                                                            isServer={false}
                                                            label={translate("administration:trucksRental.rentals.months")}
                                                            value={inputData?.lease}
                                                            onChange={(e, name, obj) => {
                                                                const [month, price] = obj?.value?.split(":")
                                                                setInputData({...inputData, [name]: obj?.value, price: price, truck: val?.name, email: subVal?.email})
                                                            }}
                                                            error={errors && errors["lease"] || undefined}
                                                            helperText={errors && errors["lease"] || ''}
                                                        />
                                                    </Grid>
                                                    <Grid item lg={12} md={12} sm={6} xs={12}>
                                                        <span
                                                            style={{fontSize: "1.1rem", padding: "2px", color: "#4e759a"}}
                                                        >
                                                            Price: &nbsp;{inputData?.price && new Intl.NumberFormat('en-SG', { style: 'currency', currency: 'SGD' }).format(inputData?.price )}
                                                        </span>
                                                    </Grid>
                                                </Grid>
                                            </C1CategoryBlock>
                                        </Grid >
                                        <Grid item lg={6} md={6} sm={12} xs={12} >
                                            <C1CategoryBlock
                                                icon={<TabIcon />}
                                                title={translate("administration:trucksRental.rentals.quotation")}
                                            >
                                                <Grid container alignItems="center" spacing={1}>
                                                    <Grid item lg={12} md={12} sm={6} xs={12}>
                                                        <C1InputField
                                                            required
                                                            isInteger
                                                            name="quantity"
                                                            type={"number"}
                                                            label={translate("administration:trucksRental.rentals.trucks")}
                                                            value={inputData?.quantity}
                                                            onChange={handleInputChange}
                                                            error={errors && errors["quantity"] || undefined}
                                                            helperText={errors && errors["quantity"] || ''}
                                                        />
                                                    </Grid>
                                                </Grid>
                                            </C1CategoryBlock>
                                        </Grid>
                                        <Grid item xs={12} style={{justifyContent: "flex-end", display: "flex"}}>
                                            <Button
                                                disabled={inputData?.lease === "" || inputData?.quantity === ""}
                                                style={{margin: "20px"}}
                                                variant="contained"
                                                color="primary"
                                                size="large"
                                                className={classes.button}
                                                startIcon={<PostAddIcon />}
                                                onClick={() => handlePopupForm()}
                                            >
                                                {translate("administration:trucksRental.rentals.rfq")}
                                            </Button>
                                        </Grid >
                                    </C1TabContainer>
                                </Grid >
                            </C1TabContainer>
                        </Grid >
                    )
                })}
            </Carousel>
        )}
            <LeaseApplicationPopupForm
                popUp={popUp}
                errors={errors}
                setOpen={setOpen}
                success={success}
                setPopUp={setPopUp}
                setErrors={setErrors}
                translate={translate}
                inputData={inputData}
                setSuccess={setSuccess}
                setConfirm={setConfirm}
                setLoading={setLoading}
                setInputData={setInputData}
                setOpenActionConfirm={setOpenActionConfirm}
            />
            {confirm && confirm.trucksName && (
                <ConfirmationDialog
                    open={open}
                    title={translate("listing:coJob.popup.confirmation")}
                    text={translate("listing:coJob.msg.confirmation", { action: openActionConfirm?.action, id: confirm.trucksName })}
                    onYesClick={() => handleActionConfirm()}
                    onConfirmDialogClose={() => setOpen(false)}
                />
            )}
        </>
    );
};

export default TrucksRentalDetails;