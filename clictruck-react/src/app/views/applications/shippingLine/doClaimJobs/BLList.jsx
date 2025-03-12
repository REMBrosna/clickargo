import React, {useState, useEffect} from "react";
import { Grid } from "@material-ui/core";
import C1DataTable from 'app/c1component/C1DataTable';
import { clickDOClaimJobs } from "fake-db/db/clickDOClaimJobs";
import {jobsDb} from "fake-db/db/jobs";
import C1DataTableActions from 'app/c1component/C1DataTableActions';

const BLList = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {

    const columns = ({ roleId, filterStatus }) => {
        return [
            // {
            //     name: "jobId",
            //     label: "Job Id"

            // },
            {
                //name: "docNo",
                name: "blNo",
                label: "BL Number"

            },
            {
                name: "shipmentType",
                label: "Type"

            },
            {
                name: "authoriserUserId",
                label: "Authoriser",
                // options: {
                //     filter: true,
                // },
            },
            // {
            //     name: "authorizedParty.accnName",
            //     label: "Authorised Party",
                // options: {
                //     filter: true,
                    // customBodyRender: (value) => null != value ? value.toUpperCase() : "",
                // },
            // },
            {
                name: "dtSubmitted",
                label: "BL Submit Date",
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
            // {
            //     name: "status",
            //     label: "Status",
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
            // },
            {
                name: "action",
                label: " ",
                options: {
                    filter: false,
                    display: true,
                    viewColumns: false,
                    customBodyRender: (value, tableMeta, updateValue) => {
                        // const jobId = tableMeta.rowData[0];
                        return <C1DataTableActions
                            // verifyDocumentEventHandler={`/applications/documents/claim/ver/view/${jobId}`} 
                            // verifyPaymentEventHandler={`/applications/documents/claim/pay/view/${jobId}`}
                            viewPath={true}
                        />
                    },
                },
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
            title={"Selected Bill of Ladings"}
            columns={columns({ roleId, filterStatus })}
            isShowDownload={false}
            isShowPrint={false}
            isServer={false}
            isShowViewColumns={false}
            isShowFilter={false}
            defaultOrder="dateSubmitted"
            defaultOrderDirection="desc"
            dbName={jobsDb}
        />
    );

};

export default BLList;