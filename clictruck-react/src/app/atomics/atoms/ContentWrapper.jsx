import { Box, makeStyles } from "@material-ui/core";
import React from "react";
import PropTypes from 'prop-types';

const ContentWrapper = (props) => {
   
    const {children} = props;

    const useStyles = makeStyles((theme) => ({
        wrapper: {
            width: '100%',
            padding: 24,
            backgroundColor: '#fff',
            minHeight: '50vh'
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

ContentWrapper.propsTypes = {
    children: PropTypes.element
}

export default ContentWrapper;