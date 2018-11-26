package com.terry.securityjpa.config.security.access.voter;

import com.terry.securityjpa.config.cache.CacheService;
import com.terry.securityjpa.config.security.access.hierarchicalroles.CustomRoleHierarchy;
import com.terry.securityjpa.dto.MemberDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class CustomRoleHierarchyVoter extends CustomRoleVoter {

  CustomRoleHierarchy customRoleHierarchy;

  public CustomRoleHierarchyVoter(CacheService cacheService, CustomRoleHierarchy customRoleHierarchy) {
    super(cacheService);
    this.customRoleHierarchy = customRoleHierarchy;
  }

  @Override
  public boolean supports(ConfigAttribute attribute) {
    return super.supports(attribute);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return super.supports(clazz);
  }

  @Override
  public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {

    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      return ACCESS_DENIED;
    }

    int result = ACCESS_ABSTAIN;
    Collection<? extends GrantedAuthority> authorities = extractAuthorities(authentication); // 계정이 가지고 있는 모든 Role들의 하위 Role들이 접근할 수 있는 권한들의 모음

    for (ConfigAttribute attribute : attributes) {
      if (this.supports(attribute)) {
        result = ACCESS_DENIED;

        // Attempt to find a matching granted authority
        for (GrantedAuthority authority : authorities) {
          if (attribute.getAttribute().equals(authority.getAuthority())) {
            return ACCESS_GRANTED;
          }
        }
      }
    }

    return result;

  }

  @Override
  Collection<? extends GrantedAuthority> extractAuthorities(Authentication authentication) {

    MemberDTO memberDTO = (MemberDTO) authentication.getPrincipal(); // 로그인 계정정보
    Set<String> roleSet = memberDTO.getRoleSet(); // 계정정보 안에 저장되어 있는 Role 정보
    Collection<? extends GrantedAuthority> result = customRoleHierarchy.getReachableGrantedAuthorities(roleSet);

    return result;
  }
}
