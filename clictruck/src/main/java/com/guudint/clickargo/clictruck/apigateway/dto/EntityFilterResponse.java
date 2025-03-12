package com.guudint.clickargo.clictruck.apigateway.dto;

import java.util.ArrayList;
/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
public class EntityFilterResponse {
    private ArrayList<Object> data;
    private int total;
    private int perPage;
    private int currentPage;
    private int totalPages;
    public ArrayList<Object> getData() {
        return data;
    }

    public void setData(ArrayList<Object> data) {
        this.data = data;
    }
    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
