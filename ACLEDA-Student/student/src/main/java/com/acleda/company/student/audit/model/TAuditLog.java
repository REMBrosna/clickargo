package com.acleda.company.student.audit.model;

import com.acleda.company.student.common.COAbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "T_AUDITLOG")
public class TAuditLog extends COAbstractEntity<TAuditLog> {
    @Serial
    private static final long serialVersionUID = 3668506361378560130L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // or GenerationType.AUTO depending on DB
    @Column(name = "AUDT_ID")
    private Long audtId;
    @Column(name = "AUDT_EVENT", length = 25)
    private String audtEvent;
    @Column(name = "AUDT_TIMESTAMP", length = 19)
    private Date audtTimestamp;
    @Column(name = "AUDT_ACCNID", length = 35)
    private String audtAccnid;
    @Column(name = "AUDT_UID", length = 35)
    private String audtUid;
    @Column(name = "AUDT_UNAME", length = 35)
    private String audtUname;
    @Column(name = "AUDT_REMOTE_IP", length = 35)
    private String audtRemoteIp;
    @Column(name = "AUDT_RECKEY", length = 35)
    private String audtReckey;
    @Column(name = "AUDT_PARAM1", length = 225)
    private String audtParam1;
    @Column(name = "AUDT_PARAM2", length = 225)
    private String audtParam2;
    @Column(name = "AUDT_PARAM3", length = 225)
    private String audtParam3;
    @Column(name = "AUDT_REMARKS", length = 4096)
    private String audtRemarks;

    @Override
    public int compareTo(@NotNull TAuditLog o) {
        return 0;
    }

    @Override
    public void init() {

    }
}
