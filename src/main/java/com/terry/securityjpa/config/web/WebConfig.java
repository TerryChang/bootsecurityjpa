package com.terry.securityjpa.config.web;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.terry.securityjpa.config.web.support.LoginMemberHandlerMethodArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.terry.securityjpa.config.converter.http.FileHttpMessageConverter;
import com.terry.securityjpa.config.properties.SpringDataWebProperties;
import com.terry.securityjpa.config.thymeleaf.ExtraLinkDialect;
import com.terry.securityjpa.config.web.support.CustomPageableHandlerMethodArgumentResolver;
import com.terry.securityjpa.config.web.support.RequestMappingHandlerAdapterCustomizer;

/**
 * @EnableSpringDataWebSupport 를 사용할 경우 3개의 HandlerMethodArgumentResolver가 등록된다
 * SortHandlerMethodArgumentResolver, PageableHandlerMethodArgumentResolver, ProxyingHandlerMethodArgumentResolver
 * 
 * https://stackoverflow.com/questions/23521280/spring-data-rest-configure-pagination 문서 참조
 * @author Terry Chang
 *
 */
@Configuration
@EnableSpringDataWebSupport
public class WebConfig extends WebMvcConfigurerAdapter {

  @Autowired
  Set<Converter<?, ?>> converters;
  
  @Autowired
  SpringDataWebProperties springDataWebProperties;

  @Value("${app.summernote.images}")
  private String appSummernoteImages;

  @Bean
  public SpringTemplateEngine templateEngine(ITemplateResolver templateResolver, SpringSecurityDialect sec,
      Java8TimeDialect java8TimeDialect) {
    final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
    templateEngine.setTemplateResolver(templateResolver);
    // templateEngine.addDialect(new LayoutDialect()); // thymeleaf 에서 layout을 사용하기
    // 위해 추가
    templateEngine.addDialect(sec); // Enable use of "sec"
    templateEngine.addDialect(java8TimeDialect); // Thymeleaf에서 Java8의 LocalDateTime을 출력하기 위해 사용된다
    templateEngine.addDialect(new ExtraLinkDialect());
    return templateEngine;
  }

  @Bean
  public Java8TimeDialect java8TimeDialect() {
    return new Java8TimeDialect();
  }

  @Override
  public void addFormatters(FormatterRegistry registry) {
    converters.stream().forEach(converter -> registry.addConverter(converter));
    super.addFormatters(registry);
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/summernote/images/**").addResourceLocations(appSummernoteImages);
    super.addResourceHandlers(registry);
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    // TODO Auto-generated method stub
    /* 페이징 관련 객체를 SearchDTO로 전달해주는 CustomPageableHandlerMethodArgumentResolver를 등록한다
    1페이지를 1부터 시작하게끔 oneIndexedParameters 속성값을 true로 주고 있다
    1페이지를 0부터 시작하게끔 할려면 이 속성값을 false로 줌과 동시에
    RequestMappingHandlerAdapterCustomizer 클래스의 postProcessAfterInitialization 메소드에서
    PageableHandlerMethodArgumentResolver 객체에 대한 작업을 하는 부분에도 oneIndexedParameters 속성값을 같은 값으로 변경해주어야 한다 */
    CustomPageableHandlerMethodArgumentResolver customPageableHandlerArgumentResolver = new CustomPageableHandlerMethodArgumentResolver();
    customPageableHandlerArgumentResolver.setMaxPageSize(springDataWebProperties.getPageable().getMaxPageSize());
    customPageableHandlerArgumentResolver.setOneIndexedParameters(springDataWebProperties.getPageable().isOneIndexedParameters());
    customPageableHandlerArgumentResolver.setPageParameterName(springDataWebProperties.getPageable().getPageParameter());
    customPageableHandlerArgumentResolver.setPrefix(springDataWebProperties.getPageable().getPrefix());
    customPageableHandlerArgumentResolver.setQualifierDelimiter(springDataWebProperties.getPageable().getQualifierDelimiter());
    customPageableHandlerArgumentResolver.setSizeParameterName(springDataWebProperties.getPageable().getSizeParameter());
    argumentResolvers.add(customPageableHandlerArgumentResolver);

    // Controller에서 로그인 한 사용자의 정보를 보관하고 있는 MemberDTO 객체를 파라미터로 받을수 있게 해주는 LoginMemberHandlerMethodArgumentResolver를 등록한다
    LoginMemberHandlerMethodArgumentResolver loginMemberHandlerMethodArgumentResolver = new LoginMemberHandlerMethodArgumentResolver();
    argumentResolvers.add(loginMemberHandlerMethodArgumentResolver);
    super.addArgumentResolvers(argumentResolvers);    
  }

  /**
   * Custom HttpMessageConverter를 등록할때 해당 Custom HttpMessageConverter를 생성한뒤에
   * HttpMesssageConverters 객체에 ArrayList 에 추가된 형태로 해서 넣는다
   * 
   * @return
   */
  @Bean
  public HttpMessageConverters httpMessageConverters() {
    List<HttpMessageConverter<?>> httpMessageConverterList = new ArrayList<>();
    HttpMessageConverter<File> fileHttpMessageConverter = new FileHttpMessageConverter();
    httpMessageConverterList.add(fileHttpMessageConverter);
    HttpMessageConverters httpMessageConverters = new HttpMessageConverters(httpMessageConverterList);
    return httpMessageConverters;
  }
  
  @Bean
  public RequestMappingHandlerAdapterCustomizer requestMappingHandlerAdapterCustomizer(SpringDataWebProperties springDataWebProperties) {
    return new RequestMappingHandlerAdapterCustomizer(springDataWebProperties);
  }
  
}
