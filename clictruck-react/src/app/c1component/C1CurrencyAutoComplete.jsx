import React, { useState, useEffect } from "react";
import axios from 'axios.js';
import PropTypes from 'prop-types';

import { TextField, MenuItem, } from "@material-ui/core";
import { Autocomplete, createFilterOptions } from "@material-ui/lab";
import SessionCache from 'app/services/sessionCacheService.js';

/**
 * 
 * @deprecated to be deleted soon
 */
export default function C1CurrencyAutoComplete({
  id,
  label,
  name,
  value,
  onChange
}) {

  // console.log('C1CountryDropDownList= ', label, name, value, onChange);

  const [currencyList, setCurrencyList] = useState([]);

  const handleChange = (event) => {
    onChange(event);
  };


  useEffect(() => {

    var currencyListSession = SessionCache.getCurrencyList();
    console.log(`currencyListSession = ${currencyListSession}`);

    if (!currencyListSession) {
      axios.get("/api/co/master/entity/currency/list?sEcho=3&iDisplayStart=0&iDisplayLength=10&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=ccyCode&iColumns=1")
        .then(result => {
          //console.log( `result = ${JSON.stringify(result)}`);
          currencyListSession = result.data.aaData;
          // console.log( `currencyListSession 1 = ${JSON.stringify(countryListSession)}`);

          if (currencyListSession) {
            SessionCache.setCurrencyList(currencyListSession);
            setCurrencyList(currencyListSession || []);
          }
        })
        .catch((error) => {
          console.log(error);
        });
    } else {
      setCurrencyList(currencyListSession || []);
    }
  }, []);


  return (
    <Autocomplete
      id={id}
      className="w-300"
      options={currencyList}
      name={name}
      value={value}
      renderOption={option => option.ccyDescription}
      getOptionLabel={option => {
        // console.log("option= ", option);
        if (typeof option === "object") {
          return option.ccyDescription;
        }
        return option;
      }}
      getOptionSelected={option => {
        //console.log("option= ", option);
        if (typeof option === "object") {
          return option.ccyDescription;
        }
        return option;
      }}
      renderInput={params => (
        <TextField
          {...params}
          name={name}
          label={label}
          value={value}
          fullWidth
          variant="outlined"
          onChange={handleChange}
          onBlur={handleChange}
        />
      )}
    />
  );

}

C1CurrencyAutoComplete.propTypes = {
  additional: PropTypes.shape({
    label: PropTypes.string,
    name: PropTypes.string,
    value: PropTypes.string,
    onChange: PropTypes.func
  })
}

C1CurrencyAutoComplete.defaultProps = {
  additional: {
    label: '',
    name: '',
    value: ''
  }
}

