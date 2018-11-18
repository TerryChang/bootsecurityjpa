package com.terry.securityjpa.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
public class CustomDaoAuthenticationProvider extends DaoAuthenticationProvider {

  @Autowired
  MessageSource messageSource;

  @Override
  protected void additionalAuthenticationChecks(UserDetails userDetails,
      UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

    Object salt = null;

    if (getSaltSource() != null) {
      salt = getSaltSource().getSalt(userDetails);
    }

    if (authentication.getCredentials() == null) {
      logger.debug("Authentication failed: no credentials provided");

      throw new BadCredentialsException(
          messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
    }

    String presentedPassword = authentication.getCredentials().toString();

    if (!getPasswordEncoder().isPasswordValid(userDetails.getPassword(), presentedPassword, salt)) {
      List<String> messageObjects = new ArrayList<>();
      messageObjects.add(userDetails.getUsername());

      logger.debug("Authentication failed: password does not match stored value");

      /*
       * throw new BadCredentialsException(messages.getMessage(
       * "spring.security.usernameNotFoundException.PasswordMistmatch",
       * "Bad credentials"));
       */
      String message = messageSource.getMessage("spring.security.usernameNotFoundException.PasswordMismatch",
          messageObjects.toArray(new String[messageObjects.size()]), "Bad credentials", Locale.getDefault());
      throw new BadCredentialsException(message);
    }
  }
}
