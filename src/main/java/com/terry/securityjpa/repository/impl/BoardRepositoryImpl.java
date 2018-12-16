package com.terry.securityjpa.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.terry.securityjpa.dto.BoardDTO;
import com.terry.securityjpa.dto.SearchDTO;
import com.terry.securityjpa.entity.Board;
import com.terry.securityjpa.entity.QBoard;
import com.terry.securityjpa.repository.BoardRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.repository.support.QueryDslRepositorySupport;
import org.springframework.util.StringUtils;

public class BoardRepositoryImpl extends QueryDslRepositorySupport implements BoardRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  public BoardRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
    super(Board.class);
    this.jpaQueryFactory = jpaQueryFactory;
  }

  @Override
  public Page<BoardDTO> getBoardList(String boardType, SearchDTO searchDTO) {
    QBoard board = QBoard.board;

    // 조건 생성
    BooleanBuilder booleanBuilder = new BooleanBuilder();
    booleanBuilder.and(board.boardType.eq(boardType));
    if(StringUtils.hasText(searchDTO.getSearchWord())) {
      if("title".equals(searchDTO.getSearchType())) {
        booleanBuilder.and(board.title.contains(searchDTO.getSearchWord()));
      }else if("contents".equals(searchDTO.getSearchType())) {
        booleanBuilder.and(board.contents.contains(searchDTO.getSearchWord()));
      }
    }

    QueryResults<BoardDTO> queryResults = jpaQueryFactory
        .select(Projections.bean(BoardDTO.class, board.idx.as("idx"), board.member.loginId.as("loginId"), board.title.as("title"), board.boardType.as("boardType"), board.createUpdateDT.createDT.as("createDT")))
        .from(board)
        .where(booleanBuilder)
        .orderBy(board.idx.desc())
        .offset(searchDTO.getOffset())
        .limit(searchDTO.getPageSize())
        .fetchResults();

    return new PageImpl(queryResults.getResults(), searchDTO, queryResults.getTotal());
  }
}
