package com.terry.securityjpa.service.impl;

import com.terry.securityjpa.dto.BoardDTO;
import com.terry.securityjpa.dto.MemberDTO;
import com.terry.securityjpa.entity.Member;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.terry.securityjpa.dto.SearchDTO;
import com.terry.securityjpa.entity.Board;
import com.terry.securityjpa.repository.BoardRepository;
import com.terry.securityjpa.service.BoardService;

import java.util.Arrays;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

  private final BoardRepository boardRepository;
  private final ModelMapper modelMapper;

  @Override
  public Page<Board> list(String boardType, SearchDTO searchDTO)
      throws DataAccessException {
    // TODO Auto-generated method stub
    Page<Board> result = null;
    if(StringUtils.hasText(searchDTO.getSearchWord())) {
      if("title".equals(searchDTO.getSearchType())) {
        result = boardRepository.findByBoardTypeAndTitleContainingOrderByIdxDesc(boardType, searchDTO.getSearchWord(), searchDTO);
      }else if("contents".equals(searchDTO.getSearchType())) {
        result = boardRepository.findByBoardTypeAndContentsContainingOrderByIdxDesc(boardType, searchDTO.getSearchWord(), searchDTO);
      }
    }else {
      result = boardRepository.findAll(searchDTO);
    }
    return result;
  }

  @Override
  public Board view(Long idx) throws DataAccessException {
    // TODO Auto-generated method stub
    return boardRepository.findOne(idx);
  }

  @Override
  public void write(BoardDTO boardDTO, MemberDTO memberDTO) throws DataAccessException{
    // TODO Auto-generated method stub
    Board board = modelMapper.map(boardDTO, Board.class);
    board.setMember(memberDTO.getMember());
    boardRepository.save(board);
  }

  @Override
  public void update(BoardDTO boardDTO, MemberDTO memberDTO) throws DataAccessException, AccessDeniedException {
    // TODO Auto-generated method stub
    Board board = boardRepository.findOne(boardDTO.getIdx());
    if(!memberDTO.getRoleSet().contains("ADMIN") && !board.getMember().equals(memberDTO.getMember())) {
      throw new AccessDeniedException("관리자이거나 글의 작성자만이 수정/삭제할 수 있습니다");
    }
    board.setTitle(boardDTO.getTitle());
    board.setContents(boardDTO.getContents());
    boardRepository.save(board);
  }

  @Override
  public void delete(Long idx, MemberDTO memberDTO) throws DataAccessException, AccessDeniedException {
    Board board = boardRepository.findOne(idx);
    if(!memberDTO.getRoleSet().contains("ADMIN") && !board.getMember().equals(memberDTO.getMember())) {
      throw new AccessDeniedException("관리자이거나 글의 작성자만이 수정/삭제할 수 있습니다");
    }
    boardRepository.delete(idx);
  }

  @Override
  public void delete(Long[] idxs, MemberDTO memberDTO) throws DataAccessException, AccessDeniedException {
    // 삭제하려고 하는 글이 로그인 한 사람이 쓴 것인지를 확인하기 위해 로그인 한 사람의 정보와 삭제하고자 하는 글의 key를 같이 조건으로 주어 갯수를 구한뒤
    // 삭제하고자 하는 글의 갯수와 위에서 구한 글의 갯수가 동일하면 삭제하고자 하는 글이 모두 로그인 한 사람이 쓴 글이라 판단하여 진행하고
    // 만약 틀리면 삭제하고자 하는 글중 로그인 한 사람이 쓴 것이 아닌 글이 있을수 있기 때문에 진행을 중지해야 한다
    Long count = boardRepository.countByMemberIsAndIdxIn(memberDTO.getMember(), Arrays.asList(idxs));
    if(idxs.length != count) {
      throw new AccessDeniedException("관리자이거나 글의 작성자만이 수정/삭제할 수 있습니다");
    }
    boardRepository.deleteAllByIdxIn(Arrays.asList(idxs));
  }
}
