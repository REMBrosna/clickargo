import React, { useState, useEffect } from "react";
import {
    Grid,
    Paper,
    Tabs,
    Tab,
    Divider
} from "@material-ui/core";

import { useParams, useHistory } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { MatxLoading } from "matx";

import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1FormButtons from "app/c1component/C1FormButtons";

import { auditTab } from "app/c1utils/const";
import useHttp from "app/c1hooks/http";
import ExceptionDetail from "./ExceptionDetail";

const ExceptionForm = () => {

    let { viewType, id } = useParams();
    const { t } = useTranslation(['administration']);
    let history = useHistory();

    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();
    const [loading, setLoading] = useState(false);
    const [inputData, setInputData] = useState({});

    useEffect(() => {
        setLoading(false);
        if (viewType !== 'new') {
            sendRequest("/api/co/common/entity/exceptions/" + id, "getException", "get", {});
        }

        // eslint-disable-next-line
    }, [id, viewType]);


    useEffect(() => {
        if (!isLoading && !error && res) {
            setLoading(isLoading);

            setInputData({ ...inputData, ...res.data });
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [urlId, isLoading, isFormSubmission, res, validation, error]);


    let formButtons = <C1FormButtons options={{
        back: {
            show: true,
            eventHandler: () => history.goBack()
        },
    }} />;

    let bcLabel = t("exception.details.view");

    return (
        loading ? <MatxLoading /> : <React.Fragment>
            <C1FormDetailsPanel
                breadcrumbs={[
                    { name: t("exception.list.routeSegment"), path: "/administrations/exception/list" },
                    { name: bcLabel },
                ]}

                title={bcLabel}
                formButtons={formButtons}
                initialValues={{ ...inputData }}
                values={{ ...inputData }}
                isLoading={loading} >
                {(props) => (
                    <Grid container spacing={3}>
                        <Grid item xs={12}>
                            <Paper className="p-3">
                                <Tabs
                                    className="mt-4"
                                    value={0}
                                    indicatorColor="primary"
                                    textColor="primary"
                                >
                                    {auditTab.map((item, ind) => (
                                        <Tab className="capitalize" value={ind} label={t(item.text)} key={ind} icon={item.icon} />
                                    ))}
                                </Tabs>
                                <Divider className="mb-6" />
                                {<ExceptionDetail
                                    data={inputData.id}
                                    viewType={viewType}
                                    isSubmitting={loading}
                                    locale={t}
                                />}
                            </Paper>
                        </Grid>
                    </Grid>
                )}
            </C1FormDetailsPanel>
        </React.Fragment>
    );
};


export default withErrorHandler(ExceptionForm);