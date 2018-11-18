package com.terry.securityjpa.repository;

import com.terry.securityjpa.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface BoardRepository extends JpaRepository<Board, Long> {
  Page<Board> findByBoardTypeAndTitleContainingOrderByIdxDesc(String boardType, String title, Pageable pageable);
  Page<Board> findByBoardTypeAndContentsContainingOrderByIdxDesc(String boardType, String contents, Pageable pageable);
}
