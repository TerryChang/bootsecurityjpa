package com.terry.securityjpa.dto;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.terry.securityjpa.entity.Authority;
import com.terry.securityjpa.entity.Member;
import com.terry.securityjpa.entity.Role;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class MemberDTO implements Serializable, UserDetails {

  private static final long serialVersionUID = 944515108867793813L;

  private final Member member;

  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<GrantedAuthority> grantedAuthoritySet = new HashSet<>();

  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<String> roleSet = new HashSet<>();

  public MemberDTO(Member member) {
    this.member = member;

    if (member.getAuthoritySet() != null) {
      Set<Authority> memberAuthoritySet = member.getAuthoritySet();
      for (Authority authority : memberAuthoritySet) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority.getAuthorityName());
        grantedAuthoritySet.add(grantedAuthority);
      }
    }

    if (member.getRoleSet() != null) {
      Set<Role> memberRoleSet = member.getRoleSet();
      for (Role role : memberRoleSet) {
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
    return member.getLoginPassword();
  }

  @Override
  public String getUsername() {
    return member.getLoginId();
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
