import React, { useState, useEffect } from "react";
import axios from 'axios.js';
import PropTypes from 'prop-types';

import { TextField, MenuItem, } from "@material-ui/core";
import SessionCache from 'app/services/sessionCacheService.js';
import useAuth from "../../hooks/useAuth";


export default function C1AccnDropDownList({
    label,
    name,
    value="",
    onChange,
    className,
    error,
    helperText,
    disabled,
    additionalProps
}) {

    const [accnList, setAccnList] = useState([]);

    const handleChange = (event) => {
        const value = event.target.value;
        const object = accnList?.find(val => val?.["accnId"] === value);
        onChange(event, object);
    };

    useEffect(() => {
        let accnListSession = SessionCache.getAccnList();
        if (!accnListSession) {
            axios.get(`/api/v1/clickargo/shell/accn`)
                .then(result => {
                    accnListSession = result?.data?.data;
                    if (accnListSession) {
                        SessionCache.setAccnList(accnListSession);
                        setAccnList(accnListSession || []);
                    }
                })
                .catch((error) => {
                    console.log(error);
                });
        } else {
            setAccnList(accnListSession || []);
        }
    }, [value]);

    return (
        <div className={additionalProps.required ? "C1-Required" : ""}>
            <TextField
                fullWidth
                disabled={disabled}
                variant="outlined"
                size="medium"
                required={additionalProps.required}
                className={className}
                label={label}
                error={error}
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
                {accnList.map((item, ind) => (
                    <MenuItem value={item.accnId} key={item.accnId}>{item.accnId} </MenuItem>
                ))}
            </TextField>
        </div>
    )
}

C1AccnDropDownList.propTypes = {
    label: PropTypes.string,
    name: PropTypes.string.isRequired,
    value: PropTypes.string.isRequired,
    onChange: PropTypes.func.isRequired
}

C1AccnDropDownList.defaultProps = {
    label: 'Account',
    name: 'tcoreAccn.accnId',
}

