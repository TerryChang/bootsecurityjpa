package com.terry.securityjpa.config.security.access.voter;

import com.terry.securityjpa.config.cache.CacheService;
import com.terry.securityjpa.config.security.access.hierarchicalroles.CustomRoleHierachyImpl;
import com.terry.securityjpa.dto.MemberDTO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class CustomRoleHierachyVoter extends CustomRoleVoter {

  public CustomRoleHierachyVoter(CacheService cacheService) {
    super(cacheService);
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
    Set<GrantedAuthority> result = new HashSet<>();

    MemberDTO memberDTO = (MemberDTO) authentication.getPrincipal(); // 로그인 계정정보
    Set<String> roleSet = memberDTO.getRoleSet(); // 계정정보 안에 저장되어 있는 Role 정보

    Map<String, Set<GrantedAuthority>> roleHierarchyAuthorities = cacheService.getRoleHierarchyAuthorities(); // 특정 Role에 대한 모든 하위 Role이 접근할 수 있는 권한들의 모음
    Set<String> keySet = roleHierarchyAuthorities.keySet();

    for (String roleName : roleSet) {       // 계정정보 안에 저장되어 있는 로그인한 사람이 가지고 있는 Role들에 대한 이름
      for (String role : keySet) {            // cache에 저장되어 있는 특정 Role별 하위 Role이 접근할 수 있는 권한들의 모음에서 Role들에 대한 keyset
        if (roleName.equals(role)) {  // 로그인 한 사람의 Role과 cache에 저장되어 Role을 찾으면(정확하게는 Role의 이름들로 비교)
          Set<GrantedAuthority> grantedAuthoritySet = roleHierarchyAuthorities.get(role);  // cache에 저장되어 있는 Role에 대한 권한들들 가져온다
          result.addAll(grantedAuthoritySet);
        }
      }
    }

    return result;
  }
}
