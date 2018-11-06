package com.terry.securityjpa.entity;

import com.terry.securityjpa.config.jpa.listener.CreateUpdateDTAuditEntityListener;
import com.terry.securityjpa.entity.embed.CreateUpdateDT;
import com.terry.securityjpa.entity.enumerated.RequestMatcherType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = {"createUpdateDT", "authoritySet"})
@ToString(exclude = {"authoritySet"})
@NoArgsConstructor
@Slf4j
@Entity
@EntityListeners(value={AuditingEntityListener.class, CreateUpdateDTAuditEntityListener.class})
@Table(name = "URL_RESOURCES")
@SequenceGenerator(name="UrlResourcesSequenceGenerator", sequenceName="SEQ_URL_RESOURCES", initialValue=1, allocationSize=1)
@Access(AccessType.FIELD)
public class UrlResources {

  private Long idx;

  @Column(name="RESOURCE_NAME")
  private String resourceName;

  @Column(name="RESOURCE_PATTERN")
  private String resourcePattern;

  @Column(name="REQUEST_MATCHER_TYPE")
  @Enumerated(EnumType.STRING)
  private RequestMatcherType requestMatcherType;

  @Column(name="ORDERNUM")
  private Short ordernum;

  @Embedded
  private CreateUpdateDT createUpdateDT;

  @Id
  @Column(name="IDX")
  @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="UrlResourcesSequenceGenerator")
  @Access(AccessType.PROPERTY)
  public Long getIdx() {
    return idx;
  }

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name="URL_RESOURCES_AUTHORITY"
      , joinColumns = {@JoinColumn(name="URL_RESOURCES_IDX")}
      , inverseJoinColumns = {@JoinColumn(name="AUTHORITY_IDX")})
  private Set<Authority> authoritySet = new HashSet<>();

  public void addAuthority(Authority authority) {
    this.authoritySet.add(authority);
    authority.getUrlResourcesSet().add(this);
  }

  public void removeAuthority(Authority authority) {
    if(authority.getUrlResourcesSet().contains(this))
      authority.getUrlResourcesSet().remove(this);
    this.authoritySet.remove(authority);
  }
}
