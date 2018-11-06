package com.terry.securityjpa.config.jpa.listener;

import com.terry.securityjpa.entity.audit.CreateUpdateDTAuditable;
import com.terry.securityjpa.entity.embed.CreateUpdateDT;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

public class CreateUpdateDTAuditEntityListener {

    @PrePersist
    public void setCreateOn(Object o) {
        /*
        CreateUpdateDTAuditable audit = (CreateUpdateDTAuditable)o;
        CreateUpdateDT createUpdateDT = audit.getCreateUpdateDT();
        if(createUpdateDT == null) {
            createUpdateDT = new CreateUpdateDT();
            audit.setCreateUpdateDT(createUpdateDT);
        }

        createUpdateDT.setCreateDT(LocalDateTime.now());
        createUpdateDT.setUpdateDT(null);
        */
        CreateUpdateDTAuditable audit = (CreateUpdateDTAuditable)o;
        CreateUpdateDT createUpdateDT = new CreateUpdateDT();
        createUpdateDT.setCreateDT(LocalDateTime.now());
        createUpdateDT.setUpdateDT(null);
        audit.setCreateUpdateDT(createUpdateDT);
    }

    @PreUpdate
    public void setUpdateOn(Object o) {
        /*
        CreateUpdateDTAuditable audit = (CreateUpdateDTAuditable)o;
        CreateUpdateDT createUpdateDT = audit.getCreateUpdateDT();
        createUpdateDT.setUpdateDT(LocalDateTime.now());
        */
        CreateUpdateDTAuditable audit = (CreateUpdateDTAuditable)o;
        CreateUpdateDT createUpdateDT = new CreateUpdateDT();
        createUpdateDT.setCreateDT(null);
        createUpdateDT.setUpdateDT(LocalDateTime.now());
        audit.setCreateUpdateDT(createUpdateDT);
    }
}
