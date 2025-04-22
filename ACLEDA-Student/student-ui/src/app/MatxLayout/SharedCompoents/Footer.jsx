import React, { useEffect, useState } from "react";
import { makeStyles, useTheme } from "@material-ui/core/styles";
import { Toolbar, AppBar } from "@material-ui/core";
import clsx from "clsx";
import { Link } from "react-router-dom";
import useAuth from "../../hooks/useAuth";
import useHttp from "app/c1hooks/http";

const useStyles = makeStyles(({ palette, ...theme }) => ({
  footer: {
    minHeight: "var(--topbar-height)",
    "@media (max-width: 499px)": {
      display: "table",
      width: "100%",
      minHeight: "auto",
      padding: "1rem 0",
      "& .container": {
        flexDirection: "column !important",
        "& a": {
          margin: "0 0 16px !important",
        },
      },
    },
  },
  appbar: {
    zIndex: 96,
  },
}));

const Footer = () => {
  const classes = useStyles();
  const { user } = useAuth();

  const { isLoading, res, error, urlId, sendRequest } = useHttp();
  const [openAnncmt, setOpenAnncmt] = useState(false);
  const [anncmtData, setAnncmtData] = useState([]);

  useEffect(() => {
    //if (user)
    //sendRequest("/api/pedi/announcement/private/CPEDI", "getAnnouncement", "get", {});
    // eslint-disable-next-line
  }, []);

  useEffect(() => {
    if (!isLoading && !error && res) {
      if (urlId === 'getAnnouncement') {
        setAnncmtData(res.data);
      }

      if (error)
        console.log("Error", error);
    }
    // eslint-disable-next-line
  }, [urlId, isLoading, res, error]);

  const handleAnnouncementClose = () => {
    setOpenAnncmt(false);
  }

  const handleOnClick = () => {
    setOpenAnncmt(true);
  }

  return (
    //<ThemeProvider theme={footerTheme}>
    <AppBar color="primary" position="static" className={classes.appbar}>
      <Toolbar className={clsx("flex items-center", classes.footer)}>
        <div className="flex items-center container w-full">
          {/* <a
              href="https://github.com/uilibrary/matx-react"
              target="_blank"
              className="mr-2"
              rel="noopener noreferrer"
            >
              <Button variant="contained">Download Free version</Button>
            </a> */}
          {/* <a href="https://material-ui.com/store/items/matx-pro-react-dashboard-template/">
              <Button variant="contained" color="secondary">
                Get MatX Pro
              </Button>
            </a> */}

          <div>
            <Link
              to={user == null ? "/session/faq/" : "/applications/info/faq"}>
              <span className="pl-4"> FAQ </span>
            </Link>
            <Link
              to={user == null ? "/session/contact/" : "/applications/info/contact"}>
              <span className="pl-4"> CONTACT </span>
            </Link>
            {user && Array.isArray(anncmtData) && anncmtData.length > 0
              && <span className="pl-4" style={{ cursor: 'pointer' }} onClick={handleOnClick}>ANNOUNCEMENT</span>}
          </div>

          <span className="m-auto"></span>
          <p className="m-0">
            Developed and Powered by Brosna
          </p>
        </div>
      </Toolbar>
    </AppBar >
    //  </ThemeProvider>
  );
};

export default Footer;
