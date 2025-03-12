import { Box, Button, Card, Divider, Grid, IconButton, Tooltip } from "@material-ui/core";
import MenuItem from "@material-ui/core/MenuItem";
import Typography from '@material-ui/core/Typography';
import PageviewIcon from '@material-ui/icons/Pageview';
import SearchBoxIcon from '@material-ui/icons/Search';
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import C1DataTable from 'app/c1component/C1DataTable';
import C1DataTableActions from 'app/c1component/C1DataTableActions';
import C1DateField from "app/c1component/C1DateField";
import C1InputField from "app/c1component/C1InputField";
import C1ListPanel from "app/c1component/C1ListPanel";
import C1SelectField from "app/c1component/C1SelectField";
import useHttp from "app/c1hooks/http";
import { ApplicationType, ATTACH_TYPE } from "app/c1utils/const";
import { CCM_ACCOUNT_ALL_SL_SA, PEDI_MST_VOYAGE_TYPE_URL } from "app/c1utils/const";
import { deepUpdateState } from "app/c1utils/stateUtils";
import { customFilterDateDisplay, downloadFile, formatDate } from "app/c1utils/utility";
import useAuth from "app/hooks/useAuth";

let URL = "/api/portedi/attach";

const DocumentList = ({ roleId, filterStatus }) => {

    let sectionArray = [];

    const { isLoading, res, error, urlId, sendRequest } = useHttp();
    const { t } = useTranslation(['administration']);
    const defaultSearch = {
        searchApptype: "", searchDocType: "", searchShipperName: "", fromDate: null, toDate: null,
        searchVesselName: "", searchVoyageType: "", searchVoyageNo: "", searchAppnId: ""
    };

    const { user } = useAuth();
    let map = new Set(user.authorities.map(el => el.authority));
    let isHidden = false;
    if (map.has("SHIP_LINE_USER") || map.has("SHIP_AGENT_USER")) {
        isHidden = true;
    }

    const [searchOptions, setSearchOptions] = useState(defaultSearch);
    const [isRefresh, setRefresh] = useState(false);
    const [filterBy, setFilterBy] = useState([])

    useEffect(() => {
        let msg = "";
        let severity = "success";
        if (!isLoading && !error && res) {
            switch (urlId) {
                case "download":
                    viewFile(res.data.fileName, res.data.data);
                    break;
                default:
                    console.log('action not handled.');
                    break;
            }
        } else if (error) {
            msg = "Error encountered whilte trying to fetch data!";
            severity = "error";
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isLoading, res, error, urlId]);


    const handleDateChange = (name, e) => {
        setRefresh(false)
        setSearchOptions({ ...searchOptions, [name]: e });
    }

    const handleInputChange = (e) => {
        setRefresh(false)
        setSearchOptions({ ...searchOptions, ...deepUpdateState(searchOptions, [e.target.name], e.target.value) });
    };

    // For advanced search
    const handleSearch = (e) => {
        setRefresh(false)
        setFilterBy([
            { attribute: "id.appnId", value: searchOptions.searchAppnId },
            { attribute: "id.appnAppType", value: searchOptions.searchAppType },
            { attribute: "id.docType", value: searchOptions.searchDocType },
            { attribute: "id.accnId", value: searchOptions.searchShipperName },
            { attribute: "fromDate", value: formatDate(searchOptions.fromDate) },
            { attribute: "toDate", value: formatDate(searchOptions.toDate) },
            { attribute: "id.apexShipName", value: searchOptions.searchVesselName },
            { attribute: "id.apexVoyageType", value: searchOptions.searchVoyageType },
            { attribute: "id.apexVoyageNo", value: searchOptions.searchVoyageNo }
        ])
        setRefresh(true)
    }

    const viewFile = (fileName, data) => {
        downloadFile(fileName, data);
    };

    const columns = ({ roleId, filterStatus }) => {
        return [
            {
                name: "id.attId",
                options: {
                    display: 'excluded'
                },
            },
            {
                name: "id.appnId",
                label: t("document.list.table.headers.appnId"),
                options: {
                    sort: true,
                    filter: true
                },
            },
            {
                name: "id.appnAppType",
                label: t("document.list.table.headers.appnAppType"),
                options: {
                    filter: true,
                    filterType: 'dropdown',
                    filterOptions: {
                        names: [ApplicationType.VP.code, ApplicationType.SR.code,
                        ApplicationType.VC.code, ApplicationType.EP.code,
                        ApplicationType.PAS.code, ApplicationType.PO.code,
                        ApplicationType.DOS.code, ApplicationType.SSCC.code,
                        ApplicationType.SSCEC.code, ApplicationType.PAN.code,
                        ApplicationType.AD.code, ApplicationType.DD.code],
                        renderValue: v => {
                            switch (v) {
                                case ApplicationType.AD.code: return ApplicationType.AD.code;
                                case ApplicationType.DD.code: return ApplicationType.DD.code;
                                case ApplicationType.DOS.code: return ApplicationType.DOS.code;
                                case ApplicationType.EP.code: return ApplicationType.EP.code;
                                case ApplicationType.PAN.code: return ApplicationType.PAN.code;
                                case ApplicationType.PAS.code: return ApplicationType.PAS.code;
                                case ApplicationType.PO.code: return ApplicationType.PO.code;
                                case ApplicationType.SR.code: return ApplicationType.SR.code;
                                case ApplicationType.SSCC.code: return ApplicationType.SSCC.code;
                                case ApplicationType.SSCEC.code: return ApplicationType.SSCEC.code;
                                case ApplicationType.VC.code: return ApplicationType.VC.code;
                                case ApplicationType.VP.code: return ApplicationType.VP.code;
                                default: break;
                            }
                        }
                    },
                    customFilterListOptions: {
                        render: v => {
                            switch (v) {
                                case ApplicationType.AD.code: return ApplicationType.AD.code;
                                case ApplicationType.DD.code: return ApplicationType.DD.code;
                                case ApplicationType.DOS.code: return ApplicationType.DOS.code;
                                case ApplicationType.EP.code: return ApplicationType.EP.code;
                                case ApplicationType.PAN.code: return ApplicationType.PAN.code;
                                case ApplicationType.PAS.code: return ApplicationType.PAS.code;
                                case ApplicationType.PO.code: return ApplicationType.PO.code;
                                case ApplicationType.SR.code: return ApplicationType.SR.code;
                                case ApplicationType.SSCC.code: return ApplicationType.SSCC.code;
                                case ApplicationType.SSCEC.code: return ApplicationType.SSCEC.code;
                                case ApplicationType.VC.code: return ApplicationType.VC.code;
                                case ApplicationType.VP.code: return ApplicationType.VP.code;
                                default: break;
                            }
                        }
                    },
                    customBodyRender: (value, tableMeta, updateValue) => {
                        return value;

                    }
                },
            },
            {
                name: "id.accnName",
                label: t("document.list.table.headers.shipperName"),
                options: {
                    display: true,
                    filter: true,
                    sort: true,
                },
            },
            {
                name: "id.apexShipName",
                label: t("document.list.table.headers.shipName"),
                options: {
                    display: true,
                    filter: true,
                    sort: true,
                },
            },
            {
                name: "id.apexVoyageType",
                label: t("document.list.table.headers.voyageType"),
                options: {
                    display: true,
                    filter: true,
                    sort: true,
                },
            },
            {
                name: "id.apexVoyageNo",
                label: t("document.list.table.headers.voyageNo"),
                options: {
                    display: true,
                    filter: true,
                    sort: true,
                },
            },
            {
                name: "id.attDtCreate",
                label: t("document.list.table.headers.attDtCreate"),
                options: {
                    filter: true,
                    filterType: 'custom',
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
                        return formatDate(value, true);
                    }
                },
            },
            {
                name: "id.docType",
                label: t("document.list.table.headers.docType"),
                options: {
                    filter: true,
                    sort: true,
                },
            },
            {
                name: "id.refNo",
                label: t("document.list.table.headers.refNo"),
                options: {
                    filter: true,
                    sort: true,
                },
            },
            {
                name: "id.attName",
                label: t("document.list.table.headers.attName"),
                options: {
                    filter: true,
                    sort: true,
                },
            },
            {
                name: "id.attId",
                label: " ",
                options: {
                    filter: false,
                    display: true,
                    viewColumns: false,
                    customBodyRender: (value, tableMeta, updateValue) => {
                        const attId = tableMeta.rowData[0]
                        return <C1DataTableActions>
                            <Tooltip title={t("document.list.table.button.download")}>
                                <IconButton aria-label="Preview" type="button"
                                    color="primary" onClick={() => sendRequest(`${URL}/${attId}`, "download")}>
                                    <PageviewIcon />
                                </IconButton>
                            </Tooltip>
                        </C1DataTableActions >
                    }
                },
            },
        ];
    }

    return (
        <C1ListPanel
            routeSegments={[
                { name: t("document.list.routeSegment") },
            ]}
        >
            <Card elevation={3}>
                <Typography variant="h6" className="ml-6 mt-4 font-medium">{t("document.list.table.title")}</Typography>
                <Divider className="mt-4" />
                <Box m={1}>
                    <Grid container spacing={3} alignItems="center">
                        <Grid item xs={2}>
                            <C1InputField
                                label={t("document.options.appnId")}
                                name="searchAppnId"
                                onChange={handleInputChange}
                            />
                        </Grid>
                        <Grid item xs={2}>
                            <C1SelectField
                                name="searchAppType"
                                label={t("document.options.appType")}
                                value={searchOptions.searchAppType}
                                onChange={handleInputChange}
                                disabled={false}
                                isServer={true}
                                optionsMenuItemArr={Object.values(ApplicationType).map((app) => {
                                    // Do not include ADSUB and DDSUB in the dropdown
                                    if (app.code !== ApplicationType.ADSUB.code
                                        && app.code !== ApplicationType.DDSUB.code) {
                                        return <MenuItem value={app.code} key={app.code}>{app.desc}</MenuItem>
                                    }
                                })}
                            />
                        </Grid>
                        <Grid item xs={3}>
                            <C1SelectField
                                name="searchDocType"
                                label={t("document.options.docType")}
                                value={searchOptions.searchDocType}
                                onChange={handleInputChange}
                                disabled={false}
                                isServer={true}
                                options={{
                                    url: ATTACH_TYPE,
                                    id: 'mattId',
                                    desc: 'mattName',
                                    isCache: true
                                }}
                            />
                        </Grid>
                        <Grid item xs={3} className={isHidden ? "hidden" : ""}>
                            <C1SelectField
                                name="searchShipperName"
                                label={t("document.options.shipperName")}
                                value={searchOptions.searchShipperName}
                                onChange={handleInputChange}
                                disabled={false}
                                isServer={true}
                                options={{
                                    url: CCM_ACCOUNT_ALL_SL_SA,
                                    id: 'accnId',
                                    desc: 'accnName',
                                    isCache: false
                                }}
                            />
                        </Grid>
                        <Grid item xs={2}>
                            <C1InputField
                                label={t("document.list.table.headers.shipName")}
                                name="searchVesselName"
                                onChange={handleInputChange}
                            />
                        </Grid>
                        <Grid item xs={2}>
                            <C1SelectField
                                label={t("document.list.table.headers.voyageType")}
                                name="searchVoyageType"
                                onChange={handleInputChange}
                                isServer={true}
                                options={{
                                    url: PEDI_MST_VOYAGE_TYPE_URL,
                                    id: 'voyageTypeId',
                                    desc: 'voyageTypeName',
                                    isCache: false
                                }} />
                        </Grid>
                        <Grid item xs={2}>
                            <C1InputField
                                label={t("document.list.table.headers.voyageNo")}
                                name="searchVoyageNo"
                                onChange={handleInputChange}
                            />
                        </Grid>
                        <Grid item xs={2}>
                            <C1DateField
                                label={t("document.options.fromDate")}
                                type="date"
                                name="fromDate"
                                disabled={false}
                                value={searchOptions.fromDate}
                                maxDate={new Date()}
                                maxDateMessage="End Date should be greater than Start Date."
                                onChange={handleDateChange}
                            />
                        </Grid>
                        <Grid item xs={2}>
                            <C1DateField
                                label={t("document.options.toDate")}
                                type="date"
                                name="toDate"
                                disabled={false}
                                value={searchOptions.toDate}
                                minDate={null === searchOptions.fromDate ? formatDate(1) : searchOptions.fromDate}
                                maxDate={new Date()}
                                minDateMessage="End Date should be greater than Start Date."
                                onChange={handleDateChange}
                            />
                        </Grid>
                        <Grid item xs={2}>
                            <Tooltip title={t("document.list.table.button.search")} aria-label="search">
                                <Button onClick={handleSearch} color="primary" variant="contained">
                                    <SearchBoxIcon />
                                    {t("document.list.table.button.search")}
                                </Button>
                            </Tooltip>
                        </Grid>
                    </Grid>
                </Box>

                <C1DataTable url="/api/pedi/pediAttachView"
                    columns={columns({ roleId, filterStatus })}
                    title={t("document.list.table.subtitle")}
                    defaultOrder="id.attDtCreate"
                    defaultOrderDirection="desc"
                    isServer={true}
                    isRefresh={isRefresh}
                    isShowDownload={true}
                    filterBy={filterBy}
                    isShowFilter={false}
                />
            </Card>
        </C1ListPanel >
    );
}

export default DocumentList;