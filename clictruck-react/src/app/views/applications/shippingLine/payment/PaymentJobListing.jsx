
import React, { useEffect, useState } from "react";
import { useHistory } from "react-router-dom";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import { Button, Checkbox, Dialog, Divider, Grid, IconButton, Paper, Tooltip, Typography } from "@material-ui/core";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useHttp from "app/c1hooks/http";
import { previewPDF } from "app/c1utils/utility";
import useAuth from 'app/hooks/useAuth';
import { MatxLoading } from "matx";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";
import { Roles, Status } from "app/c1utils/const";
import { getFormActionButton } from "app/c1utils/formActionUtils";
import C1Dialog from "app/c1component/C1Dialog";
import C1DataTable from "app/c1component/C1DataTable";
import GetAppIcon from "@material-ui/icons/GetApp";
import { useTranslation } from "react-i18next";

const PaymentJobListing = () => {
    const history = useHistory();
    const { user } = useAuth();
    const { t } = useTranslation(["buttons","other"]);

    /** ------------------ States ---------------------------------*/

    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();

    const [loading, setLoading] = useState(true);

    const [controls, setControls] = useState([]);

    const [verifyErrorOpen, setVerifyErrorOpen] = useState({ msg: null, open: false });
    const [verifySubmitConfirm, setVerifySubmitConfirm] = useState({ action: "", open: false });

    const handleExitOnClick = () => {
        console.log("no  exit for now..");
        //history.push("/applications/documents/do");
    }

    const initialButtons = {
        // back: { show: false, eventHandler: () => handleExitOnClick() },
        // verify: { show: false, eventHandler: () => console.log("initializing") },
        pay: { show: true, eventHandler: () => openPayFfDoiJobConfirmPopup() },
        exit: { show: true, eventHandler: handleExitOnClick }
    };

    //initialize both tabs not allowed to access first until all rendered
    const [allowedAccess, setAllowedAccess] = useState(false);
    /** ------------------- Update states ----------------- */
    useEffect(() => {
        //call for controls
        const reqBody = {
            entityType: "JOB_DOI_FF",
            entityState: Status.SUB.code,
            page: "VIEW",
        };
        sendRequest("/api/v1/clickargo/controls/", "fetchControls", "post", reqBody);

        //check if user is authorized to access
        //   alert(user?.authorities.some(el => el?.authority === Roles.OFFICER.code))
        setAllowedAccess(user?.authorities.some(el => el?.authority === Roles.OFFICER.code) || false);
        //   setTabIndex(user?.authorities.some(el => el?.authority === Roles.OPERATIONS.code) ? 0 : 1);
    }, []);


    useEffect(() => {
        if (!isLoading && !error && res) {
            setLoading(isLoading);
            switch (urlId) {
                case "fetchControls": {
                    setControls([...res.data]);
                    break;
                }
                case "verifyDocs": {
                    setVerifySubmitConfirm({ ...verifySubmitConfirm, open: false });
                    break;
                }
                case "getProformaInvoice": {
                    if (res?.data) {
                        viewFile(res.data);
                    }
                    break;
                }
                case "payDoiFfJob": {
                    console.log('resp ' + JSON.stringify(res))
                    if (res?.data) {
                        setRefresh(false)
                        setSelectedRowIds([])
                        // tableData = []
                        setTimeout(() => setRefresh(true), 150)
                    }
                    break;
                }
                default: break;
            }
        }
    }, [urlId, isLoading, isFormSubmission, res, error]);

    /** ---------------- Event handlers ----------------- */

    const eventHandler = (action) => {
        if (action.toLowerCase() === "pay") {
            openPayFfDoiJobConfirmPopup();
            //    handleVerifyOnClick(tabIndex === 0 ? handleVerifyOnClick("verify_docs") : handleVerifyOnClick("verify_payment"));

        } else if (action.toLowerCase() === "exit") {
            handleExitOnClick();
        }
    };

    const payFfDoiJobHandler = () => {
        sendRequest(`/api/v1/clickargo/clicdo/payment/payFfDoiJobs`, "payDoiFfJob", "post", { jobIds: selectedRowIds })
    }

    const handleViewFile = ffJobId => {
        sendRequest(`/api/v1/clickargo/clicdo/job/payment/txn/invoices/${ffJobId}`, "getProformaInvoice");
    };

    const viewFile = data => {
        previewPDF('proforma_invoices.zip', data);
    };

    let formButtons;
    if (!loading) {
        formButtons = (
            <C1FormButtons options={getFormActionButton(initialButtons, controls, eventHandler)} />
        );
    }

    const [selectedRowIds, setSelectedRowIds] = useState([])

    const doiListingTableCols = [
        {
            name: "", // field name in the row object
            label: "", // column title that will be shown in table
            options: {
                sort: false,
                filter: false,
                customBodyRender: (emptyStr, tableMeta, updateValue) => {
                    const ffJobId = tableMeta.rowData[1]
                    const status = tableMeta.rowData[7]
                    return <Checkbox disabled={status !== 'CONFIRMED'} checked={selectedRowIds.includes(ffJobId)} onChange={({ target: { checked } }) =>
                        checked ? setSelectedRowIds(selectedRowIds.concat(ffJobId)) :
                            setSelectedRowIds(selectedRowIds.filter(rowId => rowId !== ffJobId))
                    } />
                }
            },
        },
        {
            name: "id", // field name in the row object
            label: "Id", // column title that will be shown in table
            options: {
                sort: false,
                filter: false
            },
        },
        {
            name: "shipmentType", // field name in the row object
            label: "Shipment Type", // column title that will be shown in table
            options: {
                sort: false,
                filter: false,
                //   customBodyRender: (tckJob, tableMeta, updateValue) => tckJob?.tckMstShipmentType.shtDesc
            },
        },
        {
            name: "submittedDate", // field name in the row object
            label: "Submitted Date", // column title that will be shown in table
            options: {
                sort: false,
                filter: false,
                //   customBodyRender: (tckJob, tableMeta, updateValue) => tckJob?.tckMstShipmentType.shtDesc
            },
        },
        {
            name: "noOfBl", // field name in the row object
            label: "No of Bls", // column title that will be shown in table
            options: {
                sort: false,
                filter: false
                //  customBodyRender: (tckJob, tableMeta, updateValue) => tckJob?.tckMstShipmentType.shtDesc
            },
        },
        {
            name: "invoiceAmtIdr", // field name in the row object
            label: "Amount (IDR)", // column title that will be shown in table
            options: {
                sort: false,
                filter: false
            },
        },
        {
            name: "invoiceAmtUsd", // field name in the row object
            label: "Amount (USD)", // column title that will be shown in table
            options: {
                sort: false,
                filter: false
            },
        },
        {
            name: "status", // field name in the row object
            label: "Status", // column title that will be shown in table
            options: {
                sort: false,
                filter: false
                //    customBodyRender: (tckJob, tableMeta, updateValue) => tckJob?.tckMstJobState.jbstId
            },
        },
        {
            name: "status", // field name in the row object
            label: "Action", // column title that will be shown in table
            options: {
                sort: false,
                filter: false,
                customBodyRender: (status, tableMeta, updateValue) => {
                    const ffJobId = tableMeta.rowData[1]
                    return (<Grid container alignItems={'center'} justifyContent={'center'}>
                        {(status === 'PAYING' || status === 'PAID') && <Tooltip title={'Proforma Invoice'}>
                            <IconButton aria-label="View" type="button"
                                color="primary">
                                <GetAppIcon onClick={() => handleViewFile(ffJobId)} />
                            </IconButton>
                        </Tooltip>}
                    </Grid>)
                }
            },
        }
    ]

    const filterBy = []

    const [isRefresh, setRefresh] = useState(true);

    const defaultConfirmationObj = {
        openConfirmPopUp: false,
        onConfirmationDialogClose: () => { },
        text: t("payment.confirm.text"),
        title: t("payment.confirm.title"),
        onYesClick: () => { },
        yesBtnText: "buttons:yes",
        noBtnText: "buttons:no"
    }
    const [confirmationObj, setConfirmationObj] = useState(defaultConfirmationObj)

    const openPayFfDoiJobConfirmPopup = () => {
        if (selectedRowIds.length === 0) {
            setConfirmationObj({
                ...confirmationObj,
                openConfirmPopUp: true,
                onConfirmationDialogClose: () => setConfirmationObj(defaultConfirmationObj),
                text: t("payment.confirm.text"),
                title: t("payment.confirm.title"),
                onYesClick: () => {
                    setConfirmationObj(defaultConfirmationObj)
                },
                yesBtnText: 'buttons:ok',
                noBtnText: null
            })
            return
        }
        setConfirmationObj({
            ...confirmationObj,
            openConfirmPopUp: true,
            onConfirmationDialogClose: () => setConfirmationObj(defaultConfirmationObj),
            text: 'Are you sure you want to pay for ' + selectedRowIds.join(', '),
            title: 'Confirm Make Payment',
            onYesClick: () => {
                payFfDoiJobHandler()
                setConfirmationObj(defaultConfirmationObj)
            }
        })
    }

    return loading ? <MatxLoading /> : (<React.Fragment>
        <C1FormDetailsPanel
            breadcrumbs={[
                { name: "Services", path: '' },
                { name: "ClicDo" }
            ]}
            title={t("payment.activeJobPay")}
            formButtons={formButtons}
            isLoading={loading}>
            {(props) => (
                <Grid container spacing={3}>
                    <Grid item xs={12}>
                        <Paper>
                            <Divider className="mb-6" />
                            {<C1TabInfoContainer guideId="clicdo.job-payment.job-listing.table">
                                <C1DataTable url="/api/v1/clickargo/clicdo/payment/getActiveJobsForPayment"
                                    columns={doiListingTableCols}
                                    defaultOrder="createdDate"
                                    defaultOrderDirection={"desc"}
                                    filterBy={filterBy}
                                    isServer={true}
                                    isShowViewColumns={false}
                                    isShowDownload={false}
                                    isShowPrint={false}
                                    isShowFilter={false}
                                    isRefresh={isRefresh}
                                    isRowSelectable={false}
                                    isShowToolbar={false}
                                    isShowDownloadData={false}
                                />


                            </C1TabInfoContainer>}

                        </Paper>
                    </Grid>
                </Grid>
            )}
        </C1FormDetailsPanel>
        {/** Confirmation for verify button */}
        <Dialog maxWidth="xs" open={confirmationObj.openConfirmPopUp} onClose={confirmationObj.onConfirmationDialogClose}>
            <div className="p-8 text-center w-360 mx-auto">
                <h4 className="capitalize m-0 mb-2">{confirmationObj.title}</h4>
                <p>{confirmationObj.text}</p>
                <div className="flex justify-center pt-2 m--2">
                    {confirmationObj.yesBtnText && <Button
                        className="m-2 rounded hover-bg-primary px-6"
                        variant="outlined"
                        color="primary"
                        disabled={false}
                        onClick={confirmationObj.onYesClick}
                    >
                        {t(confirmationObj.yesBtnText)}
                    </Button>}
                    {confirmationObj.noBtnText && <Button
                        className="m-2 rounded hover-bg-secondary px-6"
                        variant="outlined"
                        color="secondary"
                        disabled={false}
                        onClick={confirmationObj.onConfirmationDialogClose}
                    >
                        {t(confirmationObj.noBtnText)}
                    </Button>}
                </div>
            </div>
        </Dialog>
        {/** Dialog popup for validation upon form button clicks*/}
        <C1Dialog
            title="Error"
            isOpen={verifyErrorOpen?.open}
            actionsEl={null}
            maxWidth="xs"
            showActions={false}
            isError
            handleCloseEvent={() => setVerifyErrorOpen({ ...verifyErrorOpen, open: false })} >
            <Typography variant="body1">{verifyErrorOpen?.msg}</Typography>
        </C1Dialog>}
    </React.Fragment>

    );
};

export default withErrorHandler(PaymentJobListing);