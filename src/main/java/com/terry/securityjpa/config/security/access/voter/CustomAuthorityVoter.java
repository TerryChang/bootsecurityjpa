package com.terry.securityjpa.config.security.access.voter;

import com.terry.securityjpa.config.cache.CacheService;
import com.terry.securityjpa.config.security.access.hierarchicalroles.CustomRoleHierarchy;
import com.terry.securityjpa.dto.MemberDTO;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CustomAuthorityVoter implements AccessDecisionVoter<Object> {

  private CustomRoleHierarchy customRoleHierarchy;

  public CustomAuthorityVoter(CustomRoleHierarchy customRoleHierarchy) {
    this.customRoleHierarchy = customRoleHierarchy;
  }

  @Override
  public boolean supports(ConfigAttribute attribute) {
    if (attribute.getAttribute() != null) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return true;
  }

  @Override
  public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      return ACCESS_DENIED;
    }
    
    int result = ACCESS_ABSTAIN;

    MemberDTO memberDTO = (MemberDTO) authentication.getPrincipal(); // 로그인 계정정보
    Set<GrantedAuthority> grantedAuthoritySet = memberDTO.getGrantedAuthoritySet(); // 계정정보 안에 저장되어 있는 별개 권한 정보
    Set<String> roleSet = memberDTO.getRoleSet(); // 계정정보 안에 저장되어 있는 Role 정보
    Collection<? extends GrantedAuthority> roleAuthority = customRoleHierarchy.getReachableGrantedAuthorities(roleSet); // role 들이 가지고 있는 권한 정보

    // 계정정보 안에 저장되어 있는 별개 권한 정보와 role들이 가지고 있는 권한 정보를 병합한 Set을 하나 만든다
    Set<GrantedAuthority> totalAuthoritySet = new HashSet<>();
    totalAuthoritySet.addAll(grantedAuthoritySet);
    totalAuthoritySet.addAll(roleAuthority);


    for (ConfigAttribute attribute : attributes) {
      if (this.supports(attribute)) {
        result = ACCESS_DENIED;

        // 별개 권한 role 권한이 통합되어 있는 권한 Set에 들어 있는 권한과 자원에 있는 권한이 같으면 접근 가능하다고 설정해준다
        for (GrantedAuthority grantedAuthority : totalAuthoritySet) {
          if (attribute.getAttribute().equals(grantedAuthority.getAuthority())) {
            return ACCESS_GRANTED;
          }
        }
      }
    }

    return result;
  }
}
