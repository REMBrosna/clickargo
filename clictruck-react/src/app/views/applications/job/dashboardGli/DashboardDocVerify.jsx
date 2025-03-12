import React, { useEffect, useState } from "react";
import useHttp from "app/c1hooks/http";
import { MatxLoading } from "matx";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import DashboardNav from "app/atomics/organisms/DashboardNav";
import PageWrapper from "app/atomics/atoms/PageWrapper";
import Breadcrumbs from "app/atomics/atoms/Breadcrumbs";
import ContentWrapper from "app/atomics/atoms/ContentWrapper";
import DocumentVerifications from "./grid/DocumentVerifications";
import useAuth from "app/hooks/useAuth";

const DashboardDocVerify = () => {

    const { user } = useAuth();

    const { isLoading, res, error, sendRequest } = useHttp();
    const [loading, setLoading] = useState(true);
    const [selectedId, setSelectedId] = useState(0);
    const [cardItems, setCardItems] = useState([]);


    useEffect(() => {
        sendRequest(`/api/v1/clickargo/clictruck/dashboard/docbillverification`);
    }, [sendRequest, user]);

    useEffect(() => {
        if (!isLoading && !error && res) {
            setLoading(isLoading);
            setCardItems(res.data)
        }
    }, [isLoading, error, res]);

    return loading ? <MatxLoading /> :
        (
            <PageWrapper>
                <Breadcrumbs segments={[{ name: 'Document Verifications' }]} />
                <DashboardNav data={cardItems} handleClick={(e) => console.log(e)} activeId={selectedId} />
                <ContentWrapper>
                    <DocumentVerifications />
                </ContentWrapper>
            </PageWrapper>
        )
};

export default withErrorHandler(DashboardDocVerify);