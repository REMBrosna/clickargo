import React, { useState, useEffect } from "react";
import axios from 'axios.js';
import PropTypes from 'prop-types';

import { TextField, MenuItem, } from "@material-ui/core";
import SessionCache from 'app/services/sessionCacheService.js';


export default function C1AddrTypeDropDownList({
    label,
    name,
    value,
    onChange,
    className,
    additionalProps
}) {

    // console.log('C1CurrencyDropDownList= ', label, name, value, onChange);

    const [addrTypeList, setAddrTypeList] = useState([]);


    const handleChange = (event) => {
        onChange(event);
    };


    // console.log(`currencyListSession = ${currencyListSession}`);

    useEffect(() => {

        var addrTypeListSession = SessionCache.getAddrTypeList();
        if( addrTypeListSession ) {
            setAddrTypeList(addrTypeListSession);
        }

        if (!addrTypeListSession) {
            axios.get("/api/co/master/entity/addrType")
                .then(result => {
                    //console.log( `result = ${JSON.stringify(result)}`);
                    addrTypeListSession = result.data;
                    // console.log( `currencyListSession 1 = ${JSON.stringify(countryListSession)}`);

                    if (addrTypeListSession) {
                        SessionCache.setAddrTypeList(addrTypeListSession);
                        setAddrTypeList(addrTypeListSession || []);
                    }
                })
                .catch((error) => {
                    console.log(error);
                });
        }
    }, []);


    return (
        <div className={additionalProps.required ? "C1-Required" : ""}>
            <TextField
                className={className}
                label={label}
                name={name}
                size="medium"
                variant="outlined"
                value={value}
                onChange={handleChange}
                select
                {...additionalProps}
                InputLabelProps={{
                    shrink: true
                }}
            >
                <MenuItem value='' key=''>  </MenuItem>
                {addrTypeList.map((item, ind) => (
                    <MenuItem value={item.adtCode} key={item.adtCode}> {item.adtCode}({item.adtDesc}) </MenuItem>
                ))}
            </TextField>
        </div>
    )

}


C1AddrTypeDropDownList.propTypes = {
    additional: PropTypes.shape({
        label: PropTypes.string,
        name: PropTypes.string,
        value: PropTypes.string,
        onChange: PropTypes.func
    })
}

C1AddrTypeDropDownList.defaultProps = {
    additional: {
        label: '',
        name: '',
        value: ''
    }
}
