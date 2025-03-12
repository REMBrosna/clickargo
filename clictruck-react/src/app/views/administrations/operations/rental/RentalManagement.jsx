import C1ListPanel from "app/c1component/C1ListPanel";
import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link, useHistory } from "react-router-dom";
import { Button, ButtonGroup, Grid, Icon, IconButton, Tooltip } from "@material-ui/core";
import C1DataTable from "app/c1component/C1DataTable";
import C1FormButtons from "app/c1component/C1FormButtons";
import { Block, DescriptionOutlined, Edit, Visibility } from "@material-ui/icons";
import C1DataTableActions from "app/c1component/C1DataTableActions";
import history from "history.js";
import useAuth from "app/hooks/useAuth";

const RentalMangement = () => {

    const { t } = useTranslation(["buttons", "listing", "administration"]);
    const [showHistory, setShowHistory] = useState(false);
    const { user } = useAuth();

    const popUpAddHandler = () => {
        return true
    };

    const columns = [
        {
            name: "rtId",
            label: t("listing:common.id")
        },
        {
            name: "rtName",
            label: t("listing:common.name")
        },
        {
            name: "tckDoi.doiBlNo",
            label: t("listing:rental.to")
        },
        {
            name: "tckDoi.doiBlNo",
            label: t("listing:rental.noTrucks")
        },
        {
            name: "tckDoi.doiBlNo",
            label: t("listing:common.desc")
        },
        {
            name: "tckDoi.doiBlNo",
            label: t("listing:common.startDate")
        },
        {
            name: "tckDoi.doiBlNo",
            label: t("listing:common.expiryDate")
        },
        {
            name: "tckDoi.doiBlNo",
            label: t("listing:common.dateCreated")
        },
        {
            name: "tckDoi.doiBlNo",
            label: t("listing:common.dateUpdated")
        },
        {
            name: "tckDoi.doiBlNo",
            label: t("listing:common.status")
        },
        {
            name: "tckDoi.doiBlNo",
            label: t("listing:common.action"),
            options: {
                filter: false,
                sort: false,
                display: true,
                viewColumns: false,
                setCellHeaderProps: () => { return { style: { paddingLeft: '5%' } } },
                customBodyRender: (value, tableMeta, updateValue) => {
                    const status = tableMeta.rowData[6];
                    const jobId = tableMeta.rowData[0];
                    return <C1DataTableActions
                    >
                        {/* do note that this beats the purpose of just passing prop values into C1DataTableActions*/}
                        <Grid item xs={12}>
                            <Grid container alignItems="flex-start">
                                <Grid container item alignItems="center">
                                    <Grid item xs={4}>
                                        <span style={{ minWidth: '48px' }}>
                                            {<Link to={`/administrations/truck-management/edit/${jobId}`}>
                                                <Tooltip title={t("buttons:edit")}>
                                                    <IconButton>
                                                        <Edit color="primary" />
                                                    </IconButton>
                                                </Tooltip>
                                            </Link>}
                                        </span>
                                    </Grid>
                                    <Grid item xs={4}>
                                        <span style={{ minWidth: '48px' }}>
                                            {/* {(status === JobStates.SUB.code) && */}
                                            <Tooltip title={t("buttons:cancel")}>
                                                <IconButton onClick={(e) => console.log()} >
                                                    <Block color="primary" />
                                                </IconButton>
                                            </Tooltip>
                                            {/* } */}
                                        </span>
                                    </Grid>
                                    <Grid item xs={4}>
                                        <span style={{ minWidth: '48px' }}>
                                            <Link to={`/applications/services/co/job/view/${jobId}`}>
                                                <Tooltip title={t("buttons:view")}>
                                                    <IconButton>
                                                        <Visibility color="primary" />
                                                    </IconButton>
                                                </Tooltip>
                                            </Link>
                                        </span>
                                    </Grid>
                                </Grid>
                            </Grid>
                        </Grid>
                    </C1DataTableActions>
                    // </Grid>
                }
            }
        },
    ];

    const buttonStyle = {
        marginLeft: "-20px",
        marginButton: "-20px"
    };

    const handleEventDownloadTemplete = () => {
        console.log('download')
    }

    const handleEventUploadTemplete = () => {
        console.log('updaload')
    }

    const handleEventAdd = () => {
        history.push('/administrations/rental-management/new/-')
    }

    return (
        <React.Fragment>
            <C1ListPanel
                routeSegments={[
                    { name: t("administration:rentalManagement.breadCrumbs.list") }
                ]} guideId="clicdo.doi.co.jobs.list"
                title={t("administration:rentalManagement.breadCrumbs.list")}
            >
                <C1DataTable
                    url={"/api/v1/clickargo/clictruck/administrator/vehicle"}
                    isServer={true}
                    columns={columns}
                    defaultOrder="jobId"
                    defaultOrderDirection="desc"
                    // isRefresh={filterHistory}
                    // filterBy={filterHistory}
                    isShowToolbar
                    isShowFilterChip
                    isShowDownload={true}
                    // handleBuildBody={handleDownloadBuildBody}
                    isShowPrint={true}
                    isRowSelectable={false}
                    guideId="clicdo.doi.co.jobs.list.table"
                    filterBy={[
                        { attribute: "TcoreAccn.accnId", value: user?.coreAccn?.accnId }
                    ]}
                    viewTextFilter={
                        <ButtonGroup color="primary" key="viewTextFilter" aria-label="outlined primary button group">
                            <C1FormButtons
                                options={{
                                    uploadTemplate: {
                                        show: true,
                                        eventHandler: handleEventUploadTemplete
                                    },
                                    downloadTemplate: {
                                        show: true,
                                        eventHandler: handleEventUploadTemplete
                                        // path: ""
                                    },
                                    add: {
                                        show: true,
                                        eventHandler: handleEventAdd
                                    },
                                }}
                            />
                        </ButtonGroup>
                    }
                />

            </C1ListPanel>
        </React.Fragment>
    )
}

export default RentalMangement;