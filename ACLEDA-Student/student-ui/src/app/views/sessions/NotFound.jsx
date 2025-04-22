import React from "react";
import { Button } from "@material-ui/core";
import { Link } from "react-router-dom";
import clsx from "clsx";
import { Card, Grid, Typography } from "@material-ui/core";
import portEdiLogo from './login/ACLEDA.png';
import { makeStyles } from "@material-ui/core/styles";
import { useTranslation } from "react-i18next";

const useStyles = makeStyles(({ palette, ...theme }) => ({
  cardHolder: {
    background: "#1976d2",
  },
  card: {
    maxWidth: 800,
    borderRadius: 12,
    margin: "1rem",
  },
  buttonProgress: {
    position: "absolute",
    top: "50%",
    left: "50%",
    marginTop: -12,
    marginLeft: -12,
  },
}));

const NotFound = () => {

  const classes = useStyles();
  const { t } = useTranslation(["session"]);

  return (
    <div
      className={clsx(
        "flex justify-center items-center  min-h-full-screen",
        classes.cardHolder
      )}
    >
      <Card className={classes.card}>
        <Grid container>
          <Grid item lg={5} md={5} sm={5} xs={12}>
            <div className="p-8 flex justify-center items-center h-full">
              <img
                className="w-full"
                src={portEdiLogo}
                alt=""
              />
            </div>
          </Grid>
          <Grid item lg={7} md={7} sm={7} xs={12}>
            <div className="p-8 h-full bg-light-gray relative">
              <div align="center">
                <Typography variant="h1" align="center" style={{ fontWeight: 'bold' }}>404</Typography>
                <Typography variant="subtitle1" align="center">Oops!</Typography>
                <Typography variant="caption" align="center">{t("notFound.msg")}</Typography>
              </div>

              <div align="center">
                <Link to="/">
                  <Button className="capitalize" variant="contained" color="primary">
                    {t("notFound.btnBack")}
                  </Button>
                </Link>
              </div>
            </div>

          </Grid>
        </Grid>
      </Card>
    </div>
  );
};

export default NotFound;
