import React, { useState, useEffect } from "react";
import { useHistory } from "react-router-dom";
import WorkBench from "./WorkBench";
import Snackbar from "@material-ui/core/Snackbar";
import { MatxLoading } from "matx";
import C1Alert from "app/c1component/C1Alert";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import { Grid } from "@material-ui/core";
import useHttp from "app/c1hooks/http";

import ShipRegListDashBoard from "app/views/vessel/shipRegistration/ShipRegListDashBoard.jsx";
import VesselCallListDashBoard from "app/views/applications/vesselCall/VesselCallListDashBoard.jsx";
import DosListDashboard from "app/views/applications/declarationSecurity/DosListDashboard.jsx";
import PilotOrderListDashboard from "app/views/applications/pilotOrder/PilotOrderListDashboard.jsx";
import ArrivalDeclarationListDashboard from "app/views/applications/arrivalDeclaration/ArrivalDeclarationListDashboard.jsx";
import PreArrivalNoticeListDashBoard from "app/views/applications/preArrivalNotice/PreArrivalNoticeListDashBoard.jsx";
import DepartureDeclarationListDashboard from "app/views/applications/departureDeclaration/DepartureDeclarationListDashboard.jsx";
import EntryPermitListDashboard from "app/views/applications/entryPermit/EntryPermitListDashboard.jsx";
import SSCECListDashboard from "app/views/applications/sscec/SSCECListDashboard.jsx";
import SSCCListDashboard from "app/views/applications/sscec/SSCCListDashboard.jsx";
import CargoSecurityListDashBoard from "app/views/applications/cargoSecurityInfo/CargoSecurityListDashBoard.jsx";
import AdviceGenerationListDashBoard from "../configurations/payment/adviceGeneration/AdviceGenerationListDashBoard";

