package com.terry.securityjpa.service.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.terry.securityjpa.dto.SearchDTO;
import com.terry.securityjpa.entity.Board;
import com.terry.securityjpa.repository.BoardRepository;
import com.terry.securityjpa.service.BoardService;

@Service
@Transactional
public class BoardServiceImpl implements BoardService {

  private final BoardRepository boardRepository;
  
  public BoardServiceImpl(BoardRepository boardRepository) {
    this.boardRepository = boardRepository;
  }

  @Override
  public Page<Board> list(String boardType, SearchDTO searchDTO, Pageable pageable)
      throws DataAccessException {
    // TODO Auto-generated method stub
    Page<Board> result = null;
    if(StringUtils.hasText(searchDTO.getSearchWord())) {
      if("title".equals(searchDTO.getSearchType())) {
        result = boardRepository.findByBoardTypeAndTitleContainingOrderByIdxDesc(boardType, searchDTO.getSearchWord(), pageable);
      }else if("contents".equals(searchDTO.getSearchType())) {
        result = boardRepository.findByBoardTypeAndContentsContainingOrderByIdxDesc(boardType, searchDTO.getSearchWord(), pageable);
      }
    }
    return result;
  }

  @Override
  public Board view(Long idx) throws DataAccessException {
    // TODO Auto-generated method stub
    return boardRepository.findOne(idx);
  }

  @Override
  public void write(Board board) throws DataAccessException {
    // TODO Auto-generated method stub
    boardRepository.save(board);
  }

  @Override
  public void update(Board board) throws DataAccessException {
    // TODO Auto-generated method stub
    boardRepository.save(board);
  }
  
  
}
