import C1DataTable from 'app/c1component/C1DataTable'
import C1DataTableActions from 'app/c1component/C1DataTableActions';
import { Grid } from '@material-ui/core';
import VisibilityOutlined from '@material-ui/icons/VisibilityOutlined';
import C1LabeledIconButton from 'app/c1component/C1LabeledIconButton';
import CommentsDetailsPopUp from '../popups/CommentsDetailsPopUp'
import React from 'react'
import { customFilterDateDisplay, formatDate } from "app/c1utils/utility";
import { useState } from 'react';
import { useTranslation } from "react-i18next";

const RateTableComments = ({ inputData }) => {

    const { t } = useTranslation(["administration"]);

    const rtId = inputData?.rtId;
    const [popUp, setPopUp] = useState(false);
    const [popUpData, setPopUpData] = useState([]);

    const openDetailHandler = (data) => {
        console.log(data);
        setPopUp(true);
        setPopUpData(data);
    }

    const columns = [
        {
            name: "rtrType",
            label: t("administration:rateTableManagement.remark.type"),
            options: {
                customBodyRender: (value) => {
                    switch (value) {
                        case "R":
                            return "Rejection";
                        case "V":
                            return "Verification";
                        case "A":
                            return "Approval";
                        default:
                            return value;
                    }
                },
            }
        },
        {
            name: "rtrComment",
            label: t("administration:rateTableManagement.remark.comment"),
            options: {
                customBodyRender: (value, tableMeta, updateValue) => {
                    const maxLength = 10;
                    return (value?.length > maxLength) ? value?.slice(0, maxLength) + '...' : value;
                }
            }
        },
        {
            name: "rtrDtCreate",
            label: t("administration:rateTableManagement.remark.dt"),
            options: {
                customBodyRender: (value, tableMeta, updateValue) => { return formatDate(value, true); },
                filterType: 'custom',
                filterOptions: { display: customFilterDateDisplay },
            }
        },
        {
            name: "rtrUidCreate",
            label: t("administration:rateTableManagement.remark.by"),
        },
        {
            name: "rtrId",
            options: {
                display: false,
                filter: false,
            }
        },
        {
            name: "",
            label: "",
            options: {
                customBodyRender: (value, tableMeta, updateValue) => {
                    return <C1DataTableActions>
                        <Grid container alignItems="flex-start" justifyContent="center">
                            <span style={{ minWidth: '48px' }}>
                                <C1LabeledIconButton
                                    tooltip={"View"}
                                    label={"View"}
                                    action={() => { openDetailHandler(tableMeta?.rowData) }}>
                                    <VisibilityOutlined />
                                </C1LabeledIconButton>
                            </span>
                        </Grid>
                    </C1DataTableActions>
                },
            },
        }
    ]

    return (
        <>
            <C1DataTable
                // isServer={false}
                url="/api/v1/clickargo/clictruck/administrator/ratetableRemark/"
                columns={columns}
                defaultOrder="rtrDtCreate"
                defaultOrderDirection="desc"
                filterBy={[{ attribute: "TCkCtRateTable.rtId", value: rtId }]}
            />
            <CommentsDetailsPopUp
                popUp={popUp}
                setPopUp={setPopUp}
                data={popUpData}
            />
        </>
    )
}

export default RateTableComments;