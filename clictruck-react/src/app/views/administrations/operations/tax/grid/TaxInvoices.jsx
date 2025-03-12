import { Button, Grid, makeStyles, Tooltip } from "@material-ui/core";
import { NearMeOutlined, Publish } from "@material-ui/icons";
import GetAppIcon from '@material-ui/icons/GetAppOutlined';
import React, { useContext, useEffect, useState } from "react";
import { FilePond } from 'react-filepond';
import { useTranslation } from "react-i18next";

import ChipStatus from "app/atomics/atoms/ChipStatus";
import DataTable from "app/atomics/organisms/DataTable";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1PopUp from "app/c1component/C1PopUp";
import useHttp from "app/c1hooks/http";
import { JobStates, ServiceTypes, TaxInvoiceStatus } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import { dialogStyles } from "app/c1utils/styles";
import { customFilterDateDisplay, downloadFile, formatDate, previewPDF } from "app/c1utils/utility";

import TaxContext from "../TaxContext";

import 'filepond/dist/filepond.min.css';

const useStyles = makeStyles((theme) => ({
    scrollbarStyles: {
        overflowY: "auto",
        maxHeight: "400px",
        msOverflowStyle: "none",
        scrollbarWidth: 0,
        scrollbarColor: "#fff"
    },
    dateRowContainer: {
        paddingLeft: 16,

    }
}))

