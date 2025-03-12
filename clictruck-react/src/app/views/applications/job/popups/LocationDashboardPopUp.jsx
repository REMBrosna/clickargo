import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Backdrop, Button, ButtonGroup, Checkbox, CircularProgress, Grid, TableCell, Tooltip, Typography } from "@material-ui/core";
import C1DataTable from 'app/c1component/C1DataTable';
import C1PopUp from "app/c1component/C1PopUp";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";

/**
 * 
 * this popup for popup multidrop location,
 * preparing this component if all the location remove from dashboard 
 */
const LocationDashboardPopUp = ({
    openPopUp,
    setOpenPopUp,
    tripListData,
    title,
    columns,
})=>{
    return (<C1PopUp 
        title={`${title}`}
        openPopUp={openPopUp}
        setOpenPopUp={setOpenPopUp}
    >
        <C1DataTable
            title={""}
            isServer={false}
            dbName={{list: tripListData? tripListData: []}}
            columns={columns}
            defaultOrder="trDtCreate"
            defaultOrderDirection="desc"
            isShowFilter={false}
            isShowFilterChip={false}
            isShowViewColumns={false}
            isShowPrint={false}
            isShowDownload={false}
            isRowSelectable={false}
            isShowPagination={false}
            isShowToolbar={false}
        />
    </C1PopUp>
    )
}

export default withErrorHandler(LocationDashboardPopUp)