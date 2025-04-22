import React, { useState } from "react";
import { useTranslation } from 'react-i18next';
import IconButton from "@material-ui/core/IconButton";
import Tooltip from "@material-ui/core/Tooltip";
import Grid from "@material-ui/core/Grid";
import LanguageIcon from '@material-ui/icons/Language';
import Flag from 'react-flagkit';
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import Avatar from "@material-ui/core/Avatar";
import { useEffect } from "react";
import { Link } from "react-router-dom";
import useAuth from "../../hooks/useAuth";
import localStorageService from "app/services/localStorageService";

const LanguageSelector = () => {

    const { i18n } = useTranslation()
    const { t } = useTranslation(["common"]);
    const { isAuthenticated } = useAuth();

    const languageOptions = [
        {
            code: 'kh',
            text: 'Khmer',
            flagUrl: 'KH'

        },
        {
            code: 'en',
            text: 'English',
            flagUrl: 'GB'
        }
    ];

    const [lang, setLang] = useState("en");
    const [anchorEl, setAnchorEl] = useState(null);
    const [selectedIndex, setSelectedIndex] = useState(-1);
    const [flagToDisplay, setFlagToDisplay] = useState(null);
    const open = Boolean(anchorEl);


    const changeLanguage = (code) => {
        localStorageService.setItem("langPref", code);
        setLang(code);
        i18n.changeLanguage(code)
    }

    const handleMenuClick = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handleClick = (event, index, code) => {
        setSelectedIndex(index);
        changeLanguage(code);
        setAnchorEl(null);

        let data = languageOptions.find(e => e.code === code);
        setFlagToDisplay({ ...flagToDisplay, ...data });
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    // eslint-disable-next-line
    useEffect(() => {
        //to check if language prop is set, otherwise default to the langaugeIcon component
        let langCode = localStorageService.getItem("langPref") || i18n.language;
        let data = languageOptions.find(e => e.code === langCode);
        if (data) {
            setFlagToDisplay({ ...flagToDisplay, ...data });
        }

        // eslint-disable-next-line
    }, [i18n])

    return (
        <React.Fragment>

            <Grid container justify="flex-end">
                {
                    isAuthenticated ? <div></div> : <div style={{ marginTop: '15px' }}>
                        <Link
                            to={"/session/faq/"}>
                            <span className="pl-4"> FAQ </span>
                        </Link>
                        <Link
                            to={"/session/contact/"}>
                            <span className="pl-4"> CONTACT </span>
                        </Link>

                        <Link
                            to={"/session/signin/"}>
                            <span className="pl-4"> LOGIN </span>
                        </Link>
                    </div>
                }

                <IconButton
                    aria-label="Language Selection"
                    aria-controls="fade-menu"
                    aria-haspopup="true"
                    onClick={handleMenuClick}
                    color="inherit">
                    <Tooltip title={t("common:language")} aria-label="add">
                        {flagToDisplay ?
                            <Avatar className="w-24 h-24"  >
                                <Flag country={flagToDisplay.flagUrl} />
                            </Avatar>
                            : <LanguageIcon />}
                    </Tooltip>
                </IconButton>
            </Grid>
            <Menu id="fade-menu" anchorEl={anchorEl} keepMounted
                open={open}
                value={lang}
                onClose={handleClose}>

                {languageOptions.map((option, index) => {
                    return <MenuItem value={option.code} key={option.code}
                        selected={flagToDisplay ? flagToDisplay.code === option.code : (index === selectedIndex)}
                        onClick={(event) => handleClick(event, index, option.code)}>
                        <Flag size={15} country={option.flagUrl} role="button" />
                        <div className="ml-3">
                            <h5 className="my-0 text-15">{option.text}</h5>
                        </div>
                    </MenuItem>
                })}
            </Menu>

        </React.Fragment >
    );
}

export default LanguageSelector;