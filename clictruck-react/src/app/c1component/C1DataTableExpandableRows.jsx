import React, { useCallback, useState } from 'react';
import MUIDataTable from "mui-datatables";

import axios from 'axios.js';
import { debounce } from "lodash";
import { createTheme, MuiThemeProvider } from '@material-ui/core/styles';
import { Button, Tooltip } from "@material-ui/core";
import AddBoxIcon from '@material-ui/icons/AddBox';
import { Link } from "react-router-dom";
import PropTypes from 'prop-types';
import C1PopUp from "./C1PopUp";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import Paper from "@material-ui/core/Paper";
import C1DataTableActions from 'app/c1component/C1DataTableActions';
import { useTranslation } from "react-i18next";

const C1DataTableExpandableRows = (props) => {
    const { showFilterChip,
        defaultOrder,
        defaultOrderDirection,
        url,
        columns,
        title,
        showToolbar,
        showAdd,
        dbName,
        children } = props;
    const getMuiTheme = () => createTheme({
        typography: {
            fontFamily: [
                "Poppins"
            ].join(",")
        },
        overrides: {

            MUIDataTableFilterList: {
                chip: {
                    display: showFilterChip === undefined ? 'display' : 'none'
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
            },
        }
    });
    const { popUpTitle } = showAdd;
    const [openPopUp, setOpenPopUp] = useState(false);
    const [c1DtState, setC1DtState] = useState({
        page: 0,
        count: 1,
        rowsPerPage: 10,
        previousPageNo: 0,
        sortOrder: { name: defaultOrder, direction: defaultOrderDirection === undefined ? 'asc' : defaultOrderDirection },
        data: [["Loading Data..."]],
        isLoading: false
    });

    const setData = (res, isLoading) => {
        setC1DtState({
            isLoading: isLoading,
            data: res.data,
            count: res.count,
            page: res.page,

        });
    }

    const debouncedFilterSearch = useCallback(debounce((tableState) => search(tableState), 1000, { maxWait: 2000 }), []);

    const getData = (tableState) => {

        setC1DtState({ isLoading: true });

        apiRequest(tableState).then(res => {
            setData(res, false);
        });
    }

    /*Called when action from onTableChange is sort */
    const sort = (tableState) => {
        setC1DtState({ isLoading: true });
        apiRequest(tableState).then(res => {
            setData(res, false);
        });
    }

    const changePage = (tableState) => {
        setC1DtState({ isLoading: false });
        apiRequest(tableState).then(res => {
            setData(res, false);
        });
    }

    const search = (tableState) => {
        setC1DtState({ isLoading: false });
        apiRequest(tableState).then(res => {
            setData(res, false);

        });

    }

    const reset = (tableState) => {
        setC1DtState({ isLoading: false });
        tableState.filterList = [];
        apiRequest(tableState).then(res => {
            setData(res, false);

        });

    }

    const apiRequest = (tableState) => {
        //The base url from set in props
        if (!url) {
            setC1DtState({ isLoading: true });
        }

        let baseRequestUrl = url;
        return new Promise((resolve, reject) => {
            let displayStart = getDisplayStart(tableState);
            let postRequesturl = '/list?sEcho=3&iDisplayStart=' + displayStart
                + '&iDisplayLength=' + tableState.rowsPerPage
                + getSortParam(columns, tableState)
                + getQueryFieldParams(columns, tableState);


            let requestUrl = postRequesturl;

            setC1DtState({ previousPageNo: tableState.rowsPerPage });

            resolve({
                data: dbName.list,
                page: displayStart > 0 ? tableState.page : 0,
                count: 50
            });




            // axios.get(requestUrl)
            //     .then(result => {
            //         resolve({
            //             data: result.data.aaData,
            //             page: displayStart > 0 ? tableState.page : 0,
            //             count: result.data.iTotalDisplayRecords,
            //         });
            //     })
            //     .catch((error) => {
            //         console.log(error);
            //     });
        });

    }

    const getDisplayStart = (tableState) => {
        if (c1DtState.previousPageNo === tableState.page) {
            return 0;
        }

        return tableState.page * tableState.rowsPerPage;
    }

    const getQueryFieldParams = (columns, tableState) => {
        let idx = 0;
        let [sortFieldName, sortDirection] = getSortFieldNameAndDirection(columns, tableState);

        let fieldParams = '';

        if (sortDirection) {
            fieldParams = '&mDataProp_' + idx + '=' + sortFieldName;
            idx++;
        }

        if (tableState.filterList) {
            for (let i = 0; i < tableState.filterList.length; i++) {
                let filter = tableState.filterList[i];

                if (filter.length > 0) {
                    fieldParams += '&mDataProp_' + idx + '=' + tableState.columns[i].name + '&sSearch_' + idx + '=' + filter;
                    idx++;
                }

            }

        }
        fieldParams = fieldParams + '&iColumns=' + idx;
        return fieldParams;
    }

    const getSortParam = (columns, tableState) => {

        let [sortFieldName, sortDirection] = getSortFieldNameAndDirection(columns, tableState);

        if (sortDirection) {
            return '&iSortCol_0=0&sSortDir_0=' + sortDirection + '&iSortingCols=1';
        }

        return '&iSortCol_0=0&sSortDir_0=asc&iSortingCols=0';
    }

    const getSortFieldNameAndDirection = (columns, tableState) => {

        if (tableState.sortOrder && tableState.sortOrder.name) {
            setC1DtState({ ...c1DtState, sortOrder: { name: tableState.sortOrder.name, direction: tableState.sortOrder.direction } });
            return [tableState.sortOrder.name, tableState.sortOrder.direction];
        }

        const retVal = getDefaultSortFieldNameAndDirection(columns);

        return retVal;
    }

    const getDefaultSortFieldNameAndDirection = (columns) => {

        let columnsTmp = columns;
        columnsTmp = columnsTmp.filter(column => {
            return !column.options.sort;
        });

        if (columnsTmp) {
            return [columnsTmp[0].field, columnsTmp[0].options.sort];
        }

        return null;
    }
    function createData(department, borderStatus, portStatus, action) {
        return { department, borderStatus, portStatus, action };
    }

    const rows = [
        createData("Quarantine", <small className="px-1 py-2px bg-light-primary text-red border-radius-4">
            Pending
        </small>, <small className="px-1 py-2px bg-light-green text-green border-radius-4">
            Approved
        </small>, <C1DataTableActions params={{
            edit: {
                show: true,
                path: "#"
            },
            view: {
                show: true,
                path: "#"
            },
            print: {
                show: true,
            },
            fileName: "FreePratiqueEg.pdf"
        }} />),
        createData("Immigration", <small className="px-1 py-2px bg-light-green text-green border-radius-4">
            Approved
        </small>, <small className="px-1 py-2px bg-light-green text-green border-radius-4">
            Approved
        </small>, <C1DataTableActions params={{
            edit: {
                show: true,
                path: "#"
            },
            view: {
                show: true,
                path: "#"
            }
        }} />),
        createData("Customs", <small className="px-1 py-2px bg-light-secondary text-error border-radius-4">
            Returned
        </small>, <small className="px-1 py-2px bg-light-secondary text-error border-radius-4">
            Returned
        </small>, <C1DataTableActions params={{
            edit: {
                show: true,
                path: "#"
            },
            view: {
                show: true,
                path: "#"
            }
        }} />),
        createData("Port", <small className="px-1 py-2px bg-light-secondary text-gray border-radius-4">
            NA
        </small>,
            <small className="px-1 py-2px bg-light-green text-green border-radius-4">
                Approved
            </small>, <C1DataTableActions params={{
                edit: {
                    show: true,
                    path: "#"
                },
                view: {
                    show: true,
                    path: "#"
                },
                print: {
                    show: true,
                    path: "#"
                },
                fileName: "PortClearanceEg.pdf"
            }} />),

    ];

    const isShowToolbar = showToolbar === undefined ? true : showToolbar
    const { t } = useTranslation(["buttons", "common"]);
    return (
        <MuiThemeProvider theme={getMuiTheme}>
            <MUIDataTable
                title={title}
                data={c1DtState.data || [["Loading Data..."]]}
                columns={columns}
                options={{
                    customToolbar: () => {
                        if (showAdd) {
                            if (showAdd.path === "popUp") {
                                return <Tooltip title={t("buttons:add")} aria-label="add">
                                    <Button><AddBoxIcon viewBox="0 0 24 24" color="primary"
                                        onClick={() => setOpenPopUp(true)}></AddBoxIcon></Button>
                                </Tooltip>
                            } else {
                                return <Link to={showAdd.path}>
                                    <Tooltip title={t("buttons:add")} aria-label="add">
                                        <Button><AddBoxIcon viewBox="0 0 24 24" color="primary"></AddBoxIcon></Button>
                                    </Tooltip></Link>;
                            }

                        }
                    },
                    fixedHeader: false,
                    count: c1DtState.count,
                    page: c1DtState.page,
                    rowsPerPage: c1DtState.rowsPerPage,
                    sortOrder: c1DtState.sortOrder,
                    download: isShowToolbar,
                    filter: isShowToolbar,
                    print: isShowToolbar,
                    viewColumns: isShowToolbar,
                    selectableRows: isShowToolbar,
                    filterType: "textField",
                    responsive: "standard",
                    enableNestedDataAccess: ".",
                    // selectableRows: "none", // set checkbox for each row
                    // search: false, // set search option
                    // filter: false, // set data filter option
                    // download: false, // set download option
                    // print: false, // set print option
                    // pagination: true, //set pagination option
                    // viewColumns: false, // set column option
                    expandableRows: true,
                    renderExpandableRow: (rowData, rowMeta) => {
                        console.log(rowData);
                        return (
                            <React.Fragment>

                                <tr>
                                    <td colSpan={2}></td>
                                    <td colSpan={8}>
                                        <TableContainer component={Paper}>
                                            <Table style={{ minWidth: "650" }} aria-label="simple table">
                                                <TableHead>
                                                    <TableRow>

                                                        <TableCell align="left">{t("common:common.dataTable.department")}&nbsp;</TableCell>
                                                        <TableCell align="left">{t("common:common.dataTable.borderStatus")}&nbsp;</TableCell>
                                                        <TableCell align="left">{t("common:common.dataTable.portStatus")}&nbsp;</TableCell>
                                                        <TableCell align="right">{t("common:common.dataTable.action")}&nbsp;</TableCell>

                                                    </TableRow>
                                                </TableHead>
                                                <TableBody>
                                                    {rows.map(row => (
                                                        <TableRow key={row.appId}>

                                                            <TableCell align="left">{row.department}</TableCell>
                                                            <TableCell align="left">{row.borderStatus}</TableCell>
                                                            <TableCell align="left">{row.portStatus}</TableCell>
                                                            <TableCell align="right">{row.action}</TableCell>
                                                        </TableRow>
                                                    ))}
                                                </TableBody>
                                            </Table>
                                        </TableContainer>
                                    </td>
                                    <td colSpan={2}></td>
                                </tr>

                            </React.Fragment>
                        );
                    },

                    elevation: 0,
                    rowsPerPageOptions: [10, 20, 40, 80, 100],
                    //disables the search icon in the toolbar
                    search: false,
                    serverSide: false,
                    // customSort: (data, colIndex, order, meta) => {

                    //     return data.sort((a, b) => {
                    //         return (a.data[colIndex].length < b.data[colIndex].length ? -1 : 1) * (order === 'desc' ? 1 : -1);
                    //     });
                    // },
                    onTableChange: (action, tableState) => {

                        // a developer could react to change on an action basis or
                        // examine the state as a whole and do whatever they want
                        console.log('Table Action: ', tableState, action);
                        switch (action) {
                            case 'changePage':
                                changePage(tableState);
                                break;
                            case 'sort':
                                sort(tableState);
                                break;
                            case 'changeRowsPerPage':
                                getData(tableState);
                                break;
                            case 'filterChange':
                                debouncedFilterSearch(tableState);
                                break;
                            case 'resetFilters':
                                reset(tableState);
                                break;
                            default:
                                console.log('action not handled.');
                                break;
                        }

                    },
                    onTableInit: (action, tableState) => {
                        getData(tableState);

                    }
                }}
            />
            <C1PopUp
                title={popUpTitle}
                openPopUp={openPopUp}
                setOpenPopUp={setOpenPopUp}
            >
                {children}
            </C1PopUp>
        </MuiThemeProvider>


    );

};

C1DataTableExpandableRows.propTypes = {
    showFilterChip: PropTypes.string,
    defaultOrder: PropTypes.string,
    defaultOrderDirection: PropTypes.string,
    url: PropTypes.string.isRequired,
    columns: PropTypes.array.isRequired,
    title: PropTypes.string,
    showToolbar: PropTypes.bool,
    showAdd: PropTypes.object
}

export default C1DataTableExpandableRows;