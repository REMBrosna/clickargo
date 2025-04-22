import React, { useState, useEffect } from "react";
import axios from 'axios.js';
import PropTypes from 'prop-types';

import { TextField, MenuItem, } from "@material-ui/core";
import SessionCache from 'app/services/sessionCacheService.js';


export default function C1CountryDropDownList({
    label,
    name,
    value,
    onChange,
    className,
    additionalProps,
    viewType,
    disabled,
}) {

    // console.log('C1CountryDropDownList= ', label, name, value, onChange);

    const [countryList, setCountryList] = useState([]);

    const handleChange = (event) => {
        onChange(event);
    };

    useEffect(() => {

        var countryListSession = SessionCache.getCountryList();
        console.log(`countryListSession = ${countryListSession}`);

        if (!countryListSession) {
            axios.get("/api/co/master/entity/country/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=ctyCode&iColumns=1")
                .then(result => {
                    //console.log( `result = ${JSON.stringify(result)}`);
                    countryListSession = result.data.aaData;
                    // console.log( `countryListSession 1 = ${JSON.stringify(countryListSession)}`);

                    if (countryListSession) {
                        SessionCache.setCountryList(countryListSession);
                        setCountryList(countryListSession || []);
                    }
                })
                .catch((error) => {
                    console.log(error);
                });
        } else {
            setCountryList(countryListSession || []);
        }
    }, []);


    return (
        <TextField
            fullWidth
            variant="outlined"
            className={className ? className : "min-w-188"}
            label={label}
            disabled={disabled ? disabled : ((viewType === 'view') ? true : false)}
            name={name}
            size="medium"
            margin="normal"
            variant="outlined"
            value={value}
            onChange={handleChange}
            select
            {...additionalProps}
        >
            <MenuItem value='' key=''>  </MenuItem>
            {countryList.map((item, ind) => (
                <MenuItem value={item.ctyCode} key={item.ctyCode}> {item.ctyCode}({item.ctyDescription}) </MenuItem>
            ))}
        </TextField>
    )

}

C1CountryDropDownList.propTypes = {
    label: PropTypes.string,
    name: PropTypes.string.isRequired,
    value: PropTypes.string.isRequired,
    onChange: PropTypes.func.isRequired
}

C1CountryDropDownList.defaultProps = {
    label: 'Country',
}

