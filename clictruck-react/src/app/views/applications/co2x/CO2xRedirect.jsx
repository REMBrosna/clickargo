import React from "react";
import useHttp from "app/c1hooks/http";
import { useEffect, useState } from "react";
import {
  Box,
  Button,
  Card,
  Divider,
  Grid,
  Typography,
} from "@material-ui/core";
import clsx from "clsx";

import { useHistory } from "react-router-dom/cjs/react-router-dom";
import { MatxLoading } from "matx";

export default function CO2xRedirect() {
  const { isLoading, res, error, urlId, sendRequest } = useHttp();
  const history = useHistory();

  const [loading, setLoading] = useState(true);
  const [response, setResponse] = useState({});
  const [errorNoAccess, setErrorNoAccess] = useState("");

  //check if the principal account exist in co2x
  useEffect(() => {
    sendRequest(
      "/api/v1/clickargo/clictruck/admin/misc/co2x/check",
      "checkEligibility",
      "get"
    );
  }, []);

  function goToExternalURL(email, pwd, url) {
    const form = document.createElement("form");
    form.method = "POST";
    form.action = url;

    // Sample 1
    const hiddenEmail = document.createElement("input");
    hiddenEmail.type = "hidden";
    hiddenEmail.name = "email";
    hiddenEmail.value = email;
    form.appendChild(hiddenEmail);

    // Sample 2
    const hiddenPwd = document.createElement("input");
    hiddenPwd.type = "hidden";
    hiddenPwd.name = "password";
    hiddenPwd.value = pwd;
    form.appendChild(hiddenPwd);

    form.target = "_blank";
    document.body.appendChild(form);

    form.submit();
  }

  useEffect(() => {
    if (!isLoading && !error && res) {
      switch (urlId) {
        case "checkEligibility": {
          setLoading(false);
          if (res?.data?.data) {
            goToExternalURL(
              res?.data?.data?.co2xUid,
              res?.data?.data?.co2xPwd,
              res?.data?.data?.ssoUrl
            );
            history.push("/");
          } else {
            setErrorNoAccess("You are not subscribed to CO2 Connect");
          }

          break;
        }
        default:
          break;
      }
    }

    if (error) setLoading(false);

    // eslint-disable-next-line
  }, [urlId, res, isLoading, error]);

  return loading ? (
    <MatxLoading />
  ) : (
    errorNoAccess && (
      <Box className={clsx("flex justify-center items-center")}>
        <Card
          style={{
            maxWidth: 1000,
            borderRadius: 12,
            // margin: "1rem",
            marginTop: 150,
          }}
        >
          <Grid container alignItems="center" justifyContent="center">
            <Grid item lg={12} md={12} sm={12} xs={12}>
              <Box className="p-8 h-full bg-light-gray relative">
                <Box className="m-2" fontWeight="fontWeightMedium">
                  <Typography variant="h5">Invalid Access</Typography>
                </Box>
                <Divider />
                <br />
                <Box className="m-2">
                  You are not subscribed to CO2 Connect.
                  <br />
                  <br />
                  Please contact administrator to subscribe.
                  <br />
                  <br />
                </Box>
                <br />
                <br />
                <br />
                <br />
              </Box>
            </Grid>
          </Grid>
        </Card>
      </Box>
    )
  );
}
