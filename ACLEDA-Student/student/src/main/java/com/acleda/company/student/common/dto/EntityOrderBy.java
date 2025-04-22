package com.acleda.company.student.common.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EntityOrderBy {

    private String attribute;
    private ORDERED ordered;

    public static enum ORDERED {
        ASC,
        DESC;

        private ORDERED() {
        }
    }

    public String toString() {
        return " order by o." + this.attribute + " " + this.ordered.toString().toLowerCase();
    }
}
