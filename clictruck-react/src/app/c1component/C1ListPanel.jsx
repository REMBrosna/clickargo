import React from "react";
import { Breadcrumb } from "matx";
import Grid from "@material-ui/core/Grid";
import Divider from "@material-ui/core/Divider";
import PropTypes from "prop-types";
import C1TabInfoContainer from "./C1TabInfoContainer";
import { Card } from "@material-ui/core";

const C1ListPanel = ({
  routeSegments,
  showSelection,
  isOverFlow = true,
  guideId,
  title,
  elAction,
  isFullHeight = false,
  children,
}) => {
  let selectComponent = showSelection || null;
  return (
    <div className={"m-sm-30" + (isFullHeight ? " FullHeight" : "")}>
      <div className="mb-sm-30">
        <Breadcrumb routeSegments={routeSegments} />
      </div>
      <div
        className={
          (isOverFlow ? "overflow-auto" : "") +
          (isFullHeight ? " FullHeight" : "")
        }
      >
        <div className={"min-w-750" + (isFullHeight ? " FullHeight" : "")}>
          <Card elevation={3} className={isFullHeight ? " FullHeight" : ""}>
            <Grid
              container
              spacing={0}
              className={isFullHeight ? " FullHeight" : ""}
            >
              {selectComponent && (
                <Grid item xs={12} style={{ border: "0px solid red" }}>
                  <Grid
                    container
                    item
                    direction="row"
                    justifyContent="flex-end"
                    alignItems="flex-end"
                  >
                    <Grid item xs={6}>
                      {selectComponent}
                    </Grid>
                  </Grid>
                </Grid>
              )}
              {selectComponent && (
                <Grid item xs={12}>
                  <Divider />
                </Grid>
              )}
              {guideId && title !== undefined ? (
                <Grid container spacing={0} style={{ marginTop: "30px" }}>
                  <Grid item xs={12}>
                    <C1TabInfoContainer
                      guideId={guideId}
                      title={title}
                      elAction={elAction}
                    />
                  </Grid>
                </Grid>
              ) : null}
              <Grid item xs={12}>
                <Grid
                  container
                  style={{
                    paddingLeft: 10,
                    paddingRight: 10,
                  }}
                  justifyContent="center"
                  className={isFullHeight ? " FullHeight" : ""}
                >
                  <Grid item xs={12}>
                    {children}
                  </Grid>
                </Grid>
              </Grid>
            </Grid>
          </Card>
        </div>
      </div>
    </div>
  );
};

C1ListPanel.propTypes = {
  showSelection: PropTypes.element,
  routeSegments: PropTypes.array,
  children: PropTypes.any,
  guideId: PropTypes.string,
};

export default C1ListPanel;
