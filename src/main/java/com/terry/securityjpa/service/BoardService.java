package com.terry.securityjpa.service;

import com.terry.securityjpa.dto.BoardDTO;
import com.terry.securityjpa.dto.MemberDTO;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.terry.securityjpa.dto.SearchDTO;
import com.terry.securityjpa.entity.Board;
import org.springframework.security.access.AccessDeniedException;

public interface BoardService {

  Page<BoardDTO> list(String boardType, SearchDTO searchDTO) throws DataAccessException;
  Board view(Long idx) throws DataAccessException;
  void write(BoardDTO boardDTO, MemberDTO memberDTO) throws DataAccessException;
  void update(BoardDTO boardDTO, MemberDTO memberDTO) throws DataAccessException, AccessDeniedException;
  void delete(Long idx, MemberDTO memberDTO) throws DataAccessException;
  void delete(Long [] idxs, MemberDTO memberDTO) throws DataAccessException;
  
}
