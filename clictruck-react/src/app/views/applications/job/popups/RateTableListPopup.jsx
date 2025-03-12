import C1PopUp from 'app/c1component/C1PopUp';
import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import PropTypes from 'prop-types';
import C1DataTable from 'app/c1component/C1DataTable';
import C1DataTableActions from 'app/c1component/C1DataTableActions';
import C1LabeledIconButton from 'app/c1component/C1LabeledIconButton';
import { CheckCircleOutline } from '@material-ui/icons';
import { Grid, MenuItem, Table, TableHead, TableBody, TableRow, TableCell, CircularProgress, makeStyles } from '@material-ui/core';
import { RecordStatus } from 'app/c1utils/const';
import C1SelectField from 'app/c1component/C1SelectField';
import useHttp from 'app/c1hooks/http';
import axios from "axios.js";


const useStyles = makeStyles(({ palette, ...theme }) => ({
    buttonProgress: {
        position: "absolute",
        top: "50%",
        left: "50%",
        marginTop: -12,
        marginLeft: -12,
    },
}));

const RateTableListPopup = (props) => {

    //tripRateType: S:Single, M:Multi-Drop, default is S and M
    const { open, handleClose, isDisabled, actionEl, handleSelected, company, truckTypeData, isDomestic, trTypeFilter } = props;
    const { isLoading, res, urlId, sendRequest, error } = useHttp();
    const [loading, setLoading] = useState(false);
    const classes = useStyles();

    const { t } = useTranslation(["job", "buttons"]);
    const [truckType, setTruckType] = useState(truckTypeData);
    const [vehTypeList, setVehTypeList] = useState([]);
    const [isRefresh, setIsRefresh] = useState(false);
    const [rateDB, setRateDB] = useState([]);

    const handleSelectData = (item) => {
        handleSelected(item)
    }

    useEffect(() => {
        
        if(open === true) {
            sendRequest(`/api/v1/clickargo/clictruck/vehicle/veh-type/${company?.to}`, 'getVehType')
        }

    // eslint-disable-next-line react-hooks/exhaustive-deps
    },[open]);

    useEffect(() => {
        if(isRefresh === true) {
            setIsRefresh(false)
        }
    },[isRefresh])

    useEffect(() => {
        if (!isLoading && res && !error) {
            switch (urlId) {
                case 'getVehType':
                    setVehTypeList(res?.data?.data)
                    break;
            
                default:
                    break;
            }
        }
    }, [isLoading, res, urlId, error]);

    const fetchRateData = async () => {
        
        setLoading(true);
        try {
            let rateUrl = `/api/v1/clickargo/clictruck/administrator/triprate/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=trId&mDataProp_1=TCkCtRateTable.tcoreAccnByRtCoFf.accnId&sSearch_1=${company?.coff}&mDataProp_2=TCkCtRateTable.tcoreAccnByRtCompany.accnId&sSearch_2=${company?.to}&mDataProp_3=TCkCtRateTable.rtStatus&sSearch_3=${RecordStatus.ACTIVE.code}&mDataProp_4=trStatus&sSearch_4=${RecordStatus.ACTIVE.code}&mDataProp_5=trTypeFilter&sSearch_5=${trTypeFilter}`;

            if(truckType) {
                rateUrl = rateUrl + `&mDataProp_6=TCkCtMstVehType.vhtyId&sSearch_6=${truckType}&iColumns=7`
            } else {
                rateUrl = rateUrl + `&iColumns=6`
            }

            let resp = await axios.get(rateUrl);

            if (resp?.data?.aaData) {
                setLoading(false);
                setRateDB(resp?.data?.aaData)
            }
        } catch (error) {
            setLoading(false);
        }
        setLoading(false);
    };

    useEffect(() => {
        fetchRateData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const columns = [
        {
            name: "trId",
            label: "Id",
            options: {
                display: false,
                filter: false
            }
        },
        {
            name: "tckCtLocationByTrLocFrom.locName",
            label: "From",
        },
        {
            name: "tckCtLocationByTrLocFrom.tckCtMstLocationType.lctyName",
            label: "From Type",
            options: {
                display: "excluded",
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: ["Address", "Region"],
                    renderValue: v => {
                        switch (v) {
                            case "Address": return "Address";
                            case "Region": return "Region";
                            default: break;
                        }
                    }
                },
            },
        },
        {
            name: "tckCtLocationByTrLocTo.locName",
            label: "To",
        },
        {
            name: "tckCtLocationByTrLocTo.tckCtMstLocationType.lctyName",
            label: "To Type",
            options: {
                display: "excluded",
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: ["Address", "Region"],
                    renderValue: v => {
                        switch (v) {
                            case "Address": return "Address";
                            case "Region": return "Region";
                            default: break;
                        }
                    }
                },
            },
        },
        {
            name: "trTypeDesc",
            label: "Trip Type",
            options: {
                sort: true,
                filter: true,
                filterType: "dropdown", /*
                filterOptions: {
                    names: ["S", "M"],
                    renderValue: (v) => {
                        switch (v) {
                            case "S": return "Single Trip";
                            case "M": return "Multi-Drop";
                            default: break
                        }
                    },
                },
                customFilterListOptions: {
                    render: (v) => {
                        switch (v) {
                            case "S": return "Single Trip";
                            case "M": return "Multi-Drop";
                            default: break
                        }
                    },
                }*/
            },
        },
        {
            name: "tckCtMstVehType.vhtyName",
            label: "Truck Type",
            options: {
                display: "excluded",
                sort: true,
                filter: true,
                filterType: "dropdown",
                // customBodyRender: (value) => getStatusDesc(value),
                filterOptions: {
                    names: ["CDD", "CDD LONG", "CDE", "CONTAINER 20FT", "CONTAINER 40FT", "VAN", "WING BOX"],
                    renderValue: (v) => {
                        switch (v) {
                            case "CDD": return "CDD";
                            case "CDD LONG": return "CDD Long";
                            case "CDE": return "CDE";
                            case "CONTAINER 20FT": return "Container 20FT";
                            case "CONTAINER 40FT": return "Container 40FT";
                            case "VAN": return "Van";
                            case "WING BOX": return "Wing Box";
                            default: break
                        }
                    },
                },
                customFilterListOptions: {
                    render: (v) => {
                        switch (v) {
                            case "CDD": return "CDD";
                            case "CDD LONG": return "CDD Long";
                            case "CDE": return "CDE";
                            case "CONTAINER 20FT": return "Container 20FT";
                            case "CONTAINER 40FT": return "Container 40FT";
                            case "VAN": return "Van";
                            case "WING BOX": return "Wing Box";
                            default: break
                        }
                    },
                },
            },
        },
        {
            name: "trCharge",
            label: "Price",
            options: {
                display: true,
                filter: false,
                sort: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return value ? value?.toLocaleString("in-ID", { maximumFractionDigits: 2, style: "currency", currency: "IDR" }) : '0.00';
                }
            }
        },
        {
            name: "",
            label: "Action",
            options: {
                filter: false,
                sort: false,
                viewColumns: false,
                display: true,
                setCellHeaderProps: () => { return { style: { textAlign: 'center' } } },
                customBodyRender: (value, tableMeta, updateValue) => {
                    const selected = tableMeta?.tableData.length > 0 ? tableMeta.tableData[tableMeta.rowIndex] : {}
                    return <C1DataTableActions>
                        <Grid container alignItems="flex-start" justifyContent="center">
                            <span style={{ minWidth: '48px' }}>
                                <C1LabeledIconButton
                                    tooltip={t("buttons:select")}
                                    label={t("buttons:select")}
                                    action={() => handleSelectData(selected)}>
                                    <CheckCircleOutline color="primary" />
                                </C1LabeledIconButton>
                            </span>
                        </Grid>
                    </C1DataTableActions>
                },
            },
        },
    ];

    const handleSelectVehType = (e) => {
        const { value } = e.target;
        setTruckType(value)
        setIsRefresh(true)
    }

    const isRowExpandable = (dataIndex, expandedRows) => {

        console.log("dataIndex, expandedRows", dataIndex, expandedRows);

        if(rateDB.length> dataIndex) {
            return "M" === rateDB[dataIndex]?.trType
        }
        return false;
    }

    const renderExpandableRow = (rowData, rowMeta) => {
        
        console.log("rowData, rowMeta", rowData, rowMeta);

        const colSpan = rowData.length + 1;
        let tckCtTripRates = rateDB[rowMeta.dataIndex].tckCtTripRates ;
        return (

            <TableRow>
            <TableCell colSpan={colSpan}>
                <Table style={{ minWidth: "650" }} aria-label="simple table">
                    <TableHead>
                        <TableRow>

                            <TableCell align="left"> No. &nbsp;</TableCell>
                            <TableCell align="left">From &nbsp; </TableCell>
                            <TableCell align="left">To &nbsp;</TableCell>

                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {tckCtTripRates.sort((a, b) => a.trSeq - b.trSeq).map((row, idx) => (
                            <TableRow key={row.trId}>

                                <TableCell align="left">{idx+1}</TableCell>
                                <TableCell align="left">{row.tckCtLocationByTrLocFrom?.locAddress}</TableCell>
                                <TableCell align="left">{row.tckCtLocationByTrLocTo?.locAddress}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableCell>
          </TableRow>

        );
    };

    const VehType = (
        <>
            <C1SelectField
                name="tckCtMstVehType.vhtyId"
                label={t("job:tripDetails.truckType")}
                value={truckType}
                onChange={handleSelectVehType}
                isServer={true}
                disabled={isDomestic}
                optionsMenuItemArr={vehTypeList.map((item, ind) => (
                    <MenuItem value={item.vhtyId} key={ind}>
                        {item.vhtyName}
                    </MenuItem>
                ))}
            />
        </>
    )

    return (
        <>
            <C1PopUp
                title={`Rate Table Selection`}
                openPopUp={open}
                setOpenPopUp={handleClose}
                actionsEl={!isDisabled && actionEl}
                maxWidth={`lg`}
            >
                <C1DataTable
                    url={"/api/v1/clickargo/clictruck/administrator/triprate"}
                    dbName={{list:rateDB}}
                    isServer={false}
                    columns={columns}
                    defaultOrder="trId"
                    defaultOrderDirection="asc"
                    isShowToolbar
                    isShowFilterChip
                    isRefresh={isRefresh}
                    isShowPrint={true}
                    isRowSelectable={false} /*
                    filterBy={[
                        { attribute: "TCkCtRateTable.tcoreAccnByRtCoFf.accnId", value: company?.coff },
                        { attribute: "TCkCtRateTable.tcoreAccnByRtCompany.accnId", value: company?.to },
                        { attribute: "TCkCtMstVehType.vhtyId", value: truckType },
                        { attribute: "TCkCtRateTable.rtStatus", value: RecordStatus.ACTIVE.code },
                        { attribute: "trStatus", value: RecordStatus.ACTIVE.code },
                        { attribute: "trTypeFilter", value: trTypeFilter }
                    ]}*/
                    title={VehType}
                    guideId="clicdo.doi.co.jobs.list.table"
                    setMaxHeight={500}
                    expandableRows={true}
                    expandableRowsHeader={false}
                    expandableRowsOnClick= {true}
                    isRowExpandable = { isRowExpandable}
                    renderExpandableRow = {renderExpandableRow}

                />
                {loading && (<CircularProgress size={24} className={classes.buttonProgress} />)}
            </C1PopUp>
        </>
    )
}

RateTableListPopup.propTypes = {
    open: PropTypes.bool,
    handleClose: PropTypes.func,
    isDisabled: PropTypes.bool,
    actionEl: PropTypes.func,
    handleSelected: PropTypes.func,
    company: PropTypes.object,
    isDomestic: PropTypes.bool,
    truckTypeData: PropTypes.string,
    trTypeFilter: PropTypes.string
}

export default RateTableListPopup;