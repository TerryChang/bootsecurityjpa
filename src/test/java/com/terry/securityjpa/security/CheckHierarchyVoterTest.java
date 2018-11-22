package com.terry.securityjpa.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.terry.securityjpa.config.cache.CacheService;
import com.terry.securityjpa.config.security.access.hierarchicalroles.CustomRoleHierachyImpl;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
@Rollback
@Slf4j
public class CheckHierarchyVoterTest {

  @Autowired
  CacheService cacheService;
  
  @Test
  public void CustomRoleHierachyImpl_내부_자료구조_생성_체크_테스트() {
    String roleHierarchyStringRepresentation = "ADMIN > 1-1 ADMIN > 1-2 ADMIN > 1-3 1-1 > 2-1 1-1 > 2-2 1-2 > 2-3 1-3 > 2-4 1-3 > 2-5 1-3 > 2-6 2-1 > 3-1 2-2 > 3-2 2-2 > 3-3 2-3 > 3-4 2-3 > 3-5 2-3 > 3-6 2-4 > 3-7 2-5 > 3-8 2-5 > 3-9 3-2 > 4-1";
    CustomRoleHierachyImpl customRoleHierachyImpl = new CustomRoleHierachyImpl(cacheService, roleHierarchyStringRepresentation);
    logger.info("roleHierarchyStringRepresentation");
  }
}
