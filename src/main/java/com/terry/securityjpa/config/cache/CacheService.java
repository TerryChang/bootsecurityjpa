package com.terry.securityjpa.config.cache;

import com.terry.securityjpa.config.security.matcher.CustomAntPathRequestMatcher;
import com.terry.securityjpa.entity.Authority;
import com.terry.securityjpa.entity.Role;
import com.terry.securityjpa.entity.UrlResources;
import com.terry.securityjpa.entity.enumerated.RequestMatcherType;
import com.terry.securityjpa.repository.RoleRepository;
import com.terry.securityjpa.repository.UrlResourcesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.hierarchicalroles.CycleInRoleHierarchyException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cache로 보관해서 작업할 데이터들을 관리하는 클래스
 */
@Slf4j
public class CacheService {

  private UrlResourcesRepository urlResourcesRepository;
  private RoleRepository roleRepository;

  public CacheService() {

  }

  public CacheService(UrlResourcesRepository urlResourcesRepository, RoleRepository roleRepository) {
    this.urlResourcesRepository = urlResourcesRepository;
    this.roleRepository = roleRepository;
    // init();
  }

  /**
   * RequestMatcher 에 대한 Authority 객체들의 List들을 보관한 LinkedHashMap을 Cache로 관리하며 작업한다
   * 
   * @return
   */
  @Cacheable(value = "urlResourceList")
  public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getUrlResourceList() {
    LinkedHashMap<RequestMatcher, List<ConfigAttribute>> result = new LinkedHashMap<>();
    List<UrlResources> urlResourcesList = urlResourcesRepository.findAllUrlResources();

    for (UrlResources urlResources : urlResourcesList) {
      // RequestMatcher requestMatcher = new
      // CustomAntPathRequestMatcher(urlResources.getResourcePattern());
      RequestMatcher requestMatcher = null;
      RequestMatcherType requestMatcherType = urlResources.getRequestMatcherType();
      String resourcePattern = urlResources.getResourcePattern();
      if (requestMatcherType == RequestMatcherType.ANT) {
        requestMatcher = new AntPathRequestMatcher(resourcePattern);
      } else if (requestMatcherType == RequestMatcherType.QUERYSTRING) {
        requestMatcher = new CustomAntPathRequestMatcher(resourcePattern);
      } else if (requestMatcherType == RequestMatcherType.REGEX) {
        requestMatcher = new RegexRequestMatcher(resourcePattern, null);
      }

      List<ConfigAttribute> configAttributeList = new ArrayList<>();
      Set<Authority> authoritySet = urlResources.getAuthoritySet();
      for (Authority authority : authoritySet) {
        configAttributeList.add(new SecurityConfig(authority.getAuthorityName()));
      }
      result.put(requestMatcher, configAttributeList);
    }

    return result;
  }

  /**
   * getUrlResourceList 로 인해 생성된 cache를 지우기 위해 이 메소드를 둔다 실제 작업하는 내용은
   * 없다. @CacheEvict 어노테이션으로 인해 cache가 지워지기 때문이다
   */
  @CacheEvict(value = "urlResourceList")
  public void clearCacheUrlResourceList() {

  }

  @Cacheable(value = "roleAuthorities")
  public Map<String, Set<GrantedAuthority>> getRoleAuthorities() {
    Map<String, Set<GrantedAuthority>> result = new HashMap<>();
    List<Role> roleList = roleRepository.getRoleWithAuthority();

    for (Role role : roleList) {
      Set<Authority> authoritySet = role.getAuthoritySet();
      Set<GrantedAuthority> grantedAuthoritySet = new HashSet<>();
      for (Authority authority : authoritySet) {
        grantedAuthoritySet.add(new SimpleGrantedAuthority(authority.getAuthorityName()));
      }
      result.put(role.getRoleName(), grantedAuthoritySet);
    }

    return result;

  }

  @CacheEvict(value = "roleAuthorities")
  public void clearCacheRoleAuthorities() {

  }

  @Cacheable(value = "roleHierarchyAuthorities")
  public Map<String, Set<GrantedAuthority>> getRoleHierarchyAuthorities() {
    Map<String, Set<String>> rolesReachableInOneStepMap = buildRolesReachableInOneStepMap();
    Map<String, Set<String>> rolesReachableInOneOrMoreStepsMap = buildRolesReachableInOneOrMoreStepsMap(rolesReachableInOneStepMap);
    Map<String, Set<GrantedAuthority>> roleAuthorities = getRoleAuthorities();
    Map<String, Set<GrantedAuthority>> roleHierarchyAuthorities = buildRolesReachableInOneOrMoreStepsAuthorityMap(roleAuthorities,  rolesReachableInOneOrMoreStepsMap);
    return roleHierarchyAuthorities;
  }

