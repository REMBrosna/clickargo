import DataTable from 'app/atomics/organisms/DataTable';
import C1TabContainer from 'app/c1component/C1TabContainer';
import { Grid } from '@material-ui/core';
import React, { Component } from 'react'
import RemarkPopUp from '../popUp/RemarkPopUp';

const RemarksTab = () => {

    const tCol =[
        {
            name: "remarkType",
            label: "Remarks Type",
        },
        {
            name: "remark",
            label: "Remarks",
        },
        {
            name: "remarkDt",
            label: "Date Time",
        },
        {
            name: "by",
            label: "By",
        },
        {
            name: "action",
            label: "action",
        },
    ]

    return(
        <C1TabContainer>
            <DataTable 
                columns={tCol}
                defaultOrder='remarkDt'
                url="/api/v1/clickargo/clictruck/administrator/ratetableRemark/"
                isServer={false}
            />
            <RemarkPopUp />
        </C1TabContainer>
        
    )
}

export default RemarksTab;