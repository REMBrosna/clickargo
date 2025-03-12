import { Box, Grid } from "@material-ui/core";
import moment from "moment";
import PropTypes from 'prop-types';
import React, { useState } from "react";

import C1DataTable from 'app/c1component/C1DataTable';
import C1OutlinedDiv from "app/c1component/C1OutlinedDiv"
import { useStyles } from "app/c1utils/styles";

import { jobsAudit } from '../../fake-db/db/jobs';
import C1Information from "./C1Information";

const C1AuditTab = ({ filterId, appStatus }) => {

    const classes = useStyles();
    // let filteredDb = { list: jobsAudit.list.filter(el => el.status === appStatus) };
    // const [auditFakeDB, setAuditFakeDB] = useState(filteredDb);
    const columns = [
        {
            name: "audtId", // field name in the row object
            label: "Audit ID", // column title that will be shown in table
            options: {
                display: false,

            }
        },
        {
            name: "audtEvent", // field name in the row object
            label: "Event", // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
            },

        },
        {
            name: "audtTimestamp",
            label: "Timestamp",
            options: {
                sort: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return moment(value).format('DD/MM/YYYY hh:mm:ss');
                },
            },


        },
        {
            name: "audtRemarks",
            label: "Remarks",

        },
        {
            name: "audtUid",
            label: "User ID",

        },
        {
            name: "audtUname",
            label: "User Name",

        },
        {
            name: "audtReckey",
            label: "Audit Key",
            options: {
                display: false,
                filter: false,
                // filterType: 'custom',
                //filterList: [filterId === undefined || filterId === null ? null : filterId],

            }

        },
        {
            name: "audtParam1",
            label: "Audit Param",
            options: {
                display: false,
                filter: false,
                // filterType: 'custom',

            }

        },


    ];

    return (
        <React.Fragment>
            <Grid container alignItems="flex-start" spacing={1} className={classes.gridContainer}>
                <Grid item xs={12} lg={12} md={12}>
                    <C1DataTable isServer={false} columns={columns} dbName={jobsAudit}
                        isEmpty={filterId === undefined || filterId === '0' || filterId === null || filterId === '' ? true : false}
                        defaultOrder="audtTimestamp" isRowsSelectable="none" isShowPagination={true}
                        defaultOrderDirection="desc" showToolbar={false} showFilterChip="false" />
                </Grid>
                <Grid item xs={12}>
                    <C1Information information="auditDetails" />
                </Grid>
            </Grid>


        </React.Fragment >
    );

};


C1AuditTab.propTypes = {
    filterId: PropTypes.string
}

export default C1AuditTab;