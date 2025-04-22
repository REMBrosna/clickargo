import React from "react";
import Grid from "@material-ui/core/Grid";
import { useStyles } from "app/c1utils/styles";
import C1InputField from "app/c1component/C1InputField";
import { getValue } from "app/c1utils/utility";
import C1SelectField from "../../../c1component/C1SelectField";
import {MenuItem} from "@material-ui/core";
import C1DateField from "../../../c1component/C1DateField";

const UserDetail = ({
    viewType,
    inputData,
    handleDateChange,
    handleInputChange,
    errors,
    isSubmitting,
    locale
}) => {
    console.log("errors",errors)

    const classes = useStyles();
    const dropdownOptionsJobType = [
        { value: "M", desc: "Male" },
        { value: "F", desc: "Female" },
    ];
    return (
        <Grid container alignItems="flex-start" spacing={3} className={classes.gridContainer}>
            <Grid item lg={4} md={6} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12} >
                        <C1InputField
                            label={locale("userDetails.username")}
                            name="username"
                            disabled={viewType === "view"}
                            required
                            inputProps={{
                                maxLength: 35,
                                placeholder: locale("userDetails.enterYourUsername")
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.username)}
                            error={errors && errors['username'] !== undefined}
                            helperText={(errors && errors['username']) || ''} />

                        <C1InputField
                            label={locale("userDetails.email")}
                            disabled={viewType === "view"}
                            name="email"
                            required
                            inputProps={{
                                maxLength: 35,
                                placeholder: locale("userDetails.enterYourEmail")
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.email)}
                            error={errors && errors['email'] !== undefined}
                            helperText={(errors && errors['email']) || ''} />
                        <C1InputField
                            name="conNumber"
                            disabled={viewType === "view"}
                            type="number"
                            label={locale("userDetails.contactTel")}
                            onChange={(e) => handleInputChange(e)}
                            value={inputData?.conNumber}
                            error={errors && errors['conNumber'] !== undefined}
                            helperText={(errors && errors['conNumber']) || ''}
                        />
                    </Grid>
                </Grid>
            </Grid>

            <Grid item lg={4} md={6} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12}>
                        <C1InputField
                            label={locale("userDetails.firstName")}
                            disabled={viewType === "view"}
                            name="firstname"
                            required
                            inputProps={{
                                placeholder: locale("userDetails.enterYourFirstName")
                            }}
                            onChange={handleInputChange}
                            value={getValue(inputData?.firstname)}
                            error={errors && errors['firstname'] !== undefined}
                            helperText={(errors && errors['firstname']) || ''} />
                        <C1SelectField
                            name="gender"
                            disabled={viewType === "view"}
                            label={locale("user:user.details.gender")}
                            value={inputData?.gender ?? ""}
                            onChange={(e) => handleInputChange(e)}
                            isServer={false}
                            required={true}
                            optionsMenuItemArr={dropdownOptionsJobType?.map((item, ind) => (
                                <MenuItem value={item.value} key={ind}>
                                    {item.desc}
                                </MenuItem>
                            ))}
                            error={errors["gender"] !== undefined}
                            helperText={errors["gender"] || ""}
                        />
                        <C1DateField
                            label={locale("user:user.details.dateOfBirth")}
                            disabled={viewType === "view"}
                            name="dtOfBirth"
                            type="date"
                            required
                            onChange={handleDateChange}
                            value={inputData?.dtOfBirth}
                            disablePast={true}
                            error={errors["gender"] !== undefined}
                            helperText={errors["gender"] || ""}
                        />
                    </Grid>
                </Grid>
            </Grid>

            <Grid item lg={4} md={6} xs={12} >
                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                    <Grid item xs={12} >
                        <C1InputField
                            label={locale("userDetails.lastName")}
                            disabled={viewType === "view"}
                            name="lastname"
                            inputProps={{
                                placeholder: locale("userDetails.enterYourLastName")
                            }}
                            required
                            onChange={handleInputChange}
                            value={getValue(inputData?.lastname)}
                            error={errors && errors['lastname'] !== undefined}
                            helperText={(errors && errors['lastname']) || ''} />
                        <C1InputField
                            name="address"
                            disabled={viewType === "view"}
                            label={locale("user:user.details.address")}
                            onChange={(e) => handleInputChange(e)}
                            value={inputData?.address}
                            error={errors && errors['address'] !== undefined}
                            helperText={(errors && errors['address']) || ''}
                        />
                    </Grid>
                </Grid>
            </Grid>
        </Grid>
    );
};
export default UserDetail;