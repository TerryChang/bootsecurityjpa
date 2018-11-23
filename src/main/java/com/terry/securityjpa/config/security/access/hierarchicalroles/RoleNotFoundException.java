package com.terry.securityjpa.config.security.access.hierarchicalroles;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class RoleNotFoundException extends RuntimeException {

  private String roleName;

  @Override
  public String getMessage() {
    String message = "Role name " + roleName + " not found";
    logger.error(message);
    return message;
  }
}
