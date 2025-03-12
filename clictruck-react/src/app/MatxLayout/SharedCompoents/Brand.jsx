import { makeStyles } from "@material-ui/core/styles";
import clsx from "clsx";
import React from "react";
import { useSelector } from "react-redux";

import cliclogo from '../Layout2/cliclogo.png';

const useStyles = makeStyles(({ palette, ...theme }) => ({
  brand: {
    padding: "20px 18px 32px 24px",
  },
  hideOnCompact: {
    display: "none",
  },
  brandText: {
    color: palette.primary.contrastText,
  },
}));

const Brand = ({ children }) => {
  const classes = useStyles();
  const { settings } = useSelector((state) => state.layout);
  const leftSidebar = settings.layout1Settings.leftSidebar;
  const { mode } = leftSidebar;

  return (
    <div className={clsx("flex items-center justify-between", classes.brand)}>
      <div className="flex items-center">
        <img src={cliclogo} alt="company-logo" width="80" height="auto" />
        {/* <MatxLogo className="" /> */}
        <span
          className={clsx({
            "text-18 ml-2 font-medium sidenavHoverShow": true,
            [classes.hideOnCompact]: mode === "compact"
          }, classes.brandText)} >
          ClicTruck
        </span>
      </div>
      <div
        className={clsx({
          sidenavHoverShow: true,
          [classes.hideOnCompact]: mode === "compact",
        })}
      >
        {children}
      </div>
    </div>
  );
};

export default Brand;
