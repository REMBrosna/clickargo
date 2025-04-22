import React from "react";
import PropTypes from 'prop-types';
import { TimePicker, MuiPickersUtilsProvider } from "@material-ui/pickers";
import DateFnsUtils from "@date-io/date-fns";
import C1Version from "app/c1component/C1Version";
import C1InputField from "./C1InputField";
import { InputAdornment } from "@material-ui/core";
/**
 * @param label - form field label
 * @param name - form field name
 * @param onChange - event handler for field change
 * @param value - form field value
 * @param disabled - boolean value if field is disabled or not
 * @param required - boolean value if field is required or not
 * @param ampm - boolean value if time will be displayed in 24 hrs format. If false, 24 hrs, otherwise 12 hours
 * @param views - ("hours" | "minutes" | "seconds")[]
 * @param error - boolean value if field has error upon validation
 * @param helperText - text to display if error occurs
 */

const C1TimeField = ({
    label,
    name,
    onChange,
    value,
    disabled,
    required,
    ampm,
    views,
    error,
    helperText,
    changes,
    lock = false
}) => {

    return <React.Fragment>
        <MuiPickersUtilsProvider utils={DateFnsUtils}>
            <TimePicker
                inputVariant="outlined"
                size="medium"
                margin="normal"
                emptyLabel={" "}
                label={label}
                fullWidth
                value={value}
                name={name}
                ampm={ampm}
                clearable="true"
                autoOk
                variant="inline"
                onChange={(date) => onChange(name, date)}
                required={required}
                className={required ? 'C1-Required' : ''}
                disabled={lock ? true : disabled}
                views={views}
                error={error}
                helperText={helperText}
                InputProps={{
                    endAdornment: changes ? <InputAdornment position="end"><C1Version changes={changes} /></InputAdornment> : null
                }}
            />
        </MuiPickersUtilsProvider>
    </React.Fragment>;

}

C1TimeField.propTypes = {
    label: PropTypes.string,
    name: PropTypes.string,
    value: PropTypes.any,
    onChange: PropTypes.func,
    required: PropTypes.bool,
    disabled: PropTypes.bool,
    ampm: PropTypes.bool,
    views: PropTypes.array,
    error: PropTypes.bool,
    helperText: PropTypes.string
}
export default C1TimeField;