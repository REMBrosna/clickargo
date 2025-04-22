import React from "react";
import PropTypes from 'prop-types';
import {DateTimePicker, MuiPickersUtilsProvider} from "@material-ui/pickers";
import DateFnsUtils from "@date-io/date-fns";
import C1Version from "app/c1component/C1Version";
import { InputAdornment } from "@material-ui/core";

/**
 * @param label - form field label
 * @param name - form field name
 * @param onChange - event handler for field change
 * @param value - form field value
 * @param disabled - boolean value if field is disabled or not
 * @param required - boolean value if field is required or not
 * @param format - date format, default is dd/MM/yyyy
 * @param error - boolean value if field has error upon validation
 * @param helperText - text to display if error occurs
 * @param disablePast - disables past date selection
 * @param disableFuture - disables future date selection
 * @param maxDate - sets the maximum allowable date to be selected
 * @param maxDateMessage - message to be displayed if date selected exceeds the maxDate specified
 * @param minDate - sets the minimum allowable date to be selected
 * @param minDateMessage -  - message to be displayed if date selected exceeds the minDate specified
 */
const C1DateTimeField = ({
    label,
    name,
    onChange,
    value,
    disabled,
    required,
    format,
    disablePast,
    disableFuture,
    maxDate,
    maxDateMessage,
    minDate,
    minDateMessage,
    error,
    helperText,
    changes
}) => {

    return <React.Fragment><MuiPickersUtilsProvider utils={DateFnsUtils}>
        <DateTimePicker
            inputVariant="outlined"
            size="medium"
            format={format ? format : "dd/MM/yyyy HH:mm"}
            margin="normal"
            label={label}
            fullWidth
            value={value}
            name={name}
            autoOk
            disablePast={disablePast}
            emptyLabel=" "
            InputLabelProps={{
                shrink: true
            }}
            disableFuture={disableFuture}
            maxDate={maxDate}
            maxDateMessage={maxDateMessage}
            minDate={minDate}
            minDateMessage={minDateMessage}
            variant="inline"
            onChange={(date) => onChange(name, date)}
            required={required}
            className={required ? 'C1-Required' : ''}
            disabled={disabled}
            error={error}
            helperText={helperText}
            InputProps={{
                endAdornment: changes ? <InputAdornment position="end"><C1Version changes={changes} /></InputAdornment> : null
            }}
        />
    </MuiPickersUtilsProvider>
    </React.Fragment>;
}

C1DateTimeField.propTypes = {
    label: PropTypes.string,
    name: PropTypes.string,
    value: PropTypes.any,
    onChange: PropTypes.func,
    required: PropTypes.bool,
    disabled: PropTypes.bool,
    format: PropTypes.string,
    disablePast: PropTypes.bool,
    disableFuture: PropTypes.bool,
    maxDate: PropTypes.any,
    maxDateMessage: PropTypes.string,
    minDate: PropTypes.any,
    minDateMessage: PropTypes.string,
    error: PropTypes.bool,
    helperText: PropTypes.string
}


export default C1DateTimeField;