package com.terry.securityjpa.config.security;

import com.terry.securityjpa.config.cache.CacheService;
import com.terry.securityjpa.config.security.access.hierarchicalroles.CustomRoleHierarchy;
import com.terry.securityjpa.config.security.access.voter.CustomAuthorityVoter;
import com.terry.securityjpa.config.security.access.voter.CustomRoleHierarchyVoter;
import com.terry.securityjpa.config.security.access.voter.CustomRoleVoter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import java.util.Arrays;
import java.util.List;

@Configuration
@Slf4j
public class SecurityBeanConfig {

  private String[] permitAllPattern = { "/", "/index.html", "/password.html", "/errorpage/**" };

  @Autowired
  LoginUserDetailsService loginUserDetailsService;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * 기본적으로 설정되는 DaoAuthenticationProvider는 UserNotFoundException이 던져진것을 받게 될 경우 이를
   * BadCridentialsException으로 변환해서 던지게 된다. 그 이유는 DaoAuthenticationProvider 멤버변수로
   * hideUserNotFoundExceptions가 있는데 이 변수값이 true일 경우 UserNotFoundException을 받을때 이를
   * BadCridentialsException으로 변환하기 때문이다. 이 변수의 기본값이 true이기 때문에
   * UserNotFoundException을 던져도 최종적으로 받게 되는 예외는 BadCridentialsException이 된다. 그래서
   * UserNotFoundException을 던질경우 이것을 그대로 UserNotFoundException으로 받기 위해
   * DaoAuthenticationProvider bean을 새로 설정한 뒤 여기에 userDetailService와
   * passwordEncoder를 설정한뒤 hideUserNotFoundExceptions를 false로 설정해서 이를
   * WebSecurityConfigurerAdapter 클래스의 configure(AuthenticationManagerBuilder
   * auth)에서 사용하도록 했다
   * 
   * @return
   */
  @Bean
  public DaoAuthenticationProvider daoAuthenticationProvider() {
    DaoAuthenticationProvider daoAuthenticationProvider = new CustomDaoAuthenticationProvider();
    daoAuthenticationProvider.setUserDetailsService(loginUserDetailsService);
    daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
    daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
    return daoAuthenticationProvider;
  }

  /**
   * Spring Security에서 세션 동시 접속 제어를 설정할 경우 HttpSessionEventPublisher 클래스가 web.xml에
   * listener로 등록되어 야한다. Spring Boot에서는 @Bean으로 Spring Bean을 설정하는 것으로 listener로
   * 등록이 된다. 그러나 이러한 동작 방식은 Spring Boot의 Embedded Container에 제한된 것이지, war 파일을 통한
   * 배포에서는 맞지 않을수 있어서 이 부분에 대한 확인이 필요하다
   * 
   * @return
   */
  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }

  @Bean
  public UrlSecurityMetadataSource urlSecurityMetadataSource(CacheService cacheService) {
    UrlSecurityMetadataSource urlSecurityMetadataSource = new UrlSecurityMetadataSource(cacheService);
    return urlSecurityMetadataSource;
  }

  @Bean
  public AffirmativeBased accessDecisionManager(CacheService cacheService) {
    CustomRoleVoter customRoleVoter = new CustomRoleVoter(cacheService);
    CustomRoleHierarchyVoter customRoleHierarchyVoter = new CustomRoleHierarchyVoter(cacheService, new CustomRoleHierarchy(cacheService));
    CustomAuthorityVoter customAuthorityVoter = new CustomAuthorityVoter();
    AuthenticatedVoter authenticatedVoter = new AuthenticatedVoter();
    
    // voter를 등록할때 AuthenticatedVoter가 먼저 등록되도록 설정해야 한다. 
    // 왜냐면 그걸 제외한 나머지는 로그인이 되어야만 voter 역할을 제대로 할 수 있기 때문이다. 
    List<AccessDecisionVoter<? extends Object>> accessDecisionVoterList = Arrays.asList(authenticatedVoter, 
        customAuthorityVoter, customRoleVoter, customRoleHierarchyVoter );
    AffirmativeBased accessDecisionManager = new AffirmativeBased(accessDecisionVoterList);
    // 접근 승인이나 거부에 대한 판단을 할 수 없는 경우 접근 허용 여부를 설정하는 것이 allowIfAllAbstainDecisions
    // 속성인데 이것을 true로 하면 접근을 허용하는 것이고 그렇지 않은 경우 접근을 허용하지 않는 것을 의미한다
    accessDecisionManager.setAllowIfAllAbstainDecisions(false);

    return accessDecisionManager;
  }

  @Bean
  public CustomFilterSecurityInterceptor customFilterSecurityInterceptor(AuthenticationManager authenticationManager,
      AccessDecisionManager accessDecisionManager, UrlSecurityMetadataSource UrlSecurityMetadataSource) {
    CustomFilterSecurityInterceptor customFilterSecurityInterceptor = new CustomFilterSecurityInterceptor(
        permitAllPattern);
    customFilterSecurityInterceptor.setAuthenticationManager(authenticationManager);
    customFilterSecurityInterceptor.setAccessDecisionManager(accessDecisionManager);
    customFilterSecurityInterceptor.setSecurityMetadataSource(UrlSecurityMetadataSource);
    // 이 옵션을 true로 하면 모든 페이지들은 다 권한이 부여되어야 하는 관리 대상이 되어야 한다.
    // 그러나 현실적으로 public page가 없을 수는 없다(login.html을 예로 들면 여러 권한(익명 권한 포함)을 가진 사람이 모두
    // 접근할 수 있는 public page 이다)
    // 모든 page를 일일이 권한을 매치하여 관리 할 수는 없기 때문에 false로 설정한다(기본값이 false)
    customFilterSecurityInterceptor.setRejectPublicInvocations(false);
    return customFilterSecurityInterceptor;
  }


}
