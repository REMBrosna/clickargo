import { Grid, makeStyles } from "@material-ui/core";
import React from "react";
import ButtonActiveHistory from "../molecules/ButtonActiveHistory";
import Colors from "../styles/color";
import ActionButton from "../atoms/ActionButton";
import PropTypes from "prop-types";

const GridActionButton = (props) => {
  const {
    showAddButton,
    showActiveHistoryButton,
    showMultiSelectActionButton
  } = props;

  const useStyles = makeStyles((theme) => ({
    addButton: {
      backgroundColor: Colors.ADD_BUTTON,
      color: "#fff",
      fontWeight: "bold",
      width: 110,
    },
    wrapper: {
      marginBottom: 20,
      marginTop: 10,
    },
    additionalButtonWrapper: {
      display: "flex",
      flexDirection: "row",
      alignItems: "center",
      justifyContent: "center",
      minWidth: "100px",
    },
  }));

  const classNames = useStyles();

  return (
    <>
      <Grid
        container
        justifyContent="space-between"
        alignItems="flex-end"
        className={classNames.wrapper}
      >
        <Grid item>
          {showActiveHistoryButton && (
            <ButtonActiveHistory handleAction={showActiveHistoryButton} />
          )}
        </Grid>
        <Grid item>
          <Grid container spacing={1} direction="row">
            {showAddButton &&
              showAddButton.map((item, index) => {
                return (
                  item?.show !== false && (
                    <Grid item key={index}>
                      <Grid>
                        <ActionButton
                          variant="contained"
                          icon={item.icon}
                          handleAction={item.action}
                        >
                          {item.label}
                        </ActionButton>
                      </Grid>
                    </Grid>
                  )
                );
              })}
            {showMultiSelectActionButton && (
              <Grid item>
                {showMultiSelectActionButton.map((item, index) => {
                  return (
                    item?.show !== false && (
                        <ActionButton
                          variant="contained"
                          icon={item.icon}
                          handleAction={item.action}
                        >
                          {item.label}
                        </ActionButton>
                    )
                  );
                })}
              </Grid>
            )}
          </Grid>
        </Grid>
      </Grid>
    </>
  );
};

GridActionButton.propTypes = {
  showActiveHistoryButton: PropTypes.func,
  showAddButton: PropTypes.array,
  // showAddButton: PropTypes.shape({
  //     show: PropTypes.bool,
  //     label: PropTypes.string,
  //     icon: PropTypes.element,
  //     action: PropTypes.func
  // }),
};

export default GridActionButton;
