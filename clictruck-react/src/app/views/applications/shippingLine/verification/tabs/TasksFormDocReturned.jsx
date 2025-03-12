import { Button, Grid, IconButton, Tooltip } from "@material-ui/core";
import { green, orange, red } from "@material-ui/core/colors";
import DescriptionOutlinedIcon from '@material-ui/icons/DescriptionOutlined';
import SearchOutlinedIcon from '@material-ui/icons/SearchOutlined';
import WorkOutlineOutlinedIcon from '@material-ui/icons/WorkOutlineOutlined';
import React from "react";
import { useTranslation } from "react-i18next";

import C1Button from "app/c1component/C1Button";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DataTable from "app/c1component/C1DataTable";
import C1DataTableActions from "app/c1component/C1DataTableActions";
import C1DateField from "app/c1component/C1DateField";
import C1InputField from "app/c1component/C1InputField";
import C1SelectField from "app/c1component/C1SelectField";
import C1TabContainer from "app/c1component/C1TabContainer";
import { formatDate } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import { MatxLoading } from "matx";

const TasksFormDocReturned = ({
    filter,
    handleInputChange,
    handleSearchChange,
    handleDocSearchByBl,
    blNo,
    handleDateChange,
    readOnlyData,
    records,
    jobListToDate,
    handleViewFile,
    loading,
    isDocsVerified,
    setHasPendingReturn }) => {

    const { t } = useTranslation(["buttons", "listing", "verification"]);

    const columns = [
        {
            name: "attId",
            label: "",
            options: {
                display: false,
            }
        },
        {
            name: "attReturnFlag",
            label: "",
            options: {
                display: false,
            }
        },
        {
            name: "attDtReturned",
            label: "",
            options: {
                display: false,
            }
        },
        // {
        //     name: "",
        //     label: t("listing:docVerification.requireReturn"),
        //     options: {
        //         sort: false,
        //         filter: false,
        //         customBodyRender: (emptyStr, tableMeta, updateValue) => {
        //             const attId = tableMeta.rowData[0]
        //             const attReturnFlag = tableMeta.rowData[1]
        //             return <Checkbox disabled={attReturnFlag !== null} checked={selectedRowIds.includes(attId) || attReturnFlag === 'Y'} onChange={({ target: { checked } }) =>
        //                 checked ? setSelectedRowIds(selectedRowIds.concat(attId)) :
        //                     setSelectedRowIds(selectedRowIds.filter(rowId => rowId !== attId))
        //             } />
        //         }
        //     },
        // },
        {
            name: "tmstAttType.mattName",
            label: t("listing:docVerification.docType"),
        },
        {
            name: "refNo",
            label: t("listing:docVerification.refNo"),
        },
        {
            name: "doNo",
            label: t("listing:docVerification.doNo"),
        },
        {
            name: "attDtCreate",
            label: t("listing:docVerification.dtCreate"),
            options: {
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                }
            },
        },
        {
            name: "",
            label: t("listing:docVerification.status"),
            options: {
                filter: false,
                display: true,
                viewColumns: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    const attReturnFlag = tableMeta.rowData[1]
                    const attDtReturned = tableMeta.rowData[2]
                    if (attReturnFlag === 'N' || (attReturnFlag === 'Y' && attDtReturned !== null)) {
                        return <small
                            className="px-1 py-2px border-radius-4"
                            style={{ backgroundColor: green[200], color: green[800] }}>
                            Verified
                        </small>;
                    }
                    if (attReturnFlag === 'Y' && attDtReturned === null) {
                        setHasPendingReturn(true)
                        return <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: orange[200], color: orange[800] }}>
                            Pending Return
                        </small>;
                    } if (isDocsVerified === undefined || isDocsVerified === null) {
                        return <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: red[200], color: red[800] }}>
                            Not Verified
                        </small>;
                    }
                }
            }
        },
        {
            name: "action",
            label: t("listing:docVerification.action"),
            options: {
                filter: false,
                display: true,
                viewColumns: false,
                setCellHeaderProps: () => { return { style: { paddingLeft: '5%' } } },
                customBodyRender: (value, tableMeta, updateValue) => {
                    const attId = tableMeta.rowData[0];
                    return <C1DataTableActions
                        downloadFileEventHandler={(e) => handleViewFile(e, attId)} />
                }
            }
        },
    ];


    return (<React.Fragment>
        {loading && <MatxLoading />}
        <Grid item xs={12}>
            <C1TabContainer>
                <Grid item lg={4} md={6} xs={12}>
                    <C1CategoryBlock icon={<SearchOutlinedIcon />} title={t("verification:header.blsearch")}>
                        <Grid container alignItems="center" spacing={3}>
                            <Grid item xs={9}>
                                <C1InputField label={t("verification:details.blno")}
                                    // Cannot type more than 200 characters, but db allows 255
                                    inputProps={{
                                        maxLength: 200
                                    }}
                                    value={blNo}
                                    name="blNo"
                                    disabled={false}
                                    onChange={handleSearchChange} />

                            </Grid>
                            <Grid item xs={3}>
                                <Tooltip title={t("verification:tooltip.search")}>
                                    {/* <IconButton aria-label="Search" type="button" disabled={false}
                                        color="primary" onClick={(e) => console.log("Search BL")}>
                                        <SearchOutlinedIcon />
                                    </IconButton> */}
                                    <span><Button
                                        disabled={!blNo}
                                        color="primary"
                                        variant="contained"
                                        size="small"
                                        onClick={handleDocSearchByBl}>
                                        <SearchOutlinedIcon viewBox="1 -1 30 30"></SearchOutlinedIcon>
                                        {"Search"}
                                    </Button></span>
                                </Tooltip>
                            </Grid>
                        </Grid>
                    </C1CategoryBlock>
                </Grid>
                <Grid item lg={1} md={1} xs={1}></Grid>

                <Grid item lg={12} md={12} xs={12}>
                    <C1CategoryBlock icon={<DescriptionOutlinedIcon />} title={t("verification:header.docsreturnedlist")}>
                        <C1DataTable
                            dbName={records}
                            isServer={false}
                            columns={columns}
                            title=""
                            isRefresh={records}
                            isShowFilter={false}
                            isShowViewColumns={false}
                            isShowPrint={false}
                            isShowDownload={false}
                            isShowToolbar={false}
                        />
                    </C1CategoryBlock>
                </Grid>
            </C1TabContainer>
        </Grid>
    </React.Fragment >);
};

export default withErrorHandler(TasksFormDocReturned);