  @CacheEvict(value = "roleHierarchyAuthorities")
  public void clearCacheRoleHierarchyAuthorities() {

  }

  /**
   * 하위단계 Role 이름들이 들어있는 Set 객체와 하위단계 Role 이름을 입력받가
   * 입력받은 하위단계 Role 이름이 Set 객체에 없으면 Set 객체에 하위 단계 Role 이름을 넣어주는 메소드이다.
   * @param reachableRoleNames
   * @param lowerRoleName
   */
  private void addReachableRoleNames(Set<String> reachableRoleNames,
                                     String lowerRoleName) {

    for (String roleName : reachableRoleNames) {
      if(roleName.equals(lowerRoleName)) return;
    }
    reachableRoleNames.add(lowerRoleName);
  }

  /**
   * Spring Security에서 사용할 Role 에 대한 바로 아래단계 하위 Role을 조회한다
   *
   * @return 상위 Role 이름에 대한 바로 아래 하위 Role 이름
   */
  private Map<String, Set<String>> buildRolesReachableInOneStepMap() {
    Map<String, Set<String>> rolesReachableInOneStepMap = new HashMap<String, Set<String>>();
    Pattern pattern = Pattern.compile("(\\s*([^\\s>]+)\\s*>\\s*([^\\s>]+))");
    StringBuffer sbRoleHierarchyStringRepresentation = new StringBuffer();
    String roleHierarchyStringRepresentation = null;
    List<Role> roleList = roleRepository.getRoleWithchildRoleSet();

    // 상위 Role 이름 > 하위 Role 이름의 반복된 문자열을 만드는 작업을 진행한다
    // ex : ADMIN > ASSOCIATE ADMIN > REGULAR ASSOCIATE > TEMPORARY ...
    for (Role role : roleList) {
      String roleName = role.getRoleName();
      Set<Role> childRoleSet = role.getChildRoleSet();
      for (Role childRole : childRoleSet) {
        String childRoleName = childRole.getRoleName();
        logger.debug(roleName + " > " + childRoleName);
        sbRoleHierarchyStringRepresentation.append(roleName + " > " + childRoleName + " ");
      }
    }

    // 만들어진 문자열 끝에 공백이 한 글자 들어가 있어서 StringUtils 클래스의 trimTrailingWhitespace 메소드를 통해 공백을 제거한다
    roleHierarchyStringRepresentation = StringUtils.trimTrailingWhitespace(sbRoleHierarchyStringRepresentation.toString());

    Matcher roleHierarchyMatcher = pattern.matcher(roleHierarchyStringRepresentation);

    while (roleHierarchyMatcher.find()) {
      String higherRoleName = roleHierarchyMatcher.group(2);      // 상위 Role 이름
      String lowerRoleName = roleHierarchyMatcher.group(3);       // 하위 Role 이름

      Set<String> rolesReachableInOneStepSet;

      if (!rolesReachableInOneStepMap.containsKey(higherRoleName)) {
        rolesReachableInOneStepSet = new HashSet<String>();
        rolesReachableInOneStepMap.put(higherRoleName, rolesReachableInOneStepSet);
      } else {
        rolesReachableInOneStepSet = rolesReachableInOneStepMap.get(higherRoleName);
      }

      addReachableRoleNames(rolesReachableInOneStepSet, lowerRoleName);

      logger.debug("buildRolesReachableInOneStepMap() - From role " + higherRoleName
          + " one can reach role " + lowerRoleName + " in one step.");
    }

    return rolesReachableInOneStepMap;
  }

