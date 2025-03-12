import { Grid } from "@material-ui/core";
import React, { useEffect, useState } from "react";
import { useHistory } from "react-router-dom";

import C1ListPanel from "app/c1component/C1ListPanel";
import useHttp from "app/c1hooks/http";
import useAuth from "app/hooks/useAuth";
import { MatxLoading } from "matx";

import Dashboard from "./Dashboard";
import InvoiceApprovals from "./grids/InvoiceApprovals";
import PendingPayments from "./grids/PendingPayments";
import JobPayments from "./grids/JobPayments";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";

const DashboardPanel = () => {

    const history = useHistory();

    const { user } = useAuth();

    const [carouselItems, setCarouselItems] = useState([]);
    const [refreshPage, setRefreshPage] = useState(0);
    const { isLoading, isFormSubmission, res, error, urlId, sendRequest } = useHttp();
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        sendRequest(`/api/v1/clickargo/clictruck/dashboard`);
    }, [sendRequest, user, refreshPage]);

    useEffect(() => {
        if (!isLoading && !error && res) {
            setLoading(isLoading);
            prepareListComponent(res.data);
        }

        // eslint-disable-next-line
    }, [isLoading, error, res, isFormSubmission]);

    const prepareListComponent = (data) => {
        let carouselItemsComponent = data.map((app) => {
            // convert id from int to String;
            let newApp = {
                ...app, id: app.id + "", img: `${app.dbType}.png`, state: 'inactive'
            };

            switch (app.dbType) {
                case "VERIFIED_JOBS": {
                    newApp.listComponent = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {
                        return (
                            <InvoiceApprovals
                                roleId={roleId}
                                filterStatus={filterStatus}
                                key={app.dbType}
                                prevPath={history.location.pathname}
                                onFilterChipClose={onFilterChipClose}
                                onFilterChange={onFilterChange} />
                        );
                    };
                    break;
                }
                case "APPROVED_JOBS": {
                    newApp.listComponent = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {
                        return (
                            <PendingPayments
                                roleId={roleId}
                                filterStatus={filterStatus}
                                key={app.dbType}
                                prevPath={history.location.pathname}
                                onFilterChipClose={onFilterChipClose}
                                onFilterChange={onFilterChange} />
                        );
                    };
                    break;
                }
                case "JOB_PAYMENTS": {
                    newApp.listComponent = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {
                        return <JobPayments
                            roleId={roleId}
                            filterStatus={filterStatus}
                            key={app.dbType}
                            prevPath={history.location.pathname}
                            onFilterChipClose={onFilterChipClose}
                            onFilterChange={onFilterChange} />
                    };
                    break;
                }
                default: break;
            }

            return newApp;
        });

        setCarouselItems(carouselItemsComponent);
    };

    return loading ? <MatxLoading /> : (<React.Fragment>
        <div className="min-w-750">
            <C1ListPanel
                routeSegments={[
                    { name: "Dashboard", },
                ]}>
                <Grid container spacing={3}>
                    <Grid item xs={12}>
                        <Dashboard docs={carouselItems} />
                    </Grid>
                </Grid>
            </C1ListPanel>

        </div>
    </React.Fragment>

    );
};

export default withErrorHandler(DashboardPanel);