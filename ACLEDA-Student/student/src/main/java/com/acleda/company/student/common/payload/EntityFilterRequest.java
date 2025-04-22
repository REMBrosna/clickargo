package com.acleda.company.student.common.payload;


import com.acleda.company.student.common.dto.EntityOrderBy;
import com.acleda.company.student.common.dto.EntityWhere;

import java.util.ArrayList;

public class EntityFilterRequest {

    private int displayStart;
    private int displayLength;
    private EntityOrderBy orderBy;
    private ArrayList<EntityWhere> whereList;
    private int columns;
    private int totalRecords;

    public EntityFilterRequest() {
    }

    public int compareTo(EntityFilterRequest o) {
        return 0;
    }

    public void init() {
    }

    public boolean isValid() {
        boolean valid = true;
        if (this.displayStart < 0) {
            valid = false;
        }

        if (this.displayLength < 0) {
            valid = false;
        }

        if (this.orderBy == null) {
            valid = false;
        }

        return valid;
    }

    public int getDisplayStart() {
        return displayStart;
    }

    public void setDisplayStart(int displayStart) {
        this.displayStart = displayStart;
    }

    public int getDisplayLength() {
        return displayLength;
    }

    public void setDisplayLength(int displayLength) {
        this.displayLength = displayLength;
    }

    public EntityOrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(EntityOrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public ArrayList<EntityWhere> getWhereList() {
        return whereList;
    }

    public void setWhereList(ArrayList<EntityWhere> whereList) {
        this.whereList = whereList;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }
}
