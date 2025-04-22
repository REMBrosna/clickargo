import { makeStyles } from "@material-ui/core/styles";

const colorStyles = makeStyles(({ palette, ...theme }) => ({
  "@global": {
    "input[required], .C1-Required .MuiInputBase-root": {
      backgroundColor: 'rgba(224,224,229,0.39)'
    },
    "input[disabled]": {
      backgroundColor: palette.grey[1],
      color: palette.grey[700]
    },
    "div.Mui-disabled.MuiInputBase-root": {
      backgroundColor: palette.grey[100]
    },
    "label[Mui-disabled]": {
      color: palette.grey[900]
    },
    ".Mui-disabled.MuiOutlinedInput-multiline": {
      backgroundColor: "#ffffff",
      height: "136px"
    }
  },
}));

const customFormFieldStyles = makeStyles(({ palette, ...theme }) => ({
  root: {
    fontSize: theme.typography.fontSize + 3,
    color: palette.grey[900],
    "&.Mui-disabled": {
      color: palette.grey[900],
    },
  },
  asterisk: {
    color: '#db0726',
    fontSize: theme.typography.fontSize + 3,
  }

}), { name: "MuiFormLabel" })

const customFormFieldInput = makeStyles(({ palette, ...theme }) => ({
  input: {
    fontSize: theme.typography.fontSize + 2,
  }
}), { name: "MuiOutlinedInput" })

const customSelectFormFieldStyles = makeStyles(({ palette, ...theme }) => ({
  root: {
    "&.Mui-disabled": {
      backgroundColor: palette.grey[100],
      color: palette.grey[700]
    },
    "input[required]": {
      backgroundColor: '#faf7d4'
    }
  },

}), { name: "MuiSelect" })

const customTabStyles = makeStyles(({ palette, ...theme }) => ({
  root: {
    color: palette.grey[900],
    "min-width": "130px !important",
    "&.MuiTab-textColorPrimary": {
      color: palette.grey[700],
    },
    "&.Mui-selected": {
      color: "#1976d2"
    }
  },

}), { name: "MuiTab" })

const customTableRowStyles = makeStyles(({ palette, ...theme }) => ({
  root: {
    "& th": {
      "padding-left": "5px",
      "padding-right": "5px"
    },
    "& td": {
      "padding-left": "5px",
      "padding-right": "5px"
    }
  },

}), { name: "MuiTableRow" })

const customHelperTextStyle = makeStyles(({ palette, ...theme }) => ({

  root: {
    "margin-left": "2px",
  }
}), { name: "MuiFormHelperText" })

const C1GlobalCss = ({ children }) => {

  colorStyles();
  customFormFieldStyles();
  customFormFieldInput();
  customSelectFormFieldStyles();
  customTabStyles();
  customTableRowStyles();
  customHelperTextStyle();
  return children;
};

export default C1GlobalCss;