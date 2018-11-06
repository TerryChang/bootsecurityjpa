package com.terry.securityjpa.entity;

import com.terry.securityjpa.config.jpa.listener.CreateUpdateDTAuditEntityListener;
import com.terry.securityjpa.entity.audit.CreateUpdateDTAuditable;
import com.terry.securityjpa.entity.embed.CreateUpdateDT;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = {"createUpdateDT", "roleSet", "authoritySet"})
@ToString(exclude = {"roleSet", "authoritySet"})
@NoArgsConstructor
@Slf4j
@Entity
@EntityListeners(value={CreateUpdateDTAuditEntityListener.class})
@Table(name = "MEMBER")
@SequenceGenerator(name="MemberSequenceGenerator", sequenceName="SEQ_MEMBER", initialValue=1, allocationSize=1)
@Access(AccessType.FIELD)
public class Member implements CreateUpdateDTAuditable {

  private Long idx;

  @Column(name="NAME")
  private String name;

  @Column(name="LOGIN_ID")
  private String loginId;

  @Column(name="LOGIN_PASSWORD")
  private String loginPassword;

  @Embedded
  private CreateUpdateDT createUpdateDT;

  @Id
  @Column(name="IDX")
  @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="MemberSequenceGenerator")
  @Access(AccessType.PROPERTY)
  public Long getIdx() {
    return idx;
  }

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name="MEMBER_ROLE"
      , joinColumns = {@JoinColumn(name="MEMBER_IDX")}
      , inverseJoinColumns = {@JoinColumn(name="ROLE_IDX")})
  private Set<Role> roleSet = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name="MEMBER_AUTHORITY"
      , joinColumns = {@JoinColumn(name="MEMBER_IDX")}
      , inverseJoinColumns = {@JoinColumn(name="AUTHORITY_IDX")})
  private Set<Authority> authoritySet = new HashSet<>();

  public void addRole(Role role) {
    role.getMemberSet().add(this);
    roleSet.add(role);
  }

  public void removeRole(Role role) {
    if(role.getMemberSet().contains(this)) {
      role.getMemberSet().remove(this);
    }
    roleSet.remove(role);
  }

  public void addAuthority(Authority authority) {
    authority.getMemberSet().add(this);
    authoritySet.add(authority);
  }

  public void removeAuthority(Authority authority) {
    if(authority.getMemberSet().contains(this)) {
      authority.getMemberSet().remove(this);
    }
    authoritySet.remove(authority);
  }

}
