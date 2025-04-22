import { InputAdornment } from "@material-ui/core";
import TextField from "@material-ui/core/TextField";
import PropTypes from 'prop-types';
import React, { useState } from "react";

import C1Version from "./C1Version";

/**
 * @param label - form label for the field
 * @param name  - form name for the field
 * @param onChange - event handler
 * @param value - form value
 * @param disabled - boolean value to flag if field is disabled or not
 * @param required - boolean value to flag if field is required, it will be highlighted in yellow
 * @param type - can be file, input or date. Default is 'input'
 * @param multiline - boolean value to flag if field is multiline or a textarea
 * @param rows - no. of rows if the field is multiline
 * @param rowsMax - maximum no. of rows if the field is multiline
 * @param error - boolean value if the field has error upon validation
 * @param helperText - text to display if error occurs
 * @param select - boolean value to flag if the input field is select field
 * @param inputProps - additional props for input field
 * @param children - array of children to be passed to <TextField>, this is only applicable for select field.
 */
const C1TextArea = ({
    label,
    name,
    onChange,
    value,
    disabled,
    required,
    type,
    rows = 3,
    rowsMax = 3,
    textLimit,
    error,
    helperText,
    select,
    inputProps,
    style,
    changes,
    lock = false,
    children
}) => {

    const [valLen, setValLen] = useState(0);
    const onChangeLocal = (e) => {
        //Get the value and check if the length is greater than the textLimit
        //if yes, then cut of based on the textLimit value
        let str = e.target.value;
        if (str.length > textLimit) {
            e.target.value = str.substring(0, textLimit);
        }

        if (valLen <= textLimit - 1) {
            onChange(e);
            setValLen(e.target.value.length);
        }
    }

    const onKeyDownLocal = (e) => {
        //This will deduct 1 to length every backspace
        if (e.key === 'Backspace') {
            setValLen(valLen > 0 ? valLen - 1 : 0);
        }

    }


    if (textLimit === 256) {
        rows = 3;
        rowsMax = 6;
    } else if (textLimit === 512) {
        rows = 6;
        rowsMax = 6;
    }
    //default is rows = 3; rowsMax=3

    return <TextField
        margin="normal"
        label={label}
        name={name}
        type={type || 'input'}
        fullWidth
        multiline={true}
        rows={rows}
        rowsMax={rowsMax}
        size="medium"
        variant="outlined"
        onChange={e => onChangeLocal(e)}
        onKeyDown={e => onKeyDownLocal(e)}
        value={value}
        disabled={lock ? true : disabled}
        required={required}
        InputLabelProps={{
            shrink: true
        }}
        helperText={helperText || `${textLimit ? '(' + valLen + '/' + textLimit + " characters)" : ''}`}
        error={error ? error : false}
        select={select || false}
        className={required ? "C1-Required" : ''}
        inputProps={inputProps}
        style={style}
        InputProps={{
            endAdornment: changes ? <InputAdornment position="end"><C1Version changes={changes} /></InputAdornment> : null
        }}>
        {children}
    </TextField>;
}

C1TextArea.propTypes = {
    label: PropTypes.string,
    name: PropTypes.string,
    value: PropTypes.any,
    textLimit: PropTypes.number,
    onChange: PropTypes.func,
    disabled: PropTypes.bool,
    required: PropTypes.bool,
    type: PropTypes.string,
    multiline: PropTypes.bool,
    rows: PropTypes.oneOfType([
        PropTypes.string,
        PropTypes.number
    ]),
    rowsMax: PropTypes.oneOfType([
        PropTypes.string,
        PropTypes.number
    ]),
    error: PropTypes.bool,
    helperText: PropTypes.string,
    select: PropTypes.bool,
    children: PropTypes.array
}


export default C1TextArea;