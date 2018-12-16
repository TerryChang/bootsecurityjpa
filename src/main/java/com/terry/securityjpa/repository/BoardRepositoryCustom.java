package com.terry.securityjpa.repository;

import com.terry.securityjpa.dto.BoardDTO;
import com.terry.securityjpa.dto.SearchDTO;
import org.springframework.data.domain.Page;

public interface BoardRepositoryCustom {
  Page<BoardDTO> getBoardList(String boardType, SearchDTO searchDTO);
}
