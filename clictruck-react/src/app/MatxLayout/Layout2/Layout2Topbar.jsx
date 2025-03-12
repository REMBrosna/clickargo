import { Avatar, Hidden, Icon, IconButton, MenuItem, colors } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import LockOpenIcon from '@material-ui/icons/LockOpen';
//import { logoutUser } from "app/redux/actions/UserActions";
import clsx from "clsx";
import { merge } from "lodash";
import React, { useEffect, useState } from "react";
import { useTranslation } from 'react-i18next';
import { useDispatch, useSelector } from "react-redux";
import { Link } from "react-router-dom";

import useAuth from "app/hooks/useAuth";
//import NotificationBar from "../SharedCompoents/NotificationBar";
import LanguageSelector from "app/MatxLayout/SharedCompoents/LanguageSelector";
import { setLayoutSettings } from "app/redux/actions/LayoutActions";
import history from "history.js";
import { MatxMenu, MatxToolbarMenu } from "matx";

//import cliclogo from './cliclogo.png';
import cliclogo from './Clickargo.svg';
import { Business, EmailOutlined } from "@material-ui/icons";

const useStyles = makeStyles(({ palette, ...theme }) => ({
    root: {
        backgroundColor: "#fff",//'#0772BA',
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
        color: "#000",//palette.primary.contrastText,
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
        minWidth: 220,
        color: "#fff",
        fontWeight: "bold",
        height: '40px'
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
        console.log("handleSignoutCalled");
        try {
            logout();
            const loginPath = window.localStorage.getItem("loginPath");
            if (loginPath)
                window.location.href = loginPath;
            else
                window.location.href = "/";

            //remove the local storage, this will be set again during login
            localStorage.removeItem("loginPath");
        } catch (e) {
            console.log(e);
        }
    };

    let elUserId = <div className={classes.userMenu}>
        <span className={classes.brandText}>
            {t("session:login.label.greetings")}
            <strong style={{
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
                overflow: 'hidden',
                width: 119,
                display: 'block'
            }}
            >
                {user?.username}
            </strong>
        </span>
        <span>
            <Avatar className="cursor-pointer" src={`${process.env.PUBLIC_URL}/assets/images/avatars/001-man.svg`} />
        </span>
    </div>

    return (
        <div className={clsx("relative w-full", classes.root)}>
            <div className="flex justify-between items-center h-full">
                <div className="flex items-center h-full">
                    <img
                        className="h-32"
                        src={cliclogo}
                        alt=""
                    />
                    <span className={clsx("font-medium text-24 mx-4", classes.brandText)}>
                        Clickargo
                    </span>
                </div>
                <div className="mx-auto"></div>
                <div style={{ display: 'flex', flexDirection: 'column' }}>
                    <div className="flex items-center">
                        <MatxToolbarMenu offsetTop="80px" style={{ color: "#fff" }}>
                            {/* <MatxSearchBox />

                        <NotificationBar /> */}

                            <LanguageSelector />
                            <MatxMenu menuButton={
                                isMobile ? <Avatar className="cursor-pointer mx-2"
                                    src={`${process.env.PUBLIC_URL}/assets/images/avatars/001-man.svg`} /> : elUserId
                            }
                            >
                                <MenuItem onClick={() => history.push(`/`)} className={classes.menuItem}>
                                    {/* <Link className={classes.menuItem} to="/"> */}
                                    <Icon> home </Icon>
                                    <span className="pl-4"> Home </span>
                                    {/* </Link> */}
                                </MenuItem>
                                <MenuItem onClick={() => history.push(`/manageUsers/user/edit/profile`)} className={classes.menuItem}>
                                    {/* <Link className={classes.menuItem} to={`/manageUsers/user/edit/profile`}> */}
                                    <Icon> person </Icon>
                                    <span className="pl-4"> Profile </span>
                                    {/* </Link> */}
                                </MenuItem>
                                <MenuItem onClick={() => history.push(`/manageUsers/updatePassword/`)} className={classes.menuItem}>
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
                    {
                        !isMobile && <div style={{ display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'end', fontSize: 11, fontWeight: 500, color: colors.grey[900], paddingRight: 15, paddingTop: 3, textAlign: 'right' }}>
                            <div style={{ whiteSpace: 'nowrap', display: 'flex', alignItems: 'center' }}><EmailOutlined style={{ fontSize: 12, marginRight: 2, marginBottom: 1 }} /> {user?.email?.toLowerCase()}</div>
                            <div style={{ whiteSpace: 'nowrap', display: 'flex', alignItems: 'center' }}><Business style={{ fontSize: 12, marginRight: 2, marginBottom: 3 }} /> {user?.coreAccn?.accnName?.replace(/\b\w/g, (char) => char.toUpperCase())}</div>
                        </div>
                    }
                </div>
            </div>
        </div >
    );
};

export default Layout2Topbar;
