package com.acleda.company.student.infrastructure.api;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DataTableResponse<T> {
    // Getters and Setters
    private String sEcho;
    private long iTotalRecords;
    private long iTotalDisplayRecords;
    private List<T> aaData;

    // Constructor
    public DataTableResponse(String sEcho, long iTotalRecords, long iTotalDisplayRecords, List<T> aaData) {
        this.sEcho = sEcho;
        this.iTotalRecords = iTotalRecords;
        this.iTotalDisplayRecords = iTotalDisplayRecords;
        this.aaData = aaData;
    }

}