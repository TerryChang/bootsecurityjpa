package com.terry.securityjpa.repository;

import com.terry.securityjpa.entity.Board;
import com.terry.securityjpa.entity.Member;
import org.hibernate.persister.walking.spi.CollectionDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {
  Long countByMemberIsAndIdxIn(Member member, Collection<Long> idxs);

  // 첨부파일이 없는 게시물을 조회할 수 있기 때문에 left outer join을 걸어준다
  @Query("select b from Board b join fetch b.member left join fetch b.boardFileList where b.idx = :idx")
  Board findBoardAndBoardFileByIdx(@Param("idx") Long idx);

  // 첨부파일이 없는 게시물을 조회할 수 있기 때문에 left outer join을 걸어준다
  @Query("select b from Board b join fetch b.member left join fetch b.boardFileList where b.idx in :idxs")
  List<Board> findBoardAndBoardFileByIdxs(@Param("idxs") Collection<Long> idxs);

  @Modifying
  @Query("delete from Board b where b.idx in :idxs")
  void deleteAllByIdxIn(@Param("idxs") Collection<Long> idxs);
}
