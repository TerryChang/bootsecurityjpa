package com.terry.securityjpa.config.security.access.hierarchicalroles;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.access.hierarchicalroles.CycleInRoleHierarchyException;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.terry.securityjpa.config.cache.CacheService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomRoleHierachyImpl implements RoleHierarchy {

  private CacheService cacheService;
  private String roleHierarchyStringRepresentation = null;
  
  private Map<String, Set<String>> rolesReachableInOneStepMap = null;
  private Map<String, Set<String>> rolesReachableInOneOrMoreStepsMap = null;
  private Map<String, Set<GrantedAuthority>> rolesReachableInOneOrMoreStepsAuthorityMap = null;
  
  public CustomRoleHierachyImpl(CacheService cacheService, String roleHierarchyStringRepresentation) {
    this.cacheService = cacheService;
    this.roleHierarchyStringRepresentation = roleHierarchyStringRepresentation;
    
    logger.debug("setHierarchy() - The following role hierarchy was set: "
        + roleHierarchyStringRepresentation);

    buildRolesReachableInOneStepMap();
    buildRolesReachableInOneOrMoreStepsMap();
  }

  @Override
  public Collection<? extends GrantedAuthority> getReachableGrantedAuthorities(
      Collection<? extends GrantedAuthority> authorities) {
    // TODO Auto-generated method stub
    return null;
  }
  
  private void addReachableRoleNames(Set<String> reachableRoleNames,
      String lowerRoleName) {

    for (String roleName : reachableRoleNames) {
      if(roleName.equals(lowerRoleName)) return;
    }
    reachableRoleNames.add(lowerRoleName);
  }
  
  private void buildRolesReachableInOneStepMap() {
    Pattern pattern = Pattern.compile("(\\s*([^\\s>]+)\\s*>\\s*([^\\s>]+))");

    Matcher roleHierarchyMatcher = pattern
        .matcher(this.roleHierarchyStringRepresentation);
    this.rolesReachableInOneStepMap = new HashMap<String, Set<String>>();

    while (roleHierarchyMatcher.find()) {
      String higherRoleName = roleHierarchyMatcher.group(2);      // 상위 Role 이름
      String lowerRoleName = roleHierarchyMatcher.group(3);       // 하위 Role 이름
      
      Set<String> rolesReachableInOneStepSet;

      if (!this.rolesReachableInOneStepMap.containsKey(higherRoleName)) {
        rolesReachableInOneStepSet = new HashSet<String>();
        this.rolesReachableInOneStepMap.put(higherRoleName,
            rolesReachableInOneStepSet);
      }
      else {
        rolesReachableInOneStepSet = this.rolesReachableInOneStepMap
            .get(higherRoleName);
      }
      addReachableRoleNames(rolesReachableInOneStepSet, lowerRoleName);

      logger.debug("buildRolesReachableInOneStepMap() - From role " + higherRoleName
          + " one can reach role " + lowerRoleName + " in one step.");
    }
  }

  /**
   * For every higher role from rolesReachableInOneStepMap store all roles that are
   * reachable from it in the map of roles reachable in one or more steps. (Or throw a
   * CycleInRoleHierarchyException if a cycle in the role hierarchy definition is
   * detected)
   */
  private void buildRolesReachableInOneOrMoreStepsMap() {
    this.rolesReachableInOneOrMoreStepsMap = new HashMap<String, Set<String>>();
    
    for (String role : this.rolesReachableInOneStepMap.keySet()) {
      Set<String> rolesToVisitSet = new HashSet<String>();

      if (this.rolesReachableInOneStepMap.containsKey(role)) {
        rolesToVisitSet.addAll(this.rolesReachableInOneStepMap.get(role));
      }

      Set<String> visitedRolesSet = new HashSet<String>();

      while (!rolesToVisitSet.isEmpty()) {
        // take a role from the rolesToVisit set
        String aRole = rolesToVisitSet.iterator().next();
        rolesToVisitSet.remove(aRole);
        addReachableRoleNames(visitedRolesSet, aRole);
        if (this.rolesReachableInOneStepMap.containsKey(aRole)) {
          Set<String> newReachableRoles = this.rolesReachableInOneStepMap
              .get(aRole);

          // definition of a cycle: you can reach the role you are starting from
          if (rolesToVisitSet.contains(role)
              || visitedRolesSet.contains(role)) {
            throw new CycleInRoleHierarchyException();
          }
          else {
            // no cycle
            rolesToVisitSet.addAll(newReachableRoles);
          }
        }
      }
      this.rolesReachableInOneOrMoreStepsMap.put(role, visitedRolesSet);

      logger.debug("buildRolesReachableInOneOrMoreStepsMap() - From role " + role
          + " one can reach " + visitedRolesSet + " in one or more steps.");
    }  

  }

}
