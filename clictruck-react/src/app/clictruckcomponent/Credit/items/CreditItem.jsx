import { Box, Typography, makeStyles } from "@material-ui/core";
import { grey, lightGreen } from "@material-ui/core/colors";
import { formatCurrency } from "app/c1utils/utility";
import PropTypes from 'prop-types';
import React from "react";


const DashboardItem = (props) => {

    const {category, amount, color} = props;


    const useStyless = makeStyles((theme) => ({
        wrapper: { display: 'flex', borderBottom: `3px solid ${grey[300]}`, alignItems: 'center' },
        box: {
            borderTopLeftRadius: 5,
            borderTopRightRadius: 5,
            height: 50,
            width: 170,
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            backgroundColor: color ? color : lightGreen[300],
            marginRight: 5,
            whitesSpace: 'pre'

        },
        priceWrapper: {
            width: '100%',
            display: 'flex',
            justifyContent: 'flex-end'
        },
        amount: {
            fontWeight: 700,
            textAlign: 'right',
            fontFamily: 'Lato',
            color: grey[800]
        },
        label: {
            whitesSpace: 'pre',
            color: '#fff'
        }
    }));

    const classNames = useStyless();

    return (
        <>
            <Box component="div" className={classNames.wrapper}>
                <Box component="div" className={classNames.box}>
                    <Typography variant="subtitle2" className={classNames.label}>{category}</Typography>
                </Box>
                <Box component="div" className={classNames.priceWrapper}>
                    <Typography className={classNames.amount} variant="h5"> {amount && amount !== 0 ? formatCurrency(amount, 'IDR').replace('Rp','') : 0}</Typography>
                </Box>
            </Box>
        </>
    )
}

DashboardItem.propTypes = {
    category: PropTypes.string,
    amount: PropTypes.number,
    color: PropTypes.string
}

export default DashboardItem;