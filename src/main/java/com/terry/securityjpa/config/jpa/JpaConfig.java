package com.terry.securityjpa.config.jpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaAuditing
@EnableJpaRepositories({"com.terry.securityjpa.repository"})
@EntityScan(
    basePackageClasses = {Jsr310JpaConverters.class}
    , basePackages = {"com.terry.securityjpa.entity"}
)
public class JpaConfig {

  @Bean
  public PageableHandlerMethodArgumentResolver pageableResolver() {
    PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver(sortResolver());
    resolver.setOneIndexedParameters(true);
    return resolver;
  }

  @Bean
  public SortHandlerMethodArgumentResolver sortResolver() {
    return new SortHandlerMethodArgumentResolver();
  }
}
