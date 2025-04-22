package com.acleda.company.student.audit.dto;


import com.acleda.company.student.audit.model.TAuditLog;
import com.acleda.company.student.common.AbstractDTO;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.util.Date;

public class AuditLog extends AbstractDTO<AuditLog, TAuditLog> {
    @Serial
    private static final long serialVersionUID = 3668506361378560130L;
    private String audtId;
    private String audtEvent;
    private Date audtTimestamp;
    private String audtAccnid;
    private String audtUid;
    private String audtUname;
    private String audtRemoteIp;
    private String audtReckey;
    private String audtParam1;
    private String audtParam2;
    private String audtParam3;
    private String audtRemarks;

    @Override
    public void init() {

    }

    public AuditLog() {
    }

    public AuditLog(TAuditLog entity) {
        super(entity);
    }

    public AuditLog(String audtId, String audtEvent, Date audtTimestamp, String audtAccnid, String audtUid, String audtUname, String audtRemoteIp, String audtReckey, String audtParam1, String audtParam2, String audtParam3, String audtRemarks) {
        this.audtId = audtId;
        this.audtEvent = audtEvent;
        this.audtTimestamp = audtTimestamp;
        this.audtAccnid = audtAccnid;
        this.audtUid = audtUid;
        this.audtUname = audtUname;
        this.audtRemoteIp = audtRemoteIp;
        this.audtReckey = audtReckey;
        this.audtParam1 = audtParam1;
        this.audtParam2 = audtParam2;
        this.audtParam3 = audtParam3;
        this.audtRemarks = audtRemarks;
    }

    public String getAudtId() {
        return audtId;
    }

    public void setAudtId(String audtId) {
        this.audtId = audtId;
    }

    public String getAudtEvent() {
        return audtEvent;
    }

    public void setAudtEvent(String audtEvent) {
        this.audtEvent = audtEvent;
    }

    public Date getAudtTimestamp() {
        return audtTimestamp;
    }

    public void setAudtTimestamp(Date audtTimestamp) {
        this.audtTimestamp = audtTimestamp;
    }

    public String getAudtAccnid() {
        return audtAccnid;
    }

    public void setAudtAccnid(String audtAccnid) {
        this.audtAccnid = audtAccnid;
    }

    public String getAudtUid() {
        return audtUid;
    }

    public void setAudtUid(String audtUid) {
        this.audtUid = audtUid;
    }

    public String getAudtUname() {
        return audtUname;
    }

    public void setAudtUname(String audtUname) {
        this.audtUname = audtUname;
    }

    public String getAudtRemoteIp() {
        return audtRemoteIp;
    }

    public void setAudtRemoteIp(String audtRemoteIp) {
        this.audtRemoteIp = audtRemoteIp;
    }

    public String getAudtReckey() {
        return audtReckey;
    }

    public void setAudtReckey(String audtReckey) {
        this.audtReckey = audtReckey;
    }

    public String getAudtParam1() {
        return audtParam1;
    }

    public void setAudtParam1(String audtParam1) {
        this.audtParam1 = audtParam1;
    }

    public String getAudtParam2() {
        return audtParam2;
    }

    public void setAudtParam2(String audtParam2) {
        this.audtParam2 = audtParam2;
    }

    public String getAudtParam3() {
        return audtParam3;
    }

    public void setAudtParam3(String audtParam3) {
        this.audtParam3 = audtParam3;
    }

    public String getAudtRemarks() {
        return audtRemarks;
    }

    public void setAudtRemarks(String audtRemarks) {
        this.audtRemarks = audtRemarks;
    }

    @Override
    public int compareTo(@NotNull AuditLog o) {
        return 0;
    }
}
