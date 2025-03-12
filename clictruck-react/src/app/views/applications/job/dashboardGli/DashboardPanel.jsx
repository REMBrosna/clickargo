import { Grid } from "@material-ui/core";
import React, { useEffect, useState } from "react";
import { useHistory } from "react-router-dom";

import C1ListPanel from "app/c1component/C1ListPanel";
import useHttp from "app/c1hooks/http";
import useAuth from "app/hooks/useAuth";
import { MatxLoading } from "matx";

import Dashboard from "./Dashboard";
import JobPayments from "./grid/JobPayments";
import ApprovedJobs from "./grid/ApprovedJobs";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import DashboardNav from "app/atomics/organisms/DashboardNav";
import PageWrapper from "app/atomics/atoms/PageWrapper";
import Breadcrumbs from "app/atomics/atoms/Breadcrumbs";
import ContentWrapper from "app/atomics/atoms/ContentWrapper";
import { isArrayNotEmpty } from "app/c1utils/utility";

/** Dashboard for Service Provider */
const DashboardPanel = () => {

    const history = useHistory();

    const { user } = useAuth();

    // const [carouselItems, setCarouselItems] = useState([]);
    const [carouselItems, setCarouselItems] = useState([]);
    const [refreshPage, setRefreshPage] = useState(0);
    const { isLoading, isFormSubmission, res, error, urlId, sendRequest } = useHttp();
    const [loading, setLoading] = useState(true);
    const [selectedId, setSelectedId] = useState(0);
    const [filterStatus, setFilterStatus] = useState([]);
    const [cardItems, setCardItems] = useState([]);
    const workBenchSelectedId = "dashboardGLI";

    useEffect(() => {
        sendRequest(`/api/v1/clickargo/clictruck/accnconfig/edashboard`);
    }, []);

    useEffect(() => {
        if (!isLoading && !error && res) {
            setLoading(isLoading);
            setCardItems(res.data)
            prepareListComponent(res.data);
        }

        // eslint-disable-next-line
    }, [isLoading, error, res, isFormSubmission]);

    //monitor carouselItems
    useEffect(() => {
        let isMissing = false;
        carouselItems && carouselItems.map(file => {
            if (file && file?.id === selectedId) {
                if (typeof file.listComponent === 'undefined') {
                    isMissing = true;
                }
            }

            return null;
        });

        //if this returns true, redirect
        if (isMissing) {
            history.push("/")
        }

    }, [carouselItems]);


    const prepareListComponent = (data) => {
        let storagedId = sessionStorage.getItem(workBenchSelectedId);
        let carouselItemsComponent = data?.map((app, i) => {
            let state = 'inactive';
            if (!storagedId) {
                if (i === 0) {
                    state = 'active';
                }
            } else if (storagedId && parseInt(storagedId) === app.id) {
                state = 'active'
                setSelectedId(app.id)
            }
            let newApp = {
                ...app, id: app.id, img: `${app.dbType}.png`, state
            };

            switch (app.dbType) {
                case "APPROVED_JOBS": {
                    newApp.listComponent = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {
                        return (
                            <ApprovedJobs
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

    const handleClickStatus = (e, docObjId, filterStatusParam) => {
        e.stopPropagation(); // important, can't trigger toggleImageShowListing function;
        if ("Open" !== filterStatusParam) {
            setFilterStatus([filterStatusParam]);
        } else {
            setFilterStatus();
        }
        sessionStorage.setItem(workBenchSelectedId, docObjId);

        setSelectedId(docObjId);
        setFilterStatus([]);
    }

    const handleFilterChipClose = (index, removedFilter, filterList) => {
        let indx = filterStatus.indexOf(removedFilter);
        filterStatus.splice(indx, 1);
        setFilterStatus(filterStatus);
    }

    const handleFilterChange = (changedColumn, filterList, type, changedColumnIndex, displayData) => {
        if (type === 'reset')
            setFilterStatus([]);
        else {
            //To append the status selected from workbench card, and at the same time to also not include
            //the status when the workbench card folder is clicked instead of status filtering - which causes an extra blank chip
            if (changedColumn && (changedColumn.includes('status') || changedColumn.includes('Status'))
                && filterStatus && isArrayNotEmpty(filterStatus))
                setFilterStatus([...filterStatus, filterList[changedColumnIndex]]);
        }
    }

    return loading ? <MatxLoading /> :
        (
            <PageWrapper>
                <Breadcrumbs segments={[{ name: 'Dashboard' }]} />
                <DashboardNav data={cardItems} handleClick={handleClickStatus} activeId={selectedId} />
                <ContentWrapper>
                    {carouselItems && carouselItems.map(file => {
                        if (file && file?.id === selectedId) {
                            if (typeof file.listComponent !== 'undefined') {
                                return file.listComponent({ roleId: user.role, filterStatus, setFilterStatus, onFilterChipClose: handleFilterChipClose, onFilterChange: handleFilterChange });
                            }
                        }

                        return null;
                    })}
                </ContentWrapper>
            </PageWrapper>
        )
};

export default withErrorHandler(DashboardPanel);