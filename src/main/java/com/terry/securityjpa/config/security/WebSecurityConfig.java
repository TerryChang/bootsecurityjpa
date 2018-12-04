package com.terry.securityjpa.config.security;

import com.terry.securityjpa.config.security.access.expression.CustomWebSecurityExpressionRoot;
import com.terry.securityjpa.config.security.access.hierarchicalroles.CustomRoleHierarchy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
// @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private String[] ignoredMatcherPattern = { "/static/**", "/css/**", "/webjars/**", "/summernote/**",
      "/**/favicon.ico" };

  @Autowired
  private DaoAuthenticationProvider daoAuthenticationProvider;

  @Autowired
  private CustomFilterSecurityInterceptor customFilterSecurityInterceptor;

  @Autowired
  private CustomRoleHierarchy customRoleHierarchy;

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers(ignoredMatcherPattern)
        .and()
        .expressionHandler(new DefaultWebSecurityExpressionHandler() {
          @Override
          protected SecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, FilterInvocation fi) {
            CustomWebSecurityExpressionRoot customWebSecurityExpressionRoot = new CustomWebSecurityExpressionRoot(authentication, fi, customRoleHierarchy);
            customWebSecurityExpressionRoot.setPermissionEvaluator(super.getPermissionEvaluator());
            customWebSecurityExpressionRoot.setTrustResolver(new AuthenticationTrustResolverImpl());
            customWebSecurityExpressionRoot.setDefaultRolePrefix("");
            return customWebSecurityExpressionRoot;
          }
        })
        .debug(true);
  }

  /**
   * 기록해두는 내용 별도로 설정하지 않으면 AuthenticationSuccessHandler는
   * SavedRequestAwareAuthenticationSuccessHandler를 사용한다 별도로 설정하지 않으면
   * AuthenticationFailureHandler는 SimpleUrlAuthenticationFailureHandler를 사용한다
   * 
   * @param http
   * @throws Exception
   */
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.addFilterBefore(customFilterSecurityInterceptor, FilterSecurityInterceptor.class)
        .csrf().disable()
        .anonymous().authorities("ANONYMOUS")
        .and()
        .authorizeRequests().anyRequest().authenticated()
        .and()
        .formLogin()
          .loginPage("/login.html")
          .loginProcessingUrl("/login_proc.html")
          .failureUrl("/login.html?error=true")
          .defaultSuccessUrl("/index.html")
          .usernameParameter("loginId")
          .passwordParameter("password")
          .permitAll()
        .and()
        .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.NEVER)
          .maximumSessions(1)
        // .expiredUrl("/errorpage/sessionError.html?error=expired")
        /*
         * Maximum Session 에 도달되어 있는 상태에서 이 값을 true로 하면 기존 로그인 되어 있는 사용자들의 로그인 상태는 유지시키고
         * 새로 접속하는 사람의 로그인을 막게 되지만 이 값이 false(default)가 되면 기존에 로그인 되어 있는 사용자들의 로그인 세션을
         * invalidate 시켜서 로그인 되지 않은 상태로 만들고 신규 사용자의 로그인을 허용시키게 된다 maximumSessions 를 1로
         * 설정한 상황에서 신규 로그인을 어떻게 처리할지에 대한 상황정리(기존 로그인 사용자를 강제로 session invalidate 시키고 신규
         * 로그인을 받게 하면서 기존 로그인 사용자에게 세션 만료로 안내하게 할지 아니면 기존 로그인 사용자의 로그인 세션을 살리고 신규 로그인
         * 사용자를 막아야 할지에 대한 결정)가 된 뒤에 이 옵션을 설정해야 한다 Maximum Session에 도달되어 기존 로그인 사용자의 세션이
         * 만료되었을 경우 만료된 사용자는 expiredUrl 메소드에서 설정한 URL로 이동하지만 invalidSessionUrl 메소드가 설정되어
         * 있으면 세션이 만료되는 상황이기 때문에 invalidSessionUrl 메소드에서 설정한 URL로 이동한다
         * 
         * 이 메소드는 기본값인 false로 두는 것이 좋다. true일 경우 오동작을 해서가 아니다. true일 경우에 의도대로 잘 동작이
         * 되어진다. 문제는 사용자가 정상적인 로그아웃을 거친게 아닌 브라우저를 그냥 닫았을때 발생한다 이런 상황이면 WAS가 이용하지 않는 세션을
         * 정리하는 시간이 되기 전까지는 세션이 계속 살아있기 때문에 사용자는 계속 로그인도 못하고 block이 되어버리는 상황이 혼다 그래서 동접자
         * 관리를 할때 기존 접속자의 세션을 자동적으로 invalidate 시키고 신규 접속자의 세션이 생성되도록 기본값인 false로 설정하는 것이
         * 좋다
         */
          .maxSessionsPreventsLogin(false)
          .and()
          // 서버가 재시작되거나 세션이 만료되면 이 메소드에서 지장한 URL로 이동한다(중복 로그인으로 인한 세션 만료시에도 이 메소드에서 지정한 URL로 이동한다)
          .invalidSessionUrl("/errorpage/sessionError.html?error=invalidate") 
        .and()
        .logout()
          .invalidateHttpSession(true)
          .clearAuthentication(true)
          // .logoutUrl("/logout.html")
          .logoutRequestMatcher(new AntPathRequestMatcher("/logout.html")).logoutSuccessUrl("/index.html")
          .deleteCookies("JSESSIONID").permitAll(); // logoutUrl 메소드, logoutSuccessUrl 메소드를 통해 정의되는 페이지의 접근 권한을 모두에게 허용한다
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(daoAuthenticationProvider);
    super.configure(auth);
  }

  /**
   * Spring Boot에서 Spring Bean으로 등록된 Servlet, Filter, Servlet*Listerner 들은
   * Embedded Container(Tomcat)에 자동으로 올라가게 되어 있다. 그러나 이러한 동작으로 인해 문제의 소지가 발생하는 상황이
   * 있다. 이미지, js 파일 등을 가리키는 특정 URL은 Spring Security의 filter chain을 거쳐야 할 필요가 없기
   * 때문에 WebSecurity.ignoring 메소드를 이용해 패턴을 지정함으로써 Spring Security의 filter chain을
   * 거치지 않는다. 그러나 우리가 Spring Security의 filter chain에 custom filter를 등록하는 상황에서 이
   * Filter를 Spring Bean으로 설정하게 되면 해당 Filter가 Embedded Container에 올라가기 때문에 위에서 언급한
   * 이미지, js 파일등을 가리키는 특정 URL이 Spring Security의 filter chain을 거치지는 않더라도 embedded
   * container의 filter로는 등록되었기 때문에 이 filter를 거쳐가는 상황이 발생하게 된다. 그래서 의도하지 않은 결과가 나올수
   * 있게 된다
   *
   * 예를 들어 FilterSecurityInterceptor 클래스가 상속받은 클래스인 AbstractSecurityInterceptor
   * 속성중 rejectPublicInvocations 속성이 있는데 기본값으로는 false로 되어 있다. 이 속성이 true일 경우엔
   * Spring Security가 관리하는 URL이 아닌 다른 URL을 접근하게 할 경우 IllegalArgumentException 예외를
   * 발생시키면서 접근을 막게 된다 위에서 언급한 이미지, js 파일등을 가리키는 특정 URL이 비록 spring security filter
   * chain 흐름을 타지는 않더라도 embedded container에 등록된 filter로는 흐름을 타기 때문에 결국
   * IllegalArgumentException 예외가 발생되면서 접근을 막게 된다 이걸 확인할 수 있는 방법은
   * FilterSecurityInterceptor 클래스의 invoke 메소드를 접근하는지 확인하면 된다 특정 URL이 ignoring
   * 메소드를 통해 등록이 되었을 경우 spring security의 filter chain을 접근하지 않기 때문에
   * FilterSecurityInterceptor 클래스의 invoke 메소드에 접근하지 말아야 한다 그러나 embedded
   * container의 filter로 등록되어 있으면 FilterSecurityInterceptor 클래스의 invoke 메소드를 접근하게
   * 된다
   *
   * 그래서 이렇게 spring security filter chain에만 참여하고 embedded container에 올라가기 않게끔 할려면
   * 해당 filter를 bean으로 등록하지 말아야 하는데 해당 filter가 spring bean을 사용해야 하는 상황에서는 filter가
   * spring bean으로 등록되어야 하는 상황이기 때문에 앞뒤가 맞지 않는 상황이 생긴다. 이럴 경우에
   * FilterRegistrationBean 클래스 객체에 해당 filter 객체를 설정한 후 setEnabled 메소드의 파라미터 값을
   * false로 줌으로써 해당 filter가 embedded container에 등록되지 않게끔 해준다
   *
   * 이러한 filter가 2개 이상 존재하면 FilterRegistrationBean 을 해당 개수만큼 만들어서 bean으로 등록하면 된다
   * (https://github.com/spring-projects/spring-boot/issues/6902)
   *
   *
   * @return
   */
  @Bean
  public FilterRegistrationBean filterRegistrationBean() {
    FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
    // filterRegistrationBean.setFilter(filterSecurityInterceptor);
    filterRegistrationBean.setFilter(customFilterSecurityInterceptor);
    filterRegistrationBean.setEnabled(false);
    return filterRegistrationBean;
  }
}
