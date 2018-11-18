package com.terry.securityjpa.service;

import java.util.ArrayList;
import java.util.List;

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

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
@Transactional
public class BoardServiceTest {

  MemberRepository memberRepository;
  BoardService boardService;
  
  List<Board> saveList = new ArrayList<>();

  public BoardServiceTest() {

  }

  @Autowired
  public BoardServiceTest(MemberRepository memberRepository, BoardService boardService) {
    this.memberRepository = memberRepository;
    this.boardService = boardService;
  }
  
  @Before
  public void before() {
    String title = "테스트 제목 ";
    String contents = "테스트 내용 입니다";
    Member associate_Member = memberRepository.findOne(1L);
    Member regular_Member = memberRepository.findOne(2L);
    for (int i = 0; i < 17; i++) {
      String newTitle = "준회원 " + title + i + " 번째 타이틀입니다";
      String newContents = "준회원" + contents + "\n" + i + "번째 컨텐츠입니다";
      Board board = new Board(associate_Member, newTitle, newContents, "associate");
      boardService.write(board);
      saveList.add(board);
    }
    
    for(int i=0; i < 21; i++) {
      String newTitle = "정회원 " + title + i + " 번째 타이틀입니다";
      String newContents = "정회원 " + contents + "\n" + i + "번째 컨텐츠입니다";
      Board board = new Board(regular_Member, newTitle, newContents, "regular");
      boardService.write(board);
      saveList.add(board);
    }
  }
  
  @Test
  public void associate_게시판_조회() {
    Pageable pageable = new PageRequest(1, 5);
    // Page<Board> result = boardService.list("associate", "", "", pageable);
  }
  
  @After
  public void after() {
    
  }
}
