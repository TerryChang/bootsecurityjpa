package com.terry.securityjpa.repository;

import com.terry.securityjpa.entity.UrlResources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlResourcesRepository extends JpaRepository<UrlResources, Long> {

  @Query("select u from UrlResources u join fetch u.authoritySet order by u.ordernum desc")
  List<UrlResources> findAllUrlResources();
}
