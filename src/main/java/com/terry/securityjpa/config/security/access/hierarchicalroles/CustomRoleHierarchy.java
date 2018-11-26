package com.terry.securityjpa.config.security.access.hierarchicalroles;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.terry.securityjpa.entity.Role;
import org.springframework.security.access.hierarchicalroles.CycleInRoleHierarchyException;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.terry.securityjpa.config.cache.CacheService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomRoleHierarchy {

  private CacheService cacheService;

  public CustomRoleHierarchy(CacheService cacheService) {
    this.cacheService = cacheService;
  }

  /**
   * Role 이름을 입력받아 해당 Role의 모든 하위단계(바래 아래 단계가 아닌 모든 하위 단계)의 Role 들이 가지고 있는 GrantedAuthroities 객체들이 들어있는 Collection 구현체를 return 한다
   * @param roleName 검색대상인 Role 이름
   * @return 파라미터로 받은 roleName의 모든 하위단계의 Role들이 가지고 있는 GrantedAuthority 객체들이 들어있는 Collection 구현체
   */
  public Collection<? extends GrantedAuthority> getReachableGrantedAuthorities(
      String roleName) {
    // TODO Auto-generated method stub
    // Map<String, Set<GrantedAuthority>> roleHierarchyAuthorities 변수에는 cacheService에 저장되어 있는 Role에 대한 모든 하위 Role이 접근할 수 있는 GrantedAuthority 객체들이 있다.
    // 이것을 CustomRoleHierarchy의 멤버변수가 아닌 cacheService에서 읽어오도록 한 이유는
    // Role에 대한 Authority에 대한 변화가 발생했을때 cacheService의 경우 저장되어 있는 cache를 재작성하는 식으로 재반영이 가능하지만..
    // 이를 특정 Bean의 멤버변수로 하게 할 경우 멤버변수의 재갱신을 할 수 있는 방법이 이를 위한 별도 작업메소드를 만들지 않는 한에는 없기 때문이다
    // 메모리에 저장되어 있기 때문에 cache에서 가져오는 시간이 오래걸리는 것도 아니어서..
    // 그때그때 cache에서 호출하는 방식을 사용해도 지장이 없을것이란 판단이 들어 이렇게 작성했다
    Map<String, Set<GrantedAuthority>> roleHierarchyAuthorities = cacheService.getRoleHierarchyAuthorities();
    Set<GrantedAuthority> result = roleHierarchyAuthorities.get(roleName);
    return result;
  }

  /**
   * Role 이름들이 들어있는 Set 객체를 받아 Set 객체에 들어있는 Role 이름의 Role의 모든 하위단계(바래 아래 단계가 아닌 모든 하위 단계)의
   * Role 들이 가지고 있는 GrantedAuthroities 객체들이 들어있는 Collection 구현체를 return 한다
   * @param roleNameSet 검색대상인 Role 이름들이 들어있는 Set 객체
   * @return 파라미터로 받은 roleNameSet에 들어있는 Role 이름의 Role의 모든 하위단계의 Role들이 가지고 있는 GrantedAuthority 객체들이 들어있는 Collection 구현체
   */
  public Collection<? extends GrantedAuthority> getReachableGrantedAuthorities(
      Set<String> roleNameSet) {
    // TODO Auto-generated method stub
    Set<GrantedAuthority> result = new HashSet<>();
    for(String roleName : roleNameSet) {
      Collection<? extends GrantedAuthority> reachableGrantedAuthorities = getReachableGrantedAuthorities(roleName);
      if(reachableGrantedAuthorities != null)
        result.addAll(getReachableGrantedAuthorities(roleName));
    }
    return result;
  }

}
