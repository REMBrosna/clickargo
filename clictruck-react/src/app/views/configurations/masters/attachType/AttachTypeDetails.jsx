import React from "react";
import Grid from "@material-ui/core/Grid";
import { isEditable, getValue } from "app/c1utils/utility";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import FormGroup from '@material-ui/core/FormGroup';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Switch from '@material-ui/core/Switch';

const AttachTypeDetails = ({
    inputData,
    viewType,
    isSubmitting,
    locale,
    errors,
    handleInputChange
}) => {

    let isDisabled = isEditable(viewType, isSubmitting);

    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid container spacing={3}>
                    <Grid item md={6} xs={12}>
                        <C1InputField
                            required
                            onChange={handleInputChange}
                            disabled={viewType === 'edit' || viewType === 'view'}
                            label={locale("masters:attachType.list.table.headers.mattId")}
                            name="mattId"
                            value={getValue(inputData?.mattId)}
                            error={errors?.["mattId"] ?? false}
                            helperText={errors?.["mattId"] ?? null}
                            inputProps={{
                                maxLength: 35
                            }}
                        />
                    </Grid>

                    <Grid item md={6} xs={12}>
                        <C1InputField
                            required
                            onChange={handleInputChange}
                            disabled={isDisabled}
                            label={locale("masters:attachType.list.table.headers.mattName")}
                            name="mattName"
                            value={getValue(inputData?.mattName)}
                            error={errors?.["mattName"] ?? false}
                            helperText={errors?.["mattName"] ?? null}
                            inputProps={{
                                maxLength: 128
                            }}
                        />
                    </Grid>

                    <Grid item md={6} xs={12}>
                        <C1InputField
                            required
                            multiline
                            rows={4}
                            onChange={handleInputChange}
                            disabled={isDisabled}
                            label={locale("masters:attachType.list.table.headers.mattDesc")}
                            name="mattDesc"
                            value={getValue(inputData?.mattDesc)}
                            error={errors?.["mattDesc"] ?? false}
                            helperText={errors?.["mattDesc"] ?? null}
                            inputProps={{
                                maxLength: 256
                            }}
                        />
                    </Grid>

                    <Grid item md={6} xs={12}>
                        <C1InputField
                            multiline
                            rows={4}
                            onChange={handleInputChange}
                            disabled={isDisabled}
                            label={locale("masters:attachType.list.table.headers.mattDescOth")}
                            name="mattDescOth"
                            value={getValue(inputData?.mattDescOth)}
                            error={errors?.["mattDescOth"] ?? false}
                            helperText={errors?.["mattDescOth"] ?? null}
                            inputProps={{
                                maxLength: 512
                            }}
                        />
                    </Grid>
                    <Grid item lg={4} md={6} xs={12} >
                        <Grid container alignItems="center" spacing={3}>
                            <Grid item xs={12} >
                                <FormGroup>
                                    <FormControlLabel control={<Switch checked={inputData?.mattExpiry === 'Y'}
                                        disabled={isDisabled}
                                        name="mattExpiry"
                                        onChange={handleInputChange}
                                    />} label={locale("masters:attachType.list.table.headers.mattExpiry")} />
                                </FormGroup>
                            </Grid>
                        </Grid>
                    </Grid>
                    <Grid item lg={4} md={6} xs={12} >
                        <Grid container alignItems="center" spacing={3}>
                            <Grid item xs={12} >
                                <FormGroup>
                                    <FormControlLabel control={<Switch checked={inputData?.mattRefNo === 'Y'}
                                        name="mattRefNo"
                                        onChange={handleInputChange}
                                        disabled={isDisabled} />}
                                        label={locale("masters:attachType.list.table.headers.mattRefNo")} />
                                </FormGroup>
                            </Grid>
                        </Grid>
                    </Grid>
                </Grid>
            </C1TabContainer>
        </React.Fragment>
    );
};

export default AttachTypeDetails;