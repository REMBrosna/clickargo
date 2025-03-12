import { Button, makeStyles } from '@material-ui/core';
import React from 'react';
import PropTypes from 'prop-types';
import Colors from '../styles/color';

const ActionButton = (props) => {

    const { variant, children, handleAction, icon, ...other} = props;

    const useStyles = makeStyles((theme) => ({
        button: {
            backgroundColor: variant === 'outlined' ? Colors.ACTIVE_BUTTON : Colors.ADD_BUTTON,
            fontWeight: 'bold',
            marginLeft: 3,
            fontSize: '0.75rem'
        },
    }));

    const classNames = useStyles();

    return (
        <>
            <Button 
            variant={variant} 
            {...other} 
            color='primary' 
            className={classNames.button} 
            onClick={handleAction}
            startIcon={icon}
            >
                {children}
            </Button>
        </>
    )
}

ActionButton.propTypes = {
    variant: PropTypes.string,
    children: PropTypes.oneOfType([
        PropTypes.string,
        PropTypes.element
    ]),
    handleAction: PropTypes.func,
    icon: PropTypes.element
}

export default ActionButton;