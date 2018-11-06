package com.terry.securityjpa.dto;

import com.terry.securityjpa.entity.Authority;
import com.terry.securityjpa.entity.Member;
import com.terry.securityjpa.entity.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Data
@NoArgsConstructor
@Slf4j
public class MemberDTO implements UserDetails {

  private Long idx;
  private String name;
  private String loginId;
  private String loginPassword;
  @EqualsAndHashCode.Exclude
  private LocalDateTime createDT;
  @EqualsAndHashCode.Exclude
  private LocalDateTime updateDT;

  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<GrantedAuthority> grantedAuthoritySet = new HashSet<>();
  
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<String> roleSet = new HashSet<>();

  public MemberDTO(Member member) {
    this.idx = member.getIdx();
    this.name = member.getName();
    this.loginId = member.getLoginId();
    this.loginPassword = member.getLoginPassword();
    this.createDT = member.getCreateUpdateDT().getCreateDT();
    this.updateDT = member.getCreateUpdateDT().getUpdateDT();

    if(member.getAuthoritySet() != null) {
      Set<Authority> memberAuthoritySet = member.getAuthoritySet();
      for(Authority authority : memberAuthoritySet) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority.getAuthorityName());
        grantedAuthoritySet.add(grantedAuthority);
      }
    }

    if(member.getRoleSet() != null) {
      Set<Role> memberRoleSet = member.getRoleSet();
      for(Role role : memberRoleSet) {
        String roleName = role.getRoleName();
        roleSet.add(roleName);
      }
    }
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return getGrantedAuthoritySet();
  }

  @Override
  public String getPassword() {
    return getLoginPassword();
  }

  @Override
  public String getUsername() {
    return getLoginId();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
