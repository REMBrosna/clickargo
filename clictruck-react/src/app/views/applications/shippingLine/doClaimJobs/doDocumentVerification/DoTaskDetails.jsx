import React, { useState } from "react";
import {
    Button,
    Grid,
    IconButton,
    Paper,
    Tooltip
} from "@material-ui/core";
import C1TabContainer from "app/c1component/C1TabContainer";
import { useStyles } from "app/c1utils/styles";
import { createTheme, MuiThemeProvider } from "@material-ui/core/styles";
import MUIDataTable from "mui-datatables";
import ZoomInIcon from "@material-ui/icons/ZoomIn";
import ContainerDetailsPopup from "../../../fforwarder/doClaim/popups/ContainerDetailsPopup";
import C1PopUp from "../../../../../c1component/C1PopUp";
import {useTranslation} from "react-i18next";

const DoTaskDetails = ({
    data,
    setData,
    handleInputChange,
    handleDateChange,
    handleInputFileChange,
    handleViewFile,
    viewType,
    isDisabled
}) => {
    const { t } = useTranslation(["ffclaims"]);

    const getMuiTheme = () => createTheme({
        typography: {
            fontFamily: [
                "Poppins"
            ].join(",")
        },
        overrides: {
            MUIDataTableFilterList: {
                chip: {
                    display: 'none'
                }
            },
            MuiToolbar: {
                root: {
                    display: 'none',
                    alignItems: 'center',

                },
            },
            MuiTablePagination: {
                toolbar: {
                    //to show pagination if isShowPagination or title is set
                    display: 'none'
                }
            },
            MuiTableCell: {
                head: {
                    backgroundColor: 'rgb(60, 119, 208)',
                    color: 'white'
                },
            },
            MUIDataTableSelectCell: {
                headerCell: {
                    backgroundColor: 'rgb(60, 119, 208)',
                },
            },
            MUIDataTableHeadCell: {
                sortActive: {
                    paddingLeft: 16,
                    color: 'white'
                },
                data: {
                    backgroundColor: 'rgb(60, 119, 208)',
                    color: 'white',
                    display: "inline-block",
                    fontWeight: "bold"
                }
            },
            MUIDataTableToolbar: {
                root: {
                    display: 'none',
                    minHeight: "0px"
                },
                actions: {
                    display: 'inline-table'
                },
                left: {
                    display: 'flex',
                    textAlign: 'center'
                }
            }
            // MUIDataTable: {
            //     responsiveScroll: {
            //         overflow: 'scroll'
            //     }
            // }
        }
    });
    const classes = useStyles();
    const doiListingTableCols = [
        {
            name: "tckDo", // field name in the row object
            label: t("ffclaims:taskDetails.doNo"), // column title that will be shown in table
            options: {
                sort: false,
                filter: false,
                customBodyRender: (tckDo, tableMeta, updateValue) => tckDo.doNo
            },
        },
        {
            name: "doiBlNo", // field name in the row object
            label: t("ffclaims:taskDetails.bLNo"), // column title that will be shown in table
            options: {
                sort: false,
                filter: false
            },
        },
        {
            name: "authoriser", // field name in the row object
            label: t("ffclaims:taskDetails.authoriser"), // column title that will be shown in table
            options: {
                sort: false,
                filter: false
            },
        },
        {
            name: "cntList", // field name in the row object
            label: t("ffclaims:taskDetails.noOfContainers"), // column title that will be shown in table
            options: {
                sort: false,
                filter: false,
                customBodyRender: (cntList, tableMeta, updateValue) => {
                    // let state = value?.tckTask.tckMstTaskState?.tskstDesc
                    // console.log('state ' + state)
                    return (<Grid container alignItems="center">
                        <Grid item xs={6} justifyContent="flex-start">
                            {cntList?.length}
                        </Grid>
                        <Grid item xs={6} justifyContent="flex-start">
                            <div className="flex items-center">
                                <div className="flex-grow" />
                                <Tooltip title={t("ffclaims:taskDetails.viewContainers")}>
                                    <IconButton onClick={(e) => openViewContainerPopup(cntList, e)}>
                                        <ZoomInIcon color="primary" />
                                    </IconButton>
                                </Tooltip>
                            </div>
                        </Grid>
                    </Grid>)
                }
            },
        }
    ]

    const [cntList, setCntList] = useState(null)

    const openViewContainerPopup = (cntList, e) => {
        setCntList(cntList)
        setOpenContainerDetailsPopUp(true)
        e.stopPropagation()
    }

    const cols = [
        // 0
        {
            name: "tckDo", // field name in the row object
            label: t("ffclaims:taskDetails.readyDos"), // column title that will be shown in table
            options: {
                sort: false,
                filter: false,
                customBodyRender: (tckDo, tableMeta, updateValue) => tckDo.doNo
            }
        }
    ]

    const handleRowClick2 = e => {
        //    console.log(JSON.stringify(e))
        let doNo = e[0];
        //   console.log('data ' + JSON.stringify(data))
        let selectedDo = data.selectedDos.list.find(selectedDo => selectedDo.doiNo === doNo);
        if (!selectedDo) {
            return
        }
        //  console.log('selectedDo ' + JSON.stringify(selectedDo))
        let updatedSelectedDos = data.selectedDos.list.filter(selectedDo => selectedDo.doiNo !== doNo)
        data.readyDos.list.splice(0, 0, selectedDo)
        //  data.readyDos.list = updatedReadyDos
        data.selectedDos.list = updatedSelectedDos
        //     console.log('updatedData ' + JSON.stringify(data))
        setData(Object.assign({}, data))
    }

    const [selectedReadyDO, setSelectedReadyDO] = useState(null);

    const handleRowClick = e => {
        // console.log(JSON.stringify(e))
        let doNo = e[0];
        setSelectedReadyDO(doNo)
        //     console.log('data ' + JSON.stringify(data))
        //      let selectedDo = data.readyDos.list.find(readyDo => readyDo.doiNo === doNo);
        //      if (!selectedDo) {
        //          return
        //      }
        //      let updatedReadyDos = data.readyDos.list.filter(readyDo => readyDo.doiNo !== doNo)
        //      data.selectedDos.list.splice(0,0,selectedDo)
        //      data.readyDos.list = updatedReadyDos
        //      setData(Object.assign({}, data))
    }
    const handleAssignDo = () => {
        let selectedDo = data.readyDos.list.find(readyDo => readyDo.tckDo.doNo === selectedReadyDO);
        if (!selectedDo) {
            return
        }
        let updatedReadyDos = data.readyDos.list.filter(readyDo => readyDo.tckDo.doNo !== selectedReadyDO)
        data.selectedDos.list.splice(0, 0, selectedDo)
        data.readyDos.list = updatedReadyDos
        setData(Object.assign({}, data))
        setSelectedReadyDO(null)
    }
    const setRowProps = row => {
        if (row[0] === selectedReadyDO) {
            return { style: { background: 'grey' } }
        }
        return { style: { background: 'white' } }
    }

    const [openContainerDetailsPopUp, setOpenContainerDetailsPopUp] = useState(false)
    return (
        <React.Fragment>
            <Grid container className={classes.gridContainer} justifyContent="center">
                <Grid item xs={12}>
                    <C1TabContainer>
                        <Grid item lg={3} md={3} xs={3} >
                            {/*<div style={{maxHeight: "400px", minHeight:'350px', overflowY: "auto"}}>*/}
                            <Grid className={classes.gridContainer} item xs={12} style={{ maxHeight: "400px", overflowY: 'auto' }}>
                                <Paper id={'paper'} className={classes.dataTablePaper}>
                                    <MuiThemeProvider theme={getMuiTheme}>
                                        <MUIDataTable
                                            data={data.readyDos.list}
                                            columns={cols}
                                            options={{
                                                onRowClick: e => handleRowClick(e),
                                                selectableRows: 'none',
                                                setRowProps: setRowProps,
                                                textLabels: {
                                                    body: {
                                                        noMatch: t("ffclaims:taskDetails.noReadyDo")
                                                    }
                                                },
                                                fixedHeader: false
                                            }}
                                        >
                                        </MUIDataTable>
                                    </MuiThemeProvider>
                                </Paper>
                            </Grid>
                            <Grid className={classes.gridContainer} item xs={12}>
                                <Button
                                    className="m-2 rounded hover-bg-primary px-6"
                                    variant="outlined"
                                    color="primary"
                                    disabled={false}
                                    onClick={handleAssignDo}>
                                    {t("ffclaims:taskDetails.assignToTask")}
                                </Button>
                            </Grid>
                            {/*<Grid className={classes.gridContainer} item xs={12}>*/}
                            {/*    {'Click on the row to assign to task'}*/}
                            {/*</Grid>*/}
                            {/*</div>*/}
                        </Grid>
                        <Grid item lg={9} md={9} xs={9} style={{ maxHeight: "400px", minHeight: "400px", overflowY: "auto" }}>
                            {/*<div style={{maxHeight: "400px", minHeight:'350px', overflowY: "auto"}}>*/}
                            <Paper id={'unitt'} className={classes.dataTablePaper}>
                                <MuiThemeProvider theme={getMuiTheme}>
                                    <MUIDataTable
                                        data={data.selectedDos.list}
                                        columns={doiListingTableCols}
                                        options={{
                                            //   onRowClick: e => handleRowClick2(e),
                                            selectableRows: 'none',
                                            textLabels: {
                                                body: {
                                                    noMatch: t("ffclaims:taskDetails.addReadyDohere")
                                                }
                                            },
                                            fixedHeader: false
                                        }}
                                    >
                                    </MUIDataTable>
                                </MuiThemeProvider>
                            </Paper>
                            {/*<Grid className={classes.gridContainer} item xs={12}>*/}
                            {/*    {'Click on the row to unassign from task'}*/}
                            {/*</Grid>*/}
                            {/*<C1DataTable url="/api/v1/clickargo/clicdo/task"*/}
                            {/*      // handleRowClick={(e) => handleRowClick(e)}*/}
                            {/*      // onRowClickEvent={true}*/}
                            {/*               columns={col2s}*/}
                            {/*               defaultOrder="doId"*/}
                            {/*               dbName={data.selectedDos}*/}
                            {/*      // showAdd={!isDisabled}*/}
                            {/*      //          showAdd={{*/}
                            {/*      //              type: "popUp",*/}
                            {/*      //              popUpHandler: popUpHandler,*/}
                            {/*      //          }}*/}
                            {/*               showTemplate={false}*/}
                            {/*               showCustomDownload={false}*/}
                            {/*               isServer={false}*/}
                            {/*               isShowViewColumns={false}*/}
                            {/*               isShowDownload={false}*/}
                            {/*               isShowPrint={false}*/}
                            {/*               isShowFilter={false}*/}
                            {/*               isShowToolbar = {false}*/}
                            {/*               isShowDownloadData = {false}*/}
                            {/*               isShowFilterChip = {false}*/}
                            {/*               isShowViewColumns = {false}*/}
                            {/*               isShowPrint = {false}*/}
                            {/*               isShowDownload = {false}*/}
                            {/*               isRowSelectable = {false}*/}
                            {/*               isShowPagination = {false}*/}
                            {/*  />*/}
                            {/*</div>*/}
                        </Grid>
                        {/*<Grid className={classes.gridContainer}  item lg={3} md={3} xs={3}>*/}
                        {/*    {'Click on the row to assign to task'}*/}
                        {/*</Grid>*/}
                        {/*<Grid className={classes.gridContainer}  item lg={9} md={9} xs={9}>*/}
                        {/*    {'Click on the row to unassign from task'}*/}
                        {/*</Grid>*/}
                    </C1TabContainer>
                    {/* </C1OutlinedDiv> */}
                </Grid>
                {/*<Grid className={classes.gridContainer} item xs={12}>*/}
                {/*    {"*Click on the EYE icon to view the BL document. " +*/}
                {/*        "BL documents cannot be removed because the job is confirmed."}*/}
                {/*</Grid>*/}
                {/*<Grid item xs={12}>*/}
                {/*    <C1Information information="shiplineDoClaimJobs" />*/}
                {/*</Grid>*/}
            </Grid>
            <C1PopUp
                title={t("ffclaims:taskDetails.viewContainers")}
                openPopUp={openContainerDetailsPopUp}
                setOpenPopUp={setOpenContainerDetailsPopUp}>
                <ContainerDetailsPopup
                    // view={view}
                    //   viewType={"view"}
                    data={cntList}
                //handleInputChange={handleInputChange}
                // handleBtnAddClick={(e) => handleBtnAddFile(e, "ManuallyAddBL")}
                />
            </C1PopUp>

        </React.Fragment>
    );
};

export default DoTaskDetails;