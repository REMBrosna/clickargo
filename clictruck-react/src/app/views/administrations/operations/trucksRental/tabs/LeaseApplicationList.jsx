import Snackbar from "@material-ui/core/Snackbar";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import DataTable from "app/atomics/organisms/DataTable";
import C1Alert from "app/c1component/C1Alert";
import C1Warning from "app/c1component/C1Warning";
import useHttp from "app/c1hooks/http";
import {
    JobStates,
} from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import {
    formatDate,
    isArrayNotEmpty,
} from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import history from "history.js";
import { MatxLoading } from "matx";
import DescriptionIcon from '@material-ui/icons/Description';
import ChipStatus from "../../../../../atomics/atoms/ChipStatus";

const LeaseApplicationList = (props) => {

    const {
        roleId,
        filterStatus,
        onFilterChange,
        onFilterChipClose,
    } = props;

    const { t } = useTranslation([
        "job",
        "common",
        "status",
        "buttons",
        "listing",
        "ffclaims",
        "administration"
    ]);

    const { user } = useAuth();
    const { isLoading, res, error, urlId, sendRequest } = useHttp();

    const [loading, setLoading] = useState(false);
    const [isRefresh, setRefresh] = useState(false);
    const [success, setSuccess] = useState(false);
    const [stateFilter, setStateFilter] = useState([])
    const [warningMessage, setWarningMessage] = useState({
        open: false,
        msg: "",
    });
    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: "top",
        horizontal: "center",
        msg: t("common:common.msg.deleted"),
        severity: "success",
    });

    const columns = [
        {
            name: "truck",
            label: t("administration:trucksRental.rentals.trucks"),
            options: {
                filter: false,
            },
        },
        {
            name: "provider",
            label: t("administration:trucksRental.rentals.provider"),
            options: {
                filter: false,
            },
        },
        {
            name: "lease",
            label: t("administration:trucksRental.rentals.leasePlans"),
            options: {
                filter: false,
            },
        },
        {
            name: "price",
            label: t("administration:trucksRental.rentals.price"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return value && new Intl.NumberFormat('en-SG', { style: 'currency', currency: 'SGD' }).format(value)
                },
            },
        },
        {
            name: "quantity",
            label: t("administration:trucksRental.rentals.numberTrucks"),
            options: {
                filter: false,
            },
        },
        {
            name: "vrDtCreate",
            label: t("administration:trucksRental.rentals.dateSubmit"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                },
            },
        },
        {
            name: "vrStatus",
            label: t("administration:trucksRental.rentals.status"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    const getStatusColorAndText = (va) => {
                        let statusColor, statusText;
                        switch (va) {
                            case "S":
                                statusColor = "#00D16D";
                                statusText = "Approved";
                                break;
                            case "R":
                                statusColor = "#FF2E6C";
                                statusText = "Rejected";
                                break;
                            case "N":
                                statusColor = "#37B7FF";
                                statusText = "New";
                                break;
                            default:
                                break;
                        }

                        return { statusColor, statusText };
                    };
                    const { statusColor, statusText } = getStatusColorAndText(value);
                    return <ChipStatus text={statusText} color={statusColor} />;
                },
            },
        }
    ];

    const handleWarningAction = (e) => {
        setWarningMessage({ open: false, msg: "", hlMsg: "", subMsg: "" });
    };

    const handleCloseSnackBar = () => {
        setSnackBarState({ ...snackBarState, open: false });
    };

    let snackBar = null;
    if (success) {
        const anchorOriginV = snackBarState.vertical;
        const anchorOriginH = snackBarState.horizontal;
        snackBar = (
            <Snackbar
                anchorOrigin={{ vertical: anchorOriginV, horizontal: anchorOriginH }}
                open={snackBarState.open}
                onClose={handleCloseSnackBar}
                autoHideDuration={3000}
                key={anchorOriginV + anchorOriginH}
            >
                <C1Alert
                    onClose={handleCloseSnackBar}
                    severity={snackBarState.severity}
                >
                    {snackBarState.msg}
                </C1Alert>
            </Snackbar>
        );
    }

    return (
        <React.Fragment>
            {loading && <MatxLoading />}
            {snackBar}
            <DataTable
                title=""
                url="/api/v1/clickargo/clictruck/administrator/rentalApp"
                columns={columns}
                defaultOrder="vrDtCreate"
                defaultOrderDirection="desc"
                isServer={true}
                isShowViewColumns={true}
                isShowDownload={true}
                isShowPrint={true}
                isShowFilter={false}
                isRefresh={isRefresh}
                isShowToolbar={true}
                isShowFilterChip
                filterBy={[
                    { attribute: "history", value: "all" },
                    { attribute: "accnId", value: user?.coreAccn?.accnId },
                ]}
                guideId={""}
                showActiveHistoryButton={false}
                customRowsPerPage={[10, 20]}
            />
            <C1Warning
                warningMessage={warningMessage}
                handleWarningAction={handleWarningAction}
            />
        </React.Fragment>
    );
};

export default withErrorHandler(LeaseApplicationList);
