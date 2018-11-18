package com.terry.securityjpa.web;

import com.terry.securityjpa.dto.MemberDTO;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NoticeController {
  @RequestMapping(value = "/notice/list")
  public String list(Authentication authentication, Model model) {
    MemberDTO memberDTO = (MemberDTO) authentication.getPrincipal(); // 로그인 계정정보
    // memberDTO 변수를 이용해서 ROLE을 얻어온다
    return "board/list";
  }

  @RequestMapping(value = "/notice/view")
  public String view(@RequestParam(value = "type", defaultValue = "") String type, Model model) {
    model.addAttribute("type", type);
    return "board/view";
  }
}
