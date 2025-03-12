import { Grid } from '@material-ui/core';
import VisibilityOutlined from '@material-ui/icons/VisibilityOutlined';
import React from 'react'
import { useState } from 'react';

import C1DataTable from 'app/c1component/C1DataTable'
import C1DataTableActions from 'app/c1component/C1DataTableActions';
import C1LabeledIconButton from 'app/c1component/C1LabeledIconButton';
import { customFilterDateDisplay, formatDate } from "app/c1utils/utility";

import AccountRemarkPopup from './popups/AccountRemarkPopup';

const AccountRemarks = ({ inputData, locale }) => {

    const accnId = inputData?.accnDetails?.accnId;

    const [popUp, setPopUp] = useState(false);
    const [popUpData, setPopUpData] = useState([]);

    const openDetailHandler = (data) => {
        console.log(data);
        setPopUp(true);
        setPopUpData(data);
    }

    const columns = [
        {
            name: "arId",
            label: "",
            options: {
                display: false,
                filter: false,
            }
        },
        {
            name: "tckMstRemarkType.rtDesc",
            label: locale("listing:remarks.rmkType"),
            options: {
                display: true,
                filter: true
            }
        },
        {
            name: "arRemark",
            label: locale("listing:remarks.rmk"),
            options: {
                customBodyRender: (value, tableMeta, updateValue) => {
                    const maxLength = 10;
                    return (value?.length > maxLength) ? value?.slice(0, maxLength) + '...' : value;
                }
            }
        },
        {
            name: "atDtCreate",
            label: locale("listing:remarks.rmkDtCreate"),
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
            name: "atUidCreate",
            label: locale("listing:remarks.rmkUidLupd"),
            options: {
                display: true,
                filter: true
            }
        },
        {
            name: "",
            label: "",
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    // const id = tableMeta?.rowData[0];
                    return <C1DataTableActions>
                        <Grid container alignItems="flex-start" justifyContent="center">
                            <span style={{ minWidth: '48px' }}>
                                <C1LabeledIconButton
                                    tooltip={locale("buttons:view")}
                                    label={locale("buttons:view")}
                                    action={() => { openDetailHandler(tableMeta?.rowData) }}>
                                    <VisibilityOutlined />
                                </C1LabeledIconButton>
                            </span>
                        </Grid>
                    </C1DataTableActions>
                },
            },
        },
    ]

    return (
        <>
            <C1DataTable
                isServer={accnId ? true : false}
                url="/api/v1/clickargo/ckWfRmk"
                columns={columns}
                defaultOrder="atDtCreate"
                defaultOrderDirection="desc"
                filterBy={[{ attribute: "TCoreAccn.accnId", value: accnId }]}
            />
            <AccountRemarkPopup
                popUp={popUp}
                setPopUp={setPopUp}
                data={popUpData}
                locale={locale}
            />
        </>
    )
}

export default AccountRemarks;