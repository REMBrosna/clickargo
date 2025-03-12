package com.guudint.clickargo.clictruck.planexec.job.dto;

import java.util.ArrayList;
import java.util.List;

import com.guudint.clickargo.common.enums.JobActions;

public class MultiRecordResponse {

    private JobActions action;
    private String accType, role;
    private List<String> id, success;
    private List<FailedDescription> failed;
    private int noSuccess, noFailed;
    private boolean isSuspended;

    public boolean isSuspended() {
        return isSuspended;
    }

    public void setSuspended(boolean isSuspended) {
        this.isSuspended = isSuspended;
    }

    public JobActions getAction() {
        return action;
    }

    public void setAction(JobActions action) {
        this.action = action;
    }

    public String getAccType() {
        return accType;
    }

    public void setAccType(String accType) {
        this.accType = accType;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getId() {
        if (id == null) {
            id = new ArrayList<>();
        }
        return id;
    }

    public void setId(List<String> id) {
        this.id = id;
    }

    public List<String> getSuccess() {
        if (success == null) {
            success = new ArrayList<>();
        }
        return success;
    }

    public void setSuccess(List<String> success) {
        this.success = success;
    }

    public List<FailedDescription> getFailed() {
        if (failed == null) {
            failed = new ArrayList<>();
        }
        return failed;
    }

    public void setFailed(List<FailedDescription> failed) {
        this.failed = failed;
    }

    public int getNoSuccess() {
        return noSuccess;
    }

    public void setNoSuccess(int noSuccess) {
        this.noSuccess = noSuccess;
    }

    public int getNoFailed() {
        return noFailed;
    }

    public void setNoFailed(int noFailed) {
        this.noFailed = noFailed;
    }

}
