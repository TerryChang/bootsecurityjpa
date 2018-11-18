package com.terry.securityjpa.web;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.terry.securityjpa.dto.MemberDTO;
import com.terry.securityjpa.entity.Member;

@Controller
public class BoardController {

  /*
   * @RequestMapping(value="/{member_type}/board/list") public String
   * list(Authentication authentication, Model model, @PathVariable(value =
   * "member_type") String member_type) { MemberDTO memberDTO =
   * (MemberDTO)authentication.getPrincipal(); // 로그인 계정정보 // memberDTO 변수를 이용해서
   * ROLE을 얻어온다 Set<String> roleSet = memberDTO.getRoleSet(); return "board/list";
   * }
   */

  @GetMapping(value = "/board/{member_type}/list")
  public String list(@PathVariable(value = "member_type") String member_type, Model model) {
    model.addAttribute("type", member_type);
    return "board/list";
  }

  @GetMapping(value = "/board/{member_type}/view")
  public String view(@PathVariable(value = "member_type") String member_type, Model model) {
    model.addAttribute("type", member_type);
    return "board/view";
  }

  @GetMapping(value = "/board/{member_type}/write")
  public String write(@PathVariable(value = "member_type") String member_type, Principal principal, Model model) {
    Member loginMember = ((MemberDTO) principal).getMember();
    model.addAttribute("type", member_type);
    return "board/write";
  }

  @PostMapping(value = "/board/{member_type}/write")
  public String writePost(@PathVariable(value = "member_type") String member_type, Principal principal, Model model) {
    Member loginMember = ((MemberDTO) principal).getMember();
    model.addAttribute("type", member_type);
    return "board/write";
  }

  @GetMapping(value = "/board/{member_type}/update")
  public String update(@PathVariable(value = "member_type") String member_type, Principal principal, Model model) {
    Member loginMember = ((MemberDTO) principal).getMember();
    model.addAttribute("type", member_type);
    return "board/update";
  }

  @PostMapping(value = "/board/{member_type}/update")
  public String updatePost(@PathVariable(value = "member_type") String member_type, Principal principal, Model model) {
    Member loginMember = ((MemberDTO) principal).getMember();
    model.addAttribute("type", member_type);
    return "board/update";
  }
}
