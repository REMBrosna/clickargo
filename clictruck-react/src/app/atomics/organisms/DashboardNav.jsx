import React, { useEffect, useState } from 'react';
import DashboardCard from '../molecules/DashboardCard';
import { Box, Grid, makeStyles } from '@material-ui/core';
import PropTypes from 'prop-types';

/** Blocks in Dashboard */
const DashboardNav = (props) => {

    const { data, activeId, handleClick } = props;

    const [dashboard, setDashboard] =  useState([])

    const useStyles = makeStyles((theme) => ({
        wrapper: {
            width: '100%',
            borderBottom: '3px solid #0a72ba',
            paddingLeft: 2,
            paddingRight: 2
        },
        gridItem: {
            padding: 1
        }
    }));

    const classNames = useStyles();

    useEffect(() => {
        const arrDashboard = data.map((item) => {
            return {
                ...item,
                id: item.id,
                img: `${item.dbType}.png`,
                statistics: Object.values(item.transStatistic).join(' / '),
                category: toTitleCase(Object.keys(item.transStatistic).join(' / '))
            }
        })
        setDashboard(arrDashboard)
    },[data])

    const toTitleCase = (str) => {
        const titleCase = str
            .replaceAll('_', ' ')
            .toLowerCase()
            .split(' ')
            .map(word => {
                return word.charAt(0).toUpperCase() + word.slice(1);
            })
            .join(' ');

        return titleCase;
    }

    return (
        <>
            <Box component="div" className={classNames.wrapper}>
                <Grid container spacing={1}>
                    {
                        dashboard.map((item, index) => {
                            return (
                                <Grid item xs={12} md={Math.round(12 / data.length)} key={index} style={{padding: 1.5}}>
                                    <DashboardCard
                                        id={item.id}
                                        active={item.id === activeId}
                                        icon={item.img}
                                        title={item.title}
                                        statistics={item.statistics}
                                        category={item.category}
                                        handleClick={handleClick}
                                         />
                                </Grid>
                            )
                        })
                    }
                </Grid>
            </Box>
        </>
    )
}

DashboardNav.propTypes = {
    data: PropTypes.array,
    handleClick: PropTypes.func,
    activeId: PropTypes.number
}

export default DashboardNav;