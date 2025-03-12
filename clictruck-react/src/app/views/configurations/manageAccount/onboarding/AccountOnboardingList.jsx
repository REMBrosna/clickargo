import { Card, Grid } from "@material-ui/core";
import React from "react";
import { useTranslation } from "react-i18next";
import C1DataTable from 'app/c1component/C1DataTable';
import C1DataTableActions from 'app/c1component/C1DataTableActions';
import C1Information from "app/c1component/C1Information";
import C1ListPanel from "app/c1component/C1ListPanel";
import { getStatusDesc } from "app/c1utils/statusUtils";
import { useStyles } from "app/c1utils/styles";
import { formatDate } from "app/c1utils/utility";

import { accOnBoardingDB } from "../../../../../fake-db/db/accountOnboarding";

const AccountOnboardingList = () => {
    const { t } = useTranslation(["register"]);
    const classes = useStyles();
    const columns = [
        // 1
        {
            name: "accnrId", // field name in the row object
            label: "ID", // column title that will be shown in table
            options: {
                sort: true,
                filter: true
            },
        },
        // 2
        {
            name: "accnrCoIntial",
            label: t("register.table.accountID"),
            options: {
                filter: true,
            },
        },
        // 3
        {
            name: "accnrCompName",
            label: t("register.table.companyName"),
            options: {
                filter: true,
            },
        },
        // 4
        {
            name: "TMstAccnType.atypDescription",
            label: "Account Type",
            options: {
                filter: true,
            },
        },
        // 5
        {
            name: "accnrCompReg",
            label: "Tax Registration No.",
            options: {
                filter: true,
            },
        },
        // 6
        {
            name: "accnrSubmitDt",
            label: "Submit Date",
            options: {
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, false) || "";
                }
            },
        },
        // 7
        {
            name: "accnrApproveDt",
            label: "Approval Date",
            options: {
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, false) || "";
                }
            },
        },
        // 8
        {
            name: "accnrChannel",
            label: "Channel",
            options: {
                filter: true,
            },
        },
        // 9
        {
            name: "accnrStatus",
            label: t("register.table.status"),
            options: {
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return getStatusDesc(value);
                }
            },
        },
        // 10
        {
            name: "action",
            label: " ",
            options: {
                filter: false,
                display: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    if (tableMeta.rowData[8] === 'N') {
                        return <C1DataTableActions
                            editPath={"/onboarding/edit/" + tableMeta.rowData[0]}
                            removeEventHandler={(e) => handleDeleteConfirm(e, tableMeta.rowData[0])} />
                    } else {
                        return <C1DataTableActions
                            viewPath={"/onboarding/view/" + tableMeta.rowData[0]}
                            editPath={"/onboarding/edit/" + tableMeta.rowData[0]}
                            deActiveEventHandler={(e) => handleDeActiveHandler(tableMeta.rowData[0])} />
                    }
                },
            },
        },
    ];

    const handleDeleteConfirm = (e) => {
        return;
    }

    const handleDeActiveHandler = (e) => {
        return;
    }

    return (
        <div>

            <C1ListPanel
                routeSegments={[
                    { name: "Account Registration" },
                ]}
                information={<C1Information information="onboarding" />}
            >

                <Card elevation={3}>
                    <Grid container alignItems="center">
                        <Grid container item direction="column">
                            <Grid item xs={12}>
                                <C1DataTable url="/api/process/all"
                                    columns={columns}
                                    title="Account Registration"
                                    defaultOrder="appId"
                                    dbName={accOnBoardingDB}
                                    showAdd={true}
                                    isServer={false}
                                    isShowViewColumns={false}
                                    isShowDownload={false}
                                    isShowPrint={false}
                                    isShowFilter={false}
                                />
                            </Grid>
                        </Grid>
                    </Grid>
                    {/* <Grid container alignItems="flex-start" class="p-5">
                        <C1Information information="onboarding" />
                    </Grid> */}
                </Card>

            </C1ListPanel>
        </div >
    );
};

export default AccountOnboardingList;
