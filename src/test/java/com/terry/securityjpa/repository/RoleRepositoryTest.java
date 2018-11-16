package com.terry.securityjpa.repository;

import com.terry.securityjpa.entity.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
public class RoleRepositoryTest {

  @Autowired
  RoleRepository roleRepository;

  @Test
  public void testGetRoleWithchildRoleSet() {
    List<Role> roleList = roleRepository.getRoleWithchildRoleSet();
    assertThat(roleList).isNotNull();
  }

}
