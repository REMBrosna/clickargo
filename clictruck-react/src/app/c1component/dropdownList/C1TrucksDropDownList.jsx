import React, { useState, useEffect } from "react";
import axios from 'axios.js';
import PropTypes from 'prop-types';

import { TextField, MenuItem, } from "@material-ui/core";
import SessionCache from 'app/services/sessionCacheService.js';
import useAuth from "../../hooks/useAuth";


export default function C1TrucksDropDownList({
    label,
    name,
    value="",
    onChange,
    accnId,
    error,
    helperText,
    disabled,
    className,
    additionalProps
}) {

    const [trucksList, setTrucksList] = useState([]);

    const handleChange = (event) => {
        const value = event.target.value;
        const object = trucksList?.find(val => val?.[name] === value)
        onChange(event, object);
    };

    useEffect(() => {
        accnId && axios.get(`/api/v1/clickargo/shell/truck/${accnId}`)
            .then(result => {
                if (result?.data?.data) {
                    setTrucksList(result?.data?.data || []);
                }
            })
            .catch((error) => {
                console.log(error);
            });
    }, [accnId]);

    return (
        <div className={additionalProps.required ? "C1-Required" : ""}>
            <TextField
                fullWidth
                variant="outlined"
                size="medium"
                disabled={disabled}
                required={additionalProps.required}
                className={className}
                error={error}
                label={label}
                helperText={helperText}
                name={name}
                value={value}
                onChange={handleChange}
                select
                {...additionalProps}
                InputLabelProps={{
                    shrink: true,
                }}
            >
                {trucksList.map((item, ind) => (
                    <MenuItem value={item.vehId} key={item.vehId}>{item.plateNo}({item.vehType})</MenuItem>
                ))}
            </TextField>
        </div>
    )
}

C1TrucksDropDownList.propTypes = {
    label: PropTypes.string,
    name: PropTypes.string.isRequired,
    value: PropTypes.string.isRequired,
    onChange: PropTypes.func.isRequired
}

C1TrucksDropDownList.defaultProps = {
    label: 'Plate Number ',
    name: 'tckCtVeh.vhId',
}

