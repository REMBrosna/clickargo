import React, { useState } from "react";
import { Link } from "react-router-dom";
import { Card, Grid, Button, CircularProgress } from "@material-ui/core";
import { TextValidator, ValidatorForm } from "react-material-ui-form-validator";
import { makeStyles } from "@material-ui/core/styles";
import clsx from "clsx";
import axios from "axios.js";
import { useTranslation } from "react-i18next";
import portEdiLogo from './login/ACLEDA.png';

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

const ForgotPassword = () => {

  const { t } = useTranslation(["session"]);

  const [state, setState] = useState({});
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  const classes = useStyles();

  const handleChange = ({ target: { name, value } }) => {
    setMessage("");
    setState({
      ...state,
      [name]: value,
    });
  };

  const handleFormSubmit = async (event) => {
    setLoading(true);
    try {
      const { data } = await axios.post("/reset-password", state);
      if (data) {
        setMessage(t(data.message));
        setLoading(false);
      }
    } catch (error) {
      setMessage(error?.err?.msg);
      setLoading(false);
    }
  };

  let { userNameOrEmail } = state;

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
              <ValidatorForm onSubmit={handleFormSubmit} noValidate>
                <TextValidator
                  required
                  disabled={loading}
                  className="mb-6 w-full"
                  variant="outlined"
                  label={t("session:resetPwd.email")}
                  onChange={handleChange}
                  type="email"
                  name="userNameOrEmail"
                  size="small"
                  value={userNameOrEmail || ""}
                  validators={["required", "isEmail"]}
                  errorMessages={[
                    "This field is required",
                    "Email is not valid",
                  ]}
                />

                {message && <p className="text-error">{message}</p>}

                <div className="flex items-center">
                  <Button
                    type="submit"
                    variant="contained"
                    color="primary"
                    disabled={loading}
                  >
                    {t("session:resetPwd.reset")}
                  </Button>
                  {loading && (<CircularProgress size={24} className={classes.buttonProgress} />)}

                  <span className="ml-4 mr-2">{t("session:login.label.or")}</span>
                  <Link to="/session/signin">
                    <Button className="capitalize">{t("session:resetPwd.signIn")}</Button>
                  </Link>
                </div>
              </ValidatorForm>
            </div>
          </Grid>
        </Grid>
      </Card>
    </div>
  );
};

export default ForgotPassword;
