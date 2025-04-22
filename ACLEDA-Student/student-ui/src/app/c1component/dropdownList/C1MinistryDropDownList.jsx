import React, { useState, useEffect } from "react";
import axios from 'axios.js';
import PropTypes from 'prop-types';

import { TextField, MenuItem, } from "@material-ui/core";
import SessionCache from 'app/services/sessionCacheService.js';


export default function C1MinistryDropDownList({
    label,
    name,
    value,
    onChange,
    className,
    additionalProps
}) {

    // console.log('C1MinistryDropDownList= ', label, name, value, onChange);

    const [ministryList, setMinistryList] = useState([]);

    const handleChange = (event) => {
        onChange(event);
    };

    useEffect(() => {

        var ministryListSession = SessionCache.getMinistryList();
        // console.log(`MinistryListSession = ${MinistryListSession}`);

        if (!ministryListSession) {
            axios.get("/api/co/ccm/entity/ministry/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=minCode&iColumns=1")
                .then(result => {
                    //console.log( `result = ${JSON.stringify(result)}`);
                    ministryListSession = result.data.aaData;
                    // console.log( `MinistryListSession 1 = ${JSON.stringify(MinistryListSession)}`);

                    if (ministryListSession) {
                        SessionCache.setMinistryList(ministryListSession);
                        setMinistryList(ministryListSession || []);
                    }
                })
                .catch((error) => {
                    console.log(error);
                });
        } else {
            setMinistryList(ministryListSession || []);
        }
    }, []);


    return (
        <div className={additionalProps.required ? "C1-Required" : ""}>
            <TextField
                fullWidth
                variant="outlined"
                size="medium"
                className={className}
                label={label}
                name={name}
                value={value}
                onChange={handleChange}
                select
                {...additionalProps}
                InputLabelProps={{
                    shrink: true,
                }}
            >
                <MenuItem value='' key=''>  </MenuItem>
                {ministryList.map((item, ind) => (
                    <MenuItem value={item.minCode} key={item.minCode}> {item.minCode}({item.minDesc}) </MenuItem>
                ))}
            </TextField>
        </div>
    )

}

C1MinistryDropDownList.propTypes = {
    label: PropTypes.string,
    name: PropTypes.string.isRequired,
    value: PropTypes.string.isRequired,
    onChange: PropTypes.func.isRequired
}

C1MinistryDropDownList.defaultProps = {
    label: 'Ministry',
}

