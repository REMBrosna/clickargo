import React from "react";
import { CircularProgress, Grid, MenuItem } from "@material-ui/core";
import C1InputField from "app/c1component/C1InputField";
import { getValue, isArrayNotEmpty } from "app/c1utils/utility";

import C1SelectField from "app/c1component/C1SelectField";
import C1TextArea from "app/c1component/C1TextArea";

const JobAdditionalFields = ({
  addtionalFieldsInputData,
  fields,
  errors,
  handleAddtlFieldsInputChange,
  disabled,
}) => {
  return (
    <Grid container alignItems="center" spacing={3}>
      {isArrayNotEmpty(fields) ? (
        <Grid item xs={12}>
          {fields?.map((el, idx) => {
            let value = addtionalFieldsInputData?.find((attr) => {
              return attr?.tckCtConAddAttr?.caaId === el?.caaId ? attr : null;
            });
            if (el?.caaType === "M") {
              return (
                <C1TextArea
                  key={idx}
                  label={el?.caaLabel}
                  name={el?.caaId}
                  multiline
                  textLimit={1024}
                  onChange={handleAddtlFieldsInputChange}
                  value={getValue(value?.jaaValue)}
                  disabled={disabled}
                  required={el?.caaRequired === "N" ? false : true}
                />
              );
            } else if (el?.caaType === "T") {
              return (
                <C1InputField
                  key={idx}
                  name={el?.caaId}
                  label={el?.caaLabel}
                  value={getValue(value?.jaaValue)}
                  disabled={disabled}
                  onChange={handleAddtlFieldsInputChange}
                  error={errors["jobUidCreate"] !== undefined}
                  helperText={errors["jobUidCreate"] || ""}
                  required={el?.caaRequired === "N" ? false : true}
                />
              );
            } else if (el?.caaType === "L") {
              const options = JSON.parse(el?.tckCtAddAttrList?.aalList);
              return (
                <C1SelectField
                  key={idx}
                  label={el?.caaLabel}
                  name={el?.caaId}
                  onChange={(e) => {
                    handleAddtlFieldsInputChange(e);
                  }}
                  value={getValue(value?.jaaValue)}
                  disabled={disabled}
                  optionsMenuItemArr={options?.map((item, ind) => (
                    <MenuItem value={item?.code} key={ind}>
                      {item?.desc}
                    </MenuItem>
                  ))}
                  error={errors && errors.qurSection}
                  helperText={errors && errors.qurSection}
                  required={el?.caaRequired === "N" ? false : true}
                />
              );
            }
          })}
        </Grid>
      ) : (
        <Grid item xs={12}>
          {" "}
          <CircularProgress
            size={24}
            style={{
              position: "relative",
              top: "50%",
              left: "50%",
              marginTop: 12,
              marginLeft: -12,
            }}
          />
        </Grid>
      )}
    </Grid>
  );
};

export default JobAdditionalFields;
