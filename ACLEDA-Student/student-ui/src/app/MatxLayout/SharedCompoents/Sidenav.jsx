import React, { Fragment, useEffect } from "react";
import Scrollbar from "react-perfect-scrollbar";
import { useDispatch, useSelector } from "react-redux";

import { navigations } from "../../navigations";
import { MatxVerticalNav } from "matx";
import { setLayoutSettings } from "app/redux/actions/LayoutActions";
import { makeStyles } from "@material-ui/core/styles";
import clsx from "clsx";
import {getMenuByUser, getNavigationByUser} from "app/redux/actions/NavigationAction";
import useAuth from 'app/hooks/useAuth';

const useStyles = makeStyles(({ palette, ...theme }) => ({
  scrollable: {
    paddingLeft: 20,
    paddingRight: 20,
  },
  sidenavMobileOverlay: {
    position: "fixed",
    top: 0,
    left: 0,
    bottom: 0,
    right: 0,
    width: "100vw",
    background: "rgba(0, 0, 0, 0.54)",
    zIndex: -1,
    [theme.breakpoints.up("lg")]: {
      display: "none",
    },
  },
}));

const Sidenav = ({ children }) => {
  const classes = useStyles();
  const dispatch = useDispatch();
  const { settings } = useSelector((state) => state.layout);
  const { navs } = useSelector(({ navigations }) => navigations);
  const { user } = useAuth();

  const updateSidebarMode = (sidebarSettings) => {
    let activeLayoutSettingsName = settings.activeLayout + "Settings";
    let activeLayoutSettings = settings[activeLayoutSettingsName];

    dispatch(
      setLayoutSettings({
        ...settings,
        [activeLayoutSettingsName]: {
          ...activeLayoutSettings,
          leftSidebar: {
            ...activeLayoutSettings.leftSidebar,
            ...sidebarSettings,
          },
        },
      })
    );
  };

  useEffect(() => {
    //dispatching the call to load the menu by user  
    dispatch(getNavigationByUser(user));
  }, []);

  return (
    <Fragment>
      <Scrollbar
        options={{ suppressScrollX: true }}
        className={clsx("relative px-4", classes.scrollable)}>
        {children}
        <MatxVerticalNav navigation={navigations} />
      </Scrollbar>

      <div
        onClick={() => updateSidebarMode({ mode: "close" })}
        className={classes.sidenavMobileOverlay}
      />
    </Fragment>
  );
};

export default Sidenav;
