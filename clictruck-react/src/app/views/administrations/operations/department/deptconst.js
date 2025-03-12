import ChipStatus from "app/atomics/atoms/ChipStatus";
import React from "react";

export const colorCodes = [
  { code: "0", value: "BLACK", hex: "#000000" },
  { code: "1", value: "RED", hex: "#E53935" },
  { code: "2", value: "GREEN", hex: "#4CAF50" },
  { code: "3", value: "BLUE", hex: "#1E88E5" },
  { code: "4", value: "YELLOW", hex: "#FFEE58" },
  { code: "5", value: "ORANGE", hex: "#FFA726" },
  { code: "6", value: "PURPLE", hex: "#CE93D8" },
  { code: "7", value: "WHITE", hex: "#FFFFFF" },
  { code: "8", value: "CYAN", hex: "#00FFFF" },
  { code: "9", value: "GREY", hex: "#BDBDBD" },
];

export function displayColor(code) {
  let item = colorCodes?.find((e) => e?.code === code);
  return (
    <ChipStatus
      label={item?.value}
      color={item?.code === "7" ? "secondary" : item?.hex}
    />
  );
}
