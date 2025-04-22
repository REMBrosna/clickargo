import React, { useEffect, useState, useCallback } from "react";
import { MatxHorizontalNav } from "matx";
import { navigations } from "../../navigations";
import { makeStyles } from "@material-ui/core/styles";
import clsx from "clsx";
import { useDispatch, useSelector } from "react-redux";
import { getMenuByUser, getNavigationByUser } from "app/redux/actions/NavigationAction";
import useAuth from 'app/hooks/useAuth';

const useStyles = makeStyles(({ palette, ...theme }) => ({
  root: {
    "&, & .horizontal-nav ul ul": {
      background: palette.primary.main,
    },
    "& .horizontal-nav a, & .horizontal-nav label": {
      color: palette.primary.contrastText,
    },
    "& .horizontal-nav ul li ul li:hover, & .horizontal-nav ul li ul li.open": {
      background: palette.primary.dark,
    },
  },
}));

const Layout2Navbar = () => {
  const dispatch = useDispatch();
  const classes = useStyles();
  const { user } = useAuth();
  const [maxSize, setMaxSize] = useState(9);
  // Get navigation from Redux store
  const userNavigation = useSelector((state) => state.navigation?.userNavigation);

  // Memoized resize handler
  const handleResize = useCallback(() => {
    setMaxSize(window.innerWidth < 1600 ? 5 : 9);
  }, []);

  useEffect(() => {
    // Fetch navigation only if user exists and navigation hasn't been loaded
    if (user) {
      dispatch(getNavigationByUser(user));
    }
  }, [user, dispatch, userNavigation]);

  useEffect(() => {
    // Handle window resize
    window.addEventListener('resize', handleResize);
    handleResize(); // Initial call

    return () => {
      window.removeEventListener('resize', handleResize);
    };
  }, [handleResize]);

  // Use userNavigation if available, fallback to default navigations
  const activeNavigation = userNavigation || navigations;

  return (
      <div className={clsx("navbar", classes.root)}>
        <div className="pl-6">
          <MatxHorizontalNav navigation={activeNavigation} max={maxSize} />
        </div>
      </div>
  );
};

export default Layout2Navbar;