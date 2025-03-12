import { LinearGradient } from "expo-linear-gradient";
import React from "react";
import { materialTheme } from "../constants";

const CtLinearBackground = ({ children }) => {
    return <LinearGradient
        start={{ x: 0, y: 0 }}
        end={{ x: 1, y: 1 }}
        locations={[0.30, 0.70]}
        colors={[materialTheme.COLORS.CKSECONDARY, materialTheme.COLORS.WHITE]}
        style={{ width: "100%" }}  >
        {children}
    </LinearGradient>
}

export default CtLinearBackground;