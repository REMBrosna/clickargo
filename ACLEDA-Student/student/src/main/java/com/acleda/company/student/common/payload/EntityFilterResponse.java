package com.acleda.company.student.common.payload;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
public class EntityFilterResponse {

    private int totalRecords;
    private int totalDisplayRecords;
    private ArrayList<Object> data;

    public int compareTo(EntityFilterResponse o) {
        return 0;
    }

    public void init() {
    }

}
