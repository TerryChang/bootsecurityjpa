package com.terry.securityjpa.config.security;

import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * FilterSecurityInterceptor 클래스를 상속받아서 접근 URL에 대한 권한 체크등을 진행하는 클래스이다.
 * Spring Security 에서는 HttpSecurity 클래스의 authorizeRequests 메소드를 이용해서 모든 권한이 접근할 수 있는 URL을 설정할 수 있는데
 * (ex http.authorizeRequests().antMatchers("/", "/index.html", "/password.html", "/errorpage/**").permitAll()) )
 * URL에 대한 권한 체크를 별도로 진행하는 filter를 별도로 둘 경우 이 메소드를 이용한 동작을 할 수 없게 된다
 * 왜냐면 이 Filter에서는 위에서 했던 permitAll 을 설정한 패턴 조차도 권한을 체크해야 하는 관리대상 URL이 되기 때문이다
 * 그래서 authorizeRequests 메소드를 이용해서 설정하지 않고
 * 이 Filter 클래스 객체에 permitAll로 설정할 패턴을 설정한뒤 이 패턴을 만족하는 URL은 권한 체크를 진행하기 않게끔 한다
 * URL에 대한 권한 체크는 FilterSecurityInterceptor 클래스가 상속받은 AbstractSecurityInterceptor 클래스의 beforeInvocation 메소드에서 진행하게 되는데
 * 이때 이러한 권한 체크를 bypass 하고 싶으면 beforeInvocation 메소드에서 null을 return 하게 해주면 된다
 * 이 클래스에서는 beforeInvocation 메소드를 override 해서 재정의했다.
 * 권한 체크를 해야 하는 url 일 경우(permitAll 패턴을 만족하지 않는 URL)는 super.beforeInvocation(object) 를 호출해서 진행하게끔 했다
 */
public class CustomFilterSecurityInterceptor extends FilterSecurityInterceptor {

  private static final String FILTER_APPLIED = "__spring_security_filterSecurityInterceptor_filterApplied";

  private List<RequestMatcher> permitAllRequestMatcher = new ArrayList<>();

  public CustomFilterSecurityInterceptor(String ... permitAllPattern) {
    createPermitAllPattern(permitAllPattern);
  }

  /**
   * permitAll 로 지정된 패턴은 관리 대상 Resource로 취급되지 않게 하기 위해 사전에 먼저 체크해서 permitAll로 지정된 패턴은 null을 return 하게 하도록 한다
   * @param object
   * @return
   */
  @Override
  protected InterceptorStatusToken beforeInvocation(Object object) {
    boolean permitAll = false;
    HttpServletRequest request = ((FilterInvocation)object).getRequest();
    for(RequestMatcher requestMatcher : permitAllRequestMatcher) {
      if(requestMatcher.matches(request)) {
        permitAll = true;
        break;
      }
    }

    if(permitAll) {
      return null;
    }

    return super.beforeInvocation(object);
  }

  @Override
  public void invoke(FilterInvocation fi) throws IOException, ServletException {

    if ((fi.getRequest() != null)
        && (fi.getRequest().getAttribute(FILTER_APPLIED) != null)
        && super.isObserveOncePerRequest()) {
      // filter already applied to this request and user wants us to observe
      // once-per-request handling, so don't re-do security checking
      fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
    }
    else {
      // first time this request being called, so perform security checking
      if (fi.getRequest() != null) {
        fi.getRequest().setAttribute(FILTER_APPLIED, Boolean.TRUE);
      }

      InterceptorStatusToken token = beforeInvocation(fi);

      try {
        fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
      }
      finally {
        super.finallyInvocation(token);
      }

      super.afterInvocation(token, null);
    }


    // super.invoke(fi);
  }



  private void createPermitAllPattern(String ... permitAllPattern) {
    for(String pattern : permitAllPattern) {
      permitAllRequestMatcher.add(new CustomAntPathRequestMatcher(pattern));
    }

  }
}
