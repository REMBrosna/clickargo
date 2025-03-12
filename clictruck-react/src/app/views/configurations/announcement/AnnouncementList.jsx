import React,{useState, useEffect} from "react";
import useHttp from "app/c1hooks/http";
import C1DataTable from "app/c1component/C1DataTable";
import C1DataTableActions from 'app/c1component/C1DataTableActions';
import C1ListPanel from "app/c1component/C1ListPanel";
import { useTranslation } from "react-i18next";
import { getStatusDesc } from "app/c1utils/statusUtils";
import ConfirmationDialog from "matx/components/ConfirmationDialog";

const AnnouncementList = () => {
    const { t } = useTranslation(['configuration']);
    const { isLoading, res, error, urlId, sendRequest } = useHttp();
    const [isRefresh, setRefresh] = useState(false);

    // eslint-disable-next-line no-unused-vars
    const [loading, setLoading] = useState(false);

    const [confirm, setConfirm] = useState({ id: null });
    const [open, setOpen] = useState(false);

    useEffect(() => {
        if (!isLoading && !error && res) {
            setOpen(false);
            setRefresh(true);
            setLoading(false);
        }
    }, [isLoading, res, error, urlId]);


    const handleDeleteConfirm = (e, id) => {
        setLoading(false);
        setConfirm({ ...confirm, id: id});
        setOpen(true);
    }

    const handleDeleteHandler = (e) => {
        if (confirm && !confirm.id)
            return;

        setLoading(true);
        setRefresh(false);
        setTimeout(() => sendRequest("/api/co/anncmt/entity/anncmt/"+ confirm.id, "delete", "delete", {}), 1000);
    }

    const announcementColumn = [
        {
            name: "canuId",
            label: t("announcement.list.table.headers.id"),
            options: {
                sort: true,
                filter: true,
            }
        },
        {
            name: "TCoreApps.appsDesc",
            label: t("announcement.list.table.headers.application"),
            options: {
                sort: true,
                filter: true,
            }
        },
        {
            name: "TMstAnnouncementType.anypDescription",
            label: t("announcement.list.table.headers.announceType"),
            options: {
                sort: true,
                filter: true,
            }
        },
        {
            name: "canuSubject",
            label: t("announcement.list.table.headers.subject"),
            options: {
                sort: true,
                filter: true,
            }
        },
        {
            name: "canuPublic",
            label: t("announcement.list.table.headers.public"),
            options: {
                sort: false,
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return getStatusDesc(value);
                }
            }
        },
        {
            name: "canuStatus",
            label: t("announcement.list.table.headers.status"),
            options: {
                filter: true,
                sort: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return getStatusDesc(value);
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
                    return <C1DataTableActions
                        editPath={"/configuration/announcement/edit/" + tableMeta.rowData[0]}
                        viewPath={"/configuration/announcement/view/" + tableMeta.rowData[0]}
                        removeEventHandler={(e) => handleDeleteConfirm(e, tableMeta.rowData[0])}
                    />
                }
            },
        }
    ];

    return (
        <div>
            {confirm?.id && (
                <ConfirmationDialog
                    open={open}
                    title={t("common:confirmMsgs.delete.title")}
                    text={t("common:confirmMsgs.delete.content", { appnId: confirm?.id  })}
                    onYesClick={() => handleDeleteHandler()}
                    onConfirmDialogClose={() => setOpen(false)}
                />
            )}

            <C1ListPanel
                routeSegments={[
                    { name: t("announcement.list.routeSegment") },
                ]}
            >
                <C1DataTable url="/api/co/anncmt/entity/anncmt"
                             showAdd={{
                                 path: "/configuration/announcement/new"
                             }}
                             columns={announcementColumn}
                             title={t("announcement.list.table.title")}
                             defaultOrder="canuDtLupd"
                             isServer={true}
                             isRefresh={isRefresh}
                             defaultOrderDirection="desc"
                />
            </C1ListPanel>
        </div>
    );
}

export default AnnouncementList;