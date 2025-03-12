import { Box, makeStyles, useMediaQuery, useTheme } from "@material-ui/core";
import React from "react";
import PropTypes from 'prop-types';

const PageWrapper = (props) => {
   
    const {children} = props;

    const theme = useTheme();
    const isSmall = useMediaQuery(theme.breakpoints.down('sm'));

    const useStyles = makeStyles((theme) => ({
        wrapper: {
            paddingTop: 25,
            paddingLeft: isSmall ? 15 : 50,
            paddingRight: isSmall ? 15 : 50
        }
    }));

    const classNames = useStyles();

    return (
        <>
           <Box component="div" className={classNames.wrapper}>
                {children}
            </Box> 
        </>
    )
}

PageWrapper.propsTypes = {
    children: PropTypes.element
}

export default PageWrapper;