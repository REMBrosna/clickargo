import React from "react";
import C1DataTable from 'app/c1component/C1DataTable';
import moment from "moment";
import PropTypes from 'prop-types';
import { useTranslation } from "react-i18next";
import { Box } from "@material-ui/core";

/**
 * @param filterId - id of the record to display the audits
 */
const C1AuditTab = ({ filterId }) => {
    const { t } = useTranslation(["common"]);
    // let snackBar = null;
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
            label: t("audits.table.headers.event"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
            },

        },
        {
            name: "audtTimestamp",
            label: t("audits.table.headers.timestamp"),
            options: {
                sort: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return moment(value).format('YYYY-MM-DD HH:mm:ss');
                },
            },
        },
        {
            name: "audtUname",
            label: t("audits.table.headers.usrName"),

        },
        {
            name: "audtReckey",
            label: "Audit Key",
            options: {
                display: false,
                filter: false,
                filterType: 'custom',
                filterList: [filterId === undefined || filterId === null ? null : filterId],

            }

        },
        {
            name: "audtParam1",
            label: "Audit Param",
            options: {
                display: false,
                filter: false
            }
        },
    ];

    return (
        <React.Fragment>
            <Box className="p-3">
                <C1DataTable url="/api/v1/auditLog"
                    columns={columns}
                    isShowToolbar={false}
                    filterBy={[{ attribute: "audtUname", value: filterId }]}
                    defaultOrder="audtTimestamp" isRowsSelectable={false}
                    defaultOrderDirection="desc" isShowFilterChip={false} />
            </Box>
        </React.Fragment>
    );
};


C1AuditTab.propTypes = {
    filterId: PropTypes.string
}

export default C1AuditTab;