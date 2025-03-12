import React, { useEffect, useState } from "react";
import { Divider, Grid, Paper, Tabs } from "@material-ui/core";
import { WorkOutlineOutlined } from "@material-ui/icons";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";
import TabLabel from "app/portedicomponent/TabLabel";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";
import { tabScroll } from "app/c1utils/styles";
import CreditLineDetails from "./tabs/CreditLineDetails";
import history from "history.js";
import useHttp from "app/c1hooks/http";

const CreditHistory = (props) => {
  const [tabIndex, setTabIndex] = useState(0);
  const [creditData, setCreditData] = useState(history?.location?.state);
  const { isLoading, res, error, urlId, sendRequest } = useHttp();

  const tabList = [
    { text: "Credit Line Details", icon: <WorkOutlineOutlined /> },
  ];

  const handleTabChange = (e, value) => {
    setTabIndex(value);
  };

  //   useEffect(() => {
  //     if (!isLoading && !error && res) {
  //       switch (urlId) {
  //         case "getCreditData":
  //           setCreditData({ ...creditData, credit: res?.data });
  //           setLoading(false);
  //           break;

  //         default:
  //           break;
  //       }
  //     }

  //     if (error) {
  //       setLoading(false);
  //     }
  //   }, [isLoading, error, res, urlId]);

  return (
    <>
      <C1FormDetailsPanel
        breadcrumbs={[{ name: "View Credit Line" }]}
        title={`View Credit Line`}
        formButtons={
          <C1FormButtons
            options={{
              back: {
                show: true,
                eventHandler: () => history.goBack(),
              },
            }}
          />
        }
      >
        {() => (
          <>
            <Grid container spacing={3}>
              <Grid item xs={12}>
                <Paper>
                  <Tabs
                    className="mt-4"
                    value={tabIndex}
                    onChange={handleTabChange}
                    indicatorColor="primary"
                    textColor="primary"
                    variant="scrollable"
                    scrollButtons="auto"
                  >
                    {tabList &&
                      tabList.map((item, ind) => {
                        return (
                          <TabsWrapper
                            className="capitalize"
                            value={ind}
                            disabled={item.disabled}
                            label={
                              <TabLabel
                                viewType={`vuew`}
                                invalidTabs={true}
                                tab={item}
                              />
                            }
                            key={ind}
                            icon={item.icon}
                            {...tabScroll(ind)}
                          />
                        );
                      })}
                  </Tabs>
                  <Divider className="mb-6" />
                  {tabIndex === 0 && (
                    <C1TabInfoContainer
                      guideId="clicdo.doi.co.jobs.tabs.details"
                      title="empty"
                      guideAlign="right"
                      open={false}
                    >
                      <CreditLineDetails data={creditData} />
                    </C1TabInfoContainer>
                  )}
                </Paper>
              </Grid>
            </Grid>
          </>
        )}
      </C1FormDetailsPanel>
    </>
  );
};

export default CreditHistory;
