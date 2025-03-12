import { Badge } from "@material-ui/core";
import Divider from "@material-ui/core/Divider";
import Grid from "@material-ui/core/Grid";
import Tab from "@material-ui/core/Tab";
import Tabs from "@material-ui/core/Tabs";
import { Comment, LiveHelp } from "@material-ui/icons";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";

import { Status } from "app/c1utils/const";
import { useStyles } from "app/c1utils/styles";

import C1Query from "./C1Query"
import C1Remark from "./C1Remark"

const C1QueryRemark = ({
    handleSubmit,
    data,
    inputData,
    appId,
    headerInfo,
    handleInputChange,
    handleValidate,
    viewType,
    subTabs,
    isSubmitting,
    sectionDb,
    isEndWorkflow,
    isDisabled,
    hasQueryRemarks,
    activeTabIndex,
    getUpdateRead,
    refresh,
    getRefreshQuery,
    props }) => {

    const gridClass = useStyles();

    const [tabIndex, setTabIndex] = useState(activeTabIndex ? activeTabIndex : 0);
    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };

    const { t } = useTranslation(["common"]);

    return (
        <React.Fragment>
            <Tabs
                className="mt-4"
                value={tabIndex}
                onChange={handleTabChange}
                variant="scrollable"
                indicatorColor="primary"
                textColor="primary">
                <Tab className="capitalize" value={0} label={t("common:queries.title")} key={0}
                    icon={(hasQueryRemarks?.queries > 0) ? <Badge color="error" badgeContent={hasQueryRemarks?.queries} max={100}><LiveHelp /></Badge> : <LiveHelp />} />
                <Tab className="capitalize" value={1} label={t("common:remarks.title")} key={1}
                    icon={(hasQueryRemarks?.remarks) ? <Badge color="error" badgeContent={hasQueryRemarks?.remarks} max={100}><Comment /></Badge> : <Comment />} />
            </Tabs>

            <Divider className="mb-6" />
            <Grid container spacing={1} alignItems="center" className={gridClass.gridContainer}>
                <Grid container item direction="column">
                    <Grid item xs={12}>

                        {tabIndex === 0 && <C1Query handleSubmit={handleSubmit}
                            inputData={inputData}
                            handleInputChange={handleInputChange}
                            handleValidate={handleValidate}
                            sectionDb={sectionDb}
                            appId={appId}
                            getRefreshQuery={getRefreshQuery}
                            isEndWorkflow={isEndWorkflow}
                            appStatus={inputData?.pediApps?.pediMstAppStatus?.appStatusId}
                            viewType={viewType}
                            isSubmitting={isSubmitting}
                            isDisabled={isDisabled}
                            props={props} />}

                        {tabIndex === 1 && <C1Remark handleSubmit={handleSubmit}
                            data={data} inputData={inputData} headerInfo={headerInfo}
                            handleInputChange={handleInputChange}
                            handleValidate={handleValidate}
                            viewType={viewType}
                            getUpdateRead={getUpdateRead}
                            refresh={refresh}
                            isSubmitting={isSubmitting}
                            isEndWorkflow={isEndWorkflow}
                            sectionDb={sectionDb}
                            appId={appId}
                            isDisabled={isDisabled}
                            props={props}
                            isRetOrRej={Status.RET.code === inputData?.pediApps?.pediMstAppStatus?.appStatusId
                                || Status.RET.code === inputData?.pediApps?.pediMstAppStatus?.appStatusId}
                        />}
                    </Grid>
                </Grid>

            </Grid>
        </React.Fragment>

    );
};

export default C1QueryRemark;