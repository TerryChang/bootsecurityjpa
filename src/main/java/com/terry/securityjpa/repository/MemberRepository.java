package com.terry.securityjpa.repository;

import com.terry.securityjpa.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  // @Query("select m from Member m join fetch m.authoritySet a1 join fetch
  // m.roleSet r join fetch r.authoritySet a2 where m.loginId = :loginId")
  // role은 갖고 있으나 해당 회원만이 가지고 있는 authority는 없을 수 있기 때문에 authoritySet은 left outer
  // join fetch를 걸어주어야 한다
  @Query("select m from Member m left outer join fetch m.authoritySet a1 join fetch m.roleSet r where m.loginId = :loginId")
  Member findMemberByLoginId(@Param("loginId") String loginId);

  Long countMemberByLoginId(String loginId);

}
