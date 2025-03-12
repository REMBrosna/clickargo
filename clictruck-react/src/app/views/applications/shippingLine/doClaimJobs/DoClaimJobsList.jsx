import React from "react";

import C1DataTable from 'app/c1component/C1DataTable';
import C1DataTableActions from 'app/c1component/C1DataTableActions';
import { JobStates } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import { customFilterDateDisplay, formatDate } from "app/c1utils/utility";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";

const DoRedemptionTasksList = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {

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
                name: "authoriserId.name",
                label: "Authoriser",
                // options: {
                //     filter: true,
                // },
            },
            {
                name: "authorizedParty.accnName",
                label: "Authorised Party",
                // options: {
                //     filter: true,
                // customBodyRender: (value) => null != value ? value.toUpperCase() : "",
                // },
            },
            {
                name: "dtSubmitted",
                label: "Date Submitted",
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
                        return formatDate(value, false);
                    }
                },
            },
            {
                name: "status",
                label: "Status",
                options: {
                    filter: true,
                    filterType: 'dropdown',
                    filterList: filterStatus,
                    filterOptions: {
                        names: [JobStates.SUB.code, JobStates.COM.code, JobStates.NEW.code,
                        JobStates.PAID.code, JobStates.PMV.code, JobStates.PROG.code],
                        renderValue: v => {
                            switch (v) {
                                case JobStates.SUB.code: return JobStates.SUB.desc;
                                case JobStates.COM.code: return JobStates.COM.desc;
                                case JobStates.NEW.code: return JobStates.NEW.desc;
                                case JobStates.PAID.code: return JobStates.PAID.desc;
                                case JobStates.PMV.code: return JobStates.PMV.desc;
                                case JobStates.PROG.code: return JobStates.PROG.desc;
                                default: break;
                            }
                        }
                    },
                    customFilterListOptions: {
                        render: v => {
                            switch (v) {
                                case JobStates.SUB.code: return JobStates.SUB.desc;
                                case JobStates.COM.code: return JobStates.COM.desc;
                                case JobStates.NEW.code: return JobStates.NEW.desc;
                                case JobStates.PAID.code: return JobStates.PAID.desc;
                                case JobStates.PMV.code: return JobStates.PMV.desc;
                                case JobStates.PROG.code: return JobStates.PROG.desc;
                                default: break;
                            }
                        }
                    },
                    customBodyRender: (value, tableMeta, updateValue) => {
                        return getStatusDesc(value)
                    }
                },
            },
            {
                name: "action",
                label: " ",
                options: {
                    filter: false,
                    display: true,
                    viewColumns: false,
                    customBodyRender: (value, tableMeta, updateValue) => {
                        const jobId = tableMeta.rowData[0];
                        return <C1DataTableActions
                            verifyDocumentEventHandler={`/applications/documents/claim/ver/view/${jobId}`}
                            verifyPaymentEventHandler={`/applications/documents/claim/pay/view/${jobId}`}
                        />
                    },
                },
            },
        ];
    }

    return (
        <C1TabInfoContainer guideId='clicdo.doi.ff.claim.jobs.list'>
            <C1DataTable
                url="/api/v1/clickargo/clicdo/task/taskDoiFf"
                title={"DO Claim Jobs"}
                columns={columns({ roleId, filterStatus })}
                isShowDownload={false}
                isShowPrint={false}
                isServer={true}
                defaultOrder="dateSubmitted"
                defaultOrderDirection="desc"
                guideId="clicdo.doi.ff.jobs.list.table"
            />
        </C1TabInfoContainer>
    );

};

export default DoRedemptionTasksList;