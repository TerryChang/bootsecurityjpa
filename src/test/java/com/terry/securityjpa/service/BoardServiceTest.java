package com.terry.securityjpa.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.terry.securityjpa.config.WithMockCustomUser;
import com.terry.securityjpa.dto.BoardDTO;
import com.terry.securityjpa.dto.MemberDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.terry.securityjpa.dto.SearchDTO;
import com.terry.securityjpa.entity.Board;
import com.terry.securityjpa.entity.Member;
import com.terry.securityjpa.repository.BoardRepository;
import com.terry.securityjpa.repository.MemberRepository;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
@Slf4j
@Transactional
public class BoardServiceTest {

  @PersistenceContext
  EntityManager entityManager;
  
  @Autowired
  MemberRepository memberRepository;

  @Autowired
  BoardService boardService;

  @Autowired
  BoardRepository boardRepository;
  
  List<Board> saveList = new ArrayList<>();

  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  
  @Before
  public void before() {
    String title = "테스트 제목 ";
    String contents = "테스트 내용 입니다";
    Member associateMember = memberRepository.findOne(1L);  // 준회원
    Member regularMember = memberRepository.findOne(2L); // 정회원
    for (int i = 0; i < 17; i++) {
      String newTitle = "준회원 " + title + i + " 번째 타이틀입니다";
      String newContents = "준회원 " + contents + "\n" + i + "번째 컨텐츠입니다";
      Board board = new Board(associateMember, newTitle, newContents, "associate");
      boardRepository.save(board);
      saveList.add(board);
    }

    for (int i = 0; i < 13; i++) {
      String newTitle = "정회원 " + title + i + " 번째 타이틀입니다";
      String newContents = "정회원 " + contents + "\n" + i + "번째 컨텐츠입니다";
      Board board = new Board(regularMember, newTitle, newContents, "regular");
      // boardService.write(board);
      boardRepository.save(board);
      saveList.add(board);
    }

    boardRepository.flush();
  }

  @Test
  public void 준회원게시판_제목_Like_검색_페이징() {
    SearchDTO searchDTO = new SearchDTO("title", "1", 1, 5);
    Page<Board> result = boardService.list("associate", searchDTO);
    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(8); // 전체 갯수
    assertThat(result.getTotalPages()).isEqualTo(2); // 전체 페이지수
    assertThat(result.getNumberOfElements()).isEqualTo(3); // 현재 보고자하는 페이지의 레코드 갯수
    List<Board> boardList = result.getContent();

    // 예상 결과물을 만든다
    List<Board> expectList = new ArrayList<>();
    expectList.add(saveList.get(11)); // 11
    expectList.add(saveList.get(10)); // 10
    expectList.add(saveList.get(1)); // 1

    assertThat(boardList).isEqualTo(expectList);
  }

  @Test
  public void 준회원게시판_내용_Like_검색_페이징() {
    SearchDTO searchDTO = new SearchDTO("contents", "2", 0, 5);
    Page<Board> result = boardService.list("associate", searchDTO);
    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(2); // 전체 갯수
    assertThat(result.getTotalPages()).isEqualTo(1); // 전체 페이지수
    assertThat(result.getNumberOfElements()).isEqualTo(2); // 현재 보고자하는 페이지의 레코드 갯수
    List<Board> boardList = result.getContent();

    // 예상 결과물을 만든다
    List<Board> expectList = new ArrayList<>();
    expectList.add(saveList.get(12)); // 12
    expectList.add(saveList.get(2)); // 2

    assertThat(boardList).isEqualTo(expectList);
  }

  @Test
  @WithMockCustomUser("associate_id")
  public void 준회원게시판_쓰기_테스트() {
    MemberDTO memberDTO = (MemberDTO)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    // Board board = new Board(memberDTO.getMember(), "준회원 테스트 제목", "준회원 테스트 내용", "associate");
    // boardService.write(memberDTO, board);
    BoardDTO boardDTO = BoardDTO.builder().title("준회원 테스트 제목").contents("준회원 테스트 내용").boardType("associate").build();
    boardService.write(boardDTO, memberDTO);
    boardRepository.flush();

    Board selectBoard = boardService.view(31L); // flush를 해도 영속성 컨텍스트에서 엔티티가 지워지는 것이 아니기 때문에 findOne을 통해 조회해도 DB를 거치지않는다.
    assertThat(selectBoard.getTitle()).isEqualTo(boardDTO.getTitle());
    assertThat(selectBoard.getContents()).isEqualTo(boardDTO.getContents());
  }


