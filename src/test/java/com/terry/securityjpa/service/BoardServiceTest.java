package com.terry.securityjpa.service;

import java.util.ArrayList;
import java.util.List;

import com.terry.securityjpa.dto.SearchDTO;
import com.terry.securityjpa.repository.BoardRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.terry.securityjpa.entity.Board;
import com.terry.securityjpa.entity.Member;
import com.terry.securityjpa.repository.MemberRepository;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
@Transactional
public class BoardServiceTest {

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  BoardService boardService;

  @Autowired
  BoardRepository boardRepository;
  
  List<Board> saveList = new ArrayList<>();
  
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
      boardService.write(board);
      saveList.add(board);
    }

    for (int i = 0; i < 13; i++) {
      String newTitle = "정회원 " + title + i + " 번째 타이틀입니다";
      String newContents = "정회원 " + contents + "\n" + i + "번째 컨텐츠입니다";
      Board board = new Board(regularMember, newTitle, newContents, "regular");
      boardService.write(board);
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
  public void 준회원게시판_쓰기_테스트() {
    Member associateMember = memberRepository.findOne(1L);  // 준회원
    Board board = new Board(associateMember, "준회원 테스트 제목", "준회원 테스트 내용", "associate");
    boardService.write(board);
    boardRepository.flush();
    Board selectBoard = boardService.view(board.getIdx()); // flush를 해도 영속성 컨텍스트에서 엔티티가 지워지는 것이 아니기 때문에 findOne을 통해 조회해도 DB를 거치지않는다.
    assertThat(selectBoard).isEqualTo(board);
  }

  @Test
  public void 준회원게시판_수정_테스트() {
    Board board = boardService.view(3L);
    board.setTitle("수정된 타이틀");
    board.setContents("수정된 내용");
    boardService.update(board);
    boardRepository.flush();
    Board selectBoard = boardService.view(3L);
    assertThat(selectBoard).isEqualTo(board);

  }

  @Test
  public void 준회원게시판_삭제_테스트() {
    Board board = boardService.view(3L);
    boardService.delete(3L);
    boardRepository.flush();
    Board selectBoard = boardService.view(3L); // 삭제를 할 경우 영속성 컨텍스트에서 엔티티가 지워지기 때문에 이 상태에서 select 를 하면 영속성 컨텍스트에 없으므로 실제로 DB에 조회하는 작업을 거친다
    assertThat(selectBoard).isNull();
  }

  @Test
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
    
  }
}
