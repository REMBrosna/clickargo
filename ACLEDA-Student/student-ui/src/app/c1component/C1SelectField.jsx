import React from "react";
import C1InputField from "./C1InputField";
import MenuItem from "@material-ui/core/MenuItem";
import { useFetchDropdownData } from "app/c1hooks/dropdown";
import PropTypes from 'prop-types';
import Select from "@material-ui/core/Select";

/**
 * @param label - form label for the field
 * @param name - form name for the field
 * @param onChange - event handler on field change
 * @param value - form value
 * @param disabled - boolean value to flag if the field is to be disabled or not
 * @param required - boolean value to flag if the field is required or not, field will be highlighted in yellow
 * @param isNative - boolean value if select will be native
 * @param isMultiple - boolean value if select is multiple select
 * @param isServer - boolean value to flag if data will be retrieved from API
 * @param options -  object that contains key, id, desc and isCache. Key is the cache key (e.g. country, port);
 * id and desc refer to the attribute corresponding to the maste table (e.g. portCode, portDesc); isCache boolean value to flag if 
 * the response is to be cached or not.
 * @param optionsMenuItemArr - array of <MenuItem> to populate the select options; this will only be used if isServer = false
 * @param error - boolean value if field has error upon validation
 * @param helperText - text to display if error occurs
  */
const C1SelectField = ({
    label,
    name,
    onChange,
    value,
    disabled,
    required,
    isShowCode = false,
    isNative = false,
    isMultiple = false,
    isServer = false,
    options,
    optionsMenuItemArr,
    error,
    helperText,
    changes,
    lock = false,
    children,
}) => {

    let { url, key, id, desc, isCache } = options || {};

    let dataList = useFetchDropdownData(url, key, id, desc, isCache, isServer);

    let el = <C1InputField
        label={label}
        name={name}
        disabled={lock ? true : disabled}
        required={required}
        value={value}
        onChange={onChange}
        select
        error={error}
        helperText={helperText}
        changes={changes}>
        <MenuItem value='' key='-1'>Select...</MenuItem>
        {optionsMenuItemArr}
        {children}
    </C1InputField >;

    if (isServer && dataList && dataList.length > 0) {
        el = <C1InputField
            label={label}
            name={name}
            disabled={lock ? true : disabled}
            required={required}
            value={value}
            onChange={onChange}
            select
            error={error}
            helperText={helperText}
            changes={changes}>
            <MenuItem value='' key='-1' >Select...</MenuItem>
            {dataList && dataList.map((d, ind) => (
                <MenuItem value={d.value} key={ind}> {isShowCode ? d.value + "-" : ""} {d.desc} </MenuItem>
            ))}
        </C1InputField>;

        if (isNative && isMultiple) {
            el = <Select size="medium"
                margin="normal"
                value={[]}
                name={name}
                label={label}
                multiple
                native
                disableunderline="true"
                displayEmpty
                variant="outlined"
                fullWidth>
                {dataList && dataList.map((d, ind) => (
                    <option value={d.value} key={ind}> {d.desc} </option>
                ))}
            </Select >
        }
    }

    return el;
}

C1SelectField.propTypes = {
    label: PropTypes.string,
    name: PropTypes.string.isRequired,
    value: PropTypes.string,
    onChange: PropTypes.func,
    required: PropTypes.bool,
    disabled: PropTypes.bool,
    isServer: PropTypes.bool,
    optionsMenuItemArr: PropTypes.array,
    options: PropTypes.shape({
        url: PropTypes.string,
        key: PropTypes.string,
        id: PropTypes.string,
        desc: PropTypes.string,
        isCache: PropTypes.bool,
    }),
    helperText: PropTypes.string,
    isNative: PropTypes.bool,
    isMultiple: PropTypes.bool,
    error: PropTypes.bool
}

export default C1SelectField;