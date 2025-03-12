import { Button, createTheme, InputAdornment, MuiThemeProvider } from "@material-ui/core";
import React from "react";
import { useTranslation } from "react-i18next";

import C1InputField from "app/c1component/C1InputField";

const C1FileUpload = ({
    name,
    value,
    inputLabel,
    disabled,
    fileChangeHandler,
    label,
    icon,
    required,
    errors,
    helperText,
    inputProps
}) => {
    const { i18n } = useTranslation();
    const getMuiTheme = () => createTheme({
        overrides: {
            MuiOutlinedInput: {
                adornedStart: {
                    paddingLeft: "0px"
                },
                adornedEnd: {
                    paddingRight: "0px"
                },
                multiline: {
                    padding: "0px"
                },

                marginDense: {
                    paddingTop: "0px!important",
                    paddingBottom: "0px!important"
                }
            },

            MuiInputBase: {
                marginDense: {
                    paddingTop: "0px"
                }
            },
            MuiButton: {
                root: {
                    paddingLeft: "6px",
                    paddingRight: "6px",
                    minWidth: "fit-content",
                    borderRadius: "0px",
                    border: "1px solid rgba(224, 224, 224, 1)"

                }
            }
        }
    })
    const handleOnChange = (e) => {
    }
    return (
        <MuiThemeProvider theme={getMuiTheme}>
            <C1InputField
                label={inputLabel}
                style={{ paddingRight: "0px" }}
                value={value || ""}
                name={name}
                disabled={disabled}
                onChange={handleOnChange}
                required={required}
                inputProps={inputProps}
                InputProps={{
                    endAdornment:
                        icon ? icon : <InputAdornment position="end" style={{ paddingRight: "8px" }}>
                            <Button
                                size="large"
                                disabled={disabled}
                                variant="contained"
                                component="label"
                                disableElevation
                            >
                                {label}
                                <input type={"file"} onChange={fileChangeHandler} hidden />
                            </Button>
                        </InputAdornment>,

                }}
                error={errors ? true : false}
                helperText={helperText}
            />
        </MuiThemeProvider>
    )

}

export default C1FileUpload;