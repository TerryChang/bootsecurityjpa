package com.terry.securityjpa.repository;

import com.terry.securityjpa.entity.Board;
import com.terry.securityjpa.entity.Member;
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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
// @DataJpaTest로 테스트 할 경우 내가 사용하는 DataSource가 아니라 내가 만든 엔티티들을 기반으로 만든 In Memory Database를 사용하기 때문에 @AutoConfigureTestDatabase를 아래와 같이 주어 내가 사용하는 DataSource를 사용하도록 한다
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("local")
@Transactional
public class BoardRepositoryTest {

  @Autowired
  BoardRepository boardRepository;

  @Autowired
  MemberRepository memberRepository;

  List<Board> saveList = new ArrayList<>();

  @Before
  public void before() {
    String title = "테스트 제목 ";
    String contents = "테스트 내용 입니다";
    Member member = memberRepository.findOne(1L);
    for(int i=0; i < 17; i++) {
      String newTitle = title + i + " 번째 타이틀입니다";
      String newContents = contents + "\n" + i + "번째 컨텐츠입니다";
      Board board = new Board(member, newTitle, newContents);
      boardRepository.save(board);
      saveList.add(board);
    }
  }

  @Test
  public void 제목_Like_검색_페이징() {
    Pageable pageable = new PageRequest(1, 5); // 2 페이지, 한 페이지당 5개 레코드로 구성
    Page<Board> result = boardRepository.findByTitleContainingOrderByIdxDesc("1", pageable);
    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(8);         // 전체 갯수
    assertThat(result.getTotalPages()).isEqualTo(2);            // 전체 페이지수
    assertThat(result.getNumberOfElements()).isEqualTo(3);      // 현재 보고자하는 페이지의 레코드 갯수
    List<Board> boardList = result.getContent();

    // 예상 결과물을 만든다
    List<Board> expectList = new ArrayList<>();
    expectList.add(saveList.get(11)); // 11
    expectList.add(saveList.get(10)); // 10
    expectList.add(saveList.get(1)); // 1

    assertThat(boardList).isEqualTo(expectList);
  }

  @Test
  public void 내용_Like_검색_페이징() {
    Pageable pageable = new PageRequest(0, 5); // 1 페이지, 한 페이지당 5개 레코드로 구성
    Page<Board> result = boardRepository.findByContentsContainingOrderByIdxDesc("2", pageable);
    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(2);         // 전체 갯수
    assertThat(result.getTotalPages()).isEqualTo(1);            // 전체 페이지수
    assertThat(result.getNumberOfElements()).isEqualTo(2);      // 현재 보고자하는 페이지의 레코드 갯수
    List<Board> boardList = result.getContent();

    // 예상 결과물을 만든다
    List<Board> expectList = new ArrayList<>();
    expectList.add(saveList.get(12)); // 12
    expectList.add(saveList.get(2)); // 2

    assertThat(boardList).isEqualTo(expectList);
  }

  @After
  public void after() {
    saveList.clear();
  }
}
