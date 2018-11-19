package com.terry.securityjpa.repository;

import com.terry.securityjpa.dto.SearchDTO;
import com.terry.securityjpa.entity.Board;
import com.terry.securityjpa.entity.Member;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
// @DataJpaTest로 테스트 할 경우 내가 사용하는 DataSource가 아니라 내가 만든 엔티티들을 기반으로 만든 In Memory Database를 사용하기 때문에 @AutoConfigureTestDatabase를 아래와 같이 주어 내가 사용하는 DataSource를 사용하도록 한다
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("local")
@Transactional
public class BoardRepositoryTest {

  @PersistenceContext
  private EntityManager em;

  @Autowired
  BoardRepository boardRepository;

  @Autowired
  MemberRepository memberRepository;

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
      boardRepository.save(board);
      saveList.add(board);
    }
    
    for (int i = 0; i < 13; i++) {
      String newTitle = "정회원 " + title + i + " 번째 타이틀입니다";
      String newContents = "정회원 " + contents + "\n" + i + "번째 컨텐츠입니다";
      Board board = new Board(regularMember, newTitle, newContents, "regular");
      boardRepository.save(board);
      saveList.add(board);
    }
  }

  @Test
  public void 준회원게시판_제목_Like_검색_페이징() {
    Pageable pageable = new PageRequest(1, 5); // 2 페이지, 한 페이지당 5개 레코드로 구성
    Page<Board> result = boardRepository.findByBoardTypeAndTitleContainingOrderByIdxDesc("associate", "1", pageable);
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
    Pageable pageable = new PageRequest(0, 5); // 1 페이지, 한 페이지당 5개 레코드로 구성
    Page<Board> result = boardRepository.findByBoardTypeAndContentsContainingOrderByIdxDesc("associate", "2", pageable);
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
  public void 게시판_삭제_테스트() {
    Long [] idxs = {3L, 4L, 5L};
    boardRepository.deleteAllByIdxIn(Arrays.asList(idxs));
    int rowCount = -1;
    Session session = em.unwrap(Session.class);
    session.doWork(new Work() {
      @Override
      public void execute(Connection connection) throws SQLException {
        Statement stmt = connection.prepareStatement("select count(idx) from board where idx in (3,4,5)");
        ResultSet rs = ((PreparedStatement) stmt).executeQuery();
        while(rs.next()) {
          int rowCount = rs.getInt(0);
        }
      }
    });
  }
  
  @Test
  public void 정회원게시판_제목_Like_검색_페이징() {
    Pageable pageable = new PageRequest(0, 5); // 1 페이지, 한 페이지당 5개 레코드로 구성
    Page<Board> result = boardRepository.findByBoardTypeAndTitleContainingOrderByIdxDesc("regular", "1", pageable);
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
    Pageable pageable = new PageRequest(0, 5); // 1 페이지, 한 페이지당 5개 레코드로 구성
    Page<Board> result = boardRepository.findByBoardTypeAndContentsContainingOrderByIdxDesc("regular", "2", pageable);
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
