import React from "react";
import C1DataTable from 'app/c1component/C1DataTable';
import moment from "moment";
import { useTranslation } from "react-i18next";
import { customFilterDateTimeDisplay, formatDate } from "app/c1utils/utility";
import { Grid } from "@material-ui/core";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import { VisibilityOutlined } from "@material-ui/icons";
import C1PopUp from "app/c1component/C1PopUp";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1TextArea from "app/c1component/C1TextArea";
import C1InputField from "app/c1component/C1InputField";


/**
 * @param filterId - id of the record to display the audits
 */
const PaymentTxnLogs = ({ filterId }) => {

    const { t } = useTranslation(["common","buttons", "listing"]);

    let detailDefaultData = {
        state: "",
        date: "",
        by: "",
        remarks: ""
    }
    const [showDetails, setShowDetails]= React.useState(false)
    const [popUpDetailsData, setPopUpDetailsData] = React.useState(detailDefaultData)

    // let snackBar = null;
    const columns = [
        {
            name: "ptxlId", // field name in the row object
            label: "Log ID", // column title that will be shown in table
            options: {
                display: false,
                filter: false,
                viewColumns: false,
            }
        },
        {
            name: "ptxlTxnState", // field name in the row object
            label: "State", // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
            },

        },
        {
            name: "ptxlDtCreate",
            label: "Date",
            options: {
                sort: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                    // return moment(value).format('YYYY-MM-DD HH:mm:ss');
                },
                filter: true,
                filterType: 'custom',
                filterOptions: {
                    display: customFilterDateTimeDisplay
                },
            },
        },
        {
            name: "ptxlUidCreate",
            label: "By",

        },
        {
            name: "ptxlRemarks",
            label: "Remarks",
            options:{
                customBodyRender: (value, tableMeta, updateValue)=>{
                    let str = value?.slice(0,150)
                    return value?.length >= 150 ? str?.concat("...") :value
                }
            }
        },
        {
            name: "",
            label: t("listing:common.action"),
            options:{
                filter: false,
                sort: false,
                viewColumns: false,
                customHeadLabelRender: (columnMeta) => {
                    return <div style={{ textAlign: "center" }}>{columnMeta.label}</div>
                },
                customBodyRender: (value, tableMeta, updateValue)=>{
                    const state = tableMeta.rowData[1]
                    const date = formatDate(tableMeta.rowData[2], true)
                    const by=  tableMeta.rowData[3]
                    const remarks = tableMeta.rowData[4]
                    return (
                        <Grid
                            container
                            justifyContent="center"
                            alignItems="center"
                        >
                            <C1LabeledIconButton
                                tooltip={t("buttons:view")}
                                label={t("buttons:view")}
                                action={() => handleShowDetails(state, date, by, remarks)}
                            >
                                <VisibilityOutlined />
                            </C1LabeledIconButton>
                        </Grid>
                    )
                }
            }
        },
    ];

    const handleDownloadBuildBody = (values) => {
        return values?.length > 0 && values.map(value => {
            value.data[2] = moment(value?.data[2]).format('YYYY-MM-DD HH:mm').toString();
            return value;
        });
    }

    const handleShowDetails = (state, date, by, remarks)=>{
        if(showDetails === false){
            setPopUpDetailsData({state, date, by, remarks})
            setTimeout(()=> setShowDetails(true), 500)
        }else{
            setShowDetails(false)
            setPopUpDetailsData(detailDefaultData)
        }
    }

    return (
        <React.Fragment>

            <C1PopUp
                title={`Audit Details`}
                openPopUp={showDetails}
                setOpenPopUp={handleShowDetails}
                maxWidth={"md"}
            >
                <C1TabContainer>
                    <Grid item lg={6} md={6} xs={12}>
                        {/* <C1CategoryBlock icon={<BusinessIcon />} title={""}> */}
                            <C1InputField disabled value={popUpDetailsData?.state} label="State" />
                            <C1InputField disabled value={popUpDetailsData?.date} label="Date" />
                            <C1InputField disabled value={popUpDetailsData?.by} label="By" />
                        {/* </C1CategoryBlock> */}
                    </Grid>
                    <Grid item lg={6} md={6} xs={12}>
                        {/* <C1CategoryBlock icon={<ChatBubbleIcon />} title={"Exceptions"}> */}
                            <C1TextArea
                                inputProps={{
                                    readOnly: true,
                                }}
                                multiline={true}
                                label="Remarks"
                                // disabled={true}
                                rows={10}
                                rowsMax={10}
                                value={`${popUpDetailsData?.remarks}`}
                            />
                        {/* </C1CategoryBlock> */}
                    </Grid>
                </C1TabContainer>
            </C1PopUp>

            <C1DataTable url="/api/v1/clickargo/payment/paymentTxnLog"
                columns={columns}
                isShowToolbar={false}
                defaultOrder="ptxlDtCreate"
                isRowsSelectable={false}
                filterBy={[{ attribute: "TCkPaymentTxn.ptxId", value: !filterId ? '-' : filterId }]}
                defaultOrderDirection="desc" isShowFilterChip={false}
                handleBuildBody={handleDownloadBuildBody} />
        </React.Fragment>
    );
};




export default PaymentTxnLogs;