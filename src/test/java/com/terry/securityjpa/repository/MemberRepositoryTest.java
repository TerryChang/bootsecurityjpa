package com.terry.securityjpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.terry.securityjpa.entity.Member;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
@Transactional
public class MemberRepositoryTest {

  @Autowired
  MemberRepository memberRepository;

  @Test
  public void checkMemberRepository() {
    Member member = memberRepository.findMemberByLoginId("regular_id");
    assertThat(member).isNotNull();
  }

  @Test
  public void insertMemberRepository() {
    Member member = new Member();
    member.setLoginId("test");
    member.setLoginPassword("testPassword");
    member.setName("testName");

    memberRepository.save(member);

    Member resultMember = memberRepository.findOne(5L);
    assertThat(resultMember).isEqualTo(member);

  }
}
