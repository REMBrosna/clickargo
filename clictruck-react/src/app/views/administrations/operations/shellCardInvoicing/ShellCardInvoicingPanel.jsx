import React, {useEffect, useState} from "react";
import {Grid} from "@material-ui/core";
import C1TabContainer from "../../../../c1component/C1TabContainer";
import C1CategoryBlock from "../../../../c1component/C1CategoryBlock";
import DescriptionIcon from "@material-ui/icons/DescriptionOutlined";
import {useTranslation} from "react-i18next";
import PageWrapper from "../../../../atomics/atoms/PageWrapper";
import TrendingUpIcon from '@material-ui/icons/TrendingUp';
import FileCopyIcon from '@material-ui/icons/FileCopy';
import Chart from "react-apexcharts";
import useHttp from "../../../../c1hooks/http";
import ShellCardInvoiceList from "./ShellCardInvoiceList";
import useAuth from "../../../../hooks/useAuth";
import C1ListPanel from "../../../../c1component/C1ListPanel";

const ShellCardInvoicingPanel = () => {

    const { t } = useTranslation([
        "common",
        "status",
        "buttons",
        "listing",
        "administration"
    ]);

    const { user } = useAuth();
    const { isLoading, res, error, urlId, sendRequest } = useHttp();

    const defaultData = {
        options: {
            chart: {
                id: "Saving Statistic"
            },
            xaxis: {
                categories: []
            }
        },
        series: [
            {
                name: "Saving",
                data: []
            }
        ]
    }
    const [statistic, setStatistic] = useState(defaultData);

    useEffect(() => {
        sendRequest(`/api/v1/clickargo/shell/invoicing/statistic/${user?.coreAccn?.accnId}`, "GET_STATISTIC", "GET", {});
    },[])

    useEffect(() => {
        if (!isLoading && !error && res) {
            const response  = res?.data?.data;
            const groupedData = groupByMonth(response);
            const categories = groupedData.map(item => item.month);
            const seriesData = groupedData.map(item => parseFloat(item.totalAmount));
            switch (urlId) {
                case "GET_STATISTIC": {
                    setStatistic({
                        options: {
                            ...statistic.options,
                            xaxis: {
                                categories: categories
                            }
                        },
                        series: [
                            {
                                name: "Saving",
                                data: seriesData
                            }
                        ]
                    });
                    break;
                }
                default: break;
            }
        }
    }, [isLoading, res, error, urlId]);

    const groupByMonth = (data) => {
        const result = data.reduce((acc, item) => {
            // Extract the year and month from the paymentDate with timezone
            const date = new Date(item.paymentDate);
            const yearMonth = date.toLocaleString('en-US', { month: 'short', year: 'numeric', timeZone: 'Asia/Singapore' });

            // Convert totalAmount to a number and add it to the corresponding month
            const amount = parseFloat(item.totalAmount);
            if (!acc[yearMonth]) {
                acc[yearMonth] = 0;
            }
            acc[yearMonth] += amount;

            return acc;
        }, {});

        let groupedData = Object.entries(result).map(([month, totalAmount]) => ({ month, totalAmount: totalAmount.toFixed(2) }));
        groupedData = groupedData.sort((a, b) => new Date(a.month) - new Date(b.month));
        return groupedData;
    }

    return(
        <PageWrapper>
            <C1ListPanel
                routeSegments={[{ name: t("administration:shellCardInv.breadCrumbs.title") }]}
                guideId="null"
                title={t("administration:shellCardInv.breadCrumbs.title")}
            >
                <Grid item xs={12}>
                <C1TabContainer>
                    <Grid item lg={5} md={5} sm={12} xs={12} style={{paddingTop: "50px"}}>
                        <C1CategoryBlock
                            icon={<TrendingUpIcon style={{marginRight:"8px",borderLeft: "1px solid",borderBottom: "1px solid"}}/>}
                            title={t("administration:shellCardInv.section.saving.title")}
                        >
                            <Grid container alignItems="center" spacing={1}>
                                <Grid item xs={12} style={{paddingTop:"24px"}}>
                                    <Chart
                                        options={statistic.options}
                                        series={statistic.series}
                                        type="bar"
                                    />
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>
                    <Grid item lg={7} md={7} sm={12} xs={12} style={{paddingTop: "50px"}}>
                        <C1CategoryBlock
                            icon={<FileCopyIcon style={{marginRight: "8px"}}/>}
                            title={t("administration:shellCardInv.section.invoice.title")}
                        >
                            <Grid container alignItems="center" spacing={1}>
                                <Grid item xs={12}>
                                    <ShellCardInvoiceList user={user}/>
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>
                </C1TabContainer>
            </Grid>
            </C1ListPanel>
        </PageWrapper>
    )
}

export default ShellCardInvoicingPanel;