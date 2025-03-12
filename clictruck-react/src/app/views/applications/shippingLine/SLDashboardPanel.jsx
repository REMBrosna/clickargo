import React, { useState, useEffect } from "react";
import SLDashboard from "./SLDashboard";
import { Grid } from "@material-ui/core";
import { useHistory } from "react-router-dom";
import useAuth from "app/hooks/useAuth";
import useHttp from "app/c1hooks/http";
import DoRedemptionTasksList from "./doClaimJobs/DoClaimJobsList";
import DoExtensionTasksList from "./doExtensionJobs/DoExtensionJobsList";
import C1ListPanel from "app/c1component/C1ListPanel";

const SLDashboardPanel = () => {



    const history = useHistory();

    const { user } = useAuth();

    const [carouselItems, setCarouselItems] = useState([]);
    const [refreshPage, setRefreshPage] = useState(0);
    const { isLoading, isFormSubmission, res, error, urlId, sendRequest } = useHttp();

    useEffect(() => {
        sendRequest(`/api/v1/clickargo/clicdo/dashboard`);
    }, [sendRequest, user, refreshPage]);

    useEffect(() => {
        if (!isLoading && !error && res) {
            prepareListComponent(res.data);
        }
        // eslint-disable-next-line
    }, [isLoading, error, res, isFormSubmission]);

    const prepareListComponent = (data) => {
        let carouselItemsComponent = data.map((app) => {
            // convert id from int to String;
            let newApp = { ...app, id: app.id + "", img: `${app.dbType}.png`, state: 'inactive' };

            switch (app.dbType) {
                case "DO_CLAIM": {
                    newApp.listComponent = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {
                        return <DoRedemptionTasksList
                            roleId={roleId}
                            filterStatus={filterStatus}
                            key={app.dbType}
                            prevPath={history.location.pathname}
                            onFilterChipClose={onFilterChipClose}
                            onFilterChange={onFilterChange} />
                    };
                    break;
                }
                case "DO_EXT": {
                    newApp.listComponent = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {
                        return <DoExtensionTasksList
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

    return (<React.Fragment>
        <div className="min-w-750">
            <C1ListPanel
                routeSegments={[
                    { name: "Dashboard", },
                ]}>
                <Grid container spacing={3}>
                    <Grid item xs={12}>
                        <SLDashboard docs={carouselItems} />
                    </Grid>
                </Grid>
            </C1ListPanel>


        </div>
    </React.Fragment>

    );
};

export default SLDashboardPanel;