  /**
   * Spring Security에서 사용할 Role 에 대한 바로 모든 하위 Role을 조회한다
   * 예를 들어 ADMIN > ASSOCIATE ADMIN > REGULAR ASSOCIATE > TEMPORARY 구조이면
   * ADMIN 에 대해 ASSOCIATE, REGULAR, TEMPORARY 를 저장하고
   * ASSOCIATE 에 대해 TEMPORAY 를 저장한다
   *
   * @param rolesReachableInOneStepMap 상위 Role 이름에 대한 바로 아래 하위 Role 이름
   * @return 상위 Role 이름에 대한 모든 하위 Role 이름
   */
  private Map<String, Set<String>> buildRolesReachableInOneOrMoreStepsMap(Map<String, Set<String>> rolesReachableInOneStepMap) {
    Map<String, Set<String>> rolesReachableInOneOrMoreStepsMap = new HashMap<String, Set<String>>();

    for (String role : rolesReachableInOneStepMap.keySet()) {
      Set<String> rolesToVisitSet = new HashSet<String>();

      if (rolesReachableInOneStepMap.containsKey(role)) {
        rolesToVisitSet.addAll(rolesReachableInOneStepMap.get(role));
      }

      Set<String> visitedRolesSet = new HashSet<String>();

      while (!rolesToVisitSet.isEmpty()) {
        // take a role from the rolesToVisit set
        String aRole = rolesToVisitSet.iterator().next();
        rolesToVisitSet.remove(aRole);
        addReachableRoleNames(visitedRolesSet, aRole);
        if (rolesReachableInOneStepMap.containsKey(aRole)) {
          Set<String> newReachableRoles = rolesReachableInOneStepMap.get(aRole);

          // definition of a cycle: you can reach the role you are starting from
          if (rolesToVisitSet.contains(role) || visitedRolesSet.contains(role)) {
            throw new CycleInRoleHierarchyException();
          }
          else {
            // no cycle
            rolesToVisitSet.addAll(newReachableRoles);
          }
        }
      }
      rolesReachableInOneOrMoreStepsMap.put(role, visitedRolesSet);

      logger.debug("buildRolesReachableInOneOrMoreStepsMap() - From role " + role
          + " one can reach " + visitedRolesSet + " in one or more steps.");
    }

    return rolesReachableInOneOrMoreStepsMap;
  }

  /**
   * 상위 Role 이름에 대한 모든 하위 Role 이름을 받아 상위 Role에 대한 모든 하위 Role들의 Authority들을 저장하여 return 한다
   * @param roleAuthorities Role에 대한 Authority들이 저장되어 있는 Map 객체
   * @param rolesReachableInOneOrMoreStepsMap 상위 Role 이름에 대한 모든 하위 Role 이름
   * @return 상위 Role에 대한 모든 하위 Role들의 Authority들
   */
  private Map<String, Set<GrantedAuthority>> buildRolesReachableInOneOrMoreStepsAuthorityMap(
      Map<String, Set<GrantedAuthority>> roleAuthorities
      , Map<String, Set<String>> rolesReachableInOneOrMoreStepsMap) {

    Map<String, Set<GrantedAuthority>> result = new HashMap<String, Set<GrantedAuthority>>();
    Iterator<String> roleAuthoritiesIterator = roleAuthorities.keySet().iterator();
    Iterator<String> rolesReachableInOneOrMoreStepsMapIterator = rolesReachableInOneOrMoreStepsMap.keySet().iterator();

    while(rolesReachableInOneOrMoreStepsMapIterator.hasNext()) {
      String upperRoleName = rolesReachableInOneOrMoreStepsMapIterator.next(); // 상위 Role 이름
      Set<String> lowerRoleNames = rolesReachableInOneOrMoreStepsMap.get(upperRoleName);  // 상위 Role들에 대한 하위 Role들의 이름
      Set<GrantedAuthority> lowerRoleAuthoriies = new HashSet<>();   // 하위 Role들의 권한들이 들어갈 Set

      String upperRole = null;
      while(roleAuthoritiesIterator.hasNext()) {
        String checkRole = roleAuthoritiesIterator.next();
        if(checkRole.equals(upperRoleName)) {
          upperRole = checkRole;
          break;
        }
      }

      // key로 사용된 상위 Role이 가지고 있는 권한들을 추가
      lowerRoleAuthoriies.addAll(roleAuthorities.get(upperRole));

      // 상위 Role이 가지고 있는 모든 하위 Role들에 대한 권한들을 추가
      for(String lowerRoleName : lowerRoleNames) {
        lowerRoleAuthoriies.addAll(roleAuthorities.get(lowerRoleName));
      }

      if(upperRole != null) {
        result.put(upperRole, lowerRoleAuthoriies);
      }

    }

    return result;
  }

  private void init() {
    getUrlResourceList();
  }


}
