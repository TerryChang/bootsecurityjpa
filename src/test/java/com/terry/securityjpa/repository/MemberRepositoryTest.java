package com.terry.securityjpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.terry.securityjpa.entity.Member;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
public class MemberRepositoryTest {

  @Autowired
  MemberRepository memberRepository;

  @Test
  public void checkMemberRepository() {
    Member member = memberRepository.findMemberByLoginId("junmemberid");
    assertThat(member).isNotNull();
  }
}
