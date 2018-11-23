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

  }
}
