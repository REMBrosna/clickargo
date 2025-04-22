
import { makeStyles } from '@material-ui/core/styles';

export const useStyles = makeStyles({
    gridContainer: {
        paddingLeft: 10,
        paddingRight: 10
    },
    gridContainerFormRow: {
        marginBottom: 20
    }
});


export const titleTab = makeStyles(theme => ({
    root: {
      boxShadow: "none",
      backgroundColor: "white" ,  
      color: '#3C77D0',
      borderBottom: "0.5px solid #bdc0c5",
      lineHeight: "1.9",
      marginTop: "5px",
      fontFamily: [
        "Roboto", "Helvetica", "Arial", "sans-serif", "Khmer OS Siemreap"
      ].join(','),
      fontSize:'0.875rem',
      fontWeight:500,
      letterSpacing:'0.02857em',
      whiteSpace:'normal',
      marginBottom: 0
    } 
  }));


  
  export const buttonStyles = makeStyles(theme => ({
    root: {
        '& > *': {
            margin: theme.spacing(1),
            fontFamily: [
                "Roboto", "Helvetica", "Arial", "sans-serif", "Khmer OS Siemreap"
              ].join(','),
        },
    },
    buttonSpace: {
        float:"right"
    },
}));
