import { Button, ButtonGroup, makeStyles } from '@material-ui/core';
import { AssignmentTurnedInOutlined, History } from '@material-ui/icons';
import PropTypes from 'prop-types';
import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';

import Colors from '../styles/color'

const ButtonActiveHistory = (props) => {

    const { handleAction } = props;
    const [active, setActive] = useState(true);

    const { t } = useTranslation(['listing'])

    const useStyles = makeStyles((theme) => ({
        wrapper: {
            width: 200
        },
        button: {
            fontWeight: 700,
            textTransform: 'uppercase',
            width: 100,
            fontSize: '0.75rem'
        },
        text: {
            fontWeight: 700,
            textTransform: 'uppercase'
        }
    }));

    const handleClick = (status) => {
        setActive(!active);
        handleAction(status);
    }

    const classNames = useStyles();

    return (
        <>
            <ButtonGroup color="primary" key="viewTextFilter" aria-label="outlined primary button group" className={classNames.wrapper}>
                <Button key="active" style={{ backgroundColor: active ? Colors.ACTIVE_BUTTON : Colors.INACTIVE_BUTTON }} className={classNames.button} variant="outlined" size="small" onClick={() => handleClick("active")}>{t("listing:common.active")}</Button>
                <Button key="history" style={{ backgroundColor: !active ? Colors.ACTIVE_BUTTON : Colors.INACTIVE_BUTTON }} className={classNames.button} size="small" variant="outlined" onClick={() => handleClick("history")}>{t("listing:common.history")}</Button>
            </ButtonGroup>
        </>
    )
}

ButtonActiveHistory.propTypes = {
    handleAction: PropTypes.func
}

export default ButtonActiveHistory;