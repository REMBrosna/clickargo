import React from "react";
import C1DataTable from 'app/c1component/C1DataTable';
import moment from "moment";
import PropTypes from 'prop-types';
import { useTranslation } from "react-i18next";
import { customFilterDateTimeDisplay, formatDate } from "app/c1utils/utility";


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
                filter: false,
                viewColumns: false,
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
                    return formatDate(value, true);
                    // return moment(value).format('YYYY-MM-DD HH:mm:ss');
                },
                filter: true,
                filterType: 'custom',
                filterOptions: {
                    display: customFilterDateTimeDisplay
                },
            },
        },
        {
            name: "audtRemarks",
            label: t("audits.table.headers.remarks"),

        },
        {
            name: "audtUid",
            label: t("audits.table.headers.usrUid"),

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
                viewColumns: false,
            }

        },
        {
            name: "audtParam1",
            label: "Audit Param",
            options: {
                display: false,
                filter: false,
                viewColumns: false,
            }
        },
    ];

    const handleDownloadBuildBody = (values) => {
        return values?.length > 0 && values.map(value => {
            value.data[2] = moment(value?.data[2]).format('YYYY-MM-DD HH:mm').toString();
            return value;
        });
    }

    return (
        <React.Fragment>
            <C1DataTable url="/api/co/common/entity/auditLog"
                columns={columns}
                isShowToolbar={false}
                defaultOrder="audtTimestamp"
                isRowsSelectable={false}
                filterBy={[{ attribute: "audtReckey", value: filterId }]}
                defaultOrderDirection="desc" isShowFilterChip={false}
                handleBuildBody={handleDownloadBuildBody} />
        </React.Fragment>
    );
};


C1AuditTab.propTypes = {
    filterId: PropTypes.string
}

export default C1AuditTab;