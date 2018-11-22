package com.terry.securityjpa.config.security.access.voter;

import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.util.Assert;

public class CustomRoleHierachyVoter extends CustomRoleVoter {

  private RoleHierarchy roleHierarchy = null;

  public CustomRoleHierachyVoter(RoleHierarchy roleHierarchy) {
    Assert.notNull(roleHierarchy, "RoleHierarchy must not be null");
    this.roleHierarchy = roleHierarchy;
  }
}
