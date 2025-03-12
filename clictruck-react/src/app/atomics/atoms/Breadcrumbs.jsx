import { Box, Typography, makeStyles } from "@material-ui/core";
import React from "react";
import PropTypes from 'prop-types';

const Breadcrumbs = (props) => {

    const  { segments } = props;

    const useStyles = makeStyles((theme) => ({
        breadcrumb: {
            // fontSize: '0.688rem'
        }
    }));

    const classNames = useStyles();

    return (
        <>
            <Box component="div" className="w-full my-sm-24" sx={{marginBottom: 10}}>
                <Typography variant="h6" className={classNames.breadcrumb}>{segments[0].name}</Typography>
            </Box>
        </>
    )
}

Breadcrumbs.propTypes = {
    segments: PropTypes.array
}

export default Breadcrumbs;