const WorkBenchPanel = () => {
    const history = useHistory();

    const { user } = useAuth();
    // console.log(user);

    const [carouselItems, setCarouselItems] = useState([]);

    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: "top",
        horizontal: "center",
        msg: "",
        severity: "success",
    });
    const [refreshPage, setRefreshPage] = useState(0);
    const { isLoading, isFormSubmission, res, error, sendRequest } = useHttp();

    useEffect(() => {
        sendRequest(`/api/portedi/workbench`);
    }, [sendRequest, user, refreshPage]);

    const prepareListComponent = (data) => {
        let carouselItemsComponent = data.map((app) => {
            // convert id from int to String;
            let newApp = { ...app, id: app.id + "" };

            switch (app.docType) {
                case "SR": {
                    newApp.listComponent = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {
                        return (
                            <ShipRegListDashBoard
                                roleId={roleId}
                                filterStatus={filterStatus}
                                key="SR"
                                prevPath={history.location.pathname}
                                onFilterChipClose={onFilterChipClose}
                                onFilterChange={onFilterChange} />
                        );
                    };
                    break;
                }
                case "VC": {
                    newApp.listComponent = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {
                        return <VesselCallListDashBoard
                            roleId={roleId}
                            filterStatus={filterStatus}
                            key="VC"
                            prevPath={history.location.pathname}
                            onFilterChipClose={onFilterChipClose}
                            onFilterChange={onFilterChange} />;
                    };
                    break;
                }
                case "DOS": {
                    newApp.listComponent = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {
                        return <DosListDashboard roleId={roleId} filterStatus={filterStatus} key="DOS"
                            onFilterChipClose={onFilterChipClose} onFilterChange={onFilterChange} />;
                    };
                    break;
                }
                case "PO": {
                    newApp.listComponent = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {
                        return (
                            <PilotOrderListDashboard
                                roleId={roleId}
                                filterStatus={filterStatus}
                                key="PO" onFilterChipClose={onFilterChipClose}
                                onFilterChange={onFilterChange} />
                        );
                    };
                    break;
                }
                case "ADSUB": {
                    newApp.listComponent = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {
                        return (
                            <ArrivalDeclarationListDashboard
                                roleId={roleId}
                                filterStatus={filterStatus}
                                key="ADSUB" onFilterChipClose={onFilterChipClose}
                                onFilterChange={onFilterChange} />
                        );
                    };
                    break;
                }
                case "PAN": {
                    newApp.listComponent = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {
                        return (
                            <PreArrivalNoticeListDashBoard
                                roleId={roleId}
                                filterStatus={filterStatus}
                                key="PAN" onFilterChipClose={onFilterChipClose} onFilterChange={onFilterChange} />
                        );
                    };
                    break;
                }
                case "DDSUB": {
                    newApp.listComponent = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {
                        return (
                            <DepartureDeclarationListDashboard
                                roleId={roleId}
                                filterStatus={filterStatus}
                                key="DDSUB" onFilterChipClose={onFilterChipClose} onFilterChange={onFilterChange} />
                        );
                    };
                    break;
                }
                case "EP": {
                    newApp.listComponent = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {
                        return (
                            <EntryPermitListDashboard
                                roleId={roleId}
                                filterStatus={filterStatus}
                                key="EP" onFilterChipClose={onFilterChipClose} onFilterChange={onFilterChange} />
                        );
                    };
                    break;
                }
                case "PAY": {
                    newApp.listComponent = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {
                        return (
                            <AdviceGenerationListDashBoard
                                roleId={roleId}
                                filterStatus={filterStatus}
                                onFilterChipClose={onFilterChipClose}
                                onFilterChange={onFilterChange} />
                        );
                    };
                    break;
                }
                case "SSCEC": {
                    newApp.listComponent = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {
                        return (
                            <SSCECListDashboard
                                roleId={roleId}
                                filterStatus={filterStatus}
                                key="SSCEC" onFilterChipClose={onFilterChipClose} onFilterChange={onFilterChange} />
                        );
                    };
                    break;
                }
                case "SSCC": {
                    newApp.listComponent = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {
                        return (
                            <SSCCListDashboard
                                roleId={roleId}
                                filterStatus={filterStatus}
                                key="SSCC" onFilterChipClose={onFilterChipClose} onFilterChange={onFilterChange} />
                        );
                    };
                    break;
                }
                case "PAS": {
                    newApp.listComponent = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {
                        return <CargoSecurityListDashBoard key="PAS" roleId={roleId}
                            filterStatus={filterStatus} onFilterChipClose={onFilterChipClose} onFilterChange={onFilterChange} />;
                    }
                    break;
                }
                default: {
                }
            }

            return newApp;
        });

        // console.log("carouselItemsComponent", carouselItemsComponent);

        setCarouselItems(carouselItemsComponent);
    };

    /////////////////
    useEffect(() => {
        let msg = "";
        let severity = "success";
        setSubmitSuccess(false);

        if (!isLoading && !error && res) {
            prepareListComponent(res.data);
        } else if (error) {
            msg = "Error encountered whilte trying to fetch data!";
            severity = "error";
            setSubmitSuccess(true);
            setSnackBarState((sb) => {
                return { ...sb, open: true, msg: msg, severity: severity };
            });
        }

        // eslint-disable-next-line
    }, [isLoading, error, res, isFormSubmission]);

    const handleClose = () => {
        setSnackBarState({ ...snackBarState, open: false });
    };

    let snackBar = null;
    if (isSubmitSuccess) {
        const anchorOriginV = snackBarState.vertical;
        const anchorOriginH = snackBarState.horizontal;

        snackBar = (
            <Snackbar
                anchorOrigin={{ vertical: anchorOriginV, horizontal: anchorOriginH }}
                open={snackBarState.open}
                onClose={handleClose}
                autoHideDuration={3000}
                key={anchorOriginV + anchorOriginH}
            >
                <C1Alert onClose={handleClose} severity={snackBarState.severity}>
                    {snackBarState.msg}
                </C1Alert>
            </Snackbar>
        );
    }
    return isLoading ? (
        <MatxLoading />
    ) : (
        <React.Fragment>
            {snackBar}
            <div className="min-w-750">
                <Grid container spacing={3}>
                    <Grid item xs={12}>
                        <WorkBench docs={carouselItems} />
                    </Grid>
                </Grid>
            </div>
        </React.Fragment>
    );
};

export default withErrorHandler(WorkBenchPanel);
