package com.terry.securityjpa.config.security.access.expression;

import com.terry.securityjpa.config.security.access.hierarchicalroles.CustomRoleHierarchy;
import com.terry.securityjpa.dto.MemberDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CustomWebSecurityExpressionRoot extends WebSecurityExpressionRoot {

  private final CustomRoleHierarchy customRoleHierarchy;

  public CustomWebSecurityExpressionRoot(Authentication a, FilterInvocation fi, CustomRoleHierarchy customRoleHierarchy) {
    super(a, fi);
    this.customRoleHierarchy = customRoleHierarchy;
  }

  public boolean customHasAuthority(String authority) {
    return customHasAnyAuthority(authority);
  }

  public boolean customHasAnyAuthority(String... authorities) {
    MemberDTO memberDTO = (MemberDTO)super.authentication.getPrincipal();
    Set<GrantedAuthority> grantedAuthoritySet = memberDTO.getGrantedAuthoritySet(); // 계정정보 안에 저장되어 있는 별개 권한 정보
    Set<String> roleSet = memberDTO.getRoleSet(); // 계정정보 안에 저장되어 있는 Role 정보
    Collection<? extends GrantedAuthority> roleAuthority = customRoleHierarchy.getReachableGrantedAuthorities(roleSet); // role 들이 가지고 있는 권한 정보

    // 계정정보 안에 저장되어 있는 별개 권한 정보와 role들이 가지고 있는 권한 정보를 병합한 Set을 하나 만든다
    Set<GrantedAuthority> totalAuthoritySet = new HashSet<>();
    totalAuthoritySet.addAll(grantedAuthoritySet);
    totalAuthoritySet.addAll(roleAuthority);

    for(GrantedAuthority grantedAuthority : totalAuthoritySet) {
      String grantedAuthorityName = grantedAuthority.getAuthority();
      for(String authority : authorities) {
        if(grantedAuthorityName.equals(authority)) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean customHasRole(String role) {
    return customHasAnyRole(role);
  }

  public boolean customHasAnyRole(String... roles) {
    MemberDTO memberDTO = (MemberDTO)(super.authentication.getPrincipal());
    Set<String> roleSet = memberDTO.getRoleSet();
    for(String role : roles) {
      for(String setRole : roleSet) {
        if(role.equals(setRole)) {
          return true;
        }
      }
    }
    return false;
  }
}
