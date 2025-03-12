import React from "react";
import TextField from "@material-ui/core/TextField";
import PropTypes from 'prop-types';
import C1Version from "app/c1component/C1Version";
import { InputAdornment } from "@material-ui/core";

/**
 * @param label - form label for the field
 * @param name  - form name for the field
 * @param title
 * @param onChange - event handler
 * @param onBlur
 * @param value - form value
 * @param defaultValue
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
 * @param style
 * @param changes
 * @param isInteger
 * @param info
 * @param InputProps
 * @param children - array of children to be passed to <TextField>, this is only applicable for select field.
 * @param autoComplete
 * @param placeholder
 */
const C1InputField = ({
    label,
    name,
    title,
    onChange,
    onBlur,
    value,
    defaultValue,
    disabled,
    required,
    type,
    multiline,
    rows,
    rowsMax,
    error,
    helperText,
    select,
    inputProps,
    style,
    changes,
    isInteger = false,
    info,
    InputProps,
    children,
    autoComplete = "",
    placeholder = ""
}) => {

    const onChangeLocal = (e) => {
        if (type === 'number' && isInteger) {
            const re = /^[0-9\b]+$/;
            if (e.target.value === "" || re.test(e.target.value)) {
                onChange(e);
            }
        } else if (type === 'number') {//for decimal values
            const re = /^[0-9\b]+\.?[0-9]*/;
            if (e.target.value === "" || re.test(e.target.value)) {
                onChange(e);
            }
        } else {
            onChange(e);
        }
    }

    return <React.Fragment>
        <TextField
            title={title}
            margin="normal"
            label={label}
            name={name}
            type={type || 'input'}
            fullWidth
            multiline={multiline || false}
            minRows={rows}
            maxRows={rowsMax}
            size="medium"
            variant="outlined"
            onChange={onChangeLocal}
            onBlur={onBlur}
            value={value === null || value === undefined ? "" : value}
            // value={value}
            defaultValue={defaultValue}
            disabled={disabled}
            required={required}
            placeholder={placeholder}
            InputLabelProps={{
                shrink: true
            }}
            helperText={helperText || ''}
            error={error ? error : false}
            select={select || false}
            className={required ? "C1-Required" : ''}
            inputProps={inputProps}
            style={style}
            autoComplete={autoComplete}
            InputProps={InputProps || {
                endAdornment: changes ? <InputAdornment position="end"><C1Version changes={changes} /></InputAdornment> : info ? info : null
            }}>
            {children}
        </TextField>
    </React.Fragment >;
}

C1InputField.propTypes = {
    label: PropTypes.string,
    name: PropTypes.string,
    title: PropTypes.string,
    value: PropTypes.any,
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
    children: PropTypes.array,
    autoComplete: PropTypes.string,
    placeholder: PropTypes.string,
}


export default C1InputField;