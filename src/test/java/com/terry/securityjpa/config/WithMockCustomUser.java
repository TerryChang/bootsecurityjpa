package com.terry.securityjpa.config;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithUserDetailsSecurityContextFactory.class)
public @interface WithMockCustomUser {
  String value() default "rob";

  String name() default "Rob Winch";

  String password() default  "";
}
