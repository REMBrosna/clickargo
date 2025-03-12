import React, { useState, useEffect } from "react";
import axios from 'axios.js';
import PropTypes from 'prop-types';

import { TextField, MenuItem, } from "@material-ui/core";
import SessionCache from 'app/services/sessionCacheService.js';


export default function C1AgencyDropDownList({
    label,
    name,
    value,
    onChange,
    className,
    additionalProps
}) {

    // console.log('C1AgencyDropDownList= ', label, name, value, onChange);

    const [agencyList, setAgencyList] = useState([]);

    const handleChange = (event) => {
        onChange(event);
    };

    useEffect(() => {

        var agencyListSession = SessionCache.getAgencyList();
        // console.log(`agencyListSession = ${agencyListSession}`);

        if (!agencyListSession) {
            axios.get("/api/co/ccm/entity/agency/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=agyCode&iColumns=1")
                .then(result => {
                    //console.log( `result = ${JSON.stringify(result)}`);
                    agencyListSession = result.data.aaData;
                    // console.log( `agencyListSession 1 = ${JSON.stringify(agencyListSession)}`);

                    if (agencyListSession) {
                        SessionCache.setAgencyList(agencyListSession);
                        setAgencyList(agencyListSession || []);
                    }
                })
                .catch((error) => {
                    console.log(error);
                });
        } else {
            setAgencyList(agencyListSession || []);
        }
    }, []);


    return (
        <div className={additionalProps.required ? "C1-Required" : ""}>
            <TextField
                className={className }
                label={label}
                name={name}
                size="medium"
                variant="outlined"
                value={value}
                onChange={handleChange}
                select
                {...additionalProps}
                InputLabelProps={{
                    shrink: true,
                }}
            >
                <MenuItem value='' key=''>  </MenuItem>
                {agencyList.map((item, ind) => (
                    <MenuItem value={item.agyCode} key={item.agyCode}> {item.agyCode}({item.agyDesc}) </MenuItem>
                ))}
            </TextField>
        </div>
    )

}

C1AgencyDropDownList.propTypes = {
    label: PropTypes.string,
    name: PropTypes.string.isRequired,
    value: PropTypes.string.isRequired,
    onChange: PropTypes.func.isRequired
}

C1AgencyDropDownList.defaultProps = {
    label: 'Agency',
}

