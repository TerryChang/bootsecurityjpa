package com.terry.securityjpa.web;

import com.terry.securityjpa.dto.MemberDTO;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@Controller
public class BoardController {

  /*
  @RequestMapping(value="/{member_type}/board/list")
  public String list(Authentication authentication, Model model, @PathVariable(value = "member_type") String member_type) {
    MemberDTO memberDTO = (MemberDTO)authentication.getPrincipal();                         // 로그인 계정정보
    // memberDTO 변수를 이용해서 ROLE을 얻어온다
    Set<String> roleSet = memberDTO.getRoleSet();
    return "board/list";
  }
  */

  @RequestMapping(value="/{member_type}/board/list")
  public String list(@PathVariable(value = "member_type") String member_type, Model model) {
    model.addAttribute("type", member_type);
    return "board/list";
  }

  @RequestMapping(value="/{member_type}/board/view")
  public String view(@PathVariable(value = "member_type") String member_type, Model model) {
    model.addAttribute("type", member_type);
    return "board/view";
  }

  @RequestMapping(value="/{member_type}/board/write")
  public String write(@PathVariable(value = "member_type") String member_type, Model model) {
    model.addAttribute("type", member_type);
    return "board/write";
  }

  @RequestMapping(value="/{member_type}/board/update")
  public String update(@PathVariable(value = "member_type") String member_type, Model model) {
    model.addAttribute("type", member_type);
    return "board/update";
  }
}