  @Test
  @WithMockCustomUser("associate_id")
  public void 준회원게시판_수정_테스트() {
    MemberDTO memberDTO = (MemberDTO)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    BoardDTO boardDTO = BoardDTO.builder().idx(3L).title("수정된 타이틀").contents("수정된 내용").build();
    /*
    Board board = boardService.view(3L);
    board.setTitle("수정된 타이틀");
    board.setContents("수정된 내용");
    */
    boardService.update(boardDTO, memberDTO);
    boardRepository.flush();
    Board selectBoard = boardService.view(3L);
    assertThat(selectBoard.getTitle()).isEqualTo(boardDTO.getTitle());
    assertThat(selectBoard.getContents()).isEqualTo(boardDTO.getContents());

  }

  @Test
  @WithMockCustomUser("associate_id")
  public void 준회원게시판_삭제_테스트() {
    MemberDTO memberDTO = (MemberDTO)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    boardService.delete(3L, memberDTO);
    // delete 메소드가 내부적으로는 findOne 메소드를 실행하여 영속성 컨텍스트에 엔티티가 올라가게끔 하고 있기 때문에 엔티티가 영속성 컨텍스트에 존재하는지 확인할 수 있다
    if(entityManager.contains(boardService.view(3L))) {
      logger.info("엔티티 존재함");
    } else {
      logger.info("엔티티 존재하지 않음");
    }
    Board selectBoard = boardService.view(3L); // 삭제를 할 경우 영속성 컨텍스트에서 엔티티가 지워지기 때문에 이 상태에서 select 를 하면 영속성 컨텍스트에 없으므로 실제로 DB에 조회하는 작업을 거친다
    assertThat(selectBoard).isNull();
    logger.info("준회원게시판_삭제_테스트 종료");
  }

  /**
   * 로그인한 사용자가 작성한 게시글이 아닌 게시글을 수정할 경우 AccessDeniedException이 발생하는데
   * 이 예외가 발생하는지에 대한 테스트
   */
  @Test
  @WithMockCustomUser("regular_id")
  public void 로그인_사용자가_작성한_게시글이_아닌_게시글을_수정할_경우의_테스트() {
    expectedException.expect(AccessDeniedException.class);
    expectedException.expectMessage("관리자이거나 글의 작성자만이 수정/삭제할 수 있습니다");
    MemberDTO memberDTO = (MemberDTO)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    BoardDTO boardDTO = BoardDTO.builder().idx(3L).title("수정된 타이틀").contents("수정된 내용").build();
    /*
    Board board = boardService.view(3L);
    board.setTitle("수정된 타이틀");
    board.setContents("수정된 내용");
    */
    boardService.update(boardDTO, memberDTO);
  }


  @Test
  @WithMockCustomUser("associate_id")
  public void 정회원게시판_제목_Like_검색_페이징() {

    SearchDTO searchDTO = new SearchDTO("title", "1", 0, 5);
    Page<Board> result = boardService.list("regular", searchDTO);
    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(4); // 전체 갯수
    assertThat(result.getTotalPages()).isEqualTo(1); // 전체 페이지수
    assertThat(result.getNumberOfElements()).isEqualTo(4); // 현재 보고자하는 페이지의 레코드 갯수
    List<Board> boardList = result.getContent();

    // 예상 결과물을 만든다
    List<Board> expectList = new ArrayList<>();
    expectList.add(saveList.get(29)); // 12
    expectList.add(saveList.get(28)); // 11
    expectList.add(saveList.get(27)); // 10
    expectList.add(saveList.get(18)); // 1

    assertThat(boardList).isEqualTo(expectList);
  }

  @Test
  public void 정회원게시판_내용_Like_검색_페이징() {

    SearchDTO searchDTO = new SearchDTO("contents", "2", 0, 5);
    Page<Board> result = boardService.list("regular", searchDTO);
    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(2); // 전체 갯수
    assertThat(result.getTotalPages()).isEqualTo(1); // 전체 페이지수
    assertThat(result.getNumberOfElements()).isEqualTo(2); // 현재 보고자하는 페이지의 레코드 갯수
    List<Board> boardList = result.getContent();

    // 예상 결과물을 만든다
    List<Board> expectList = new ArrayList<>();
    expectList.add(saveList.get(29)); // 12
    expectList.add(saveList.get(19)); // 2

    assertThat(boardList).isEqualTo(expectList);
  }

  
  @After
  public void after() {
    saveList.clear();
  }
}
