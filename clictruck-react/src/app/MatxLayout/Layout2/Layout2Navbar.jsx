import { makeStyles } from "@material-ui/core/styles";
import clsx from "clsx";
import React, { useEffect, useState } from "react";
import { useDispatch } from "react-redux";

import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from 'app/hooks/useAuth';
import { getMenuByUser } from "app/redux/actions/NavigationAction";
import { getUserGuideByServiceType } from "app/redux/actions/UserGuideAction";
import { MatxHorizontalNav } from "matx";

import { navigations } from "../../navigations";

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
    useEffect(() => {
        dispatch(getMenuByUser(user));
        //Dispatch for user guide
        //TODO to replace for something that can be captured from {user}
        dispatch(getUserGuideByServiceType("clictruck"));

        if (window.innerWidth < 1600) {
            setMaxSize(5);
        }

        //to recalculate the max no. of menus visible in the navbar
        window.addEventListener('resize', () => {
            if (window.innerWidth < 1600) {
                setMaxSize(5);
            } else {
                setMaxSize(9);
            }
        });

    // eslint-disable-next-line
    }, []);

    return (
        <div className={clsx("navbar", classes.root)}>
            <div className="pl-6">
                <MatxHorizontalNav navigation={navigations} max={maxSize} />
            </div>
        </div>
    );
};

export default withErrorHandler(Layout2Navbar);
