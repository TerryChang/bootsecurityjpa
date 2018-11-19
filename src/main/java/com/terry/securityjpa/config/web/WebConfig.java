package com.terry.securityjpa.config.web;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
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
import com.terry.securityjpa.config.thymeleaf.ExtraLinkDialect;
import com.terry.securityjpa.config.web.support.CustomPageableHandlerMethodArgumentResolver;

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
    PageableHandlerMethodArgumentResolver pageableHandlerArgumentResolver = new CustomPageableHandlerMethodArgumentResolver();
    pageableHandlerArgumentResolver.setOneIndexedParameters(true);
    argumentResolvers.add(pageableHandlerArgumentResolver);
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
  public PageableHandlerMethodArgumentResolver pageableHandlerArgumentResolver() {
    PageableHandlerMethodArgumentResolver pageableHandlerArgumentResolver = new CustomPageableHandlerMethodArgumentResolver();
    pageableHandlerArgumentResolver.setOneIndexedParameters(true);
    return pageableHandlerArgumentResolver;
  }
  
}
