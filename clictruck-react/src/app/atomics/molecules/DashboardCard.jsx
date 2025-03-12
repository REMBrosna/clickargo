import { Box, Typography, makeStyles } from '@material-ui/core';
import React, { useEffect, useState } from 'react';
import PropTypes from 'prop-types';

const DashboardCard = (props) => {

    const { id, active, icon, title, statistics, category, handleClick } = props;
    const [hover, setHover] = useState(false)

    const useStyles = makeStyles((theme) => ({
        wrapper: {
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            width: '100%',
            backgroundColor: active ? '#0a72ba' : '#d2d8da',
            height: 110,
            color: '#fff',
            paddingTop: 10,
            paddingLeft: 20,
            paddingRight: 20,
            paddingBottom: 10,
            borderBottom: '3px solid #0a72ba',
            cursor: 'pointer',
            marginBottom: '-0.5px'
        },
        content: {
            height: '100%',
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'space-between',
            alignContent: 'flex-start',
            transition: 'transform .4s ease',
            transform: hover ? 'scale(1.05)' : ''
        },
        imgWrapper: {
            display: 'flex',
            justifyContent: 'flex-end',
            alignItems: 'center',
            transition: 'transform .4s ease',
            transform: hover ? 'scale(1.1)' : ''
        },
        image: {
            height: 80,
            filter: !active ? 'grayscale(100%)' : ''
        },
        title: {
            fontWeight: 600
        },
        textCount: {
            fontWeight: 'bold'
        }
    }));

    const classNames = useStyles();

    return (
        <>
            <Box 
                component="div" 
                className={classNames.wrapper} 
                onClick={(e) => handleClick(e, id, title)} 
                onMouseEnter={() => setHover(true)} 
                onMouseLeave={() => setHover(false)}
            >
                <Box component="div" className={classNames.content}>
                    <Typography variant='subtitle1' className={classNames.title}>{title}</Typography>
                    <Box component="div">
                        <Typography variant='h5' className={classNames.textCount}>{statistics}</Typography>
                        <Typography variant='subtitle2'>{category}</Typography>
                    </Box>
                </Box>
                <Box component="div" className={classNames.imgWrapper}>
                   <img src={`${process.env.PUBLIC_URL}/assets/images/illustrations/${icon}`} className={classNames.image} alt='dashboard icon' />
                </Box>
            </Box>
        </>
    )
}

DashboardCard.propTypes = {
    active: PropTypes.bool,
    icon: PropTypes.string,
    title: PropTypes.string,
    statistics: PropTypes.string,
    category: PropTypes.string,
    handleClick: PropTypes.func
}


export default DashboardCard;