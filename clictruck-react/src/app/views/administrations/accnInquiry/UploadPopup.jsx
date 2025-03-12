import { Grid } from "@material-ui/core";
import MenuItem from "@material-ui/core/MenuItem";
import React, { useEffect, useState } from "react";

import C1FileUpload from "app/c1component/C1FileUpload";
import C1InputField from "app/c1component/C1InputField";
import C1SelectField from "app/c1component/C1SelectField";
import useHttp from "app/c1hooks/http";
import { getValue } from "app/c1utils/utility";
import { ATTACH_TYPE } from "app/c1utils/const";

const UploadPopup = (props) => {
  const {
    inputData,
    viewType,
    errors,
    handleInputFileChange,
    handleInputChange,
    locale,
  } = props;

  /** ---------------- Declare states ------------------- */
  const { isLoading, res, urlId, sendRequest } = useHttp();

  // const attTypes = [{ value: "OTH", desc: "OTHERS" }];
  const [attTypeList, setAttTypeList] = useState([]);

  /** --------------- Update states -------------------- */
  useEffect(() => {
    sendRequest(ATTACH_TYPE, "getAttTypeList", "GET");
  }, []);

  useEffect(() => {
    if (!isLoading && res) {
      switch (urlId) {
        case "getAttTypeList":
          setAttTypeList(res?.data?.aaData);
          break;
        default:
          break;
      }
    }
  }, [isLoading, res, urlId]);

  return (
    <React.Fragment>
      <Grid container spacing={2}>
        <Grid container item xs={12} sm={6} direction="column">
          <C1SelectField
            label={locale("listing:attachments.docType")}
            name="tmstAttType.mattId"
            required
            value={
              inputData?.tmstAttType?.mattId
                ? inputData?.tmstAttType?.mattId
                : ""
            }
            onChange={handleInputChange}
            isServer={true}
            optionsMenuItemArr={Object.values(attTypeList).map((item) => {
              return (
                <MenuItem value={item.mattId} key={item.mattId}>
                  {item.mattName}
                </MenuItem>
              );
            })}
            error={!!errors.mattId}
            helperText={errors.mattId ?? null}
          />
        </Grid>

        <Grid container item xs={12} sm={6} direction="column">
          <Grid item xs={12}>
            <C1FileUpload
              inputLabel={locale("listing:attachments.docFile")}
              inputProps={{
                placeholder: locale("listing:attachments.nofilechosen"),
              }}
              value={getValue(inputData?.airdFilename)}
              fileChangeHandler={handleInputFileChange}
              label={locale("listing:attachments.browse").toUpperCase()}
              required
              disabled={false}
              errors={errors && errors.airdFilename ? true : false}
              helperText={
                errors && errors.airdFilename ? errors.airdFilename : null
              }
            />
          </Grid>
        </Grid>
      </Grid>
    </React.Fragment>
  );
};

export default UploadPopup;
