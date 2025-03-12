import React from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

import C1DataTable from "app/c1component/C1DataTable";
import C1ListPanel from "app/c1component/C1ListPanel";
import C1DataTableActions from "app/c1component/C1DataTableActions";

import { getStatusDesc } from "app/c1utils/statusUtils";
import { CAN_NOTIFICATION_TEMPLATE_URL } from "app/c1utils/const";

const NotificationTemplateList = () => {
    const { t } = useTranslation(["masters", "common"]);

    const columns = [
        {
            name: "id.ntplId",
            label: t("masters:notificationTemplate.list.table.headers.notifTemplateId"),
            options: {
                sort: true,
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <div className="flex items-center">
                            <div className="ml-3">
                                <Link
                                    to={`/notification/templates/view/${tableMeta.rowData[0]}`}
                                >
                                    <h5 className="my-0 text-15"> {value} </h5>
                                </Link>
                            </div>
                        </div>
                    );
                },
            },
        },
        {
            name: "id.ntplAppscode",
            label: t("masters:notificationTemplate.list.table.headers.notifTemplateAppCode"),
            options: {
                filter: true,
                display: false,
            },
        },
        {
            name: "TCoreNotificationChannelType.nchntypeDesc",
            label: t("masters:notificationTemplate.list.table.headers.notifTemplateChannel"),
            options: {
                filter: true,
            },
        },
        {
            name: "ntplSubject",
            label: t("masters:notificationTemplate.list.table.headers.notifTemplateSubject"),
            options: {
                filter: true,
            },
        },
        {
            name: "ntplStatus",
            label: t("masters:notificationTemplate.list.table.headers.notifTemplateStatus"),
            options: {
                sort: true,
                filter: true,
                filterType: "dropdown",
                customBodyRender: (value, tableMeta, updateValue) => getStatusDesc(value),
                filterOptions: {
                    names: ["A", "I"],
                    renderValue: (v) => {
                        if (v === "A") {
                            return "Active";
                        } else if (v === "I") {
                            return "InActive";
                        }
                    },
                },
                customFilterListOptions: {
                    render: (v) => {
                        if (v === "A") {
                            return "Active";
                        } else if (v === "I") {
                            return "InActive";
                        }
                    },
                },
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
                    return (
                        <C1DataTableActions
                            editPath={`/notification/templates/edit/${tableMeta.rowData[0]}`}
                            viewPath={`/notification/templates/view/${tableMeta.rowData[0]}`}
                        />
                    );
                },
            },
        },
    ];

    return (
        <div>
            <C1ListPanel routeSegments={[{ name: t("masters:notificationTemplate.list.routeSegment") }]}>
                <C1DataTable
                    url={CAN_NOTIFICATION_TEMPLATE_URL}
                    columns={columns}
                    title={t("masters:notificationTemplate.list.table.title")}
                    defaultOrder="id.ntplId"
                    isServer={true}
                    filterBy={[{ attribute: "id.ntplAppscode", value: "CPEDI" }]}
                />
            </C1ListPanel>
        </div>
    );
};

export default NotificationTemplateList;
