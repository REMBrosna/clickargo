import React, { useState, useEffect } from "react";
import { Icon, IconButton, Hidden, MenuItem, Avatar } from "@material-ui/core";
import { MatxMenu, MatxToolbarMenu } from "matx";
import { setLayoutSettings } from "app/redux/actions/LayoutActions";
import { useDispatch, useSelector } from "react-redux";
import LanguageSelector from "app/MatxLayout/SharedCompoents/LanguageSelector";
import { useTranslation } from 'react-i18next';
import { makeStyles } from "@material-ui/core/styles";
import clsx from "clsx";
import { merge } from "lodash";
import mainLogo from './mainLogo.png';
import { Link } from "react-router-dom";
import LockOpenIcon from '@material-ui/icons/LockOpen';
import useAuth from "app/hooks/useAuth";
import history from "history.js";


const useStyles = makeStyles(({ palette, ...theme }) => ({
  root: {
    backgroundColor: '#566bf1',
    borderColor: palette.divider,
    display: "table",
    height: "var(--topbar-height)",
    borderBottom: "1px solid transparent",
    paddingTop: "1rem",
    paddingBottom: "1rem",
    zIndex: 98,
    paddingLeft: "1.75rem",
    [theme.breakpoints.down("sm")]: {
      paddingLeft: "1rem",
    },
  },
  brandText: {
    color: palette.primary.contrastText,
  },
  userMenu: {
    minWidth: 200,
    display: "flex",
    alignItems: "center",
    cursor: "pointer",
    borderRadius: 24,
    padding: 4,
    "& span": {
      margin: "0 8px",
      // color: palette.text.secondary
    },
    "&:hover": {
      backgroundColor: palette.action.hover,
    },
  },
  menuItem: {
    minWidth: 185,
  },
}));

const Layout2Topbar = () => {
  const classes = useStyles();
  const dispatch = useDispatch();
  const { settings } = useSelector(({ layout }) => layout);
  const { logout, user } = useAuth();
  const [isMobile, setIsMobile] = useState(false);

  const { t } = useTranslation(["session"]);


  const updateSidebarMode = (sidebarSettings) => {
    dispatch(
      setLayoutSettings(
        merge({}, settings, {
          layout2Settings: {
            leftSidebar: {
              ...sidebarSettings,
            },
          },
        })
      )
    );
  };

  useEffect(() => {

    if (window.innerWidth < 760) {
      setIsMobile(true);
    }

    window.addEventListener('resize', () => {
      if (window.innerWidth < 760) {
        setIsMobile(true);
      } else {
        setIsMobile(false);
      }
    });

  }, []);

  const handleSidebarToggle = () => {
    let { layout2Settings } = settings;

    let mode =
      layout2Settings.leftSidebar.mode === "close" ? "mobile" : "close";

    updateSidebarMode({ mode });
  };

  const handleSignOut = async () => {
    try {
      await logout();
      history.push({
        pathname: "/session/signin"
      });
    } catch (e) {
      console.log(e);
    }
  };

  let elUserId = <div className={classes.userMenu}>
    <span className={classes.brandText}>
      {t("session:login.label.greetings")} <strong>{user?.username}</strong>
    </span>
    <span>
      <Avatar className="cursor-pointer" src={`${process.env.PUBLIC_URL}/assets/images/avatars/001-man.svg`} />
    </span>
  </div>;

  return (
    <div className={clsx("relative w-full", classes.root)}>
      <div className="flex justify-between items-center h-full">
        <div className="flex items-center h-full">
          <img
              style={{
                width : '115px',
                height:'75px !importance'
              }}
            src={mainLogo}
            alt=""
          />
          <span className={clsx("font-medium text-24 mx-4", classes.brandText)}>
            Student Management
          </span>
        </div>
        <div className="mx-auto"></div>
        <div className="flex items-center">
          <MatxToolbarMenu offsetTop="80px">

            <LanguageSelector />
            <MatxMenu menuButton={
              isMobile ? <Avatar className="cursor-pointer mx-2" src={`${process.env.PUBLIC_URL}/assets/images/avatars/001-man.svg`} /> : elUserId
            }
            >
              <MenuItem className={classes.menuItem}>
                <Link className={classes.menuItem} to="/">
                  <Icon> home </Icon>
                  <span className="pl-4"> Home </span>
                </Link>
              </MenuItem>
              <MenuItem onClick={() => history.push(`/student/applicationStudent/profile`)} className={classes.menuItem}>
                {/* <Link className={classes.menuItem} to={`/manageUsers/user/edit/profile`}> */}
                <Icon> person </Icon>
                <span className="pl-4"> Profile </span>
                {/* </Link> */}
              </MenuItem>
              <MenuItem onClick={() => history.push(`/student/applicationStudent/updatePassword`)} className={classes.menuItem}>
                {/* <Link
                                        className={classes.menuItem}
                                        to={"/manageUsers/updatePassword/"}> */}
                <LockOpenIcon />
                <span className="pl-4"> Change password </span>
                {/* </Link> */}
              </MenuItem>
              <MenuItem onClick={handleSignOut} className={classes.menuItem}>
                <Icon> power_settings_new </Icon>
                <span className="pl-4"> Logout </span>
              </MenuItem>
            </MatxMenu>
          </MatxToolbarMenu>

          <Hidden mdUp>
            <IconButton className="text-white" onClick={handleSidebarToggle}>
              <Icon>menu</Icon>
            </IconButton>
          </Hidden>
        </div>
      </div>
    </div >
  );
};

export default Layout2Topbar;
