package com.terry.securityjpa.config.web.support;

import com.terry.securityjpa.dto.MemberDTO;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class LoginMemberHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    if(parameter.hasParameterAnnotation(LoginMember.class) && parameter.getParameterType() == MemberDTO.class) return true;
    return false;
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if(authentication == null) return null;
    if(!(authentication instanceof UsernamePasswordAuthenticationToken)) return null;
    Object principal = authentication.getPrincipal();
    if(principal instanceof MemberDTO) {
      MemberDTO memberDTO = (MemberDTO) principal;
      return memberDTO;
    }

    return null;
  }
}
