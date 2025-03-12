import React,{useState, useEffect} from "react";
import useHttp from "app/c1hooks/http";
import C1DataTable from "app/c1component/C1DataTable";
import C1DataTableActions from 'app/c1component/C1DataTableActions';
import C1ListPanel from "app/c1component/C1ListPanel";
import { useTranslation } from "react-i18next";
import {getActiveMode, getDeActiveMode, getStatusDesc} from "app/c1utils/statusUtils";

const AnnouncementTypeList = () => {

    const { t } = useTranslation(['configuration']);
    const { isLoading, res, error, urlId, sendRequest } = useHttp();
    const [isRefresh, setRefresh] = useState(false);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (!isLoading && !error && res) {
            if (urlId==='getForActive'){
                sendRequest("/api/co/anncmt/entity/anncmtType/" + res.data.anypId, "active", "put", { ...res.data, anypStatus: 'A' });
            }
            if (urlId==='active' || urlId==='deActive'){
                setRefresh(true);
                setLoading(false);
            }
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isLoading, res, error, urlId]);

    const announcementTypeColumn = [
        {
            name: "anypId",
            label: t("announcementType.list.table.headers.anypId"),
            options: {
                sort: true,
                filter: true,
            }
        },
        {
            name: "anypDescription",
            label: t("announcementType.list.table.headers.anypDescription"),
            options: {
                sort: true,
                filter: true,
            }
        },
        {
            name: "anypStatus",
            label: t("announcementType.list.table.headers.anypStatus"),
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
                        editPath={getDeActiveMode(tableMeta.rowData[2]) ? "/configuration/announcementType/edit/" + tableMeta.rowData[0]: null}
                        viewPath={"/configuration/announcementType/view/" + tableMeta.rowData[0]}
                        deActiveEventHandler={getDeActiveMode(tableMeta.rowData[2]) ? () => handleDeActiveHandler(tableMeta.rowData[0]): null}
                        activeEventHandler={getActiveMode(tableMeta.rowData[2]) ? () => handleActiveHandler(tableMeta.rowData[0]): null}
                    />
                }
            },
        }
    ];

    const handleDeActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest("/api/co/anncmt/entity/anncmtType/"+ id, "deActive", "delete", {})
    }

    const handleActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest("/api/co/anncmt/entity/anncmtType/"+ id, "getForActive", "get", {});
    }

    return (
        <div>
            <C1ListPanel
                routeSegments={[
                    { name: t("announcementType.list.routeSegment") },
                ]}
            >
                <C1DataTable url="/api/co/anncmt/entity/anncmtType"
                             showAdd={{
                                 path: "/configuration/announcementType/new"
                             }}
                             columns={announcementTypeColumn}
                             title={t("announcementType.list.table.title")}
                             defaultOrder="anypDtLupd"
                             isServer={true}
                             isRefresh={isRefresh}
                             defaultOrderDirection="desc"
                />
            </C1ListPanel>
        </div>
    );
}

export default AnnouncementTypeList;