const TaxInvoices = () => {

    const { t } = useTranslation(["buttons", "listing", "ffclaims", "common", "status"]);
    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();

    const { refreshPage, setRefreshPage } = useContext(TaxContext)
    const defaultSnackbarValue = {
        success: false,
        successMsg: "",
        error: false,
        errorMsg: "",
        redirectPath: "",
    };
    const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);

    const bdClasses = useStyles();
    const [isRefresh, setRefresh] = useState(false);
    const [showHistory, setShowHistory] = useState(false);
    const [filterBy, setFilterBy] = useState([{ attribute: "history", value: "default" }]);
    const [loading, setLoading] = useState(false);
    const [openAddPopUp, setOpenAddPopUp] = useState(false);
    const dialogClasses = dialogStyles();
    const [files, setFiles] = useState([]);

    const [currentUploaded, setCurrentUploaded] = useState(1)
    // state for pond ref at UploadPopup
    const [uploadDataRef, setUploadDataRef] = useState(null)


    useEffect(() => {
        console.log("History", showHistory);
        if (showHistory) {
            setFilterBy([{ attribute: "history", value: "history" }]);
        } else {
            setFilterBy([{ attribute: "history", value: "default" }])

        }
    }, [showHistory]);

    useEffect(() => {
        if (!isLoading && !error && res) {
            if (urlId === "downloadFileInv") {
                console.log("downloadFileInv", res);
                // const downloadData = res?.data;
                // downloadFile("excelForTaxReport.xls", downloadData);
                previewPDF("PlatfromInvoice.pdf", res.data);
            } else if (urlId === "downloadFileTaxInvoice") {
                console.log("downloadTaxInvoice", res);
                // let file = res.data;
                // downloadFile("TaxInvoice.pdf",res.data);
                previewPDF("TaxInvoice.pdf", res.data);
            }
        }

    }, [urlId, isLoading, error, res]);

    const toggleHistory = (filter) => {
        setLoading(true);
        setRefresh(false);
        setShowHistory(filter === 'history' ? true : false);
        setTimeout(() => setRefresh(true), 500);
        setTimeout(() => setLoading(false), 500);
    }

    const dlInvHandler = (Id) => {
        const fileEntity = "platformInvoiceByInvNo";
        const fileId = Id.replace(/[^a-zA-Z0-9 ]/g, '');
        // const dlApi = `/api/v1/clickargo/clictruck/attach/byJobId/${fileEntity}/${fileId}`;
        const dlApi = `/api/v1/clickargo/clictruck/attach/${fileEntity}/${Id}`;
        // const dlApi = `/api/v1/clickargo/clictruck/attach/platformInvoice2/CT-PF-20230621-00029`;

        sendRequest(dlApi, "downloadFileInv", "get");
    }

    const dlTaxInvHandler = (Id) => {
        const fileEntity = "taxinvoice";
        const fileId = Id.replace(/[^a-zA-Z0-9 ]/g, '');
        const dlApi = `/api/v1/clickargo/clictruck/attach/${fileEntity}/${fileId}`;
        console.log(dlApi, "downloadFileTaxInvoice", "get");
        sendRequest(dlApi, "downloadFileTaxInvoice", "get");
    }
    const uploadEndpoint = "/api/v1/clickargo/clictruck/upload/taxinvoice";

    const columns =
        [
            // 0 DO NOT REMOVE, USED FOR FILTER ETC tableMeta.rowData[0]
            {
                name: "tiId",
                label: "ID",
                options: {
                    display: "excluded",
                    filter: false
                }
            },
            {
                name: "tiService",
                label: t("listing:taxInvoices.service"),
                options: {
                    filter: true,
                    filterType: 'dropdown',
                    filterOptions: {
                        names: Object.keys(ServiceTypes),
                        renderValue: v => {
                            return ServiceTypes[v].desc;
                        }
                    },
                    customFilterListOptions: {
                        render: v => {
                            return ServiceTypes[v].desc;
                        }
                    },
                    customBodyRender: (value, tableMeta, updateValue) => {
                        return value;
                    }
                },
            },
            {
                name: "tiInvNo",
                label: t("listing:taxInvoices.invoiceNo")
            },
            {
                name: "tiInvDtIssue",
                label: t("listing:taxInvoices.invoiceIssueDate"),
                options: {
                    filter: true,
                    filterType: 'custom',
                    display: true,
                    customFilterListOptions: {
                        render: v => v.map(l => l),
                        update: (filterList, filterPos, index) => {
                            filterList[index].splice(filterPos, 1);
                            return filterList;
                        }
                    },
                    filterOptions: {
                        display: customFilterDateDisplay
                    },
                    customBodyRender: (value, tableMeta, updateValue) => {
                        return <div className={bdClasses.dateRowContainer}>
                            {formatDate(value, true)}
                        </div>

                    }
                },
            },
            {
                name: "tiNo",
                label: t("listing:taxInvoices.taxNo")
            },
            {
                name: "tcoreAccn.accnName",
                label: t("listing:taxInvoices.customer")
            },
            {
                name: "tiStatus",
                label: t("listing:taxInvoices.status"),
                options: {
                    filter: true,
                    filterType: 'dropdown',
                    filterOptions: {
                        names: Object.keys(TaxInvoiceStatus),
                        renderValue: v => {
                            return TaxInvoiceStatus[v].desc;
                        }
                    },
                    customFilterListOptions: {
                        render: v => {
                            return TaxInvoiceStatus[v].desc;
                        }
                    },
                    customBodyRender: (value, tableMeta, updateValue) => {
                        let status = "";
                        switch (value) {
                            case "N": {
                                // status = "NEW";
                                status = <ChipStatus text="New" color="#FFC633" />;
                                break;
                            }
                            case "E": {
                                // status = "EXPORTED";
                                status = <ChipStatus text="Exported" color="#00D16D" />
                                break;
                            }
                            case "C": {
                                // status = "COMPLETED";
                                status = <ChipStatus text="Completed" color="#0095A9" />
                                break;
                            }
                            default: break;
                        }
                        return status;
                    }
                },
            },
            {
                name: "action",
                label: t("listing:taxInvoices.action"),
                options: {
                    filter: false,
                    sort: false,
                    display: true,
                    viewColumns: false,
                    customHeadLabelRender: (columnMeta) => {
                        return <div style={{ textAlign: "center" }}>{columnMeta.label}</div>
                    },
                    customBodyRender: (value, tableMeta, updateValue) => {
                        // console.log(tableMeta);
                        const dlInvoice = <Grid item sm={6} xs={6}>
                            <C1LabeledIconButton
                                tooltip={t("listing:taxInvoices.inv")}
                                label={t("listing:taxInvoices.inv")}
                                action={() => dlInvHandler(tableMeta?.rowData[2])}
                            >
                                <GetAppIcon />
                            </C1LabeledIconButton>
                        </Grid>;
                        const dlTaxInv = <Grid item sm={6} xs={6}>
                            <C1LabeledIconButton
                                tooltip={t("listing:taxInvoices.taxInv")}
                                label={t("listing:taxInvoices.taxInv")}
                                action={() => dlTaxInvHandler(tableMeta?.rowData[0])}
                            >
                                <GetAppIcon />
                            </C1LabeledIconButton>
                        </Grid>
                        return <Grid container direction="row" justifyContent='center' alignItems="center" style={{ minWidth: "120px" }}>
                            {dlInvoice}
                            {(tableMeta.rowData[6] === "C") ? dlTaxInv : ""}
                        </Grid>
                    }
                }
            },
        ];

    const handleUpload = () => {
        // console.log('get file', uploadDataRef.getFiles())

        setCurrentUploaded(1)

        const fileItems = uploadDataRef.getFiles()

        const arrFiles = fileItems.map((fileItem) => {
            return {
                file: fileItem?.file,
                fileExtension: fileItem?.fileExtension,
                fileSize: fileItem?.fileSize,
                fileType: fileItem?.fileType,
                filename: fileItem?.filename,
                filenameWithoutExtension: fileItem?.filenameWithoutExtension
            }
        });

        setFiles(arrFiles);

        const formData = new FormData();
        formData.append(`file`, arrFiles[0].file, arrFiles[0].filename);
        const url = `/api/v1/clickargo/clictruck/tripTax/taxInvoice`;
        const reqID = "uploadFiles";
        const method = "POST";
        const body = formData;
        // console.log("file upload first file", uploadEndpoint, reqID, method, body);
        sendRequest(uploadEndpoint, reqID, method, body);
        // sendRequest(`/api/co/master/entity/portType`, "uploadFiles", "GET");

    }


    let downloadActionEl = <Tooltip title={t("buttons:submit")}>
        <Button onClick={handleUpload} className={dialogClasses.dialogButtonSpace}>
            <NearMeOutlined color="primary" fontSize="large" />
        </Button>
    </Tooltip>

    useEffect(() => {
        if (!isLoading && !error && res && !validation) {
            switch (urlId) {
                case "uploadFiles": {

                    uploadDataRef.removeFile(currentUploaded - 1);

                    if (currentUploaded < files?.length) {

                        setCurrentUploaded(currentUploaded + 1)
                        // console.log("current processing file",currentUploaded);
                        const formData = new FormData();
                        formData.append(`file`, files[currentUploaded].file, files[currentUploaded].filename)

                        const url = `/api/v1/clickargo/clictruck/tripTax/taxInvoice`;
                        const reqID = "uploadFiles";
                        const method = "POST";
                        const body = formData;
                        // console.log("file upload first file", uploadEndpoint, reqID, method, body);
                        sendRequest(uploadEndpoint, reqID, method, body);


                    } else {
                        setOpenAddPopUp(false)
                        setRefresh(!isRefresh)
                        setRefreshPage(refreshPage + 1)

                    }

                    break;
                }
                default: break;
            }
        }
        if (error) {
            //goes back to the screen
            // setLoading(false);
        }
        //If validation has value then set to the errors
        if (validation) {

        }

        // eslint-disable-next-line
    }, [urlId, isLoading, isFormSubmission, res, validation, error]);

    return (
        <>
            <DataTable
                // url={"api/v1/clickargo/clictruck/gli/finance/tax-management/tax-invoices"}
                url={"/api/v1/clickargo/clictruck/taxinvoice"}
                columns={columns}
                title=""
                defaultOrder="tiInvDtIssue"
                defaultOrderDirection="desc"
                isServer={true}
                isShowViewColumns={true}
                isShowDownload={true}
                isShowPrint={true}
                isShowFilter={true}
                isRefresh={isRefresh}
                isShowFilterChip
                filterBy={filterBy}
                guideId={""}
                showActiveHistoryButton={toggleHistory}
                showAddButton={[
                    {
                        label: t("buttons:upload").toUpperCase(),
                        icon: <Publish />,
                        action: () => setOpenAddPopUp(true)
                    }
                ]}
            />

            <C1PopUp
                title={t("listing:taxInvoices.upload")}
                openPopUp={openAddPopUp}
                setOpenPopUp={setOpenAddPopUp}
                actionsEl={downloadActionEl}
                maxWidth={'sm'}

            >
                <Grid className={bdClasses.scrollbarStyles}>

                    <FilePond
                        ref={(ref) => setUploadDataRef(ref)}
                        allowMultiple={true}
                        maxFiles={5000}
                        instantUpload={false}
                    />

                </Grid>
            </C1PopUp>

            {/* For downloading of BL */}
            {/* <Backdrop open={blLoadDlOpen} className={bdClasses.backdrop}> <CircularProgress color="inherit" /></Backdrop> */}
        </>
    )

}

export default TaxInvoices;
