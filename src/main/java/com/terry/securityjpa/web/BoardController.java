package com.terry.securityjpa.web;

import java.security.Principal;

import com.terry.securityjpa.config.web.support.LoginMember;
import com.terry.securityjpa.dto.BoardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.terry.securityjpa.dto.MemberDTO;
import com.terry.securityjpa.dto.SearchDTO;
import com.terry.securityjpa.entity.Board;
import com.terry.securityjpa.entity.Member;
import com.terry.securityjpa.service.BoardService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@AllArgsConstructor
@Slf4j
public class BoardController {

  /*
   * @RequestMapping(value="/{member_type}/board/list") public String
   * list(Authentication authentication, Model model, @PathVariable(value =
   * "member_type") String member_type) { MemberDTO memberDTO =
   * (MemberDTO)authentication.getPrincipal(); // 로그인 계정정보 // memberDTO 변수를 이용해서
   * ROLE을 얻어온다 Set<String> roleSet = memberDTO.getRoleSet(); return "board/list";
   * }
   */

  private BoardService boardService;
  
  /**
   * @PageDefault 어노테이션 사용시엔 PageableHandlerMethodArgumentResolver 클래스의 oneIndexedParameter 속성값 설정과는 상관없이 1페이지를 볼려면 page 속성을 0으로 주어야 한다
   * 
   * @param boardType
   * @param model
   * @param searchDTO
   * @return
   */
  @GetMapping(value = "/board/{boardType}/list")
  public String list(@PathVariable(value = "boardType") String boardType, Model model, @PageableDefault(page=0, size=10) SearchDTO searchDTO) {
    Page<BoardDTO> result = boardService.list(boardType, searchDTO);
    model.addAttribute("searchDTO", searchDTO);
    model.addAttribute("boardType", boardType);
    model.addAttribute("result", result);
    return "board/list";
  }

  @GetMapping(value = "/board/{boardType}/view")
  public String view(@PathVariable(value = "boardType") String boardType, Model model, @RequestParam(value="idx") Long idx) {
    Board board = boardService.view(idx);
    model.addAttribute("boardType", boardType);
    model.addAttribute("board", board);
    return "board/view";
  }

  @GetMapping(value = "/board/{boardType}/write")
  public String write(@PathVariable(value = "boardType") String boardType, @LoginMember MemberDTO loginUser, @ModelAttribute(name="board") BoardDTO boardDTO, Model model) {
    model.addAttribute("boardType", boardType);
    return "board/write";
  }

  @PostMapping(value = "/board/{boardType}/write")
  public String writePost(@PathVariable(value = "boardType") String boardType, @LoginMember MemberDTO loginUser, @ModelAttribute(name="board") BoardDTO boardDTO, Model model) {
    boardService.write(boardDTO, loginUser);
    model.addAttribute("boardType", boardType);
    return "redirect:/board/" + boardType + "/list.html";
  }

  @GetMapping(value = "/board/{boardType}/update")
  public String update(@PathVariable(value = "member_type") String boardType, @LoginMember MemberDTO loginUser, @RequestParam(value="idx") long idx, Model model) {

    model.addAttribute("boardType", boardType);
    return "board/update";
  }

  @PostMapping(value = "/board/{boardType}/update")
  public String updatePost(@PathVariable(value = "boardType") String boardType, @LoginMember MemberDTO loginUser, @ModelAttribute(name="board") BoardDTO boardDTO, Model model) {

    model.addAttribute("boardType", boardType);
    return "board/update";
  }
}
