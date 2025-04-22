import React from "react";

import { makeStyles } from "@material-ui/core/styles";
import clsx from "clsx";
import { useSelector } from "react-redux";
import mpwtlogo from '../Layout2/sample_stamp.jpg';

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
        <img src={mpwtlogo} alt="company-logo" width="80" height="auto" />
        {/* <MatxLogo className="" /> */}
        <span
          className={clsx({
            "text-18 ml-2 font-medium sidenavHoverShow": true,
            [classes.hideOnCompact]: mode === "compact"
          }, classes.brandText)} >
          PortEDI
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
