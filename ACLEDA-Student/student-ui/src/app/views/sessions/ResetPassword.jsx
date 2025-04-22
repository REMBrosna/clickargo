import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { Card, Grid, Button, CircularProgress } from "@material-ui/core";
import { TextValidator, ValidatorForm } from "react-material-ui-form-validator";
import { makeStyles } from "@material-ui/core/styles";
import clsx from "clsx";
import axios from "axios.js";
import { useLocation } from "react-router-dom";

const useStyles = makeStyles(({ palette, ...theme }) => ({
  cardHolder: {
    background: "#1A2038",
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
  const [inputData, setInputData] = useState({
    newPassword: "",
    confirmPassword: ""
  });
  const classes = useStyles();
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  const search = useLocation().search;
  const key = new URLSearchParams(search).get('key');


  const handleChange = ({ target: { name, value } }) => {
    setInputData({
      ...inputData,
      [name]: value,
    });
  };

  const handleFormSubmit = async (event) => {
    setLoading(true);
    try {
      const { data } = await axios.put("/api/pedi/manageusr/chgpwd", {...inputData, token: key});
      if (data) {
        setMessage(data.message);
        setLoading(false);
        setSuccess(true)
      }
    } catch (error) {
      setMessage(error?.err?.msg);
      setLoading(false);
    }
  };

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
                src="/assets/images/illustrations/dreamer.svg"
                alt=""
              />
            </div>
          </Grid>
          <Grid item lg={7} md={7} sm={7} xs={12}>
            <div className="p-8 h-full bg-light-gray relative">
              {!success && (
                <ValidatorForm onSubmit={handleFormSubmit} noValidate>
                  <TextValidator
                    required
                    disabled={loading}
                    className="mb-6 w-full"
                    variant="outlined"
                    label="New Password"
                    onChange={handleChange}
                    type="password"
                    name="newPassword"
                    size="small"
                    value={inputData.newPassword || ""}
                    validators={["required"]}
                    errorMessages={[
                      "this field is required"
                    ]}
                  />

                  <TextValidator
                    required
                    disabled={loading}
                    className="mb-6 w-full"
                    variant="outlined"
                    label="Confirm Password"
                    onChange={handleChange}
                    type="password"
                    name="confirmPassword"
                    size="small"
                    value={inputData.confirmPassword || ""}
                    validators={["required", "isPasswordMatch"]}
                    errorMessages={[
                      "this field is required",
                      "Passwords do not match"
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
                      Change Password
                    </Button>
                    {loading && (<CircularProgress size={24} className={classes.buttonProgress} />)}
                  </div>
                </ValidatorForm>
              )}

              {success && (
                <>
                  <p>{message}</p>

                  <div className="flex items-center">
                    <Link to="/session/signin">
                      <Button
                        variant="contained"
                        color="primary"
                        className="capitalize"
                      >
                          Back to Sign in
                      </Button>
                    </Link>
                  </div>
                </>
              )}
            </div>
          </Grid>
        </Grid>
      </Card>
    </div>
  );
};

export default ForgotPassword;
