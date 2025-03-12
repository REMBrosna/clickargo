import React, { useState, useEffect } from "react";
import axios from 'axios.js';
import PropTypes from 'prop-types';

import { TextField, MenuItem, } from "@material-ui/core";
import moment from "moment";


export default function C1ShellCardDropDownList({
    label,
    name,
    value="",
    onChange,
    disabled,
    error,
    helperText,
    className,
    additionalProps
}) {

    const [listOption, setListOption] = useState([]);

    const handleChange = (event) => {
        const value = event.target.value;
        const object = listOption?.find(val => val?.["scId"] === value)
        onChange(event, object);
    };

    useEffect(() => {
        axios.get("/api/v1/clickargo/shell/shellCard")
            .then(result => {
                if (result?.data?.data) {
                    setListOption(result?.data?.data || []);
                }
            })
            .catch((error) => {
                console.log(error);
            });
    }, []);

    return (
        <div className={additionalProps.required ? "C1-Required" : ""}>
            <TextField
                fullWidth
                variant="outlined"
                size="medium"
                required={additionalProps.required}
                disabled={disabled}
                className={className}
                label={label}
                name={name}
                error={error}
                helperText={helperText}
                value={value}
                onChange={handleChange}
                select
                {...additionalProps}
                InputLabelProps={{
                    shrink: true,
                }}
            >
                {listOption.map((item, ind) => (
                    <MenuItem value={item.scId} key={item.scId}> {`${item.scId}(Expire on ${moment(item.scDtExpiry).format('YYYY-MM-DD')})`} </MenuItem>
                ))}
            </TextField>
        </div>
    )
}

C1ShellCardDropDownList.propTypes = {
    label: PropTypes.string,
    name: PropTypes.string.isRequired,
    value: PropTypes.string.isRequired,
    onChange: PropTypes.func.isRequired
}

C1ShellCardDropDownList.defaultProps = {
    label: 'Card Number ',
    name: 'tckCtShellCard.scId',
}

