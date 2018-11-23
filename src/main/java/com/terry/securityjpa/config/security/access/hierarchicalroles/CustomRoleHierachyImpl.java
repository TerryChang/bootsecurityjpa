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
public class CustomRoleHierachyImpl {

  private CacheService cacheService;
  private Map<String, Set<GrantedAuthority>> roleHierarchyAuthorities;

  public CustomRoleHierachyImpl(CacheService cacheService) {
    this.cacheService = cacheService;
    this.roleHierarchyAuthorities = cacheService.getRoleHierarchyAuthorities();
  }

  public Collection<? extends GrantedAuthority> getReachableGrantedAuthorities(
      String roleName) {
    // TODO Auto-generated method stub
    Set<GrantedAuthority> result = roleHierarchyAuthorities.get(roleName);
    return result;
  }

  public Collection<? extends GrantedAuthority> getReachableGrantedAuthorities(
      Set<String> roleNameSet) {
    // TODO Auto-generated method stub
    Set<GrantedAuthority> result = new HashSet<>();
    for(String roleName : roleNameSet) {
      result.addAll(getReachableGrantedAuthorities(roleName));
    }
    return result;
  }

}
