import React, {useState, useEffect} from "react";
import { Grid } from "@material-ui/core";
import C1DataTable from 'app/c1component/C1DataTable';
import { clickDOExtensionJobs } from "fake-db/db/clickDOExtensionJobs";

const DoExtensionTasksList = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {

    const columns = ({ roleId, filterStatus }) => {
        return [
            {
                name: "jobId",
                label: "Job Id"

            },
            {
                name: "shipmentType",
                label: "Shipment Type"

            },
            {
                name: "authoriser",
                label: "Authoriser",
                // options: {
                //     filter: true,
                // },
            },
            {
                name: "authorisedParty",
                label: "Authorised Party",
                // options: {
                //     filter: true,
                    // customBodyRender: (value) => null != value ? value.toUpperCase() : "",
                // },
            },
            {
                name: "dateSubmitted",
                label: "Date Submitted",
                // options: {
                //     filter: true,
                //     filterType: 'custom',
                //     customFilterListOptions: {
                //         render: v => v.map(l => l),
                //         update: (filterList, filterPos, index) => {
                //             filterList[index].splice(filterPos, 1);
                //             return filterList;
                //         }
                //     },
                    // filterOptions: {
                    //     display: customFilterDateDisplay
                    // },
                    // customBodyRender: (value, tableMeta, updateValue) => {
                    //     return formatDate(value, false);
                    // }
                // },
            },
            {
                name: "status",
                label: "Status",
                // options: {
                //     filter: true,
                //     filterType: 'dropdown',
                //     filterList: filterStatus,
                    // filterOptions: {
                    //     names: [Status.SUB.code, Status.APP.code, Status.VER.code, Status.REJ.code, Status.RET.code, Status.ORT.code, Status.EXP.code],
                    //     renderValue: v => {
                    //         switch (v) {
                    //             case Status.SUB.code: return Status.SUB.desc;
                    //             case Status.REJ.code: return Status.REJ.desc;
                    //             case Status.RET.code: return Status.RET.desc;
                    //             case Status.ORT.code: return Status.ORT.desc;
                    //             case Status.VER.code: return Status.VER.desc;
                    //             case Status.APP.code: return Status.APP.desc;
                    //             case Status.EXP.code: return Status.EXP.desc;
                    //             default: break;
                    //         }
                    //     }
                    // },
                    // customFilterListOptions: {
                    //     render: v => {
                    //         switch (v) {
                    //             case Status.SUB.code: return Status.SUB.desc;
                    //             case Status.REJ.code: return Status.REJ.desc;
                    //             case Status.RET.code: return Status.RET.desc;
                    //             case Status.ORT.code: return Status.ORT.desc;
                    //             case Status.VER.code: return Status.VER.desc;
                    //             case Status.APP.code: return Status.APP.desc;
                    //             case Status.EXP.code: return Status.EXP.desc;
                    //             default: break;
                    //         }
                    //     }
                    // },
                    // customBodyRender: (value, tableMeta, updateValue) => {
                    //     let extra = null;
                    //     if (tableMeta.rowData[7] === '*') {
                    //         extra = <Tooltip title={t("common:common.msg.resubmitted")}>
                    //             <b style={{ color: orange[800] }}>{tableMeta.rowData[7]}</b></Tooltip>
                    //     }
                    //     return getStatusDesc(value, extra);
                    // }
                // },
            },
            {
                name: "action",
                label: " ",
                // options: {
                //     filter: false,
                //     display: true,
                //     viewColumns: false,
                    // customBodyRender: (value, tableMeta, updateValue) => {
                    //     return <C1DataTableActions
                    //         viewPath={getViewMode(tableMeta.rowData[11]) && "/vessel/shipRegistration/view/" + tableMeta.rowData[0]}
                    //         previewEventHandler={((e) => handlePreviewFormPdf(e, tableMeta.rowData[0]))}>
                    //         <C1FormButtonsSuppDocs appType="sr" appId={tableMeta.rowData[0]} />
                    //     </C1DataTableActions>
                    // },
                // },
            },
        ];
    }

    // return (<React.Fragment>
    //         <div className="min-w-750">
    //             <Grid container spacing={3}>
    //                 <Grid item xs={12}>
    //                     WELCOME TO THE WORLD!
    //                 </Grid>
    //             </Grid>
    //         </div>
    //     </React.Fragment>

    // );
    return (
        <C1DataTable
            // url=""
            title={"DO Extension Jobs"}
            columns={columns({ roleId, filterStatus })}
            isShowDownload={false}
            isShowPrint={false}
            isServer={false}
            defaultOrder="dateSubmitted"
            defaultOrderDirection="desc"
            dbName={clickDOExtensionJobs}
        />
    );

};

export default DoExtensionTasksList;