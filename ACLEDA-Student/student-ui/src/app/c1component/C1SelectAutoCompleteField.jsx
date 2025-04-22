import React, { useEffect, useState } from "react";
import { useFetchDropdownData } from "app/c1hooks/dropdown";
import PropTypes from 'prop-types';
import Autocomplete from '@material-ui/lab/Autocomplete';
import { CircularProgress, InputAdornment, TextField } from "@material-ui/core";
import C1Version from "./C1Version";


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
const C1SelectAutoCompleteField = ({
    label,
    name,
    onChange,
    value,
    disabled,
    required,
    isServer = false,
    isShowCode = false,
    options,
    error,
    helperText,
    changes,
    disablePortal = true,
    lock = false,
    children,
}) => {

    const [open, setOpen] = useState(false);
    const [optionsSelection, setOptionsSelection] = useState([]);
    const loading = open && optionsSelection.length === 0;
    const [inputValue, setInputValue] = useState(value);
    const [isLoading, setIsLoading] = useState(loading);

    let { url, key, id, desc, isCache } = options || {};

    let dataList = useFetchDropdownData(url, key, id, desc, isCache, isServer);


    useEffect(() => {

        if (!loading && !dataList) {
            return undefined;
        }

        if (dataList && dataList.length > 0) {
            setIsLoading(false);
            setOptionsSelection(dataList);

            if (value) {
                let val = '';
                Object.keys(dataList).forEach(element => {
                    //console.log("option", option, optionsSelection[element].value);
                    if (dataList[element].value === value) {
                        val = isShowCode ? `${dataList[element].value} - ${dataList[element].desc}` : dataList[element].desc;
                    }
                });
                setInputValue(val);
            }
        }

        if (dataList.length === 0) {
            setIsLoading(false);
        }

        // eslint-disable-next-line
    }, [dataList, loading, value]);

    let el = <Autocomplete
        open={open}
        autoHighlight
        onOpen={() => setOpen(true)}
        onClose={() => setOpen(false)}
        options={optionsSelection}
        inputValue={inputValue}
        disablePortal={disablePortal}
        clearOnBlur
        getOptionSelected={(option, value) => {
            return option.value === value;
        }}
        getOptionLabel={(option) => {
            if (option && option instanceof Object) {
                return isShowCode ? `${option.value} - ${option.desc}` : option.desc
            } else {
                //option is just the code
                let display = "";
                Object.keys(optionsSelection).forEach(element => {
                    //console.log("option", option, optionsSelection[element].value);
                    if (optionsSelection[element].value === option) {
                        display = isShowCode ? `${optionsSelection[element].value} - ${optionsSelection[element].desc}` : optionsSelection[element].desc;
                    }

                });

                return display;
            }

        }}
        id={name}
        loading={isLoading}
        value={value}
        onChange={(e, value, reason) => {
            if (reason === 'clear') {
                setOptionsSelection([]);
            }

            onChange(e, name, value, reason);
        }}
        onInputChange={(event, newInputValue, reason) => {
            if (reason === 'clear')
                setOptionsSelection([]);
            setInputValue(newInputValue);
        }}
        name={name}
        disabled={lock ? true : disabled}
        includeInputInList
        loadingText={isLoading ? "No data found" : null}
        noOptionsText={!isLoading ? "No data found" : "No options"}
        renderInput={(params) => {
            return <TextField
                {...params}
                margin="normal"
                label={label}
                name={name}
                fullWidth
                size="medium"
                variant="outlined"
                disabled={lock ? true : disabled}
                required={required}
                InputLabelProps={{
                    shrink: true
                }}
                inputProps={{
                    ...params.inputProps
                }}
                helperText={helperText || ''}
                error={error ? error : false}
                className={required ? "C1-Required" : ''}
                InputProps={{
                    ...params.InputProps,
                    endAdornment: changes ? <InputAdornment position="end"><C1Version changes={changes} /></InputAdornment>
                        : <React.Fragment>
                            {isLoading ? <CircularProgress color="inherit" size={20} /> : null}
                            {params.InputProps.endAdornment}
                        </React.Fragment>
                }}>
                {children}
            </TextField>
        }}
    />;

    return el;

}

C1SelectAutoCompleteField.propTypes = {
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

export default C1SelectAutoCompleteField;