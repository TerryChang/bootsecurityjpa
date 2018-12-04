package com.terry.securityjpa.config.security;

import com.terry.securityjpa.config.security.access.expression.CustomMethodSecurityExpressionHandler;
import com.terry.securityjpa.config.security.access.hierarchicalroles.CustomRoleHierarchy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

  @Autowired
  CustomRoleHierarchy customRoleHierarchy;

  @Override
  protected MethodSecurityExpressionHandler createExpressionHandler() {
    CustomMethodSecurityExpressionHandler customMethodSecurityExpressionHandler = new CustomMethodSecurityExpressionHandler(customRoleHierarchy);
    return customMethodSecurityExpressionHandler;
  }
}
