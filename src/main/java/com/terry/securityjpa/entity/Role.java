package com.terry.securityjpa.entity;

import com.terry.securityjpa.config.jpa.listener.CreateUpdateDTAuditEntityListener;
import com.terry.securityjpa.entity.embed.CreateUpdateDT;
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
@EqualsAndHashCode(exclude = {"createUpdateDT", "authoritySet", "parentRole", "childRoleSet", "memberSet"})
@ToString(exclude = {"authoritySet", "parentRole", "childRoleSet", "memberSet"})
@NoArgsConstructor
@Slf4j
@Entity
@EntityListeners(value={AuditingEntityListener.class, CreateUpdateDTAuditEntityListener.class})
@Table(name = "ROLE")
@SequenceGenerator(name="RoleSequenceGenerator", sequenceName="SEQ_ROLE", initialValue=1, allocationSize=1)
@Access(AccessType.FIELD)
public class Role {

  private Long idx;

  @Column(name="ROLE_NAME")
  private String roleName;

  @Column(name="ROLE_DESC")
  private String roleDesc;

  @Embedded
  private CreateUpdateDT createUpdateDT;

  @Id
  @Column(name="IDX")
  @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="RoleSequenceGenerator")
  @Access(AccessType.PROPERTY)
  public Long getIdx() {
    return idx;
  }

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name="ROLE_AUTHORITY"
      , joinColumns = {@JoinColumn(name="ROLE_IDX")}
      , inverseJoinColumns = {@JoinColumn(name="AUTHORITY_IDX")})
  private Set<Authority> authoritySet = new HashSet<>();

  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name = "PARENT_ROLE_IDX")
  private Role parentRole;

  @OneToMany(fetch=FetchType.LAZY, mappedBy = "parentRole")
  private Set<Role> childRoleSet = new HashSet<>();

  @ManyToMany(fetch=FetchType.LAZY, mappedBy = "roleSet")
  private Set<Member> memberSet = new HashSet<>();

  public void addAuthority(Authority authority) {
    this.authoritySet.add(authority);
  }

  public void removeAuthority(Authority authority) {
    this.authoritySet.remove(authority);
  }
}
