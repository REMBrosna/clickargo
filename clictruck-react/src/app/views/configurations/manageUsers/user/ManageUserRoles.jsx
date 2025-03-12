import { Typography } from "@material-ui/core";
import Grid from "@material-ui/core/Grid";
import { useTheme } from "@material-ui/core/styles";
import React, { useEffect, useState } from "react";
import { DragDropContext, Draggable, Droppable } from "react-beautiful-dnd";

import { Roles } from "app/c1utils/const";
import { useStyles } from "app/c1utils/styles";
import { isEditable } from "app/c1utils/utility";
import useAuth from "app/hooks/useAuth";

// fake data generator
const getItems = (roleList) => {
    return roleList.map((v) => ({
        id: `${v.id.roleId}`,
        content: `${v.roleDesc}`,
    }));
};

const getRoleList = (items) => {
    return items.map((v) => ({
        id: { roleId: `${v.id}` },
        roleDesc: v.content,
    }));
};

// a little function to help us with reordering the result
const reorder = (list, startIndex, endIndex) => {
    const result = Array.from(list);
    const [removed] = result.splice(startIndex, 1);
    result.splice(endIndex, 0, removed);

    return result;
};

/**
 * Moves an item from one list to another list.
 */
const move = (source, destination, droppableSource, droppableDestination) => {
    const sourceClone = Array.from(source);
    const destClone = Array.from(destination);
    const [removed] = sourceClone.splice(droppableSource.index, 1);

    destClone.splice(droppableDestination.index, 0, removed);

    const result = {};
    result[droppableSource.droppableId] = sourceClone;
    result[droppableDestination.droppableId] = destClone;

    return result;
};

const grid = 8;

const getItemStyle = (isDragging, draggableStyle, theme) => ({
    // some basic styles to make the items look a bit nicer
    userSelect: "none",
    padding: grid * 2,
    margin: `0 0 ${grid}px 0`,
    boxShadow: theme.shadows[4],
    borderRadius: "4px",
    // change background colour if dragging
    background: isDragging ? "rgba(var(--primary),1)" : "rgba(var(--bg-paper),1)",

    // styles we need to apply on draggables
    ...draggableStyle,
});

const getListStyle = (isDraggingOver) => ({
    borderRadius: "4px",
    //background: isDraggingOver ? "rgba(0,0,0, .1)" : "rgba(var(--bg-default),1)",
    background: "rgba(0,0,0, .1)",
    padding: grid,
    width: 250,
});

const ManageUserRoles = ({
    notHoldRoleList,
    holdRoleList,
    handleRoleChange,
    viewType,
    isSubmitting,
    locale,
    errors,
}) => {
    const [items, setItems] = useState([]);
    const [selected, setSelected] = useState([]);
    const theme = useTheme();

    const classes = useStyles();    
    let isDisabled = isEditable(viewType, isSubmitting);
    
    if (viewType === 'newAll'){isDisabled = false};
    /**
     * Added so that other users that are not admin cannot update own roles when accessing Profile link
     */
    const { user } = useAuth();
    if (viewType !== "view") {
        let map = new Set(user.authorities.map((el) => el.authority));
        if (
            !(
                map.has(Roles.SYS_SUPER_ADMIN.code) ||
                map.has(Roles.ADMIN.code) ||
                map.has(Roles.SP_OP_ADMIN.code) ||
                map.has(Roles.SP_L1.code)||
                map.has(Roles.FF_CO_ADMIN.code) ||
                map.has(Roles.OP_ADMIN_WJ.code) ||
                map.has(Roles.OP_OFFICER.code)
                )
               
        ) {
            isDisabled = true;
        }
    }
   
    useEffect(() => {
        setItems(getItems(notHoldRoleList || []));
        setSelected(getItems(holdRoleList || []));
    }, []);


    const getList = (id) => (id === "droppable" ? items : selected);

    const onDragEnd = (result) => {
        const { source, destination } = result;

        // dropped outside the list
        if (!destination) {
            return;
        }

        if (source.droppableId === destination.droppableId) {
            const items = reorder(getList(source.droppableId), source.index, destination.index);
            if (source.droppableId === "droppable2") {
                setSelected(items);
            } else setItems(items);
        } else {
            const result = move(getList(source.droppableId),
                getList(destination.droppableId), source, destination);
            setItems(result.droppable);
            setSelected(result.droppable2);
            handleRoleChange(getRoleList(result.droppable2));
        }
    };


    return (
        <React.Fragment>
            {errors && errors.holdRoleList && (
                <Grid container spacing={6} alignItems="flex-start" className={classes.gridContainer}>
                    <Grid item lg={6} md={6} xs={12}>
                        <Typography variant="body2" color="error">
                            {errors.holdRoleList}
                        </Typography>
                    </Grid>
                </Grid>
            )}
            <DragDropContext onDragEnd={onDragEnd}>
                <Grid container spacing={6} alignItems="flex-start" className={classes.gridContainer}>
                    <Grid item lg={3} md={6} xs={12}>
                        <div>
                            <Droppable droppableId="droppable" isDropDisabled={isDisabled}>
                                {(provided, snapshot) => (
                                    <div ref={provided.innerRef} style={getListStyle(snapshot.isDraggingOver)}>
                                        <b className="m-3">{locale("user.details.roles.availableRoles")}</b>
                                        {items.map((item, index) => {
                                            return (
                                                <Draggable
                                                    key={item.id}
                                                    draggableId={item.id}
                                                    index={index}
                                                    isDragDisabled={isDisabled}
                                                >
                                                    {(provided, snapshot) => (
                                                        <div
                                                            ref={provided.innerRef}
                                                            {...provided.draggableProps}
                                                            {...provided.dragHandleProps}
                                                            style={getItemStyle(
                                                                snapshot.isDragging,
                                                                provided.draggableProps.style,
                                                                theme
                                                            )}
                                                        >
                                                            {item.content}
                                                        </div>
                                                    )}
                                                </Draggable>
                                            );
                                        })}
                                        {provided.placeholder}
                                    </div>
                                )}
                            </Droppable>
                        </div>
                    </Grid>
                    <Grid item lg={3} md={6} xs={12}>
                        <div>
                            <Droppable droppableId="droppable2" isDropDisabled={isDisabled}>
                                {(provided, snapshot) => (
                                    <div ref={provided.innerRef} style={getListStyle(snapshot.isDraggingOver)}>
                                        <b className="m-3">{locale("user.details.roles.selectedRoles")}</b>
                                        {selected.map((item, index) => (
                                            <Draggable
                                                key={item.id}
                                                draggableId={item.id}
                                                index={index}
                                                isDragDisabled={isDisabled}
                                            >
                                                {(provided, snapshot) => (
                                                    <div
                                                        ref={provided.innerRef}
                                                        {...provided.draggableProps}
                                                        {...provided.dragHandleProps}
                                                        style={getItemStyle(
                                                            snapshot.isDragging,
                                                            provided.draggableProps.style,
                                                            theme
                                                        )}
                                                    >
                                                        {item.content}
                                                    </div>
                                                )}
                                            </Draggable>
                                        ))}
                                        {provided.placeholder}
                                    </div>
                                )}
                            </Droppable>
                        </div>
                    </Grid>
                </Grid>
            </DragDropContext>
            <p className={classes.gridContainer}>
                {" "}
                {locale("user.details.roles.text1")} <br />
                {locale("user.details.roles.text2")}{" "}
            </p>
        </React.Fragment>
    );
};

export default ManageUserRoles;
