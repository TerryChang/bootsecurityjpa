package com.terry.securityjpa.config.cache;

import com.terry.securityjpa.config.security.CustomAntPathRequestMatcher;
import com.terry.securityjpa.entity.Authority;
import com.terry.securityjpa.entity.Role;
import com.terry.securityjpa.entity.UrlResources;
import com.terry.securityjpa.entity.enumerated.RequestMatcherType;
import com.terry.securityjpa.repository.RoleRepository;
import com.terry.securityjpa.repository.UrlResourcesRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.*;

/**
 * Cache로 보관해서 작업할 데이터들을 관리하는 클래스
 */
public class CacheService {

  private UrlResourcesRepository urlResourcesRepository;
  private RoleRepository roleRepository;

  public CacheService() {

  }

  public CacheService(UrlResourcesRepository urlResourcesRepository, RoleRepository roleRepository) {
    this.urlResourcesRepository = urlResourcesRepository;
    this.roleRepository = roleRepository;
    init();
  }

  /**
   * RequestMatcher 에 대한 Authority 객체들의 List들을 보관한 LinkedHashMap을 Cache로 관리하며 작업한다
   * @return
   */
  @Cacheable(value="urlResourceList")
  public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getUrlResourceList() {
    LinkedHashMap<RequestMatcher, List<ConfigAttribute>> result = new LinkedHashMap<>();
    List<UrlResources> urlResourcesList = urlResourcesRepository.findAllUrlResources();

    for(UrlResources urlResources : urlResourcesList) {
      // RequestMatcher requestMatcher = new CustomAntPathRequestMatcher(urlResources.getResourcePattern());
      RequestMatcher requestMatcher = null;
      RequestMatcherType requestMatcherType = urlResources.getRequestMatcherType();
      String resourcePattern = urlResources.getResourcePattern();
      if(requestMatcherType == RequestMatcherType.ANT) {
        requestMatcher = new AntPathRequestMatcher(resourcePattern);
      }else if(requestMatcherType == RequestMatcherType.QUERYSTRING) {
        requestMatcher = new CustomAntPathRequestMatcher(resourcePattern);
      }else if(requestMatcherType == RequestMatcherType.REGEX) {
        requestMatcher = new RegexRequestMatcher(resourcePattern, null);
      }

      List<ConfigAttribute> configAttributeList = new ArrayList<>();
      Set<Authority> authoritySet = urlResources.getAuthoritySet();
      for(Authority authority : authoritySet) {
        configAttributeList.add(new SecurityConfig(authority.getAuthorityName()));
      }
      result.put(requestMatcher, configAttributeList);
    }

    return result;
  }

  /**
   * getUrlResourceList 로 인해 생성된 cache를 지우기 위해 이 메소드를 둔다
   * 실제 작업하는 내용은 없다. @CacheEvict 어노테이션으로 인해 cache가 지워지기 때문이다
   */
  @CacheEvict(value="urlResourceList")
  public void clearCacheUrlResourceList() {

  }

  @Cacheable(value="roleAuthorities")
  public Map<Role, List<GrantedAuthority>> getRoleAuthorities() {
    Map<Role, List<GrantedAuthority>> result = new HashMap<>();
    List<Role> roleList = roleRepository.getRoleWithAuthority();

    for(Role role : roleList) {
      Set<Authority> authoritySet = role.getAuthoritySet();
      List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
      for(Authority authority : authoritySet) {
        grantedAuthorityList.add(new SimpleGrantedAuthority(authority.getAuthorityName()));
      }
      result.put(role, grantedAuthorityList);
    }

    return result;

  }

  @CacheEvict(value="roleAuthorities")
  public void clearCacheRoleAuthorities() {

  }

  private void init() {
    getUrlResourceList();
  }

}
