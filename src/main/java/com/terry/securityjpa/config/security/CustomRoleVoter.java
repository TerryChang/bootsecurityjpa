package com.terry.securityjpa.config.security;

import com.terry.securityjpa.config.cache.CacheService;
import com.terry.securityjpa.dto.MemberDTO;
import com.terry.securityjpa.entity.Role;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CustomRoleVoter extends RoleVoter {

  private CacheService cacheService;

  public CustomRoleVoter() {

  }

  public CustomRoleVoter(CacheService cacheService) {
    this.cacheService = cacheService;
  }

  @Override
  public boolean supports(ConfigAttribute attribute) {
    if (attribute.getAttribute() != null) {
      return true;
    }
    else {
      return false;
    }
  }

  @Override
  public int vote(Authentication authentication, Object object,
                  Collection<ConfigAttribute> attributes) {
    if(authentication == null) {
      return ACCESS_DENIED;
    }
    int result = ACCESS_ABSTAIN;

    MemberDTO memberDTO = (MemberDTO)authentication.getPrincipal();                         // 로그인 계정정보
    Set<String> roleSet = memberDTO.getRoleSet();                                          // 계정정보 안에 저장되어 있는 Role 정보
    Map<Role, List<GrantedAuthority>> roleAuthorities = cacheService.getRoleAuthorities();  // cache에 저장되어 있는 Role 별 권한 정보
    Set<Role> keySet = roleAuthorities.keySet();

    for (ConfigAttribute attribute : attributes) {
      if (this.supports(attribute)) {
        result = ACCESS_DENIED;

        for(String roleName : roleSet) {
          for(Role role : keySet) {

            // 로그인 한 사용자의 role과 cache에 저장되어 있는 role을 찾으면 그에 따른 권한(authority)를 가져와서 비교 작업을 진행한다
            if(roleName.equals(role.getRoleName())) {
              List<GrantedAuthority> grantedAuthorityList = roleAuthorities.get(role);
              for(GrantedAuthority authority : grantedAuthorityList) {
                if(attribute.getAttribute().equals(authority.getAuthority())) { // role에 있는 권한과 자원에 있는 권한이 같으면 접근 가능하다고 설정해준다
                  return ACCESS_GRANTED;
                }
              }
            }
          }
        }
      }
    }

    return result;
  }

}
