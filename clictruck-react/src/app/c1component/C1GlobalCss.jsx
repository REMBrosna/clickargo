import { makeStyles } from "@material-ui/core/styles";

const colorStyles = makeStyles(({ palette, ...theme }) => ({
  "@global": {
    "input[required], .C1-Required .MuiInputBase-root": {
      // backgroundColor: '#faf7d4',
      backgroundColor: '#e7f4fd'
    },
    "input[disabled]": {
      backgroundColor: palette.grey[100],
      color: palette.grey[700]
    },
    "div.Mui-disabled.MuiInputBase-root": {
      backgroundColor: palette.grey[100]
    },
    "label[Mui-disabled]": {
      color: palette.grey[900]
    },
    ".Mui-disabled.MuiOutlinedInput-multiline": {
      backgroundColor: "#f5f5f5",
      height: "136px"
    },
    "div.react-multi-carousel-list": {
      display: 'block',
      alignItems: 'center',
      overflow: 'hidden',
      position: 'relative'
    },
    ".MuiIconButton-colorPrimary": {
      color: '#0a72ba'
    },
    ".FullHeight" : {
      minHeight:"100vh"
    },
    ".FullHeight100p" : {
      height:"100%",
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
      // backgroundColor: '#faf7d4'
      backgroundColor: '#e7f4fd'
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

const customGirdStyles = makeStyles(({ palette, ...theme }) => ({
  root: {
    //  height: '100%',
  },

}), { name: "MuiGrid" })

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
  customGirdStyles()
  return children;
};

export default C1GlobalCss;