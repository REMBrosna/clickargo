import React from "react";
import { AccountTypes, DashboardTypes } from "app/c1utils/const";
import InvoiceApprovals from "../job/dashboard/finance/grids/InvoiceApprovals";
import TruckJobList from "../job/dashboard/grid/TruckJobList";
import BilledJobs from "../job/dashboard/grid/BilledJobs";
import PendingPayments from "../job/dashboard/finance/grids/PendingPayments";
import JobPayments from "../job/dashboard/finance/grids/JobPayments";
import JobPaymentsSp from "../job/dashboardGli/grid/JobPayments";
import TruckJobListTO from "../job/dashboardTruckJob/grid/TruckJobList";
import ApprovedJobs from "../job/dashboardGli/grid/ApprovedJobs";
import JobsToBill from "../job/dashboardTruckJob/grid/JobsToBill";


export const prepareListComponent = (data, workBenchSelectedId, setSelectedId,setCarouselItems, history, accnType) => {
    let storagedId = sessionStorage.getItem(workBenchSelectedId);
    let carouselItemsComponent = data.map((app, i) => {
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
            case DashboardTypes.VERIFIED_JOBS.code: {
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
            case DashboardTypes.TRUCK_JOBS.code: {
                newApp.listComponent = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {
                   if(accnType === AccountTypes.ACC_TYPE_TO.code)
                        return (
                            <TruckJobList
                                roleId={roleId}
                                filterStatus={filterStatus}
                                key={app.dbType}
                                prevPath={history.location.pathname}
                                onFilterChipClose={onFilterChipClose}
                                onFilterChange={onFilterChange} />
                        );
                    else  
                        return (
                            <TruckJobList
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
            case DashboardTypes.BILLED_JOBS.code: {
                newApp.listComponent = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {
                    return (
                        <BilledJobs
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
            case DashboardTypes.APPROVED_JOBS.code: {
                newApp.listComponent = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {
                    if(accnType === AccountTypes.ACC_TYPE_TO.code)
                        return (
                            <ApprovedJobs
                                roleId={roleId}
                                filterStatus={filterStatus}
                                key={app.dbType}
                                prevPath={history.location.pathname}
                                onFilterChipClose={onFilterChipClose}
                                onFilterChange={onFilterChange} />
                        );
                    else 
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
            case DashboardTypes.JOB_PAYMENTS.code: {
                newApp.listComponent = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {
                   if([AccountTypes.ACC_TYPE_SP.code, AccountTypes.ACC_TYPE_TO.code].includes(accnType))
                        return  <JobPaymentsSp
                        roleId={roleId}
                        filterStatus={filterStatus}
                        key={app.dbType}
                        prevPath={history.location.pathname}
                        onFilterChipClose={onFilterChipClose}
                        onFilterChange={onFilterChange} />
                    else
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
            case DashboardTypes.JOB_BILLING.code: {
                newApp.listComponent = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {
                    return (
                        <JobsToBill
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
            default: break;
        }

        return newApp;
    });

    setCarouselItems(carouselItemsComponent);
};