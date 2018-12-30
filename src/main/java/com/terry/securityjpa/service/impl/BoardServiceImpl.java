package com.terry.securityjpa.service.impl;

import com.terry.securityjpa.dto.BoardDTO;
import com.terry.securityjpa.dto.BoardFileDTO;
import com.terry.securityjpa.dto.MemberDTO;
import com.terry.securityjpa.entity.BoardFile;
import com.terry.securityjpa.entity.Member;
import com.terry.securityjpa.repository.BoardRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.terry.securityjpa.dto.SearchDTO;
import com.terry.securityjpa.entity.Board;
import com.terry.securityjpa.repository.BoardRepository;
import com.terry.securityjpa.service.BoardService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

  private final BoardRepository boardRepository;
  private final ModelMapper modelMapper;

  @Value("${app.uploadFilePath}")
  private String uploadFilePath;

  @Override
  public Page<BoardDTO> list(String boardType, SearchDTO searchDTO)
      throws DataAccessException {
    // TODO Auto-generated method stub
    return boardRepository.getBoardList(boardType, searchDTO);
  }

  @Override
  public Board view(Long idx) throws DataAccessException {
    // TODO Auto-generated method stub
    return boardRepository.findBoardAndBoardFileByIdx(idx);
  }

  @Override
  public void write(BoardDTO boardDTO, MemberDTO memberDTO) throws DataAccessException, IOException {
    // TODO Auto-generated method stub
    Board board = modelMapper.map(boardDTO, Board.class);
    board.setMember(memberDTO.getMember());

    List<BoardFile> boardFileList = null;
    List<BoardFileDTO> boardFileDTOList = boardDTO.getBoardFileDTOList();
    if(!CollectionUtils.isEmpty(boardFileDTOList)) {
      boardFileList = new ArrayList<>();
      for(BoardFileDTO boardFileDTO : boardFileDTOList) {
        if(boardFileDTO != null) {
          // 업로드된 파일을 디스크에 저장한다
          // 먼저 밀리세컨드단위까지 표기된 오늘의 날짜와 시간 문자열을 구한다
          String realFileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")) ;
          if(StringUtils.hasText(boardFileDTO.getExtension())) {
            realFileName += "." + boardFileDTO.getExtension();
          }
          String savePath = uploadFilePath + realFileName.substring(0, 8) + "/" +realFileName.substring(8, 12) + "/";
          boardFileDTO.setSavePath(savePath);
          boardFileDTO.setRealFileName(realFileName);
          boardFileDTO.transferFile();              // 디스크에 파일로 저장한다
          BoardFile boardFile = BoardFile.builder()
              .savePath(boardFileDTO.getSavePath())
              .orgFileName(boardFileDTO.getOrgFileName())
              .realFileName(boardFileDTO.getRealFileName())
              .extension(boardFileDTO.getExtension())
              .size(boardFileDTO.getSize())
              .createDT(LocalDateTime.now())
              .build();
          boardFile.setBoard(board);
          boardFileList.add(boardFile);
        }
      }
      board.setBoardFileList(boardFileList);
    }

    boardRepository.save(board);
  }

  @Override
  public void update(BoardDTO boardDTO, MemberDTO memberDTO) throws DataAccessException, AccessDeniedException, IOException {
    // TODO Auto-generated method stub
    Board board = boardRepository.findOne(boardDTO.getIdx());
    if(!memberDTO.getRoleSet().contains("ADMIN") && !board.getMember().equals(memberDTO.getMember())) {
      throw new AccessDeniedException("관리자이거나 글의 작성자만이 수정/삭제할 수 있습니다");
    }
    board.setTitle(boardDTO.getTitle());
    board.setContents(boardDTO.getContents());

    List<BoardFileDTO> boardFileDTOList = boardDTO.getBoardFileDTOList();
    if(!CollectionUtils.isEmpty(boardFileDTOList)) {

      // 첨부파일이 없을 경우 첨부파일을 저장해야 하기 때문에 해당 필드를 ArrayList 객체로 초기화 시켜준다
      if(board.getBoardFileList() == null) {
        board.setBoardFileList(new ArrayList<BoardFile>());
      }

      for(BoardFileDTO boardFileDTO : boardFileDTOList) {
        if(boardFileDTO != null) {
          // 업로드된 파일을 디스크에 저장한다
          // 먼저 밀리세컨드단위까지 표기된 오늘의 날짜와 시간 문자열을 구한다
          String realFileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")) ;
          if(StringUtils.hasText(boardFileDTO.getExtension())) {
            realFileName += "." + boardFileDTO.getExtension();
          }
          String savePath = uploadFilePath + realFileName.substring(0, 8) + "/" +realFileName.substring(8, 12) + "/";
          boardFileDTO.setSavePath(savePath);
          boardFileDTO.setRealFileName(realFileName);
          boardFileDTO.transferFile();              // 디스크에 파일로 저장한다
          BoardFile boardFile = BoardFile.builder()
              .savePath(boardFileDTO.getSavePath())
              .orgFileName(boardFileDTO.getOrgFileName())
              .realFileName(boardFileDTO.getRealFileName())
              .extension(boardFileDTO.getExtension())
              .size(boardFileDTO.getSize())
              .build();
          boardFile.setBoard(board);
          // 기존의 첨부파일 리스트가 있기 때문에 write 메소드때 처럼 ArrayList 객체를 setter 메소드로 설정하는것이 아니라 addBoardFlle 메소드를 이용해서 추가하는 형식을 취한다
          board.addBoardFile(boardFile);
        }
      }
    }


    boardRepository.save(board);
  }

  @Override
  public void delete(Long idx, MemberDTO memberDTO) throws DataAccessException, AccessDeniedException {
    Board board = boardRepository.findBoardAndBoardFileByIdx(idx);
    if(!memberDTO.getRoleSet().contains("ADMIN") && !board.getMember().equals(memberDTO.getMember())) {
      throw new AccessDeniedException("관리자이거나 글의 작성자만이 수정/삭제할 수 있습니다");
    }

    // 첨부파일에 대한 정보를 따로 백업한다.
    List<BoardFileDTO> boardFileDTOList = new ArrayList<>();
    for(BoardFile boardFile : board.getBoardFileList()) {
      BoardFileDTO boardFileDTO =  modelMapper.map(boardFile, BoardFileDTO.class);
      boardFileDTOList.add(boardFileDTO);
    }

    boardRepository.delete(idx);

    // 첨부파일 삭제 작업을 진행한다
    for(BoardFileDTO boardFileDTO : boardFileDTOList) {
      File file = new File(boardFileDTO.getSavePath() + boardFileDTO.getRealFileName());
      file.delete();
    }
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

    List<Board> boardList = boardRepository.findBoardAndBoardFileByIdxs(Arrays.asList(idxs));
    // 첨부파일에 대한 정보를 따로 백업한다
    List<BoardFileDTO> boardFileDTOList = new ArrayList<>();
    for(Board board : boardList) {
      List<BoardFile> boardFileList = board.getBoardFileList();
      for(BoardFile boardFile : boardFileList) {
        BoardFileDTO boardFileDTO =  modelMapper.map(boardFile, BoardFileDTO.class);
        boardFileDTOList.add(boardFileDTO);
      }
    }

    // DB 삭제작업을 진행한다
    boardRepository.deleteAllByIdxIn(Arrays.asList(idxs));
    // 첨부파일 삭제작업을 진행한다
    for(BoardFileDTO boardFileDTO : boardFileDTOList) {
      File file = new File(boardFileDTO.getSavePath() + boardFileDTO.getRealFileName());
      file.delete();
    }
  }
}
