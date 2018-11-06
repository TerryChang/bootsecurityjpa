package com.terry.securityjpa.config;

import com.terry.securityjpa.config.cache.CacheService;
import com.terry.securityjpa.repository.RoleRepository;
import com.terry.securityjpa.repository.UrlResourcesRepository;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class MainConfig {

  @Bean
  public CacheService cacheService(UrlResourcesRepository urlResourcesRepository, RoleRepository roleRepository) {
    CacheService cacheService = new CacheService(urlResourcesRepository, roleRepository);
    return cacheService;
  }
}
