package com.terry.securityjpa.repository;

import com.terry.securityjpa.dto.BoardDTO;
import com.terry.securityjpa.dto.SearchDTO;
import com.terry.securityjpa.entity.Board;
import com.terry.securityjpa.entity.Member;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
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
@SpringBootTest
// @DataJpaTest
// @DataJpaTest로 테스트 할 경우 내가 사용하는 DataSource가 아니라 내가 만든 엔티티들을 기반으로 만든 In Memory Database를 사용하기 때문에 @AutoConfigureTestDatabase를 아래와 같이 주어 내가 사용하는 DataSource를 사용하도록 한다
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("local")
@Transactional
@Slf4j
public class BoardRepositoryTest {

  @PersistenceContext
  private EntityManager em;

  @Autowired
  private BoardRepository boardRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private ModelMapper modelMapper;

  List<BoardDTO> saveList = new ArrayList<>();
  
  int rowCount = -1;

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
      saveList.add(modelMapper.map(board, BoardDTO.class));
    }
    
    for (int i = 0; i < 13; i++) {
      String newTitle = "정회원 " + title + i + " 번째 타이틀입니다";
      String newContents = "정회원 " + contents + "\n" + i + "번째 컨텐츠입니다";
      Board board = new Board(regularMember, newTitle, newContents, "regular");
      boardRepository.save(board);
      saveList.add(modelMapper.map(board, BoardDTO.class));
    }
  }

  @Test
  public void 준회원게시판_제목_Like_검색_페이징() {
    SearchDTO searchDTO = new SearchDTO();
    searchDTO.setSearchType("title");
    searchDTO.setSearchWord("1");
    searchDTO.setPageNo(1);       // 테스트 클래스에서는 PageableArgumentResovler를 거치는 것이 아니기 때문에 1페이지를 볼려면 0으로 설정해야 한다
    searchDTO.setPageSize(5);
    Page<BoardDTO> result = boardRepository.getBoardList("associate", searchDTO);
    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(8); // 전체 갯수
    assertThat(result.getTotalPages()).isEqualTo(2); // 전체 페이지수
    assertThat(result.getNumberOfElements()).isEqualTo(3); // 현재 보고자하는 페이지의 레코드 갯수
    List<BoardDTO> boardList = result.getContent();

    // 예상 결과물을 만든다
    List<BoardDTO> expectList = new ArrayList<>();
    expectList.add(saveList.get(11)); // 11
    expectList.add(saveList.get(10)); // 10
    expectList.add(saveList.get(1)); // 1

    // assertThat(boardList).isEqualTo(expectList);

    int boardListSize = boardList.size();
    assertThat(boardListSize).isEqualTo(expectList.size());

    // b.idx, b.title, b.member, b.boardType, b.createUpdateDT
    compareList(boardList, expectList);

  }


  @Test
  public void 준회원게시판_내용_Like_검색_페이징() {
    SearchDTO searchDTO = new SearchDTO();
    searchDTO.setSearchType("contents");
    searchDTO.setSearchWord("2");
    searchDTO.setPageNo(0);       // 테스트 클래스에서는 PageableArgumentResovler를 거치는 것이 아니기 때문에 1페이지를 볼려면 0으로 설정해야 한다
    searchDTO.setPageSize(5);
    Page<BoardDTO> result = boardRepository.getBoardList("associate", searchDTO);
    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(2); // 전체 갯수
    assertThat(result.getTotalPages()).isEqualTo(1); // 전체 페이지수
    assertThat(result.getNumberOfElements()).isEqualTo(2); // 현재 보고자하는 페이지의 레코드 갯수
    List<BoardDTO> boardList = result.getContent();

    // 예상 결과물을 만든다
    List<BoardDTO> expectList = new ArrayList<>();
    expectList.add(saveList.get(12)); // 12
    expectList.add(saveList.get(2)); // 2

    int boardListSize = boardList.size();
    assertThat(boardListSize).isEqualTo(expectList.size());

    // b.idx, b.title, b.member, b.boardType, b.createUpdateDT
    compareList(boardList, expectList);
  }

  
  /**
   * 엔티티 삭제 작업은 먼저 영속성 컨텍스트에 엔티티가 올라와 있어야 한다.
   * 이 테스트의 경우 이 테스트 작업을 하기 전에 before 메소드를 통해 등록하는 작업을 진행하면서 엔티티가 이미 영속성 컨텍스트에 올라와 있는 상황이기 때문에
   * 별도 조회작업을 하지 않았으나 만약 영속성 컨텍스트에 없는 엔티티를 조회하려 할 경우 delete 메소드는 내부적으로 select문을 한번 실행하여
   * 삭제하고자 하는 엔티티를 먼저 영속성 컨텍스트에 올려놓는 작업을 진행하게 된다
   * 이 부분에 대해 알고 있어야 다른 사람들에게 이해시키기가 쉽다
   * 단일건을 삭제할 경우에는 부담이 가지 않지만 여러개를 삭제하는 작업을 진행할경우 삭제할 건들을 한건 조회하고 삭제하고 한건 조회하고 삭제하고 하는 식의
   * 이런 반복작업이 벌어지기 때문에 이러한 상황이 퍼포먼스에 영향을 줄 수 있다.
   * 이런 상황일때는 bulk 삭제를 진행하면 해결이 되지만 bulk 삭제의 경우 오히려 영속성 컨텍스트를 거치지 않고 바로 database에 SQL문을 바로 날리기 때문에
   * 영속성 컨텍스트에 이미 올라와 있는 엔티티가 bulk 삭제 대상이 되는 엔티티가 되지 않도록 주의해야 한다
   * 
   * 단일 삭제 테스트를 진행할때는 before 메소드를 통해 등록된 엔티티를 삭제하는 것이 아니라 현재 진행하는 테스트 메소드에서 엔티티를 등록한뒤 
   * 이를 삭제하는 방법으로 진행한다
   * 왜냐면 before 메소드가 테스트 메소드 실행시 매번 실행되기 때문에
   * 삭제 테스트 메소드 시작전에 실행되는 before 메소드에서 사용된 시퀀스 값을 알 수 있는 방법이 없다
   * 그래서 테스트 메소드에서 엔티티 객체 하나를 등록한뒤 이를 삭제하는 방법으로 진행한다
   */
  @Test
  public void 게시판_단일_삭제_테스트() {
    String newTitle = "준회원 타이틀입니다";
    String newContents = "준회원 컨텐츠입니다";
    Member associateMember = memberRepository.findOne(1L);  // 준회원
    Board board = new Board(associateMember, newTitle, newContents, "associate");
    boardRepository.save(board);
    boardRepository.flush();
    
    Long idx = board.getIdx();
    
    boardRepository.delete(idx);
    Board selectBoard = boardRepository.findOne(idx);
    assertThat(selectBoard).isNull();
  }

  /**
   * deleteAllByIdxIn 메소드는 @Modify와 @Query를 이용한 bulk 삭제 작업이기 때문에 
   * 영속성 컨텍스트를 거치지 않고 바로 database에 작업을 진행한다
   * 그래서 이를 확인할려면 Connection 객체를 얻어와서 직접 SQL문을 실행하여 관련 레코드가 존재하는지 확인하는 방법으로 진행한다 
   * @throws Exception
   */
  @Test
  public void 게시판_복수_삭제_테스트() throws Exception {
    Long [] idxs = {3L, 4L, 5L};
    boardRepository.deleteAllByIdxIn(Arrays.asList(idxs));
    Session session = em.unwrap(Session.class);
    session.doWork(new Work() {
      @Override
      public void execute(Connection connection) throws SQLException {
        Statement stmt = connection.prepareStatement("select count(idx) from board where idx in (3,4,5)");
        ResultSet rs = ((PreparedStatement) stmt).executeQuery();
        while(rs.next()) {
          rowCount = rs.getInt(1);
        }
        
        rs.close();
        stmt.close();
      }
    });
  }

  @Test
  public void 정회원게시판_제목_Like_검색_페이징() {
    SearchDTO searchDTO = new SearchDTO();
    searchDTO.setSearchType("title");
    searchDTO.setSearchWord("1");
    searchDTO.setPageNo(0);       // 테스트 클래스에서는 PageableArgumentResovler를 거치는 것이 아니기 때문에 1페이지를 볼려면 0으로 설정해야 한다
    searchDTO.setPageSize(5);
    Page<BoardDTO> result = boardRepository.getBoardList("regular", searchDTO);
    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(4); // 전체 갯수
    assertThat(result.getTotalPages()).isEqualTo(1); // 전체 페이지수
    assertThat(result.getNumberOfElements()).isEqualTo(4); // 현재 보고자하는 페이지의 레코드 갯수
    List<BoardDTO> boardList = result.getContent();

    // 예상 결과물을 만든다
    List<BoardDTO> expectList = new ArrayList<>();
    expectList.add(saveList.get(29)); // 12
    expectList.add(saveList.get(28)); // 11
    expectList.add(saveList.get(27)); // 10
    expectList.add(saveList.get(18)); // 1

    int boardListSize = boardList.size();
    assertThat(boardListSize).isEqualTo(expectList.size());

    // b.idx, b.title, b.member, b.boardType, b.createUpdateDT
    compareList(boardList, expectList);
  }

  @Test
  public void 정회원게시판_내용_Like_검색_페이징() {
    SearchDTO searchDTO = new SearchDTO();
    searchDTO.setSearchType("contents");
    searchDTO.setSearchWord("2");
    searchDTO.setPageNo(0);       // 테스트 클래스에서는 PageableArgumentResovler를 거치는 것이 아니기 때문에 1페이지를 볼려면 0으로 설정해야 한다
    searchDTO.setPageSize(5);
    Page<BoardDTO> result = boardRepository.getBoardList("regular", searchDTO);
    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(2); // 전체 갯수
    assertThat(result.getTotalPages()).isEqualTo(1); // 전체 페이지수
    assertThat(result.getNumberOfElements()).isEqualTo(2); // 현재 보고자하는 페이지의 레코드 갯수
    List<BoardDTO> boardList = result.getContent();

    // 예상 결과물을 만든다
    List<BoardDTO> expectList = new ArrayList<>();
    expectList.add(saveList.get(29)); // 12
    expectList.add(saveList.get(19)); // 2

    int boardListSize = boardList.size();
    assertThat(boardListSize).isEqualTo(expectList.size());

    compareList(boardList, expectList);
  }

  @Test
  public void BoardRepository의_countByMemberIsAndIdxIn메소드_기능확인테스트() {
    Member associateMember = memberRepository.findOne(1L);  // associate_id
    Long [] idxs1 = {saveList.get(2).getIdx(), saveList.get(3).getIdx(), saveList.get(4).getIdx()};
    Long [] idxs2 = {saveList.get(0).getIdx(), saveList.get(1).getIdx(), saveList.get(19).getIdx(), saveList.get(20).getIdx()};
    // Long [] idxs1 = {3L, 4L, 5L};                              // associate_id가 쓴 글
    // Long [] idxs2 = {1L, 2L, 20L, 21L};                         // associate_id와 regular_id가 쓴 글
    Long count1 = boardRepository.countByMemberIsAndIdxIn(associateMember, Arrays.asList(idxs1));
    assertThat(count1).isEqualTo(3);
    Long count2 = boardRepository.countByMemberIsAndIdxIn(associateMember, Arrays.asList(idxs2));
    assertThat(count2).isEqualTo(2);                            // associate_id가 쓴 글만 조회하는 것이기 때문에 2가 나와야 한다

  }


  @After
  public void after() {
    saveList.clear();
  }

  private void compareList(List<BoardDTO> boardList, List<BoardDTO> expectList) {
    int boardListSize = boardList.size();

    for(int i = 0; i < boardListSize; i++) {
      BoardDTO boardDTO = boardList.get(i);
      BoardDTO expect = expectList.get(i);
      logger.info(i + "번쪠 entity 비교 체크");
      assertThat(boardDTO.getIdx()).isEqualTo(expect.getIdx());
      assertThat(boardDTO.getTitle()).isEqualTo(expect.getTitle());
      assertThat(boardDTO.getLoginId()).isEqualTo(expect.getLoginId());
      assertThat(boardDTO.getBoardType()).isEqualTo(expect.getBoardType());
      assertThat(boardDTO.getCreateDT()).isEqualTo(expect.getCreateDT());
    }
  }
}
