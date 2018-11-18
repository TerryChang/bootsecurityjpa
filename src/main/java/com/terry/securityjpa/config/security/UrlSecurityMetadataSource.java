package com.terry.securityjpa.config.security;

import com.terry.securityjpa.config.cache.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
public class UrlSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

  private CacheService cacheService;

  public UrlSecurityMetadataSource(CacheService cacheService) {
    this.cacheService = cacheService;
  }

  @Override
  public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
    Collection<ConfigAttribute> result = null;
    FilterInvocation fi = (FilterInvocation) object;

    LinkedHashMap<RequestMatcher, List<ConfigAttribute>> requestMap = cacheService.getUrlResourceList();
    HttpServletRequest httpServletRequest = fi.getHttpRequest();

    if (requestMap != null) {
      for (Map.Entry<RequestMatcher, List<ConfigAttribute>> entry : requestMap.entrySet()) {
        RequestMatcher matcher = entry.getKey();
        if (matcher.matches(httpServletRequest)) {
          result = entry.getValue();
          break;
        }
      }
    }
    return result;
  }

  @Override
  public Collection<ConfigAttribute> getAllConfigAttributes() {
    LinkedHashMap<RequestMatcher, List<ConfigAttribute>> requestMap = cacheService.getUrlResourceList();
    Set<ConfigAttribute> result = new HashSet<>();
    for (Map.Entry<RequestMatcher, List<ConfigAttribute>> entry : requestMap.entrySet()) {
      List<ConfigAttribute> list = entry.getValue();
      if (list != null) {
        result.addAll(list);
      }
    }
    return result;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return FilterInvocation.class.isAssignableFrom(clazz);
    // return false;
  }

}
