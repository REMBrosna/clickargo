import React, { useEffect, useState } from "react";
import useHttp from "app/c1hooks/http";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import { Breadcrumb, MatxLoading } from "matx";
import DashboardNav from "app/atomics/organisms/DashboardNav";
import PageWrapper from "app/atomics/atoms/PageWrapper";
import {DashboardTypes} from "app/c1utils/const";
import ContentWrapper from "../../../../atomics/atoms/ContentWrapper";
import { isArrayNotEmpty } from "../../../../c1utils/utility";
import {useTranslation} from "react-i18next";
import TruckTrackingDetails from "./TruckTrackingDetails";

const TrucksTrackingPanel = () => {

    const { user } = useAuth();
    const { t } = useTranslation(["administration", "common", "listing"]);
    const { isLoading, isFormSubmission, res, error, urlId, sendRequest } = useHttp();

    const defaultInputData = {
        provider: "",
        truck: "",
        lease: "",
        price: "",
        quantity: "",
        accn: user?.coreAccn?.accnId,
        name: user?.name,
        contact: user?.coreAccn?.accnContact?.contactTel,
        email: user?.coreAccn?.accnContact?.contactEmail,
        company: user?.coreAccn?.accnName,
    };

    const [inputData, setInputData] = useState(defaultInputData);
    const [validationErrors, setValidationErrors] = useState({});
    const [carouselItems, setCarouselItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedId, setSelectedId] = useState(0);
    const [filterStatus, setFilterStatus] = useState([]);
    const [cardItems, setCardItems] = useState([]);
    const [rentalProvider, setRentalProvider] = useState([]);
    const workBenchSelectedId = "dashboardTruckingJobs";

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
    const createListComponent = (Component, app, props) => (
        <Component
            roleId={props.roleId}
            filterStatus={props?.filterStatus}
            key={app?.dbType}
            errors={props?.errors}
            prevPath={props?.history?.location?.pathname}
            onFilterChipClose={props?.onFilterChipClose}
            translate={props?.translate}
            inputData={props?.inputData}
            setErrors={props?.setErrors}
            providers={props?.providers}
            setInputData={props?.setInputData}
            onFilterChange={props?.onFilterChange}
        />
    );

    const prepareListComponent = (data) => {
        console.log(data)
        const storagedId = sessionStorage.getItem(workBenchSelectedId);
        const carouselItemsComponent = data?.map((app, i) => {
            const state = !storagedId
                ? i === 0 ? "active" : "inactive"
                : parseInt(storagedId) === app.id ? "active" : "inactive";

            if (state === "active") {
                setSelectedId(app.id);
            }
            return {
                ...app,
                img: `${app.dbType}.png`,
                state,
                listComponent: (props) => {
                    switch (app.dbType) {
                        case DashboardTypes.TRUCK_TRACKING.code:
                            return createListComponent(TruckTrackingDetails, app, props);
                        default:
                            return null;
                    }
                }
            };
        });
        setCarouselItems(carouselItemsComponent);
    };

    const handleClickStatus = (e, docObjId, filterStatusParam) => {
        e.stopPropagation()
        if ("Open" !== filterStatusParam) {
            setFilterStatus([filterStatusParam]);
        } else {
            setFilterStatus();
        }
        sessionStorage.setItem(workBenchSelectedId, docObjId);
        setSelectedId(docObjId);
        setFilterStatus([]);
    };

    const handleFilterChange = (
        changedColumn,
        filterList,
        type,
        changedColumnIndex
    ) => {
        if (type === "reset") {
            setFilterStatus([]);
        } else if (
            changedColumn &&
            (changedColumn.includes("status") || changedColumn.includes("Status")) &&
            isArrayNotEmpty(filterStatus)
        ) {
            setFilterStatus([...filterStatus, filterList[changedColumnIndex]]);
        }
    };

    const handleFilterChipClose = (index, removedFilter, filterList) => {
        let indx = filterStatus.indexOf(removedFilter);
        filterStatus.splice(indx, 1);
        setFilterStatus(filterStatus);
    };

    return loading ? (
        <MatxLoading />
    ) : (
        <PageWrapper>
            <div className="mb-sm-30">
               <h6 style={{fontSize: "0.875rem"}}>Dashboard</h6>
            </div>
            <DashboardNav
                data={cardItems}
                activeId={selectedId}
                handleClick={handleClickStatus}
            />
            <ContentWrapper>
                {carouselItems && carouselItems?.map((file) => {
                    if (file && file?.id === selectedId) {
                        if (typeof file.listComponent !== "undefined")
                            return file.listComponent({
                                translate: t,
                                filterStatus,
                                setFilterStatus,
                                roleId: user.role,
                                inputData: inputData,
                                errors: validationErrors,
                                providers: rentalProvider,
                                setInputData: setInputData,
                                setErrors: setValidationErrors,
                                onFilterChange: handleFilterChange,
                                onFilterChipClose: handleFilterChipClose,
                            });
                    }
                    return null;
                })}
            </ContentWrapper>
        </PageWrapper>
    );
};

export default withErrorHandler(TrucksTrackingPanel);
