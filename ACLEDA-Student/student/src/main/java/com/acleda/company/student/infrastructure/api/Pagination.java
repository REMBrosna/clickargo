package com.acleda.company.student.infrastructure.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class Pagination {
    @JsonProperty("page")
    @ApiParam(required = false, value = "page", defaultValue = "1", example = "1")
    private Integer page;
    @JsonProperty("size")
    @ApiParam(required = false, value = "size", defaultValue = "15", example = "15")
    private Integer size;
    @JsonProperty("total_pages")
    @ApiParam(hidden = true)
    private Long totalPages;
    @JsonProperty("total_counts")
    @ApiParam(hidden = true)
    private Long totalCounts;

    public Pagination() {
        this(1, 15, 0L, 0L);
    }

    public Long getTotalPages() {
        return (long) Math.ceil((double) this.totalCounts / size);
    }

    @JsonIgnore
    @ApiParam(hidden = true)
    public Integer getOffSet() {
        return this.page * this.size;
    }

}
