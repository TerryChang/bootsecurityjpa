package com.terry.securityjpa.config.security.access.expression;

import com.terry.securityjpa.config.security.access.hierarchicalroles.CustomRoleHierarchy;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;

public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

  private final CustomRoleHierarchy customRoleHierarchy;
  private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

  public CustomMethodSecurityExpressionHandler(CustomRoleHierarchy customRoleHierarchy) {
    this.customRoleHierarchy = customRoleHierarchy;
  }

  public CustomMethodSecurityExpressionHandler(CustomRoleHierarchy customRoleHierarchy, AuthenticationTrustResolver trustResolver) {
    this.customRoleHierarchy = customRoleHierarchy;
    this.trustResolver = trustResolver;
  }

  @Override
  protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
    CustomMethodSecurityExpressionRoot customMethodSecurityExpressionRoot = new CustomMethodSecurityExpressionRoot(authentication, customRoleHierarchy);
    customMethodSecurityExpressionRoot.setTrustResolver(trustResolver);
    return customMethodSecurityExpressionRoot;
  }
}
