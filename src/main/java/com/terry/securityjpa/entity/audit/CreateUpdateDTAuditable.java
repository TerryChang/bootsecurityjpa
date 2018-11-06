package com.terry.securityjpa.entity.audit;


import com.terry.securityjpa.entity.embed.CreateUpdateDT;

public interface CreateUpdateDTAuditable {
    public CreateUpdateDT getCreateUpdateDT();
    public void setCreateUpdateDT(CreateUpdateDT createUpdateDT);
}
