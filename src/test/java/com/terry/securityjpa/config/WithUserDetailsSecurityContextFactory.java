package com.terry.securityjpa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.security.test.context.support.WithUserDetails;

public class WithUserDetailsSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

  private UserDetailsService userDetailsService;

  @Autowired
  public WithUserDetailsSecurityContextFactory(UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  @Override
  public SecurityContext createSecurityContext(WithMockCustomUser withMockCustomUser) {
    String loginId = withMockCustomUser.value();
    UserDetails userDetails = userDetailsService.loadUserByUsername(loginId);
    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(authentication);
    return securityContext;
  }
}
