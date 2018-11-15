package com.terry.securityjpa.entity;

import com.terry.securityjpa.config.jpa.listener.CreateUpdateDTAuditEntityListener;
import com.terry.securityjpa.entity.embed.CreateUpdateDT;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.*;

@Data
@NoArgsConstructor
@Slf4j
@Entity
@EntityListeners(value={CreateUpdateDTAuditEntityListener.class})
@Table(name = "AUTHORITY")
@SequenceGenerator(name="AuthoritySequenceGenerator", sequenceName="SEQ_AUTHORITY", initialValue=1, allocationSize=1)
@Access(AccessType.FIELD)
public class Authority {

  private Long idx;

  @Column(name="AUTHORITY_NAME")
  private String authorityName;

  @Column(name="AUTHORITY_DESC")
  private String authorityDesc;

  @Embedded
  @EqualsAndHashCode.Exclude
  private CreateUpdateDT createUpdateDT;

  @Id
  @Column(name="IDX")
  @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="AuthoritySequenceGenerator")
  @Access(AccessType.PROPERTY)
  public Long getIdx() {
    return idx;
  }

  @ManyToMany(fetch=FetchType.LAZY, mappedBy = "authoritySet")
  @OrderBy("ordernum desc")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<UrlResources> urlResourcesSet = new LinkedHashSet<>();

  @ManyToMany(fetch=FetchType.LAZY, mappedBy = "authoritySet")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<Member> memberSet = new HashSet<>();

}
