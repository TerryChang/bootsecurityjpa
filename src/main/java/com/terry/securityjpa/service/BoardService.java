package com.terry.securityjpa.service;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.terry.securityjpa.dto.SearchDTO;
import com.terry.securityjpa.entity.Board;

public interface BoardService {

  Page<Board> list(String boardType, SearchDTO searchDTO, Pageable pageable) throws DataAccessException;
  Board view(Long idx) throws DataAccessException;
  void write(Board board) throws DataAccessException;
  void update(Board board) throws DataAccessException;
  
}
