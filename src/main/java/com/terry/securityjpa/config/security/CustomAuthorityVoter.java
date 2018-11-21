package com.terry.securityjpa.config.security;

import com.terry.securityjpa.config.cache.CacheService;
import com.terry.securityjpa.dto.MemberDTO;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

public class CustomAuthorityVoter implements AccessDecisionVoter<Object> {

  // private CacheService cacheService;

  public CustomAuthorityVoter() {

  }

  /*
   * public CustomAuthorityVoter(CacheService cacheService) { this.cacheService =
   * cacheService; }
   */

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

    for (ConfigAttribute attribute : attributes) {
      if (this.supports(attribute)) {
        result = ACCESS_DENIED;

        for (GrantedAuthority grantedAuthority : grantedAuthoritySet) {
          if (attribute.getAttribute().equals(grantedAuthority.getAuthority())) { // 별개 권한과 자원에 있는 권한이 같으면 접근 가능하다고
                                                                                  // 설정해준다
            return ACCESS_GRANTED;
          }
        }
      }
    }

    return result;
  }
}
