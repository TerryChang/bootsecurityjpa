package com.terry.securityjpa.entity.embed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class CreateUpdateDT {

    @Column(name="CREATE_DT", nullable = false, updatable = false)
    private LocalDateTime createDT;

    @Column(name="UPDATE_DT", insertable = false, updatable = true)
    private LocalDateTime updateDT;

}
