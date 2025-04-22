import React, { useState } from "react";
import Select from "@material-ui/core/Select";
import MenuItem from "@material-ui/core/MenuItem";
import axios from 'axios.js';

import SessionCache from 'app/services/sessionCacheService.js';

/**
 * @deprecated to be deleted soon
 * 
 */
const C1CountryDropDownList = (props) => {

    const [countryList, setCountryList] = useState({});

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
                    setCountryList(countryListSession);
                }
            })
            .catch((error) => {
                console.log(error);
            });
    }

    return (
        <Select
            label="Network"
            labelId="Network"
            id="Network"
            value="">
            {
                (countryListSession) ? (countryListSession.map((item) => (
                    <MenuItem value={item.ctyCode}> {item.ctyDescription} </MenuItem>
                ))) : ""
            }
        </Select>
    )

}

export default C1CountryDropDownList;