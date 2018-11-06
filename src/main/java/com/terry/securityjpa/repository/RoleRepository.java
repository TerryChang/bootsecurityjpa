package com.terry.securityjpa.repository;

import com.terry.securityjpa.entity.Authority;
import com.terry.securityjpa.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  @Query("select distinct r from Role r left outer join fetch r.childRoleSet")
  List<Role> getRoleWithchildRoleSet();

  @Query("select distinct r from Role r left outer join fetch r.authoritySet")
  List<Role> getRoleWithAuthority();
}
