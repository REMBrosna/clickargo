import React, {useEffect, useState} from "react";
import useHttp from "app/c1hooks/http";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import {Breadcrumb, MatxLoading} from "matx";

import DashboardNav from "app/atomics/organisms/DashboardNav";
import PageWrapper from "app/atomics/atoms/PageWrapper";
import {DashboardTypes} from "app/c1utils/const";
import {useTranslation} from "react-i18next";
import TrucksRentalDetails from "./tabs/TrucksRentalDetails";
import ContentWrapper from "../../../../atomics/atoms/ContentWrapper";
import {isArrayNotEmpty} from "../../../../c1utils/utility";
import LeaseApplicationList from "./tabs/LeaseApplicationList";
import {STATIC_RENTAL_DAS} from "../../../../c1utils/const";

const TrucksRentalPanel = () => {

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
        email: "",
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
    const [page, setPage] = useState({current: 0, allPage: 0, currentIndex: 0})
    const workBenchSelectedId = "dashboardTruckingJobs";

    useEffect(() => {
        sendRequest(`/api/v1/clickargo/clictruck/administrator/rental-params/providers`, "GET_RENTAL_PARAMS");
    }, [sendRequest]);

    useEffect(() => {
        if (!isLoading && !error && res) {
            switch (urlId) {
                case "GET_RENTAL_PARAMS":
                    setLoading(isLoading);
                    setRentalProvider(res.data);
                    const dashboardItems = STATIC_RENTAL_DAS?.map(val => {
                        const transStatistic = { ...val?.transStatistic };
                        if (val?.dbType === DashboardTypes.TRUCK_RENTAL.code) {
                            transStatistic.PROVIDERS = res.data?.length;
                        }
                        return {
                            ...val,
                            transStatistic
                        };
                    });
                    setCardItems(dashboardItems);
                    prepareListComponent(dashboardItems);
                    break;
                default:
                    break;
            }
        }
    }, [isLoading, error, res, isFormSubmission, urlId]);

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
            page={props?.page}
            setPage={props?.setPage}
        />
    );

    const prepareListComponent = (data) => {
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
                        case DashboardTypes.TRUCK_RENTAL.code:
                            return createListComponent(TrucksRentalDetails, app, props);
                        case DashboardTypes.LEASE_APPLICATION.code:
                            return createListComponent(LeaseApplicationList, app, props);
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
                <Breadcrumb
                    routeSegments={[{
                        name: t("administration:trucksRental.breadCrumbs.list"),
                    }]}
                />
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
                                page: page,
                                translate: t,
                                filterStatus,
                                setFilterStatus,
                                setPage: setPage,
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

export default withErrorHandler(TrucksRentalPanel);
