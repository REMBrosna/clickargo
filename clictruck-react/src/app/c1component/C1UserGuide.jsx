import React from "react";
import { Button, Typography } from "@material-ui/core";
import Grid from "@material-ui/core/Grid";
import C1OutlinedDiv from "./C1OutlinedDiv";

const C1UserGuide = ({ information, onHide, locale }) => {
  return (
    <React.Fragment>
      <C1OutlinedDiv>
        <Grid container>
          <Grid item xs={10}>
            <Typography variant="caption">{information}</Typography>
          </Grid>
          <Grid
            container
            item
            xs={2}
            direction="row"
            justifyContent="flex-end"
            alignItems="flex-start"
          >
            <Button color="primary" onClick={onHide}>
              <Typography variant="caption">
                {locale ? locale("common:common.guide.btnHide") : "Hide"}
              </Typography>
            </Button>
          </Grid>
        </Grid>
      </C1OutlinedDiv>
    </React.Fragment>
  );
};

export default C1UserGuide;
