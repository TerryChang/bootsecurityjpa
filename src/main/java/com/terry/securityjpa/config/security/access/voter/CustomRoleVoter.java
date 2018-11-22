package com.terry.securityjpa.config.security.access.voter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.terry.securityjpa.config.cache.CacheService;
import com.terry.securityjpa.dto.MemberDTO;
import com.terry.securityjpa.entity.Role;

public class CustomRoleVoter implements AccessDecisionVoter<Object> {

  private CacheService cacheService;

  public CustomRoleVoter() {

  }

  public CustomRoleVoter(CacheService cacheService) {
    this.cacheService = cacheService;
  }

  public boolean supports(ConfigAttribute attribute) {
    if (attribute.getAttribute() != null) {
      return true;
    } else {
      return false;
    }
  }
  
  @Override
  public boolean supports(Class<?> clazz) {
    // TODO Auto-generated method stub
    return true;
  }

  @Override
  public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      return ACCESS_DENIED;
    }
    
    int result = ACCESS_ABSTAIN;
    Collection<? extends GrantedAuthority> authorities = extractAuthorities(authentication);

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
  
  /**
   * 로그인 한 사용자가 가진 모든 권한(사용자가 가지고 있는 Role에 대한 권한 and Role과는 무관하게 단독으로 가지고 있는 권한)들의 목록을 가져온다
   * @param authentication
   * @return
   */
  Collection<? extends GrantedAuthority> extractAuthorities(
      Authentication authentication) {
    
    Set<GrantedAuthority> result = new HashSet<>();
    
    MemberDTO memberDTO = (MemberDTO) authentication.getPrincipal(); // 로그인 계정정보
    Set<String> roleSet = memberDTO.getRoleSet(); // 계정정보 안에 저장되어 있는 Role 정보
    Map<Role, List<GrantedAuthority>> roleAuthorities = cacheService.getRoleAuthorities(); // cache에 저장되어 있는 Role 별 권한 정보
    Set<Role> keySet = roleAuthorities.keySet();
    
    for (String roleName : roleSet) {       // 계정정보 안에 저장되어 있는 로그인한 사람이 가지고 있는 Role들에 대한 이름
      for (Role role : keySet) {            // cache에 저장되어 있는 Role별 권한들 모음에서 Role들에 대한 keyset
        if (roleName.equals(role.getRoleName())) {  // 로그인 한 사람의 Role과 cache에 저장되어 Role을 찾으면(정확하게는 Role의 이름들로 비교)
          List<GrantedAuthority> grantedAuthorityList = roleAuthorities.get(role);  // cache에 저장되어 있는 Role에 대한 권한들들 가져온다
          result.addAll(grantedAuthorityList);
        }
      }
    }
    return result;
  }

}
