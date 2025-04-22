import React, { useCallback, useState, useEffect } from 'react';
import MUIDataTable from "mui-datatables";

import axios from 'axios.js';
import { debounce } from "lodash";
import { createMuiTheme, MuiThemeProvider } from '@material-ui/core/styles';
import { Button, Tooltip } from "@material-ui/core";
import AddBoxIcon from '@material-ui/icons/AddBox';
import { Link } from "react-router-dom";
import PropTypes from 'prop-types';
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import { useTranslation } from "react-i18next";

const C1DataTableServer = ({
    showFilterChip,
    defaultOrder,
    defaultOrderDirection,
    url,
    columns,
    title,
    showToolbar,
    showAdd,
    isEmpty,
    statusFieldName,
    isRowsSelectable,
}) => {

    const getMuiTheme = () => createMuiTheme({
        overrides: {
            MUIDataTableFilterList: {
                chip: {
                    display: showFilterChip === undefined ? 'display' : 'none'
                }
            },
            MuiToolbar: {
                root: {
                    display: title !== '' ? 'flex' : 'none',
                },
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
    const [clolumnsWithStatus, setClolumnsWithStatus] = useState(columns);

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


            let requestUrl = baseRequestUrl + postRequesturl;

            setC1DtState({ previousPageNo: tableState.rowsPerPage });

            axios.get(requestUrl)
                .then(result => {
                    resolve({
                        data: result.data.aaData,
                        page: displayStart > 0 ? tableState.page : 0,
                        count: result.data.iTotalDisplayRecords,
                    });
                })
                .catch((error) => {
                    console.log(error);
                });
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


    const isShowToolbar = showToolbar === undefined ? true : showToolbar

    useEffect(() => {
        if (statusFieldName) {

            const statusColumn =
            {
                name: statusFieldName,
                label: "Status",
                options: {
                    filter: true,
                    filterType: 'dropdown',
                    filterOptions: {
                        names: ["A", "I"],
                        renderValue: v => {
                            if (v === "A") {
                                return "Active";
                            } else if (v === "I") {
                                return "InActive";
                            }
                        }
                    },
                    customFilterListOptions: {
                        render: v => {
                            if (v === "A") {
                                return "Active";
                            } else if (v === "I") {
                                return "InActive";
                            }
                        }
                    },
                    customBodyRender: (value, tableMeta, updateValue) => {
                        if (value === 'A')
                            return <small className="px-1 py-2px bg-light-green text-green border-radius-4">
                                Active
                            </small>;
                        else if (value === 'I')
                            return <small className="px-1 py-2px bg-light-red text-red border-radius-4">
                                Inactive
                            </small>;
                    },
                },
            };
            columns.splice(-1, 0, statusColumn);
            setClolumnsWithStatus(columns);
        }

    }, [columns, statusFieldName]);

    const { t } = useTranslation(["buttons"]);

    return (
        <MuiThemeProvider theme={getMuiTheme}>
            <MUIDataTable
                title={title}
                data={c1DtState.data || [["Loading Data..."]]}
                columns={clolumnsWithStatus}
                options={{
                    customToolbar: () => {
                        if (showAdd) {
                            return <Link to={showAdd.path}>
                                <Tooltip title={t("buttons:add")} aria-label="add">
                                    <Button><AddBoxIcon viewBox="0 0 24 24" color="primary"></AddBoxIcon></Button>
                                </Tooltip></Link>;
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
                    selectableRows: isRowsSelectable ? isRowsSelectable : "multiple",   //"multiple", "single", "none". default is multiple
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
                    elevation: 0,
                    rowsPerPageOptions: [10, 20, 40, 80, 100],
                    //disables the search icon in the toolbar
                    search: false,
                    serverSide: isEmpty ? false : true,
                    // customSort: (data, colIndex, order, meta) => {

                    //     return data.sort((a, b) => {
                    //         return (a.data[colIndex].length < b.data[colIndex].length ? -1 : 1) * (order === 'desc' ? 1 : -1);
                    //     });
                    // },
                    onTableChange: (action, tableState) => {

                        // a developer could react to change on an action basis or
                        // examine the state as a whole and do whatever they want
                        console.log('Table Action: ', tableState);
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
        </MuiThemeProvider>

    );

};

C1DataTableServer.propTypes = {
    showFilterChip: PropTypes.string,
    defaultOrder: PropTypes.string,
    defaultOrderDirection: PropTypes.string,
    url: PropTypes.string.isRequired,
    columns: PropTypes.array.isRequired,
    title: PropTypes.string,
    showToolbar: PropTypes.bool,
    showAdd: PropTypes.object,
    isEmpty: PropTypes.bool
}

export default withErrorHandler(C1DataTableServer, axios);