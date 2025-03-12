import React, {useState} from "react";
import { Grid } from "@material-ui/core";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import {DateRangeOutlined} from '@material-ui/icons'
import C1InputField from "app/c1component/C1InputField";
import C1PopUp from "app/c1component/C1PopUp";
import moment from "moment";
import DataTable from "../../../../../atomics/organisms/DataTable";

const ShellCardInvoiceItemsPopupList = (props) => {

    const {
        popUp,
        setPopUp,
        translate,
        inputData,
        setInputData,
    } = props;

    const [isRefresh, setRefresh] = useState(false);

    const handleInputChange = (e) => {
        const { value, name } = e.target;
        setInputData(pre => ({...pre, [name]: value}))
    }

    const columns = [
        {
            name: "invId",
            options: {
                display: false,
            },
        },
        {
            name: "tckCtVeh.vhPlateNo",
            label: translate("administration:shellCardInv.popup.section.invItem.list.vehNo"),
            options: {
                filter: false,
            },
        },
        {
            name: "itmDtTxn",
            label: translate("administration:shellCardInv.popup.section.invItem.list.txnDateTime"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return moment(value).format('YYYY-MM-DD HH:mm A');
                }
            },
        },
        {
            name: "itmDesc",
            label: translate("administration:shellCardInv.popup.section.invItem.list.fuelType"),
            options: {
                filter: false,
            },
        },
        {
            name: "itmCost",
            label: translate("administration:shellCardInv.popup.section.invItem.list.actCost"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return  value ? `SGD ${value?.toLocaleString('en-US')}` : "N/A"
                },
            },
        },
        {
            name: "itmDiscount",
            label: translate("administration:shellCardInv.popup.section.invItem.list.discount"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return  value ? `SGD ${value?.toLocaleString('en-US')}` : "N/A"
                },
            },
        },
        {
            name: "itmTax",
            label: translate("administration:shellCardInv.popup.section.invItem.list.gtsAmt"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return  value ? `SGD ${value?.toLocaleString('en-US')}` : "N/A"
                },
            },
        },
        {
            name: "itmCo2",
            label: translate("administration:shellCardInv.popup.section.invItem.list.co2Com"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return  `SGD ${value?.toLocaleString('en-US')}`
                },
            },
        },
        {
            name: "itmTotal",
            label: translate("administration:shellCardInv.popup.section.invItem.list.totalAmt"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return `SGD ${value?.toLocaleString('en-US')}`
                },
            },
        },
    ];

    return(
        <C1PopUp
            title={translate("administration:shellCardInv.popup.title")}
            openPopUp={popUp}
            setOpenPopUp={setPopUp}
            maxWidth={"lg"}
        >
            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <C1CategoryBlock
                        icon={<DateRangeOutlined />}
                        title={translate("administration:shellCardInv.popup.section.inv.title")}
                    >
                        <Grid container alignItems="center" spacing={1} style={{paddingTop: "25px"}}>
                            <Grid item lg={4} md={4} sm={6} xs={12}>
                                <C1InputField
                                    required
                                    disabled
                                    name={"invNo"}
                                    value={inputData?.invNo}
                                    onChange={handleInputChange}
                                    label={translate("administration:shellCardInv.popup.section.inv.invNo")}
                                />
                            </Grid>
                            <Grid item lg={4} md={4} sm={6} xs={12}>
                                <C1InputField
                                    required
                                    disabled
                                    name={"invDt"}
                                    value={moment(inputData?.invDt).format('YYYY-MM-DD')}
                                    onChange={handleInputChange}
                                    label={translate("administration:shellCardInv.popup.section.inv.invDate")}
                                />
                            </Grid>
                            <Grid item lg={4} md={4} sm={6} xs={12}>
                                <C1InputField
                                    required
                                    disabled
                                    name={"invPaymentDt"}
                                    value={moment(inputData?.invPaymentDt).format('YYYY-MM-DD')}
                                    onChange={handleInputChange}
                                    label={translate("administration:shellCardInv.popup.section.inv.payDate")}
                                />
                            </Grid>
                            <Grid item lg={4} md={4} sm={6} xs={12}>
                                <C1InputField
                                    required
                                    disabled
                                    name={"invAmt"}
                                    value={`SGD ${inputData?.invAmt?.toLocaleString('en-US')}`}
                                    onChange={handleInputChange}
                                    label={translate("administration:shellCardInv.popup.section.inv.amount")}
                                />
                            </Grid>
                            <Grid item lg={4} md={4} sm={6} xs={12}>
                                <C1InputField
                                    required
                                    disabled
                                    name={"invPaymentAmt"}
                                    value={`SGD ${inputData?.invPaymentAmt?.toLocaleString('en-US')}`}
                                    onChange={handleInputChange}
                                    label={translate("administration:shellCardInv.popup.section.inv.payAmount")}
                                />
                            </Grid>
                            <Grid item lg={4} md={4} sm={6} xs={12}>
                                <C1InputField
                                    required
                                    disabled
                                    name={"invBalanceAmt"}
                                    value={`SGD ${inputData?.invBalanceAmt?.toLocaleString('en-US')}`}
                                    onChange={handleInputChange}
                                    label={translate("administration:shellCardInv.popup.section.inv.balAmount")}
                                />
                            </Grid>
                        </Grid >
                    </C1CategoryBlock>
                </Grid>
                <Grid item xs={12}>
                    <C1CategoryBlock
                        icon={<DateRangeOutlined />}
                        title={translate("administration:shellCardInv.popup.section.invItem.title")}
                    >
                        <Grid container alignItems="center" spacing={1} style={{paddingTop: "25px"}}>
                            <DataTable
                                title={""}
                                guideId={""}
                                url="/api/v1/clickargo/clictruck/administrator/shellCardInvoiceItem"
                                isServer={true}
                                isShowFilterChip
                                columns={columns}
                                isShowPrint={true}
                                filterBy={[{ attribute: "invId", value: inputData?.invId }]}
                                isShowToolbar={false}
                                isShowFilter={false}
                                isRefresh={isRefresh}
                                isShowDownload={true}
                                isShowViewColumns={true}
                                defaultOrder="itmDtTxn"
                                defaultOrderDirection="desc"
                                customRowsPerPage={[10, 20]}
                                showActiveHistoryButton={false}
                                showAddButton={false}
                            />
                        </Grid>
                    </C1CategoryBlock>
                </Grid>
            </Grid>
        </C1PopUp>
    )
};

export default ShellCardInvoiceItemsPopupList;