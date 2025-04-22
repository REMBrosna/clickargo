import React from "react";
import styled, {css} from "styled-components";
import {Tab} from "@material-ui/core";

export const TabsWrapper = styled(Tab)`
    ${props => props.disabled && css`
        pointer-events: all !important;
        cursor: not-allowed !important;
        &:hover {
            color: #b93535 !important
        }`
    }
    ${props => props?.isInvalid &&  css`
        color: #e60606 !important
    `}
`;