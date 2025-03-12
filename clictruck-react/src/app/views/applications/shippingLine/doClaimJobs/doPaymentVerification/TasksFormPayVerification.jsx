
import React from "react";
import { Grid } from "@material-ui/core";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import C1SelectField from "app/c1component/C1SelectField";
import C1DateField from "app/c1component/C1DateField";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import MenuItem from "@material-ui/core/MenuItem";
import C1DataTable from "app/c1component/C1DataTable";
import { CK_MST_PORT_ID } from "app/c1utils/const";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import CalendarTodayIcon from '@material-ui/icons/CalendarTodayOutlined';
import WorkOutlineOutlinedIcon from '@material-ui/icons/WorkOutlineOutlined';
import PaymentOutlinedIcon from '@material-ui/icons/PaymentOutlined';
import PersonIcon from '@material-ui/icons/PersonOutlineOutlined';
import { currencyFormat, formatDate } from "app/c1utils/utility";
import { useTranslation } from "react-i18next";

const TasksFormPayVerification = ({ 
    inputs, 
    handlePayInputChange, 
    filterBy,
    isRefresh,
    selectedTotalCharges,
    setSelectedTotalCharges,
    selectedTotalChargesUsd,
    setSelectedTotalChargesUsd
}) => {

    const { t } = useTranslation(["payments"]);

    const authorizerDropdownOptionsArr = inputs?.info?.authorizerOptions?.map(authorizer => <MenuItem value={authorizer.id} key={authorizer.id}>{authorizer.name}</MenuItem>)

    const doiListingTableCols = [
        {
            name: "doNo", // field name in the row object
            label: t("payments:paymentVerification.list.table.headers.doNo"), // column title that will be shown in table //
            options: {
                sort: false,
                filter: false,
            },
        },
        {
            name: "doiBlNo", // field name in the row object
            label: t("payments:paymentVerification.list.table.headers.bLNo"), // column title that will be shown in table
            options: {
                sort: false,
                filter: false
            },
        },
        {
            name: "invNo",
            label: t("payments:paymentVerification.list.table.headers.invoiceNo"),
            options: {
                sort: false,
                filter: false
            },
        },
        {
            name: "invoiceAmt", // field name in the row object
            label: t("payments:paymentVerification.list.table.headers.invoiceAmt"), // column title that will be shown in table 
            options: {
                sort: false,
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {

                    let count = [];
                    let countUsd = [];
                    let c;
                    for (let i = 0; i < tableMeta.currentTableData.length; i++) {
                        if(tableMeta.currentTableData[i].data[4] === "IDR"){
                            c = tableMeta.currentTableData[i].data[3];
                            if(c)
                                count.push(c);
                        }else if(tableMeta.currentTableData[i].data[4] === "USD"){
                            c = tableMeta.currentTableData[i].data[3];
                            if(c)
                                countUsd.push(c);
                        }
                    }
                    let sum = count.reduce((x, y) => x + y, 0);
                    setSelectedTotalCharges(isNaN(sum) ? 0 : sum);
                    let sumUsd = countUsd.reduce((x, y) => x + y, 0);
                    setSelectedTotalChargesUsd(isNaN(sumUsd) ? 0 : sumUsd);
                    return value?currencyFormat(value):value;
                },
        	}
    	},
        {
            name: "tmstCurrency.ccyCode",
            label: t("payments:paymentVerification.list.table.headers.currency"),
            options: {
                sort: false,
                filter: false
            },
        },
    ]

    return (<React.Fragment>
        <Grid item xs={12}>
            <C1TabContainer>
                <Grid container item spacing={2} alignItems="center">
                    <Grid item xs={4} >
                        <C1CategoryBlock icon={<CalendarTodayIcon />} title={t("payments:paymentVerification.label.sections.genDet")}>
                            <C1DateField
                                label={t("payments:paymentVerification.label.fields.date")}
                                name="date"
                                value={inputs?.query?.date}
                                onChange={(newValue, value2) => handlePayInputChange(value2, 'date')} 
                                disableFuture    
                            />
                        </C1CategoryBlock>
                    </Grid>
                    <Grid item xs={4} >
                        <C1CategoryBlock icon={<WorkOutlineOutlinedIcon />} title={t("payments:paymentVerification.label.sections.countDet", {date: formatDate(inputs?.query?.date)})}>
                            <C1InputField label={t("payments:paymentVerification.label.fields.totalDo")}
                                value={inputs?.info?.totalDo}
                                name="totalDo"
                                disabled={true}
                            />
                        </C1CategoryBlock>
                    </Grid>
                    <Grid item xs={4} >
                        <C1CategoryBlock icon={<PaymentOutlinedIcon />} title={t("payments:paymentVerification.label.sections.totalChrgs", {date: formatDate(inputs?.query?.date)})}>
                            <C1InputField label={t("payments:paymentVerification.label.fields.totalClmChrgIdr")}
                                value={inputs?.info?.totalCharges?currencyFormat(inputs?.info?.totalCharges):0}
                                name="totalCharges"
                                disabled={true}
                            />
                        </C1CategoryBlock>
                    </Grid>
                </Grid>
                <Grid container item spacing={2} alignItems="center">
                    <Grid item xs={4} >
                    </Grid>
                    <Grid item xs={4} >
                        <C1InputField label={t("payments:paymentVerification.label.fields.totalCo")}
                            value={inputs?.info?.totalCo}
                            name="totalCo"
                            disabled={true}
                        />
                    </Grid>
                    <Grid item xs={4} >
                        <C1InputField label={t("payments:paymentVerification.label.fields.totalClmChrgUsd")}
                            value={inputs?.info?.totalChargesUsd?currencyFormat(inputs?.info?.totalChargesUsd):0}
                            name="totalChargesUsd"
                            disabled={true}
                        />
                    </Grid>
                </Grid>
                <Grid container item spacing={2} alignItems="center">
                    <Grid item xs={4} >
                    </Grid>
                </Grid>
                <C1CategoryBlock xs={8} icon={<PersonIcon />} title={t("payments:paymentVerification.label.sections.consignRegionPayList")}>
                    <Grid xs={12} container item spacing={2} alignItems="center">
                        <Grid item xs={6} >
                            <C1SelectField
                                name="authoriserPaymentDetails"
                                label={t("payments:paymentVerification.label.fields.authriserPayDet")}
                                optionsMenuItemArr={authorizerDropdownOptionsArr}
                                onChange={newValue => handlePayInputChange(newValue, 'authorizer')}
                                value={inputs?.query?.authorizer}
                            />
                        </Grid>
                        <Grid item xs={6} >
                            <C1SelectField
                                name="region"
                                label={t("payments:paymentVerification.label.fields.region")}
                                onChange={newValue => handlePayInputChange(newValue, 'region')}
                                isServer={true}
                                options={{
                                    url: CK_MST_PORT_ID,
                                    key: "portCode",
                                    id: 'portCode',
                                    desc: 'portDescription',
                                    isCache: true
                                }}
                                value={inputs?.query?.region}
                            />
                        </Grid>
                    </Grid>
                </C1CategoryBlock>
                <C1DataTable url="/api/v1/clickargo/clicdo/do"
                    columns={doiListingTableCols}
                    title={''}
                    defaultOrder="doDtCreate"
                    defaultOrderDirection={"desc"}
                    filterBy={filterBy}
                    isServer={true}
                    isShowViewColumns={false}
                    isShowDownload={false}
                    isShowPrint={false}
                    isShowFilter={false}
                    isRefresh={isRefresh}
                />
                <Grid container item spacing={2} alignItems="center">
                    <Grid item xs={4}/>
                    <C1CategoryBlock xs={8} icon={<PaymentOutlinedIcon />} title={t("payments:paymentVerification.label.sections.amount")}>
                        <Grid xs={12} container item spacing={2} alignItems="center">
                            <Grid item xs={6} >
                                <C1InputField label={t("payments:paymentVerification.label.fields.amountIdr")}
                                    value={inputs?.info?.totalCharges?currencyFormat(selectedTotalCharges):0}
                                    name="totalInvAmt"
                                    disabled={true}
                                />
                            </Grid>
                            <Grid item xs={6}>
                                <C1InputField label={t("payments:paymentVerification.label.fields.amountUsd")}
                                    value={inputs?.info?.totalChargesUsd?currencyFormat(selectedTotalChargesUsd):0}
                                    name="totalInvAmtUsd"
                                    disabled={true}
                                />
                            </Grid>
                        </Grid>
                    </C1CategoryBlock>
                </Grid>
            </C1TabContainer>
        </Grid >
    </React.Fragment >

    );
};

export default withErrorHandler(TasksFormPayVerification);