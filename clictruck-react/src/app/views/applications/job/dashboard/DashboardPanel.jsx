import { Box } from "@material-ui/core";
import React, { useEffect, useState } from "react";
import { useHistory } from "react-router-dom";
import useHttp from "app/c1hooks/http";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import { MatxLoading } from "matx";

import InvoiceApprovals from "./finance/grids/InvoiceApprovals";
import BilledJobs from "./grid/BilledJobs";
import TruckJobList from "./grid/TruckJobList";
import PendingPayments from "./finance/grids/PendingPayments";
import JobPayments from "./finance/grids/JobPayments";
import DashboardNav from "app/atomics/organisms/DashboardNav";
import PageWrapper from "app/atomics/atoms/PageWrapper";
import Breadcrumbs from "app/atomics/atoms/Breadcrumbs";
import ContentWrapper from "app/atomics/atoms/ContentWrapper";
import { isArrayNotEmpty } from "app/c1utils/utility";
import DashboardCredit from "./finance/DashboardCredit";
import { DashboardTypes } from "app/c1utils/const";
import DriverAvailability from "./grid/DriverAvailability";
import TruckTrackingDetails from "../../../administrations/operations/truckTracking/TruckTrackingDetails";
/** Dashboard for CO/FF. */
const DashboardPanel = () => {
  const history = useHistory();

  const { user } = useAuth();

  const [carouselItems, setCarouselItems] = useState([]);
  const [refreshPage, setRefreshPage] = useState(0);
  const { isLoading, isFormSubmission, res, error, urlId, sendRequest } =
    useHttp();
  const [loading, setLoading] = useState(true);
  const [selectedId, setSelectedId] = useState(0);
  const [filterStatus, setFilterStatus] = useState([]);
  const [cardItems, setCardItems] = useState([]);
  const [creditApply, setCreditApply] = useState(null);
  const workBenchSelectedId = "dashboardTruckingJobs";

  useEffect(() => {
    sendRequest(
      `/api/v1/clickargo/clictruck/accnconfig/edashboard`,
      "getDashboard"
    );
  }, []);

  useEffect(() => {
    if (!isLoading && !error && res) {
      //setLoading(isLoading);
      if (urlId === "getDashboard") {
        setCardItems(res.data);
        prepareListComponent(res.data);
        sendRequest(
          `/api/v1/clickargo/clictruck/accnconfig/ecredit`,
          "getCredit"
        );
        //setTimeout(()=> sendRequest(`/api/v1/clickargo/clictruck/accnconfig/ecredit`,"getCredit"), 2000);
      } else if (urlId === "getCredit") {
        //only unload if credit is done
        setLoading(isLoading);
        setCreditApply({ ...res.data });
      }
    }

    // eslint-disable-next-line
  }, [isLoading, error, res, isFormSubmission, urlId]);

  //monitor carouselItems
  useEffect(() => {
    let isMissing = false;
    carouselItems &&
      carouselItems.map((file) => {
        if (file && file?.id === selectedId) {
          if (typeof file.listComponent === "undefined") {
            isMissing = true;
          }
        }

        return null;
      });

    //if this returns true, redirect
    if (isMissing) {
      history.push("/");
    }
  }, [carouselItems]);

  const prepareListComponent = (data) => {
    let storagedId = sessionStorage.getItem(workBenchSelectedId);
    let carouselItemsComponent = data?.map((app, i) => {
      let state = "inactive";
      if (!storagedId) {
        if (i === 0) {
          state = "active";
        }
      } else if (storagedId && parseInt(storagedId) === app.id) {
        state = "active";
        setSelectedId(app.id);
      }
      let newApp = {
        ...app,
        id: app.id,
        img: `${app.dbType}.png`,
        state,
      };

      switch (app.dbType) {
        case DashboardTypes.VERIFIED_JOBS.code: {
          newApp.listComponent = ({
            roleId,
            filterStatus,
            onFilterChipClose,
            onFilterChange,
          }) => {
            return (
              <InvoiceApprovals
                roleId={roleId}
                filterStatus={filterStatus}
                key={app.dbType}
                prevPath={history.location.pathname}
                onFilterChipClose={onFilterChipClose}
                onFilterChange={onFilterChange}
              />
            );
          };
          break;
        }
        case DashboardTypes.TRACKING.code: {
          newApp.listComponent = ({
              roleId,
              filterStatus,
              onFilterChipClose,
              onFilterChange,
            }) => {
            return (
                <TruckTrackingDetails
                    roleId={roleId}
                    filterStatus={filterStatus}
                    key={app.dbType}
                    prevPath={history.location.pathname}
                    onFilterChipClose={onFilterChipClose}
                    onFilterChange={onFilterChange}
                />
            );
          };
          break;
        }
        case DashboardTypes.TRUCK_JOBS.code: {
          newApp.listComponent = ({
            roleId,
            filterStatus,
            onFilterChipClose,
            onFilterChange,
          }) => {
            return (
              <TruckJobList
                roleId={roleId}
                filterStatus={filterStatus}
                key={app.dbType}
                prevPath={history.location.pathname}
                onFilterChipClose={onFilterChipClose}
                onFilterChange={onFilterChange}
              />
            );
          };
          break;
        }
        case DashboardTypes.BILLED_JOBS.code: {
          newApp.listComponent = ({
            roleId,
            filterStatus,
            onFilterChipClose,
            onFilterChange,
          }) => {
            return (
              <BilledJobs
                roleId={roleId}
                filterStatus={filterStatus}
                key={app.dbType}
                prevPath={history.location.pathname}
                onFilterChipClose={onFilterChipClose}
                onFilterChange={onFilterChange}
              />
            );
          };
          break;
        }
        case DashboardTypes.APPROVED_JOBS.code: {
          newApp.listComponent = ({
            roleId,
            filterStatus,
            onFilterChipClose,
            onFilterChange,
          }) => {
            return (
              <PendingPayments
                roleId={roleId}
                filterStatus={filterStatus}
                key={app.dbType}
                prevPath={history.location.pathname}
                onFilterChipClose={onFilterChipClose}
                onFilterChange={onFilterChange}
              />
            );
          };
          break;
        }
        case DashboardTypes.JOB_PAYMENTS.code: {
          newApp.listComponent = ({
            roleId,
            filterStatus,
            onFilterChipClose,
            onFilterChange,
          }) => {
            return (
              <JobPayments
                roleId={roleId}
                filterStatus={filterStatus}
                key={app.dbType}
                prevPath={history.location.pathname}
                onFilterChipClose={onFilterChipClose}
                onFilterChange={onFilterChange}
              />
            );
          };
          break;
        }
        case DashboardTypes.DRIVER_AVAILABILITY.code: {
          newApp.listComponent = ({
            roleId,
            filterStatus,
            onFilterChipClose,
            onFilterChange,
          }) => {
            return (
              <DriverAvailability
                roleId={roleId}
                filterStatus={filterStatus}
                key={app.dbType}
                prevPath={history.location.pathname}
                onFilterChipClose={onFilterChipClose}
                onFilterChange={onFilterChange}
              />
            );
          };
          break;
        }
        default:
          break;
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
  };

  const handleFilterChipClose = (index, removedFilter, filterList) => {
    let indx = filterStatus.indexOf(removedFilter);
    filterStatus.splice(indx, 1);
    setFilterStatus(filterStatus);
  };

  const handleFilterChange = (
    changedColumn,
    filterList,
    type,
    changedColumnIndex,
    displayData
  ) => {
    if (type === "reset") setFilterStatus([]);
    else {
      //To append the status selected from workbench card, and at the same time to also not include
      //the status when the workbench card folder is clicked instead of status filtering - which causes an extra blank chip
      if (
        changedColumn &&
        (changedColumn.includes("status") ||
          changedColumn.includes("Status")) &&
        filterStatus &&
        isArrayNotEmpty(filterStatus)
      )
        setFilterStatus([...filterStatus, filterList[changedColumnIndex]]);
    }
  };

  return loading ? (
    <MatxLoading />
  ) : (
    <PageWrapper>
      <Breadcrumbs segments={[{ name: "Dashboard" }]} />
      {creditApply?.isFinanced && (
        <Box component={`div`} sx={{ marginBottom: 10 }}>
          <DashboardCredit opm={creditApply?.isOpm} />
        </Box>
      )}
      <DashboardNav
        data={cardItems}
        handleClick={handleClickStatus}
        activeId={selectedId}
      />
      <ContentWrapper>
        {carouselItems &&
          carouselItems.map((file) => {
            if (file && file?.id === selectedId) {
              if (typeof file.listComponent !== "undefined")
                return file.listComponent({
                  roleId: user.role,
                  filterStatus,
                  setFilterStatus,
                  onFilterChipClose: handleFilterChipClose,
                  onFilterChange: handleFilterChange,
                });
            }

            return null;
          })}
      </ContentWrapper>
    </PageWrapper>
  );
};

export default withErrorHandler(DashboardPanel);
