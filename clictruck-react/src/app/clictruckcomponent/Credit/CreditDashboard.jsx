import {
  Box,
  CircularProgress,
  Divider,
  Grid,
  Switch,
  Typography,
  makeStyles,
} from "@material-ui/core";
import React from "react";
import CreditItem from "./items/CreditItem";
import { useStyles } from "app/c1utils/styles";
import {
  HistoryOutlined,
  LocalAtmOutlined,
  RefreshOutlined,
} from "@material-ui/icons";
import {
  deepOrange,
  lightBlue,
  lightGreen,
  orange,
} from "@material-ui/core/colors";
import PropTypes from "prop-types";
import history from "history.js";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import { useTranslation } from "react-i18next";

const CreditDashboard = (props) => {
  const {
    data,
    origin,
    historyRoute,
    handleRefresh,
    isRefresh,
    showCap,
    isCap,
    handleChangeCap,
    ...other
  } = props;
  const { t } = useTranslation(["button"]);

  const classes = useStyles();

  const useStyless = makeStyles((theme) => ({
    title: {
      fontSize: 15,
      color: "#000",
    },
    icon: {
      color: "#000",
    },
    monthlyBox: {
      display: "flex",
      flexDirection: "column",
      justifyContent: "center",
      alignItems: "center",
      marginTop: "-4px",
    },
    monthlyLabel: {
      fontSize: 10,
      fontWeight: 500,
    },
    switchCap: {
      marginBottom: "-3px",
    },
  }));

  const classNames = useStyless();

  return (
    <>
      <Box component="div" {...other}>
        <Grid container justifyContent="space-between" alignItems="flex-end">
          <Grid item>
            <Grid
              container
              item
              justifyContent="flex-start"
              alignItems="center"
            >
              <LocalAtmOutlined
                fontSize={`small`}
                className={classNames.icon}
              />
              <Box sx={{ marginRight: 1 }} />
              <Typography variant="h6" className={classNames.title}>
                Credit Status
              </Typography>
            </Grid>
          </Grid>
          <Grid item>
            <Grid container justifyContent="flex-end">
              {showCap && (
                <Grid item>
                  <Box component={`div`} className={classNames.monthlyBox}>
                    <Switch
                      color="primary"
                      checked={isCap}
                      onChange={handleChangeCap}
                      className={classNames.switchCap}
                    />
                    <div className={classNames.monthlyLabel}>MONTHLY</div>
                  </Box>
                </Grid>
              )}
              <Grid item>
                <C1LabeledIconButton
                  tooltip={t("buttons:refresh")}
                  label={t("buttons:refresh")}
                  disabled={isRefresh}
                  action={handleRefresh}
                >
                  {isRefresh ? (
                    <CircularProgress color="inherit" size={23} />
                  ) : (
                    <RefreshOutlined color="primary" />
                  )}
                </C1LabeledIconButton>
              </Grid>
              <Grid item>
                <C1LabeledIconButton
                  tooltip={t("buttons:history")}
                  label={t("buttons:history")}
                  action={() => history.push(historyRoute, data)}
                >
                  <HistoryOutlined color="primary" />
                </C1LabeledIconButton>
              </Grid>
            </Grid>
          </Grid>
        </Grid>
        <Divider sx={{ width: "100%" }} className={classes.divider} />
        <Box sx={{ marginY: 2 }} />
        <Grid
          container
          justifyContent="space-between"
          alignItems="center"
          spacing={5}
        >
          <Grid item md={3} xs={12}>
            <CreditItem
              color={lightBlue[300]}
              category={isCap ? `Monthly Cap` : `Credit Line`}
              amount={isCap ? data?.crTxnCap : data?.tckCreditSummary?.crsAmt}
            />
          </Grid>
          <Grid item md={3} xs={12}>
            <CreditItem
              color={orange[300]}
              category="Reserve"
              amount={data?.tckCreditSummary?.crsReserve}
            />
          </Grid>
          <Grid item md={3} xs={12}>
            <CreditItem
              color={deepOrange[300]}
              category="Utilized"
              amount={data?.tckCreditSummary?.crsUtilized}
            />
          </Grid>
          <Grid item md={3} xs={12}>
            <CreditItem
              color={lightGreen[300]}
              category="Balance"
              amount={data?.tckCreditSummary?.crsBalance}
            />
          </Grid>
        </Grid>
      </Box>
    </>
  );
};

CreditDashboard.propTypes = {
  data: PropTypes.object,
  origin: PropTypes.string,
  historyRoute: PropTypes.string,
  handleRefresh: PropTypes.func,
  isRefresh: PropTypes.bool,
  isCap: PropTypes.bool,
  showCap: PropTypes.bool,
  handleChangeCap: PropTypes.func,
};

export default CreditDashboard;
