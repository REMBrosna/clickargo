import { Grid } from "@material-ui/core";
import MenuItem from "@material-ui/core/MenuItem";
import React, { useEffect } from "react";

import C1InputField from "app/c1component/C1InputField";
import C1SelectField from "app/c1component/C1SelectField";
import useHttp from "app/c1hooks/http";
import { ATTACH_TYPE } from "app/c1utils/const";

const AddJobAttPopup = (props) => {

    const {
        inputData,
        viewType,
        errors,
        handleInputFileChange,
        handleInputChange,
        locale
    } = props

    /** ---------------- Declare states ------------------- */
    const { isLoading, res, urlId, sendRequest } = useHttp();

    const attTypes = [{ value: "OTH", desc: "OTHERS" }];

    /** --------------- Update states -------------------- */
    useEffect(() => {

    }, [viewType]);

    useEffect(() => {
        if (!isLoading && res) {
            switch (urlId) {
                default: break;
            }
        }
    }, [isLoading, res, urlId]);

    return (<React.Fragment>

        <Grid container spacing={2} >
            <Grid container item xs={12} sm={6} direction="column">
                <C1SelectField
                    label={locale("listing:attachments.docType")}
                    name="tmstAttType.mattId"
                    required
                    value={inputData?.tmstAttType?.mattId ? inputData?.tmstAttType?.mattId : ''}
                    onChange={handleInputChange}
                    isServer={true}
                    // options={{
                    //     url: ATTACH_TYPE,
                    //     key: "mattId",
                    //     id: 'mattId',
                    //     desc: 'mattName',
                    //     isCache: false
                    // }}
                    optionsMenuItemArr={attTypes.map((item, ind) => (
                        <MenuItem value={item.value} key={ind}>
                            {item.desc}
                        </MenuItem>
                    ))}
                    error={!!errors.mattId}
                    helperText={errors.mattId ?? null}
                />
            </Grid>

            <Grid container item xs={12} sm={6} direction="column">
                <Grid xs={12} container item spacing={0} alignItems="flex-start">
                    <Grid item xs={4}>
                        <C1InputField
                            label={""}
                            name="attName"
                            type="file"
                            inputProps={{
                                id: 'uploadFile',
                                accept: "image/*;application/pdf",
                                style: {
                                    color: "transparent"
                                }
                            }}
                            onChange={handleInputFileChange}
                        />
                    </Grid>
                    <Grid item xs={8}>
                        <C1InputField
                            label={locale("listing:attachments.docFile")}
                            name="attName"
                            value={inputData?.attName ? inputData?.attName : locale("listing:attachments.nofilechosen")}
                            onChange={handleInputFileChange}
                            required
                            disabled
                            error={errors && errors.attName ? true : false}
                            helperText={errors && errors.attName ? errors.attName : null}
                        />
                    </Grid>
                </Grid>
            </Grid>
        </Grid>
    </React.Fragment >

    );
};

export default AddJobAttPopup;


